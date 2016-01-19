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
 * The type Csdl structural type.
 */
public abstract class CsdlStructuralType extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  /**
   * The Name.
   */
  protected String name;

  /**
   * The Is open type.
   */
  protected boolean isOpenType = false;

  /**
   * The Base type.
   */
  protected FullQualifiedName baseType;

  /**
   * The Is abstract.
   */
  protected boolean isAbstract;

  /**
   * The Properties.
   */
  protected List<CsdlProperty> properties = new ArrayList<CsdlProperty>();

  /**
   * The Navigation properties.
   */
  protected List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();

  /**
   * The Annotations.
   */
  protected List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   * @return the name
   */
  public CsdlStructuralType setName(final String name) {
    this.name = name;
    return this;
  }

  /**
   * Is open type.
   *
   * @return the boolean
   */
  public boolean isOpenType() {
    return isOpenType;
  }

  /**
   * Sets open type.
   *
   * @param isOpenType the is open type
   * @return the open type
   */
  public CsdlStructuralType setOpenType(final boolean isOpenType) {
    this.isOpenType = isOpenType;
    return this;
  }

  /**
   * Gets base type.
   *
   * @return the base type
   */
  public String getBaseType() {
    if (baseType != null) {
      return baseType.getFullQualifiedNameAsString();
    }
    return null;
  }

  /**
   * Gets base type fQN.
   *
   * @return the base type fQN
   */
  public FullQualifiedName getBaseTypeFQN() {
    return baseType;
  }

  /**
   * Sets base type.
   *
   * @param baseType the base type
   * @return the base type
   */
  public CsdlStructuralType setBaseType(final String baseType) {
    this.baseType = new FullQualifiedName(baseType);
    return this;
  }

  /**
   * Sets base type.
   *
   * @param baseType the base type
   * @return the base type
   */
  public CsdlStructuralType setBaseType(final FullQualifiedName baseType) {
    this.baseType = baseType;
    return this;
  }

  /**
   * Is abstract.
   *
   * @return the boolean
   */
  public boolean isAbstract() {
    return isAbstract;
  }

  /**
   * Sets abstract.
   *
   * @param isAbstract the is abstract
   * @return the abstract
   */
  public CsdlStructuralType setAbstract(final boolean isAbstract) {
    this.isAbstract = isAbstract;
    return this;
  }

  /**
   * Gets properties.
   *
   * @return the properties
   */
  public List<CsdlProperty> getProperties() {
    return properties;
  }

  /**
   * Gets property.
   *
   * @param name the name
   * @return the property
   */
  public CsdlProperty getProperty(final String name) {
    return getOneByName(name, properties);
  }

  /**
   * Sets properties.
   *
   * @param properties the properties
   * @return the properties
   */
  public CsdlStructuralType setProperties(final List<CsdlProperty> properties) {
    this.properties = properties;
    return this;
  }

  /**
   * Gets navigation properties.
   *
   * @return the navigation properties
   */
  public List<CsdlNavigationProperty> getNavigationProperties() {
    return navigationProperties;
  }

  /**
   * Gets navigation property.
   *
   * @param name the name
   * @return the navigation property
   */
  public CsdlNavigationProperty getNavigationProperty(final String name) {
    return getOneByName(name, navigationProperties);
  }

  /**
   * Sets navigation properties.
   *
   * @param navigationProperties the navigation properties
   * @return the navigation properties
   */
  public CsdlStructuralType setNavigationProperties(final List<CsdlNavigationProperty> navigationProperties) {
    this.navigationProperties = navigationProperties;
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
  public CsdlStructuralType setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
