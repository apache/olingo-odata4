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
package org.apache.olingo.server.core.batch.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchOperation;
import org.apache.olingo.server.api.batch.BatchRequestPart;
import org.apache.olingo.server.api.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.core.ODataHandler;
import org.apache.olingo.server.core.batch.parser.BatchParserCommon;

public class BatchPartHandler {

  private ODataHandler oDataHandler;
  private BatchProcessor batchProcessor;
  private BatchOperation batchOperation;
  private Map<BatchRequestPart, UriMapping> uriMapping = new HashMap<BatchRequestPart, UriMapping>();
  
  public BatchPartHandler(final ODataHandler oDataHandler, final BatchProcessor processor,
      final BatchOperation batchOperation) {
    this.oDataHandler = oDataHandler;
    this.batchProcessor = processor;
    this.batchOperation = batchOperation;
  }

  public ODataResponse handleODataRequest(ODataRequest request, BatchRequestPart requestPart) {
    final ODataResponse response;
    
    if(requestPart.isChangeSet()) {
      final UriMapping mapping = getUriMappingOrDefault(requestPart);
      final String reference = BatchChangeSetSorter.getReferenceInURI(request);
      if(reference != null) {
        BatchChangeSetSorter.replaceContentIdReference(request, reference, mapping.getUri(reference));
      }
      
       response = oDataHandler.process(request);
       
      final String contentId = request.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
      final String locationHeader = request.getHeader(HttpHeader.LOCATION);
      mapping.addMapping(contentId, locationHeader);
    } else {
      response = oDataHandler.process(request);
    }
    
    final String contentId = request.getHeader(BatchParserCommon.HTTP_CONTENT_ID);
    if(contentId != null) {
      response.setHeader(BatchParserCommon.HTTP_CONTENT_ID, contentId);
    }
    
    return  response;
  }
  
  private UriMapping getUriMappingOrDefault(final BatchRequestPart requestPart) {
    UriMapping mapping = uriMapping.get(requestPart);
    
    if(uriMapping == null) {
      mapping = new UriMapping();
    }
    uriMapping.put(requestPart, mapping);
    
    return mapping;
  }
  
  public ODataResponsePart handleBatchRequest(BatchRequestPart request) throws BatchException {
    if (request.isChangeSet()) {
      return handleChangeSet(request);
    } else {
      final List<ODataResponse> responses = new ArrayList<ODataResponse>();
      responses.add(handleODataRequest(request.getRequests().get(0), request));
      
      return new ODataResponsePartImpl(responses, false);
    }
  }

  private ODataResponsePart handleChangeSet(BatchRequestPart request) throws BatchException {
    final List<ODataResponse> responses = new ArrayList<ODataResponse>();
    final BatchChangeSetSorter sorter = new BatchChangeSetSorter(request.getRequests());

    responses.addAll(batchProcessor.executeChangeSet(batchOperation, sorter.getOrderdRequests(), request));
    
    return new ODataResponsePartImpl(responses, true);
  }

}
