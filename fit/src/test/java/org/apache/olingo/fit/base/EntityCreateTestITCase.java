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
import java.util.Calendar;

import org.apache.commons.lang3.RandomUtils;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class EntityCreateTestITCase extends AbstractTestITCase {

  @Test
  public void atomOnContained() {
    onContained(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonOnContained() {
    onContained(ContentType.JSON);
  }
  
  private void onContained(final ContentType contentType) {
    final URI uri = getClient().newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Accounts").
        appendKeySegment(101).appendNavigationSegment("MyPaymentInstruments").build();

    // 1. read contained collection before any operation
    ClientEntitySet instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeBefore = instruments.getCount();

    // 2. instantiate an ODataEntity of the same type as the collection above
    final ClientEntity instrument = getClient().getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument"));

    int id = RandomUtils.nextInt(101999, 105000);
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("PaymentInstrumentID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("FriendlyName",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("New one")));
    instrument.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("CreatedDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(Calendar.getInstance()).build()));

    // 3. create it as contained entity
    final ODataEntityCreateRequest<ClientEntity> req = getClient().getCUDRequestFactory().
        getEntityCreateRequest(uri, instrument);
    req.setFormat(contentType);

    final ODataEntityCreateResponse<ClientEntity> res = req.execute();
    assertEquals(201, res.getStatusCode());

    // 4. verify that the contained collection effectively grew
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeAfter = instruments.getCount();
    assertEquals(sizeBefore + 1, sizeAfter);

    // 5. remove the contained entity created above
    final ODataDeleteResponse deleteRes = getClient().getCUDRequestFactory().
        getDeleteRequest(getClient().newURIBuilder(uri.toASCIIString()).appendKeySegment(id).build()).execute();
    assertEquals(204, deleteRes.getStatusCode());

    // 6. verify that the contained collection effectively reduced
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeEnd = instruments.getCount();
    assertEquals(sizeBefore, sizeEnd);
  }
}
