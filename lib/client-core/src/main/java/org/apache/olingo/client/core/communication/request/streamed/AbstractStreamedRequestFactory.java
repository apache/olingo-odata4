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
package org.apache.olingo.client.core.communication.request.streamed;

import java.io.InputStream;
import java.net.URI;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.StreamedRequestFactory;
import org.apache.olingo.client.api.http.HttpMethod;

public abstract class AbstractStreamedRequestFactory implements StreamedRequestFactory {

  private static final long serialVersionUID = -2438839640443961168L;

  protected final ODataClient client;

  protected AbstractStreamedRequestFactory(final ODataClient client) {
    this.client = client;
  }

  @Override
  public ODataMediaEntityCreateRequest getMediaEntityCreateRequest(
          final URI targetURI, final InputStream media) {

    return new ODataMediaEntityCreateRequestImpl(client, targetURI, media);
  }

  @Override
  public ODataStreamUpdateRequest getStreamUpdateRequest(final URI targetURI, final InputStream stream) {
    final ODataStreamUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataStreamUpdateRequestImpl(client, HttpMethod.POST, targetURI, stream);
      req.setXHTTPMethod(HttpMethod.PUT.name());
    } else {
      req = new ODataStreamUpdateRequestImpl(client, HttpMethod.PUT, targetURI, stream);
    }

    return req;
  }

  @Override
  public ODataMediaEntityUpdateRequest getMediaEntityUpdateRequest(
          final URI editURI, final InputStream media) {

    final ODataMediaEntityUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataMediaEntityUpdateRequestImpl(client, HttpMethod.POST, editURI, media);
      req.setXHTTPMethod(HttpMethod.PUT.name());
    } else {
      req = new ODataMediaEntityUpdateRequestImpl(client, HttpMethod.PUT, editURI, media);
    }

    return req;
  }
}
