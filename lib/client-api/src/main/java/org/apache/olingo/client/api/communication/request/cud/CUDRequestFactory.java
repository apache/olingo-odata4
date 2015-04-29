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
package org.apache.olingo.client.api.communication.request.cud;

import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;

public interface CUDRequestFactory {

  /**
   * Gets a create request object instance.
   * <br/>
   * Use this kind of request to create a new entity.
   *
   * @param <E> concrete ODataEntity implementation
   * @param targetURI entity set URI.
   * @param entity entity to be created.
   * @return new ODataEntityCreateRequest instance.
   */
  <E extends ClientEntity> ODataEntityCreateRequest<E> getEntityCreateRequest(URI targetURI, E entity);

  /**
   * Gets an update request object instance.
   *
   * @param <E> concrete ODataEntity implementation
   * @param targetURI edit link of the object to be updated.
   * @param type type of update to be performed.
   * @param changes changes to be applied.
   * @return new ODataEntityUpdateRequest instance.
   */
  <E extends ClientEntity> ODataEntityUpdateRequest<E> getEntityUpdateRequest(URI targetURI,
                                                                              UpdateType type, E changes);

  /**
   * Gets an update request object instance; uses entity's edit link as endpoint.
   *
   * @param <E> concrete ODataEntity implementation
   * @param type type of update to be performed.
   * @param entity changes to be applied.
   * @return new ODataEntityUpdateRequest instance.
   */
  <E extends ClientEntity> ODataEntityUpdateRequest<E> getEntityUpdateRequest(UpdateType type, E entity);

  /**
   * Gets a create request object instance.
   * <br/>
   * Use this kind of request to create a new value (e.g. http://Northwind.svc/Customer(1)/Picture/$value).
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param type type of update to be performed.
   * @param value value to be created.
   * @return new ODataValueUpdateRequest instance.
   */
  ODataValueUpdateRequest getValueUpdateRequest(URI targetURI, UpdateType type, ClientPrimitiveValue value);

  /**
   * Gets an update request object instance.
   * <br/>
   * Use this kind of request to update a primitive property value.
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param property value to be update.
   * @return new ODataPropertyUpdateRequest instance.
   */
  ODataPropertyUpdateRequest getPropertyPrimitiveValueUpdateRequest(URI targetURI, ClientProperty property);

  /**
   * Gets an update request object instance.
   * <br/>
   * Use this kind of request to update a complex property value.
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param type type of update to be performed.
   * @param property value to be update.
   * @return new ODataPropertyUpdateRequest instance.
   */
  ODataPropertyUpdateRequest
      getPropertyComplexValueUpdateRequest(URI targetURI, UpdateType type, ClientProperty property);

  /**
   * Gets an update request object instance.
   * <br/>
   * Use this kind of request to update a collection property value.
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param property value to be update.
   * @return new ODataPropertyUpdateRequest instance.
   */
  ODataPropertyUpdateRequest getPropertyCollectionValueUpdateRequest(URI targetURI, ClientProperty property);

  /**
   * Gets a delete request object instance.
   * <br/>
   * Use this kind of request to delete an entity and media entity as well.
   *
   * @param targetURI edit link of the object to be removed.
   * @return new ODataDeleteRequest instance.
   */
  ODataDeleteRequest getDeleteRequest(URI targetURI);

  /**
   * Gets a media entity create request object instance.
   * <br/>
   * Use this kind of request to create a new media entity.
   *
   * @param <E> concrete ODataEntity implementation
   * @param targetURI entity set URI.
   * @param media entity blob to be created.
   * @return new ODataMediaEntityCreateRequest instance.
   */
  <E extends ClientEntity> ODataMediaEntityCreateRequest<E> getMediaEntityCreateRequest(
      URI targetURI, InputStream media);

  /**
   * Gets a stream update request object instance.
   * <br/>
   * Use this kind of request to update a named stream property.
   *
   * @param targetURI target URI.
   * @param stream stream to be updated.
   * @return new ODataStreamUpdateRequest instance.
   */
  ODataStreamUpdateRequest getStreamUpdateRequest(URI targetURI, InputStream stream);

  /**
   * Gets a media entity update request object instance.
   * <br/>
   * Use this kind of request to update a media entity.
   *
   * @param <E> concrete ODataEntity implementation
   * @param editURI media entity edit link URI.
   * @param media entity blob to be updated.
   * @return new ODataMediaEntityUpdateRequest instance.
   */
  <E extends ClientEntity> ODataMediaEntityUpdateRequest<E> getMediaEntityUpdateRequest(
      URI editURI, InputStream media);

  ODataEntityUpdateRequest<ClientSingleton> getSingletonUpdateRequest(
      URI targetURI, UpdateType type, ClientSingleton changes);

  ODataEntityUpdateRequest<ClientSingleton> getSingletonUpdateRequest(
      UpdateType type, ClientSingleton entity);

  /**
   * A successful POST request to a navigation property's references collection adds a relationship to an existing
   * entity. The request body MUST contain a single entity reference that identifies the entity to be added.
   * [OData-Protocol 4.0 - 11.4.6.1]
   * 
   * @param serviceRoot serviceRoot URI
   * @param targetURI navigation property reference collection URI
   * @param reference entity reference
   * @return new ODataReferenceAddingRequest instance.
   */
  ODataReferenceAddingRequest getReferenceAddingRequest(URI serviceRoot, URI targetURI, URI reference);

  /**
   * A successful PUT request to a single-valued navigation property�s reference resource changes the related entity.
   * The request body MUST contain a single entity reference that identifies the existing entity to be related.
   * [OData-Protocol 4.0 - 11.4.6.3]
   * 
   * @param serviceRoot serviceRoot URI
   * @param targetURI single-valued navigation property URI
   * @param reference reference
   * @return new ODataReferenceAddingRequest instance
   */
  ODataReferenceAddingRequest getReferenceSingleChangeRequest(URI serviceRoot, URI targetURI, URI reference);
}
