/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type DateTimeOffset.
 */
public final class EdmDateTimeOffset extends SingletonPrimitiveType {

  private static final Pattern PATTERN = Pattern.compile(
          "(-?\\p{Digit}{4,})-(\\p{Digit}{2})-(\\p{Digit}{2})"
          + "T(\\p{Digit}{2}):(\\p{Digit}{2})(?::(\\p{Digit}{2})(\\.(\\p{Digit}{0,3}?)0*)?)?"
          + "(Z|([-+]\\p{Digit}{2}:\\p{Digit}{2}))?");

  private static final EdmDateTimeOffset INSTANCE = new EdmDateTimeOffset();

  public static EdmDateTimeOffset getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<?> getDefaultType() {
    return Calendar.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    final Matcher matcher = PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }

    final String timeZoneOffset = matcher.group(9) != null && matcher.group(10) != null
                                  && !matcher.group(10).matches("[-+]0+:0+") ? matcher.group(10) : null;
    final Calendar dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone("GMT" + timeZoneOffset));
    if (dateTimeValue.get(Calendar.ZONE_OFFSET) == 0 && timeZoneOffset != null) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
    }
    dateTimeValue.clear();

    dateTimeValue.set(
            Short.parseShort(matcher.group(1)),
            Byte.parseByte(matcher.group(2)) - 1, // month is zero-based
            Byte.parseByte(matcher.group(3)),
            Byte.parseByte(matcher.group(4)),
            Byte.parseByte(matcher.group(5)),
            matcher.group(6) == null ? 0 : Byte.parseByte(matcher.group(6)));

    if (matcher.group(7) != null) {
      if (matcher.group(7).length() == 1 || matcher.group(7).length() > 13) {
        throw new EdmPrimitiveTypeException(
                "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)");
      }
      final String decimals = matcher.group(8);
      if (decimals.length() > (precision == null ? 0 : precision)) {
        throw new EdmPrimitiveTypeException(
                "EdmPrimitiveTypeException.LITERAL_FACETS_NOT_MATCHED.addContent(value, facets)");
      }
      final String milliSeconds = decimals + "000".substring(decimals.length());
      dateTimeValue.set(Calendar.MILLISECOND, Short.parseShort(milliSeconds));
    }

    try {
      return convertDateTime(dateTimeValue, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value), e");
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(returnType), e");
    }
  }

  /**
   * Converts a {@link Calendar} value into the requested return type if possible.
   *
   * @param dateTimeValue the value
   * @param returnType the class of the returned value; it must be one of {@link Calendar}, {@link Long}, or
   * {@link Date}
   * @return the converted value
   * @throws IllegalArgumentException if the Calendar value is not valid
   * @throws ClassCastException if the return type is not allowed
   */
  protected static <T> T convertDateTime(final Calendar dateTimeValue, final Class<T> returnType)
          throws IllegalArgumentException, ClassCastException {

    // The Calendar class does not check any values until a get method is called,
    // so we do just that to validate the fields that may have been set,
    // not because we want to return something else.
    // For strict checks, the lenient mode is switched off.
    dateTimeValue.setLenient(false);

    if (returnType.isAssignableFrom(Calendar.class)) {
      // Ensure that all fields are recomputed.
      dateTimeValue.get(Calendar.MILLISECOND); // may throw IllegalArgumentException
      // Reset the lenient mode to its default.
      dateTimeValue.setLenient(true);
      return returnType.cast(dateTimeValue);
    } else if (returnType.isAssignableFrom(Long.class)) {
      return returnType.cast(dateTimeValue.getTimeInMillis()); // may throw IllegalArgumentException
    } else if (returnType.isAssignableFrom(Date.class)) {
      return returnType.cast(dateTimeValue.getTime()); // may throw IllegalArgumentException
    } else {
      throw new ClassCastException("unsupported return type " + returnType.getSimpleName());
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Calendar dateTimeValue = createDateTime(value);

    final StringBuilder result = new StringBuilder(23); // 23 characters are enough for millisecond precision.
    final int year = dateTimeValue.get(Calendar.YEAR);
    appendTwoDigits(result, year / 100);
    appendTwoDigits(result, year % 100);
    result.append('-');
    appendTwoDigits(result, dateTimeValue.get(Calendar.MONTH) + 1); // month is zero-based
    result.append('-');
    appendTwoDigits(result, dateTimeValue.get(Calendar.DAY_OF_MONTH));
    result.append('T');
    appendTwoDigits(result, dateTimeValue.get(Calendar.HOUR_OF_DAY));
    result.append(':');
    appendTwoDigits(result, dateTimeValue.get(Calendar.MINUTE));
    result.append(':');
    appendTwoDigits(result, dateTimeValue.get(Calendar.SECOND));

    try {
      appendMilliseconds(result, dateTimeValue.get(Calendar.MILLISECOND), precision);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_FACETS_NOT_MATCHED.addContent(value, facets), e");
    }

    final int offsetInMinutes = (dateTimeValue.get(Calendar.ZONE_OFFSET)
                                 + dateTimeValue.get(Calendar.DST_OFFSET)) / 60 / 1000;
    final int offsetHours = offsetInMinutes / 60;
    final int offsetMinutes = Math.abs(offsetInMinutes % 60);
    final String offsetString = offsetInMinutes == 0 ? "Z" : String.format("%+03d:%02d", offsetHours, offsetMinutes);
    result.append(offsetString);

    return result.toString();
  }

  /**
   * Creates a date/time value from the given value.
   *
   * @param value the value as {@link Calendar}, {@link Date}, or {@link Long}
   * @return the value as {@link Calendar}
   * @throws EdmPrimitiveTypeException if the type of the value is not supported
   */
  protected static <T> Calendar createDateTime(final T value) throws EdmPrimitiveTypeException {
    Calendar dateTimeValue;
    if (value instanceof Date) {
      // Although java.util.Date, as stated in its documentation,
      // "is intended to reflect coordinated universal time (UTC)",
      // its toString() method uses the default time zone. And so do we.
      dateTimeValue = Calendar.getInstance();
      dateTimeValue.setTime((Date) value);
    } else if (value instanceof Calendar) {
      dateTimeValue = (Calendar) ((Calendar) value).clone();
    } else if (value instanceof Long) {
      dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      dateTimeValue.setTimeInMillis((Long) value);
    } else {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
    return dateTimeValue;
  }

  /**
   * Appends the given number to the given string builder, assuming that the number has at most two digits,
   * performance-optimized.
   *
   * @param result a {@link StringBuilder}
   * @param number an integer that must satisfy <code>0 <= number <= 99</code>
   */
  protected static void appendTwoDigits(final StringBuilder result, final int number) {
    result.append((char) ('0' + number / 10));
    result.append((char) ('0' + number % 10));
  }

  /**
   * Appends the given number of milliseconds to the given string builder, assuming that the number has at most three
   * digits, performance-optimized.
   *
   * @param result a {@link StringBuilder}
   * @param milliseconds an integer that must satisfy <code>0 &lt;= milliseconds &lt;= 999</code>
   * @param precision the upper limit for decimal digits (optional, defaults to zero)
   */
  protected static void appendMilliseconds(final StringBuilder result, final long milliseconds,
          final Integer precision) throws IllegalArgumentException {
    final int digits = milliseconds % 1000 == 0 ? 0 : milliseconds % 100 == 0 ? 1 : milliseconds % 10 == 0 ? 2 : 3;
    if (digits > 0) {
      result.append('.');
      for (int d = 100; d > 0; d /= 10) {
        final byte digit = (byte) (milliseconds % (d * 10) / d);
        if (digit > 0 || milliseconds % d > 0) {
          result.append((char) ('0' + digit));
        }
      }

      if (precision == null || precision < digits) {
        throw new IllegalArgumentException();
      }
    }
  }
}
