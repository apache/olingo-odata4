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
 * Supports the default debug case. Will always deliver a debug response if requested from the server.
 */
public class DefaultDebugSupport implements DebugSupport {

  private OData odata;

  @Override
  public void init(final OData odata) {
    this.odata = odata;
  }

  @Override
  public boolean isUserAuthorized() {
    return true;
  }

  @Override
  public ODataResponse createDebugResponse(final String debugFormat, final DebugInformation debugInfo) {
    // Check if debugFormat is supported by the library
    if (DebugSupport.ODATA_DEBUG_JSON.equalsIgnoreCase(debugFormat)
        || DebugSupport.ODATA_DEBUG_HTML.equalsIgnoreCase(debugFormat)
        || DebugSupport.ODATA_DEBUG_DOWNLOAD.equalsIgnoreCase(debugFormat)) {
      return odata.createDebugResponseHelper(debugFormat).createDebugResponse(debugInfo);
    } else {
      // Debug format is not supported by the library by default so in order to avoid an exception we will just give
      // back the original response from the application.
      return debugInfo.getApplicationResponse();
    }
  }

}
