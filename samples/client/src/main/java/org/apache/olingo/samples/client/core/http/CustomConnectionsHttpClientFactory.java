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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionOperator;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.impl.conn.DefaultHttpResponseParser;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicLineParser;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.CharArrayBuffer;
import org.apache.olingo.client.core.http.AbstractHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Shows how to use custom client connections.
 * <br/>
 * In certain situations it may be necessary to customize the way HTTP messages get transmitted across the wire beyond
 * what is possible using HTTP parameters in order to be able to deal non-standard, non-compliant behaviours. For
 * instance, for web crawlers it may be necessary to force HttpClient into accepting malformed response heads in order
 * to salvage the content of the messages.
 * <a
 * href="http://svn.apache.org/repos/asf/httpcomponents/site/httpcomponents-client-4.2.x/tutorial/html/advanced.html#d5e1339">More
 * information</a>.
 */
public class CustomConnectionsHttpClientFactory extends AbstractHttpClientFactory {

  private static class MyLineParser extends BasicLineParser {

    @Override
    public Header parseHeader(final CharArrayBuffer buffer) throws ParseException {
      try {
        return super.parseHeader(buffer);
      } catch (ParseException ex) {
        // Suppress ParseException exception
        return new BasicHeader("invalid", buffer.toString());
      }
    }

  }

  private static class MyClientConnection extends DefaultClientConnection {

    @Override
    protected HttpMessageParser<HttpResponse> createResponseParser(
            final SessionInputBuffer buffer,
            final HttpResponseFactory responseFactory,
            final HttpParams params) {

      return new DefaultHttpResponseParser(
              buffer,
              new MyLineParser(),
              responseFactory,
              params);
    }

  }

  private static class MyClientConnectionOperator extends DefaultClientConnectionOperator {

    public MyClientConnectionOperator(final SchemeRegistry registry) {
      super(registry);
    }

    @Override
    public OperatedClientConnection createConnection() {
      return new MyClientConnection();
    }

  }

  private static class MyClientConnManager extends BasicClientConnectionManager {

    @Override
    protected ClientConnectionOperator createConnectionOperator(final SchemeRegistry registry) {
      return new MyClientConnectionOperator(registry);
    }

  }

  @Override
  public DefaultHttpClient create(final HttpMethod method, final URI uri) {
    final DefaultHttpClient httpClient = new DefaultHttpClient(new MyClientConnManager());
    httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);

    return httpClient;
  }

  @Override
  public void close(final HttpClient httpClient) {
    httpClient.getConnectionManager().shutdown();
  }
}
