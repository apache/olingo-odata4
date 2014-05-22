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
package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types;

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty;
import org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EntityType;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.commons.api.edm.constants.EdmContentKind;
import org.apache.olingo.client.api.edm.ConcurrencyMode;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.*;
import org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.*;

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


@Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@EntityType(name = "Order",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface Order 
  extends Annotatable,Serializable {

    
    @Key
    @Property(name = "OrderID", 
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
                fcKeepInContent = false)
    Integer getOrderID();

    void setOrderID(Integer _orderID);    
    
    
    @Property(name = "OrderDate", 
                type = "Edm.DateTimeOffset", 
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
                fcKeepInContent = false)
    Calendar getOrderDate();

    void setOrderDate(Calendar _orderDate);    
    
    
    @Property(name = "ShelfLife", 
                type = "Edm.Duration", 
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
                fcKeepInContent = false)
    BigDecimal getShelfLife();

    void setShelfLife(BigDecimal _shelfLife);    
    
    
    @Property(name = "OrderShelfLifes", 
                type = "Edm.Duration", 
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
                fcKeepInContent = false)
    Collection<BigDecimal> getOrderShelfLifes();

    void setOrderShelfLifes(Collection<BigDecimal> _orderShelfLifes);    
    
    

    @NavigationProperty(name = "LoggedInEmployee", 
                type = "Microsoft.Test.OData.Services.ODataWCFService.Employee", 
                targetSchema = "Microsoft.Test.OData.Services.ODataWCFService", 
                targetContainer = "InMemoryEntities", 
                targetEntitySet = "Employees",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Employee getLoggedInEmployee();

    void setLoggedInEmployee(org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Employee _loggedInEmployee);
    
    @NavigationProperty(name = "CustomerForOrder", 
                type = "Microsoft.Test.OData.Services.ODataWCFService.Customer", 
                targetSchema = "Microsoft.Test.OData.Services.ODataWCFService", 
                targetContainer = "InMemoryEntities", 
                targetEntitySet = "Customers",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer getCustomerForOrder();

    void setCustomerForOrder(org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Customer _customerForOrder);
    
    @NavigationProperty(name = "OrderDetails", 
                type = "Microsoft.Test.OData.Services.ODataWCFService.OrderDetail", 
                targetSchema = "Microsoft.Test.OData.Services.ODataWCFService", 
                targetContainer = "InMemoryEntities", 
                targetEntitySet = "OrderDetails",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetailCollection getOrderDetails();

    void setOrderDetails(org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.OrderDetailCollection _orderDetails);
    



        Annotations annotations();

    interface Annotations {

            @AnnotationsForProperty(name = "OrderID",
                   type = "Edm.Int32")
        Annotatable getOrderIDAnnotations();

            @AnnotationsForProperty(name = "OrderDate",
                   type = "Edm.DateTimeOffset")
        Annotatable getOrderDateAnnotations();

            @AnnotationsForProperty(name = "ShelfLife",
                   type = "Edm.Duration")
        Annotatable getShelfLifeAnnotations();

            @AnnotationsForProperty(name = "OrderShelfLifes",
                   type = "Edm.Duration")
        Annotatable getOrderShelfLifesAnnotations();

    
    
        @AnnotationsForNavigationProperty(name = "LoggedInEmployee", 
                  type = "Microsoft.Test.OData.Services.ODataWCFService.Employee")
        Annotatable getLoggedInEmployeeAnnotations();
    
        @AnnotationsForNavigationProperty(name = "CustomerForOrder", 
                  type = "Microsoft.Test.OData.Services.ODataWCFService.Customer")
        Annotatable getCustomerForOrderAnnotations();
    
        @AnnotationsForNavigationProperty(name = "OrderDetails", 
                  type = "Microsoft.Test.OData.Services.ODataWCFService.OrderDetail")
        Annotatable getOrderDetailsAnnotations();
        }
}
