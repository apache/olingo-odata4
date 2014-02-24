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
package org.apache.olingo.odata4.client.api.data.geospatial;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.olingo.odata4.client.api.data.EdmSimpleType;

/**
 * Base class for all geospatial info.
 */
public abstract class Geospatial implements Serializable {

    public enum Dimension {

        GEOMETRY,
        GEOGRAPHY;

    }

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
        GEOSPATIALCOLLECTION;

    }

    protected final Dimension dimension;

    protected final Type type;

    /**
     * Null value means it is expected to vary per instance.
     */
    protected Integer srid;

    /**
     * Constructor.
     *
     * @param dimension dimension.
     * @param type type.
     */
    protected Geospatial(final Dimension dimension, final Type type) {
        this.dimension = dimension;
        this.type = type;
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
    public Type getType() {
        return type;
    }

    /**
     * Gets s-rid.
     *
     * @return s-rid.
     */
    public Integer getSrid() {
        return srid;
    }

    /**
     * Sets s-rid.
     *
     * @param srid s-rid.
     */
    public void setSrid(final Integer srid) {
        this.srid = srid;
    }

    public abstract EdmSimpleType getEdmSimpleType();

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
