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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.v4.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.fit.v4.AbstractTestITCase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BatchClientITCase extends AbstractTestITCase {
  private final static String ACCEPT = ContentType.APPLICATION_OCTET_STREAM.toContentTypeString();
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  @Before
  public void setup() {
    client.getConfiguration().setContinueOnError(false);
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
  public void getBatchRequest() {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    // create new request
    appendGetRequest(payload, "ESAllPrim", 32767);

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
  public void testErrorWithoutContinueOnErrorPreferHeader() {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    appendGetRequest(payload, "ESAllPrim", 32767); // Without error
    appendGetRequest(payload, "ESAllPrim", 42); // Error ( Key does not exist )
    appendGetRequest(payload, "ESAllPrim", 0); // Without error

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
  public void testErrorWithContinueOnErrorPreferHeader() {
    client.getConfiguration().setContinueOnError(true);

    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();

    appendGetRequest(payload, "ESAllPrim", 32767); // Without error
    appendGetRequest(payload, "ESAllPrim", 42); // Error ( Key does not exist )
    appendGetRequest(payload, "ESAllPrim", 0); // Without error

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

  @SuppressWarnings("unchecked")
  @Test
  @Ignore("Not implemented")
  public void changesetWithReferences() throws EdmPrimitiveTypeException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);
    final BatchManager streamManager = request.payloadManager();

    final ODataChangeset changeset = streamManager.addChangeset();
    ODataEntity esAllPrim = newESAllPrim((short) 23);

    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim");

    // add create request
    final ODataEntityCreateRequest<ODataEntity> createReq =
        client.getCUDRequestFactory().getEntityCreateRequest(uriBuilder.build(), esAllPrim);

    changeset.addRequest(createReq);

    // retrieve request reference
    int createRequestRef = changeset.getLastContentId();

    // add update request
    final ODataEntity customerChanges = client.getObjectFactory().newEntity(esAllPrim.getTypeName());
    customerChanges.addLink(client.getObjectFactory().newEntitySetNavigationLink(
        "NavPropertyETTwoPrimMany",
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("NavPropertyETTwoPrimMany").
            appendKeySegment(new HashMap<String, Object>() {
              private static final long serialVersionUID = 3109256773218160485L;

              {
                put("PropertyInt16", 4242);
                put("PropertyString", "Test");
              }
            }).build()));

    final ODataEntityUpdateRequest<ODataEntity> updateReq = client.getCUDRequestFactory().getEntityUpdateRequest(
        URI.create("$" + createRequestRef), UpdateType.PATCH, customerChanges);

    changeset.addRequest(updateReq);

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(200, response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());

    // verify response payload ...
    final Iterator<ODataBatchResponseItem> iter = response.getBody();

    final ODataBatchResponseItem item = iter.next();
    assertTrue(item instanceof ODataChangesetResponseItem);

    final ODataChangesetResponseItem chgitem = (ODataChangesetResponseItem) item;

    ODataResponse res = chgitem.next();
    assertEquals(201, res.getStatusCode());
    assertTrue(res instanceof ODataEntityCreateResponse);

    esAllPrim = ((ODataEntityCreateResponse<ODataEntity>) res).getBody();
    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(
        URIUtils.getURI(SERVICE_URI, esAllPrim.getEditLink().toASCIIString() + "/NavPropertyETTwoPrimMany"));

    assertEquals(Integer.valueOf(4242),
        req.execute().getBody().getEntities().get(0).getProperty("PropertyInt16").getPrimitiveValue().
            toCastValue(Integer.class));

    res = chgitem.next();
    assertEquals(204, res.getStatusCode());
    assertTrue(res instanceof ODataEntityUpdateResponse);

    // clean ...
    assertEquals(204, client.getCUDRequestFactory().getDeleteRequest(
        URIUtils.getURI(SERVICE_URI, esAllPrim.getEditLink().toASCIIString())).execute().
        getStatusCode());

    try {
      client.getRetrieveRequestFactory().getEntityRequest(
          URIUtils.getURI(SERVICE_URI, esAllPrim.getEditLink().toASCIIString())).
          execute().getBody();
      fail("Entity not deleted");
    } catch (Exception e) {
      // ignore
    }
  }

  private ODataEntity newESAllPrim(short id) {
    final ODataEntity entity = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));

    entity.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16(id)));

    entity.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    return entity;
  }

  //TODO If write support is implemented, remove ignore tag
  @Test
  @Ignore("Not implemented")
  public void changesetBatchRequest() {
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(SERVICE_URI);
    request.setAccept(ACCEPT);

    final BatchManager payload = request.payloadManager();
    // -----------------------------
    // - Append get request
    // -----------------------------
    appendGetRequest(payload, "ESAllPrim", 32767); // Without error

    // -----------------------------
    // - Append change set
    // -----------------------------
    final ODataChangeset changeset = payload.addChangeset();

    // ------------------------
    // POST request (Insert)
    URIBuilder targetURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim");
    URI editLink = targetURI.build();

    ODataEntity post = client.getObjectFactory().newEntity(
        new FullQualifiedName("olingo.odata.test1.ESAllPrim"));

    post.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyInt16",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt16((short) 15)));

    post.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    final ODataEntityCreateRequest<ODataEntity> createRequest =
        client.getCUDRequestFactory().getEntityCreateRequest(editLink, post);
    createRequest.setFormat(ODataFormat.JSON_FULL_METADATA);
    createRequest.setContentType("1");

    changeset.addRequest(createRequest);

    // ------------------------
    // Patch request (Update)
    targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(0);
    editLink = targetURI.build();

    ODataEntity patch = client.getObjectFactory().newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));
    patch.setEditLink(editLink);

    patch.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    ODataEntityUpdateRequest<ODataEntity> changeReq =
        client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);
    changeReq.setFormat(ODataFormat.JSON_FULL_METADATA);
    changeReq.setContentType("2");
    changeset.addRequest(changeReq);

    // ------------------------
    // Patch request (Upsert)
    targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(35);
    editLink = targetURI.build();

    patch = client.getObjectFactory().newEntity(new FullQualifiedName("olingo.odata.test1.ESAllPrim"));
    patch.setEditLink(editLink);

    patch.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "PropertyDouble",
        client.getObjectFactory().newPrimitiveValueBuilder().buildDouble(3.1415)));

    changeReq = client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);
    changeReq.setFormat(ODataFormat.JSON_FULL_METADATA);
    changeReq.setContentType("3");
    changeset.addRequest(changeReq);

    // -----------------------------
    // - Append get request
    // -----------------------------
    appendGetRequest(payload, "ESAllPrim", 32767); // Without error

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

    // Check change set
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertTrue(item.isChangeset());

    for (int i = 0; i < 3; i++) {
      assertTrue(item.hasNext());
      assertTrue(item instanceof ODataChangesetResponseItem);
      ODataChangesetResponseItem changeSetResponseItem = (ODataChangesetResponseItem) item.next();
      assertNotNull(changeSetResponseItem);

      ODataResponse chgRequest = changeSetResponseItem.next();
      final String contentId = chgRequest.getHeader(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME).iterator().next();

      if (contentId == "1") {
        // Insert
        assertEquals(HttpStatusCode.CREATED.getStatusCode(), chgRequest.getStatusCode());
      } else if (contentId == "2") {
        // Update
        assertEquals(HttpStatusCode.OK.getStatusCode(), chgRequest.getStatusCode());
      } else if (contentId == "3") {
        // Upsert
        assertEquals(HttpStatusCode.CREATED.getStatusCode(), chgRequest.getStatusCode());
      } else {
        fail("Unkonwn content id " + contentId);
      }
    }
    assertFalse(item.hasNext());

    // Check second get request
    assertTrue(bodyIterator.hasNext());
    item = bodyIterator.next();
    assertFalse(item.isChangeset());
  }

  private void appendGetRequest(final BatchManager manager, final String segment, final Object key) {
    URIBuilder targetURI = client.newURIBuilder(SERVICE_URI);
    targetURI.appendEntitySetSegment(segment).appendKeySegment(key);

    ODataEntityRequest<ODataEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());
    queryReq.setFormat(ODataFormat.JSON);
    manager.addRequest(queryReq);
  }
}
