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

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

/**
 * Used for URI info kind {@link UriInfoKind#entityId} to describe URIs like
 * http://.../serviceroot/$entity...
 */
public interface UriInfoEntityId {

  /**
   * @return List of custom query options used in the URI (without alias definitions)
   */
  List<CustomQueryOption> getCustomQueryOptions();

  /**
   * Behind $entity a optional type cast can be used in the URI.
   * For example: http://.../serviceroot/$entity/namespace.entitytype
   * @return Type cast if found, otherwise null
   */
  EdmEntityType getEntityTypeCast();

  /**
   * @return Object containing information of the $expand option
   */
  ExpandOption getExpandOption();

  /**
   * @return Object containing information of the $format option
   */
  FormatOption getFormatOption();

  /**
   * @return Object containing information of the $id option
   */
  IdOption getIdOption();

  /**
   * @return Object containing information of the $select option
   */
  SelectOption getSelectOption();
}
