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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.junit.Test;

public class EdmGeoTest extends PrimitiveTypeBaseTest {

  @Test
  public void point() throws EdmPrimitiveTypeException {
    final String input = "geometry'SRID=0;Point(142.1 64.1)'";

    expectContentErrorInValueOfString(EdmGeographyPoint.getInstance(), input);

    final Point point = EdmGeometryPoint.getInstance().valueOfString(input, null, null, null, null, null, Point.class);
    assertNotNull(point);
    assertEquals("0", point.getSrid().toString());
    assertEquals(142.1, point.getX(), 0);
    assertEquals(64.1, point.getY(), 0);

    assertEquals(input, EdmGeometryPoint.getInstance().valueToString(point, null, null, null, null, null));
  }

  @Test
  public void multiPoint() throws EdmPrimitiveTypeException {
    final String input = "geography'SRID=0;MultiPoint((142.1 64.1),(1.0 2.0))'";

    expectContentErrorInValueOfString(EdmGeometryMultiPoint.getInstance(), input);

    MultiPoint multipoint = EdmGeographyMultiPoint.getInstance().
        valueOfString(input, null, null, null, null, null, MultiPoint.class);
    assertNotNull(multipoint);
    assertEquals("0", multipoint.getSrid().toString());
    assertEquals(142.1, multipoint.iterator().next().getX(), 0);
    assertEquals(64.1, multipoint.iterator().next().getY(), 0);

    assertEquals(input, EdmGeographyMultiPoint.getInstance().valueToString(multipoint, null, null, null, null, null));

    multipoint = EdmGeographyMultiPoint.getInstance().
        valueOfString("geography'SRID=0;MultiPoint()'", null, null, null, null, null, MultiPoint.class);
    assertFalse(multipoint.iterator().hasNext());
  }

  @Test
  public void lineString() throws EdmPrimitiveTypeException {
    final String input = "geography'SRID=0;LineString(142.1 64.1,3.14 2.78)'";

    expectContentErrorInValueOfString(EdmGeographyPoint.getInstance(), input);
    expectContentErrorInValueOfString(EdmGeometryLineString.getInstance(), input);

    final LineString lineString = EdmGeographyLineString.getInstance().
        valueOfString(input, null, null, null, null, null, LineString.class);
    assertNotNull(lineString);
    assertEquals("0", lineString.getSrid().toString());
    final Iterator<Point> itor = lineString.iterator();
    assertEquals(142.1, itor.next().getX(), 0);
    assertEquals(2.78, itor.next().getY(), 0);

    assertEquals(input, EdmGeographyLineString.getInstance().valueToString(lineString, null, null, null, null, null));
  }

  @Test
  public void multiLineString() throws EdmPrimitiveTypeException {
    final String input = "geography'SRID=0;MultiLineString((142.1 64.1,3.14 2.78),(142.1 64.7,3.14 2.78))'";

    expectContentErrorInValueOfString(EdmGeographyPoint.getInstance(), input);
    expectContentErrorInValueOfString(EdmGeometryLineString.getInstance(), input);

    final MultiLineString multiLineString = EdmGeographyMultiLineString.getInstance().
        valueOfString(input, null, null, null, null, null, MultiLineString.class);
    assertNotNull(multiLineString);
    assertEquals("0", multiLineString.getSrid().toString());
    final Iterator<LineString> itor = multiLineString.iterator();
    assertEquals(142.1, itor.next().iterator().next().getX(), 0);
    assertEquals(64.7, itor.next().iterator().next().getY(), 0);

    assertEquals(input, EdmGeographyMultiLineString.getInstance().
        valueToString(multiLineString, null, null, null, null, null));
  }

  @Test
  public void polygon() throws EdmPrimitiveTypeException {
    final String input = "geography'SRID=0;Polygon((1.0 1.0,1.0 1.0),(1.0 1.0,2.0 2.0,3.0 3.0,1.0 1.0))'";

    expectContentErrorInValueOfString(EdmGeometryPolygon.getInstance(), input);

    final Polygon polygon = EdmGeographyPolygon.getInstance().
        valueOfString(input, null, null, null, null, null, Polygon.class);
    assertNotNull(polygon);
    assertEquals("0", polygon.getSrid().toString());
    Iterator<Point> itor = polygon.getInterior(0).iterator();
    assertEquals(1, itor.next().getX(), 0);
    assertEquals(1, itor.next().getY(), 0);
    itor = polygon.getExterior().iterator();
    itor.next();
    assertEquals(2, itor.next().getX(), 0);
    assertEquals(3, itor.next().getY(), 0);

    assertEquals(input, EdmGeographyPolygon.getInstance().valueToString(polygon, null, null, null, null, null));
  }
  
  @Test
  public void polygonMultipleHoles() throws EdmPrimitiveTypeException {
    final String input = "geography'SRID=4326;Polygon((1.0 1.0,1.0 1.0),(2.0 2.0,2.0 2.0)"
      + ",(1.0 1.0,2.0 2.0,3.0 3.0,1.0 1.0))'";

    expectContentErrorInValueOfString(EdmGeometryPolygon.getInstance(), input);

    final Polygon polygon = EdmGeographyPolygon.getInstance().
        valueOfString(input, null, null, null, null, null, Polygon.class);
    assertNotNull(polygon);
    assertEquals("4326", polygon.getSrid().toString());
    Iterator<Point> itor = polygon.getInterior(0).iterator();
    assertEquals(1, itor.next().getX(), 0);
    assertEquals(1, itor.next().getY(), 0);
    itor = polygon.getInterior(1).iterator();
    assertEquals(2, itor.next().getX(), 0);
    assertEquals(2, itor.next().getY(), 0);
    itor = polygon.getExterior().iterator();
    itor.next();
    assertEquals(2, itor.next().getX(), 0);
    assertEquals(3, itor.next().getY(), 0);

    assertEquals(input, EdmGeographyPolygon.getInstance().valueToString(polygon, null, null, null, null, null));
  }

  @Test
  public void multiPolygon() throws EdmPrimitiveTypeException {
    final String input = "geometry'SRID=0;MultiPolygon("
        + "((1.0 1.0,1.0 1.0),(1.0 1.0,2.0 2.0,3.0 3.0,1.0 1.0)),"
        + "((1.0 1.0,1.0 1.0),(1.0 1.0,2.0 2.0,3.0 3.0,1.0 1.0))"
        + ")'";

    expectContentErrorInValueOfString(EdmGeographyPolygon.getInstance(), input);

    final MultiPolygon multiPolygon = EdmGeometryMultiPolygon.getInstance().
        valueOfString(input, null, null, null, null, null, MultiPolygon.class);
    assertNotNull(multiPolygon);
    assertEquals("0", multiPolygon.getSrid().toString());
    final Iterator<Polygon> itor = multiPolygon.iterator();
    assertEquals(1, itor.next().getInterior(0).iterator().next().getX(), 0);
    assertEquals(1, itor.next().getInterior(0).iterator().next().getX(), 0);

    assertEquals(input, EdmGeometryMultiPolygon.getInstance().
        valueToString(multiPolygon, null, null, null, null, null));

    EdmGeographyMultiPolygon.getInstance().valueOfString(
        "geography'SRID=0;MultiPolygon(((1 1,1 1),(1 1,2 2,3 3,1 1)))'",
        null, null, null, null, null, MultiPolygon.class);
  }

  @Test
  public void collection() throws EdmPrimitiveTypeException {
    final String input = "geometry'SRID=0;Collection(LineString(142.1 64.1,3.14 2.78))'";

    final GeospatialCollection collection = EdmGeometryCollection.getInstance().
        valueOfString(input, null, null, null, null, null, GeospatialCollection.class);
    assertNotNull(collection);
    assertEquals("0", collection.getSrid().toString());

    final Geospatial item = collection.iterator().next();
    assertNotNull(item);
    assertTrue(item instanceof LineString);

    assertEquals(input, EdmGeometryCollection.getInstance().
        valueToString(collection, null, null, null, null, null));
  }
}
