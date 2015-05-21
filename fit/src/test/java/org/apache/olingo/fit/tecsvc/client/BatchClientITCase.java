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
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.fit.v4.AbstractTestITCase;
import org.junit.Before;
import org.junit.Test;

public class BatchClientITCase extends AbstractTestITCase {
  private final static String ACCEPT = ContentType.APPLICATION_OCTET_STREAM.toContentTypeString();
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String SERVICE_NAMESPACE = "olingo.odata.test1";
  private static final String ES_NOT_AVAILABLE_NAME = "ESNotAvailable";
  private static final FullQualifiedName ES_NOT_AVAILABLE = new FullQualifiedName(SERVICE_NAMESPACE,
      ES_NOT_AVAILABLE_NAME);
  private static final String PROPERTY_STRING = "PropertyString";

  @Before
  public void setup() {
    client.getConfiguration().setContinueOnError(false);
  }

  @Test
  public void testBadRequestInChangeSet() {
    /*
     * A bad request (status code >= 400) without "continue on error prefer header" in a changeset
     * should return a single response with Content-Type: application/http
     *
     * See:
     * OData Version 4.0 Part 1: Protocol Plus Errata 01
     * 11.7.4 Responding to a Batch Request
     *
     * When a request within a change set fails, the change set response is not represented using
     * the multipart/mixed media type. Instead, a single response, using the application/http media type
     * and a Content-Transfer-Encoding header with a value of binary, is returned that applies to all requests
     * in the change set and MUST be formatted according to the Error Handling defined
     * for the particular response format.
     */

    final ODataClient client = getClient();
    final ClientObjectFactory of = client.getObjectFactory();

    // Try to create entity, with invalid type
    final ClientEntity entity = of.newEntity(ES_NOT_AVAILABLE);
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder()
        .buildString("1")));
    final ODataBatchRequest batchRequest = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    batchRequest.setAccept(ACCEPT);
    final BatchManager payloadManager = batchRequest.payloadManager();
    final ODataChangeset changeset = payloadManager.addChangeset();
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_NOT_AVAILABLE_NAME)
        .build();
    final ODataEntityCreateRequest<ClientEntity> createRequest = client.getCUDRequestFactory()
        .getEntityCreateRequest(targetURI, entity);
    changeset.addRequest(createRequest);

    final ODataBatchResponse response = payloadManager.getResponse();
    assertEquals(HttpStatusCode.ACCEPTED.getStatusCode(), response.getStatusCode());

    // Check response items
    final Iterator<ODataBatchResponseItem> responseBodyIter = response.getBody();
    assertTrue(responseBodyIter.hasNext());

    final ODataBatchResponseItem changeSetResponse = responseBodyIter.next();
    assertTrue(changeSetResponse.isChangeset());
    assertTrue(changeSetResponse.hasNext());

    final ODataResponse updateResponse = changeSetResponse.next();
    assertTrue(changeSetResponse.isBreaking());

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), updateResponse.getStatusCode());
    //assertEquals(ODataFormat.JSON.toString(), updateResponse.getContentType());
  }

  @Test
  public void emptyBatchRequest() {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    final ODataBatchResponse response = payload.getResponse();

    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertFalse(iter.hasNext());
  }

  @Test
  public void getBatchRequestWithRelativeUris() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    // create new request
    appendGetRequest(payload, "ESAllPrim", 32767, true);

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();

    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());

    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResonse.getStatusCode());
    assertEquals(1, oDataResonse.getHeader("OData-Version").size());
    assertEquals("4.0", oDataResonse.getHeader("OData-Version").toArray()[0]);
    assertEquals(1, oDataResonse.getHeader("Content-Length").size());
    assertEquals("538", oDataResonse.getHeader("Content-Length").toArray()[0]);
    assertEquals("application/json;odata.metadata=minimal", oDataResonse.getContentType());
  }

  @Test
  public void getBatchRequest() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    // create new request
    appendGetRequest(payload, "ESAllPrim", 32767, false);

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();

    assertEquals(202, response.getStatusCode());
    assertEquals("Accepted", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());

    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResonse.getStatusCode());
    assertEquals(1, oDataResonse.getHeader("OData-Version").size());
    assertEquals("4.0", oDataResonse.getHeader("OData-Version").toArray()[0]);
    assertEquals(1, oDataResonse.getHeader("Content-Length").size());
    assertEquals("538", oDataResonse.getHeader("Content-Length").toArray()[0]);
    assertEquals("application/json;odata.metadata=minimal", oDataResonse.getContentType());
  }

  @Test
  public void testErrorWithoutContinueOnErrorPreferHeader() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    appendGetRequest(payload, "ESAllPrim", 32767, false); // Without error
    appendGetRequest(payload, "ESAllPrim", 42, false); // Error ( Key does not exist )
    appendGetRequest(payload, "ESAllPrim", 0, false); // Without error

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    assertEquals(202, response.getStatusCode());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();

    // Check first get request
    assertTrue(iter.hasNext());
    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResonse.getStatusCode());
    assertEquals(1, oDataResonse.getHeader("OData-Version").size());
    assertEquals("4.0", oDataResonse.getHeader("OData-Version").toArray()[0]);
    assertEquals(1, oDataResonse.getHeader("Content-Length").size());
    assertEquals("538", oDataResonse.getHeader("Content-Length").toArray()[0]);
    assertEquals("application/json;odata.metadata=minimal", oDataResonse.getContentType());

    // Check second get request
    assertTrue(iter.hasNext());
    item = iter.next();
    assertFalse(item.isChangeset());

    oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), oDataResonse.getStatusCode());

    // Check if third request is available
    assertFalse(iter.hasNext());
  }

  @Test
  public void testInvalidAbsoluteUri() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    final URI uri = new URI(SERVICE_URI + "/../ESAllPrim(32767)");
    final ODataEntityRequest<ClientEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(uri);
    queryReq.setFormat(ODataFormat.JSON);
    payload.addRequest(queryReq);

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    assertEquals(202, response.getStatusCode());

    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();
    assertTrue(bodyIterator.hasNext());

    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());

    final ODataResponse oDataResponse = item.next();
    assertEquals(400, oDataResponse.getStatusCode());
  }

  @Test(expected = HttpClientException.class)
  public void testInvalidHost() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    final URI uri = new URI("http://otherhost/odata/ESAllPrim(32767)");
    final ODataEntityRequest<ClientEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(uri);
    queryReq.setFormat(ODataFormat.JSON);
    payload.addRequest(queryReq);

    // Fetch result
    payload.getResponse();
  }

  @Test(expected = HttpClientException.class)
  public void testInvalidAbsoluteRequest() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    final URI uri = new URI("/ESAllPrim(32767)");
    final ODataEntityRequest<ClientEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(uri);
    queryReq.setFormat(ODataFormat.JSON);
    payload.addRequest(queryReq);

    // Fetch result
    payload.getResponse();
  }

  @Test
  public void testErrorWithContinueOnErrorPreferHeader() throws URISyntaxException {
    client.getConfiguration().setContinueOnError(true);
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    appendGetRequest(payload, "ESAllPrim", 32767, false); // Without error
    appendGetRequest(payload, "ESAllPrim", 42, false); // Error ( Key does not exist )
    appendGetRequest(payload, "ESAllPrim", 0, false); // Without error

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    assertEquals(202, response.getStatusCode());

    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();

    // Check first get request
    assertTrue(bodyIterator.hasNext());
    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResonse.getStatusCode());
    assertEquals(1, oDataResonse.getHeader("OData-Version").size());
    assertEquals("4.0", oDataResonse.getHeader("OData-Version").toArray()[0]);
    assertEquals(1, oDataResonse.getHeader("Content-Length").size());
    assertEquals("538", oDataResonse.getHeader("Content-Length").toArray()[0]);
    assertEquals("application/json;odata.metadata=minimal", oDataResonse.getContentType());

    // Check second get request
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());

    oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), oDataResonse.getStatusCode());

    // Check if third request is available
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());

    oDataResonse = item.next();
    assertNotNull(oDataResonse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResonse.getStatusCode());
    assertEquals(1, oDataResonse.getHeader("OData-Version").size());
    assertEquals("4.0", oDataResonse.getHeader("OData-Version").toArray()[0]);
    assertEquals(1, oDataResonse.getHeader("Content-Length").size());
    assertEquals("446", oDataResonse.getHeader("Content-Length").toArray()[0]);
    assertEquals("application/json;odata.metadata=minimal", oDataResonse.getContentType());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void changesetWithReferences() throws EdmPrimitiveTypeException, URISyntaxException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();
    request.setAccept(ACCEPT);
    final BatchManager streamManager = request.payloadManager();

    final ODataChangeset changeset = streamManager.addChangeset();
    final ClientEntity entityESAllPrim = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));

    entityESAllPrim.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    entityESAllPrim.addLink(
        of.newEntityNavigationLink("NavPropertyETTwoPrimOne", client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(-365)
            .build()));

    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim");

    // add create request
    final ODataEntityCreateRequest<ClientEntity> createReq =
        client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), entityESAllPrim);
    createReq.setFormat(ODataFormat.JSON);
    changeset.addRequest(createReq);

    // retrieve request reference
    int createRequestRef = changeset.getLastContentId();

    // add update request
    final ClientEntity entityUpdate = client.getObjectFactory().newEntity(entityESAllPrim.getTypeName());
    entityUpdate.addLink(client.getObjectFactory().newEntitySetNavigationLink(
        "NavPropertyETTwoPrimMany",
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767).build()));

    final ODataEntityUpdateRequest<ClientEntity> updateReq = client.getCUDRequestFactory().getEntityUpdateRequest(
        URI.create("$" + createRequestRef), UpdateType.PATCH, entityUpdate);
    updateReq.setFormat(ODataFormat.JSON);

    changeset.addRequest(updateReq);

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(HttpStatusCode.ACCEPTED.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // verify response payload ...
    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();
    final ODataBatchResponseItem item = bodyIterator.next();

    assertTrue(item instanceof ODataChangesetResponseItem);
    final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;
    assertTrue(chgitem.hasNext());
    ODataResponse res = chgitem.next();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), res.getStatusCode());
    assertTrue(res instanceof ODataEntityCreateResponse);
    final ODataEntityCreateResponse<ClientEntity> createResponse = ((ODataEntityCreateResponse<ClientEntity>) res);

    res = chgitem.next();
    assertEquals(HttpStatusCode.OK.getStatusCode(), res.getStatusCode());
    assertTrue(res instanceof ODataEntityUpdateResponse);

    final ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(
        new URI(createResponse.getHeader(HttpHeader.LOCATION).iterator().next() + "/NavPropertyETTwoPrimMany"));
    req.setFormat(ODataFormat.JSON);
    req.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntitySet> getResponse = req.execute();

    assertEquals(32767, getResponse.getBody()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void changesetBatchRequest() throws URISyntaxException {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    // -----------------------------
    // - Append get request
    // -----------------------------
    appendGetRequest(payload, "ESAllPrim", 32767, false); // Without error

    // -----------------------------
    // - Append change set
    // -----------------------------
    final ODataChangeset changeset = payload.addChangeset();

    // ------------------------
    // POST request (Insert)
    URIBuilder targetURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim");
    URI editLink = targetURI.build();

    ClientEntity postEntity = client.getObjectFactory().newEntity(
        new FullQualifiedName("olingo.odata.test1.ESAllPrim"));
    postEntity.addLink(of.newEntityNavigationLink("NavPropertyETTwoPrimOne", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim")
        .appendKeySegment(32766)
        .build()));

    postEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    final ODataEntityCreateRequest<ClientEntity> createRequest =
        client.getCUDRequestFactory().getEntityCreateRequest(editLink, postEntity);
    createRequest.setFormat(ODataFormat.JSON);

    changeset.addRequest(createRequest);

    // ------------------------
    // Patch request (Update)
    targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(0);
    editLink = targetURI.build();

    ClientEntity patchEntity = client.getObjectFactory()
        .newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));
    patchEntity.setEditLink(editLink);

    patchEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    ODataEntityUpdateRequest<ClientEntity> changeReq =
        client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patchEntity);
    changeReq.setFormat(ODataFormat.JSON);
    changeset.addRequest(changeReq);

    // ------------------------
    // Patch request (Upsert)
    targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(15);
    editLink = targetURI.build();

    patchEntity = client.getObjectFactory().newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));
    patchEntity.setEditLink(editLink);

    patchEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    patchEntity.addLink(of.newEntityNavigationLink("NavPropertyETTwoPrimOne", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim")
        .appendKeySegment(32766)
        .build()));

    changeReq = client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patchEntity);
    changeReq.setFormat(ODataFormat.JSON);
    changeset.addRequest(changeReq);

    // -----------------------------
    // - Append get request
    // -----------------------------
    appendGetRequest(payload, "ESAllPrim", 0, false); // Without error

    // -----------------------------
    // - Fetch result
    // -----------------------------
    final ODataBatchResponse response = payload.getResponse();
    assertEquals(202, response.getStatusCode());
    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();

    // Check first get request
    assertTrue(bodyIterator.hasNext());
    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());
    assertTrue(item.hasNext());
    final ODataResponse response0 = item.next();
    assertTrue(response0 instanceof ODataRetrieveResponse);
    assertEquals(34, ((ODataRetrieveResponse<ClientEntity>) response0).getBody()
        .getProperty("PropertyDecimal")
        .getPrimitiveValue()
        .toValue());

    // Check change set
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertTrue(item.isChangeset());

    // Insert
    assertTrue(item.hasNext());
    final ODataResponse response1 = item.next();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response1.getStatusCode());
    assertTrue(response1 instanceof ODataEntityCreateResponse);
    assertEquals(3.1415, ((ODataEntityCreateResponse<ClientEntity>) response1).getBody().getProperty("PropertyDouble")
        .getPrimitiveValue()
        .toValue());
    // Update
    assertTrue(item.hasNext());
    final ODataResponse response2 = item.next();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response2.getStatusCode());
    assertTrue(response2 instanceof ODataEntityUpdateResponse);

    // Upsert
    assertTrue(item.hasNext());
    final ODataResponse response3 = item.next();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response3.getStatusCode());
    assertTrue(response3 instanceof ODataEntityUpdateResponse);
    assertEquals(3.1415, ((ODataEntityUpdateResponse<ClientEntity>) response3).getBody().getProperty("PropertyDouble")
        .getPrimitiveValue()
        .toValue());

    // Check second get request
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());
    assertTrue(item.hasNext());
    final ODataResponse response4 = item.next();
    assertTrue(response4 instanceof ODataRetrieveResponse);
    assertEquals(3.1415, ((ODataRetrieveResponse<ClientEntity>) response4).getBody()
        .getProperty("PropertyDouble")
        .getPrimitiveValue()
        .toValue());
  }

  private void appendGetRequest(final BatchManager manager, final String segment, final Object key,
      final boolean isRelative)
      throws URISyntaxException {
    final URIBuilder targetURI = client.newURIBuilder(SERVICE_URI);
    targetURI.appendEntitySetSegment(segment).appendKeySegment(key);
    final URI uri = (isRelative) ? new URI(SERVICE_URI).relativize(targetURI.build()) : targetURI.build();

    ODataEntityRequest<ClientEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(uri);
    queryReq.setFormat(ODataFormat.JSON);
    manager.addRequest(queryReq);
  }
}
