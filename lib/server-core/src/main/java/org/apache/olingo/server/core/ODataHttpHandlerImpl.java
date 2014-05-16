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
package org.apache.olingo.server.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ODataServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ODataHttpHandlerImpl implements ODataHttpHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ODataHttpHandlerImpl.class);

  private Edm edm;
  private ODataServer server;

  public ODataHttpHandlerImpl(final ODataServer server, final Edm edm) {
    this.edm = edm;
    this.server = server;
  }

  @Override
  public void process(final HttpServletRequest request, final HttpServletResponse response) {
    ODataRequest odRequest = createODataRequest(request);

    ODataHandler handler = new ODataHandler(server, edm);
    ODataResponse odResponse = handler.process(odRequest);
    convertToHttp(response, odResponse);
  }

  private void convertToHttp(final HttpServletResponse response, final ODataResponse odResponse) {
    response.setStatus(odResponse.getStatusCode());

    for (Entry<String, String> entry : odResponse.getHeaders().entrySet()) {
      response.setHeader(entry.getKey(), entry.getValue());
    }

    InputStream in = odResponse.getContent();
    try {
      byte[] buffer = new byte[1024];
      int bytesRead = 0;

      do {
        bytesRead = in.read(buffer, 0, buffer.length);
        response.getOutputStream().write(buffer, 0, bytesRead);
      } while (bytesRead == buffer.length);

      response.getOutputStream().flush();
    } catch (IOException e) {
      LOG.error(e.getMessage(), e);
      throw new ODataRuntimeException(e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          throw new ODataRuntimeException(e);
        }
      }
    }
  }

  private ODataRequest createODataRequest(final HttpServletRequest httpRequest) {
    try {
      ODataRequest odRequest = new ODataRequest();

      odRequest.setBody(httpRequest.getInputStream());
      odRequest.setHeaders(extractHeaders(httpRequest));
      odRequest.setMethod(HttpMethod.valueOf(httpRequest.getMethod()));

      // request uri string
      fillRequestUri(odRequest, httpRequest, 0);

      return odRequest;
    } catch (Exception e) {
      throw new ODataRuntimeException(e);
    }
  }

  static void fillRequestUri(ODataRequest odRequest, final HttpServletRequest httpRequest, int split) {

    String rawRequestUri = httpRequest.getRequestURL().toString();

    String rawODataPath;
    if (!"".equals(httpRequest.getServletPath())) {
      int beginIndex;
      beginIndex = rawRequestUri.indexOf(httpRequest.getServletPath());
      beginIndex += httpRequest.getServletPath().length();
      rawODataPath = rawRequestUri.substring(beginIndex);
    } else if (!"".equals(httpRequest.getContextPath())) {
      int beginIndex;
      beginIndex = rawRequestUri.indexOf(httpRequest.getContextPath());
      beginIndex += httpRequest.getContextPath().length();
      rawODataPath = rawRequestUri.substring(beginIndex);
    } else {
      rawODataPath = httpRequest.getRequestURI();
    }

    for (int i = 0; i < split; i++) {
      int e = rawODataPath.indexOf("/", 1);
      rawODataPath = rawODataPath.substring(e);
    }

    String rawBaseUri = rawRequestUri.substring(0, rawRequestUri.length() - rawODataPath.length());

    odRequest.setRawQueryPath(httpRequest.getQueryString());
    odRequest.setRawRequestUri(rawRequestUri
        + (httpRequest.getQueryString() == null ? "" : "?" + httpRequest.getQueryString()));
    odRequest.setRawODataPath(rawODataPath);
    odRequest.setRawBaseUri(rawBaseUri);
  }

  private Map<String, List<String>> extractHeaders(final HttpServletRequest req) {
    Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();

    for (Enumeration<?> headerNames = req.getHeaderNames(); headerNames.hasMoreElements();) {
      String headerName = (String) headerNames.nextElement();
      List<String> headerValues = new ArrayList<String>();
      for (Enumeration<?> headers = req.getHeaders(headerName); headers.hasMoreElements();) {
        String value = (String) headers.nextElement();
        headerValues.add(value);
      }
      if (requestHeaders.containsKey(headerName)) {
        requestHeaders.get(headerName).addAll(headerValues);
      } else {
        requestHeaders.put(headerName, headerValues);
      }
    }
    return requestHeaders;
  }
}
