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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.core.deserializer.StringUtil;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.apache.olingo.server.core.deserializer.batch.BufferedReaderIncludingLineEndings;
import org.apache.olingo.server.core.serializer.BatchResponseSerializer;
import org.junit.Test;

public class BatchResponseWriterTest {
  private static final String CRLF = "\r\n";

  @Test
  public void testBatchResponse() throws IOException, BatchException {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    response.setContent(StringUtil.toInputStream("Walter Winter" + CRLF));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(BatchParserCommon.HTTP_CONTENT_ID, "1");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer writer = new BatchResponseSerializer();
    ODataResponse batchResponse = new ODataResponse();
    writer.toODataResponse(parts, batchResponse);

    assertEquals(202, batchResponse.getStatusCode());
    assertNotNull(batchResponse.getContent());
    final BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(batchResponse.getContent()));
    final List<String> body = reader.toList();
    reader.close();
    
    int line = 0;
    assertEquals(25, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json" + CRLF, body.get(line++));
    assertEquals("Content-Length: 15" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Walter Winter" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-Id: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
  }

  @Test
  public void testResponse() throws IOException, BatchException {
    List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    response.setContent(StringUtil.toInputStream("Walter Winter"));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse batchResponse = new ODataResponse();
    new BatchResponseSerializer().toODataResponse(parts, batchResponse);

    assertEquals(202, batchResponse.getStatusCode());
    assertNotNull(batchResponse.getContent());
    final BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(batchResponse.getContent()));
    final List<String> body = reader.toList();
    reader.close();
    
    int line = 0;
    assertEquals(10, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json" + CRLF, body.get(line++));
    assertEquals("Content-Length: 13" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Walter Winter" + CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
  }

  @Test
  public void testChangeSetResponse() throws IOException, BatchException {
    List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setHeader(BatchParserCommon.HTTP_CONTENT_ID, "1");
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer writer = new BatchResponseSerializer();
    ODataResponse batchResponse = new ODataResponse();
    writer.toODataResponse(parts, batchResponse);

    assertEquals(202, batchResponse.getStatusCode());
    assertNotNull(batchResponse.getContent());

    final BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(batchResponse.getContent()));
    final List<String> body = reader.toList();
    reader.close();
    
    int line = 0;
    assertEquals(15, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-Id: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
  }
}
