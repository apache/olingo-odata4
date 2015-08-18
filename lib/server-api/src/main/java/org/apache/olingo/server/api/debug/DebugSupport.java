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
package org.apache.olingo.server.api.debug;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataResponse;

/**
 * Register this interface to add debug support to your service.
 */
public interface DebugSupport {

  public static final String ODATA_DEBUG_QUERY_PARAMETER = "odata-debug";
  public static final String ODATA_DEBUG_JSON = "json";
  public static final String ODATA_DEBUG_HTML = "html";
  public static final String ODATA_DEBUG_DOWNLOAD = "download";

  /**
   * Initializes the debug support implementation. Is called before {@link #isUserAuthorized()} and
   * {@link #createDebugResponse(String, DebugInformation)}
   * @param odata
   */
  void init(OData odata);

  /**
   * This method is called to make sure that the user that requested the debug output is authorized to see this output.
   * @return true if the current user is authorized
   */
  boolean isUserAuthorized();

  /**
   * This method should create a debug response and deliver it back to the Olingo library. This method MUST NEVER throw
   * an exception.
   * @param debugFormat which is requested via the odata-debug query parameter
   * @param request object which was send to the server
   * @param response object which was filled by the application
   * @param exception which has been thrown. Might be null in case there was no exception
   * @return a new debug response which will be send to the client
   */
  ODataResponse createDebugResponse(String debugFormat, DebugInformation debugInfo);

}
