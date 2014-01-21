/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.api.edm.provider;

import java.util.List;


public abstract class StructuralType {

  protected String name;
  protected boolean isOpenType;
  protected FullQualifiedName baseType;
  protected boolean isAbstract;
  protected List<Property> properties;
  protected List<NavigationProperty> navigationProperties;

  // What about mapping and annotations?

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

  public FullQualifiedName getBaseType() {
    return baseType;
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

  public StructuralType setProperties(final List<Property> properties) {
    this.properties = properties;
    return this;
  }

  public List<NavigationProperty> getNavigationProperties() {
    return navigationProperties;
  }

  public StructuralType setNavigationProperties(final List<NavigationProperty> navigationProperties) {
    this.navigationProperties = navigationProperties;
    return this;
  }
}
