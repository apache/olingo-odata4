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

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

/**
 * Used for URI-related tasks.
 */
public interface UriHelper {

  /**
   * Builds the select-list part of a {@link org.apache.olingo.commons.api.data.ContextURL ContextURL}.
   * @param type   the {@link EdmStructuredType}
   * @param expand the $expand option
   * @param select the $select option
   * @return a String with the select list
   */
  String buildContextURLSelectList(EdmStructuredType type, ExpandOption expand, SelectOption select)
      throws SerializerException;

  /**
   * Builds the key-predicate part of a {@link org.apache.olingo.commons.api.data.ContextURL ContextURL}.
   * @param keys the keys as a list of {@link UriParameter} instances
   * @return a String with the key predicate
   */
  String buildContextURLKeyPredicate(List<UriParameter> keys) throws SerializerException;

  /**
   * Builds the relative canonical URL for the given entity in the given entity set.
   * @param edmEntitySet the entity set
   * @param entity       the entity data
   * @return the relative canonical URL
   */
  String buildCanonicalURL(EdmEntitySet edmEntitySet, Entity entity) throws SerializerException;

  /**
   * Builds the key predicate for the given entity.
   * @param edmEntityType the entity type of the entity
   * @param entity        the entity data
   * @return the key predicate
   */
  String buildKeyPredicate(EdmEntityType edmEntityType, Entity entity) throws SerializerException;
}
