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
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntitySetRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataEntitySet;
import com.msopentech.odatajclient.engine.uri.URIBuilder;

public class NavigationLinkTestITCase extends AbstractTestITCase {
    //collection of navigation links

    private void getListNavigationLinks(String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Orders");
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setAccept(acceptFormat);
        ODataRetrieveResponse<ODataEntitySet> res = req.execute();
        assertEquals(200, res.getStatusCode());
        ODataEntitySet entitySet = res.getBody();
        assertNotNull(entitySet);
        List<ODataEntity> entity = entitySet.getEntities();
        assertNotNull(entity);
        for (int i = 0; i < entity.size(); i++) {
            assertNotNull(entity.get(i).getProperties().get(0).getValue());
        }
    }
    //invalid query

    private void getListNavigationLinksInvalidSegment(String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Address");
        ODataEntitySetRequest req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
        req.setAccept(acceptFormat);
        ODataRetrieveResponse<ODataEntitySet> res = req.execute();
        assertEquals(415, res.getStatusCode());
    }
    //Reference navigation link

    private void getSingleNavigationLinks(String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Orders").
                appendKeySegment(-10);
        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setAccept(acceptFormat);
        ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());
        ODataEntity entity = res.getBody();
        assertNotNull(entity.getProperties().get(0).getValue());
    }
    // reference navigation link

    private void getReferenceNavigationLinks(String acceptFormat) {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("Customer").appendKeySegment(-10).appendLinksSegment("Logins").
                appendKeySegment(-1);
        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setAccept(acceptFormat);
        try {
            ODataRetrieveResponse<ODataEntity> res = req.execute();
            assertEquals(200, res.getStatusCode());
            ODataEntity entity = res.getBody();
            assertNotNull(entity);
            assertNotNull(entity.getProperties().get(0).getValue());
        } catch (ODataClientErrorException e) {
            if (e.getStatusLine().getStatusCode() == 415) {
                assertEquals(415, e.getStatusLine().getStatusCode());
            }
            if (e.getStatusLine().getStatusCode() == 404) {
                assertEquals(404, e.getStatusLine().getStatusCode());
            }
        }
    }
    // json header test

    @Test
    public void withJSON() {
        getListNavigationLinks("application/json");
        getSingleNavigationLinks("application/json");
    }
    // atom header test

    @Test(expected = ODataClientErrorException.class)
    public void withATOM() {
        getListNavigationLinks("application/atom+xml");
        getSingleNavigationLinks("application/atom+xml");
        getReferenceNavigationLinks("application/atom+xml");
    }
    // unable to deserialize. xml is an invalid accept header for collection reference link

    @Test(expected = IllegalArgumentException.class)
    public void withXML() {
        getListNavigationLinks("application/xml");
        getSingleNavigationLinks("application/xml");
        getReferenceNavigationLinks("application/xml");
    }
    //with no accept header test

    @Test
    public void withNull() {
        getListNavigationLinks(null);
        getSingleNavigationLinks(null);
        getReferenceNavigationLinks(null);
    }
    // with an invalid navigation segment which does not exists. 404 error is returned

    @Test(expected = ODataClientErrorException.class)
    public void withInvalidSegment() {
        getListNavigationLinksInvalidSegment("application/json");
    }
}
