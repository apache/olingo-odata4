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

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.SRID;

/** A dynamic EDM property containing an aggregation. */
public class DynamicProperty implements EdmProperty {

  private final String name;
  private final EdmType propertyType;

  /** Creates a dynamic property with a mandatory name and an optional type. */
  public DynamicProperty(final String name, final EdmType type) {
    this.name = name;
    propertyType = type;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public EdmType getType() {
    return propertyType;
  }

  @Override
  public boolean isCollection() {
    return false;
  }

  @Override
  public EdmMapping getMapping() {
    return null;
  }

  @Override
  public String getMimeType() {
    return null;
  }

  @Override
  public boolean isNullable() {
    return false;
  }

  @Override
  public Integer getMaxLength() {
    return null;
  }

  @Override
  public Integer getPrecision() {
    return null;
  }

  @Override
  public Integer getScale() {
    return null;
  }

  @Override
  public SRID getSrid() {
    return null;
  }

  @Override
  public boolean isUnicode() {
    return true;
  }

  @Override
  public String getDefaultValue() {
    return null;
  }

  @Override
  public boolean isPrimitive() {
    return propertyType != null && propertyType.getKind() == EdmTypeKind.PRIMITIVE;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term, final String qualifier) {
    return null;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return Collections.emptyList();
  }
  
  @Override
  public EdmType getTypeWithAnnotations() {
    return propertyType;
  }
}
