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

public class ODataVersionConformanceITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void invalidODataVersionHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name()); 
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "3.0");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("OData version '3.0' is not supported."));
  }
  
  @Test
  public void invalidODataVersionHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "5.0");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("OData version '5.0' is not supported."));
  }
  
  @Test
  public void invalidODataMaxVersionHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "3.0");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("OData version '3.0' is not supported."));
  }
  
  @Test
  public void validODataMaxVersionHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "5.0");
    connection.connect();

    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertNotNull(content);
  }

  @Test
  public void validODataVersionAndMaxVersionHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "4.0");
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "5.0");
    connection.connect();

    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertNotNull(content);;
  }
  
  @Test
  public void validODataVersionAndMaxVersionHeader1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "4.0");
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "4.01");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));
    assertEquals("application/json; odata.metadata=minimal", 
        connection.getHeaderField(HttpHeader.CONTENT_TYPE));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void validODataVersionAndMaxVersionHeader2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "4.0");
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "4.0");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));
    assertEquals("application/json; odata.metadata=minimal", 
        connection.getHeaderField(HttpHeader.CONTENT_TYPE));

    final String content = IOUtils.toString(connection.getInputStream());
    assertNotNull(content);
  }
  
  @Test
  public void invalidODataVersionAndMaxVersionHeader() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ODATA_VERSION, "5.0");
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "5.0");
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertEquals("4.0", connection.getHeaderField(HttpHeader.ODATA_VERSION));

    final String content = IOUtils.toString(connection.getErrorStream());
    assertTrue(content.contains("OData version '5.0' is not supported."));
  }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }
}
