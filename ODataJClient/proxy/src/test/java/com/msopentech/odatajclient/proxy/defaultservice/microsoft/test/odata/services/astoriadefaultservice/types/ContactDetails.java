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
@ComplexType("ContactDetails")
public class ContactDetails extends AbstractComplexType {

    private Collection<String> _emailBag;

    @Property(name = "EmailBag", type = "Collection(Edm.String)", nullable = false)
    public Collection<String> getEmailBag() {
        return _emailBag;
    }

    public void setEmailBag(final Collection<String> _emailBag) {
        this._emailBag = _emailBag;
    }
    private Collection<String> _alternativeNames;

    @Property(name = "AlternativeNames", type = "Collection(Edm.String)", nullable = false)
    public Collection<String> getAlternativeNames() {
        return _alternativeNames;
    }

    public void setAlternativeNames(final Collection<String> _alternativeNames) {
        this._alternativeNames = _alternativeNames;
    }
    private com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases _contactAlias;

    @Property(name = "ContactAlias", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases", nullable = true)
    public com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases getContactAlias() {
        return _contactAlias;
    }

    public void setContactAlias(final com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases _contactAlias) {
        this._contactAlias = _contactAlias;
    }
    private com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _homePhone;

    @Property(name = "HomePhone", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", nullable = true)
    public com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone getHomePhone() {
        return _homePhone;
    }

    public void setHomePhone(final com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _homePhone) {
        this._homePhone = _homePhone;
    }
    private com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _workPhone;

    @Property(name = "WorkPhone", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", nullable = true)
    public com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone getWorkPhone() {
        return _workPhone;
    }

    public void setWorkPhone(final com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _workPhone) {
        this._workPhone = _workPhone;
    }
    private Collection<com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone> _mobilePhoneBag;

    @Property(name = "MobilePhoneBag", type = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Phone)", nullable = false)
    public Collection<com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone> getMobilePhoneBag() {
        return _mobilePhoneBag;
    }

    public void setMobilePhoneBag(final Collection<com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone> _mobilePhoneBag) {
        this._mobilePhoneBag = _mobilePhoneBag;
    }
}
