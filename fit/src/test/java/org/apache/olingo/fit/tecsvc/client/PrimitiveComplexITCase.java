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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.communication.response.ODataValueUpdateResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;

public class PrimitiveComplexITCase extends AbstractParamTecSvcITCase {

  @Test
  public void readSimpleProperty() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .appendPropertySegment("PropertyString")
            .build());
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String1", property.getPrimitiveValue().toValue());
  }

  @Test
  public void readSimplePropertyContextURL() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .appendPropertySegment("PropertyString")
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);

    if (isJson()) {
      String actualResult = IOUtils.toString(response.getRawResponse(), "UTF-8");
      assertTrue(actualResult.startsWith("{\"@odata.context\":\"../$metadata#ESTwoPrim(32766)/PropertyString\","));
      assertTrue(actualResult.endsWith("\"value\":\"Test String1\"}"));
    } else {
      ClientProperty property = response.getBody();
      assertEquals("Test String1", property.getPrimitiveValue().toValue());
    }
  }

  @Test
  public void deletePrimitive() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766).appendPropertySegment("PropertyString")
        .build();
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uri);
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the property is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataPropertyRequest<ClientProperty> propertyRequest = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    propertyRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), propertyRequest.execute().getStatusCode());

    try {
      getClient().getCUDRequestFactory().getDeleteRequest(getClient().newURIBuilder(SERVICE_URI)
          .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766).appendPropertySegment("PropertyInt16")
          .build()).execute();
      fail("Expected exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void deletePrimitiveCollection() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(7).appendPropertySegment("CollPropertyString")
        .build();
    final ODataDeleteResponse response = getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the property is not gone but empty now.
    // This check has to be in the same session in order to access the same data provider.
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    final ODataRetrieveResponse<ClientProperty> propertyResponse = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), propertyResponse.getStatusCode());
    final ClientProperty property = propertyResponse.getBody();
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertTrue(property.getCollectionValue().isEmpty());
  }

  @Test
  public void readComplexProperty() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp")
            .appendKeySegment(7)
            .appendPropertySegment("PropertyComp")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getComplexValue());
    assertEquals("TEST B", property.getComplexValue().get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void readComplexPropertyContextURL() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp")
            .appendKeySegment(7)
            .appendPropertySegment("PropertyComp")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);

    if (isJson()) {
      String actualResult = IOUtils.toString(response.getRawResponse(), "UTF-8");
      assertTrue(actualResult.startsWith("{\"@odata.context\":\"../$metadata#ESMixPrimCollComp(7)/PropertyComp\","));
      assertTrue(actualResult.endsWith("\"PropertyInt16\":222,\"PropertyString\":\"TEST B\"}"));
    } else {
      ClientProperty property = response.getBody();
      assertEquals((short)222, property.getComplexValue().get("PropertyInt16").getValue().asPrimitive().toValue());
      assertEquals("TEST B", property.getComplexValue().get("PropertyString").getValue().asPrimitive().toValue());      
    }
  }

  @Test
  public void deleteComplex() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(7).appendPropertySegment("PropertyComp")
        .build();
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uri);
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the property is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataPropertyRequest<ClientProperty> propertyRequest = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    propertyRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), propertyRequest.execute().getStatusCode());
  }

  @Test
  public void readUnknownProperty() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .appendPropertySegment("Unknown")
            .build());
    setCookieHeader(request);
    try {
      request.execute();
      fail("Expected exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void readNoContentProperty() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(-32766)
            .appendPropertySegment("PropertyString")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void updatePrimitiveProperty() throws Exception {
    final ODataPropertyUpdateRequest request =
        getClient().getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest(
            getClient().newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766)
                .appendPropertySegment("PropertyString")
                .build(),
            getFactory().newPrimitiveProperty("PropertyString",
                getFactory().newPrimitiveValueBuilder().buildString("Test String1")));
    assertNotNull(request);

    final ODataPropertyUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String1", property.getPrimitiveValue().toValue());
  }

  @Test
  public void patchComplexProperty() throws Exception {
    final ODataPropertyUpdateRequest request =
        getClient().getCUDRequestFactory().getPropertyComplexValueUpdateRequest(
            getClient().newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(7)
                .appendPropertySegment("PropertyComp")
                .build(),
            UpdateType.PATCH,
            getFactory().newComplexProperty("PropertyComp",
                getFactory().newComplexValue(null).add(
                    getFactory().newPrimitiveProperty("PropertyString",
                        getFactory().newPrimitiveValueBuilder().buildString("Test String42")))));
    assertNotNull(request);

    final ODataPropertyUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getComplexValue());
    final ClientComplexValue value = property.getComplexValue();
    assertEquals("Test String42", value.get("PropertyString").getPrimitiveValue().toValue());
    if(isJson()) {
      assertEquals(222, value.get("PropertyInt16").getPrimitiveValue().toValue());
    } else {
      assertEquals((short)222, value.get("PropertyInt16").getPrimitiveValue().toValue());
    }
  }

  @Test
  public void updatePrimitiveCollection() throws Exception {
    final ODataPropertyUpdateRequest request =
        getClient().getCUDRequestFactory().getPropertyCollectionValueUpdateRequest(
            getClient().newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(7)
                .appendPropertySegment("CollPropertyString")
                .build(),
            getFactory().newCollectionProperty("CollPropertyString",
                getFactory().newCollectionValue(null)
                    .add(getFactory().newPrimitiveValueBuilder().buildString("Test String1"))
                    .add(getFactory().newPrimitiveValueBuilder().buildString("Test String2"))));
    assertNotNull(request);

    final ODataPropertyUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    final ClientCollectionValue<ClientValue> value = property.getCollectionValue();
    assertNotNull(value);
    Iterator<ClientValue> iterator = value.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("Test String1", iterator.next().asPrimitive().toValue());
    assertEquals("Test String2", iterator.next().asPrimitive().toValue());
    assertFalse(iterator.hasNext());
  }

  @Test
  public void updateComplexCollection() throws Exception {
    final ODataPropertyUpdateRequest request =
        getClient().getCUDRequestFactory().getPropertyCollectionValueUpdateRequest(
            getClient().newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment(7)
                .appendPropertySegment("CollPropertyComp")
                .build(),
            getFactory().newCollectionProperty("CollPropertyComp", getFactory().newCollectionValue(null)));
    assertNotNull(request);

    final ODataPropertyUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    final ClientCollectionValue<ClientValue> value = property.getCollectionValue();
    assertNotNull(value);
    assertFalse(value.iterator().hasNext());
  }

  @Test
  public void readPropertyValue() throws Exception {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getPropertyValueRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766)
            .appendPropertySegment("PropertyString").appendValueSegment()
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals("Test String1", response.getBody().toValue());
  }

  @Test
  public void deletePropertyValue() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav").appendKeySegment(1)
        .appendPropertySegment("PropertyCompAllPrim").appendPropertySegment("PropertyString")
        .appendValueSegment()
        .build();
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uri);
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the property is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataValueRequest valueRequest = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uri);
    valueRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), valueRequest.execute().getStatusCode());
  }

  @Test
  public void updatePropertyValue() throws Exception {
    final ODataValueUpdateRequest request =
        getClient().getCUDRequestFactory().getValueUpdateRequest(
            getClient().newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766)
                .appendPropertySegment("PropertyString")
                .build(),
            UpdateType.REPLACE,
            getFactory().newPrimitiveValueBuilder().buildString("Test String1"));
    assertNotNull(request);

    final ODataValueUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), response.getContentType());

    final ClientPrimitiveValue value = response.getBody();
    assertNotNull(value);
    assertEquals("Test String1", IOUtils.toString((InputStream) value.toValue(), "UTF-8"));
  }

  @Test
  public void updatePropertyValueMinimalResponse() throws Exception {
    ODataValueUpdateRequest request = getClient().getCUDRequestFactory().getValueUpdateRequest(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(32766)
            .appendPropertySegment("PropertyString")
            .build(),
        UpdateType.REPLACE,
        getFactory().newPrimitiveValueBuilder().buildString("Test String1"));
    request.setPrefer(getClient().newPreferences().returnMinimal());

    final ODataValueUpdateResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
  }

  @Test
  public void readPrimitiveCollectionCount() {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getValueRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCollAllPrim").appendKeySegment(1)
            .appendPropertySegment("CollPropertyBoolean").appendCountSegment().build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), response.getContentType());

    final ClientPrimitiveValue value = response.getBody();
    assertNotNull(value);
    assertEquals("3", value.toValue());
  }

  @Test
  public void readComplexCollectionCount() {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getValueRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCompCollAllPrim").appendKeySegment(5678)
            .appendPropertySegment("PropertyComp").appendPropertySegment("CollPropertyBoolean").appendCountSegment()
            .build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), response.getContentType());

    final ClientPrimitiveValue value = response.getBody();
    assertNotNull(value);
    assertEquals("3", value.toValue());
  }
  
  @Test
  public void retrieveIntPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESAllPrim").appendKeySegment(32767).appendPropertySegment("PropertyInt16");
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    assertEquals("32767", req.execute().getBody().toString());
  }

  @Test
  public void retrieveBooleanPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESAllPrim").appendKeySegment(32767).appendPropertySegment("PropertyBoolean");
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    assertEquals("true", req.execute().getBody().toString());
  }

  @Test
  public void retrieveDatePropertyValueTest() {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESAllPrim").appendKeySegment(32767).appendPropertySegment("PropertyDate");
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertEquals("2012-12-03", property.toString());
  }

  @Test
  public void retrieveDecimalPropertyValueTest() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESAllPrim").appendKeySegment(32767).appendPropertySegment("PropertyDecimal");
    final ODataValueRequest req = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    req.setFormat(ContentType.TEXT_PLAIN);
    final ClientPrimitiveValue property = req.execute().getBody();
    assertEquals("34", property.toString());
  }
}
