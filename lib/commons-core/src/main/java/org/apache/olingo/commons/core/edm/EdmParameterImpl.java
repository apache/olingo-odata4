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
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;

public class EdmParameterImpl extends AbstractEdmNamed implements EdmParameter {

  private final CsdlParameter parameter;
  private EdmType typeImpl;

  public EdmParameterImpl(final Edm edm, final CsdlParameter parameter) {
    super(edm, parameter.getName(), parameter);
    this.parameter = parameter;
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
  public EdmType getType() {
    if (typeImpl == null) {
      if (parameter.getType() == null) {
        throw new EdmException("Parameter " + parameter.getName() + " must hava a full qualified type.");
      }
      typeImpl = new EdmTypeInfo.Builder().setEdm(edm).setTypeExpression(parameter.getType()).build().getType();
      if (typeImpl == null) {
        throw new EdmException("Cannot find type with name: " + parameter.getTypeFQN());
      }
    }

    return typeImpl;
  }
}
