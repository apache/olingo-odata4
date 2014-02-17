/**
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
package com.msopentech.odatajclient.engine.it;

import static org.junit.Assert.*;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataMediaRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.streamed.ODataMediaEntityUpdateRequest.MediaEntityUpdateStreamManager;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.response.ODataMediaEntityUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class MediaEntityUpdateTestITCase extends AbstractTestITCase {

    private void updateMediaEntity(
            final ODataPubFormat format,
            final String prefer,
            final String image,
            final int id) throws Exception {

        URIBuilder builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).appendValueSegment();

        // The sample service has an upload request size of 65k
        InputStream input = new BoundedInputStream(getClass().getResourceAsStream(image), 65000);
        final byte[] expected = new byte[65000];
        IOUtils.read(input, expected, 0, expected.length);
        IOUtils.closeQuietly(input);

        input = new BoundedInputStream(getClass().getResourceAsStream(image), 65000);

        final ODataMediaEntityUpdateRequest updateReq =
                client.getStreamedRequestFactory().getMediaEntityUpdateRequest(builder.build(), input);
        updateReq.setFormat(format);
        updateReq.setPrefer(prefer);
        final URI uri = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).build();
        final String etag = getETag(uri);
        if (StringUtils.isNotBlank(etag)) {
            updateReq.setIfMatch(etag);
        }
        final MediaEntityUpdateStreamManager streamManager = updateReq.execute();
        final ODataMediaEntityUpdateResponse updateRes = streamManager.getResponse();
        assertEquals(204, updateRes.getStatusCode());

        builder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(id).appendValueSegment();

        final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

        final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        final byte[] actual = new byte[65000];
        IOUtils.read(retrieveRes.getBody(), actual, 0, actual.length);
        assertTrue(new EqualsBuilder().append(expected, actual).isEquals());
    }
    // update media with JSON full metadata

    @Test
    public void updateMediaWithJSON() {
        ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        String prefer = "return-content";
        String media1 = "/images/big_buck_bunny.mp4";
        String media2 = "/images/Renault.jpg";
        String media3 = "/images/image1.png";
        String media4 = "/images/20051210-w50s.flv";
        int id = 11;
        try {
            updateMediaEntity(format, prefer, media1, id);
            updateMediaEntity(format, prefer, media2, id);
            updateMediaEntity(format, prefer, media3, id);
            updateMediaEntity(format, prefer, media4, id);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update media with ATOM

    @Test
    public void updateMediaWithATOM() {
        ODataPubFormat format = ODataPubFormat.ATOM;
        String prefer = "return-content";
        String media = "/images/big_buck_bunny.mp4";
        int id = 12;
        try {
            updateMediaEntity(format, prefer, media, id);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update media with JSON minimla metadata

    @Test
    public void updateMediaWithJSONMinimal() {
        ODataPubFormat format = ODataPubFormat.JSON;
        String prefer = "return-content";
        String media = "/images/big_buck_bunny.mp4";
        int id = 13;
        try {
            updateMediaEntity(format, prefer, media, id);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
    // update media with JSON no metadata

    @Test
    public void updateMediaWithJSONNoMetadata() {
        ODataPubFormat format = ODataPubFormat.JSON_NO_METADATA;
        String prefer = "return-content";
        String media = "/images/big_buck_bunny.mp4";
        int id = 14;
        try {
            updateMediaEntity(format, prefer, media, id);
        } catch (Exception e) {
            fail(e.getMessage());
        } catch (AssertionError e) {
            fail(e.getMessage());
        }
    }
}
