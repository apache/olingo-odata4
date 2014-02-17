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
import java.io.IOException;
import org.junit.Test;

import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.RetrieveRequestFactory;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataValueRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;

public class PropertyValueTestITCase extends AbstractTestITCase {
    //retrieves Edm.Int32

    @Test
    public void retrieveIntPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10).appendStructuralSegment("ProductId").
                appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        assertNotNull(value);
        assertEquals(-10, Integer.parseInt(value.toString()));
    }
    //retrieves boolean

    @Test
    public void retrieveBooleanPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10).appendStructuralSegment("ProductId").
                appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        assertNotNull(value);
        assertEquals(-10, Integer.parseInt(value.toString()));
    }
    //retrieves Edm.String 

    @Test
    public void retrieveStringPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-6).appendStructuralSegment("Description").
                appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        assertNotNull(value);
        assertEquals("expdybhclurfobuyvzmhkgrnrajhamqmkhqpmiypittnp", value.toString());
    }

    // date from a complex structure. 
    @Test
    public void retrieveDatePropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-7).appendStructuralSegment(
                "NestedComplexConcurrency/ModifiedDate").appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        assertNotNull(value);
        assertEquals("7866-11-16T22:25:52.747755+01:00", value.toString());
    }
    //decimal type test

    @Test
    public void retrieveDecimalPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-6).appendStructuralSegment("Dimensions/Height").
                appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        assertNotNull(value);
        assertEquals("-79228162514264337593543950335", value.toString());
    }
    //binary test with json format 

    @Test
    public void retrieveBinaryPropertyValueTest() throws IOException {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendStructuralSegment("Photo");
        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setAccept("application/json");
        ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());
        ODataEntity entitySet = res.getBody();
        assertNotNull(entitySet);
        assertEquals(
                "fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJVIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=",
                entitySet.getProperties().get(0).getValue().toString());
    }
    //binary test with atom format.

    @Test(expected = ODataClientErrorException.class)
    public void retrieveBinaryPropertyValueTestWithAtom() throws IOException {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendStructuralSegment("Photo");
        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setAccept("application/atom+xml");
        ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());
        ODataEntity entitySet = res.getBody();
        assertNotNull(entitySet);
        assertEquals(
                "fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJVIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=",
                entitySet.getProperties().get(0).getValue().toString());
    }
    // binary data with xml. Unable to deserialize JSON

    @Test(expected = IllegalArgumentException.class)
    public void retrieveBinaryPropertyValueTestWithXML() throws IOException {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendNavigationLinkSegment("ProductPhoto(PhotoId=-3,ProductId=-3)").appendStructuralSegment("Photo");
        ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
        req.setAccept("application/xml");
        ODataRetrieveResponse<ODataEntity> res = req.execute();
        assertEquals(200, res.getStatusCode());
        ODataEntity entitySet = res.getBody();
        assertNotNull(entitySet);
        assertEquals(
                "fi653p3+MklA/LdoBlhWgnMTUUEo8tEgtbMXnF0a3CUNL9BZxXpSRiD9ebTnmNR0zWPjJVIDx4tdmCnq55XrJh+RW9aI/b34wAogK3kcORw=",
                entitySet.getProperties().get(0).getValue().toString());
    }
    //collection of primitives

    @Test
    public void retrieveCollectionPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-7).appendStructuralSegment(
                "ComplexConcurrency/QueriedDateTime").appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);
        final ODataValue value = req.execute().getBody();
        if (value.isPrimitive()) {
            assertNotNull(value);
            assertEquals("2013-09-18T00:44:43.6196168", value.toString());
        }
    }
    // No resource found error. Token is not available under ComplexConcurrency

    @Test
    public void retrieveNullPropertyValueTest() {
        URIBuilder uriBuilder = client.getURIBuilder(testDefaultServiceRootURL).
                appendEntityTypeSegment("Product").appendKeySegment(-10).appendStructuralSegment(
                "ComplexConcurrency/Token").appendValueSegment();
        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        try {
            final ODataValue value = req.execute().getBody();
        } catch (ODataClientErrorException e) {
            assertEquals(404, e.getStatusLine().getStatusCode());
        }
    }
}
