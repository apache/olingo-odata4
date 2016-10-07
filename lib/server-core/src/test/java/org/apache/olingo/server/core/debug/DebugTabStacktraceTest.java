/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * + "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * + "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.debug;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DebugTabStacktraceTest extends AbstractDebugTabTest {

  @Test
  public void stacktrace() throws Exception {
    Exception cause = new Exception("innerError");
    cause.setStackTrace(new StackTraceElement[] {
        new StackTraceElement("inner.class", "inner.method", "inner/filename", 99) });
    Exception exception = new Exception("error", cause);
    exception.setStackTrace(new StackTraceElement[] {
        new StackTraceElement("some.class", "some.method", "filename", 42),
        cause.getStackTrace()[0] });
    final DebugTabStacktrace tab = new DebugTabStacktrace(exception);
    assertEquals("{\"exceptions\":["
        + "{\"class\":\"java.lang.Exception\",\"message\":\"error\","
        + "\"invocation\":{\"class\":\"some.class\",\"method\":\"some.method\",\"line\":42}},"
        + "{\"class\":\"java.lang.Exception\",\"message\":\"innerError\","
        + "\"invocation\":{\"class\":\"inner.class\",\"method\":\"inner.method\",\"line\":99}}],"
        + "\"stacktrace\":["
        + "{\"class\":\"some.class\",\"method\":\"some.method\",\"line\":42},"
        + "{\"class\":\"inner.class\",\"method\":\"inner.method\",\"line\":99}]}",
        createJson(tab));

    assertEquals("<h2>java.lang.Exception</h2>\n"
        + "<p>innerError</p>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr>\n"
        + "<th class=\"name\">Class</th>\n"
        + "<th class=\"name\">Method</th>\n"
        + "<th class=\"value\">Line number in class</th>\n"
        + "</tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr>\n"
        + "<td class=\"name\">inner.class</td>\n"
        + "<td class=\"name\">inner.method</td>\n"
        + "<td class=\"value\">99</td>\n"
        + "</tr>\n"
        + "</tbody>\n"
        + "</table>\n"
        + "<h2>java.lang.Exception</h2>\n"
        + "<p>error</p>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr>\n"
        + "<th class=\"name\">Class</th>\n"
        + "<th class=\"name\">Method</th>\n"
        + "<th class=\"value\">Line number in class</th>\n"
        + "</tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr>\n"
        + "<td class=\"name\">some.class</td>\n"
        + "<td class=\"name\">some.method</td>\n"
        + "<td class=\"value\">42</td>\n"
        + "</tr>\n"
        + "</tbody>\n"
        + "</table>\n"
        + "<h2>Stacktrace</h2>\n"
        + "<table>\n"
        + "<thead>\n"
        + "<tr>\n"
        + "<th class=\"name\">Class</th>\n"
        + "<th class=\"name\">Method</th>\n"
        + "<th class=\"value\">Line number in class</th>\n"
        + "</tr>\n"
        + "</thead>\n"
        + "<tbody>\n"
        + "<tr>\n"
        + "<td class=\"name\">some.class</td>\n"
        + "<td class=\"name\">some.method</td>\n"
        + "<td class=\"value\">42</td>\n"
        + "</tr>\n"
        + "<tr>\n"
        + "<td class=\"name\">inner.class</td>\n"
        + "<td class=\"name\">inner.method</td>\n"
        + "<td class=\"value\">99</td>\n"
        + "</tr>\n"
        + "</tbody>\n"
        + "</table>\n",
        createHtml(tab));
  }
}
