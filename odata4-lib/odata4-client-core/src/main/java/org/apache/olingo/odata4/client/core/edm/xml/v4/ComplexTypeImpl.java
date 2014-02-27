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
package org.apache.olingo.odata4.client.core.edm.xml.v4;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.odata4.client.api.edm.xml.v4.ComplexType;
import org.apache.olingo.odata4.client.core.edm.xml.AbstractComplexType;

public class ComplexTypeImpl extends AbstractComplexType implements ComplexType {

  private static final long serialVersionUID = -1251230308269425962L;

  private boolean abstractEntityType = false;

  private String baseType;

  private boolean openType = false;

  private final List<PropertyImpl> properties = new ArrayList<PropertyImpl>();

  private final List<NavigationPropertyImpl> navigationProperties = new ArrayList<NavigationPropertyImpl>();

  private AnnotationImpl annotation;

  @Override
  public boolean isAbstractEntityType() {
    return abstractEntityType;
  }

  @Override
  public void setAbstractEntityType(final boolean abstractEntityType) {
    this.abstractEntityType = abstractEntityType;
  }

  @Override
  public String getBaseType() {
    return baseType;
  }

  @Override
  public void setBaseType(final String baseType) {
    this.baseType = baseType;
  }

  @Override
  public boolean isOpenType() {
    return openType;
  }

  @Override
  public void setOpenType(final boolean openType) {
    this.openType = openType;
  }

  @Override
  public PropertyImpl getProperty(final String name) {
    return (PropertyImpl) super.getProperty(name);
  }

  @Override
  public List<PropertyImpl> getProperties() {
    return properties;
  }

  @Override
  public NavigationPropertyImpl getNavigationProperty(String name) {
    return (NavigationPropertyImpl) super.getNavigationProperty(name);
  }

  @Override
  public List<NavigationPropertyImpl> getNavigationProperties() {
    return navigationProperties;
  }

  @Override
  public AnnotationImpl getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = (AnnotationImpl) annotation;
  }

}
