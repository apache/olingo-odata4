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

import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class MediaITCase extends AbstractParamTecSvcITCase {

  @Test
  public void read() throws Exception {
    final ODataMediaRequest request = getClient().getRetrieveRequestFactory().getMediaRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESMedia").appendKeySegment(1).appendValueSegment().build());
    assertNotNull(request);

    final ODataRetrieveResponse<InputStream> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("image/svg+xml", response.getContentType());
    assertEquals("W/\"1\"", response.getETag());

    InputStream media = response.getBody();
    assertNotNull(media);
    assertThat(IOUtils.toString(media), startsWith("<?xml"));
  }

  @Test
  public void delete() {
    final URI uri = getClient().newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESMedia").appendKeySegment(4).appendValueSegment().build();
    ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uri);
    request.setIfMatch("W/\"4\"");
    assertNotNull(request);

    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the media stream is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = getClient().getRetrieveRequestFactory().getMediaRequest(uri);
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
    final URI uri = getClient().newURIBuilder(TecSvcConst.BASE_URI)
        .appendEntitySetSegment("ESMedia").appendKeySegment(4).appendValueSegment().build();
    ODataMediaEntityUpdateRequest<ClientEntity> request =
        getClient().getCUDRequestFactory().getMediaEntityUpdateRequest(uri,
            IOUtils.toInputStream("just a test"));
    request.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());
    request.setIfMatch("W/\"4\"");
    assertNotNull(request);

    final ODataMediaEntityUpdateResponse<ClientEntity> response = request.payloadManager().getResponse();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the media stream has changed.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = getClient().getRetrieveRequestFactory().getMediaRequest(uri);
    mediaRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    ODataRetrieveResponse<InputStream> mediaResponse = mediaRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), mediaResponse.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), mediaResponse.getContentType());
    assertEquals("just a test", IOUtils.toString(mediaResponse.getBody()));
    assertNotNull(mediaResponse.getETag());
    assertNotEquals("W/\"4\"", mediaResponse.getETag());
  }

  @Test
  public void create() throws Exception {
    ODataMediaEntityCreateRequest<ClientEntity> request =
        getClient().getCUDRequestFactory().getMediaEntityCreateRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESMedia").build(),
            IOUtils.toInputStream("just a test"));
    request.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());
    assertNotNull(request);

    final ODataMediaEntityCreateResponse<ClientEntity> response = request.payloadManager().getResponse();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    assertEquals(request.getURI() + "(5)", response.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(5, property.getPrimitiveValue().toValue());

    // Check that the media stream has been created.
    // This check has to be in the same session in order to access the same data provider.
    ODataMediaRequest mediaRequest = getClient().getRetrieveRequestFactory().getMediaRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI).appendEntitySetSegment("ESMedia")
            .appendKeySegment(5).appendValueSegment().build());
    mediaRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    ODataRetrieveResponse<InputStream> mediaResponse = mediaRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), mediaResponse.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), mediaResponse.getContentType());
    assertEquals("just a test", IOUtils.toString(mediaResponse.getBody()));
  }
}
