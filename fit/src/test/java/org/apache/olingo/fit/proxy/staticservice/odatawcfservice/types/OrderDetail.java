/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types;

// CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.annotations.Key;
import org.apache.olingo.ext.proxy.api.annotations.KeyRef;

// CHECKSTYLE:ON (Maven checkstyle)

@KeyRef(OrderDetailKey.class)
@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityType(name = "OrderDetail",
    openType = false,
    hasStream = false,
    isAbstract = false)
public interface OrderDetail
    extends org.apache.olingo.ext.proxy.api.Annotatable,
    org.apache.olingo.ext.proxy.api.EntityType<OrderDetail>,
    org.apache.olingo.ext.proxy.api.StructuredQuery<OrderDetail> {

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
      srid = "")
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
      srid = "")
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
      srid = "")
  java.sql.Timestamp getOrderPlaced();

  void setOrderPlaced(java.sql.Timestamp _orderPlaced);

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
      srid = "")
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
      srid = "")
  java.lang.Float getUnitPrice();

  void setUnitPrice(java.lang.Float _unitPrice);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "ProductOrdered",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Product",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Products",
      containsTarget = false)
  ProductCollection
      getProductOrdered();

      void
      setProductOrdered(
          ProductCollection _productOrdered);

  @org.apache.olingo.ext.proxy.api.annotations.NavigationProperty(name = "AssociatedOrder",
      type = "Microsoft.Test.OData.Services.ODataWCFService.Order",
      targetSchema = "Microsoft.Test.OData.Services.ODataWCFService",
      targetContainer = "InMemoryEntities",
      targetEntitySet = "Orders",
      containsTarget = false)
  Order
      getAssociatedOrder();

      void
      setAssociatedOrder(
          Order _associatedOrder);

  Operations operations();

  interface Operations extends org.apache.olingo.ext.proxy.api.Operations {
    // No additional methods needed for now.
  }

  Annotations annotations();

  interface Annotations {

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "ProductID",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getProductIDAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "OrderPlaced",
        type = "Edm.DateTimeOffset")
    org.apache.olingo.ext.proxy.api.Annotatable getOrderPlacedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "Quantity",
        type = "Edm.Int32")
    org.apache.olingo.ext.proxy.api.Annotatable getQuantityAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForProperty(name = "UnitPrice",
        type = "Edm.Single")
    org.apache.olingo.ext.proxy.api.Annotatable getUnitPriceAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "ProductOrdered",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Product")
    org.apache.olingo.ext.proxy.api.Annotatable getProductOrderedAnnotations();

    @org.apache.olingo.ext.proxy.api.annotations.AnnotationsForNavigationProperty(name = "AssociatedOrder",
        type = "Microsoft.Test.OData.Services.ODataWCFService.Order")
    org.apache.olingo.ext.proxy.api.Annotatable getAssociatedOrderAnnotations();
  }

}
