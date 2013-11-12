package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

//TODO: Is this class still necessary?
/**
 * Implementation of the simple type Null.
 */
final class EdmNull extends SingletonPrimitiveType {

  private static final EdmNull instance = new EdmNull();

  public static EdmNull getInstance() {
    return instance;
  }

  @Override
  public boolean equals(final Object obj) {
    return this == obj || obj == null;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public Class<?> getDefaultType() {
    return null;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return null;
  }

  @Override
  public String toUriLiteral(final String literal) {
    return "null";
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return null;
  }
}
