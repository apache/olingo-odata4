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
package org.apache.olingo.server.core.edm.provider;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.EdmStructuredTypeHelper;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.StructuredType;

public class EdmStructuredTypeHelperImpl implements EdmStructuredTypeHelper {

  private final Edm edm;

  private final FullQualifiedName structuredTypeName;

  private final StructuredType structuredType;

  private Map<String, EdmProperty> properties;

  private Map<String, EdmNavigationProperty> navigationProperties;

  public EdmStructuredTypeHelperImpl(
          final Edm edm, final FullQualifiedName structuredTypeName, final StructuredType structuredType) {

    this.edm = edm;
    this.structuredTypeName = structuredTypeName;
    this.structuredType = structuredType;
  }

  @Override
  public Map<String, EdmProperty> getProperties() {
    if (properties == null) {
      properties = new LinkedHashMap<String, EdmProperty>();
      if (structuredType.getProperties() != null) {
        for (Property property : structuredType.getProperties()) {
          properties.put(property.getName(), new EdmPropertyImpl(edm, structuredTypeName, property));
        }
      }
    }
    return properties;
  }

  @Override
  public Map<String, EdmNavigationProperty> getNavigationProperties() {
    if (navigationProperties == null) {
      navigationProperties = new LinkedHashMap<String, EdmNavigationProperty>();
      if (structuredType.getNavigationProperties() != null) {
        for (NavigationProperty navigationProperty : structuredType.getNavigationProperties()) {
          navigationProperties.put(navigationProperty.getName(),
                  new EdmNavigationPropertyImpl(edm, structuredTypeName, navigationProperty));
        }
      }
    }
    return navigationProperties;
  }

  @Override
  public boolean isOpenType() {
    return structuredType.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return structuredType.isAbstract();
  }
}
