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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.ODataHandler;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;

public class ODataHandlerImpl implements ODataHandler {

  private Edm edm;
  private ODataServer server;

  public ODataHandlerImpl(ODataServer server, Edm edm) {
    this.edm = edm;
    this.server = server;
  }

  @Override
  public void process(HttpServletRequest request, HttpServletResponse response) {
    ODataRequest odRequest = createODataRequest(request);
    ODataResponse odResponse;
   
    //        odResponse = process(odRequest);
//    convertToHttp(response, odResponse);
  }

  private ODataRequest createODataRequest(HttpServletRequest request) {
    try {
      ODataRequest odRequest = new ODataRequest();

      odRequest.setBody(request.getInputStream());
      odRequest.setHeaders(extractHeaders(request));
      odRequest.setQueryParameters(extractQueryParameters(request.getQueryString()));
      odRequest.setMethod(HttpMethod.valueOf(request.getMethod()));

      return odRequest;
    } catch (Exception e) {
      throw new ODataRuntimeException(e);
    }
  }

  public void processx(HttpServletRequest request, HttpServletResponse response) {
    try {
      InputStream responseEntity = null;
      if (request.getPathInfo().contains("$metadata")) {
        ODataSerializer serializer = server.createSerializer(ODataFormat.XML);
        responseEntity = serializer.metadataDocument(edm);
      } else {
        ODataSerializer serializer = server.createSerializer(ODataFormat.JSON);
        responseEntity = serializer.serviceDocument(edm, "http//:root");
      }

      response.setStatus(200);
      response.setContentType("application/json");

      if (responseEntity != null) {
        ServletOutputStream out = response.getOutputStream();
        int curByte = -1;
        if (responseEntity instanceof InputStream) {
          while ((curByte = ((InputStream) responseEntity).read()) != -1) {
            out.write((char) curByte);
          }
          ((InputStream) responseEntity).close();
        }

        out.flush();
        out.close();
      }

    } catch (Exception e) {
      throw new ODataRuntimeException(e);
    }
  }

  private Map<String, String> extractQueryParameters(final String queryString) {
    Map<String, String> queryParametersMap = new HashMap<String, String>();
    if (queryString != null) {
      // At first the queryString will be decoded.
      List<String> queryParameters = Arrays.asList(Decoder.decode(queryString).split("\\&"));
      for (String param : queryParameters) {
        int indexOfEqualSign = param.indexOf("=");
        if (indexOfEqualSign < 0) {
          queryParametersMap.put(param, "");
        } else {
          queryParametersMap.put(param.substring(0, indexOfEqualSign), param.substring(indexOfEqualSign + 1));
        }
      }
    }
    return queryParametersMap;
  }

  private Map<String, List<String>> extractHeaders(final HttpServletRequest req) {
    Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
    for (Enumeration<String> headerNames = req.getHeaderNames(); headerNames.hasMoreElements();) {
      String headerName = headerNames.nextElement();
      List<String> headerValues = new ArrayList<String>();
      for (Enumeration<String> headers = req.getHeaders(headerName); headers.hasMoreElements();) {
        String value = headers.nextElement();
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
