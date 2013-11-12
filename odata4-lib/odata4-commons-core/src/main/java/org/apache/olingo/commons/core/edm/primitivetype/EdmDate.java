package org.apache.olingo.commons.core.edm.primitivetype;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type Date.
 */
final class EdmDate extends SingletonPrimitiveType {

  private static final Pattern PATTERN = Pattern.compile(
      "(-?\\p{Digit}{4,})-(\\p{Digit}{2})-(\\p{Digit}{2})");
  private static final EdmDate instance = new EdmDate();

  public static EdmDate getInstance() {
    return instance;
  }

  @Override
  public Class<?> getDefaultType() {
    return Calendar.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {
    Calendar dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTimeValue.clear();

    final Matcher matcher = PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }

    dateTimeValue.set(
        Integer.parseInt(matcher.group(1)),
        Byte.parseByte(matcher.group(2)) - 1, // month is zero-based
        Byte.parseByte(matcher.group(3)));

    try {
      return EdmDateTimeOffset.convertDateTime(dateTimeValue, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
          "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    final Calendar dateTimeValue = EdmDateTimeOffset.createDateTime(value);

    StringBuilder result = new StringBuilder(10); // Ten characters are enough for "normal" dates.
    final int year = dateTimeValue.get(Calendar.YEAR);
    if (year < 0 || year >= 10000) {
      result.append(year);
    } else {
      EdmDateTimeOffset.appendTwoDigits(result, (year / 100) % 100);
      EdmDateTimeOffset.appendTwoDigits(result, year % 100);
    }
    result.append('-');
    EdmDateTimeOffset.appendTwoDigits(result, dateTimeValue.get(Calendar.MONTH) + 1); // month is zero-based
    result.append('-');
    EdmDateTimeOffset.appendTwoDigits(result, dateTimeValue.get(Calendar.DAY_OF_MONTH));
    return result.toString();
  }
}
