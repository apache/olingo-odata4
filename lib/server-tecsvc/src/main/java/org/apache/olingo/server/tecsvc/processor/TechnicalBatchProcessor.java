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
import java.util.UUID;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.prefer.PreferencesApplied;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.tecsvc.async.AsyncProcessor;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.apache.olingo.server.tecsvc.data.DataProvider;

public class TechnicalBatchProcessor extends TechnicalProcessor implements BatchProcessor {

  public TechnicalBatchProcessor(final DataProvider dataProvider) {
    super(dataProvider);
  }

  @Override
  public void processBatch(final BatchFacade facade, final ODataRequest request, final ODataResponse response)
      throws ODataApplicationException, ODataLibraryException {
    // only the first batch call (process batch) must be handled in a separate way for async support
    // because a changeset has to be wrapped within a process batch call
    if(odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).hasRespondAsync()) {
      TechnicalAsyncService asyncService = TechnicalAsyncService.getInstance();
      BatchProcessor processor = new TechnicalBatchProcessor(dataProvider);
      processor.init(odata, serviceMetadata);
      AsyncProcessor<BatchProcessor> asyncProcessor = asyncService.register(processor, BatchProcessor.class);
      asyncProcessor.prepareFor().processBatch(facade, request, response);
      String location = asyncProcessor.processAsync();
      TechnicalAsyncService.acceptedResponse(response, location);
      //
      return;
    }


    final boolean continueOnError =
        odata.createPreferences(request.getHeaders(HttpHeader.PREFER)).hasContinueOnError();

    final String boundary = facade.extractBoundaryFromContentType(request.getHeader(HttpHeader.CONTENT_TYPE));
    final BatchOptions options = BatchOptions.with()
        .rawBaseUri(request.getRawBaseUri())
        .rawServiceResolutionUri(request.getRawServiceResolutionUri()).build();
    final List<BatchRequestPart> parts = odata.createFixedFormatDeserializer().parseBatchRequest(request.getBody(),
        boundary, options);
    final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

    for (BatchRequestPart part : parts) {
      final ODataResponsePart responsePart = facade.handleBatchRequest(part);
      responseParts.add(responsePart); // Also add failed responses.
      final int statusCode = responsePart.getResponses().get(0).getStatusCode();

      if ((statusCode >= 400 && statusCode <= 600) && !continueOnError) {

        // Perform some additional actions.
        // ...

        break; // Stop processing, but serialize responses to all recent requests.
      }
    }

    final String responseBoundary = "batch_" + UUID.randomUUID().toString();
    final InputStream responseContent =
        odata.createFixedFormatSerializer().batchResponse(responseParts, responseBoundary);
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=" + responseBoundary);
    response.setContent(responseContent);
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    if (continueOnError) {
      response.setHeader(HttpHeader.PREFERENCE_APPLIED,
          PreferencesApplied.with().continueOnError().build().toValueString());
    }
  }

  @Override
  public ODataResponsePart processChangeSet(final BatchFacade facade, final List<ODataRequest> requests)
      throws ODataApplicationException, ODataLibraryException {
    List<ODataResponse> responses = new ArrayList<ODataResponse>();

    for (ODataRequest request : requests) {
      final ODataResponse oDataResponse = facade.handleODataRequest(request);
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
    }

    return new ODataResponsePart(responses, true);
  }

}
