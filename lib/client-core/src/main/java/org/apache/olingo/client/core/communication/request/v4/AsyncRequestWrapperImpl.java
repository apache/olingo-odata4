/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request.v4;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.v4.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.v4.AsyncResponseWrapper;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.header.ODataHeadersImpl;
import org.apache.olingo.client.core.communication.request.AbstractODataRequest;
import org.apache.olingo.client.core.communication.request.AbstractRequest;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class AsyncRequestWrapperImpl<R extends ODataResponse> extends AbstractRequest
        implements AsyncRequestWrapper<R> {

  private final ODataClient odataClient;

  private final static int MAX_RETRY = 5;

  /**
   * Request to be wrapped.
   */
  private final ODataRequest odataRequest;

  /**
   * HTTP client.
   */
  private final HttpClient httpClient;

  /**
   * HTTP request.
   */
  private final HttpUriRequest request;

  /**
   * OData request header.
   */
  private final ODataHeadersImpl odataHeaders;

  /**
   * Target URI.
   */
  private final URI uri;

  protected AsyncRequestWrapperImpl(final ODataClient odataClient, final ODataRequest odataRequest) {
    this.odataRequest = odataRequest;
    this.odataRequest.setAccept(this.odataRequest.getAccept());
    this.odataRequest.setContentType(this.odataRequest.getContentType());

    extendHeader(HeaderName.prefer.toString(), new ODataPreferences(ODataServiceVersion.V40).respondAsync());

    this.odataClient = odataClient;
    final HttpMethod method = odataRequest.getMethod();

    // initialize default headers
    this.odataHeaders = (ODataHeadersImpl) odataClient.getVersionHeaders();

    // target uri
    this.uri = odataRequest.getURI();

    HttpClient _httpClient = odataClient.getConfiguration().getHttpClientFactory().createHttpClient(method, this.uri);
    if (odataClient.getConfiguration().isGzipCompression()) {
      _httpClient = new DecompressingHttpClient(_httpClient);
    }
    this.httpClient = _httpClient;

    this.request = odataClient.getConfiguration().getHttpUriRequestFactory().createHttpUriRequest(method, this.uri);
  }

  @Override
  public AsyncRequestWrapper<R> wait(final int waitInSeconds) {
    extendHeader(HeaderName.prefer.toString(), new ODataPreferences(ODataServiceVersion.V40).wait(waitInSeconds));
    return this;
  }

  private void extendHeader(final String headerName, final String headerValue) {
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

  private HttpResponse doExecute() {

    // Add all available headers
    for (String key : odataRequest.getHeaderNames()) {
      final String value = odataRequest.getHeader(key);
      this.request.addHeader(key, value);
      LOG.debug("HTTP header being sent {}: {}", key, value);
    }

    return executeHttpRequest(httpClient, this.request);
  }

  public class AsyncResponseWrapperImpl implements AsyncResponseWrapper<R> {

    private URI location = null;

    private R response = null;

    private int retryAfter = 5;

    private boolean preferenceApplied = false;

    /**
     * Constructor.
     *
     * @param res HTTP response.
     */
    @SuppressWarnings("unchecked")
    public AsyncResponseWrapperImpl(final HttpResponse res) {

      if (res.getStatusLine().getStatusCode() == 202) {
        retrieveMonitorDetails(res, true);
      } else {
        response = (R) ((AbstractODataRequest<?>) odataRequest).getResponseTemplate().initFromHttpResponse(res);
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
          retrieveMonitorDetails(res, false);
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

        if (res.getStatusLine().getStatusCode() == 202) {

          final Header[] headers = res.getHeaders(HeaderName.retryAfter.toString());
          if (ArrayUtils.isNotEmpty(headers)) {
            this.retryAfter = Integer.parseInt(headers[0].getValue());
          }

          try {
            // wait for retry-after
            Thread.sleep(retryAfter * 1000);
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

    @Override
    public ODataDeleteResponse delete() {
      final ODataDeleteRequest deleteRequest = odataClient.getCUDRequestFactory().getDeleteRequest(location);
      return deleteRequest.execute();
    }

    @Override
    public AsyncResponseWrapper<ODataDeleteResponse> asyncDelete() {
      return odataClient.getAsyncRequestFactory().<ODataDeleteResponse>getAsyncRequestWrapper(
              odataClient.getCUDRequestFactory().getDeleteRequest(location)).execute();
    }

    @SuppressWarnings("unchecked")
    private R instantiateResponse(final HttpResponse res) {
      R odataResponse;

      try {

        odataResponse = (R) ((AbstractODataRequest<?>) odataRequest).getResponseTemplate().
                initFromEnclosedPart(res.getEntity().getContent());

      } catch (Exception e) {
        LOG.error("Error instantiating odata response", e);
        odataResponse = null;
      }

      return odataResponse;
    }

    private void retrieveMonitorDetails(final HttpResponse res, final boolean includePreferenceApplied) {
      Header[] headers = res.getHeaders(HeaderName.location.toString());
      if (ArrayUtils.isNotEmpty(headers)) {
        this.location = URI.create(headers[0].getValue());
      } else {
        throw new AsyncRequestException(
                "Invalid async request response. Monitor URL '" + headers[0].getValue() + "'");
      }

      headers = res.getHeaders(HeaderName.retryAfter.toString());
      if (ArrayUtils.isNotEmpty(headers)) {
        this.retryAfter = Integer.parseInt(headers[0].getValue());
      }

      headers = res.getHeaders(HeaderName.preferenceApplied.toString());
      if (ArrayUtils.isNotEmpty(headers)) {
        for (Header header : headers) {
          if (header.getValue().equalsIgnoreCase(new ODataPreferences(ODataServiceVersion.V40).respondAsync())) {
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

  private HttpResponse checkMonitor(final URI location) {
    if (location == null) {
      throw new AsyncRequestException("Invalid async request response. Missing monitor URL");
    }

    final HttpUriRequest monitor = odataClient.getConfiguration().getHttpUriRequestFactory().
            createHttpUriRequest(HttpMethod.GET, location);

    return executeHttpRequest(httpClient, monitor);
  }

  private HttpResponse executeHttpRequest(final HttpClient client, final HttpUriRequest req) {
    final HttpResponse response;
    try {
      response = client.execute(req);
    } catch (IOException e) {
      throw new HttpClientException(e);
    } catch (RuntimeException e) {
      req.abort();
      throw new HttpClientException(e);
    }

    checkForResponse(odataClient, response, odataRequest.getAccept());

    return response;
  }
}
