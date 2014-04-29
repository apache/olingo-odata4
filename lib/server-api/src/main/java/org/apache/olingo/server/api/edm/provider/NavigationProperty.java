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
package org.apache.olingo.server.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmOnDelete;

public class NavigationProperty {

  private String name;

  private FullQualifiedName type;

  private boolean isCollection;

  private String partner;

  private boolean containsTarget;

  private List<ReferentialConstraint> referentialConstraints;

  // Facets
  private Boolean nullable;

  // Refactor this onDelete
  private EdmOnDelete onDelete;

//Annotations?
  public String getName() {
    return name;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public NavigationProperty setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public NavigationProperty setName(final String name) {
    this.name = name;
    return this;
  }

  public FullQualifiedName getType() {
    return type;
  }

  public NavigationProperty setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  public String getPartner() {
    return partner;
  }

  public NavigationProperty setPartner(final String partner) {
    this.partner = partner;
    return this;
  }

  public boolean isContainsTarget() {
    return containsTarget;
  }

  public NavigationProperty setContainsTarget(final boolean containsTarget) {
    this.containsTarget = containsTarget;
    return this;
  }

  public List<ReferentialConstraint> getReferentialConstraints() {
    return referentialConstraints;
  }

  public NavigationProperty setReferentialConstraints(final List<ReferentialConstraint> referentialConstraints) {
    this.referentialConstraints = referentialConstraints;
    return this;
  }

  public Boolean getNullable() {
    return nullable;
  }

  public NavigationProperty setNullable(final Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public EdmOnDelete getOnDelete() {
    return onDelete;
  }

  public NavigationProperty setOnDelete(final EdmOnDelete onDelete) {
    this.onDelete = onDelete;
    return this;
  }
}
