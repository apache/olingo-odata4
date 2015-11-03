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
package myservice.mynamespace.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchOptions;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;

import myservice.mynamespace.data.Storage;

public class DemoBatchProcessor implements BatchProcessor {

  private OData odata;
  private Storage storage;

  public DemoBatchProcessor(final Storage storage) {
    this.storage = storage;
  }

  @Override
  public void init(final OData odata, final ServiceMetadata serviceMetadata) {
    this.odata = odata;
  }

  @Override
  public void processBatch(final BatchFacade facade, final ODataRequest request, final ODataResponse response)
      throws ODataApplicationException, ODataLibraryException {
    
    // 1. Extract the boundary
    final String boundary = facade.extractBoundaryFromContentType(request.getHeader(HttpHeader.CONTENT_TYPE));
    
    // 2. Prepare the batch options
    final BatchOptions options = BatchOptions.with().rawBaseUri(request.getRawBaseUri())
                                                    .rawServiceResolutionUri(request.getRawServiceResolutionUri())
                                                    .build();
    
    // 3. Deserialize the batch request
    final List<BatchRequestPart> requestParts = odata.createFixedFormatDeserializer()
                                                     .parseBatchRequest(request.getBody(), boundary, options);
    
    // 4. Execute the batch request parts
    final List<ODataResponsePart> responseParts = new ArrayList<ODataResponsePart>();
    for (final BatchRequestPart part : requestParts) {
      responseParts.add(facade.handleBatchRequest(part));
    }

    // 5. Serialize the response content
    final InputStream responseContent = odata.createFixedFormatSerializer().batchResponse(responseParts, boundary);
    
    // 6. Create a new boundary for the response
    final String responseBoundary = "batch_" + UUID.randomUUID().toString();

    // 7. Setup response
    response.setHeader(HttpHeader.CONTENT_TYPE, ContentType.MULTIPART_MIXED + ";boundary=" + responseBoundary);
    response.setContent(responseContent);
    response.setStatusCode(HttpStatusCode.ACCEPTED.getStatusCode());
  }
  
  
  @Override
  public ODataResponsePart processChangeSet(final BatchFacade facade, final List<ODataRequest> requests)
      throws ODataApplicationException, ODataLibraryException {
    /* 
     * OData Version 4.0 Part 1: Protocol Plus Errata 02
     *      11.7.4 Responding to a Batch Request
     * 
     *      All operations in a change set represent a single change unit so a service MUST successfully process and 
     *      apply all the requests in the change set or else apply none of them. It is up to the service implementation 
     *      to define rollback semantics to undo any requests within a change set that may have been applied before 
     *      another request in that same change set failed and thereby apply this all-or-nothing requirement. 
     *      The service MAY execute the requests within a change set in any order and MAY return the responses to the 
     *       individual requests in any order. The service MUST include the Content-ID header in each response with the 
     *      same value that the client specified in the corresponding request, so clients can correlate requests 
     *      and responses.
     * 
     * To keep things simple, we dispatch the requests within the change set to the other processor interfaces.
     */
    final List<ODataResponse> responses = new ArrayList<ODataResponse>();
    
    try {
      storage.beginTransaction();
      
      for(final ODataRequest request : requests) {
        // Actual request dispatching to the other processor interfaces.
        final ODataResponse response = facade.handleODataRequest(request);
  
        // Determine if an error occurred while executing the request.
        // Exceptions thrown by the processors get caught and result in a proper OData response.
        final int statusCode = response.getStatusCode();
        if(statusCode < 400) {
          // The request has been executed successfully. Return the response as a part of the change set
          responses.add(response);
        } else {
          // Something went wrong. Undo all previous requests in this Change Set
          storage.rollbackTranscation();
          
          /*
           * In addition the response must be provided as follows:
           * 
           * OData Version 4.0 Part 1: Protocol Plus Errata 02
           *     11.7.4 Responding to a Batch Request
           *
           *     When a request within a change set fails, the change set response is not represented using
           *     the multipart/mixed media type. Instead, a single response, using the application/http media type
           *     and a Content-Transfer-Encoding header with a value of binary, is returned that applies to all requests
           *     in the change set and MUST be formatted according to the Error Handling defined
           *     for the particular response format.
           *     
           * This can be simply done by passing the response of the failed ODataRequest to a new instance of 
           * ODataResponsePart and setting the second parameter "isChangeSet" to false.
           */
          return new ODataResponsePart(response, false);
        }
      }
      
      // Everything went well, so commit the changes.
      storage.commitTransaction();
      return new ODataResponsePart(responses, true);
      
    } catch(ODataApplicationException e) {
      // See below
      storage.rollbackTranscation();
      throw e;
    } catch(ODataLibraryException e) {
      // The request is malformed or the processor implementation is not correct.
      // Throwing an exception will stop the whole batch request not only the change set!
      storage.rollbackTranscation();
      throw e;
    }
  }
}
