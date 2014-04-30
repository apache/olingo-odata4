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
package org.apache.olingo.server.core.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.ODataServerImpl;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;

public class ODataServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private HttpServletResponse resp;

  @Override
  protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    this.resp = resp;

    EdmProvider provider = createEdmProvider();
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    ODataServerImpl impl = new ODataServerImpl();
    InputStream responseEntity = null;
    if (req.getPathInfo().contains("$metadata")) {
      ODataSerializer serializer = impl.getSerializer(ODataFormat.XML);
      responseEntity = serializer.metadataDocument(edm);
    } else {
      ODataSerializer serializer = impl.getSerializer(ODataFormat.JSON);
      responseEntity = serializer.serviceDocument(edm, "http//:root");
    }
    sendResponse(responseEntity);
  }

  protected EdmProvider createEdmProvider() {
    return null;
  }

  // TODO: check throws
  private void sendResponse(final Object entity) throws IOException {
    resp.setStatus(200);
    resp.setContentType("application/json");
    if (entity != null) {
      ServletOutputStream out = resp.getOutputStream();
      int curByte = -1;
      if (entity instanceof InputStream) {
        while ((curByte = ((InputStream) entity).read()) != -1) {
          out.write((char) curByte);
        }
        ((InputStream) entity).close();
      } else if (entity instanceof String) {
        String body = (String) entity;
        out.write(body.getBytes("utf-8"));
      }

      out.flush();
      out.close();
    }
  }

}
