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

/**
 * The type Csdl navigation property.
 */
public class CsdlNavigationProperty extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

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

  @Override
  public String getName() {
    return name;
  }

  /**
   * Is collection.
   *
   * @return the boolean
   */
  public boolean isCollection() {
    return isCollection;
  }

  /**
   * Sets collection.
   *
   * @param isCollection the is collection
   * @return the collection
   */
  public CsdlNavigationProperty setCollection(final boolean isCollection) {
    this.isCollection = isCollection;
    return this;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public CsdlNavigationProperty setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Gets type fQN.
   *
   * @return the type fQN
   */
  public FullQualifiedName getTypeFQN() {
    return type;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    if (type != null) {
      return type.getFullQualifiedNameAsString();
    }
    return null;
  }

  /**
   * Sets type.
   *
   * @param type the type
   * @return the type
   */
  public CsdlNavigationProperty setType(final FullQualifiedName type) {
    this.type = type;
    return this;
  }

  /**
   * Sets type.
   *
   * @param type the type
   * @return the type
   */
  public CsdlNavigationProperty setType(final String type) {
    this.type = new FullQualifiedName(type);
    return this;
  }

  /**
   * Gets partner.
   *
   * @return the partner
   */
  public String getPartner() {
    return partner;
  }

  /**
   * Sets partner.
   *
   * @param partner the partner
   * @return the partner
   */
  public CsdlNavigationProperty setPartner(final String partner) {
    this.partner = partner;
    return this;
  }

  /**
   * Is contains target.
   *
   * @return the boolean
   */
  public boolean isContainsTarget() {
    return containsTarget;
  }

  /**
   * Sets contains target.
   *
   * @param containsTarget the contains target
   * @return the contains target
   */
  public CsdlNavigationProperty setContainsTarget(final boolean containsTarget) {
    this.containsTarget = containsTarget;
    return this;
  }

  /**
   * Gets referential constraints.
   *
   * @return the referential constraints
   */
  public List<CsdlReferentialConstraint> getReferentialConstraints() {
    return referentialConstraints;
  }

  /**
   * Sets referential constraints.
   *
   * @param referentialConstraints the referential constraints
   * @return the referential constraints
   */
  public CsdlNavigationProperty setReferentialConstraints(
      final List<CsdlReferentialConstraint> referentialConstraints) {
    this.referentialConstraints = referentialConstraints;
    return this;
  }

  /**
   * Is nullable.
   *
   * @return the boolean
   */
  public Boolean isNullable() {
    return nullable;
  }

  /**
   * Sets nullable.
   *
   * @param nullable the nullable
   * @return the nullable
   */
  public CsdlNavigationProperty setNullable(final Boolean nullable) {
    this.nullable = nullable;
    return this;
  }

  /**
   * Gets on delete.
   *
   * @return the on delete
   */
  public CsdlOnDelete getOnDelete() {
    return onDelete;
  }

  /**
   * Sets on delete.
   *
   * @param onDelete the on delete
   * @return the on delete
   */
  public CsdlNavigationProperty setOnDelete(final CsdlOnDelete onDelete) {
    this.onDelete = onDelete;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
  
  /**
   * Sets a list of annotations
   * @param annotations list of annotations
   * @return this instance
   */
  public CsdlNavigationProperty setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
