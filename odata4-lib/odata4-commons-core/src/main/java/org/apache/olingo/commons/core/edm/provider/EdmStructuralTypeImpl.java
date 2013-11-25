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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmStructuralType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.NavigationProperty;
import org.apache.olingo.commons.api.edm.provider.Property;
import org.apache.olingo.commons.api.edm.provider.StructuralType;

public abstract class EdmStructuralTypeImpl extends EdmTypeImpl implements EdmStructuralType {

  private final Map<String, EdmElement> properties = new HashMap<String, EdmElement>();
  private ArrayList<String> navigationPropertyNames;
  private ArrayList<String> propertyNames;
  protected final EdmStructuralType baseType;
  private final StructuralType structuralType;

  public EdmStructuralTypeImpl(final EdmProviderImpl edm, final FullQualifiedName name,
      final StructuralType structuralType, final EdmTypeKind kind) {
    super(edm, name, kind);
    this.structuralType = structuralType;
    baseType = buildBaseType(structuralType.getBaseType());
    buildProperties(structuralType.getProperties());
    buildNavigationProperties(structuralType.getNavigationProperties());
  }

  private void buildNavigationProperties(final List<NavigationProperty> providerNavigationProperties) {
    if (providerNavigationProperties != null) {
      for (NavigationProperty navigationProperty : providerNavigationProperties) {
        properties.put(navigationProperty.getName(), new EdmNavigationPropertyImpl(edm, navigationProperty));
      }
    }

  }

  private void buildProperties(final List<Property> providerProperties) {
    if (providerProperties != null) {
      for (Property property : providerProperties) {
        properties.put(property.getName(), new EdmPropertyImpl(edm, property));
      }
    }

  }

  @Override
  public EdmElement getProperty(final String name) {
    EdmElement property = null;
    if (baseType != null) {
      property = baseType.getProperty(name);
    }
    if (property == null) {
      property = properties.get(name);
    }
    return property;
  }

  @Override
  public List<String> getPropertyNames() {
    if (propertyNames == null) {
      propertyNames = new ArrayList<String>();
      if (baseType != null) {
        propertyNames.addAll(baseType.getPropertyNames());
      }
      for (Property property : structuralType.getProperties()) {
        propertyNames.add(property.getName());
      }
    }
    return propertyNames;
  }

  @Override
  public List<String> getNavigationPropertyNames() {
    if (navigationPropertyNames == null) {
      navigationPropertyNames = new ArrayList<String>();
      if (baseType != null) {
        navigationPropertyNames.addAll(baseType.getNavigationPropertyNames());
      }
      for (NavigationProperty navProperty : structuralType.getNavigationProperties()) {
        navigationPropertyNames.add(navProperty.getName());
      }
    }
    return navigationPropertyNames;
  }

  protected abstract EdmStructuralType buildBaseType(FullQualifiedName baseTypeName);
}
