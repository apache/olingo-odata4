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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.server.api.ODataHandler;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;

public class ODataHandlerImpl implements ODataHandler {

  private EdmProvider edmProvider;
  private ODataServer server;
  
  public ODataHandlerImpl(ODataServer server, EdmProvider edmProvider) {
    this.edmProvider = edmProvider;
    this.server = server;
  }

  @Override
  public void process(HttpServletRequest request, HttpServletResponse response) {
    try {
      EdmProviderImpl edm = new EdmProviderImpl(edmProvider);

      InputStream responseEntity = null;
      if (request.getPathInfo().contains("$metadata")) {
        ODataSerializer serializer = server.getSerializer(ODataFormat.XML);
        responseEntity = serializer.metadataDocument(edm);
      } else {
        ODataSerializer serializer = server.getSerializer(ODataFormat.JSON);
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
}
