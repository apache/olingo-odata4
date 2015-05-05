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

import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.core.ODataHandler;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;

public class BatchFascadeImpl implements BatchFacade {
  private final BatchPartHandler partHandler;

  public BatchFascadeImpl(final ODataHandler oDataHandler, final ODataRequest request,
      final BatchProcessor batchProcessor,
      final boolean isStrict) {
    partHandler = new BatchPartHandler(oDataHandler, batchProcessor, this);
  }

  @Override
  public ODataResponse handleODataRequest(final ODataRequest request) throws BatchDeserializerException {
    return partHandler.handleODataRequest(request);
  }

  @Override
  public ODataResponsePart handleBatchRequest(final BatchRequestPart request) throws BatchDeserializerException {
    return partHandler.handleBatchRequest(request);
  }

  @Override
  public String extractBoundaryFromContentType(final String contentType) throws BatchDeserializerException {
    return BatchParserCommon.getBoundary(contentType, 0);
  }
}
