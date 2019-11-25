/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;

public class AsyncRequestWrapperImpl<R extends ODataResponse> extends AbstractRequest
    implements AsyncRequestWrapper<R> {

  protected static final int MAX_RETRY = 5;

  protected final ODataClient odataClient;

  /**
   * Request to be wrapped.
   */
  protected final ODataRequest odataRequest;

  /**
   * HTTP client.
   */
  protected final HttpClient httpClient;

  /**
   * HTTP request.
   */
  protected final HttpUriRequest request;

  /**
   * Target URI.
   */
  protected final URI uri;

  protected AsyncRequestWrapperImpl(final ODataClient odataClient, final ODataRequest odataRequest) {
    this.odataRequest = odataRequest;
    this.odataRequest.setAccept(this.odataRequest.getAccept());
    this.odataRequest.setContentType(this.odataRequest.getContentType());

    extendHeader(HttpHeader.PREFER, new ODataPreferences().respondAsync());

    this.odataClient = odataClient;
    final HttpMethod method = odataRequest.getMethod();

    // target uri
    this.uri = odataRequest.getURI();

    HttpClient _httpClient = odataClient.getConfiguration().getHttpClientFactory().create(method, this.uri);
    if (odataClient.getConfiguration().isGzipCompression()) {
      _httpClient = new DecompressingHttpClient(_httpClient);
    }
    this.httpClient = _httpClient;

    this.request = odataClient.getConfiguration().getHttpUriRequestFactory().create(method, this.uri);

    if (request instanceof HttpEntityEnclosingRequestBase && odataRequest instanceof AbstractODataBasicRequest) {
      AbstractODataBasicRequest<?> br = (AbstractODataBasicRequest<?>) odataRequest;
      HttpEntityEnclosingRequestBase httpRequest = ((HttpEntityEnclosingRequestBase) request);
      httpRequest.setEntity(new InputStreamEntity(br.getPayload(), -1));
    }
  }

  @Override
  public final AsyncRequestWrapper<R> wait(final int waitInSeconds) {
    extendHeader(HttpHeader.PREFER, new ODataPreferences().wait(waitInSeconds));
    return this;
  }

  @Override
  public final AsyncRequestWrapper<R> callback(URI url) {
    extendHeader(HttpHeader.PREFER, new ODataPreferences().callback(url.toASCIIString()));
    return this;
  }

  protected final void extendHeader(final String headerName, final String headerValue) {
    final StringBuilder extended = new StringBuilder();
    if (this.odataRequest.getHeaderNames().contains(headerName)) {
      extended.append(this.odataRequest.getHeader(headerName)).append(", ");
    }

    this.odataRequest.addCustomHeader(headerName, extended.append(headerValue).toString());
  }

  @Override
  public AsyncResponseWrapper<R> execute() {
    return new AsyncResponseWrapperImpl(doExecute());
  }

  protected HttpResponse doExecute() {
    // Add all available headers
    for (String key : odataRequest.getHeaderNames()) {
      final String value = odataRequest.getHeader(key);
      this.request.addHeader(key, value);
      LOG.debug("HTTP header being sent {}: {}", key, value);
    }

    return executeHttpRequest(httpClient, this.request);
  }

  public class AsyncResponseWrapperImpl implements AsyncResponseWrapper<R> {

    static final int DEFAULT_RETRY_AFTER = 5;
    static final int MAX_RETRY_AFTER = 10;

    protected URI location = null;

    protected R response = null;

    protected int retryAfter = DEFAULT_RETRY_AFTER;

    protected boolean preferenceApplied = false;

    public AsyncResponseWrapperImpl() {}

    /**
     * Constructor.
     *
     * @param res HTTP response.
     */
    @SuppressWarnings("unchecked")
    public AsyncResponseWrapperImpl(final HttpResponse res) {
      if (res.getStatusLine().getStatusCode() == 202) {
        retrieveMonitorDetails(res);
      } else {
        response = (R) ((AbstractODataRequest) odataRequest).getResponseTemplate().initFromHttpResponse(res);
      }
    }

    @Override
    public boolean isPreferenceApplied() {
      return preferenceApplied;
    }

    @Override
    public boolean isDone() {
      if (response == null) {
        // check to the monitor URL
        final HttpResponse res = checkMonitor(location);

        if (res.getStatusLine().getStatusCode() == 202) {
          retrieveMonitorDetails(res);
        } else {
          response = instantiateResponse(res);
        }
      }

      return response != null;
    }

    @Override
    public R getODataResponse() {
      HttpResponse res = null;
      for (int i = 0; response == null && i < MAX_RETRY; i++) {
        res = checkMonitor(location);

        if (res.getStatusLine().getStatusCode() == HttpStatusCode.ACCEPTED.getStatusCode()) {

          final Header[] headers = res.getHeaders(HttpHeader.RETRY_AFTER);
          if (ArrayUtils.isNotEmpty(headers)) {
            this.retryAfter = parseReplyAfter(headers[0].getValue());
          }

          try {
            // wait for retry-after
            Thread.sleep((long) retryAfter * 1000);
          } catch (InterruptedException ignore) {
            // ignore
          }

        } else {
          location = null;
          return instantiateResponse(res);
        }
      }

      if (response == null) {
        throw new ODataClientErrorException(res == null ? null : res.getStatusLine());
      }

      return response;
    }

    int parseReplyAfter(String value) {
      if (value == null || value.isEmpty()) {
        return DEFAULT_RETRY_AFTER;

      try {
        int n = Integer.parseInt(value);
        if (n < 0) {
          return DEFAULT_RETRY_AFTER;
        }
        return Math.min(n, MAX_RETRY_AFTER);
      } catch (NumberFormatException e) {
        return DEFAULT_RETRY_AFTER;
      }
    }

    @Override
    public ODataDeleteResponse delete() {
      final ODataDeleteRequest deleteRequest = odataClient.getCUDRequestFactory().getDeleteRequest(location);
      return deleteRequest.execute();
    }

    @Override
    public AsyncResponseWrapper<ODataDeleteResponse> asyncDelete() {
      return odataClient.getAsyncRequestFactory().<ODataDeleteResponse> getAsyncRequestWrapper(
          odataClient.getCUDRequestFactory().getDeleteRequest(location)).execute();
    }

    @Override
    public AsyncResponseWrapper<R> forceNextMonitorCheck(final URI uri) {
      this.location = uri;
      this.response = null;
      return this;
    }

    @SuppressWarnings("unchecked")
    private R instantiateResponse(final HttpResponse res) {
      R odataResponse;
      try {
        odataResponse = (R) ((AbstractODataRequest) odataRequest).getResponseTemplate().initFromEnclosedPart(res
            .getEntity().getContent());
      } catch (Exception e) {
        LOG.error("Error instantiating odata response", e);
        odataResponse = null;
      } finally {
        HttpClientUtils.closeQuietly(res);
      }
      return odataResponse;
    }

    private void retrieveMonitorDetails(final HttpResponse res) {
      Header[] headers = res.getHeaders(HttpHeader.LOCATION);
      if (ArrayUtils.isNotEmpty(headers)) {
        this.location = URI.create(headers[0].getValue());
      } else {
        throw new AsyncRequestException(
            "Invalid async request response. Monitor URL '" + headers[0].getValue() + "'");
      }

      headers = res.getHeaders(HttpHeader.RETRY_AFTER);
      if (ArrayUtils.isNotEmpty(headers)) {
        this.retryAfter = parseReplyAfter(headers[0].getValue());
      }

      headers = res.getHeaders(HttpHeader.PREFERENCE_APPLIED);
      if (ArrayUtils.isNotEmpty(headers)) {
        for (Header header : headers) {
          if (header.getValue().equalsIgnoreCase(new ODataPreferences().respondAsync())) {
            preferenceApplied = true;
          }
        }
      }
      try {
        EntityUtils.consume(res.getEntity());
      } catch (IOException ex) {
        Logger.getLogger(AsyncRequestWrapperImpl.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  protected final HttpResponse checkMonitor(final URI location) {
    if (location == null) {
      throw new AsyncRequestException("Invalid async request response. Missing monitor URL");
    }

    final HttpUriRequest monitor = odataClient.getConfiguration().getHttpUriRequestFactory().create(HttpMethod.GET,
        location);

    return executeHttpRequest(httpClient, monitor);
  }

  protected final HttpResponse executeHttpRequest(final HttpClient client, final HttpUriRequest req) {
    final HttpResponse response;
    try {
      response = client.execute(req);
    } catch (IOException e) {
      throw new HttpClientException(e);
    } catch (RuntimeException e) {
      req.abort();
      throw new HttpClientException(e);
    }

    checkResponse(odataClient, response, odataRequest.getAccept());

    return response;
  }
}
