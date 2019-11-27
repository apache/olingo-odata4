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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmDateTimeOffsetTest extends PrimitiveTypeBaseTest {

  final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset);

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("2009-12-26T21:23:38Z", instance.toUriLiteral("2009-12-26T21:23:38Z"));
    assertEquals("2002-10-10T12:00:00-05:00", instance.toUriLiteral("2002-10-10T12:00:00-05:00"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("2009-12-26T21:23:38Z", instance.fromUriLiteral("2009-12-26T21:23:38Z"));
    assertEquals("2002-10-10T12:00:00-05:00", instance.fromUriLiteral("2002-10-10T12:00:00-05:00"));
  }

  @Test
  public void valueToStringFromInstant() throws Exception {
    Instant instant = Instant.parse("2012-02-29T01:02:03Z");
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(instant, null, null, null, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(instant, null, null, 0, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(instant, null, null, 5, null, null));
  }

  @Test
  public void valueToStringFromZonedDateTime() throws Exception {
    ZonedDateTime zdt = ZonedDateTime.parse("2012-02-28T23:32:03-01:30");

    assertEquals("2012-02-28T23:32:03-01:30", instance.valueToString(zdt, null, null, null, null, null));

    zdt = zdt.withZoneSameInstant(ZoneId.of("GMT+11:00"));
    assertEquals("2012-02-29T12:02:03+11:00", instance.valueToString(zdt, null, null, null, null, null));

    zdt = zdt.plus(123, ChronoUnit.MILLIS);

    assertEquals("2012-02-29T12:02:03.123+11:00", instance.valueToString(zdt, null, null, null, null, null));
    assertEquals("2012-02-29T12:02:03.123+11:00", instance.valueToString(zdt, null, null, 3, null, null));

    zdt = zdt.plus(456789, ChronoUnit.NANOS);
    assertEquals("2012-02-29T12:02:03.123456789+11:00", instance.valueToString(zdt, null, null, 9, null, null));
  }

  @Test
  public void valueToStringFromCalendar() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    setTimeZone(dateTime, "GMT");
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, 0, null, null));
    assertEquals("2012-02-29T01:02:03Z", instance.valueToString(dateTime, null, null, 5, null, null));

    setTimeZone(dateTime, "GMT-1:30");
    assertEquals("2012-02-28T23:32:03-01:30", instance.valueToString(dateTime, null, null, null, null, null));

    setTimeZone(dateTime, "GMT+11:00");
    assertEquals("2012-02-29T12:02:03+11:00", instance.valueToString(dateTime, null, null, null, null, null));

    dateTime.set(Calendar.MILLISECOND, 503);
    assertEquals("2012-02-29T12:02:03.503+11:00", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("2012-02-29T12:02:03.503+11:00", instance.valueToString(dateTime, null, null, 3, null, null));

    dateTime.set(Calendar.MILLISECOND, 530);
    assertEquals("2012-02-29T12:02:03.53+11:00", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("2012-02-29T12:02:03.53+11:00", instance.valueToString(dateTime, null, null, 3, null, null));

    dateTime.set(Calendar.MILLISECOND, 53);
    assertEquals("2012-02-29T12:02:03.053+11:00", instance.valueToString(dateTime, null, null, null, null, null));
    assertEquals("2012-02-29T12:02:03.053+11:00", instance.valueToString(dateTime, null, null, 3, null, null));
  }

  @Test
  public void valueToStringFromLong() throws Exception {
    Long millis = 1330558323000L;
    assertEquals("2012-02-29T23:32:03Z", instance.valueToString(millis, null, null, null, null, null));
    millis = 1330558323007L;
    assertEquals("2012-02-29T23:32:03.007Z", instance.valueToString(millis, null, null, null, null, null));
    assertEquals("2012-02-29T23:32:03.007Z", instance.valueToString(millis, null, null, 3, null, null));
    assertEquals("1969-12-31T23:59:59.9Z", instance.valueToString(-100L, null, null, 1, null, null));
    assertEquals("1969-12-31T23:59:59.98Z", instance.valueToString(-20L, null, null, 2, null, null));
  }

  @Test
  public void valueToStringFromJavaUtilDate() throws Exception {
    final Long millis = 1330558323007L;
    final Date date = new Date(millis);
    assertEquals("2012-02-29T23:32:03.007Z", instance.valueToString(date, null, null, null, null, null));
    assertEquals("2012-02-29T23:32:03.007Z", instance.valueToString(date, null, null, 3, null, null));
  }

  @Test
  public void valueToStringFromTimestamp() throws Exception {
    Timestamp timestamp = new Timestamp(0);
    timestamp.setNanos(120);
    assertEquals("1970-01-01T00:00:00.00000012Z", instance.valueToString(timestamp, null, null, null, null, null));
    assertEquals("1970-01-01T00:00:00.00000012Z", instance.valueToString(timestamp, null, null, 8, null, null));

  }

  @Test
  public void valueToStringFromInvalidTypes() throws Exception {
    expectTypeErrorInValueToString(instance, Integer.valueOf(0));
    expectTypeErrorInValueToString(instance, Time.valueOf("12:13:14"));
    expectTypeErrorInValueToString(instance, java.sql.Date.valueOf("2019-10-25"));
  }

  @Test
  public void valueOfStringToInstant() throws Exception {
    Instant instant = Instant.parse("2012-02-29T01:02:03Z");
    assertEquals(instant,
        instance.valueOfString("2012-02-29T01:02:03Z", null, null, null, null, null, Instant.class));
    assertEquals(instant,
        instance.valueOfString("2012-02-29T01:02:03", null, null, null, null, null, Instant.class));
  }

  @Test
  public void valueOfStringToZonedDateTime() throws Exception {
    ZonedDateTime zdt = ZonedDateTime.parse("2012-02-29T01:02:03-01:30");
    assertEquals(zdt,
        instance.valueOfString("2012-02-29T01:02:03-01:30", null, null, null, null, null, ZonedDateTime.class));
  }

  @Test
  public void valueOfStringToCalendar() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    setTimeZone(dateTime, "GMT");
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEqualCalendar(dateTime,
        instance.valueOfString("2012-02-29T01:02:03Z", null, null, null, null, null, Calendar.class));
    assertEqualCalendar(dateTime,
        instance.valueOfString("2012-02-29T01:02:03", null, null, null, null, null, Calendar.class));

    dateTime.clear();
    setTimeZone(dateTime, "GMT-01:30");
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals(dateTime.getTime(),
        instance.valueOfString("2012-02-29T01:02:03-01:30", null, null, null, null, null, Date.class));

    dateTime.clear();
    setTimeZone(dateTime, "GMT+11:00");
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEqualCalendar(dateTime,
        instance.valueOfString("2012-02-29T01:02:03+11:00", null, null, null, null, null, Calendar.class));
    dateTime.add(Calendar.MILLISECOND, 7);
    assertEqualCalendar(dateTime,
        instance.valueOfString("2012-02-29T01:02:03.007+11:00", null, null, 3, null, null, Calendar.class));
  }

  @Test
  public void valueOfStringToTimestamp() throws Exception {
    assertEquals(530000001, instance
        .valueOfString("2012-02-29T01:02:03.530000001+11:00", null, null, 9, null, null, Timestamp.class)
        .getNanos());
  }

  @Test
  public void valueOfStringToLong() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    setTimeZone(dateTime, "GMT");
    dateTime.set(2012, 1, 29, 1, 2, 3);
    assertEquals(Long.valueOf(dateTime.getTimeInMillis()),
        instance.valueOfString("2012-02-29T01:02:03+00:00", null, null, null, null, null, Long.class));
    assertEquals(Long.valueOf(120000L),
        instance.valueOfString("1970-01-01T00:02Z", null, null, null, null, null, Long.class));
    assertEquals(Long.valueOf(120000L),
        instance.valueOfString("1970-01-01T00:02", null, null, null, null, null, Long.class));
    assertEquals(Long.valueOf(12L),
        instance.valueOfString("1970-01-01T00:00:00.012", null, null, 3, null, null, Long.class));
    assertEquals(Long.valueOf(120L),
        instance.valueOfString("1970-01-01T00:00:00.12", null, null, 2, null, null, Long.class));
  }

  @Test
  public void valueOfStringToJavaSqlTime() throws Exception {
    assertEquals(new Time(120000L),
        instance.valueOfString("1970-01-01T00:02", null, null, null, null, null, Time.class));
    // java.sql.Time does not keep track of milliseconds.
    assertEquals(new Time(0),
        instance.valueOfString("1970-01-01T00:00:00.012", null, null, 3, null, null, Time.class));
    assertEquals(new Time(0),
        instance.valueOfString("1970-01-01T00:00:00.12", null, null, 2, null, null, Time.class));
  }

  @Test
  public void valueOfStringToJavaSqlDate() throws Exception {
    assertEquals(new java.sql.Date(120000L),
        instance.valueOfString("1970-01-01T00:02", null, null, null, null, null, java.sql.Date.class));
    // java.sql.Time does not keep track of milliseconds.
    assertEquals(new java.sql.Date(0),
        instance.valueOfString("1970-01-01T00:00:00.012", null, null, 3, null, null, java.sql.Date.class));
    assertEquals(new java.sql.Date(0),
        instance.valueOfString("1970-01-01T00:00:00.12", null, null, 2, null, null, java.sql.Date.class));
  }

  @Test
  public void valueOfStringInvalidData() throws Exception {
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02X");
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02+24:00");
    expectContentErrorInValueOfString(instance, "2012-02-30T01:02:03");
    expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02.0000000000000");

    expectTypeErrorInValueOfString(instance, "2012-02-29T01:02:03Z");
  }

}
