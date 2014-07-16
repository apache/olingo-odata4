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
package org.apache.olingo.server.api;

import org.apache.olingo.server.api.processor.Processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handels http requests as OData requests.
 */
public interface ODataHttpHandler {

  /**
   * Process an OData request. This includes uri parsing, content negotiation, dispatching the request to a specific
   * custom processor implementation for handling data and creating the serialized content for the response object.
   * @param request - must be a http OData request
   * @param response - http OData response
   */
  void process(HttpServletRequest request, HttpServletResponse response);

  /**
   * Register additional custom processor implementations for handling OData requests. If a request processing requires
   * a processor which is not registered then an not implemented exception will happen.
   */
  void register(Processor processor);

}
