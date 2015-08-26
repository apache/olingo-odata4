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

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Server debug information.
 */
public class DebugTabServer implements DebugTab {

  private final Map<String, String> serverEnvironmentVariables;

  public DebugTabServer(Map<String, String> serverEnvironmentVariables) {
    this.serverEnvironmentVariables = serverEnvironmentVariables;
  }

  @Override
  public String getName() {
    return "Environment";
  }

  @Override
  public void appendJson(JsonGenerator gen) throws IOException {
    if (serverEnvironmentVariables != null && !serverEnvironmentVariables.isEmpty()) {
      DebugResponseHelperImpl.appendJsonTable(gen, serverEnvironmentVariables);
    } else {
      gen.writeNull();
    }
  }

  @Override
  public void appendHtml(Writer writer) throws IOException {
    final Package pack = DebugResponseHelperImpl.class.getPackage();
    writer.append("<h2>Library Version</h2>\n")
        .append("<p>").append(pack.getImplementationTitle())
        .append(" Version ").append(pack.getImplementationVersion()).append("</p>\n")
        .append("<h2>Server Environment</h2>\n");
    if (serverEnvironmentVariables != null && !serverEnvironmentVariables.isEmpty()) {
      DebugResponseHelperImpl.appendHtmlTable(writer, serverEnvironmentVariables);
    }
  }
}
