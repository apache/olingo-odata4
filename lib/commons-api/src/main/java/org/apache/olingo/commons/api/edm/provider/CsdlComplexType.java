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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

/**
 * The type Csdl complex type.
 */
public class CsdlComplexType extends CsdlStructuralType {

  @Override
  public CsdlComplexType setName(final String name) {
    this.name = name;
    return this;
  }

  @Override
  public CsdlComplexType setOpenType(final boolean isOpenType) {
    this.isOpenType = isOpenType;
    return this;
  }

  @Override
  public CsdlComplexType setBaseType(final String baseType) {
    this.baseType = new FullQualifiedName(baseType);
    return this;
  }

  @Override
  public CsdlComplexType setBaseType(final FullQualifiedName baseType) {
    this.baseType = baseType;
    return this;
  }

  @Override
  public CsdlComplexType setAbstract(final boolean isAbstract) {
    this.isAbstract = isAbstract;
    return this;
  }

  @Override
  public CsdlComplexType setProperties(final List<CsdlProperty> properties) {
    this.properties = properties;
    return this;
  }

  @Override
  public CsdlComplexType setNavigationProperties(final List<CsdlNavigationProperty> navigationProperties) {
    this.navigationProperties = navigationProperties;
    return this;
  }

  @Override
  public CsdlComplexType setAnnotations(final List<CsdlAnnotation> annotations) {
    this.annotations = annotations;
    return this;
  }
}
