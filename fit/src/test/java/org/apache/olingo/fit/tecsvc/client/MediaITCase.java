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
package org.apache.olingo.fit.tecsvc.client;

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public final class MediaITCase extends AbstractBaseTestITCase {

  @Test
  public void read() throws Exception {
    final CommonODataClient<?> client = getClient();
    final ODataMediaRequest request = client.getRetrieveRequestFactory().getMediaRequest(
        client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESMedia").appendKeySegment(1).appendValueSegment().build());
    assertNotNull(request);

    final ODataRetrieveResponse<InputStream> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("image/svg+xml", response.getContentType());

    InputStream media = response.getBody();
    assertNotNull(media);
    assertThat(IOUtils.toString(media), startsWith("<?xml"));
  }

  @Test
  public void delete() {
    final CommonODataClient<?> client = getClient();
    final URI uri = client.newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESMedia").appendKeySegment(4).appendValueSegment().build();
    final ODataDeleteRequest request = client.getCUDRequestFactory().getDeleteRequest(uri);
    assertNotNull(request);

    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the media stream is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = client.getRetrieveRequestFactory().getMediaRequest(uri);
    mediaRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    try {
      mediaRequest.execute();
      fail("Expected exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void update() throws Exception {
    final CommonODataClient<?> client = getClient();
    final URI uri = client.newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESMedia").appendKeySegment(4).appendValueSegment().build();
    ODataMediaEntityUpdateRequest<CommonODataEntity> request =
        client.getCUDRequestFactory().getMediaEntityUpdateRequest(uri,
            IOUtils.toInputStream("just a test"));
    request.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());
    assertNotNull(request);

    final ODataMediaEntityUpdateResponse<CommonODataEntity> response = request.payloadManager().getResponse();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the media stream has changed.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = client.getRetrieveRequestFactory().getMediaRequest(uri);
    mediaRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    ODataRetrieveResponse<InputStream> mediaResponse = mediaRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), mediaResponse.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), mediaResponse.getContentType());
    assertEquals("just a test", IOUtils.toString(mediaResponse.getBody()));
  }

  @Test
  public void create() throws Exception {
    final CommonODataClient<?> client = getClient();
    ODataMediaEntityCreateRequest<CommonODataEntity> request =
        client.getCUDRequestFactory().getMediaEntityCreateRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESMedia").build(),
            IOUtils.toInputStream("just a test"));
    request.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());
    assertNotNull(request);

    final ODataMediaEntityCreateResponse<CommonODataEntity> response = request.payloadManager().getResponse();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    assertEquals(request.getURI() + "(5)", response.getHeader(HttpHeader.LOCATION).iterator().next());
    final CommonODataEntity entity = response.getBody();
    assertNotNull(entity);
    final CommonODataProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(5, property.getPrimitiveValue().toValue());

    // Check that the media stream has been created.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = client.getRetrieveRequestFactory().getMediaRequest(
        client.newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESMedia")
            .appendKeySegment(5).appendValueSegment().build());
    mediaRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    ODataRetrieveResponse<InputStream> mediaResponse = mediaRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), mediaResponse.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), mediaResponse.getContentType());
    assertEquals("just a test", IOUtils.toString(mediaResponse.getBody()));
  }

  @Override
  protected CommonODataClient<?> getClient() {
    ODataClient odata = ODataClientFactory.getV4();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
