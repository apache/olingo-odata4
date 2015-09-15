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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.codec.binary.Base64;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataResponse;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Response body debug information.
 */
public class DebugTabBody implements DebugTab {

  private enum ResponseContent {
    JSON, XML, TEXT, IMAGE
  }

  private final ODataResponse response;
  private final ResponseContent responseContent;

  public DebugTabBody(final ODataResponse response) {
    this.response = response;
    if (response != null) {
      final String contentTypeString = response.getHeader(HttpHeader.CONTENT_TYPE);
      if (contentTypeString != null) {
        if (contentTypeString.startsWith("application/json")) {
          responseContent = ResponseContent.JSON;
        } else if (contentTypeString.startsWith("image/")) {
          responseContent = ResponseContent.IMAGE;
        } else if (contentTypeString.contains("xml")) {
          responseContent = ResponseContent.XML;
        } else {
          responseContent = ResponseContent.TEXT;
        }
      } else {
        responseContent = ResponseContent.TEXT;
      }
    } else {
      responseContent = ResponseContent.TEXT;
    }
  }

  @Override
  public String getName() {
    return "Body";
  }

//
  @Override
  public void appendJson(final JsonGenerator gen) throws IOException {
    if (response == null || response.getContent() == null) {
      gen.writeNull();
    } else {
      gen.writeString(getContentString());
    }
  }

  private String getContentString() {
    try {
      String contentString;
      switch (responseContent) {
      case IMAGE:
        contentString = Base64.encodeBase64String(streamToBytes(response.getContent()));
        break;
      case JSON:
      case XML:
      case TEXT:
      default:
        contentString = new String(streamToBytes(response.getContent()), "UTF-8");
        break;
      }
      return contentString;
    } catch (IOException e) {
      return "Could not parse Body for Debug Output";
    }
  }

  @Override
  public void appendHtml(final Writer writer) throws IOException {

    final String body =
        response == null || response.getContent() == null ? "ODataLibrary: No body." : getContentString();
    switch (responseContent) {
    case XML:
      writer.append("<pre class=\"code").append(" xml").append("\">\n");
      writer.append(DebugResponseHelperImpl.escapeHtml(body));
      writer.append("\n</pre>\n");
      break;
    case JSON:
      writer.append("<pre class=\"code").append(" json").append("\">\n");
      writer.append(DebugResponseHelperImpl.escapeHtml(body));
      writer.append("\n</pre>\n");
      break;
    case IMAGE:
      writer.append("<img src=\"data:").append(response.getHeader(HttpHeader.CONTENT_TYPE)).append(";base64,")
          .append(body)
          .append("\" />\n");
      break;
    case TEXT:
    default:
      writer.append("<pre class=\"code").append("\">\n");
      writer.append(DebugResponseHelperImpl.escapeHtml(body));
      writer.append("\n</pre>\n");
      break;
    }
  }

  private byte[] streamToBytes(InputStream input) {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    if (input != null) {
      try {
        ByteBuffer inBuffer = ByteBuffer.allocate(8192);
        ReadableByteChannel ic = Channels.newChannel(input);
        WritableByteChannel oc = Channels.newChannel(buffer);
        while (ic.read(inBuffer) > 0) {
          inBuffer.flip();
          oc.write(inBuffer);
          inBuffer.rewind();
        }
        return buffer.toByteArray();
      } catch (IOException e) {
        throw new ODataRuntimeException("Error on reading request content");
      }
    }
    return null;
  }
}
