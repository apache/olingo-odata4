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
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
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

  public BatchPartHandler(final ODataHandler oDataHandler, final BatchProcessor processor,
      final BatchOperation batchOperation) {
    this.oDataHandler = oDataHandler;
    this.batchProcessor = processor;
    this.batchOperation = batchOperation;
  }

  public ODataResponse handleODataRequest(ODataRequest request) {
    final ODataResponse response = oDataHandler.process(request);
    response.setHeader(BatchParserCommon.HTTP_CONTENT_ID, request.getHeader(BatchParserCommon.HTTP_CONTENT_ID));
    
    return  response;
  }

  public ODataResponsePart handleBatchRequest(BatchRequestPart request) {
    final List<ODataResponse> responses = new ArrayList<ODataResponse>();

    if (request.isChangeSet()) {
      responses.addAll(batchProcessor.executeChangeSet(batchOperation, request.getRequests()));
      return new ODataResponsePartImpl(responses, true);
    } else {
      responses.add(handleODataRequest(request.getRequests().get(0)));
      return new ODataResponsePartImpl(responses, false);
    }
  }

}
