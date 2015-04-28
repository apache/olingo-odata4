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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class CsdlNavigationProperty extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private static final long serialVersionUID = -788021920718310799L;

  private String name;

  private FullQualifiedName type;

  private boolean isCollection;

  private String partner;

  private boolean containsTarget = false;

  private List<CsdlReferentialConstraint> referentialConstraints = new ArrayList<CsdlReferentialConstraint>();

  // Facets
  private boolean nullable = true;

  private CsdlOnDelete onDelete;
  
  private List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  public String getName() {
    return name;
  }

  public boolean isCollection() {
    return isCollection;
  }

  public CsdlNavigationProperty setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  public CsdlNavigationProperty setName(final String name) {
    this.name = name;
    return this;
  }

  public FullQualifiedName getTypeFQN() {
    return type;
  }
  
  public String getType() {
    if(type != null){
      return type.getFullQualifiedNameAsString();
    }
    return null;
  }

  public CsdlNavigationProperty setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }
  
  public CsdlNavigationProperty setType(final String type) {
    this.type = new FullQualifiedName(type);
    return this;
  }

  public String getPartner() {
    return partner;
  }

  public CsdlNavigationProperty setPartner(final String partner) {
    this.partner = partner;
    return this;
  }

  public boolean isContainsTarget() {
    return containsTarget;
  }

  public CsdlNavigationProperty setContainsTarget(final boolean containsTarget) {
    this.containsTarget = containsTarget;
    return this;
  }

  public List<CsdlReferentialConstraint> getReferentialConstraints() {
    return referentialConstraints;
  }

  public CsdlNavigationProperty setReferentialConstraints(
          final List<CsdlReferentialConstraint> referentialConstraints) {
    this.referentialConstraints = referentialConstraints;
    return this;
  }

  public Boolean isNullable() {
    return nullable;
  }

  public CsdlNavigationProperty setNullable(final Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  public CsdlOnDelete getOnDelete() {
    return onDelete;
  }

  public CsdlNavigationProperty setOnDelete(final CsdlOnDelete onDelete) {
    this.onDelete = onDelete;
    return this;
  }
  
  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
}
