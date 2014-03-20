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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;

public abstract class AbstractRetrieveRequestFactory implements RetrieveRequestFactory {

  private static final long serialVersionUID = -111683263158803362L;

  protected final ODataClient client;

  protected AbstractRetrieveRequestFactory(final ODataClient client) {
    this.client = client;
  }

  @Override
  public ODataEntitySetRequest getEntitySetRequest(final URI query) {
    return new ODataEntitySetRequestImpl(client, query);
  }

  @Override
  public ODataEntitySetIteratorRequest getEntitySetIteratorRequest(final URI query) {
    return new ODataEntitySetIteratorRequestImpl(client, query);
  }

  @Override
  public ODataEntityRequest getEntityRequest(final URI query) {
    return new ODataEntityRequestImpl(client, query);
  }

  @Override
  public ODataPropertyRequest getPropertyRequest(final URI query) {
    return new ODataPropertyRequestImpl(client, query);
  }

  @Override
  public ODataValueRequest getValueRequest(final URI query) {
    return new ODataValueRequestImpl(client, query);
  }

  @Override
  public ODataMediaRequest getMediaRequest(final URI query) {
    return new ODataMediaRequestImpl(client, query);
  }

  @Override
  public ODataRawRequest getRawRequest(final URI uri) {
    return new ODataRawRequestImpl(client, uri);
  }

  @Override
  public ODataMetadataRequest getMetadataRequest(final String serviceRoot) {
    return new ODataMetadataRequestImpl(client, client.getURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataServiceDocumentRequest getServiceDocumentRequest(final String serviceRoot) {
    return new ODataServiceDocumentRequestImpl(client,
            StringUtils.isNotBlank(serviceRoot) && serviceRoot.endsWith("/")
            ? client.getURIBuilder(serviceRoot).build()
            : client.getURIBuilder(serviceRoot + "/").build());
  }
}
