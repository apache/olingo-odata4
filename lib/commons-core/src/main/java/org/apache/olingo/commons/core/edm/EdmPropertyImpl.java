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
package org.apache.olingo.commons.core.edm;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;

public class EdmPropertyImpl extends AbstractEdmNamed implements EdmProperty, EdmElement {

  private final CsdlProperty property;
  private final EdmTypeInfo typeInfo;
  private EdmType propertyType;

  public EdmPropertyImpl(final Edm edm, final CsdlProperty property) {
    super(edm, property.getName(), property);

    this.property = property;
    typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(property.getType().toString()).build();
  }

  @Override
  public EdmType getType() {
    if (propertyType == null) {
      propertyType = typeInfo.getType();
      if (propertyType == null) {
        throw new EdmException("Cannot find type with name: " + typeInfo.getFullQualifiedName());
      }
    }

    return propertyType;
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
  public boolean isNullable() {
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
  public SRID getSrid() {
    return property.getSrid();
  }

  @Override
  public boolean isUnicode() {
    return property.isUnicode();
  }

  @Override
  public String getDefaultValue() {
    return property.getDefaultValue();
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.Property;
  }

  @Override
  public boolean isPrimitive() {
    return typeInfo.isPrimitiveType();
  }
}
