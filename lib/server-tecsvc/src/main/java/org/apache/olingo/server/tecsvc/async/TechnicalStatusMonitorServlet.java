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
package org.apache.olingo.server.tecsvc.async;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.tecsvc.ETagSupport;
import org.apache.olingo.server.tecsvc.MetadataETagSupport;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.processor.TechnicalActionProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalBatchProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalEntityProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalPrimitiveComplexProcessor;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicalStatusMonitorServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(TechnicalStatusMonitorServlet.class);

  @Override
  protected void service(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    try {
      if(TechnicalAsyncService.getInstance().isStatusMonitorResource(request)) {
        TechnicalAsyncService asyncService = TechnicalAsyncService.getInstance();
        asyncService.handle(request, response);
      }
    } catch (final Exception e) {
      LOG.error("Server Error", e);
      throw new ServletException(e);
    }
  }
}
