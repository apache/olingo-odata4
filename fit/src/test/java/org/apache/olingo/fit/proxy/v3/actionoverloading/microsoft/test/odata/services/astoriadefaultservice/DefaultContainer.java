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

package org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice;

import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.ext.proxy.api.annotations.Operation;
import org.apache.olingo.ext.proxy.api.annotations.Parameter;
import org.apache.olingo.ext.proxy.api.annotations.Property;
import org.apache.olingo.ext.proxy.api.PersistenceManager;
import org.apache.olingo.ext.proxy.api.OperationType;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import java.io.Serializable;
import java.util.Collection;
import java.util.Calendar;
import javax.xml.datatype.Duration;

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
  
        @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "UpdatePersonInfo",
                    type = OperationType.ACTION)
  void updatePersonInfo(
    );
  
          @org.apache.olingo.ext.proxy.api.annotations.Operation(name = "RetrieveProduct",
                    type = OperationType.ACTION,
                    returnType = "Edm.Int32")
  java.lang.Integer retrieveProduct(
    );
  
      }

      ComplexFactory complexFactory();

    interface ComplexFactory {
          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ContactDetails",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ContactDetails")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ContactDetails newContactDetails();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Aliases",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Aliases")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Aliases newAliases();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Phone",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Phone")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Phone newPhone();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "AuditInfo",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.AuditInfo")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.AuditInfo newAuditInfo();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ConcurrencyInfo",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ConcurrencyInfo")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ConcurrencyInfo newConcurrencyInfo();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "Dimensions",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.Dimensions")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.Dimensions newDimensions();

          @org.apache.olingo.ext.proxy.api.annotations.Property(name = "ComplexToCategory",
                type = "Microsoft.Test.OData.Services.AstoriaDefaultService.ComplexToCategory")
      org.apache.olingo.fit.proxy.v3.actionoverloading.microsoft.test.odata.services.astoriadefaultservice.types.ComplexToCategory newComplexToCategory();

        }
  }
