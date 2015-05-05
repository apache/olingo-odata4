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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.olingo.client.api.communication.request.invoke.ClientNoContent;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class OperationImportInvokeTestITCase extends AbstractTestITCase {

  private void functionImports(final ODataFormat format) throws EdmPrimitiveTypeException {
    // GetDefaultColor
    final ODataInvokeRequest<ClientProperty> defaultColorReq = getClient().getInvokeRequestFactory().
        getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetDefaultColor").build(), ClientProperty.class);
    defaultColorReq.setFormat(format);
    final ClientProperty defaultColor = defaultColorReq.execute().getBody();
    assertNotNull(defaultColor);
    assertTrue(defaultColor.hasEnumValue());
    assertEquals("Red", defaultColor.getEnumValue().getValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Color", defaultColor.getEnumValue().getTypeName());

    // GetPerson2
    final ClientPrimitiveValue city = getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("London");

    final ODataInvokeRequest<ClientEntity> person2Req = getClient().getInvokeRequestFactory().
        getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetPerson2").build(), ClientEntity.class,
            Collections.<String, ClientValue> singletonMap("city", city));
    person2Req.setFormat(format);
    final ClientEntity person2 = person2Req.execute().getBody();
    assertNotNull(person2);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", person2.getTypeName().toString());
    assertEquals(1, person2.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetPerson
    final ClientComplexValue address = getClient().getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("1 Microsoft Way")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("London")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("98052")));

    final ODataInvokeRequest<ClientEntity> personReq = getClient().getInvokeRequestFactory().
        getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetPerson").build(), ClientEntity.class,
            Collections.<String, ClientValue> singletonMap("address", address));
    personReq.setFormat(format);
    final ClientEntity person = personReq.execute().getBody();
    assertNotNull(person);
    assertEquals(person2, person);

    // GetAllProducts
    final ODataInvokeRequest<ClientEntitySet> productsReq = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetAllProducts").build(), ClientEntitySet.class);
    productsReq.setFormat(format);
    final ClientEntitySet products = productsReq.execute().getBody();
    assertNotNull(products);
    assertEquals(5, products.getEntities().size());

    // GetProductsByAccessLevel
    final ClientEnumValue accessLevel = getClient().getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "None");

    final ODataInvokeRequest<ClientProperty> prodByALReq = getClient().getInvokeRequestFactory().
        getFunctionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("GetProductsByAccessLevel").build(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("accessLevel", accessLevel));
    prodByALReq.setFormat(format);
    final ClientProperty prodByAL = prodByALReq.execute().getBody();
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
    final ODataInvokeRequest<ClientProperty> defaultColorReq = edmClient.getInvokeRequestFactory().
        getFunctionImportInvokeRequest("GetDefaultColor");
    final ClientProperty defaultColor = defaultColorReq.execute().getBody();
    assertNotNull(defaultColor);
    assertTrue(defaultColor.hasEnumValue());
    assertEquals("Red", defaultColor.getEnumValue().getValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Color", defaultColor.getEnumValue().getTypeName());

    // GetPerson2
    final ClientPrimitiveValue city =
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("London");
    final ODataInvokeRequest<ClientEntity> person2Req = edmClient.getInvokeRequestFactory().
        getFunctionImportInvokeRequest(
            "GetPerson2", Collections.<String, ClientValue> singletonMap("city", city));
    final ClientEntity person2 = person2Req.execute().getBody();
    assertNotNull(person2);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", person2.getTypeName().toString());
    assertEquals(1, person2.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetPerson
    final ClientComplexValue address = getClient().getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("1 Microsoft Way")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("London")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("98052")));

    final ODataInvokeRequest<ClientEntity> personReq = edmClient.getInvokeRequestFactory().
        getFunctionImportInvokeRequest(
            "GetPerson", Collections.<String, ClientValue> singletonMap("address", address));
    final ClientEntity person = personReq.execute().getBody();
    assertNotNull(person);
    assertEquals(person2, person);

    // GetAllProducts
    final ODataInvokeRequest<ClientEntitySet> productsReq = edmClient.getInvokeRequestFactory().
        getFunctionImportInvokeRequest("GetAllProducts");
    final ClientEntitySet products = productsReq.execute().getBody();
    assertNotNull(products);
    assertEquals(5, products.getEntities().size());

    // GetProductsByAccessLevel
    final ClientEnumValue accessLevel = getClient().getObjectFactory().
        newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "None");

    final ODataInvokeRequest<ClientProperty> prodByALReq = edmClient.getInvokeRequestFactory().
        getFunctionImportInvokeRequest(
            "GetProductsByAccessLevel",
            Collections.<String, ClientValue> singletonMap("accessLevel", accessLevel));
    final ClientProperty prodByAL = prodByALReq.execute().getBody();
    assertNotNull(prodByAL);
    assertTrue(prodByAL.hasCollectionValue());
    assertEquals(5, prodByAL.getCollectionValue().size());
    assertTrue(prodByAL.getCollectionValue().asJavaCollection().contains("Car"));
  }

  private void actionImports(final ODataFormat format) {
    // Discount
    final ClientPrimitiveValue percentage = getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(22);
    final ODataInvokeRequest<ClientNoContent> discountReq = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("Discount").build(), ClientNoContent.class,
            Collections.<String, ClientValue> singletonMap("percentage", percentage));
    discountReq.setFormat(format);
    final ClientNoContent discount = discountReq.execute().getBody();
    assertNotNull(discount);

    // ResetBossAddress
    final ClientComplexValue address = getClient().getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Via Le Mani Dal Naso, 123")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));

    final ODataInvokeRequest<ClientProperty> resetBossAddressReq = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("ResetBossAddress").build(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("address", address));
    resetBossAddressReq.setFormat(format);
    final ClientProperty resetBossAddress = resetBossAddressReq.execute().getBody();
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
    final ClientPrimitiveValue percentage = getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(22);
    final ODataInvokeRequest<ClientNoContent> discountReq = edmClient.getInvokeRequestFactory().
        getActionImportInvokeRequest(
            "Discount", Collections.<String, ClientValue> singletonMap("percentage", percentage));
    final ClientNoContent discount = discountReq.execute().getBody();
    assertNotNull(discount);

    // ResetBossAddress
    final ClientComplexValue address = getClient().getObjectFactory().
        newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Via Le Mani Dal Naso, 123")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));

    final ODataInvokeRequest<ClientProperty> resetBossAddressReq = edmClient.getInvokeRequestFactory().
        getActionImportInvokeRequest(
            "ResetBossAddress", Collections.<String, ClientValue> singletonMap("address", address));
    final ClientProperty resetBossAddress = resetBossAddressReq.execute().getBody();
    assertNotNull(resetBossAddress);
    assertEquals(address.getTypeName(), resetBossAddress.getComplexValue().getTypeName());
  }

  private void bossEmails(final ODataFormat format) {
    // ResetBossEmail
    final ClientCollectionValue<ClientValue> emails =
        getClient().getObjectFactory().newCollectionValue("Collection(Edm.String)");
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("first@olingo.apache.org"));
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("second@olingo.apache.org"));
    ODataInvokeRequest<ClientProperty> bossEmailsReq = getClient().getInvokeRequestFactory().
        getActionInvokeRequest(getClient().newURIBuilder(testStaticServiceRootURL).
            appendOperationCallSegment("ResetBossEmail").build(), ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("emails", emails));
    bossEmailsReq.setFormat(format);
    final ClientProperty bossEmails = bossEmailsReq.execute().getBody();
    assertNotNull(bossEmails);
    assertTrue(bossEmails.hasCollectionValue());
    assertEquals(2, bossEmails.getCollectionValue().size());

    final Map<String, ClientValue> params = new LinkedHashMap<String, ClientValue>(2);
    params.put("start", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(0));
    params.put("count", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(100));
    bossEmailsReq = getClient().getInvokeRequestFactory().getFunctionInvokeRequest(
        getClient().newURIBuilder(testStaticServiceRootURL).
        appendOperationCallSegment("GetBossEmails").build(), ClientProperty.class, params);
    bossEmailsReq.setFormat(format);
    final ClientProperty bossEmailsViaGET = bossEmailsReq.execute().getBody();
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
