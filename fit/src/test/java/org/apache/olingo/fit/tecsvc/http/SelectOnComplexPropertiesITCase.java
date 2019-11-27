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

public class SelectOnComplexPropertiesITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void queryESKeyColPropertyComp1() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav(1)/CollPropertyComp"
        + "?$select=PropertyComp/PropertyString");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"PropertyComp\":"
            + "{\"PropertyString\":\"First Resource - positive values\"}},"
            + "{\"PropertyComp\":{\"PropertyString\":\"First Resource - positive values\"}},"
            + "{\"PropertyComp\":{\"PropertyString\":\"First Resource - positive values\"}}]"));
  }
  
  @Test
  public void queryESKeyColPropertyComp2() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav(1)/CollPropertyComp"
        + "?$select=PropertyInt16");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"value\":[{\"PropertyInt16\":1},{\"PropertyInt16\":2},{\"PropertyInt16\":3}]"));
  }
  
  @Test
  public void queryESKeyColPropertyComp3() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav(1)/CollPropertyComp"
        + "?$select=PropertyComp");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    System.out.println("Content is::"+ content);
    assertTrue(content.contains("\"value\":[{\"PropertyComp\":{"
        + "\"PropertyString\":\"First Resource - positive values\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,"
        + "\"PropertyInt64\":9223372036854775807,\"PropertySByte\":127,"
        + "\"PropertyTimeOfDay\":\"21:05:59\"}},{\"PropertyComp\":"
        + "{\"PropertyString\":\"First Resource - positive values\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,"
        + "\"PropertyInt64\":9223372036854775807,\"PropertySByte\":127,"
        + "\"PropertyTimeOfDay\":\"21:05:59\"}},{\"PropertyComp\":"
        + "{\"PropertyString\":\"First Resource - positive values\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\",\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        + "\"PropertyDecimal\":34,\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E20,\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,\"PropertyInt32\":2147483647,"
        + "\"PropertyInt64\":9223372036854775807,\"PropertySByte\":127,"
        + "\"PropertyTimeOfDay\":\"21:05:59\"}}]"));
  }
  
  @Test
  public void queryESKeyPropertyCompCompNav() throws Exception {
    URL url = new URL(SERVICE_URI + "ESKeyNav(1)/PropertyCompCompNav"
        + "?$select=PropertyCompNav/PropertyInt16");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=minimal");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"PropertyCompNav\":{\"PropertyInt16\":1}"));
    connection.disconnect();
  }
  
  @Test
  public void queryESCompCollDerivedWithMixedComplexTypes() throws Exception {
    URL url = new URL(SERVICE_URI + "ESCompCollDerived(12345)/"
        + "CollPropertyCompAno?$select=PropertyString");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json;odata.metadata=full");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON_FULL_METADATA, ContentType.create(
        connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("\"@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrimAno)\","
        + "\"value\":[{\"@odata.type\":\"#olingo.odata.test1.CTBaseAno\","
        + "\"PropertyString\":\"TEST12345\"},{\"@odata.type\":"
        + "\"#olingo.odata.test1.CTTwoPrimAno\",\"PropertyString\":\"TESTabcd\"}]"));
  }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }
}
