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
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataReferenceAddingResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class EntityReferencesITCase extends AbstractBaseTestITCase {
  
  private static final String INVALID_HOST = "http://otherhost.com/service.svc";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_SERVER_SIDE_PAGING = "ESServerSidePaging";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_MANY = "NavPropertyETTwoPrimMany";
  private static final String NAV_PROPERTY_ET_KEY_NAV_MANY = "NavPropertyETKeyNavMany";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";
  private static final String DESCENDING = " desc";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";
  
  @Test
  public void testOrderBy() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16).build();
    
    sendRequest(uri, 3, "ESAllPrim(-32768)", "ESAllPrim(0)", "ESAllPrim(32767)");
  }
  
  @Test
  public void testOrderByReverse() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16 + DESCENDING).build();
    
    sendRequest(uri, 3, "ESAllPrim(32767)", "ESAllPrim(0)", "ESAllPrim(-32768)");
  }
  
  @Test
  public void testNavigationToOne() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE).appendRefSegment().build();
    
    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
                                                                .getEntityRequest(uri)
                                                                .execute();
    
    assertEquals("ESTwoPrim(32767)", response.getBody().getId().toASCIIString());
  }
  
  @Test
  public void testNavigationToMany() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_MANY).orderBy(PROPERTY_INT16)
                          .appendRefSegment().build();
    
    sendRequest(uri, 3, "ESTwoPrim(-32766)", "ESTwoPrim(32766)", "ESTwoPrim(32767)");
  }
  
  @Test
  public void testFilter() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendRefSegment()
                          .filter("PropertyDecimal eq 34").build();

    sendRequest(uri, 1, "ESAllPrim(32767)");
  }
  
  @Test
  public void testCount() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendRefSegment().count(true).build();
    
    final ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();
    
    assertEquals(Integer.valueOf(3), response.getBody().getCount());
  }
  
  @Test
  public void testSkip() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16).skip(2).build();
    
    sendRequest(uri, 1, "ESAllPrim(32767)");
  }
  
  @Test
  public void testServerDrivenPaging() {
    final ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
                          .appendRefSegment().build();
    
    int entityId = 1;
    final int EXPECTED_REQUESTS = 51;
    for(int requestCount = 0; requestCount < EXPECTED_REQUESTS; requestCount++) {
      ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
                                                                    .getEntitySetRequest(uri)
                                                                    .execute();
      
      List<ClientEntity> entities = response.getBody().getEntities();
      assertEquals(Math.min(10, 503 - (entityId - 1)), entities.size());
      
      for( int i = 0; i < entities.size(); i++) {
        final String entityID = entities.get(i).getId().toASCIIString();
        assertEquals(ES_SERVER_SIDE_PAGING + "(" + (entityId++) + ")", entityID);
      }
      
      if(requestCount < (EXPECTED_REQUESTS - 1)) {
        assertNotNull(response.getBody().getNext());
      } else {
        assertNull(response.getBody().getNext());
      }
      uri = response.getBody().getNext();
    }
  }
  
  @Test
  public void testResponseNonExistingEntity() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE).appendRefSegment().build();
    
    try {
      client.getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .execute();
      fail();
    } catch(ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
    
  @Test
  public void testEmptyCollection() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendKeySegment(-32768)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_MANY)
                          .appendRefSegment().build();
    
    sendRequest(uri, 0, new String[0]);
  }
  
  @Test
  public void testTwoNavigationStepsBeforeRead() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment().build();
    
    sendRequest(uri, 2, "ESKeyNav(2)", "ESKeyNav(3)");
  }
  
  @Test
  public void testCreateReferenceRelativeUri() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = new URI(ES_KEY_NAV + "(3)");
    
    final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
                                                        .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
                                                        .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final URI getURI = client.newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    
    final ODataEntityRequest<ClientEntity> getRequest = client.getRetrieveRequestFactory().getEntityRequest(getURI);
    getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
    
    final ClientEntitySet inlineEntitySet = getResponse.getBody()
                                                       .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                       .asInlineEntitySet()
                                                       .getEntitySet();
    assertEquals(3, inlineEntitySet.getEntities().size());
    assertEquals(1, inlineEntitySet.getEntities().get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(2, inlineEntitySet.getEntities().get(1).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(3, inlineEntitySet.getEntities().get(2).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void testCreateReferenceAbsoluteUri() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    
    final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
                                                        .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
                                                        .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final URI getURI = client.newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    
    final ODataEntityRequest<ClientEntity> getRequest = client.getRetrieveRequestFactory().getEntityRequest(getURI);
    getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
    
    final ClientEntitySet inlineEntitySet = getResponse.getBody()
                                                       .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                       .asInlineEntitySet()
                                                       .getEntitySet();
    assertEquals(3, inlineEntitySet.getEntities().size());
    assertEquals(1, inlineEntitySet.getEntities().get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(2, inlineEntitySet.getEntities().get(1).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(3, inlineEntitySet.getEntities().get(2).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void testCreateReferenceNonExistingEntityId() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(35)
                                                           .build();
    
    try {
      client.getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testCreateReferenceInvalidEntityId() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("NotExisting")
                                                           .build();
    
    try {
      client.getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testCreateReferenceInvalidHost() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = client.newURIBuilder(INVALID_HOST)
                                .appendEntitySetSegment("NotExisting")
                                .build();
    
    try {
      client.getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceAbsoluteURI() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    
    final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
                                               .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                               .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final URI getURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                        .build();
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(getURI);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseGet.getStatusCode());
    
    assertEquals(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                         .asInlineEntity()
                                         .getEntity()
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .asPrimitive().toValue());
  }
  
  @Test
  public void testUpdateReferenceRelativeURI() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI(ES_KEY_NAV + "(3)");
    
    final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
                                               .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                               .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final URI getURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                        .build();
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(getURI);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseGet.getStatusCode());
    
    assertEquals(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                         .asInlineEntity()
                                         .getEntity()
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .asPrimitive().toValue());
  }
  
  @Test
  public void testUpdateReferenceInvalidEntityID() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI("NonExistingEntity" + "(3)");
    
    try {
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                   .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceNotExistingEnityId() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI(ES_KEY_NAV + "(42)");
    
    try {
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                   .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceInvalidHost() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = client.newURIBuilder(INVALID_HOST).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceNull() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = client.newURIBuilder(INVALID_HOST).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceToPrimitiveProperty() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_INT16)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testCreateReferenceToPrimitiveProperty() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_INT16)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      client.getCUDRequestFactory().getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
                                   .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateReferenceWithTwoNavigations() throws Exception {
    final ODataClient client = getClient();
    final Map<String, Object> esTwoKeyNavKey = new HashMap<String, Object>();
    esTwoKeyNavKey.put(PROPERTY_INT16, 1);
    esTwoKeyNavKey.put(PROPERTY_STRING, "1");
    
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(esTwoKeyNavKey)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE).appendRefSegment().build();
   
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(3)
        .build();

      final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
           .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
           .execute();
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
      
      final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
      final Map<QueryOption, Object> expandOptions = new HashMap<QueryOption, Object>();
      expandOptions.put(QueryOption.EXPAND, NAV_PROPERTY_ET_KEY_NAV_ONE);
      
      final URI getURI = client.newURIBuilder(SERVICE_URI)
                               .appendEntitySetSegment(ES_TWO_KEY_NAV)
                               .appendKeySegment(esTwoKeyNavKey)
                               .expandWithOptions(NAV_PROPERTY_ET_KEY_NAV_MANY, expandOptions)
                               .build();
      
      final ODataEntityRequest<ClientEntity> getRequest = client.getRetrieveRequestFactory().getEntityRequest(getURI);
      getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
      final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
      
      final ClientEntity inlineEntity = getResponse.getBody()
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
          .asInlineEntitySet()
          .getEntitySet()
          .getEntities()
          .get(0)
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
          .asInlineEntity().getEntity();
      
      assertEquals(3, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().asPrimitive().toValue());
  }
  
  @Test
  public void testCreateReferenceWithTwoNavigations() throws Exception {
    final ODataClient client = getClient();
    final Map<String, Object> esTwoKeyNavKey = new HashMap<String, Object>();
    esTwoKeyNavKey.put(PROPERTY_INT16, 1);
    esTwoKeyNavKey.put(PROPERTY_STRING, "1");
    
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(esTwoKeyNavKey)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendRefSegment().build();
   
    final URI reference = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(3)
        .build();

      final ODataReferenceAddingResponse response = client.getCUDRequestFactory()
           .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
           .execute();
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
      
      final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
      final Map<QueryOption, Object> expandOptions = new HashMap<QueryOption, Object>();
      expandOptions.put(QueryOption.EXPAND, NAV_PROPERTY_ET_KEY_NAV_MANY);
      expandOptions.put(QueryOption.FILTER, "PropertyInt16 eq 1");
      
      final URI getURI = client.newURIBuilder(SERVICE_URI)
                               .appendEntitySetSegment(ES_TWO_KEY_NAV)
                               .appendKeySegment(esTwoKeyNavKey)
                               .expandWithOptions(NAV_PROPERTY_ET_KEY_NAV_MANY, expandOptions)
                               .build();
      
      final ODataEntityRequest<ClientEntity> getRequest = client.getRetrieveRequestFactory().getEntityRequest(getURI);
      getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
      final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
      
      final ClientEntitySet inlineEntitySet = getResponse.getBody()
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
          .asInlineEntitySet()
          .getEntitySet()
          .getEntities()
          .get(0)
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
          .asInlineEntitySet()
          .getEntitySet();
      
      assertEquals(3, inlineEntitySet.getEntities().size());
      assertEquals(1, inlineEntitySet.getEntities().get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      assertEquals(2, inlineEntitySet.getEntities().get(1).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      assertEquals(3, inlineEntitySet.getEntities().get(2).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void testDeleteReferenceInCollectionNavigationProperty() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(1)")
                          .build();
    
    final ODataDeleteResponse deleteResponse = client.getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGet = client.newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertEquals(1, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .size());
    
    assertEquals(2, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .get(0)
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .toValue());
  }
  
  @Test
  public void testDeleteReferenceOnSingleNavigationProperty() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                          .appendRefSegment()
                          .build();
    
    final ODataDeleteResponse deleteResponse = client.getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGet = client.newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                             .build();
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertEquals(0, responseGet.getBody().getNavigationLinks().size());
  }
  
  @Test
  public void testDeleteReferenceNotExistingEntity() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(3)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(42)")
                          .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testDeleteReferenceNotExistingEntityInCollection() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(1)")
                          .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testDeleteReferenceInvalidEntityId() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("NonExistingEntitySet(1)")
                          .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testDeleteReferenceInvalidHost() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id(INVALID_HOST + "ESKeyNav(2)")
                          .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testDeleteReferenceOnNonNullableSingleNavigationProperty() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendKeySegment(32767)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE)
                          .appendRefSegment()
                          .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
 
  @Test
  public void testNavigateTwoTimesThanDeleteReferenceInCollection() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                          .appendRefSegment()
                          .build();
    
    final ODataDeleteResponse deleteResponse = client.getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    
    final URI uriGet = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                        .appendKeySegment(2)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                        .build();
    
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertNull(responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
  }
  
  @Test
  public void testNavigateTwoTimeThanDeleteReference() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(2)")
                          .build();
    
    final ODataDeleteResponse deleteResponse = client.getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    
    final URI uriGet = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                        .appendKeySegment(2)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                        .build();
    
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
  
    assertEquals(1, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .size());
    
    assertEquals(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .get(0)
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .toValue());
  }
  
  @Test
  public void testDeleteSingleValuedNavigationPropertyReferenceWithColectionValuedNavigatioPropertyPartner() {
    final ODataClient client = getClient();
    final Map<String, Object> esTwoKEyNavKey = new HashMap<String, Object>();
    esTwoKEyNavKey.put("PropertyInt16", 1);
    esTwoKEyNavKey.put("PropertyString", "1");
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV)
                                                     .appendKeySegment(esTwoKEyNavKey)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final ODataDeleteResponse responseDelete = client.getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = responseDelete.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGetESTwoKeyNav = client.newURIBuilder(SERVICE_URI)
                                        .appendEntitySetSegment(ES_TWO_KEY_NAV)
                                        .appendKeySegment(esTwoKEyNavKey)
                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                        .build();
    
    final ODataEntityRequest<ClientEntity> getRequestESTwoKeyNav = client.getRetrieveRequestFactory()
                                                                         .getEntityRequest(uriGetESTwoKeyNav);
    getRequestESTwoKeyNav.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGetRequestESTwoKeyNav = getRequestESTwoKeyNav.execute();
    // Entity has been removed
    assertNull(responseGetRequestESTwoKeyNav.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    
    final URI uriGetESKeyNav = client.newURIBuilder(SERVICE_URI)
                                     .appendEntitySetSegment(ES_KEY_NAV)
                                     .appendKeySegment(1)
                                     .expand(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                     .build();
    
    final ODataEntityRequest<ClientEntity> requestGetESKeyNav = client.getRetrieveRequestFactory()
                                                                      .getEntityRequest(uriGetESKeyNav);
    requestGetESKeyNav.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGetESKeyNav = requestGetESKeyNav.execute();
    
    final List<ClientEntity> navEntities = responseGetESKeyNav.getBody()
                                                              .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                                              .asInlineEntitySet()
                                                              .getEntitySet()
                                                              .getEntities();
    
    // The Entities in the collection are still there
    assertEquals(2, navEntities.size());
    assertEquals(1, navEntities.get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("1", navEntities.get(0).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
    
    assertEquals(1, navEntities.get(1).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", navEntities.get(1).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
  }
  
  @Test
  public void testDeleteCollectionValuedNavigationPropertyReferenceWithSingleValuedNavigationPropertyPartner() {
    final ODataClient client = getClient();
    final URI uriDelete = client.newURIBuilder(SERVICE_URI)
                                .appendEntitySetSegment(ES_KEY_NAV)
                                .appendKeySegment(1)
                                .appendNavigationSegment(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                .appendRefSegment()
                                .id("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')")
                                .build();
    
    final ODataDeleteResponse responseDelete = client.getCUDRequestFactory().getDeleteRequest(uriDelete).execute();
    final String cookie = responseDelete.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGetESKeyNav = client.newURIBuilder(SERVICE_URI)
                                     .appendEntitySetSegment(ES_KEY_NAV)
                                     .appendKeySegment(1)
                                     .expand(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                     .build();
    
    final ODataEntityRequest<ClientEntity> requestGetESKeyNav = client.getRetrieveRequestFactory()
                                                                      .getEntityRequest(uriGetESKeyNav);
    requestGetESKeyNav.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseESKeyNav = requestGetESKeyNav.execute();
    final List<ClientEntity> navEntities = responseESKeyNav.getBody()
                                                           .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                                           .asInlineEntitySet()
                                                           .getEntitySet()
                                                           .getEntities();
    
    assertEquals(1, navEntities.size());
    assertEquals(1, navEntities.get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", navEntities.get(0).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
    
    final Map<String, Object> esTwoKEyNavKey = new HashMap<String, Object>();
    esTwoKEyNavKey.put("PropertyInt16", 1);
    esTwoKEyNavKey.put("PropertyString", "1");
    final URI uriGetESTwoKeyNav = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV)
                                                                .appendKeySegment(esTwoKEyNavKey)
                                                                .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                                .build();
    final ODataEntityRequest<ClientEntity> requestGetESTwoKey = client.getRetrieveRequestFactory()
                                                                      .getEntityRequest(uriGetESTwoKeyNav);
    requestGetESTwoKey.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGetESTwoKeyNav = requestGetESTwoKey.execute();
    assertNull(responseGetESTwoKeyNav.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
  }
  
  @Test
  public void testCreateMissingNavigationProperty() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendRefSegment().build();
    final URI ref = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    
    try {
      client.getCUDRequestFactory().getReferenceAddingRequest(new URI(SERVICE_URI), uri, ref).execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testUpdateMissingNavigationProperty() throws Exception {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendRefSegment()
                                                     .build();
    final URI ref = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    
    try {
      client.getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, ref).execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void testDeleteMissingNavigationProperty() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendRefSegment()
                                                     .build();
    
    try {
      client.getCUDRequestFactory().getDeleteRequest(uri);
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  private void sendRequest(final URI uri, final int count, final String... expected) {
    final ODataClient client = getClient();
    final ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
                                                                  .getEntitySetRequest(uri)
                                                                  .execute();
    
    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(count, entities.size());
    
    for( int i = 0; i < entities.size(); i++) {
      final String entityID = entities.get(i).getId().toASCIIString();
      assertEquals(expected[i], entityID);
    }
  }
  
  @Override
  protected ODataClient getClient() {
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    
    return client;
  }
}
