/**
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
package com.msopentech.odatajclient.engine.metadata.edm;

import com.msopentech.odatajclient.engine.data.ODataDuration;
import com.msopentech.odatajclient.engine.data.ODataTimestamp;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Geospatial;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.GeospatialCollection;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.LineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiLineString;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPoint;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.MultiPolygon;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Point;
import com.msopentech.odatajclient.engine.metadata.edm.geospatial.Polygon;
import com.msopentech.odatajclient.engine.utils.ODataVersion;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;

/**
 * Represent the primitive types of the Entity Data Model (EDM).
 *
 * @see http://dl.windowsazure.com/javadoc/com/microsoft/windowsazure/services/table/models/EdmType.html
 * <p>
 * For an overview of the available EDM primitive data types and names, see the <a
 * href="http://www.odata.org/developers/protocols/overview#AbstractTypeSystem">Primitive Data Types</a> section of the
 * <a href="http://www.odata.org/developers/protocols/overview">OData Protocol Overview</a>.
 * </p>
 * <p>
 * The Abstract Type System used to define the primitive types supported by OData is defined in detail in <a
 * href="http://msdn.microsoft.com/en-us/library/dd541474.aspx">[MC-CSDL] (section 2.2.1).</a>
 * </p>
 */
public enum EdmSimpleType {

    /**
     * The absence of a value.
     */
    Null(Void.class),
    /**
     * An array of bytes.
     */
    Binary(byte[].class),
    /**
     * A Boolean value.
     */
    Boolean(Boolean.class),
    /**
     * Unsigned 8-bit integer value.
     */
    Byte(Integer.class),
    /**
     * A signed 8-bit integer value.
     */
    SByte(Byte.class),
    /**
     * A 64-bit value expressed as Coordinated Universal Time (UTC).
     */
    DateTime(new ODataVersion[] { ODataVersion.V3 }, ODataTimestamp.class, "yyyy-MM-dd'T'HH:mm:ss"),
    /**
     * Date without a time-zone offset.
     */
    Date(new ODataVersion[] { ODataVersion.V4 }, ODataTimestamp.class, "yyyy-MM-dd"),
    /**
     * Date and time as an Offset in minutes from GMT.
     */
    DateTimeOffset(ODataTimestamp.class, "yyyy-MM-dd'T'HH:mm:ss"),
    /**
     * The time of day with values ranging from 0:00:00.x to 23:59:59.y, where x and y depend upon the precision.
     */
    Time(new ODataVersion[] { ODataVersion.V3 }, ODataDuration.class),
    /**
     * The time of day with values ranging from 0:00:00.x to 23:59:59.y, where x and y depend upon the precision.
     */
    TimeOfDay(new ODataVersion[] { ODataVersion.V4 }, ODataDuration.class),
    /**
     * Signed duration in days, hours, minutes, and (sub)seconds.
     */
    Duration(new ODataVersion[] { ODataVersion.V4 }, ODataDuration.class),
    /**
     * Numeric values with fixed precision and scale.
     */
    Decimal(BigDecimal.class, "#.#######################"),
    /**
     * A floating point number with 7 digits precision.
     */
    Single(Float.class, "#.#######E0"),
    /**
     * A 64-bit double-precision floating point value.
     */
    Double(Double.class, "#.#######################E0"),
    // --- Geospatial ---
    Geography(Geospatial.class),
    GeographyPoint(Point.class),
    GeographyLineString(LineString.class),
    GeographyPolygon(Polygon.class),
    GeographyMultiPoint(MultiPoint.class),
    GeographyMultiLineString(MultiLineString.class),
    GeographyMultiPolygon(MultiPolygon.class),
    GeographyCollection(GeospatialCollection.class),
    Geometry(Geospatial.class),
    GeometryPoint(Point.class),
    GeometryLineString(LineString.class),
    GeometryPolygon(Polygon.class),
    GeometryMultiPoint(MultiPoint.class),
    GeometryMultiLineString(MultiLineString.class),
    GeometryMultiPolygon(MultiPolygon.class),
    GeometryCollection(GeospatialCollection.class),
    /**
     * A 128-bit globally unique identifier.
     */
    Guid(UUID.class),
    /**
     * A 16-bit integer value.
     */
    Int16(Short.class),
    /**
     * A 32-bit integer value.
     */
    Int32(Integer.class),
    /**
     * A 64-bit integer value.
     */
    Int64(Long.class),
    /**
     * A UTF-16-encoded value.
     * String values may be up to 64 KB in size.
     */
    String(String.class),
    /**
     * Resource stream (for media entities).
     */
    Stream(URI.class);

    private final Class<?> clazz;

    private final String pattern;

    private final ODataVersion[] versions;

    /**
     * Constructor (all OData versions).
     *
     * @param clazz type.
     */
    EdmSimpleType(final Class<?> clazz) {
        this(ODataVersion.values(), clazz, null);
    }

    /**
     * Constructor.
     *
     * @param versions supported OData versions.
     * @param clazz type.
     */
    EdmSimpleType(final ODataVersion[] versions, final Class<?> clazz) {
        this(versions, clazz, null);
    }

    /**
     * Constructor (all OData versions).
     *
     * @param clazz type.
     * @param pattern pattern.
     */
    EdmSimpleType(final Class<?> clazz, final String pattern) {
        this(ODataVersion.values(), clazz, pattern);
    }

    /**
     * Constructor.
     *
     * @param versions supported OData versions.
     * @param clazz type.
     * @param pattern pattern.
     */
    EdmSimpleType(final ODataVersion[] versions, final Class<?> clazz, final String pattern) {
        this.clazz = clazz;
        this.pattern = pattern;
        this.versions = versions.clone();
    }

    /**
     * Gets pattern.
     *
     * @return pattern.
     */
    public String pattern() {
        return pattern;
    }

    /**
     * Gets corresponding java type.
     *
     * @return java type.
     */
    public Class<?> javaType() {
        return this.clazz;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String toString() {
        return namespace() + "." + name();
    }

    /**
     * Checks if is a geospatial type.
     *
     * @return <tt>true</tt> if is geospatial type; <tt>false</tt> otherwise.
     */
    public boolean isGeospatial() {
        return name().startsWith("Geo");
    }

    /**
     * Checks if the given type is a geospatial type.
     *
     * @param type type.
     * @return <tt>true</tt> if is geospatial type; <tt>false</tt> otherwise.
     */
    public static boolean isGeospatial(final String type) {
        return type != null && type.startsWith(namespace() + ".Geo");
    }

    /**
     * Gets <tt>EdmSimpleType</tt> from string.
     *
     * @param value string value type.
     * @return <tt>EdmSimpleType</tt> object.
     */
    public static EdmSimpleType fromValue(final String value) {
        final String noNsValue = value.substring(4);
        for (EdmSimpleType edmSimpleType : EdmSimpleType.values()) {
            if (edmSimpleType.name().equals(noNsValue)) {
                return edmSimpleType;
            }
        }
        throw new IllegalArgumentException(value);
    }

    /**
     * Gets <tt>EdmSimpleType</tt> from object instance.
     *
     * @param workingVersion OData version.
     * @param obj object.
     * @return <tt>EdmSimpleType</tt> object.
     */
    public static EdmSimpleType fromObject(final ODataVersion workingVersion, final Object obj) {
        for (EdmSimpleType edmSimpleType : EdmSimpleType.values()) {
            if (edmSimpleType.javaType().equals(obj.getClass())) {
                return edmSimpleType == DateTimeOffset || edmSimpleType == DateTime || edmSimpleType == Date
                        ? ((ODataTimestamp) obj).isOffset()
                        ? DateTimeOffset : workingVersion == ODataVersion.V3 ? DateTime : Date
                        : edmSimpleType;
            }
        }
        throw new IllegalArgumentException(obj.getClass().getSimpleName() + " is not a simple type");
    }

    /**
     * Gets namespace.
     *
     * @return namespace.
     */
    public static String namespace() {
        return "Edm";
    }

    public ODataVersion[] getSupportedVersions() {
        return versions.clone();
    }
}
