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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeTrue;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class ActionImportITCase extends AbstractParamTecSvcITCase {

  @Test
  public void noReturnTypeAction() throws Exception {
    final ODataInvokeResponse<ClientProperty> response = callAction("AIRT", ClientProperty.class, null, false);
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void primitiveAction() throws Exception {
    final ODataInvokeResponse<ClientProperty> response = callAction("AIRTString", ClientProperty.class, null, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("UARTString string value", response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void primitiveActionMinimalResponse() throws Exception {
    callAction("AIRTString", ClientProperty.class, null, true);
  }

  @Test
  public void primitiveCollectionActionInvalidParameters() throws Exception {
    try {
      callAction("AIRTCollStringTwoParam", ClientProperty.class,
          Collections.singletonMap("ParameterInt16",
              (ClientValue) getFactory().newPrimitiveValueBuilder().buildString("42")),
          false);
      fail("Expected an ODataClientErrorException");
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void primitiveCollectionAction() throws Exception {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterInt16", getFactory().newPrimitiveValueBuilder().buildInt16((short) 3));
    parameters.put("ParameterDuration", getFactory().newPrimitiveValueBuilder()
        .setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal(1)).build());
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollStringTwoParam", ClientProperty.class, parameters, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientCollectionValue<ClientValue> valueArray = response.getBody().getCollectionValue();
    assertEquals(3, valueArray.size());
    Iterator<ClientValue> iterator = valueArray.iterator();
    assertEquals("UARTCollStringTwoParam duration value: PT1S", iterator.next().asPrimitive().toValue());
    assertEquals("UARTCollStringTwoParam duration value: PT2S", iterator.next().asPrimitive().toValue());
    assertEquals("UARTCollStringTwoParam duration value: PT3S", iterator.next().asPrimitive().toValue());
  }

  @Test
  public void complexAction() throws Exception {
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCTTwoPrimParam", ClientProperty.class, buildParameterInt16(3), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientComplexValue complexValue = response.getBody().getComplexValue();
    ClientProperty propInt16 = complexValue.get("PropertyInt16");
    assertNotNull(propInt16);
    assertEquals(3, propInt16.getPrimitiveValue().toValue());
    ClientProperty propString = complexValue.get("PropertyString");
    assertNotNull(propString);
    assertEquals("UARTCTTwoPrimParam string value", propString.getPrimitiveValue().toValue());
  }

  @Test
  public void complexCollectionActionNoContent() throws Exception {
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollCTTwoPrimParam", ClientProperty.class, buildParameterInt16(0), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientCollectionValue<ClientValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(0, complexValueCollection.size());
  }

  @Test
  public void complexCollectionActionSubContent() throws Exception {
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollCTTwoPrimParam", ClientProperty.class, buildParameterInt16(1), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientCollectionValue<ClientValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(1, complexValueCollection.size());
    Iterator<ClientValue> iterator = complexValueCollection.iterator();

    ClientComplexValue next = iterator.next().asComplex();
    assertEquals(16, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test123", next.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void complexCollectionActionAllContent() throws Exception {
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollCTTwoPrimParam", ClientProperty.class, buildParameterInt16(3), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientCollectionValue<ClientValue> complexValueCollection = response.getBody().getCollectionValue();
    assertEquals(3, complexValueCollection.size());
    Iterator<ClientValue> iterator = complexValueCollection.iterator();

    ClientComplexValue next = iterator.next().asComplex();
    assertEquals(16, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test123", next.get("PropertyString").getPrimitiveValue().toValue());

    next = iterator.next().asComplex();
    assertEquals(17, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test456", next.get("PropertyString").getPrimitiveValue().toValue());

    next = iterator.next().asComplex();
    assertEquals(18, next.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test678", next.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void entityActionETTwoKeyTwoPrim() throws Exception {
    final ODataInvokeResponse<ClientEntity> response =
        callAction("AIRTETTwoKeyTwoPrimParam", ClientEntity.class, buildParameterInt16(-365), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntity entity = response.getBody();
    ClientProperty propInt16 = entity.getProperty("PropertyInt16");
    assertNotNull(propInt16);
    assertEquals(-365, propInt16.getPrimitiveValue().toValue());
    ClientProperty propString = entity.getProperty("PropertyString");
    assertNotNull(propString);
    assertEquals("Test String2", propString.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionActionETKeyNav() throws Exception {
    final ODataInvokeResponse<ClientEntitySet> response =
        callAction("AIRTCollETKeyNavParam", ClientEntitySet.class, buildParameterInt16(3), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntitySet entitySet = response.getBody();
    assertEquals(3, entitySet.getEntities().size());
    Integer key = 1;
    for (ClientEntity entity : entitySet.getEntities()) {
      assertEquals(key, entity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
      key++;
    }
  }

  @Test
  public void entityCollectionActionETKeyNavEmptyCollection() throws Exception {
    final ODataInvokeResponse<ClientEntitySet> response =
        callAction("AIRTCollETKeyNavParam", ClientEntitySet.class, buildParameterInt16(0), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntitySet entitySet = response.getBody();
    assertEquals(0, entitySet.getEntities().size());
  }

  @Test
  public void entityCollectionActionETKeyNavNegativeParam() throws Exception {
    final ODataInvokeResponse<ClientEntitySet> response =
        callAction("AIRTCollETKeyNavParam", ClientEntitySet.class, buildParameterInt16(-10), false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntitySet entitySet = response.getBody();
    assertEquals(0, entitySet.getEntities().size());
  }

  @Test
  public void entityCollectionActionETAllPrim() throws Exception {
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(Calendar.HOUR_OF_DAY, 3);
    time.set(Calendar.MINUTE, 0);
    time.set(Calendar.SECOND, 0);
    Map<String, ClientValue> parameters = Collections.singletonMap(
        "ParameterTimeOfDay",
        (ClientValue) getFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.TimeOfDay).setValue(time).build());
    final ODataInvokeResponse<ClientEntitySet> response =
        callAction("AIRTCollESAllPrimParam", ClientEntitySet.class, parameters, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntitySet entitySet = response.getBody();
    assertEquals(3, entitySet.getEntities().size());
    Integer key = 1;
    for (ClientEntity entity : entitySet.getEntities()) {
      assertEquals(key, entity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
      key++;
    }
  }

  @Test
  public void entityActionETAllPrim() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.set(1012, 2, 0, 0, 0, 0);
    final Map<String, ClientValue> parameters = Collections.singletonMap(
        "ParameterDate",
        (ClientValue) getFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.Date).setValue(dateTime).build());
    final ODataInvokeResponse<ClientEntity> response =
        callAction("AIRTESAllPrimParam", ClientEntity.class, parameters, false);
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    assertEquals(TecSvcConst.BASE_URI + "/ESAllPrim(1)", response.getHeader(HttpHeader.LOCATION).iterator().next());
  }

  @Test
  public void entityActionETAllPrimNoContent() throws Exception {
    final ODataInvokeResponse<ClientEntity> response =
        callAction("AIRTESAllPrimParam", ClientEntity.class,
            Collections.singletonMap("ParameterDate",
                (ClientValue) getFactory().newPrimitiveValueBuilder().buildString("2000-02-29")),
            true);
    final String location = TecSvcConst.BASE_URI + "/ESAllPrim(1)";
    assertEquals(location, response.getHeader(HttpHeader.LOCATION).iterator().next());
    assertEquals(location, response.getHeader(HttpHeader.ODATA_ENTITY_ID).iterator().next());
  }

  @Test
  public void airtCollStringTwoParamNotNull() {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterInt16", getFactory().newPrimitiveValueBuilder().buildInt16((short) 2));
    parameters.put("ParameterDuration", getFactory().newPrimitiveValueBuilder().buildDuration(BigDecimal.valueOf(1)));
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollStringTwoParam", ClientProperty.class, parameters, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    ClientCollectionValue<ClientValue> collectionValue = response.getBody().getCollectionValue().asCollection();
    assertEquals(2, collectionValue.size());
    final Iterator<ClientValue> iter = collectionValue.iterator();
    assertEquals("UARTCollStringTwoParam duration value: PT1S", iter.next().asPrimitive().toValue());
    assertEquals("UARTCollStringTwoParam duration value: PT2S", iter.next().asPrimitive().toValue());
  }

  @Test
  public void airtCollStringTwoParamNull() {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterInt16", getFactory().newPrimitiveValueBuilder().buildInt16((short) 2));
    parameters.put("ParameterDuration", getFactory().newPrimitiveValueBuilder().buildDuration(null));
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTCollStringTwoParam", ClientProperty.class, parameters, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    ClientCollectionValue<ClientValue> collectionValue = response.getBody().getCollectionValue().asCollection();
    assertEquals(2, collectionValue.size());
    final Iterator<ClientValue> iter = collectionValue.iterator();

    assertEquals("UARTCollStringTwoParam int16 value: 2", iter.next().asPrimitive().toValue());
    assertEquals("UARTCollStringTwoParam duration value: null", iter.next().asPrimitive().toValue());
  }

  @Test
  public void allParameterKinds() {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterEnum", getFactory().newEnumValue(null, "String3"));
    parameters.put("ParameterDef", getFactory().newPrimitiveValueBuilder().build());
    parameters.put("ParameterComp", getFactory().newComplexValue(null)
        .add(getFactory().newPrimitiveProperty("PropertyInt16",
            getFactory().newPrimitiveValueBuilder().buildInt16((short) 3))));
    parameters.put("ParameterETTwoPrim", getFactory().newComplexValue(null));
    parameters.put("CollParameterByte", getFactory().newCollectionValue(null)
        .add(getFactory().newPrimitiveValueBuilder().buildInt16((short) 10)));
    parameters.put("CollParameterEnum", getFactory().newCollectionValue(null)
        .add(getFactory().newEnumValue(null, "String1")));
    parameters.put("CollParameterDef", getFactory().newCollectionValue(null)
        .add(getFactory().newPrimitiveValueBuilder().setValue("CollDefString").build()));
    parameters.put("CollParameterComp", getFactory().newCollectionValue(null)
        .add(getFactory().newComplexValue(null)
            .add(getFactory().newPrimitiveProperty("PropertyString",
                getFactory().newPrimitiveValueBuilder().setValue("CollCompString").build()))));
    parameters.put("CollParameterETTwoPrim", getFactory().newCollectionValue(null));
    final ODataInvokeResponse<ClientProperty> response =
        callAction("AIRTByteNineParam", ClientProperty.class, parameters, false);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(6, response.getBody().getPrimitiveValue().toValue());
  }

  private Map<String, ClientValue> buildParameterInt16(final int value) {
    return Collections.singletonMap("ParameterInt16",
        (ClientValue) getFactory().newPrimitiveValueBuilder().buildInt16((short) value));
  }

  private <T extends ClientInvokeResult> ODataInvokeResponse<T> callAction(final String name,
      final Class<T> resultRef, final Map<String, ClientValue> parameters, final boolean returnMinimal) {
    assumeTrue("The client would send wrongly formatted parameters in XML.",
        parameters == null || parameters.isEmpty() || isJson());  // TODO: XML case
    final URI actionURI = getClient().newURIBuilder(TecSvcConst.BASE_URI).appendActionCallSegment(name).build();
    ODataInvokeRequest<T> request = getClient().getInvokeRequestFactory()
        .getActionInvokeRequest(actionURI, resultRef, parameters);
    if (returnMinimal) {
      request.setPrefer(getClient().newPreferences().returnMinimal());
    }
    // We can re-use the session since our actions don't (yet?!) modify existing data.
    setCookieHeader(request);
    final ODataInvokeResponse<T> response = request.execute();
    saveCookieHeader(response);
    if (returnMinimal) {
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
      assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    }
    return response;
  }
}
