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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

/**
 * This is the unit test class to check for query options.
 */
public class QueryOptionsTestITCase extends AbstractTestITCase {

  /**
   * Test <tt>$expand</tt>.
   */
  public void expand() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Orders");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    final ClientEntity customer = req.execute().getBody();
    assertTrue(customer.getNavigationLink("Orders") instanceof ClientInlineEntitySet);
  }

  @Test
  public void expandWithFilter() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).
        expandWithOptions("Orders", Collections.<QueryOption, Object> singletonMap(
            QueryOption.FILTER, getClient().getFilterFactory().gt("OrderID", 7).build()));

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    final ClientEntity customer = req.execute().getBody();
    assertTrue(customer.getNavigationLink("Orders") instanceof ClientInlineEntitySet);
  }

  /**
   * Test <tt>$filter</tt> and <tt>$orderby</tt>.
   *
   * @see org.apache.olingo.fit.v4.FilterFactoryTestITCase for more tests.
   */
  @Test
  public void filterOrderby() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").filter("(PersonID lt 3)");

    // 1. check that filtered entity set looks as expected
    ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());

    ClientEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(2, feed.getEntities().size());

    // 2. extract PersonID values - sorted ASC by default
    final List<Integer> former = new ArrayList<Integer>(2);
    for (ClientEntity entity : feed.getEntities()) {
      final Integer personID = entity.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class);
      assertTrue(personID < 3);
      former.add(personID);
    }

    // 3. add orderby clause to filter above
    req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.orderBy("PersonID desc").build());

    feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(2, feed.getEntities().size());

    // 4. extract again VIN value - now they were required to be sorted DESC
    final List<Integer> latter = new ArrayList<Integer>(2);
    for (ClientEntity entity : feed.getEntities()) {
      final Integer personID = entity.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class);
      assertTrue(personID < 3);
      latter.add(personID);
    }

    // 5. reverse latter and expect to be equal to former
    Collections.reverse(latter);
    assertEquals(former, latter);
  }

  /**
   * Test <tt>$format</tt>.
   */
  @Test
  public void format() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).format("json");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataFormat.ATOM);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertNotNull(res);
    assertTrue(res.getContentType().replaceAll(" ", "").
        startsWith(ODataFormat.JSON.getContentType().toContentTypeString()));
  }

  /**
   * Test <tt>$skip</tt>.
   */
  public void skip() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    // 1. check that filtered entity set looks as expected
    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.skip(2).build());

    final ClientEntitySet feed = req.execute().getBody();
    assertEquals(3, feed.getEntities().size());
  }

  /**
   * Test <tt>$top</tt>.
   */
  public void top() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    // 1. check that filtered entity set looks as expected
    final ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.top(2).build());

    final ClientEntitySet feed = req.execute().getBody();
    assertEquals(2, feed.getEntities().size());
  }

  /**
   * Test <tt>$skiptoken</tt>.
   */
  @Test
  public void skiptoken() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL);
    uriBuilder.appendEntitySetSegment("People").skipToken("5");

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());

    final ClientEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(1, feed.getEntities().size());

    for (ClientEntity entity : feed.getEntities()) {
      assertTrue(entity.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class) > 5);
    }
  }

  /**
   * Test <tt>$inlinecount</tt>.
   */
  @Test
  public void count() {
    final URIBuilder uriBuilder =
        client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customers").count(true);

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());

    final ClientEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(Integer.valueOf(feed.getEntities().size()), feed.getCount());
  }

  /**
   * Test <tt>$select</tt>.
   */
  @Test
  public void select() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).select("PersonID,Orders").expand("Orders");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    final ClientEntity customer = req.execute().getBody();
    assertEquals(1, customer.getProperties().size());
    assertEquals(1, customer.getNavigationLinks().size());
    assertTrue((customer.getNavigationLinks().get(0) instanceof ClientInlineEntitySet));
  }

  @Test
  public void issue253() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("relatedEntitySelect").appendEntitySetSegment("Customers").appendKeySegment(1).
        expandWithSelect("Orders", "OrderID", "OrderDetails");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());
  }

  @Test
  public void search() {
    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").search(client.getSearchFactory().
            or(client.getSearchFactory().literal("Bob"), client.getSearchFactory().literal("Jill")));

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(builder.build());

    final ODataRetrieveResponse<ClientEntitySet> res = req.execute();
    assertEquals(200, res.getStatusCode());
    assertFalse(res.getBody().getEntities().isEmpty());
  }
}
