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
package org.apache.olingo.osgi.itests.server;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.olingo.osgi.itests.OlingoOSGiTestSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;

/**
 *
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class CarServiceTest extends OlingoOSGiTestSupport {
    private static final String SERVICE_URL = "http://localhost:8181/olingo-cars/cars.svc/";

    @Test
    public void testServiceStarted() throws Exception {
        // check if the bundle is started
        assertBundleStarted("org.apache.olingo.odata-server-osgi-sample");

        // use the jdk http client to verify the server side
        verifyContent(getContent(SERVICE_URL));

        verifyContent(getContent(SERVICE_URL + "$metadata"));

        verifyContent(getContent(SERVICE_URL + "Cars"));

        verifyContent(getContent(SERVICE_URL + "Cars(1)"));

        verifyContent(getContent(SERVICE_URL + "Cars(1)/Price"));
    }

    private static void verifyContent(String content) {
        // this is currently a simple test to check if there is no error
        Assert.assertNotNull(content);
        Assert.assertTrue(content.indexOf("error") < 0);
    }

    private static String getContent(String target) throws Exception {
        InputStream in = null;
        try {
            URL url = new URL(target);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Accept", "*/*");
            in = urlConnection.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n = 0;
            while ((n = in.read(buf, 0, buf.length)) != -1) {
                baos.write(buf, 0, n);
            }
            return new String(baos.toByteArray(), "utf-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }

    }

    @Configuration
    public Option[] config() {
        return new Option[] {
            olingoBaseConfig(),
            features(olingoUrl, "olingo-server", "olingo-client"),
            mavenBundle("org.apache.olingo", "odata-server-osgi-sample", "4.7.0-SNAPSHOT"),
            logLevel(LogLevel.INFO)
        };
    }


}
