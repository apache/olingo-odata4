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
import java.util.HashSet;
import java.util.Map;

public class Container extends AbstractMetadataElement {

  private final String name;
  private final Map<String, EntitySet> entitySets;

  public Container(final String name) {
    this.name = name;
    entitySets = new HashMap<String, EntitySet>();
  }

  public String getName() {
    return name;
  }

  public Collection<EntitySet> getEntitySets() {
    return entitySets.values();
  }

  public Collection<EntitySet> getEntitySets(final String namespace, final String entityTypeName) {
    final Collection<EntitySet> res = new HashSet<EntitySet>();
    for (EntitySet entitySet : entitySets.values()) {
      if ((namespace + "." + entityTypeName).equals(entitySet.getType())) {
        res.add(entitySet);
      }
    }

    return res;
  }

  public EntitySet getEntitySet(final String name) {
    return entitySets.get(name);
  }

  public Container addEntitySet(final String name, final EntitySet entitySet) {
    entitySets.put(name, entitySet);
    return this;
  }
}
