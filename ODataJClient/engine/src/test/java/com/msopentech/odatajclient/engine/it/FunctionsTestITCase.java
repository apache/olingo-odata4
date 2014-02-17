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

import java.util.List;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataPropertyRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.util.Iterator;

public class FunctionsTestITCase extends AbstractTestITCase {
    //function returns a reference 

    private void refReturnFunction(final ODataPubFormat format, final String accept) {
        // GetSpecificCustomer takes 'Name' parameter, but seems to be buggy in the sample services
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("GetSpecificCustomer?CustomerId=-8");
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(accept);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(res.getBody());
            final List<ODataEntity> entity = entitySet.getEntities();
            // other tests can change the number of customers with no name
            assertFalse(entity.isEmpty());
            if (accept.equals("application/json;odata-fullmetadata")
                    || accept.equals("application/json;odata-minimalmetadata")) {
                assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer",
                        entity.get(0).getName());
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            }
            if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
            if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(400, e.getStatusLine().getStatusCode());
            }
        }
    }
    //CustomerIds is an invalid query ID. Returns ODataClientErrorException

    private void withInvalidQuery(final ODataPubFormat format, final String accept) {
        // GetSpecificCustomer takes 'Name' parameter, but seems to be buggy in the sample services
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("GetSpecificCustomer?CustomerIds=-10");
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(accept);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(res.getBody());
            final List<ODataEntity> entity = entitySet.getEntities();
            // other tests can change the number of customers with no name
            assertFalse(entity.isEmpty());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            }
            if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
            if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(400, e.getStatusLine().getStatusCode());
            }
        }
    }
    //function returning collection of complex types

    private void collectionFunction(final ODataFormat format, final String accept) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("EntityProjectionReturnsCollectionOfComplexTypes");
        final ODataPropertyRequest req = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(accept);
        try {
            final ODataRetrieveResponse<ODataProperty> res = req.execute();
            final ODataProperty property = res.getBody();
            assertTrue(property.hasCollectionValue());
            final ODataCollectionValue value = property.getCollectionValue();
            assertTrue(9 <= value.size());
            for (Iterator<ODataValue> itor = value.iterator(); itor.hasNext();) {
                ODataValue itemValue = itor.next();
                assertTrue(itemValue.isComplex());
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 400) {
                assertEquals(400, e.getStatusLine().getStatusCode());
            }
        }
    }
    //with atom header

    @Test
    public void atomTest() {
        refReturnFunction(ODataPubFormat.ATOM, "application/atom+xml");
        withInvalidQuery(ODataPubFormat.ATOM, "application/atom+xml");
        collectionFunction(ODataFormat.XML, "application/atom+xml");
    }
    //with json header

    @Test
    public void jsonTest() {
        refReturnFunction(ODataPubFormat.JSON, "application/json");
        withInvalidQuery(ODataPubFormat.JSON, "application/json");
        collectionFunction(ODataFormat.JSON, "application/json");
    }
    //with json header full metadata

    @Test
    public void jsonFullMetadataTest() {
        refReturnFunction(ODataPubFormat.JSON_FULL_METADATA, "application/json;odata=fullmetadata");
        collectionFunction(ODataFormat.JSON_FULL_METADATA, "application/json;odata=fullmetadata");
    }
    //with json header no metadata

    @Test
    public void jsonNoMetadataTest() {
        refReturnFunction(ODataPubFormat.JSON_NO_METADATA, "application/json;odata=nometadata");
        collectionFunction(ODataFormat.JSON_NO_METADATA, "application/json;odata=nometadata");
    }
    //with json header minimal metadata

    @Test
    public void jsonMinimalMetadataTest() {
        refReturnFunction(ODataPubFormat.JSON, "application/json;odata=minimal");
        collectionFunction(ODataFormat.JSON, "application/json;odata=minimal");
        withInvalidQuery(ODataPubFormat.JSON, "application/json;odata=minimal");
    }
    //with accept type as xml

    @Test
    public void xmlTest() {
        collectionFunction(ODataFormat.XML, "application/xml");
    }
    //with null accept header and atom format

    @Test
    public void nullAcceptTest() {
        withInvalidQuery(ODataPubFormat.ATOM, null);
        collectionFunction(ODataFormat.XML, null);
        withInvalidQuery(ODataPubFormat.ATOM, null);
    }
    //with null accept header and json format

    @Test
    public void nullAcceptWithJSONTest() {
        withInvalidQuery(ODataPubFormat.JSON, null);
        collectionFunction(ODataFormat.JSON, null);
        withInvalidQuery(ODataPubFormat.JSON, null);
    }
    //null pub format

    @Test
    public void nullFormatTest() {
        withInvalidQuery(null, "application/json");
        collectionFunction(null, "application/json");
        withInvalidQuery(null, "application/json");
    }
}
