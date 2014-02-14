/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;

import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataMediaRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

    @Test
    public void retrieveEntitySet() throws InterruptedException, ExecutionException {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Product");
        final Future<ODataRetrieveResponse<ODataEntitySet>> futureRes =
                client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).asyncExecute();
        assertNotNull(futureRes);

        while (!futureRes.isDone()) {
        }

        final ODataRetrieveResponse<ODataEntitySet> res = futureRes.get();
        assertNotNull(res);
        assertEquals(200, res.getStatusCode());
        assertFalse(res.getBody().getEntities().isEmpty());
    }

    @Test
    public void updateEntity() throws InterruptedException, ExecutionException {
        final URI uri = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10).build();

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
        }

        final ODataEntityUpdateResponse res = futureRes.get();
        assertNotNull(res);
        assertEquals(204, res.getStatusCode());
    }

    /**
     * @see MediaEntityTest#createMediaEntity(com.msopentech.odatajclient.engine.format.ODataPubFormat)
     */
    @Test
    public void createMediaEntity() throws InterruptedException, ExecutionException, IOException {
        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).appendEntitySetSegment("Car");

        final String TO_BE_UPDATED = "async buffered stream sample";
        final InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

        final ODataMediaEntityCreateRequest createReq =
                client.getStreamedRequestFactory().getMediaEntityCreateRequest(builder.build(), input);

        final ODataMediaEntityCreateRequest.MediaEntityCreateStreamManager streamManager = createReq.execute();
        final Future<ODataMediaEntityCreateResponse> futureCreateRes = streamManager.getAsyncResponse();

        while (!futureCreateRes.isDone()) {
        }
        final ODataMediaEntityCreateResponse createRes = futureCreateRes.get();

        assertEquals(201, createRes.getStatusCode());

        final ODataEntity created = createRes.getBody();
        assertNotNull(created);
        assertEquals(2, created.getProperties().size());

        final int id = "VIN".equals(created.getProperties().get(0).getName())
                ? created.getProperties().get(0).getPrimitiveValue().<Integer>toCastValue()
                : created.getProperties().get(1).getPrimitiveValue().<Integer>toCastValue();

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());
        assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
    }
}
