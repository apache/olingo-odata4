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
package org.apache.olingo.commons.api.edm.geo;

import java.util.List;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * Polygon.
 */
public class Polygon extends Geospatial {

  private static final long serialVersionUID = 7797602503445391678L;

  final ComposedGeospatial<Point> interior;

  final ComposedGeospatial<Point> exterior;

  public Polygon(final Dimension dimension, final SRID srid, 
          final List<Point> interior, final List<Point> exterior) {
    
    super(dimension, Type.POLYGON, srid);
    this.interior = new MultiPoint(dimension, srid, interior);
    this.exterior = new MultiPoint(dimension, srid, exterior);
  }

  /**
   * Gest interior points.
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
    return dimension == Dimension.GEOGRAPHY
           ? EdmPrimitiveTypeKind.GeographyPolygon
           : EdmPrimitiveTypeKind.GeometryPolygon;
  }
}
