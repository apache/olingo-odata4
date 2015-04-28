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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.domain.ClientComplexValue;
import org.apache.olingo.commons.api.domain.ClientEntity;
import org.apache.olingo.commons.api.domain.ClientEntitySet;
import org.apache.olingo.commons.api.domain.ClientValuable;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class DerivedTypeTestITCase extends AbstractTestITCase {

  private void read(final ODataFormat format) {
    // 1. entity set
    URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").
        appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.ODataWCFService.Customer");
    ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    for (ClientEntity customer : req.execute().getBody().getEntities()) {
      assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", customer.getTypeName().toString());
    }

    // 2. contained entity set
    uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).
        appendNavigationSegment("MyPaymentInstruments").
        appendDerivedEntityTypeSegment("Microsoft.Test.OData.Services.ODataWCFService.CreditCardPI");
    req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    for (ClientEntity customer : req.execute().getBody().getEntities()) {
      assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CreditCardPI", customer.getTypeName().toString());
    }
  }

  @Test
  public void readfromAtom() {
    read(ODataFormat.ATOM);
  }

  @Test
  public void readfromJSON() {
    read(ODataFormat.JSON_FULL_METADATA);
  }

  private void createDelete(final ODataFormat format) {
    final ClientEntity customer = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));

    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("PersonID",
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt32(976)));
    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Test")));
    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Test")));

    final ClientComplexValue homeAddress =
        client.getObjectFactory().newComplexValue("Microsoft.Test.OData.Services.ODataWCFService.CompanyAddress");
    homeAddress.add(client.getObjectFactory().newPrimitiveProperty("Street",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("V.le Gabriele D'Annunzio")));
    homeAddress.add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Pescara")));
    homeAddress.add(client.getObjectFactory().newPrimitiveProperty("PostalCode",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("65127")));
    homeAddress.add(client.getObjectFactory().newPrimitiveProperty("CompanyName",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Tirasa")));
    customer.getProperties().add(client.getObjectFactory().newComplexProperty("HomeAddress", homeAddress));

    customer.getProperties().add(client.getObjectFactory().newCollectionProperty("Numbers",
        client.getObjectFactory().newCollectionValue("Edm.String")));
    customer.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails",
        client.getObjectFactory().newCollectionValue("Edm.String")));
    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("City",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Pescara")));
    final Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(1977, 8, 8, 0, 0, 0);
    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Birthday",
        client.getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build()));
    customer.getProperties().add(client.getObjectFactory().newPrimitiveProperty("TimeBetweenLastTwoOrders",
        client.getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000002")).build()));

    final ODataEntityCreateRequest<ClientEntity> createReq = client.getCUDRequestFactory().
        getEntityCreateRequest(
            client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("People").build(),
            customer);
    createReq.setFormat(format);

    final ODataEntityCreateResponse<ClientEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    final ODataEntityRequest<ClientEntity> fetchReq = client.getRetrieveRequestFactory().
        getEntityRequest(client.newURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(976).build());
    fetchReq.setFormat(format);

    final ClientEntity actual = fetchReq.execute().getBody();
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", actual.getTypeName().toString());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CompanyAddress",
        ((ClientValuable) actual.getProperty("HomeAddress")).getValue().getTypeName());

    final ODataDeleteRequest deleteReq = client.getCUDRequestFactory().getDeleteRequest(actual.getEditLink());
    assertEquals(204, deleteReq.execute().getStatusCode());
  }

  @Test
  public void createDeleteAsAtom() {
    createDelete(ODataFormat.ATOM);
  }

  @Test
  public void createDeleteAsJSON() {
    createDelete(ODataFormat.JSON_FULL_METADATA);
  }
}
