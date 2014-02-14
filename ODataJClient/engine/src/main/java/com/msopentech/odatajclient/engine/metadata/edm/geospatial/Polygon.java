/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.metadata.edm.geospatial;

import com.msopentech.odatajclient.engine.metadata.edm.EdmSimpleType;
import java.util.List;

/**
 * Polygon.
 */
public class Polygon extends Geospatial {

    private static final long serialVersionUID = 7797602503445391678L;

    final ComposedGeospatial<Point> interior;

    final ComposedGeospatial<Point> exterior;

    /**
     * Constructor.
     *
     * @param dimension dimension.
     * @param interior interior points.
     * @param exterior exterior points.
     */
    public Polygon(final Dimension dimension, final List<Point> interior, final List<Point> exterior) {
        super(dimension, Type.POLYGON);
        this.interior = new MultiPoint(dimension, interior);
        this.exterior = new MultiPoint(dimension, exterior);
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
    public EdmSimpleType getEdmSimpleType() {
        return dimension == Dimension.GEOGRAPHY
                ? EdmSimpleType.GeographyPolygon
                : EdmSimpleType.GeometryPolygon;
    }
}
