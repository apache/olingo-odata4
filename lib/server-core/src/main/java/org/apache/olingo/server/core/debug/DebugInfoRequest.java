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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.server.api.ODataRequest;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Request debug information.
 */
public class DebugInfoRequest implements DebugInfo {

  private final String method;
  private final String uri;
  private final String protocol;
  private final Map<String, String> headers;

  public DebugInfoRequest(ODataRequest request) {
    method = request.getMethod() == null ? "unkown" : request.getMethod().toString();
    uri = request.getRawRequestUri() == null ? "unkown" : request.getRawRequestUri();
    protocol = request.getProtocol() == null ? "unkown" : request.getProtocol();
    // TODO: Should we really wrap the headers here or keep the original structure?
    headers = wrapHeaders(request.getAllHeaders());
  }

  private Map<String, String> wrapHeaders(Map<String, List<String>> allHeaders) {
    Map<String, String> localHeaders = new HashMap<String, String>();
    for (Map.Entry<String, List<String>> entry : allHeaders.entrySet()) {
      String value = null;
      if (entry.getValue() != null) {
        value = "";
        boolean first = true;
        for (String valuePart : entry.getValue()) {
          if (!first) {
            value = value + ", ";
          }
          value = value + valuePart;
        }
      }
    }
    return localHeaders;
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
//    writer.append("<h2>Request Method</h2>\n")
//        .append("<p>").append(method).append("</p>\n")
//        .append("<h2>Request URI</h2>\n")
//        .append("<p>").append(DebugResponseHelperImpl.escapeHtml(uri.toString())).append("</p>\n")
//        .append("<h2>Request Protocol</h2>\n")
//        .append("<p>").append(protocol).append("</p>\n");
//    writer.append("<h2>Request Headers</h2>\n")
//        .append("<table>\n<thead>\n")
//        .append("<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n")
//        .append("</thead>\n<tbody>\n");
//    for (final String name : headers.keySet()) {
//      for (final String value : headers.get(name)) {
//        if (value != null) {
//          writer.append("<tr><td class=\"name\">").append(name).append("</td>")
//              .append("<td class=\"value\">").append(DebugResponseHelperImpl.escapeHtml(value))
//              .append("</td></tr>\n");
//        }
//      }
//    }
//    writer.append("</tbody>\n</table>\n");
  }

  @Override
  public String getName() {
    return "Request";
  }

  @Override
  public void appendJson(JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("method", method);

    gen.writeStringField("uri", uri);

    gen.writeStringField("protocol", protocol);

    if (!headers.isEmpty()) {
      gen.writeFieldName("headers");
      DebugResponseHelperImpl.appendJsonTable(gen, headers);
    }

    gen.writeEndObject();
  }
}
