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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.uri.ParameterAlias;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.fit.util.StringHelper;
import org.junit.Test;

public class FunctionImportITCase extends AbstractParamTecSvcITCase {

  @Test
  public void entity() throws Exception {
    ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTESTwoKeyNav").build(),
            ClientEntity.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertShortOrInt(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityWithoutEntitySet() throws Exception {
    ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTETKeyNav").build(),
            ClientEntity.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertShortOrInt(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollection() {
    ODataInvokeRequest<ClientEntitySet> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendOperationCallSegment("FICRTCollESTwoKeyNavParam").build(),
            ClientEntitySet.class,
            Collections.<String, ClientValue> singletonMap("ParameterInt16",
                getFactory().newPrimitiveValueBuilder().buildInt32(2)));
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);
    final List<ClientEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    final ClientEntity entity = entities.get(1);
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionWithAppendedKey() {
    // .../odata.svc/FICRTCollESMedia()(1)
    ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollESMedia")
                .appendKeySegment(getFactory().newPrimitiveValueBuilder().buildInt32(1))
                .build(),
            ClientEntity.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionWithAppendedKeyAndProperty() {
    // .../odata.svc/FICRTCollESMedia()(2)/PropertyInt16
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollESMedia")
                .appendKeySegment(getFactory().newPrimitiveValueBuilder().buildInt32(2))
                .appendPropertySegment("PropertyInt16")
                .build(),
            ClientProperty.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(2, property.getPrimitiveValue().toValue());
  }

  @Test
  public void countEntityCollection() throws Exception {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getValueRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTCollESMedia").count().build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals("4", response.getBody().toValue());
  }

  @Test
  public void complexWithPath() throws Exception {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTCTTwoPrim")
            .appendPropertySegment("PropertyInt16").build(),
            ClientProperty.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertShortOrInt(16, property.getPrimitiveValue().toValue());
  }

  @Test
  public void primitiveCollection() throws Exception {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollString").build(),
            ClientProperty.class);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    assertEquals("Employee1@company.example", iterator.next().asPrimitive().toValue());
    assertEquals("Employee2@company.example", iterator.next().asPrimitive().toValue());
    assertEquals("Employee3@company.example", iterator.next().asPrimitive().toValue());
  }

  @Test
  public void primitiveValue() throws Exception {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getPropertyValueRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTString")
                .appendValueSegment().build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals("UFCRTString string value", response.getBody().toValue());
  }

  @Test
  public void primitiveValueWithPath() throws Exception {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getPropertyValueRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCTTwoPrim")
                .appendPropertySegment("PropertyString").appendValueSegment().build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals("UFCRTCTTwoPrim string value", response.getBody().toValue());
  }

  @Test
  public void FICRTStringTwoParamNotNull() {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTStringTwoParam").build(),
            ClientProperty.class,
            buildTwoParameters(3, "ab"));
    setCookieHeader(request);
    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals("\"ab\",\"ab\",\"ab\"", response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void FICRTStringTwoParamNull() {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTStringTwoParam").build(),
            ClientProperty.class,
            Collections.<String, ClientValue> singletonMap("ParameterInt16",
                getFactory().newPrimitiveValueBuilder().buildInt32(1)));
    setCookieHeader(request);
    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void FICRTStringTwoParamWithAliases() {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterInt16", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("first")).build());
    parameters.put("ParameterString", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("second")).build());
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory().getFunctionInvokeRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTStringTwoParam")
            .addParameterAlias("second", "'x'").addParameterAlias("first", "4")
            .build(),
        ClientProperty.class,
        parameters);
    setCookieHeader(request);
    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals("\"x\",\"x\",\"x\",\"x\"", response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void FICRTCollCTTwoPrimTwoParamNotNull() {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTCollCTTwoPrimTwoParam").build(),
        ClientProperty.class,
        buildTwoParameters(3, "TestString"));
    setCookieHeader(request);
    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    final ClientCollectionValue<ClientValue> collection = response.getBody().getCollectionValue().asCollection();
    final Iterator<ClientValue> iter = collection.iterator();

    ClientComplexValue complexValue = iter.next().asComplex();
    assertShortOrInt(1, complexValue.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("UFCRTCollCTTwoPrimTwoParam string value: TestString",
        complexValue.get("PropertyString").getPrimitiveValue().toValue());
    complexValue = iter.next().asComplex();
    assertShortOrInt(2, complexValue.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("UFCRTCollCTTwoPrimTwoParam string value: TestString",
        complexValue.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void FICRTCollCTTwoPrimTwoParamNull() {
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FICRTCollCTTwoPrimTwoParam").build(),
        ClientProperty.class,
        buildTwoParameters(2, null));
    setCookieHeader(request);
    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    final ClientCollectionValue<ClientValue> collection = response.getBody().getCollectionValue().asCollection();
    final Iterator<ClientValue> iter = collection.iterator();

    ClientComplexValue complexValue = iter.next().asComplex();
    assertShortOrInt(1, complexValue.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("UFCRTCollCTTwoPrimTwoParam int16 value: 2",
        complexValue.get("PropertyString").getPrimitiveValue().toValue());
    complexValue = iter.next().asComplex();
    assertShortOrInt(2, complexValue.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("UFCRTCollCTTwoPrimTwoParamstring value: null",
        complexValue.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void allParameterKinds() {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterEnum", getFactory().newEnumValue("Namespace1_Alias.ENString", "String1"));
    parameters.put("ParameterDef", getFactory().newPrimitiveValueBuilder().buildString("key1"));
    parameters.put("ParameterComp", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("comp")).build());
    parameters.put("ParameterETTwoPrim",  getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("comp")).build());
    parameters.put("CollParameterByte", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("collByte")).build());
    parameters.put("CollParameterEnum", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("collEnum")).build());
    parameters.put("CollParameterDef", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("collDef")).build());
    parameters.put("CollParameterComp", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("collComp")).build());
    parameters.put("CollParameterETTwoPrim",  getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("collComp")).build());
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory().getFunctionInvokeRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendOperationCallSegment("FINRTByteNineParam")
            .addParameterAlias("comp", "{\"PropertyInt16\":1}")
            .addParameterAlias("collByte", "[1]")
            .addParameterAlias("collEnum", "[\"String1,String1\"]")
            .addParameterAlias("collDef", "[\"Test\"]")
            .addParameterAlias("collComp", "[{\"PropertyInt16\":11}]")
            .build(),
        ClientProperty.class,
        parameters);
    setCookieHeader(request);
    ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertShortOrInt(9, response.getBody().getPrimitiveValue().toValue());

    // All parameters having the null value should also work, without any aliases.
    for (final String name : parameters.keySet()) {
      parameters.put(name, getFactory().newPrimitiveValueBuilder().build());
    }
    request = getClient().getInvokeRequestFactory().getFunctionInvokeRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FINRTByteNineParam").build(),
        ClientProperty.class,
        parameters);
    setCookieHeader(request);
    response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertShortOrInt(0, response.getBody().getPrimitiveValue().toValue());
  }

  private Map<String, ClientValue> buildTwoParameters(final int parameterInt16, final String parameterString) {
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("ParameterInt16", getFactory().newPrimitiveValueBuilder().buildInt32(parameterInt16));
    parameters.put("ParameterString", getFactory().newPrimitiveValueBuilder().buildString(parameterString));
    return parameters;
  }
  
  @Test
  public void test1OLINGO753() throws Exception {
    final Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    String parameterString = "1";
    final ClientPrimitiveValue value = getClient().getObjectFactory().newPrimitiveValueBuilder().
        buildString(parameterString);
    parameters.put("ParameterString", value);
    
    ODataInvokeRequest<ClientEntitySet> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESKeyNav").
            appendOperationCallSegment("olingo.odata.test1.BFCESKeyNavRTETKeyNavParam").
            appendNavigationSegment("NavPropertyETTwoKeyNavMany").build(),
            ClientEntitySet.class, parameters);
    assertNotNull(request);
    setCookieHeader(request);

    final ODataInvokeResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntitySet entity = response.getBody();
    assertNotNull(entity);
    assertEquals(3, entity.getEntities().size());
  }
  
  @Test
  public void test2OLINGO753() throws Exception {
    final Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    Short parameterInt = 1;
    final ClientPrimitiveValue value = getClient().getObjectFactory().newPrimitiveValueBuilder().
        buildInt16(parameterInt);
    parameters.put("ParameterInt16", value);
    
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).
            appendOperationCallSegment("FICRTETTwoKeyNavParam").appendPropertySegment("PropertyString").
            appendValueSegment().build(),
            ClientProperty.class, parameters);
    assertNotNull(request);
    request.setAccept("text/plain");
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    String result = StringHelper.asString(response.getRawResponse());
    assertNotNull(result);
    assertEquals(2, Integer.parseInt(result));
  }
  
  @Test
  public void test3OLINGO753() throws Exception {
    final Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    String parameterString = "1";
    final ClientPrimitiveValue value = getClient().getObjectFactory().newPrimitiveValueBuilder().
        buildString(parameterString);
    parameters.put("ParameterString", value);
    
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESKeyNav").
            appendOperationCallSegment("olingo.odata.test1.BFCESKeyNavRTETKeyNavParam").
            appendNavigationSegment("NavPropertyETTwoKeyNavMany").count().build(),
            ClientProperty.class, parameters);
    assertNotNull(request);
    request.setAccept("text/plain");
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    String result = StringHelper.asString(response.getRawResponse());
    assertNotNull(result);
    assertEquals(3, Integer.parseInt(result));    
  }
  
  @Test
  public void test4OLINGO753() throws Exception {
    final Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    String parameterString = "1";
    final ClientPrimitiveValue value = getClient().getObjectFactory().newPrimitiveValueBuilder().
        buildString(parameterString);
    parameters.put("ParameterString", value);
    
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESKeyNav").
            appendOperationCallSegment("olingo.odata.test1.BFCESKeyNavRTETKeyNavParam").
            appendNavigationSegment("NavPropertyETTwoKeyNavMany").count().
            filter("substring(PropertyString,2) eq 'am String Property 1'").build(),
            ClientProperty.class, parameters);
    assertNotNull(request);
    request.setAccept("text/plain");
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    String result = StringHelper.asString(response.getRawResponse());
    assertNotNull(result);
    assertEquals(1, Integer.parseInt(result));    
  }
  
  @Test
  public void test5OLINGO753() throws Exception {
    final Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    String parameterString = "'1'";
    parameters.put("ParameterString", getFactory().newPrimitiveValueBuilder().setValue(
        new ParameterAlias("first")).build());
    
    ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESKeyNav").
            appendOperationCallSegment("olingo.odata.test1.BFCESKeyNavRTETKeyNavParam").
            appendNavigationSegment("NavPropertyETTwoKeyNavMany").count().
            addParameterAlias("first", parameterString).
            filter("substring(PropertyString,2) eq 'am String Property 1'").build(),
            ClientProperty.class, parameters);
    assertNotNull(request);
    request.setAccept("text/plain");
    setCookieHeader(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    String result = StringHelper.asString(response.getRawResponse());
    assertNotNull(result);
    assertEquals(1, Integer.parseInt(result));    
  }
}
