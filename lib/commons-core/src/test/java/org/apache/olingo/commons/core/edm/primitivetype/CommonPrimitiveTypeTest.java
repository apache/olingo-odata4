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

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CommonPrimitiveTypeTest extends PrimitiveTypeBaseTest {

  @Test
  public void nameSpace() throws Exception {
    assertEquals(EdmPrimitiveType.SYSTEM_NAMESPACE, Uint7.getInstance().getNamespace());

    assertEquals(EdmPrimitiveType.EDM_NAMESPACE, EdmInt32.getInstance().getNamespace());
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertEquals(EdmPrimitiveType.EDM_NAMESPACE, instance.getNamespace());
    }
  }

  @Test
  public void names() throws Exception {
    assertEquals("Uint7", Uint7.getInstance().getName());

    assertEquals("Binary", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary).getName());
    assertEquals("Boolean", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean).getName());
    assertEquals("Byte", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte).getName());
    assertEquals("Date", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Date).getName());
    assertEquals("DateTimeOffset",
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName());
    assertEquals("Decimal", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).getName());
    assertEquals("Double", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double).getName());
    assertEquals("Duration", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Duration).getName());
    assertEquals("Guid", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Guid).getName());
    assertEquals("Int16", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16).getName());
    assertEquals("Int32", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32).getName());
    assertEquals("Int64", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64).getName());
    assertEquals("SByte", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte).getName());
    assertEquals("Single", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single).getName());
    assertEquals("String", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String).getName());
    assertEquals("TimeOfDay", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.TimeOfDay).getName());
  }

  @Test
  public void kind() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(EdmTypeKind.PRIMITIVE, EdmPrimitiveTypeFactory.getInstance(kind).getKind());
    }
  }

  @Test
  public void toStringAll() throws Exception {
    assertEquals("System.Uint7", Uint7.getInstance().toString());

    assertEquals("Edm.Binary", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary).toString());
    assertEquals("Edm.Boolean", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean).toString());
    assertEquals("Edm.Byte", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte).toString());
    assertEquals("Edm.Date", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Date).toString());
    assertEquals("Edm.DateTimeOffset",
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset).toString());
    assertEquals("Edm.Decimal", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).toString());
    assertEquals("Edm.Double", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double).toString());
    assertEquals("Edm.Duration", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Duration).toString());
    assertEquals("Edm.Guid", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Guid).toString());
    assertEquals("Edm.Int16", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16).toString());
    assertEquals("Edm.Int32", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32).toString());
    assertEquals("Edm.Int64", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64).toString());
    assertEquals("Edm.SByte", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte).toString());
    assertEquals("Edm.Single", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single).toString());
    assertEquals("Edm.String", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String).toString());
    assertEquals("Edm.TimeOfDay", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.TimeOfDay).toString());

    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertEquals(instance.toString(), kind.getFullQualifiedName().toString());
    }
  }

  @Test
  public void compatibility() {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertTrue(instance.isCompatible(instance));
      assertFalse(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(
          (kind == EdmPrimitiveTypeKind.String ? EdmPrimitiveTypeKind.Binary : EdmPrimitiveTypeKind.String))));
    }
  }

  @Test
  public void defaultType() throws Exception {
    assertEquals(Byte.class, Uint7.getInstance().getDefaultType());

    assertEquals(byte[].class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary).getDefaultType());
    assertEquals(Boolean.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean).getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte).getDefaultType());
    assertEquals(Calendar.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Date).getDefaultType());
    assertEquals(Timestamp.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.DateTimeOffset).getDefaultType());
    assertEquals(BigDecimal.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).getDefaultType());
    assertEquals(Double.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double).getDefaultType());
    assertEquals(BigDecimal.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Duration).getDefaultType());
    assertEquals(UUID.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Guid).getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16).getDefaultType());
    assertEquals(Integer.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32).getDefaultType());
    assertEquals(Long.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64).getDefaultType());
    assertEquals(Byte.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte).getDefaultType());
    assertEquals(Float.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single).getDefaultType());
    assertEquals(String.class, EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.String).getDefaultType());
    assertEquals(Calendar.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.TimeOfDay).getDefaultType());
  }

  @Test
  public void validate() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertTrue(instance.validate(null, null, null, null, null, null));
      assertTrue(instance.validate(null, true, null, null, null, null));
      assertFalse(instance.validate(null, false, null, null, null, null));
      if (kind != EdmPrimitiveTypeKind.Stream) {
        assertFalse(instance.validate("ä", null, null, null, null, false));
      }
      if (kind != EdmPrimitiveTypeKind.String && kind != EdmPrimitiveTypeKind.Binary
          && kind != EdmPrimitiveTypeKind.Stream) {

        assertFalse(instance.validate("", null, null, null, null, null));
      }
      if (kind != EdmPrimitiveTypeKind.String && kind != EdmPrimitiveTypeKind.Stream) {
        assertFalse(instance.validate("ä", null, null, null, null, null));
      }
    }

    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary).
        validate("abcd", null, 3, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Binary).
        validate("abcd", null, 2, null, null, null));

    assertTrue(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1", null, null, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal).
        validate("1.2", null, null, null, 0, null));
  }

  @Test
  public void uriLiteral() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertEquals("test", instance.fromUriLiteral(instance.toUriLiteral("test")));
      assertNull(instance.toUriLiteral(null));
      assertNull(instance.fromUriLiteral(null));
    }
  }
}
