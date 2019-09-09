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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmDecimalTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal);

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)));
  }

  @Test
  public void uriLiteral() throws Exception {
    assertEquals("12.34", instance.toUriLiteral("12.34"));
    assertEquals("12.34", instance.fromUriLiteral("12.34"));
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("0", instance.valueToString(0, null, null, null, null, null));
    assertEquals("8", instance.valueToString((byte) 8, null, null, null, null, null));
    assertEquals("16", instance.valueToString((short) 16, null, null, null, null, null));
    assertEquals("32", instance.valueToString(Integer.valueOf(32), null, null, null, null, null));
    assertEquals("-32768", instance.valueToString(-32768, null, null, null, null, null));
    assertEquals("255", instance.valueToString(255L, null, null, null, null, null));
    assertEquals("1234567890123456789012345678901", instance.valueToString(new BigInteger(
        "1234567890123456789012345678901"), null, null, null, null, null));
    assertEquals("0.00390625", instance.valueToString(1.0 / 256, null, null, null, 8, null));
    assertEquals("-0.125", instance.valueToString(-0.125f, null, null, null, 3, null));
    assertEquals("-1234567890.1234567890", instance.valueToString(new BigDecimal(
        "-1234567890.1234567890"), null, null, null, 10, null));

    assertEquals("-32768", instance.valueToString(-32768, null, null, 42, null, null));
    assertEquals("-32768", instance.valueToString(-32768, null, null, 5, null, null));
    assertEquals("32768", instance.valueToString(32768, null, null, 5, null, null));
    assertEquals("0.5", instance.valueToString(0.5, null, null, 1, 1, null));
    assertEquals("0.5", instance.valueToString(0.5, null, null, null, 1, null));
    assertEquals("100", instance.valueToString(new BigDecimal(BigInteger.ONE, -2), null, null, 3, null, null));

    expectFacetsErrorInValueToString(instance, 0.5, null, null, null, null, null);
    expectFacetsErrorInValueToString(instance, -1234, null, null, 2, null, null);
    expectFacetsErrorInValueToString(instance, 1234, null, null, 3, null, null);
    expectFacetsErrorInValueToString(instance, 0.00390625, null, null, 5, null, null);
    expectFacetsErrorInValueToString(instance, 0.00390625, null, null, null, 7, null);

    expectContentErrorInValueToString(instance, Double.NaN);

    expectTypeErrorInValueToString(instance, 'A');
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(BigDecimal.ONE, instance.valueOfString("1", null, null, null, null, null, BigDecimal.class));
    assertEquals(Byte.valueOf((byte) -2), instance.valueOfString("-2", null, null, null, null, null, Byte.class));
    assertEquals(new BigDecimal("-123456789012345678901234567890"), instance.valueOfString(
        "-123456789012345678901234567890", null, null, null, null, null, BigDecimal.class));
    assertEquals(Short.valueOf((short) 0), instance.valueOfString("0", null, null, null, null, null, Short.class));

    assertEquals(Integer.valueOf(-32768), instance.valueOfString("-32768", null, null, 42, null, null, Integer.class));
    assertEquals(Long.valueOf(-32768), instance.valueOfString("-32768", null, null, 5, null, null, Long.class));
    assertEquals(BigInteger.valueOf(32768), instance.valueOfString("32768", null, null, 5, null, null,
        BigInteger.class));
    assertEquals(Double.valueOf(0.5), instance.valueOfString("0.5", null, null, 1, 1, null, Double.class));
    assertEquals(Float.valueOf(0.5F), instance.valueOfString("0.5", null, null, null, 1, null, Float.class));
    assertEquals(new BigDecimal("12.3"), instance.valueOfString("12.3", null, null, 3, 1, null, BigDecimal.class));
    assertEquals(new BigDecimal("31991163.34"),
        instance.valueOfString("3.199116334E7", null, null, 10, 2, null, BigDecimal.class));
    
    expectFacetsErrorInValueOfString(instance, "3.1991163E7", null, 8, 7, null, null);
    expectFacetsErrorInValueOfString(instance, "0.5", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "-1234", null, null, 2, null, null);
    expectFacetsErrorInValueOfString(instance, "1234", null, null, 3, null, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 3, null, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 3, 2, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 4, 1, null);
    expectFacetsErrorInValueOfString(instance, "0.00390625", null, null, 5, null, null);
    expectFacetsErrorInValueOfString(instance, "0.00390625", null, null, null, 7, null);
    expectFacetsErrorInValueOfString(instance, "-129", null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
    expectFacetsErrorInValueOfString(instance, "-32769", null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, null);
    expectFacetsErrorInValueOfString(instance, "32768", null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, null);

    expectContentErrorInValueOfString(instance, "1.");
    expectContentErrorInValueOfString(instance, ".1");
    expectContentErrorInValueOfString(instance, "1.0.1");
    expectContentErrorInValueOfString(instance, "1M");
    expectContentErrorInValueOfString(instance, "0x42");

    expectUnconvertibleErrorInValueOfString(instance, "-2147483649", Integer.class);
    expectUnconvertibleErrorInValueOfString(instance, "2147483648", Integer.class);
    expectUnconvertibleErrorInValueOfString(instance, "-9223372036854775809", Long.class);
    expectUnconvertibleErrorInValueOfString(instance, "9223372036854775808", Long.class);
    expectUnconvertibleErrorInValueOfString(instance, "12345678901234567", Double.class);
    expectUnconvertibleErrorInValueOfString(instance, "1234567890", Float.class);

    expectTypeErrorInValueOfString(instance, "1");
  }
  
  @Test
  public void validateDecimal() throws Exception {
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("3.1991163E7", null, null, 8, 7, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1", null, null, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.2", null, null, null, 0, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
    validate("10.2", false, null, 3, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.2", false, null, 3, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.12", false, null, 3, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.23", false, null, 3, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.03", false, null, 3, 2, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.03", false, null, 3, null, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("12", false, null, 3, null, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("12", false, null, null, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.12", false, null, null, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("12", false, null, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.12", false, null, null, null, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.23", false, null, 2, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.7", false, null, 2, 2, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("12e-101", false, null, 2, 2, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("12e101", false, null, 2, 2, null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("-1.234567e3", false, null, 7, 3, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("0.7", false, null, 2, 3, null));
  }
  
  @Test
  public void validateDecimalInV401() throws Exception {
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("0.123", null, null, 3, "variable", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("1.23", null, null, 3, "variable", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("0.23", null, null, 3, "variable", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("0.7", null, null, 3, "variable", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("123", null, null, 3, "variable", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("12.3", null, null, 3, "variable", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("12.34", null, null, 3, "variable", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("1234", null, null, 3, "variable", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("123.4", null, null, 3, "variable", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("123.4", null, null, null, "variable", null));
    
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("-1.234567e3", null, null, 7, "floating", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("1e-101", null, null, 7, "floating", null));
    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("9.999999e96", null, null, 7, "floating", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("1e-102", null, null, 7, "floating", null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validateDecimals("1e97", null, null, 7, "floating", null));
    
  }
}
