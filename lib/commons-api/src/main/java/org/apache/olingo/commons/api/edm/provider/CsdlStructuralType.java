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

public abstract class CsdlStructuralType extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private static final long serialVersionUID = 8662852373514258646L;

  protected String name;

  protected boolean isOpenType = false;

  protected FullQualifiedName baseType;

  protected boolean isAbstract;

  protected List<CsdlProperty> properties = new ArrayList<CsdlProperty>();

  protected List<CsdlNavigationProperty> navigationProperties = new ArrayList<CsdlNavigationProperty>();

  protected final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  @Override
  public String getName() {
    return name;
  }

  public CsdlStructuralType setName(final String name) {
    this.name = name;
    return this;
  }

  public boolean isOpenType() {
    return isOpenType;
  }

  public CsdlStructuralType setOpenType(final boolean isOpenType) {
    this.isOpenType = isOpenType;
    return this;
  }

  public String getBaseType() {
    if (baseType != null) {
      return baseType.getFullQualifiedNameAsString();
    }
    return null;
  }

  public FullQualifiedName getBaseTypeFQN() {
    return baseType;
  }

  public CsdlStructuralType setBaseType(final String baseType) {
    this.baseType = new FullQualifiedName(baseType);
    return this;
  }

  public CsdlStructuralType setBaseType(final FullQualifiedName baseType) {
    this.baseType = baseType;
    return this;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public CsdlStructuralType setAbstract(final boolean isAbstract) {
    this.isAbstract = isAbstract;
    return this;
  }

  public List<CsdlProperty> getProperties() {
    return properties;
  }

  public CsdlProperty getProperty(final String name) {
    return getOneByName(name, properties);
  }

  public CsdlStructuralType setProperties(final List<CsdlProperty> properties) {
    this.properties = properties;
    return this;
  }

  public List<CsdlNavigationProperty> getNavigationProperties() {
    return navigationProperties;
  }

  public CsdlNavigationProperty getNavigationProperty(final String name) {
    return getOneByName(name, navigationProperties);
  }

  public CsdlStructuralType setNavigationProperties(final List<CsdlNavigationProperty> navigationProperties) {
    this.navigationProperties = navigationProperties;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }
}
