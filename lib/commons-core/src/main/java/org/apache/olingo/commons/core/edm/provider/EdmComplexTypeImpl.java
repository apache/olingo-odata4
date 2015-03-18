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
package org.apache.olingo.commons.core.edm.provider;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
import org.apache.olingo.commons.core.edm.AbstractEdmComplexType;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmStructuredTypeHelper;

import java.util.List;
import java.util.Map;

public class EdmComplexTypeImpl extends AbstractEdmComplexType {

  private final EdmStructuredTypeHelper helper;
  
  private EdmAnnotationHelper annotationHelper;

  public static EdmComplexTypeImpl getInstance(
      final Edm edm, final FullQualifiedName name, final ComplexType complexType) {

    return new EdmComplexTypeImpl(edm, name, complexType);
  }

  private EdmComplexTypeImpl(final Edm edm, final FullQualifiedName name, final ComplexType complexType) {
    super(edm, name, complexType.getBaseTypeFQN());
    this.helper = new EdmStructuredTypeHelperImpl(edm, name, complexType);
    this.annotationHelper = new EdmAnnotationHelperImpl(edm, complexType);
  }

  @Override
  protected Map<String, EdmProperty> getProperties() {
    return helper.getProperties();
  }

  @Override
  protected Map<String, EdmNavigationProperty> getNavigationProperties() {
    return helper.getNavigationProperties();
  }

  @Override
  public boolean isOpenType() {
    return helper.isOpenType();
  }

  @Override
  public boolean isAbstract() {
    return helper.isAbstract();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return annotationHelper == null ? null : annotationHelper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return annotationHelper == null ? null : annotationHelper.getAnnotations();
  }
}
