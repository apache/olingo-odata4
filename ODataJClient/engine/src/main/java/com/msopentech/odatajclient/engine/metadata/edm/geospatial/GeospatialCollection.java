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
 * Wrapper for a collection of geospatials info.
 */
public class GeospatialCollection extends ComposedGeospatial<Geospatial> {

    private static final long serialVersionUID = -9181547636133878977L;

    /**
     * Constructor.
     *
     * @param dimension dimension.
     * @param geospatials geospatials info.
     */
    public GeospatialCollection(final Dimension dimension, final List<Geospatial> geospatials) {
        super(dimension, Type.GEOSPATIALCOLLECTION, geospatials);
    }

    @Override
    public EdmSimpleType getEdmSimpleType() {
        return dimension == Dimension.GEOGRAPHY
                ? EdmSimpleType.GeographyCollection
                : EdmSimpleType.GeometryCollection;
    }
}
