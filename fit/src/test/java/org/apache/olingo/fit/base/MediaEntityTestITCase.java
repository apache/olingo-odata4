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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collection;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityUpdateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class MediaEntityTestITCase extends AbstractTestITCase {

  private void read(final ODataClient client, final ContentType contentType) throws IOException, URISyntaxException {
    final URIBuilder builder = client.newURIBuilder(testDemoServiceRootURL).
        appendEntitySetSegment("Advertisements").
        appendKeySegment(UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7"));
    final ODataEntityRequest<ClientEntity> entityReq =
        client.getRetrieveRequestFactory().getEntityRequest(builder.build());
    entityReq.setFormat(contentType);

    final ClientEntity entity = entityReq.execute().getBody();
    assertNotNull(entity);
    assertTrue(entity.isMediaEntity());
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals(EdmPrimitiveTypeKind.DateTimeOffset.getFullQualifiedName().toString(),
        ((ClientValuable) entity.getProperty("AirDate")).getValue().getTypeName());

    final ODataMediaRequest streamReq = client.getRetrieveRequestFactory().
        getMediaRequest(entity.getMediaContentSource());
    final ODataRetrieveResponse<InputStream> streamRes = streamReq.execute();
    assertEquals(200, streamRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(streamRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(streamRes.getBody(), actual, 0, actual.length);
  }

  @Test
  public void readAsAtom() throws IOException, URISyntaxException {
    read(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void readAsJSON() throws IOException, URISyntaxException {
    read(ODataClientFactory.getEdmEnabledClient(testDemoServiceRootURL, ContentType.JSON), ContentType.JSON);
  }

  @Test
  public void readAsJSONFull() throws IOException, URISyntaxException {
    read(client, ContentType.JSON_FULL_METADATA);
  }

  private void create(final ContentType contentType) throws IOException, URISyntaxException {
    final String random = RandomStringUtils.random(110);
    final InputStream input = IOUtils.toInputStream(random);

    final URI uri = client.newURIBuilder(testDemoServiceRootURL).appendEntitySetSegment("Advertisements").build();
    final ODataMediaEntityCreateRequest<ClientEntity> createReq =
        client.getCUDRequestFactory().getMediaEntityCreateRequest(uri, input);
    createReq.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());
    final MediaEntityCreateStreamManager<ClientEntity> streamManager = createReq.payloadManager();

    ODataMediaEntityCreateResponse<ClientEntity> createRes = null;
    try {
      createRes = streamManager.getResponse();
    } catch (final RuntimeException ex) {
      // If service rejects content type (415), retry using binary octet-stream content type
      Throwable t = ex;
      org.apache.olingo.client.api.communication.ODataClientErrorException oce = null;
      while (t != null) {
        if (t instanceof org.apache.olingo.client.api.communication.ODataClientErrorException) {
          oce = (org.apache.olingo.client.api.communication.ODataClientErrorException) t;
          break;
        }
        t = t.getCause();
      }
      if (oce != null && oce.getStatusLine() != null && oce.getStatusLine().getStatusCode() == 415) {
        // recreate input and retry with octet-stream
        final InputStream retryInput = IOUtils.toInputStream(random);
        final ODataMediaEntityCreateRequest<ClientEntity> retryReq =
            client.getCUDRequestFactory().getMediaEntityCreateRequest(uri, retryInput);
        retryReq.setContentType(ContentType.APPLICATION_OCTET_STREAM.toContentTypeString());
        final MediaEntityCreateStreamManager<ClientEntity> retryManager = retryReq.payloadManager();
        createRes = retryManager.getResponse();
      } else {
        throw ex;
      }
    }
    assertEquals(201, createRes.getStatusCode());

    final Collection<String> location = createRes.getHeader(HttpHeader.LOCATION);
    assertNotNull(location);
    final URI createdLocation = URI.create(location.iterator().next());

    final ClientEntity changes = client.getObjectFactory().
        newEntity(new FullQualifiedName("ODataDemo.Advertisement"));
    changes.getProperties().add(client.getObjectFactory().newPrimitiveProperty("AirDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(Calendar.getInstance()).build()));

    final ODataEntityUpdateRequest<ClientEntity> updateReq = getClient().getCUDRequestFactory().
        getEntityUpdateRequest(createdLocation, UpdateType.PATCH, changes);
    updateReq.setFormat(contentType);

    final ODataEntityUpdateResponse<ClientEntity> updateRes = updateReq.execute();
    assertEquals(204, updateRes.getStatusCode());

    final ODataMediaRequest retrieveReq = client.getRetrieveRequestFactory().
        getMediaEntityRequest(client.newURIBuilder(createdLocation.toASCIIString()).appendValueSegment().build());
    final ODataRetrieveResponse<InputStream> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(retrieveRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(retrieveRes.getBody(), actual, 0, actual.length);
    assertEquals(random, new String(actual));
  }

  @Test
  public void createAsAtom() throws IOException, URISyntaxException {
    create(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void createAsJSON() throws IOException, URISyntaxException {
    create(ContentType.JSON);
  }

  private void update(final ContentType contentType) throws IOException, EdmPrimitiveTypeException, URISyntaxException {
    final URI uri = client.newURIBuilder(testDemoServiceRootURL).
        appendEntitySetSegment("Advertisements").
        appendKeySegment(UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7")).appendValueSegment().build();

    final String random = RandomStringUtils.random(124);

    // 1. update providing media content
    final ODataMediaEntityUpdateRequest<ClientEntity> updateReq = client.getCUDRequestFactory().
        getMediaEntityUpdateRequest(uri, IOUtils.toInputStream(random));
    updateReq.setFormat(contentType);
    updateReq.setContentType(ContentType.TEXT_PLAIN.toContentTypeString());

    final MediaEntityUpdateStreamManager<ClientEntity> streamManager = updateReq.payloadManager();
    final ODataMediaEntityUpdateResponse<ClientEntity> createRes = streamManager.getResponse();
    assertEquals(204, createRes.getStatusCode());

    // 2. check that media content was effectively uploaded
    final ODataMediaRequest streamReq = client.getRetrieveRequestFactory().getMediaEntityRequest(uri);
    final ODataRetrieveResponse<InputStream> streamRes = streamReq.execute();
    assertEquals(200, streamRes.getStatusCode());

    final byte[] actual = new byte[Integer.parseInt(streamRes.getHeader("Content-Length").iterator().next())];
    IOUtils.read(streamRes.getBody(), actual, 0, actual.length);
    assertEquals(random, new String(actual));
  }

  @Test
  public void updateAsAtom() throws IOException, EdmPrimitiveTypeException, URISyntaxException {
    update(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void updateAsJSON() throws IOException, EdmPrimitiveTypeException, URISyntaxException {
    update(ContentType.JSON);
  }
}
