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
package org.apache.olingo.fit.tecsvc;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.olingo.commons.api.http.HttpHeader;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingITCase {

  private static final Logger LOG = LoggerFactory.getLogger(PingITCase.class);

  private static final String REF_SERVICE = TecSvcConst.BASE_URL + "/";
  private static final String REDIRECT_URL = TecSvcConst.BASE_URL;

  @Test
  public void ping() throws Exception {
    URL url = new URL(REF_SERVICE);

    LOG.debug("ping request: " + REF_SERVICE);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
  }

  @Test
  public void redirect() throws Exception {

    URL url = new URL(REDIRECT_URL);

    LOG.debug("redirect request: " + REDIRECT_URL);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty(HttpHeader.ACCEPT, "application/json");
    connection.connect();

    int code = connection.getResponseCode();
    assertEquals(200, code);
  }

}
