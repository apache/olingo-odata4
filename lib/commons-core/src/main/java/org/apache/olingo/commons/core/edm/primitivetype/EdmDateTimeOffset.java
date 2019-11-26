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

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type DateTimeOffset.
 */
public final class EdmDateTimeOffset extends SingletonPrimitiveType {

  private static final ZoneId ZULU = ZoneId.of("Z");

  private static final Pattern PATTERN = Pattern.compile("(-?\\p{Digit}{4,})-(\\p{Digit}{2})-(\\p{Digit}{2})"
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
  protected <T> T internalValueOfString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode, final Class<T> returnType)
      throws EdmPrimitiveTypeException {
    try {
      ZonedDateTime zdt = parseZonedDateTime(value);

      return convertZonedDateTime(zdt, returnType);
    } catch (DateTimeParseException ex) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.", ex);
    } catch (final ClassCastException e) {
      throw new EdmPrimitiveTypeException("The value type " + returnType + " is not supported.", e);
    }
  }

  private static ZonedDateTime parseZonedDateTime(final String value) {
    ZonedDateTime zdt;
    try {
      // ISO-8601 conform pattern
      zdt = ZonedDateTime.parse(value);
    } catch (DateTimeParseException ex) {
      // for backward compatibility - allow patterns that don't specify a time zone
      final Matcher matcher = PATTERN.matcher(value);
      if (matcher.matches() && matcher.group(9) == null) {
        zdt = ZonedDateTime.parse(value + "Z");
      } else {
        throw ex;
      }
    }
    return zdt;
  }

  @SuppressWarnings("unchecked")
  private static <T> T convertZonedDateTime(ZonedDateTime zdt, Class<T> returnType) {
    if (returnType == ZonedDateTime.class) {
      return (T) zdt;
    } else if (returnType == Instant.class) {
      return (T) zdt.toInstant();
    } else if (returnType.isAssignableFrom(Timestamp.class)) {
      return (T) Timestamp.from(zdt.toInstant());
    } else if (returnType.isAssignableFrom(java.util.Date.class)) {
      return (T) java.util.Date.from(zdt.toInstant());
    } else if (returnType.isAssignableFrom(java.sql.Time.class)) {
      return (T) new java.sql.Time(zdt.toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
    } else if (returnType.isAssignableFrom(java.sql.Date.class)) {
      return (T) new java.sql.Date(zdt.toInstant().truncatedTo(ChronoUnit.SECONDS).toEpochMilli());
    } else if (returnType.isAssignableFrom(Long.class)) {
      return (T) Long.valueOf(zdt.toInstant().toEpochMilli());
    } else if (returnType.isAssignableFrom(Calendar.class)) {
      return (T) GregorianCalendar.from(zdt);
    } else {
      throw new ClassCastException("Unsupported return type " + returnType.getSimpleName());
    }
  }

  @Override
  protected <T> String internalValueToString(final T value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {
    ZonedDateTime zdt = createZonedDateTime(value);

    return format(zdt.toLocalDateTime(), zdt.getOffset(), zdt.getNano());
  }

  private static <T> ZonedDateTime createZonedDateTime(final T value) throws EdmPrimitiveTypeException {
    if (value instanceof ZonedDateTime) {
      return (ZonedDateTime) value;
    }

    if (value instanceof Instant) {
      return ((Instant) value).atZone(ZULU);
    }

    if (value instanceof GregorianCalendar) {
      GregorianCalendar calendar = (GregorianCalendar) value;
      ZonedDateTime zdt = calendar.toZonedDateTime();
      ZoneId normalizedZoneId = calendar.getTimeZone().toZoneId().normalized();
      return zdt.withZoneSameInstant(normalizedZoneId);
    }

    return convertToInstant(value).atZone(ZULU);
  }

  private static String format(LocalDateTime dateTime, ZoneOffset offset, int nanos) {
    String str = dateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    if (nanos > 0) {
      str = removeTrailingZeros(str);
    }
    return str + offset.toString();
  }

  private static String removeTrailingZeros(String str) {
    char[] chars = str.toCharArray();
    int trailingZeros = 0;
    for (int i = chars.length - 1; i >= 0 && chars[i] == '0'; i--) {
      trailingZeros++;
    }
    return str.substring(0, chars.length - trailingZeros);
  }

  /**
   * Creates an {@link Instant} from the given value.
   *
   * @param value the value as {@link Instant}, {@link java.util.Date},
   *   {@link java.sql.Timestamp}, {@link Long} or
   *   {@link GregorianCalendar}
   * @return the value as {@link Instant}
   * @throws EdmPrimitiveTypeException if the type of the value is not supported
   */
  private static <T> Instant convertToInstant(final T value) throws EdmPrimitiveTypeException {
    if (value instanceof java.sql.Time || value instanceof java.sql.Date) {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    } else if (value instanceof java.util.Date) {
      return ((java.util.Date) value).toInstant();
    } else if (value instanceof Timestamp) {
      return ((Timestamp) value).toInstant();
    } else if (value instanceof Long) {
      return Instant.ofEpochMilli((Long) value);
    } else {
      throw new EdmPrimitiveTypeException("The value type " + value.getClass() + " is not supported.");
    }
  }
}
