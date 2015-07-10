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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.core.deserializer.batch.BatchLineReader;
import org.junit.Test;

public class BatchResponseSerializerTest {
  private static final String CRLF = "\r\n";
  private static final String BOUNDARY = "batch_" + UUID.randomUUID().toString();

  private static final Charset CS_ISO_8859_1 = Charset.forName("iso-8859-1");

  @Test
  public void testBatchResponse() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.toContentTypeString());
    response.setContent(IOUtils.toInputStream("Walter Winter" + CRLF));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
        new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(24, body.size());
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
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testBatchResponseUmlautsUtf8() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE,
            ContentType.APPLICATION_JSON.toContentTypeString() + "; charset=UTF-8");
    response.setContent(IOUtils.toInputStream("Wälter Winter" + CRLF));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
            new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(24, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json; charset=UTF-8" + CRLF, body.get(line++));
    assertEquals("Content-Length: 16" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Wälter Winter" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testBatchResponseUmlautsUtf8BodyIsoHeader() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE,
        ContentType.APPLICATION_JSON.toContentTypeString() + "; charset=UTF-8");
    response.setContent(IOUtils.toInputStream("Wälter Winter" + CRLF));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");

    byte[] umlauts = "äüö".getBytes(CS_ISO_8859_1);
    changeSetResponse.setHeader("Custom-Header", new String(umlauts, CS_ISO_8859_1));
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
        new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(25, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json; charset=UTF-8" + CRLF, body.get(line++));
    assertEquals("Content-Length: 16" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Wälter Winter" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Custom-Header: äüö" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testBatchResponseUmlautsUtf8BodyAndHeader() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE,
        ContentType.APPLICATION_JSON.toContentTypeString() + "; charset=UTF-8");
    response.setContent(IOUtils.toInputStream("Wälter Winter" + CRLF));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");

//    byte[] umlauts = "äüö".getBytes(CS_UTF_8);
//    changeSetResponse.setHeader("Custom-Header", new String(umlauts, CS_UTF_8));
    changeSetResponse.setHeader("Custom-Header", "äüö");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
        new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    assertEquals(25, body.size());
    // TODO: check: with latest change in BatchResponseSerializer is not possible
    // to set header values with UTF-8 (only iso-8859-1)
//    assertEquals("Custom-Header: Ã¤Ã¼Ã¶" + CRLF, body.get(19));
    assertEquals("Custom-Header: äüö" + CRLF, body.get(19));
  }

  @Test
  public void testBatchResponseUmlautsIso() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE,
            ContentType.APPLICATION_JSON.toContentTypeString() + "; charset=iso-8859-1");
    byte[] payload = ("Wälter Winter" + CRLF).getBytes("iso-8859-1");
    response.setContent(new ByteArrayInputStream(payload));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
            new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(24, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json; charset=iso-8859-1" + CRLF, body.get(line++));
    assertEquals("Content-Length: 15" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Wälter Winter" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testBatchResponseWithEndingCRLF() throws Exception {
    final List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    response.setContent(IOUtils.toInputStream("Walter Winter"));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    ODataResponse changeSetResponse = new ODataResponse();
    changeSetResponse.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());
    changeSetResponse.setHeader(HttpHeader.CONTENT_ID, "1");
    responses = new ArrayList<ODataResponse>(1);
    responses.add(changeSetResponse);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);
    assertNotNull(content);
    final BatchLineReader reader =
        new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(23, body.size());
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
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testResponse() throws Exception {
    List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    response.setContent(IOUtils.toInputStream("Walter Winter"));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    final BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);

    assertNotNull(content);
    final BatchLineReader reader =
        new BatchLineReader(content);
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
    assertTrue(body.get(line).contains("--batch_"));
  }

  @Test
  public void testResponseVeryLargeHeader() throws Exception {
    List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, "application/json");
    final String chValue = generateTestData(20000);
    response.setHeader("Custom-Header", chValue);
    response.setContent(IOUtils.toInputStream("Walter Winter"));

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, false));

    final BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);

    assertNotNull(content);
    final BatchLineReader reader =
            new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(11, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, body.get(line++));
    assertEquals("Custom-Header: " + chValue + CRLF, body.get(line++));
    assertEquals("Content-Type: application/json" + CRLF, body.get(line++));
    assertEquals("Content-Length: 13" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("Walter Winter" + CRLF, body.get(line++));
    assertTrue(body.get(line).contains("--batch_"));
  }

  private String generateTestData(int amount) {
    StringBuilder sb = new StringBuilder();
    Random r = new Random();
    for (int j = 0; j < amount; j++) {
      sb.append((char)(65 + r.nextInt(25)));
    }
    return sb.toString();
  }

  @Test
  public void testChangeSetResponse() throws Exception {
    List<ODataResponsePart> parts = new ArrayList<ODataResponsePart>();
    ODataResponse response = new ODataResponse();
    response.setHeader(HttpHeader.CONTENT_ID, "1");
    response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());

    List<ODataResponse> responses = new ArrayList<ODataResponse>(1);
    responses.add(response);
    parts.add(new ODataResponsePart(responses, true));

    BatchResponseSerializer serializer = new BatchResponseSerializer();
    final InputStream content = serializer.serialize(parts, BOUNDARY);

    assertNotNull(content);

    final BatchLineReader reader =
        new BatchLineReader(content);
    final List<String> body = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(14, body.size());
    assertTrue(body.get(line++).contains("--batch_"));
    assertTrue(body.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, body.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, body.get(line++));
    assertEquals("Content-ID: 1" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals("HTTP/1.1 204 No Content" + CRLF, body.get(line++));
    assertEquals("Content-Length: 0" + CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertEquals(CRLF, body.get(line++));
    assertTrue(body.get(line++).contains("--changeset_"));
    assertTrue(body.get(line).contains("--batch_"));
  }
}
