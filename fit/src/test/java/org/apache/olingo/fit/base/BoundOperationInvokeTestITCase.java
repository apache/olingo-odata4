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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class BoundOperationInvokeTestITCase extends AbstractTestITCase {

  private void functions(final ContentType contentType) throws EdmPrimitiveTypeException {
    // GetEmployeesCount
    URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<ClientSingleton> singletonReq =
        client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singletonReq.setFormat(contentType);
    final ClientSingleton company = singletonReq.execute().getBody();
    assertNotNull(company);

    ClientOperation boundOp = company.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ClientProperty> getEmployeesCountReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientProperty.class);
    getEmployeesCountReq.setFormat(contentType);
    final ClientProperty getEmployeesCountRes = getEmployeesCountReq.execute().getBody();
    assertNotNull(getEmployeesCountRes);
    assertTrue(getEmployeesCountRes.hasPrimitiveValue());

    // GetProductDetails
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Products").appendKeySegment(5);
    ODataEntityRequest<ClientEntity> entityReq = client.getRetrieveRequestFactory().
        getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    ClientEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails");
    assertNotNull(boundOp);

    final ClientPrimitiveValue count = client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1);
    final ODataInvokeRequest<ClientEntitySet> getProductDetailsReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientEntitySet.class,
            Collections.<String, ClientValue> singletonMap("count", count));
    getProductDetailsReq.setFormat(contentType);
    final ClientEntitySet getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertEquals(1, getProductDetailsRes.getEntities().size());

    // GetRelatedProduct
    final Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
    keyMap.put("ProductID", 6);
    keyMap.put("ProductDetailID", 1);
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("ProductDetails").appendKeySegment(keyMap);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ClientEntity> getRelatedProductReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientEntity.class);
    getRelatedProductReq.setFormat(contentType);
    final ClientEntity getRelatedProductRes = getRelatedProductReq.execute().getBody();
    assertNotNull(getRelatedProductRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Product",
        getRelatedProductRes.getTypeName().toString());
    assertEquals(6, getRelatedProductRes.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetDefaultPI
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(102);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ClientEntity> getDefaultPIReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientEntity.class);
    getDefaultPIReq.setFormat(contentType);
    final ClientEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
        getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
        getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetAccountInfo
    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo");
    assertNotNull(boundOp);

    final ODataInvokeRequest<ClientProperty> getAccountInfoReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientProperty.class);
    getAccountInfoReq.setFormat(contentType);
    final ClientProperty getAccountInfoRes = getAccountInfoReq.execute().getBody();
    assertNotNull(getAccountInfoRes);
    assertTrue(getAccountInfoRes.hasComplexValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
        getAccountInfoRes.getComplexValue().getTypeName());

    // GetActualAmount
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(
        entity.getNavigationLink("MyGiftCard").getLink());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);
    assertEquals(301, entity.getProperty("GiftCardID").getPrimitiveValue().toCastValue(Integer.class), 0);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetActualAmount");
    assertNotNull(boundOp);

    final ClientPrimitiveValue bonusRate = client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(1.1);
    final ODataInvokeRequest<ClientProperty> getActualAmountReq =
        client.getInvokeRequestFactory().getFunctionInvokeRequest(boundOp.getTarget(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("bonusRate", bonusRate));
    getActualAmountReq.setFormat(contentType);
    final ClientProperty getActualAmountRes = getActualAmountReq.execute().getBody();
    assertNotNull(getActualAmountRes);
    assertEquals(41.79, getActualAmountRes.getPrimitiveValue().toCastValue(Double.class), 0);
  }

  @Test
  public void atomFunctions() throws EdmPrimitiveTypeException {
    functions(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonFunctions() throws EdmPrimitiveTypeException {
    functions(ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledFunctions() throws EdmPrimitiveTypeException {
    // GetEmployeesCount
    final ODataInvokeRequest<ClientProperty> getEmployeesCountReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            edmClient.newURIBuilder().appendSingletonSegment("Company").build(),
            new FullQualifiedName(("Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount")),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"),
            false);
    final ClientProperty getEmployeesCountRes = getEmployeesCountReq.execute().getBody();
    assertNotNull(getEmployeesCountRes);
    assertTrue(getEmployeesCountRes.hasPrimitiveValue());

    // GetProductDetails
    final ClientPrimitiveValue count = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1);
    final ODataInvokeRequest<ClientEntitySet> getProductDetailsReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Products").appendKeySegment(5).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"),
            false,
            Collections.<String, ClientValue> singletonMap("count", count));
    final ClientEntitySet getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertEquals(1, getProductDetailsRes.getEntities().size());

    // GetRelatedProduct
    final Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
    keyMap.put("ProductID", 6);
    keyMap.put("ProductDetailID", 1);
    URIBuilder builder = edmClient.newURIBuilder().appendEntitySetSegment("ProductDetails").appendKeySegment(keyMap);

    final ODataInvokeRequest<ClientEntity> getRelatedProductReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            builder.build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.ProductDetail"),
            false);
    final ClientEntity getRelatedProductRes = getRelatedProductReq.execute().getBody();
    assertNotNull(getRelatedProductRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Product",
        getRelatedProductRes.getTypeName().toString());
    assertEquals(6, getRelatedProductRes.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetDefaultPI
    final ODataInvokeRequest<ClientEntity> getDefaultPIReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
            false);
    final ClientEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
        getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
        getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetAccountInfo
    final ODataInvokeRequest<ClientProperty> getAccountInfoReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
            false);
    final ClientProperty getAccountInfoRes = getAccountInfoReq.execute().getBody();
    assertNotNull(getAccountInfoRes);
    assertTrue(getAccountInfoRes.hasComplexValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.AccountInfo",
        getAccountInfoRes.getComplexValue().getTypeName());

    // GetActualAmount
    final ClientPrimitiveValue bonusRate = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildDouble(1.1);
    final ODataInvokeRequest<ClientProperty> getActualAmountReq =
        edmClient.getInvokeRequestFactory().getBoundFunctionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).
            appendNavigationSegment("MyGiftCard").build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GetActualAmount"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.GiftCard"),
            false,
            Collections.<String, ClientValue> singletonMap("bonusRate", bonusRate));
    final ClientProperty getActualAmountRes = getActualAmountReq.execute().getBody();
    assertNotNull(getActualAmountRes);
    assertEquals(41.79, getActualAmountRes.getPrimitiveValue().toCastValue(Double.class), 0);
  }

  private void actions(final ContentType contentType) throws EdmPrimitiveTypeException {
    // IncreaseRevenue
    URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    ODataEntityRequest<ClientEntity> entityReq =
        client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    ClientEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    ClientOperation boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue");
    assertNotNull(boundOp);

    final ClientPrimitiveValue increaseValue =
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(12L);
    final ODataInvokeRequest<ClientProperty> increaseRevenueReq =
        client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("IncreaseValue", increaseValue));
    increaseRevenueReq.setFormat(contentType);
    final ClientProperty increaseRevenueRes = increaseRevenueReq.execute().getBody();
    assertNotNull(increaseRevenueRes);
    assertTrue(increaseRevenueRes.hasPrimitiveValue());

    // AddAccessRight
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Products").appendKeySegment(5);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight");
    assertNotNull(boundOp);

    final ClientEnumValue accessRight = client.getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute");
    final ODataInvokeRequest<ClientProperty> getProductDetailsReq =
        client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("accessRight", accessRight));
    getProductDetailsReq.setFormat(contentType);
    final ClientProperty getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertTrue(getProductDetailsRes.hasEnumValue());

    // ResetAddress
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(2);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.ResetAddress");
    assertNotNull(boundOp);

    final ClientCollectionValue<ClientValue> addresses =
        client.getObjectFactory().
        newCollectionValue("Collection(Microsoft.Test.OData.Services.ODataWCFService.Address)");
    final ClientComplexValue address = client.getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Piazza La Bomba E Scappa")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));
    addresses.add(address);
    final ClientPrimitiveValue index = client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(0);
    final Map<String, ClientValue> params = new LinkedHashMap<String, ClientValue>(2);
    params.put("addresses", addresses);
    params.put("index", index);
    final ODataInvokeRequest<ClientEntity> resetAddressReq =
        client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ClientEntity.class, params);
    resetAddressReq.setFormat(contentType);
    final ClientEntity resetAddressRes = resetAddressReq.execute().getBody();
    assertNotNull(resetAddressRes);
    assertEquals(2, resetAddressRes.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // RefreshDefaultPI
    builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(102);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI");
    assertNotNull(boundOp);

    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(2014, 3, 9, 0, 0, 0);
    final ClientPrimitiveValue newDate = client.getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build();
    final ODataInvokeRequest<ClientEntity> getDefaultPIReq =
        client.getInvokeRequestFactory().getActionInvokeRequest(boundOp.getTarget(), ClientEntity.class,
            Collections.<String, ClientValue> singletonMap("newDate", newDate));
    getDefaultPIReq.setFormat(contentType);
    final ClientEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
        getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
        getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomActions() throws EdmPrimitiveTypeException {
    actions(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonActions() throws EdmPrimitiveTypeException {
    actions(ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledActions() throws EdmPrimitiveTypeException {
    // IncreaseRevenue
    final ClientPrimitiveValue increaseValue = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt64(12L);
    final ODataInvokeRequest<ClientProperty> increaseRevenueReq =
        edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
            edmClient.newURIBuilder().appendSingletonSegment("Company").build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"),
            false,
            Collections.<String, ClientValue> singletonMap("IncreaseValue", increaseValue));
    final ClientProperty increaseRevenueRes = increaseRevenueReq.execute().getBody();
    assertNotNull(increaseRevenueRes);
    assertTrue(increaseRevenueRes.hasPrimitiveValue());

    // AddAccessRight
    final ClientEnumValue accessRight = edmClient.getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute");
    final ODataInvokeRequest<ClientProperty> getProductDetailsReq =
        edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Products").appendKeySegment(5).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Product"),
            false,
            Collections.<String, ClientValue> singletonMap("accessRight", accessRight));
    getProductDetailsReq.setFormat(ContentType.JSON_FULL_METADATA);
    final ClientProperty getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertTrue(getProductDetailsRes.hasEnumValue());

    // ResetAddress
    final ClientCollectionValue<ClientValue> addresses =
        edmClient.getObjectFactory().
        newCollectionValue("Collection(Microsoft.Test.OData.Services.ODataWCFService.Address)");
    final ClientComplexValue address = edmClient.getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("Street",
        edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("Piazza La Bomba E Scappa")));
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("City",
        edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(edmClient.getObjectFactory().newPrimitiveProperty("PostalCode",
        edmClient.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));
    addresses.add(address);
    final ClientPrimitiveValue index = edmClient.getObjectFactory().newPrimitiveValueBuilder().buildInt32(0);
    final Map<String, ClientValue> params = new LinkedHashMap<String, ClientValue>(2);
    params.put("addresses", addresses);
    params.put("index", index);
    final Map<String, Object> keys = new HashMap<String, Object>();
    keys.put("PersonID", 2);
    final ODataInvokeRequest<ClientEntity> resetAddressReq =
        edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Customers").appendKeySegment(keys).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.ResetAddress"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Person"),
            false,
            params);
    final ClientEntity resetAddressRes = resetAddressReq.execute().getBody();
    assertNotNull(resetAddressRes);
    assertEquals(2, resetAddressRes.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // RefreshDefaultPI
    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(2014, 3, 9, 0, 0, 0);
    final ClientPrimitiveValue newDate = edmClient.getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build();
    final ODataInvokeRequest<ClientEntity> getDefaultPIReq =
        edmClient.getInvokeRequestFactory().getBoundActionInvokeRequest(
            edmClient.newURIBuilder().appendEntitySetSegment("Accounts").appendKeySegment(102).build(),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI"),
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Account"),
            false,
            Collections.<String, ClientValue> singletonMap("newDate", newDate));
    final ClientEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
        getDefaultPIRes.getTypeName().toString());
    assertEquals(102901,
        getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }
}
