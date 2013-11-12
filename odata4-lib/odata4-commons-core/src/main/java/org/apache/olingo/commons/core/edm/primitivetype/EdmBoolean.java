package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

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
