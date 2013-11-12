package org.apache.olingo.commons.core.edm.primitivetype;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Byte.
 */
final class EdmByte extends SingletonPrimitiveType {

  private static final EdmByte instance = new EdmByte();

  public static EdmByte getInstance() {
    return instance;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof Uint7
        || primitiveType instanceof EdmByte;
  }

  @Override
  public Class<?> getDefaultType() {
    return Short.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Short valueShort;
    try {
      valueShort = Short.parseShort(value);
    } catch (final NumberFormatException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }
    if (valueShort < 0 || valueShort >= 1 << Byte.SIZE) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }

    try {
      return EdmInt64.convertNumber(valueShort, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
      if (((Number) value).longValue() >= 0 && ((Number) value).longValue() < 1 << Byte.SIZE) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else if (value instanceof BigInteger) {
      if (((BigInteger) value).compareTo(BigInteger.ZERO) >= 0
          && ((BigInteger) value).compareTo(BigInteger.valueOf(1 << Byte.SIZE)) < 0) {
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
