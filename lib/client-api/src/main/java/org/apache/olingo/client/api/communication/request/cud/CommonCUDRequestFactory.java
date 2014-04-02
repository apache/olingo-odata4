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
package org.apache.olingo.client.api.communication.request.cud;

import java.io.Serializable;
import java.net.URI;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.CommonODataProperty;

/**
 * OData request factory class.
 */
public interface CommonCUDRequestFactory extends Serializable {

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
  <E extends CommonODataEntity> ODataEntityCreateRequest<E> getEntityCreateRequest(URI targetURI, E entity);

  /**
   * Gets an update request object instance.
   *
   * @param <UT> concrete UpdateType.
   * @param targetURI edit link of the object to be updated.
   * @param type type of update to be performed.
   * @param changes changes to be applied.
   * @return new ODataEntityUpdateRequest instance.
   */
  <UT extends UpdateType> ODataEntityUpdateRequest getEntityUpdateRequest(URI targetURI, UT type,
          CommonODataEntity changes);

  /**
   * Gets an update request object instance; uses entity's edit link as endpoint.
   *
   * @param type type of update to be performed.
   * @param entity changes to be applied.
   * @return new ODataEntityUpdateRequest instance.
   */
  ODataEntityUpdateRequest getEntityUpdateRequest(UpdateType type, CommonODataEntity entity);

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
  ODataValueUpdateRequest getValueUpdateRequest(URI targetURI, UpdateType type, ODataPrimitiveValue value);

  /**
   * Gets an update request object instance.
   * <br/>
   * Use this kind of request to update a primitive property value.
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param property value to be update.
   * @return new ODataPropertyUpdateRequest instance.
   */
  ODataPropertyUpdateRequest getPropertyPrimitiveValueUpdateRequest(URI targetURI, CommonODataProperty property);

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
  ODataPropertyUpdateRequest getPropertyComplexValueUpdateRequest(
          URI targetURI, UpdateType type, CommonODataProperty property);

  /**
   * Gets an update request object instance.
   * <br/>
   * Use this kind of request to update a collection property value.
   *
   * @param targetURI entity set or entity or entity property URI.
   * @param property value to be update.
   * @return new ODataPropertyUpdateRequest instance.
   */
  ODataPropertyUpdateRequest getPropertyCollectionValueUpdateRequest(URI targetURI, CommonODataProperty property);

  /**
   * Gets an add link request object instance.
   * <br/>
   * Use this kind of request to create a navigation link between existing entities.
   *
   * @param targetURI navigation property's link collection.
   * @param link navigation link to be added.
   * @return new ODataLinkCreateRequest instance.
   */
  ODataLinkCreateRequest getLinkCreateRequest(URI targetURI, ODataLink link);

  /**
   * Gets a link update request object instance.
   * <br/>
   * Use this kind of request to update a navigation link between existing entities.
   * <br/>
   * In case of the old navigation link doesn't exist the new one will be added as well.
   *
   * @param targetURI navigation property's link collection.
   * @param type type of update to be performed.
   * @param link URL that identifies the entity to be linked.
   * @return new ODataLinkUpdateRequest instance.
   */
  ODataLinkUpdateRequest getLinkUpdateRequest(URI targetURI, UpdateType type, ODataLink link);

  /**
   * Gets a delete request object instance.
   * <br/>
   * Use this kind of request to delete an entity and media entity as well.
   *
   * @param targetURI edit link of the object to be removed.
   * @return new ODataDeleteRequest instance.
   */
  ODataDeleteRequest getDeleteRequest(URI targetURI);
}
