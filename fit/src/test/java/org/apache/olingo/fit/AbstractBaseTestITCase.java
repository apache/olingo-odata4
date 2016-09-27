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
package org.apache.olingo.fit;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.fit.server.TomcatTestServer;
import org.apache.olingo.server.tecsvc.TechnicalServlet;
import org.apache.olingo.server.tecsvc.async.TechnicalStatusMonitorServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractBaseTestITCase {

  protected abstract ODataClient getClient();

  private static TomcatTestServer server;

  @BeforeClass
  public static void init()
      throws LifecycleException, IOException, 
          InstantiationException, IllegalAccessException, 
          ClassNotFoundException, ServletException {
    server = TomcatTestServer.init(9080)
        .addServlet(TechnicalServlet.class, "/odata-server-tecsvc/odata.svc/*")
        .addServlet(TechnicalStatusMonitorServlet.class, "/odata-server-tecsvc/status/*")
        .addAuthServlet(TechnicalServlet.class, "/odata-server-tecsvc/auth", "/*")
        .addServlet(StaticContent.create("org-odata-core-v1.xml"),
            "/odata-server-tecsvc/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml")
        .addWebApp(false)
        .start();
  }

  @AfterClass
  public static void cleanUp() throws LifecycleException {
    server.invalidateAllSessions();
  }

  public static class StaticContent extends HttpServlet {
    private static final long serialVersionUID = -6663569573355398997L;
    private final String resourceName;

    public StaticContent(final String resourceName) {
      this.resourceName = resourceName;
    }

    public static HttpServlet create(final String resourceName) {
      return new StaticContent(resourceName);
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
        IOException {
      resp.getOutputStream().write(IOUtils.toByteArray(
          Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)));
    }
  }
}