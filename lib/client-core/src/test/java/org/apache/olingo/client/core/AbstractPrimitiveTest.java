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
package org.apache.olingo.client.core;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.domain.ODataGeospatialValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class AbstractPrimitiveTest extends AbstractTest {

  protected abstract ODataFormat getFormat();

  protected ODataServiceVersion getVersion() {
    return getClient().getServiceVersion();
  }

  protected String getFilename(final String entity, final String propertyName) {
    return getVersion().name().toLowerCase()
            + File.separatorChar
            + entity.replace('(', '_').replace(")", "")
            + "_" + propertyName.replaceAll("/", "_") + "." + getSuffix(getFormat());
  }

  protected ODataPrimitiveValue writePrimitiveValue(final ODataPrimitiveValue value) {
    final ODataPrimitiveValue newValue = getClient().getPrimitiveValueBuilder().
            setType(value.getTypeKind()).
            setValue(value.toValue()).build();

    final InputStream written = getClient().getWriter().writeProperty(
            getClient().getObjectFactory().newPrimitiveProperty(Constants.ELEM_PROPERTY, newValue),
            getFormat());
    return readPrimitiveValue(written);
  }

  protected ODataPrimitiveValue readPrimitiveValue(final InputStream input) {
    final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
    assertNotNull(property);
    assertTrue(property.hasPrimitiveValue());
    assertNotNull(property.getPrimitiveValue());

    return property.getPrimitiveValue();
  }

  protected ODataPrimitiveValue readPrimitiveValue(final String entity, final String propertyName) {
    final ODataPrimitiveValue value =
            readPrimitiveValue(getClass().getResourceAsStream(getFilename(entity, propertyName)));

    assertEquals(value.toString(), writePrimitiveValue(value).toString());

    return value;
  }

  protected ODataGeospatialValue writeGeospatialValue(final ODataGeospatialValue value) {
    final ODataGeospatialValue newValue = getClient().getGeospatialValueBuilder().
            setType(value.getTypeKind()).
            setValue(value.toValue()).
            build();
    final InputStream written = getClient().getWriter().writeProperty(
            getClient().getObjectFactory().newPrimitiveProperty(Constants.ELEM_PROPERTY, newValue),
            getFormat());
    return readGeospatialValue(written);
  }

  protected ODataGeospatialValue readGeospatialValue(final InputStream input) {
    final ODataProperty property = getClient().getReader().readProperty(input, getFormat());
    assertNotNull(property);
    assertTrue(property.hasGeospatialValue());
    assertNotNull(property.getGeospatialValue());

    return property.getGeospatialValue();
  }

  protected ODataGeospatialValue readGeospatialValue(final String entity, final String propertyName) {
    final ODataGeospatialValue value =
            readGeospatialValue(getClass().getResourceAsStream(getFilename(entity, propertyName)));

    assertEquals(value.toValue(), writeGeospatialValue(value).toValue());

    return value;
  }

  protected void int32(final String entity, final String propertyName, final int check)
          throws EdmPrimitiveTypeException {

    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.Int32, opv.getTypeKind());

    final Integer value = opv.toCastValue(Integer.class);
    assertNotNull(value);
    assertTrue(check == value);
  }

  protected void string(final String entity, final String propertyName, final String check)
          throws EdmPrimitiveTypeException {

    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.String, opv.getTypeKind());

    final String value = opv.toCastValue(String.class);
    assertNotNull(value);
    assertEquals(check, value);

    assertEquals(opv, writePrimitiveValue(opv));
  }

  protected void decimal(final String entity, final String propertyName, final BigDecimal check)
          throws EdmPrimitiveTypeException {

    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.Decimal, opv.getTypeKind());

    final BigDecimal value = opv.toCastValue(BigDecimal.class);
    assertNotNull(value);
    assertTrue(check.equals(value));
  }

  protected void datetime(final String entity, final String propertyName, final String check)
          throws EdmPrimitiveTypeException {

    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.DateTime, opv.getTypeKind());

    final Timestamp value = opv.toCastValue(Timestamp.class);
    assertNotNull(value);
    assertEquals(check, opv.toString());
  }

  protected void guid(final String entity, final String propertyName, final String check)
          throws EdmPrimitiveTypeException {

    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.Guid, opv.getTypeKind());

    final UUID value = opv.toCastValue(UUID.class);
    assertNotNull(value);
    assertEquals(check, opv.toString());
  }

  protected void binary(final String entity, final String propertyName) throws EdmPrimitiveTypeException {
    final ODataPrimitiveValue opv = readPrimitiveValue(entity, propertyName);
    assertEquals(EdmPrimitiveTypeKind.Binary, opv.getTypeKind());

    final byte[] value = opv.toCastValue(byte[].class);
    assertNotNull(value);
    assertTrue(value.length > 0);
    assertTrue(Base64.isBase64(opv.toString()));
  }

  private void checkPoint(final Point point, final Point check) {
    assertEquals(check.getX(), point.getX(), 0);
    assertEquals(check.getY(), point.getY(), 0);
    assertEquals(check.getZ(), point.getZ(), 0);
  }

  protected void point(
          final String entity,
          final String propertyName,
          final Point expectedValues,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final Point point = opv.toCastValue(Point.class);
    assertNotNull(point);
    assertEquals(expectedDimension, point.getDimension());

    checkPoint(point, expectedValues);
  }

  private void checkLine(final LineString line, final List<Point> check) {
    final List<Point> points = new ArrayList<Point>();

    for (Point point : line) {
      points.add(point);
    }

    assertEquals(check.size(), points.size());

    for (int i = 0; i < points.size(); i++) {
      checkPoint(points.get(i), check.get(i));
    }
  }

  protected void lineString(
          final String entity,
          final String propertyName,
          final List<Point> check,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final LineString lineString = opv.toCastValue(LineString.class);
    assertNotNull(lineString);
    assertEquals(expectedDimension, lineString.getDimension());

    checkLine(lineString, check);
  }

  protected void multiPoint(
          final String entity,
          final String propertyName,
          final List<Point> check,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final MultiPoint multiPoint = opv.toCastValue(MultiPoint.class);
    assertNotNull(multiPoint);
    assertEquals(expectedDimension, multiPoint.getDimension());

    final List<Point> points = new ArrayList<Point>();

    for (Point point : multiPoint) {
      points.add(point);
    }

    assertEquals(check.size(), points.size());

    for (int i = 0; i < points.size(); i++) {
      checkPoint(points.get(i), check.get(i));
    }
  }

  protected void multiLine(
          final String entity,
          final String propertyName,
          final List<List<Point>> check,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final MultiLineString multiLine = opv.toCastValue(MultiLineString.class);
    assertNotNull(multiLine);
    assertEquals(expectedDimension, multiLine.getDimension());

    final List<LineString> lines = new ArrayList<LineString>();

    int i = 0;
    for (LineString line : multiLine) {
      checkLine(line, check.get(i));
      i++;
    }
  }

  private void checkPoligon(
          final Polygon polygon,
          final List<Point> checkInterior,
          final List<Point> checkExterior) {

    final List<Point> points = new ArrayList<Point>();

    for (Point point : polygon.getInterior()) {
      points.add(point);
    }

    assertEquals(checkInterior.size(), points.size());

    for (int i = 0; i < points.size(); i++) {
      checkPoint(checkInterior.get(i), points.get(i));
    }

    points.clear();

    for (Point point : polygon.getExterior()) {
      points.add(point);
    }

    assertEquals(checkExterior.size(), points.size());

    for (int i = 0; i < points.size(); i++) {
      checkPoint(checkExterior.get(i), points.get(i));
    }

  }

  protected void polygon(
          final String entity,
          final String propertyName,
          final List<Point> checkInterior,
          final List<Point> checkExterior,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final Polygon polygon = opv.toCastValue(Polygon.class);

    assertNotNull(polygon);
    assertEquals(expectedDimension, polygon.getDimension());

    checkPoligon(polygon, checkInterior, checkExterior);

  }

  protected void multiPolygon(
          final String entity,
          final String propertyName,
          final List<List<Point>> checkInterior,
          final List<List<Point>> checkExterior,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final MultiPolygon multiPolygon = opv.toCastValue(MultiPolygon.class);
    assertNotNull(multiPolygon);
    assertEquals(expectedDimension, multiPolygon.getDimension());

    int i = 0;
    for (Polygon polygon : multiPolygon) {
      checkPoligon(
              polygon,
              checkInterior.isEmpty() ? Collections.<Point>emptyList() : checkInterior.get(i),
              checkExterior.isEmpty() ? Collections.<Point>emptyList() : checkExterior.get(i));
      i++;
    }
  }

  protected void geomCollection(
          final String entity,
          final String propertyName,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final GeospatialCollection collection = opv.toCastValue(GeospatialCollection.class);
    assertNotNull(collection);
    assertEquals(expectedDimension, collection.getDimension());

    final Iterator<Geospatial> itor = collection.iterator();
    int count = 0;
    while (itor.hasNext()) {
      count++;

      final Geospatial geospatial = itor.next();
      if (count == 1) {
        assertTrue(geospatial instanceof Point);
      }
      if (count == 2) {
        assertTrue(geospatial instanceof LineString);
      }
    }
    assertEquals(2, count);
  }

  protected void geogCollection(
          final String entity,
          final String propertyName,
          final EdmPrimitiveTypeKind expectedType,
          final Dimension expectedDimension) {

    final ODataGeospatialValue opv = readGeospatialValue(entity, propertyName);
    assertEquals(expectedType, opv.getTypeKind());

    final GeospatialCollection collection = opv.toCastValue(GeospatialCollection.class);
    assertNotNull(collection);
    assertEquals(expectedDimension, collection.getDimension());

    final Iterator<Geospatial> itor = collection.iterator();
    int count = 0;
    while (itor.hasNext()) {
      count++;

      final Geospatial geospatial = itor.next();
      if (count == 1) {
        assertTrue(geospatial instanceof GeospatialCollection);
      }
      if (count == 2) {
        assertTrue(geospatial instanceof GeospatialCollection);
      }
    }
    assertEquals(2, count);
  }
}
