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
package com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types;

import com.msopentech.odatajclient.proxy.api.annotations.Namespace;
import com.msopentech.odatajclient.proxy.api.annotations.ComplexType;
import com.msopentech.odatajclient.proxy.api.annotations.Property;
import com.msopentech.odatajclient.proxy.api.AbstractComplexType;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.*;
import com.msopentech.odatajclient.proxy.opentypeservice.microsoft.test.odata.services.opentypesservice.types.*;

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

@Namespace("Microsoft.Test.OData.Services.OpenTypesService")
@ComplexType("ContactDetails")
public class ContactDetails extends AbstractComplexType {

    private byte[] _firstContacted;

    @Property(name = "FirstContacted", type = "Edm.Binary", nullable = true)
    public byte[] getFirstContacted() {
        return _firstContacted;
    }

    public void setFirstContacted(final byte[] _firstContacted) {
        this._firstContacted = _firstContacted;
    }
    private ODataTimestamp _lastContacted;

    @Property(name = "LastContacted", type = "Edm.DateTimeOffset", nullable = false)
    public ODataTimestamp getLastContacted() {
        return _lastContacted;
    }

    public void setLastContacted(final ODataTimestamp _lastContacted) {
        this._lastContacted = _lastContacted;
    }
    private ODataTimestamp _contacted;

    @Property(name = "Contacted", type = "Edm.DateTime", nullable = false)
    public ODataTimestamp getContacted() {
        return _contacted;
    }

    public void setContacted(final ODataTimestamp _contacted) {
        this._contacted = _contacted;
    }
    private UUID _gUID;

    @Property(name = "GUID", type = "Edm.Guid", nullable = false)
    public UUID getGUID() {
        return _gUID;
    }

    public void setGUID(final UUID _gUID) {
        this._gUID = _gUID;
    }
    private ODataDuration _preferedContactTime;

    @Property(name = "PreferedContactTime", type = "Edm.Time", nullable = false)
    public ODataDuration getPreferedContactTime() {
        return _preferedContactTime;
    }

    public void setPreferedContactTime(final ODataDuration _preferedContactTime) {
        this._preferedContactTime = _preferedContactTime;
    }
    private Integer _byte;

    @Property(name = "Byte", type = "Edm.Byte", nullable = false)
    public Integer getByte() {
        return _byte;
    }

    public void setByte(final Integer _byte) {
        this._byte = _byte;
    }
    private Byte _signedByte;

    @Property(name = "SignedByte", type = "Edm.SByte", nullable = false)
    public Byte getSignedByte() {
        return _signedByte;
    }

    public void setSignedByte(final Byte _signedByte) {
        this._signedByte = _signedByte;
    }
    private Double _double;

    @Property(name = "Double", type = "Edm.Double", nullable = false)
    public Double getDouble() {
        return _double;
    }

    public void setDouble(final Double _double) {
        this._double = _double;
    }
    private Float _single;

    @Property(name = "Single", type = "Edm.Single", nullable = false)
    public Float getSingle() {
        return _single;
    }

    public void setSingle(final Float _single) {
        this._single = _single;
    }
    private Short _short;

    @Property(name = "Short", type = "Edm.Int16", nullable = false)
    public Short getShort() {
        return _short;
    }

    public void setShort(final Short _short) {
        this._short = _short;
    }
    private Integer _int;

    @Property(name = "Int", type = "Edm.Int32", nullable = false)
    public Integer getInt() {
        return _int;
    }

    public void setInt(final Integer _int) {
        this._int = _int;
    }
    private Long _long;

    @Property(name = "Long", type = "Edm.Int64", nullable = false)
    public Long getLong() {
        return _long;
    }

    public void setLong(final Long _long) {
        this._long = _long;
    }
}
