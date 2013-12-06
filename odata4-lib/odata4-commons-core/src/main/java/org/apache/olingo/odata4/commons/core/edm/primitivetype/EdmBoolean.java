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
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Boolean.
 */
final class EdmBoolean extends SingletonPrimitiveType {

  private static final EdmBoolean instance = new EdmBoolean();

  public static EdmBoolean getInstance() {
    return instance;
  }

  @Override
  public Class<?> getDefaultType() {
    return Boolean.class;
  }

  @Override
  public boolean validate(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {
    return value == null ? isNullable == null || isNullable : validateLiteral(value);
  }

  private static boolean validateLiteral(final String value) {
    return "true".equals(value) || "false".equals(value);
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    if (validateLiteral(value)) {
      if (returnType.isAssignableFrom(Boolean.class)) {
        return returnType.cast(Boolean.valueOf("true".equals(value)));
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType)");
      }
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Boolean) {
      return Boolean.toString((Boolean) value);
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
