package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UInt7Test extends PrimitiveTypeBaseTest {

  @Test
  public void compatibility() {
    assertTrue(Uint7.getInstance().isCompatible(Uint7.getInstance()));
    assertFalse(Uint7.getInstance().isCompatible(EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance()));
  }
}
