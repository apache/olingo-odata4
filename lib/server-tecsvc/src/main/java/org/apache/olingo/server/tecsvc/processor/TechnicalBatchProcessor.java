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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.tecsvc.data.DataProvider;

public class TechnicalBatchProcessor extends TechnicalProcessor implements BatchProcessor {

  // TODO: Move to different location
  private static final String PREFERENCE_CONTINUE_ON_ERROR = "odata.continue-on-error";
  private static final String PREFER_HEADER = "Prefer";

  public TechnicalBatchProcessor(DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void executeBatch(BatchFacade operation, ODataRequest request, ODataResponse response)
      throws SerializerException, BatchException {
    boolean continueOnError = shouldContinueOnError(request);

    final List<BatchRequestPart> parts = odata.createFixedFormatDeserializer().parseBatchRequest(request, true);
    final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

    for (BatchRequestPart part : parts) {
      final ODataResponsePart responsePart = operation.handleBatchRequest(part);
      responseParts.add(responsePart); // Also add failed responses

      if (responsePart.getResponses().get(0).getStatusCode() >= 400
          && !continueOnError) {

        // Perform some additions actions
        // ...

        break; // Stop processing, but serialize all recent requests
      }
    }

    odata.createFixedFormatSerializer().writeResponseParts(responseParts, response); // Serialize responses
  }

  private boolean shouldContinueOnError(ODataRequest request) {
    final List<String> preferValues = request.getHeaders(PREFER_HEADER);

    if (preferValues != null) {
      for (final String preference : preferValues) {
        if (PREFERENCE_CONTINUE_ON_ERROR.equals(preference)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public ODataResponsePart executeChangeSet(BatchFacade operation, List<ODataRequest> requests,
      BatchRequestPart requestPart) {
    List<ODataResponse> responses = new ArrayList<ODataResponse>();

    for (ODataRequest request : requests) {
      try {
        final ODataResponse oDataResponse = operation.handleODataRequest(request, requestPart);

        if (oDataResponse.getStatusCode() < 400) {
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
      } catch (BatchException e) {
        throw new ODataRuntimeException(e);
      }
    }

    return new ODataResponsePart(responses, true);
  }

}
