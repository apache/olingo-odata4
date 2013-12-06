/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.apache.olingo.odata4.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.odata4.commons.api.edm.constants.EdmTypeKind;
import org.junit.Test;

public class CommonPrimitiveTypeTest extends PrimitiveTypeBaseTest {

  @Test
  public void nameSpace() throws Exception {
    assertEquals(EdmPrimitiveType.SYSTEM_NAMESPACE, Uint7.getInstance().getNamespace());

    assertEquals(EdmPrimitiveType.EDM_NAMESPACE, EdmNull.getInstance().getNamespace());
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertEquals(EdmPrimitiveType.EDM_NAMESPACE, instance.getNamespace());
    }
  }

  @Test
  public void names() throws Exception {
    assertEquals("Uint7", Uint7.getInstance().getName());

    assertEquals("Null", EdmNull.getInstance().getName());
    assertEquals("Binary", EdmPrimitiveTypeKind.Binary.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Boolean", EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Byte", EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Date", EdmPrimitiveTypeKind.Date.getEdmPrimitiveTypeInstance().getName());
    assertEquals("DateTimeOffset", EdmPrimitiveTypeKind.DateTimeOffset.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Decimal", EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Double", EdmPrimitiveTypeKind.Double.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Duration", EdmPrimitiveTypeKind.Duration.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Guid", EdmPrimitiveTypeKind.Guid.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Int16", EdmPrimitiveTypeKind.Int16.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Int32", EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Int64", EdmPrimitiveTypeKind.Int64.getEdmPrimitiveTypeInstance().getName());
    assertEquals("SByte", EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance().getName());
    assertEquals("Single", EdmPrimitiveTypeKind.Single.getEdmPrimitiveTypeInstance().getName());
    assertEquals("String", EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance().getName());
    assertEquals("TimeOfDay", EdmPrimitiveTypeKind.TimeOfDay.getEdmPrimitiveTypeInstance().getName());
  }

  @Test
  public void kind() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(EdmTypeKind.PRIMITIVE, kind.getEdmPrimitiveTypeInstance().getKind());
    }
  }

  @Test
  public void toStringAll() throws Exception {
    assertEquals("System.Uint7", Uint7.getInstance().toString());

    assertEquals("Edm.Null", EdmNull.getInstance().toString());
    assertEquals("Edm.Binary", EdmPrimitiveTypeKind.Binary.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Boolean", EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Byte", EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Date", EdmPrimitiveTypeKind.Date.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.DateTimeOffset", EdmPrimitiveTypeKind.DateTimeOffset.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Decimal", EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Double", EdmPrimitiveTypeKind.Double.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Duration", EdmPrimitiveTypeKind.Duration.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Guid", EdmPrimitiveTypeKind.Guid.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Int16", EdmPrimitiveTypeKind.Int16.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Int32", EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Int64", EdmPrimitiveTypeKind.Int64.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.SByte", EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.Single", EdmPrimitiveTypeKind.Single.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.String", EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance().toString());
    assertEquals("Edm.TimeOfDay", EdmPrimitiveTypeKind.TimeOfDay.getEdmPrimitiveTypeInstance().toString());

    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertEquals(instance.toString(), kind.getFullQualifiedName().toString());
    }
  }

  @Test
  public void compatibility() {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertTrue(instance.isCompatible(instance));
      assertFalse(instance.isCompatible(
          (kind == EdmPrimitiveTypeKind.String ? EdmPrimitiveTypeKind.Binary : EdmPrimitiveTypeKind.String)
              .getEdmPrimitiveTypeInstance()));
    }
  }

  @Test
  public void defaultType() throws Exception {
    assertEquals(Byte.class, Uint7.getInstance().getDefaultType());
    assertNull(EdmNull.getInstance().getDefaultType());

    assertEquals(byte[].class, EdmPrimitiveTypeKind.Binary.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Boolean.class, EdmPrimitiveTypeKind.Boolean.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeKind.Byte.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Calendar.class, EdmPrimitiveTypeKind.Date.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Calendar.class, EdmPrimitiveTypeKind.DateTimeOffset.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(BigDecimal.class, EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Double.class, EdmPrimitiveTypeKind.Double.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(BigDecimal.class, EdmPrimitiveTypeKind.Duration.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(UUID.class, EdmPrimitiveTypeKind.Guid.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeKind.Int16.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Integer.class, EdmPrimitiveTypeKind.Int32.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Long.class, EdmPrimitiveTypeKind.Int64.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Byte.class, EdmPrimitiveTypeKind.SByte.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Float.class, EdmPrimitiveTypeKind.Single.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(String.class, EdmPrimitiveTypeKind.String.getEdmPrimitiveTypeInstance().getDefaultType());
    assertEquals(Calendar.class, EdmPrimitiveTypeKind.TimeOfDay.getEdmPrimitiveTypeInstance().getDefaultType());
  }

  @Test
  public void validate() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertTrue(instance.validate(null, null, null, null, null, null));
      assertTrue(instance.validate(null, true, null, null, null, null));
      assertFalse(instance.validate(null, false, null, null, null, null));
      assertFalse(instance.validate("ä", null, null, null, null, false));
      if (kind != EdmPrimitiveTypeKind.String && kind != EdmPrimitiveTypeKind.Binary) {
        assertFalse(instance.validate("", null, null, null, null, null));
      }
      if (kind != EdmPrimitiveTypeKind.String) {
        assertFalse(instance.validate("ä", null, null, null, null, null));
      }
    }

    assertTrue(EdmPrimitiveTypeKind.Binary.getEdmPrimitiveTypeInstance().validate("abcd", null, 3, null, null, null));
    assertFalse(EdmPrimitiveTypeKind.Binary.getEdmPrimitiveTypeInstance().validate("abcd", null, 2, null, null, null));

    assertTrue(EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance().validate("1", null, null, null, null,
        null));
    assertFalse(EdmPrimitiveTypeKind.Decimal.getEdmPrimitiveTypeInstance().validate("1.2", null, null, null, 0, null));
  }

  @Test
  public void uriLiteral() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = kind.getEdmPrimitiveTypeInstance();
      assertEquals("test", instance.fromUriLiteral(instance.toUriLiteral("test")));
      assertNull(instance.toUriLiteral(null));
      assertNull(instance.fromUriLiteral(null));
    }
  }
}
