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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * TODO: turn tests into assertions. Much or the association specific data
 * and tests are missing
 */
public class TripPinServiceTest {
  private static Server server = new Server();
  private static String baseURL;
  private static HttpClient http = new HttpClient();

  @BeforeClass
  public static void beforeTest() throws Exception {
    ServerConnector connector = new ServerConnector(server);
    server.setConnectors(new Connector[] { connector });

    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/trippin");
    context.addServlet(new ServletHolder(new TripPinServlet()), "/*");
    server.setHandler(context);
    server.start();
    int port = connector.getLocalPort();
    http.start();
    baseURL = "http://localhost:"+port+"/trippin";
  }

  @AfterClass
  public static void afterTest() throws Exception {
    server.stop();
  }

  @Test
  public void testMetadata() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/$metadata")
        .method(HttpMethod.GET)
        .send();
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testServiceDocument() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/")
        .method(HttpMethod.GET)
        .send();
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testEntitySet() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/People")
    .header("Content-Type", "application/json;odata.metadata=minimal")
    .method(HttpMethod.GET)
    .send();
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testEntitySetNav() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/People('russellwhyte')/Photo")
        .header("Content-Type", "application/json;odata.metadata=minimal")
        .method(HttpMethod.GET)
        .send();
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testEntitySetNavProperty() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/People('russellwhyte')/Photo/Name")
        .header("Content-Type", "application/json;odata.metadata=minimal")
        .method(HttpMethod.GET)
        .send();
    System.out.println(response.getContentAsString());
  }


  @Test
  public void testReadEntitySetWithPaging() throws Exception {
    ContentResponse response = http.newRequest(baseURL + "/People")
        .header("Prefer", "odata.maxpagesize=10").send();
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testReadEntityWithKey() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Airlines('AA')");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testRead$Count() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Airlines/$count");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testReadPrimitiveProperty() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Airlines('AA')/Name");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testReadPrimitiveArrayProperty() throws Exception {
    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')/Emails");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testReadPrimitivePropertyValue() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Airlines('AA')/Name/$value");
    System.out.println(response.getContentAsString());
  }

  @Test @Ignore
  // TODO: Support geometry types to make this run
  public void testReadComplexProperty() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Airports('KSFO')/Location");
    System.out.println(response.getContentAsString());
    fail("support geometry type");
  }

  @Test
  public void testReadComplexArrayProperty() throws Exception {
    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')/AddressInfo");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testReadMedia() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Photos(1)/$value");
    System.out.println(response.getStatus());
  }

  @Test
  public void testSingleton() throws Exception {
    ContentResponse response = http.GET(baseURL + "/Me");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testSelectOption() throws Exception {
    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')?$select=FirstName,LastName");
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testActionImportWithNoResponse() throws Exception {
    ContentResponse response = http.POST(baseURL + "/ResetDataSource").send();
    assertEquals(204, response.getStatus());
  }

  @Test
  public void testFunctionImport() throws Exception {
    //TODO: fails because of lack of geometery support
    ContentResponse response = http.GET(baseURL + "/GetNearestAirport(lat=23.0,lon=34.0)");
    System.out.println(response.getContentAsString());
  }

  @Test @Ignore
  public void testReferences() throws Exception {
    ContentResponse response = http.GET(baseURL + "/People/$ref");
    System.out.println(response.getContentAsString());
    fail(); // references are not implemented
  }

  @Test
  public void testCreateReadDeleteEntity() throws Exception {
    String payload = "{\n" +
        "         \"UserName\":\"olingo\",\n" +
        "         \"FirstName\":\"Olingo\",\n" +
        "         \"LastName\":\"Apache\",\n" +
        "         \"Emails\":[\n" +
        "            \"olingo@apache.org\"\n" +
        "         ],\n" +
        "         \"AddressInfo\":[\n" +
        "            {\n" +
        "               \"Address\":\"100 apache Ln.\",\n" +
        "               \"City\":{\n" +
        "                  \"CountryRegion\":\"United States\",\n" +
        "                  \"Name\":\"Boise\",\n" +
        "                  \"Region\":\"ID\"\n" +
        "               }\n" +
        "            }\n" +
        "         ],\n" +
        "         \"Gender\":0,\n" +
        "         \"Concurrency\":635585295719432047\n" +
        "}";
    ContentResponse response = http.POST(baseURL + "/People")
        .content(content(payload), "application/json")
        .header("Prefer", "return=minimal")
        .send();
    // the below woud be 204, if minimal was not supplied
    assertEquals(204, response.getStatus());
    System.out.println(response.getContentAsString());
    System.out.println(response.getHeaders());

    String location = response.getHeaders().get("Location");
    response = http.GET(location);
    assertEquals(200, response.getStatus());
    System.out.println(response.getContentAsString());

    response = http.newRequest(location).method(HttpMethod.DELETE).send();
    assertEquals(204, response.getStatus());
  }

  @Test
  public void testUpdatePtimitiveProperty() throws Exception {
    String payload = "{"
        + " \"value\":\"Pilar Ackerman\""
        + "}";

    String editUrl = baseURL + "/People('russellwhyte')/FirstName";
    ContentResponse response = http.newRequest(editUrl)
        .content(content(payload), "application/json")
        .method(HttpMethod.PUT)
        .send();
    assertEquals(204, response.getStatus());

    response = http.GET(editUrl);
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testUpdatePrimitiveArrayProperty() throws Exception {
    String payload = "{"
        + " \"value\": [\n" +
        "       \"olingo@apache.com\"\n" +
        "    ]"
        + "}";

    String editUrl = baseURL + "/People('russellwhyte')/Emails";
    ContentResponse response = http.newRequest(editUrl)
        .content(content(payload), "application/json")
        .method(HttpMethod.PUT)
        .send();
    assertEquals(204, response.getStatus());

    response = http.GET(editUrl);
    System.out.println(response.getContentAsString());
  }

  @Test
  public void testDeleteProperty() throws Exception {
    String editUrl = baseURL + "/People('russellwhyte')/FirstName";
    ContentResponse response = http.newRequest(editUrl)
        .method(HttpMethod.DELETE)
        .send();
    assertEquals(204, response.getStatus());

    response = http.GET(editUrl);
    System.out.println(response.getContentAsString());
    assertEquals(204, response.getStatus());
  }

  @Test
  public void testAddReference() throws Exception {
    String msg = "{\n" +
        "\"@odata.id\": \"/People('vincentcalabrese')\"\n" +
        "}";
    String editUrl = baseURL + "/People('russellwhyte')/Friends/$ref";
    ContentResponse response = http.newRequest(editUrl)
        .method(HttpMethod.POST)
        .content(content(msg))
        .header("Content-Type", "application/json;odata.metadata=minimal")
        .send();
    assertEquals(204, response.getStatus());
  }

  private ContentProvider content(final String msg) {
    return new ContentProvider() {
      boolean hasNext = true;

      @Override
      public Iterator<ByteBuffer> iterator() {
        return new Iterator<ByteBuffer>() {
          @Override
          public boolean hasNext() {
            return hasNext;
          }
          @Override
          public ByteBuffer next() {
            hasNext = false;
            return ByteBuffer.wrap(msg.getBytes());
          }
          @Override
          public void remove() {
          }
        };
      }
      @Override
      public long getLength() {
        return msg.length();
      }
    };
  }
}
