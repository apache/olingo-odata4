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

import java.net.URI;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.http.HttpMethod;

public abstract class AbstractOAuth2HttpUriRequestFactory extends DefaultHttpUriRequestFactory {

  protected final URI redirectURI;

  public AbstractOAuth2HttpUriRequestFactory(final URI redirectURI) {
    this.redirectURI = redirectURI;
  }

  protected abstract boolean isInited();

  protected abstract void init() throws OAuth2Exception;

  protected abstract void sign(HttpUriRequest request);

  @Override
  public HttpUriRequest create(final HttpMethod method, final URI uri) {
    if (!isInited()) {
      init();
    }

    final HttpUriRequest request = super.create(method, uri);

    sign(request);

    return request;
  }

}
