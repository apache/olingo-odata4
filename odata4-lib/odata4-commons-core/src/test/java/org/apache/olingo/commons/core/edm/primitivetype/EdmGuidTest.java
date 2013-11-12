package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmGuidTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeKind.Guid.getEdmPrimitiveTypeInstance();

  @Test
  public void toUriLiteral() {
    assertEquals("aabbccdd-aabb-ccdd-eeff-aabbccddeeff",
        instance.toUriLiteral("aabbccdd-aabb-ccdd-eeff-aabbccddeeff"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("aabbccdd-aabb-ccdd-eeff-aabbccddeeff",
        instance.fromUriLiteral("aabbccdd-aabb-ccdd-eeff-aabbccddeeff"));
  }

  @Test
  public void valueToString() throws Exception {
    final UUID uuid = UUID.randomUUID();
    assertEquals(uuid.toString(), instance.valueToString(uuid, null, null, null, null, null));

    expectTypeErrorInValueToString(instance, 'A');
  }

  @Test
  public void valueOfString() throws Exception {
    final UUID uuid = UUID.fromString("aabbccdd-aabb-ccdd-eeff-aabbccddeeff");

    assertEquals(uuid, instance.valueOfString("aabbccdd-aabb-ccdd-eeff-aabbccddeeff", null, null, null, null, null,
        UUID.class));
    assertEquals(uuid, instance.valueOfString("AABBCCDD-AABB-CCDD-EEFF-AABBCCDDEEFF", null, null, null, null, null,
        UUID.class));
    assertEquals(uuid, instance.valueOfString("AABBCCDD-aabb-ccdd-eeff-AABBCCDDEEFF", null, null, null, null, null,
        UUID.class));

    expectContentErrorInValueOfString(instance, "AABBCCDDAABBCCDDEEFFAABBCCDDEEFF");

    expectTypeErrorInValueOfString(instance, uuid.toString());
  }
}
