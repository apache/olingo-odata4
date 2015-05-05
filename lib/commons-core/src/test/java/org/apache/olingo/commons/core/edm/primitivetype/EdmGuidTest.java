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

import java.util.UUID;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmGuidTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Guid);

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
