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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.msopentech.odatajclient.engine.client.http.HttpMethod;
import com.msopentech.odatajclient.engine.communication.ODataClientErrorException;
import com.msopentech.odatajclient.engine.communication.request.UpdateType;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataPropertyUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.cud.ODataValueUpdateRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataGenericRetrieveRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataPropertyRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataValueRequest;
import com.msopentech.odatajclient.engine.communication.response.ODataDeleteResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataPropertyUpdateResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataRetrieveResponse;
import com.msopentech.odatajclient.engine.communication.response.ODataValueUpdateResponse;
import com.msopentech.odatajclient.engine.data.ODataCollectionValue;
import com.msopentech.odatajclient.engine.data.ODataObjectWrapper;
import com.msopentech.odatajclient.engine.data.ODataPrimitiveValue;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.URIBuilder;
import com.msopentech.odatajclient.engine.data.ODataValue;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import java.io.IOException;
import org.junit.Test;

/**
 * This is the unit test class to check basic entity operations.
 */
public class PropertyTestITCase extends AbstractTestITCase {

    protected String getServiceRoot() {
        return testDefaultServiceRootURL;
    }

    @Test
    public void replacePropertyValue() throws IOException {
        updatePropertyValue(ODataValueFormat.TEXT, UpdateType.REPLACE);
    }

    @Test
    public void replacePrimitivePropertyAsXML() throws IOException {
        updatePrimitiveProperty(ODataFormat.XML);
    }

    @Test
    public void replacePrimitivePropertyAsJSON() throws IOException {
        updatePrimitiveProperty(ODataFormat.JSON_FULL_METADATA);
    }

    @Test
    public void replaceCollectionPropertyAsXML() throws IOException {
        updateCollectionProperty(ODataFormat.XML);
    }

    @Test
    public void replaceCollectionPropertyAsJSON() throws IOException {
        updateCollectionProperty(ODataFormat.JSON_FULL_METADATA);
    }

    @Test
    public void replaceComplexPropertyAsXML() throws IOException {
        updateComplexProperty(ODataFormat.XML, UpdateType.REPLACE);
    }

    @Test
    public void replaceComplexPropertyAsJSON() throws IOException {
        updateComplexProperty(ODataFormat.JSON_FULL_METADATA, UpdateType.REPLACE);
    }

    @Test
    public void patchComplexPropertyAsXML() throws IOException {
        updateComplexProperty(ODataFormat.XML, UpdateType.PATCH);
    }

    @Test
    public void patchComplexPropertyAsJSON() throws IOException {
        updateComplexProperty(ODataFormat.JSON_FULL_METADATA, UpdateType.PATCH);
    }

    @Test
    public void mergeComplexPropertyAsXML() throws IOException {
        updateComplexProperty(ODataFormat.XML, UpdateType.MERGE);
    }

    @Test
    public void mergeComplexPropertyAsJSON() throws IOException {
        updateComplexProperty(ODataFormat.JSON_FULL_METADATA, UpdateType.MERGE);
    }

    @Test
    public void genericRequestAsXML() throws IOException {
        genericRequest(ODataFormat.XML);
    }

    @Test
    public void genericRequestAsJSON() throws IOException {
        genericRequest(ODataFormat.JSON);
    }

    @Test
    public void readCountValue() throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntityTypeSegment("Customer").appendCountSegment();

        final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        req.setFormat(ODataValueFormat.TEXT);

        final ODataRetrieveResponse<ODataValue> res = req.execute();
        assertEquals(200, res.getStatusCode());

        final ODataValue value = res.getBody();
        debugODataValue(value, "Retrieved property");

        assertNotNull(value);
        // the following assert depends on the test execution order (use >= to be sure)
        assertTrue(Integer.valueOf(value.toString()) >= 10);
    }

    @Test
    public void nullNullableProperty() {
        final ODataDeleteResponse res = client.getCUDRequestFactory().getDeleteRequest(client.getURIBuilder(
                getServiceRoot()).
                appendEntityTypeSegment("Order").appendKeySegment(-8).
                appendStructuralSegment("CustomerId").appendValueSegment().build()).
                execute();
        assertEquals(204, res.getStatusCode());
    }

    @Test(expected = ODataClientErrorException.class)
    public void nullNonNullableProperty() {
        client.getCUDRequestFactory().getDeleteRequest(client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Driver").appendKeySegment("1").
                appendStructuralSegment("BirthDate").appendValueSegment().build()).
                execute();
    }

    private void updatePropertyValue(final ODataValueFormat format, final UpdateType type) throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-9).
                appendStructuralSegment("PrimaryContactInfo").
                appendStructuralSegment("HomePhone").
                appendStructuralSegment("PhoneNumber").
                appendValueSegment();

        ODataValueRequest retrieveReq = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        ODataRetrieveResponse<ODataValue> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        ODataValue phoneNumber = retrieveRes.getBody();
        assertNotNull(phoneNumber);

        final String oldMsg = phoneNumber.asPrimitive().<String>toCastValue();
        final String newMsg = "new msg (" + System.currentTimeMillis() + ")";

        assertNotEquals(newMsg, oldMsg);

        final ODataPrimitiveValue newVal = client.getPrimitiveValueBuilder().setText(newMsg).build();

        final ODataValueUpdateRequest updateReq =
                client.getCUDRequestFactory().getValueUpdateRequest(uriBuilder.build(), type, newVal);
        updateReq.setFormat(format);

        final ODataValueUpdateResponse updateRes = updateReq.execute();
        assertEquals(204, updateRes.getStatusCode());

        retrieveReq = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        phoneNumber = retrieveRes.getBody();
        assertNotNull(phoneNumber);

        assertEquals(newMsg, phoneNumber.asPrimitive().<String>toCastValue());
    }

    private void updateComplexProperty(final ODataFormat format, final UpdateType type) throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-9).appendStructuralSegment("PrimaryContactInfo");

        ODataPropertyRequest retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        ODataProperty primaryContactInfo = client.getObjectFactory().
                newComplexProperty("PrimaryContactInfo", retrieveRes.getBody().getComplexValue());

        final String newItem = "new item " + System.currentTimeMillis();

        final ODataCollectionValue originalValue =
                primaryContactInfo.getComplexValue().get("EmailBag").getCollectionValue();

        final int origSize = originalValue.size();

        originalValue.add(client.getPrimitiveValueBuilder().setText(newItem).build());
        assertEquals(origSize + 1, originalValue.size());

        final ODataPropertyUpdateRequest updateReq = client.getCUDRequestFactory().
                getPropertyComplexValueUpdateRequest(uriBuilder.build(), type, primaryContactInfo);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, updateReq.getMethod());
        } else {
            assertEquals(type.getMethod(), updateReq.getMethod());
        }
        updateReq.setFormat(format);

        final ODataPropertyUpdateResponse updateRes = updateReq.execute();
        assertEquals(204, updateRes.getStatusCode());

        retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        primaryContactInfo = retrieveRes.getBody();

        assertEquals(origSize + 1, primaryContactInfo.getComplexValue().get("EmailBag").getCollectionValue().size());
    }

    private void updateCollectionProperty(final ODataFormat format) throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntityTypeSegment("Customer").appendKeySegment(-9).
                appendStructuralSegment("PrimaryContactInfo").appendStructuralSegment("AlternativeNames");

        ODataPropertyRequest retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        ODataProperty alternativeNames = client.getObjectFactory().newCollectionProperty("AlternativeNames",
                retrieveRes.getBody().getCollectionValue());

        final String newItem = "new item " + System.currentTimeMillis();

        final ODataCollectionValue originalValue = alternativeNames.getCollectionValue();

        final int origSize = originalValue.size();

        originalValue.add(client.getPrimitiveValueBuilder().setText(newItem).build());
        assertEquals(origSize + 1, originalValue.size());

        final ODataPropertyUpdateRequest updateReq =
                client.getCUDRequestFactory().getPropertyCollectionValueUpdateRequest(uriBuilder.build(),
                        alternativeNames);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, updateReq.getMethod());
        } else {
            assertEquals(HttpMethod.PUT, updateReq.getMethod());
        }
        updateReq.setFormat(format);

        ODataPropertyUpdateResponse updateRes = updateReq.execute();
        assertEquals(204, updateRes.getStatusCode());

        retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        alternativeNames = retrieveRes.getBody();

        assertEquals(origSize + 1, alternativeNames.getCollectionValue().size());
    }

    private void updatePrimitiveProperty(final ODataFormat format) throws IOException {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
        uriBuilder.appendEntityTypeSegment("Customer").appendKeySegment(-9).
                appendStructuralSegment("PrimaryContactInfo").
                appendStructuralSegment("HomePhone").appendStructuralSegment("PhoneNumber");

        ODataPropertyRequest retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        ODataProperty phoneNumber = retrieveRes.getBody();

        final String oldMsg = phoneNumber.getPrimitiveValue().<String>toCastValue();
        final String newMsg = "new item " + System.currentTimeMillis();

        assertNotEquals(newMsg, oldMsg);

        phoneNumber = client.getObjectFactory().newPrimitiveProperty("PhoneNumber",
                client.getPrimitiveValueBuilder().setText(newMsg).build());

        final ODataPropertyUpdateRequest updateReq =
                client.getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest(uriBuilder.build(), phoneNumber);
        if (client.getConfiguration().isUseXHTTPMethod()) {
            assertEquals(HttpMethod.POST, updateReq.getMethod());
        } else {
            assertEquals(HttpMethod.PUT, updateReq.getMethod());
        }
        updateReq.setFormat(format);

        ODataPropertyUpdateResponse updateRes = updateReq.execute();
        assertEquals(204, updateRes.getStatusCode());

        retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
        retrieveReq.setFormat(format);

        retrieveRes = retrieveReq.execute();
        assertEquals(200, retrieveRes.getStatusCode());

        phoneNumber = retrieveRes.getBody();
        assertEquals(newMsg, phoneNumber.getPrimitiveValue().<String>toCastValue());
    }

    private void genericRequest(final ODataFormat format) {
        final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
                appendEntityTypeSegment("Customer").appendKeySegment(-10).appendStructuralSegment("BackupContactInfo");

        final ODataGenericRetrieveRequest req =
                client.getRetrieveRequestFactory().getGenericRetrieveRequest(uriBuilder.build());
        req.setFormat(format.toString());

        final ODataRetrieveResponse<ODataObjectWrapper> res = req.execute();

        ODataObjectWrapper wrapper = res.getBody();

        final ODataProperty property = wrapper.getODataProperty();
        assertNotNull(property);
    }
}
