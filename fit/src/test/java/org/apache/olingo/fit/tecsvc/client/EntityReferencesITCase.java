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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataReferenceAddingResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

public class EntityReferencesITCase extends AbstractParamTecSvcITCase {
  
  private static final String INVALID_HOST = "http://otherhost.com/service.svc";
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
  public void orderBy() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16).build();
    
    sendRequest(uri, 3, "ESAllPrim(-32768)", "ESAllPrim(0)", "ESAllPrim(32767)");
  }
  
  @Test
  public void orderByReverse() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16 + DESCENDING).build();
    
    sendRequest(uri, 3, "ESAllPrim(32767)", "ESAllPrim(0)", "ESAllPrim(-32768)");
  }
  
  @Test
  public void navigationToOne() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE).appendRefSegment().build();
    
    final ODataRetrieveResponse<ClientEntity> response = getClient().getRetrieveRequestFactory()
                                                                .getEntityRequest(uri)
                                                                .execute();
    
    assertEquals("ESTwoPrim(32767)", response.getBody().getId().toASCIIString());
  }
  
  @Test
  public void navigationToMany() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_MANY).orderBy(PROPERTY_INT16)
                          .appendRefSegment().build();
    
    sendRequest(uri, 3, "ESTwoPrim(-32766)", "ESTwoPrim(32766)", "ESTwoPrim(32767)");
  }
  
  @Test
  public void filter() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendRefSegment()
                          .filter("PropertyDecimal eq 34").build();

    sendRequest(uri, 1, "ESAllPrim(32767)");
  }
  
  @Test
  public void count() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendRefSegment().count(true).build();
    
    final ODataRetrieveResponse<ClientEntitySet> response = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();
    
    assertEquals(Integer.valueOf(3), response.getBody().getCount());
  }
  
  @Test
  public void skip() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT16).skip(2).build();
    
    sendRequest(uri, 1, "ESAllPrim(32767)");
  }
  
  @Test
  public void serverDrivenPaging() {
    URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
                          .appendRefSegment().build();
    
    int entityId = 1;
    final int EXPECTED_REQUESTS = 51;
    for(int requestCount = 0; requestCount < EXPECTED_REQUESTS; requestCount++) {
      ODataRetrieveResponse<ClientEntitySet> response = getClient().getRetrieveRequestFactory()
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
  public void responseNonExistingEntity() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(-32768)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE).appendRefSegment().build();
    
    try {
      getClient().getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
    
  @Test
  public void emptyCollection() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendKeySegment(-32768)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_MANY)
                          .appendRefSegment().build();
    
    sendRequest(uri, 0, new String[0]);
  }

  @Test
  public void twoNavigationStepsBeforeRead() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment().build();
    
    sendRequest(uri, 2, "ESKeyNav(2)", "ESKeyNav(3)");
  }
  
  @Test
  public void createReferenceRelativeUri() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = new URI(ES_KEY_NAV + "(3)");
    
    final ODataReferenceAddingResponse response = getEdmEnabledClient().getCUDRequestFactory()
                                                        .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
                                                        .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final URI getURI = getClient().newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    
    final ODataEntityRequest<ClientEntity> getRequest = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(getURI);
    getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
    
    final ClientEntitySet inlineEntitySet = getResponse.getBody()
                                                       .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                       .asInlineEntitySet()
                                                       .getEntitySet();
    assertEquals(3, inlineEntitySet.getEntities().size());
    assertShortOrInt(1, inlineEntitySet.getEntities().get(0)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertShortOrInt(2, inlineEntitySet.getEntities().get(1)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertShortOrInt(3, inlineEntitySet.getEntities().get(2)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void createReferenceAbsoluteUri() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    
    final ODataReferenceAddingResponse response = getClient().getCUDRequestFactory()
                                                        .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
                                                        .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final URI getURI = getClient().newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    
    final ODataEntityRequest<ClientEntity> getRequest = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(getURI);
    getRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> getResponse = getRequest.execute();
    
    final ClientEntitySet inlineEntitySet = getResponse.getBody()
                                                       .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                       .asInlineEntitySet()
                                                       .getEntitySet();
    assertEquals(3, inlineEntitySet.getEntities().size());
    assertShortOrInt(1, inlineEntitySet.getEntities().get(0)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertShortOrInt(2, inlineEntitySet.getEntities().get(1)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertShortOrInt(3, inlineEntitySet.getEntities().get(2)
        .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void createReferenceNonExistingEntityId() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(35)
                                                           .build();
    
    try {
      getClient().getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void createReferenceInvalidEntityId() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("NotExisting")
                                                           .build();
    
    try {
      getClient().getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void createReferenceInvalidHost() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                     .appendRefSegment()
                                                     .build();
    final URI reference = getClient().newURIBuilder(INVALID_HOST)
                                .appendEntitySetSegment("NotExisting")
                                .build();
    
    try {
      getClient().getCUDRequestFactory()
            .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
            .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceAbsoluteURI() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    
    final ODataReferenceAddingResponse response = getClient().getCUDRequestFactory()
                                               .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                               .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final URI getURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                        .build();
    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(getURI);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseGet.getStatusCode());
    
    assertShortOrInt(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                         .asInlineEntity()
                                         .getEntity()
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .asPrimitive().toValue());
  }
  
  @Test
  public void updateReferenceRelativeURI() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI(ES_KEY_NAV + "(3)");
    
    final ODataReferenceAddingResponse response = getClient().getCUDRequestFactory()
                                               .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                               .execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final URI getURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                        .build();
    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(getURI);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseGet.getStatusCode());
    
    assertShortOrInt(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                         .asInlineEntity()
                                         .getEntity()
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .asPrimitive().toValue());
  }
  
  @Test
  public void updateReferenceInvalidEntityID() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI("NonExistingEntity" + "(3)");
    
    try {
      getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                   .execute();
      fail();
    } catch(ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceNotExistingEntityId() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = new URI(ES_KEY_NAV + "(42)");
    
    try {
      getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                   .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceInvalidHost() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = getClient().newURIBuilder(INVALID_HOST).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceNull() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = getClient().newURIBuilder(INVALID_HOST).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceToPrimitiveProperty() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_INT16)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
                                  .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void createReferenceToPrimitiveProperty() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_INT16)
                                                     .appendRefSegment()
                                                     .build();
    
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                           .appendKeySegment(3)
                                                           .build();
    try{
      getEdmEnabledClient().getCUDRequestFactory().getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
          .execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateReferenceWithTwoNavigations() throws Exception {
    Map<String, Object> esTwoKeyNavKey = new HashMap<String, Object>();
    esTwoKeyNavKey.put(PROPERTY_INT16, 1);
    esTwoKeyNavKey.put(PROPERTY_STRING, "1");
    
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(esTwoKeyNavKey)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE).appendRefSegment().build();
   
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(3)
        .build();

      final ODataReferenceAddingResponse response = getEdmEnabledClient().getCUDRequestFactory()
           .getReferenceSingleChangeRequest(new URI(SERVICE_URI), uri, reference)
           .execute();
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

      final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
      Map<QueryOption, Object> expandOptions = new EnumMap<QueryOption, Object>(QueryOption.class);
      expandOptions.put(QueryOption.EXPAND, NAV_PROPERTY_ET_KEY_NAV_ONE);

      final URI getURI = getClient().newURIBuilder(SERVICE_URI)
                               .appendEntitySetSegment(ES_TWO_KEY_NAV)
                               .appendKeySegment(esTwoKeyNavKey)
                               .expandWithOptions(NAV_PROPERTY_ET_KEY_NAV_MANY, expandOptions)
                               .build();
      
      final ODataEntityRequest<ClientEntity> getRequest = getEdmEnabledClient().getRetrieveRequestFactory()
          .getEntityRequest(getURI);
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
      
      assertShortOrInt(3, inlineEntity.getProperty(PROPERTY_INT16)
          .getPrimitiveValue().asPrimitive().toValue());
  }
  
  @Test
  public void createReferenceWithTwoNavigations() throws Exception {
    Map<String, Object> esTwoKeyNavKey = new HashMap<String, Object>();
    esTwoKeyNavKey.put(PROPERTY_INT16, 1);
    esTwoKeyNavKey.put(PROPERTY_STRING, "1");
    
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(esTwoKeyNavKey)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY).appendRefSegment().build();
   
    final URI reference = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(3)
        .build();

      final ODataReferenceAddingResponse response = getClient().getCUDRequestFactory()
           .getReferenceAddingRequest(new URI(SERVICE_URI), uri, reference)
           .execute();
      assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

      final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();
      final Map<QueryOption, Object> expandOptions = new EnumMap<QueryOption, Object>(QueryOption.class);
      expandOptions.put(QueryOption.EXPAND, NAV_PROPERTY_ET_KEY_NAV_MANY);
      expandOptions.put(QueryOption.FILTER, "PropertyInt16 eq 1");

      final URI getURI = getClient().newURIBuilder(SERVICE_URI)
                               .appendEntitySetSegment(ES_TWO_KEY_NAV)
                               .appendKeySegment(esTwoKeyNavKey)
                               .expandWithOptions(NAV_PROPERTY_ET_KEY_NAV_MANY, expandOptions)
                               .build();
      
      final ODataEntityRequest<ClientEntity> getRequest = getEdmEnabledClient().getRetrieveRequestFactory()
          .getEntityRequest(getURI);
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
      assertShortOrInt(1, inlineEntitySet.getEntities().get(0)
          .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      assertShortOrInt(2, inlineEntitySet.getEntities().get(1)
          .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      assertShortOrInt(3, inlineEntitySet.getEntities().get(2)
          .getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void deleteReferenceInCollectionNavigationProperty() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(1)")
                          .build();
    
    final ODataDeleteResponse deleteResponse = getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGet = getClient().newURIBuilder(SERVICE_URI)
                             .appendEntitySetSegment(ES_KEY_NAV)
                             .appendKeySegment(1)
                             .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                             .build();
    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertShortOrInt(1, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .size());
    
    assertShortOrInt(2, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .get(0)
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .toValue());
  }
  
  @Test
  public void deleteReferenceOnSingleNavigationProperty() {
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
    
    
    if (isJson()) {
      assertEquals(0, responseGet.getBody().getNavigationLinks().size());
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      assertEquals(6, responseGet.getBody().getNavigationLinks().size());
    }
  }
  
  @Test
  public void deleteReferenceNotExistingEntity() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(3)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(42)")
                          .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void deleteReferenceNotExistingEntityInCollection() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(1)")
                          .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void deleteReferenceInvalidEntityId() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("NonExistingEntitySet(1)")
                          .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void deleteReferenceInvalidHost() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id(INVALID_HOST + "ESKeyNav(2)")
                          .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void deleteReferenceOnNonNullableSingleNavigationProperty() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendKeySegment(32767)
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_ONE)
                          .appendRefSegment()
                          .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
      fail();
    } catch (ODataClientErrorException ex) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), ex.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void navigateTwoTimesThanDeleteReferenceInCollection() {
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
    if (isJson()) {
      assertNull(responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      Assert.assertFalse(responseGet.getBody()
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE) instanceof ClientInlineEntity);
    }
    
  }
  
  @Test
  public void navigateTwoTimeThanDeleteReference() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_KEY_NAV)
                          .appendKeySegment(1)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendKeySegment(2)
                          .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                          .appendRefSegment()
                          .id("ESKeyNav(2)")
                          .build();
    
    final ODataDeleteResponse deleteResponse = getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = deleteResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final URI uriGet = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                        .appendKeySegment(1)
                                                        .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                        .appendKeySegment(2)
                                                        .expand(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                                        .build();
    
    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(uriGet);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
  
    assertEquals(1, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .size());
    
    assertShortOrInt(3, responseGet.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
                                         .asInlineEntitySet()
                                         .getEntitySet()
                                         .getEntities()
                                         .get(0)
                                         .getProperty(PROPERTY_INT16)
                                         .getPrimitiveValue()
                                         .toValue());
  }
  
  @Test
  public void deleteSingleValuedNavigationPropertyReferenceWithCollectionValuedNavigationPropertyPartner() {
    Map<String, Object> esTwoKEyNavKey = new HashMap<String, Object>();
    esTwoKEyNavKey.put("PropertyInt16", 1);
    esTwoKEyNavKey.put("PropertyString", "1");
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV)
                                                     .appendKeySegment(esTwoKEyNavKey)
                                                     .appendNavigationSegment(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                     .appendRefSegment()
                                                     .build();
    
    final ODataDeleteResponse responseDelete = getClient().getCUDRequestFactory().getDeleteRequest(uri).execute();
    final String cookie = responseDelete.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGetESTwoKeyNav = getClient().newURIBuilder(SERVICE_URI)
                                        .appendEntitySetSegment(ES_TWO_KEY_NAV)
                                        .appendKeySegment(esTwoKEyNavKey)
                                        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                        .build();
    
    final ODataEntityRequest<ClientEntity> getRequestESTwoKeyNav = getClient().getRetrieveRequestFactory()
                                                                         .getEntityRequest(uriGetESTwoKeyNav);
    getRequestESTwoKeyNav.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGetRequestESTwoKeyNav = getRequestESTwoKeyNav.execute();
    // Entity has been removed
    if(isJson()) {
      assertNull(responseGetRequestESTwoKeyNav.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      Assert.assertFalse(responseGetRequestESTwoKeyNav.getBody()
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE) instanceof ClientInlineEntity);
    }    
    
    
    final URI uriGetESKeyNav = getClient().newURIBuilder(SERVICE_URI)
                                     .appendEntitySetSegment(ES_KEY_NAV)
                                     .appendKeySegment(1)
                                     .expand(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                     .build();
    
    final ODataEntityRequest<ClientEntity> requestGetESKeyNav = getEdmEnabledClient().getRetrieveRequestFactory()
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
    assertShortOrInt(1, navEntities.get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("1", navEntities.get(0).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
    
    assertShortOrInt(1, navEntities.get(1).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", navEntities.get(1).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
  }
  
  @Test
  public void deleteCollectionValuedNavigationPropertyReferenceWithSingleValuedNavigationPropertyPartner() {
    final URI uriDelete = getClient().newURIBuilder(SERVICE_URI)
                                .appendEntitySetSegment(ES_KEY_NAV)
                                .appendKeySegment(1)
                                .appendNavigationSegment(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                .appendRefSegment()
                                .id("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')")
                                .build();
    
    final ODataDeleteResponse responseDelete = getClient().getCUDRequestFactory().getDeleteRequest(uriDelete).execute();
    final String cookie = responseDelete.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final URI uriGetESKeyNav = getClient().newURIBuilder(SERVICE_URI)
                                     .appendEntitySetSegment(ES_KEY_NAV)
                                     .appendKeySegment(1)
                                     .expand(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                     .build();
    
    final ODataEntityRequest<ClientEntity> requestGetESKeyNav = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(uriGetESKeyNav);
    requestGetESKeyNav.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseESKeyNav = requestGetESKeyNav.execute();
    final List<ClientEntity> navEntities = responseESKeyNav.getBody()
                                                           .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
                                                           .asInlineEntitySet()
                                                           .getEntitySet()
                                                           .getEntities();
    
    assertEquals(1, navEntities.size());
    assertShortOrInt(1, navEntities.get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", navEntities.get(0).getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
    
    final Map<String, Object> esTwoKEyNavKey = new HashMap<String, Object>();
    esTwoKEyNavKey.put("PropertyInt16", 1);
    esTwoKEyNavKey.put("PropertyString", "1");
    final URI uriGetESTwoKeyNav = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV)
                                                                .appendKeySegment(esTwoKEyNavKey)
                                                                .expand(NAV_PROPERTY_ET_KEY_NAV_ONE)
                                                                .build();
    final ODataEntityRequest<ClientEntity> requestGetESTwoKey = getClient().getRetrieveRequestFactory()
                                                                      .getEntityRequest(uriGetESTwoKeyNav);
    requestGetESTwoKey.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseGetESTwoKeyNav = requestGetESTwoKey.execute();
    
    if (isJson()) {
      assertNull(responseGetESTwoKeyNav.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      Assert.assertFalse(responseGetESTwoKeyNav.getBody()
          .getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE) instanceof ClientInlineEntity);
    }    
  }
  
  @Test
  public void createMissingNavigationProperty() throws Exception {
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
  public void updateMissingNavigationProperty() throws Exception {
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
  public void deleteMissingNavigationProperty() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendRefSegment()
                                                     .build();
    
    try {
      getClient().getCUDRequestFactory().getDeleteRequest(uri);
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  private void sendRequest(final URI uri, final int count, final String... expected) {
    ODataEntitySetRequest<ClientEntitySet> request =
        getEdmEnabledClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(count, entities.size());

    for( int i = 0; i < entities.size(); i++) {
      final String entityID = entities.get(i).getId().toASCIIString();
      assertEquals(expected[i], entityID);
    }
  }
}
