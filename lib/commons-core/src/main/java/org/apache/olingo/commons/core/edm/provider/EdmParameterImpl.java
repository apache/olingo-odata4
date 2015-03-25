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

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.Parameter;

public class EdmParameterImpl extends EdmElementImpl implements EdmParameter {

  private final Parameter parameter;
  private final EdmAnnotationHelper helper;
  private final EdmTypeInfo typeInfo;
  private EdmType typeImpl;

  public EdmParameterImpl(final Edm edm, final Parameter parameter) {
    super(edm, parameter.getName());
    this.parameter = parameter;
    this.helper = new EdmAnnotationHelperImpl(edm, parameter);
    this.typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(parameter.getType()).build();
  }

  @Override
  public boolean isCollection() {
    return parameter.isCollection();
  }

  @Override
  public EdmMapping getMapping() {
    return parameter.getMapping();
  }

  @Override
  public boolean isNullable() {
    return parameter.isNullable();
  }

  @Override
  public Integer getMaxLength() {
    return parameter.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return parameter.getPrecision();
  }

  @Override
  public Integer getScale() {
    return parameter.getScale();
  }

  @Override
  public SRID getSrid() {
    return parameter.getSrid();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }
  
  @Override
  public EdmType getType() {
    if (typeImpl == null) {
      typeImpl = typeInfo.getType();
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + typeInfo.getFullQualifiedName());
      }
    }

    return typeImpl;
  }
}
