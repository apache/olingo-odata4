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
package org.apache.olingo.client.api.communication.request.cud.v3;

import java.net.URI;
import org.apache.olingo.client.api.communication.request.cud.CommonCUDRequestFactory;
import org.apache.olingo.commons.api.domain.ODataLink;

public interface CUDRequestFactory extends CommonCUDRequestFactory<UpdateType> {

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
}
