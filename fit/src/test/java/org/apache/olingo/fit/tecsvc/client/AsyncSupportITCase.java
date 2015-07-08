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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.junit.Test;

public final class AsyncSupportITCase extends AbstractBaseTestITCase {
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  public static final int SLEEP_TIMEOUT_IN_MS = 100;

  @Test
  public void testReadEntity() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(32767).build();

    //
    final ODataRetrieveResponse<ClientEntity> response = getClient().getRetrieveRequestFactory()
        .getEntityRequest(uri).execute();
    assertEquals(32767, response.getBody().getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals(200, response.getStatusCode());

    // first async request

    ODataRequest re1 = getClient().getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();

    assertTrue(first.isPreferenceApplied());

    // second async request
    ODataRequest re2 = getClient().getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
    assertTrue(second.isPreferenceApplied());

    // get result of first async request
    assertFalse(first.isDone());

    waitTillDone(first, 2);
    assertTrue(first.isDone());

    assertNotNull(first.getODataResponse());
    ODataResponse firstResponse = first.getODataResponse();
    assertEquals(200, firstResponse.getStatusCode());
    ResWrap<Entity> entity = getClient().getDeserializer(ContentType.APPLICATION_JSON)
        .toEntity(firstResponse.getRawResponse());
    assertEquals(32767, entity.getPayload().getProperty("PropertyInt16").asPrimitive());
    assertEquals("First Resource - positive values", entity.getPayload().getProperty("PropertyString").asPrimitive());
  }

  @Test
  public void testReadEntitySet() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .build();

    //
    final ODataRetrieveResponse<ClientEntitySet> response = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(uri).execute();
    assertEquals(200, response.getStatusCode());
    ClientEntitySet responseBody = response.getBody();
    assertEquals(3, responseBody.getEntities().size());
    checkEntityAvailableWith(responseBody, "PropertyInt16", 32767);

    // first async request
    ODataRequest re1 = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();

    assertTrue(first.isPreferenceApplied());

    // second async request
    ODataRequest re2 = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
    assertTrue(second.isPreferenceApplied());

    // get result of first async request
    assertFalse(first.isDone());

    waitTillDone(first, 2);
    assertTrue(first.isDone());

    assertNotNull(first.getODataResponse());
    ODataResponse firstResponse = first.getODataResponse();
    assertEquals(200, firstResponse.getStatusCode());
    ResWrap<Entity> entity = getClient().getDeserializer(ContentType.APPLICATION_JSON)
        .toEntity(firstResponse.getRawResponse());
    assertEquals(32767, entity.getPayload().getProperty("PropertyInt16").asPrimitive());
    assertEquals("First Resource - positive values", entity.getPayload().getProperty("PropertyString").asPrimitive());
  }

  private void checkEntityAvailableWith(ClientEntitySet entitySet, String property, Object value) {
    List<ClientEntity> entities = entitySet.getEntities();
    for (ClientEntity entity : entities) {
      ClientProperty ep = entity.getProperty("PropertyInt16");
      if(ep != null) {
        assertEquals(value, ep.getPrimitiveValue().toValue());
        return;
      }
    }
    fail("Entity with property '" + property +
        "' and value '" + value + "' not found in entitySet '" + entitySet + "'");
  }

  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";

  @Test
  public void testCreateEntity() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM).build();

    //
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt32(42)));
    newEntity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE,
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .build()));

    final ODataEntityCreateRequest<ClientEntity> createRequest =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, newEntity);
    assertNotNull(createRequest);
    final ODataEntityCreateResponse<ClientEntity> createResponse = createRequest.execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), createResponse.getStatusCode());
    assertEquals(SERVICE_URI + "/ESAllPrim(1)", createResponse.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity createdEntity = createResponse.getBody();
    assertNotNull(createdEntity);
    final ClientProperty property1 = createdEntity.getProperty("PropertyInt64");
    assertNotNull(property1);
    assertEquals(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = createdEntity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue());    //


//    // first async request
//    ODataRequest re1 = getClient().getRetrieveRequestFactory()
//        .getEntityRequest(uri)
//        .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
//    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();
//
//    assertTrue(first.isPreferenceApplied());
//
//    // second async request
//    ODataRequest re2 = getClient().getRetrieveRequestFactory()
//        .getEntityRequest(uri)
//        .addCustomHeader(HttpHeader.PREFER, "respond-async; " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
//    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
//    assertTrue(second.isPreferenceApplied());
//
//    // get result of first async request
//    assertFalse(first.isDone());
//
//    waitTillDone(first, 2);
//    assertTrue(first.isDone());
//
//    assertNotNull(first.getODataResponse());
//    ODataResponse firstResponse = first.getODataResponse();
//    assertEquals(200, firstResponse.getStatusCode());
//    ResWrap<Entity> entity = getClient().getDeserializer(ContentType.APPLICATION_JSON)
//        .toEntity(firstResponse.getRawResponse());
//    assertEquals(32767, entity.getPayload().getProperty("PropertyInt16").asPrimitive());
//    assertEquals("First Resource - positive values", entity.getPayload().getProperty("PropertyString").asPrimitive());
  }


  private void waitTillDone(AsyncResponseWrapper async, int maxWaitInSeconds) throws InterruptedException {
    int waitCounter = maxWaitInSeconds * 1000;
    while(!async.isDone() && waitCounter > 0) {
      TimeUnit.MILLISECONDS.sleep(SLEEP_TIMEOUT_IN_MS);
      waitCounter -= SLEEP_TIMEOUT_IN_MS;
    }
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    return odata;
  }
}
