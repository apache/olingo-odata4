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

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.WrappingHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

public abstract class AbstractOAuth2HttpClientFactory
        extends AbstractHttpClientFactory implements WrappingHttpClientFactory {

  protected final DefaultHttpClientFactory wrapped;

  protected final URI oauth2GrantServiceURI;

  protected final URI oauth2TokenServiceURI;

  protected ClassicHttpRequest currentRequest;

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

  protected abstract void accessToken(CloseableHttpClient client) throws OAuth2Exception;

  protected abstract void refreshToken(CloseableHttpClient client) throws OAuth2Exception;

  @Override
  public CloseableHttpClient create(final HttpMethod method, final URI uri) {
    if (!isInited()) {
      init();
    }

      final HttpClientBuilder builder = HttpClients.custom();

      // holder to reference the built client from inside interceptors
      final CloseableHttpClient[] clientHolder = new CloseableHttpClient[1];

      // Request interceptor
      builder.addRequestInterceptorLast((request, entity, context) -> {
          if (request instanceof ClassicHttpRequest) {
              currentRequest = (ClassicHttpRequest) request;
          } else {
              currentRequest = null;
          }
      });

      // Response interceptor
      builder.addResponseInterceptorLast((response, entity, context) -> {
          if (response.getCode() == HttpStatus.SC_UNAUTHORIZED && clientHolder[0] != null) {
              refreshToken(clientHolder[0]);
          }
      });

      // build the client, store it to the holder, then perform initial access token request
      final CloseableHttpClient client = builder.build();
      clientHolder[0] = client;

      // perform access token operations with the real client
      accessToken(client);

      return client;
  }

  @Override
  public void close(final CloseableHttpClient httpClient) throws IOException {
    wrapped.close(httpClient);
  }

}
