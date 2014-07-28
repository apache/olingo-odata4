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
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *  
 */
public class TomcatTestServer {
  private static final Logger LOG = LoggerFactory.getLogger(TomcatTestServer.class);

//  private static final int PORT_MIN = 19000;
//  private static final int PORT_MAX = 19200;
//  private static final int PORT_INC = 1;

  private final Tomcat tomcat;

  private TomcatTestServer(Tomcat tomcat) {
    this.tomcat = tomcat;
  }

  public static void main(String[] params) {
    try {
      LOG.trace("Start tomcat embedded server from main()");
      TomcatTestServer server = TomcatTestServer.init(9180)
          .addStaticContent("/stub/StaticService/V30/Static.svc/$metadata", "V30/metadata.xml")
          .addStaticContent("/stub/StaticService/V30/ActionOverloading.svc/$metadata",
              "V30/actionOverloadingMetadata.xml")
          .addStaticContent("/stub/StaticService/V30/OpenType.svc/$metadata", "V30/openTypeMetadata.xml")
          .addStaticContent("/stub/StaticService/V30/PrimitiveKeys.svc/$metadata", "V30/primitiveKeysMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/OpenType.svc/$metadata", "V40/openTypeMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/Demo.svc/$metadata", "V40/demoMetadata.xml")
          .addStaticContent("/stub/StaticService/V40/Static.svc/$metadata", "V40/metadata.xml")
          .start();
    } catch (Exception e) {
      throw new RuntimeException("Failed to start Tomcat server from main method.", e);
    }
  }

  public static class StaticContent extends HttpServlet {
    private final String uri;
    private final String resource;

    public StaticContent(String uri, String resource) {
      this.uri = uri;
      this.resource = resource;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

      StringHelper.Stream st;
      File resourcePath = new File(resource);
      if(resourcePath.exists() && resourcePath.isFile()) {
        FileInputStream fin = new FileInputStream(resourcePath);
        st = StringHelper.toStream(fin);
        LOG.info("Mapped uri '{}' to resource '{}'.", uri, resource);
        LOG.trace("Resource content {\n\n{}\n\n}", st.asString());
      } else {
        LOG.debug("Unable to load resource for path {} as stream.", uri);
        st = StringHelper.toStream("<html><head/><body>No resource for path found</body>");
      }
      resp.getOutputStream().write(st.asString().getBytes());
    }
  }

  private static TestServerBuilder builder;
  public static TestServerBuilder init(int port) {
    if(builder == null) {
      builder = new TestServerBuilder(port);
    }
    return builder;
  }

  public static class TestServerBuilder {
    private static final String TOMCAT_BASE_DIR = "TOMCAT_BASE_DIR";
    private static final String PROJECT_TARGET_DIR = "PROJECT_TARGET_DIR";
    private static final String PROJECT_WEB_APP_DIR = "PROJECT_WEB_APP_DIR";
    private static final String PROJECT_RESOURCES_DIR = "PROJECT_RESOURCES_DIR";

    private final Tomcat tomcat;
    private final File baseDir;
    private final File projectTarget;
    private TomcatTestServer server;
    private Properties properties;

    private TestServerBuilder(int fixedPort) {
      initializeProperties();
      //baseDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-test");
      projectTarget = getProjectTarget();
      // projectTarget == ...fit/target/test-classes
      baseDir = getFileForDirProperty(TOMCAT_BASE_DIR);
      if(!baseDir.exists() && !baseDir.mkdirs()) {
        throw new RuntimeException("Unable to create temporary test directory at {" + baseDir.getAbsolutePath() + "}");
      }
      //
      tomcat = new Tomcat();
      tomcat.setBaseDir(baseDir.getParentFile().getAbsolutePath());
      tomcat.setPort(fixedPort);
      tomcat.getHost().setAutoDeploy(true);
      tomcat.getHost().setAppBase(baseDir.getAbsolutePath());
      tomcat.getHost().setDeployOnStartup(true);
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

    public TestServerBuilder addWebApp() throws IOException {
      if(server != null) {
        return this;
      }

      File webAppProjectDir = getFileForDirProperty(PROJECT_WEB_APP_DIR);
      File webAppDir = new File(baseDir, webAppProjectDir.getName());
      FileUtils.deleteDirectory(webAppDir);
      if(!webAppDir.mkdirs()) {
        throw new RuntimeException("Unable to create temporary directory at {" + webAppDir.getAbsolutePath() + "}");
      }
      FileUtils.copyDirectory(webAppProjectDir, webAppDir);
      File libDir = new File(webAppDir, "WEB-INF/lib");
      File classesDir = new File(webAppDir, "WEB-INF/classes");
      String[] libsToExtract = libDir.list(new FilenameFilter() {
        @Override public boolean accept(File dir, String name) {
          return name.toLowerCase(Locale.ENGLISH).contains("olingo")
              && name.toLowerCase(Locale.ENGLISH).endsWith("jar");
        }
      });
      for (String lib : libsToExtract) {
        File libFile = new File(libDir, lib);
        extract(libFile, classesDir);
        FileUtils.forceDelete(libFile);
      }

      String[] libsToRemove = libDir.list(new FilenameFilter() {
        @Override public boolean accept(File dir, String name) {
          return
              (name.toLowerCase(Locale.ENGLISH).contains("tomcat")
              || name.toLowerCase(Locale.ENGLISH).contains("maven"))
              && name.toLowerCase(Locale.ENGLISH).endsWith("jar");
        }
      });
      for (String lib : libsToRemove) {
        FileUtils.forceDelete(new File(libDir, lib));
      }

      String contextPath = "/stub"; // contextFile.getName()
      tomcat.addWebapp(tomcat.getHost(), contextPath, webAppDir.getAbsolutePath());
      LOG.info("Webapp {} at context {}.", webAppDir.getName(), contextPath);

      return this;
    }

    private File getProjectTarget() {
      return getFileForDirProperty(PROJECT_TARGET_DIR);
    }

    private File getFileForDirProperty(String propertyName) {
      File targetFile = new File(properties.getProperty(propertyName));
      if(targetFile.exists() && targetFile.isDirectory()) {
        return targetFile;
      } else if(targetFile.mkdirs()) {
        return targetFile;
      }

      URL targetURL = Thread.currentThread().getContextClassLoader().getResource(targetFile.getPath());
      if(targetURL == null) {
        throw new RuntimeException("Project target was not found at '" +
            properties.getProperty(propertyName) + "'.");
      }
      return new File(targetURL.getFile());
    }

    public TestServerBuilder addServlet(final Class<? extends HttpServlet> factoryClass, String path) throws Exception {
      if(server != null) {
        return this;
      }
      String odataServlet = factoryClass.getName();
      HttpServlet httpServlet = (HttpServlet) Class.forName(odataServlet).newInstance();
      Context cxt = getContext();
      Tomcat.addServlet(cxt, odataServlet, httpServlet);
      cxt.addServletMapping(path, odataServlet);
      //
      LOG.info("Added servlet {} at context {}.", odataServlet, path);
      return this;
    }

    public TestServerBuilder addStaticContent(String uri, String resourceName) throws Exception {
      File targetResourcesDir = getFileForDirProperty(PROJECT_RESOURCES_DIR);
      String resource = new File(targetResourcesDir, resourceName).getAbsolutePath();
      LOG.info("Added static content from '{}' at uri '{}'.", resource, uri);
      StaticContent staticContent = new StaticContent(uri, resource);
      return addServlet(staticContent, String.valueOf(uri.hashCode()), uri);
    }

    public TestServerBuilder addServlet(HttpServlet httpServlet, String name, String path) throws Exception {
      if(server != null) {
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
      if(baseContext == null) {
        baseContext = tomcat.addContext("/", baseDir.getAbsolutePath());
      }
      return baseContext;
    }

    public TomcatTestServer start() throws LifecycleException {
      if(server != null) {
        return server;
      }
      tomcat.start();

      LOG.info("Started server at endpoint " + tomcat.getServer().getAddress());

//      tomcat.getServer().await();
      tomcat.getServer().getState();
      server = new TomcatTestServer(tomcat);
      return server;
    }
  }

//  public boolean start(final Class<? extends HttpServlet> factoryClass, final String context) {
//    try {
//      init(9080).addServlet(factoryClass, context).start();
//      return true;
//    } catch (Exception e) {
//      e.printStackTrace();
//      return false;
//    }
//  }

//  public void start(final Class<? extends HttpServlet> factoryClass) {
//    try {
//      for (int port = PORT_MIN; port <= PORT_MAX; port += PORT_INC) {
//        LifecycleState state = startInternal(factoryClass, port);
//        if(state == LifecycleState.STARTED) {
//          LOG.info("Tomcat in state :[" + state + "]");
//          break;
//        } else {
//          LOG.info("port is busy... " + port + " [" + state + "]");
//        }
//      }
//
//      if (!tomcat.getServer().getState().isAvailable()) {
//        throw new BindException("no free port in range of [" + PORT_MIN + ".." + PORT_MAX + "]");
//      }
//    } catch (final Exception e) {
//      LOG.error("server start failed", e);
//      throw new RuntimeException(e);
//    }
//  }

  public void stop() throws LifecycleException {
    if (tomcat.getServer() != null
        && tomcat.getServer().getState() != LifecycleState.DESTROYED) {
      if (tomcat.getServer().getState() != LifecycleState.STOPPED) {
        tomcat.stop();
      }
      tomcat.destroy();
    }
  }

  private static void extract(File jarFile, File destDir) throws IOException {
    JarFile jar = new JarFile(jarFile);
    Enumeration<JarEntry> enumEntries = jar.entries();
    while (enumEntries.hasMoreElements()) {
      JarEntry file = enumEntries.nextElement();
      File f = new File(destDir, file.getName());
      if (file.isDirectory()) {
        if(!f.exists() && !f.mkdir()) {
          throw new IOException("Unable to create directory at path '" + f.getAbsolutePath() + "'.");
        }
      } else {
        InputStream is = jar.getInputStream(file);
        FileOutputStream fos = new FileOutputStream(f);
        while (is.available() > 0) {
          fos.write(is.read());
        }
        fos.close();
        is.close();
      }
    }
  }
}