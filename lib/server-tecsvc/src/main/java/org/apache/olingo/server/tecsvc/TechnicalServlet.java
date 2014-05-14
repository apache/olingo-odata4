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
package org.apache.olingo.server.tecsvc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.ODataHandler;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicalServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(TechnicalServlet.class);

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    LOG.debug("ReferenceServlet:service() called");

    ODataServer server = ODataServer.newInstance();
    Edm edm = server.createEdm(new EdmTechProvider());

    ODataHandler handler = server.createHandler(edm);
    handler.process(req, resp);
  }
}
