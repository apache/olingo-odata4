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
 */package org.apache.olingo.server.api.batch;

import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.exception.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;
import org.apache.olingo.server.api.processor.BatchProcessor;

/**
 * Provides methods to process {@link ODataRequest} and {@link BatchRequestPart}.
 * 
 * Within a {@link BatchProcessor} implementation BatchRequestsParts should be passed to
 * {@link BatchFacade#handleBatchRequest(BatchRequestPart)}. If only if the BatchRequests part represents
 * a change set, the request will be delegated to
 * {@link org.apache.olingo.server.api.processor.BatchProcessor#processChangeSet(BatchFacade, java.util.List)}.
 * Otherwise the requests will be directly executed.
 * 
 * The processor implementation could use {@link BatchFacade#handleODataRequest(ODataRequest)} to processes
 * requests in a change set.
 */
public interface BatchFacade {
  /**
   * Executes a ODataRequest, which must be a part of a change set.
   * Each requests must have a Content-Id header field, which holds an id that is unique in the whole batch request.
   *
   * @param request   ODataRequest to process
   * @return          Corresponding ODataResult to the given request
   * @throws BatchDeserializerException
   */
  public ODataResponse handleODataRequest(ODataRequest request) throws BatchDeserializerException;
  
  /**
   * Handles a BatchRequestPart.
   * 
   * @param request   Request to process
   * @return          Corresponding  {@link ODataResponsePart}
   * @throws BatchDeserializerException
   */
  public ODataResponsePart handleBatchRequest(BatchRequestPart request) throws BatchDeserializerException;
  
  /**
   * Extracts the boundary of a multipart/mixed header. 
   * See RFC 2046#5.1
   * 
   * @param contentType    Content Type
   * @return               Boundary
   */
  public String extractBoundaryFromContentType(String contentType) throws BatchDeserializerException;
}
