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

package org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice;

//CHECKSTYLE:OFF (Maven checkstyle)
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.OperationType;
//CHECKSTYLE:ON (Maven checkstyle)

@org.apache.olingo.ext.proxy.api.annotations.Namespace("Microsoft.Test.OData.Services.AstoriaDefaultService")
@org.apache.olingo.ext.proxy.api.annotations.EntityContainer(name = "DefaultContainer",
  namespace = "Microsoft.Test.OData.Services.AstoriaDefaultService",
  isDefaultEntityContainer = true)
public interface DefaultContainer extends PersistenceManager {

    Customer getCustomer();

    Login getLogin();

    OrderLine getOrderLine();

    ComputerDetail getComputerDetail();

    Product getProduct();

    Message getMessage();

    ProductDetail getProductDetail();

    ProductPhoto getProductPhoto();

    Order getOrder();

    Computer getComputer();

    MappedEntityType getMappedEntityType();

    PageView getPageView();

    Driver getDriver();

    AllGeoCollectionTypesSet getAllGeoCollectionTypesSet();

    Car getCar();

    CustomerInfo getCustomerInfo();

    License getLicense();

    ProductReview getProductReview();

    LastLogin getLastLogin();

    MessageAttachment getMessageAttachment();

    AllGeoTypesSet getAllGeoTypesSet();

    PersonMetadata getPersonMetadata();

    RSAToken getRSAToken();

    Person getPerson();





  Operations operations();

  public interface Operations {
        @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetSpecificCustomer",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Customer)")
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection getSpecificCustomer(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "Name", type = "Edm.String", nullable = true) java.lang.String name
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "InStreamErrorGetCustomer",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.Customer)")
  org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.CustomerCollection inStreamErrorGetCustomer(
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetPrimitiveString",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Edm.String")
  java.lang.String getPrimitiveString(
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "EntityProjectionReturnsCollectionOfComplexTypes",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Collection(Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails)")
  java.util.Collection<org.apache.olingo.fit.proxy.v3.staticservice.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails> entityProjectionReturnsCollectionOfComplexTypes(
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetArgumentPlusOne",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Edm.Int32")
  java.lang.Integer getArgumentPlusOne(
        @org.apache.olingo.ext.proxy.api.annotations.Parameter(name = "arg1", type = "Edm.Int32", nullable = false) java.lang.Integer arg1
    );

          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "GetCustomerCount",
                    type = OperationType.FUNCTION,
                    isComposable = false,
                    returnType = "Edm.Int32")
  java.lang.Integer getCustomerCount(
    );

    
        @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "ResetDataSource",
                    type = OperationType.ACTION)
  void resetDataSource(
    );
  
      }   
}
