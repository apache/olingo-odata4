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

import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.domain.v4.ODataEntityImpl;
import org.junit.Test;

public class EntityCreateTestITCase extends AbstractTestITCase {

  private void createOrder(final ODataPubFormat format, final int id) {
    final ODataEntity order = new ODataEntityImpl("Microsoft.Test.OData.Services.ODataWCFService.Order");

    final ODataProperty orderId = getClient().getObjectFactory().newPrimitiveProperty("OrderID",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Int32).setValue(id).build());
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
    createOrder(ODataPubFormat.ATOM, 1000);
  }

  @Test
  public void json() {
    createOrder(ODataPubFormat.JSON, 1001);
  }
}
