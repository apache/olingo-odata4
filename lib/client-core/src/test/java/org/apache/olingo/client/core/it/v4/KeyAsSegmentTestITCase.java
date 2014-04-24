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
package org.apache.olingo.client.core.it.v4;

import java.net.URI;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import static org.junit.Assert.assertFalse;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyAsSegmentTestITCase extends AbstractTestITCase {

  @BeforeClass
  public static void enableKeyAsSegment() {
    client.getConfiguration().setKeyAsSegment(true);
  }

  private void read(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testKeyAsSegmentServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101);

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);

    assertFalse(entity.getEditLink().toASCIIString().contains("("));
    assertFalse(entity.getEditLink().toASCIIString().contains(")"));
  }

  @Test
  public void atomRead() {
    read(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonRead() {
    read(ODataPubFormat.JSON);
  }

  @Test
  public void atomCreateAndDelete() {
    createAndDeleteOrder(ODataPubFormat.ATOM, 1000);
  }

  @Test
  public void jsonCreateAndDelete() {
    createAndDeleteOrder(ODataPubFormat.JSON, 1001);
  }

  private void update(final ODataPubFormat format) {
    final ODataEntity changes = getClient().getObjectFactory().newEntity(
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));
    final ODataProperty middleName = getClient().getObjectFactory().newPrimitiveProperty("MiddleName",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("middle"));
    changes.getProperties().add(middleName);

    final URI uri = getClient().getURIBuilder(testKeyAsSegmentServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).build();
    final ODataEntityUpdateRequest<ODataEntity> req = getClient().getCUDRequestFactory().
            getEntityUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(format);

    final ODataEntityUpdateResponse<ODataEntity> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ODataEntity updated = getClient().getRetrieveRequestFactory().getEntityRequest(uri).execute().getBody();
    assertNotNull(updated);
    assertFalse(updated.getEditLink().toASCIIString().contains("("));
    assertFalse(updated.getEditLink().toASCIIString().contains(")"));

    final ODataProperty updatedMiddleName = updated.getProperty("MiddleName");
    assertNotNull(updatedMiddleName);
    assertEquals("middle", updatedMiddleName.getPrimitiveValue().toString());
  }

  @Test
  public void atomUpdate() {
    update(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonUpdate() {
    update(ODataPubFormat.JSON);
  }

  @AfterClass
  public static void disableKeyAsSegment() {
    client.getConfiguration().setKeyAsSegment(false);
  }
}
