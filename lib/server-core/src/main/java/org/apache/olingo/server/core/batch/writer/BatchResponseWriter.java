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
package org.apache.olingo.server.core.batch.writer;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.ODataResponsePart;
import org.apache.olingo.server.api.batch.BatchException.MessageKeys;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;

public class BatchResponseWriter {
  private static final int BUFFER_SIZE = 4096;
  private static final String DOUBLE_DASH = "--";
  private static final String COLON = ":";
  private static final String SP = " ";
  private static final String CRLF = "\r\n";

  public void toODataResponse(final List<ODataResponsePart> batchResponse, final ODataResponse response)
      throws IOException, BatchException {
    final String boundary = generateBoundary("batch");

    setStatusCode(response);
    ResponseWriter writer = createBody(batchResponse, boundary);

    response.setContent(writer.toInputStream());
    setHttpHeader(response, writer, boundary);
  }

  private ResponseWriter createBody(final List<ODataResponsePart> batchResponses, final String boundary)
      throws IOException, BatchException {
    final ResponseWriter writer = new ResponseWriter();

    for (final ODataResponsePart part : batchResponses) {
      writer.append(getDashBoundary(boundary));

      if (part.isChangeSet()) {
        appendChangeSet(part, writer);
      } else {
        appendBodyPart(part.getResponses().get(0), writer, false);
      }
    }
    writer.append(getCloseDelimiter(boundary));

    return writer;
  }

  private void appendChangeSet(ODataResponsePart part, ResponseWriter writer) throws IOException, BatchException {
    final String changeSetBoundary = generateBoundary("changeset");

    appendChangeSetHeader(writer, changeSetBoundary);
    writer.append(CRLF);

    for (final ODataResponse response : part.getResponses()) {
      writer.append(getDashBoundary(changeSetBoundary));
      appendBodyPart(response, writer, true);
    }

    writer.append(getCloseDelimiter(changeSetBoundary));
    writer.append(CRLF);
  }

  private void appendBodyPart(ODataResponse response, ResponseWriter writer, boolean isChangeSet) throws IOException,
      BatchException {
    byte[] body = getBody(response);
    
    appendBodyPartHeader(response, writer, isChangeSet);
    writer.append(CRLF);

    appendStatusLine(response, writer);
    appendResponseHeader(response, body.length, writer);
    writer.append(CRLF);

    writer.append(body);
    writer.append(CRLF);
  }

  private byte[] getBody(final ODataResponse response) throws IOException {
    final InputStream content = response.getContent();
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    
    if (content != null) {
      byte[] buffer = new byte[BUFFER_SIZE];
      int n;

      while ((n = content.read(buffer, 0, buffer.length)) != -1) {
        out.write(buffer, 0, n);
      }
      out.flush();
      
      return out.toByteArray();
    } else {
      return new byte[0];
    }
  }

  private void appendChangeSetHeader(ResponseWriter writer, final String changeSetBoundary) throws IOException {
    appendHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED.toString() + "; boundary="
        + changeSetBoundary, writer);
  }

  private void appendHeader(String name, String value, ResponseWriter writer) throws IOException {
    writer.append(name)
        .append(COLON)
        .append(SP)
        .append(value)
        .append(CRLF);
  }

  private void appendStatusLine(ODataResponse response, ResponseWriter writer) throws IOException {
    writer.append("HTTP/1.1")
        .append(SP)
        .append("" + response.getStatusCode())
        .append(SP)
        .append(HttpStatusCode.fromStatusCode(response.getStatusCode()).toString())
        .append(CRLF);
  }

  private void appendResponseHeader(ODataResponse response, int contentLength, ResponseWriter writer)
      throws IOException {
    final Map<String, String> header = response.getHeaders();

    for (final String key : header.keySet()) {
      // Requests do never have content id header
      if (!key.equalsIgnoreCase(BatchParserCommon.HTTP_CONTENT_ID)) {
        appendHeader(key, header.get(key), writer);
      }
    }

    appendHeader(HttpHeader.CONTENT_LENGTH, "" + contentLength, writer);
  }

  private void appendBodyPartHeader(ODataResponse response, ResponseWriter writer, boolean isChangeSet)
      throws BatchException, IOException {
    appendHeader(HttpHeader.CONTENT_TYPE, HttpContentType.APPLICATION_HTTP, writer);
    appendHeader(BatchParserCommon.HTTP_CONTENT_TRANSFER_ENCODING, BatchParserCommon.BINARY_ENCODING, writer);

    if (isChangeSet) {
      if (response.getHeaders().get(BatchParserCommon.HTTP_CONTENT_ID) != null) {
        appendHeader(BatchParserCommon.HTTP_CONTENT_ID, response.getHeaders().get(BatchParserCommon.HTTP_CONTENT_ID),
            writer);
      } else {
        throw new BatchException("Missing content id", MessageKeys.MISSING_CONTENT_ID, "");
      }
    }
  }

  private void setHttpHeader(ODataResponse response, ResponseWriter writer, final String boundary) {
    response.setHeader(HttpHeader.CONTENT_TYPE, HttpContentType.MULTIPART_MIXED.toString() + "; boundary=" + boundary);
    response.setHeader(HttpHeader.CONTENT_LENGTH, "" + writer.length());
  }

  private void setStatusCode(final ODataResponse response) {
    response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
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

  private static class ResponseWriter {
    private CircleStreamBuffer buffer = new CircleStreamBuffer();
    private BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(buffer.getOutputStream()));
    private int length = 0;

    public ResponseWriter append(final String content) throws IOException {
      length += content.length();
      writer.write(content);
      
      return this;
    }

    public ResponseWriter append(final byte[] content) throws IOException {
      length += content.length;
      writer.flush();
      buffer.getOutputStream().write(content, 0, content.length);
      
      return this;
    }

    public int length() {
      return length;
    }

    public InputStream toInputStream() throws IOException {
      writer.flush();
      writer.close();

      return buffer.getInputStream();
    }
  }
}
