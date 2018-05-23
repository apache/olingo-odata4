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

import java.util.List;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException;
import org.apache.olingo.server.api.deserializer.batch.BatchDeserializerException.MessageKeys;
import org.apache.olingo.server.api.processor.BatchProcessor;
import org.apache.olingo.server.core.ODataHandlerException;
import org.apache.olingo.server.core.ODataHandlerImpl;
import org.apache.olingo.server.core.deserializer.batch.BatchParserCommon;

public class BatchHandler {
  private final BatchProcessor batchProcessor;
  private final ODataHandlerImpl oDataHandler;
  private static final String RETURN_MINIMAL = "return=minimal";
  private static final String RETURN_REPRESENTATION = "return=representation";

  public BatchHandler(final ODataHandlerImpl oDataHandler, final BatchProcessor batchProcessor) {

    this.batchProcessor = batchProcessor;
    this.oDataHandler = oDataHandler;
  }

  public void process(final ODataRequest request, final ODataResponse response, final boolean isStrict)
      throws ODataApplicationException, ODataLibraryException {
    validateRequest(request);
    validatePreferHeader(request);

    final BatchFacade operation = new BatchFacadeImpl(oDataHandler, batchProcessor, isStrict);
    batchProcessor.processBatch(operation, request, response);
  }
  
  /** Checks if Prefer header is set with return=minimal or 
   * return=representation for batch requests
   * @param request
   * @throws ODataHandlerException
   */
  private void validatePreferHeader(final ODataRequest request) throws ODataHandlerException {
    final List<String> returnPreference = request.getHeaders(HttpHeader.PREFER);
    if (null != returnPreference) {
      for (String preference : returnPreference) {
        if (preference.equals(RETURN_MINIMAL) || preference.equals(RETURN_REPRESENTATION)) {
          throw new ODataHandlerException("Prefer Header not supported: " + preference,
              ODataHandlerException.MessageKeys.INVALID_PREFER_HEADER, preference);
        } 
      }
    }
  }

  private void validateRequest(final ODataRequest request) throws BatchDeserializerException {
    validateHttpMethod(request);
    validateContentType(request);
  }

  private void validateContentType(final ODataRequest request) throws BatchDeserializerException {
    // This method does validation.
    BatchParserCommon.parseContentType(request.getHeader(HttpHeader.CONTENT_TYPE), ContentType.MULTIPART_MIXED, 0);
  }

  private void validateHttpMethod(final ODataRequest request) throws BatchDeserializerException {
    if (request.getMethod() != HttpMethod.POST) {
      throw new BatchDeserializerException("Invalid HTTP method", MessageKeys.INVALID_METHOD, "0");
    }
  }
}
