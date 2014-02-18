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
package org.apache.olingo.odata4.server.core.edm.provider;

import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.odata4.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.odata4.commons.core.edm.primitivetype.EdmPrimitiveTypeKind;
import org.apache.olingo.odata4.server.api.edm.provider.TypeDefinition;

public class EdmTypeDefinitionImpl extends EdmNamedImpl implements EdmTypeDefinition {

  private final FullQualifiedName typeDefinitionName;
  private TypeDefinition typeDefinition;
  private EdmPrimitiveType edmPrimitiveTypeInstance;

  public EdmTypeDefinitionImpl(final EdmProviderImpl edm, final FullQualifiedName typeDefinitionName,
      final TypeDefinition typeDefinition) {
    super(edm, typeDefinitionName.getName());
    this.typeDefinitionName = typeDefinitionName;
    this.typeDefinition = typeDefinition;
    // TODO: Should we check for edmNamespace in the underlying type name?
    try {
      edmPrimitiveTypeInstance =
          EdmPrimitiveTypeKind.valueOf(typeDefinition.getUnderlyingType().getName()).getEdmPrimitiveTypeInstance();
    } catch (IllegalArgumentException e) {
      throw new EdmException("Invalid underlying type: " + typeDefinitionName, e);
    }
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return edmPrimitiveTypeInstance.isCompatible(primitiveType);
  }

  @Override
  public Class<?> getDefaultType() {
    return edmPrimitiveTypeInstance.getDefaultType();
  }

  @Override
  public boolean validate(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) {
    return edmPrimitiveTypeInstance.validate(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  @Override
  public <T> T valueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return edmPrimitiveTypeInstance
        .valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
  }

  @Override
  public String valueToString(final Object value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale,
      final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return edmPrimitiveTypeInstance.valueToString(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  @Override
  public String toUriLiteral(final String literal) {
    return edmPrimitiveTypeInstance.toUriLiteral(literal);
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return edmPrimitiveTypeInstance.fromUriLiteral(literal);
  }

  @Override
  public String getNamespace() {
    return typeDefinitionName.getNamespace();
  }

  @Override
  public EdmTypeKind getKind() {
    return EdmTypeKind.DEFINITION;
  }

  @Override
  public EdmPrimitiveType getUnderlyingType() {
    return edmPrimitiveTypeInstance;
  }

  @Override
  public Integer getMaxLength() {
    return typeDefinition.getMaxLength();
  }

  @Override
  public Integer getPrecision() {
    return typeDefinition.getPrecision();
  }

  @Override
  public Integer getScale() {
    return typeDefinition.getScale();
  }

  @Override
  public Boolean isUnicode() {
    return typeDefinition.getIsUnicode();
  }
}
