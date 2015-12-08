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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;

public class SystemQueryOptionITCase extends AbstractParamTecSvcITCase {
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String ES_SERVER_SIDE_PAGING = "ESServerSidePaging";
  private static final String ES_ALL_PRIM = "ESAllPrim";

  @Test
  public void countSimple() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .count(true)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(Integer.valueOf(3), response.getBody().getCount());
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void serverSidePagingCount() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .count(true)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(10, response.getBody().getEntities().size());
    assertEquals(Integer.valueOf(503), response.getBody().getCount());
  }

  @Test
  public void topSimple() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .top(5)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(5, response.getBody().getEntities().size());

    for (int i = 0; i < 5; i++) {
      ClientEntity entity = response.getBody().getEntities().get(i);
      assertShortOrInt(i + 1, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    }
  }

  @Test
  public void skipSimple() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .skip(5)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(10, response.getBody().getEntities().size());

    for (int i = 0; i < 10; i++) {
      ClientEntity entity = response.getBody().getEntities().get(i);
      assertShortOrInt(i + 6, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    }
  }

  @Test
  public void topNothing() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .top(20)
            .skip(503)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertTrue(response.getBody().getEntities().isEmpty());
  }

  @Test
  public void skipNothing() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .skip(10000)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertTrue(response.getBody().getEntities().isEmpty());
  }

  @Test
  public void searchAndFilterWithTopSkipOrderByAndServerSidePaging() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .search("\"Number:\" AND NOT \"106\"") // 1, 2, ..., 105, 107, ...
            .filter("PropertyInt16 le 106") // 1, 2, ..., 105
            .orderBy("PropertyInt16 desc") // 105, 104, ..., 2, 1
            .count(true) // 105
            .skip(3) // 102, 101, ..., 2, 1
            .top(43) // 102, 101, ...., 59
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    assertEquals(Integer.valueOf(105), response.getBody().getCount());
    assertEquals(10, response.getBody().getEntities().size());

    int id = 102;

    // Check first 10 entities.
    for (int i = 0; i < 10; i++) {
      ClientEntity entity = response.getBody().getEntities().get(i);
      assertShortOrInt(id, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      id--;
    }

    // Get 3 * 10 = 30 Entities and check the key.
    for (int j = 0; j < 3; j++) {
      request = getClient().getRetrieveRequestFactory().getEntitySetRequest(response.getBody().getNext());
      setCookieHeader(request);
      response = request.execute();
      saveCookieHeader(response);
      assertEquals(Integer.valueOf(105), response.getBody().getCount());
      assertEquals(10, response.getBody().getEntities().size());
      for (int i = 0; i < 10; i++) {
        ClientEntity entity = response.getBody().getEntities().get(i);
        assertShortOrInt(id, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        id--;
      }
    }

    // Get the last 3 items.
    request = getClient().getRetrieveRequestFactory().getEntitySetRequest(response.getBody().getNext());
    setCookieHeader(request);
    response = request.execute();
    saveCookieHeader(response);
    assertEquals(Integer.valueOf(105), response.getBody().getCount());
    assertEquals(3, response.getBody().getEntities().size());
    for (int i = 0; i < 3; i++) {
      ClientEntity entity = response.getBody().getEntities().get(i);
      assertShortOrInt(id, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      id--;
    }

    // Make sure that the body no not contain a next link
    assertNull(response.getBody().getNext());
  }

  @Test
  public void nextLinkFormat() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    // Check initial next link format
    URI nextLink = response.getBody().getNext();
    assertEquals(SERVICE_URI + "ESServerSidePaging?%24skiptoken=1%2A10", nextLink.toASCIIString());

    // Check subsequent next links.
    request = getClient().getRetrieveRequestFactory().getEntitySetRequest(nextLink);
    setCookieHeader(request);
    response = request.execute();
    saveCookieHeader(response);

    nextLink = response.getBody().getNext();
    assertEquals(SERVICE_URI + "ESServerSidePaging?%24skiptoken=2%2A10", nextLink.toASCIIString());
  }

  @Test
  public void nextLinkFormatWithQueryOptions() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
            .count(true)
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);

    // Check initial next link format
    URI nextLink = response.getBody().getNext();
    assertEquals(SERVICE_URI + "ESServerSidePaging?%24count=true&%24skiptoken=1%2A10",
        nextLink.toASCIIString());

    int token = 1;
    while (nextLink != null) {
      token++;

      // Check subsequent next links.
      request = getClient().getRetrieveRequestFactory().getEntitySetRequest(nextLink);
      setCookieHeader(request);
      response = request.execute();
      saveCookieHeader(response);

      nextLink = response.getBody().getNext();
      if (nextLink != null) {
        assertEquals(SERVICE_URI + "ESServerSidePaging?%24count=true&%24skiptoken=" + token + "%2A10",
            nextLink.toASCIIString());
      }
    }

    assertEquals(50 + 1, token);
  }

  @Test
  public void nextLinkFormatWithClientPageSize() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_SERVER_SIDE_PAGING).build();
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
    request.setPrefer(getClient().newPreferences().maxPageSize(7));
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals("odata.maxpagesize=7", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    assertEquals(SERVICE_URI + ES_SERVER_SIDE_PAGING + "?%24skiptoken=1%2A" + 7,
        response.getBody().getNext().toASCIIString());
  }

  @Test
  public void negativeSkip() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .skip(-5)
            .build());
    setCookieHeader(request);
    try {
      request.execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void negativeTop() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .top(-5)
            .build());
    setCookieHeader(request);
    try {
      request.execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void basicSearch() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .search("Second")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    assertEquals(1, response.getBody().getEntities().size());
  }

  @Test
  public void basicSearchPhrase() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .search("\"This is a \\\"$imple\\\"\\\\Phras~\" AND "
                + "AnUnicodeWordLl\u01E3Lm\u02B5Lo\u00AALt\u01F2Lu\u03D3Nl\u216F")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    assertTrue(response.getBody().getEntities().isEmpty());
  }

  @Test
  public void andSearch() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .search("Second AND positive")
            .build());
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    assertTrue(response.getBody().getEntities().isEmpty());
  }

  @Test
  public void orSearch() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_ALL_PRIM)
            .search("Second OR positive")
            .build());
    setCookieHeader(request);
    ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    assertEquals(2, response.getBody().getEntities().size());
  }
}
