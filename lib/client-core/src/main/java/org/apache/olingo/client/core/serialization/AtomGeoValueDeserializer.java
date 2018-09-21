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
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.StringUtils;
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
import org.apache.olingo.commons.core.edm.primitivetype.EdmDouble;

class AtomGeoValueDeserializer {

  private List<Point> points(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    final List<Point> result = new ArrayList<Point>();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isCharacters() && !event.asCharacters().isWhiteSpace()) {
        final String[] pointInfo = event.asCharacters().getData().split(" ");

        final Point point = new Point(GeoUtils.getDimension(type), srid);
        try {
          point.setX(EdmDouble.getInstance().valueOfString(pointInfo[0], null, null,
              Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null, Double.class));
          point.setY(EdmDouble.getInstance().valueOfString(pointInfo[1], null, null,
              Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null, Double.class));
        } catch (EdmPrimitiveTypeException e) {
          throw new XMLStreamException("While deserializing point coordinates as double", e);
        }
        result.add(point);
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    // handles bad input, e.g. things like <gml:pos/>
    if (result.isEmpty()) {
      result.add(new Point(GeoUtils.getDimension(type), srid));
    }

    return result;
  }

  private MultiPoint multipoint(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    List<Point> points = Collections.<Point> emptyList();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(Constants.QNAME_POINTMEMBERS)) {
        points = points(reader, event.asStartElement(), type, null);
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return new MultiPoint(GeoUtils.getDimension(type), srid, points);
  }

  private LineString lineString(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    return new LineString(GeoUtils.getDimension(type), srid, points(reader, start, type, null));
  }

  private Polygon polygon(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

	LineString extPoints = null;
    List<LineString> intRings = new ArrayList<LineString>();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement()) {
        if (event.asStartElement().getName().equals(Constants.QNAME_POLYGON_EXTERIOR)) {
          List<Point> points = points(reader, event.asStartElement(), type, null);
          extPoints = new LineString(GeoUtils.getDimension(type), srid, points);
        }
        if (event.asStartElement().getName().equals(Constants.QNAME_POLYGON_INTERIOR)) {
          List<Point> points = points(reader, event.asStartElement(), type, null);
          intRings.add(new LineString(GeoUtils.getDimension(type), srid, points));
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return new Polygon(GeoUtils.getDimension(type), srid, intRings, extPoints);
  }

  private MultiLineString multiLineString(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    final List<LineString> lineStrings = new ArrayList<LineString>();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(Constants.QNAME_LINESTRING)) {
        lineStrings.add(lineString(reader, event.asStartElement(), type, null));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return new MultiLineString(GeoUtils.getDimension(type), srid, lineStrings);
  }

  private MultiPolygon multiPolygon(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    final List<Polygon> polygons = new ArrayList<Polygon>();

    boolean foundEndProperty = false;
    while (reader.hasNext() && !foundEndProperty) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(Constants.QNAME_POLYGON)) {
        polygons.add(polygon(reader, event.asStartElement(), type, null));
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndProperty = true;
      }
    }

    return new MultiPolygon(GeoUtils.getDimension(type), srid, polygons);
  }

  private GeospatialCollection collection(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type, final SRID srid) throws XMLStreamException {

    final List<Geospatial> geospatials = new ArrayList<Geospatial>();

    boolean foundEndCollection = false;
    while (reader.hasNext() && !foundEndCollection) {
      final XMLEvent event = reader.nextEvent();

      if (event.isStartElement() && event.asStartElement().getName().equals(Constants.QNAME_GEOMEMBERS)) {
        boolean foundEndMembers = false;
        while (reader.hasNext() && !foundEndMembers) {
          final XMLEvent subevent = reader.nextEvent();

          if (subevent.isStartElement()) {
            geospatials.add(deserialize(reader, subevent.asStartElement(),
                GeoUtils.getType(GeoUtils.getDimension(type), subevent.asStartElement().getName().getLocalPart())));
          }

          if (subevent.isEndElement() && Constants.QNAME_GEOMEMBERS.equals(subevent.asEndElement().getName())) {
            foundEndMembers = true;
          }
        }
      }

      if (event.isEndElement() && start.getName().equals(event.asEndElement().getName())) {
        foundEndCollection = true;
      }
    }

    return new GeospatialCollection(GeoUtils.getDimension(type), srid, geospatials);
  }

  public Geospatial deserialize(final XMLEventReader reader, final StartElement start,
      final EdmPrimitiveTypeKind type) throws XMLStreamException {

    SRID srid = null;
    final Attribute srsName = start.getAttributeByName(Constants.QNAME_ATTR_SRSNAME);
    if (srsName != null) {
      srid = SRID.valueOf(StringUtils.substringAfterLast(srsName.getValue(), "/"));
    }

    Geospatial value;

    switch (type) {
    case GeographyPoint:
    case GeometryPoint:
      value = points(reader, start, type, srid).get(0);
      break;

    case GeographyMultiPoint:
    case GeometryMultiPoint:
      value = multipoint(reader, start, type, srid);
      break;

    case GeographyLineString:
    case GeometryLineString:
      value = lineString(reader, start, type, srid);
      break;

    case GeographyMultiLineString:
    case GeometryMultiLineString:
      value = multiLineString(reader, start, type, srid);
      break;

    case GeographyPolygon:
    case GeometryPolygon:
      value = polygon(reader, start, type, srid);
      break;

    case GeographyMultiPolygon:
    case GeometryMultiPolygon:
      value = multiPolygon(reader, start, type, srid);
      break;

    case GeographyCollection:
    case GeometryCollection:
      value = collection(reader, start, type, srid);
      break;

    default:
      value = null;
    }

    return value;
  }

}
