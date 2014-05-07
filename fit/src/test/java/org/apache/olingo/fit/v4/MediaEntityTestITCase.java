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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityUpdateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class MediaEntityTestITCase extends AbstractTestITCase {

  private void read(final ODataClient client, final ODataPubFormat format) throws IOException {
    final URIBuilder builder = client.getURIBuilder(testDemoServiceRootURL).
            appendEntitySetSegment("Advertisements").
            appendKeySegment(UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7"));
    final ODataEntityRequest<ODataEntity> entityReq =
            client.getRetrieveRequestFactory().getEntityRequest(builder.build());

    final ODataEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);
    assertTrue(entity.isMediaEntity());
    assertEquals(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName().toString(),
            entity.getProperty("AirDate").getValue().getTypeName());

    final ODataMediaRequest streamReq = client.getRetrieveRequestFactory().
            getMediaRequest(entity.getMediaContentSource());
    final ODataRetrieveResponse<InputStream> streamRes = streamReq.execute();
    assertEquals(200, streamRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(streamRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(streamRes.getBody(), actual, 0, actual.length);
  }

  @Test
  public void readAsAtom() throws IOException {
    read(client, ODataPubFormat.ATOM);
  }

  @Test
  public void readAsJSON() throws IOException {
    read(ODataClientFactory.getEdmEnabledV4(testDemoServiceRootURL), ODataPubFormat.JSON);
  }

  @Test
  public void readAsJSONFull() throws IOException {
    read(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  private void update(final ODataClient client, final ODataPubFormat format)
          throws IOException, EdmPrimitiveTypeException {

    final URI uri = client.getURIBuilder(testDemoServiceRootURL).
            appendEntitySetSegment("Advertisements").
            appendKeySegment(UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7")).appendValueSegment().build();

    final String random = RandomStringUtils.random(124);

    // 1. update providing media content
    final ODataMediaEntityUpdateRequest<ODataEntity> updateReq = client.getStreamedRequestFactory().
            getMediaEntityUpdateRequest(uri, IOUtils.toInputStream(random));
    updateReq.setFormat(format);

    final MediaEntityUpdateStreamManager<ODataEntity> streamManager = updateReq.execute();
    final ODataMediaEntityUpdateResponse<ODataEntity> createRes = streamManager.getResponse();
    assertEquals(204, createRes.getStatusCode());

    // 2. check that media content was effectively uploaded
    final ODataMediaRequest streamReq = client.getRetrieveRequestFactory().getMediaRequest(uri);
    final ODataRetrieveResponse<InputStream> streamRes = streamReq.execute();
    assertEquals(200, streamRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(streamRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(streamRes.getBody(), actual, 0, actual.length);
    assertEquals(random, new String(actual));
  }

  @Test
  public void updateAsAtom() throws IOException, EdmPrimitiveTypeException {
    update(client, ODataPubFormat.ATOM);
  }

  @Test
  public void updateAsJSON() throws IOException, EdmPrimitiveTypeException {
    update(client, ODataPubFormat.JSON);
  }
}
