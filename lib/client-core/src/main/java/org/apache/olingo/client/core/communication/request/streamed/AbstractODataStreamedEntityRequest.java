/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request.streamed;

import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataPayloadManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamedEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Abstract class representing a request concerning a streamed entity.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> OData request payload type corresponding to the request implementation.
 */
public abstract class AbstractODataStreamedEntityRequest<V extends ODataResponse, T extends ODataPayloadManager<V>>
    extends AbstractODataStreamedRequest<V, T>
    implements ODataStreamedEntityRequest<V, T> {

  private ContentType contentType;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method HTTP request method.
   * @param uri request URI.
   */
  public AbstractODataStreamedEntityRequest(final ODataClient odataClient, final HttpMethod method,
      final URI uri) {

    super(odataClient, method, uri);
    setAccept(getFormat().toContentTypeString());
  }

  @Override
  public final ContentType getFormat() {
    return contentType == null ? odataClient.getConfiguration().getDefaultPubFormat() : contentType;
  }

  @Override
  public final void setFormat(final ContentType contentType) {
    this.contentType = contentType;
    setAccept(contentType.toContentTypeString());
  }
}
