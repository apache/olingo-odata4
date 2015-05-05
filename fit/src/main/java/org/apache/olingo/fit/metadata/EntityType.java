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
package org.apache.olingo.fit.metadata;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityType extends AbstractMetadataElement {

  private final String name;

  private String baseType;

  private boolean openType = false;

  private final Map<String, Property> properties;

  private final Map<String, NavigationProperty> navigationProperties;

  public EntityType(final String name) {
    this.name = name;
    properties = new HashMap<String, Property>();
    navigationProperties = new HashMap<String, NavigationProperty>();
  }

  public String getName() {
    return name;
  }

  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(final String baseType) {
    this.baseType = baseType;
  }

  public boolean isOpenType() {
    return openType;
  }

  public void setOpenType(final boolean openType) {
    this.openType = openType;
  }

  public Collection<NavigationProperty> getNavigationProperties() {
    return new HashSet<NavigationProperty>(navigationProperties.values());
  }

  public Map<String, NavigationProperty> getNavigationPropertyMap() {
    return new HashMap<String, NavigationProperty>(navigationProperties);
  }

  public Map<String, Property> getPropertyMap() {
    return new HashMap<String, Property>(properties);
  }

  public Collection<Property> getProperties() {
    return new HashSet<Property>(properties.values());
  }

  public Property getProperty(final String name) {
    return properties.get(name);
  }

  public EntityType addProperty(final String name, final Property property) {
    properties.put(name, property);
    return this;
  }

  public NavigationProperty getNavigationProperty(final String name) {
    return navigationProperties.get(name);
  }

  public EntityType addNavigationProperty(final String name, final NavigationProperty property) {
    navigationProperties.put(name, property);
    return this;
  }
}
