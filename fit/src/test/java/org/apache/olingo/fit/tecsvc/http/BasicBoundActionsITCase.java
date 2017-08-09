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
package org.apache.olingo.fit.tecsvc.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;

public class BasicBoundActionsITCase {

  private static final String SERVICE_URI = "http://localhost:8083/odata-server-tecsvc/odata.svc/";
  protected static final ODataClient client = ODataClientFactory.getClient();
    
  @Test
  public void boundActionReturningBaseType() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoBaseTwoKeyNav(PropertyInt16=1,PropertyString='1')/"
        + "olingo.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    final String expected = "\"PropertyInt16\":1,\"PropertyString\":\"1\","
        + "\"PropertyComp\":{\"PropertyInt16\":11,"
        + "\"PropertyComp\":{\"PropertyString\":\"StringValue\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":null,\"PropertyDecimal\":34,"
        + "\"PropertySingle\":1.79E20,\"PropertyDouble\":-1.79E20,"
        + "\"PropertyDuration\":\"PT6S\",\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,\"PropertyInt64\":9223372036854775807,"
        + "\"PropertySByte\":127,\"PropertyTimeOfDay\":\"21:05:59\"}},"
        + "\"PropertyCompNav\":{\"PropertyInt16\":1,\"PropertyComp\":"
        + "{\"PropertyString\":\"First Resource - positive values\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,"
            + "\"PropertyInt64\":9223372036854775807,\"PropertySByte\":127,"
            + "\"PropertyTimeOfDay\":\"21:05:59\"}},\"CollPropertyComp\":[],"
            + "\"CollPropertyCompNav\":[{\"PropertyInt16\":1}],"
            + "\"CollPropertyString\":[\"1\",\"2\"],\"PropertyCompTwoPrim\":"
            + "{\"PropertyInt16\":11,\"PropertyString\":\"11\"},\"PropertyDate\":\"2013-12-12\"}";
    assertTrue(content.contains(expected));
  }
  
  @Test
  public void boundActionReturningEntityWithNavigationSegInAction() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/"
        + "NavPropertyETTwoKeyNavOne/olingo.odata.test1.BAETTwoKeyNavRTETTwoKeyNavParam");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.POST.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.setRequestProperty(HttpHeader.CONTENT_TYPE, "application/json");
    connection.setDoOutput(true);
    final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
    writer.append("{\"PropertyComp\" : {\"PropertyInt16\" : 30}}");
    writer.close();
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    final String expected = "\"PropertyInt16\":1,\"PropertyString\":\"1\","
        + "\"PropertyComp\":{\"PropertyInt16\":30,\"PropertyComp\":"
        + "{\"PropertyString\":\"StringValue\",\"PropertyBinary\":\"ASNFZ4mrze8=\","
        + "\"PropertyBoolean\":true,\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":null,\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\",\"PropertyInt16\":32767,"
            + "\"PropertyInt32\":2147483647,\"PropertyInt64\":9223372036854775807,"
            + "\"PropertySByte\":127,\"PropertyTimeOfDay\":\"21:05:59\"}},"
            + "\"PropertyCompNav\":{\"PropertyInt16\":1,\"PropertyComp\":"
            + "{\"PropertyString\":\"First Resource - positive values\","
            + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
            + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
            + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
            + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,\"PropertyDouble\":-1.79E20,"
            + "\"PropertyDuration\":\"PT6S\",\"PropertyGuid\":"
            + "\"01234567-89ab-cdef-0123-456789abcdef\",\"PropertyInt16\":32767,"
            + "\"PropertyInt32\":2147483647,\"PropertyInt64\":9223372036854775807,"
            + "\"PropertySByte\":127,\"PropertyTimeOfDay\":\"21:05:59\"}},\"CollPropertyComp\":[],"
            + "\"CollPropertyCompNav\":[{\"PropertyInt16\":1}],\"CollPropertyString\":[\"1\",\"2\"],"
            + "\"PropertyCompTwoPrim\":{\"PropertyInt16\":11,\"PropertyString\":\"11\"}}";
    assertTrue(content.contains(expected));
  }
  
  @Test
  public void boundActionReturningDerivedTypeWithTypeCastSegInAction() throws Exception {
    URIBuilder builder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESBase").
        appendKeySegment(111).appendDerivedEntityTypeSegment("olingo.odata.test1.ETTwoBase").
        appendActionCallSegment("olingo.odata.test1.BAETBaseETTwoBaseRTETTwoBase");
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    final ClientPrimitiveValue propStrValue =
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("NewStrValue");
    parameters.put("PropertyString", propStrValue);
    
    final ClientPrimitiveValue addPropStr1 =
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("AddStrProp_5");
    parameters.put("AdditionalPropertyString_5", addPropStr1);
    
    final ClientPrimitiveValue addPropStr2 =
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("AddStrProp_6");
    parameters.put("AdditionalPropertyString_6", addPropStr2);
    
    final ODataInvokeRequest<ClientEntity> req =
        client.getInvokeRequestFactory().getActionInvokeRequest(builder.build(), ClientEntity.class, parameters);
    req.setFormat(ContentType.JSON_FULL_METADATA);
    req.setContentType(ContentType.APPLICATION_JSON.toContentTypeString() + ";odata.metadata=full");
    ClientEntity entity = req.execute().getBody();
    assertNotNull(entity);
    assertEquals(entity.getProperties().size(), 4);
    assertEquals(entity.getTypeName().getFullQualifiedNameAsString(), "olingo.odata.test1.ETTwoBase");
  }
  
  @Test
  public void boundActionReturningColComplexTypeWithComplexTypeSegInAction() throws Exception {
    URIBuilder builder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESMixPrimCollComp").
        appendKeySegment(32767).appendPropertySegment("CollPropertyComp").
        appendActionCallSegment("olingo.odata.test1.BAETMixPrimCollCompCTTWOPrimCompRTCollCTTwoPrim");
    final ClientComplexValue propertyComp = client.getObjectFactory().
        newComplexValue("olingo.odata.test1.CTTwoPrim");
    propertyComp.add(client.getObjectFactory().newPrimitiveProperty("PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 111)));
    propertyComp.add(client.getObjectFactory().newPrimitiveProperty("PropertyString",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ABC")));
    
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("PropertyComp", propertyComp);
    
    final ODataInvokeRequest<ClientProperty> req =
        client.getInvokeRequestFactory().getActionInvokeRequest(builder.build(), ClientProperty.class, parameters);
    req.setFormat(ContentType.JSON_FULL_METADATA);
    req.setContentType(ContentType.APPLICATION_JSON.toContentTypeString() + ";odata.metadata=full");
    ClientProperty prop = req.execute().getBody();
    assertNotNull(prop);
    assertNotNull(prop.getCollectionValue());
    assertEquals(prop.getCollectionValue().asCollection().size(), 1);
    assertEquals(prop.getCollectionValue().getTypeName(), "Collection(olingo.odata.test1.CTTwoPrim)");
  }
  
  @Test
  public void boundActionReturningComplexDerivedTypeWithComplexTypeSegInAction() throws Exception {
    Map<String, Object> segmentValues = new HashMap<String, Object>();
    segmentValues.put("PropertyInt16", 1);
    segmentValues.put("PropertyString", "1");
    URIBuilder builder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoKeyNav").
        appendKeySegment(segmentValues).appendPropertySegment("PropertyCompNav").
        appendDerivedEntityTypeSegment("olingo.odata.test1.CTTwoBasePrimCompNav").
        appendActionCallSegment("olingo.odata.test1."
            + "BAETTwoKeyNavCTBasePrimCompNavCTTwoBasePrimCompNavRTCTTwoBasePrimCompNav");
    
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    
    final ODataInvokeRequest<ClientEntity> req =
        client.getInvokeRequestFactory().getActionInvokeRequest(builder.build(), ClientEntity.class, parameters);
    req.setFormat(ContentType.JSON_FULL_METADATA);
    req.setContentType(ContentType.APPLICATION_JSON.toContentTypeString() + ";odata.metadata=full");
    ClientEntity entity = req.execute().getBody();
    assertNotNull(entity);
    assertEquals(entity.getProperties().size(), 2);
    assertEquals(entity.getTypeName().getFullQualifiedNameAsString(), 
        "olingo.odata.test1.CTTwoBasePrimCompNav");
  }
  
  @Test
  public void boundActionReturningCollComplexTypeWithMixedTypes() throws Exception {
    URIBuilder builder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESMixPrimCollComp").
        appendKeySegment(32767).
        appendActionCallSegment("olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim");
    
    final ClientCollectionValue<ClientValue> colPropertyComp =
        client.getObjectFactory().
        newCollectionValue("Collection(olingo.odata.test1.CTTwoPrim)");
    
    final ClientComplexValue property1 = client.getObjectFactory().
        newComplexValue("olingo.odata.test1.CTTwoPrim");
    property1.add(client.getObjectFactory().newPrimitiveProperty("PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 111)));
    property1.add(client.getObjectFactory().newPrimitiveProperty("PropertyString",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ABC")));
    colPropertyComp.add(property1);
    
    final ClientComplexValue property2 = client.getObjectFactory().
        newComplexValue("olingo.odata.test1.CTTwoPrim");
    property2.add(client.getObjectFactory().newPrimitiveProperty("PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 222)));
    property2.add(client.getObjectFactory().newPrimitiveProperty("PropertyString",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("DEF")));
    colPropertyComp.add(property2);
    
    final ClientComplexValue property3 = client.getObjectFactory().
        newComplexValue("olingo.odata.test1.CTBase");
    property3.add(client.getObjectFactory().newPrimitiveProperty("PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 333)));
    property3.add(client.getObjectFactory().newPrimitiveProperty("PropertyString",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("GHI")));
    colPropertyComp.add(property3);
    
    Map<String, ClientValue> parameters = new HashMap<String, ClientValue>();
    parameters.put("CollPropertyComp", colPropertyComp);
    
    final ODataInvokeRequest<ClientProperty> req =
        client.getInvokeRequestFactory().getActionInvokeRequest(builder.build(), ClientProperty.class, parameters);
    req.setFormat(ContentType.JSON_FULL_METADATA);
    req.setContentType(ContentType.APPLICATION_JSON.toContentTypeString() + ";odata.metadata=full");
    ClientProperty prop = req.execute().getBody();
    assertNotNull(prop);
    assertEquals(prop.getCollectionValue().getTypeName(), 
        "Collection(olingo.odata.test1.CTTwoPrim)");
    assertEquals(prop.getCollectionValue().asCollection().size(), 3);
    Iterator<ClientValue> itr = prop.getCollectionValue().asCollection().iterator();
    int i = 0;
    while (itr.hasNext()) {
      ClientValue value = itr.next();
      if (i == 2) {
        assertEquals(value.asComplex().getTypeName(), "#olingo.odata.test1.CTBase");
      } else {
        assertEquals(value.asComplex().getTypeName(), "#olingo.odata.test1.CTTwoPrim");
      }
      i++;
    }
  }
}
