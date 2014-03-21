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

    Calendar calendar = null;
    Timestamp timestamp = null;

    final String[] dateParts = value.split("\\.");
    try {
      final Date date = DATE_FORMAT.get().parse(dateParts[0]);
      if (dateParts.length > 1) {
        int idx = dateParts[1].indexOf('+');
        if (idx == -1) {
          idx = dateParts[1].indexOf('-');
        }
        if (idx == -1) {
          calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
          calendar.setTime(date);

          timestamp = new Timestamp(calendar.getTimeInMillis());
          timestamp.setNanos(Integer.parseInt(dateParts[1]));
        } else {
          calendar = Calendar.getInstance(TimeZone.getTimeZone(dateParts[1].substring(idx)));
          calendar.setTime(date);

          timestamp = new Timestamp(calendar.getTimeInMillis());
          timestamp.setNanos(Integer.parseInt(dateParts[1].substring(0, idx)));
        }
      } else {
        timestamp = new Timestamp(date.getTime());
      }
    } catch (Exception e) {
      throw new EdmPrimitiveTypeException("EdmPrimitiveTypeException.LITERAL_ILLEGAL_CONTENT.addContent(value)", e);
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

    if (value instanceof Calendar) {
      final Calendar calendar = (Calendar) value;

      final StringBuilder formatted = new StringBuilder().append(DATE_FORMAT.get().format(calendar.getTime()));
      formatted.append(calendar.getTimeZone());

      return formatted.toString();
    } else if (value instanceof Timestamp) {
      final Timestamp timestamp = (Timestamp) value;

      final StringBuilder formatted = new StringBuilder().append(DATE_FORMAT.get().format(timestamp));
      if (timestamp.getNanos() > 0) {
        formatted.append('.').append(String.valueOf(timestamp.getNanos()));
      }

      return formatted.toString();
    } else {
      throw new EdmPrimitiveTypeException(
              "EdmPrimitiveTypeException.VALUE_TYPE_NOT_SUPPORTED.addContent(value.getClass())");
    }
  }
}
