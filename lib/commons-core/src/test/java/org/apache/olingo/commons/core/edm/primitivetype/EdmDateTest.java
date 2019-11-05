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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmDateTest extends PrimitiveTypeBaseTest {

	private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Date);

	@Test
	public void toUriLiteral() throws Exception {
		assertEquals("2009-12-26", instance.toUriLiteral("2009-12-26"));
		assertEquals("-2009-12-26", instance.toUriLiteral("-2009-12-26"));
	}

	@Test
	public void fromUriLiteral() throws Exception {
		assertEquals("2009-12-26", instance.fromUriLiteral("2009-12-26"));
		assertEquals("-2009-12-26", instance.fromUriLiteral("-2009-12-26"));
	}

	@Test
	public void valueToString() throws Exception {
		Calendar dateTime = Calendar.getInstance();
		dateTime.clear();
		setTimeZone(dateTime, "GMT-11:30");
		dateTime.set(2012, 1, 29, 13, 0, 0);
		assertEquals("2012-02-29", instance.valueToString(dateTime, null, null, null, null, null));

		Long millis = 1330558323007L;
		millis -= TimeZone.getDefault().getOffset(millis);
		assertEquals("2012-02-29", instance.valueToString(millis, null, null, null, null, null));

		assertEquals("1969-12-31", instance.valueToString(new java.util.Date(-43200000), null, null, null, null, null));
		assertEquals("1969-12-31",
				instance.valueToString(java.sql.Date.valueOf("1969-12-31"), null, null, null, null, null));
		assertEquals("1969-12-31",
				instance.valueToString(LocalDate.parse("1969-12-31"), null, null, null, null, null));

		// TODO support for years beyond 9999
//    dateTime.set(Calendar.YEAR, 12344);
//    assertEquals("12344-02-29", instance.valueToString(dateTime, null, null, null, null, null));

		expectTypeErrorInValueToString(instance, 0);
	}

	@Test
	public void valueOfString() throws Exception {
		Calendar dateTime = Calendar.getInstance();
		dateTime.clear();
		dateTime.set(2012, 1, 29);
		assertEqualCalendar(dateTime,
				instance.valueOfString("2012-02-29", null, null, null, null, null, Calendar.class));
		assertEquals(Long.valueOf(dateTime.getTimeInMillis()),
				instance.valueOfString("2012-02-29", null, null, null, null, null, Long.class));
		assertEquals(dateTime.getTime(),
				instance.valueOfString("2012-02-29", null, null, null, null, null, java.util.Date.class));
		assertEquals(java.sql.Date.valueOf("2012-02-29"),
				instance.valueOfString("2012-02-29", null, null, null, null, null, java.sql.Date.class));
		assertEquals(LocalDate.parse("2012-02-29"),
				instance.valueOfString("2012-02-29", null, null, null, null, null, LocalDate.class));

		// TODO support for years beyond 9999
		// dateTime.set(Calendar.YEAR, 12344);
		// Calendar result = instance.valueOfString("12344-02-29", null, null, null,
		// null, null, Calendar.class);
		// this.assertEqualCalendar(dateTime, result);

		// TODO: Clarify whether negative years are really needed.
		// dateTime.set(-1, 1, 28);
		// assertEquals(dateTime, instance.valueOfString("-0001-02-28", null,
		// Calendar.class));

		expectContentErrorInValueOfString(instance, "2012-02-29T23:32:02");
		expectContentErrorInValueOfString(instance, "2012-02-30");
		expectContentErrorInValueOfString(instance, "20120229");
		expectContentErrorInValueOfString(instance, "2012-02-1");
		expectContentErrorInValueOfString(instance, "2012-2-12");
		expectContentErrorInValueOfString(instance, "123-02-03");

		expectTypeErrorInValueOfString(instance, "2012-02-29");
	}
}
