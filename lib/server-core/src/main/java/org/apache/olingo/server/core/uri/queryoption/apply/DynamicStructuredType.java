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
package org.apache.olingo.server.core.uri.queryoption.apply;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

/** A dynamic structured type used to incorporate dynamic properties containing aggregations. */
public class DynamicStructuredType implements EdmStructuredType, Cloneable {

  private final EdmStructuredType startType;
  private Map<String, EdmProperty> properties;

  public DynamicStructuredType(final EdmStructuredType startType) {
    this.startType = startType;
  }

  public DynamicStructuredType addProperty(final EdmProperty property) {
    if (properties == null) {
      properties = new LinkedHashMap<String, EdmProperty>();
    }
    properties.put(property.getName(), property);
    return this;
  }

  @Override
  public EdmElement getProperty(final String name) {
    final EdmElement property = startType.getProperty(name);
    return property == null ?
        properties == null ? null : properties.get(name) :
        property;
  }

  @Override
  public List<String> getPropertyNames() {
    if (properties == null || properties.isEmpty()) {
      return startType.getPropertyNames();
    } else {
      List<String> names = new ArrayList<String>(startType.getPropertyNames());
      names.addAll(properties.keySet());
      return Collections.unmodifiableList(names);
    }
  }

  @Override
  public EdmProperty getStructuralProperty(final String name) {
    final EdmProperty property = startType.getStructuralProperty(name);
    return property == null ?
        properties == null ? null : properties.get(name) :
        property;
  }

  @Override
  public EdmNavigationProperty getNavigationProperty(final String name) {
    return startType.getNavigationProperty(name);
  }

  @Override
  public List<String> getNavigationPropertyNames() {
    return startType.getNavigationPropertyNames();
  }

  @Override
  public String getNamespace() {
    return startType.getNamespace();
  }

  @Override
  public String getName() {
    return startType.getName();
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return startType.getFullQualifiedName();
  }

  @Override
  public EdmTypeKind getKind() {
    return startType.getKind();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term, final String qualifier) {
    return startType.getAnnotation(term, qualifier);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return startType.getAnnotations();
  }

  @Override
  public EdmStructuredType getBaseType() {
    return startType.getBaseType();
  }

  @Override
  public boolean compatibleTo(final EdmType targetType) {
    return startType.compatibleTo(targetType);
  }

  @Override
  public boolean isOpenType() {
    return startType.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return startType.isAbstract();
  }
}
