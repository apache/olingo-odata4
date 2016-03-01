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

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmDurationTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Duration);

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("duration'P120D'", instance.toUriLiteral("P120D"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("P120D", instance.fromUriLiteral("duration'P120D'"));

    expectErrorInFromUriLiteral(instance, "");
    expectErrorInFromUriLiteral(instance, "Duration'PT1S'");
    expectErrorInFromUriLiteral(instance, "duration'PT1S\"");
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("PT10S", instance.valueToString(BigDecimal.TEN, null, null, null, null, null));
    assertEquals("-PT10S", instance.valueToString(BigDecimal.TEN.negate(), null, null, null, null, null));
    assertEquals("PT10S", instance.valueToString(BigDecimal.TEN, null, null, null, null, null));
    assertEquals("PT10S", instance.valueToString(BigDecimal.TEN, null, null, 0, null, null));
    assertEquals("-PT0.01S", instance.valueToString(BigDecimal.ONE.movePointLeft(2).negate(), null, null, 2, null,
        null));
    assertEquals("PT2M3S", instance.valueToString(123, null, null, null, null, null));
    assertEquals("PT2M3S", instance.valueToString((byte) 123, null, null, null, null, null));
    assertEquals("PT3H25M45S", instance.valueToString((short) 12345, null, null, null, null, null));
    assertEquals("P14288DT23H31M30S", instance.valueToString(1234567890L, null, null, null, null, null));
    assertEquals("P50903316DT2H25M4S", instance.valueToString(BigInteger.ONE.shiftLeft(42), null, null, null, null,
        null));

    expectFacetsErrorInValueToString(instance, BigDecimal.ONE.movePointLeft(1), null, null, null, null, null);
    expectFacetsErrorInValueToString(instance, BigDecimal.ONE.movePointLeft(1), null, null, 0, null, null);

    expectTypeErrorInValueToString(instance, "");
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(BigDecimal.TEN, instance.valueOfString("PT10S", null, null, null, null, null, BigDecimal.class));
    assertEquals(BigDecimal.TEN.negate(), instance.valueOfString("-PT10S", null, null, null, null, null,
        BigDecimal.class));
    assertEquals(BigDecimal.TEN, instance.valueOfString("PT10S", null, null, null, null, null, BigDecimal.class));
    assertEquals(BigDecimal.ONE.movePointLeft(1), instance.valueOfString("PT0.1S", null, null, 1, null, null,
        BigDecimal.class));
    assertEquals(Byte.valueOf((byte) 123), instance.valueOfString("PT2M3S", null, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) 123), instance.valueOfString("PT2M3S", null, null, null, null, null,
        Short.class));
    assertEquals(Integer.valueOf(12345), instance.valueOfString("PT3H25M45S", null, null, null, null, null,
        Integer.class));
    assertEquals(Long.valueOf(1234567890L), instance.valueOfString("P14288DT23H31M30S", null, null, null, null, null,
        Long.class));
    assertEquals(BigInteger.ONE.shiftLeft(42), instance.valueOfString("P50903316DT2H25M4S", null, null, null, null,
        null, BigInteger.class));

    expectFacetsErrorInValueOfString(instance, "PT1.1S", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "PT1H2M3.123S", null, null, 2, null, null);
    expectFacetsErrorInValueOfString(instance, "PT13H2M3.9S", null, null, 0, null, null);

    expectContentErrorInValueOfString(instance, "PT1H2M3S.1234");
    expectContentErrorInValueOfString(instance, "P2012Y2M29DT23H32M2S");
    expectContentErrorInValueOfString(instance, "PT-1H");
    expectContentErrorInValueOfString(instance, "PT");

    expectUnconvertibleErrorInValueOfString(instance, "-PT2M9S", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "PT2M8S", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "PT10H", Short.class);
    expectUnconvertibleErrorInValueOfString(instance, "P25000D", Integer.class);
    expectUnconvertibleErrorInValueOfString(instance, "P123456789012345D", Long.class);
    expectUnconvertibleErrorInValueOfString(instance, "PT1.1S", BigInteger.class);

    expectTypeErrorInValueOfString(instance, "PT0S");
  }
}
