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

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHost;
import org.apache.olingo.client.api.http.WrappingHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Implementation for working behind an HTTP proxy (possibly requiring authentication); requires another concrete
 * {@link org.apache.olingo.client.api.http.HttpClientFactory} implementation acting as real HTTP client factory.
 */
public class ProxyWrappingHttpClientFactory implements WrappingHttpClientFactory {

  private final URI proxy;

  private String proxyUsername;

  private String proxyPassword;

  private final DefaultHttpClientFactory wrapped;

  public ProxyWrappingHttpClientFactory(final URI proxy) {
    this(proxy, null, null, new DefaultHttpClientFactory());
  }

  public ProxyWrappingHttpClientFactory(final URI proxy, final String proxyUsername, final String proxyPassword) {
    this(proxy, proxyUsername, proxyPassword, new DefaultHttpClientFactory());
  }

  public ProxyWrappingHttpClientFactory(final URI proxy, final DefaultHttpClientFactory wrapped) {
    this(proxy, null, null, wrapped);
  }

  public ProxyWrappingHttpClientFactory(final URI proxy,
          final String proxyUsername, final String proxyPassword, final DefaultHttpClientFactory wrapped) {

    this.proxy = proxy;
    this.proxyUsername = proxyUsername;
    this.proxyPassword = proxyPassword;
    this.wrapped = wrapped;
  }

  @Override
  public DefaultHttpClientFactory getWrappedHttpClientFactory() {
    return this.wrapped;
  }

  @Override
  public HttpClient create(final HttpMethod method, final URI uri) {
    // Use wrapped factory to obtain an httpclient instance for given method and uri
      CloseableHttpClient baseClient = wrapped.create(method, uri);

    final HttpHost proxyHost = new HttpHost(proxy.getHost(), proxy.getPort());
    CredentialsProvider credentialsProvider = null;

    // Sets proxy authentication, if credentials were provided
    if (proxyUsername != null && proxyPassword != null) {
        BasicCredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(proxyHost),
              new UsernamePasswordCredentials(proxyUsername, proxyPassword.toCharArray()));
        credentialsProvider = provider;
    }

      // Build a new immutable HttpClient with proxy settings
      return HttpClients.custom()
              .setProxy(proxyHost)
              .setDefaultCredentialsProvider(credentialsProvider)
              .setConnectionManagerShared(true)
              .build();
  }

  @Override
  public void close(final CloseableHttpClient httpClient) throws IOException {
    wrapped.close(httpClient);
  }

}
