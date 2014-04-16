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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Calendar;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.domain.v4.ODataEntityImpl;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityCreateTestITCase extends AbstractTestITCase {

  private static final String serviceRoot = "http://odatae2etest.azurewebsites.net/javatest/DefaultService";

  // TODO: remove once fit provides contained entity CRUD
  @BeforeClass
  public static void checkServerIsOnline() throws IOException {
    final Socket socket = new Socket();
    boolean reachable = false;
    try {
      socket.connect(new InetSocketAddress("odatae2etest.azurewebsites.net", 80), 2000);
      reachable = true;
    } catch (Exception e) {
      LOG.warn("External test service not reachable, ignoring this whole class: {}",
              OperationImportInvokeTestITCase.class.getName());
    } finally {
      IOUtils.closeQuietly(socket);
    }
    Assume.assumeTrue(reachable);
  }

  private void order(final ODataPubFormat format, final int id) {
    final ODataEntity order = new ODataEntityImpl(
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    final ODataProperty orderId = getClient().getObjectFactory().newPrimitiveProperty("OrderID",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id));
    order.getProperties().add(orderId);

    final ODataProperty orderDate = getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setText("2011-03-04T16:03:57Z").build());
    order.getProperties().add(orderDate);

    final ODataProperty shelfLife = getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setText("PT0.0000001S").build());
    order.getProperties().add(shelfLife);

    final ODataCollectionValue<ODataValue> orderShelfLifesValue = getClient().getObjectFactory().
            newCollectionValue("Collection(Duration)");
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setText("PT0.0000001S").build());
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setText("PT0.0000002S").build());
    final ODataProperty orderShelfLifes = getClient().getObjectFactory().
            newCollectionProperty("OrderShelfLifes", orderShelfLifesValue);
    order.getProperties().add(orderShelfLifes);

    final ODataEntityCreateRequest<ODataEntity> req = getClient().getCUDRequestFactory().getEntityCreateRequest(
            getClient().getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Orders").build(), order);
    req.setFormat(format);
    final ODataEntity created = req.execute().getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperty("OrderShelfLifes").getCollectionValue().size());
  }

  @Test
  public void atom() {
    order(ODataPubFormat.ATOM, 1000);
  }

  @Test
  public void json() {
    order(ODataPubFormat.JSON, 1001);
  }

  private void onContained(final ODataPubFormat format) {
    final URI uri = getClient().getURIBuilder(serviceRoot).appendEntitySetSegment("Accounts").appendKeySegment(101).
            appendNavigationSegment("MyPaymentInstruments").build();

    // 1. read contained collection before any operation
    ODataEntitySet instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeBefore = instruments.getCount();

    // 2. instantiate an ODataEntity of the same type as the collection above
    final ODataEntity instrument = getClient().getObjectFactory().
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
    final ODataEntityCreateRequest<ODataEntity> req = getClient().getCUDRequestFactory().
            getEntityCreateRequest(uri, instrument);
    final ODataEntityCreateResponse<ODataEntity> res = req.execute();
    assertEquals(201, res.getStatusCode());

    // 4. verify that the contained collection effectively grew
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeAfter = instruments.getCount();
    assertEquals(sizeBefore + 1, sizeAfter);

    // 5. remove the contained entity created above
    final ODataDeleteResponse deleteRes = getClient().getCUDRequestFactory().
            getDeleteRequest(getClient().getURIBuilder(uri.toASCIIString()).appendKeySegment(id).build()).execute();
    assertEquals(204, deleteRes.getStatusCode());

    // 6. verify that the contained collection effectively reduced
    instruments = getClient().getRetrieveRequestFactory().getEntitySetRequest(uri).execute().getBody();
    assertNotNull(instruments);
    final int sizeEnd = instruments.getCount();
    assertEquals(sizeBefore, sizeEnd);
  }

  @Test
  public void atomOnContained() {
    onContained(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonOnContained() {
    onContained(ODataPubFormat.JSON);
  }
}
