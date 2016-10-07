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
import java.io.StringWriter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractDebugTabTest {

  protected String createHtml(DebugTab tab) throws IOException {
    return create(tab, true);
  }

  protected String createJson(DebugTab tab) throws IOException {
    return create(tab, false);
  }

  private String create(DebugTab tab, final boolean html) throws IOException {
    StringWriter writer = new StringWriter();
    if (html) {
      tab.appendHtml(writer);
    } else {
      // Create JSON generator (the object mapper is necessary to write expression trees).
      JsonGenerator json = new ObjectMapper().getFactory().createGenerator(writer);
      tab.appendJson(json);
      json.flush();
      json.close();
    }
    writer.flush();
    return writer.toString();
  }
}
