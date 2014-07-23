/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.v3;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product");
    final Future<ODataRetrieveResponse<ODataEntitySet>> futureRes =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).asyncExecute();
    assertNotNull(futureRes);

    while (!futureRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataRetrieveResponse<ODataEntitySet> res = futureRes.get();
    assertNotNull(res);
    assertEquals(200, res.getStatusCode());
    assertFalse(res.getBody().getEntities().isEmpty());
  }

  @Test
  public void updateEntity() throws InterruptedException, ExecutionException {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Product").appendKeySegment(-10).build();

    final ODataRetrieveResponse<ODataEntity> entityRes = client.getRetrieveRequestFactory().
        getEntityRequest(uri).execute();
    final ODataEntity entity = entityRes.getBody();
    entity.getAssociationLinks().clear();
    entity.getNavigationLinks().clear();
    entity.getMediaEditLinks().clear();

    entity.getProperties().remove(entity.getProperty("Description"));
    getClient().getBinder().add(entity,
        client.getObjectFactory().newPrimitiveProperty("Description",
            client.getObjectFactory().newPrimitiveValueBuilder().setValue("AsyncTest#updateEntity").build()));

    final ODataEntityUpdateRequest<ODataEntity> updateReq =
        client.getCUDRequestFactory().getEntityUpdateRequest(uri, UpdateType.MERGE, entity);
    updateReq.setIfMatch(entityRes.getETag());
    final Future<ODataEntityUpdateResponse<ODataEntity>> futureRes = updateReq.asyncExecute();

    while (!futureRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataEntityUpdateResponse<ODataEntity> res = futureRes.get();
    assertNotNull(res);
    assertEquals(204, res.getStatusCode());
  }

  @Test
  public void createMediaEntity() throws Exception {
    URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Car");

    final String TO_BE_UPDATED = "async buffered stream sample";
    final InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

    final ODataMediaEntityCreateRequest<ODataEntity> createReq =
        client.getCUDRequestFactory().getMediaEntityCreateRequest(builder.build(), input);

    final MediaEntityCreateStreamManager<ODataEntity> streamManager = createReq.payloadManager();
    final Future<ODataMediaEntityCreateResponse<ODataEntity>> futureCreateRes = streamManager.getAsyncResponse();

    while (!futureCreateRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataMediaEntityCreateResponse<ODataEntity> createRes = futureCreateRes.get();
    assertEquals(201, createRes.getStatusCode());

    final ODataEntity created = createRes.getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperties().size());

    final int id = "VIN".equals(created.getProperties().get(0).getName())
        ? created.getProperties().get(0).getPrimitiveValue().toCastValue(Integer.class)
        : created.getProperties().get(1).getPrimitiveValue().toCastValue(Integer.class);

    builder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Car").appendKeySegment(id);

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaEntityRequest(builder.build());

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());
    assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
  }
}
