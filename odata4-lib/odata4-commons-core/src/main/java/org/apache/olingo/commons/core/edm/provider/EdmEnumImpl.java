/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.commons.core.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMember;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.EnumType;

//TODO: Test
public class EdmEnumImpl extends EdmNamedImpl implements EdmEnumType {

  private final FullQualifiedName enumName;

  public EdmEnumImpl(final FullQualifiedName enumName, final EnumType enumType) {
    super(enumName.getName());
    this.enumName = enumName;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return false;
  }

  @Override
  public Class<?> getDefaultType() {
    return null;
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) {
    return false;
  }

  @Override
  public <T> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String toUriLiteral(final String literal) {
    return null;
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String getNamespace() {
    return enumName.getNamespace();
  }

  @Override
  public EdmTypeKind getKind() {
    return null;
  }

  @Override
  public EdmMember getMember(final String name) {
    return null;
  }

  @Override
  public List<String> getMemberNames() {
    return null;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return null;
  }

}
