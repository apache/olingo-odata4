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
import java.net.URI;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ODataEntitySetIterator;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class EntitySetTestITCase extends AbstractTestITCase {

  private void rawRequest(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ODataEntitySet> entitySet = res.getBodyAs(ODataEntitySet.class);
    assertNotNull(entitySet.getPayload());
    assertTrue(entitySet.getContextURL().getURI().toASCIIString().endsWith("$metadata#People"));
  }

  @Test
  public void rawRequestAsAtom() throws IOException {
    rawRequest(ODataPubFormat.ATOM);
  }

  @Test
  public void rawRequestAsJSON() throws IOException {
    rawRequest(ODataPubFormat.JSON);
  }

  private void readWithInlineCount(final ODataClient client, final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").count(true);

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ODataEntitySet> entitySet = res.getBodyAs(ODataEntitySet.class);
    assertEquals(5, entitySet.getPayload().getCount());

    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Address",
            entitySet.getPayload().getEntities().get(2).getProperty("HomeAddress").getComplexValue().getTypeName());
  }

  @Test
  public void readWithInlineCountAsJSON() throws IOException {
    readWithInlineCount(edmClient, ODataPubFormat.JSON);
  }

  @Test
  public void readWithInlineCountAsFullJSON() throws IOException {
    readWithInlineCount(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void readWithInlineCountAsAtom() throws IOException {
    readWithInlineCount(client, ODataPubFormat.ATOM);
  }

  private void readODataEntitySetIterator(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    final ODataEntitySetIteratorRequest<ODataEntitySet, ODataEntity> req =
            client.getRetrieveRequestFactory().getEntitySetIteratorRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntitySetIterator<ODataEntitySet, ODataEntity>> res = req.execute();
    final ODataEntitySetIterator<ODataEntitySet, ODataEntity> feedIterator = res.getBody();

    assertNotNull(feedIterator);

    int count = 0;

    while (feedIterator.hasNext()) {
      assertNotNull(feedIterator.next());
      count++;
    }
    assertEquals(5, count);
    assertTrue(feedIterator.getNext().toASCIIString().endsWith("People?$skiptoken=5"));
  }

  @Test
  public void readODataEntitySetIteratorFromAtom() {
    readODataEntitySetIterator(ODataPubFormat.ATOM);
  }

  @Test
  public void readODataEntitySetIteratorFromJSON() {
    readODataEntitySetIterator(ODataPubFormat.JSON);
  }

  @Test
  public void readODataEntitySetIteratorFromJSONFull() {
    readODataEntitySetIterator(ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void readODataEntitySetIteratorFromJSONNo() {
    readODataEntitySetIterator(ODataPubFormat.JSON_NO_METADATA);
  }

  private void readWithNext(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    final ODataEntitySetRequest<ODataEntitySet> req = client.getRetrieveRequestFactory().
            getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);
    req.setPrefer(client.newPreferences().maxPageSize(5));

    final ODataRetrieveResponse<ODataEntitySet> res = req.execute();
    final ODataEntitySet feed = res.getBody();

    assertNotNull(feed);

    assertEquals(5, feed.getEntities().size());
    assertNotNull(feed.getNext());

    final URI expected = URI.create(testStaticServiceRootURL + "/People?$skiptoken=5");
    final URI found = URIUtils.getURI(testStaticServiceRootURL, feed.getNext().toASCIIString());

    assertEquals(expected, found);
  }

  @Test
  public void readWithNextFromAtom() {
    readWithNext(ODataPubFormat.ATOM);
  }

  @Test
  public void readWithNextFromJSON() {
    readWithNext(ODataPubFormat.JSON_FULL_METADATA);
  }

}
