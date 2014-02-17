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
package com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types;

import com.msopentech.odatajclient.proxy.api.annotations.Namespace;
import com.msopentech.odatajclient.proxy.api.annotations.ComplexType;
import com.msopentech.odatajclient.proxy.api.annotations.Property;
import com.msopentech.odatajclient.proxy.api.AbstractComplexType;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.*;
import com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

// EdmSimpleType property imports
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;

@Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@ComplexType("ComplexWithAllPrimitiveTypes")
public class ComplexWithAllPrimitiveTypes extends AbstractComplexType {

    private byte[] _binary;

    @Property(name = "Binary", type = "Edm.Binary", nullable = true)
    public byte[] getBinary() {
        return _binary;
    }

    public void setBinary(final byte[] _binary) {
        this._binary = _binary;
    }
    private Boolean _boolean;

    @Property(name = "Boolean", type = "Edm.Boolean", nullable = false)
    public Boolean getBoolean() {
        return _boolean;
    }

    public void setBoolean(final Boolean _boolean) {
        this._boolean = _boolean;
    }
    private Integer _byte;

    @Property(name = "Byte", type = "Edm.Byte", nullable = false)
    public Integer getByte() {
        return _byte;
    }

    public void setByte(final Integer _byte) {
        this._byte = _byte;
    }
    private ODataTimestamp _dateTime;

    @Property(name = "DateTime", type = "Edm.DateTime", nullable = false)
    public ODataTimestamp getDateTime() {
        return _dateTime;
    }

    public void setDateTime(final ODataTimestamp _dateTime) {
        this._dateTime = _dateTime;
    }
    private BigDecimal _decimal;

    @Property(name = "Decimal", type = "Edm.Decimal", nullable = false)
    public BigDecimal getDecimal() {
        return _decimal;
    }

    public void setDecimal(final BigDecimal _decimal) {
        this._decimal = _decimal;
    }
    private Double _double;

    @Property(name = "Double", type = "Edm.Double", nullable = false)
    public Double getDouble() {
        return _double;
    }

    public void setDouble(final Double _double) {
        this._double = _double;
    }
    private Short _int16;

    @Property(name = "Int16", type = "Edm.Int16", nullable = false)
    public Short getInt16() {
        return _int16;
    }

    public void setInt16(final Short _int16) {
        this._int16 = _int16;
    }
    private Integer _int32;

    @Property(name = "Int32", type = "Edm.Int32", nullable = false)
    public Integer getInt32() {
        return _int32;
    }

    public void setInt32(final Integer _int32) {
        this._int32 = _int32;
    }
    private Long _int64;

    @Property(name = "Int64", type = "Edm.Int64", nullable = false)
    public Long getInt64() {
        return _int64;
    }

    public void setInt64(final Long _int64) {
        this._int64 = _int64;
    }
    private Byte _sByte;

    @Property(name = "SByte", type = "Edm.SByte", nullable = false)
    public Byte getSByte() {
        return _sByte;
    }

    public void setSByte(final Byte _sByte) {
        this._sByte = _sByte;
    }
    private String _string;

    @Property(name = "String", type = "Edm.String", nullable = true)
    public String getString() {
        return _string;
    }

    public void setString(final String _string) {
        this._string = _string;
    }
    private Float _single;

    @Property(name = "Single", type = "Edm.Single", nullable = false)
    public Float getSingle() {
        return _single;
    }

    public void setSingle(final Float _single) {
        this._single = _single;
    }
    private Point _geographyPoint;

    @Property(name = "GeographyPoint", type = "Edm.GeographyPoint", nullable = true)
    public Point getGeographyPoint() {
        return _geographyPoint;
    }

    public void setGeographyPoint(final Point _geographyPoint) {
        this._geographyPoint = _geographyPoint;
    }
    private Point _geometryPoint;

    @Property(name = "GeometryPoint", type = "Edm.GeometryPoint", nullable = true)
    public Point getGeometryPoint() {
        return _geometryPoint;
    }

    public void setGeometryPoint(final Point _geometryPoint) {
        this._geometryPoint = _geometryPoint;
    }
}
