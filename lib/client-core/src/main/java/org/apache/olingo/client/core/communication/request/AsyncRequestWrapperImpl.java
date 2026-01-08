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
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.message.StatusLine;
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
  protected final CloseableHttpClient httpClient;

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

    extendHeader(HttpHeader.PREFER, new ODataPreferences().respondAsync().toString());

    this.odataClient = odataClient;
    final String method = odataRequest.getMethod().toString();

    // target uri
    this.uri = odataRequest.getURI();
    Objects.requireNonNull(this.uri, "Target URI can't be null");

    // Prefer HttpClient provided by the configured factory (tests may mock it), fallback to a default builder
    CloseableHttpClient clientFromFactory = null;
    try {
      if (odataClient != null && odataClient.getConfiguration() != null
          && odataClient.getConfiguration().getHttpClientFactory() != null) {
        clientFromFactory = (CloseableHttpClient) odataClient.getConfiguration().getHttpClientFactory()
            .create(odataRequest.getMethod(), this.uri);
      }
    } catch (final RuntimeException e) {
      // ignore and fallback to default
      clientFromFactory = null;
    }

    if (clientFromFactory != null) {
      this.httpClient = clientFromFactory;
    } else {
      this.httpClient = HttpClients.custom()
          .addRequestInterceptorFirst((request, entity, context) -> {
            if (odataClient != null && odataClient.getConfiguration() != null
                && odataClient.getConfiguration().isGzipCompression()) {
              request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
            }
          })
          .build();
    }

    // Create HttpUriRequest
    this.request = new HttpUriRequestBase(method, this.uri);


      if (this.request instanceof HttpUriRequestBase && odataRequest instanceof AbstractODataBasicRequest) {
      AbstractODataBasicRequest<?> br = (AbstractODataBasicRequest<?>) odataRequest;
          if (br.getPayload() != null) {
            this.request.setEntity(
                new InputStreamEntity(br.getPayload(), ContentType.APPLICATION_OCTET_STREAM)
            );
          }
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

  protected ClassicHttpResponse doExecute() {
    // Add all available headers
    for (String key : odataRequest.getHeaderNames()) {
      final String value = odataRequest.getHeader(key);
      this.request.addHeader(key, value);
      LOG.debug("HTTP header being sent {}: {}", key, value);
    }

    return executeHttpRequest(httpClient, this.request);
  }

  private URI checkLocation(URI uri) {
    if (!this.uri.getScheme().equals(uri.getScheme())) {
      throw new AsyncRequestException("Unexpected scheme in the Location header");
    }
    if (!this.uri.getHost().equals(uri.getHost())) {
      throw new AsyncRequestException("Unexpected host name in the Location header");
    }
    if (this.uri.getPort() != uri.getPort()) {
      throw new AsyncRequestException("Unexpected port in the Location header");
    }
    return uri;
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
    public AsyncResponseWrapperImpl(final ClassicHttpResponse res) {
      if (res.getCode() == 202) {
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
    public boolean isDone() throws IOException {
      if (response == null) {
        // check to the monitor URL
        final ClassicHttpResponse res = checkMonitor(location);

        if (res.getCode() == 202) {
          retrieveMonitorDetails(res);
        } else {
          response = instantiateResponse(res);
        }
      }

      return response != null;
    }

    @Override
    public R getODataResponse() throws IOException {
      ClassicHttpResponse res = null;
      for (int i = 0; response == null && i < MAX_RETRY; i++) {
        res = checkMonitor(location);

        if (res.getCode() == HttpStatusCode.ACCEPTED.getStatusCode()) {

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
        throw new ODataClientErrorException(res == null ? null : new StatusLine(res));
      }

      return response;
    }

    URI createLocation(String string) {
      return checkLocation(URI.create(string));
    }

    int parseReplyAfter(String value) {
      if (value == null || value.isEmpty()) {
        return DEFAULT_RETRY_AFTER;
      }
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
    public ODataDeleteResponse delete() throws URISyntaxException, IOException {
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
    private R instantiateResponse(final ClassicHttpResponse res) throws IOException {
      R odataResponse;
      try {
        odataResponse = (R) ((AbstractODataRequest) odataRequest).getResponseTemplate().initFromEnclosedPart(res
            .getEntity().getContent());
      } catch (Exception e) {
        LOG.error("Error instantiating odata response", e);
        odataResponse = null;
      } finally {
        res.close();
      }
      return odataResponse;
    }

    private void retrieveMonitorDetails(final ClassicHttpResponse res) {
      Header[] headers = res.getHeaders(HttpHeader.LOCATION);
      if (ArrayUtils.isNotEmpty(headers)) {
        this.location = createLocation(headers[0].getValue());
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

  protected final ClassicHttpResponse checkMonitor(final URI location) {
    if (location == null) {
      throw new AsyncRequestException("Invalid async request response. Missing monitor URL");
    }

    final HttpUriRequest monitor = odataClient.getConfiguration().getHttpUriRequestFactory().create(HttpMethod.GET,
        location);

    return executeHttpRequest(httpClient, monitor);
  }

  protected final ClassicHttpResponse executeHttpRequest(final CloseableHttpClient client, final HttpUriRequest req) {
    final ClassicHttpResponse response;
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
