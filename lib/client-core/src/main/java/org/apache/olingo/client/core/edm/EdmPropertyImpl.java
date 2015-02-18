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
package org.apache.olingo.client.core.edm;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.CommonProperty;
import org.apache.olingo.client.api.edm.xml.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.AbstractEdmProperty;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class EdmPropertyImpl extends AbstractEdmProperty {

  private final FullQualifiedName structuredTypeName;

  private final CommonProperty property;

  private final EdmTypeInfo typeInfo;

  private EdmAnnotationHelper helper;

  public EdmPropertyImpl(final Edm edm, final FullQualifiedName structuredTypeName, final CommonProperty property) {
    super(edm, property.getName());

    this.structuredTypeName = structuredTypeName;
    this.property = property;
    this.typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(property.getType()).build();
    if (property instanceof Property) {
      this.helper = new EdmAnnotationHelperImpl(edm, (Property) property);
    }
  }

  @Override
  protected EdmTypeInfo getTypeInfo() {
    return typeInfo;
  }

  @Override
  public EdmMapping getMapping() {
    throw new UnsupportedOperationException("Not supported in client code.");
  }

  @Override
  public String getMimeType() {
    throw new UnsupportedOperationException("Not supported in client code.");
  }

  @Override
  public Boolean isNullable() {
    return property.isNullable();
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
  public Boolean isUnicode() {
    return property.isUnicode();
  }

  @Override
  public String getDefaultValue() {
    return property.getDefaultValue();
  }

  @Override
  public SRID getSrid() {
    return (property instanceof Property)
            ? ((Property) property).getSrid()
            : null;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return structuredTypeName;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper == null ? null : helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper == null ? Collections.<EdmAnnotation>emptyList() : helper.getAnnotations();
  }

}
