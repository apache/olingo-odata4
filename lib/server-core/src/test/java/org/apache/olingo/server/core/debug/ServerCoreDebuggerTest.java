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

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.debug.DebugInformation;
import org.apache.olingo.server.api.debug.DebugSupport;
import org.apache.olingo.server.api.debug.DefaultDebugSupport;
import org.junit.Before;
import org.junit.Test;

public class ServerCoreDebuggerTest {

  private final OData odata = OData.newInstance();
  private ServerCoreDebugger debugger;

  @Before
  public void setupDebugger() {
    debugger = new ServerCoreDebugger(odata);
    DebugSupport processor = mock(DebugSupport.class);
    when(processor.isUserAuthorized()).thenReturn(true);
    when(processor.createDebugResponse(anyString(), any(DebugInformation.class)))
        .thenThrow(new ODataRuntimeException("Test"));
    debugger.setDebugSupportProcessor(processor);
  }

  @Test
  public void standardIsDebugModeIsFalse() {
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeNoDebugSupportProcessor() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);

    ServerCoreDebugger localDebugger = new ServerCoreDebugger(odata);
    localDebugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeNullParameter() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(null);
    debugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void resolveDebugModeJsonNotAuthorized() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);

    DebugSupport debugSupportMock = mock(DebugSupport.class);
    when(debugSupportMock.isUserAuthorized()).thenReturn(false);

    ServerCoreDebugger localDebugger = new ServerCoreDebugger(odata);
    localDebugger.setDebugSupportProcessor(debugSupportMock);

    localDebugger.resolveDebugMode(request);
    assertFalse(debugger.isDebugMode());
  }

  @Test
  public void failResponse() throws IOException {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);
    debugger.resolveDebugMode(request);
    ODataResponse debugResponse = debugger.createDebugResponse(null, null, null, null, null);
    assertEquals(HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), debugResponse.getStatusCode());
    assertEquals("ODataLibrary: Could not assemble debug response.", IOUtils.toString(debugResponse.getContent()));
  }

  @Test
  public void noDebugModeCreateDebugResponseCallMustDoNothing() {
    ODataResponse odResponse = new ODataResponse();
    ODataResponse debugResponse = debugger.createDebugResponse(null, odResponse, null, null, null);

    assertEquals(odResponse, debugResponse);
  }

  @Test
  public void runtimeMeasurement() throws Exception {
    ServerCoreDebugger defaultDebugger = new ServerCoreDebugger(odata);
    defaultDebugger.setDebugSupportProcessor(new DefaultDebugSupport());
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_JSON);
    defaultDebugger.resolveDebugMode(request);

    final int handle = defaultDebugger.startRuntimeMeasurement("someClass", "someMethod");
    defaultDebugger.stopRuntimeMeasurement(handle);
    assertEquals(0, handle);

    assertThat(IOUtils.toString(defaultDebugger.createDebugResponse(null, null, null, null, null).getContent()),
        allOf(containsString("\"runtime\""), containsString("\"someClass\""), containsString("\"someMethod\""),
            containsString("]}}")));

    request = mock(HttpServletRequest.class);
    when(request.getParameter(DebugSupport.ODATA_DEBUG_QUERY_PARAMETER)).thenReturn(DebugSupport.ODATA_DEBUG_HTML);
    defaultDebugger.resolveDebugMode(request);
    assertThat(IOUtils.toString(defaultDebugger.createDebugResponse(null, null, null, null, null).getContent()),
        allOf(containsString(">Runtime<"), containsString(">someClass<"), containsString(">someMethod("),
            containsString("</html>")));
  }
}
