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
package org.apache.olingo.server.api.uri;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmType;

/**
 * Used to describe an entity set used within an resource path
 * For example: http://.../serviceroot/entityset(1)
 */
public interface UriResourceEntitySet extends UriResourcePartTyped {

  /**
   * @return Entity set used in the resource path
   */
  EdmEntitySet getEntitySet();

  /**
   * @return Type of the entity set
   */
  EdmEntityType getEntityType();

  /**
   * @return Key predicates if used, otherwise null
   */
  List<UriParameter> getKeyPredicates();

  /**
   * @return Type filter before key predicates if used, otherwise null
   */
  EdmType getTypeFilterOnCollection();

  /**
   * @return Type filter behind key predicates if used, otherwise null
   */
  EdmType getTypeFilterOnEntry();

}
