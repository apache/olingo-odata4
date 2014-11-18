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
package org.apache.olingo.server.core.batchhandler;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ODataHandler;
import org.apache.olingo.server.core.batchhandler.BatchHandler;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.apache.olingo.server.core.deserializer.batch.BufferedReaderIncludingLineEndings;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockedBatchHandlerTest {

  private static final String BATCH_CONTENT_TYPE = "multipart/mixed;boundary=batch_12345";
  private static final String BATCH_ODATA_PATH = "/$batch";
  private static final String BATCH_REQUEST_URI = "http://localhost:8080/odata/$batch";
  private static final String BASE_URI = "http://localhost:8080/odata";
  private static final String CRLF = "\r\n";
  private ODataHandler oDataHandler;
  private BatchHandler batchHandler;
  private int entityCounter = 1;

  @Before
  public void setup() {
    final BatchProcessor batchProcessor = new BatchTestProcessorImpl();
    batchProcessor.init(OData.newInstance(), null);

    entityCounter = 1;
    oDataHandler = mock(ODataHandler.class);
    batchHandler = new BatchHandler(oDataHandler, batchProcessor);
  }

  @Test
  public void test() throws Exception {
    final String content = "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_12345" + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 4" + CRLF
        + CRLF
        + "PUT /$3/PropertyInt32 HTTP/1.1" + CRLF // Absolute URI with separate Host header and ref.
        + "Host: http://localhost:8080/odata" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 5" + CRLF
        + CRLF
        + "POST http://localhost:8080/odata/$1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF // Absolute URI with ref.
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST $1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF // Relative URI with ref.
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST http://localhost:8080/odata/ESAllPrim HTTP/1.1" + CRLF // Absolute URI
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 3" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF // Relative URI
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 6" + CRLF
        + CRLF
        + "PUT /ESAllPrim(1) HTTP/1.1" + CRLF // Absolute URI with separate Host header
        + "Host: http://localhost:8080/odata"
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345--" + CRLF
        + CRLF
        + "--batch_12345--";
    final Map<String, List<String>> header = getMimeHeader();
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);

    batchHandler.process(request, response, true);

    BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(response.getContent()));

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(63, responseContent.size());

    // Check change set
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertTrue(responseContent.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));

    for (int i = 0; i < 6; i++) {
      String contentId = checkChangeSetPartHeader(responseContent, line);
      line += 6;

      if ("1".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESAllPrim(1)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("2".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(3)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("3".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("4".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("5".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(2)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("6".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else {
        fail();
      }

      assertEquals(CRLF, responseContent.get(line++));
    }

    // Close body part (change set)
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--changeset_"));

    // Close batch
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertEquals(63, line);
  }

  @Test
  public void testGetRequest() throws Exception {
    final String content = ""
        + "--batch_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + CRLF
        + "GET ESAllPrim(0) HTTP/1.1" + CRLF
        + CRLF
        + CRLF
        + "--batch_12345--";

    final Map<String, List<String>> header = getMimeHeader();
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);

    batchHandler.process(request, response, true);

    BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(response.getContent()));

    final List<String> responseContent = reader.toList();
    int line = 0;

    assertEquals(9, responseContent.size());
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertEquals("Content-Type: application/http" + CRLF, responseContent.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, responseContent.get(line++));
    assertEquals(CRLF, responseContent.get(line++));
    assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
    assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
    assertEquals(CRLF, responseContent.get(line++));
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--batch_"));

    assertEquals(9, line);

    reader.close();
  }

  @Test
  public void testMultipleChangeSets() throws Exception {
    final String content = ""
        + "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_12345" + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST /$1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
        + "Host: http://localhost:8080/odata" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345--" + CRLF

        + "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_54321" + CRLF
        + CRLF
        + "--changeset_54321" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST /$1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
        + "Host: http://localhost:8080/odata" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_54321" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(2) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_54321--" + CRLF

        + CRLF
        + "--batch_12345--";
    final Map<String, List<String>> header = getMimeHeader();
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);

    batchHandler.process(request, response, true);

    BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(response.getContent()));

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(49, responseContent.size());

    // Check first change set
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertTrue(responseContent.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));

    for (int i = 0; i < 2; i++) {
      String contentId = checkChangeSetPartHeader(responseContent, line);
      line += 6;

      if ("1".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("2".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(1)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else {
        fail();
      }

      assertEquals(CRLF, responseContent.get(line++));
    }
    // Close body part (1st change set)
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--changeset_"));

    // Check second change set
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertTrue(responseContent.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));

    for (int i = 0; i < 2; i++) {
      String contentId = checkChangeSetPartHeader(responseContent, line);
      line += 6;

      if ("1".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("2".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(2)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else {
        fail();
      }

      assertEquals(CRLF, responseContent.get(line++));
    }
    // Close body part (2nd change set)
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--changeset_"));

    // Close batch
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--batch_"));

    assertEquals(49, line);
  }

  @Test
  public void testMineBodyPartTransitiv() throws Exception {
    final String content = ""
        + "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_12345" + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 4" + CRLF
        + CRLF
        + "POST $3/NavPropertyETTwoPrimOne HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST /$1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
        + "Host: http://localhost:8080/odata" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 3" + CRLF
        + CRLF
        + "POST $2/NavPropertyETAllPrimMany HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345--" + CRLF

        + CRLF
        + "--batch_12345--";

    final Map<String, List<String>> header = getMimeHeader();
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);

    batchHandler.process(request, response, true);

    BufferedReaderIncludingLineEndings reader =
        new BufferedReaderIncludingLineEndings(new InputStreamReader(response.getContent()));

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(45, responseContent.size());

    // Check change set
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertTrue(responseContent.get(line++).contains("Content-Type: multipart/mixed; boundary=changeset_"));

    for (int i = 0; i < 4; i++) {
      String contentId = checkChangeSetPartHeader(responseContent, line);
      line += 6;

      if ("1".equals(contentId)) {
        assertEquals("HTTP/1.1 200 OK" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("2".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(1)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("3".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESAllPrim(2)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else if ("4".equals(contentId)) {
        assertEquals("HTTP/1.1 201 Created" + CRLF, responseContent.get(line++));
        assertEquals("Location: " + BASE_URI + "/ESTwoPrim(3)" + CRLF, responseContent.get(line++));
        assertEquals("Content-Length: 0" + CRLF, responseContent.get(line++));
      } else {
        fail();
      }

      assertEquals(CRLF, responseContent.get(line++));
    }

    // Close body part (change set)
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--changeset_"));

    // Close batch
    assertEquals(CRLF, responseContent.get(line++));
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertEquals(45, line);
  }

  private String checkChangeSetPartHeader(final List<String> response, int line) {
    assertEquals(CRLF, response.get(line++));
    assertTrue(response.get(line++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, response.get(line++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, response.get(line++));

    assertTrue(response.get(line).contains("Content-Id:"));
    String contentId = response.get(line).split(":")[1].trim();
    line++;

    assertEquals(CRLF, response.get(line++));

    return contentId;
  }

  @Test(expected = BatchException.class)
  public void testInvalidMethod() throws Exception {
    final String content = ""
        + "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_12345" + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345--" + CRLF
        + CRLF
        + "--batch_12345--";

    final Map<String, List<String>> header = getMimeHeader();
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);
    request.setMethod(HttpMethod.GET);

    batchHandler.process(request, response, true);
  }

  @Test(expected = BatchException.class)
  public void testInvalidContentType() throws Exception {
    final String content = ""
        + "--batch_12345" + CRLF
        + "Content-Type: multipart/mixed; boundary=changeset_12345" + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345--" + CRLF
        + CRLF
        + "--batch_12345--";

    final Map<String, List<String>> header = new HashMap<String, List<String>>();
    header.put(HttpHeader.CONTENT_TYPE, Arrays.asList(new String[] { "application/http" }));
    final ODataResponse response = new ODataResponse();
    final ODataRequest request = buildODataRequest(content, header);

    batchHandler.process(request, response, true);
  }

  /*
   * Helper methods
   */
  private Map<String, List<String>> getMimeHeader() {
    final Map<String, List<String>> header = new HashMap<String, List<String>>();
    header.put(HttpHeader.CONTENT_TYPE, Arrays.asList(new String[] { BATCH_CONTENT_TYPE }));

    return header;
  }

  private ODataRequest buildODataRequest(final String content, final Map<String, List<String>> header)
      throws UnsupportedEncodingException {
    final ODataRequest request = new ODataRequest();

    for (final String key : header.keySet()) {
      request.addHeader(key, header.get(key));
    }

    request.setMethod(HttpMethod.POST);
    request.setRawBaseUri(BASE_URI);
    request.setRawODataPath(BATCH_ODATA_PATH);
    request.setRawQueryPath("");
    request.setRawRequestUri(BATCH_REQUEST_URI);
    request.setRawServiceResolutionUri("");

    request.setBody(new ByteArrayInputStream(content.getBytes("UTF-8")));

    return request;
  }

  /**
   * Batch processor
   */
  private class BatchTestProcessorImpl implements BatchProcessor {

    private OData odata;

    @Override
    public void init(OData odata, ServiceMetadata serviceMetadata) {
      this.odata = odata;
    }

    @Override
    public ODataResponsePart executeChangeSet(BatchFacade operation, List<ODataRequest> requests,
        BatchRequestPart requestPart) {
      List<ODataResponse> responses = new ArrayList<ODataResponse>();

      for (ODataRequest request : requests) {
        try {
          responses.add(operation.handleODataRequest(request, requestPart));
        } catch (BatchException e) {
          fail();
        }
      }

      return new ODataResponsePart(responses, true);
    }

    @Override
    public void executeBatch(BatchFacade operation, ODataRequest request, ODataResponse response)
        throws SerializerException, BatchException {
      try {
        final List<BatchRequestPart> parts = odata.createFixedFormatDeserializer().parseBatchRequest(request, true);
        final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

        for (BatchRequestPart part : parts) {
          for (final ODataRequest oDataRequest : part.getRequests()) {
            // Mock the processor for a given requests
            when(oDataHandler.process(oDataRequest)).then(new Answer<ODataResponse>() {
              @Override
              public ODataResponse answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();

                return buildResponse((ODataRequest) arguments[0]);
              }
            });
          }

          responseParts.add(operation.handleBatchRequest(part));
        }

        odata.createFixedFormatSerializer().writeResponseParts(responseParts, response);
      } catch (IOException e) {
        throw new ODataRuntimeException(e);
      }
    }
  }

  private ODataResponse buildResponse(ODataRequest request) {
    final ODataResponse oDataResponse = new ODataResponse();

    if (request.getMethod() == HttpMethod.POST) {
      oDataResponse.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
      oDataResponse.setHeader(HttpHeader.LOCATION, createResourceUri(request));
    } else {
      oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
    }

    final String contentId = request.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
    if (contentId != null) {
      oDataResponse.setHeader(BatchParserCommon.HTTP_CONTENT_ID, contentId);
    }

    return oDataResponse;
  }

  private String createResourceUri(final ODataRequest request) {
    final String parts[] = request.getRawODataPath().split("/");
    String oDataPath = "";

    if (parts.length == 2) {
      // Entity Collection
      oDataPath = parts[1];
    } else {
      // Navigation property

      final String navProperty = parts[parts.length - 1];
      if (navProperty.equals("NavPropertyETTwoPrimMany")) {
        oDataPath = "ESTwoPrim";
      } else if (navProperty.equals("NavPropertyETAllPrimMany")) {
        oDataPath = "ESAllPrim";
      } else if (navProperty.equals("NavPropertyETTwoPrimOne")) {
        oDataPath = "ESTwoPrim";
      }
    }

    return BASE_URI + "/" + oDataPath + "(" + entityCounter++ + ")";
  }
}
