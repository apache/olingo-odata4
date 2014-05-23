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
package org.apache.olingo.commons.api.domain.v4;

import java.net.URI;
import java.util.List;
import org.apache.olingo.commons.api.domain.CommonODataEntity;

public interface ODataEntity extends CommonODataEntity, ODataAnnotatable {

  @Override
  ODataProperty getProperty(String name);

  @Override
  List<ODataProperty> getProperties();

  /**
   * To request entity references in place of the actual entities, the client issues a GET request with /$ref appended
   * to the resource path.
   * <br />
   * If the resource path does not identify an entity or a collection of entities, the service returns 404 Not Found.
   * <br />
   * If the resource path terminates on a collection, the response MUST be the format-specific representation of a
   * collection of entity references pointing to the related entities. If no entities are related, the response is the
   * format-specific representation of an empty collection.
   * <br />
   * If the resource path terminates on a single entity, the response MUST be the format-specific representation of an
   * entity reference pointing to the related single entity. If the resource path terminates on a single entity and no
   * such entity exists, the service returns 404 Not Found.
   *
   * @return entity reference.
   */
  URI getId();

  void setId(URI id);

}
