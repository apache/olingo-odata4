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

public class EdmBooleanTest extends PrimitiveTypeBaseTest {

  private final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean);

  @Test
  public void toUriLiteral() throws Exception {
    assertEquals("true", instance.toUriLiteral("true"));
    assertEquals("false", instance.toUriLiteral("false"));
  }

  @Test
  public void fromUriLiteral() throws Exception {
    assertEquals("true", instance.fromUriLiteral("true"));
    assertEquals("false", instance.fromUriLiteral("false"));
  }

  @Test
  public void valueToString() throws Exception {
    assertEquals("true", instance.valueToString(true, null, null, null, null, null));
    assertEquals("false", instance.valueToString(Boolean.FALSE, null, null, null, null, null));
    
    expectTypeErrorInValueToString(instance, 0);
  }
  
  @Test
  public void valueOfString() throws Exception {
    assertEquals(true, instance.valueOfString("true", null, null, null, null, null, Boolean.class));
    assertEquals(false, instance.valueOfString("false", null, null, null, null, null, Boolean.class));

    expectContentErrorInValueOfString(instance, "True");
    expectContentErrorInValueOfString(instance, "1");
    expectContentErrorInValueOfString(instance, "0");
    expectContentErrorInValueOfString(instance, "-1");
    expectContentErrorInValueOfString(instance, "FALSE");

    expectTypeErrorInValueOfString(instance, "true");
  }
}
