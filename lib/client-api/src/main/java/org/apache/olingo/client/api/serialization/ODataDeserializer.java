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
package org.apache.olingo.client.api.serialization;

import java.io.InputStream;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.ex.ODataError;

/**
 * Interface for de-serialization.
 */
public interface ODataDeserializer {

  /**
   * Gets an entity set object from the given InputStream.
   *
   * @param input stream to be de-serialized.
   * @return {@link EntityCollection} instance.
   */
  ResWrap<EntityCollection> toEntitySet(InputStream input) throws ODataDeserializerException;

  /**
   * Gets an entity object from the given InputStream.
   *
   * @param input stream to be de-serialized.
   * @return {@link Entity} instance.
   */
  ResWrap<Entity> toEntity(InputStream input) throws ODataDeserializerException;

  /**
   * Gets a property object from the given InputStream.
   *
   * @param input stream to be de-serialized.
   * @return Property instance.
   */
  ResWrap<Property> toProperty(InputStream input) throws ODataDeserializerException;

  /**
   * Gets the ODataError object represented by the given InputStream.
   *
   * @param input stream to be parsed and de-serialized.
   * @return parsed ODataError object represented by the given InputStream
   */
  ODataError toError(InputStream input) throws ODataDeserializerException;
}
