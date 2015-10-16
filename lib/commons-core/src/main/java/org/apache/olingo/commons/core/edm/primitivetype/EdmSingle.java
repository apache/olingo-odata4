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
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Single.
 */
public final class EdmSingle extends SingletonPrimitiveType {

  private static final Pattern PATTERN = Pattern.compile(
      "(?:\\+|-)?\\p{Digit}{1,9}(?:\\.\\p{Digit}{1,9})?(?:(?:E|e)(?:\\+|-)?\\p{Digit}{1,2})?");

  private static final EdmSingle INSTANCE = new EdmSingle();

  public static EdmSingle getInstance() {
    return INSTANCE;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16
        || primitiveType instanceof EdmInt32
        || primitiveType instanceof EdmInt64
        || primitiveType instanceof EdmSingle;
  }

  @Override
  public Class<?> getDefaultType() {
    return Float.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    Float result = null;
    BigDecimal bigDecimalValue = null;
    // Handle special values first.
    if (value.equals(EdmDouble.NEGATIVE_INFINITY)) {
      result = Float.NEGATIVE_INFINITY;
    } else if (value.equals(EdmDouble.POSITIVE_INFINITY)) {
      result = Float.POSITIVE_INFINITY;
    } else if (value.equals(EdmDouble.NaN)) {
      result = Float.NaN;
    } else {
      // Now only "normal" numbers remain.
      if (!PATTERN.matcher(value).matches()) {
        throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
      }

      // The number format is checked above, so we don't have to catch NumberFormatException.
      bigDecimalValue = new BigDecimal(value);
      result = bigDecimalValue.floatValue();
      // "Real" infinite values have been treated already above, so we can throw an exception
      // if the conversion to a float results in an infinite value.
      if (result.isInfinite() || bigDecimalValue.compareTo(new BigDecimal(result.toString())) != 0) {
        throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
      }
    }

    if (returnType.isAssignableFrom(Float.class)) {
      return returnType.cast(result);
    } else if (result.isInfinite() || result.isNaN()) {
      if (returnType.isAssignableFrom(Double.class)) {
        return returnType.cast(result.doubleValue());
      } else {
        throw new EdmPrimitiveTypeException("The literal '" + value
            + "' cannot be converted to value type " + returnType + ".");
      }
    } else {
      try {
        return EdmDecimal.convertDecimal(bigDecimalValue, returnType);
      } catch (final IllegalArgumentException e) {
        throw new EdmPrimitiveTypeException("The literal '" + value
            + "' cannot be converted to value type " + returnType + ".", e);
      } catch (final ClassCastException e) {
        throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.", e);
      }
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (value instanceof Long || value instanceof Integer) {
      if (Math.abs(((Number) value).longValue()) < 1L << 22) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException("The value '" + value + "' is not valid.");
      }
    } else if (value instanceof Short || value instanceof Byte) {
      return value.toString();
    } else if (value instanceof Double) {
      if (((Double) value).isInfinite()) {
        return (Double) value == Double.NEGATIVE_INFINITY ? EdmDouble.NEGATIVE_INFINITY : EdmDouble.POSITIVE_INFINITY;
      } else {
        final String floatString = Float.toString(((Double) value).floatValue());
        if (floatString.equals(value.toString())) {
          return floatString;
        } else {
          throw new EdmPrimitiveTypeException("The value '" + value + "' is not valid.");
        }
      }
    } else if (value instanceof Float) {
      return (Float) value == Float.NEGATIVE_INFINITY ? EdmDouble.NEGATIVE_INFINITY
          : (Float) value == Float.POSITIVE_INFINITY ? EdmDouble.POSITIVE_INFINITY : value.toString();
    } else if (value instanceof BigDecimal) {
      final float floatValue = ((BigDecimal) value).floatValue();
      if (!Float.isInfinite(floatValue) && BigDecimal.valueOf(floatValue).compareTo((BigDecimal) value) == 0) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException("The value '" + value + "' is not valid.");
      }
    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }
  }
}
