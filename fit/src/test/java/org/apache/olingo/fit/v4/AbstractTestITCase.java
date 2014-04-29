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

import java.io.IOException;
import java.net.URI;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.domain.v4.ODataEntityImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static final ODataClient client = ODataClientFactory.getV4();

  protected static EdmEnabledODataClient edmClient;

  protected static String testStaticServiceRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/StaticService/V40/Static.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/StaticService/V40/KeyAsSegment.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/StaticService/V40/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/StaticService/V40/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/DefaultService.svc";

    edmClient = ODataClientFactory.getEdmEnabledV4(testStaticServiceRootURL);  
  }

  @Override
  protected ODataClient getClient() {
    return client;
  }

  protected ODataEntity read(final ODataPubFormat format, final URI editLink) {
    final ODataEntityRequest<ODataEntity> req = getClient().getRetrieveRequestFactory().getEntityRequest(editLink);
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);

    if (ODataPubFormat.JSON_FULL_METADATA == format || ODataPubFormat.ATOM == format) {
      assertEquals(req.getURI(), entity.getEditLink());
    }

    return entity;
  }

  protected void createAndDeleteOrder(final ODataPubFormat format, final int id) {
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

    final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(created.getEditLink());
    final ODataDeleteResponse deleteRes = deleteReq.execute();
    assertEquals(204, deleteRes.getStatusCode());
  }
}
