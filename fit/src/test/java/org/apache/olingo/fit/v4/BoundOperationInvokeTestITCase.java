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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataOperation;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataSingleton;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class BoundOperationInvokeTestITCase extends AbstractTestITCase {

  private void functions(final ODataFormat format) throws EdmPrimitiveTypeException {
    // GetEmployeesCount
    URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<ODataSingleton> singletonReq =
            client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singletonReq.setFormat(format);
    final ODataSingleton company = singletonReq.execute().getBody();
    assertNotNull(company);

    ODataOperation boundOp = company.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ODataProperty> getEmployeesCountReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataProperty.class);
    getEmployeesCountReq.setFormat(format);
    final ODataProperty getEmployeesCountRes = getEmployeesCountReq.execute().getBody();
    assertNotNull(getEmployeesCountRes);
    assertTrue(getEmployeesCountRes.hasPrimitiveValue());

    // GetProductDetails
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Products").appendKeySegment(5);
    ODataEntityRequest<ODataEntity> entityReq = client.getRetrieveRequestFactory().
            getEntityRequest(builder.build());
    entityReq.setFormat(format);
    ODataEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails");
    assertNotNull(boundOp);

    final ODataPrimitiveValue count = client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1);
    final ODataInvokeRequest<ODataEntitySet> getProductDetailsReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataEntitySet.class,
                    Collections.<String, ODataValue>singletonMap("count", count));
    getProductDetailsReq.setFormat(format);
    final ODataEntitySet getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertEquals(1, getProductDetailsRes.getCount());

    // GetRelatedProduct
    final Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
    keyMap.put("ProductID", 6);
    keyMap.put("ProductDetailID", 1);
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("ProductDetails").appendKeySegment(keyMap);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ODataEntity> getRelatedProductReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataEntity.class);
    getRelatedProductReq.setFormat(format);
    final ODataEntity getRelatedProductRes = getRelatedProductReq.execute().getBody();
    assertNotNull(getRelatedProductRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Product",
            getRelatedProductRes.getTypeName().toString());
    assertEquals(6, getRelatedProductRes.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetDefaultPI
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(102);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataEntity.class);
    getDefaultPIReq.setFormat(format);
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetAccountInfo
    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ODataProperty> getAccountInfoReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataProperty.class);
    getAccountInfoReq.setFormat(format);
    final ODataProperty getAccountInfoRes = getAccountInfoReq.execute().getBody();
    assertNotNull(getAccountInfoRes);
    assertTrue(getAccountInfoRes.hasComplexValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
            getAccountInfoRes.getComplexValue().getTypeName());

    // GetActualAmount
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(
            entity.getNavigationLink("MyGiftCard").getLink());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);
    assertEquals(301, entity.getProperty("GiftCardID").getPrimitiveValue().toCastValue(Integer.class), 0);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetActualAmount");
    assertNotNull(boundOp);

    final ODataPrimitiveValue bonusRate = client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(1.1);
    final ODataInvokeRequest<ODataProperty> getActualAmountReq =
            client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("bonusRate", bonusRate));
    getActualAmountReq.setFormat(format);
    final ODataProperty getActualAmountRes = getActualAmountReq.execute().getBody();
    assertNotNull(getActualAmountRes);
    assertEquals(41.79, getActualAmountRes.getPrimitiveValue().toCastValue(Double.class), 0);
  }

  @Test
  public void atomFunctions() throws EdmPrimitiveTypeException {
    functions(ODataFormat.ATOM);
  }

  @Test
  public void jsonFunctions() throws EdmPrimitiveTypeException {
    functions(ODataFormat.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledFunctions() throws EdmPrimitiveTypeException {
    // GetEmployeesCount
    final ODataInvokeRequest<ODataProperty> getEmployeesCountReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    edmClient.newURIBuilder().appendSingletonSegment("Company").build(),
                    new FullQualifiedName(("Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount")),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"),
                    false);
    final ODataProperty getEmployeesCountRes = getEmployeesCountReq.execute().getBody();
    assertNotNull(getEmployeesCountRes);
    assertTrue(getEmployeesCountRes.hasPrimitiveValue());

    // GetProductDetails
    final ODataPrimitiveValue count = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1);
    final ODataInvokeRequest<ODataEntitySet> getProductDetailsReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Products").appendKeySegment(5).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"),
                    false,
                    Collections.<String, ODataValue>singletonMap("count", count));
    final ODataEntitySet getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertEquals(1, getProductDetailsRes.getCount());

    // GetRelatedProduct
    final Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
    keyMap.put("ProductID", 6);
    keyMap.put("ProductDetailID", 1);
    URIBuilder builder = edmClient.newURIBuilder().appendEntitySetSegment("ProductDetails").appendKeySegment(keyMap);

    final ODataInvokeRequest<ODataEntity> getRelatedProductReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    builder.build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.ProductDetail"),
                    false);
    final ODataEntity getRelatedProductRes = getRelatedProductReq.execute().getBody();
    assertNotNull(getRelatedProductRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Product",
            getRelatedProductRes.getTypeName().toString());
    assertEquals(6, getRelatedProductRes.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetDefaultPI
    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
                    false);
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetAccountInfo
    final ODataInvokeRequest<ODataProperty> getAccountInfoReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
                    false);
    final ODataProperty getAccountInfoRes = getAccountInfoReq.execute().getBody();
    assertNotNull(getAccountInfoRes);
    assertTrue(getAccountInfoRes.hasComplexValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
            getAccountInfoRes.getComplexValue().getTypeName());

    // GetActualAmount
    final ODataPrimitiveValue bonusRate = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildDouble(1.1);
    final ODataInvokeRequest<ODataProperty> getActualAmountReq =
            edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).
                    appendNavigationSegment("MyGiftCard").build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetActualAmount"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GiftCard"),
                    false,
                    Collections.<String, ODataValue>singletonMap("bonusRate", bonusRate));
    final ODataProperty getActualAmountRes = getActualAmountReq.execute().getBody();
    assertNotNull(getActualAmountRes);
    assertEquals(41.79, getActualAmountRes.getPrimitiveValue().toCastValue(Double.class), 0);
  }

  private void actions(final ODataFormat format) throws EdmPrimitiveTypeException {
    // IncreaseRevenue
    URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    ODataEntityRequest<ODataEntity> entityReq =
            client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    ODataEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    ODataOperation boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue");
    assertNotNull(boundOp);

    final ODataPrimitiveValue increaseValue =
            client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(12L);
    final ODataInvokeRequest<ODataProperty> increaseRevenueReq =
            client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("IncreaseValue", increaseValue));
    increaseRevenueReq.setFormat(format);
    final ODataProperty increaseRevenueRes = increaseRevenueReq.execute().getBody();
    assertNotNull(increaseRevenueRes);
    assertTrue(increaseRevenueRes.hasPrimitiveValue());

    // AddAccessRight
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Products").appendKeySegment(5);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight");
    assertNotNull(boundOp);

    final ODataEnumValue accessRight = client.getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute");
    final ODataInvokeRequest<ODataProperty> getProductDetailsReq =
            client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("accessRight", accessRight));
    getProductDetailsReq.setFormat(format);
    final ODataProperty getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertTrue(getProductDetailsRes.hasEnumValue());

    // ResetAddress
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(2);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.ResetAddress");
    assertNotNull(boundOp);

    final ODataCollectionValue<org.apache.olingo.commons.api.domain.v4.ODataValue> addresses =
            client.getObjectFactory().
            newCollectionValue("Collection(Microsoft.Test.OData.Services.ODataWCFService.Address)");
    final ODataComplexValue<ODataProperty> address = client.getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Piazza La Bomba E Scappa")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));
    addresses.add(address);
    final ODataPrimitiveValue index = client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(0);
    final Map<String, ODataValue> params = new LinkedHashMap<String, ODataValue>(2);
    params.put("addresses", addresses);
    params.put("index", index);
    final ODataInvokeRequest<ODataEntity> resetAddressReq =
            client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ODataEntity.class, params);
    resetAddressReq.setFormat(format);
    final ODataEntity resetAddressRes = resetAddressReq.execute().getBody();
    assertNotNull(resetAddressRes);
    assertEquals(2, resetAddressRes.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // RefreshDefaultPI
    builder = client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(102);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI");
    assertNotNull(boundOp);

    final ODataPrimitiveValue newDate = client.getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2014-04-09T00:00:00Z").build();
    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ODataEntity.class,
                    Collections.<String, ODataValue>singletonMap("newDate", newDate));
    getDefaultPIReq.setFormat(format);
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomActions() throws EdmPrimitiveTypeException {
    actions(ODataFormat.ATOM);
  }

  @Test
  public void jsonActions() throws EdmPrimitiveTypeException {
    actions(ODataFormat.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledActions() throws EdmPrimitiveTypeException {
    // IncreaseRevenue
    final ODataPrimitiveValue increaseValue = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt64(12L);
    final ODataInvokeRequest<ODataProperty> increaseRevenueReq =
            edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
                    edmClient.newURIBuilder().appendSingletonSegment("Company").build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"),
                    false,
                    Collections.<String, ODataValue>singletonMap("IncreaseValue", increaseValue));
    final ODataProperty increaseRevenueRes = increaseRevenueReq.execute().getBody();
    assertNotNull(increaseRevenueRes);
    assertTrue(increaseRevenueRes.hasPrimitiveValue());

    // AddAccessRight
    final ODataEnumValue accessRight = edmClient.getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute");
    final ODataInvokeRequest<ODataProperty> getProductDetailsReq =
            edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Products").appendKeySegment(5).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"),
                    false,
                    Collections.<String, ODataValue>singletonMap("accessRight", accessRight));
    final ODataProperty getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertTrue(getProductDetailsRes.hasEnumValue());

    // ResetAddress
    final ODataCollectionValue<org.apache.olingo.commons.api.domain.v4.ODataValue> addresses =
            edmClient.getObjectFactory().
            newCollectionValue("Collection(Microsoft.Test.OData.Services.ODataWCFService.Address)");
    final ODataComplexValue<ODataProperty> address = edmClient.getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("Street",
            edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("Piazza La Bomba E Scappa")));
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("City",
            edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("PostalCode",
            edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));
    addresses.add(address);
    final ODataPrimitiveValue index = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt32(0);
    final Map<String, ODataValue> params = new LinkedHashMap<String, ODataValue>(2);
    params.put("addresses", addresses);
    params.put("index", index);
    final Map<String, Object> keys = new HashMap<String, Object>();
    keys.put("PersonID", 2);
    final ODataInvokeRequest<ODataEntity> resetAddressReq =
            edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Customers").appendKeySegment(keys).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.ResetAddress"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Person"),
                    false,
                    params);
    final ODataEntity resetAddressRes = resetAddressReq.execute().getBody();
    assertNotNull(resetAddressRes);
    assertEquals(2, resetAddressRes.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // RefreshDefaultPI
    final ODataPrimitiveValue newDate = edmClient.getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2014-04-09T00:00:00Z").build();
    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
                    edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI"),
                    new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
                    false,
                    Collections.<String, ODataValue>singletonMap("newDate", newDate));
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }
}
