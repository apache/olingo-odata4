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
package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.*;
import org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;
import java.util.Calendar;
import javax.xml.datatype.Duration;


@Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@ComplexType(name = "ContactDetails")
public interface ContactDetails 
    extends Serializable {


    @Property(name = "EmailBag", type = "Edm.String", nullable = false)
    Collection<String> getEmailBag();

    void setEmailBag(final Collection<String> _emailBag);

    

    @Property(name = "AlternativeNames", type = "Edm.String", nullable = false)
    Collection<String> getAlternativeNames();

    void setAlternativeNames(final Collection<String> _alternativeNames);

    

    @Property(name = "ContactAlias", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases", nullable = true)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases getContactAlias();

    void setContactAlias(final org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases _contactAlias);

        

    @Property(name = "HomePhone", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", nullable = true)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone getHomePhone();

    void setHomePhone(final org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _homePhone);

        

    @Property(name = "WorkPhone", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", nullable = true)
    org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone getWorkPhone();

    void setWorkPhone(final org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone _workPhone);

        

    @Property(name = "MobilePhoneBag", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone", nullable = false)
    Collection<org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone> getMobilePhoneBag();

    void setMobilePhoneBag(final Collection<org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone> _mobilePhoneBag);

        

        ComplexFactory factory();

    interface ComplexFactory {
             @Property(name = "ContactAlias",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Aliases newContactAlias();

             @Property(name = "HomePhone",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone newHomePhone();

             @Property(name = "WorkPhone",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone newWorkPhone();

             @Property(name = "MobilePhoneBag",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
         org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.Phone newMobilePhoneBag();

        }
}
