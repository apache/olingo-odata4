package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmInt32Test extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance();

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(Uint7.getInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance()));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeKind.Int16.getEdmPrimitiveTypeInstance()));
  }

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("127", instance.toUriLiteral("127"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("127", instance.fromUriLiteral("127"));
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("0", instance.valueToString(0, null, null, null, null, null));
    assertEquals("8", instance.valueToString((byte) 8, null, null, null, null, null));
    assertEquals("16", instance.valueToString((short) 16, null, null, null, null, null));
    assertEquals("32", instance.valueToString(Integer.valueOf(32), null, null, null, null, null));
    assertEquals("255", instance.valueToString(255L, null, null, null, null, null));
    assertEquals("-2147483648", instance.valueToString(BigInteger.valueOf(Integer.MIN_VALUE), null, null, null, null,
        null));

    expectContentErrorInValueToString(instance, 12345678901L);
    expectContentErrorInValueToString(instance, -2147483649L);
    expectContentErrorInValueToString(instance, BigInteger.valueOf(2147483648L));

    expectTypeErrorInValueToString(instance, 1.0);
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(Byte.valueOf((byte) 1), instance.valueOfString("1", null, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) 2), instance.valueOfString("2", null, null, null, null, null, Short.class));
    assertEquals(Integer.valueOf(-10000000), instance.valueOfString("-10000000", null, null, null, null, null,
        Integer.class));
    assertEquals(Long.valueOf(10000000), instance.valueOfString("10000000", null, null, null, null, null, Long.class));
    assertEquals(BigInteger.TEN, instance.valueOfString("10", null, null, null, null, null, BigInteger.class));

    expectContentErrorInValueOfString(instance, "-2147483649");
    expectContentErrorInValueOfString(instance, "1.0");

    expectUnconvertibleErrorInValueOfString(instance, "-129", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "128", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "-32769", Short.class);
    expectUnconvertibleErrorInValueOfString(instance, "32768", Short.class);

    expectTypeErrorInValueOfString(instance, "1");
  }
}
