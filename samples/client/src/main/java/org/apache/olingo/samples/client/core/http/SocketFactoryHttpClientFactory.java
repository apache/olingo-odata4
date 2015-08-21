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
package org.apache.olingo.samples.client.core.http;

import java.net.URI;
import java.security.cert.X509Certificate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.client.core.http.AbstractHttpClientFactory;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;

/**
 * Shows how to customize the way how the underlying network socket are managed by the HTTP component; the specific
 * sample is about how to trust self-signed SSL certificates and also empowers connection management.
 * <br/>
 * HTTP connections make use of a java.net.Socket object internally to handle transmission of data across the wire.
 * However they rely on the SchemeSocketFactory interface to create, initialize and connect sockets. This enables the
 * users of HttpClient to provide application specific socket initialization code at runtime. PlainSocketFactory is the
 * default factory for creating and initializing plain (unencrypted) sockets.
 * <a
 * href="http://svn.apache.org/repos/asf/httpcomponents/site/httpcomponents-client-4.2.x/tutorial/html/connmgmt.html#d5e512">More
 * information</a>.
 */
public class SocketFactoryHttpClientFactory extends AbstractHttpClientFactory {

  @Override
  public DefaultHttpClient create(final HttpMethod method, final URI uri) {
    final TrustStrategy acceptTrustStrategy = new TrustStrategy() {
      @Override
      public boolean isTrusted(final X509Certificate[] certificate, final String authType) {
        return true;
      }
    };

    final SchemeRegistry registry = new SchemeRegistry();
    try {
      final SSLSocketFactory ssf =
              new SSLSocketFactory(acceptTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      registry.register(new Scheme(uri.getScheme(), uri.getPort(), ssf));
    } catch (Exception e) {
      throw new ODataRuntimeException(e);
    }

    final DefaultHttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(registry));
    httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);

    return httpClient;
  }

  @Override
  public void close(final HttpClient httpClient) {
    httpClient.getConnectionManager().shutdown();
  }
}
