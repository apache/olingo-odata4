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
package org.apache.olingo.odata4.client.core.edm;

import org.apache.olingo.odata4.client.api.edm.xml.CommonParameter;
import org.apache.olingo.odata4.client.api.utils.EdmTypeInfo;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmMapping;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmParameter;

public class EdmParameterImpl extends AbstractEdmParameter {

  private final CommonParameter parameter;

  private final EdmTypeInfo parameterInfo;

  public static EdmParameterImpl getInstance(final Edm edm, final CommonParameter parameter) {
    final EdmTypeInfo paramTypeInfo = new EdmTypeInfo(parameter.getType());
    return new EdmParameterImpl(edm, parameter, paramTypeInfo);
  }

  private EdmParameterImpl(final Edm edm, final CommonParameter parameter, final EdmTypeInfo parameterInfo) {
    super(edm, parameter.getName(), parameterInfo.getFullQualifiedName());
    this.parameter = parameter;
    this.parameterInfo = parameterInfo;
  }

  @Override
  public boolean isCollection() {
    return parameterInfo.isCollection();
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

}
