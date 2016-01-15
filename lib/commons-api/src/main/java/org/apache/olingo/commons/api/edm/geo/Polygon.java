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

import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * Polygon.
 */
public class Polygon extends Geospatial {

  final ComposedGeospatial<Point> interior;
  final ComposedGeospatial<Point> exterior;

  /**
   * Creates a new polygon.
   * 
   * @param dimension   Dimension of the polygon
   * @param srid        SRID values
   * @param interior    List of interior points
   * @param exterior    List of exterior point
   */
  public Polygon(final Dimension dimension, final SRID srid,
      final List<Point> interior, final List<Point> exterior) {

    super(dimension, Type.POLYGON, srid);
    this.interior = new MultiPoint(dimension, srid, interior);
    this.exterior = new MultiPoint(dimension, srid, exterior);
  }

  /**
   * Gets interior points.
   *
   * @return interior points.
   */
  public ComposedGeospatial<Point> getInterior() {
    return interior;
  }

  /**
   * Gets exterior points.
   *
   * @return exterior points.I
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
        && (interior == null ? polygon.interior == null : interior.equals(polygon.interior))
        && (exterior == null ? polygon.exterior == null : exterior.equals(polygon.exterior));
  }

  @Override
  public int hashCode() {
    int result = dimension == null ? 0 : dimension.hashCode();
    result = 31 * result + (srid == null ? 0 : srid.hashCode());
    result = 31 * result + (interior == null ? 0 : interior.hashCode());
    result = 31 * result + (exterior == null ? 0 : exterior.hashCode());
    return result;
  }
}
