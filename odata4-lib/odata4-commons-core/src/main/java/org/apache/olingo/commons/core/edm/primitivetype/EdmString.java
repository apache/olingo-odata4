package org.apache.olingo.commons.core.edm.primitivetype;

import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type String.
 */
final class EdmString extends SingletonPrimitiveType {

  private static final Pattern PATTERN_ASCII = Pattern.compile("\\p{ASCII}*");
  private static final EdmString instance = new EdmString();
  {
    uriPrefix = "'";
    uriSuffix = "'";
  }

  public static EdmString getInstance() {
    return instance;
  }

  @Override
  public Class<?> getDefaultType() {
    return String.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    if (isUnicode != null && !isUnicode && !PATTERN_ASCII.matcher(value).matches()
        || maxLength != null && maxLength < value.length()) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.LITERAL_FACETS_NOT_MATCHED.addContent(value, facets)");
    }

    if (returnType.isAssignableFrom(String.class)) {
      return returnType.cast(value);
    } else {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType)");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    final String result = value instanceof String ? (String) value : String.valueOf(value);

    if (isUnicode != null && !isUnicode && !PATTERN_ASCII.matcher(result).matches()
        || maxLength != null && maxLength < result.length()) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_FACETS_NOT_MATCHED.addContent(value, facets)");
    }

    return result;
  }

  @Override
  public String toUriLiteral(final String literal) {
    if (literal == null) {
      return null;
    }

    final int length = literal.length();

    StringBuilder uriLiteral = new StringBuilder(length + 2);
    uriLiteral.append(uriPrefix);
    for (int i = 0; i < length; i++) {
      final char c = literal.charAt(i);
      if (c == '\'') {
        uriLiteral.append(c);
      }
      uriLiteral.append(c);
    }
    uriLiteral.append(uriSuffix);
    return uriLiteral.toString();
  }

  @Override
  public String fromUriLiteral(final String literal) throws EdmPrimitiveTypeException {
    return literal == null ? null : super.fromUriLiteral(literal).replace("''", "'");
  }
}
