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

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.http.HttpUriRequestFactory;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Default implementation returning default HttpUriRequest implementations.
 */
public class DefaultHttpUriRequestFactory implements HttpUriRequestFactory {

  @Override
  public HttpUriRequest create(final HttpMethod method, final URI uri) {
    HttpUriRequest result;

    switch (method) {
      case POST:
        result = new HttpPost(uri);
        break;

      case PUT:
        result = new HttpPut(uri);
        break;

      case PATCH:
        result = new HttpPatch(uri);
        break;

      case MERGE:
        result = new HttpMerge(uri);
        break;

      case DELETE:
        result = new HttpDelete(uri);
        break;

      case GET:
      default:
        result = new HttpGet(uri);
        break;
    }

    return result;
  }
}
