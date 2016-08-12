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

import java.net.URI;
import java.sql.Timestamp;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTestITCase {

  private void contained(final ODataClient client, final ContentType contentType) throws EdmPrimitiveTypeException {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).
        appendNavigationSegment("MyPaymentInstruments").appendKeySegment(101902).build();
    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(contentType);

    final ClientEntity contained = req.execute().getBody();
    assertNotNull(contained);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument", contained.getTypeName().toString());
    assertEquals(101902,
        contained.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
    assertEquals("Edm.DateTimeOffset", contained.getProperty("CreatedDate").getPrimitiveValue().getTypeName());
    assertNotNull(contained.getProperty("CreatedDate").getPrimitiveValue().toCastValue(Timestamp.class));
  }

  @Test
  public void containedFromAtom() throws EdmPrimitiveTypeException {
    contained(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void containedFromFullJSON() throws EdmPrimitiveTypeException {
    contained(client, ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void containedFromJSON() throws EdmPrimitiveTypeException {
    contained(edmClient, ContentType.JSON);
  }

  private void entitySetNavigationLink(final ODataClient client, final ContentType contentType) {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).build();
    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(contentType);

    final ClientEntity entity = req.execute().getBody();
    assertNotNull(entity);

    // With JSON, entity set navigation links are only recognizable via Edm
    if (contentType.equals(ContentType.APPLICATION_ATOM_XML) || client instanceof EdmEnabledODataClient) {
      assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("MyPaymentInstruments").getType());
      assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("ActiveSubscriptions").getType());
    }
  }

  @Test
  public void entitySetNavigationLinkFromAtom() {
    entitySetNavigationLink(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void entitySetNavigationLinkFromJSON() {
    // only JSON_FULL_METADATA has links, only Edm can recognize entity set navigation
    entitySetNavigationLink(edmClient, ContentType.JSON_FULL_METADATA);
  }

}
