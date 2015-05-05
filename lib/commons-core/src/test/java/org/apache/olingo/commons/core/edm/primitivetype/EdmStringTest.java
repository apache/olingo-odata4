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

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.junit.Test;

public class EdmStringTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String);

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("'StringValue'", instance.toUriLiteral("StringValue"));
    assertEquals("'String''Value'", instance.toUriLiteral("String'Value"));
    assertEquals("'String''''''Value'", instance.toUriLiteral("String'''Value"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("String''Value", instance.fromUriLiteral("'String''''Value'"));

    expectErrorInFromUriLiteral(instance, "");
    expectErrorInFromUriLiteral(instance, "'");
    expectErrorInFromUriLiteral(instance, "'\"");
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("text", instance.valueToString("text", null, null, null, null, null));
    assertEquals("a\nb", instance.valueToString("a\nb", null, null, null, null, null));
    assertEquals("true", instance.valueToString(true, null, null, null, null, null));
    assertEquals("a'b", instance.valueToString("a'b", null, null, null, null, null));

    assertEquals("text", instance.valueToString("text", null, null, null, null, true));
    assertEquals("text", instance.valueToString("text", null, 4, null, null, null));
    assertEquals("text", instance.valueToString("text", null, Integer.MAX_VALUE, null, null, null));

    expectFacetsErrorInValueToString(instance, "schräg", null, null, null, null, false);
    expectFacetsErrorInValueToString(instance, "text", null, 3, null, null, null);
  }

  @Test
  public void valueOfString() throws Exception {
    assertEquals("text", instance.valueOfString("text", null, null, null, null, null, String.class));
    assertEquals("a\nb", instance.valueOfString("a\nb", null, null, null, null, null, String.class));
    assertEquals("true", instance.valueOfString("true", null, null, null, null, null, String.class));
    assertEquals("'a''b'", instance.valueOfString("'a''b'", null, null, null, null, null, String.class));

    assertEquals("text", instance.valueOfString("text", null, null, null, null, true, String.class));
    assertEquals("text", instance.valueOfString("text", null, 4, null, null, null, String.class));
    assertEquals("text", instance.valueOfString("text", null, Integer.MAX_VALUE, null, null, null, String.class));

    expectFacetsErrorInValueOfString(instance, "schräg", null, null, null, null, false);
    expectFacetsErrorInValueOfString(instance, "text", null, 3, null, null, null);

    expectTypeErrorInValueOfString(instance, "text");
  }
}
