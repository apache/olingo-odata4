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
import org.apache.olingo.client.api.communication.request.invoke.ODataNoContent;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataComplexValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataEnumValue;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class OperationImportInvokeTestITCase extends AbstractTestITCase {

  private void functionImports(final ODataFormat format) throws EdmPrimitiveTypeException {
    // GetDefaultColor
    final ODataInvokeRequest<ODataProperty> defaultColorReq = getClient().getInvokeRequestFactory().
            getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("GetDefaultColor").build(), ODataProperty.class);
    defaultColorReq.setFormat(format);
    final ODataProperty defaultColor = defaultColorReq.execute().getBody();
    assertNotNull(defaultColor);
    assertTrue(defaultColor.hasEnumValue());
    assertEquals("Red", defaultColor.getEnumValue().getValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Color", defaultColor.getEnumValue().getTypeName());

    // GetPerson2
    final ODataPrimitiveValue city = getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("London");

    final ODataInvokeRequest<ODataEntity> person2Req = getClient().getInvokeRequestFactory().
            getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("GetPerson2").build(), ODataEntity.class,
                    Collections.<String, ODataValue>singletonMap("city", city));
    person2Req.setFormat(format);
    final ODataEntity person2 = person2Req.execute().getBody();
    assertNotNull(person2);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", person2.getTypeName().toString());
    assertEquals(1, person2.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetPerson
    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("1 Microsoft Way")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("London")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("98052")));

    final ODataInvokeRequest<ODataEntity> personReq = getClient().getInvokeRequestFactory().
            getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("GetPerson").build(), ODataEntity.class,
                    Collections.<String, ODataValue>singletonMap("address", address));
    personReq.setFormat(format);
    final ODataEntity person = personReq.execute().getBody();
    assertNotNull(person);
    assertEquals(person2, person);

    // GetAllProducts
    final ODataInvokeRequest<ODataEntitySet> productsReq = getClient().getInvokeRequestFactory()
            .getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("GetAllProducts").build(), ODataEntitySet.class);
    productsReq.setFormat(format);
    final ODataEntitySet products = productsReq.execute().getBody();
    assertNotNull(products);
    assertEquals(5, products.getCount());

    // GetProductsByAccessLevel
    final ODataEnumValue accessLevel = getClient().getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "None");

    final ODataInvokeRequest<ODataProperty> prodByALReq = getClient().getInvokeRequestFactory().
            getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("GetProductsByAccessLevel").build(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("accessLevel", accessLevel));
    prodByALReq.setFormat(format);
    final ODataProperty prodByAL = prodByALReq.execute().getBody();
    assertNotNull(prodByAL);
    assertTrue(prodByAL.hasCollectionValue());
    assertEquals(5, prodByAL.getCollectionValue().size());
    assertTrue(prodByAL.getCollectionValue().asJavaCollection().contains("Car"));
  }

  @Test
  public void atomFunctionImports() throws EdmPrimitiveTypeException {
    functionImports(ODataFormat.ATOM);
  }

  @Test
  public void jsonFunctionImports() throws EdmPrimitiveTypeException {
    functionImports(ODataFormat.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledFunctionImports() throws EdmPrimitiveTypeException {
    // GetDefaultColor
    final ODataInvokeRequest<ODataProperty> defaultColorReq = edmClient.getInvokeRequestFactory().
            getFunctionImportInvokeRequest("GetDefaultColor");
    final ODataProperty defaultColor = defaultColorReq.execute().getBody();
    assertNotNull(defaultColor);
    assertTrue(defaultColor.hasEnumValue());
    assertEquals("Red", defaultColor.getEnumValue().getValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Color", defaultColor.getEnumValue().getTypeName());

    // GetPerson2
    final ODataPrimitiveValue city =
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("London");
    final ODataInvokeRequest<ODataEntity> person2Req = edmClient.getInvokeRequestFactory().
            getFunctionImportInvokeRequest(
                    "GetPerson2", Collections.<String, ODataValue>singletonMap("city", city));
    final ODataEntity person2 = person2Req.execute().getBody();
    assertNotNull(person2);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", person2.getTypeName().toString());
    assertEquals(1, person2.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetPerson
    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("1 Microsoft Way")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("London")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("98052")));

    final ODataInvokeRequest<ODataEntity> personReq = edmClient.getInvokeRequestFactory().
            getFunctionImportInvokeRequest(
                    "GetPerson", Collections.<String, ODataValue>singletonMap("address", address));
    final ODataEntity person = personReq.execute().getBody();
    assertNotNull(person);
    assertEquals(person2, person);

    // GetAllProducts
    final ODataInvokeRequest<ODataEntitySet> productsReq = edmClient.getInvokeRequestFactory().
            getFunctionImportInvokeRequest("GetAllProducts");
    final ODataEntitySet products = productsReq.execute().getBody();
    assertNotNull(products);
    assertEquals(5, products.getCount());

    // GetProductsByAccessLevel
    final ODataEnumValue accessLevel = getClient().getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "None");

    final ODataInvokeRequest<ODataProperty> prodByALReq = edmClient.getInvokeRequestFactory().
            getFunctionImportInvokeRequest(
                    "GetProductsByAccessLevel",
                    Collections.<String, ODataValue>singletonMap("accessLevel", accessLevel));
    final ODataProperty prodByAL = prodByALReq.execute().getBody();
    assertNotNull(prodByAL);
    assertTrue(prodByAL.hasCollectionValue());
    assertEquals(5, prodByAL.getCollectionValue().size());
    assertTrue(prodByAL.getCollectionValue().asJavaCollection().contains("Car"));
  }

  private void actionImports(final ODataFormat format) {
    // Discount
    final ODataPrimitiveValue percentage = getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(22);
    final ODataInvokeRequest<ODataNoContent> discountReq = getClient().getInvokeRequestFactory().
            getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("Discount").build(), ODataNoContent.class,
                    Collections.<String, ODataValue>singletonMap("percentage", percentage));
    discountReq.setFormat(format);
    final ODataNoContent discount = discountReq.execute().getBody();
    assertNotNull(discount);

    // ResetBossAddress
    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Via Le Mani Dal Naso, 123")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));

    final ODataInvokeRequest<ODataProperty> resetBossAddressReq = getClient().getInvokeRequestFactory().
            getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("ResetBossAddress").build(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("address", address));
    resetBossAddressReq.setFormat(format);
    final ODataProperty resetBossAddress = resetBossAddressReq.execute().getBody();
    assertNotNull(resetBossAddress);
    assertEquals(address, resetBossAddress.getComplexValue());
  }

  @Test
  public void atomActionImports() {
    actionImports(ODataFormat.ATOM);
  }

  @Test
  public void jsonActionImports() {
    actionImports(ODataFormat.JSON_FULL_METADATA);
  }

  @Test
  public void edmEnabledActionImports() {
    // Discount
    final ODataPrimitiveValue percentage = getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(22);
    final ODataInvokeRequest<ODataNoContent> discountReq = edmClient.getInvokeRequestFactory().
            getActionImportInvokeRequest(
                    "Discount", Collections.<String, ODataValue>singletonMap("percentage", percentage));
    final ODataNoContent discount = discountReq.execute().getBody();
    assertNotNull(discount);

    // ResetBossAddress
    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Via Le Mani Dal Naso, 123")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));

    final ODataInvokeRequest<ODataProperty> resetBossAddressReq = edmClient.getInvokeRequestFactory().
            getActionImportInvokeRequest(
                    "ResetBossAddress", Collections.<String, ODataValue>singletonMap("address", address));
    final ODataProperty resetBossAddress = resetBossAddressReq.execute().getBody();
    assertNotNull(resetBossAddress);
    assertEquals(address.getTypeName(), resetBossAddress.getComplexValue().getTypeName());
  }

  private void bossEmails(final ODataFormat format) {
    // ResetBossEmail
    final ODataCollectionValue<org.apache.olingo.commons.api.domain.v4.ODataValue> emails =
            getClient().getObjectFactory().newCollectionValue("Collection(Edm.String)");
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("first@olingo.apache.org"));
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("second@olingo.apache.org"));
    ODataInvokeRequest<ODataProperty> bossEmailsReq = getClient().getInvokeRequestFactory().
            getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
                    appendOperationCallSegment("ResetBossEmail").build(), ODataProperty.class,
                    Collections.<String, ODataValue>singletonMap("emails", emails));
    bossEmailsReq.setFormat(format);
    final ODataProperty bossEmails = bossEmailsReq.execute().getBody();
    assertNotNull(bossEmails);
    assertTrue(bossEmails.hasCollectionValue());
    assertEquals(2, bossEmails.getCollectionValue().size());

    final Map<String, ODataValue> params = new LinkedHashMap<String, ODataValue>(2);
    params.put("start", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(0));
    params.put("count", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(100));
    bossEmailsReq = getClient().getInvokeRequestFactory().getFunctionInvokeRequest(
            getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetBossEmails").build(), ODataProperty.class, params);
    bossEmailsReq.setFormat(format);
    final ODataProperty bossEmailsViaGET = bossEmailsReq.execute().getBody();
    assertNotNull(bossEmailsViaGET);
    assertTrue(bossEmailsViaGET.hasCollectionValue());
    assertEquals(2, bossEmailsViaGET.getCollectionValue().size());
    assertEquals(bossEmails.getCollectionValue().asJavaCollection(),
            bossEmailsViaGET.getCollectionValue().asJavaCollection());
  }

  @Test
  public void atomBossEmails() throws EdmPrimitiveTypeException {
    bossEmails(ODataFormat.ATOM);
  }

  @Test
  public void jsonBossEmails() throws EdmPrimitiveTypeException {
    bossEmails(ODataFormat.JSON_FULL_METADATA);
  }
}
