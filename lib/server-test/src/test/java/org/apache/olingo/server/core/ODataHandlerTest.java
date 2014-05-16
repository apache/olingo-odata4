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
package org.apache.olingo.server.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Before;
import org.junit.Test;

public class ODataHandlerTest {

  private ODataHandler handler;

  @Before
  public void before() {
    OData server = OData.newInstance();
    Edm edm = server.createEdm(new EdmTechProvider());

    handler = new ODataHandler(server, edm);
  }

  @Test
  public void testServiceDocumentDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawBaseUri("http://localhost/odata/");
    request.setRawODataPath("");
    
    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertEquals("application/json", response.getHeaders().get("Content-Type"));

    assertNotNull(response.getContent());
    String doc = IOUtils.toString(response.getContent());

    assertTrue(doc.contains("\"@odata.context\" : \"http://localhost/odata/$metadata\""));
    assertTrue(doc.contains("\"value\" :"));
  }

  @Test
  public void testMetadataDefault() throws Exception {
    ODataRequest request = new ODataRequest();

    request.setMethod(HttpMethod.GET);
    request.setRawODataPath("$metadata");

    ODataResponse response = handler.process(request);

    assertNotNull(response);
    assertEquals(200, response.getStatusCode());
    assertEquals("application/xml", response.getHeaders().get("Content-Type"));

    assertNotNull(response.getContent());
    String doc = IOUtils.toString(response.getContent());

    System.out.println(doc);
    
    assertTrue(doc.contains("<edmx:Edmx Version=\"4.0\""));

  }

}
