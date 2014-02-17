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

import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.uri.filter.ODataFilter;
import com.msopentech.odatajclient.engine.uri.filter.ODataFilterArgFactory;
import org.junit.Test;

public class FilterFactoryTestITCase extends AbstractTestITCase {

    private void match(final String entitySet, final ODataFilter filter, final int expected) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment(entitySet).filter(filter);

        final ODataEntitySet feed = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).
                execute().getBody();
        assertNotNull(feed);
        assertEquals(expected, feed.getEntities().size());
    }

    @Test
    public void simple() {
        match("Car", client.getFilterFactory().lt("VIN", 16), 5);
    }

    @Test
    public void and() {
        final ODataFilter filter =
                client.getFilterFactory().and(
                        client.getFilterFactory().lt("VIN", 16),
                        client.getFilterFactory().gt("VIN", 12));

        match("Car", filter, 3);
    }

    @Test
    public void not() {
        final ODataFilter filter =
                client.getFilterFactory().not(
                        client.getFilterFactory().or(
                                client.getFilterFactory().ge("VIN", 16),
                                client.getFilterFactory().le("VIN", 12)));

        match("Car", filter, 3);
    }

    @Test
    public void operator() {
        ODataFilter filter =
                client.getFilterFactory().eq(
                        ODataFilterArgFactory.add(ODataFilterArgFactory.property("VIN"), ODataFilterArgFactory.
                                literal(1)),
                        ODataFilterArgFactory.literal(16));

        match("Car", filter, 1);

        filter =
                client.getFilterFactory().eq(
                        ODataFilterArgFactory.add(ODataFilterArgFactory.literal(1), ODataFilterArgFactory.
                                property("VIN")),
                        ODataFilterArgFactory.literal(16));

        match("Car", filter, 1);

        filter =
                client.getFilterFactory().eq(
                        ODataFilterArgFactory.literal(16),
                        ODataFilterArgFactory.add(ODataFilterArgFactory.literal(1), ODataFilterArgFactory.
                                property("VIN")));

        match("Car", filter, 1);
    }

    @Test
    public void function() {
        final ODataFilter filter =
                client.getFilterFactory().match(
                        ODataFilterArgFactory.startswith(
                                ODataFilterArgFactory.property("Description"), ODataFilterArgFactory.literal("cen")));

        match("Car", filter, 1);
    }

    @Test
    public void composed() {
        final ODataFilter filter =
                client.getFilterFactory().gt(
                        ODataFilterArgFactory.length(ODataFilterArgFactory.property("Description")),
                        ODataFilterArgFactory.add(ODataFilterArgFactory.property("VIN"), ODataFilterArgFactory.literal(
                                        10)));

        match("Car", filter, 5);
    }

    @Test
    public void propertyPath() {
        ODataFilter filter =
                client.getFilterFactory().eq(
                        ODataFilterArgFactory.indexof(
                                ODataFilterArgFactory.property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                                ODataFilterArgFactory.literal("ODataJClient")),
                        ODataFilterArgFactory.literal(1));

        match("Customer", filter, 0);

        filter =
                client.getFilterFactory().ne(
                        ODataFilterArgFactory.indexof(
                                ODataFilterArgFactory.property("PrimaryContactInfo/HomePhone/PhoneNumber"),
                                ODataFilterArgFactory.literal("lccvussrv")),
                        ODataFilterArgFactory.literal(-1));

        match("Customer", filter, 2);
    }

    @Test
    public void datetime() {
        final ODataFilter filter =
                client.getFilterFactory().eq(
                        ODataFilterArgFactory.month(
                                ODataFilterArgFactory.property("PurchaseDate")),
                        ODataFilterArgFactory.literal(12));

        match("ComputerDetail", filter, 1);
    }

    @Test
    public void isof() {
        final ODataFilter filter =
                client.getFilterFactory().match(
                        ODataFilterArgFactory.isof(
                                ODataFilterArgFactory.literal(
                                        "Microsoft.Test.OData.Services.AstoriaDefaultService.SpecialEmployee")));

        match("Person", filter, 4);
    }
}
