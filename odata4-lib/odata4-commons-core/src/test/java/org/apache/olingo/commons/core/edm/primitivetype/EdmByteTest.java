package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmByteTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance();

  @Test
  public void compatibility() {
    assertTrue(instance.isCompatible(Uint7.getInstance()));
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
    assertEquals("255", instance.valueToString(BigInteger.valueOf(255), null, null, null, null, null));

    expectContentErrorInValueToString(instance, -1);
    expectContentErrorInValueToString(instance, 256);
    expectContentErrorInValueToString(instance, BigInteger.valueOf(-1));
    expectContentErrorInValueToString(instance, BigInteger.valueOf(256));

    expectTypeErrorInValueToString(instance, 'A');
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(Short.valueOf((short) 1), instance.valueOfString("1", null, null, null, null, null, Short.class));
    assertEquals(Integer.valueOf(2), instance.valueOfString("2", null, null, null, null, null, Integer.class));
    assertEquals(Byte.valueOf((byte) 127), instance.valueOfString("127", null, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) 255), instance.valueOfString("255", null, null, null, null, null, Short.class));
    assertEquals(Long.valueOf(0), instance.valueOfString("0", null, null, null, null, null, Long.class));
    assertEquals(BigInteger.TEN, instance.valueOfString("10", null, null, null, null, null, BigInteger.class));

    expectContentErrorInValueOfString(instance, "0x42");
    expectContentErrorInValueOfString(instance, "abc");
    expectContentErrorInValueOfString(instance, "256");
    expectContentErrorInValueOfString(instance, "-1");
    expectContentErrorInValueOfString(instance, "1.0");

    expectUnconvertibleErrorInValueOfString(instance, "128", Byte.class);

    expectTypeErrorInValueOfString(instance, "1");
  }
}
