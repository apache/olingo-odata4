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
package org.apache.olingo.server.api.processor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchOperation;
import org.apache.olingo.server.api.batch.BatchRequestPart;
import org.apache.olingo.server.api.batch.ODataResponsePart;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfo;

/**
 * <p>Processor implementation for handling default cases:
 * <ul><li>request for the metadata document</li>
 * <li>request for the service document</li>
 * <li>error handling</li></ul></p>
 * <p>This implementation is registered in the ODataHandler by default.
 * The default can be replaced by re-registering a custom implementation.</p>
 */
public class DefaultProcessor implements MetadataProcessor, ServiceDocumentProcessor, ExceptionProcessor,
    BatchProcessor {
  
  private static final String PREFERENCE_CONTINUE_ON_ERROR = "odata.continue-on-error";
  private static final String PREFER_HEADER = "Prefer";
  
  private OData odata;
  private ServiceMetadata serviceMetadata;

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
    this.serviceMetadata = serviceMetadata;
  }

  @Override
  public void readServiceDocument(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
    response.setContent(serializer.serviceDocument(serviceMetadata.getEdm(), request.getRawBaseUri()));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }

  @Override
  public void readMetadata(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo,
      final ContentType requestedContentType) throws ODataApplicationException, SerializerException {
    ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
    response.setContent(serializer.metadataDocument(serviceMetadata));
    response.setStatusCode(HttpStatusCode.OK.getStatusCode());
    response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
  }

  @Override
  public void processException(ODataRequest request, ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType) {
    try {
      ODataSerializer serializer = odata.createSerializer(ODataFormat.fromContentType(requestedContentType));
      response.setContent(serializer.error(serverError));
      response.setStatusCode(serverError.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, requestedContentType.toContentTypeString());
    } catch (Exception e) {
      // This should never happen but to be sure we have this catch here to prevent sending a stacktrace to a client.
      String responseContent =
          "{\"error\":{\"code\":null,\"message\":\"An unexpected exception occurred during " +
              "error processing with message: " + e.getMessage() + "\"}}";
      response.setContent(new ByteArrayInputStream(responseContent.getBytes()));
      response.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
      response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.APPLICATION_JSON.toContentTypeString());
    }
  }

  @Override
  public void executeBatch(BatchOperation operation, ODataRequest request, ODataResponse response) {
    boolean continueOnError = shouldContinueOnError(request);

    try {
      final List<BatchRequestPart> parts = operation.parseBatchRequest(request.getBody());
      final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

      for (BatchRequestPart part : parts) {
        final ODataResponsePart responsePart = operation.handleBatchRequest(part);
        responseParts.add(responsePart);    // Also add failed responses

        if (responsePart.getResponses().get(0).getStatusCode() >= 400
            && !continueOnError) {

          // Perform some additions actions
          // ...
          
          break; // Stop processing, but serialize all recent requests
        }
      }

      operation.writeResponseParts(responseParts, response);  // Serialize responses
    } catch (BatchException e) {
      throw new ODataRuntimeException(e);
    } catch (IOException e) {
      throw new ODataRuntimeException(e);
    }
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
  public ODataResponsePart executeChangeSet(BatchOperation operation, List<ODataRequest> requests,
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
