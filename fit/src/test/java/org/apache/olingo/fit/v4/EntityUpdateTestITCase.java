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
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataReferenceAddingResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Ignore;
import org.junit.Test;

public class EntityUpdateTestITCase extends AbstractTestITCase {

  private void upsert(final UpdateType updateType, final ODataFormat format) {
    final ClientEntity order = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("OrderID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(9)));
    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(
                Calendar.getInstance(TimeZone.getTimeZone("GMT"))).build()));
    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000002")).build()));

    final URI upsertURI = getClient().newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Orders").appendKeySegment(9).build();
    final ODataEntityUpdateRequest<ClientEntity> req = getClient().getCUDRequestFactory().
        getEntityUpdateRequest(upsertURI, updateType, order);
    req.setFormat(format);

    req.execute();
    try {
      final ClientEntity read = read(format, upsertURI);
      assertNotNull(read);
      assertEquals(order.getProperty("OrderID"), read.getProperty("OrderID"));
      assertEquals(order.getProperty("OrderDate").getPrimitiveValue().toString(),
          read.getProperty("OrderDate").getPrimitiveValue().toString());
      assertEquals(order.getProperty("ShelfLife").getPrimitiveValue().toString(),
          read.getProperty("ShelfLife").getPrimitiveValue().toString());
    } finally {
      getClient().getCUDRequestFactory().getDeleteRequest(upsertURI).execute();
    }
  }

  @Test
  @Ignore
  public void testUpateSingleValuedNavtiogationReference() throws Exception {
    URI targetURI =
        getClient().newURIBuilder(testStaticServiceRootURL)
            .appendEntitySetSegment("People")
            .appendKeySegment(1)
            .appendNavigationSegment("Parent")
            .build();

    URI reference = getClient().newURIBuilder(testStaticServiceRootURL)
        .appendEntitySetSegment("People")
        .appendKeySegment(0)
        .build();

    final ODataReferenceAddingRequest request =
        getClient().getCUDRequestFactory().getReferenceSingleChangeRequest(new URI(testStaticServiceRootURL),
            targetURI, reference);

    final ODataReferenceAddingResponse response = request.execute();

    assertEquals(204, response.getStatusCode());
  }

  @Test
  public void atomUpsert() {
    upsert(UpdateType.PATCH, ODataFormat.ATOM);
    upsert(UpdateType.REPLACE, ODataFormat.ATOM);
  }

  @Test
  public void jsonUpsert() {
    upsert(UpdateType.PATCH, ODataFormat.JSON);
    upsert(UpdateType.REPLACE, ODataFormat.JSON);
  }

  private void onContained(final ODataFormat format) {
    final String newName = UUID.randomUUID().toString();
    final ClientEntity changes = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument"));
    changes.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("FriendlyName",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString(newName)));

    final URI uri = getClient().newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).
        appendNavigationSegment("MyPaymentInstruments").appendKeySegment(101901).build();
    final ODataEntityUpdateRequest<ClientEntity> req = getClient().getCUDRequestFactory().
        getEntityUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(format);

    final ODataEntityUpdateResponse<ClientEntity> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ClientEntity actual = getClient().getRetrieveRequestFactory().getEntityRequest(uri).execute().getBody();
    assertNotNull(actual);
    assertEquals(newName, actual.getProperty("FriendlyName").getPrimitiveValue().toString());
  }

  @Test
  public void atomOnContained() {
    onContained(ODataFormat.ATOM);
  }

  @Test
  public void jsonOnContained() {
    onContained(ODataFormat.JSON);
  }

  private void bindOperation(final ODataFormat format) throws EdmPrimitiveTypeException {
    final ClientEntity changes = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));
    final ClientLink parent = getClient().getObjectFactory().newEntityNavigationLink("Parent",
        getClient().newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(1).build());
    changes.getNavigationLinks().add(parent);

    final URI uri = getClient().newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).build();
    final ODataEntityUpdateRequest<ClientEntity> req = getClient().getCUDRequestFactory().
        getEntityUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(format);

    final ODataEntityUpdateResponse<ClientEntity> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ClientEntity updated = getClient().getRetrieveRequestFactory().getEntityRequest(uri).execute().getBody();
    assertNotNull(updated);
    final ClientLink updatedLink = updated.getNavigationLink("Parent");
    assertNotNull(updatedLink);

    final ClientEntity updatedEntity = getClient().getRetrieveRequestFactory().getEntityRequest(updatedLink.getLink()).
        execute().getBody();
    assertNotNull(updatedEntity);
    assertEquals(1, updatedEntity.getProperty("PersonID").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomBindOperation() throws EdmPrimitiveTypeException {
    bindOperation(ODataFormat.ATOM);
  }

  @Test
  public void jsonBindOperation() throws EdmPrimitiveTypeException {
    bindOperation(ODataFormat.JSON);
  }
}
