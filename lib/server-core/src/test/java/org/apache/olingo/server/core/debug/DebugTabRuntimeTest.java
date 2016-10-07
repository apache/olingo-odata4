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

import java.util.Arrays;

import org.apache.olingo.server.api.debug.RuntimeMeasurement;
import org.junit.Test;

public class DebugTabRuntimeTest extends AbstractDebugTabTest {

  @Test
  public void runtime() throws Exception {
    final DebugTabRuntime tab = new DebugTabRuntime(Arrays.asList(
        createMeasurement("class1", "method1", 0, 42),
        createMeasurement("class2", "method2", 12, 23),
        createMeasurement("class2", "method2", 24, 26),
        createMeasurement("class3", "method3", 98, 0)));
    assertEquals("[{\"class\":\"class1\",\"method\":\"method1\",\"duration\":42,\"unit\":\"µs\",\"children\":["
        + "{\"class\":\"class2\",\"method\":\"method2\",\"duration\":13,\"unit\":\"µs\"}]},"
        + "{\"class\":\"class3\",\"method\":\"method3\",\"duration\":null}]",
        createJson(tab));

    assertEquals("<ol class=\"tree\">\n"
        + "<li>\n"
        + "<span class=\"code\"><span class=\"draw\">&#x251C;&#x2500;&nbsp;</span>"
        + "<span class=\"class\">class1</span>.<span class=\"method\">method1(&hellip;)</span>"
        + "</span><span class=\"numeric\" title=\"Gross duration\">42&nbsp;&micro;s</span>\n"
        + "<ol class=\"tree\">\n"
        + "<li>\n"
        + "<span class=\"code\"><span class=\"draw\">&#x2502;&nbsp;&nbsp;&#x2514;&#x2500;&nbsp;</span>"
        + "<span class=\"class\">class2</span>.<span class=\"method\">method2(&hellip;)</span>"
        + "</span><span class=\"numeric\" title=\"Gross duration\">13&nbsp;&micro;s</span>\n"
        + "</li>\n"
        + "</ol>\n"
        + "</li>\n"
        + "<li>\n"
        + "<span class=\"code\"><span class=\"draw\">&#x2514;&#x2500;&nbsp;</span>"
        + "<span class=\"class\">class3</span>.<span class=\"method\">method3(&hellip;)</span>"
        + "</span><span class=\"null\" title=\"Stop time missing\">unfinished</span>\n"
        + "</li>\n"
        + "</ol>\n",
        createHtml(tab));
  }

  private RuntimeMeasurement createMeasurement(final String className, final String methodName,
      final int startMilliseconds, final int stopMilliseconds) {
    RuntimeMeasurement measurement = new RuntimeMeasurement();
    measurement.setClassName(className);
    measurement.setMethodName(methodName);
    measurement.setTimeStarted(startMilliseconds * 1000);
    measurement.setTimeStopped(stopMilliseconds * 1000);
    return measurement;
  }
}
