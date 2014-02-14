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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;

public class EntityTestITCase extends AbstractTestITCase {
    //retrieve entity

    private void retreiveEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-9);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            assertNotNull(res.getEtag());
            final ODataEntity entity = res.getBody();
            assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.DiscontinuedProduct", entity.getName());
            final List<ODataProperty> properties = entity.getProperties();
            assertEquals(10, properties.size());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with full metadata

    private void retreiveFullMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-9);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            assertNotNull(res.getEtag());
            final ODataEntity entity = res.getBody();
            assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.DiscontinuedProduct", entity.getName());
            assertNotNull(entity.getEditLink().toASCIIString());
            assertTrue(entity.getAssociationLinks().isEmpty());
            final List<ODataProperty> properties = entity.getProperties();
            assertEquals(10, properties.size());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with NO metadata

    private void retreiveNoMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-9);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNull(entity.getName());
            final List<ODataProperty> properties = entity.getProperties();
            assertEquals(10, properties.size());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with minimal metadata

    private void retreiveMinimalMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-9);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNull(entity.getName());
            final List<ODataProperty> properties = entity.getProperties();
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with expand query

    private void retreiveEntityTestWithExpand(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).expand("Orders");
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
            final List<ODataProperty> properties = entity.getProperties();
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with select query	

    private void retreiveEntityTestWithSelect(final ODataPubFormat reqFormat, final String acceptFormat,
            final String selectFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).select(selectFormat);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
            final List<ODataProperty> properties = entity.getProperties();
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with expand and select query

    private void retreiveEntityTestWithSelectAndExpand(final ODataPubFormat reqFormat, final String acceptFormat,
            final String selectFormat, String expandFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).select(selectFormat).expand(expandFormat);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
            final List<ODataProperty> properties = entity.getProperties();
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve entity with multikey

    private void multiKeyTest(final ODataPubFormat format, final String acceptFormat) {
        final Map<String, Object> multiKey = new LinkedHashMap<String, Object>();
        multiKey.put("FromUsername", "1");
        multiKey.put("MessageId", -10);
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Message").appendKeySegment(multiKey);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    //retrieve navigation link

    private void navigationLinks(final ODataPubFormat format, final String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Orders");
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertNotNull(entity);
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(0).getProperties().get(0).getValue());
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // genral query test

    private void generalQuery(final ODataPubFormat format, String acceptFormat, final String navigationQuery,
            String links) {
        final URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment(navigationQuery).appendKeySegment(-10).appendLinksSegment(links);
        final ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntitySet entitySet = res.getBody();
            assertNotNull(entitySet);
            final List<ODataEntity> entity = entitySet.getEntities();
            assertNotNull(entity);
            for (int i = 0; i < entity.size(); i++) {
                assertNotNull(entity.get(0).getProperties().get(0).getValue());
            }
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            } else if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // test with json header

    @Test
    public void jsonHeader() {
        retreiveEntityTest(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.JSON, "application/json", "CustomerId,Name", "Orders");
        multiKeyTest(ODataPubFormat.JSON, "application/json");
        navigationLinks(ODataPubFormat.JSON, "application/json");
        generalQuery(ODataPubFormat.JSON, "application/json", "Customer", "Orders");

        multiKeyTest(ODataPubFormat.JSON, "application/json");
        navigationLinks(ODataPubFormat.JSON, "application/json");
    }
    // test with json full metadata

    @Test
    public void jsonFullMetaDataHeader() {
        retreiveFullMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
    }
    // test with json header no metadata

    @Test
    public void jsonNoMetaDataHeader() {
        retreiveNoMetadataEntityTest(ODataPubFormat.JSON_NO_METADATA, "application/json;odata=nometadata");
    }
    //deserializing JSON error.

    @Test
    public void jsonMinimalMetadataDataHeader() {
        retreiveMinimalMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=minimal");
    }
    // with atom header

    @Test
    public void atomHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "application/atom+xml");
        generalQuery(ODataPubFormat.ATOM, "application/atom+xml", "Customers", "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/atom+xml");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, "application/atom+xml");
    }
    // test with xml header

    @Test
    public void xmlHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "applicationxml");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/xml");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, "application/xml", "CustomerId,Name", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, "application/atom+xml");
        navigationLinks(ODataPubFormat.ATOM, "application/atom+xml");
    }

    @Test
    public void nullFormat() {
        retreiveEntityTest(null, "application/xml");
        retreiveEntityTestWithExpand(null, "application/xml");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(null, "application/xml", "Name");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(null, "application/xml", "CustomerId,Name", "Orders");
        multiKeyTest(null, "application/xml");
        try {
            navigationLinks(null, "application/xml");
            fail();
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        }
    }
    // with null accept header

    @Test
    public void nullHeaderWithJSONFormat() {
        retreiveEntityTest(ODataPubFormat.JSON, null);
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, null);
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.JSON, null, "CustomerId,Name", "Orders");
        multiKeyTest(ODataPubFormat.JSON, null);
        navigationLinks(ODataPubFormat.JSON, null);

    }
    //While deserializing atom entry this will return error

    @Test
    public void nullHeaderWithAtomFormat() {
        retreiveEntityTest(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, null, "CustomerId,Name", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, null);
        navigationLinks(ODataPubFormat.ATOM, null);
    }
}
