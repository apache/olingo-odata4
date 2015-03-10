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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.exception.BatchSerializerException;
import org.apache.olingo.server.api.batch.exception.BatchSerializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;

public class BatchResponseSerializer {
  private static final int BUFFER_SIZE = 4096;
  private static final String DOUBLE_DASH = "--";
  private static final String COLON = ":";
  private static final String SP = " ";
  private static final String CRLF = "\r\n";

  public InputStream serialize(final List<ODataResponsePart> responses, final String boundary)
      throws BatchSerializerException {
    StringBuilder builder = createBody(responses, boundary);

    return new ByteArrayInputStream(builder.toString().getBytes());
  }

  private StringBuilder createBody(final List<ODataResponsePart> batchResponses, final String boundary)
      throws BatchSerializerException {
    final StringBuilder builder = new StringBuilder();

    for (final ODataResponsePart part : batchResponses) {
      builder.append(getDashBoundary(boundary));

      if (part.isChangeSet()) {
        appendChangeSet(part, builder);
      } else {
        appendBodyPart(part.getResponses().get(0), builder, false);
      }
    }
    builder.append(getCloseDelimiter(boundary));

    return builder;
  }

  private void appendChangeSet(ODataResponsePart part, StringBuilder builder) throws BatchSerializerException {
    final String changeSetBoundary = generateBoundary("changeset");

    appendChangeSetHeader(builder, changeSetBoundary);
    builder.append(CRLF);

    for (final ODataResponse response : part.getResponses()) {
      builder.append(getDashBoundary(changeSetBoundary));
      appendBodyPart(response, builder, true);
    }

    builder.append(getCloseDelimiter(changeSetBoundary));
  }

  private void appendBodyPart(ODataResponse response, StringBuilder builder, boolean isChangeSet)
      throws BatchSerializerException {
    byte[] body = getBody(response);

    appendBodyPartHeader(response, builder, isChangeSet);
    builder.append(CRLF);

    appendStatusLine(response, builder);
    appendResponseHeader(response, body.length, builder);
    builder.append(CRLF);

    builder.append(new String(body));
    builder.append(CRLF);
  }

  private byte[] getBody(final ODataResponse response) {
    final InputStream content = response.getContent();
    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    if (content != null) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int n;

      try {
        while ((n = content.read(buffer, 0, buffer.length)) != -1) {
          out.write(buffer, 0, n);
        }
        out.flush();
      } catch (IOException e) {
        throw new ODataRuntimeException(e);
      }

      return out.toByteArray();
    } else {
      return new byte[0];
    }
  }

  private void appendChangeSetHeader(StringBuilder builder, final String changeSetBoundary) {
    appendHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED + "; boundary="
        + changeSetBoundary, builder);
  }

  private void appendHeader(String name, String value, StringBuilder builder) {
    builder.append(name)
        .append(COLON)
        .append(SP)
        .append(value)
        .append(CRLF);
  }

  private void appendStatusLine(ODataResponse response, StringBuilder builder) {
    builder.append("HTTP/1.1")
        .append(SP)
        .append(response.getStatusCode())
        .append(SP)
        .append(HttpStatusCode.fromStatusCode(response.getStatusCode()).toString())
        .append(CRLF);
  }

  private void appendResponseHeader(ODataResponse response, int contentLength, StringBuilder builder) {
    final Map<String, String> header = response.getHeaders();

    for (final String key : header.keySet()) {
      // Requests do never has a content id header
      if (!key.equalsIgnoreCase(BatchParserCommon.HTTP_CONTENT_ID)) {
        appendHeader(key, header.get(key), builder);
      }
    }

    appendHeader(HttpHeader.CONTENT_LENGTH, "" + contentLength, builder);
  }

  private void appendBodyPartHeader(ODataResponse response, StringBuilder builder, boolean isChangeSet)
      throws BatchSerializerException {
    appendHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_HTTP, builder);
    appendHeader(BatchParserCommon.HTTP_CONTENT_TRANSFER_ENCODING, BatchParserCommon.BINARY_ENCODING, builder);

    if (isChangeSet) {
      if (response.getHeaders().get(BatchParserCommon.HTTP_CONTENT_ID) != null) {
        appendHeader(BatchParserCommon.HTTP_CONTENT_ID, response.getHeaders().get(BatchParserCommon.HTTP_CONTENT_ID),
            builder);
      } else {
        throw new BatchSerializerException("Missing content id", MessageKeys.MISSING_CONTENT_ID);
      }
    }
  }

  private String getDashBoundary(String boundary) {
    return DOUBLE_DASH + boundary + CRLF;
  }

  private String getCloseDelimiter(final String boundary) {
    return DOUBLE_DASH + boundary + DOUBLE_DASH + CRLF;
  }

  private String generateBoundary(final String value) {
    return value + "_" + UUID.randomUUID().toString();
  }
}
