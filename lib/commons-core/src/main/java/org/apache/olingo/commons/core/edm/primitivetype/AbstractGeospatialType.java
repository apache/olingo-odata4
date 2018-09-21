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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.geo.ComposedGeospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Dimension;
import org.apache.olingo.commons.api.edm.geo.Geospatial.Type;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.apache.olingo.commons.api.edm.geo.SRID;

public abstract class AbstractGeospatialType<T extends Geospatial> extends SingletonPrimitiveType {

  private static final Pattern PATTERN =
      Pattern.compile("([a-z]+)'SRID=([0-9]+);([a-zA-Z]+)\\((.*)\\)'");

  private static final Pattern COLLECTION_PATTERN =
      Pattern.compile("([a-z]+)'SRID=([0-9]+);Collection\\(([a-zA-Z]+)\\((.*)\\)\\)'");

  private final Class<T> reference;

  protected final Dimension dimension;

  protected final Type type;

  protected AbstractGeospatialType(final Class<T> reference, final Dimension dimension, final Type type) {
    this.reference = reference;
    this.dimension = dimension;
    this.type = type;
  }

  @Override
  public Class<?> getDefaultType() {
    return reference;
  }

  private Matcher getMatcher(final Pattern pattern, final String value) throws EdmPrimitiveTypeException {
    final Matcher matcher = pattern.matcher(value);
    if (!matcher.matches()) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }

    Geospatial.Dimension _dimension = null;
    Geospatial.Type _type = null;
    try {
      _dimension = Geospatial.Dimension.valueOf(matcher.group(1).toUpperCase());
      _type = Geospatial.Type.valueOf(matcher.group(3).toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.", e);
    }
    if (_dimension != this.dimension || (!pattern.equals(COLLECTION_PATTERN) && _type != this.type)) {
      throw new EdmPrimitiveTypeException("The literal '" + value + "' has illegal content.");
    }

    return matcher;
  }

  private Point newPoint(final SRID srid, final String point, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final List<String> pointCoo = split(point, ' ');
    if (pointCoo == null || pointCoo.size() != 2) {
      throw new EdmPrimitiveTypeException("The literal '" + point + "' has illegal content.");
    }

    final Point result = new Point(this.dimension, srid);
    result.setX(EdmDouble.getInstance().valueOfString(pointCoo.get(0),
        isNullable, maxLength, precision, scale, isUnicode, Double.class));
    result.setY(EdmDouble.getInstance().valueOfString(pointCoo.get(1),
        isNullable, maxLength, precision, scale, isUnicode, Double.class));

    return result;
  }

  protected Point stringToPoint(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    return newPoint(SRID.valueOf(matcher.group(2)), matcher.group(4),
        isNullable, maxLength, precision, scale, isUnicode);
  }

  protected MultiPoint stringToMultiPoint(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    final List<Point> points = new ArrayList<Point>();
    for (final String pointCoo : split(matcher.group(4), ',')) {
      points.add(newPoint(null, pointCoo.substring(1, pointCoo.length() - 1),
          isNullable, maxLength, precision, scale, isUnicode));
    }

    return new MultiPoint(dimension, SRID.valueOf(matcher.group(2)), points);
  }

  private LineString newLineString(final SRID srid, final String lineString, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final List<Point> points = new ArrayList<Point>();
    for (final String pointCoo : split(lineString, ',')) {
      points.add(newPoint(null, pointCoo, isNullable, maxLength, precision, scale, isUnicode));
    }

    return new LineString(this.dimension, srid, points);
  }

  protected LineString stringToLineString(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    return newLineString(SRID.valueOf(matcher.group(2)), matcher.group(4),
        isNullable, maxLength, precision, scale, isUnicode);
  }

  protected MultiLineString stringToMultiLineString(final String value, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    final List<LineString> lineStrings = new ArrayList<LineString>();
    for (String coo : matcher.group(4).contains("),(")
        ? matcher.group(4).split("\\),\\(") : new String[] { matcher.group(4) }) {

      String lineString = coo;
      if (lineString.charAt(0) == '(') {
        lineString = lineString.substring(1);
      }
      if (lineString.endsWith(")")) {
        lineString = lineString.substring(0, lineString.length() - 1);
      }

      lineStrings.add(newLineString(null, lineString, isNullable, maxLength, precision, scale, isUnicode));
    }

    return new MultiLineString(this.dimension, SRID.valueOf(matcher.group(2)), lineStrings);
  }

  private Polygon newPolygon(final SRID srid, final String polygon, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final String[] first = polygon.split("\\),\\(");

    final List<LineString> interiorRings = new ArrayList<LineString>();
    for (int i = 0; i < first.length -1; i++) {
    	List<Point> interior = new ArrayList<Point>();
	    for (final String pointCoo : split(first[i].substring(i==0?1:0, first[i].length()), ',')) {
	      interior.add(newPoint(null, pointCoo, isNullable, maxLength, precision, scale, isUnicode));
	    }
	    interiorRings.add(new LineString(dimension, srid, interior));
    }
    final List<Point> exterior = new ArrayList<Point>();
    for (final String pointCoo : split(first[first.length -1].substring(0, first[first.length -1].length() - 1), ',')) {
      exterior.add(newPoint(null, pointCoo, isNullable, maxLength, precision, scale, isUnicode));
    }

    return new Polygon(dimension, srid, interiorRings, new LineString(dimension, srid, exterior));
  }

  protected Polygon stringToPolygon(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    return newPolygon(SRID.valueOf(matcher.group(2)), matcher.group(4),
        isNullable, maxLength, precision, scale, isUnicode);
  }

  protected MultiPolygon stringToMultiPolygon(final String value, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(PATTERN, value);

    final List<Polygon> polygons = new ArrayList<Polygon>();
    for (String coo : matcher.group(4).contains(")),((") ?
        matcher.group(4).split("\\)\\),\\(\\(") :
        new String[] { matcher.group(4) }) {

      String polygon = coo;
      if (polygon.startsWith("((")) {
        polygon = polygon.substring(1);
      }
      if (polygon.endsWith("))")) {
        polygon = polygon.substring(0, polygon.length() - 1);
      }
      if (polygon.charAt(0) != '(') {
        polygon = "(" + polygon;
      }
      if (!polygon.endsWith(")")) {
        polygon += ")";
      }

      polygons.add(newPolygon(null, polygon, isNullable, maxLength, precision, scale, isUnicode));
    }

    return new MultiPolygon(dimension, SRID.valueOf(matcher.group(2)), polygons);
  }

  protected GeospatialCollection stringToCollection(final String value, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final Matcher matcher = getMatcher(COLLECTION_PATTERN, value);

    Geospatial item = null;
    switch (Geospatial.Type.valueOf(matcher.group(3).toUpperCase())) {
    case POINT:
      item = newPoint(SRID.valueOf(matcher.group(2)), matcher.group(4),
          isNullable, maxLength, precision, scale, isUnicode);
      break;

    case MULTIPOINT:
      final List<Point> points = new ArrayList<Point>();
      for (final String pointCoo : split(matcher.group(4), ',')) {
        points.add(newPoint(null, pointCoo.substring(1, pointCoo.length() - 1),
            isNullable, maxLength, precision, scale, isUnicode));
      }

      item = new MultiPoint(dimension, SRID.valueOf(matcher.group(2)), points);
      break;

    case LINESTRING:
      item = newLineString(SRID.valueOf(matcher.group(2)), matcher.group(4),
          isNullable, maxLength, precision, scale, isUnicode);
      break;

    case MULTILINESTRING:
      final List<LineString> lineStrings = new ArrayList<LineString>();
      for (final String coo : split(matcher.group(4), ',')) {
        lineStrings.add(newLineString(null, coo.substring(1, coo.length() - 1),
            isNullable, maxLength, precision, scale, isUnicode));
      }

      item = new MultiLineString(this.dimension, SRID.valueOf(matcher.group(2)), lineStrings);
      break;

    case POLYGON:
      item = newPolygon(SRID.valueOf(matcher.group(2)), matcher.group(4),
          isNullable, maxLength, precision, scale, isUnicode);
      break;

    case MULTIPOLYGON:
      final List<Polygon> polygons = new ArrayList<Polygon>();
      for (final String coo : split(matcher.group(4), ',')) {
        polygons.add(newPolygon(null, coo.substring(1, coo.length() - 1),
            isNullable, maxLength, precision, scale, isUnicode));
      }

      item = new MultiPolygon(dimension, SRID.valueOf(matcher.group(2)), polygons);
      break;

    default:
    }

    return new GeospatialCollection(dimension, SRID.valueOf(matcher.group(2)),
        Collections.<Geospatial> singletonList(item));
  }

  private StringBuilder toStringBuilder(final SRID srid) {
    return new StringBuilder(dimension.name().toLowerCase()).append('\'').
        append("SRID=").append(srid).append(';');
  }

  private String point(final Point point, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    return new StringBuilder().
        append(EdmDouble.getInstance().valueToString(point.getX(),
            isNullable, maxLength, precision, scale, isUnicode)).
            append(' ').
            append(EdmDouble.getInstance().valueToString(point.getY(),
                isNullable, maxLength, precision, scale, isUnicode)).
                toString();
  }

  protected String toString(final Point point, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != point.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + point + "' is not valid.");
    }

    return toStringBuilder(point.getSrid()).
        append(reference.getSimpleName()).
        append('(').
        append(point(point, isNullable, maxLength, precision, scale, isUnicode)).
        append(")'").
        toString();
  }

  protected String toString(final MultiPoint multiPoint, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != multiPoint.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + multiPoint + "' is not valid.");
    }

    final StringBuilder result = toStringBuilder(multiPoint.getSrid()).
        append(reference.getSimpleName()).
        append('(');

    for (final Iterator<Point> itor = multiPoint.iterator(); itor.hasNext();) {
      result.append('(').
      append(point(itor.next(), isNullable, maxLength, precision, scale, isUnicode)).
      append(')');
      if (itor.hasNext()) {
        result.append(',');
      }
    }

    return result.append(")'").toString();
  }

  private StringBuilder appendPoints(final ComposedGeospatial<Point> points, final Boolean isNullable, 
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode, 
      final StringBuilder result) throws EdmPrimitiveTypeException {
	for (final Iterator<Point> itor = points.iterator(); itor.hasNext();) {
      result.append(point(itor.next(), isNullable, maxLength, precision, scale, isUnicode));
      if (itor.hasNext()) {
        result.append(',');
      }
    }
	return result;
}

  protected String toString(final LineString lineString, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != lineString.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + lineString + "' is not valid.");
    }

    StringBuilder builder = toStringBuilder(lineString.getSrid()).
        append(reference.getSimpleName()).
        append('(');
    return appendPoints(lineString, isNullable, maxLength, precision, scale, isUnicode, builder).
        append(")'").toString();
  }

  protected String toString(final MultiLineString multiLineString, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != multiLineString.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + multiLineString + "' is not valid.");
    }

    final StringBuilder result = toStringBuilder(multiLineString.getSrid()).
        append(reference.getSimpleName()).
        append('(');

    for (final Iterator<LineString> itor = multiLineString.iterator(); itor.hasNext();) {
      result.append('(');
      appendPoints(itor.next(), isNullable, maxLength, precision, scale, isUnicode, result).
      append(')');
      if (itor.hasNext()) {
        result.append(',');
      }
    }

    return result.append(")'").toString();
  }

  private String polygon(final Polygon polygon, final Boolean isNullable,
      final Integer maxLength, final Integer precision, final Integer scale, final Boolean isUnicode)
          throws EdmPrimitiveTypeException {

    final StringBuilder result = new StringBuilder();

    for (int i = 0; i < polygon.getNumberOfInteriorRings(); i++) {
	    result.append('(');
	    appendPoints(polygon.getInterior(i), isNullable, maxLength, precision, scale, isUnicode, result);
	    result.append("),");
    }
    
    result.append('(');
    appendPoints(polygon.getExterior(), isNullable, maxLength, precision, scale, isUnicode, result);

    return result.append(')').toString();
  }

  protected String toString(final Polygon polygon, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != polygon.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + polygon + "' is not valid.");
    }

    return toStringBuilder(polygon.getSrid()).
        append(reference.getSimpleName()).
        append('(').
        append(polygon(polygon, isNullable, maxLength, precision, scale, isUnicode)).
        append(")'").toString();
  }

  protected String toString(final MultiPolygon multiPolygon, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != multiPolygon.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + multiPolygon + "' is not valid.");
    }

    final StringBuilder result = toStringBuilder(multiPolygon.getSrid()).
        append(reference.getSimpleName()).
        append('(');

    for (final Iterator<Polygon> itor = multiPolygon.iterator(); itor.hasNext();) {
      result.append('(').
      append(polygon(itor.next(), isNullable, maxLength, precision, scale, isUnicode)).
      append(')');
      if (itor.hasNext()) {
        result.append(',');
      }
    }

    return result.append(")'").toString();
  }

  protected String toString(final GeospatialCollection collection, final Boolean isNullable, final Integer maxLength,
      final Integer precision, final Integer scale, final Boolean isUnicode) throws EdmPrimitiveTypeException {

    if (dimension != collection.getDimension()) {
      throw new EdmPrimitiveTypeException("The value '" + collection + "' is not valid.");
    }

    StringBuilder result = toStringBuilder(collection.getSrid()).append("Collection(");

    if (collection.iterator().hasNext()) {
      final Geospatial item = collection.iterator().next();
      result.append(item.getClass().getSimpleName()).append('(');

      switch (item.getEdmPrimitiveTypeKind()) {
      case GeographyPoint:
      case GeometryPoint:
        result.append(point((Point) item, isNullable, maxLength, precision, scale, isUnicode));
        break;

      case GeographyMultiPoint:
      case GeometryMultiPoint:
        for (final Iterator<Point> itor = ((MultiPoint) item).iterator(); itor.hasNext();) {
          result.append('(').
          append(point(itor.next(), isNullable, maxLength, precision, scale, isUnicode)).
          append(')');
          if (itor.hasNext()) {
            result.append(',');
          }
        }
        break;

      case GeographyLineString:
      case GeometryLineString:
        appendPoints((LineString) item, isNullable, maxLength, precision, scale, isUnicode, result);
        break;

      case GeographyMultiLineString:
      case GeometryMultiLineString:
        for (final Iterator<LineString> itor = ((MultiLineString) item).iterator(); itor.hasNext();) {
          result.append('(');
          appendPoints(itor.next(), isNullable, maxLength, precision, scale, isUnicode, result).
          append(')');
          if (itor.hasNext()) {
            result.append(',');
          }
        }
        break;

      case GeographyPolygon:
      case GeometryPolygon:
        result.append(polygon((Polygon) item, isNullable, maxLength, precision, scale, isUnicode));
        break;

      case GeographyMultiPolygon:
      case GeometryMultiPolygon:
        for (final Iterator<Polygon> itor = ((MultiPolygon) item).iterator(); itor.hasNext();) {
          result.append('(').
          append(polygon(itor.next(), isNullable, maxLength, precision, scale, isUnicode)).
          append(')');
          if (itor.hasNext()) {
            result.append(',');
          }
        }
        break;

      default:
      }

      result.append(')');
    }

    return result.append(")'").toString();
  }

  private List<String> split(final String input, final char separator) {
    if (input == null) {
      return null;
    }
    List<String> list = new ArrayList<String>();
    int start = 0;
    int end;
    while ((end = input.indexOf(separator, start)) >= 0) {
      list.add(input.substring(start, end));
      start = end + 1;
    }
    if (start < input.length()) {
      list.add(input.substring(start));
    }
    return list;
  }
}
