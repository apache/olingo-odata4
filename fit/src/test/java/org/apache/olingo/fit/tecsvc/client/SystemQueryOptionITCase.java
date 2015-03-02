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
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class SystemQueryOptionITCase extends AbstractBaseTestITCase {
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String ES_SERVER_SIDE_PAGING = "ESServerSidePaging";
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  @Test
  public void testCountSimple() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .addQueryOption(QueryOption.COUNT, "true")
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(Integer.valueOf(3), response.getBody().getCount());
    assertEquals(3, response.getBody().getEntities().size());
  }

  @Test
  public void testServerSidePagingCount() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.COUNT, "true")
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(10, response.getBody().getEntities().size());
    assertEquals(Integer.valueOf(503), response.getBody().getCount());
  }

  @Test
  public void testTopSimple() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.TOP, new Integer(5).toString())
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(5, response.getBody().getEntities().size());

    for (int i = 0; i < 5; i++) {
      ODataEntity entity = response.getBody().getEntities().get(i);
      assertEquals(new Integer(i + 1).toString(), entity.getProperty(PROPERTY_INT16).getValue().toString());
    }
  }

  @Test
  public void testSkipSimple() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.SKIP, new Integer(5).toString())
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(10, response.getBody().getEntities().size());

    for (int i = 0; i < 10; i++) {
      ODataEntity entity = response.getBody().getEntities().get(i);
      assertEquals(new Integer(i + 6).toString(), entity.getProperty(PROPERTY_INT16).getValue().toString());
    }
  }

  @Test
  public void testTopNothing() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.TOP, new Integer(20).toString())
        .addQueryOption(QueryOption.SKIP, new Integer(503).toString())
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(0, response.getBody().getEntities().size());
  }

  @Test
  public void testSkipNothing() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.SKIP, new Integer(10000).toString())
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(0, response.getBody().getEntities().size());
  }

  @Test
  public void testFilterWithTopSkipOrderByAndServerSidePaging() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .filter("PropertyInt16 le 105") // 1, 2, ... , 105
        .orderBy("PropertyInt16 desc") // 105, 104, ..., 2, 1
        .addQueryOption(QueryOption.COUNT, Boolean.TRUE.toString()) // 105
        .addQueryOption(QueryOption.SKIP, new Integer(3).toString()) // 102, 101, ..., 2, 1
        .addQueryOption(QueryOption.TOP, new Integer(43).toString()) // 102, 101, ...., 59
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    assertEquals(Integer.valueOf(105), response.getBody().getCount());
    assertEquals(10, response.getBody().getEntities().size());

    int id = 102;

    // Check first 10 entities
    for (int i = 0; i < 10; i++) {
      ODataEntity entity = response.getBody().getEntities().get(i);
      assertEquals(new Integer(id).toString(), entity.getProperty(PROPERTY_INT16).getValue().toString());
      id--;
    }

    // Get 3 * 10 = 30 Entities and check the key
    for (int j = 0; j < 3; j++) {
      response = client.getRetrieveRequestFactory().getEntitySetRequest(response.getBody().getNext()).execute();
      assertEquals(Integer.valueOf(105), response.getBody().getCount());
      assertEquals(10, response.getBody().getEntities().size());
      for (int i = 0; i < 10; i++) {
        ODataEntity entity = response.getBody().getEntities().get(i);
        assertEquals(new Integer(id).toString(), entity.getProperty(PROPERTY_INT16).getValue().toString());
        id--;
      }
    }

    // Get the last 3 items
    response = client.getRetrieveRequestFactory().getEntitySetRequest(response.getBody().getNext()).execute();
    assertEquals(Integer.valueOf(105), response.getBody().getCount());
    assertEquals(3, response.getBody().getEntities().size());
    for (int i = 0; i < 3; i++) {
      ODataEntity entity = response.getBody().getEntities().get(i);
      assertEquals(new Integer(id).toString(), entity.getProperty(PROPERTY_INT16).getValue().toString());
      id--;
    }

    // Make sure that the body no not contain a next link
    assertEquals(null, response.getBody().getNext());
  }

  @Test
  public void testNextLinkFormat() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    // Check initial next link format
    URI nextLink = response.getBody().getNext();
    assertEquals("http://localhost:9080/odata-server-tecsvc/odata.svc/ESServerSidePaging?%24skiptoken=1", nextLink
        .toASCIIString());

    // Check subsequent next links
    response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(nextLink)
        .execute();

    nextLink = response.getBody().getNext();
    assertEquals("http://localhost:9080/odata-server-tecsvc/odata.svc/ESServerSidePaging?%24skiptoken=2", nextLink
        .toASCIIString());
  }

  @Test
  public void testNextLinkFormatWithQueryOptions() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_SERVER_SIDE_PAGING)
        .addQueryOption(QueryOption.COUNT, Boolean.TRUE.toString())
        .build();

    ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
        .getEntitySetRequest(uri)
        .execute();

    // Check initial next link format
    URI nextLink = response.getBody().getNext();
    assertEquals("http://localhost:9080/odata-server-tecsvc/odata.svc/ESServerSidePaging?%24count=true&%24skiptoken=1",
        nextLink.toASCIIString());
    
    int token = 1;
    while (nextLink != null) {
      token++;

      // Check subsequent next links
      response = client.getRetrieveRequestFactory()
          .getEntitySetRequest(nextLink)
          .execute();

      nextLink = response.getBody().getNext();
      if(nextLink != null) {
      assertEquals(
          "http://localhost:9080/odata-server-tecsvc/odata.svc/ESServerSidePaging?%24count=true&%24skiptoken=" + token,
          nextLink.toASCIIString());
      }
    }
    
    assertEquals(50 + 1, token);
  }

  @Test
  @SuppressWarnings("unused")
  public void testNegativeSkip() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .addQueryOption(QueryOption.SKIP, new Integer(-5).toString())
        .build();

    try {
      ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
          .getEntitySetRequest(uri)
          .execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  @SuppressWarnings("unused")
  public void testNegativeTop() {
    ODataClient client = getClient();
    URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .addQueryOption(QueryOption.TOP, new Integer(-5).toString())
        .build();
    try {
      ODataRetrieveResponse<ODataEntitySet> response = client.getRetrieveRequestFactory()
          .getEntitySetRequest(uri)
          .execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getV4();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
