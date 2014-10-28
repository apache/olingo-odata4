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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchException;
import org.apache.olingo.server.api.batch.BatchOperation;
import org.apache.olingo.server.api.batch.BatchRequestPart;
import org.apache.olingo.server.api.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.core.ODataHandler;
import org.apache.olingo.server.core.batch.parser.BatchParser;
import org.apache.olingo.server.core.batch.writer.BatchResponseWriter;

public class BatchOperationImpl implements BatchOperation {
  private final BatchPartHandler partHandler;
  private final BatchResponseWriter writer;
  private final BatchParser parser;

  public BatchOperationImpl(ODataHandler oDataHandler, ODataRequest request, BatchProcessor batchProcessor,
      final boolean isStrict) {
    partHandler = new BatchPartHandler(oDataHandler, batchProcessor, this);
    writer = new BatchResponseWriter();
    parser = new BatchParser(getContentType(request), request.getRawBaseUri(),
                              request.getRawServiceResolutionUri(), isStrict);
  }

  @Override
  public List<BatchRequestPart> parseBatchRequest(InputStream in) throws BatchException {
    return parser.parseBatchRequest(in);
  }

  @Override
  public ODataResponse handleODataRequest(ODataRequest request) {
    return partHandler.handleODataRequest(request);
  }

  @Override
  public ODataResponsePart handleBatchRequest(BatchRequestPart request) {
    return partHandler.handleBatchRequest(request);
  }

  @Override
  public void writeResponseParts(List<ODataResponsePart> batchResponses, ODataResponse response) throws BatchException,
      IOException {
    writer.toODataResponse(batchResponses, response);
  }

  private String getContentType(ODataRequest request) {
    return request.getHeader(HttpHeader.CONTENT_TYPE);
  }
}
