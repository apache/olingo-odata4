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
 ******************************************************************************/
package org.apache.olingo.osgi.itests;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.apache.karaf.features.FeaturesService;
import org.junit.Assert;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import static org.ops4j.pax.exam.CoreOptions.composite;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.when;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;

/**
 * 
 */
public class OlingoOSGiTestSupport {
    private static final String MAVEN_DEPENDENCIES_PROPERTIES = "/META-INF/maven/dependencies.properties";

    @Inject
    protected BundleContext bundleContext;

    @Inject
    protected FeaturesService featureService;

    protected ExecutorService executor = Executors.newCachedThreadPool();

    protected MavenUrlReference olingoUrl;
    protected MavenUrlReference karafUrl;

    /**
     * @param probe
     * @return
     */
    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

    private static String getKarafVersion() {
        String karafVersion = getVersionFromPom("org.apache.karaf/apache-karaf/version");
        if (karafVersion == null) {
            karafVersion = System.getProperty("karaf.version");
        }
        if (karafVersion == null) {
            // setup the default version of it
            karafVersion = "3.0.3";
        }
        return karafVersion;
    }

    private static String getVersionFromPom(String key) {
        try {
            InputStream ins = OlingoOSGiTestSupport.class.getResourceAsStream(MAVEN_DEPENDENCIES_PROPERTIES);
            Properties p = new Properties();
            p.load(ins);
            return p.getProperty(key);
        } catch (Exception t) {
            throw new IllegalStateException(MAVEN_DEPENDENCIES_PROPERTIES + " can not be found", t);
        }
    }    
    /**
     * Create an {@link org.ops4j.pax.exam.Option} for using a .
     * 
     * @return
     */
    protected Option olingoBaseConfig() {
        karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").version(getKarafVersion())
            .type("tar.gz");
        olingoUrl = maven().groupId("org.apache.olingo").artifactId("odata-karaf-features").versionAsInProject()
            .type("xml").classifier("features");
        String localRepo = System.getProperty("localRepository");
        return composite(karafDistributionConfiguration()
                             .frameworkUrl(karafUrl)
                             .karafVersion(getKarafVersion())
                             .name("Apache Karaf")
                             .useDeployFolder(false)
                             .unpackDirectory(new File("target/paxexam/")),
                         //DO NOT COMMIT WITH THIS LINE ENABLED!!!    
                         //KarafDistributionOption.keepRuntimeFolder(),                         
                         systemProperty("java.awt.headless").value("true"),
                         when(localRepo != null)
                             .useOptions(editConfigurationFilePut("etc/org.ops4j.pax.url.mvn.cfg",
                                                                  "org.ops4j.pax.url.mvn.localRepository",
                                                                  localRepo)));
    }

    protected void assertBundleStarted(String name) {
        Bundle bundle = findBundleByName(name);
        Assert.assertNotNull("Bundle " + name + " should be installed", bundle);
        Assert.assertEquals("Bundle " + name + " should be started", Bundle.ACTIVE, bundle.getState());
    }

    protected Bundle findBundleByName(String symbolicName) {
        for (Bundle bundle : bundleContext.getBundles()) {
            if (bundle.getSymbolicName().equals(symbolicName)) {
                return bundle;
            }
        }
        return null;
    }
}
