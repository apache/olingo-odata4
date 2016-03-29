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

  String ODATA_DEBUG_QUERY_PARAMETER = "odata-debug";
  String ODATA_DEBUG_JSON = "json";
  String ODATA_DEBUG_HTML = "html";
  String ODATA_DEBUG_DOWNLOAD = "download";

  /**
   * Initializes the debug support implementation.
   * Is called before {@link #isUserAuthorized()} and {@link #createDebugResponse(String, DebugInformation)}.
   * @param odata related OData/Olingo service factory
   */
  void init(OData odata);

  /**
   * Ensures that the user that requested the debug output is authorized to see this output.
   * @return true if the current user is authorized
   */
  boolean isUserAuthorized();

  /**
   * Creates a debug response and delivers it back to the Olingo library.
   * This method MUST NEVER throw an exception.
   * @param debugFormat the value of the odata-debug query parameter
   * @param debugInfo all necessary information to construct debug output
   * @return a new debug response which will be sent to the client
   */
  ODataResponse createDebugResponse(String debugFormat, DebugInformation debugInfo);
}
