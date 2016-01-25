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
 * Represents a collection of polygons.
 * Either Edm.GeographyMultiPolygon or Edm.GeometryMultiPolygon
 */
public class MultiPolygon extends ComposedGeospatial<Polygon> {

  /**
   * Creates a collection of polygons
   * 
   * @param dimension   Dimension of the polygons
   * @param srid        SRID value
   * @param polygons    List of polygons
   */
  public MultiPolygon(final Dimension dimension, final SRID srid, final List<Polygon> polygons) {
    super(dimension, Type.MULTIPOLYGON, srid, polygons);
  }

  @Override
  public EdmPrimitiveTypeKind getEdmPrimitiveTypeKind() {
    return dimension == Dimension.GEOGRAPHY ?
        EdmPrimitiveTypeKind.GeographyMultiPolygon :
        EdmPrimitiveTypeKind.GeometryMultiPolygon;
  }
}
