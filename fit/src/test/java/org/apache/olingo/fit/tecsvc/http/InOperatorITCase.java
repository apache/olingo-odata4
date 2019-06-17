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

public class InOperatorITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";
  
  @Override
  protected ODataClient getClient() {
    return null;
  }

  @Test
  public void querySimple() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$filter=PropertyString%20in%20("
        + "%27Second%20Resource%20-%20negative%20values%27,%27xyz%27)");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"PropertyInt16\":-32768,"
        + "\"PropertyString\":\"Second Resource - negative values\","
        + "\"PropertyBoolean\":false,\"PropertyByte\":0,\"PropertySByte\":-128,"
        + "\"PropertyInt32\":-2147483648,\"PropertyInt64\":-9223372036854775808,"
        + "\"PropertySingle\":-1.79E8,\"PropertyDouble\":-179000.0,\"PropertyDecimal\":-34,"
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyDate\":\"2015-11-05\","
        + "\"PropertyDateTimeOffset\":\"2005-12-03T07:17:08Z\",\"PropertyDuration\":\"PT9S\","
        + "\"PropertyGuid\":\"76543201-23ab-cdef-0123-456789dddfff\","
        + "\"PropertyTimeOfDay\":\"23:49:14\"}]"));
  }
  
  @Test
  public void queryInOperatorWithFunction() throws Exception {
    URL url = new URL(SERVICE_URI + "ESAllPrim?$filter=PropertyString%20in%20olingo.odata.test1.UFCRTCollString()");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"PropertyInt16\":10,"
        + "\"PropertyString\":\"Employee1@company.example\","
        + "\"PropertyBoolean\":false,\"PropertyByte\":0,\"PropertySByte\":0,"
        + "\"PropertyInt32\":0,\"PropertyInt64\":0,\"PropertySingle\":0.0,"
        + "\"PropertyDouble\":0.0,\"PropertyDecimal\":0,\"PropertyBinary\":\"\","
        + "\"PropertyDate\":\"1970-01-01\","
        + "\"PropertyDateTimeOffset\":\"2005-12-03T00:00:00Z\","
        + "\"PropertyDuration\":\"PT0S\","
        + "\"PropertyGuid\":\"76543201-23ab-cdef-0123-456789cccddd\","
        + "\"PropertyTimeOfDay\":\"00:01:01\"}]"));
  }
  
  @Test
  public void queryInOperatorOnNavProperty() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav?$filter=PropertyCompTwoPrim/"
        + "PropertyInt16%20in%20NavPropertyETKeyNavOne/CollPropertyInt16");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[]"));
  }
}
