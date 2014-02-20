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
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;

/**
 * Abstract implementation of the EDM primitive-type interface.
 */
abstract class AbstractPrimitiveType implements EdmPrimitiveType {

  protected String uriPrefix = "";

  protected String uriSuffix = "";

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return equals(primitiveType);
  }

  @Override
  public boolean validate(final String value,
          final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
          final Boolean isUnicode) {

    try {
      valueOfString(value, isNullable, maxLength, precision, scale, isUnicode, getDefaultType());
      return true;
    } catch (final EdmPrimitiveTypeException e) {
      return false;
    }
  }

  @Override
  public final <T> T valueOfString(final String value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode, final Class<T> returnType)
          throws EdmPrimitiveTypeException {

    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_NULL_NOT_ALLOWED");
      }
      return null;
    }
    return internalValueOfString(value, isNullable, maxLength, precision, scale, isUnicode, returnType);
  }

  protected abstract <T> T internalValueOfString(String value,
          Boolean isNullable, Integer maxLength, Integer precision, Integer scale, Boolean isUnicode,
          Class<T> returnType) throws EdmPrimitiveTypeException;

  @Override
  public final String valueToString(final Object value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value == null) {
      if (isNullable != null && !isNullable) {
        throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.VALUE_NULL_NOT_ALLOWED");
      }
      return null;
    }
    return internalValueToString(value, isNullable, maxLength, precision, scale, isUnicode);
  }

  protected abstract <T> String internalValueToString(T value,
          Boolean isNullable, Integer maxLength, Integer precision, Integer scale,
          Boolean isUnicode) throws EdmPrimitiveTypeException;

  @Override
  public String toUriLiteral(final String literal) {
    return literal == null
           ? null
           : uriPrefix.isEmpty() && uriSuffix.isEmpty()
             ? literal
             : uriPrefix + literal + uriSuffix;
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    if (literal == null) {
      return null;
    } else if (uriPrefix.isEmpty() && uriSuffix.isEmpty()) {
      return literal;
    } else if (literal.length() >= uriPrefix.length() + uriSuffix.length()
               && literal.startsWith(uriPrefix) && literal.endsWith(uriSuffix)) {

      return literal.substring(uriPrefix.length(), literal.length() - uriSuffix.length());
    } else {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(literal)");
    }
  }

  @Override
  public String toString() {
    return new FullQualifiedName(getNamespace(), getName()).getFullQualifiedNameAsString();
  }
}
