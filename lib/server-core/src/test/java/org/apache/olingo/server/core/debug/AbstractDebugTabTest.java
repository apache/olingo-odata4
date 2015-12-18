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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public abstract class AbstractDebugTabTest {

  protected String createHtml(final DebugTab tab) throws IOException {
    StringWriter writer = new StringWriter();
    tab.appendHtml(writer);
    writer.flush();
    return writer.toString();
  }

  protected String createJson(final DebugTab tab) throws IOException {
    StringWriter writer = new StringWriter();
    JsonGenerator gen = new JsonFactory().createGenerator(writer);
    tab.appendJson(gen);
    gen.flush();
    gen.close();
    writer.flush();
    return writer.toString();
  }
}
