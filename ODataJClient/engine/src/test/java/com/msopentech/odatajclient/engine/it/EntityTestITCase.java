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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataLinkCollectionRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.data.ODataLinkCollection;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import java.net.URI;

public class EntityTestITCase extends AbstractTestITCase {

    private void retreiveEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveFullMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveNoMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveMinimalMetadataEntityTest(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveEntityTestWithExpand(final ODataPubFormat reqFormat, final String acceptFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).expand("Orders");
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
            assertFalse(entity.getProperties().isEmpty());
        } catch (ODataClientErrorException e) {
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveEntityTestWithSelect(
            final ODataPubFormat reqFormat, final String acceptFormat, final String selectFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).select(selectFormat);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(reqFormat);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
        } catch (ODataClientErrorException e) {
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void retreiveEntityTestWithSelectAndExpand(final ODataPubFormat reqFormat, final String acceptFormat,
            final String selectFormat, String expandFormat) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void multiKeyTest(final ODataPubFormat format, final String acceptFormat) {
        final Map<String, Object> multiKey = new LinkedHashMap<String, Object>();
        multiKey.put("FromUsername", "1");
        multiKey.put("MessageId", -10);
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                appendEntityTypeSegment("Message").appendKeySegment(multiKey);
        final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setFormat(format);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataEntity> res = req.execute();
            final ODataEntity entity = res.getBody();
            assertNotNull(entity);
        } catch (ODataClientErrorException e) {
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void navigationLinks(final ODataFormat format, final String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10);
        ODataLinkCollectionRequest req =
                client.getRetrieveRequestFactory().getLinkCollectionRequest(uriBuilder.build(), "Orders");
        req.setFormat(format);
        req.setAccept(acceptFormat);
        try {
            final ODataRetrieveResponse<ODataLinkCollection> res = req.execute();
            assertEquals(200, res.getStatusCode());
            final ODataLinkCollection linkCollection = res.getBody();
            assertNotNull(linkCollection);
            final List<URI> links = linkCollection.getLinks();
            assertFalse(links.isEmpty());
            for (URI link : links) {
                assertNotNull(link);
            }
        } catch (ODataClientErrorException e) {
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    private void generalQuery(
            final ODataPubFormat format, String acceptFormat, final String navigationQuery, final String links) {
        final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
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
            assertEquals(415, e.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void jsonHeader() {
        retreiveEntityTest(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, "application/json", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(
                ODataPubFormat.JSON, "application/json", "CustomerId,Name,Orders", "Orders");
        multiKeyTest(ODataPubFormat.JSON, "application/json");
        navigationLinks(ODataFormat.JSON, "application/json");
        generalQuery(ODataPubFormat.JSON, "application/json", "Customer", "Orders");

        multiKeyTest(ODataPubFormat.JSON, "application/json");
        navigationLinks(ODataFormat.JSON, "application/json");
    }

    @Test
    public void jsonFullMetaDataHeader() {
        retreiveFullMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=fullmetadata");
    }

    @Test
    public void jsonNoMetaDataHeader() {
        retreiveNoMetadataEntityTest(ODataPubFormat.JSON_NO_METADATA, "application/json;odata=nometadata");
    }

    @Test
    public void jsonMinimalMetadataDataHeader() {
        retreiveMinimalMetadataEntityTest(ODataPubFormat.JSON, "application/json;odata=minimal");
    }

    @Test
    public void atomHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "application/atom+xml");
        generalQuery(ODataPubFormat.ATOM, "application/atom+xml", "Customer", "Orders");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/atom+xml");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(
                ODataPubFormat.ATOM, "application/atom+xml", "CustomerId,Name,Orders", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, "application/atom+xml");
    }

    @Test
    public void xmlHeader() {
        retreiveEntityTest(ODataPubFormat.ATOM, "applicationxml");
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, "application/xml");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, "application/xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(
                ODataPubFormat.ATOM, "application/xml", "CustomerId,Name,Orders", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, "application/atom+xml");
        navigationLinks(ODataFormat.XML, "application/xml");
    }

    @Test
    public void nullFormat() {
        retreiveEntityTest(null, "application/xml");
        retreiveEntityTestWithExpand(null, "application/xml");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId");
        retreiveEntityTestWithSelect(null, "application/xml", "Name");
        retreiveEntityTestWithSelect(null, "application/xml", "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(null, "application/xml", "CustomerId,Name,Orders", "Orders");
        multiKeyTest(null, "application/xml");
        navigationLinks(null, "application/atom+xml");
    }

    @Test
    public void nullHeaderWithJSONFormat() {
        retreiveEntityTest(ODataPubFormat.JSON, null);
        retreiveEntityTestWithExpand(ODataPubFormat.JSON, null);
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.JSON, null, "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.JSON, null, "CustomerId,Name,Orders", "Orders");
        multiKeyTest(ODataPubFormat.JSON, null);
        navigationLinks(ODataFormat.JSON, null);
    }

    @Test
    public void nullHeaderWithAtomFormat() {
        retreiveEntityTest(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithExpand(ODataPubFormat.ATOM, null);
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "Name");
        retreiveEntityTestWithSelect(ODataPubFormat.ATOM, null, "CustomerId,Name");
        retreiveEntityTestWithSelectAndExpand(ODataPubFormat.ATOM, null, "CustomerId,Name,Orders", "Orders");
        multiKeyTest(ODataPubFormat.ATOM, null);
        navigationLinks(ODataFormat.XML, null);
    }
}
