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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Iterator;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.PreferenceName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Before;
import org.junit.Test;

public class BatchClientITCase extends AbstractParamTecSvcITCase {

  private static final String ES_NOT_AVAILABLE_NAME = "ESNotAvailable";
  private static final FullQualifiedName ES_NOT_AVAILABLE =
      new FullQualifiedName(SERVICE_NAMESPACE, ES_NOT_AVAILABLE_NAME);
  private static final String PROPERTY_STRING = "PropertyString";

  @Before
  public void setup() {
    getClient().getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    getClient().getConfiguration().setContinueOnError(false);
  }

  @Test
  public void badRequestInChangeSet() {
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

    // Try to create entity, with invalid type
    ClientObjectFactory factory = getFactory();
    final ClientEntity entity = factory.newEntity(ES_NOT_AVAILABLE);
    entity.getProperties().add(
        factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder().buildString("1")));
    BatchManager payloadManager = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI).payloadManager();
    final ODataChangeset changeset = payloadManager.addChangeset();
    final URI targetURI = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_NOT_AVAILABLE_NAME)
        .build();
    final ODataEntityCreateRequest<ClientEntity> createRequest = getClient().getCUDRequestFactory()
        .getEntityCreateRequest(targetURI, entity);
    changeset.addRequest(createRequest);

    final ODataBatchResponse response = payloadManager.getResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check response items
    final Iterator<ODataBatchResponseItem> responseBodyIter = response.getBody();
    assertTrue(responseBodyIter.hasNext());

    final ODataBatchResponseItem changeSetResponse = responseBodyIter.next();
    assertTrue(changeSetResponse.isChangeset());
    assertTrue(changeSetResponse.hasNext());

    final ODataResponse updateResponse = changeSetResponse.next();
    assertTrue(changeSetResponse.isBreaking());

    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), updateResponse.getStatusCode());
    assertContentType(updateResponse.getContentType());
  }

  @Test
  public void emptyBatchRequest() {
    // create your request
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    final ODataBatchResponse response = request.payloadManager().getResponse();
    saveCookieHeader(response);

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertFalse(iter.hasNext());
  }

  @Test
  public void getBatchRequestWithRelativeUris() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    payload.addRequest(createGetRequest("ESAllPrim", 32767, true));

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());

    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.ODATA_VERSION).size());
    assertEquals("4.0", oDataResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).size());
    assertEquals(isJson() ? "605" : "2223", oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).iterator().next());
    assertContentType(oDataResponse.getContentType());
  }

  @Test
  public void getBatchRequest() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();

    // create new request
    payload.addRequest(createGetRequest("ESAllPrim", 32767, false));

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());

    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.ODATA_VERSION).size());
    assertEquals("4.0", oDataResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).size());
    assertEquals(isJson() ? "605" : "2223", oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).iterator().next());
    assertContentType(oDataResponse.getContentType());
  }

  @Test
  public void binaryContent() throws Exception {
    BatchManager payload = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI).payloadManager();
    ODataMediaEntityCreateRequest<ClientEntity> request = getClient().getCUDRequestFactory()
        .getMediaEntityCreateRequest(
            getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESMedia").build(),
            new ByteArrayInputStream(new byte[] { -42, 0, 1 }));
    request.setContentType(ContentType.APPLICATION_OCTET_STREAM.toContentTypeString());
    request.addCustomHeader("Custom-Header-0123456789", "!!!");
    payload.addRequest(request);

    payload.addRequest(getClient().getRetrieveRequestFactory().getMediaRequest(URI.create(
        "ESMedia(5)/$value")));

    final ODataBatchResponse response = payload.getResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());
    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), oDataResponse.getStatusCode());
    assertFalse(item.hasNext());

    assertTrue(iter.hasNext());
    oDataResponse = iter.next().next();
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(ContentType.APPLICATION_OCTET_STREAM.toContentTypeString(), oDataResponse.getContentType());
    // TODO: Correct the client code so that the following content verification can be enabled.
//    InputStream rawResponse = oDataResponse.getRawResponse();
//    assertEquals(-42, (byte) rawResponse.read());
//    assertEquals(0, rawResponse.read());
//    assertEquals(1, rawResponse.read());
//    assertEquals(-1, rawResponse.read());
    assertFalse(iter.hasNext());
  }

  @Test
  public void absolutePath() {
    BatchManager payload = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI).payloadManager();
    final URI uri = URI.create(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767).build()
            .getRawPath());
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    setCookieHeader(request);
    request.addCustomHeader("Custom-Header-0123456789", "!!!");
    payload.addRequest(request);

    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Iterator<ODataBatchResponseItem> iter = response.getBody();
    assertTrue(iter.hasNext());
    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertFalse(item.hasNext());
    assertFalse(iter.hasNext());
  }

  @Test
  public void errorWithoutContinueOnErrorPreferHeader() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    payload.addRequest(createGetRequest("ESAllPrim", 32767, false)); // Without error
    payload.addRequest(createGetRequest("ESAllPrim", 42, false)); // Error ( Key does not exist )
    payload.addRequest(createGetRequest("ESAllPrim", 0, false)); // Without error

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final Iterator<ODataBatchResponseItem> iter = response.getBody();

    // Check first get request
    assertTrue(iter.hasNext());
    ODataBatchResponseItem item = iter.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.ODATA_VERSION).size());
    assertEquals("4.0", oDataResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).size());
    assertEquals(isJson() ? "605" : "2223", oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).iterator().next());
    assertContentType(oDataResponse.getContentType());

    // Check second get request
    assertTrue(iter.hasNext());
    item = iter.next();
    assertFalse(item.isChangeset());

    oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), oDataResponse.getStatusCode());

    // Check if third request is available
    assertFalse(iter.hasNext());
  }

  @Test
  public void invalidAbsoluteUri() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    ODataEntityRequest<ClientEntity> queryReq = getClient().getRetrieveRequestFactory().getEntityRequest(URI.create(
        SERVICE_URI + "../ESAllPrim(32767)"));
    queryReq.setFormat(getContentType());
    payload.addRequest(queryReq);

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();
    assertTrue(bodyIterator.hasNext());

    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());

    final ODataResponse oDataResponse = item.next();
    assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), oDataResponse.getStatusCode());
  }

  @Test
  public void invalidHost() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    payload.addRequest(getClient().getRetrieveRequestFactory().getEntityRequest(URI.create(
        "http://otherhost/odata/ESAllPrim(32767)")));

    try {
      payload.getResponse();
    } catch (final HttpClientException e) {
      assertTrue(e.getCause().getCause() instanceof ODataClientErrorException);
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(),
          ((ODataClientErrorException) e.getCause().getCause()).getStatusLine().getStatusCode());
    }
  }

  @Test
  public void invalidAbsoluteRequest() {
    ODataBatchRequest request = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    payload.addRequest(getClient().getRetrieveRequestFactory().getEntityRequest(URI.create(
        "/ESAllPrim(32767)")));

    try {
      payload.getResponse();
    } catch (final HttpClientException e) {
      assertTrue(e.getCause().getCause() instanceof ODataClientErrorException);
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(),
          ((ODataClientErrorException) e.getCause().getCause()).getStatusLine().getStatusCode());
    }
  }

  @Test
  public void errorWithContinueOnErrorPreferHeader() {
    ODataClient client = getClient();
    client.getConfiguration().setContinueOnError(true);
    ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    setCookieHeader(request);
    BatchManager payload = request.payloadManager();
    payload.addRequest(createGetRequest("ESAllPrim", 32767, false)); // Without error
    payload.addRequest(createGetRequest("ESAllPrim", 42, false)); // Error ( Key does not exist )
    payload.addRequest(createGetRequest("ESAllPrim", 0, false)); // Without error

    // Fetch result
    final ODataBatchResponse response = payload.getResponse();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(PreferenceName.CONTINUE_ON_ERROR.getName(),
        response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());

    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();

    // Check first get request
    assertTrue(bodyIterator.hasNext());
    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());

    ODataResponse oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.ODATA_VERSION).size());
    assertEquals("4.0", oDataResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).size());
    assertEquals(isJson() ? "605" : "2223", oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).iterator().next());
    assertContentType(oDataResponse.getContentType());

    // Check second get request
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());

    oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), oDataResponse.getStatusCode());

    // Check if third request is available
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());

    oDataResponse = item.next();
    assertNotNull(oDataResponse);
    assertEquals(HttpStatusCode.OK.getStatusCode(), oDataResponse.getStatusCode());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.ODATA_VERSION).size());
    assertEquals("4.0", oDataResponse.getHeader(HttpHeader.ODATA_VERSION).iterator().next());
    assertEquals(1, oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).size());
    assertEquals(isJson() ? "517" : "2114", oDataResponse.getHeader(HttpHeader.CONTENT_LENGTH).iterator().next());
    assertContentType(oDataResponse.getContentType());
  }

  @Test
  public void changesetWithReferences() {
    BatchManager payload = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI).payloadManager();

    final ODataChangeset changeset = payload.addChangeset();
    final ClientEntity entityESAllPrim = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));

    entityESAllPrim.getProperties().add(getFactory().newPrimitiveProperty("PropertyDouble",
        getFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    entityESAllPrim.addLink(
        getFactory().newEntityNavigationLink("NavPropertyETTwoPrimOne", getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(-365)
            .build()));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").build();

    // add create request
    final ODataEntityCreateRequest<ClientEntity> createReq =
        getClient().getCUDRequestFactory().getEntityCreateRequest(uri, entityESAllPrim);
    createReq.setFormat(getContentType());
    changeset.addRequest(createReq);

    // retrieve request reference
    int createRequestRef = changeset.getLastContentId();

    // add update request
    final ClientEntity entityUpdate = getFactory().newEntity(entityESAllPrim.getTypeName());
    entityUpdate.addLink(getFactory().newEntitySetNavigationLink("NavPropertyETTwoPrimMany",
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767).build()));

    final ODataEntityUpdateRequest<ClientEntity> updateReq = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        URI.create("$" + createRequestRef), UpdateType.PATCH, entityUpdate);
    updateReq.setFormat(getContentType());

    changeset.addRequest(updateReq);

    final ODataBatchResponse response = payload.getResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // verify response payload ...
    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();
    final ODataBatchResponseItem item = bodyIterator.next();

    assertTrue(item.hasNext());
    ODataResponse res = item.next();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), res.getStatusCode());
    assertTrue(res instanceof ODataEntityCreateResponse);
    final ODataEntityCreateResponse<?> createResponse = ((ODataEntityCreateResponse<?>) res);

    res = item.next();
    assertEquals(HttpStatusCode.OK.getStatusCode(), res.getStatusCode());
    assertTrue(res instanceof ODataEntityUpdateResponse);

    final ODataEntitySetRequest<ClientEntitySet> req = getClient().getRetrieveRequestFactory().getEntitySetRequest(
        URI.create(createResponse.getHeader(HttpHeader.LOCATION).iterator().next() + "/NavPropertyETTwoPrimMany"));
    req.setFormat(getContentType());
    req.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntitySet> getResponse = req.execute();

    assertShortOrInt(32767, getResponse.getBody()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  public void changesetBatchRequest() {
    BatchManager payload = getClient().getBatchRequestFactory().getBatchRequest(SERVICE_URI).payloadManager();
    // -----------------------------
    // - Append get request
    // -----------------------------
    payload.addRequest(createGetRequest("ESAllPrim", 32767, false));

    // -----------------------------
    // - Append change set
    // -----------------------------
    final ODataChangeset changeset = payload.addChangeset();

    // -----------------------------
    // POST request (Insert)
    // -----------------------------
    URI targetURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").build();

    ClientObjectFactory factory = getFactory();
    ClientEntity postEntity = factory.newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));
    postEntity.addLink(factory.newEntityNavigationLink("NavPropertyETTwoPrimOne", getClient().newURIBuilder
            (SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .build()));

    postEntity.getProperties().add(factory.newPrimitiveProperty("PropertyDouble",
            factory.newPrimitiveValueBuilder().buildDouble(3.1415)));

    final ODataEntityCreateRequest<ClientEntity> createRequest =
        getClient().getCUDRequestFactory().getEntityCreateRequest(targetURI, postEntity);
    createRequest.setFormat(getContentType());

    changeset.addRequest(createRequest);

    // -----------------------------
    // Patch request (Update)
    // -----------------------------
    ClientEntity patchEntity = factory.newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));
    patchEntity.setEditLink(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(0).build());

    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyDouble",
            factory.newPrimitiveValueBuilder().buildDouble(3.1415)));

    ODataEntityUpdateRequest<ClientEntity> changeReq =
        getClient().getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patchEntity);
    changeReq.setFormat(getContentType());
    changeset.addRequest(changeReq);

    // -----------------------------
    // Patch request (Upsert)
    // -----------------------------
    patchEntity = factory.newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim"));
    patchEntity.setEditLink(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(15).build());

    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyDouble",
            factory.newPrimitiveValueBuilder().buildDouble(3.1415)));

    patchEntity.addLink(factory.newEntityNavigationLink("NavPropertyETTwoPrimOne", getClient().newURIBuilder
            (SERVICE_URI)
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(32766)
            .build()));

    changeReq = getClient().getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patchEntity);
    changeReq.setFormat(getContentType());
    changeset.addRequest(changeReq);

    // -----------------------------
    // - Append get request
    // -----------------------------
    payload.addRequest(createGetRequest("ESAllPrim", 0, false));

    // -----------------------------
    // - Fetch result
    // -----------------------------
    final ODataBatchResponse response = payload.getResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    final Iterator<ODataBatchResponseItem> bodyIterator = response.getBody();

    // Check first get request
    assertTrue(bodyIterator.hasNext());
    ODataBatchResponseItem item = bodyIterator.next();
    assertFalse(item.isChangeset());
    assertTrue(item.hasNext());
    final ODataResponse response0 = item.next();
    assertTrue(response0 instanceof ODataRetrieveResponse);
    @SuppressWarnings("unchecked")
    ODataRetrieveResponse<ClientEntity> retrieveResponse = (ODataRetrieveResponse<ClientEntity>) response0;
    assertShortOrInt(34, retrieveResponse.getBody()
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
    assertEquals(3.1415, ((ODataEntityCreateResponse<?>) response1).getBody().getProperty("PropertyDouble")
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
    assertEquals(3.1415, ((ODataEntityUpdateResponse<?>) response3).getBody().getProperty("PropertyDouble")
        .getPrimitiveValue()
        .toValue());

    // Check second get request
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());
    assertTrue(item.hasNext());
    final ODataResponse response4 = item.next();
    assertTrue(response4 instanceof ODataRetrieveResponse);
    @SuppressWarnings("unchecked")
    final ODataRetrieveResponse<ClientEntity> retrieveResponse2 = (ODataRetrieveResponse<ClientEntity>) response4;
    assertEquals(3.1415, retrieveResponse2.getBody()
        .getProperty("PropertyDouble")
        .getPrimitiveValue()
        .toValue());
  }

  private ODataEntityRequest<ClientEntity> createGetRequest(final String segment, final Object key,
      final boolean isRelative) {
    final URI targetURI = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(segment).appendKeySegment(key)
        .build();
    final URI uri = isRelative ? URI.create(SERVICE_URI).relativize(targetURI) : targetURI;

    ODataEntityRequest<ClientEntity> queryReq = getClient().getRetrieveRequestFactory().getEntityRequest(uri);
    queryReq.setAccept(getContentType().toContentTypeString());
    return queryReq;
  }
}
