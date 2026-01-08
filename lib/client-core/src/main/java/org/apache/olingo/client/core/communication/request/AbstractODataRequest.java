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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.header.ODataHeaders;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * Abstract representation of an OData request. Get instance by using factories.
 *
 * @see org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory
 * @see org.apache.olingo.client.api.communication.request.batch.BatchRequestFactory
 * @see org.apache.olingo.client.api.communication.request.invoke.InvokeRequestFactory
 */
public abstract class AbstractODataRequest extends AbstractRequest implements ODataRequest {

  private static final byte[] CRLF = {13, 10};
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  protected final ODataClient odataClient;

  /**
   * OData request method.
   */
  protected final HttpMethod method;

  /**
   * OData request header.
   */
  protected final ODataHeaders odataHeaders;

  /**
   * Target URI.
   */
  protected URI uri;

  /**
   * HTTP client.
   */
  protected CloseableHttpClient httpClient;

  /**
   * HTTP request.
   */
  protected HttpUriRequest request;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method HTTP request method. If configured X-HTTP-METHOD header will be used.
   * @param uri OData request URI.
   */
  protected AbstractODataRequest(final ODataClient odataClient, final HttpMethod method, final URI uri) {
    super();

    this.odataClient = odataClient;
    this.method = method;

    // initialize default headers
    this.odataHeaders = odataClient.newVersionHeaders();

    // target uri
    this.uri = uri;
    this.httpClient = getHttpClient(method, uri);
    this.request = odataClient.getConfiguration().getHttpUriRequestFactory().create(this.method, uri);
  }

  public abstract ContentType getDefaultFormat();

  @Override
  public URI getURI() {
    return uri;
  }

  @Override
  public HttpUriRequest getHttpRequest() {
    return request;
  }

  @Override
  public void setURI(final URI uri) {
    this.uri = uri;
    this.httpClient = getHttpClient(method, uri);
    this.request = odataClient.getConfiguration().getHttpUriRequestFactory().create(this.method, this.uri);
  }

  @Override
  public Collection<String> getHeaderNames() {
    return odataHeaders.getHeaderNames();
  }

  @Override
  public String getHeader(final String name) {
    return odataHeaders.getHeader(name);
  }

  @Override
  public ODataRequest setAccept(final String value) {
    odataHeaders.setHeader(HttpHeader.ACCEPT, value);
    return this;
  }

  @Override
  public ODataRequest setIfMatch(final String value) {
    odataHeaders.setHeader(HttpHeader.IF_MATCH, value);
    return this;
  }

  @Override
  public ODataRequest setIfNoneMatch(final String value) {
    odataHeaders.setHeader(HttpHeader.IF_NONE_MATCH, value);
    return this;
  }

  @Override
  public ODataRequest setPrefer(final String value) {
    odataHeaders.setHeader(HttpHeader.PREFER, value);
    return this;
  }

  @Override
  public ODataRequest setXHTTPMethod(final String value) {
    odataHeaders.setHeader(HttpHeader.X_HTTP_METHOD, value);
    return this;
  }

  @Override
  public ODataRequest setContentType(final String value) {
    odataHeaders.setHeader(HttpHeader.CONTENT_TYPE, value);
    return this;
  }

  @Override
  public ODataRequest addCustomHeader(final String name, final String value) {
    odataHeaders.setHeader(name, value);
    return this;
  }

  @Override
  public String getAccept() {
    final String acceptHead = odataHeaders.getHeader(HttpHeader.ACCEPT);
    return StringUtils.isBlank(acceptHead) ? getDefaultFormat().toContentTypeString() : acceptHead;
  }

  @Override
  public String getIfMatch() {
    return odataHeaders.getHeader(HttpHeader.IF_MATCH);
  }

  @Override
  public String getIfNoneMatch() {
    return odataHeaders.getHeader(HttpHeader.IF_NONE_MATCH);
  }

  @Override
  public String getPrefer() {
    return odataHeaders.getHeader(HttpHeader.PREFER);
  }

  @Override
  public String getContentType() {
    final String contentTypeHead = odataHeaders.getHeader(HttpHeader.CONTENT_TYPE);
    return StringUtils.isBlank(contentTypeHead) ? getDefaultFormat().toContentTypeString() : contentTypeHead;
  }

  @Override
  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Gets request headers.
   *
   * @return request headers.
   */
  public ODataHeaders getHeader() {
    return odataHeaders;
  }

  @Override
  public byte[] toByteArray() {
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      final StringBuilder requestBuilder = new StringBuilder();
      requestBuilder.append(getMethod().toString()).append(' ').append(uri.toString()).append(' ').append("HTTP/1.1");

      baos.write(requestBuilder.toString().getBytes(DEFAULT_CHARSET));

      baos.write(CRLF);

      // Set Content-Type and Accept headers with default values, if not yet set
      if (StringUtils.isBlank(odataHeaders.getHeader(HttpHeader.CONTENT_TYPE))) {
        setContentType(getContentType());
      }
      if (StringUtils.isBlank(odataHeaders.getHeader(HttpHeader.ACCEPT))) {
        setAccept(getAccept());
      }

      for (String name : getHeaderNames()) {
        final String value = getHeader(name);

        if (StringUtils.isNotBlank(value)) {
          baos.write((name + ": " + value).getBytes(DEFAULT_CHARSET));
          baos.write(CRLF);
        }
      }

      return baos.toByteArray();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    } finally {
      IOUtils.closeQuietly(baos);
    }
  }

  @Override
  public InputStream rawExecute() {
     HttpEntity httpEntity = null;
    try {
       httpEntity = doExecute().getEntity();
      return httpEntity == null ? null : httpEntity.getContent();
    } catch (IOException e) {
      EntityUtils.consumeQuietly(httpEntity);
      throw new HttpClientException(e);
    } catch (RuntimeException e) {
      this.request.abort();
      EntityUtils.consumeQuietly(httpEntity);
      throw new HttpClientException(e);
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    }
  }

  /**
   * Builds the request and execute it.
   *
   * @return HttpReponse object.
   */
  protected ClassicHttpResponse doExecute() throws URISyntaxException, IOException {
    checkRequest(odataClient, request);

    // Set Content-Type and Accept headers with default values, if not yet set
    if (StringUtils.isBlank(odataHeaders.getHeader(HttpHeader.CONTENT_TYPE))) {
      setContentType(getContentType());
    }
    if (StringUtils.isBlank(odataHeaders.getHeader(HttpHeader.ACCEPT))) {
      setAccept(getAccept());
    }

    // Add header for KeyAsSegment management
    if (odataClient.getConfiguration().isKeyAsSegment()) {
      addCustomHeader("DataServiceUrlConventions", odataClient.newPreferences().keyAsSegment());
    }

    // Add all available headers
    for (String key : getHeaderNames()) {
      request.addHeader(key, odataHeaders.getHeader(key));
    }

    if (LOG.isDebugEnabled()) {
      for (Header header : request.getHeaders()) {
        LOG.debug("HTTP header being sent: " + header);
      }
    }

      ClassicHttpResponse response;
    try {
      response = httpClient.execute(request);
    } catch (IOException e) {
      throw new HttpClientException(request.getUri().toASCIIString(), e);
    } catch (RuntimeException e) {
      request.abort();
      throw new HttpClientException(request.getUri().toASCIIString(), e);
    }

    try {
      checkResponse(odataClient, response, getAccept());
    } catch (ODataRuntimeException e) {
      closeHttpResponse(response);
      odataClient.getConfiguration().getHttpClientFactory().close(httpClient);
      throw e;
    }

    return response;
  }

  private void closeHttpResponse(ClassicHttpResponse response) {
    if (response instanceof CloseableHttpResponse) {
      try {
        ((CloseableHttpResponse) response).close();
      } catch (IOException e) {
        LOG.warn("Unable to close response: {}", response, e);
      }
    }
  }

    /**
   * Gets an empty response that can be initialized by a stream.
   * <br/>
   * This method has to be used to build response items about a batch request.
   *
   * @param <V> ODataResponse type.
   * @return empty OData response instance.
   */
  @SuppressWarnings("unchecked")
  public <V extends ODataResponse> V getResponseTemplate() {
    for (Class<?> clazz : this.getClass().getDeclaredClasses()) {
      if (ODataResponse.class.isAssignableFrom(clazz)) {
        try {
          for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            final Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 4 && params[0] == this.getClass()
                && params[1].isAssignableFrom(ODataClient.class)
                && HttpClient.class.isAssignableFrom(params[2])
                && ClassicHttpResponse.class.isAssignableFrom(params[3])) {
              constructor.setAccessible(true);
              return (V) constructor.newInstance(this, odataClient, httpClient, null);
            }
          }
         } catch (Exception e) {
           LOG.error("Error retrieving response class template instance", e);
         }
       }
     }

    throw new IllegalStateException("No response class template has been found");
  }

  private CloseableHttpClient getHttpClient(final HttpMethod method, final URI uri) {
      CloseableHttpClient client =
              (CloseableHttpClient) odataClient.getConfiguration().getHttpClientFactory().create(method, uri);

      // HttpClient 5.x handles gzip automatically via interceptors
      return client;
  }
}

