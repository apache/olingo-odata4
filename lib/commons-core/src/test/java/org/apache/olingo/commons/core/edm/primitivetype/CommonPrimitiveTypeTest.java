/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.edm.primitivetype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.UUID;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.junit.Test;

public class CommonPrimitiveTypeTest extends PrimitiveTypeBaseTest {

  @Test
  public void nameSpace() throws Exception {
    assertEquals(EdmPrimitiveType.SYSTEM_NAMESPACE, Uint7.getInstance().getNamespace());

    assertEquals(EdmPrimitiveType.EDM_NAMESPACE, EdmInt32.getInstance().getNamespace());
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmType instance = kind.isGeospatial()
                               ? EdmPrimitiveTypeFactory.getGeoInstance(kind)
                               : EdmPrimitiveTypeFactory.getNonGeoInstance(kind);
      assertEquals(EdmPrimitiveType.EDM_NAMESPACE, instance.getNamespace());
    }
  }

  @Test
  public void names() throws Exception {
    assertEquals("Uint7", Uint7.getInstance().getName());

    assertEquals("Binary", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Binary).getName());
    assertEquals("Boolean", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Boolean).getName());
    assertEquals("Byte", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Byte).getName());
    assertEquals("Date", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Date).getName());
    assertEquals("DateTimeOffset",
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.DateTimeOffset).getName());
    assertEquals("Decimal", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Decimal).getName());
    assertEquals("Double", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Double).getName());
    assertEquals("Duration", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Duration).getName());
    assertEquals("Guid", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Guid).getName());
    assertEquals("Int16", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int16).getName());
    assertEquals("Int32", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int32).getName());
    assertEquals("Int64", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int64).getName());
    assertEquals("SByte", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.SByte).getName());
    assertEquals("Single", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Single).getName());
    assertEquals("String", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.String).getName());
    assertEquals("TimeOfDay", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.TimeOfDay).getName());
  }

  @Test
  public void kind() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (kind.isGeospatial()) {
        assertEquals(EdmTypeKind.PRIMITIVE, EdmPrimitiveTypeFactory.getGeoInstance(kind).getKind());
      } else {
        assertEquals(EdmTypeKind.PRIMITIVE, EdmPrimitiveTypeFactory.getNonGeoInstance(kind).getKind());
      }
    }
  }

  @Test
  public void toStringAll() throws Exception {
    assertEquals("System.Uint7", Uint7.getInstance().toString());

    assertEquals("Edm.Binary", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Binary).toString());
    assertEquals("Edm.Boolean", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Boolean).toString());
    assertEquals("Edm.Byte", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Byte).toString());
    assertEquals("Edm.Date", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Date).toString());
    assertEquals("Edm.DateTimeOffset",
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.DateTimeOffset).toString());
    assertEquals("Edm.Decimal", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Decimal).toString());
    assertEquals("Edm.Double", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Double).toString());
    assertEquals("Edm.Duration", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Duration).toString());
    assertEquals("Edm.Guid", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Guid).toString());
    assertEquals("Edm.Int16", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int16).toString());
    assertEquals("Edm.Int32", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int32).toString());
    assertEquals("Edm.Int64", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int64).toString());
    assertEquals("Edm.SByte", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.SByte).toString());
    assertEquals("Edm.Single", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Single).toString());
    assertEquals("Edm.String", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.String).toString());
    assertEquals("Edm.TimeOfDay", EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.TimeOfDay).toString());

    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (!kind.isGeospatial()) {
        final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getNonGeoInstance(kind);
        assertEquals(instance.toString(), kind.getFullQualifiedName().toString());
      }
    }
  }

  @Test
  public void compatibility() {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (!kind.isGeospatial()) {
        final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getNonGeoInstance(kind);
        assertTrue(instance.isCompatible(instance));
        assertFalse(instance.isCompatible(EdmPrimitiveTypeFactory.getNonGeoInstance(
                (kind == EdmPrimitiveTypeKind.String ? EdmPrimitiveTypeKind.Binary : EdmPrimitiveTypeKind.String))));
      }
    }
  }

  @Test
  public void defaultType() throws Exception {
    assertEquals(Byte.class, Uint7.getInstance().getDefaultType());

    assertEquals(byte[].class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Binary).getDefaultType());
    assertEquals(Boolean.class,
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Boolean).getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Byte).getDefaultType());
    assertEquals(Calendar.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Date).getDefaultType());
    assertEquals(Calendar.class,
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.DateTimeOffset).getDefaultType());
    assertEquals(BigDecimal.class,
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Decimal).getDefaultType());
    assertEquals(Double.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Double).getDefaultType());
    assertEquals(BigDecimal.class,
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Duration).getDefaultType());
    assertEquals(UUID.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Guid).getDefaultType());
    assertEquals(Short.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int16).getDefaultType());
    assertEquals(Integer.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int32).getDefaultType());
    assertEquals(Long.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Int64).getDefaultType());
    assertEquals(Byte.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.SByte).getDefaultType());
    assertEquals(Float.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Single).getDefaultType());
    assertEquals(String.class, EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.String).getDefaultType());
    assertEquals(Calendar.class,
            EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.TimeOfDay).getDefaultType());
  }

  @Test
  public void validate() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (!kind.isGeospatial()) {
        final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getNonGeoInstance(kind);
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
    }

    assertTrue(EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Binary).
            validate("abcd", null, 3, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Binary).
            validate("abcd", null, 2, null, null, null));

    assertTrue(EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Decimal).
            validate("1", null, null, null, null, null));
    assertFalse(EdmPrimitiveTypeFactory.getNonGeoInstance(EdmPrimitiveTypeKind.Decimal).
            validate("1.2", null, null, null, 0, null));
  }

  @Test
  public void uriLiteral() throws Exception {
    for (EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      if (!kind.isGeospatial()) {
        final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getNonGeoInstance(kind);
        assertEquals("test", instance.fromUriLiteral(instance.toUriLiteral("test")));
        assertNull(instance.toUriLiteral(null));
        assertNull(instance.fromUriLiteral(null));
      }
    }
  }
}
