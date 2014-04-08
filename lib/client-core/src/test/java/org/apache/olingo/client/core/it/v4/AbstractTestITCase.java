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
package org.apache.olingo.client.core.it.v4;

import java.io.IOException;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestITCase {

  protected static final Logger LOG = LoggerFactory.getLogger(AbstractTestITCase.class);

  protected static ODataClient client;

  protected static String testStaticServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/StaticService/V40/Static.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/StaticService/V40/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/DefaultService.svc";
  }

  @BeforeClass
  public static void setClientInstance() {
    client = ODataClientFactory.getV4();
  }

  protected ODataClient getClient() {
    return client;
  }
}
