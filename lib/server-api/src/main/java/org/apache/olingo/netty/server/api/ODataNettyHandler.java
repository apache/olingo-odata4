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
package org.apache.olingo.netty.server.api;

import java.util.Map;

import org.apache.olingo.server.api.processor.Processor;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public interface ODataNettyHandler {

  /**
   * <p>Processes a NettyRequest as an OData request.</p>
   * <p>This includes URI parsing, content negotiation, dispatching the request
   * to a specific custom processor implementation for handling data and
   * creating the serialized content for the response object.</p>
   * @param request - must be a HTTP OData request
   * @param response - HTTP OData response
   */
  void processNettyRequest(HttpRequest request, HttpResponse response, Map<String, String> requestParameters);
  
  /**
   * <p>Registers additional custom processor implementations for handling OData requests.</p>
   * <p>If request processing requires a processor that is not registered then a
   * "not implemented" exception will happen.</p>
   */
  void register(Processor processor);
  
}
