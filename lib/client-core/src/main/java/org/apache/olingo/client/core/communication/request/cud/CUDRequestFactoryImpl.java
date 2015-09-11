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
package org.apache.olingo.client.core.communication.request.cud;

import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.CUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.client.core.communication.request.streamed.ODataMediaEntityCreateRequestImpl;
import org.apache.olingo.client.core.communication.request.streamed.ODataMediaEntityUpdateRequestImpl;
import org.apache.olingo.client.core.communication.request.streamed.ODataStreamUpdateRequestImpl;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.http.HttpMethod;

public class CUDRequestFactoryImpl implements CUDRequestFactory {

  protected final ODataClient client;

  public CUDRequestFactoryImpl(final ODataClient client) {
    this.client = client;
  }

  @Override
  public <E extends ClientEntity> ODataEntityCreateRequest<E> getEntityCreateRequest(
      final URI targetURI, final E entity) {

    return new ODataEntityCreateRequestImpl<E>(client, targetURI, entity);
  }

  @Override
  public <E extends ClientEntity> ODataEntityUpdateRequest<E> getEntityUpdateRequest(
      final URI targetURI, final UpdateType type, final E changes) {

    final ODataEntityUpdateRequest<E> req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataEntityUpdateRequestImpl<E>(client, HttpMethod.POST, targetURI, changes);
      req.setXHTTPMethod(type.getMethod().name());
    } else {
      req = new ODataEntityUpdateRequestImpl<E>(client, type.getMethod(), targetURI, changes);
    }

    return req;
  }

  @Override
  public <E extends ClientEntity> ODataEntityUpdateRequest<E> getEntityUpdateRequest(
      final UpdateType type, final E entity) {

    if (entity.getEditLink() == null) {
      throw new IllegalArgumentException("No edit link found");
    }

    final ODataEntityUpdateRequest<E> req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataEntityUpdateRequestImpl<E>(client, HttpMethod.POST, entity.getEditLink(), entity);
      req.setXHTTPMethod(type.getMethod().name());
    } else {
      req = new ODataEntityUpdateRequestImpl<E>(client, type.getMethod(), entity.getEditLink(), entity);
    }

    return req;
  }

  @Override
  public ODataValueUpdateRequest getValueUpdateRequest(
      final URI targetURI, final UpdateType type, final ClientPrimitiveValue value) {

    final ODataValueUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataValueUpdateRequestImpl(client, HttpMethod.POST, URIUtils.addValueSegment(targetURI), value);
      req.setXHTTPMethod(type.getMethod().name());
    } else {
      req = new ODataValueUpdateRequestImpl(client, type.getMethod(), URIUtils.addValueSegment(targetURI), value);
    }

    return req;
  }

  @Override
  public ODataPropertyUpdateRequest getPropertyPrimitiveValueUpdateRequest(
      final URI targetURI, final ClientProperty property) {

    if (!property.hasPrimitiveValue()) {
      throw new IllegalArgumentException("A primitive value is required");
    }

    final ODataPropertyUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataPropertyUpdateRequestImpl(client, HttpMethod.POST, targetURI, property);
      req.setXHTTPMethod(HttpMethod.PUT.name());
    } else {
      req = new ODataPropertyUpdateRequestImpl(client, HttpMethod.PUT, targetURI, property);
    }

    return req;
  }

  @Override
  public ODataPropertyUpdateRequest getPropertyComplexValueUpdateRequest(
      final URI targetURI, final UpdateType type, final ClientProperty property) {

    if (!property.hasComplexValue()) {
      throw new IllegalArgumentException("A complex value is required");
    }

    final ODataPropertyUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataPropertyUpdateRequestImpl(client, HttpMethod.POST, targetURI, property);
      req.setXHTTPMethod(type.getMethod().name());
    } else {
      req = new ODataPropertyUpdateRequestImpl(client, type.getMethod(), targetURI, property);
    }

    return req;
  }

  @Override
  public ODataPropertyUpdateRequest getPropertyCollectionValueUpdateRequest(
      final URI targetURI, final ClientProperty property) {

    if (!property.hasCollectionValue()) {
      throw new IllegalArgumentException("A collection value is required");
    }

    final ODataPropertyUpdateRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataPropertyUpdateRequestImpl(client, HttpMethod.POST, targetURI, property);
      req.setXHTTPMethod(HttpMethod.PUT.name());
    } else {
      req = new ODataPropertyUpdateRequestImpl(client, HttpMethod.PUT, targetURI, property);
    }

    return req;
  }

  @Override
  public ODataDeleteRequest getDeleteRequest(final URI targetURI) {
    final ODataDeleteRequest req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataDeleteRequestImpl(client, HttpMethod.POST, targetURI);
      req.setXHTTPMethod(HttpMethod.DELETE.name());
    } else {
      req = new ODataDeleteRequestImpl(client, HttpMethod.DELETE, targetURI);
    }

    return req;
  }

  @Override
  public <E extends ClientEntity> ODataMediaEntityCreateRequest<E> getMediaEntityCreateRequest(
      final URI targetURI, final InputStream media) {

    return new ODataMediaEntityCreateRequestImpl<E>(client, targetURI, media);
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
  public <E extends ClientEntity> ODataMediaEntityUpdateRequest<E> getMediaEntityUpdateRequest(
      final URI editURI, final InputStream media) {

    final ODataMediaEntityUpdateRequest<E> req;

    if (client.getConfiguration().isUseXHTTPMethod()) {
      req = new ODataMediaEntityUpdateRequestImpl<E>(client, HttpMethod.POST, URIUtils.addValueSegment(editURI), media);
      req.setXHTTPMethod(HttpMethod.PUT.name());
    } else {
      req = new ODataMediaEntityUpdateRequestImpl<E>(client, HttpMethod.PUT, URIUtils.addValueSegment(editURI), media);
    }

    return req;
  }

  @Override
  public ODataEntityUpdateRequest<ClientSingleton> getSingletonUpdateRequest(
      final UpdateType type, final ClientSingleton entity) {

    return getEntityUpdateRequest(type, entity);
  }

  @Override
  public ODataEntityUpdateRequest<ClientSingleton> getSingletonUpdateRequest(
      final URI targetURI, final UpdateType type, final ClientSingleton changes) {

    return getEntityUpdateRequest(targetURI, type, changes);
  }

  @Override
  public ODataReferenceAddingRequest getReferenceAddingRequest(final URI serviceRoot, final URI targetURI,
      final URI reference) {
    final URI contextURI = client.newURIBuilder(serviceRoot.toASCIIString()).appendMetadataSegment()
                                                                            .appendRefSegment().build();
    ResWrap<URI> wrappedPayload = new ResWrap<URI>(contextURI, null, reference);

    return new ODataReferenceAddingRequestImpl(client, HttpMethod.POST, targetURI, wrappedPayload);
  }

  @Override
  public ODataReferenceAddingRequest getReferenceSingleChangeRequest(final URI serviceRoot, final URI targetURI,
      final URI reference) {
    // See OData Protocol 11.4.6.3
    final URI contextURI = client.newURIBuilder(serviceRoot.toASCIIString()).appendMetadataSegment().build();
    ResWrap<URI> wrappedPayload = new ResWrap<URI>(contextURI, null, reference);

    return new ODataReferenceAddingRequestImpl(client, HttpMethod.PUT, targetURI, wrappedPayload);
  }
}
