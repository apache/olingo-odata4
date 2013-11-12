package org.apache.olingo.commons.core.edm.primitivetype;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Int32.
 */
final class EdmInt32 extends SingletonPrimitiveType {

  private static final EdmInt32 instance = new EdmInt32();

  public static EdmInt32 getInstance() {
    return instance;
  }

  @Override
  public boolean isCompatible(final EdmPrimitiveType primitiveType) {
    return primitiveType instanceof Uint7
        || primitiveType instanceof EdmByte
        || primitiveType instanceof EdmSByte
        || primitiveType instanceof EdmInt16
        || primitiveType instanceof EdmInt32;
  }

  @Override
  public Class<?> getDefaultType() {
    return Integer.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Integer valueInteger;
    try {
      valueInteger = Integer.parseInt(value);
    } catch (final NumberFormatException e) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }

    try {
      return EdmInt64.convertNumber(valueInteger, returnType);
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
    if (value instanceof Byte || value instanceof Short || value instanceof Integer) {
      return value.toString();
    } else if (value instanceof Long) {
      if ((Long) value >= Integer.MIN_VALUE && (Long) value <= Integer.MAX_VALUE) {
        return value.toString();
      } else {
        throw new EdmPrimitiveTypeException(
            "EdmPrimitiveTypeException.VALUE_ILLEGAL_CONTENT.addContent(value)");
      }
    } else if (value instanceof BigInteger) {
      if (((BigInteger) value).bitLength() < Integer.SIZE) {
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
