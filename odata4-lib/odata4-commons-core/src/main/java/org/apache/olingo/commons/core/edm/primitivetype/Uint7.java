package org.apache.olingo.commons.core.edm.primitivetype;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the internal primitive type "unsigned 7-bit integer".
 */
final class Uint7 extends SingletonPrimitiveType {

  private static final Uint7 instance = new Uint7();

  public static Uint7 getInstance() {
    return instance;
  }

  @Override
  public String getNamespace() {
    return SYSTEM_NAMESPACE;
  }

  @Override
  public String getName() {
    return getClass().getSimpleName();
  }

  @Override
  public Class<?> getDefaultType() {
    return Byte.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    return EdmSByte.getInstance().internalValueOfString(value, isNullable, maxLength, precision, scale, isUnicode,
        returnType);
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    return EdmSByte.getInstance().internalValueToString(value, isNullable, maxLength, precision, scale, isUnicode);
  }
}
