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

import java.util.LinkedHashMap;
import java.util.Map;

public class Association extends AbstractMetadataElement {

  private final String name;

  private Map<String, Role> roles;

  public Association(final String name) {
    this.name = name;
    roles = new LinkedHashMap<String, Role>();
  }

  public String getName() {
    return name;
  }

  public Association addRole(final String name, final String type, final String multiplicity) {
    roles.put(name, new Role(name, type, multiplicity));
    return this;
  }

  public Role getRole(final String name) {
    return roles.get(name);
  }

  public static class Role {

    final String name;

    final String type;

    final String multiplicity;

    public Role(final String name, final String type, final String multiplicity) {
      this.name = name;
      this.type = type;
      this.multiplicity = multiplicity;
    }

    public String getType() {
      return type;
    }

    public String getMultiplicity() {
      return multiplicity;
    }
  }
}
