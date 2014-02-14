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
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.impl.AbstractODataDeserializer;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataInlineEntitySet;
import com.msopentech.odatajclient.engine.data.impl.v3.AtomEntry;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

/**
 * This is the unit test class to check for query options.
 */
public class QueryOptionsTestITCase extends AbstractTestITCase {

    /**
     * Test <tt>$expand</tt>.
     *
     * @see EntityRetrieveTest#readODataEntityWithInline(com.msopentech.odatajclient.engine.types.ODataPubFormat)
     */
    public void expand() {
        // empty
    }

    /**
     * Test <tt>$filter</tt> and <tt>orderby</tt>.
     *
     * @see FilterFactoryTest for more tests.
     */
    @Test
    public void filterOrderby() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Car").filter("VIN lt 16");

        // 1. check that filtered entity set looks as expected
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        ODataEntitySet feed = req.execute().getBody();
        assertNotNull(feed);
        assertEquals(5, feed.getEntities().size());

        // 2. extract VIN values - sorted ASC by default
        final List<Integer> vinsASC = new ArrayList<Integer>(5);
        for (ODataEntity entity : feed.getEntities()) {
            final Integer vin = entity.getProperty("VIN").getPrimitiveValue().<Integer>toCastValue();
            assertTrue(vin < 16);
            vinsASC.add(vin);
        }

        // 3. add orderby clause to filter above
        req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.orderBy("VIN desc").build());
        feed = req.execute().getBody();
        assertNotNull(feed);
        assertEquals(5, feed.getEntities().size());

        // 4. extract again VIN value - now they were required to be sorted DESC
        final List<Integer> vinsDESC = new ArrayList<Integer>(5);
        for (ODataEntity entity : feed.getEntities()) {
            vinsDESC.add(entity.getProperty("VIN").getPrimitiveValue().<Integer>toCastValue());
        }

        // 5. reverse vinsASC and expect to be equal to vinsDESC
        Collections.reverse(vinsASC);
        assertEquals(vinsASC, vinsDESC);
    }

    /**
     * Test <tt>$format</tt>.
     */
    @Test
    public void format() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).format("json");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(ODataPubFormat.ATOM);

        final ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertNotNull(res);
        assertTrue(res.getContentType().startsWith(ODataPubFormat.JSON.toString()));
    }

    /**
     * Test <tt>$skip</tt>.
     *
     * @see FeedTest#readFeedWithNextLink(com.msopentech.odatajclient.engine.types.ODataPubFormat)
     */
    public void skip() {
        // empty
    }

    /**
     * Test <tt>$top</tt>.
     *
     * @see FeedTest#readFeed(com.msopentech.odatajclient.engine.types.ODataPubFormat)
     */
    public void top() {
        // empty
    }

    /**
     * Test <tt>$skiptoken</tt>.
     */
    @Test
    public void skiptoken() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
        uriBuilder.appendEntityTypeSegment("Customer").skipToken("-10");

        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        final ODataEntitySet feed = req.execute().getBody();
        assertNotNull(feed);
        assertEquals(2, feed.getEntities().size());

        for (ODataEntity entity : feed.getEntities()) {
            assertTrue(entity.getProperty("CustomerId").getPrimitiveValue().<Integer>toCastValue() > -10);
        }
    }

    /**
     * Test <tt>$inlinecount</tt>.
     */
    @Test
    public void inlinecount() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL);
        uriBuilder.appendEntityTypeSegment("Car").inlineCount();

        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(ODataPubFormat.ATOM);
        final ODataEntitySet feed = req.execute().getBody();
        assertNotNull(feed);
        assertEquals(feed.getEntities().size(), feed.getCount());
    }

    /**
     * Test <tt>$select</tt>.
     */
    @Test
    public void select() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).select("CustomerId,Orders").expand("Orders");

        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        final ODataEntity customer = req.execute().getBody();
        assertEquals(1, customer.getProperties().size());
        assertEquals(1, customer.getNavigationLinks().size());
        assertTrue((customer.getNavigationLinks().get(0) instanceof ODataInlineEntitySet));
    }

    @Test
    public void issue131() {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-7).select("Name");

        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(ODataPubFormat.ATOM);

        final ODataEntity customer = req.execute().getBody();
        assertEquals(0, customer.getProperties().size());

        req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(ODataPubFormat.ATOM);

        final AtomEntry atomEntry = client.getDeserializer().toEntry(req.execute().getRawResponse(), AtomEntry.class);
        assertEquals("remotingdestructorprinterswitcheschannelssatellitelanguageresolve",
                atomEntry.getSummary());
    }
}
