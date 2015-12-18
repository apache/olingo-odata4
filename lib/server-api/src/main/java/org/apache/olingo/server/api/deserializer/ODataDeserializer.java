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
package org.apache.olingo.server.api.deserializer;

import java.io.InputStream;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;

/**
 * Deserializer on OData server side.
 */
public interface ODataDeserializer {

  /**
   * Deserializes an entity stream into an {@link org.apache.olingo.commons.api.data.Entity Entity} object.
   * Validates: property types, no double properties, correct json types.
   * Returns a deserialized {@link org.apache.olingo.commons.api.data.Entity Entity} object and an
   * {@link org.apache.olingo.server.api.uri.queryoption.ExpandOption ExpandOption} object.
   * @param stream
   * @param edmEntityType
   * @return {@link DeserializerResult#getEntity()} and {@link DeserializerResult#getExpandTree()}
   * @throws DeserializerException
   */
  DeserializerResult entity(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException;

  /**
   * Deserializes an entity collection stream into an {@link org.apache.olingo.commons.api.data.EntityCollection
   * EntityCollection} object.
   * @param stream
   * @param edmEntityType
   * @return {@link DeserializerResult#getEntityCollection()}
   * @throws DeserializerException
   */
  DeserializerResult entityCollection(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException;

  /**
   * Deserializes an action-parameters stream into a map of key/value pairs.
   * Validates: parameter types, no double parameters, correct json types.
   * @param stream
   * @param edmAction
   * @return {@link DeserializerResult#getActionParameters()}
   * @throws DeserializerException
   */
  DeserializerResult actionParameters(InputStream stream, EdmAction edmAction) throws DeserializerException;

  /**
   * Deserializes the Property or collections of properties (primitive & complex).
   * @param stream
   * @param edmProperty
   * @return {@link DeserializerResult#getProperty()}
   * @throws DeserializerException
   */
  DeserializerResult property(InputStream stream, EdmProperty edmProperty) throws DeserializerException;

  /**
   * Reads entity references from the provided document.
   * @param stream
   * @param keys
   * @return {@link DeserializerResult#getEntityReferences()}
   * @throws DeserializerException
   */
  DeserializerResult entityReferences(InputStream stream) throws DeserializerException;
}
