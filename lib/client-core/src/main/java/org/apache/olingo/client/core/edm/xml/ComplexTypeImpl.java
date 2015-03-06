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
 * KIND, either express or >ied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.edm.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.Annotation;
import org.apache.olingo.client.api.edm.xml.ComplexType;
import org.apache.olingo.client.api.edm.xml.NavigationProperty;
import org.apache.olingo.client.api.edm.xml.Property;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ComplexTypeDeserializer.class)
public class ComplexTypeImpl extends AbstractStructuralType implements ComplexType {

  private static final long serialVersionUID = 4076944306925840115L;

  private boolean abstractEntityType = false;

  private String baseType;

  private boolean openType = false;

  private final List<Property> properties = new ArrayList<Property>();

  private final List<NavigationProperty> navigationProperties = new ArrayList<NavigationProperty>();

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  @Override
  public boolean isAbstractType() {
    return abstractEntityType;
  }

  public void setAbstractEntityType(final boolean abstractEntityType) {
    this.abstractEntityType = abstractEntityType;
  }

  @Override
  public String getBaseType() {
    return baseType;
  }

  public void setBaseType(final String baseType) {
    this.baseType = baseType;
  }

  @Override
  public boolean isOpenType() {
    return openType;
  }

  public void setOpenType(final boolean openType) {
    this.openType = openType;
  }

  @Override
  public Property getProperty(final String name) {
    return super.getProperty(name);
  }

  @Override
  public List<Property> getProperties() {
    return properties;
  }

  @Override
  public NavigationProperty getNavigationProperty(final String name) {
    return super.getNavigationProperty(name);
  }

  @Override
  public List<NavigationProperty> getNavigationProperties() {
    return navigationProperties;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
