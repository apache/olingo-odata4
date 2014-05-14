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

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.Namespace;
import org.apache.olingo.ext.proxy.api.annotations.EntityContainer;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.Container;
import org.apache.olingo.ext.proxy.api.OperationType;
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
@EntityContainer(name = "InMemoryEntities",
  namespace = "Microsoft.Test.OData.Services.ODataWCFService",
  isDefaultEntityContainer = true)
public interface InMemoryEntities extends Container {

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
        @Operation(name = "GetBossEmails",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Edm.String)")
  Collection<String> getBossEmails(
        @Parameter(name = "start", type = "Edm.Int32", nullable = false) Integer start, 
        @Parameter(name = "count", type = "Edm.Int32", nullable = false) Integer count
    );

          @Operation(name = "GetPerson2",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person getPerson2(
        @Parameter(name = "city", type = "Edm.String", nullable = false) String city
    );

          @Operation(name = "GetDefaultColor",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Color")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Color getDefaultColor(
    );

          @Operation(name = "GetPerson",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Person")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Person getPerson(
        @Parameter(name = "address", type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address address
    );

          @Operation(name = "GetProductsByAccessLevel",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Edm.String)")
  Collection<String> getProductsByAccessLevel(
        @Parameter(name = "accessLevel", type = "Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccessLevel accessLevel
    );

          @Operation(name = "GetAllProducts",
                    type = OperationType.FUNCTION,
                    isComposable = true,
                    returnType = "Collection(Microsoft.Test.OData.Services.ODataWCFService.Product)")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.ProductCollection getAllProducts(
    );

    
        @Operation(name = "ResetBossAddress",
                    type = OperationType.ACTION,
                    returnType = "Microsoft.Test.OData.Services.ODataWCFService.Address")
  org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address resetBossAddress(
        @Parameter(name = "address", type = "Microsoft.Test.OData.Services.ODataWCFService.Address", nullable = false) org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address address
    );
  
          @Operation(name = "ResetDataSource",
                    type = OperationType.ACTION)
  void resetDataSource(
    );
  
          @Operation(name = "Discount",
                    type = OperationType.ACTION)
  void discount(
        @Parameter(name = "percentage", type = "Edm.Int32", nullable = false) Integer percentage
    );
  
          @Operation(name = "ResetBossEmail",
                    type = OperationType.ACTION,
                    returnType = "Collection(Edm.String)")
  Collection<String> resetBossEmail(
        @Parameter(name = "emails", type = "Collection(Edm.String)", nullable = false) Collection<String> emails
    );
  
      }

      ComplexFactory complexFactory();

    interface ComplexFactory {
          @Property(name = "Address",
                type = "Microsoft.Test.OData.Services.ODataWCFService.Address")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.Address newAddress();

          @Property(name = "HomeAddress",
                type = "Microsoft.Test.OData.Services.ODataWCFService.HomeAddress")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.HomeAddress newHomeAddress();

          @Property(name = "CompanyAddress",
                type = "Microsoft.Test.OData.Services.ODataWCFService.CompanyAddress")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.CompanyAddress newCompanyAddress();

          @Property(name = "AccountInfo",
                type = "Microsoft.Test.OData.Services.ODataWCFService.AccountInfo")
      org.apache.olingo.fit.proxy.v4.staticservice.microsoft.test.odata.services.odatawcfservice.types.AccountInfo newAccountInfo();

        }
  }
