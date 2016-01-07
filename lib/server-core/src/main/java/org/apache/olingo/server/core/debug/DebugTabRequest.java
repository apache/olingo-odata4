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
import java.util.List;
import java.util.Map;

import org.apache.olingo.server.api.ODataRequest;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Request debug information.
 */
public class DebugTabRequest implements DebugTab {

  private static final String UNKOWN_MSG = "unkown";
  private final String method;
  private final String uri;
  private final String protocol;
  private final Map<String, List<String>> headers;

  public DebugTabRequest(final ODataRequest request) {
    if (request != null) {
      method = request.getMethod() == null ? UNKOWN_MSG : request.getMethod().toString();
      uri = request.getRawRequestUri() == null ? UNKOWN_MSG : request.getRawRequestUri();
      protocol = request.getProtocol() == null ? UNKOWN_MSG : request.getProtocol();
      headers = request.getAllHeaders();
    } else {
      method = UNKOWN_MSG;
      uri = UNKOWN_MSG;
      protocol = UNKOWN_MSG;
      headers = Collections.emptyMap();
    }
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {
    writer.append("<h2>Request Method</h2>\n")
    .append("<p>").append(method).append("</p>\n")
    .append("<h2>Request URI</h2>\n")
    .append("<p>").append(DebugResponseHelperImpl.escapeHtml(uri)).append("</p>\n")
    .append("<h2>Request Protocol</h2>\n")
    .append("<p>").append(DebugResponseHelperImpl.escapeHtml(protocol)).append("</p>\n");
    writer.append("<h2>Request Headers</h2>\n");

    writer.append("<table>\n<thead>\n")
    .append("<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n")
    .append("</thead>\n<tbody>\n");
    for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
      List<String> headersList = entry.getValue();
      if (headersList != null && !headersList.isEmpty()) {
        for (String headerValue : headersList) {
          writer.append("<tr><td class=\"name\">").append(entry.getKey()).append("</td>")
          .append("<td class=\"value\">")
          .append(DebugResponseHelperImpl.escapeHtml(headerValue))
          .append("</td></tr>\n");
        }
      }
    }
    writer.append("</tbody>\n</table>\n");
  }

  @Override
  public String getName() {
    return "Request";
  }

  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    gen.writeStartObject();
    gen.writeStringField("method", method);

    gen.writeStringField("uri", uri);

    gen.writeStringField("protocol", protocol);

    if (!headers.isEmpty()) {
      gen.writeFieldName("headers");

      gen.writeStartObject();

      for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
        List<String> headersList = entry.getValue();
        if (headersList != null && !headersList.isEmpty()) {
          if (headersList.size() == 1) {
            gen.writeStringField(entry.getKey(), headersList.get(0));
          } else {
            gen.writeFieldName(entry.getKey());
            gen.writeStartArray();
            for (String headerValue : headersList) {
              if (headerValue != null) {
                gen.writeString(headerValue);
              } else {
                gen.writeNull();
              }
            }
            gen.writeEndArray();
          }
        } else {
          gen.writeNullField(entry.getKey());
        }
      }

      gen.writeEndObject();
    }

    gen.writeEndObject();
  }
}
