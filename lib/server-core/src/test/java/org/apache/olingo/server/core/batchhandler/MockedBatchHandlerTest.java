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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.serializer.BatchSerializerException;
import org.apache.olingo.server.core.ODataHandlerImpl;
import org.apache.olingo.server.core.deserializer.batch.BatchLineReader;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class MockedBatchHandlerTest {

  private static final String BATCH_CONTENT_TYPE = "multipart/mixed;boundary=batch_12345";
  private static final String BATCH_ODATA_PATH = "/$batch";
  private static final String BATCH_REQUEST_URI = "http://localhost:8080/odata/$batch";
  private static final String BASE_URI = "http://localhost:8080/odata";
  private static final String CRLF = "\r\n";
  private ODataHandlerImpl oDataHandler;
  private BatchHandler batchHandler;
  private int entityCounter = 1;

  @Before
  public void setup() {
    final BatchProcessor batchProcessor = new BatchTestProcessorImpl();
    batchProcessor.init(OData.newInstance(), null);

    entityCounter = 1;
    oDataHandler = mock(ODataHandlerImpl.class);
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
        + "Content-Id: 3" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 4" + CRLF
        + CRLF
        + "PUT $3/PropertyInt32 HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 1" + CRLF
        + CRLF
        + "POST ESAllPrim HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 5" + CRLF
        + CRLF
        + "POST http://localhost:8080/odata/$1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST $1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 6" + CRLF
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

    batchHandler.process(request, response, true);

    BatchLineReader reader =
        new BatchLineReader(response.getContent());

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(62, responseContent.size());

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
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertEquals(62, line);
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

    BatchLineReader reader =
        new BatchLineReader(response.getContent());

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
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT ESAllPrim(1) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST $1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
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
        + "Content-Id: 1" + CRLF
        + CRLF
        + "PUT http://localhost:8080/odata/ESAllPrim(2) HTTP/1.1" + CRLF
        + "Content-Type: application/json;odata=verbose" + CRLF
        + CRLF
        + CRLF
        + "--changeset_54321" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST $1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
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

    BatchLineReader reader =
        new BatchLineReader(response.getContent());

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(47, responseContent.size());

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
    assertTrue(responseContent.get(line++).contains("--batch_"));

    assertEquals(47, line);
  }

  @Test
  public void mimeBodyPartTransitive() throws Exception {
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
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 2" + CRLF
        + CRLF
        + "POST $1/NavPropertyETTwoPrimMany HTTP/1.1" + CRLF
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
        + "--changeset_12345" + CRLF
        + "Content-Type: application/http" + CRLF
        + "Content-Transfer-Encoding: binary" + CRLF
        + "Content-Id: 4" + CRLF
        + CRLF
        + "POST $3/NavPropertyETTwoPrimOne HTTP/1.1" + CRLF
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

    BatchLineReader reader =
        new BatchLineReader(response.getContent());

    final List<String> responseContent = reader.toList();
    reader.close();

    int line = 0;
    assertEquals(44, responseContent.size());

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
    assertTrue(responseContent.get(line++).contains("--batch_"));
    assertEquals(44, line);
  }

  @Test(expected = BatchDeserializerException.class)
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

  @Test(expected = BatchDeserializerException.class)
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
  private String checkChangeSetPartHeader(final List<String> response, final int line) {
    int lineNumber = line;
    assertEquals(CRLF, response.get(lineNumber++));
    assertTrue(response.get(lineNumber++).contains("--changeset_"));
    assertEquals("Content-Type: application/http" + CRLF, response.get(lineNumber++));
    assertEquals("Content-Transfer-Encoding: binary" + CRLF, response.get(lineNumber++));

    assertTrue(response.get(lineNumber).contains("Content-ID:"));
    String contentId = response.get(lineNumber).split(":")[1].trim();
    lineNumber++;

    assertEquals(CRLF, response.get(lineNumber++));

    return contentId;
  }

  private Map<String, List<String>> getMimeHeader() {
    return Collections.singletonMap(HttpHeader.CONTENT_TYPE, Collections.singletonList(BATCH_CONTENT_TYPE));
  }

  private ODataRequest buildODataRequest(final String content, final Map<String, List<String>> header)
      throws Exception {
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
    public void init(final OData odata, final ServiceMetadata serviceMetadata) {
      this.odata = odata;
    }

    @Override
    public ODataResponsePart processChangeSet(final BatchFacade facade, final List<ODataRequest> requests) {
      List<ODataResponse> responses = new ArrayList<ODataResponse>();

      for (ODataRequest request : requests) {
        try {
          responses.add(facade.handleODataRequest(request));
        } catch (final ODataException e) {
          fail();
        }
      }

      return new ODataResponsePart(responses, true);
    }

    @Override
    public void processBatch(final BatchFacade fascade, final ODataRequest request, final ODataResponse response)
        throws ODataApplicationException, BatchSerializerException, ODataLibraryException {
      final String boundary = getBoundary(request.getHeader(HttpHeader.CONTENT_TYPE));
      final BatchOptions options = BatchOptions.with().isStrict(true).rawBaseUri(BASE_URI).build();
      final List<BatchRequestPart> parts =
          odata.createFixedFormatDeserializer().parseBatchRequest(request.getBody(), boundary, options);
      final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

      for (BatchRequestPart part : parts) {
        for (final ODataRequest oDataRequest : part.getRequests()) {
          // Mock the processor for a given requests
          when(oDataHandler.process(oDataRequest)).then(new Answer<ODataResponse>() {
            @Override
            public ODataResponse answer(final InvocationOnMock invocation) throws Throwable {
              Object[] arguments = invocation.getArguments();

              return buildResponse((ODataRequest) arguments[0]);
            }
          });
        }

        responseParts.add(fascade.handleBatchRequest(part));
      }

      final String responeBoundary = "batch_" + UUID.randomUUID().toString();
      final InputStream responseStream =
          odata.createFixedFormatSerializer().batchResponse(responseParts, responeBoundary);

      response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=" + responeBoundary);
      response.setContent(responseStream);
    }

    private String getBoundary(final String contentType) throws BatchDeserializerException {
      return BatchParserCommon.getBoundary(contentType, 0);
    }
  }

  private ODataResponse buildResponse(final ODataRequest request) {
    final ODataResponse oDataResponse = new ODataResponse();

    if (request.getMethod() == HttpMethod.POST) {
      oDataResponse.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
      oDataResponse.setHeader(HttpHeader.LOCATION, createResourceUri(request));
    } else {
      oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
    }

    final String contentId = request.getHeader(HttpHeader.CONTENT_ID);
    if (contentId != null) {
      oDataResponse.setHeader(HttpHeader.CONTENT_ID, contentId);
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
