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
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class AcceptHeaderAcceptCharsetHeaderITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";
  
  @Test
  public void validAcceptHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void invalidAcceptHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "abc");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The content-type range ' abc' is not supported as "
        + "value of the Accept header."));
  }
  
  @Test
  public void invalidAcceptHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/xyz");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The content-type range 'application/xyz' is "
        + "not supported as value of the Accept header."));
  }
  
  @Test
  public void validAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=0.1");
    connection.setRequestProperty(HttpHeader.ACCEPT, ContentType.APPLICATION_JSON.toContentTypeString());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(3, contentType.getParameters().size());
    assertEquals("0.1", contentType.getParameter("q"));
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    assertEquals("utf-8", contentType.getParameter("charset"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void invalidAcceptCharsetHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "US-ASCII");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());
    
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept charset header "
        + "'US-ASCII' is not supported."));
  }
  
  @Test
  public void invalidAcceptCharsetHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "abc");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());
    
    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept charset header 'abc' is not supported."));
  }
  
  @Test
  public void unsupportedAcceptHeaderWithSupportedAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf8;q=0.1");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;charset=iso8859-1");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(3, contentType.getParameters().size());
    assertEquals("0.1", contentType.getParameter("q"));
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    assertEquals("utf8", contentType.getParameter("charset"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void supportedAcceptHeaderWithUnSupportedAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "iso-8859-1");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;charset=utf8");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept charset header "
        + "'iso-8859-1' is not supported."));
  }
  
  @Test
  public void validFormatWithAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=0.1");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(3, contentType.getParameters().size());
    assertEquals("0.1", contentType.getParameter("q"));
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    assertEquals("utf-8", contentType.getParameter("charset"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void validFormatWithUnsupportedAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "iso-8859-1");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept charset header "
        + "'iso-8859-1' is not supported."));
  }
  
  @Test
  public void validFormatWithIllegalAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "abc");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept charset "
        + "header 'abc' is not supported."));
  }
  
  @Test
  public void multipleValuesInAcceptCharsetHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=0.1,iso-8859-1,unicode-1-1");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(3, contentType.getParameters().size());
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    assertEquals("utf-8", contentType.getParameter("charset"));
    assertEquals("0.1", contentType.getParameter("q"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void multipleValuesInAcceptCharsetHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim"); 

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=0.1,utf-8;q=0.8,utf8");
    connection.setRequestProperty(HttpHeader.ACCEPT, ContentType.APPLICATION_JSON.toContentTypeString());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(2, contentType.getParameters().size());
    assertEquals("utf8", contentType.getParameter("charset"));
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    
    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void multipleValuesInAcceptHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json,"
        + "application/json;q=0.1,application/json;q=0.8");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(1, contentType.getParameters().size());
    assertEquals("minimal", contentType.getParameter("odata.metadata"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void multipleValuesInAcceptHeaderWithOneIncorrectValue() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json,"
        + "application/json;q=0.1,application/json;q=0.8,abc");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The content-type range ' abc' is not supported as value of the Accept header."));
  }
  
  @Test
  public void multipleValuesInAcceptHeaderWithIncorrectParam() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json,"
        + "application/json;q=0.1,application/json;q=<1");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The content-type range 'application/json;q=<1' is not "
        + "supported as value of the Accept header."));
  }
  
  @Test
  public void multipleValuesInAcceptHeaderWithIncorrectCharset() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json,"
        + "application/json;q=0.1,application/json;charset=utf<8");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The charset specified in Accept header "
        + "'application/json;charset=utf<8' is not supported."));
  }
  
  @Test
  public void acceptHeaderWithIncorrectParams() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;abc=xyz");
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The content-type range 'application/json;abc=xyz' is not "
        + "supported as value of the Accept header."));
  }
  
  @Test
  public void formatWithIllegalCharset1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=application/json;charset=abc");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The $format option 'application/json;charset=abc' is not supported."));
  }
  
  @Test
  public void formatWithWrongParams() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=application/json;abc=xyz");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.NOT_ACCEPTABLE.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The $format option 'application/json;abc=xyz' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectParamsInAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;abc=xyz");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf-8;abc=xyz' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf<8");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf<8' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectMultipleAcceptCharsetHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf<8,utf-8;q=0.8");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf<8' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectMultipleAcceptCharsetHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=0.8,utf-8;q=<");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf-8;q=<' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectQParamInAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;q=<");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf-8;q=<' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectParamInAcceptCharsetHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "utf-8;abc=xyz");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The Accept charset header 'utf-8;abc=xyz' is not supported."));
  }
  
  @Test
  public void validFormatWithIncorrectCharset() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=application/json;charset=utf<8");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;charset=utf-8");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("The $format option 'application/json;charset=utf<8' is not supported."));
  }
  
  @Test
  public void formatWithAcceptCharset() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=application/json;charset=utf-8");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT_CHARSET, "abc");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    assertNotNull(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    ContentType contentType = ContentType.parse(connection.getHeaderField(HttpHeader.CONTENT_TYPE));
    assertEquals("application", contentType.getType());
    assertEquals("json", contentType.getSubtype());
    assertEquals(2, contentType.getParameters().size());
    assertEquals("minimal", contentType.getParameter("odata.metadata"));
    assertEquals("utf-8", contentType.getParameter("charset"));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }

}
