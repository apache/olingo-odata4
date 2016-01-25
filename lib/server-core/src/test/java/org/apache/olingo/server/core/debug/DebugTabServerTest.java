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
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class DebugTabServerTest extends AbstractDebugTabTest {

  @Test
  public void nullServerInformationMustNotleadToException() throws Exception {
    DebugTabServer serverTab = new DebugTabServer(null);

    assertEquals("null", createJson(serverTab));
    String html = createHtml(serverTab);
    assertTrue(html.startsWith("<h2>Library Version</h2>"));
    assertTrue(html.contains("<h2>Server Environment</h2>\n"));
  }

  @Test
  public void initialServerInformationMustNotleadToException() throws Exception {
    DebugTabServer serverTab = new DebugTabServer(Collections.<String, String> emptyMap());

    assertEquals("null", createJson(serverTab));
    String html = createHtml(serverTab);
    assertTrue(html.startsWith("<h2>Library Version</h2>"));
    assertTrue(html.contains("<h2>Server Environment</h2>\n"));
  }

  @Test
  public void twoParametersNoNull() throws Exception {
    Map<String, String> env = new LinkedHashMap<String, String>();
    env.put("key1", "value1");
    env.put("key2", "value2");
    DebugTabServer serverTab = new DebugTabServer(env);

    String expectedJson = "{\"key1\":\"value1\",\"key2\":\"value2\"}";

    assertEquals(expectedJson, createJson(serverTab));
    String html = createHtml(serverTab);
    assertTrue(html.contains("<tr><td class=\"name\">key1</td><td class=\"value\">value1</td></tr>"));
    assertTrue(html.contains("<tr><td class=\"name\">key2</td><td class=\"value\">value2</td></tr>"));
    assertTrue(html.endsWith("</table>\n"));
  }

  @Test
  public void twoParametersWithNull() throws Exception {
    Map<String, String> env = new LinkedHashMap<String, String>();
    env.put("key1", null);
    env.put("key2", null);
    DebugTabServer serverTab = new DebugTabServer(env);

    String expectedJson = "{\"key1\":null,\"key2\":null}";

    assertEquals(expectedJson, createJson(serverTab));
    String html = createHtml(serverTab);
    assertTrue(html.contains("<tr><td class=\"name\">key1</td><td class=\"value\">null</td></tr>"));
    assertTrue(html.contains("<tr><td class=\"name\">key2</td><td class=\"value\">null</td></tr>"));
    assertTrue(html.endsWith("</table>\n"));
  }

}
