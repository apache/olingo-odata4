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
package org.apache.olingo.server.core.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.serializer.SerializerException;

public class AsyncResponseSerializer {
  private static final int BUFFER_SIZE = 8192;
  private static final String COLON = ":";
  private static final String SP = " ";
  private static final String CRLF = "\r\n";
  private static final String HEADER_CHARSET_NAME = "ISO-8859-1";
  private static final String HTTP_VERSION = "HTTP/1.1";

  public InputStream serialize(final ODataResponse response) throws SerializerException {
    try {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      appendStatusLine(response, buffer);
      appendResponseHeader(response, buffer);
      append(CRLF, buffer);
      appendBody(response, buffer);

      buffer.flush();
      return new ByteArrayInputStream(buffer.toByteArray(), 0, buffer.size());
    } catch (IOException e) {
      throw new SerializerException("Exception occurred during serialization of asynchronous response.",
          e, SerializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private void appendResponseHeader(final ODataResponse response,
      final ByteArrayOutputStream buffer) throws IOException {
    final Map<String, List<String>> header = response.getAllHeaders();

    for (final Map.Entry<String, List<String>> entry : header.entrySet()) {
      appendHeader(entry.getKey(), entry.getValue(), buffer);
    }
  }

  private void appendHeader(final String name, final List<String> values, final ByteArrayOutputStream buffer)
      throws IOException {
    for (String value : values) {
      append(name + COLON + SP + value + CRLF, buffer);
    }
  }

  private void appendStatusLine(final ODataResponse response, final ByteArrayOutputStream buffer)
      throws IOException {
    HttpStatusCode status = HttpStatusCode.fromStatusCode(response.getStatusCode());
    append(HTTP_VERSION + SP + response.getStatusCode() + SP + status + CRLF, buffer);
  }

  private void appendBody(final ODataResponse response, final ByteArrayOutputStream buffer) throws IOException {
    InputStream input = response.getContent();
    if (input != null) {
      ByteBuffer inBuffer = ByteBuffer.allocate(BUFFER_SIZE);
      ReadableByteChannel ic = Channels.newChannel(input);
      WritableByteChannel oc = Channels.newChannel(buffer);
      while (ic.read(inBuffer) > 0) {
        inBuffer.flip();
        oc.write(inBuffer);
        inBuffer.rewind();
      }
    }
  }

  private void append(final String value, final ByteArrayOutputStream buffer) throws IOException {
    try {
      buffer.write(value.getBytes(HEADER_CHARSET_NAME));
    } catch (UnsupportedEncodingException e) {
      throw new IOException("Default header charset with name '" + HEADER_CHARSET_NAME +
          "' is not available.", e);
    }
  }
}