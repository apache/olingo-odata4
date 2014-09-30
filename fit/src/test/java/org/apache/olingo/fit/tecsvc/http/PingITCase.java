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
package org.apache.olingo.fit.tecsvc.http;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class PingITCase extends AbstractBaseTestITCase{

  private static final Logger LOG = LoggerFactory.getLogger(PingITCase.class);

  private static final String SERVICE_URI = TecSvcConst.BASE_URI + "/";
  private static final String REDIRECT_URI = TecSvcConst.BASE_URI;

  @Test
  public void ping() throws Exception {
    URL url = new URL(SERVICE_URI);

    LOG.debug("ping request: " + SERVICE_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
  }

  @Test
  public void redirect() throws Exception {

    URL url = new URL(REDIRECT_URI);

    LOG.debug("redirect request: " + REDIRECT_URI);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
  }

  @Override
  protected CommonODataClient<?> getClient() {
    return null;
  }

}
