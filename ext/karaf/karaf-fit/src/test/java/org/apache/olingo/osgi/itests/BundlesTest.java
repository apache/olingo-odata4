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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class BundlesTest extends OlingoOSGiTestSupport {
    @Test
    public void test() throws Exception {
        // check the server-api and server-core
        assertBundleStarted("org.apache.olingo.odata-server-api");
        assertBundleStarted("org.apache.olingo.odata-server-core");

        // check the client-api and client-core
        assertBundleStarted("org.apache.olingo.odata-client-api");
        assertBundleStarted("org.apache.olingo.odata-client-core");
    }

    @Configuration
    public Option[] config() {
        return new Option[]{
            olingoBaseConfig(),
            features(olingoUrl, "olingo-server", "olingo-client"),
            logLevel(LogLevel.INFO)};
    }
}
