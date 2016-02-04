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

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * Base class for all geospatial info.
 */
public abstract class Geospatial {

  /**
   * Dimension of the geospatial type
   */
  public enum Dimension {
    /**
     * Geometry type
     */
    GEOMETRY,
    /**
     * Geography type
     */
    GEOGRAPHY
  }

  /**
   * Type of the geospatial type
   */
  public enum Type {

    /**
     * The OGIS geometry type number for points.
     */
    POINT,
    /**
     * The OGIS geometry type number for lines.
     */
    LINESTRING,
    /**
     * The OGIS geometry type number for polygons.
     */
    POLYGON,
    /**
     * The OGIS geometry type number for aggregate points.
     */
    MULTIPOINT,
    /**
     * The OGIS geometry type number for aggregate lines.
     */
    MULTILINESTRING,
    /**
     * The OGIS geometry type number for aggregate polygons.
     */
    MULTIPOLYGON,
    /**
     * The OGIS geometry type number for feature collections.
     */
    GEOSPATIALCOLLECTION
  }

  protected final Dimension dimension;

  protected final Type type;

  /**
   * Null value means it is expected to vary per instance.
   */
  protected final SRID srid;

  /**
   * Constructor.
   *
   * @param dimension dimension
   * @param type type
   * @param srid SRID
   */
  protected Geospatial(final Dimension dimension, final Type type, final SRID srid) {
    this.dimension = dimension;
    this.type = type;
    this.srid = srid == null ? new SRID() : srid;
    this.srid.setDimension(dimension);
  }

  /**
   * Gets dimension.
   *
   * @return dimension.
   * @see Dimension
   */
  public Dimension getDimension() {
    return dimension;
  }

  /**
   * Gets type.
   *
   * @return type.
   * @see Type
   */
  public Type getGeoType() {
    return type;
  }

  /**
   * Gets s-rid.
   *
   * @return s-rid.
   */
  public SRID getSrid() {
    return srid;
  }

  /**
   * Returns the {@link EdmPrimitiveTypeKind}
   * @return EDM primitive type kind
   */
  public abstract EdmPrimitiveTypeKind getEdmPrimitiveTypeKind();
}
