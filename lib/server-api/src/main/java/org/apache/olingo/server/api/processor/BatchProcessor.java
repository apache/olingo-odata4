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

import java.util.List;

import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.batch.BatchFacade;
import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;

/**
 * Processor interface for handling a single instance of an Entity Type.
 */
public interface BatchProcessor extends Processor {

  /**
   * Process a complete batch request and puts serialized content and status into the response.
   * @param facade BatchFacade which should be used for further batch part handling
   * @param request OData request object containing raw HTTP information
   * @param response OData response object for collecting response data
   * @throws ODataApplicationException
   * @throws ODataLibraryException
   */
  void processBatch(BatchFacade facade, ODataRequest request, ODataResponse response)
      throws ODataApplicationException, ODataLibraryException;

  /**
   * Process a batch change set (containing several batch requests)
   * and puts serialized content and status into the response.
   * @param facade BatchFacade which should be used for further batch part handling
   * @param requests List of ODataRequests which are included in the to be processed change set
   * @throws ODataApplicationException
   * @throws ODataLibraryException
   */
  ODataResponsePart processChangeSet(BatchFacade facade, List<ODataRequest> requests)
      throws ODataApplicationException, ODataLibraryException;
}
