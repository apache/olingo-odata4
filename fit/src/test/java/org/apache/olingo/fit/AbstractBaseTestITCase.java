/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit;

import org.apache.catalina.LifecycleException;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;
import org.apache.olingo.fit.server.TomcatTestServer;
import org.apache.olingo.server.tecsvc.TechnicalServlet;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public abstract class AbstractBaseTestITCase {

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(AbstractBaseTestITCase.class);

  protected abstract CommonODataClient<?> getClient();

  @BeforeClass
  public static void init()
      throws LifecycleException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    TomcatTestServer.init(9080)
        .addServlet(TechnicalServlet.class, "/odata-server-tecsvc/odata.svc/*")
        .addServlet(StaticContent.class, "/odata-server-tecsvc/v4.0/cs02/vocabularies/Org.OData.Core.V1.xml")
        .addServlet(MetadataContent.class, "/odata-metadata/$metadata")
        .addWebApp()
        .start();
  }

//  @AfterClass
//  public static void cleanUp() throws LifecycleException {
//    server.stop();
//  }

  protected void debugEntity(final Entity entity, final String message) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      try {
        getClient().getSerializer(ODataFormat.JSON).write(writer, entity);
      } catch (final ODataSerializerException e) {}
      writer.flush();
      LOG.debug(message + "\n{}", writer.toString());
    }
  }

  protected void debugEntitySet(final EntitySet entitySet, final String message) {
    if (LOG.isDebugEnabled()) {
      final StringWriter writer = new StringWriter();
      try {
        getClient().getSerializer(ODataFormat.JSON).write(writer, entitySet);
      } catch (final ODataSerializerException e) {}
      writer.flush();
      LOG.debug(message + "\n{}", writer.toString());
    }
  }

  protected void debugODataProperty(final CommonODataProperty property, final String message) {
    LOG.debug(message + "\n{}", property.toString());
  }

  protected void debugODataValue(final ODataValue value, final String message) {
    LOG.debug(message + "\n{}", value.toString());
  }

  protected void debugODataEntity(final CommonODataEntity entity, final String message) {
    if (LOG.isDebugEnabled()) {
      StringWriter writer = new StringWriter();
      try {
        getClient().getSerializer(ODataFormat.ATOM).write(writer, getClient().getBinder().getEntity(entity));
      } catch (final ODataSerializerException e) {}
      writer.flush();
      LOG.debug(message + " (Atom)\n{}", writer.toString());

      writer = new StringWriter();
      try {
        getClient().getSerializer(ODataFormat.JSON).write(writer, getClient().getBinder().getEntity(entity));
      } catch (final ODataSerializerException e) {}
      writer.flush();
      LOG.debug(message + " (JSON)\n{}", writer.toString());
    }
  }

  protected void debugInputStream(final InputStream input, final String message) {
    if (LOG.isDebugEnabled()) {
      try {
        LOG.debug(message + "\n{}", IOUtils.toString(input));
      } catch (IOException e) {
        LOG.error("Error writing stream", e);
      } finally {
        IOUtils.closeQuietly(input);
      }
    }
  }

  public static class StaticContent extends HttpServlet {
    private static final long serialVersionUID = -6663569573355398997L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.getOutputStream().write(IOUtils.toByteArray(
          Thread.currentThread().getContextClassLoader().getResourceAsStream("org-odata-core-v1.xml")));
    }
  }

  public static class MetadataContent extends HttpServlet {
    private static final long serialVersionUID = -6663569573355398997L;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      resp.getOutputStream().write(IOUtils.toByteArray(
          Thread.currentThread().getContextClassLoader().getResourceAsStream("metadata-ref.xml")));
    }
  }
}
