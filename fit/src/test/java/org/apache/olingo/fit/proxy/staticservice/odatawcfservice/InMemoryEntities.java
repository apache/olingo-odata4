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
package org.apache.olingo.fit.proxy.staticservice.odatawcfservice;

// CHECKSTYLE:OFF (Maven checkstyle)
import java.io.InputStream;

// CHECKSTYLE:ON (Maven checkstyle)
import java.io.Serializable;

import org.apache.olingo.ext.proxy.api.ComplexCollection;
import org.apache.olingo.ext.proxy.api.ComplexType;
import org.apache.olingo.ext.proxy.api.EdmStreamValue;
import org.apache.olingo.ext.proxy.api.EntityCollection;
import org.apache.olingo.ext.proxy.api.EntityType;
import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.PrimitiveCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.AccessLevel;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Address;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Color;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Company;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Customer;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.LabourUnion;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.Person;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.PersonComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollection;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.ProductCollectionComposableInvoker;
import org.apache.olingo.fit.proxy.staticservice.odatawcfservice.types.StoredPI;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "InMemoryEntities",
    namespace = "Microsoft.Test.OData.Services.ODataWCFService")
public interface InMemoryEntities extends PersistenceManager {

  Accounts getAccounts();

  StoredPIs getStoredPIs();

  Customers getCustomers();

  Products getProducts();

  OrderDetails getOrderDetails();

  Departments getDepartments();

  Employees getEmployees();

  Orders getOrders();

  People getPeople();

  SubscriptionTemplates getSubscriptionTemplates();

  ProductReviews getProductReviews();

  ProductDetails getProductDetails();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "PublicCompany",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  Company
      getPublicCompany();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "DefaultStoredPI",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  StoredPI
      getDefaultStoredPI();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "VipCustomer",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  Customer
      getVipCustomer();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "Company",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  Company getCompany();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "Boss",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  Person getBoss();

  @org.apache.olingo.ext.proxy.api.annotations.Singleton(
      name = "LabourUnion",
      container = "Microsoft.Test.OData.Services.ODataWCFService.InMemoryEntities")
  LabourUnion
      getLabourUnion();

  Operations operations();

  public interface Operations extends org.apache.olingo.ext.proxy.api.Operations {

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetBossEmails",
        type = OperationType.FUNCTION,
        isComposable = false,
        referenceType = org.apache.olingo.ext.proxy.api.PrimitiveCollection.class,
        returnType = "Collection(Edm.String)")
        org.apache.olingo.ext.proxy.api.PrimitiveCollectionInvoker<org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String>>
        getBossEmails(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "start", type = "Edm.Int32", nullable = false) java.lang.Integer start,
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "count", type = "Edm.Int32", nullable = false) java.lang.Integer count
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetPerson2",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = Person.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
    PersonComposableInvoker
        getPerson2(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "city", type = "Edm.String", nullable = false) java.lang.String city
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetDefaultColor",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = Color.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Color")
        org.apache.olingo.ext.proxy.api.Invoker<Color>
        getDefaultColor(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetPerson",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = Person.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
    PersonComposableInvoker
        getPerson(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "address",
                type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) Address address
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetProductsByAccessLevel",
        type = OperationType.FUNCTION,
        isComposable = false,
        referenceType = org.apache.olingo.ext.proxy.api.PrimitiveCollection.class,
        returnType = "Collection(Edm.String)")
        org.apache.olingo.ext.proxy.api.PrimitiveCollectionInvoker<org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String>>
        getProductsByAccessLevel(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "accessLevel",
                type = "Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", nullable = false) AccessLevel accessLevel
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "GetAllProducts",
        type = OperationType.FUNCTION,
        isComposable = true,
        referenceType = ProductCollection.class,
        returnType = "Collection(Microsoft.Test.OData.Services.ODataWCFService.Product)")
    ProductCollectionComposableInvoker
        getAllProducts(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(
        name = "ResetBossAddress",
        type = OperationType.ACTION,
        referenceType = Address.class,
        returnType = "Microsoft.Test.OData.Services.ODataWCFService.Address")
        org.apache.olingo.ext.proxy.api.StructuredInvoker<Address>
        resetBossAddress(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "address",
                type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) Address address
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetDataSource",
        type = OperationType.ACTION)
    org.apache.olingo.ext.proxy.api.Invoker<Void> resetDataSource(
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "Discount",
        type = OperationType.ACTION)
    org.apache.olingo.ext.proxy.api.Invoker<Void> discount(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "percentage", type = "Edm.Int32",
            nullable = false) java.lang.Integer percentage
        );

    @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetBossEmail",
        type = OperationType.ACTION,
        referenceType = org.apache.olingo.ext.proxy.api.PrimitiveCollection.class,
        returnType = "Collection(Edm.String)")
        org.apache.olingo.ext.proxy.api.PrimitiveCollectionInvoker<org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String>>
        resetBossEmail(
            @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "emails", type = "Collection(Edm.String)",
                nullable = false) org.apache.olingo.ext.proxy.api.PrimitiveCollection<java.lang.String> emails
        );

  }

  <NE extends EntityType<?>> NE newEntityInstance(Class<NE> ref);

  <T extends EntityType<?>, NEC extends EntityCollection<T, ?, ?>> NEC newEntityCollection(Class<NEC> ref);

  <NE extends ComplexType<?>> NE newComplexInstance(Class<NE> ref);

  <T extends ComplexType<?>, NEC extends ComplexCollection<T, ?, ?>> NEC newComplexCollection(Class<NEC> ref);

  <T extends Serializable, NEC extends PrimitiveCollection<T>> NEC newPrimitiveCollection(Class<T> ref);

  EdmStreamValue newEdmStreamValue(String contentType, InputStream stream);
}
