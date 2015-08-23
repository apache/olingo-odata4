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
package org.apache.olingo.jpa.api;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.jpa.api.exception.ODataJPAException;
import org.apache.olingo.jpa.api.factory.ODataJPAFactory;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;

/**
 * The class {@link org.apache.olingo.jpa.api.ODataJPAServlet} is an abstract Servlet that
 * <ul>
 * <li> Creates an instance of Edm Provider</li>
 * <li> Creates an instance of OData processor</li>
 * <li> Handles OData requests and response</li>
 * </ul>
 * 
 * <p>Applications exposing JPA application as OData application can extend this servlet and should implement the
 * abstract method <b>initializeODataJPAContext</b>
 * 
 * <p>Applications can pass the persistence unit name which needs to be transformed into OData service as
 * a value for servlet's init parameter - <b>persitence.unit</b> in Web application descriptor (web.xml)</p>
 * 
 * <p>
 * Applications can also pass their own ODataJPAProcessor by overriding the method getODataJPAService in this servlet
 * and return an instance of {@link org.apache.olingo.jpa.api.ODataJPAService} </p>
 */
public abstract class ODataJPAServlet extends HttpServlet {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private static final String PERSISTENCE_UNIT_NAME = "persistence.unit";
  private static final String ODATA_JPA_TRANSACTION = "odata.jpa.transaction";
  private String persistenceUnitName;
  private String transaction;
  private ODataJPAService odataJPAService = null;
  private ODataJPAContext odataJPAContext = null;
  private ODataJPAFactory odataJPAFactory = null;

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    setPersistenceUnitName(config.getInitParameter(PERSISTENCE_UNIT_NAME));
    transaction = config.getInitParameter(ODATA_JPA_TRANSACTION);
    odataJPAFactory = ODataJPAFactory.newInstance();
    try {
      odataJPAService = getODataJPAService();
    } catch (ODataJPAException e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected final void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    OData odata = OData.newInstance();
    ServiceMetadata edm =
        odata.createServiceMetadata(odataJPAService.getODataJPAEdmProvider(), new ArrayList<EdmxReference>());
    ODataHttpHandler handler = odata.createHandler(edm);
    handler.register(odataJPAService.getODataJPAProcessor());
    handler.process(req, resp);
  }

  protected ODataJPAService getODataJPAService() throws ODataJPAException {
    odataJPAService = new ODataJPAService();
    odataJPAContext = getODataJPAContext();
    initializeODataJPAContext(odataJPAContext);
    odataJPAService.setODataJPAEdmProvider(odataJPAFactory.getODataJPAEdmProvider(persistenceUnitName));
    odataJPAService.setODataJPAProcessor(odataJPAFactory.getODataJPAProcessor(odataJPAContext));
    return odataJPAService;
  }

  /**
   * @return the persistencUnitName
   */
  protected String getPersistenceUnitName() {
    return persistenceUnitName;
  }

  /**
   * @param persistencUnitName the persistencUnitName to set
   */
  private void setPersistenceUnitName(String persistencUnitName) {
    this.persistenceUnitName = persistencUnitName;
  }

  protected abstract void initializeODataJPAContext(ODataJPAContext odataJPAContext2);

  private ODataJPAContext getODataJPAContext() {
    return odataJPAFactory.newODataJPAContext();
  }
}
