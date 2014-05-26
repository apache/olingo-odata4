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
import org.apache.olingo.ext.proxy.api.annotations.EntitySet;
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;
import org.apache.olingo.ext.proxy.api.annotations.NavigationProperty;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.Annotatable;
import org.apache.olingo.ext.proxy.api.AbstractOpenType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.AbstractEntitySet;
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

@KeyRef(OrderDetailKey.class)
@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "OrderDetail",
        openType = false,
        hasStream = false,
        isAbstract = false)
public interface OrderDetail 
  extends Annotatable,java.io.Serializable {

        
    @Key
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OrderID", 
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
    java.lang.Integer getOrderID();

    void setOrderID(java.lang.Integer _orderID);    
    
    @Key
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ProductID", 
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
    java.lang.Integer getProductID();

    void setProductID(java.lang.Integer _productID);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "OrderPlaced", 
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
    java.util.Calendar getOrderPlaced();

    void setOrderPlaced(java.util.Calendar _orderPlaced);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Quantity", 
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
    java.lang.Integer getQuantity();

    void setQuantity(java.lang.Integer _quantity);    
    
    
    @org.apache.olingo.ext.proxy.api.annotations.Property(name = "UnitPrice", 
                type = "Edm.Single", 
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
    java.lang.Float getUnitPrice();

    void setUnitPrice(java.lang.Float _unitPrice);    
    
    

    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ProductOrdered", 
                type = "Microsoft.Test.OData.Services.ODataWCFService.Product", 
                targetSchema = "Microsoft.Test.OData.Services.ODataWCFService", 
                targetContainer = "InMemoryEntities", 
                targetEntitySet = "Products",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductCollection getProductOrdered();

    void setProductOrdered(org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductCollection _productOrdered);
    
    @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "AssociatedOrder", 
                type = "Microsoft.Test.OData.Services.ODataWCFService.Order", 
                targetSchema = "Microsoft.Test.OData.Services.ODataWCFService", 
                targetContainer = "InMemoryEntities", 
                targetEntitySet = "Orders",
                containsTarget = false)
    org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order getAssociatedOrder();

    void setAssociatedOrder(org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Order _associatedOrder);
    


    ComplexFactory factory();

    interface ComplexFactory {
    }

    Annotations annotations();

    interface Annotations {

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderID",
                   type = "Edm.Int32")
        Annotatable getOrderIDAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductID",
                   type = "Edm.Int32")
        Annotatable getProductIDAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderPlaced",
                   type = "Edm.DateTimeOffset")
        Annotatable getOrderPlacedAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Quantity",
                   type = "Edm.Int32")
        Annotatable getQuantityAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "UnitPrice",
                   type = "Edm.Single")
        Annotatable getUnitPriceAnnotations();



        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ProductOrdered", 
                  type = "Microsoft.Test.OData.Services.ODataWCFService.Product")
        Annotatable getProductOrderedAnnotations();

        @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "AssociatedOrder", 
                  type = "Microsoft.Test.OData.Services.ODataWCFService.Order")
        Annotatable getAssociatedOrderAnnotations();
    }

}
