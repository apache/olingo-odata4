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

package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types;

import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.ComplexType;
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.*;
import org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.*;

import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;


@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.ComplexType(name = "AuditInfo")
public interface AuditInfo 
    extends org.apache.olingo.ext.proxy.api.StructuredType,org.apache.olingo.ext.proxy.api.SingleQuery<AuditInfo> {



    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ModifiedDate", type = "Edm.DateTime", nullable = false)
    java.sql.Timestamp getModifiedDate();

    void setModifiedDate(java.sql.Timestamp _modifiedDate);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ModifiedBy", type = "Edm.String", nullable = true)
    java.lang.String getModifiedBy();

    void setModifiedBy(java.lang.String _modifiedBy);

    

    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Concurrency", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo", nullable = true)
    org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo getConcurrency();

    void setConcurrency(org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo _concurrency);

        

    ComplexFactory factory();

    interface ComplexFactory {
         @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Concurrency",
                   type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
         org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo newConcurrency();

    }
}
