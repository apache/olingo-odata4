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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.junit.Test;

public class BatchRequestParserTest {

  private static final String SERVICE_ROOT = "http://localhost/odata";
  private static final String CRLF = "\r\n";
  private static final String BOUNDARY = "batch_8194-cf13-1f56";
  private static final String MIME_HEADERS = "Content-Type: application/http" + CRLF
      + "Content-Transfer-Encoding: binary" + CRLF;
  private static final String GET_REQUEST = ""
      + MIME_HEADERS
      + CRLF
      + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
      + CRLF
      + CRLF;

  @Test
  public void test() throws Exception {
    final InputStream in = readFile("/batchWithPost.batch");
    final List<BatchRequestPart> batchRequestParts = parse(in);

    assertNotNull(batchRequestParts);
    assertFalse(batchRequestParts.isEmpty());

    for (BatchRequestPart object : batchRequestParts) {
      if (!object.isChangeSet()) {
        assertEquals(1, object.getRequests().size());
        ODataRequest retrieveRequest = object.getRequests().get(0);
        assertEquals(HttpMethod.GET, retrieveRequest.getMethod());

        if (retrieveRequest.getHeaders(HttpHeader.ACCEPT_LANGUAGE) != null) {
          assertEquals(3, retrieveRequest.getHeaders(HttpHeader.ACCEPT_LANGUAGE).size());
        }

        assertEquals(SERVICE_ROOT, retrieveRequest.getRawBaseUri());
        assertEquals("/Employees('2')/EmployeeName", retrieveRequest.getRawODataPath());
        assertEquals("http://localhost/odata/Employees('2')/EmployeeName?$format=json", retrieveRequest
            .getRawRequestUri());
        assertEquals("$format=json", retrieveRequest.getRawQueryPath());
      } else {
        List<ODataRequest> requests = object.getRequests();
        for (ODataRequest request : requests) {

          assertEquals(HttpMethod.PUT, request.getMethod());
          assertEquals("100000", request.getHeader(HttpHeader.CONTENT_LENGTH));
          assertEquals("application/json;odata=verbose", request.getHeader(HttpHeader.CONTENT_TYPE));

          List<String> acceptHeader = request.getHeaders(HttpHeader.ACCEPT);
          assertEquals(3, request.getHeaders(HttpHeader.ACCEPT).size());
          assertEquals("application/atomsvc+xml;q=0.8", acceptHeader.get(0));
          assertEquals("*/*;q=0.1", acceptHeader.get(2));

          assertEquals("http://localhost/odata/Employees('2')/EmployeeName", request.getRawRequestUri());
          assertEquals("http://localhost/odata", request.getRawBaseUri());
          assertEquals("/Employees('2')/EmployeeName", request.getRawODataPath());
          assertEquals("", request.getRawQueryPath()); // No query parameter
        }
      }
    }
  }

  @Test
  public void testImageInContent() throws Exception {
    final InputStream contentInputStream = readFile("/batchWithContent.batch");
    final String content = IOUtils.toString(contentInputStream);
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees?$filter=Age%20gt%2040 HTTP/1.1" + CRLF
        + "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + "content-type:     Application/http" + CRLF
        + "content-transfer-encoding: Binary" + CRLF
        + "Content-ID: 1" + CRLF
        + CRLF
        + "POST Employees HTTP/1.1" + CRLF
        + "Content-length: 100000" + CRLF
        + "Content-type: application/octet-stream" + CRLF
        + CRLF
        + content
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> BatchRequestParts = parse(batch);

    for (BatchRequestPart part : BatchRequestParts) {
      if (!part.isChangeSet()) {
        assertEquals(1, part.getRequests().size());
        final ODataRequest retrieveRequest = part.getRequests().get(0);

        assertEquals(HttpMethod.GET, retrieveRequest.getMethod());
        assertEquals("http://localhost/odata/Employees?$filter=Age%20gt%2040", retrieveRequest.getRawRequestUri());
        assertEquals("http://localhost/odata", retrieveRequest.getRawBaseUri());
        assertEquals("/Employees", retrieveRequest.getRawODataPath());
        assertEquals("$filter=Age%20gt%2040", retrieveRequest.getRawQueryPath());
      } else {
        final List<ODataRequest> requests = part.getRequests();
        for (ODataRequest request : requests) {
          assertEquals(HttpMethod.POST, request.getMethod());
          assertEquals("100000", request.getHeader(HttpHeader.CONTENT_LENGTH));
          assertEquals("1", request.getHeader(BatchParserCommon.HTTP_CONTENT_ID));
          assertEquals("application/octet-stream", request.getHeader(HttpHeader.CONTENT_TYPE));

          final InputStream body = request.getBody();
          assertEquals(content, IOUtils.toString(body));
        }
      }
    }
  }

  @Test
  public void testPostWithoutBody() throws Exception {
    final String batch = CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: changeRequest1" + CRLF
        + CRLF
        + "POST Employees('2') HTTP/1.1" + CRLF
        + "Content-Length: 100" + CRLF
        + "Content-Type: application/octet-stream" + CRLF
        + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    for (BatchRequestPart object : batchRequestParts) {
      if (object.isChangeSet()) {
        final List<ODataRequest> requests = object.getRequests();

        for (ODataRequest request : requests) {
          assertEquals(HttpMethod.POST, request.getMethod());
          assertEquals("100", request.getHeader(HttpHeader.CONTENT_LENGTH));
          assertEquals("application/octet-stream", request.getHeader(HttpHeader.CONTENT_TYPE));
          assertNotNull(request.getBody());
        }
      }
    }
  }

  @Test
  public void testAbsoluteUri() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET http://localhost/odata/Employees('1')/EmployeeName?$top=1 HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertEquals(1, batchRequestParts.size());
    final BatchRequestPart part = batchRequestParts.get(0);

    assertEquals(1, part.getRequests().size());
    final ODataRequest request = part.getRequests().get(0);

    assertEquals("/Employees('1')/EmployeeName", request.getRawODataPath());
    assertEquals("$top=1", request.getRawQueryPath());
    assertEquals("http://localhost/odata/Employees('1')/EmployeeName?$top=1", request.getRawRequestUri());
    assertEquals("http://localhost/odata", request.getRawBaseUri());
  }

  @Test
  public void testBoundaryParameterWithQuotas() throws Exception {
    final String contentType = "multipart/mixed; boundary=\"batch_1.2+34:2j)0?\"";
    final String boundary = BatchParserCommon.getBoundary(contentType, 0);
    final String batch = ""
        + "--batch_1.2+34:2j)0?" + CRLF
        + GET_REQUEST
        + "--batch_1.2+34:2j)0?--";
    final BatchParser parser = new BatchParser();
    final BatchOptions batchOptions = BatchOptions.with().isStrict(true).rawBaseUri(SERVICE_ROOT).build();
    final List<BatchRequestPart> batchRequestParts =
        parser.parseBatchRequest(IOUtils.toInputStream(batch), boundary, batchOptions);

    assertNotNull(batchRequestParts);
    assertFalse(batchRequestParts.isEmpty());
  }

  @Test
  public void testBatchWithInvalidContentType() throws Exception {
    final String invalidContentType = "multipart;boundary=batch_1740-bb84-2f7f";

    try {
      BatchParserCommon.getBoundary(invalidContentType, 0);
      fail();
    } catch (BatchDeserializerException e) {
      assertMessageKey(e, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE);
    }
  }

  @Test
  public void testContentTypeCharset() throws Exception {
    final String contentType = "multipart/mixed; charset=UTF-8;boundary=batch_14d1-b293-b99a";
    final String boundary = BatchParserCommon.getBoundary(contentType, 0);

    assertEquals("batch_14d1-b293-b99a", boundary);
  }

  @Test
  public void testBatchWithoutBoundaryParameter() throws Exception {
    final String invalidContentType = "multipart/mixed";

    try {
      BatchParserCommon.getBoundary(invalidContentType, 0);
      fail();
    } catch (BatchDeserializerException e) {
      assertMessageKey(e, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE);
    }
  }

  @Test
  public void testBoundaryParameterWithoutQuota() throws Exception {
    final String invalidContentType = "multipart/mixed;boundary=batch_1740-bb:84-2f7f";

    try {
      BatchParserCommon.getBoundary(invalidContentType, 0);
      fail();
    } catch (BatchDeserializerException e) {
      assertMessageKey(e, BatchDeserializerException.MessageKeys.INVALID_BOUNDARY);
    }
  }

  @Test
  public void testWrongBoundaryString() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f5" + CRLF
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> parts = parse(batch);
    assertEquals(0, parts.size());
  }

  @Test
  public void testMissingHttpVersion() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding:binary" + CRLF
        + CRLF
        + "GET Employees?$format=json" + CRLF
        + "Host: localhost:8080" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void testMissingHttpVersion2() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding:binary" + CRLF
        + CRLF
        + "GET Employees?$format=json " + CRLF
        + "Host: localhost:8080" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void testMissingHttpVersion3() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding:binary" + CRLF
        + CRLF
        + "GET Employees?$format=json SMTP:3.1" + CRLF
        + "Host: localhost:8080" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void testBoundaryWithoutHyphen() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        + "batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void testNoBoundaryString() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        // + no boundary string
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void testBatchBoundaryEqualsChangeSetBoundary() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=batch_8194-cf13-1f56" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "PUT Employees('2')/EmployeeName HTTP/1.1" + CRLF
        + "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Frederic Fall MODIFIED\"}" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--"
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void testNoContentType() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void testMimeHeaderContentType() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: text/plain" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE);
  }

  @Test
  public void testMimeHeaderEncoding() throws Exception {
    String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: 8bit" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TRANSFER_ENCODING);
  }

  @Test
  public void testGetRequestMissingCRLF() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        // + CRLF // Belongs to the GET request
        + CRLF // Belongs to the
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void testInvalidMethodForBatch() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "POST Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_QUERY_OPERATION_METHOD);
  }

  @Test
  public void testNoBoundaryFound() throws Exception {
    final String batch = "batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "POST Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testEmptyRequest() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> parts = parse(batch);
    assertEquals(0, parts.size());
  }

  @Test
  public void testBadRequest() throws Exception {
    final String batch = "This is a bad request. There is no syntax and also no semantic";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testNoMethod() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + /* GET */"Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void testInvalidMethodForChangeset() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "GET Employees('2')/EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--"
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CHANGESET_METHOD);
  }

  @Test
  public void testInvalidChangeSetBoundary() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94d"/* +"d" */+ CRLF
        + MIME_HEADERS
        + CRLF
        + "POST Employees('2') HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> parts = parse(batch);
    assertEquals(1, parts.size());

    final BatchRequestPart part = parts.get(0);
    assertTrue(part.isChangeSet());
    assertEquals(0, part.getRequests().size());
  }

  @Test
  public void testNestedChangeset() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_f980-1cb6-94dd2" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd2" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST Employees('2') HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF + "MaxDataServiceVersion: 2.0" + CRLF
        + "Content-Id: 2"
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_CONTENT_TYPE);
  }

  @Test
  public void testMissingContentTransferEncoding() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + "Content-Id: 1" + CRLF
        + "Content-Type: application/http" + CRLF
        // + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "POST Employees('2') HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CONTENT_TRANSFER_ENCODING);
  }

  @Test
  public void testMissingContentType() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + "Content-Id: 1"
        // + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "POST Employees('2') HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void testNoCloseDelimiter() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testNoCloseDelimiter2() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testUriWithAbsolutePath() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Host: http://localhost" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_URI);
  }

  @Test
  public void testUriWithAbsolutePathMissingHostHeader() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.INVALID_URI);
  }

  @Test
  public void testUriWithAbsolutePathOtherHost() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Host: http://localhost2" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.INVALID_URI);
  }

  @Test
  public void testUriWithAbsolutePathWrongPath() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /myservice/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Host: http://localhost" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.INVALID_URI);
  }

  @Test
  public void testNoCloseDelimiter3() throws Exception {
    final String batch = "--batch_8194-cf13-1f56" + CRLF + GET_REQUEST + "--batch_8194-cf13-1f56-"/* no hyphen */;

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testNegativeContentLengthChangeSet() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + "Content-Length: -2" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parse(batch);
  }

  @Test
  public void testNegativeContentLengthRequest() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Id: 1" + CRLF
        + "Content-Length: 2" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parse(batch);
  }

  @Test
  public void testContentLengthGreatherThanBodyLength() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "PUT Employee/Name HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Length: 100000" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertNotNull(batchRequestParts);

    for (BatchRequestPart multipart : batchRequestParts) {
      if (multipart.isChangeSet()) {
        assertEquals(1, multipart.getRequests().size());

        final ODataRequest request = multipart.getRequests().get(0);
        assertEquals("{\"EmployeeName\":\"Peter Fall\"}", IOUtils.toString(request.getBody()));
      }
    }
  }

  @Test
  public void testContentLengthSmallerThanBodyLength() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Length: 10" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertNotNull(batchRequestParts);

    for (BatchRequestPart multipart : batchRequestParts) {
      if (multipart.isChangeSet()) {
        assertEquals(1, multipart.getRequests().size());

        final ODataRequest request = multipart.getRequests().get(0);
        assertEquals("{\"Employee", IOUtils.toString(request.getBody()));
      }
    }
  }

  @Test
  public void testNonNumericContentLength() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Length: 10abc" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_HEADER);
  }

  @Test
  public void testNonStrictParser() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_8194-cf13-1f56" + CRLF
        + "--changeset_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + "Content-ID: myRequest" + CRLF
        + "PUT Employees('2')/EmployeeName HTTP/1.1" + CRLF
        + "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + "{\"EmployeeName\":\"Frederic Fall MODIFIED\"}" + CRLF
        + "--changeset_8194-cf13-1f56--" + CRLF
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> requests = parse(batch, false);

    assertNotNull(requests);
    assertEquals(1, requests.size());

    final BatchRequestPart part = requests.get(0);
    assertTrue(part.isChangeSet());
    assertNotNull(part.getRequests());
    assertEquals(1, part.getRequests().size());

    final ODataRequest changeRequest = part.getRequests().get(0);
    assertEquals("{\"EmployeeName\":\"Frederic Fall MODIFIED\"}",
        IOUtils.toString(changeRequest.getBody()));
    assertEquals("application/json;odata=verbose", changeRequest.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(HttpMethod.PUT, changeRequest.getMethod());
  }

  @Test
  public void testNonStrictParserMoreCRLF() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed;boundary=changeset_8194-cf13-1f56" + CRLF
        + "--changeset_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + CRLF // Only one CRLF allowed
        + "PUT Employees('2')/EmployeeName HTTP/1.1" + CRLF
        + "Accept: application/atomsvc+xml;q=0.8, application/json;odata=verbose;q=0.5, */*;q=0.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + "{\"EmployeeName\":\"Frederic Fall MODIFIED\"}" + CRLF
        + "--changeset_8194-cf13-1f56--" + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchDeserializerException.MessageKeys.INVALID_STATUS_LINE, false);
  }

  @Test
  public void testContentId() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees HTTP/1.1" + CRLF
        + "accept: */*,application/atom+xml,application/atomsvc+xml,application/xml" + CRLF
        + "Content-Id: BBB" + CRLF
        + CRLF + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST Employees HTTP/1.1" + CRLF
        + "Content-type: application/octet-stream" + CRLF
        + CRLF
        + "/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + CRLF
        + "PUT $1/EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> batchRequestParts = parse(batch);
    assertNotNull(batchRequestParts);

    for (BatchRequestPart multipart : batchRequestParts) {
      if (!multipart.isChangeSet()) {
        assertEquals(1, multipart.getRequests().size());
        final ODataRequest retrieveRequest = multipart.getRequests().get(0);

        assertEquals("BBB", retrieveRequest.getHeader(BatchParserCommon.HTTP_CONTENT_ID));
      } else {
        for (ODataRequest request : multipart.getRequests()) {
          if (HttpMethod.POST.equals(request.getMethod())) {
            assertEquals("1", request.getHeader(BatchParserCommon.HTTP_CONTENT_ID));
          } else if (HttpMethod.PUT.equals(request.getMethod())) {
            assertEquals("2", request.getHeader(BatchParserCommon.HTTP_CONTENT_ID));
            assertEquals("/$1/EmployeeName", request.getRawODataPath());
            assertEquals("http://localhost/odata/$1/EmployeeName", request.getRawRequestUri());
          }
        }
      }
    }
  }

  @Test
  public void testNoContentId() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees HTTP/1.1" + CRLF
        + "accept: */*,application/atom+xml,application/atomsvc+xml,application/xml" + CRLF
        + CRLF + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST Employees HTTP/1.1" + CRLF
        + "Content-type: application/octet-stream" + CRLF
        + CRLF
        + "/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT $1/EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parse(batch);
  }

  @Test
  public void testPreamble() throws Exception {
    final String batch = ""
        + "This is a preamble and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242" + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees HTTP/1.1" + CRLF
        + "accept: */*,application/atom+xml,application/atomsvc+xml,application/xml" + CRLF
        + "Content-Id: BBB" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "This is a preamble and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242" + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST Employees HTTP/1.1" + CRLF
        + "Content-type: application/octet-stream" + CRLF
        + CRLF
        + "/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 2" + CRLF
        + CRLF
        + "PUT $1/EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertNotNull(batchRequestParts);
    assertEquals(2, batchRequestParts.size());

    final BatchRequestPart getRequestPart = batchRequestParts.get(0);
    assertEquals(1, getRequestPart.getRequests().size());

    final ODataRequest getRequest = getRequestPart.getRequests().get(0);
    assertEquals(HttpMethod.GET, getRequest.getMethod());

    final BatchRequestPart changeSetPart = batchRequestParts.get(1);
    assertEquals(2, changeSetPart.getRequests().size());
    assertEquals("/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA"
        + CRLF,
        IOUtils.toString(changeSetPart.getRequests().get(0).getBody()));
    assertEquals("{\"EmployeeName\":\"Peter Fall\"}",
        IOUtils.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void testContentTypeCaseInsensitive() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: muLTiParT/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + "Content-Length: 200" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parse(batch);
  }

  @Test
  public void testContentTypeBoundaryCaseInsensitive() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; bOunDaRy=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "PUT EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertNotNull(batchRequestParts);
    assertEquals(1, batchRequestParts.size());
    assertTrue(batchRequestParts.get(0).isChangeSet());
    assertEquals(1, batchRequestParts.get(0).getRequests().size());
  }

  @Test
  public void testEpilog() throws Exception {
    String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees HTTP/1.1" + CRLF
        + "accept: */*,application/atom+xml,application/atomsvc+xml,application/xml" + CRLF
        + "Content-Id: BBB" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_f980-1cb6-94dd" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST Employees HTTP/1.1" + CRLF
        + "Content-type: application/octet-stream" + CRLF
        + CRLF
        + "/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd" + CRLF
        + MIME_HEADERS
        + "Content-ID: 2" + CRLF
        + CRLF
        + "PUT $1/EmployeeName HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + "{\"EmployeeName\":\"Peter Fall\"}" + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "This is an epilog and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242"
        + CRLF
        + "--batch_8194-cf13-1f56--"
        + CRLF
        + "This is an epilog and must be ignored" + CRLF
        + CRLF
        + CRLF
        + "----1242";
    final List<BatchRequestPart> batchRequestParts = parse(batch);

    assertNotNull(batchRequestParts);
    assertEquals(2, batchRequestParts.size());

    BatchRequestPart getRequestPart = batchRequestParts.get(0);
    assertEquals(1, getRequestPart.getRequests().size());
    ODataRequest getRequest = getRequestPart.getRequests().get(0);
    assertEquals(HttpMethod.GET, getRequest.getMethod());

    BatchRequestPart changeSetPart = batchRequestParts.get(1);
    assertEquals(2, changeSetPart.getRequests().size());
    assertEquals("/9j/4AAQSkZJRgABAQEBLAEsAAD/4RM0RXhpZgAATU0AKgAAAAgABwESAAMAAAABAAEAAAEaAAUAAAABAAAAYgEbAAUAAAA"
        + CRLF,
        IOUtils.toString(changeSetPart.getRequests().get(0).getBody()));
    assertEquals("{\"EmployeeName\":\"Peter Fall\"}",
        IOUtils.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void testLargeBatch() throws Exception {
    final InputStream in = readFile("/batchLarge.batch");
    parse(in);
  }

  @Test
  public void testForddenHeaderAuthorisation() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Authorization: Basic QWxhZdsdsddsduIHNlc2FtZQ==" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  @Test
  public void testForddenHeaderExpect() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Expect: 100-continue" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  @Test
  public void testForddenHeaderFrom() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "From: test@test.com" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  @Test
  public void testForddenHeaderRange() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Range: 200-256" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  @Test
  public void testForddenHeaderMaxForwards() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Max-Forwards: 3" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  @Test
  public void testForddenHeaderTE() throws Exception {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "TE: deflate" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.FORBIDDEN_HEADER);
  }

  private List<BatchRequestPart> parse(final InputStream in, final boolean isStrict) throws Exception {
    final BatchParser parser = new BatchParser();
    final BatchOptions options = BatchOptions.with().isStrict(isStrict).rawBaseUri(SERVICE_ROOT).build();
    final List<BatchRequestPart> batchRequestParts =
        parser.parseBatchRequest(in, BOUNDARY, options);

    assertNotNull(batchRequestParts);

    return batchRequestParts;
  }

  private List<BatchRequestPart> parse(final InputStream in) throws Exception {
    return parse(in, true);
  }

  private List<BatchRequestPart> parse(final String batch) throws Exception {
    return parse(batch, true);
  }

  private List<BatchRequestPart> parse(final String batch, final boolean isStrict) throws Exception {
    return parse(IOUtils.toInputStream(batch), isStrict);
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key, final boolean isStrict)
      throws Exception {
    final BatchParser parser = new BatchParser();
    final BatchOptions options = BatchOptions.with().isStrict(isStrict).rawBaseUri(SERVICE_ROOT).build();
    try {
      parser.parseBatchRequest(IOUtils.toInputStream(batch), BOUNDARY, options);
      fail("No exception thrown. Expect: " + key.toString());
    } catch (BatchDeserializerException e) {
      assertMessageKey(e, key);
    }
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key) throws Exception {
    parseInvalidBatchBody(batch, key, true);
  }

  private void assertMessageKey(final BatchDeserializerException e, final MessageKeys key) {
    assertEquals(key, e.getMessageKey());
  }

  private InputStream readFile(final String fileName) throws Exception {
    final InputStream in = ClassLoader.class.getResourceAsStream(fileName);
    if (in == null) {
      throw new IOException("Requested file '" + fileName + "' was not found.");
    }
    return in;
  }
}
