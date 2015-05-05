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

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmBinaryTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary);

  @Test
  public void validate() throws Exception {
    assertTrue(instance.validate(null, null, null, null, null, null));
    assertTrue(instance.validate(null, true, null, null, null, null));
    assertFalse(instance.validate(null, false, null, null, null, null));
    assertTrue(instance.validate("", null, null, null, null, null));
    assertFalse(instance.validate("????", null, null, null, null, null));

    assertTrue(instance.validate("qrvM3e7_", null, null, null, null, null));
    assertTrue(instance.validate("qrvM3e7_", null, 6, null, null, null));
    assertFalse(instance.validate("qrvM3e7_", null, 5, null, null, null));
  }

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("binary'+hKqoQ=='", instance.toUriLiteral("+hKqoQ=="));
    assertEquals("binary''", instance.toUriLiteral(""));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("+hKqoQ==", instance.fromUriLiteral("binary'+hKqoQ=='"));
    assertEquals("", instance.fromUriLiteral("binary''"));

    expectErrorInFromUriLiteral(instance, "");
    expectErrorInFromUriLiteral(instance, "binary'\"");
    expectErrorInFromUriLiteral(instance, "X''");
    expectErrorInFromUriLiteral(instance, "Xinary''");
  }

  @Test
  public void valueToString() throws Exception {
    final byte[] binary = new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };

    assertEquals("qrvM3e7/", instance.valueToString(binary, null, null, null, null, null));

    assertEquals("qrvM3e7/", instance.valueToString(binary, null, 6, null, null, null));
    assertEquals("qrvM3e7/", instance.valueToString(binary, null, Integer.MAX_VALUE, null, null, null));

    assertEquals("qg==", instance.valueToString(new Byte[] { new Byte((byte) 170) }, null, null, null, null, null));

    expectFacetsErrorInValueToString(instance, binary, null, 3, null, null, null);

    expectTypeErrorInValueToString(instance, 0);
  }

  @Test
  public void valueOfString() throws Exception {
    final byte[] binary = new byte[] { (byte) 0xAA, (byte) 0xBB, (byte) 0xCC, (byte) 0xDD, (byte) 0xEE, (byte) 0xFF };

    assertTrue(Arrays.equals(binary, instance.valueOfString("qrvM3e7_", null, null, null, null, null, byte[].class)));
    assertTrue(Arrays.equals(new Byte[] { binary[0], binary[1], binary[2] }, instance.valueOfString("qrvM", null, null,
        null, null, null, Byte[].class)));

    assertTrue(Arrays.equals(binary, instance.valueOfString("qrvM3e7_", null, 6, null, null, null, byte[].class)));
    assertTrue(Arrays.equals(new byte[] { 42 }, instance.valueOfString("Kg==", null, 1, null, null, null,
        byte[].class)));
    assertTrue(Arrays.equals(new byte[] { 42 }, instance.valueOfString("Kg", null, 1, null, null, null,
        byte[].class)));
    assertTrue(Arrays.equals(new byte[] { 1, 2 }, instance.valueOfString("AQI=", null, 2, null, null, null,
        byte[].class)));
    assertTrue(Arrays.equals(binary, instance.valueOfString("qrvM3e7_", null, 6, null, null, null,
        byte[].class)));
    assertTrue(Arrays.equals(binary, instance.valueOfString("qrvM3e7_", null, Integer.MAX_VALUE, null, null, null,
        byte[].class)));
    assertTrue(Arrays.equals(binary, instance.valueOfString("\nqrvM\n3e7_\r\n", null, 6, null, null, null,
        byte[].class)));

    expectFacetsErrorInValueOfString(instance, "qrvM3e7_", null, 3, null, null, null);
    expectContentErrorInValueOfString(instance, "@");

    expectTypeErrorInValueOfString(instance, "qrvM3e7_");
  }
}
