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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.junit.Ignore;
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void retrieveEntitySet() throws InterruptedException, ExecutionException {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
    final URI uri = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Product").appendKeySegment(-10).build();

    final ODataRetrieveResponse<ODataEntity> entityRes = client.getRetrieveRequestFactory().
            getEntityRequest(uri).execute();
    final ODataEntity entity = entityRes.getBody();
    entity.getAssociationLinks().clear();
    entity.getNavigationLinks().clear();
    entity.getEditMediaLinks().clear();
    entity.getProperty("Description").setValue(
            client.getPrimitiveValueBuilder().setText("AsyncTest#updateEntity").build());

    final ODataEntityUpdateRequest updateReq =
            client.getCUDRequestFactory().getEntityUpdateRequest(uri, UpdateType.MERGE, entity);
    updateReq.setIfMatch(entityRes.getEtag());
    final Future<ODataEntityUpdateResponse> futureRes = updateReq.asyncExecute();

    while (!futureRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataEntityUpdateResponse res = futureRes.get();
    assertNotNull(res);
    assertEquals(204, res.getStatusCode());
  }

  /**
   * @see MediaEntityTest#createMediaEntity(com.msopentech.odatajclient.engine.format.ODataPubFormat)
   */
  @Test
  @Ignore
  public void createMediaEntity() throws Exception {
    CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Car");

    final String TO_BE_UPDATED = "async buffered stream sample";
    final InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

    final ODataMediaEntityCreateRequest createReq =
            client.getStreamedRequestFactory().getMediaEntityCreateRequest(builder.build(), input);

    final MediaEntityCreateStreamManager streamManager = createReq.execute();
    final Future<ODataMediaEntityCreateResponse> futureCreateRes = streamManager.getAsyncResponse();

    while (!futureCreateRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataMediaEntityCreateResponse createRes = futureCreateRes.get();

    assertEquals(201, createRes.getStatusCode());

    final ODataEntity created = createRes.getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperties().size());

    final int id = "VIN".equals(created.getProperties().get(0).getName())
            ? created.getProperties().get(0).getPrimitiveValue().toCastValue(Integer.class)
            : created.getProperties().get(1).getPrimitiveValue().toCastValue(Integer.class);

    builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(id).appendValueSegment();

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());
    assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
  }
}
