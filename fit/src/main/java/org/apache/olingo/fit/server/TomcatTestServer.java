/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.fit.server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

/**
 * Server for integration tests.
 */
public class TomcatTestServer {
  private static final Logger LOG = LoggerFactory.getLogger(TomcatTestServer.class);

  private final Tomcat tomcat;

  private TomcatTestServer(Tomcat tomcat) {
    this.tomcat = tomcat;
  }

  public static void main(String[] params) {
    try {
      LOG.trace("Start tomcat embedded server from main()");
      TestServerBuilder server = TomcatTestServer.init(9180)
          .addStaticContent("/stub/StaticService/V30/Static.svc/$metadata", "V30/metadata.xml")
          .addStaticContent("/stub/StaticService/V30/ActionOverloading.svc/$metadata",
              "V30/actionOverloadingMetadata.xml")
          .addStaticContent("/stub/StaticService/V30/OpenType.svc/$metadata", "V30/openTypeMetadata.xml")
          .addStaticContent("/stub/StaticService/V30/PrimitiveKeys.svc/$metadata", "V30/primitiveKeysMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/OpenType.svc/$metadata", "V40/openTypeMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/Demo.svc/$metadata", "V40/demoMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/Static.svc/$metadata", "V40/metadata.xml");

      boolean keepRunning = false;
      for (String param : params) {
        if (param.equalsIgnoreCase("keeprunning")) {
          keepRunning = true;
        } else if (param.equalsIgnoreCase("addwebapp")) {
          server.addWebApp();
        } else if (param.startsWith("port")) {
          server.atPort(extractPortParam(param));
        }
      }

      if (keepRunning) {
        LOG.info("...and keep server running.");
        server.startAndWait();
      } else {
        LOG.info("...and run as long as the thread is running.");
        server.start();
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to start Tomcat server from main method.", e);
    } catch (LifecycleException e) {
      throw new RuntimeException("Failed to start Tomcat server from main method.", e);
    }
  }

  public static int extractPortParam(String portParameter) {
    String[] portParam = portParameter.split("=");
    if (portParam.length == 2) {
      try {
        return Integer.parseInt(portParam[1]);
      } catch (NumberFormatException e) {
        throw new IllegalArgumentException("Port parameter (" + portParameter + ") could not be parsed.");
      }
    }
    throw new IllegalArgumentException("Port parameter (" + portParameter + ") could not be parsed.");
  }

  public static class StaticContent extends HttpServlet {
    private static final long serialVersionUID = 6850459331131987539L;
    private final String uri;
    private final String resource;

    public StaticContent(String uri, String resource) {
      this.uri = uri;
      this.resource = resource;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

      String result;
      File resourcePath = new File(resource);
      if (resourcePath.exists() && resourcePath.isFile()) {
        FileInputStream fin = new FileInputStream(resourcePath);
        result = IOUtils.toString(fin, "UTF-8");
        LOG.info("Mapped uri '{}' to resource '{}'.", uri, resource);
        LOG.trace("Resource content {\n\n{}\n\n}", result);
      } else {
        LOG.debug("Unable to load resource for path {} as stream.", uri);
        result = "<html><head/><body>No resource for path found</body>";
      }
      resp.getOutputStream().write(result.getBytes());
    }
  }

  private static TestServerBuilder builder;

  public static TestServerBuilder init(int port) {
    if (builder == null) {
      builder = new TestServerBuilder(port);
    }
    return builder;
  }

  public static class TestServerBuilder {
    private static final String TOMCAT_BASE_DIR = "TOMCAT_BASE_DIR";
    private static final String PROJECT_WEB_APP_DIR = "PROJECT_WEB_APP_DIR";
    private static final String PROJECT_RESOURCES_DIR = "PROJECT_RESOURCES_DIR";

    private final Tomcat tomcat;
    private final File baseDir;
    private TomcatTestServer server;
    private Properties properties;

    private TestServerBuilder(int fixedPort) {
      initializeProperties();
      //baseDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-test");
      baseDir = getFileForDirProperty(TOMCAT_BASE_DIR);
      if (!baseDir.exists() && !baseDir.mkdirs()) {
        throw new RuntimeException("Unable to create temporary test directory at {" + baseDir.getAbsolutePath() + "}");
      }
      //
      tomcat = new Tomcat();
      tomcat.setBaseDir(baseDir.getParentFile().getAbsolutePath());
      tomcat.setPort(fixedPort);
      tomcat.getHost().setAppBase(baseDir.getAbsolutePath());
      tomcat.getHost().setDeployOnStartup(true);
      tomcat.getConnector().setSecure(false);
      tomcat.setSilent(true);
      tomcat.addUser("odatajclient", "odatajclient");
      tomcat.addRole("odatajclient", "odatajclient");
    }

    private void initializeProperties() {
      InputStream propertiesFile =
          Thread.currentThread().getContextClassLoader().getResourceAsStream("tomcat-fit.properties");
      try {
        properties = new Properties();
        properties.load(propertiesFile);
      } catch (IOException e) {
        LOG.error("Unable to load properties for embedded tomcat test server.");
        throw new RuntimeException("Unable to load properties for embedded tomcat test server.");
      }
    }

    public void enableLogging(Level level) {
      tomcat.setSilent(false);
      try {
        Handler fileHandler = new FileHandler(tomcat.getHost().getAppBase() + "/catalina.out", true);
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(level);
        java.util.logging.Logger.getLogger("").addHandler(fileHandler);
      } catch (IOException e) {
        throw new RuntimeException("Unable to configure embedded tomcat logging.");
      }
    }

    public void atPort(int port) {
      tomcat.setPort(port);
    }

    public TestServerBuilder addWebApp() throws IOException {
      return addWebApp(true);
    }

    public TestServerBuilder addWebApp(boolean copy) throws IOException {
      if (server != null) {
        return this;
      }

      File webAppProjectDir = getFileForDirProperty(PROJECT_WEB_APP_DIR);
      final File webAppDir;
      if(copy) {
        webAppDir = new File(baseDir, webAppProjectDir.getName());
        FileUtils.deleteDirectory(webAppDir);
        if (!webAppDir.mkdirs()) {
          throw new RuntimeException("Unable to create temporary directory at {" + webAppDir.getAbsolutePath() + "}");
        }
        FileUtils.copyDirectory(webAppProjectDir, webAppDir);
      } else {
        webAppDir = webAppProjectDir;
      }

      String contextPath = "/stub";
      Context context = tomcat.addWebapp(tomcat.getHost(), contextPath, webAppDir.getAbsolutePath());
      context.setLoader(new WebappLoader(Thread.currentThread().getContextClassLoader()));
      LOG.info("Webapp {} at context {}.", webAppDir.getName(), contextPath);

      return this;
    }

    private File getFileForDirProperty(String propertyName) {
      File targetFile = new File(properties.getProperty(propertyName));
      if (targetFile.exists() && targetFile.isDirectory()) {
        return targetFile;
      } else if (targetFile.mkdirs()) {
        return targetFile;
      }

      URL targetURL = Thread.currentThread().getContextClassLoader().getResource(targetFile.getPath());
      if (targetURL == null) {
        throw new RuntimeException("Project target was not found at '" +
            properties.getProperty(propertyName) + "'.");
      }
      return new File(targetURL.getFile());
    }

    public TestServerBuilder addServlet(final Class<? extends HttpServlet> factoryClass, String path)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException {
      if (server != null) {
        return this;
      }
      String servletClassname = factoryClass.getName();
      HttpServlet httpServlet = (HttpServlet) Class.forName(servletClassname).newInstance();
      Context cxt = getContext();
      String randomServletId = UUID.randomUUID().toString();
      Tomcat.addServlet(cxt, randomServletId, httpServlet);
      cxt.addServletMapping(path, randomServletId);
      //
      LOG.info("Added servlet {} at context {} (mapping id={}).", servletClassname, path, randomServletId);
      return this;
    }

    public TestServerBuilder addStaticContent(String uri, String resourceName) throws IOException {
      File targetResourcesDir = getFileForDirProperty(PROJECT_RESOURCES_DIR);
      String resource = new File(targetResourcesDir, resourceName).getAbsolutePath();
      LOG.info("Added static content from '{}' at uri '{}'.", resource, uri);
      StaticContent staticContent = new StaticContent(uri, resource);
      return addServlet(staticContent, String.valueOf(uri.hashCode()), uri);
    }

    public TestServerBuilder addServlet(HttpServlet httpServlet, String path) throws IOException {
      String name = UUID.randomUUID().toString();
      return addServlet(httpServlet, name, path);
    }

    public TestServerBuilder addServlet(HttpServlet httpServlet, String name, String path) throws IOException {
      if (server != null) {
        return this;
      }
      Context cxt = getContext();
      Tomcat.addServlet(cxt, name, httpServlet);
      cxt.addServletMapping(path, name);
      //
      LOG.info("Added servlet {} at context {}.", name, path);
      return this;
    }

    private Context baseContext = null;

    private Context getContext() {
      if (baseContext == null) {
        baseContext = tomcat.addContext("/", baseDir.getAbsolutePath());
      }
      return baseContext;
    }

    public TomcatTestServer start() throws LifecycleException {
      if (server != null) {
        return server;
      }
      tomcat.start();

      LOG.info("Started server at endpoint "
          + tomcat.getServer().getAddress() + ":" + tomcat.getConnector().getPort() +
          " (with base dir: " + baseDir.getAbsolutePath());

      server = new TomcatTestServer(tomcat);
      return server;
    }

    public void startAndWait() throws LifecycleException {
      start();
      tomcat.getServer().await();
    }
  }

  public void stop() throws LifecycleException {
    if (tomcat.getServer() != null
        && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
      if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
        tomcat.stop();
      }
      tomcat.destroy();
    }
  }
}