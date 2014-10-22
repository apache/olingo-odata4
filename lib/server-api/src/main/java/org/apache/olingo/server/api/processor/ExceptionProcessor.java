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

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ODataServerError;

/**
 * Processor which is called if any exception occurs inside the library or another processor.
 */
public interface ExceptionProcessor extends Processor {

  /**
   * Processes an exception. MUST NOT throw an exception!
   * @param request              the request
   * @param response             the response
   * @param serverError          the server error
   * @param requestedContentType the requested format for the error message
   */
  public void processException(ODataRequest request, ODataResponse response, ODataServerError serverError,
      ContentType requestedContentType);
}
