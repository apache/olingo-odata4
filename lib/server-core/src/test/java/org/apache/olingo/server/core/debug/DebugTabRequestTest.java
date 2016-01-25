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
package org.apache.olingo.server.core.debug;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.junit.Test;

public class DebugTabRequestTest extends AbstractDebugTabTest {

  @Test
  public void initialRequestMustNotleadToException() throws Exception {
    String expectedJson = "{\"method\":\"unkown\",\"uri\":\"unkown\",\"protocol\":\"unkown\"}";
    String expectedHtml = "<h2>Request Method</h2>\n"
        + "<p>unkown</p>\n"
        + "<h2>Request URI</h2>\n"
        + "<p>unkown</p>\n"
        + "<h2>Request Protocol</h2>\n"
        + "<p>unkown</p>\n"
        + "<h2>Request Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "</tbody>\n"
        + "</table>\n";

    DebugTabRequest requestTab = new DebugTabRequest(null);
    assertEquals(expectedJson, createJson(requestTab));
    assertEquals(expectedHtml, createHtml(requestTab));

    requestTab = new DebugTabRequest(new ODataRequest());
    assertEquals(expectedJson, createJson(requestTab));
    assertEquals(expectedHtml, createHtml(requestTab));
  }

  @Test
  public void onlyProtocolNotSet() throws Exception {
    String expectedJson = "{\"method\":\"GET\",\"uri\":\"def&\",\"protocol\":\"unkown\"}";
    String expectedHtml = "<h2>Request Method</h2>\n"
        + "<p>GET</p>\n"
        + "<h2>Request URI</h2>\n"
        + "<p>def&amp;</p>\n"
        + "<h2>Request Protocol</h2>\n"
        + "<p>unkown</p>\n"
        + "<h2>Request Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "</tbody>\n"
        + "</table>\n";

    ODataRequest oDataRequest = new ODataRequest();
    oDataRequest.setMethod(HttpMethod.GET);
    oDataRequest.setRawRequestUri("def&");

    DebugTabRequest requestTab = new DebugTabRequest(oDataRequest);
    assertEquals(expectedJson, createJson(requestTab));
    assertEquals(expectedHtml, createHtml(requestTab));
  }

  @Test
  public void singleHeaderValue() throws Exception {
    String expectedJson =
        "{\"method\":\"GET\",\"uri\":\"def&\",\"protocol\":\"def&\",\"headers\":{\"HeaderName\":\"Value1\"}}";
    String expectedHtml = "<h2>Request Method</h2>\n"
        + "<p>GET</p>\n"
        + "<h2>Request URI</h2>\n"
        + "<p>def&amp;</p>\n"
        + "<h2>Request Protocol</h2>\n"
        + "<p>def&amp;</p>\n"
        + "<h2>Request Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr><td class=\"name\">HeaderName</td><td class=\"value\">Value1</td></tr>\n"
        + "</tbody>\n"
        + "</table>\n";

    ODataRequest oDataRequest = new ODataRequest();
    oDataRequest.setMethod(HttpMethod.GET);
    oDataRequest.setRawRequestUri("def&");
    oDataRequest.setProtocol("def&");
    List<String> headerValues = new ArrayList<String>();
    headerValues.add("Value1");
    oDataRequest.addHeader("HeaderName", headerValues);

    DebugTabRequest requestTab = new DebugTabRequest(oDataRequest);
    assertEquals(expectedJson, createJson(requestTab));
    assertEquals(expectedHtml, createHtml(requestTab));
  }

  @Test
  public void multiHeaderValueResultsInMap() throws Exception {
    String expectedJson = "{\"method\":\"GET\",\"uri\":\"def&\",\"protocol\":\"def&\","
        + "\"headers\":{\"HeaderName\":[\"Value1\",\"Value2\"]}}";
    String expectedHtml = "<h2>Request Method</h2>\n"
        + "<p>GET</p>\n"
        + "<h2>Request URI</h2>\n"
        + "<p>def&amp;</p>\n"
        + "<h2>Request Protocol</h2>\n"
        + "<p>def&amp;</p>\n"
        + "<h2>Request Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr><td class=\"name\">HeaderName</td><td class=\"value\">Value1</td></tr>\n"
        + "<tr><td class=\"name\">HeaderName</td><td class=\"value\">Value2</td></tr>\n"
        + "</tbody>\n"
        + "</table>\n";

    ODataRequest oDataRequest = new ODataRequest();
    oDataRequest.setMethod(HttpMethod.GET);
    oDataRequest.setRawRequestUri("def&");
    oDataRequest.setProtocol("def&");
    List<String> headerValues = new ArrayList<String>();
    headerValues.add("Value1");
    headerValues.add("Value2");
    oDataRequest.addHeader("HeaderName", headerValues);

    DebugTabRequest requestTab = new DebugTabRequest(oDataRequest);
    assertEquals(expectedJson, createJson(requestTab));
    assertEquals(expectedHtml, createHtml(requestTab));
  }

}
