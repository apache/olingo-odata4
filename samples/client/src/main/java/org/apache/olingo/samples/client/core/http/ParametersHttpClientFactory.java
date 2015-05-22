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

import org.apache.http.HttpVersion;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Shows how to customize the runtime behavior of HTTP client component.
 * <a
 * href="http://svn.apache.org/repos/asf/httpcomponents/site/httpcomponents-client-4.2.x/tutorial/html/fundamentals.html#d5e299">More
 * information</a>.
 *
 * @see ParametersHttpUriRequestFactory for how to customize at request level
 */
public class ParametersHttpClientFactory extends DefaultHttpClientFactory {

  @Override
  public DefaultHttpClient create(final HttpMethod method, final URI uri) {
    final DefaultHttpClient httpClient = super.create(method, uri);

    httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
    httpClient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

    final int timeout = 1000;
    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), timeout);
    HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);

    return httpClient;
  }

}
