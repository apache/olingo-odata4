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
package org.apache.olingo.client.api.utils;

import org.apache.olingo.client.api.Constants;
import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;
import org.apache.olingo.client.api.domain.geospatial.Geospatial;

public final class GeoUtils {

  private GeoUtils() {
    // Empty private constructor for static utility classes       
  }

  public static Geospatial.Dimension getDimension(final ODataJClientEdmPrimitiveType type) {
    Geospatial.Dimension dimension;

    switch (type) {
      case Geography:
      case GeographyCollection:
      case GeographyLineString:
      case GeographyMultiLineString:
      case GeographyPoint:
      case GeographyMultiPoint:
      case GeographyPolygon:
      case GeographyMultiPolygon:
        dimension = Geospatial.Dimension.GEOGRAPHY;
        break;

      default:
        dimension = Geospatial.Dimension.GEOMETRY;
    }

    return dimension;
  }

  public static ODataJClientEdmPrimitiveType getType(final Geospatial.Dimension dimension, final String elementName) {
    ODataJClientEdmPrimitiveType type = null;

    if (Constants.ELEM_POINT.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyPoint
              : ODataJClientEdmPrimitiveType.GeometryPoint;
    } else if (Constants.ELEM_MULTIPOINT.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyMultiPoint
              : ODataJClientEdmPrimitiveType.GeometryMultiPoint;
    } else if (Constants.ELEM_LINESTRING.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyLineString
              : ODataJClientEdmPrimitiveType.GeometryLineString;
    } else if (Constants.ELEM_MULTILINESTRING.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyMultiLineString
              : ODataJClientEdmPrimitiveType.GeometryMultiLineString;
    } else if (Constants.ELEM_POLYGON.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyPolygon
              : ODataJClientEdmPrimitiveType.GeometryPolygon;
    } else if (Constants.ELEM_MULTIPOLYGON.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyMultiPolygon
              : ODataJClientEdmPrimitiveType.GeometryMultiPolygon;
    } else if (Constants.ELEM_GEOCOLLECTION.equals(elementName)
            || Constants.ELEM_GEOMEMBERS.equals(elementName)) {

      type = dimension == Geospatial.Dimension.GEOGRAPHY
              ? ODataJClientEdmPrimitiveType.GeographyCollection
              : ODataJClientEdmPrimitiveType.GeometryCollection;
    }

    return type;
  }
}
