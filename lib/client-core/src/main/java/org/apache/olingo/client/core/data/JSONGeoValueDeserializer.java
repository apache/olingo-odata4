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
package org.apache.olingo.client.core.data;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.domain.geospatial.Geospatial;
import org.apache.olingo.client.api.domain.geospatial.GeospatialCollection;
import org.apache.olingo.client.api.domain.geospatial.LineString;
import org.apache.olingo.client.api.domain.geospatial.MultiLineString;
import org.apache.olingo.client.api.domain.geospatial.MultiPoint;
import org.apache.olingo.client.api.domain.geospatial.MultiPolygon;
import org.apache.olingo.client.api.domain.geospatial.Point;
import org.apache.olingo.client.api.domain.geospatial.Polygon;

class JSONGeoValueDeserializer {

  private Point point(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type, final String crs) {
    Point point = null;

    if (itor.hasNext()) {
      point = new Point(GeoUtils.getDimension(type), crs);
      point.setX(Double.valueOf(itor.next().asText()));
      point.setY(Double.valueOf(itor.next().asText()));
    }

    return point;
  }

  private MultiPoint multipoint(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    MultiPoint multiPoint = null;

    if (itor.hasNext()) {
      final List<Point> points = new ArrayList<Point>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        points.add(point(mpItor, type, crs));
      }
      multiPoint = new MultiPoint(GeoUtils.getDimension(type), crs, points);
    } else {
      multiPoint = new MultiPoint(GeoUtils.getDimension(type), crs, Collections.<Point>emptyList());
    }

    return multiPoint;
  }

  private LineString lineString(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    LineString lineString = null;

    if (itor.hasNext()) {
      final List<Point> points = new ArrayList<Point>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        points.add(point(mpItor, type, crs));
      }
      lineString = new LineString(GeoUtils.getDimension(type), crs, points);
    } else {
      lineString = new LineString(GeoUtils.getDimension(type), crs, Collections.<Point>emptyList());
    }

    return lineString;
  }

  private MultiLineString multiLineString(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    MultiLineString multiLineString = null;

    if (itor.hasNext()) {
      final List<LineString> lineStrings = new ArrayList<LineString>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mlsItor = itor.next().elements();
        lineStrings.add(lineString(mlsItor, type, crs));
      }
      multiLineString = new MultiLineString(GeoUtils.getDimension(type), crs, lineStrings);
    } else {
      multiLineString = new MultiLineString(GeoUtils.getDimension(type), crs, Collections.<LineString>emptyList());
    }

    return multiLineString;
  }

  private Polygon polygon(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    List<Point> extPoints = null;
    if (itor.hasNext()) {
      final Iterator<JsonNode> extItor = itor.next().elements();
      if (extItor.hasNext()) {
        extPoints = new ArrayList<Point>();
        while (extItor.hasNext()) {
          final Iterator<JsonNode> mpItor = extItor.next().elements();
          extPoints.add(point(mpItor, type, crs));
        }
      }
    }

    List<Point> intPoints = null;
    if (itor.hasNext()) {
      final Iterator<JsonNode> intItor = itor.next().elements();
      if (intItor.hasNext()) {
        intPoints = new ArrayList<Point>();
        while (intItor.hasNext()) {
          final Iterator<JsonNode> mpItor = intItor.next().elements();
          intPoints.add(point(mpItor, type, crs));
        }
      }
    }

    return new Polygon(GeoUtils.getDimension(type), crs, intPoints, extPoints);
  }

  private MultiPolygon multiPolygon(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    MultiPolygon multiPolygon = null;

    if (itor.hasNext()) {
      final List<Polygon> polygons = new ArrayList<Polygon>();
      while (itor.hasNext()) {
        final Iterator<JsonNode> mpItor = itor.next().elements();
        polygons.add(polygon(mpItor, type, crs));
      }
      multiPolygon = new MultiPolygon(GeoUtils.getDimension(type), crs, polygons);
    } else {
      multiPolygon = new MultiPolygon(GeoUtils.getDimension(type), crs, Collections.<Polygon>emptyList());
    }

    return multiPolygon;
  }

  private GeospatialCollection collection(final Iterator<JsonNode> itor, final ODataJClientEdmPrimitiveType type,
          final String crs) {

    GeospatialCollection collection = null;

    if (itor.hasNext()) {
      final List<Geospatial> geospatials = new ArrayList<Geospatial>();

      while (itor.hasNext()) {
        final JsonNode geo = itor.next();
        final String collItemType = geo.get(Constants.ATTR_TYPE).asText();
        final String callAsType;
        if (ODataJClientEdmPrimitiveType.GeographyCollection.name().equals(collItemType)
                || ODataJClientEdmPrimitiveType.GeometryCollection.name().equals(collItemType)) {

          callAsType = collItemType;
        } else {
          callAsType = (type == ODataJClientEdmPrimitiveType.GeographyCollection ? "Geography" : "Geometry")
                  + collItemType;
        }

        geospatials.add(deserialize(geo, ODataJClientEdmPrimitiveType.valueOf(callAsType)));
      }

      collection = new GeospatialCollection(GeoUtils.getDimension(type), crs, geospatials);
    } else {
      collection = new GeospatialCollection(GeoUtils.getDimension(type), crs, Collections.<Geospatial>emptyList());
    }

    return collection;
  }

  public Geospatial deserialize(final JsonNode node, final ODataJClientEdmPrimitiveType type) {
    final ODataJClientEdmPrimitiveType actualType;
    if ((type == ODataJClientEdmPrimitiveType.Geography || type == ODataJClientEdmPrimitiveType.Geometry)
            && node.has(Constants.ATTR_TYPE)) {

      String nodeType = node.get(Constants.ATTR_TYPE).asText();
      if (nodeType.startsWith("Geo")) {
        final int yIdx = nodeType.indexOf('y');
        nodeType = nodeType.substring(yIdx + 1);
      }
      actualType = ODataJClientEdmPrimitiveType.fromValue(type.toString() + nodeType);
    } else {
      actualType = type;
    }

    final Iterator<JsonNode> cooItor = node.has(Constants.JSON_COORDINATES)
            ? node.get(Constants.JSON_COORDINATES).elements()
            : Collections.<JsonNode>emptyList().iterator();

    String crs = null;
    if (node.has(Constants.JSON_CRS)) {
      crs = node.get(Constants.JSON_CRS).get(Constants.PROPERTIES).get(Constants.NAME).asText().split(":")[1];
    }

    Geospatial value = null;
    switch (actualType) {
      case GeographyPoint:
      case GeometryPoint:
        value = point(cooItor, type, crs);
        break;

      case GeographyMultiPoint:
      case GeometryMultiPoint:
        value = multipoint(cooItor, type, crs);
        break;

      case GeographyLineString:
      case GeometryLineString:
        value = lineString(cooItor, type, crs);
        break;

      case GeographyMultiLineString:
      case GeometryMultiLineString:
        value = multiLineString(cooItor, type, crs);
        break;

      case GeographyPolygon:
      case GeometryPolygon:
        value = polygon(cooItor, type, crs);
        break;

      case GeographyMultiPolygon:
      case GeometryMultiPolygon:
        value = multiPolygon(cooItor, type, crs);
        break;

      case GeographyCollection:
      case GeometryCollection:
        value = collection(node.get(Constants.JSON_GEOMETRIES).elements(), type, crs);
        break;

      default:
    }

    return value;
  }

}
