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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.api.uri.v3.URIBuilder.InlineCount;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.data.AtomEntityImpl;
import org.junit.Test;

/**
 * This is the unit test class to check for query options.
 */
public class QueryOptionsTestITCase extends AbstractTestITCase {

  /**
   * Test <tt>$expand</tt>.
   *
   * @see EntityRetrieveTest#readODataEntityWithInline(org.apache.olingo.commons.api.format.ODataPubFormat)
   */
  public void expand() {
    // empty
  }

  /**
   * Test <tt>$filter</tt> and <tt>orderby</tt>.
   *
   * @see org.apache.olingo.fit.v3.FilterFactoryTestITCase for more tests.
   */
  @Test
  public void filterOrderby() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Car").filter("(VIN lt 16)");

    // 1. check that filtered entity set looks as expected
    ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    ODataEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(5, feed.getEntities().size());

    // 2. extract VIN values - sorted ASC by default
    final List<Integer> vinsASC = new ArrayList<Integer>(5);
    for (ODataEntity entity : feed.getEntities()) {
      final Integer vin = entity.getProperty("VIN").getPrimitiveValue().toCastValue(Integer.class);
      assertTrue(vin < 16);
      vinsASC.add(vin);
    }

    // 3. add orderby clause to filter above
    req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.orderBy("VIN desc").build());
    feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(5, feed.getEntities().size());

    // 4. extract again VIN value - now they were required to be sorted DESC
    final List<Integer> vinsDESC = new ArrayList<Integer>(5);
    for (ODataEntity entity : feed.getEntities()) {
      vinsDESC.add(entity.getProperty("VIN").getPrimitiveValue().toCastValue(Integer.class));
    }

    // 5. reverse vinsASC and expect to be equal to vinsDESC
    Collections.reverse(vinsASC);
    assertEquals(vinsASC, vinsDESC);
  }

  /**
   * Test <tt>$format</tt>.
   */
  @Test
  public void format() {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customer").appendKeySegment(-10).format("json");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.ATOM);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertNotNull(res);
    assertTrue(res.getContentType().replaceAll(" ", "").
            startsWith(ODataPubFormat.JSON.toString(client.getServiceVersion())));
  }

  /**
   * Test <tt>$skip</tt>.
   *
   * @see FeedTest#readFeedWithNextLink(org.apache.olingo.commons.api.format.ODataPubFormat)
   */
  public void skip() {
    // empty
  }

  /**
   * Test <tt>$top</tt>.
   *
   * @see FeedTest#readFeed(org.apache.olingo.commons.api.format.ODataPubFormat)
   */
  public void top() {
    // empty
  }

  /**
   * Test <tt>$skiptoken</tt>.
   */
  @Test
  public void skiptoken() throws EdmPrimitiveTypeException {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL);
    uriBuilder.appendEntitySetSegment("Customer").skipToken("-10");

    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    final ODataEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(2, feed.getEntities().size());

    for (ODataEntity entity : feed.getEntities()) {
      assertTrue(entity.getProperty("CustomerId").getPrimitiveValue().toCastValue(Integer.class) > -10);
    }
  }

  /**
   * Test <tt>$inlinecount</tt>.
   */
  @Test
  public void inlinecount() {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Car").
            inlineCount(InlineCount.allpages);

    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.ATOM);
    final ODataEntitySet feed = req.execute().getBody();
    assertNotNull(feed);
    assertEquals(feed.getEntities().size(), feed.getCount());
  }

  /**
   * Test <tt>$select</tt>.
   */
  @Test
  public void select() {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customer").appendKeySegment(-10).select("CustomerId,Orders").expand("Orders");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    final ODataEntity customer = req.execute().getBody();
    assertEquals(1, customer.getProperties().size());
    assertEquals(1, customer.getNavigationLinks().size());
    assertTrue((customer.getNavigationLinks().get(0) instanceof ODataInlineEntitySet));
  }

  @Test
  public void issue131() {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customer").appendKeySegment(-7).select("Name");

    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.ATOM);

    final ODataEntity customer = req.execute().getBody();
    assertEquals(0, customer.getProperties().size());

    req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.ATOM);

    final Entity atomEntry =
            client.getDeserializer().toEntity(req.execute().getRawResponse(), ODataPubFormat.ATOM).getPayload();
    assertEquals("remotingdestructorprinterswitcheschannelssatellitelanguageresolve",
            ((AtomEntityImpl) atomEntry).getSummary());
  }
}
