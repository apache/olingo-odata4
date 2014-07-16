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

import org.apache.olingo.client.api.edm.xml.CommonParameter;
import org.apache.olingo.client.api.edm.xml.v4.Parameter;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.AbstractEdmParameter;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

import java.util.Collections;
import java.util.List;

public class EdmParameterImpl extends AbstractEdmParameter {

  private final CommonParameter parameter;

  private final EdmTypeInfo typeInfo;

  private EdmAnnotationHelper helper;

  public EdmParameterImpl(final Edm edm, final CommonParameter parameter) {
    super(edm, parameter.getName(), new FullQualifiedName(parameter.getType()));
    this.parameter = parameter;
    this.typeInfo = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(parameter.getType()).build();
    if (parameter instanceof Parameter) {
      this.helper = new EdmAnnotationHelperImpl(edm, (Parameter) parameter);
    }
  }

  @Override
  public boolean isCollection() {
    return typeInfo.isCollection();
  }

  @Override
  public EdmMapping getMapping() {
    throw new UnsupportedOperationException("Not supported in client code.");
  }

  @Override
  public Boolean isNullable() {
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
    return (parameter instanceof Parameter)
            ? ((Parameter) parameter).getSrid()
            : null;
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
