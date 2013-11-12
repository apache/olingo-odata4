package org.apache.olingo.commons.core.edm.primitivetype;

import java.util.UUID;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Guid.
 */
final class EdmGuid extends SingletonPrimitiveType {

  private static final String PATTERN = "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}";
  private static final EdmGuid instance = new EdmGuid();

  public static EdmGuid getInstance() {
    return instance;
  }

  @Override
  public Class<?> getDefaultType() {
    return UUID.class;
  }

  @Override
  public boolean validate(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) {
    return value == null ? isNullable == null || isNullable : validateLiteral(value);
  }

  private boolean validateLiteral(final String value) {
    return value.matches(PATTERN);
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode,
      final Class<T> returnType) throws EdmPrimitiveTypeException {
    UUID result;
    if (validateLiteral(value)) {
      result = UUID.fromString(value);
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }

    if (returnType.isAssignableFrom(UUID.class)) {
      return returnType.cast(result);
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType)");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    if (value instanceof UUID) {
      return ((UUID) value).toString();
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
