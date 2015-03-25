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

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

/**
 * Deserializer on OData server side.
 */
public interface ODataDeserializer {

  /**
   * Deserializes an entity stream into an {@link Entity} object.
   * Validates: property types, no double properties, correct json types 
   * Returns a deserialized {@link Entity} object and an {@link ExpandOption} object
   * @param stream
   * @param edmEntityType
   * @return {@link DeserializerResult}
   * @throws DeserializerException
   */
  DeserializerResult entity(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException;

  /**
   * Deserializes an entity collection stream into an {@link EntitySet} object.
   * Returns a deserialized {@link EntitySet} object
   * @param stream
   * @param edmEntityType
   * @return {@link DeserializerResult}
   * @throws DeserializerException
   */
  DeserializerResult entityCollection(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException;
  
  

}
