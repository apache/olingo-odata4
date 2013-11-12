package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertNull;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.junit.Test;

public class EdmNullTest extends PrimitiveTypeBaseTest {

  @Test
  public void checkNull() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertNull(instance.valueToString(null, null, null, null, null, null));
      assertNull(instance.valueToString(null, true, null, null, null, null));

      expectNullErrorInValueToString(instance);
    }
  }

  @Test
  public void checkValueOfNull() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertNull(instance.valueOfString(null, null, null, null, null, null, instance.getDefaultType()));
      assertNull(instance.valueOfString(null, true, null, null, null, null, instance.getDefaultType()));

      expectNullErrorInValueOfString(instance);
    }
  }
}