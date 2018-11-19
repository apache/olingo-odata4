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
package org.apache.olingo.client.core.communication.request.invoke;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.junit.Test;

public class ODataInvokeRequestTest {


  @Test
  public void testRequest() throws URISyntaxException {
    ODataClient client = ODataClientFactory.getClient();
    Class<ClientInvokeResult> reference = ClientInvokeResult.class;
    HttpMethod method = HttpMethod.GET;
    URI uri = new URI("test");
    ODataInvokeRequestImpl req = new ODataInvokeRequestImpl<ClientInvokeResult>(client, reference, method, uri);
    assertNotNull(req);
    assertNotNull(req.getAccept());
    assertNotNull(req.getContentType());
    assertNotNull(req.getDefaultFormat());
    assertNotNull(req.getHeader());
    assertNotNull(req.getHeaderNames());
    assertNull(req.getIfMatch());
    assertNull(req.getIfNoneMatch());
    assertNotNull(req.getHttpRequest());
    assertNotNull(req.getMethod());
    assertNull(req.getPayload());
    assertNotNull(req.getPOSTParameterFormat());
    assertNotNull(req.getPOSTParameterFormat());
    assertNull(req.getPrefer());
    assertNotNull(req.getResponseTemplate());
    assertNotNull(req.getURI());
    assertNotNull(req.addCustomHeader("custom", "header"));
    assertNotNull(req.setAccept("json"));
    assertNotNull(req.setContentType("json"));
    req.setFormat(ContentType.APPLICATION_ATOM_XML);
  }
}
