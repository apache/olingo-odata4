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

import static com.msopentech.odatajclient.engine.it.AbstractTestITCase.testDefaultServiceRootURL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.client.http.HttpClientException;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataMediaRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityCreateRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityCreateRequest.MediaEntityCreateStreamManager;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityUpdateRequest.MediaEntityUpdateStreamManager;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataStreamUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataStreamUpdateRequest.StreamUpdateStreamManager;
import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityCreateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataStreamUpdateResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataMediaFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class MediaEntityTestITCase extends AbstractTestITCase {

    @Test
    public void read() throws Exception {
        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(12).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
        retrieveReq.setFormat(ODataMediaFormat.WILDCARD);

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        final byte[] actual = new byte[Integer.parseInt(retrieveRes.getHeader("Content-Length").iterator().next())];
        IOUtils.read(retrieveRes.getBody(), actual, 0, actual.length);
    }

    @Test(expected = ODataClientErrorException.class)
    public void readWithXmlError() throws Exception {
        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(12).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
        retrieveReq.setFormat(ODataMediaFormat.APPLICATION_XML);

        retrieveReq.execute();
    }

    @Test(expected = ODataClientErrorException.class)
    public void readWithJsonError() throws Exception {
        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(12).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
        retrieveReq.setFormat(ODataMediaFormat.APPLICATION_JSON);

        retrieveReq.execute();
    }

    @Test
    public void updateMediaEntityAsAtom() throws Exception {
        updateMediaEntity(ODataPubFormat.ATOM, 14);
    }

    @Test
    public void updateMediaEntityAsJson() throws Exception {
        updateMediaEntity(ODataPubFormat.JSON, 15);
    }

    @Test
    public void createMediaEntityAsAtom() throws Exception {
        createMediaEntity(ODataPubFormat.ATOM, IOUtils.toInputStream("buffered stream sample"));
    }

    @Test
    public void createMediaEntityAsJson() throws Exception {
        createMediaEntity(ODataPubFormat.JSON, IOUtils.toInputStream("buffered stream sample"));
    }

    @Test
    public void issue137() throws Exception {
        createMediaEntity(ODataPubFormat.JSON, this.getClass().getResourceAsStream("/sample.png"));
    }

    @Test
    public void issue136() throws Exception {
        byte[] input = new byte[65000];
        for (int i = 0; i < 65000; i++) {
            input[i] = (byte) i;
        }
        createMediaEntity(ODataPubFormat.ATOM, new ByteArrayInputStream(input));
    }

    @Test(expected = HttpClientException.class)
    public void issue136FailsWithException() throws Exception {
        byte[] input = new byte[68000];
        for (int i = 0; i < 68000; i++) {
            input[i] = (byte) i;
        }
        createMediaEntity(ODataPubFormat.ATOM, new ByteArrayInputStream(input));
    }

    @Test
    public void updateNamedStream() throws Exception {
        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(16).appendStructuralSegment("Photo");

        final String TO_BE_UPDATED = "buffered stream sample";
        final InputStream input = new ByteArrayInputStream(TO_BE_UPDATED.getBytes());

        final ODataStreamUpdateRequest updateReq =
                client.getStreamedRequestFactory().getStreamUpdateRequest(builder.build(), input);

        final StreamUpdateStreamManager streamManager = updateReq.execute();
        final ODataStreamUpdateResponse updateRes = streamManager.getResponse();
        updateRes.close();
        assertEquals(204, updateRes.getStatusCode());

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());
        assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
    }

    private void updateMediaEntity(final ODataPubFormat format, final int id) throws Exception {
        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).appendValueSegment();

        final String TO_BE_UPDATED = "new buffered stream sample";
        final InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

        final ODataMediaEntityUpdateRequest updateReq =
                client.getStreamedRequestFactory().getMediaEntityUpdateRequest(builder.build(), input);
        updateReq.setFormat(format);

        final MediaEntityUpdateStreamManager streamManager = updateReq.execute();
        final ODataMediaEntityUpdateResponse updateRes = streamManager.getResponse();
        assertEquals(204, updateRes.getStatusCode());

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());
        assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
    }

    private void createMediaEntity(final ODataPubFormat format, final InputStream input) throws Exception {
        final URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Car");

        final ODataMediaEntityCreateRequest createReq =
                client.getStreamedRequestFactory().getMediaEntityCreateRequest(builder.build(), input);
        createReq.setFormat(format);

        final MediaEntityCreateStreamManager streamManager = createReq.execute();
        final ODataMediaEntityCreateResponse createRes = streamManager.getResponse();
        assertEquals(201, createRes.getStatusCode());

        final ODataEntity created = createRes.getBody();
        assertNotNull(created);
        assertEquals(2, created.getProperties().size());

        Integer id = null;
        for (ODataProperty prop : created.getProperties()) {
            if ("VIN".equals(prop.getName())) {
                id = prop.getPrimitiveValue().<Integer>toCastValue();
            }
        }
        assertNotNull(id);

        builder.appendKeySegment(id).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());
        assertNotNull(retrieveRes.getBody());
    }
}
