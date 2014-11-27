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
package org.apache.olingo.server.tecsvc.processor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.batch.exception.BatchException;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.tecsvc.data.DataProvider;

public class TechnicalBatchProcessor extends TechnicalProcessor implements BatchProcessor {
  // TODO remove
  private static final String PREFERENCE_CONTINUE_ON_ERROR = "odata.continue-on-error";

  public TechnicalBatchProcessor(DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void executeBatch(BatchFacade fascade, ODataRequest request, ODataResponse response)
      throws SerializerException, BatchException {

    // TODO refactor isContinueOnError
    boolean continueOnError = isContinueOnError(request);

    final String boundary = getBoundary(request.getHeader(HttpHeader.CONTENT_TYPE));
    final BatchOptions options = BatchOptions.with()
                                         .rawBaseUri(request.getRawBaseUri())
                                         .rawServiceResolutionUri(request.getRawServiceResolutionUri()).build();
    final List<BatchRequestPart> parts = odata.createFixedFormatDeserializer().parseBatchRequest(request.getBody(),
        boundary, options);
    final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

    for (BatchRequestPart part : parts) {
      final ODataResponsePart responsePart = fascade.handleBatchRequest(part);
      responseParts.add(responsePart); // Also add failed responses
      final int statusCode = responsePart.getResponses().get(0).getStatusCode();

      if ((statusCode >= 400 && statusCode <= 600) && !continueOnError) {

        // Perform some additions actions
        // ...

        break; // Stop processing, but serialize all recent requests
      }
    }

    final String responseBoundary = "batch_" + UUID.randomUUID().toString();;
    final InputStream responseContent =
        odata.createFixedFormatSerializer().batchResponse(responseParts, responseBoundary);
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=" + responseBoundary);
    response.setContent(responseContent);
    response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
  }

  // TODO refactor isContinueOnError
  private boolean isContinueOnError(ODataRequest request) {
    final List<String> preferValues = request.getHeaders(HttpHeader.PREFER);

    if (preferValues != null) {
      for (final String preference : preferValues) {
        if (PREFERENCE_CONTINUE_ON_ERROR.equals(preference)) {
          return true;
        }
      }
    }
    return false;

  }

  // TODO refactor getBoundary
  private String getBoundary(String contentType) {
    if (contentType == null) {
      throw new IllegalArgumentException("Content mustn`t be null.");
    }

    if (contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/mixed")) {
      final String[] parameter = contentType.split(";");

      for (final String pair : parameter) {
        final String[] attrValue = pair.split("=");
        if (attrValue.length == 2 && "boundary".equals(attrValue[0].trim().toLowerCase(Locale.ENGLISH))) {
          if (attrValue[1].matches("([a-zA-Z0-9_\\-\\.'\\+]{1,70})|\"([a-zA-Z0-9_\\-\\.'\\+\\s\\" +
              "(\\),/:=\\?]{1,69}[a-zA-Z0-9_\\-\\.'\\+\\(\\),/:=\\?])\"")) {

            String boundary = attrValue[1].trim();
            if (boundary.matches("\".*\"")) {
              boundary = boundary.replace("\"", "");
            }

            return boundary;
          } else {
            throw new IllegalArgumentException("Invalid boundary");
          }
        }

      }
    }
    throw new IllegalArgumentException("Content type is not multipart mixed.");
  }

  @Override
  public ODataResponsePart executeChangeSet(BatchFacade fascade, List<ODataRequest> requests) {
    List<ODataResponse> responses = new ArrayList<ODataResponse>();

    for (ODataRequest request : requests) {
      try {
        final ODataResponse oDataResponse = fascade.handleODataRequest(request);
        final int statusCode = oDataResponse.getStatusCode();

        if (statusCode < 400) {
          responses.add(oDataResponse);
        } else {
          // Rollback
          // ...

          // OData Version 4.0 Part 1: Protocol Plus Errata 01
          // 11.7.4 Responding to a Batch Request
          //
          // When a request within a change set fails, the change set response is not represented using
          // the multipart/mixed media type. Instead, a single response, using the application/http media type
          // and a Content-Transfer-Encoding header with a value of binary, is returned that applies to all requests
          // in the change set and MUST be formatted according to the Error Handling defined
          // for the particular response format.

          return new ODataResponsePart(oDataResponse, false);
        }
      } catch (BatchDeserializerException e) {
        throw new ODataRuntimeException(e);
      }
    }

    return new ODataResponsePart(responses, true);
  }

}
