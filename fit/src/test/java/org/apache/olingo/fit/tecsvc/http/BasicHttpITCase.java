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
import static org.junit.Assert.assertNull;
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

public class BasicHttpITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void testHeadMethodOnServiceDocument() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.HEAD.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("", IOUtils.toString(connection.getInputStream()));
    connection.disconnect();
  }
  
  @Test
  public void testHeadMethodOnMetadataDocument() throws Exception {
    URL url = new URL(SERVICE_URI + "$metadata");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.HEAD.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/xml");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("", IOUtils.toString(connection.getInputStream()));
    connection.disconnect();
  }

  @Test
  public void testFormat() throws Exception {
    URL url = new URL(SERVICE_URI + "?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));
  }

  @Test
  public void testAccept() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;q=0.2;odata.metadata=minimal");

    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));
  }

  @Test
  public void testAcceptSimple() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");

    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));
  }

  @Test
  public void testAcceptCharset() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;q=0.2;odata.metadata=minimal;charset=utf-8");

    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.create(ContentType.JSON, ContentType.PARAMETER_CHARSET, "utf-8"),
        ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));
  }

  @Test
  public void testODataMaxVersion() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "4.0");
    connection.setRequestProperty(HttpHeader.ACCEPT, "*/*");

    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));
  }

  @Test
  public void testIEEE754ParameterContentNegotiation() throws Exception {
    final URL url = new URL(SERVICE_URI + "ESAllPrim(32767)?$format=application/json;IEEE754Compatible=true");
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;IEEE754Compatible=false");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true"),
        ContentType.create(connection.getContentType()));
    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyDecimal\":\"34\""));
    assertTrue(content.contains("\"PropertyInt64\":\"9223372036854775807\""));
  }

  @Test
  public void testIEEE754ParameterViaAcceptHeader() throws Exception {
    final URL url = new URL(SERVICE_URI + "ESAllPrim(32767)");
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;IEEE754Compatible=true");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true"),
        ContentType.create(connection.getContentType()));
    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyDecimal\":\"34\""));
    assertTrue(content.contains("\"PropertyInt64\":\"9223372036854775807\""));
  }

  @Override
  protected ODataClient getClient() {
    return null;
  }
  @Test
  public void testInvalidTopUrl() throws Exception {
    URL url = new URL(SERVICE_URI + "?$top");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertTrue(IOUtils.toString(connection.getErrorStream()).
        contains("The system query option '$top' has the not-allowed value ''."));
  }
 
  @Test
  public void testInvalidSkipUrl() throws Exception {
    URL url = new URL(SERVICE_URI + "?$skip=");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertTrue(IOUtils.toString(connection.getErrorStream()).
        contains("The system query option '$skip' has the not-allowed value ''."));
  }
  
  @Test
  public void testBaseTypeDerivedTypeCasting1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(111)/olingo.odata.test1.ETBase");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyInt16\":111"));
    assertTrue(content.contains("\"PropertyString\":\"TEST A\""));
    assertTrue(content.contains("\"AdditionalPropertyString_5\":\"TEST A 0815\""));
    assertTrue(content.contains("\"@odata.type\":\"#olingo.odata.test1.ETBase\""));
  }
  
  @Test
  public void testBaseTypeDerivedTypeCasting2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(111)/olingo.odata.test1.ETBase/AdditionalPropertyString_5");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"TEST A 0815\""));
  }
  
  @Test
  public void testBaseTypeDerivedTypeCasting3() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim(32766)/olingo.odata.test1.ETTwoPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyInt16\":32766"));
    assertTrue(content.contains("\"PropertyString\":\"Test String1\""));
    assertTrue(content.contains("\"@odata.type\":\"#olingo.odata.test1.ETTwoPrim\""));
  }
  
  @Test
  public void testBaseTypeDerivedTypeCastingException() throws Exception {
    URL url = new URL(SERVICE_URI + "ESBase(111)/olingo.odata.test1.ETTwoPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getErrorStream());

    assertTrue(content.contains("The type filter 'olingo.odata.test1.ETTwoPrim' is incompatible."));
  }
  
  @Test
  public void testBaseTypeDerivedComplexTypeCasting1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESMixPrimCollComp(32767)/PropertyComp/olingo.odata.test1.CTBase");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyInt16\":111"));
    assertTrue(content.contains("\"PropertyString\":\"TEST A\""));
    assertTrue(content.contains("\"AdditionalPropString\":null"));
    assertTrue(content.contains("\"@odata.type\":\"#olingo.odata.test1.CTBase\""));
  }
  
  @Test
  public void testFilterWithSpaceFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16%20eq%201&odata-accept-forms-encoding=true");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertNotNull(content);
  }
  
  @Test
  public void testFilterWithSpaceNoFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16%20eq%201");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertNotNull(content);
  }
  
  @Test
  public void testFilterWithFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16+eq+1&odata-accept-forms-encoding=true");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertNotNull(content);
  }
  
  @Test
  public void testFilterWithFormEncodingOrderChange() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?odata-accept-forms-encoding=true&$filter=PropertyInt16+eq+1");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    final String content = IOUtils.toString(connection.getInputStream());

    assertNotNull(content);
  }
  
  @Test
  public void testFilterWithFalseFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16+eq+1&odata-accept-forms-encoding=false");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void testFilterWithNoFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16+eq+1");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void testFilterWithInvalidFormEncoding() throws Exception {
    URL url = new URL(SERVICE_URI + 
        "ESAllPrim?$filter=PropertyInt16+eq+1&odata-accept-forms-encoding=qwer");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
  }
  
  @Test
  public void testCountWithSearch() throws Exception {
    URL url = new URL(SERVICE_URI + "ESTwoPrim/$count?$search=%2232%22");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals("3", IOUtils.toString(connection.getInputStream()));
    connection.disconnect();
  }
}
