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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlStructuralType;

public abstract class AbstractEdmStructuredType extends EdmTypeImpl implements EdmStructuredType {

  protected EdmStructuredType baseType;
  protected FullQualifiedName baseTypeName;

  private final CsdlStructuralType providerStructuredType;

  private List<String> propertyNames;
  private Map<String, EdmProperty> properties;
  private List<String> navigationPropertyNames;
  private Map<String, EdmNavigationProperty> navigationProperties;

  public AbstractEdmStructuredType(
      final Edm edm,
      final FullQualifiedName typeName,
      final EdmTypeKind kind,
      final CsdlStructuralType structuredType) {

    super(edm, typeName, kind, structuredType);
    baseTypeName = structuredType.getBaseTypeFQN();
    providerStructuredType = structuredType;
  }

  protected abstract EdmStructuredType buildBaseType(FullQualifiedName baseTypeName);

  protected abstract void checkBaseType();

  @Override
  public List<String> getPropertyNames() {
    if (propertyNames == null) {
      final List<String> localPropertyNames = new ArrayList<String>();
      checkBaseType();
      if (baseType != null) {
        localPropertyNames.addAll(baseType.getPropertyNames());
      }
      localPropertyNames.addAll(getProperties().keySet());
      propertyNames = Collections.unmodifiableList(localPropertyNames);
    }
    return propertyNames;
  }

  @Override
  public List<String> getNavigationPropertyNames() {
    if (navigationPropertyNames == null) {
      final ArrayList<String> localNavigatinPropertyNames = new ArrayList<String>();
      checkBaseType();
      if (baseType != null) {
        localNavigatinPropertyNames.addAll(baseType.getNavigationPropertyNames());
      }
      localNavigatinPropertyNames.addAll(getNavigationProperties().keySet());
      navigationPropertyNames = Collections.unmodifiableList(localNavigatinPropertyNames);
    }
    return navigationPropertyNames;
  }

  @Override
  public EdmElement getProperty(final String name) {
    EdmElement property = getStructuralProperty(name);
    if (property == null) {
      property = getNavigationProperty(name);
    }
    return property;
  }

  @Override
  public EdmProperty getStructuralProperty(final String name) {
    EdmProperty property = null;
    checkBaseType();
    if (baseType != null) {
      property = baseType.getStructuralProperty(name);
    }
    if (property == null) {
      property = getProperties().get(name);
    }
    return property;
  }

  @Override
  public EdmNavigationProperty getNavigationProperty(final String name) {
    EdmNavigationProperty property = null;
    checkBaseType();
    if (baseType != null) {
      property = baseType.getNavigationProperty(name);
    }
    if (property == null) {
      property = getNavigationProperties().get(name);
    }
    return property;
  }

  @Override
  public boolean compatibleTo(final EdmType targetType) {
    EdmStructuredType sourceType = this;
    if (targetType == null) {
      throw new EdmException("Target type must not be null");
    }
    while (!sourceType.getName().equals(targetType.getName())
        || !sourceType.getNamespace().equals(targetType.getNamespace())) {

      sourceType = sourceType.getBaseType();
      if (sourceType == null) {
        return false;
      }
    }

    return true;
  }

  public Map<String, EdmProperty> getProperties() {
    if (properties == null) {
      final Map<String, EdmProperty> localPorperties = new LinkedHashMap<String, EdmProperty>();
      final List<CsdlProperty> structureTypeProperties = providerStructuredType.getProperties();
      for (CsdlProperty property : structureTypeProperties) {
        localPorperties.put(property.getName(), new EdmPropertyImpl(edm, property));
      }
      properties = Collections.unmodifiableMap(localPorperties);
    }
    return properties;
  }

  public Map<String, EdmNavigationProperty> getNavigationProperties() {
    if (navigationProperties == null) {
      final Map<String, EdmNavigationProperty> localNavigationProperties =
          new LinkedHashMap<String, EdmNavigationProperty>();
      final List<CsdlNavigationProperty> structuredTypeNavigationProperties =
          providerStructuredType.getNavigationProperties();

      if (structuredTypeNavigationProperties != null) {
        for (CsdlNavigationProperty navigationProperty : structuredTypeNavigationProperties) {
          localNavigationProperties.put(navigationProperty.getName(),
              new EdmNavigationPropertyImpl(edm, navigationProperty));
        }
      }

      navigationProperties = Collections.unmodifiableMap(localNavigationProperties);
    }
    return navigationProperties;
  }

  @Override
  public boolean isOpenType() {
    return providerStructuredType.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return providerStructuredType.isAbstract();
  }
}
