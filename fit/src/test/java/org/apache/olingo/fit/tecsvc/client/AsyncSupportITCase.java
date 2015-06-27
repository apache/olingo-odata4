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
import java.util.concurrent.TimeUnit;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.apache.olingo.server.tecsvc.async.TechnicalAsyncService;
import org.junit.Test;

public final class AsyncSupportITCase extends AbstractBaseTestITCase {
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  @Test
  public void testSimple() throws Exception {
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
            .addCustomHeader("Prefer", "respond-async, " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> first = client.getAsyncRequestFactory().getAsyncRequestWrapper(re1).execute();

    assertTrue(first.isPreferenceApplied());

    // second async request
    ODataRequest re2 = getClient().getRetrieveRequestFactory()
            .getEntityRequest(uri)
            .addCustomHeader("Prefer", "respond-async, " + TechnicalAsyncService.TEC_ASYNC_SLEEP + "=1");
    AsyncResponseWrapper<ODataResponse> second = client.getAsyncRequestFactory().getAsyncRequestWrapper(re2).execute();
    assertTrue(second.isPreferenceApplied());

    // get result of first async request
    assertFalse(first.isDone());

    TimeUnit.SECONDS.sleep(2);
    assertTrue(first.isDone());

    assertNotNull(first.getODataResponse());
    ODataResponse firstResponse = first.getODataResponse();
    assertEquals(200, firstResponse.getStatusCode());
    ResWrap<Entity> entity = getClient().getDeserializer(ContentType.APPLICATION_JSON)
        .toEntity(firstResponse.getRawResponse());
    assertEquals(32767, entity.getPayload().getProperty("PropertyInt16").asPrimitive());
    assertEquals("First Resource - positive values", entity.getPayload().getProperty("PropertyString").asPrimitive());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    return odata;
  }
}
