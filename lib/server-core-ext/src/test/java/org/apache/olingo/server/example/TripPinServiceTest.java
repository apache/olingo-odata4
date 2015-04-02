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
package org.apache.olingo.server.example;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Please note that NONE of the system query options are developed in the sample
 * service like $filter, $orderby etc. So using those options will be ignored
 * right now. These tests designed to test the framework, all options are responsibilities
 * of service developer.
 */
public class TripPinServiceTest {
  private static Tomcat tomcat = new Tomcat();
  private static String baseURL;
  private static DefaultHttpClient http = new DefaultHttpClient();
  private static final int TOMCAT_PORT = 9900;

  @BeforeClass
  public static void beforeTest() throws Exception {
    tomcat.setPort(TOMCAT_PORT);
    File baseDir = new File(System.getProperty("java.io.tmpdir"));
    Context cxt = tomcat.addContext("/", baseDir.getAbsolutePath());
    Tomcat.addServlet(cxt, "trippin", new TripPinServlet());
    cxt.addServletMapping("/*", "trippin");
    baseURL = "http://" + tomcat.getHost().getName() + ":"+ TOMCAT_PORT;
    tomcat.start();
  }

  @AfterClass
  public static void afterTest() throws Exception {
    tomcat.stop();
  }

  private HttpHost getLocalhost() {
    return new HttpHost(tomcat.getHost().getName(), 9900);
  }

  @Test
  public void testEntitySet() throws Exception {
    HttpRequest req = new HttpGet("/People");
    req.setHeader("Content-Type", "application/json;odata.metadata=minimal");

    HttpResponse response = http.execute(getLocalhost(), req);
    assertEquals(200, response.getStatusLine().getStatusCode());

    JsonNode node = getJSONNode(response);

    assertEquals("$metadata#People", node.get("@odata.context").asText());
    assertEquals(baseURL+"/People?$skiptoken=8", node.get("@odata.nextLink").asText());

    JsonNode person = ((ArrayNode)node.get("value")).get(0);
    assertEquals("russellwhyte", person.get("UserName").asText());
  }


  private JsonNode getJSONNode(HttpResponse response) throws IOException,
      JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode node = objectMapper.readTree(response.getEntity().getContent());
    return node;
  }

//  private static Server server = new Server();
//  private static String baseURL;
//  private static HttpClient http = new HttpClient();
//
//  @BeforeClass
//  public static void beforeTest() throws Exception {
//    ServerConnector connector = new ServerConnector(server);
//    server.setConnectors(new Connector[] { connector });
//
//    ServletContextHandler context = new ServletContextHandler();
//    context.setContextPath("/trippin");
//    context.addServlet(new ServletHolder(new TripPinServlet()), "/*");
//    server.setHandler(context);
//    server.start();
//    int port = connector.getLocalPort();
//    http.start();
//    baseURL = "http://localhost:"+port+"/trippin";
//  }
//
//  @AfterClass
//  public static void afterTest() throws Exception {
//    server.stop();
//  }
//
//  @Test
//  public void testEntitySet() throws Exception {
//    ContentResponse response = http.newRequest(baseURL + "/People")
//    .header("Content-Type", "application/json;odata.metadata=minimal")
//    .method(HttpMethod.GET)
//    .send();
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//
//    assertEquals("$metadata#People", node.get("@odata.context").asText());
//    assertEquals(baseURL+"/People?$skiptoken=8", node.get("@odata.nextLink").asText());
//
//    JsonNode person = ((ArrayNode)node.get("value")).get(0);
//    assertEquals("russellwhyte", person.get("UserName").asText());
//  }
//
//  private JsonNode getJSONNode(ContentResponse response) throws IOException,
//      JsonProcessingException {
//    ObjectMapper objectMapper = new ObjectMapper();
//    JsonNode node = objectMapper.readTree(response.getContent());
//    return node;
//  }
//
//  @Test
//  public void testReadEntitySetWithPaging() throws Exception {
//    ContentResponse response = http.newRequest(baseURL + "/People")
//        .header("Prefer", "odata.maxpagesize=10").send();
//
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People", node.get("@odata.context").asText());
//    assertEquals(baseURL+"/People?$skiptoken=10", node.get("@odata.nextLink").asText());
//
//    JsonNode person = ((ArrayNode)node.get("value")).get(0);
//    assertEquals("russellwhyte", person.get("UserName").asText());
//
//    assertNotNull(response.getHeaders().get("Preference-Applied"));
//  }
//
//  @Test
//  public void testReadEntityWithKey() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines('AA')");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#Airlines/$entity", node.get("@odata.context").asText());
//    assertEquals("American Airlines", node.get("Name").asText());
//  }
//
//  @Test
//  public void testReadEntityWithNonExistingKey() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines('OO')");
//    assertEquals(404, response.getStatus());
//  }
//
//  @Test
//  public void testRead$Count() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines/$count");
//    assertEquals(200, response.getStatus());
//    assertEquals("15", response.getContentAsString());
//  }
//
//  @Test
//  public void testReadPrimitiveProperty() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines('AA')/Name");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#Airlines('AA')/Name", node.get("@odata.context").asText());
//    assertEquals("American Airlines", node.get("value").asText());
//  }
//
//  @Test
//  public void testReadNonExistentProperty() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines('AA')/Unknown");
//    assertEquals(404, response.getStatus());
//  }
//
//  @Test
//  public void testReadPrimitiveArrayProperty() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')/Emails");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Emails", node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("Russell@example.com", ((ArrayNode)node.get("value")).get(0).asText());
//    assertEquals("Russell@contoso.com", ((ArrayNode)node.get("value")).get(1).asText());
//  }
//
//  @Test
//  public void testReadPrimitivePropertyValue() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airlines('AA')/Name/$value");
//    assertEquals(200, response.getStatus());
//    assertEquals("American Airlines", response.getContentAsString());
//  }
//
//  @Test @Ignore
//  // TODO: Support geometry types to make this run
//  public void testReadComplexProperty() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Airports('KSFO')/Location");
//    fail("support geometry type");
//  }
//
//  @Test
//  public void testReadComplexArrayProperty() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')/AddressInfo");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/AddressInfo", node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("187 Suffolk Ln.", ((ArrayNode)node.get("value")).get(0).get("Address").asText());
//  }
//
//  @Test
//  public void testReadMedia() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Photos(1)/$value");
//    assertEquals(200, response.getStatus());
//  }
//
//  @Test
//  public void testCreateMedia() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Photos(1)/$value";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content("bytecontents"), "image/jpeg")
//        .method(HttpMethod.PUT)
//        .send();
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testDeleteMedia() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Photos(1)/$value";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content("bytecontents"), "image/jpeg")
//        .method(HttpMethod.DELETE)
//        .send();
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testCreateStream() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Airlines('AA')/Picture";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content("bytecontents"), "image/jpeg")
//        .method(HttpMethod.POST)
//        .send();
//    // method not allowed
//    assertEquals(405, response.getStatus());
//  }
//
//  @Test
//  public void testCreateStream2() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Airlines('AA')/Picture";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content("bytecontents"), "image/jpeg")
//        .method(HttpMethod.PUT)
//        .send();
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testDeleteStream() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Airlines('AA')/Picture";
//    ContentResponse response = http.newRequest(editUrl)
//        .method(HttpMethod.DELETE)
//        .send();
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testReadStream() throws Exception {
//    // treating update and create as same for now, as there is details about
//    // how entity payload and media payload can be sent at same time in request's body
//    String editUrl = baseURL + "/Airlines('AA')/Picture";
//    ContentResponse response = http.newRequest(editUrl)
//        .method(HttpMethod.GET)
//        .send();
//    assertEquals(200, response.getStatus());
//  }
//
//  @Test
//  public void testLambdaAny() throws Exception {
//    // this is just testing to see the labba expresions are going through the
//    // framework, none of the system options are not implemented in example service
//    String query = "Friends/any(d:d/UserName eq 'foo')";
//    ContentResponse response = http.newRequest(baseURL + "/People?$filter="+Encoder.encode(query))
//        .method(HttpMethod.GET)
//        .send();
//    assertEquals(200, response.getStatus());
//  }
//
//  @Test
//  public void testSingleton() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/Me");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#Me", node.get("@odata.context").asText());
//    assertEquals("russellwhyte", node.get("UserName").asText());
//  }
//
//  @Test
//  public void testSelectOption() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')?$select=FirstName,LastName");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People(FirstName,LastName)/$entity", node.get("@odata.context").asText());
//    assertEquals("Russell", node.get("FirstName").asText());
//  }
//
//  @Test
//  public void testActionImportWithNoResponse() throws Exception {
//    ContentResponse response = http.POST(baseURL + "/ResetDataSource").send();
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testFunctionImport() throws Exception {
//    //TODO: fails because of lack of geometery support
//    ContentResponse response = http.GET(baseURL + "/GetNearestAirport(lat=23.0,lon=34.0)");
//  }
//
//  @Test
//  public void testBadReferences() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/People('russelwhyte')/$ref");
//    assertEquals(405, response.getStatus());
//  }
//
//  @Test
//  public void testReadReferences() throws Exception {
//    ContentResponse response = http.GET(baseURL + "/People('russellwhyte')/Friends/$ref");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#Collection($ref)", node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("/People('scottketchum')", ((ArrayNode)node.get("value")).get(0).get("@odata.id").asText());
//  }
//
//  @Test
//  public void testAddCollectionReferences() throws Exception {
//    //GET
//    ContentResponse response = http.GET(baseURL + "/People('kristakemp')/Friends/$ref");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//
//    assertTrue(node.get("value").isArray());
//    assertEquals("/People('genevievereeves')", ((ArrayNode)node.get("value")).get(0).get("@odata.id").asText());
//    assertNull(((ArrayNode)node.get("value")).get(1));
//
//    //ADD
//    String payload = "{\n" +
//        "  \"@odata.context\": \""+baseURL+"/$metadata#Collection($ref)\",\n" +
//        "  \"value\": [\n" +
//        "    { \"@odata.id\": \"People('russellwhyte')\" },\n" +
//        "    { \"@odata.id\": \"People('scottketchum')\" } \n" +
//        "  ]\n" +
//        "}";
//    response = http.POST(baseURL + "/People('kristakemp')/Friends/$ref")
//        .content(content(payload), "application/json")
//        .send();
//    assertEquals(204, response.getStatus());
//
//    //GET
//    response = http.GET(baseURL + "/People('kristakemp')/Friends/$ref");
//    assertEquals(200, response.getStatus());
//    node = getJSONNode(response);
//
//    assertTrue(node.get("value").isArray());
//    assertEquals("/People('genevievereeves')", ((ArrayNode)node.get("value")).get(0).get("@odata.id").asText());
//    assertEquals("/People('russellwhyte')", ((ArrayNode)node.get("value")).get(1).get("@odata.id").asText());
//    assertEquals("/People('scottketchum')", ((ArrayNode)node.get("value")).get(2).get("@odata.id").asText());
//  }
//
//
//  @Test
//  public void testEntityId() throws Exception {
//    ContentResponse response = http.GET(baseURL+"/$entity?$id="+baseURL + "/People('kristakemp')");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People/$entity", node.get("@odata.context").asText());
//    assertEquals("kristakemp", node.get("UserName").asText());
//
//    // using relative URL
//    response = http.GET(baseURL+"/$entity?$id="+"People('kristakemp')");
//    assertEquals(200, response.getStatus());
//    node = getJSONNode(response);
//    assertEquals("$metadata#People/$entity", node.get("@odata.context").asText());
//    assertEquals("kristakemp", node.get("UserName").asText());
//  }
//
//  @Test
//  public void testCreateReadDeleteEntity() throws Exception {
//    String payload = "{\n" +
//        "         \"UserName\":\"olingodude\",\n" +
//        "         \"FirstName\":\"Olingo\",\n" +
//        "         \"LastName\":\"Apache\",\n" +
//        "         \"Emails\":[\n" +
//        "            \"olingo@apache.org\"\n" +
//        "         ],\n" +
//        "         \"AddressInfo\":[\n" +
//        "            {\n" +
//        "               \"Address\":\"100 apache Ln.\",\n" +
//        "               \"City\":{\n" +
//        "                  \"CountryRegion\":\"United States\",\n" +
//        "                  \"Name\":\"Boise\",\n" +
//        "                  \"Region\":\"ID\"\n" +
//        "               }\n" +
//        "            }\n" +
//        "         ],\n" +
//        "         \"Gender\":\"0\",\n" +
//        "         \"Concurrency\":635585295719432047\n" +
//        "}";
//    ContentResponse response = http.POST(baseURL + "/People")
//        .content(content(payload), "application/json")
//        .header("Prefer", "return=minimal")
//        .send();
//    // the below woud be 204, if minimal was not supplied
//    assertEquals(204, response.getStatus());
//    assertEquals("/People('olingodude')", response.getHeaders().get("Location"));
//    assertEquals("return=minimal", response.getHeaders().get("Preference-Applied"));
//
//    String location = baseURL+response.getHeaders().get("Location");
//    response = http.GET(location);
//    assertEquals(200, response.getStatus());
//
//    response = http.newRequest(location).method(HttpMethod.DELETE).send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(location);
//    assertEquals(404, response.getStatus());
//  }
//
//
//  @Test
//  public void testCreateEntityWithLinkToRelatedEntities() throws Exception {
//    String payload = "{\n" +
//        "         \"UserName\":\"olingo\",\n" +
//        "         \"FirstName\":\"Olingo\",\n" +
//        "         \"LastName\":\"Apache\",\n" +
//        "         \"Emails\":[\n" +
//        "            \"olingo@apache.org\"\n" +
//        "         ],\n" +
//        "         \"AddressInfo\":[\n" +
//        "            {\n" +
//        "               \"Address\":\"100 apache Ln.\",\n" +
//        "               \"City\":{\n" +
//        "                  \"CountryRegion\":\"United States\",\n" +
//        "                  \"Name\":\"Boise\",\n" +
//        "                  \"Region\":\"ID\"\n" +
//        "               }\n" +
//        "            }\n" +
//        "         ],\n" +
//        "         \"Gender\":\"0\",\n" +
//        "         \"Concurrency\":635585295719432047,\n" +
//        "\"Friends@odata.bind\":[\"" +
//         baseURL+"/People('russellwhyte')\",\""+
//         baseURL+"/People('scottketchum')\""+
//        "]"+
//        "}";
//    ContentResponse response = http.POST(baseURL + "/People")
//        .content(content(payload), "application/json")
//        .header("Prefer", "return=minimal")
//        .send();
//    // the below woud be 204, if minimal was not supplied
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(baseURL+"/People('olingo')/Friends");
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People", node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("scottketchum", ((ArrayNode)node.get("value")).get(1).get("UserName").asText());
//  }
//
//  @Test
//  public void testUpdatePrimitiveProperty() throws Exception {
//    String payload = "{"
//        + " \"value\":\"Pilar Ackerman\""
//        + "}";
//
//    String editUrl = baseURL + "/People('russellwhyte')/FirstName";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content(payload), "application/json")
//        .method(HttpMethod.PUT)
//        .send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/FirstName", node.get("@odata.context").asText());
//    assertEquals("Pilar Ackerman", node.get("value").asText());
//  }
//
//  @Test
//  public void testUpdatePrimitiveArrayProperty() throws Exception {
//    String payload = "{"
//        + " \"value\": [\n" +
//        "       \"olingo@apache.com\"\n" +
//        "    ]"
//        + "}";
//
//    String editUrl = baseURL + "/People('russellwhyte')/Emails";
//    ContentResponse response = http.newRequest(editUrl)
//        .content(content(payload), "application/json")
//        .method(HttpMethod.PUT)
//        .send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Emails", node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("olingo@apache.com", ((ArrayNode)node.get("value")).get(0).asText());
//  }
//
//  @Test
//  public void testDeleteProperty() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/FirstName";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("Russell", node.get("value").asText());
//
//    response = http.newRequest(editUrl)
//        .method(HttpMethod.DELETE)
//        .send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(editUrl);
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityCollection() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Friends";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People", node.get("@odata.context").asText());
//
//    JsonNode person = ((ArrayNode)node.get("value")).get(0);
//    assertEquals("scottketchum", person.get("UserName").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityCollection2() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Friends('scottketchum')/Trips";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Friends('scottketchum')/Trips",
//        node.get("@odata.context").asText());
//    assertTrue(node.get("value").isArray());
//    assertEquals("1001", ((ArrayNode)node.get("value")).get(0).get("TripId").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntity() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Trips(1003)";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Trips/$entity",
//        node.get("@odata.context").asText());
//    assertEquals("f94e9116-8bdd-4dac-ab61-08438d0d9a71", node.get("ShareId").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityNotExisting() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Trips(9999)";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntitySetNotExisting() throws Exception {
//    String editUrl = baseURL + "/People('jhondoe')/Trips";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('jhondoe')/Trips",
//        node.get("@odata.context").asText());
//    assertEquals(0, ((ArrayNode)node.get("value")).size());
//  }
//
//  @Test
//  public void testBadNavigationProperty() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Unknown";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(404, response.getStatus());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityProperty() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Trips(1003)/PlanItems(5)/ConfirmationCode";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Trips(1003)/PlanItems(5)/ConfirmationCode",
//        node.get("@odata.context").asText());
//
//    assertEquals("JH58494", node.get("value").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityMultipleDerivedTypes() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Trips(1003)/PlanItems";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Trips(1003)/PlanItems",
//        node.get("@odata.context").asText());
//
//    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Flight",
//        ((ArrayNode) node.get("value")).get(0).get("@odata.type").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityCoolectionDerivedFilter() throws Exception {
//    String editUrl = baseURL
//        + "/People('russellwhyte')/Trips(1003)/PlanItems/Microsoft.OData.SampleService.Models.TripPin.Event";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Trips(1003)/PlanItems/"
//        + "Microsoft.OData.SampleService.Models.TripPin.Event",
//        node.get("@odata.context").asText());
//
//    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Event",
//        ((ArrayNode) node.get("value")).get(0).get("@odata.type").asText());
//  }
//
//  @Test
//  public void testReadNavigationPropertyEntityDerivedFilter() throws Exception {
//    String editUrl = baseURL+ "/People('russellwhyte')/Trips(1003)/PlanItems(56)/"
//        + "Microsoft.OData.SampleService.Models.TripPin.Event";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("$metadata#People('russellwhyte')/Trips(1003)/PlanItems/"
//        + "Microsoft.OData.SampleService.Models.TripPin.Event/$entity",
//        node.get("@odata.context").asText());
//
//    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Event", node.get("@odata.type").asText());
//    assertEquals("56", node.get("PlanItemId").asText());
//  }
//
//  @Test
//  public void testUpdateReference() throws Exception {
//    ContentResponse response = http.GET(baseURL+"/People('ronaldmundy')/Photo/$ref");
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("/Photos(12)", node.get("@odata.id").asText());
//
//    String msg = "{\n" +
//        "\"@odata.id\": \"/Photos(11)\"\n" +
//        "}";
//    String editUrl = baseURL + "/People('ronaldmundy')/Photo/$ref";
//    response = http.newRequest(editUrl)
//        .method(HttpMethod.PUT)
//        .content(content(msg))
//        .header("Content-Type", "application/json;odata.metadata=minimal")
//        .send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(baseURL+"/People('ronaldmundy')/Photo/$ref");
//    assertEquals(200, response.getStatus());
//    node = getJSONNode(response);
//    assertEquals("/Photos(11)", node.get("@odata.id").asText());
//  }
//
//  @Test
//  public void testAddDelete2ReferenceCollection() throws Exception {
//    // add
//    String msg = "{\n" +
//        "\"@odata.id\": \"/People('russellwhyte')\"\n" +
//        "}";
//    String editUrl = baseURL + "/People('vincentcalabrese')/Friends/$ref";
//    ContentResponse response = http.newRequest(editUrl)
//        .method(HttpMethod.POST)
//        .content(content(msg))
//        .header("Content-Type", "application/json;odata.metadata=minimal")
//        .send();
//    assertEquals(204, response.getStatus());
//
//    // get
//    response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    JsonNode node = getJSONNode(response);
//    assertEquals("/People('russellwhyte')",
//        ((ArrayNode) node.get("value")).get(2).get("@odata.id").asText());
//
//    //delete
//    response = http.newRequest(editUrl+"?$id="+baseURL+"/People('russellwhyte')")
//        .method(HttpMethod.DELETE)
//        .content(content(msg))
//        .header("Content-Type", "application/json;odata.metadata=minimal")
//        .send();
//    assertEquals(204, response.getStatus());
//
//    // get
//    response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//    node = getJSONNode(response);
//    assertNull("/People('russellwhyte')", ((ArrayNode) node.get("value")).get(2));
//  }
//
//  @Test
//  public void testDeleteReference() throws Exception {
//    String editUrl = baseURL + "/People('russellwhyte')/Photo/$ref";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//
//    response = http.newRequest(editUrl)
//        .method(HttpMethod.DELETE)
//        .send();
//    assertEquals(204, response.getStatus());
//
//    response = http.GET(editUrl);
//    assertEquals(204, response.getStatus());
//  }
//
//  @Test
//  public void testCrossJoin() throws Exception {
//    String editUrl = baseURL + "/$crossjoin(People,Airlines)";
//    ContentResponse response = http.GET(editUrl);
//    assertEquals(200, response.getStatus());
//  }
//
//  public static ContentProvider content(final String msg) {
//    return new ContentProvider() {
//      boolean hasNext = true;
//
//      @Override
//      public Iterator<ByteBuffer> iterator() {
//        return new Iterator<ByteBuffer>() {
//          @Override
//          public boolean hasNext() {
//            return hasNext;
//          }
//          @Override
//          public ByteBuffer next() {
//            hasNext = false;
//            return ByteBuffer.wrap(msg.getBytes());
//          }
//          @Override
//          public void remove() {
//          }
//        };
//      }
//      @Override
//      public long getLength() {
//        return msg.length();
//      }
//    };
//  }
}
