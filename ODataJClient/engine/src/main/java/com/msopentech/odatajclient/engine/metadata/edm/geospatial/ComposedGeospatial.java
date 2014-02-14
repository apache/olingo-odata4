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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract base class for all Geometries that are composed out of other geospatial elements.
 */
public abstract class ComposedGeospatial<T extends Geospatial> extends Geospatial implements Iterable<T> {

    private static final long serialVersionUID = 8796254901098541307L;

    protected final List<T> geospatials;

    /**
     * Constructor.
     *
     * @param dimension dimension.
     * @param type type.
     * @param geospatials geospatials info.
     */
    protected ComposedGeospatial(final Dimension dimension, final Type type, final List<T> geospatials) {
        super(dimension, type);
        this.geospatials = new ArrayList<T>();
        if (geospatials != null) {
            this.geospatials.addAll(geospatials);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Iterator<T> iterator() {
        return this.geospatials.iterator();
    }

    /**
     * Checks if is empty.
     *
     * @return 'TRUE' if is empty; 'FALSE' otherwise.
     */
    public boolean isEmpty() {
        return geospatials.isEmpty();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setSrid(final Integer srid) {
        for (Geospatial geospatial : this.geospatials) {
            geospatial.setSrid(srid);
        }
    }
}
