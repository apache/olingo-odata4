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
package org.apache.olingo.commons.api.data;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;

/**
 * Utilities class for Geography data types.
 */
public final class GeoUtils {

  /**
   * Get dimension based on given Geography / Geometry type.
   *
   * @param type a geography / geometry type
   * @return dimension according to given geography / geometry type
   */
  public static Geospatial.Dimension getDimension(final EdmPrimitiveTypeKind type) {
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

  /**
   * Get type based on given dimension (Geography / Geometry) and element name.
   *
   * @param dimension either geography or geometry
   * @param elementName Element of return. Can be one of the following constants 
   *        <ul>
   *           <li>{@link Constants#ELEM_POINT}</li>
   *           <li>{@link Constants#ELEM_MULTIPOINT}</li>
   *           <li>{@link Constants#ELEM_LINESTRING}</li>
   *           <li>{@link Constants#ELEM_MULTILINESTRING}</li> 
   *           <li>{@link Constants#ELEM_POLYGON}</li>
   *           <li>{@link Constants#ELEM_MULTIPOLYGON}</li>
   *           <li>{@link Constants#ELEM_GEOCOLLECTION}</li>
   *        </ul>
   * @return elementName name of type
   */
  public static EdmPrimitiveTypeKind getType(final Geospatial.Dimension dimension, final String elementName) {
    EdmPrimitiveTypeKind type = null;

    if (Constants.ELEM_POINT.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyPoint
              : EdmPrimitiveTypeKind.GeometryPoint;
    } else if (Constants.ELEM_MULTIPOINT.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyMultiPoint
              : EdmPrimitiveTypeKind.GeometryMultiPoint;
    } else if (Constants.ELEM_LINESTRING.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyLineString
              : EdmPrimitiveTypeKind.GeometryLineString;
    } else if (Constants.ELEM_MULTILINESTRING.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyMultiLineString
              : EdmPrimitiveTypeKind.GeometryMultiLineString;
    } else if (Constants.ELEM_POLYGON.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyPolygon
              : EdmPrimitiveTypeKind.GeometryPolygon;
    } else if (Constants.ELEM_MULTIPOLYGON.equals(elementName)) {
      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyMultiPolygon
              : EdmPrimitiveTypeKind.GeometryMultiPolygon;
    } else if (Constants.ELEM_GEOCOLLECTION.equals(elementName)
        || Constants.ELEM_GEOMEMBERS.equals(elementName)) {

      type = dimension == Geospatial.Dimension.GEOGRAPHY
          ? EdmPrimitiveTypeKind.GeographyCollection
              : EdmPrimitiveTypeKind.GeometryCollection;
    }

    return type;
  }

  private GeoUtils() {
    // Empty private constructor for static utility classes
  }

}
