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
package org.apache.olingo.odata4.consumer.core.deserializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata4.consumer.api.deserializer.AnnotationProperty;
import org.apache.olingo.odata4.consumer.api.deserializer.NavigationProperty;
import org.apache.olingo.odata4.consumer.api.deserializer.Property;
import org.apache.olingo.odata4.consumer.api.deserializer.StructuralProperty;

abstract class PropertyCollection {
  protected Map<String, AnnotationProperty> annotationProperties = new HashMap<String, AnnotationProperty>();
  protected Map<String, NavigationProperty> navigationProperties = new HashMap<String, NavigationProperty>();
  protected Map<String, StructuralProperty> structuralProperties = new HashMap<String, StructuralProperty>();

  public PropertyCollection() {}

  protected PropertyCollection(final Map<String, AnnotationProperty> annotationProperties,
      final Map<String, NavigationProperty> navigationProperties,
      final Map<String, StructuralProperty> structuralProperties) {
    this.annotationProperties = annotationProperties;
    this.navigationProperties = navigationProperties;
    this.structuralProperties = structuralProperties;
  }

  public List<Property> getProperties() {
    int initialCapacity = annotationProperties.size() + navigationProperties.size() + structuralProperties.size();

    List<Property> properties = new ArrayList<Property>(initialCapacity);
    properties.addAll(annotationProperties.values());
    properties.addAll(navigationProperties.values());
    properties.addAll(structuralProperties.values());

    return properties;
  }

  public Map<String, AnnotationProperty> getAnnotationProperties() {
    return Collections.unmodifiableMap(annotationProperties);
  }

  public Map<String, NavigationProperty> getNavigationProperties() {
    return Collections.unmodifiableMap(navigationProperties);
  }

  public Map<String, StructuralProperty> getStructuralProperties() {
    return Collections.unmodifiableMap(structuralProperties);
  }

  public void addProperty(final Property property) {
    if (property == null) {
      throw new IllegalArgumentException("Property parameter MUST NOT be NULL.");
    }

    if (property instanceof NavigationPropertyImpl) {
      NavigationPropertyImpl navProperty = (NavigationPropertyImpl) navigationProperties.get(property.getName());
      if (navProperty == null) {
        navigationProperties.put(property.getName(), (NavigationPropertyImpl) property);
      } else {
        NavigationProperty temp = (NavigationProperty) property;
        navProperty.updateLink(temp);
      }
    } else if (property instanceof AnnotationPropertyImpl) {
      annotationProperties.put(property.getName(), (AnnotationPropertyImpl) property);
    } else if (property instanceof StructuralProperty) {
      structuralProperties.put(property.getName(), (StructuralProperty) property);
    } else {
      throw new IllegalArgumentException("Unknown class '" + property.getClass() + "'.");
    }
  }
}
