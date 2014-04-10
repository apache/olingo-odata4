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
package org.apache.olingo.fit.metadata;

import java.util.LinkedHashMap;
import java.util.Map;

public class AssociationSet extends AbstractMetadataElement {

  private final String name;

  private final String association;

  private Map<String, Role> roles;

  public AssociationSet(final String name, final String association) {
    this.name = name;
    this.association = association;
    roles = new LinkedHashMap<String, Role>();
  }

  public String getName() {
    return name;
  }

  public String getAssociation() {
    return association;
  }

  public AssociationSet addRole(final String name, final String entitySet) {
    roles.put(name, new Role(name, entitySet));
    return this;
  }

  public Role getRole(final String name) {
    return roles.get(name);
  }

  public static class Role {

    final String name;

    final String entitySet;

    public Role(final String name, final String entitySet) {
      this.name = name;
      this.entitySet = entitySet;
    }

    public String getName() {
      return name;
    }

    public String getEntitySet() {
      return entitySet;
    }
  }
}
