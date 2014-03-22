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
package org.apache.olingo.commons.core.edm.primitivetype;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;

/**
 * Implementation of the EDM primitive type DateTime.
 */
public final class EdmDateTime extends SingletonPrimitiveType {

  public static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
    @Override
    protected SimpleDateFormat initialValue() {
      return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    }
  };

  private static final EdmDateTime INSTANCE = new EdmDateTime();

  public static EdmDateTime getInstance() {
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

    final String[] dateParts = value.split("\\.");

    final Date date;
    try {
      date = DATE_FORMAT.get().parse(dateParts[0]);
    } catch (Exception e) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
    }

    TimeZone timezone = null;
    Integer fractionalSecs = null;
    if (dateParts.length > 1) {
      int idx = dateParts[1].indexOf('+');
      if (idx == -1) {
        idx = dateParts[1].indexOf('-');
      }
      if (idx == -1) {
        fractionalSecs = Integer.parseInt(dateParts[1]);
      } else {
        timezone = TimeZone.getTimeZone(dateParts[1].substring(idx));
        fractionalSecs = Integer.parseInt(dateParts[1].substring(0, idx));
      }
    }

    if (fractionalSecs != null && String.valueOf(fractionalSecs).length() > (precision == null ? 0 : precision)) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.LITERAL_FACETS_NOT_MATCHED.addContent(value, facets)");
    }

    final Calendar calendar = timezone == null ? Calendar.getInstance() : Calendar.getInstance(timezone);
    calendar.setTime(date);
    final Timestamp timestamp = new Timestamp(date.getTime());
    if (fractionalSecs != null) {
      calendar.set(Calendar.MILLISECOND, fractionalSecs);
      timestamp.setNanos(fractionalSecs);
    }

    if (returnType.isAssignableFrom(Calendar.class)) {
      return returnType.cast(calendar);
    } else if (returnType.isAssignableFrom(Timestamp.class)) {
      return returnType.cast(timestamp);
    } else {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.LITERAL_UNCONVERTIBLE_TO_VALUE_TYPE.addContent(value, returnType)");
    }
  }

  @Override
  protected <T> String internalValueToString(final T value,
          final Boolean isNullable, final Integer maxLength, final Integer precision,
          final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    Date date = null;
    Integer fractionalSecs = null;
    if (value instanceof Calendar) {
      final Calendar calendar = (Calendar) value;
      date = calendar.getTime();
      fractionalSecs = calendar.get(Calendar.MILLISECOND);
    }
    if (value instanceof Timestamp) {
      final Timestamp timestamp = (Timestamp) value;
      date = new Date(timestamp.getTime());
      fractionalSecs = timestamp.getNanos();
    }

    final StringBuilder result = new StringBuilder().append(DATE_FORMAT.get().format(date));

    try {
      if (value instanceof Timestamp) {
        EdmDateTimeOffset.appendFractionalSeconds(result, fractionalSecs, precision);
      } else {
        EdmDateTimeOffset.appendMilliseconds(result, fractionalSecs, precision);
      }
    } catch (final IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_FACETS_NOT_MATCHED.addContent(value, facets)", e);
    }

    return result.toString();
  }
}
