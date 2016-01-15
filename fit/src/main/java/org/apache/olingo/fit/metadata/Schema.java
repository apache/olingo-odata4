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
package org.apache.olingo.fit.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Schema extends AbstractMetadataElement {

  private final String namespace;
  private final Map<String, Container> containers;
  private final Map<String, EntityType> entityTypes;

  public Schema(final String namespace) {
    this.namespace = namespace;
    entityTypes = new HashMap<String, EntityType>();
    containers = new HashMap<String, Container>();
  }

  public String getNamespace() {
    return namespace;
  }

  public Collection<EntityType> getEntityTypes() {
    return entityTypes.values();
  }

  public EntityType getEntityType(final String name) {
    return entityTypes.get(name);
  }

  public Schema addEntityType(final String name, final EntityType entityType) {
    entityTypes.put(name, entityType);
    return this;
  }

  public Collection<Container> getContainers() {
    return containers.values();
  }

  public Container getContainer(final String name) {
    return containers.get(name);
  }

  public Schema addContainer(final String name, final Container container) {
    containers.put(name, container);
    return this;
  }
}
