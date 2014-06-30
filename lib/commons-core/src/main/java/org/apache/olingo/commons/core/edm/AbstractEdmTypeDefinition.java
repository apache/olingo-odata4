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
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;

public abstract class AbstractEdmTypeDefinition extends EdmNamedImpl implements EdmTypeDefinition {

  private final String namespace;

  public AbstractEdmTypeDefinition(final Edm edm, final FullQualifiedName typeDefinitionName) {
    super(edm, typeDefinitionName.getName());
    namespace = typeDefinitionName.getNamespace();
  }

  @Override
  public abstract EdmPrimitiveType getUnderlyingType();

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return getUnderlyingType().isCompatible(primitiveType);
  }

  @Override
  public Class<?> getDefaultType() {
    return getUnderlyingType().getDefaultType();
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) {

    return getUnderlyingType().validate(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  @Override
  public <T> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    return getUnderlyingType().
        valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) throws EdmPrimitiveTypeException {

    return getUnderlyingType().valueToString(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  @Override
  public String toUriLiteral(final String literal) {
    return getUnderlyingType().toUriLiteral(literal);
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return getUnderlyingType().fromUriLiteral(literal);
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(getNamespace(), getName());
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.DEFINITION;
  }

  @Override
  public abstract Integer getMaxLength();

  @Override
  public abstract Integer getPrecision();

  @Override
  public abstract Integer getScale();

  @Override
  public abstract Boolean isUnicode();

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.TypeDefinition;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return getFullQualifiedName();
  }

  @Override
  public String getAnnotationsTargetPath() {
    return null;
  }

}
