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
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.client.core.http.DefaultHttpUriRequestFactory;

/**
 * Shows how to customize the runtime behavior of an HTTP request.
 * <a
 * href="http://svn.apache.org/repos/asf/httpcomponents/site/httpcomponents-client-4.2.x/tutorial/html/fundamentals.html#d5e299">More
 * information</a>.
 *
 * @see ParametersHttpClientFactory for how to customize at whole client level
 */
public class ParametersHttpUriRequestFactory extends DefaultHttpUriRequestFactory {

  @Override
  public HttpUriRequest create(final HttpMethod method, final URI uri) {
    final HttpUriRequest request = super.create(method, uri);

    request.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_0);
    request.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

    final int timeout = 1000;
    HttpConnectionParams.setConnectionTimeout(request.getParams(), timeout);
    HttpConnectionParams.setSoTimeout(request.getParams(), timeout);

    return request;
  }

}
