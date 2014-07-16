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

package org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice;

import org.apache.olingo.ext.proxy.api.OperationType;
import org.apache.olingo.ext.proxy.api.PersistenceManager;

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.ODataWCFService")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "InMemoryEntities",
  namespace = "Microsoft.Test.OData.Services.ODataWCFService",
  isDefaultEntityContainer = true)
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



    PublicCompany getPublicCompany();

    DefaultStoredPI getDefaultStoredPI();

    VipCustomer getVipCustomer();

    Company getCompany();

    Boss getBoss();

    LabourUnion getLabourUnion();


  Operations operations();

  public interface Operations {
        @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetBossEmails",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Edm.String)")
  java.util.Collection<java.lang.String> getBossEmails(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "start", type = "Edm.Int32", nullable = false) java.lang.Integer start, 
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "count", type = "Edm.Int32", nullable = false) java.lang.Integer count
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetPerson2",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person getPerson2(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "city", type = "Edm.String", nullable = false) java.lang.String city
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetDefaultColor",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Color")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Color getDefaultColor(
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetPerson",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person getPerson(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "address", type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address address
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetProductsByAccessLevel",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Edm.String)")
  java.util.Collection<java.lang.String> getProductsByAccessLevel(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "accessLevel", type = "Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccessLevel accessLevel
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetAllProducts",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Collection(Microsoft.Test.OData.Services.ODataWCFService.Product)")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductCollection getAllProducts(
    );

    
        @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetBossAddress",
                    type = OperationType.ACTION,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Address")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address resetBossAddress(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "address", type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address address
    );
  
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetDataSource",
                    type = OperationType.ACTION)
  void resetDataSource(
    );
  
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "Discount",
                    type = OperationType.ACTION)
  void discount(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "percentage", type = "Edm.Int32", nullable = false) java.lang.Integer percentage
    );
  
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetBossEmail",
                    type = OperationType.ACTION,
                    returnType = "Collection(Edm.String)")
  java.util.Collection<java.lang.String> resetBossEmail(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "emails", type = "Collection(Edm.String)", nullable = false) java.util.Collection<java.lang.String> emails
    );
  
      }

      ComplexFactory complexFactory();

    interface ComplexFactory {
          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Address",
                type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address newAddress();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "HomeAddress",
                type = "Microsoft.Test.OData.Services.ODataWCFService.HomeAddress")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.HomeAddress newHomeAddress();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "CompanyAddress",
                type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyAddress")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CompanyAddress newCompanyAddress();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "AccountInfo",
                type = "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccountInfo newAccountInfo();

        }
  }
