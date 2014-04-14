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

import java.net.URI;
import java.util.Calendar;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class EntityUpdateTestITCase extends AbstractTestITCase {

  private void upsert(final UpdateType updateType, final ODataPubFormat format) {
    final ODataEntity order = getClient().getObjectFactory().
            newEntity(new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("OrderID",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(9)));
    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(Calendar.getInstance()).build()));
    order.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setText("PT0.0000002S").build()));

    final URI upsertURI = getClient().getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Orders").appendKeySegment(9).build();
    final ODataEntityUpdateRequest req = getClient().getCUDRequestFactory().
            getEntityUpsertRequest(updateType, upsertURI, order);
    req.setFormat(format);

    final ODataEntityUpdateResponse res = req.execute();
    try {
      final ODataEntity read = read(format, upsertURI);
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
  public void atomUpsert() {
    upsert(UpdateType.PATCH, ODataPubFormat.ATOM);
    upsert(UpdateType.REPLACE, ODataPubFormat.ATOM);
  }

  @Test
  public void jsonUpsert() {
    upsert(UpdateType.PATCH, ODataPubFormat.JSON);
    upsert(UpdateType.REPLACE, ODataPubFormat.JSON);
  }

}
