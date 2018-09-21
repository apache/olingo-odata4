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
package org.apache.olingo.client.core.serialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.GeoUtils;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.apache.olingo.commons.api.edm.geo.SRID;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;

import com.fasterxml.jackson.databind.JsonNode;

class JsonGeoValueDeserializer {

  private Point point(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type, final SRID srid) {
    Point point = null;

    if (itor.hasNext()) {
      point = new Point(GeoUtils.getDimension(type), srid);
      try {
        point.setX(EdmDouble.getInstance().valueOfString(itor.next().asText(), null, null,
            Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null, Double.class));
        point.setY(EdmDouble.getInstance().valueOfString(itor.next().asText(), null, null,
            Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null, Double.class));
      } catch (EdmPrimitiveTypeException e) {
        throw new IllegalArgumentException("While deserializing point coordinates as double", e);
      }
    }

    return point;
  }

  private MultiPoint multipoint(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type, final SRID srid) {
    final MultiPoint multiPoint;

    if (itor.hasNext()) {
      final List<Point> points = new ArrayList<Point>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        points.add(point(mpItor, type, srid));
      }
      multiPoint = new MultiPoint(GeoUtils.getDimension(type), srid, points);
    } else {
      multiPoint = new MultiPoint(GeoUtils.getDimension(type), srid, Collections.<Point> emptyList());
    }

    return multiPoint;
  }

  private LineString lineString(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type, final SRID srid) {
    final LineString lineString;

    if (itor.hasNext()) {
      final List<Point> points = new ArrayList<Point>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        points.add(point(mpItor, type, srid));
      }
      lineString = new LineString(GeoUtils.getDimension(type), srid, points);
    } else {
      lineString = new LineString(GeoUtils.getDimension(type), srid, Collections.<Point> emptyList());
    }

    return lineString;
  }

  private MultiLineString multiLineString(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type,
      final SRID srid) {

    final MultiLineString multiLineString;

    if (itor.hasNext()) {
      final List<LineString> lineStrings = new ArrayList<LineString>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mlsItor = itor.next().elements();
        lineStrings.add(lineString(mlsItor, type, srid));
      }
      multiLineString = new MultiLineString(GeoUtils.getDimension(type), srid, lineStrings);
    } else {
      multiLineString = new MultiLineString(GeoUtils.getDimension(type), srid, Collections.<LineString> emptyList());
    }

    return multiLineString;
  }

  private Polygon polygon(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type, final SRID srid) {
    List<Point> extPoints = null;
    if (itor.hasNext()) {
      final Iterator<JsonNode> extItor = itor.next().elements();
      if (extItor.hasNext()) {
        extPoints = new ArrayList<Point>();
        while (extItor.hasNext()) {
          final Iterator<JsonNode> mpItor = extItor.next().elements();
          extPoints.add(point(mpItor, type, srid));
        }
      }
    }

    List<LineString> intRings = new ArrayList<LineString>();
    while (itor.hasNext()) {
      final Iterator<JsonNode> intItor = itor.next().elements();
      if (intItor.hasNext()) {
        List<Point> intPoints = new ArrayList<Point>();
        while (intItor.hasNext()) {
          final Iterator<JsonNode> mpItor = intItor.next().elements();
          intPoints.add(point(mpItor, type, srid));
        }
        intRings.add(new LineString(GeoUtils.getDimension(type), srid, intPoints));
      }
    }

    LineString exterior = new LineString(GeoUtils.getDimension(type), srid, extPoints);
	return new Polygon(GeoUtils.getDimension(type), srid, intRings, exterior);
  }

  private MultiPolygon multiPolygon(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type, final SRID srid) {
    final MultiPolygon multiPolygon;

    if (itor.hasNext()) {
      final List<Polygon> polygons = new ArrayList<Polygon>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        polygons.add(polygon(mpItor, type, srid));
      }
      multiPolygon = new MultiPolygon(GeoUtils.getDimension(type), srid, polygons);
    } else {
      multiPolygon = new MultiPolygon(GeoUtils.getDimension(type), srid, Collections.<Polygon> emptyList());
    }

    return multiPolygon;
  }

  private GeospatialCollection collection(final Iterator<JsonNode> itor, final EdmPrimitiveTypeKind type,
      final SRID srid) {

    final GeospatialCollection collection;

    if (itor.hasNext()) {
      final List<Geospatial> geospatials = new ArrayList<Geospatial>();

      while (itor.hasNext()) {
        final JsonNode geo = itor.next();
        final String collItemType = geo.get(Constants.ATTR_TYPE).asText();
        final String callAsType;
        if (EdmPrimitiveTypeKind.GeographyCollection.name().equals(collItemType)
            || EdmPrimitiveTypeKind.GeometryCollection.name().equals(collItemType)) {

          callAsType = collItemType;
        } else {
          callAsType = (type == EdmPrimitiveTypeKind.GeographyCollection ? "Geography" : "Geometry")
              + collItemType;
        }

        geospatials.add(deserialize(geo, new EdmTypeInfo.Builder().setTypeExpression(callAsType).build()));
      }

      collection = new GeospatialCollection(GeoUtils.getDimension(type), srid, geospatials);
    } else {
      collection = new GeospatialCollection(GeoUtils.getDimension(type), srid, Collections.<Geospatial> emptyList());
    }

    return collection;
  }

  public Geospatial deserialize(final JsonNode node, final EdmTypeInfo typeInfo) {
    final EdmPrimitiveTypeKind actualType;
    if ((typeInfo.getPrimitiveTypeKind() == EdmPrimitiveTypeKind.Geography
        || typeInfo.getPrimitiveTypeKind() == EdmPrimitiveTypeKind.Geometry)
        && node.has(Constants.ATTR_TYPE)) {

      String nodeType = node.get(Constants.ATTR_TYPE).asText();
      if (nodeType.startsWith("Geo")) {
        final int yIdx = nodeType.indexOf('y');
        nodeType = nodeType.substring(yIdx + 1);
      }
      actualType = EdmPrimitiveTypeKind.valueOfFQN(typeInfo.getFullQualifiedName().toString() + nodeType);
    } else {
      actualType = typeInfo.getPrimitiveTypeKind();
    }

    final Iterator<JsonNode> cooItor = node.has(Constants.JSON_COORDINATES)
        ? node.get(Constants.JSON_COORDINATES).elements()
            : Collections.<JsonNode> emptyList().iterator();

        SRID srid = null;
        if (node.has(Constants.JSON_CRS)) {
          srid = SRID.valueOf(
              node.get(Constants.JSON_CRS).get(Constants.PROPERTIES).get(Constants.JSON_NAME).asText().split(":")[1]);
        }

        Geospatial value = null;
        switch (actualType) {
        case GeographyPoint:
        case GeometryPoint:
          value = point(cooItor, actualType, srid);
          break;

        case GeographyMultiPoint:
        case GeometryMultiPoint:
          value = multipoint(cooItor, actualType, srid);
          break;

        case GeographyLineString:
        case GeometryLineString:
          value = lineString(cooItor, actualType, srid);
          break;

        case GeographyMultiLineString:
        case GeometryMultiLineString:
          value = multiLineString(cooItor, actualType, srid);
          break;

        case GeographyPolygon:
        case GeometryPolygon:
          value = polygon(cooItor, actualType, srid);
          break;

        case GeographyMultiPolygon:
        case GeometryMultiPolygon:
          value = multiPolygon(cooItor, actualType, srid);
          break;

        case GeographyCollection:
        case GeometryCollection:
          value = collection(node.get(Constants.JSON_GEOMETRIES).elements(), actualType, srid);
          break;

        default:
        }

        return value;
  }
}
