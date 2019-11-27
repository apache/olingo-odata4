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
import static org.junit.Assert.assertFalse;

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

public class BasicStreamITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";

  @Test
  public void streamESStreamJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStream?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("Streamed-Employee1@company.example\"," +
            "\"Streamed-Employee2@company.example\"," +
            "\"Streamed-Employee3@company.example\""));
    assertTrue(content.contains("\"PropertyString\":\"TEST 1->streamed\""));
    assertTrue(content.contains("\"PropertyString\":\"TEST 2->streamed\""));
  }
  
  @Test
  public void streamESWithStreamJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESWithStream?$expand=PropertyStream&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));
  }

  @Test
  public void streamESStreamXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStream?$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<m:element>Streamed-Employee1@company.example</m:element>" +
            "<m:element>Streamed-Employee2@company.example</m:element>" +
            "<m:element>Streamed-Employee3@company.example</m:element>"));
    assertTrue(content.contains("<d:PropertyString>TEST 1->streamed</d:PropertyString>"));
    assertTrue(content.contains("<d:PropertyString>TEST 2->streamed</d:PropertyString>"));
  }

  @Test
  public void streamESStreamServerSidePagingXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<a:link rel=\"next\" href="));
    assertTrue(content.contains("ESStreamServerSidePaging?$format=xml&amp;%24skiptoken=1%2A10\"/>"));
    assertTrue(content.contains("<a:id>ESStreamServerSidePaging(1)</a:id>"));
    assertTrue(content.contains("<d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>"));
    assertTrue(content.contains("<d:PropertyStream m:type=\"Stream\">readLink</d:PropertyStream>"));
  }
  
  @Test
  public void streamESStreamServerSidePagingNextXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$format=xml&$skiptoken=1%2A10");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<a:link rel=\"next\" href="));
    assertTrue(content.contains("ESStreamServerSidePaging?$format=xml&amp;%24skiptoken=2%2A10\"/>"));
    assertTrue(content.contains("<a:id>ESStreamServerSidePaging(11)</a:id>"));
    assertTrue(content.contains("<d:PropertyInt16 m:type=\"Int16\">11</d:PropertyInt16>"));
    assertTrue(content.contains("<d:PropertyStream m:type=\"Stream\">readLink</d:PropertyStream>"));
  }
  
  @Test
  public void streamESStreamServerSidePagingJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("{\"PropertyInt16\":2,"+
    "\"PropertyStream@odata.mediaEtag\":\"eTag\",\"PropertyStream@odata.mediaContentType\":\"image/jpeg\"}"));
    assertTrue(content.contains("\"@odata.nextLink\""));
    assertTrue(content.contains("ESStreamServerSidePaging?$format=json&%24skiptoken=1%2A10"));
  }
  
  
  @Test
  public void streamESStreamServerSidePagingJsonNext() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$format=json&$skiptoken=1%2A10");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("{\"PropertyInt16\":12,"+
    "\"PropertyStream@odata.mediaEtag\":\"eTag\",\"PropertyStream@odata.mediaContentType\":\"image/jpeg\"}"));
    assertTrue(content.contains("\"@odata.nextLink\""));
    assertTrue(content.contains("ESStreamServerSidePaging?$format=json&%24skiptoken=2%2A10"));
  }
  
  
  @Test
  public void streamCountXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$count=true&$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<a:link rel=\"next\" href="));
    assertTrue(content.contains("ESStreamServerSidePaging?$count=true&amp;$format=xml&amp;%24skiptoken=1%2A10\"/>"));
    assertTrue(content.contains("<a:id>ESStreamServerSidePaging(1)</a:id>"));
    assertTrue(content.contains("<m:count>504</m:count>"));
    assertTrue(content.contains("<d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>"));
    assertTrue(content.contains("<d:PropertyStream m:type=\"Stream\">readLink</d:PropertyStream>"));
  }
  
   
  @Test
  public void streamCountJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$count=true&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("{\"PropertyInt16\":2,"+
    "\"PropertyStream@odata.mediaEtag\":\"eTag\",\"PropertyStream@odata.mediaContentType\":\"image/jpeg\"}"));
    assertTrue(content.contains("\"@odata.nextLink\""));
    assertTrue(content.contains("ESStreamServerSidePaging?$count=true&$format=json&%24skiptoken=1%2A10"));
    assertTrue(content.contains("\"@odata.count\":504"));
  }
  
  @Test
  public void streamCountFalsetXml() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$count=false&$format=xml");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.APPLICATION_XML, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains("<a:link rel=\"next\" href="));
    assertTrue(content.contains("ESStreamServerSidePaging?$count=false&amp;$format=xml&amp;%24skiptoken=1%2A10\"/>"));
    assertTrue(content.contains("<a:id>ESStreamServerSidePaging(1)</a:id>"));
    assertTrue(content.contains("<d:PropertyInt16 m:type=\"Int16\">1</d:PropertyInt16>"));
    assertTrue(content.contains("<d:PropertyStream m:type=\"Stream\">readLink</d:PropertyStream>"));
    assertFalse(content.contains("<m:count>504</m:count>"));
    }
  
   
  @Test
  public void streamCountFalseJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamServerSidePaging?$count=false&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("{\"PropertyInt16\":2,"+
    "\"PropertyStream@odata.mediaEtag\":\"eTag\",\"PropertyStream@odata.mediaContentType\":\"image/jpeg\"}"));
    assertTrue(content.contains("\"@odata.nextLink\""));
    assertTrue(content.contains("ESStreamServerSidePaging?$count=false&$format=json&%24skiptoken=1%2A10"));
    assertFalse(content.contains("\"@odata.count\":504"));
    }
  
  @Test
  public void expandStreamPropOnComplexTypeJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamOnComplexProp(7)?$expand=PropertyCompWithStream/PropertyStream,"
        + "PropertyEntityStream,PropertyCompWithStream/NavPropertyETStreamOnComplexPropOne($expand=PropertyStream),"
        + "PropertyCompWithStream/NavPropertyETStreamOnComplexPropMany/$count&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty("OData-Version", "4.01");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"NavPropertyETStreamOnComplexPropOne\":{"
        + "\"PropertyInt16\":7,"
        + "\"PropertyStream@mediaEtag\":\"eTag\","
        + "\"PropertyStream@mediaContentType\":\"image/jpeg\","
        + "\"PropertyStream\":\"\ufffdioz\ufffd\\\"\ufffd\"}"));
    assertTrue(content.contains("\"NavPropertyETStreamOnComplexPropMany@count\":2"));
    assertTrue(content.contains("\"PropertyCompWithStream\":{"
        + "\"PropertyStream@mediaEtag\":\"eTag\","
        + "\"PropertyStream@mediaContentType\":\"image/jpeg\","
        + "\"PropertyStream\":\"\ufffdioz\ufffd\\\"\ufffd\","
        + "\"PropertyComp\":{\"PropertyInt16\":333,\"PropertyString\":\"TEST123\"}"));
    assertFalse(content.contains("\"PropertyInt16\":7,"
        + "\"PropertyInt32\":10,"
        + "\"PropertyEntityStream@mediaEtag\":\"eTag\","
        + "\"PropertyEntityStream@mediaContentType\":\"image/jpeg\","
        + "\"PropertyEntityStream\":\"ufffdioz\ufffd\\\"\ufffd\""));
    }
  
  @Test
  public void expandStreamPropOnComplexTypeWithRefJson() throws Exception {
    URL url = new URL(SERVICE_URI + "ESStreamOnComplexProp(7)?$expand="
        + "PropertyCompWithStream/NavPropertyETStreamOnComplexPropMany/$ref&$format=json");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.setRequestProperty("OData-Version", "4.01");
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());
    assertEquals(ContentType.JSON, ContentType.create(connection.getHeaderField(HttpHeader.CONTENT_TYPE)));

    final String content = IOUtils.toString(connection.getInputStream());

    assertTrue(content.contains("\"PropertyInt16\":7,"
        + "\"PropertyInt32\":10,\"PropertyEntityStream@mediaEtag\":\"eTag\","
        + "\"PropertyEntityStream@mediaContentType\":\"image/jpeg\","
        + "\"PropertyCompWithStream\":{\"PropertyStream@mediaEtag\":\"eTag\","
        + "\"PropertyStream@mediaContentType\":\"image/jpeg\","
        + "\"PropertyComp\":{\"PropertyInt16\":333,\"PropertyString\":\"TEST123\"},"
        + "\"NavPropertyETStreamOnComplexPropMany\":["
        + "{\"@id\":\"ESWithStream(32767)\"},"
        + "{\"@id\":\"ESWithStream(7)\"}]}"));
    }
  
  @Override
  protected ODataClient getClient() {
    return null;
  }

}
