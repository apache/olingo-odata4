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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataDeltaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.retrieve.RetrieveRequestFactory;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.client.core.uri.URIUtils;

public class RetrieveRequestFactoryImpl implements RetrieveRequestFactory {

  protected final ODataClient client;

  public RetrieveRequestFactoryImpl(final ODataClient client) {
    this.client = client;
  }

  @Override
  public ODataValueRequest getValueRequest(final URI uri) {
    return new ODataValueRequestImpl(client, uri);
  }

  @Override
  public ODataValueRequest getPropertyValueRequest(final URI uri) {
    return getValueRequest(URIUtils.addValueSegment(uri));
  }

  @Override
  public ODataMediaRequest getMediaRequest(final URI uri) {
    return new ODataMediaRequestImpl(client, uri);
  }

  @Override
  public ODataMediaRequest getMediaEntityRequest(final URI uri) {
    return getMediaRequest(URIUtils.addValueSegment(uri));
  }

  @Override
  public ODataRawRequest getRawRequest(final URI uri) {
    return new ODataRawRequestImpl(client, uri);
  }

  @Override
  public EdmMetadataRequest getMetadataRequest(final String serviceRoot) {
    return new EdmMetadataRequestImpl(client, serviceRoot,
        client.newURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataServiceDocumentRequest getServiceDocumentRequest(final String serviceRoot) {
    return new ODataServiceDocumentRequestImpl(client,
        serviceRoot != null && !serviceRoot.isEmpty() && serviceRoot.endsWith("/") ?
            client.newURIBuilder(serviceRoot).build() :
            client.newURIBuilder(serviceRoot + '/').build());
  }

  @Override
  public XMLMetadataRequest getXMLMetadataRequest(final String serviceRoot) {
    return new XMLMetadataRequestImpl(client, client.newURIBuilder(serviceRoot).appendMetadataSegment().build());
  }

  @Override
  public ODataEntitySetRequest<ClientEntitySet> getEntitySetRequest(final URI uri) {
    return new ODataEntitySetRequestImpl<ClientEntitySet>(client, uri);
  }

  @Override
  public ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> getEntitySetIteratorRequest(final URI uri) {
    return new ODataEntitySetIteratorRequestImpl<ClientEntitySet, ClientEntity>(client, uri);
  }

  @Override
  public ODataEntityRequest<ClientSingleton> getSingletonRequest(final URI uri) {
    return new ODataEntityRequestImpl<ClientSingleton>(client, uri);
  }

  @Override
  public ODataEntityRequest<ClientEntity> getEntityRequest(final URI uri) {
    return new ODataEntityRequestImpl<ClientEntity>(client, uri);
  }

  @Override
  public ODataPropertyRequest<ClientProperty> getPropertyRequest(final URI uri) {
    return new ODataPropertyRequestImpl<ClientProperty>(client, uri);
  }

  @Override
  public ODataDeltaRequest getDeltaRequest(final URI uri) {
    return new ODataDeltaRequestImpl(client, uri);
  }
}
