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

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type DateTimeOffset.
 */
public final class EdmDateTimeOffset extends SingletonPrimitiveType {

  private static final Pattern PATTERN = Pattern.compile(
      "(-?\\p{Digit}{4,})-(\\p{Digit}{2})-(\\p{Digit}{2})"
          + "T(\\p{Digit}{2}):(\\p{Digit}{2})(?::(\\p{Digit}{2})(\\.(\\p{Digit}{0,12}?)0*)?)?"
          + "(Z|([-+]\\p{Digit}{2}:\\p{Digit}{2}))?");

  private static final EdmDateTimeOffset INSTANCE = new EdmDateTimeOffset();

  public static EdmDateTimeOffset getInstance() {
    return INSTANCE;
  }

  @Override
  public Class<?> getDefaultType() {
    return Timestamp.class;
  }

  @Override
  protected <T> T internalValueOfString(final String value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode, final Class<T> returnType) throws EdmPrimitiveTypeException {

    final Matcher matcher = PATTERN.matcher(value);
    if (!matcher.matches()) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }

    final String timeZoneOffset = matcher.group(9) == null || matcher.group(10) == null
        || matcher.group(10).matches("[-+]0+:0+") ? "" : matcher.group(10);
    final Calendar dateTimeValue = Calendar.getInstance(TimeZone.getTimeZone("GMT" + timeZoneOffset));
    if (dateTimeValue.get(Calendar.ZONE_OFFSET) == 0 && !timeZoneOffset.isEmpty()) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }
    dateTimeValue.clear();

    dateTimeValue.set(
        Short.parseShort(matcher.group(1)),
        Byte.parseByte(matcher.group(2)) - 1, // month is zero-based
        Byte.parseByte(matcher.group(3)),
        Byte.parseByte(matcher.group(4)),
        Byte.parseByte(matcher.group(5)),
        matcher.group(6) == null ? 0 : Byte.parseByte(matcher.group(6)));

    int nanoSeconds = 0;
    if (matcher.group(7) != null) {
      if (matcher.group(7).length() == 1 || matcher.group(7).length() > 13) {
        throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
      }
      final String decimals = matcher.group(8);
      if (decimals.length() > (precision == null ? 0 : precision)) {
        throw new EdmPrimitiveTypeException("The literal '" + value + "' does not match the facets' constraints.");
      }
      if (returnType.isAssignableFrom(Timestamp.class)) {
        if (decimals.length() <= 9) {
          nanoSeconds = Integer.parseInt(decimals + "000000000".substring(decimals.length()));
        } else {
          throw new EdmPrimitiveTypeException("The literal '" + value
              + "' cannot be converted to value type " + returnType + ".");
        }
      } else {
        if (decimals.length() <= 3) {
          final String milliSeconds = decimals + "000".substring(decimals.length());
          dateTimeValue.set(Calendar.MILLISECOND, Short.parseShort(milliSeconds));
        } else {
          throw new EdmPrimitiveTypeException("The literal '" + value
              + "' cannot be converted to value type " + returnType + ".");
        }
      }
    }

    try {
      return convertDateTime(dateTimeValue, nanoSeconds, returnType);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.", e);
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.", e);
    }
  }

  /**
   * <p>Converts a {@link Calendar} value into the requested return type if possible.</p>
   * <p>It is expected that the {@link Calendar} value will already be in the desired time zone.</p>
   * @param dateTimeValue the value
   * @param nanoSeconds nanoseconds part of the value; only used for the {@link Timestamp} return type
   * @param returnType the class of the returned value;
   *                   it must be one of {@link Calendar}, {@link Long}, {@link Date},
   *                   {@link Time}, or {@link Timestamp}
   * @return the converted value
   * @throws IllegalArgumentException if the Calendar value is not valid
   * @throws ClassCastException if the return type is not allowed
   */
  protected static <T> T convertDateTime(final Calendar dateTimeValue, final int nanoSeconds,
      final Class<T> returnType) throws IllegalArgumentException, ClassCastException {

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
    } else if (returnType.isAssignableFrom(Timestamp.class)) {
      Timestamp timestamp = new Timestamp(dateTimeValue.getTimeInMillis());
      timestamp.setNanos(nanoSeconds);
      return returnType.cast(timestamp);
    } else if (returnType.isAssignableFrom(Time.class)) {
      // Normalize the value.
      dateTimeValue.set(Calendar.YEAR, 1970);
      dateTimeValue.set(Calendar.MONTH, Calendar.JANUARY);
      dateTimeValue.set(Calendar.DAY_OF_MONTH, 1);
      dateTimeValue.set(Calendar.MILLISECOND, 0);
      return returnType.cast(new Time(dateTimeValue.getTimeInMillis())); // may throw IllegalArgumentException
    } else if (returnType.isAssignableFrom(java.sql.Date.class)) {
      // Normalize the value.
      dateTimeValue.set(Calendar.HOUR_OF_DAY, 0);
      dateTimeValue.set(Calendar.MINUTE, 0);
      dateTimeValue.set(Calendar.SECOND, 0);
      dateTimeValue.set(Calendar.MILLISECOND, 0);
      return returnType.cast(new java.sql.Date(dateTimeValue.getTimeInMillis())); // may throw IllegalArgumentException
    } else {
      throw new ClassCastException("unsupported return type " + returnType.getSimpleName());
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
      final Boolean isNullable, final Integer maxLength, final Integer precision,
      final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Calendar dateTimeValue = createDateTime(value, false);

    StringBuilder result = new StringBuilder();
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

    final int fractionalSecs = value instanceof Timestamp ?
        ((Timestamp) value).getNanos() :
        dateTimeValue.get(Calendar.MILLISECOND);
    try {
      appendFractionalSeconds(result, fractionalSecs, value instanceof Timestamp, precision);
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("The value '" + value + "' does not match the facets' constraints.", e);
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
   * @param value   the value as {@link Calendar}, {@link Date}, or {@link Long}
   * @param isLocal whether the value is to be in the default time zone (or in GMT)
   * @return the value as {@link Calendar} in the desired time zone
   * @throws EdmPrimitiveTypeException if the type of the value is not supported
   */
	protected static <T> Calendar createDateTime(final T value, final boolean isLocal)
			throws EdmPrimitiveTypeException {
		Calendar dateTimeValue;
		if (value instanceof Date) {
			dateTimeValue = Calendar.getInstance(isLocal ? TimeZone.getDefault() : TimeZone.getTimeZone("GMT"));
			dateTimeValue.setTime((Date) value);
		} else if (value instanceof Calendar) {
			Calendar calendar = (Calendar) value;
			// make sure that the fields are populated before the clone
			calendar.get(Calendar.YEAR);
			dateTimeValue = (Calendar) calendar.clone();
		} else if (value instanceof Long) {
			dateTimeValue = Calendar.getInstance(isLocal ? TimeZone.getDefault() : TimeZone.getTimeZone("GMT"));
			dateTimeValue.setTimeInMillis((Long) value);
		} else {
			throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
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
   * Appends the given milli- or nanoseconds to the given string builder, performance-optimized.
   * @param result a {@link StringBuilder}
   * @param fractionalSeconds fractional seconds (nonnegative and assumed to be in the valid range)
   * @param isNano whether the value is to be interpreted as nanoseconds (milliseconds if false)
   * @param precision the upper limit for decimal digits (optional, defaults to zero)
   * @throws IllegalArgumentException if precision is not met
   */
  protected static void appendFractionalSeconds(StringBuilder result, final int fractionalSeconds,
      final boolean isNano, final Integer precision) throws IllegalArgumentException {
    if (fractionalSeconds > 0) {
      // Determine the number of trailing zeroes.
      int nonSignificant = 0;
      int output = fractionalSeconds;
      while (output % 10 == 0) {
        output /= 10;
        nonSignificant++;
      }

      if (precision != null && precision < (isNano ? 9 : 3) - nonSignificant) {
        throw new IllegalArgumentException();
      }

      result.append('.');
      for (int d = 100 * (isNano ? 1000 * 1000 : 1); d > 0; d /= 10) {
        final byte digit = (byte) (fractionalSeconds % (d * 10) / d);
        if (digit > 0 || fractionalSeconds % d > 0) {
          result.append((char) ('0' + digit));
        }
      }
    }
  }
}
