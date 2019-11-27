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

import static org.junit.Assert.assertEquals;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmTimeOfDayTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.TimeOfDay);

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("11:12", instance.toUriLiteral("11:12"));
    assertEquals("11:12:13.012", instance.toUriLiteral("11:12:13.012"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("11:12", instance.fromUriLiteral("11:12"));
    assertEquals("11:12:13.012", instance.fromUriLiteral("11:12:13.012"));
  }

  @Test
  public void valueToString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    setTimeZone(dateTime, "GMT+11:30");
    dateTime.set(1, 2, 3, 4, 5, 6);
    assertEquals("04:05:06", instance.valueToString(dateTime, null, null, null, null, null));

    dateTime.add(Calendar.MILLISECOND, 42);
    assertEquals("04:05:06.042", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("04:05:06.042", instance.valueToString(dateTime, null, null, 3, null, null));
    assertEquals("04:05:06.042", instance.valueToString(dateTime, null, null, 4, null, null));

    Calendar dateTime2 = Calendar.getInstance();
    dateTime2.clear();
    setTimeZone(dateTime, TimeZone.getDefault());
    dateTime2.set(Calendar.HOUR, 5);
    dateTime2.set(Calendar.MINUTE, 59);
    dateTime2.set(Calendar.SECOND, 23);

    final java.sql.Time time = new java.sql.Time(dateTime2.getTimeInMillis());
    assertEquals("05:59:23", instance.valueToString(time, null, null, null, null, null));

    assertEquals("05:59:23", instance.valueToString(dateTime2.getTimeInMillis(), null, null, null, null, null));

//    Timestamp timestamp = new Timestamp(0);
//    timestamp.setNanos(42);

    expectTypeErrorInValueToString(instance, 0);
  }

  @Test
  public void valueToStringFromJavaUtilDate() throws Exception {
    LocalTime time = LocalTime.parse("04:05:06");
    ZonedDateTime zdt = ZonedDateTime.of(LocalDate.ofEpochDay(0), time, ZoneId.systemDefault());
    long millis = zdt.toInstant().toEpochMilli();

    java.util.Date javaUtilDate = new java.util.Date(millis);
    assertEquals("04:05:06", instance.valueToString(javaUtilDate, null, null, null, null, null));

    java.sql.Timestamp javaSqlTimestamp = new java.sql.Timestamp(millis);
    assertEquals("04:05:06", instance.valueToString(javaSqlTimestamp, null, null, null, null, null));
  }

  @Test
  public void valueToStringFromLocalTime() throws Exception {
    LocalTime time = LocalTime.parse("04:05:06");
    assertEquals("04:05:06", instance.valueToString(time, null, null, null, null, null));
  }

  @Test
  public void valueToStringFromJavaSqlTime() throws Exception {
    java.sql.Time time = java.sql.Time.valueOf("04:05:06");
    assertEquals("04:05:06", instance.valueToString(time, null, null, null, null, null));
  }

  @Test
  public void valueOfString() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();

    assertEqualCalendar(dateTime, instance.valueOfString("00:00", null, null, null, null, null, Calendar.class));
    assertEqualCalendar(dateTime, instance.valueOfString("00:00:00", null, null, null, null, null, Calendar.class));
    assertEqualCalendar(dateTime,
        instance.valueOfString("00:00:00.000000000", null, null, null, null, null, Calendar.class));

    final Time timeValue = instance.valueOfString("00:00:00.999", null, null, 3, null, null, Time.class);
    assertEquals(dateTime.getTimeInMillis(), timeValue.getTime());

    dateTime.set(Calendar.MILLISECOND, 999);
    assertEqualCalendar(dateTime,
        instance.valueOfString("00:00:00.999", null, null, 3, null, null, Calendar.class));
    assertEqualCalendar(dateTime,
        instance.valueOfString("00:00:00.999", null, null, 3, null, null, Calendar.class));
    assertEquals(Long.valueOf(dateTime.getTimeInMillis()),
        instance.valueOfString("00:00:00.999", null, null, 3, null, null, Long.class));

    final Timestamp timestamp = instance.valueOfString("00:00:00.999888777", null, null, 9, null, null,
        Timestamp.class);
    assertEquals(dateTime.getTimeInMillis(), timestamp.getTime());
    assertEquals(999888777, timestamp.getNanos());

//    expectUnconvertibleErrorInValueOfString(instance, "11:12:13.1234", Calendar.class);
//    expectUnconvertibleErrorInValueOfString(instance, "11:12:13.0123456789", Timestamp.class);

    expectContentErrorInValueOfString(instance, "24:32:02");
    expectContentErrorInValueOfString(instance, "011:12:13");
    expectContentErrorInValueOfString(instance, "11:12:13:14");
    expectContentErrorInValueOfString(instance, "111213");
    expectContentErrorInValueOfString(instance, "1:2:3");
    expectContentErrorInValueOfString(instance, "11:12:13.0.1");
//    expectContentErrorInValueOfString(instance, "11:12:13.");
    expectContentErrorInValueOfString(instance, "11:12:13.0000000000000");

    expectTypeErrorInValueOfString(instance, "11:12:13");
  }

  @Test
  public void valueOfStringToLocalTime() throws Exception {
    LocalTime time = LocalTime.parse("04:05:06");
    assertEquals(time, instance.valueOfString("04:05:06", null, null, null, null, null, LocalTime.class));

    time = time.plus(123, ChronoUnit.MILLIS);
    assertEquals(time, instance.valueOfString("04:05:06.123", null, null, null, null, null, LocalTime.class));

    time = time.plus(456789, ChronoUnit.NANOS);
    assertEquals(time, instance.valueOfString("04:05:06.123456789", null, null, null, null, null, LocalTime.class));
  }

  @Test
  public void valueOfStringToJavaSqlTime() throws Exception {
    java.sql.Time time = java.sql.Time.valueOf("04:05:06");
    assertEquals(time, instance.valueOfString("04:05:06", null, null, null, null, null, java.sql.Time.class));
  }

  @Test
  public void valueOfStringToJavaUtilDateTime() throws Exception {
    LocalTime time = LocalTime.parse("04:05:06");
    ZonedDateTime zdt = ZonedDateTime.of(LocalDate.ofEpochDay(0), time, ZoneId.systemDefault());
    long millis = zdt.toInstant().toEpochMilli();
    java.util.Date javaUtilDate = new java.util.Date(millis);
    assertEquals(javaUtilDate, instance.valueOfString("04:05:06", null, null, null, null, null, java.util.Date.class));
  }

  @Test
  public void testRoundTripTime() throws Exception {
    java.sql.Time time = instance.valueOfString("04:05:06.002", true, 4000, 3, 0, true, java.sql.Time.class);
    String val = instance.valueToString(time, true, 4000, 3, 0, true);
    assertEquals("04:05:06", val);
  }

  @Test
  public void tests() throws Exception {
    instance.validate("12:34:55", null, null, null, null, null);
  }

}
