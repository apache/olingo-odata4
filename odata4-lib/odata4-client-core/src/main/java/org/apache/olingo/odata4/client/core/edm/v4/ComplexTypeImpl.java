/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.client.core.edm.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.core.edm.AbstractComplexType;

public class ComplexTypeImpl extends AbstractComplexType implements AnnotatedEdmItem {

  private static final long serialVersionUID = -1251230308269425962L;

  private boolean abstractEntityType = false;

  private String baseType;

  private boolean openType = false;

  private final List<PropertyImpl> properties = new ArrayList<PropertyImpl>();

  private final List<NavigationPropertyImpl> navigationProperties = new ArrayList<NavigationPropertyImpl>();

  private Annotation annotation;

  public boolean isAbstractEntityType() {
    return abstractEntityType;
  }

  public void setAbstractEntityType(final boolean abstractEntityType) {
    this.abstractEntityType = abstractEntityType;
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

  @Override
  public List<PropertyImpl> getProperties() {
    return properties;
  }

  @Override
  public PropertyImpl getProperty(final String name) {
    PropertyImpl result = null;
    for (PropertyImpl property : getProperties()) {
      if (name.equals(property.getName())) {
        result = property;
      }
    }
    return result;
  }

  public List<NavigationPropertyImpl> getNavigationProperties() {
    return navigationProperties;
  }

  public NavigationPropertyImpl getNavigationProperty(final String name) {
    NavigationPropertyImpl result = null;
    for (NavigationPropertyImpl property : getNavigationProperties()) {
      if (name.equals(property.getName())) {
        result = property;
      }
    }
    return result;
  }

  @Override
  public Annotation getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = annotation;
  }

}
