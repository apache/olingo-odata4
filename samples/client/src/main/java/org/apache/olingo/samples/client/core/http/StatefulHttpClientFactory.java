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
import org.apache.http.client.UserTokenHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;

/**
 * Shows how to work with stateful HTTP connections.
 * <br/>
 * HttpClient relies on <tt>UserTokenHandler</tt> interface to determine if the given execution context is user specific
 * or not. The token object returned by this handler is expected to uniquely identify the current user if the context is
 * user specific or to be null if the context does not contain any resources or details specific to the current user.
 * The user token will be used to ensure that user specific resources will not be shared with or reused by other users.
 * <a
 * href="http://svn.apache.org/repos/asf/httpcomponents/site/httpcomponents-client-4.2.x/tutorial/html/advanced.html#stateful_conn">More
 * information</a>.
 */
public class StatefulHttpClientFactory extends DefaultHttpClientFactory {

  @Override
  public DefaultHttpClient create(final HttpMethod method, final URI uri) {
    final DefaultHttpClient httpClient = super.create(method, uri);

    httpClient.setUserTokenHandler(new UserTokenHandler() {

      @Override
      public Object getUserToken(final HttpContext context) {
        return context.getAttribute("my-token");
      }

    });

    return httpClient;
  }

}
