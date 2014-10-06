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
package org.apache.olingo.client.core.http;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.WrappingHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

public abstract class AbstractOAuth2HttpClientFactory
        extends AbstractHttpClientFactory implements WrappingHttpClientFactory {

  protected final DefaultHttpClientFactory wrapped;

  protected final URI oauth2GrantServiceURI;

  protected final URI oauth2TokenServiceURI;

  protected HttpUriRequest currentRequest;

  public AbstractOAuth2HttpClientFactory(final URI oauth2GrantServiceURI, final URI oauth2TokenServiceURI) {
    this(new DefaultHttpClientFactory(), oauth2GrantServiceURI, oauth2TokenServiceURI);
  }

  public AbstractOAuth2HttpClientFactory(final DefaultHttpClientFactory wrapped,
          final URI oauth2GrantServiceURI, final URI oauth2TokenServiceURI) {

    super();
    this.wrapped = wrapped;
    this.oauth2GrantServiceURI = oauth2GrantServiceURI;
    this.oauth2TokenServiceURI = oauth2TokenServiceURI;
  }

  @Override
  public HttpClientFactory getWrappedHttpClientFactory() {
    return wrapped;
  }

  protected abstract boolean isInited() throws OAuth2Exception;

  protected abstract void init() throws OAuth2Exception;

  protected abstract void accessToken(DefaultHttpClient client) throws OAuth2Exception;

  protected abstract void refreshToken(DefaultHttpClient client) throws OAuth2Exception;

  @Override
  public HttpClient create(final HttpMethod method, final URI uri) {
    if (!isInited()) {
      init();
    }

    final DefaultHttpClient httpClient = wrapped.create(method, uri);
    accessToken(httpClient);

    httpClient.addRequestInterceptor(new HttpRequestInterceptor() {

      @Override
      public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        if (request instanceof HttpUriRequest) {
          currentRequest = (HttpUriRequest) request;
        } else {
          currentRequest = null;
        }
      }
    });
    httpClient.addResponseInterceptor(new HttpResponseInterceptor() {

      @Override
      public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
          refreshToken(httpClient);

          if (currentRequest != null) {
            httpClient.execute(currentRequest);
          }
        }
      }
    });

    return httpClient;
  }

  @Override
  public void close(final HttpClient httpClient) {
    wrapped.close(httpClient);
  }

}
