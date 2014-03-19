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
package org.apache.olingo.client.api.domain.geospatial;

import java.util.List;

import org.apache.olingo.client.api.domain.ODataJClientEdmPrimitiveType;

/**
 * Wrapper for a collection of geospatials info.
 */
public class GeospatialCollection extends ComposedGeospatial<Geospatial> {

  private static final long serialVersionUID = -9181547636133878977L;

  /**
   * Constructor.
   *
   * @param dimension dimension.
   * @param crs crs.
   * @param geospatials geospatials info.
   */
  public GeospatialCollection(final Dimension dimension, final String crs, final List<Geospatial> geospatials) {
    super(dimension, Type.GEOSPATIALCOLLECTION, crs, geospatials);
  }

  @Override
  public ODataJClientEdmPrimitiveType getEdmSimpleType() {
    return dimension == Dimension.GEOGRAPHY
            ? ODataJClientEdmPrimitiveType.GeographyCollection
            : ODataJClientEdmPrimitiveType.GeometryCollection;
  }
}
