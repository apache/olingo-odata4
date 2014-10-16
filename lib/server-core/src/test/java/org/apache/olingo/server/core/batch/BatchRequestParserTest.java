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
package org.apache.olingo.server.core.batch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.batch.BatchRequestPart;
import org.apache.olingo.server.core.batch.BatchException.MessageKeys;
import org.apache.olingo.server.core.batch.parser.BatchParser;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;
import org.junit.Test;

public class BatchRequestParserTest {

  private static final String SERVICE_ROOT = "http://localhost/odata";
  private static final String CONTENT_TYPE = "multipart/mixed;boundary=batch_8194-cf13-1f56";
  private static final String CRLF = "\r\n";
  private static final String MIME_HEADERS = "Content-Type: application/http" + CRLF
      + "Content-Transfer-Encoding: binary" + CRLF;
  private static final String GET_REQUEST = ""
      + MIME_HEADERS
      + CRLF
      + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
      + CRLF
      + CRLF;

  @Test
  public void test() throws IOException, BatchException, URISyntaxException {
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
  public void testImageInContent() throws IOException, BatchException, URISyntaxException {
    final InputStream contentInputStream = readFile("/batchWithContent.batch");
    final String content = StringUtil.toString(contentInputStream);
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
          assertEquals(content, StringUtil.toString(body));
        }
      }
    }
  }

  @Test
  public void testPostWithoutBody() throws IOException, BatchException, URISyntaxException {
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
  public void testBoundaryParameterWithQuotas() throws BatchException, UnsupportedEncodingException {
    final String contentType = "multipart/mixed; boundary=\"batch_1.2+34:2j)0?\"";
    final String batch = ""
        + "--batch_1.2+34:2j)0?" + CRLF
        + GET_REQUEST
        + "--batch_1.2+34:2j)0?--";
    final BatchParser parser = new BatchParser(contentType, SERVICE_ROOT, "", true);
    final List<BatchRequestPart> batchRequestParts = parser.parseBatchRequest(StringUtil.toInputStream(batch));

    assertNotNull(batchRequestParts);
    assertFalse(batchRequestParts.isEmpty());
  }

  @Test
  public void testBatchWithInvalidContentType() throws UnsupportedEncodingException {
    final String invalidContentType = "multipart;boundary=batch_1740-bb84-2f7f";
    final String batch = ""
        + "--batch_1740-bb84-2f7f" + CRLF
        + GET_REQUEST
        + "--batch_1740-bb84-2f7f--";
    final BatchParser parser = new BatchParser(invalidContentType, SERVICE_ROOT, "", true);

    try {
      parser.parseBatchRequest(StringUtil.toInputStream(batch));
      fail();
    } catch (BatchException e) {
      assertMessageKey(e, BatchException.MessageKeys.INVALID_CONTENT_TYPE);
    }
  }

  @Test
  public void testContentTypeCharset() throws BatchException {
    final String contentType = "multipart/mixed; charset=UTF-8;boundary=batch_14d1-b293-b99a";
    final String batch = ""
                    + "--batch_14d1-b293-b99a" + CRLF
                    + GET_REQUEST
                    + "--batch_14d1-b293-b99a--";
    final BatchParser parser = new BatchParser(contentType, SERVICE_ROOT, "", true);
    final List<BatchRequestPart> parts = parser.parseBatchRequest(StringUtil.toInputStream(batch));
    
    assertEquals(1, parts.size());
  }
  
  @Test
  public void testBatchWithoutBoundaryParameter() throws UnsupportedEncodingException {
    final String invalidContentType = "multipart/mixed";
    final String batch = ""
        + "--batch_1740-bb84-2f7f" + CRLF
        + GET_REQUEST
        + "--batch_1740-bb84-2f7f--";
    final BatchParser parser = new BatchParser(invalidContentType, SERVICE_ROOT, "", true);

    try {
      parser.parseBatchRequest(StringUtil.toInputStream(batch));
      fail();
    } catch (BatchException e) {
      assertMessageKey(e, BatchException.MessageKeys.INVALID_CONTENT_TYPE);
    }
  }

  @Test
  public void testBoundaryParameterWithoutQuota() throws UnsupportedEncodingException {
    final String invalidContentType = "multipart/mixed;boundary=batch_1740-bb:84-2f7f";
    final String batch = ""
        + "--batch_1740-bb:84-2f7f" + CRLF
        + GET_REQUEST
        + "--batch_1740-bb:84-2f7f--";
    final BatchParser parser = new BatchParser(invalidContentType, SERVICE_ROOT, "", true);

    try {
      parser.parseBatchRequest(StringUtil.toInputStream(batch));
      fail();
    } catch (BatchException e) {
      assertMessageKey(e, BatchException.MessageKeys.INVALID_BOUNDARY);
    }
  }

  @Test
  public void testWrongBoundaryString() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f5" + CRLF
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER);
  }

  @Test
  public void testMissingHttpVersion() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void testMissingHttpVersion2() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void testMissingHttpVersion3() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_HTTP_VERSION);
  }

  @Test
  public void testBoundaryWithoutHyphen() throws UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        + "batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void testNoBoundaryString() throws UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST
        // + no boundary string
        + GET_REQUEST
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CONTENT);
  }

  @Test
  public void testBatchBoundaryEqualsChangeSetBoundary() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void testNoContentType() throws UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void testMimeHeaderContentType() throws UnsupportedEncodingException {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: text/plain" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CONTENT_TYPE);
  }

  @Test
  public void testMimeHeaderEncoding() throws UnsupportedEncodingException {
    String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: 8bit" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CONTENT_TRANSFER_ENCODING);
  }

  @Test
  public void testGetRequestMissingCRLF() throws UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + "Content-ID: 1" + CRLF
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF
        // + CRLF // Belongs to the GET request
        + CRLF // Belongs to the
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BLANK_LINE);
  }

  @Test
  public void testInvalidMethodForBatch() throws UnsupportedEncodingException {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "POST Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_QUERY_OPERATION_METHOD);
  }

  @Test
  public void testNoBoundaryFound() throws UnsupportedEncodingException {
    final String batch = "batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "POST Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF;

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER);
  }

  @Test
  public void testBadRequest() throws UnsupportedEncodingException {
    final String batch = "This is a bad request. There is no syntax and also no semantic";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER);
  }

  @Test
  public void testNoMethod() throws UnsupportedEncodingException {
    final String batch = "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + /* GET */"Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_STATUS_LINE);
  }

  @Test
  public void testInvalidMethodForChangeset() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CHANGESET_METHOD);
  }

  @Test
  public void testInvalidChangeSetBoundary() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_BOUNDARY_DELIMITER);
  }

  @Test
  public void testNestedChangeset() throws UnsupportedEncodingException {
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
        + "Content-Type: application/json;odata=verbose" + CRLF
        + "MaxDataServiceVersion: 2.0" + CRLF
        + "Content-Id: 2"
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--changeset_f980-1cb6-94dd--" + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_CONTENT_TYPE);
  }

  @Test
  public void testMissingContentTransferEncoding() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CONTENT_TRANSFER_ENCODING);
  }

  @Test
  public void testMissingContentType() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CONTENT_TYPE);
  }

  @Test
  public void testNoCloseDelimiter() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + GET_REQUEST;

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testNoCloseDelimiter2() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET Employees('1')/EmployeeName HTTP/1.1" + CRLF;

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testInvalidUri() throws UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET http://localhost/aa/odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_URI);
  }

  @Test
  public void testUriWithAbsolutePath() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Host: http://localhost" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    final List<BatchRequestPart> parts = parse(batch);
    assertEquals(1, parts.size());

    final BatchRequestPart part = parts.get(0);
    assertEquals(1, part.getRequests().size());
    final ODataRequest request = part.getRequests().get(0);

    assertEquals("http://localhost/odata/Employees('1')/EmployeeName", request.getRawRequestUri());
    assertEquals("http://localhost/odata", request.getRawBaseUri());
    assertEquals("/Employees('1')/EmployeeName", request.getRawODataPath());
    assertEquals("", request.getRawQueryPath());
  }

  @Test
  public void testUriWithAbsolutePathMissingHostHeader() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.MISSING_MANDATORY_HEADER);
  }

  @Test
  public void testUriWithAbsolutePathMissingHostDulpicatedHeader() throws BatchException, UnsupportedEncodingException {
    final String batch = ""
        + "--batch_8194-cf13-1f56" + CRLF
        + MIME_HEADERS
        + CRLF
        + "GET /odata/Employees('1')/EmployeeName HTTP/1.1" + CRLF
        + "Host: http://localhost" + CRLF
        + "Host: http://localhost/odata" + CRLF
        + CRLF
        + CRLF
        + "--batch_8194-cf13-1f56--";

    parseInvalidBatchBody(batch, MessageKeys.MISSING_MANDATORY_HEADER);
  }

  @Test
  public void testUriWithAbsolutePathOtherHost() throws BatchException, UnsupportedEncodingException {
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
  public void testUriWithAbsolutePathWrongPath() throws BatchException, UnsupportedEncodingException {
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
  public void testNoCloseDelimiter3() throws UnsupportedEncodingException {
    final String batch = "--batch_8194-cf13-1f56" + CRLF + GET_REQUEST + "--batch_8194-cf13-1f56-"/* no hyphen */;

    parseInvalidBatchBody(batch, BatchException.MessageKeys.MISSING_CLOSE_DELIMITER);
  }

  @Test
  public void testNegativeContentLengthChangeSet() throws BatchException, IOException {
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
  public void testNegativeContentLengthRequest() throws BatchException, IOException {
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
  public void testContentLengthGreatherThanBodyLength() throws BatchException, IOException {
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
        assertEquals("{\"EmployeeName\":\"Peter Fall\"}", StringUtil.toString(request.getBody()));
      }
    }
  }

  @Test
  public void testContentLengthSmallerThanBodyLength() throws BatchException, IOException {
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
        assertEquals("{\"Employee", StringUtil.toString(request.getBody()));
      }
    }
  }

  @Test
  public void testNonNumericContentLength() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_HEADER);
  }

  @Test
  public void testNonStrictParser() throws BatchException, IOException {
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
        StringUtil.toString(changeRequest.getBody()));
    assertEquals("application/json;odata=verbose", changeRequest.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(HttpMethod.PUT, changeRequest.getMethod());
  }

  @Test
  public void testNonStrictParserMoreCRLF() throws UnsupportedEncodingException {
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

    parseInvalidBatchBody(batch, BatchException.MessageKeys.INVALID_STATUS_LINE, false);
  }

  @Test
  public void testContentId() throws BatchException, UnsupportedEncodingException {
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
  public void testNoContentId() throws BatchException, UnsupportedEncodingException {
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
  public void testPreamble() throws BatchException, IOException {
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
        StringUtil.toString(changeSetPart.getRequests().get(0).getBody()));
    assertEquals("{\"EmployeeName\":\"Peter Fall\"}",
        StringUtil.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void testContentTypeCaseInsensitive() throws BatchException, IOException {
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
  public void testContentTypeBoundaryCaseInsensitive() throws BatchException, IOException {
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
  public void testEpilog() throws BatchException, IOException {
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
        StringUtil.toString(changeSetPart.getRequests().get(0).getBody()));
    assertEquals("{\"EmployeeName\":\"Peter Fall\"}",
        StringUtil.toString(changeSetPart.getRequests().get(1).getBody()));
  }

  @Test
  public void testLargeBatch() throws BatchException, IOException {
    final InputStream in = readFile("/batchLarge.batch");
    parse(in);
  }

  @Test
  public void testForddenHeaderAuthorisation() throws UnsupportedEncodingException {
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
  public void testForddenHeaderExpect() throws UnsupportedEncodingException {
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
  public void testForddenHeaderFrom() throws UnsupportedEncodingException {
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
  public void testForddenHeaderRange() throws UnsupportedEncodingException {
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
  public void testForddenHeaderMaxForwards() throws UnsupportedEncodingException {
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
  public void testForddenHeaderTE() throws UnsupportedEncodingException {
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

  private List<BatchRequestPart> parse(final InputStream in, final boolean isStrict) throws BatchException {
    final BatchParser parser = new BatchParser(CONTENT_TYPE, SERVICE_ROOT, "", isStrict);
    final List<BatchRequestPart> batchRequestParts = parser.parseBatchRequest(in);

    assertNotNull(batchRequestParts);
    assertFalse(batchRequestParts.isEmpty());

    return batchRequestParts;
  }

  private List<BatchRequestPart> parse(final InputStream in) throws BatchException {
    return parse(in, true);
  }

  private List<BatchRequestPart> parse(final String batch) throws BatchException, UnsupportedEncodingException {
    return parse(batch, true);
  }

  private List<BatchRequestPart> parse(final String batch, final boolean isStrict) throws BatchException,
      UnsupportedEncodingException {
    return parse(StringUtil.toInputStream(batch), isStrict);
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key, final boolean isStrict)
      throws UnsupportedEncodingException {
    final BatchParser parser = new BatchParser(CONTENT_TYPE, SERVICE_ROOT, "", isStrict);

    try {
      parser.parseBatchRequest(StringUtil.toInputStream(batch));
      fail("No exception thrown. Expect: " + key.toString());
    } catch (BatchException e) {
      assertMessageKey(e, key);
    }
  }

  private void parseInvalidBatchBody(final String batch, final MessageKeys key) throws UnsupportedEncodingException {
    parseInvalidBatchBody(batch, key, true);
  }

  private void assertMessageKey(final BatchException e, final MessageKeys key) {
    assertEquals(key, e.getMessageKey());
  }

  private InputStream readFile(final String fileName) throws IOException {
    final InputStream in = ClassLoader.class.getResourceAsStream(fileName);
    if (in == null) {
      throw new IOException("Requested file '" + fileName + "' was not found.");
    }
    return in;
  }
}
