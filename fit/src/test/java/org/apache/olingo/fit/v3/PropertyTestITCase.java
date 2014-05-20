/*
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
package org.apache.olingo.fit.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v3.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.communication.response.ODataValueUpdateResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.domain.v3.ODataProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.format.ODataValueFormat;
import org.junit.Test;

/**
 * This is the unit test class to check basic entity operations.
 */
public class PropertyTestITCase extends AbstractTestITCase {

  protected String getServiceRoot() {
    return testStaticServiceRootURL;
  }

  @Test
  public void replacePropertyValue() throws Exception {
    updatePropertyValue(ODataValueFormat.TEXT, UpdateType.REPLACE);
  }

  @Test
  public void replacePrimitivePropertyAsXML() throws IOException, EdmPrimitiveTypeException {
    updatePrimitiveProperty(ODataFormat.XML);
  }

  @Test
  public void replacePrimitivePropertyAsJSON() throws IOException, EdmPrimitiveTypeException {
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
  public void rawRequestAsXML() throws IOException {
    rawRequest(ODataFormat.XML);
  }

  @Test
  public void rawRequestAsJSON() throws IOException {
    rawRequest(ODataFormat.JSON);
  }

  @Test
  public void readCountValue() throws IOException {
    final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
    uriBuilder.appendEntitySetSegment("Customer").count();

    final ODataValueRequest req = client.getRetrieveRequestFactory().getValueRequest(uriBuilder.build());
    req.setFormat(ODataValueFormat.TEXT);

    final ODataRetrieveResponse<ODataPrimitiveValue> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final ODataPrimitiveValue value = res.getBody();
    debugODataValue(value, "Retrieved property");

    assertNotNull(value);
    // the following assert depends on the test execution order (use >= to be sure)
    assertTrue(Integer.valueOf(value.toString()) >= 10);
  }

  @Test
  public void nullNullableProperty() {
    final ODataDeleteResponse res = client.getCUDRequestFactory().getDeleteRequest(client.getURIBuilder(
            getServiceRoot()).
            appendEntitySetSegment("Order").appendKeySegment(-8).
            appendPropertySegment("CustomerId").appendValueSegment().build()).
            execute();
    assertEquals(204, res.getStatusCode());
  }

  @Test(expected = ODataClientErrorException.class)
  public void nullNonNullableProperty() {
    client.getCUDRequestFactory().getDeleteRequest(client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Driver").appendKeySegment("1").
            appendPropertySegment("BirthDate").appendValueSegment().build()).
            execute();
  }

  private void updatePropertyValue(final ODataValueFormat format, final UpdateType type)
          throws IOException, EdmPrimitiveTypeException {

    final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customer").appendKeySegment(-9).
            appendPropertySegment("PrimaryContactInfo").
            appendPropertySegment("HomePhone").
            appendPropertySegment("PhoneNumber");

    ODataValueRequest retrieveReq = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    ODataRetrieveResponse<ODataPrimitiveValue> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    ODataPrimitiveValue phoneNumber = retrieveRes.getBody();
    assertNotNull(phoneNumber);

    final String oldMsg = phoneNumber.toCastValue(String.class);
    final String newMsg = "new msg (" + System.currentTimeMillis() + ")";

    assertNotEquals(newMsg, oldMsg);

    final ODataPrimitiveValue newVal = client.getObjectFactory().newPrimitiveValueBuilder().setText(newMsg).build();

    final ODataValueUpdateRequest updateReq =
            client.getCUDRequestFactory().getValueUpdateRequest(uriBuilder.build(), type, newVal);
    updateReq.setFormat(format);

    final ODataValueUpdateResponse updateRes = updateReq.execute();
    assertEquals(204, updateRes.getStatusCode());

    retrieveReq = client.getRetrieveRequestFactory().getPropertyValueRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    phoneNumber = retrieveRes.getBody();
    assertNotNull(phoneNumber);

    assertEquals(newMsg, phoneNumber.asPrimitive().toCastValue(String.class));
  }

  private void updateComplexProperty(final ODataFormat format, final UpdateType type) throws IOException {
    final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customer").appendKeySegment(-9).appendPropertySegment("PrimaryContactInfo");

    ODataPropertyRequest<ODataProperty> retrieveReq = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    ODataProperty primaryContactInfo = client.getObjectFactory().
            newComplexProperty("PrimaryContactInfo", retrieveRes.getBody().getComplexValue());

    final String newItem = "new item " + System.currentTimeMillis();

    final ODataCollectionValue<ODataValue> originalValue =
            primaryContactInfo.getComplexValue().get("EmailBag").getCollectionValue();

    final int origSize = originalValue.size();

    originalValue.add(client.getObjectFactory().newPrimitiveValueBuilder().setText(newItem).build());
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
    uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(-10).
            appendPropertySegment("PrimaryContactInfo").appendPropertySegment("AlternativeNames");

    ODataPropertyRequest<ODataProperty> retrieveReq = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    ODataProperty alternativeNames = client.getObjectFactory().newCollectionProperty("AlternativeNames",
            retrieveRes.getBody().getCollectionValue());

    final String newItem = "new item " + System.currentTimeMillis();

    final ODataCollectionValue<ODataValue> originalValue = alternativeNames.getCollectionValue();

    final int origSize = originalValue.size();

    originalValue.add(client.getObjectFactory().newPrimitiveValueBuilder().setText(newItem).build());
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

    final ODataPropertyUpdateResponse updateRes = updateReq.execute();
    assertEquals(204, updateRes.getStatusCode());

    retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    alternativeNames = retrieveRes.getBody();

    assertEquals(origSize + 1, alternativeNames.getCollectionValue().size());
  }

  private void updatePrimitiveProperty(final ODataFormat format) throws IOException, EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot());
    uriBuilder.appendEntitySetSegment("Customer").appendKeySegment(-9).
            appendPropertySegment("PrimaryContactInfo").
            appendPropertySegment("HomePhone").appendPropertySegment("PhoneNumber");

    ODataPropertyRequest<ODataProperty> retrieveReq = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    ODataProperty phoneNumber = retrieveRes.getBody();

    final String oldMsg = phoneNumber.getPrimitiveValue().toCastValue(String.class);
    final String newMsg = "new item " + System.currentTimeMillis();

    assertNotEquals(newMsg, oldMsg);

    phoneNumber = client.getObjectFactory().newPrimitiveProperty("PhoneNumber",
            client.getObjectFactory().newPrimitiveValueBuilder().setText(newMsg).build());

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
    assertEquals(newMsg, phoneNumber.getPrimitiveValue().toCastValue(String.class));
  }

  private void rawRequest(final ODataFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customer").appendKeySegment(-10).appendPropertySegment("BackupContactInfo");

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ODataProperty> property = res.getBodyAs(ODataProperty.class);
    assertNotNull(property.getPayload());
  }
}
