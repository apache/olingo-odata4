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
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class DerivedAndMixedTypeTestITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void queryESCompCollDerivedJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains(
        "[{\"PropertyInt16\":32767,\"PropertyCompAno\":null,\"CollPropertyCompAno\":[{\"PropertyString\":" +
        "\"TEST9876\"}]},{\"PropertyInt16\":12345,\"PropertyCompAno\":{\"@odata.type\":" +
        "\"#olingo.odata.test1.CTBaseAno\",\"PropertyString\":\"Num111\",\"AdditionalPropString\":" +
        "\"Test123\"},\"CollPropertyCompAno\":[{\"@odata.type\":\"#olingo.odata.test1.CTBaseAno\"," +
        "\"PropertyString\":\"TEST12345\",\"AdditionalPropString\":\"Additional12345\"}," +
        "{\"PropertyString\":\"TESTabcd\"}]}]}" ));
  }

  @Test
  public void queryESCompCollDerivedXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived?$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<d:PropertyCompAno m:type=\"#olingo.odata.test1.CTBaseAno\">" +
        "<d:PropertyString>Num111</d:PropertyString>" +
        "<d:AdditionalPropString>Test123</d:AdditionalPropString>" +
        "</d:PropertyCompAno>" +
        "<d:CollPropertyCompAno m:type=\"#Collection(olingo.odata.test1.CTTwoPrimAno)\">" +
        "<m:element m:type=\"olingo.odata.test1.CTBaseAno\">" +
        "<d:PropertyString>TEST12345</d:PropertyString>" +
        "<d:AdditionalPropString>Additional12345</d:AdditionalPropString>" ));
  }

  @Test
  public void queryESAllPrimDerivedJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrimDerived(0)?$expand=NavPropertyETTwoPrimMany&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"@odata.type\":\"#olingo.odata.test1.ETBase\"," +
        "\"PropertyInt16\":32766," +
        "\"PropertyString\":\"Test String1\"," +
        "\"AdditionalPropertyString_5\":\"Additional String1\"" ));
  }
  
  @Test
  public void queryESAllPrimDerivedXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrimDerived(0)?$expand=NavPropertyETTwoPrimMany&$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("term=\"#olingo.odata.test1.ETBase\"/>"));
    assertTrue(content.contains(
        "<d:PropertyInt16 m:type=\"Int16\">32766</d:PropertyInt16>" +
        "<d:PropertyString>Test String1</d:PropertyString>" +
        "<d:AdditionalPropertyString_5>Additional String1</d:AdditionalPropertyString_5>"));
  }
  
  @Test
  public void queryESCompCollDerivedJsonNone() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=none");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_NO_METADATA, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains(
        "[{\"PropertyInt16\":32767,\"PropertyCompAno\":null,\"CollPropertyCompAno\":[{\"PropertyString\":" +
        "\"TEST9876\"}]},{\"PropertyInt16\":12345,\"PropertyCompAno\":{"+
        "\"PropertyString\":\"Num111\",\"AdditionalPropString\":" +
        "\"Test123\"},\"CollPropertyCompAno\":[{" +
        "\"PropertyString\":\"TEST12345\",\"AdditionalPropString\":\"Additional12345\"}," +
        "{\"PropertyString\":\"TESTabcd\"}]}]}" ));
  }
  @Test
  public void queryESCompCollDerivedJsonFull() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_FULL_METADATA, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyInt16\":32767,\"PropertyCompAno\":null," +
        "\"CollPropertyCompAno@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrimAno)\"," +
        "\"CollPropertyCompAno\":[{\"@odata.type\":" +
        "\"#olingo.odata.test1.CTTwoPrimAno\",\"PropertyString\":\"TEST9876\"}]}," +
        "{\"@odata.type\":\"#olingo.odata.test1.ETDeriveCollComp\",\"@odata.id\":\"ESCompCollDerived(12345)\"," +
        "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":12345,\"PropertyCompAno\":" +
        "{\"@odata.type\":\"#olingo.odata.test1.CTBaseAno\"," +
        "\"PropertyString\":\"Num111\",\"AdditionalPropString\":\"Test123\"}," +
        "\"CollPropertyCompAno@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrimAno)\",\"CollPropertyCompAno\":" +
        "[{\"@odata.type\":\"#olingo.odata.test1.CTBaseAno\"," +
        "\"PropertyString\":\"TEST12345\",\"AdditionalPropString\":\"Additional12345\"}," +
        "{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrimAno\",\"PropertyString\":\"TESTabcd\"}]}]}" ));
  }

  @Override
  protected ODataClient getClient() {
    return null;
  }

  @Test
  public void queryESMixPrimWithLambdaDerived_JsonFull_Olingo1122() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMixPrimCollComp?$filter=CollPropertyComp/any"
        + "(f:f/olingo.odata.test1.CTBase/AdditionalPropString%20eq%20%27ADD%20TEST%27)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_FULL_METADATA, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    final String actualContent = "\"value\":[{\"@odata.type\":\"#olingo.odata.test1.ETMixPrimCollComp\","
        + "\"@odata.id\":\"ESMixPrimCollComp(32767)\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":32767,"
        + "\"CollPropertyString@odata.type\":\"#Collection(String)\","
        + "\"CollPropertyString\":[\"Employee1@company.example\",\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":111,"
        + "\"PropertyString\":\"TEST A\","
        + "\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":\"ESTwoKeyNav(PropertyInt16=1,PropertyString='1')\"},"
        + "\"CollPropertyComp@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrim)\","
        + "\"CollPropertyComp\":[{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":123,"
        + "\"PropertyString\":\"TEST 1\","
        + "\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":456,"
        + "\"PropertyString\":\"TEST 2\","
        + "\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\",\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\","
        + "\"AdditionalPropString\":\"ADD TEST\","
        + "\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\"}],"
        + "\"#olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\":"
        + "{\"title\":\"olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\","
        + "\"target\":\"ESMixPrimCollComp(32767)/olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\"}},"
        + "{\"@odata.type\":\"#olingo.odata.test1.ETMixPrimCollComp\","
        + "\"@odata.id\":\"ESMixPrimCollComp(7)\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":7,"
        + "\"CollPropertyString@odata.type\":\"#Collection(String)\","
        + "\"CollPropertyString\":[\"Employee1@company.example\",\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":222,\"PropertyString\":\"TEST B\"},"
        + "\"CollPropertyComp@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrim)\","
        + "\"CollPropertyComp\":[{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":123,"
        + "\"PropertyString\":\"TEST 1\"},{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\",\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\",\"AdditionalPropString\":\"ADD TEST\"}],"
        + "\"#olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\":"
        + "{\"title\":\"olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\","
        + "\"target\":\"ESMixPrimCollComp(7)/olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\"}},"
        + "{\"@odata.type\":\"#olingo.odata.test1.ETMixPrimCollComp\",\"@odata.id\":\"ESMixPrimCollComp(0)\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":0,"
        + "\"CollPropertyString@odata.type\":\"#Collection(String)\","
        + "\"CollPropertyString\":[\"Employee1@company.example\",\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":333,\"PropertyString\":\"TEST C\"},"
        + "\"CollPropertyComp@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrim)\","
        + "\"CollPropertyComp\":[{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":123,"
        + "\"PropertyString\":\"TEST 1\"},{\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
        + "\"PropertyInt16@odata.type\":\"#Int16\",\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\",\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\",\"AdditionalPropString\":\"ADD TEST\"}],"
        + "\"#olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\":"
        + "{\"title\":\"olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\","
        + "\"target\":\"ESMixPrimCollComp(0)/olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\"}}]";
    assertTrue(content.contains(actualContent));
  }
  
  @Test
  public void queryESMixPrimWithLambdaDerived_JsonMinimal_Olingo1122() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMixPrimCollComp?$filter=CollPropertyComp/any"
        + "(f:f/olingo.odata.test1.CTBase/AdditionalPropString%20eq%20%27ADD%20TEST%27)");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":[\"Employee1@company.example\","
        + "\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\","
        + "\"AdditionalPropString\":\"ADD TEST\"}]},"
        + "{\"PropertyInt16\":7,\"CollPropertyString\":"
        + "[\"Employee1@company.example\","
        + "\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":222,\"PropertyString\":\"TEST B\"},"
        + "\"CollPropertyComp\":[{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\","
        + "\"AdditionalPropString\":\"ADD TEST\"}]},"
        + "{\"PropertyInt16\":0,\"CollPropertyString\":["
        + "\"Employee1@company.example\","
        + "\"Employee2@company.example\","
        + "\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":333,\"PropertyString\":\"TEST C\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"@odata.type\":\"#olingo.odata.test1.CTBase\","
        + "\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\","
        + "\"AdditionalPropString\":\"ADD TEST\"}]}]"));
  }
  
  @Test
  public void queryESTwoPrimWithEntityTypeCast() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(111)/olingo.odata.test1.ETBase");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_FULL_METADATA, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"@odata.type\":\"#olingo.odata.test1.ETBase\","
        + "\"@odata.id\":\"ESBase(111)\","
        + "\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":111,"
        + "\"PropertyString\":\"TEST A\","
        + "\"AdditionalPropertyString_5\":\"TEST A 0815\""));
  }
  
  @Test
  public void queryESTwoPrimWithEntityTypeCastInFilter() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim?$filter=olingo.odata.test1.ETBase/"
        + "AdditionalPropertyString_5%20eq%20%27TEST%20A%200815%27");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_FULL_METADATA, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"@odata.type\":\"#olingo.odata.test1.ETBase\","
        + "\"@odata.id\":\"ESBase(111)\","
        + "\"PropertyInt16@odata.type\":\"#Int16\","
        + "\"PropertyInt16\":111,"
        + "\"PropertyString\":\"TEST A\","
        + "\"AdditionalPropertyString_5\":\"TEST A 0815\""
        + ",\"#olingo.odata.test1.BAETBaseETTwoBaseRTETTwoBase\":"
        + "{\"title\":\"olingo.odata.test1.BAETBaseETTwoBaseRTETTwoBase\","
        + "\"target\":\"ESBase(111)/olingo.odata.test1.BAETBaseETTwoBaseRTETTwoBase\"}}]"));
  }
  
  @Test
  public void queryESAllPrimWithEntityTypeCastInExpand() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim(0)?$expand=NavPropertyETTwoPrimOne/olingo.odata.test1.ETBase");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, 
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"PropertyInt16\":0,"
        + "\"PropertyString\":\"\","
        + "\"PropertyBoolean\":false,"
        + "\"PropertyByte\":0,"
        + "\"PropertySByte\":0,"
        + "\"PropertyInt32\":0,"
        + "\"PropertyInt64\":0,"
        + "\"PropertySingle\":0.0,"
        + "\"PropertyDouble\":0.0,"
        + "\"PropertyDecimal\":0,"
        + "\"PropertyBinary\":\"\","
        + "\"PropertyDate\":\"1970-01-01\","
        + "\"PropertyDateTimeOffset\":\"2005-12-03T00:00:00Z\","
        + "\"PropertyDuration\":\"PT0S\","
        + "\"PropertyGuid\":\"76543201-23ab-cdef-0123-456789cccddd\","
        + "\"PropertyTimeOfDay\":\"00:01:01\",\"NavPropertyETTwoPrimOne\":{"
        + "\"@odata.type\":\"#olingo.odata.test1.ETBase\","
        + "\"PropertyInt16\":111,\"PropertyString\":\"TEST A\","
        + "\"AdditionalPropertyString_5\":\"TEST A 0815\"}"));
  }
}
