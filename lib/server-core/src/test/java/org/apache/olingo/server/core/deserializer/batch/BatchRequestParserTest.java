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
package org.apache.olingo.server.core.deserializer.batch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.junit.Assert;
import org.junit.Test;

public class BatchRequestParserTest {

  private static final String SERVICE_ROOT = "http://localhost/odata";
  private static final String PROPERTY_URI = "ESAllPrim(32767)/PropertyString";
  private static final String HTTP_VERSION = " HTTP/1.1";
  private static final String CRLF = "\r\n";
  private static final String BOUNDARY = "batch_8194-cf13-1f56";
  private static final String CHANGESET_BOUNDARY = "changeset_f980-1cb6-94dd";
  private static final String MULTIPART_MIXED = "multipart/mixed";
  private static final String APPLICATION_HTTP = "application/http";
  private static final String APPLICATION_JSON = "application/json";
  private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  private static final String MIME_HEADERS = HttpHeader.CONTENT_TYPE + ": " + APPLICATION_HTTP + CRLF
      + HttpHeader.ODATA_VERSION + ": 4.0" + CRLF;
  private static final String ACCEPT_HEADER = HttpHeader.ACCEPT + ": "
      + APPLICATION_JSON + ";q=0.9, application/xml;q=0.8, application/atom+xml;q=0.8, */*;q=0.1" + CRLF;
  private static final String GET_REQUEST = MIME_HEADERS
      + CRLF
      + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
      + CRLF
      + CRLF;

  @Test
  public void basic() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + "?$format=json" + HTTP_VERSION + CRLF
        + HttpHeader.ACCEPT_LANGUAGE + ":en-US,en;q=0.7,en-GB;q=0.9" + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY +CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": changeRequest1" + CRLF
        + CRLF
        + HttpMethod.PUT + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 100000" + CRLF
        + ACCEPT_HEADER
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"value\":\"€ MODIFIED\"}" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + "?$format=json" + HTTP_VERSION + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertFalse(batchRequestParts.isEmpty());

    for (final BatchRequestPart object : batchRequestParts) {
      Assert.assertEquals(1, object.getRequests().size());
      final ODataRequest request = object.getRequests().get(0);
      Assert.assertEquals(SERVICE_ROOT, request.getRawBaseUri());
      Assert.assertEquals("/" + PROPERTY_URI, request.getRawODataPath());

      if (!object.isChangeSet()) {
        Assert.assertEquals(HttpMethod.GET, request.getMethod());

        if (request.getHeaders(HttpHeader.ACCEPT_LANGUAGE) != null) {
          Assert.assertEquals(3, request.getHeaders(HttpHeader.ACCEPT_LANGUAGE).size());
        }

        Assert.assertEquals(SERVICE_ROOT + "/" + PROPERTY_URI + "?$format=json", request.getRawRequestUri());
        Assert.assertEquals("$format=json", request.getRawQueryPath());

      } else {
        Assert.assertEquals(HttpMethod.PUT, request.getMethod());
        Assert.assertEquals("100000", request.getHeader(HttpHeader.CONTENT_LENGTH));
        Assert.assertEquals(APPLICATION_JSON, request.getHeader(HttpHeader.CONTENT_TYPE));

        final List<String> acceptHeader = request.getHeaders(HttpHeader.ACCEPT);
        Assert.assertEquals(4, request.getHeaders(HttpHeader.ACCEPT).size());
        Assert.assertEquals("application/atom+xml;q=0.8", acceptHeader.get(2));
        Assert.assertEquals("*/*;q=0.1", acceptHeader.get(3));

        Assert.assertEquals(SERVICE_ROOT + "/" + PROPERTY_URI, request.getRawRequestUri());
        Assert.assertEquals("", request.getRawQueryPath()); // No query parameter

        Assert.assertEquals("{\"value\":\"€ MODIFIED\"}" + CRLF, IOUtils.toString(request.getBody()));
      }
    }
  }

  @Test
  public void imageInContent() throws Exception {
    final String content = IOUtils.toString(readFile("/batchWithContent.batch"));
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST
        + "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 100000" + CRLF
        + HttpHeader.CONTENT_TYPE + ": image/jpeg" + CRLF
        + "Content-Transfer-Encoding: base64" + CRLF
        + CRLF
        + content
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> BatchRequestParts = parse(batch);

    for (final BatchRequestPart part : BatchRequestParts) {
      Assert.assertEquals(1, part.getRequests().size());
      final ODataRequest request = part.getRequests().get(0);
      if (!part.isChangeSet()) {
        Assert.assertEquals(HttpMethod.GET, request.getMethod());
        Assert.assertEquals(SERVICE_ROOT + "/" + PROPERTY_URI, request.getRawRequestUri());
        Assert.assertEquals(SERVICE_ROOT, request.getRawBaseUri());
        Assert.assertEquals("/" + PROPERTY_URI, request.getRawODataPath());
      } else {
        Assert.assertEquals(HttpMethod.POST, request.getMethod());
        Assert.assertEquals("100000", request.getHeader(HttpHeader.CONTENT_LENGTH));
        Assert.assertEquals("1", request.getHeader(HttpHeader.CONTENT_ID));
        Assert.assertEquals("image/jpeg", request.getHeader(HttpHeader.CONTENT_TYPE));
        Assert.assertEquals(content, IOUtils.toString(request.getBody()));
      }
    }
  }

  @Test
  public void binaryContent() throws Exception {
    // binary content, not a valid UTF-8 representation of a string
    byte[] content = new byte[Byte.MAX_VALUE - Byte.MIN_VALUE + 1];
    for (int i = Byte.MIN_VALUE; i <= Byte.MAX_VALUE; i++) {
      content[i - Byte.MIN_VALUE] = (byte) i;
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream(500);
    out.write(("--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_OCTET_STREAM + CRLF
        + HttpHeader.CONTENT_LENGTH + ": " + (Byte.MAX_VALUE - Byte.MIN_VALUE + 1) + CRLF
        + CRLF).getBytes());
    out.write(content);
    out.write((CRLF
        + "--" + BOUNDARY + "--").getBytes());
    final List<BatchRequestPart> parts = parse(new ByteArrayInputStream(out.toByteArray()), true);
    Assert.assertEquals(1, parts.size());
    Assert.assertEquals(1, parts.get(0).getRequests().size());
    InputStream body = parts.get(0).getRequests().get(0).getBody();
    Assert.assertNotNull(body);
    Assert.assertArrayEquals(content, IOUtils.toByteArray(body));
  }

  @Test
  public void postWithoutBody() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": changeRequest1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 100" + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_OCTET_STREAM + CRLF
        + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertEquals(1, batchRequestParts.size());
    Assert.assertTrue(batchRequestParts.get(0).isChangeSet());
    Assert.assertEquals(1, batchRequestParts.get(0).getRequests().size());
    final ODataRequest request = batchRequestParts.get(0).getRequests().get(0);
    Assert.assertEquals(HttpMethod.POST, request.getMethod());
    Assert.assertEquals("100", request.getHeader(HttpHeader.CONTENT_LENGTH));
    Assert.assertEquals(APPLICATION_OCTET_STREAM, request.getHeader(HttpHeader.CONTENT_TYPE));
    Assert.assertNotNull(request.getBody());
    Assert.assertEquals(-1, request.getBody().read());
  }

  @Test
  public void boundaryParameterWithQuotes() throws Exception {
    final String boundary = "batch_1.2+34:2j)0?";
    final String batch = "--" + boundary + CRLF
        + GET_REQUEST
        + "--" + boundary + "--";
    final List<BatchRequestPart> batchRequestParts = new BatchParser().parseBatchRequest(
        IOUtils.toInputStream(batch),
        boundary,
        BatchOptions.with().isStrict(true).rawBaseUri(SERVICE_ROOT).build());

    Assert.assertNotNull(batchRequestParts);
    Assert.assertFalse(batchRequestParts.isEmpty());
  }

  @Test
  public void wrongBoundaryString() throws Exception {
    final String batch = "--batch_8194-cf13-1f5" + CRLF
        + GET_REQUEST
        + "--" + BOUNDARY + "--";

    final List<BatchRequestPart> parts = parse(batch);
    Assert.assertEquals(0, parts.size());
  }

  @Test
  public void missingHttpVersion() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim?$format=json" + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void missingHttpVersion2() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim?$format=json " + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void missingHttpVersion3() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim?$format=json SMTP:3.1" + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void boundaryWithoutHyphen() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST
        + BOUNDARY + CRLF
        + GET_REQUEST
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void noBoundaryString() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST
        // + no boundary string
        + GET_REQUEST
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void batchBoundaryEqualsChangeSetBoundary() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + BOUNDARY + CRLF
        + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PUT + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"value\":\"MODIFIED\"}" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--"
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void noContentType() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.ODATA_VERSION + ": 4.0" + CRLF
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void mimeHeaderContentType() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": text/plain" + CRLF
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.UNEXPECTED_CONTENT_TYPE);
  }

  @Test
  public void mimeHeaderEncoding() throws Exception {
    String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + "Content-Transfer-Encoding: 8bit" + CRLF
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TRANSFER_ENCODING);
  }

  @Test
  public void getRequestMissingCRLF() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        // + CRLF // Belongs to the GET request
        + CRLF // Belongs to the boundary
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void methodsForIndividualRequests() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{ \"PropertyString\": \"Foo\" }"
        + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.DELETE + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{ \"PropertyString\": \"Foo\" }" + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.PUT + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{ \"PropertyString\": \"Foo\" }" + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    final List<BatchRequestPart> requests = parse(batch);

    Assert.assertEquals(HttpMethod.POST, requests.get(0).getRequests().get(0).getMethod());
    Assert.assertEquals("/ESAllPrim", requests.get(0).getRequests().get(0).getRawODataPath());
    Assert.assertEquals("{ \"PropertyString\": \"Foo\" }",
        IOUtils.toString(requests.get(0).getRequests().get(0).getBody()));

    Assert.assertEquals(HttpMethod.DELETE, requests.get(1).getRequests().get(0).getMethod());
    Assert.assertEquals("/ESAllPrim(32767)", requests.get(1).getRequests().get(0).getRawODataPath());

    Assert.assertEquals(HttpMethod.PATCH, requests.get(2).getRequests().get(0).getMethod());
    Assert.assertEquals("/ESAllPrim(32767)", requests.get(2).getRequests().get(0).getRawODataPath());
    Assert.assertEquals("{ \"PropertyString\": \"Foo\" }",
        IOUtils.toString(requests.get(2).getRequests().get(0).getBody()));

    Assert.assertEquals(HttpMethod.PUT, requests.get(3).getRequests().get(0).getMethod());
    Assert.assertEquals("/ESAllPrim(32767)", requests.get(3).getRequests().get(0).getRawODataPath());
    Assert.assertEquals("{ \"PropertyString\": \"Foo\" }",
        IOUtils.toString(requests.get(3).getRequests().get(0).getBody()));

    Assert.assertEquals(HttpMethod.GET, requests.get(4).getRequests().get(0).getMethod());
    Assert.assertEquals("/ESAllPrim(32767)", requests.get(4).getRequests().get(0).getRawODataPath());
  }

  @Test
  public void noBoundaryFound() throws Exception {
    final String batch = BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + CRLF;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void emptyRequest() throws Exception {
    final String batch = "--" + BOUNDARY + "--";

    Assert.assertEquals(0, parse(batch).size());
  }

  @Test
  public void badRequest() throws Exception {
    final String batch = "This is a bad request. There is no syntax and also no semantic";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void noMethod() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + /* HttpMethod.GET + " " + */ PROPERTY_URI + HTTP_VERSION + CRLF
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void invalidMethodForChangeset() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.GET + " " + PROPERTY_URI + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--"
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CHANGESET_METHOD);
  }

  @Test
  public void invalidChangeSetBoundary() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY.substring(0, CHANGESET_BOUNDARY.length() - 1) + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    final List<BatchRequestPart> parts = parse(batch);
    Assert.assertEquals(1, parts.size());

    final BatchRequestPart part = parts.get(0);
    Assert.assertTrue(part.isChangeSet());
    Assert.assertEquals(0, part.getRequests().size());
  }

  @Test
  public void nestedChangeset() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=changeset_f980-1cb6-94dd2" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd2" + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_ID + ": 2" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.UNEXPECTED_CONTENT_TYPE);
  }

  @Test
  public void missingContentType() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        // + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_HTTP + CRLF
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESAllPrim" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void noCloseDelimiter() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void noCloseDelimiter2() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void noCloseDelimiter3() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST
        + "--" + BOUNDARY + "-"/* no hyphen */;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void absoluteUri() throws Exception {
    final List<BatchRequestPart> batchRequestParts = parse(
        createBatchWithGetRequest(SERVICE_ROOT + "/ESAllPrim?$top=1", null));

    Assert.assertEquals(1, batchRequestParts.size());
    final BatchRequestPart part = batchRequestParts.get(0);

    Assert.assertEquals(1, part.getRequests().size());
    final ODataRequest request = part.getRequests().get(0);

    Assert.assertEquals("/ESAllPrim", request.getRawODataPath());
    Assert.assertEquals("$top=1", request.getRawQueryPath());
    Assert.assertEquals(SERVICE_ROOT + "/ESAllPrim?$top=1", request.getRawRequestUri());
    Assert.assertEquals(SERVICE_ROOT, request.getRawBaseUri());
  }

  @Test
  public void uriWithAbsolutePath() throws Exception {
    final List<BatchRequestPart> batchRequestParts = parse(
        createBatchWithGetRequest("/odata/" + PROPERTY_URI, "Host: localhost"));
    final BatchRequestPart part = batchRequestParts.get(0);
    Assert.assertEquals(1, part.getRequests().size());
    final ODataRequest request = part.getRequests().get(0);
    Assert.assertEquals("/" + PROPERTY_URI, request.getRawODataPath());
    Assert.assertEquals(SERVICE_ROOT + "/" + PROPERTY_URI, request.getRawRequestUri());
  }

  @Test
  public void uriWithAbsolutePathMissingHostHeader() throws Exception {
    final List<BatchRequestPart> batchRequestParts = parse(
        createBatchWithGetRequest("/odata/" + PROPERTY_URI, null));
    final BatchRequestPart part = batchRequestParts.get(0);
    Assert.assertEquals(1, part.getRequests().size());
    final ODataRequest request = part.getRequests().get(0);
    Assert.assertEquals("/" + PROPERTY_URI, request.getRawODataPath());
    Assert.assertEquals(SERVICE_ROOT + "/" + PROPERTY_URI, request.getRawRequestUri());
  }

  @Test
  public void uriWithAbsolutePathTwoHostHeaders() throws Exception {
    parseInvalidBatchBody(createBatchWithGetRequest("/odata/" + PROPERTY_URI,
        "Host: localhost" + CRLF + "Host: localhost:80"),
        BatchDeserializerException.MessageKeys.INVALID_HOST);
  }

  @Test
  public void uriWithAbsolutePathOtherHost() throws Exception {
    parseInvalidBatchBody(createBatchWithGetRequest("/odata/" + PROPERTY_URI, "Host: localhost2"),
        BatchDeserializerException.MessageKeys.INVALID_HOST);
  }

  @Test
  public void uriWithAbsolutePathOtherPort() throws Exception {
    parseInvalidBatchBody(createBatchWithGetRequest("/odata/" + PROPERTY_URI, "Host: localhost:90"),
        BatchDeserializerException.MessageKeys.INVALID_HOST);
  }

  @Test
  public void uriWithWrongAbsolutePath() throws Exception {
    parseInvalidBatchBody(createBatchWithGetRequest("/myservice/" + PROPERTY_URI, "Host: localhost"),
        BatchDeserializerException.MessageKeys.INVALID_URI);
  }

  @Test
  public void negativeContentLengthChangeSet() throws Exception {
    parse("--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + HttpHeader.CONTENT_LENGTH + ": -2" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--");
  }

  @Test
  public void negativeContentLengthRequest() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_LENGTH + ": -2" + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_LENGTH);
  }

  @Test
  public void contentLengthGreatherThanBodyLength() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 100000" + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertEquals(1, batchRequestParts.size());

    final BatchRequestPart part = batchRequestParts.get(0);
    Assert.assertTrue(part.isChangeSet());
    Assert.assertEquals(1, part.getRequests().size());

    final ODataRequest request = part.getRequests().get(0);
    Assert.assertEquals("{\"PropertyString\":\"new\"}", IOUtils.toString(request.getBody()));
  }

  @Test
  public void contentLengthSmallerThanBodyLength() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 10" + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertEquals(1, batchRequestParts.size());

    final BatchRequestPart part = batchRequestParts.get(0);
    Assert.assertTrue(part.isChangeSet());
    Assert.assertEquals(1, part.getRequests().size());

    final ODataRequest request = part.getRequests().get(0);
    Assert.assertEquals("{\"Property", IOUtils.toString(request.getBody()));
  }

  @Test
  public void nonNumericContentLength() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 10abc" + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_LENGTH);
  }

  @Test
  public void nonStrictParser() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + CHANGESET_BOUNDARY + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": myRequest" + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + "--" + BOUNDARY + "--";

    final List<BatchRequestPart> requests = parse(batch, false);

    Assert.assertNotNull(requests);
    Assert.assertEquals(1, requests.size());

    final BatchRequestPart part = requests.get(0);
    Assert.assertTrue(part.isChangeSet());
    Assert.assertNotNull(part.getRequests());
    Assert.assertEquals(1, part.getRequests().size());

    final ODataRequest changeRequest = part.getRequests().get(0);
    Assert.assertEquals("{\"PropertyString\":\"new\"}", IOUtils.toString(changeRequest.getBody()));
    Assert.assertEquals(APPLICATION_JSON, changeRequest.getHeader(HttpHeader.CONTENT_TYPE));
    Assert.assertEquals(HttpMethod.PATCH, changeRequest.getMethod());
  }

  @Test
  public void nonStrictParserMoreCRLF() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + ";boundary=" + CHANGESET_BOUNDARY + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + CRLF // Only one CRLF allowed
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + "--" + BOUNDARY + "--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE, false);
  }

  @Test
  public void contentId() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + HttpHeader.CONTENT_ID + ": BBB" + CRLF
        + CRLF + CRLF
        + "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": image/png" + CRLF
        + "Content-Transfer-Encoding: base64" + CRLF
        + CRLF
        + "iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.PUT + " $1/PropertyInt16" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + HttpHeader.CONTENT_ID + ": 2" + CRLF
        + CRLF
        + "{\"value\":5}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    final List<BatchRequestPart> batchRequestParts = parse(batch);
    Assert.assertNotNull(batchRequestParts);

    for (BatchRequestPart multipart : batchRequestParts) {
      if (!multipart.isChangeSet()) {
        Assert.assertEquals(1, multipart.getRequests().size());
        Assert.assertEquals("BBB", multipart.getRequests().get(0).getHeader(HttpHeader.CONTENT_ID));
      } else {
        for (ODataRequest request : multipart.getRequests()) {
          if (HttpMethod.POST.equals(request.getMethod())) {
            Assert.assertEquals("1", request.getHeader(HttpHeader.CONTENT_ID));
          } else if (HttpMethod.PUT.equals(request.getMethod())) {
            Assert.assertEquals("2", request.getHeader(HttpHeader.CONTENT_ID));
            Assert.assertEquals("/$1/PropertyInt16", request.getRawODataPath());
            Assert.assertEquals(SERVICE_ROOT + "/$1/PropertyInt16", request.getRawRequestUri());
          }
        }
      }
    }
  }

  @Test
  public void noContentId() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESMedia" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + CRLF
        + CRLF
        + "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": image/png" + CRLF
        + "Content-Transfer-Encoding: base64" + CRLF
        + CRLF
        + "iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PUT + " $1/PropertyInt16" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"value\":5}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parse(batch);
  }

  @Test
  public void preamble() throws Exception {
    final String batch = "This is a preamble and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242" + CRLF
        + "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " ESAllPrim" + HTTP_VERSION + CRLF
        + ACCEPT_HEADER
        + CRLF
        + CRLF
        + "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "This is a preamble and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242" + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": image/png" + CRLF
        + "Content-Transfer-Encoding: base64" + CRLF
        + CRLF
        + "iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 2" + CRLF
        + CRLF
        + HttpMethod.PUT + " $1/PropertyInt16" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"value\":5}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertEquals(2, batchRequestParts.size());

    final BatchRequestPart getRequestPart = batchRequestParts.get(0);
    Assert.assertEquals(1, getRequestPart.getRequests().size());

    final ODataRequest getRequest = getRequestPart.getRequests().get(0);
    Assert.assertEquals(HttpMethod.GET, getRequest.getMethod());

    final BatchRequestPart changeSetPart = batchRequestParts.get(1);
    Assert.assertEquals(2, changeSetPart.getRequests().size());
    Assert.assertEquals("iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF,
        IOUtils.toString(changeSetPart.getRequests().get(0).getBody()));
    Assert.assertEquals("{\"value\":5}", IOUtils.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void contentTypeCaseInsensitive() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + HttpHeader.CONTENT_LENGTH + ": 200" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";

    parse(batch);
  }

  @Test
  public void contentTypeBoundaryCaseInsensitive() throws Exception {
    final String batch = "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; bOunDaRy=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.PATCH + " ESAllPrim(32767)" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"PropertyString\":\"new\"}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertEquals(1, batchRequestParts.size());
    Assert.assertTrue(batchRequestParts.get(0).isChangeSet());
    Assert.assertEquals(1, batchRequestParts.get(0).getRequests().size());
  }

  @Test
  public void epilog() throws Exception {
    String batch = "--" + BOUNDARY + CRLF
        + GET_REQUEST
        + "--" + BOUNDARY + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + MULTIPART_MIXED + "; boundary=" + CHANGESET_BOUNDARY + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 1" + CRLF
        + CRLF
        + HttpMethod.POST + " ESMedia" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": image/png" + CRLF
        + "Content-Transfer-Encoding: base64" + CRLF
        + CRLF
        + "iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF
        + CRLF
        + "--" + CHANGESET_BOUNDARY + CRLF
        + MIME_HEADERS
        + HttpHeader.CONTENT_ID + ": 2" + CRLF
        + CRLF
        + HttpMethod.PUT + " $1/PropertyInt16" + HTTP_VERSION + CRLF
        + HttpHeader.CONTENT_TYPE + ": " + APPLICATION_JSON + CRLF
        + CRLF
        + "{\"value\":5}" + CRLF
        + "--" + CHANGESET_BOUNDARY + "--" + CRLF
        + CRLF
        + "This is an epilog and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242"
        + CRLF
        + "--" + BOUNDARY + "--"
        + CRLF
        + "This is an epilog and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    Assert.assertNotNull(batchRequestParts);
    Assert.assertEquals(2, batchRequestParts.size());

    BatchRequestPart getRequestPart = batchRequestParts.get(0);
    Assert.assertEquals(1, getRequestPart.getRequests().size());
    ODataRequest getRequest = getRequestPart.getRequests().get(0);
    Assert.assertEquals(HttpMethod.GET, getRequest.getMethod());

    BatchRequestPart changeSetPart = batchRequestParts.get(1);
    Assert.assertEquals(2, changeSetPart.getRequests().size());
    Assert.assertEquals("iVBORw0KGgoAAAANSUhEUgAAABQAAAAMCAIAAADtbgqsAAAABmJLR0QA/wD/AP+gvaeTAAAAH0lE"
        + "QVQokWNgGHmA8S4FmpkosXngNDP+PzdANg+cZgBqiQK5mkdWWgAAAABJRU5ErkJggg==" + CRLF,
        IOUtils.toString(changeSetPart.getRequests().get(0).getBody()));
    Assert.assertEquals("{\"value\":5}",
        IOUtils.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void largeBatch() throws Exception {
    parse(readFile("/batchLarge.batch"), true);
  }

  @Test
  public void forbiddenHeaderWWWAuthenticate() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.WWW_AUTHENTICATE + ": Basic realm=\"simple\"");
  }

  @Test
  public void forbiddenHeaderAuthorization() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.AUTHORIZATION + ": Basic QWxhZdsdsddsduIHNlc2FtZQ==");
  }

  @Test
  public void forbiddenHeaderExpect() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.EXPECT + ": 100-continue");
  }

  @Test
  public void forbiddenHeaderFrom() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.FROM + ": test@test.com");
  }

  @Test
  public void forbiddenHeaderRange() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.RANGE + ": 200-256");
  }

  @Test
  public void forbiddenHeaderMaxForwards() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.MAX_FORWARDS + ": 3");
  }

  @Test
  public void forbiddenHeaderTE() throws Exception {
    parseBatchWithForbiddenHeader(HttpHeader.TE + ": deflate");
  }

  private void parseBatchWithForbiddenHeader(final String header) {
    parseInvalidBatchBody(createBatchWithGetRequest(PROPERTY_URI, header), MessageKeys.FORBIDDEN_HEADER);
  }

  private String createBatchWithGetRequest(final String url, final String additionalHeader) {
    return "--" + BOUNDARY + CRLF
        + MIME_HEADERS
        + CRLF
        + HttpMethod.GET + " " + url + HTTP_VERSION + CRLF
        + (additionalHeader == null ? "" : (additionalHeader + CRLF))
        + CRLF
        + CRLF
        + "--" + BOUNDARY + "--";
  }

  private List<BatchRequestPart> parse(final InputStream in, final boolean isStrict)
      throws BatchDeserializerException {
    final List<BatchRequestPart> batchRequestParts =
        new BatchParser().parseBatchRequest(in, BOUNDARY,
            BatchOptions.with().isStrict(isStrict).rawBaseUri(SERVICE_ROOT).build());
    Assert.assertNotNull(batchRequestParts);
    return batchRequestParts;
  }

  private List<BatchRequestPart> parse(final String batch) throws BatchDeserializerException {
    return parse(batch, true);
  }

  private List<BatchRequestPart> parse(final String batch, final boolean isStrict) throws BatchDeserializerException {
    return parse(IOUtils.toInputStream(batch), isStrict);
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key, final boolean isStrict) {
    try {
      new BatchParser().parseBatchRequest(IOUtils.toInputStream(batch), BOUNDARY,
          BatchOptions.with().isStrict(isStrict).rawBaseUri(SERVICE_ROOT).build());
      Assert.fail("No exception thrown. Expected: " + key);
    } catch (BatchDeserializerException e) {
      Assert.assertEquals(key, e.getMessageKey());
    }
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key) {
    parseInvalidBatchBody(batch, key, true);
  }

  private InputStream readFile(final String fileName) throws IOException {
    final InputStream in = ClassLoader.class.getResourceAsStream(fileName);
    if (in == null) {
      throw new IOException("Requested file '" + fileName + "' was not found.");
    }
    return in;
  }
}
