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

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;

/**
 * Result type for {@link ODataDeserializer} methods
 */
public interface DeserializerResult {
  /**
   * Returns an entity.
   * @return an {@link Entity} or null
   */
  Entity getEntity();

  /**
   * Returns an entity collection.
   * @return an {@link EntityCollection} or null
   */
  EntityCollection getEntityCollection();

  /**
   * Returns the ExpandOptions for the deserialized entity.
   * @return an {@link ExpandOption} or null
   */
  ExpandOption getExpandTree();

  /**
   * Returns the deserialized action parameters of an {@link Entity} as key/value pairs.
   * @return the action parameters
   */
  Map<String, Parameter> getActionParameters();

  /**
   * Returns a Property or collections of properties (primitive & complex).
   * @return {@link Property} or collections of properties (primitive & complex) or null
   */
  Property getProperty();

  /**
   * Returns the entity references from the provided document.
   * @return a collection of entity references
   */
  List<URI> getEntityReferences();
}
