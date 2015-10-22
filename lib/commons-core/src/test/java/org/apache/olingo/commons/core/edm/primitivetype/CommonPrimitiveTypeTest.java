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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;

import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.junit.Test;

public class CommonPrimitiveTypeTest extends PrimitiveTypeBaseTest {

  @Test
  public void nameSpace() throws Exception {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(EdmPrimitiveType.EDM_NAMESPACE, EdmPrimitiveTypeFactory.getInstance(kind).getNamespace());
    }
  }

  @Test
  public void names() throws Exception {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(kind.name(), EdmPrimitiveTypeFactory.getInstance(kind).getName());
    }
  }

  @Test
  public void kind() throws Exception {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(EdmTypeKind.PRIMITIVE, EdmPrimitiveTypeFactory.getInstance(kind).getKind());
    }
  }

  @Test
  public void toStringAll() throws Exception {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      assertEquals(kind.getFullQualifiedName().toString(), EdmPrimitiveTypeFactory.getInstance(kind).toString());
    }
  }

  @Test
  public void compatibility() {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertTrue(instance.isCompatible(instance));
      assertFalse(instance.isCompatible(EdmPrimitiveTypeFactory.getInstance(
          (kind == EdmPrimitiveTypeKind.String ? EdmPrimitiveTypeKind.Binary : EdmPrimitiveTypeKind.String))));
    }
  }

  @Test
  public void defaultType() throws Exception {
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

    assertEquals(Geospatial.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Geography).getDefaultType());
    assertEquals(Point.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyPoint).getDefaultType());
    assertEquals(LineString.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyLineString).getDefaultType());
    assertEquals(Polygon.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyPolygon).getDefaultType());
    assertEquals(MultiPoint.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyMultiPoint).getDefaultType());
    assertEquals(MultiLineString.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyMultiLineString).getDefaultType());
    assertEquals(MultiPolygon.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyMultiPolygon).getDefaultType());
    assertEquals(GeospatialCollection.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeographyCollection).getDefaultType());
    assertEquals(Geospatial.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Geometry).getDefaultType());
    assertEquals(Point.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryPoint).getDefaultType());
    assertEquals(LineString.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryLineString).getDefaultType());
    assertEquals(Polygon.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryPolygon).getDefaultType());
    assertEquals(MultiPoint.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryMultiPoint).getDefaultType());
    assertEquals(MultiLineString.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryMultiLineString).getDefaultType());
    assertEquals(MultiPolygon.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryMultiPolygon).getDefaultType());
    assertEquals(GeospatialCollection.class,
        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.GeometryCollection).getDefaultType());
  }

  @Test
  public void validate() throws Exception {
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
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
    for (final EdmPrimitiveTypeKind kind : EdmPrimitiveTypeKind.values()) {
      final EdmPrimitiveType instance = EdmPrimitiveTypeFactory.getInstance(kind);
      assertEquals("test", instance.fromUriLiteral(instance.toUriLiteral("test")));
      assertNull(instance.toUriLiteral(null));
      assertNull(instance.fromUriLiteral(null));
    }
  }
}
