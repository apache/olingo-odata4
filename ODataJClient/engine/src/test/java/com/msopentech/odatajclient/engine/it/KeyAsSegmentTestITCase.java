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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyAsSegmentTestITCase extends AbstractTestITCase {

    @BeforeClass
    public static void enableKeyAsSegment() {
        client.getConfiguration().setKeyAsSegment(true);
    }

    private void read(final ODataPubFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(testKeyAsSegmentServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10);

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        final ODataEntity entity = res.getBody();
        assertNotNull(entity);

        assertFalse(entity.getEditLink().toASCIIString().contains("("));
        assertFalse(entity.getEditLink().toASCIIString().contains(")"));
    }

    @Test
    public void fromAtom() {
        read(ODataPubFormat.ATOM);
    }

    @Test
    public void fromJSON() {
        read(ODataPubFormat.JSON_FULL_METADATA);
    }

    @Test
    public void createODataEntityAsAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final int id = 1;
        final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

        createEntity(testKeyAsSegmentServiceRootURL, format, original, "Customer");
        final ODataEntity actual = compareEntities(testKeyAsSegmentServiceRootURL, format, original, id, null);

        cleanAfterCreate(format, actual, false, testKeyAsSegmentServiceRootURL);
    }

    @Test
    public void createODataEntityAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final int id = 2;
        final ODataEntity original = getSampleCustomerProfile(id, "Sample customer", false);

        createEntity(testKeyAsSegmentServiceRootURL, format, original, "Customer");
        final ODataEntity actual = compareEntities(testKeyAsSegmentServiceRootURL, format, original, id, null);

        cleanAfterCreate(format, actual, false, testKeyAsSegmentServiceRootURL);
    }

    @Test
    public void replaceODataEntityAsAtom() {
        final ODataPubFormat format = ODataPubFormat.ATOM;
        final ODataEntity changes = read(format, client.getURIBuilder(testKeyAsSegmentServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(14).build());
        updateEntityDescription(format, changes, UpdateType.REPLACE);
    }

    @Test
    public void replaceODataEntityAsJSON() {
        final ODataPubFormat format = ODataPubFormat.JSON_FULL_METADATA;
        final ODataEntity changes = read(format, client.getURIBuilder(testKeyAsSegmentServiceRootURL).
                appendEntityTypeSegment("Car").appendKeySegment(14).build());
        updateEntityDescription(format, changes, UpdateType.REPLACE);
    }

    @AfterClass
    public static void disableKeyAsSegment() {
        client.getConfiguration().setKeyAsSegment(false);
    }
}
