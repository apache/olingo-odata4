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

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class BasicBoundFunctionITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void boundFunctionReturningDerievedType() throws Exception {
    URL url = new URL(SERVICE_URI + "ESBase(111)/olingo.odata.test1.ETTwoBase/"
        + "olingo.odata.test1.BFESBaseRTESTwoBase()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String expected =  "\"PropertyInt16\":111,"
        +  "\"PropertyString\":\"TEST A\","
        +  "\"AdditionalPropertyString_5\":\"TEST A 0815\","
        +  "\"AdditionalPropertyString_6\":\"TEST B 0815\"";
    String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains(expected));
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionReturningNavigationType() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.BFC_RTESTwoKeyNav_()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String expected =    "\"PropertyCompNav\":{"
        +             "\"PropertyInt16\":1,"
        +             "\"PropertyComp\":{"
        +             "\"PropertyString\":\"First Resource - positive values\","
        +             "\"PropertyBinary\":\"ASNFZ4mrze8=\","
        +             "\"PropertyBoolean\":true,"
        +             "\"PropertyByte\":255,"
        +             "\"PropertyDate\":\"2012-12-03\","
        +             "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        +             "\"PropertyDecimal\":34,"
        +             "\"PropertySingle\":1.79E20,"
        +             "\"PropertyDouble\":-1.79E20,"
        +             "\"PropertyDuration\":\"PT6S\","
        +             "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        +             "\"PropertyInt16\":32767,"
        +             "\"PropertyInt32\":2147483647,"
        +             "\"PropertyInt64\":9223372036854775807,"
        +             "\"PropertySByte\":127,"
        +             "\"PropertyTimeOfDay\":\"21:05:59\"";
    String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains(expected));
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionCETReturningCT() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETReturningET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionCETWithParamReturningET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='qw')");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETParamReturningET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam"
        + "(ParameterString=@p1)?@p1=%27qw%27");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETParamReturningCET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESBaseTwoKeyNav/olingo.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETReturningPT() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFNESTwoKeyNavRTString()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETReturningCCT() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTNavFiveProp()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionCETReturningCPT() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollDecimal()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETReturningCT() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/"
        + "olingo.odata.test1.BFCETTwoKeyNavRTCTTwoPrim()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETReturningET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav(1)/olingo.odata.test1.BFCETKeyNavRTETKeyNav()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETReturningCET() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/"
        + "olingo.odata.test1.BFCSINavRTESTwoKeyNav()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  @Test
  public void boundFunctionETSetPath() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/"
        + "NavPropertyETTwoKeyNavMany/olingo.odata.test1.BFC_RTESTwoKeyNav_()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETIsComposible() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()"
        + "?$top=1&$skip=1&$select=PropertyString");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETIsComposibleFilter() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()?"
        + "$filter=PropertyString%20eq%20%272%27");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionETIsComposibleOrderBy() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()?"
        + "$orderby=PropertyInt16%20desc");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionOverload() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  @Test
  public void boundFunctionOverloadParam() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='abc')");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
    connection.disconnect();
  }
  
  
  
  @Override
  protected ODataClient getClient() {
    return null;
  }

}
