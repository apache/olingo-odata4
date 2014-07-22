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

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.AbstractEdmProperty;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.server.api.edm.provider.Property;

public class EdmPropertyImpl extends AbstractEdmProperty {

  private final FullQualifiedName structuredTypeName;

  private final Property property;

  private final EdmTypeInfo typeInfo;

  public EdmPropertyImpl(final Edm edm, final FullQualifiedName structuredTypeName, final Property property) {
    super(edm, property.getName());

    this.structuredTypeName = structuredTypeName;
    this.property = property;
    typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(property.getType().toString()).build();
  }

  @Override
  public EdmTypeInfo getTypeInfo() {
    return typeInfo;
  }

  @Override
  public boolean isCollection() {
    return property.isCollection();
  }

  @Override
  public EdmMapping getMapping() {
    return property.getMapping();
  }

  @Override
  public String getMimeType() {
    return property.getMimeType();
  }

  @Override
  public Boolean isNullable() {
    return property.getNullable();
  }

  @Override
  public Integer getMaxLength() {
    return property.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return property.getPrecision();
  }

  @Override
  public Integer getScale() {
    return property.getScale();
  }

  @Override
  public SRID getSrid() {
    return null; // TODO: provide implementation
  }

  @Override
  public Boolean isUnicode() {
    return property.isUnicode();
  }

  @Override
  public String getDefaultValue() {
    return property.getDefaultValue();
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return structuredTypeName;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
