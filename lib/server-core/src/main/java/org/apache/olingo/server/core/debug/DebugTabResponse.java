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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Response debug information.
 */
public class DebugTabResponse implements DebugTab {

  private final ODataResponse response;
  private final HttpStatusCode status;
  private final Map<String, List<String>> headers;

  public DebugTabResponse(final ODataResponse applicationResponse) {
    response = applicationResponse;
    if (response != null) {
      status = HttpStatusCode.fromStatusCode(response.getStatusCode());
      headers = response.getAllHeaders();
    } else {
      status = HttpStatusCode.INTERNAL_SERVER_ERROR;
      headers = Collections.emptyMap();
    }
  }

  @Override
  public String getName() {
    return "Response";
  }

  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    gen.writeStartObject();

    if (status != null) {
      gen.writeFieldName("status");
      gen.writeStartObject();
      gen.writeStringField("code", Integer.toString(status.getStatusCode()));
      gen.writeStringField("info", status.getInfo());
      gen.writeEndObject();
    }

    if (headers != null && !headers.isEmpty()) {
      gen.writeFieldName("headers");
      DebugResponseHelperImpl.appendJsonTable(gen, map(headers));
    }

    gen.writeFieldName("body");
    if (response != null && response.getContent() != null) {
      new DebugTabBody(response).appendJson(gen);
    } else {
      gen.writeNull();
    }

    gen.writeEndObject();
  }

  private Map<String, String> map(final Map<String, List<String>> headers) {
    Map<String, String> result = new HashMap<String, String>();
    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
      if (entry.getValue().size() == 1) {
        result.put(entry.getKey(), entry.getValue().get(0));
      } else {
        result.put(entry.getKey(), entry.getValue().toString());
      }
    }
    return result;
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    writer.append("<h2>Status Code</h2>\n")
    .append("<p>").append(Integer.toString(status.getStatusCode())).append(' ')
    .append(status.getInfo()).append("</p>\n")
    .append("<h2>Response Headers</h2>\n");
    DebugResponseHelperImpl.appendHtmlTable(writer, map(headers));
    writer.append("<h2>Response Body</h2>\n");
    if (response != null && response.getContent() != null) {
      new DebugTabBody(response).appendHtml(writer);
    } else {
      writer.append("<p>ODataLibrary: no response body</p>\n");
    }
  }
}
