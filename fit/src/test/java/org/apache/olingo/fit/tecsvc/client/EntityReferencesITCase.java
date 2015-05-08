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
import java.util.List;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class EntityReferencesITCase extends AbstractBaseTestITCase {
  
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String PROPERTY_INT_16 = "PropertyInt16";
  private static final String ES_SERVER_SIDE_PAGING = "ESServerSidePaging";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_MANY = "NavPropertyETTwoPrimMany";
  private static final String DESCENDING = " desc";
  
  @Test
  public void testOrderBy() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT_16).build();
    
    executeCollection(uri, 3, "ESAllPrim(-32768)", "ESAllPrim(0)", "ESAllPrim(32767)");
  }
  
  @Test
  public void testOrderByReverse() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM)
                          .appendRefSegment()
                          .orderBy(PROPERTY_INT_16 + DESCENDING).build();
    
    executeCollection(uri, 3, "ESAllPrim(32767)", "ESAllPrim(0)", "ESAllPrim(-32768)");
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
                          .appendNavigationSegment(NAV_PROPERTY_ET_TWO_PRIM_MANY).orderBy(PROPERTY_INT_16)
                          .appendRefSegment().build();
    
    executeCollection(uri, 3, "ESTwoPrim(-32766)", "ESTwoPrim(32766)", "ESTwoPrim(32767)");
  }
  
  @Test
  public void testFilter() {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI)
                          .appendEntitySetSegment(ES_ALL_PRIM).appendRefSegment()
                          .filter("PropertyDecimal eq 34").build();

    executeCollection(uri, 1, "ESAllPrim(32767)");
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
                          .orderBy(PROPERTY_INT_16).skip(2).build();
    
    executeCollection(uri, 1, "ESAllPrim(32767)");
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
    
    executeCollection(uri, 0, new String[0]);
  }
  
  private void executeCollection(final URI uri, final int count, final String... expected) {
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
    client.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    
    return client;
  }
}
