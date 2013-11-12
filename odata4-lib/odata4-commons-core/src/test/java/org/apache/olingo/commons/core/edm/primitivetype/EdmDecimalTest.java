package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmDecimalTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance();

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(Uint7.getInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Int16.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Int64.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Single.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Double.getEdmPrimitiveTypeInstance()));
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

    expectFacetsErrorInValueOfString(instance, "0.5", null, null, null, null, null);
    expectFacetsErrorInValueOfString(instance, "-1234", null, null, 2, null, null);
    expectFacetsErrorInValueOfString(instance, "1234", null, null, 3, null, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 3, null, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 3, 2, null);
    expectFacetsErrorInValueOfString(instance, "12.34", null, null, 4, 1, null);
    expectFacetsErrorInValueOfString(instance, "0.00390625", null, null, 5, null, null);
    expectFacetsErrorInValueOfString(instance, "0.00390625", null, null, null, 7, null);

    expectContentErrorInValueOfString(instance, "-1E2");
    expectContentErrorInValueOfString(instance, "1.");
    expectContentErrorInValueOfString(instance, ".1");
    expectContentErrorInValueOfString(instance, "1.0.1");
    expectContentErrorInValueOfString(instance, "1M");
    expectContentErrorInValueOfString(instance, "0x42");

    expectUnconvertibleErrorInValueOfString(instance, "-129", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "128", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "-32769", Short.class);
    expectUnconvertibleErrorInValueOfString(instance, "32768", Short.class);
    expectUnconvertibleErrorInValueOfString(instance, "-2147483649", Integer.class);
    expectUnconvertibleErrorInValueOfString(instance, "2147483648", Integer.class);
    expectUnconvertibleErrorInValueOfString(instance, "-9223372036854775809", Long.class);
    expectUnconvertibleErrorInValueOfString(instance, "9223372036854775808", Long.class);
    expectUnconvertibleErrorInValueOfString(instance, "12345678901234567", Double.class);
    expectUnconvertibleErrorInValueOfString(instance, "1234567890", Float.class);

    expectTypeErrorInValueOfString(instance, "1");
  }
}
