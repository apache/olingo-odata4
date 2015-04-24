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
package org.apache.olingo.server.core.requests;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataTranslatedException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.core.ContentNegotiatorException;
import org.apache.olingo.server.core.ErrorHandler;
import org.apache.olingo.server.core.ServiceDispatcher;
import org.apache.olingo.server.core.ServiceHandler;
import org.apache.olingo.server.core.ServiceRequest;
import org.apache.olingo.server.core.batchhandler.referenceRewriting.BatchReferenceRewriter;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;

public class BatchRequest extends ServiceRequest {
  private static final String PREFERENCE_CONTINUE_ON_ERROR = "odata.continue-on-error";
  private final BatchReferenceRewriter rewriter;

  public BatchRequest(OData odata, ServiceMetadata serviceMetadata) {
    super(odata, serviceMetadata);
    this.rewriter = new BatchReferenceRewriter();
  }

  @Override
  public void execute(ServiceHandler handler, ODataResponse response)
      throws ODataTranslatedException, ODataApplicationException {

    if (!allowedMethod()) {
      methodNotAllowed();
    }

    validateContentType();
    boolean continueOnError = isContinueOnError();
    final String boundary = extractBoundary(getRequestContentType());

    final BatchOptions options = BatchOptions.with().rawBaseUri(request.getRawBaseUri())
        .rawServiceResolutionUri(this.request.getRawServiceResolutionUri()).build();

    final List<BatchRequestPart> parts = this.odata.createFixedFormatDeserializer()
        .parseBatchRequest(request.getBody(), boundary, options);

    ODataResponsePart partResponse = null;
    final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();

    for (BatchRequestPart part : parts) {
      if (part.isChangeSet()) {
        String txnId = null;
        try {
          txnId = handler.startTransaction();
          partResponse = processChangeSet(part, handler);
          if (partResponse.getResponses().get(0).getStatusCode() > 400) {
            handler.rollback(txnId);
          }
          handler.commit(txnId);
        } catch(ODataTranslatedException e) {
          if (txnId != null) {
            handler.rollback(txnId);
          }
          throw e;
        } catch (ODataApplicationException e) {
          if (txnId != null) {
            handler.rollback(txnId);
          }
          throw e;
        }
      } else {
        // single request, a static request
        ODataRequest partRequest = part.getRequests().get(0);
        partResponse = process(partRequest, handler);
      }
      responseParts.add(partResponse);

      // on error, should we continue?
      final int statusCode = partResponse.getResponses().get(0).getStatusCode();
      if ((statusCode >= 400 && statusCode <= 600) && !continueOnError) {
        break;
      }
    }

    // send response
    final String responseBoundary = "batch_" + UUID.randomUUID().toString();
    ;
    final InputStream responseContent = odata.createFixedFormatSerializer().batchResponse(
        responseParts, responseBoundary);
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary="
        + responseBoundary);
    response.setContent(responseContent);
    response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
  }

  ODataResponsePart process(ODataRequest partRequest, ServiceHandler serviceHandler) {
    ODataResponse partResponse = executeSingleRequest(partRequest, serviceHandler);
    addContentID(partRequest, partResponse);
    return new ODataResponsePart(partResponse, false);
  }

  ODataResponsePart processChangeSet(BatchRequestPart partRequest, ServiceHandler serviceHandler)
      throws BatchDeserializerException {
    List<ODataResponse> changeSetResponses = new ArrayList<ODataResponse>();
    // change set need to be a in a atomic operation
    for (ODataRequest changeSetPartRequest : partRequest.getRequests()) {

      this.rewriter.replaceReference(changeSetPartRequest);

      ODataResponse partResponse = executeSingleRequest(changeSetPartRequest, serviceHandler);

      this.rewriter.addMapping(changeSetPartRequest, partResponse);
      addContentID(changeSetPartRequest, partResponse);

      if (partResponse.getStatusCode() < 400) {
        changeSetResponses.add(partResponse);
      } else {
        // 11.7.4 Responding to a Batch Request
        return new ODataResponsePart(partResponse, false);
      }
    }
    return new ODataResponsePart(changeSetResponses, true);
  }

  ODataResponse executeSingleRequest(ODataRequest singleRequest, ServiceHandler handler) {
    ServiceDispatcher dispatcher = new ServiceDispatcher(this.odata, this.serviceMetadata, handler,
        this.customContentType);
    ODataResponse res = new ODataResponse();
    try {
      dispatcher.execute(singleRequest, res);
    } catch (Exception e) {
      ErrorHandler ehandler = new ErrorHandler(this.odata, this.serviceMetadata,
          getCustomContentTypeSupport());
      ehandler.handleException(e, singleRequest, res);
    }
    return res;
  }

  private void addContentID(ODataRequest batchPartRequest, ODataResponse batchPartResponse) {
    final String contentId = batchPartRequest.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
    if (contentId != null) {
      batchPartResponse.setHeader(BatchParserCommon.HTTP_CONTENT_ID, contentId);
    }
  }

  @Override
  public boolean allowedMethod() {
    return isPOST();
  }

  private void validateContentType() throws ODataApplicationException {
    final String contentType = getRequestContentType().toContentTypeString();

    if (contentType == null
        || !BatchParserCommon.PATTERN_MULTIPART_BOUNDARY.matcher(contentType).matches()) {
      throw new ODataApplicationException("Invalid content type",
          HttpStatusCode.PRECONDITION_FAILED.getStatusCode(), Locale.getDefault());
    }
  }

  @Override
  public ContentType getResponseContentType() throws ContentNegotiatorException {
    return null;
  }

  private boolean isContinueOnError() {
    final List<String> preferValues = this.request.getHeaders(HttpHeader.PREFER);

    if (preferValues != null) {
      for (final String preference : preferValues) {
        if (PREFERENCE_CONTINUE_ON_ERROR.equals(preference)) {
          return true;
        }
      }
    }
    return false;
  }

  private String extractBoundary(ContentType contentType) throws BatchDeserializerException {
    return BatchParserCommon.getBoundary(contentType.toContentTypeString(), 0);
  }
}
