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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Before;
import org.junit.Test;

public class BasicITCase {

  private static final String REF_SERVICE = TecSvcConst.BASE_URL;

  private ODataClient odata;

  @Before
  public void before() {
    odata = ODataClientFactory.getV4();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
  }

  @Test
  public void readServiceDocument() {
    ODataServiceDocumentRequest request =
            odata.getRetrieveRequestFactory().getServiceDocumentRequest(REF_SERVICE);
    request.setAccept("application/json;odata.metadata=minimal");
    assertNotNull(request);

    ODataRetrieveResponse<ODataServiceDocument> response = request.execute();

    assertEquals(200, response.getStatusCode());

    ODataServiceDocument serviceDocument = response.getBody();
    assertNotNull(serviceDocument);

    assertTrue(serviceDocument.getEntitySetNames().contains("ESAllPrim"));
    assertTrue(serviceDocument.getFunctionImportNames().contains("FICRTCollCTTwoPrim"));
    assertTrue(serviceDocument.getSingletonNames().contains("SIMedia"));
  }

  @Test
  public void readMetadata() {
    EdmMetadataRequest request = odata.getRetrieveRequestFactory().getMetadataRequest(REF_SERVICE);
    assertNotNull(request);

    ODataRetrieveResponse<Edm> response = request.execute();
    assertEquals(200, response.getStatusCode());

    Edm edm = response.getBody();
    assertNotNull(edm);
    assertEquals("com.sap.odata.test1", edm.getSchema("com.sap.odata.test1").getNamespace());
    assertEquals("Namespace1_Alias", edm.getSchema("com.sap.odata.test1").getAlias());
    assertNotNull(edm.getTerm(new FullQualifiedName("Core.Description")));
    assertEquals(2, edm.getSchemas().size());
  }
}
