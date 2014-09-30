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
package org.apache.olingo.fit.tecsvc.http;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class BasicHttpITCase extends AbstractBaseTestITCase{

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void testFormat() throws Exception {
    URL url = new URL(SERVICE_URI + "?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
    String ct = connection.getHeaderField(HttpHeader.CONTENT_TYPE);
    assertEquals(ContentType.create("application/json;odata.metadata=minimal"), ContentType.create(ct));
  }

  @Test
  public void testAccept() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;q=0.2;odata.metadata=minimal");
    
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
    String ct = connection.getHeaderField(HttpHeader.CONTENT_TYPE);
    assertEquals(ContentType.create("application/json;odata.metadata=minimal"), ContentType.create(ct));
  }

  @Test
  public void testAcceptSimple() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
    String ct = connection.getHeaderField(HttpHeader.CONTENT_TYPE);
    assertEquals(ContentType.create("application/json;odata.metadata=minimal"), ContentType.create(ct));
  }

  @Test
  public void testAcceptCharset() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;q=0.2;odata.metadata=minimal;charset=utf-8");
    
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
    String ct = connection.getHeaderField(HttpHeader.CONTENT_TYPE);
    assertEquals(ContentType.create("application/json;odata.metadata=minimal;charset=utf-8"), ContentType.create(ct));
  }

  @Test
  public void testODataMaxVersion() throws Exception {
    URL url = new URL(SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ODATA_MAX_VERSION, "4.0");
    
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
    String v = connection.getHeaderField(HttpHeader.ODATA_VERSION);
    assertEquals("4.0", v);
  }

  @Override
  protected CommonODataClient<?> getClient() {
    // TODO Auto-generated method stub
    return null;
  }

  
  
}
