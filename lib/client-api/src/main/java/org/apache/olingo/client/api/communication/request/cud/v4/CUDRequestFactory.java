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
package org.apache.olingo.client.api.communication.request.cud.v4;

import org.apache.olingo.client.api.communication.request.cud.CommonCUDRequestFactory;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.commons.api.domain.v4.ODataSingleton;

import java.net.URI;

public interface CUDRequestFactory extends CommonCUDRequestFactory<UpdateType> {

  ODataEntityUpdateRequest<ODataSingleton> getSingletonUpdateRequest(
          URI targetURI, UpdateType type, ODataSingleton changes);

  ODataEntityUpdateRequest<ODataSingleton> getSingletonUpdateRequest(
          UpdateType type, ODataSingleton entity);

  /**
   * A successful POST request to a navigation property's references collection adds a relationship to an existing
   * entity. The request body MUST contain a single entity reference that identifies the entity to be added. See the
   * appropriate format document for details. On successful completion, the response MUST be 204 No Content and contain
   * an empty body.
   *
   * @param <E> concrete ODataEntity implementation
   * @param targetURI entity set URI.
   * @param entity entity to be created.
   * @return new ODataEntityCreateRequest instance.
   */
  ODataReferenceAddingRequest getReferenceAddingRequest(URI targetURI, URI reference);
}
