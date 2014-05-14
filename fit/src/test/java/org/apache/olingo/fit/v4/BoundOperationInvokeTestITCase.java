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
import org.apache.olingo.commons.api.domain.v4.Singleton;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class BoundOperationInvokeTestITCase extends AbstractTestITCase {

  private Edm getEdm() {
    final Edm edm = client.getRetrieveRequestFactory().getMetadataRequest(testStaticServiceRootURL).execute().getBody();
    assertNotNull(edm);

    return edm;
  }

  private void functions(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final Edm edm = getEdm();
    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    // GetEmployeesCount
    URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<Singleton> singletonReq =
            client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singletonReq.setFormat(format);
    final Singleton company = singletonReq.execute().getBody();
    assertNotNull(company);

    ODataOperation boundOp = company.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetEmployeesCount");
    assertNotNull(boundOp);

    EdmFunction func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), company.getTypeName(),
            false, null);
    assertNotNull(func);

    final ODataInvokeRequest<ODataProperty> getEmployeesCountReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func);
    getEmployeesCountReq.setFormat(format);
    final ODataProperty getEmployeesCountRes = getEmployeesCountReq.execute().getBody();
    assertNotNull(getEmployeesCountRes);
    assertTrue(getEmployeesCountRes.hasPrimitiveValue());

    // GetProductDetails
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Products").appendKeySegment(5);
    ODataEntityRequest<ODataEntity> entityReq = client.getRetrieveRequestFactory().
            getEntityRequest(builder.build());
    entityReq.setFormat(format);
    ODataEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetProductDetails");
    assertNotNull(boundOp);

    func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false, null);
    assertNotNull(func);

    final ODataPrimitiveValue count = client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(1);
    final ODataInvokeRequest<ODataEntitySet> getProductDetailsReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func,
                    Collections.<String, ODataValue>singletonMap("count", count));
    getProductDetailsReq.setFormat(format);
    final ODataEntitySet getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertEquals(1, getProductDetailsRes.getCount());

    // GetRelatedProduct
    final Map<String, Object> keyMap = new LinkedHashMap<String, Object>();
    keyMap.put("ProductID", 6);
    keyMap.put("ProductDetailID", 1);
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("ProductDetails").appendKeySegment(keyMap);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetRelatedProduct");
    assertNotNull(boundOp);

    func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false, null);
    assertNotNull(func);

    final ODataInvokeRequest<ODataEntity> getRelatedProductReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func);
    getRelatedProductReq.setFormat(format);
    final ODataEntity getRelatedProductRes = getRelatedProductReq.execute().getBody();
    assertNotNull(getRelatedProductRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Product",
            getRelatedProductRes.getTypeName().toString());
    assertEquals(6, getRelatedProductRes.getProperty("ProductID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetDefaultPI
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetDefaultPI");
    assertNotNull(boundOp);

    func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false, null);
    assertNotNull(func);

    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func);
    getDefaultPIReq.setFormat(format);
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(101901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetAccountInfo
    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.GetAccountInfo");
    assertNotNull(boundOp);

    func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false, null);
    assertNotNull(func);

    final ODataInvokeRequest<ODataProperty> getAccountInfoReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func);
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

    func = edm.getBoundFunction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false, null);
    assertNotNull(func);

    final ODataPrimitiveValue bonusRate = client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(1.1);
    final ODataInvokeRequest<ODataProperty> getActualAmountReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), func,
                    Collections.<String, ODataValue>singletonMap("bonusRate", bonusRate));
    getActualAmountReq.setFormat(format);
    final ODataProperty getActualAmountRes = getActualAmountReq.execute().getBody();
    assertNotNull(getActualAmountRes);
    assertEquals(41.79, getActualAmountRes.getPrimitiveValue().toCastValue(Double.class), 0);
  }

  @Test
  public void atomFunctions() throws EdmPrimitiveTypeException {
    functions(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonFunctions() throws EdmPrimitiveTypeException {
    functions(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void actions(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final Edm edm = getEdm();
    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    // IncreaseRevenue
    URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    ODataEntityRequest<ODataEntity> entityReq =
            client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    ODataEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);

    ODataOperation boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.IncreaseRevenue");
    assertNotNull(boundOp);

    EdmAction act = edm.getBoundAction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false);
    assertNotNull(act);

    final ODataPrimitiveValue increaseValue =
            client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(12L);
    final ODataInvokeRequest<ODataProperty> increaseRevenueReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), act,
                    Collections.<String, ODataValue>singletonMap("IncreaseValue", increaseValue));
    increaseRevenueReq.setFormat(format);
    final ODataProperty increaseRevenueRes = increaseRevenueReq.execute().getBody();
    assertNotNull(increaseRevenueRes);
    assertTrue(increaseRevenueRes.hasPrimitiveValue());

    // AddAccessRight
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Products").appendKeySegment(5);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.AddAccessRight");
    assertNotNull(boundOp);

    act = edm.getBoundAction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false);
    assertNotNull(act);

    final ODataEnumValue accessRight = client.getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "Execute");
    final ODataInvokeRequest<ODataProperty> getProductDetailsReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), act,
                    Collections.<String, ODataValue>singletonMap("accessRight", accessRight));
    getProductDetailsReq.setFormat(format);
    final ODataProperty getProductDetailsRes = getProductDetailsReq.execute().getBody();
    assertNotNull(getProductDetailsRes);
    assertTrue(getProductDetailsRes.hasEnumValue());

    // ResetAddress
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(2);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.ResetAddress");
    assertNotNull(boundOp);

    act = edm.getBoundAction(new FullQualifiedName(boundOp.getTitle()),
            edm.getEntityType(entity.getTypeName()).getBaseType().getFullQualifiedName(), false);
    assertNotNull(act);

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
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), act, params);
    resetAddressReq.setFormat(format);
    final ODataEntity resetAddressRes = resetAddressReq.execute().getBody();
    assertNotNull(resetAddressRes);
    assertEquals(2, resetAddressRes.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // RefreshDefaultPI
    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101);
    entityReq = client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(format);
    entity = entityReq.execute().getBody();
    assertNotNull(entity);

    boundOp = entity.getOperation("Microsoft.Test.OData.Services.ODataWCFService.RefreshDefaultPI");
    assertNotNull(boundOp);

    act = edm.getBoundAction(new FullQualifiedName(boundOp.getTitle()), entity.getTypeName(), false);
    assertNotNull(act);

    final ODataPrimitiveValue newDate = client.getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2014-04-09T00:00:00Z").build();
    final ODataInvokeRequest<ODataEntity> getDefaultPIReq =
            client.getInvokeRequestFactory().getInvokeRequest(boundOp.getTarget(), act,
                    Collections.<String, ODataValue>singletonMap("newDate", newDate));
    getDefaultPIReq.setFormat(format);
    final ODataEntity getDefaultPIRes = getDefaultPIReq.execute().getBody();
    assertNotNull(getDefaultPIRes);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument",
            getDefaultPIRes.getTypeName().toString());
    assertEquals(101901,
            getDefaultPIRes.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomActions() throws EdmPrimitiveTypeException {
    actions(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonActions() throws EdmPrimitiveTypeException {
    actions(ODataPubFormat.JSON_FULL_METADATA);
  }

}
