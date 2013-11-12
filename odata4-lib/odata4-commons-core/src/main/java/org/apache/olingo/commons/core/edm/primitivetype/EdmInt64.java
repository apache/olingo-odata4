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
package org.apache.olingo.commons.core.edm.primitivetype;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Int64.
 */
final class EdmInt64 extends SingletonPrimitiveType {

  private static final EdmInt64 instance = new EdmInt64();

  public static EdmInt64 getInstance() {
    return instance;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof Uint7
        || primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16
        || primitiveType instanceof EdmInt32
        || primitiveType instanceof EdmInt64;
  }

  @Override
  public Class<?> getDefaultType() {
    return Long.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Long valueLong;
    try {
      valueLong = Long.parseLong(value);
    } catch (final NumberFormatException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }

    try {
      return convertNumber(valueLong, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  /**
   * Converts a whole {@link Number} value into the requested return type if possible.
   * @param value the value
   * @param returnType the class of the returned value; it must be one of {@link BigInteger}, {@link Long},
   * {@link Integer}, {@link Short}, or {@link Byte}
   * @return the converted value
   * @throws IllegalArgumentException if the conversion is not possible
   * @throws ClassCastException if the return type is not allowed
   */
  protected static <T> T convertNumber(final Number value, final Class<T> returnType) throws IllegalArgumentException,
      ClassCastException {
    if (returnType.isAssignableFrom(Long.class)) {
      return returnType.cast(value.longValue());
    } else if (returnType.isAssignableFrom(BigInteger.class)) {
      return returnType.cast(BigInteger.valueOf(value.longValue()));
    } else if (returnType.isAssignableFrom(Byte.class)) {
      if (value.longValue() >= Byte.MIN_VALUE && value.longValue() <= Byte.MAX_VALUE) {
        return returnType.cast(value.byteValue());
      } else {
        throw new IllegalArgumentException();
      }
    } else if (returnType.isAssignableFrom(Short.class)) {
      if (value.longValue() >= Short.MIN_VALUE && value.longValue() <= Short.MAX_VALUE) {
        return returnType.cast(value.shortValue());
      } else {
        throw new IllegalArgumentException();
      }
    } else if (returnType.isAssignableFrom(Integer.class)) {
      if (value.longValue() >= Integer.MIN_VALUE && value.longValue() <= Integer.MAX_VALUE) {
        return returnType.cast(value.intValue());
      } else {
        throw new IllegalArgumentException();
      }
    } else {
      throw new ClassCastException("unsupported return type " + returnType.getSimpleName());
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      return value.toString();
    } else if (value instanceof BigInteger) {
      if (((BigInteger) value).bitLength() < Long.SIZE) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
