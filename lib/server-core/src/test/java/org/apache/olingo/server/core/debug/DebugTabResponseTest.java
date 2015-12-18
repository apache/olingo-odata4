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

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.junit.Test;

public class DebugTabResponseTest extends AbstractDebugTabTest {

  @Test
  public void nullResponseMustNotLeadToException() throws Exception {
    DebugTabResponse tab = new DebugTabResponse(null);

    String expectedJson = "{\"status\":{\"code\":\"500\",\"info\":\"Internal Server Error\"},\"body\":null}";
    String expectedHtml = "<h2>Status Code</h2>\n"
        + "<p>500 Internal Server Error</p>\n"
        + "<h2>Response Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "</tbody>\n"
        + "</table>\n"
        + "<h2>Response Body</h2>\n"
        + "<p>ODataLibrary: no response body</p>\n";

    assertEquals(expectedJson, createJson(tab));
    assertEquals(expectedHtml, createHtml(tab));
  }

  @Test
  public void withInformationNoBody() throws Exception {
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    response.setHeader("headername", "headervalue");
    response.setHeader("headername2", "headervalue2");
    DebugTabResponse tab = new DebugTabResponse(response);

    String expectedJson = "{\"status\":{\"code\":\"204\",\"info\":\"No Content\"},"
        + "\"headers\":{\"headername\":\"headervalue\",\"headername2\":\"headervalue2\"},\"body\":null}";
    String expectedHtml = "<h2>Status Code</h2>\n"
        + "<p>204 No Content</p>\n"
        + "<h2>Response Headers</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr><td class=\"name\">headername</td><td class=\"value\">headervalue</td></tr>\n"
        + "<tr><td class=\"name\">headername2</td><td class=\"value\">headervalue2</td></tr>\n"
        + "</tbody>\n"
        + "</table>\n"
        + "<h2>Response Body</h2>\n"
        + "<p>ODataLibrary: no response body</p>\n";
    assertEquals(expectedJson, createJson(tab));
    assertEquals(expectedHtml, createHtml(tab));
  }

}
