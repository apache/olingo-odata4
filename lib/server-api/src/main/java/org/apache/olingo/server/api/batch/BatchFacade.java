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

 import org.apache.olingo.server.api.ODataApplicationException;
 import org.apache.olingo.server.api.ODataLibraryException;
 import org.apache.olingo.server.api.ODataRequest;
 import org.apache.olingo.server.api.ODataResponse;
 import org.apache.olingo.server.api.deserializer.batch.BatchRequestPart;
 import org.apache.olingo.server.api.deserializer.batch.ODataResponsePart;

 /**
  * <p>Provides methods to process {@link ODataRequest}s and {@link BatchRequestPart}s
  * in the context of a batch request.</p>
  *
  * <p>Within a {@link org.apache.olingo.server.api.processor.BatchProcessor BatchProcessor} implementation
 * BatchRequestParts should be passed to {@link #handleBatchRequest(BatchRequestPart)}.
  * Only if the BatchRequestPart represents a change set, the request will be delegated to
  * {@link org.apache.olingo.server.api.processor.BatchProcessor#processChangeSet(BatchFacade, java.util.List)}.
  * Otherwise the requests will be directly executed.</p>
  *
  * <p>The processor implementation could use {@link #handleODataRequest(ODataRequest)} to process
  * requests in a change set.</p>
  */
 public interface BatchFacade {
   /**
    * Executes an ODataRequest, which must be a part of a change set.
    * Each request must have a Content-Id header field, which holds an identifier
    * that is unique in the whole batch request.
    * @param request ODataRequest to process
    * @return corresponding ODataResponse to the given request
    * @throws ODataApplicationException
    * @throws ODataLibraryException
    */
   public ODataResponse handleODataRequest(ODataRequest request)
       throws ODataApplicationException, ODataLibraryException;

   /**
    * Handles a BatchRequestPart.
    * @param request Request to process
    * @return corresponding {@link ODataResponsePart}
    * @throws ODataApplicationException
    * @throws ODataLibraryException
    */
   public ODataResponsePart handleBatchRequest(BatchRequestPart request)
       throws ODataApplicationException, ODataLibraryException;

   /**
    * Extracts the boundary of a multipart/mixed header.
    * See RFC 2046#5.1
    * @param contentType Content Type
    * @return boundary
    * @throws ODataApplicationException
    * @throws ODataLibraryException
    */
   public String extractBoundaryFromContentType(String contentType)
       throws ODataApplicationException, ODataLibraryException;
 }
