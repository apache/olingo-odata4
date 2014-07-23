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

import javax.naming.directory.DirContext;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;
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

  private static TestServerBuilder builder;
  public static TestServerBuilder init(int port) {
    if(builder == null) {
      builder = new TestServerBuilder(port);
    }
    return builder;
  }

  public static class TestServerBuilder {
    private final Tomcat tomcat;
    private final File baseDir;
    private TomcatTestServer server;

    private TestServerBuilder(int fixedPort) {
      tomcat = new Tomcat();
      tomcat.setPort(fixedPort);
      //baseDir = new File(System.getProperty("java.io.tmpdir"), "tomcat-test");
      File projectTarget = new File(Thread.currentThread().getContextClassLoader().getResource(".").getFile());
      // projectTarget == ...fit/target/test-classes
      baseDir = new File(projectTarget, "../emb-tom-fit");
      if(!baseDir.exists() && !baseDir.mkdirs()) {
        throw new RuntimeException("Unable to create temporary test directory at {" + baseDir.getAbsolutePath() + "}");
      }
      tomcat.getHost().setAutoDeploy(true);
      tomcat.getHost().setAppBase(baseDir.getAbsolutePath());
      tomcat.setBaseDir(baseDir.getParentFile().getAbsolutePath());
      tomcat.getHost().setDeployOnStartup(true);
      //   <user name="odatajclient" password="odatajclient" roles="odatajclient"/>
      tomcat.addUser("odatajclient", "odatajclient");
      tomcat.addRole("odatajclient", "odatajclient");
    }

    public TestServerBuilder addWebApp() throws IOException {
      if(server != null) {
        return this;
      }

      File projectTarget = new File(Thread.currentThread().getContextClassLoader().getResource(".").getFile());
      File webAppProjectDir = new File(projectTarget, "../olingo-fit-0.1.0-SNAPSHOT");
      File webAppDir = new File(baseDir, webAppProjectDir.getName());
      FileUtils.deleteDirectory(webAppDir);
      if(!webAppDir.mkdirs()) {
        throw new RuntimeException("Unable to create temporary directory at {" + webAppDir.getAbsolutePath() + "}");
      }
      FileUtils.copyDirectory(webAppProjectDir, webAppDir);
      File libDir = new File(webAppDir, "WEB-INF/lib");
      File classesDir = new File(webAppDir, "WEB-INF/classes");
      String[] libs = new String[]{"olingo-client-proxy-0.1.0-SNAPSHOT.jar",
          "olingo-commons-api-0.1.0-SNAPSHOT.jar",
          "olingo-commons-core-0.1.0-SNAPSHOT.jar"};
      for (String lib : libs) {
        File libFile = new File(libDir, lib);
        extract(libFile, classesDir);
        FileUtils.forceDelete(libFile);
      }

      String contextPath = "/stub"; // contextFile.getName()
      tomcat.addWebapp(tomcat.getHost(), contextPath, webAppDir.getAbsolutePath());
      LOG.info("Webapp {} at context {}.", webAppDir.getName(), contextPath);

      return this;
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
    JarFile jar = new java.util.jar.JarFile(jarFile);
    java.util.Enumeration enumEntries = jar.entries();
    while (enumEntries.hasMoreElements()) {
      java.util.jar.JarEntry file = (java.util.jar.JarEntry) enumEntries.nextElement();
      java.io.File f = new java.io.File(destDir + java.io.File.separator + file.getName());
      if (file.isDirectory()) { // if its a directory, create it
        f.mkdir();
        continue;
      }
      java.io.InputStream is = jar.getInputStream(file); // get the input stream
      java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
      while (is.available() > 0) {  // write contents of 'is' to 'fos'
        fos.write(is.read());
      }
      fos.close();
      is.close();
    }
  }
}