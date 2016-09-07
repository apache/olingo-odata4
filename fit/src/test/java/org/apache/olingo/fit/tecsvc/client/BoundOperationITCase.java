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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;
import org.junit.runners.Parameterized;

public class BoundOperationITCase extends AbstractParamTecSvcITCase {
  private static final ContentType CONTENT_TYPE_JSON_FULL_METADATA =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_ODATA_METADATA, 
          ContentType.VALUE_ODATA_METADATA_FULL);
  
  @Parameterized.Parameters(name = "{0}")
  public static List<ContentType[]> parameters() {
    ContentType[] a = new ContentType[1];
    a[0] = CONTENT_TYPE_JSON_FULL_METADATA;
    ArrayList<ContentType[]> type = new ArrayList<ContentType[]>();
    type.add(a);
    return type;
  }
  
  @Test
  public void readEntitySetOperation() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESAllPrim").build());    
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);

    assertNull(entitySet.getCount());
    assertNull(entitySet.getNext());
    assertEquals(Collections.<ClientAnnotation> emptyList(), entitySet.getAnnotations());
    assertNull(entitySet.getDeltaLink());

    List<ClientOperation> ecOperations = entitySet.getOperations();
    assertNotNull(ecOperations);
    assertEquals(3, ecOperations.size());
    
    assertEquals("#olingo.odata.test1.BAESAllPrimRTETAllPrim", ecOperations.get(0).getMetadataAnchor());
    assertEquals("olingo.odata.test1.BAESAllPrimRTETAllPrim", ecOperations.get(0).getTitle());
    assertEquals("/ESAllPrim/olingo.odata.test1.BAESAllPrimRTETAllPrim", 
        ecOperations.get(0).getTarget().toASCIIString());    
    
    assertEquals("#olingo.odata.test1.BAESAllPrimRT", ecOperations.get(1).getMetadataAnchor());
    assertEquals("olingo.odata.test1.BAESAllPrimRT", ecOperations.get(1).getTitle());
    assertEquals("/ESAllPrim/olingo.odata.test1.BAESAllPrimRT", 
        ecOperations.get(1).getTarget().toASCIIString());
    
    
    assertEquals("#olingo.odata.test1.BFNESAllPrimRTCTAllPrim", ecOperations.get(2).getMetadataAnchor());
    assertEquals("olingo.odata.test1.BFNESAllPrimRTCTAllPrim", ecOperations.get(2).getTitle());
    assertEquals("/ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim", 
        ecOperations.get(2).getTarget().toASCIIString());
        
    final List<ClientEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(3, entities.size());
    
    ClientEntity entity = entities.get(0);
    assertNotNull(entity);
    List<ClientOperation> operations = entity.getOperations();
    assertNotNull(operations);
    assertEquals(1, operations.size());
    
    assertEquals("#olingo.odata.test1.BAETAllPrimRT", operations.get(0).getMetadataAnchor());
    assertEquals("olingo.odata.test1.BAETAllPrimRT", operations.get(0).getTitle());
    assertEquals("/ESAllPrim(32767)/olingo.odata.test1.BAETAllPrimRT", 
        operations.get(0).getTarget().toASCIIString());              
  } 
  
  @Test
  public void readComplexPropertyOperation() {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCompAllPrim")
            .appendKeySegment(32767)
            .appendPropertySegment("PropertyComp")
            .build());    
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    
    List<ClientOperation> operations = property.getOperations();
    assertNotNull(operations);
    
    assertEquals(1, operations.size());
    
    assertEquals("#olingo.odata.test1.BFCColCTAllPrimRTESAllPrim", operations.get(0).getMetadataAnchor());
    assertEquals("olingo.odata.test1.BFCColCTAllPrimRTESAllPrim", operations.get(0).getTitle());
    assertEquals("PropertyComp/olingo.odata.test1.BFCColCTAllPrimRTESAllPrim", 
        operations.get(0).getTarget().toASCIIString());                  
  }
  
  @Test
  public void invokeFunction(){
      ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESAllPrim").build());    
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("application/json; odata.metadata=full", response.getContentType());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);

    entitySet.getOperation(SERVICE_URI);
    
    
  }
}
