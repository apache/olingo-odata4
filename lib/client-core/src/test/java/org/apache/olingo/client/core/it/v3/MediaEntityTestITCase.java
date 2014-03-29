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
package org.apache.olingo.client.core.it.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityUpdateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.StreamUpdateStreamManager;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.communication.response.ODataStreamUpdateResponse;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.format.ODataMediaFormat;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.junit.Test;

public class MediaEntityTestITCase extends AbstractTestITCase {

  @Test
  public void read() throws Exception {
    final CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(12).appendValueSegment();

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
    retrieveReq.setFormat(ODataMediaFormat.WILDCARD);

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(retrieveRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(retrieveRes.getBody(), actual, 0, actual.length);
  }

  @Test(expected = ODataClientErrorException.class)
  public void readWithXmlError() throws Exception {
    final CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(12).appendValueSegment();

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
    retrieveReq.setFormat(ODataMediaFormat.APPLICATION_XML);

    retrieveReq.execute();
  }

  @Test(expected = ODataClientErrorException.class)
  public void readWithJsonError() throws Exception {
    final CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(12).appendValueSegment();

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());
    retrieveReq.setFormat(ODataMediaFormat.APPLICATION_JSON);

    retrieveReq.execute();
  }

  @Test
  public void updateMediaEntityAsAtom() throws Exception {
    updateMediaEntity(ODataPubFormat.ATOM, 14);
  }

  @Test
  public void updateMediaEntityAsJson() throws Exception {
    updateMediaEntity(ODataPubFormat.JSON, 15);
  }

  @Test
  public void createMediaEntityAsAtom() throws Exception {
    createMediaEntity(ODataPubFormat.ATOM, IOUtils.toInputStream("buffered stream sample"));
  }

  @Test
  public void createMediaEntityAsJson() throws Exception {
    createMediaEntity(ODataPubFormat.JSON, IOUtils.toInputStream("buffered stream sample"));
  }

  @Test
  public void issue137() throws Exception {
    createMediaEntity(ODataPubFormat.JSON, this.getClass().getResourceAsStream("/sample.png"));
  }

  @Test
  public void updateNamedStream() throws Exception {
    CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(16).appendNavigationSegment("Photo");

    final String TO_BE_UPDATED = "buffered stream sample";
    final InputStream input = new ByteArrayInputStream(TO_BE_UPDATED.getBytes());

    final ODataStreamUpdateRequest updateReq =
            client.getStreamedRequestFactory().getStreamUpdateRequest(builder.build(), input);

    final StreamUpdateStreamManager streamManager = updateReq.execute();
    final ODataStreamUpdateResponse updateRes = streamManager.getResponse();
    updateRes.close();
    assertEquals(204, updateRes.getStatusCode());

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());
    assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
  }

  private void updateMediaEntity(final ODataPubFormat format, final int id) throws Exception {
    CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").appendKeySegment(id).appendValueSegment();

    final String TO_BE_UPDATED = "new buffered stream sample";
    final InputStream input = IOUtils.toInputStream(TO_BE_UPDATED);

    final ODataMediaEntityUpdateRequest updateReq =
            client.getStreamedRequestFactory().getMediaEntityUpdateRequest(builder.build(), input);
    updateReq.setFormat(format);

    final MediaEntityUpdateStreamManager streamManager = updateReq.execute();
    final ODataMediaEntityUpdateResponse updateRes = streamManager.getResponse();
    assertEquals(204, updateRes.getStatusCode());

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());
    assertEquals(TO_BE_UPDATED, IOUtils.toString(retrieveRes.getBody()));
  }

  private void createMediaEntity(final ODataPubFormat format, final InputStream input) throws Exception {
    final CommonURIBuilder<?> builder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car");

    final ODataMediaEntityCreateRequest createReq =
            client.getStreamedRequestFactory().getMediaEntityCreateRequest(builder.build(), input);
    createReq.setFormat(format);

    final MediaEntityCreateStreamManager streamManager = createReq.execute();
    final ODataMediaEntityCreateResponse createRes = streamManager.getResponse();
    assertEquals(201, createRes.getStatusCode());

    final CommonODataEntity created = createRes.getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperties().size());

    Integer id = null;
    for (CommonODataProperty prop : created.getProperties()) {
      if ("VIN".equals(prop.getName())) {
        id = prop.getPrimitiveValue().toCastValue(Integer.class);
      }
    }
    assertNotNull(id);

    builder.appendKeySegment(id).appendValueSegment();

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().getMediaRequest(builder.build());

    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());
    assertNotNull(retrieveRes.getBody());
  }
}
