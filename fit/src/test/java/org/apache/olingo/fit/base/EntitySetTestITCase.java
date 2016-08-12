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

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientEntitySetIterator;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

/**
 * This is the unit test class to check basic feed operations.
 */
public class EntitySetTestITCase extends AbstractTestITCase {

  private void readODataEntitySetIterator(final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People");

    final ODataEntitySetIteratorRequest<ClientEntitySet, ClientEntity> req =
        client.getRetrieveRequestFactory().getEntitySetIteratorRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntitySetIterator<ClientEntitySet, ClientEntity>> res = req.execute();
    final ClientEntitySetIterator<ClientEntitySet, ClientEntity> feedIterator = res.getBody();

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
    readODataEntitySetIterator(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void readODataEntitySetIteratorFromJSON() {
    readODataEntitySetIterator(ContentType.JSON);
  }

  @Test
  public void readODataEntitySetIteratorFromJSONFull() {
    readODataEntitySetIterator(ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void readODataEntitySetIteratorFromJSONNo() {
    readODataEntitySetIterator(ContentType.JSON_NO_METADATA);
  }
}
