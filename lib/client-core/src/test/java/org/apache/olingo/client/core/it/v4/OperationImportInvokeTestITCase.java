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
package org.apache.olingo.client.core.it.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
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
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Assume;
import org.junit.BeforeClass;

import org.junit.Test;

public class OperationImportInvokeTestITCase extends AbstractTestITCase {

  private static final String serviceRoot = "http://odatae2etest.azurewebsites.net/javatest/DefaultService";

  // TODO: remove once fit provides function / action imports
  @BeforeClass
  public static void checkServerIsOnline() throws IOException {
    final Socket socket = new Socket();
    boolean reachable = false;
    try {
      socket.connect(new InetSocketAddress("odatae2etest.azurewebsites.net", 80), 2000);
      reachable = true;
    } catch (Exception e) {
      LOG.warn("External test service not reachable, ignoring this whole class: {}",
              OperationImportInvokeTestITCase.class.getName());
    } finally {
      IOUtils.closeQuietly(socket);
    }
    Assume.assumeTrue(reachable);
  }

  private Edm getEdm() {
    final Edm edm = getClient().getRetrieveRequestFactory().
            getMetadataRequest(serviceRoot).execute().getBody();
    assertNotNull(edm);

    return edm;
  }

  private void functionImports(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final Edm edm = getEdm();
    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    // GetDefaultColor
    EdmFunctionImport funcImp = container.getFunctionImport("GetDefaultColor");

    final ODataInvokeRequest<ODataProperty> defaultColorReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0));
    defaultColorReq.setFormat(format);
    final ODataProperty defaultColor = defaultColorReq.execute().getBody();
    assertNotNull(defaultColor);
    assertTrue(defaultColor.hasEnumValue());
    assertEquals("Red", defaultColor.getEnumValue().getValue());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Color", defaultColor.getEnumValue().getTypeName());

    // GetPerson2
    funcImp = container.getFunctionImport("GetPerson2");

    final ODataPrimitiveValue city =
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("London");

    final ODataInvokeRequest<ODataEntity> person2Req = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0),
                    Collections.<String, ODataValue>singletonMap("city", city));
    person2Req.setFormat(format);
    final ODataEntity person2 = person2Req.execute().getBody();
    assertNotNull(person2);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", person2.getTypeName().toString());
    assertEquals(1, person2.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);

    // GetPerson
    funcImp = container.getFunctionImport("GetPerson");

    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("1 Microsoft Way")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("London")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("98052")));

    final ODataInvokeRequest<ODataEntity> personReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0),
                    Collections.<String, ODataValue>singletonMap("address", address));
    personReq.setFormat(format);
    //TODO test service doesn't support yet complex and collection values as inline parameters
    try {
      final ODataEntity person = personReq.execute().getBody();
      assertNotNull(person);
    } catch (Exception e) {
      // ignore
    }

    // GetAllProducts
    funcImp = container.getFunctionImport("GetAllProducts");

    final ODataInvokeRequest<ODataEntitySet> productsReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0));
    productsReq.setFormat(format);
    final ODataEntitySet products = productsReq.execute().getBody();
    assertNotNull(products);
    assertEquals(5, products.getCount());

    // GetProductsByAccessLevel
    funcImp = container.getFunctionImport("GetProductsByAccessLevel");

    final ODataEnumValue accessLevel = getClient().getObjectFactory().
            newEnumValue("Microsoft.Test.OData.Services.ODataWCFService.AccessLevel", "None");

    final ODataInvokeRequest<ODataProperty> prodByALReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0),
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
    functionImports(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonFunctionImports() throws EdmPrimitiveTypeException {
    functionImports(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void actionImports(final ODataPubFormat format) {
    final Edm edm = getEdm();
    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    // Discount
    EdmActionImport actImp = container.getActionImport("Discount");

    final ODataPrimitiveValue percentage = getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(22);
    final ODataInvokeRequest<ODataNoContent> discountReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(actImp.getName()).build(),
                    actImp.getUnboundAction(),
                    Collections.<String, ODataValue>singletonMap("percentage", percentage));
    discountReq.setFormat(format);
    final ODataNoContent discount = discountReq.execute().getBody();
    assertNotNull(discount);

    // ResetBossAddress
    actImp = container.getActionImport("ResetBossAddress");

    final ODataComplexValue<ODataProperty> address = getClient().getObjectFactory().
            newLinkedComplexValue("Microsoft.Test.OData.Services.ODataWCFService.Address");
    address.add(client.getObjectFactory().newPrimitiveProperty("Street",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Via Le Mani Dal Naso, 123")));
    address.add(client.getObjectFactory().newPrimitiveProperty("City",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tollo")));
    address.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("66010")));

    final ODataInvokeRequest<ODataProperty> resetBossAddressReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(actImp.getName()).build(),
                    actImp.getUnboundAction(),
                    Collections.<String, ODataValue>singletonMap("address", address));
    resetBossAddressReq.setFormat(format);
    final ODataProperty resetBossAddress = resetBossAddressReq.execute().getBody();
    assertNotNull(resetBossAddress);
  }

  @Test
  public void atomActionImports() {
    //TODO test service doesn't support yet Atom POST params
    try {
      actionImports(ODataPubFormat.ATOM);
    } catch (Exception e) {
      // ignore
    }
  }

  @Test
  public void jsonActionImports() {
    actionImports(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void bossEmails(final ODataPubFormat format) {
    final Edm edm = getEdm();
    final EdmEntityContainer container = edm.getSchemas().get(0).getEntityContainer();
    assertNotNull(container);

    // ResetBossEmail
    final EdmActionImport actImp = container.getActionImport("ResetBossEmail");

    final ODataCollectionValue<org.apache.olingo.commons.api.domain.v4.ODataValue> emails =
            getClient().getObjectFactory().newCollectionValue(
                    EdmPrimitiveTypeKind.String.getFullQualifiedName().toString());
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("first@olingo.apache.org"));
    emails.add(getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("second@olingo.apache.org"));
    ODataInvokeRequest<ODataProperty> bossEmailsReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(actImp.getName()).build(),
                    actImp.getUnboundAction(),
                    Collections.<String, ODataValue>singletonMap("emails", emails));
    bossEmailsReq.setFormat(format);
    final ODataProperty bossEmails = bossEmailsReq.execute().getBody();
    assertNotNull(bossEmails);
    assertTrue(bossEmails.hasCollectionValue());
    assertEquals(2, bossEmails.getCollectionValue().size());

    EdmFunctionImport funcImp = container.getFunctionImport("GetBossEmails");

    final Map<String, ODataValue> params = new LinkedHashMap<String, ODataValue>(2);
    params.put("start", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(0));
    params.put("count", getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(100));
    bossEmailsReq = getClient().getInvokeRequestFactory().
            getInvokeRequest(getClient().getURIBuilder(serviceRoot).
                    appendOperationCallSegment(funcImp.getName()).build(),
                    funcImp.getUnboundFunctions().get(0),
                    params);
    bossEmailsReq.setFormat(format);
    final ODataProperty bossEmailsViaGET = bossEmailsReq.execute().getBody();
    assertNotNull(bossEmailsViaGET);
    assertTrue(bossEmailsViaGET.hasCollectionValue());
    assertEquals(2, bossEmailsViaGET.getCollectionValue().size());
    assertEquals(bossEmails, bossEmailsViaGET);
  }

  @Test
  public void atomBossEmails() throws EdmPrimitiveTypeException {
    //TODO test service doesn't support yet Atom POST params
    try {
      bossEmails(ODataPubFormat.ATOM);
    } catch (Exception e) {
      // ignore
    }
  }

  @Test
  public void jsonBossEmails() throws EdmPrimitiveTypeException {
    bossEmails(ODataPubFormat.JSON_FULL_METADATA);
  }

}
