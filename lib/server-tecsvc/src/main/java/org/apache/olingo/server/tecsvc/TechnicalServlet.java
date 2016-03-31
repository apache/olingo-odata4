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
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edmx.EdmxReferenceInclude;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.debug.DefaultDebugSupport;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.processor.TechnicalActionProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalBatchProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalEntityProcessor;
import org.apache.olingo.server.tecsvc.processor.TechnicalPrimitiveComplexProcessor;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TechnicalServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private static final Logger LOG = LoggerFactory.getLogger(TechnicalServlet.class);
  /**
   * <p>ETag for the service document and the metadata document</p>
   * <p>We use the same field for service-document and metadata-document ETags.
   * It must change whenever the corresponding document changes.
   * We don't know when someone changed the EDM in a way that changes one of these
   * documents, but we do know that the EDM is defined completely in code and that
   * therefore any change must be deployed, resulting in re-loading of this class,
   * giving this field a new and hopefully unique value.</p>
   */
  private static final String metadataETag = "W/\"" + UUID.randomUUID() + "\"";

  @Override
  protected void service(final HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      OData odata = OData.newInstance();
      EdmxReference reference = new EdmxReference(URI.create("../v4.0/cs02/vocabularies/Org.OData.Core.V1.xml"));
      reference.addInclude(new EdmxReferenceInclude("Org.OData.Core.V1", "Core"));
      final ServiceMetadata serviceMetadata = odata.createServiceMetadata(
          new EdmTechProvider(),
          Collections.singletonList(reference),
          new MetadataETagSupport(metadataETag));

      HttpSession session = request.getSession(true);
      DataProvider dataProvider = (DataProvider) session.getAttribute(DataProvider.class.getName());
      if (dataProvider == null) {
        dataProvider = new DataProvider(odata, serviceMetadata.getEdm());
        session.setAttribute(DataProvider.class.getName(), dataProvider);
        LOG.info("Created new data provider.");
      }

      ODataHttpHandler handler = odata.createHandler(serviceMetadata);
      // Register processors.
      handler.register(new TechnicalEntityProcessor(dataProvider, serviceMetadata));
      handler.register(new TechnicalPrimitiveComplexProcessor(dataProvider, serviceMetadata));
      handler.register(new TechnicalActionProcessor(dataProvider, serviceMetadata));
      handler.register(new TechnicalBatchProcessor(dataProvider));
      // Register helpers.
      handler.register(new ETagSupport());
      handler.register(new DefaultDebugSupport());
      // Process the request.
      handler.process(request, response);
    } catch (final RuntimeException e) {
      LOG.error("Server Error", e);
      throw new ServletException(e);
    }
  }
}
