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
package org.apache.olingo.commons.core.edm.primitivetype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Decimal.
 */
public final class EdmDecimal extends SingletonPrimitiveType {

  private static final Pattern PATTERN = Pattern.compile(
          "(?:\\+|-)?(?:0*(\\p{Digit}+?))(?:\\.(\\p{Digit}+?)0*)?((?:E|e)(?:\\+|-)?\\p{Digit}+)?");

  private static final EdmDecimal INSTANCE = new EdmDecimal();

  public static EdmDecimal getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16
        || primitiveType instanceof EdmInt32
        || primitiveType instanceof EdmInt64
        || primitiveType instanceof EdmSingle
        || primitiveType instanceof EdmDouble
        || primitiveType instanceof EdmDecimal;
  }

  @Override
  public Class<?> getDefaultType() {
    return BigDecimal.class;
  }

  @Override
  public boolean validate(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {

    return value == null
        ? isNullable == null || isNullable
        : validateLiteral(value) && validatePrecisionAndScale(value, precision, scale);
  }

  private static boolean validateLiteral(final String value) {
    return PATTERN.matcher(value).matches();
  }

  private static boolean validatePrecisionAndScale(final String value, final Integer precision,
      final Integer scale) {

    final Matcher matcher = PATTERN.matcher(value);
    matcher.matches();
    final int significantIntegerDigits = matcher.group(1).equals("0") ? 0 : matcher.group(1).length();
    final int decimals = matcher.group(2) == null ? 0 : matcher.group(2).length();
    return (precision == null || precision >= significantIntegerDigits + decimals)
        && (decimals <= (scale == null ? 0 : scale));
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    if (!validateLiteral(value)) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }
    if (!validatePrecisionAndScale(value, precision, scale)) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' does not match the facets' constraints.");
    }

    try {
      return convertDecimal(new BigDecimal(value), returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("The literal '" + value
          + "' cannot be converted to value type " + returnType + ".", e);
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.", e);
    }
  }

  /**
   * Converts a {@link BigDecimal} value into the requested return type if possible.
   *
   * @param value the value
   * @param returnType the class of the returned value; it must be one of {@link BigDecimal}, {@link Double},
   * {@link Float}, {@link BigInteger}, {@link Long}, {@link Integer}, {@link Short}, or {@link Byte}
   * @return the converted value
   * @throws IllegalArgumentException if the conversion is not possible or would lead to loss of data
   * @throws ClassCastException if the return type is not allowed
   */
  protected static <T> T convertDecimal(final BigDecimal value, final Class<T> returnType)
      throws IllegalArgumentException, ClassCastException {

    if (returnType.isAssignableFrom(BigDecimal.class)) {
      return returnType.cast(value);
    } else if (returnType.isAssignableFrom(Double.class)) {
      final double doubleValue = value.doubleValue();
      if (BigDecimal.valueOf(doubleValue).compareTo(value) == 0) {
        return returnType.cast(doubleValue);
      } else {
        throw new IllegalArgumentException();
      }
    } else if (returnType.isAssignableFrom(Float.class)) {
      final Float floatValue = value.floatValue();
      if (BigDecimal.valueOf(floatValue).compareTo(value) == 0) {
        return returnType.cast(floatValue);
      } else {
        throw new IllegalArgumentException();
      }
    } else {
      try {
        if (returnType.isAssignableFrom(BigInteger.class)) {
          return returnType.cast(value.toBigIntegerExact());
        } else if (returnType.isAssignableFrom(Long.class)) {
          return returnType.cast(value.longValueExact());
        } else if (returnType.isAssignableFrom(Integer.class)) {
          return returnType.cast(value.intValueExact());
        } else if (returnType.isAssignableFrom(Short.class)) {
          return returnType.cast(value.shortValueExact());
        } else if (returnType.isAssignableFrom(Byte.class)) {
          return returnType.cast(value.byteValueExact());
        } else {
          throw new ClassCastException("unsupported return type " + returnType.getSimpleName());
        }
      } catch (final ArithmeticException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    String result;
    if (value instanceof Long || value instanceof Integer || value instanceof Short
        || value instanceof Byte || value instanceof BigInteger) {
      result = value.toString();
      final int digits = result.startsWith("-") ? result.length() - 1 : result.length();
      if (precision != null && precision < digits) {
        throw new EdmPrimitiveTypeException("The value '" + value + "' does not match the facets' constraints.");
      }

    } else if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
      BigDecimal bigDecimalValue;
      try {
        bigDecimalValue = value instanceof Double ? BigDecimal.valueOf((Double) value)
            : value instanceof Float ? BigDecimal.valueOf((Float) value) : (BigDecimal) value;
      } catch (final NumberFormatException e) {
        throw new EdmPrimitiveTypeException("The value '" + value + "' is not valid.", e);
      }

      final int digits = bigDecimalValue.scale() >= 0
          ? Math.max(bigDecimalValue.precision(), bigDecimalValue.scale())
              : bigDecimalValue.precision() - bigDecimalValue.scale();
          if ((precision == null || precision >= digits) && (bigDecimalValue.scale() <= (scale == null ? 0 : scale))) {
            result = bigDecimalValue.toPlainString();
          } else {
            throw new EdmPrimitiveTypeException("The value '" + value + "' does not match the facets' constraints.");
          }

    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }

    return result;
  }
}
