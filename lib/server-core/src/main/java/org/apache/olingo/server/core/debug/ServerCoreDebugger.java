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
package org.apache.olingo.server.core.debug;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.debug.DebugInformation;
import org.apache.olingo.server.api.debug.DebugSupport;
import org.apache.olingo.server.api.debug.RuntimeMeasurement;
import org.apache.olingo.server.api.uri.UriInfo;

public class ServerCoreDebugger {

  private final List<RuntimeMeasurement> runtimeInformation = new ArrayList<RuntimeMeasurement>();
  private final OData odata;

  private boolean isDebugMode = false;
  private DebugSupport debugSupport;
  private String debugFormat;

  public ServerCoreDebugger(OData odata) {
    this.odata = odata;
  }

  public void resolveDebugMode(HttpServletRequest request) {
    if (debugSupport != null) {
      // Should we read the parameter from the servlet here and ignore multiple parameters?
      debugFormat = request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER);
      if (debugFormat != null) {
        debugSupport.init(odata);
        isDebugMode = debugSupport.isUserAuthorized();
      }
    }
  }

  public ODataResponse createDebugResponse(final HttpServletRequest request, final Exception exception,
      final ODataRequest odRequest, final ODataResponse odResponse, UriInfo uriInfo,
      Map<String, String> serverEnvironmentVariables) {
    //Failsafe so we do not generate unauthorized debug messages
    if(!isDebugMode){
      return odResponse;
    }
    
    try {
      DebugInformation debugInfo =
          createDebugInformation(request, exception, odRequest, odResponse, uriInfo, serverEnvironmentVariables);

      return debugSupport.createDebugResponse(debugFormat, debugInfo);
    } catch (Exception e) {
      return createFailResponse();
    }
  }

  private ODataResponse createFailResponse() {
    ODataResponse odResponse = new ODataResponse();
    odResponse.setStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode());
    odResponse.setHeader(HttpHeader.CONTENT_TYPE, ContentType.TEXT_PLAIN.toContentTypeString());
    InputStream content = new ByteArrayInputStream("ODataLibrary: Could not assemble debug response.".getBytes());
    odResponse.setContent(content);
    return odResponse;
  }

  private DebugInformation createDebugInformation(final HttpServletRequest request, final Exception exception,
      final ODataRequest odRequest, final ODataResponse odResponse, UriInfo uriInfo,
      Map<String, String> serverEnvironmentVaribles) {
    DebugInformation debugInfo = new DebugInformation();
    debugInfo.setRequest(odRequest);
    debugInfo.setApplicationResponse(odResponse);

    debugInfo.setException(exception);

    debugInfo.setServerEnvironmentVariables(serverEnvironmentVaribles);

    debugInfo.setUriInfo(uriInfo);

    debugInfo.setRuntimeInformation(runtimeInformation);
    return debugInfo;
  }

  public int startRuntimeMeasurement(final String className, final String methodName) {
    if (isDebugMode) {
      int handleId = runtimeInformation.size();

      final RuntimeMeasurement measurement = new RuntimeMeasurement();
      measurement.setTimeStarted(System.nanoTime());
      measurement.setClassName(className);
      measurement.setMethodName(methodName);

      runtimeInformation.add(measurement);

      return handleId;
    } else {
      return 0;
    }
  }

  public void stopRuntimeMeasurement(final int handle) {
    if (isDebugMode && handle < runtimeInformation.size()) {
      long stopTime = System.nanoTime();
      RuntimeMeasurement runtimeMeasurement = runtimeInformation.get(handle);
      if (runtimeMeasurement != null) {
        runtimeMeasurement.setTimeStopped(stopTime);
      }
    }
  }

  public void setDebugSupportProcessor(DebugSupport debugSupport) {
    this.debugSupport = debugSupport;
  }

  public boolean isDebugMode() {
    return isDebugMode;
  }
}
