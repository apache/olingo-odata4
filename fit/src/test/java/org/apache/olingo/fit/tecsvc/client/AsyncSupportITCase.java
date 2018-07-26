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
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.AsyncBatchRequestWrapper;
import org.apache.olingo.client.api.communication.request.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.PreferenceName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public final class AsyncSupportITCase extends AbstractParamTecSvcITCase {

  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";
  private static final String TEC_ASYNC_SLEEP = "tec.sleep";  // see TechnicalAsyncService
  private static final int SLEEP_TIMEOUT_IN_MS = 100;

  @Test
  public void clientAsync() throws InterruptedException, ExecutionException, TimeoutException {
    ODataClient client = getClient();
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM);
    final Future<ODataRetrieveResponse<ClientEntitySet>> futureRes =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).asyncExecute();
    assertNotNull(futureRes);
    //wait a maximum of 10 seconds, otherwise throw TimeoutException
    final ODataRetrieveResponse<ClientEntitySet> res = futureRes.get(10, TimeUnit.SECONDS);
    assertNotNull(res);
    assertEquals(200, res.getStatusCode());
    assertFalse(res.getBody().getEntities().isEmpty());
  }
  
  @Test
  public void readEntity() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(32767).build();

    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
        .getEntityRequest(uri).execute();
    assertShortOrInt(32767, response.getBody().getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // first async request

    ODataRequest re1 = client.getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();

    assertTrue(first.isPreferenceApplied());

    // second async request
    ODataRequest re2 = client.getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
    assertTrue(second.isPreferenceApplied());

    // get result of first async request
    assertFalse(first.isDone());

    waitTillDone(first, 2);
    assertTrue(first.isDone());

    assertNotNull(first.getODataResponse());
    ODataResponse firstResponse = first.getODataResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), firstResponse.getStatusCode());
    ResWrap<Entity> entity = client.getDeserializer(getContentType()).toEntity(firstResponse.getRawResponse());
    assertShortOrInt(32767, entity.getPayload().getProperty("PropertyInt16").asPrimitive());
    assertEquals("First Resource - positive values", entity.getPayload().getProperty("PropertyString").asPrimitive());
  }

  @Test
  public void readEntitySet() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .build();

    final ODataRetrieveResponse<ClientEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri).execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    ClientEntitySet responseBody = response.getBody();
    assertEquals(3, responseBody.getEntities().size());
    checkEntityAvailableWith(responseBody, "PropertyInt16", 32767);

    // first async request
    ODataRequest re1 = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();

    assertTrue(first.isPreferenceApplied());

    // second async request
    ODataRequest re2 = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
    assertTrue(second.isPreferenceApplied());

    // get result of first async request
    assertFalse(first.isDone());

    waitTillDone(first, 2);
    assertTrue(first.isDone());

    assertNotNull(first.getODataResponse());
    ODataResponse firstResponse = first.getODataResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), firstResponse.getStatusCode());
    assertEquals(2, firstResponse.getHeaderNames().size());
    assertEquals("4.0", firstResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    ResWrap<EntityCollection> firWrap = client.getDeserializer(getContentType())
        .toEntitySet(firstResponse.getRawResponse());
    EntityCollection firstResponseEntitySet = firWrap.getPayload();
    assertEquals(3, firstResponseEntitySet.getEntities().size());
    Entity firstResponseEntity = firstResponseEntitySet.getEntities().get(0);
    assertShortOrInt(32767, firstResponseEntity.getProperty("PropertyInt16").asPrimitive());
    assertEquals("First Resource - positive values", firstResponseEntity.getProperty("PropertyString").asPrimitive());
  }

  @Test
  public void createEntity() throws Exception {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM).build();

    ClientEntity newEntity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty("PropertyInt64",
            getFactory().newPrimitiveValueBuilder().buildInt32(42)));
    newEntity.addLink(getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE,
            client.newURIBuilder(SERVICE_URI)
                    .appendEntitySetSegment("ESTwoPrim")
                    .appendKeySegment(32766)
                    .build()));

    final ODataEntityCreateRequest<ClientEntity> createRequest =
        client.getCUDRequestFactory().getEntityCreateRequest(uri, newEntity);
    createRequest.setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    assertNotNull(createRequest);
    AsyncResponseWrapper<ODataResponse> asyncResponse =
        client.getAsyncRequestFactory().getAsyncRequestWrapper(createRequest).execute();

    assertTrue(asyncResponse.isPreferenceApplied());
    assertFalse(asyncResponse.isDone());

    waitTillDone(asyncResponse, 10);

    @SuppressWarnings("unchecked")
    final ODataEntityCreateResponse<ClientEntity> createResponse =
        (ODataEntityCreateResponse<ClientEntity>) asyncResponse.getODataResponse();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), createResponse.getStatusCode());
    assertEquals(SERVICE_URI + "ESAllPrim(1)", createResponse.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity createdEntity = createResponse.getBody();
    assertNotNull(createdEntity);
    final ClientProperty property1 = createdEntity.getProperty("PropertyInt64");
    assertNotNull(property1);
    assertShortOrInt(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = createdEntity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue().toValue());
  }

  @Test
  public void getBatchRequest() throws Exception {
    ODataClient client = getClient();
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setPrefer(PreferenceName.RESPOND_ASYNC + "; " + TEC_ASYNC_SLEEP + "=1");
    ODataBatchableRequest getRequest = appendGetRequest(client, "ESAllPrim", 32767, false);
    AsyncBatchRequestWrapper asyncRequest =
        client.getAsyncRequestFactory().getAsyncBatchRequestWrapper(request);
    asyncRequest.addRetrieve(getRequest);
    AsyncResponseWrapper<ODataBatchResponse> asyncResponse = asyncRequest.execute();
    assertTrue(asyncResponse.isPreferenceApplied());
    assertFalse(asyncResponse.isDone());

    waitTillDone(asyncResponse, 3);

    final ODataBatchResponse response = asyncResponse.getODataResponse();
    final ODataBatchResponseItem item = response.getBody().next();
    @SuppressWarnings("unchecked")
    final ODataRetrieveResponse<ClientEntity> firstResponse = (ODataRetrieveResponse<ClientEntity>) item.next();
    assertEquals(HttpStatusCode.OK.getStatusCode(), firstResponse.getStatusCode());
    assertEquals(3, firstResponse.getHeaderNames().size());
    assertEquals("4.0", firstResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());

    final ClientEntity entity = firstResponse.getBody();
    assertShortOrInt(32767, entity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("First Resource - positive values",
        entity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }
  
  @Test
  public void entityAction() throws Exception {
    Calendar dateTime = Calendar.getInstance();
    dateTime.clear();
    dateTime.set(1012, 2, 0, 0, 0, 0);
    final Map<String, ClientValue> parameters = Collections.singletonMap(
        "ParameterDate",
        (ClientValue) getFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.Date).setValue(dateTime).build());
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(TecSvcConst.BASE_URI)
        .appendActionCallSegment("AIRTESAllPrimParam").build();

    ODataInvokeRequest<ClientEntity> req = client.getInvokeRequestFactory()
        .getActionInvokeRequest(uri, ClientEntity.class, parameters);
    AsyncRequestWrapper<ODataRetrieveResponse<ClientEntity>>
    asyncReqWrp = client.getAsyncRequestFactory().getAsyncRequestWrapper(req);
    AsyncResponseWrapper<ODataRetrieveResponse<ClientEntity>>
    asyncRespWrp = asyncReqWrp.execute();
    waitTillDone(asyncRespWrp, 5);
    @SuppressWarnings("unchecked")
    ODataInvokeResponse<ClientEntity> response = (ODataInvokeResponse<ClientEntity>)asyncRespWrp.getODataResponse();
   
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    assertEquals(TecSvcConst.BASE_URI  +"/ESAllPrim(1)", response.getHeader(HttpHeader.LOCATION).iterator().next());
  }

  /**
   * Test delete with async prefer header but without async support from TecSvc.
   */
  @Test
  public void deleteEntity() throws Exception {
    final ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(32767).build();

    // asyncDeleteRequest async request
    ODataRequest deleteRequest = client.getCUDRequestFactory().getDeleteRequest(uri)
        .setPrefer("respond-async; " + TEC_ASYNC_SLEEP + "=5");
    AsyncResponseWrapper<ODataResponse> asyncDeleteRequest =
        client.getAsyncRequestFactory().getAsyncRequestWrapper(deleteRequest).execute();

    waitTillDone(asyncDeleteRequest, 5);

    ODataResponse response = asyncDeleteRequest.getODataResponse();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the deleted entity is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory().getEntityRequest(uri);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    try {
      entityRequest.execute();
      fail("Expected exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  private ODataEntityRequest<ClientEntity> appendGetRequest(final ODataClient client,
      final String segment, final Object key, final boolean isRelative) {
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(segment).appendKeySegment(key).build();
    final URI uri = isRelative ? URI.create(SERVICE_URI).relativize(targetURI) : targetURI;
    return client.getRetrieveRequestFactory().getEntityRequest(uri);
  }

  private void checkEntityAvailableWith(ClientEntitySet entitySet, String property, int value) {
    for (ClientEntity entity : entitySet.getEntities()) {
      ClientProperty ep = entity.getProperty(property);
      if (ep != null) {
        assertShortOrInt(value, ep.getPrimitiveValue().toValue());
        return;
      }
    }
    fail("Entity with property '" + property +
        "' and value '" + value + "' not found in entitySet '" + entitySet + "'");
  }

  private void waitTillDone(AsyncResponseWrapper<?> async, int maxWaitInSeconds) throws InterruptedException {
    int waitCounter = maxWaitInSeconds * 1000;
    while (!async.isDone() && waitCounter > 0) {
      Thread.sleep(SLEEP_TIMEOUT_IN_MS);
      waitCounter -= SLEEP_TIMEOUT_IN_MS;
    }
  }
}
