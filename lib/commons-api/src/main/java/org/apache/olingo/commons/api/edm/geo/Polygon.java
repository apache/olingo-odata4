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
package org.apache.olingo.commons.api.edm.geo;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * Polygon.
 */
public class Polygon extends Geospatial {

  final ComposedGeospatial<LineString> interiorRings;
  final ComposedGeospatial<Point> exterior;

  /**
   * Creates a new polygon.
   * 
   * @param dimension   Dimension of the polygon
   * @param srid        SRID values
   * @param interior    List of interior points
   * @param exterior    List of exterior point
   * @deprecated
   */
  public Polygon(final Dimension dimension, final SRID srid,
      final List<Point> interior, final List<Point> exterior) {

    super(dimension, Type.POLYGON, srid);
    if (interior != null) {
		LineString lineString = new LineString(dimension, srid, interior);
		this.interiorRings = new MultiLineString(dimension, srid, Arrays.asList(lineString));
    } else {
    	this.interiorRings = null;
    }
    this.exterior = new LineString(dimension, srid, exterior);
  }
  
  /**
   * Creates a new polygon.
   * 
   * @param dimension   Dimension of the polygon
   * @param srid        SRID values
   * @param interiors    List of interior rings
   * @param exterior    Ring of exterior point
   */
  public Polygon(final Dimension dimension, final SRID srid,
      final List<LineString> interiors, LineString exterior) {

    super(dimension, Type.POLYGON, srid);
    if (interiors != null) {
    	this.interiorRings = new MultiLineString(dimension, srid, interiors);
    } else {
    	this.interiorRings = null;
    }
    this.exterior = exterior;
  }
  /**
   * Gets interior points.
   *
   * @return interior points.
   * @deprecated
   * @see #getInterior(int)
   */
  public ComposedGeospatial<Point> getInterior() {
	if (interiorRings == null || interiorRings.geospatials.isEmpty()) {
		return null;
	}
    return getInterior(0);
  }
  
  /**
   * Get the number of interior rings
   * @return number of interior rings
   */
  public int getNumberOfInteriorRings() {
	  if (interiorRings == null) {
		  return 0;
	  }
	  return interiorRings.geospatials.size();
  }
  
  /**
   * Gets the nth interior ring
   * @param n
   * @return the ring or an exception if no such ring exists
   */
  public ComposedGeospatial<Point> getInterior(int n) {
	  return interiorRings.geospatials.get(n);
  }

  /**
   * Gets exterior points.
   *
   * @return exterior points.
   */
  public ComposedGeospatial<Point> getExterior() {
    return exterior;
  }

  @Override
  public EdmPrimitiveTypeKind getEdmPrimitiveTypeKind() {
    return dimension == Dimension.GEOGRAPHY ?
        EdmPrimitiveTypeKind.GeographyPolygon :
        EdmPrimitiveTypeKind.GeometryPolygon;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final Polygon polygon = (Polygon) o;
    return dimension == polygon.dimension
        && (srid == null ? polygon.srid == null : srid.equals(polygon.srid))
        && (interiorRings == null ? polygon.interiorRings == null : interiorRings.equals(polygon.interiorRings))
        && (exterior == null ? polygon.exterior == null : exterior.equals(polygon.exterior));
  }

  @Override
  public int hashCode() {
    int result = dimension == null ? 0 : dimension.hashCode();
    result = 31 * result + (srid == null ? 0 : srid.hashCode());
    result = 31 * result + (interiorRings == null ? 0 : interiorRings.hashCode());
    result = 31 * result + (exterior == null ? 0 : exterior.hashCode());
    return result;
  }
}
