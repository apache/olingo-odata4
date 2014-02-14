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

package com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types;

import com.msopentech.odatajclient.proxy.api.annotations.Namespace;
import com.msopentech.odatajclient.proxy.api.annotations.EntityType;
import com.msopentech.odatajclient.proxy.api.annotations.Key;
import com.msopentech.odatajclient.proxy.api.annotations.KeyRef;
import com.msopentech.odatajclient.proxy.api.annotations.NavigationProperty;
import com.msopentech.odatajclient.proxy.api.annotations.Property;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.api.annotations.Parameter;
import com.msopentech.odatajclient.engine.metadata.edm.v3.ParameterMode;
import com.msopentech.odatajclient.proxy.api.AbstractComplexType;
import com.msopentech.odatajclient.proxy.api.AbstractOpenType;
import com.msopentech.odatajclient.engine.metadata.EdmContentKind;
import com.msopentech.odatajclient.engine.metadata.edm.ConcurrencyMode;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.*;
import com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.*;

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
@EntityType(name = "Order",
        openType = false,
        hasStream = false,
        isAbstract = false,
        baseType = "")
public interface Order extends Serializable {

    
    @Key
    @Property(name = "OrderId", 
                type = "Edm.Int32", 
                nullable = false,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = true)
    public Integer getOrderId();

    public void setOrderId(final Integer _orderId);

    
    @Property(name = "CustomerId", 
                type = "Edm.Int32", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = true)
    public Integer getCustomerId();

    public void setCustomerId(final Integer _customerId);

    
    @Property(name = "Concurrency", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo", 
                nullable = true,
                defaultValue = "",
                maxLenght = Integer.MAX_VALUE,
                fixedLenght = false,
                precision = 0,
                scale = 0,
                unicode = true,
                collation = "",
                srid = "",
                concurrencyMode = ConcurrencyMode.None,
                fcSourcePath = "",
                fcTargetPath = "",
                fcContentKind = EdmContentKind.text,
                fcNSPrefix = "",
                fcNSURI = "",
                fcKeepInContent = true)
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo getConcurrency();

    public void setConcurrency(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo _concurrency);

    


    @NavigationProperty(name = "Login", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Login", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Login")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Login getLogin();

    public void setLogin(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Login _login);


    @NavigationProperty(name = "Customer", 
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", 
                targetSchema = "Microsoft.Test.OData.Services.AstoriaDefaultService", 
                targetContainer = "DefaultContainer", 
                targetEntitySet = "Customer")
    public com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer getCustomer();

    public void setCustomer(final com.msopentech.odatajclient.proxy.actionoverloadingservice.microsoft.test.odata.services.astoriadefaultservice.types.Customer _customer);



}
