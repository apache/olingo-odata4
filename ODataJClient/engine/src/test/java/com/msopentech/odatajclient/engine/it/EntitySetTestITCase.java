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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetIteratorRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataGenericRetrieveRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataEntitySetIterator;
import com.msopentech.odatajclient.engine.data.ODataObjectWrapper;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.ResourceFactory;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.io.IOException;
import java.net.URI;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class EntitySetTestITCase extends AbstractTestITCase {

    protected String getServiceRoot() {
        return testStaticServiceRootURL;
    }

    @Test
    public void genericRequestAsAtom() throws IOException {
        genericRequest(ODataPubFormat.ATOM);
    }

    @Test
    public void genericRequestAsJSON() throws IOException {
        genericRequest(ODataPubFormat.JSON);
    }

    @Test
    public void readODataEntitySetIteratorFromAtom() {
        readODataEntitySetIterator(ODataPubFormat.ATOM);
    }

    @Test
    public void readODataEntitySetIteratorFromJSON() {
        readODataEntitySetIterator(ODataPubFormat.JSON);
    }

    @Test
    public void readODataEntitySetIteratorFromJSONFullMeta() {
        readODataEntitySetIterator(ODataPubFormat.JSON_FULL_METADATA);
    }

    @Test
    public void readODataEntitySetIteratorFromJSONNoMeta() {
        readODataEntitySetIterator(ODataPubFormat.JSON_NO_METADATA);
    }

    @Test
    public void readODataEntitySetWithNextFromAtom() {
        readEntitySetWithNextLink(ODataPubFormat.ATOM);
    }

    @Test
    public void readODataEntitySetWithNextFromJSON() {
        readEntitySetWithNextLink(ODataPubFormat.JSON_FULL_METADATA);
    }

    private void readEntitySetWithNextLink(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntitySetSegment("Customer");

        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
        final ODataEntitySet feed = res.getBody();

        assertNotNull(feed);

        debugFeed(client.getBinder().getFeed(feed, ResourceFactory.feedClassForFormat(format)), "Just retrieved feed");

        assertEquals(2, feed.getEntities().size());
        assertNotNull(feed.getNext());

        final URI expected = URI.create(getServiceRoot() + "/Customer?$skiptoken=-9");
        final URI found = URIUtils.getURI(getServiceRoot(), feed.getNext().toASCIIString());

        assertEquals(expected, found);
    }

    private void readODataEntitySetIterator(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntitySetSegment("Customer");

        final ODataEntitySetIteratorRequest req =
                client.getRetrieveRequestFactory().getEntitySetIteratorRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntitySetIterator> res = req.execute();
        final ODataEntitySetIterator feedIterator = res.getBody();

        assertNotNull(feedIterator);

        int count = 0;

        while (feedIterator.hasNext()) {
            assertNotNull(feedIterator.next());
            count++;
        }
        assertEquals(2, count);
        assertTrue(feedIterator.getNext().toASCIIString().endsWith("Customer?$skiptoken=-9"));
    }

    private void genericRequest(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntitySetSegment("Car");

        final ODataGenericRetrieveRequest req =
                client.getRetrieveRequestFactory().getGenericRetrieveRequest(uriBuilder.build());
        req.setFormat(format.toString());

        final ODataRetrieveResponse<ODataObjectWrapper> res = req.execute();

        ODataObjectWrapper wrapper = res.getBody();

        final ODataEntitySet entitySet = wrapper.getODataEntitySet();
        assertNotNull(entitySet);
    }
}
