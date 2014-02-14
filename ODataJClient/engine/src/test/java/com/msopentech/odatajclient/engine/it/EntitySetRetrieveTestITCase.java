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

import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class EntitySetRetrieveTestITCase extends AbstractTestITCase {
    // retrieves an entity set

    private void retreiveEntityTest(ODataPubFormat reqFormat, String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product");
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntitySet entitySet = res.getBody();
            List<ODataEntity> entity = entitySet.getEntities();
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(i));
            }
            assertEquals(10, entity.size());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // get entity set with fullmetadata

    private void retreiveFullMetadataEntityTest(ODataPubFormat reqFormat, String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Product");
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            List<ODataEntity> entity = entitySet.getEntities();
            assertEquals(10, entity.size());
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(i));
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // get entity set with no metadata

    private void retreiveNoMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Product");
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);

        try {

            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            List<ODataEntity> entity = entitySet.getEntities();
            assertEquals(10, entity.size());
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(i));
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // get entity set with minimal metadata

    private void retreiveMinimalMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Product");
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertEquals(10, entity.size());
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(i));
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // retrieve entity set with expand

    private void retreiveEntityTestWithExpand(final ODataPubFormat reqFormat, final String acceptFormat,
            final String expandFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Customer").expand(expandFormat);
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertNotNull(entity);

        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // retrieve entity set with select

    private void retreiveEntityTestWithSelect(ODataPubFormat reqFormat, String acceptFormat, String selectFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Customer").select(selectFormat);
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertNotNull(entity);
            /* if(reqFormat.equals(ODataPubFormat.ATOM)){
             * AtomEntry atomEntry = Deserializer.toEntry(req.execute().getRawResponse(), AtomEntry.class);
             * String name = atomEntry.getSummary();
             * } */
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // retrieve entity set with expand and select

    private void retreiveEntityTestWithSelectAndExpand(ODataPubFormat reqFormat, String acceptFormat,
            String selectFormat, String expandFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntitySetSegment("Customer").select(selectFormat).expand(expandFormat);
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertNotNull(entity);
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //general query

    private void generalQuery(final ODataPubFormat format, final String acceptHeader, String query) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment(query);
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptHeader);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // inline count and filter test

    private void inlineCountTest(final ODataPubFormat format, final String acceptHeader, final String filterValue) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").inlineCount().filter(filterValue);
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptHeader);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            List<ODataEntity> entity = entitySet.getEntities();
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(i));
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //inlinecount and filter test

    @Test
    public void inlineAndFilterTest() {
        inlineCountTest(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId eq -10");
        inlineCountTest(ODataPubFormat.JSON, "application/json", "CustomerId eq -10");
        inlineCountTest(ODataPubFormat.JSON_FULL_METADATA, "application/json;odata=fullmetadata", "CustomerId eq -10");
        inlineCountTest(ODataPubFormat.JSON_NO_METADATA, "application/json;odata=nometadata", "CustomerId eq -10");

        //xml headers is not a supported media type 
        inlineCountTest(ODataPubFormat.ATOM, "application/xml", "CustomerId eq -10");

        //with different filters
        inlineCountTest(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId lt -10");
        inlineCountTest(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId gt -10");
    }
    // test with json

    @Test
    public void jsonHeader() {
        retreiveEntityTest(ODataPubFormat.JSON, "application/json");
        generalQuery(ODataPubFormat.JSON, "application/json", "Products");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, "application/json", "Orders");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "CustomerId,Name,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.JSON, "application/json", "CustomerId,Name", "Orders");
    }
    // test with json full metadata

    @Test
    public void jsonFullMetaDataHeader() {
        retreiveFullMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
        generalQuery(ODataPubFormat.JSON, "application/json;odata=fullmetadata", "Product");
    }
    //json with no metadata

    @Test
    public void jsonNoMetaDataHeader() {
        retreiveNoMetadataEntityTest(ODataPubFormat.JSON_NO_METADATA, "application/json;odata=nometadata");
    }
    //deserializing JSON error.

    @Test
    public void jsonMinimalMetadataDataHeader() {
        retreiveMinimalMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=minimal");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, "application/json;odata=minimal", "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, "application/json;odata=minimal", "Orders,Info");
    }
    // with atom header

    @Test
    public void atomHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "application/atom+xml");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/atom+xml", "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/atom+xml", "Orders,Info");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name", "Orders");
    }
    //with xml header

    @Test
    public void xmlHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "application/xml");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/xml", "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/xml", "Orders,Info");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId,Name,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, "application/xml", "CustomerId,Name", "Orders");
    }
    // with null format

    @Test
    public void nullFormat() {
        retreiveEntityTest(null, "application/xml");
        retreiveEntityTestWithExpand(null, "application/xml", "Orders");
        retreiveEntityTestWithExpand(null, "application/xml", "Orders,Info");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(null, "application/xml", "Name");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId,Name,,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(null, "application/xml", "CustomerId,Name", "Orders");
    }

    @Test
    public void nullHeaderWithJSONFormat() {
        retreiveEntityTest(ODataPubFormat.JSON, null);
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, null, "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, null, "Orders,Info");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId,Name,,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.JSON, null, "CustomerId,Name", "Orders");
    }
    //While deserializing atom entry an error is thrown

    @Test
    public void nullHeaderWithAtomFormat() {
        retreiveEntityTest(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null, "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null, "Orders,Info");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId,Name,,PrimaryContactInfo");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, null, "CustomerId,Name", "Orders");
    }
}
