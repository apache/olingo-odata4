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
package com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.proxy.api.annotations.Namespace;
import com.msopentech.odatajclient.proxy.api.annotations.EntityContainer;
import com.msopentech.odatajclient.proxy.api.annotations.Operation;
import com.msopentech.odatajclient.proxy.api.annotations.Parameter;
import com.msopentech.odatajclient.engine.metadata.edm.v3.ParameterMode;
import com.msopentech.odatajclient.proxy.api.AbstractContainer;
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
@EntityContainer(name = "DefaultContainer",
  isDefaultEntityContainer = true)
public interface DefaultContainer extends AbstractContainer {

    AllGeoTypesSet getAllGeoTypesSet();

    AllGeoCollectionTypesSet getAllGeoCollectionTypesSet();

    Customer getCustomer();

    Login getLogin();

    RSAToken getRSAToken();

    PageView getPageView();

    LastLogin getLastLogin();

    Message getMessage();

    MessageAttachment getMessageAttachment();

    Order getOrder();

    OrderLine getOrderLine();

    Product getProduct();

    ProductDetail getProductDetail();

    ProductReview getProductReview();

    ProductPhoto getProductPhoto();

    CustomerInfo getCustomerInfo();

    Computer getComputer();

    ComputerDetail getComputerDetail();

    Driver getDriver();

    License getLicense();

    MappedEntityType getMappedEntityType();

    Car getCar();

    Person getPerson();

    PersonMetadata getPersonMetadata();




      @Operation(name = "GetPrimitiveString"     ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Edm.String")
    String getPrimitiveString(
    );
        @Operation(name = "GetSpecificCustomer" , 
                    entitySet = Customer.class    ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Customer)")
    com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection getSpecificCustomer(
        @Parameter(name = "Name", type = "Edm.String", nullable = true) String name
    );
        @Operation(name = "GetCustomerCount"     ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Edm.Int32")
    Integer getCustomerCount(
    );
        @Operation(name = "GetArgumentPlusOne"     ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Edm.Int32")
    Integer getArgumentPlusOne(
        @Parameter(name = "arg1", type = "Edm.Int32", nullable = false) Integer arg1
    );
        @Operation(name = "EntityProjectionReturnsCollectionOfComplexTypes"     ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)")
    Collection<com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails> entityProjectionReturnsCollectionOfComplexTypes(
    );
        @Operation(name = "ResetDataSource"     ,
                    httpMethod = HttpMethod.POST )
    void resetDataSource(
    );
        @Operation(name = "InStreamErrorGetCustomer" , 
                    entitySet = Customer.class    ,
                    httpMethod = HttpMethod.GET ,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Customer)")
    com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection inStreamErrorGetCustomer(
    );
        @Operation(name = "IncreaseSalaries"      )
    void increaseSalaries(
        @Parameter(name = "employees", type = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Employee)", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.EmployeeCollection employees, 
        @Parameter(name = "n", type = "Edm.Int32", nullable = false) Integer n
    );
        @Operation(name = "Sack"      )
    void sack(
        @Parameter(name = "employee", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Employee", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Employee employee
    );
        @Operation(name = "GetComputer" , 
                    entitySet = Computer.class     ,
                    returnType = "Microsoft.Test.OData.Services.AstoriaDefaultService.Computer")
    com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Computer getComputer(
        @Parameter(name = "computer", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Computer", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Computer computer
    );
        @Operation(name = "ChangeProductDimensions"      )
    void changeProductDimensions(
        @Parameter(name = "product", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Product", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Product product, 
        @Parameter(name = "dimensions", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions dimensions
    );
        @Operation(name = "ResetComputerDetailsSpecifications"      )
    void resetComputerDetailsSpecifications(
        @Parameter(name = "computerDetail", type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComputerDetail", nullable = true) com.msopentech.odatajclient.proxy.defaultservice.microsoft.test.odata.services.astoriadefaultservice.types.ComputerDetail computerDetail, 
        @Parameter(name = "specifications", type = "Collection(Edm.String)", nullable = false) Collection<String> specifications, 
        @Parameter(name = "purchaseTime", type = "Edm.DateTime", nullable = false) ODataTimestamp purchaseTime
    );
  }
