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
package org.apache.olingo.fit.tecsvc.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class EntityReferenceITCase extends AbstractBaseTestITCase {

  private static final String CONTEXT_ENTITY_REFERENCE = "\"@odata.context\":\"../$metadata#$ref\"";
  private static final String CONTEXT_COLLECTION_REFERENCE = "\"@odata.context\":\"../$metadata#Collection($ref)";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  @Test
  public void testContextURlSingleEntity() throws Exception {
    URL url = new URL(SERVICE_URI + "/ESAllPrim(0)/$ref");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty(HttpHeader.ACCEPT, ContentType.APPLICATION_JSON.toContentTypeString());
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains(CONTEXT_ENTITY_REFERENCE));
  }

  @Test
  public void testContextURLEntityCollection() throws Exception {
    URL url = new URL(SERVICE_URI + "/ESAllPrim/$ref");

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty(HttpHeader.ACCEPT, ContentType.APPLICATION_JSON.toContentTypeString());
    connection.setRequestMethod(HttpMethod.GET.name());
    connection.connect();

    assertEquals(HttpStatusCode.OK.getStatusCode(), connection.getResponseCode());

    final String content = IOUtils.toString(connection.getInputStream());
    assertTrue(content.contains(CONTEXT_COLLECTION_REFERENCE));
  }

  @Override
  protected ODataClient getClient() {
    return null;
  }
}
