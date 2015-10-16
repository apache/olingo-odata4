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
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmInt16Test extends PrimitiveTypeBaseTest {

  final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16);

  @Test
  public void testInt16Compatibility() {
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)));
    assertTrue(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)));
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
    assertEquals("-32768", instance.valueToString(BigInteger.valueOf(Short.MIN_VALUE), null, null, null, null, null));

    expectContentErrorInValueToString(instance, 123456);
    expectContentErrorInValueToString(instance, -32769);
    expectContentErrorInValueToString(instance, BigInteger.valueOf(32768));

    expectTypeErrorInValueToString(instance, 1.0);
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals(Byte.valueOf((byte) 1), instance.valueOfString("1", null, null, null, null, null, Byte.class));
    assertEquals(Short.valueOf((short) 2), instance.valueOfString("2", null, null, null, null, null, Short.class));
    assertEquals(Short.valueOf((short) -32768), instance.valueOfString("-32768", null, null, null, null, null,
        Short.class));
    assertEquals(Short.valueOf((short) 32767), instance.valueOfString("32767", null, null, null, null, null,
        Short.class));
    assertEquals(Integer.valueOf(0), instance.valueOfString("0", null, null, null, null, null, Integer.class));
    assertEquals(Long.valueOf(-1), instance.valueOfString("-1", null, null, null, null, null, Long.class));
    assertEquals(BigInteger.TEN, instance.valueOfString("10", null, null, null, null, null, BigInteger.class));

    expectContentErrorInValueOfString(instance, "32768");
    expectContentErrorInValueOfString(instance, "1.0");

    expectUnconvertibleErrorInValueOfString(instance, "-129", Byte.class);
    expectUnconvertibleErrorInValueOfString(instance, "128", Byte.class);

    expectTypeErrorInValueOfString(instance, "1");
  }
}
