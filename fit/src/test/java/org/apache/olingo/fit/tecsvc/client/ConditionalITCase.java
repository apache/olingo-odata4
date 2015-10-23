/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.tecsvc.client;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.ODataBasicRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class ConditionalITCase extends AbstractParamTecSvcITCase {

  private final URI uriEntity = getClient().newURIBuilder(TecSvcConst.BASE_URI)
      .appendEntitySetSegment("ESCompAllPrim").appendKeySegment(0).build();
  private final URI uriProperty = getClient().newURIBuilder(uriEntity.toASCIIString())
      .appendPropertySegment("PropertyComp").appendPropertySegment("PropertyDuration").build();
  private final URI uriPropertyValue = getClient().newURIBuilder(
          uriProperty.toASCIIString()).appendValueSegment().build();
  private final URI uriMedia = getClient().newURIBuilder(TecSvcConst.BASE_URI)
      .appendEntitySetSegment("ESMedia").appendKeySegment(1).appendValueSegment().build();

  @Test
  public void readServiceDocument() throws Exception {
    ODataServiceDocumentRequest request = getClient().getRetrieveRequestFactory()
        .getServiceDocumentRequest(TecSvcConst.BASE_URI);
    setCookieHeader(request);
    ODataRetrieveResponse<ClientServiceDocument> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    request = getClient().getRetrieveRequestFactory().getServiceDocumentRequest(TecSvcConst.BASE_URI);
    request.setIfNoneMatch(response.getETag());
    setCookieHeader(request);
    response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());

    request = getClient().getRetrieveRequestFactory().getServiceDocumentRequest(TecSvcConst.BASE_URI);
    request.setIfMatch("W/\"0\"");
    setCookieHeader(request);
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void readMetadataDocument() throws Exception {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(TecSvcConst.BASE_URI);
    setCookieHeader(request);
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    request = getClient().getRetrieveRequestFactory().getMetadataRequest(TecSvcConst.BASE_URI);
    request.setIfNoneMatch(response.getETag());
    setCookieHeader(request);
    response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());

    request = getClient().getRetrieveRequestFactory().getMetadataRequest(TecSvcConst.BASE_URI);
    request.setIfMatch("W/\"0\"");
    setCookieHeader(request);
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void readWithWrongIfMatch() throws Exception {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory().getEntityRequest(uriEntity);
    request.setIfMatch("W/\"1\"");
    assertNotNull(request);
    setCookieHeader(request);
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void readNotModified() throws Exception {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory().getEntityRequest(uriEntity);
    request.setIfNoneMatch("W/\"0\"");
    assertNotNull(request);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void updateWithoutIfMatch() throws Exception {
    executeAndExpectError(
        getClient().getCUDRequestFactory().getEntityUpdateRequest(
            uriEntity, UpdateType.PATCH, getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "Order"))),
        HttpStatusCode.PRECONDITION_REQUIRED);
  }

  @Test
  public void updateWithWrongIfMatch() throws Exception {
    ODataEntityUpdateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        uriEntity, UpdateType.PATCH, getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "Order")));
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void updateMediaWithWrongIfMatch() throws Exception {
    ODataMediaEntityUpdateRequest<ClientEntity> request =
            getClient().getCUDRequestFactory().getMediaEntityUpdateRequest(uriMedia, IOUtils.toInputStream("ignored"));
    request.setIfMatch("W/\"42\"");

    try {
      request.payloadManager().getResponse();
      fail("Expected Exception not thrown!");
    } catch (final HttpClientException e) {
      final ODataClientErrorException ex = (ODataClientErrorException) e.getCause().getCause();
      assertEquals(HttpStatusCode.PRECONDITION_FAILED.getStatusCode(), ex.getStatusLine().getStatusCode());
      assertThat(ex.getODataError().getMessage(), containsString("condition"));
    }
  }

  @Test
  public void deleteWithWrongIfMatch() throws Exception {
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriEntity);
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void deleteMediaWithWrongIfMatch() throws Exception {
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriMedia);
    request.setIfMatch("W/\"42\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void indirectEntityChange() throws Exception {
    final String eTag = "W/\"0\"";
    ODataDeleteRequest deleteRequest = getClient().getCUDRequestFactory().getDeleteRequest(uriProperty);
    deleteRequest.setIfMatch(eTag);
    final ODataDeleteResponse response = deleteRequest.execute();

    ODataEntityUpdateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        uriEntity, UpdateType.PATCH, getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "Order")));
    request.setIfMatch(eTag);
    // This request has to be in the same session as the first in order to access the same data provider.
    request.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void readPropertyNotModified() throws Exception {
    ODataPropertyRequest<ClientProperty> request =
            getClient().getRetrieveRequestFactory().getPropertyRequest(uriProperty);
    request.setIfNoneMatch("W/\"0\"");
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void readPropertyValueNotModified() throws Exception {
    ODataValueRequest request = getClient().getRetrieveRequestFactory().getPropertyValueRequest(uriPropertyValue);
    request.setIfNoneMatch("W/\"0\"");
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.NOT_MODIFIED.getStatusCode(), response.getStatusCode());
  }

  @Test
  public void updatePropertyWithoutIfMatch() throws Exception {
    final ODataPropertyUpdateRequest request =
            getClient().getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest( uriProperty,
            getFactory().newPrimitiveProperty("PropertyDuration",
            getFactory().newPrimitiveValueBuilder().buildString("PT42S")));
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_REQUIRED);
  }

  @Test
  public void updatePropertyWithWrongIfMatch() throws Exception {
    ODataPropertyUpdateRequest request = getClient().getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest(
        uriProperty,
        getFactory().newPrimitiveProperty("PropertyDuration",
            getFactory().newPrimitiveValueBuilder().buildString("PT42S")));
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void updatePropertyValueWithoutIfMatch() throws Exception {
    final ODataValueUpdateRequest request = getClient().getCUDRequestFactory().getValueUpdateRequest(
        uriPropertyValue,
        UpdateType.REPLACE,
        getFactory().newPrimitiveValueBuilder().buildString("PT42S"));
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_REQUIRED);
  }

  @Test
  public void updatePropertyValueWithWrongIfMatch() throws Exception {
    ODataValueUpdateRequest request = getClient().getCUDRequestFactory().getValueUpdateRequest(
        uriPropertyValue,
        UpdateType.REPLACE,
        getFactory().newPrimitiveValueBuilder().buildString("PT42S"));
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void deletePropertyWithoutIfMatch() throws Exception {
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriProperty);
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_REQUIRED);
  }

  @Test
  public void deletePropertyWithWrongIfMatch() throws Exception {
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriProperty);
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  @Test
  public void deletePropertyValue() throws Exception {
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriPropertyValue);
    request.setIfMatch("W/\"0\"");
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getETag());
    assertNotEquals(request.getIfMatch(), response.getETag());
  }

  @Test
  public void deletePropertyValueWithoutIfMatch() throws Exception {
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriPropertyValue);
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_REQUIRED);
  }

  @Test
  public void deletePropertyValueWithWrongIfMatch() throws Exception {
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uriPropertyValue);
    request.setIfMatch("W/\"1\"");
    executeAndExpectError(request, HttpStatusCode.PRECONDITION_FAILED);
  }

  private void executeAndExpectError(ODataBasicRequest<?> request, final HttpStatusCode status) {
    try {
      request.execute();
      fail("Expected Exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(status.getStatusCode(), e.getStatusLine().getStatusCode());
      assertThat(e.getODataError().getMessage(), anyOf(containsString("condition"), containsString("match")));
    }
  }
}
