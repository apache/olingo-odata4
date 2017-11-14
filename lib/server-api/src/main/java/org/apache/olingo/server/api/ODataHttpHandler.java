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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.debug.DebugSupport;
import org.apache.olingo.server.api.etag.CustomETagSupport;
import org.apache.olingo.server.api.serializer.CustomContentTypeSupport;

/**
 * Handles HTTP requests as OData requests.
 */
public interface ODataHttpHandler extends ODataHandler {

  /**
   * <p>Processes a HttpServletRequest as an OData request.</p>
   * <p>This includes URI parsing, content negotiation, dispatching the request
   * to a specific custom processor implementation for handling data and
   * creating the serialized content for the response object.</p>
   * @param request - must be a HTTP OData request
   * @param response - HTTP OData response
   */
  void process(HttpServletRequest request, HttpServletResponse response);
  
  /**
   * Sets the split parameter which is used for service resolution.
   * @param split the number of path segments reserved for service resolution; default is 0
   */
  void setSplit(int split);


  /**
   * Registers the debug support handler.
   * @param debugSupport handler to register
   */
  void register(DebugSupport debugSupport);

  /**
   * Registers a service implementation for modifying the standard list of supported
   * content types.
   * @see CustomContentTypeSupport
   */
  void register(CustomContentTypeSupport customContentTypeSupport);

  /**
   * Registers support for concurrency control for certain entity sets.
   * @param customETagSupport handler to register
   */
  void register(CustomETagSupport customETagSupport);
}
