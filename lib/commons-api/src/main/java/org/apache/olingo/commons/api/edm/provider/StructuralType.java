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

public abstract class StructuralType extends AbstractEdmItem implements Named, Annotatable {

  private static final long serialVersionUID = 8662852373514258646L;

  protected String name;

  protected boolean isOpenType = false;

  protected FullQualifiedName baseType;

  protected boolean isAbstract;

  protected List<Property> properties = new ArrayList<Property>();

  protected List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();

  protected final List<Annotation> annotations = new ArrayList<Annotation>();

  public String getName() {
    return name;
  }

  public StructuralType setName(final String name) {
    this.name = name;
    return this;
  }

  public boolean isOpenType() {
    return isOpenType;
  }

  public StructuralType setOpenType(final boolean isOpenType) {
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

  public StructuralType setBaseType(final String baseType) {
    this.baseType = new FullQualifiedName(baseType);
    return this;
  }

  public StructuralType setBaseType(final FullQualifiedName baseType) {
    this.baseType = baseType;
    return this;
  }

  public boolean isAbstract() {
    return isAbstract;
  }

  public StructuralType setAbstract(final boolean isAbstract) {
    this.isAbstract = isAbstract;
    return this;
  }

  public List<Property> getProperties() {
    return properties;
  }

  public Property getProperty(String name) {
    return getOneByName(name, properties);
  }

  public StructuralType setProperties(final List<Property> properties) {
    this.properties = properties;
    return this;
  }

  public List<NavigationProperty> getNavigationProperties() {
    return navigationProperties;
  }

  public NavigationProperty getNavigationProperty(String name) {
    return getOneByName(name, navigationProperties);
  }

  public StructuralType setNavigationProperties(final List<NavigationProperty> navigationProperties) {
    this.navigationProperties = navigationProperties;
    return this;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }
}
