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

import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataCollectionValue;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.domain.v4.ODataEntityImpl;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.junit.BeforeClass;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static final ODataClient client = ODataClientFactory.getV4();

  protected static EdmEnabledODataClient edmClient;

  protected static String testStaticServiceRootURL;

  protected static String testDemoServiceRootURL;

  protected static String testVocabulariesServiceRootURL;

  protected static String testNorthwindRootURL;

  protected static String testKeyAsSegmentServiceRootURL;

  protected static String testOpenTypeServiceRootURL;

  protected static String testLargeModelServiceRootURL;

  protected static String testAuthServiceRootURL;

  protected static String testOAuth2ServiceRootURL;

  @BeforeClass
  public static void setUpODataServiceRoot() throws IOException {
    testStaticServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc";
    testDemoServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Demo.svc";
    testVocabulariesServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Vocabularies.svc";
    testNorthwindRootURL = "http://localhost:9080/stub/StaticService/V40/NorthWind.svc";
    testKeyAsSegmentServiceRootURL = "http://localhost:9080/stub/StaticService/V40/KeyAsSegment.svc";
    testOpenTypeServiceRootURL = "http://localhost:9080/stub/StaticService/V40/OpenType.svc";
    testLargeModelServiceRootURL = "http://localhost:9080/stub/StaticService/V40/Static.svc/large";
    testAuthServiceRootURL = "http://localhost:9080/stub/DefaultService.svc/V40/Static.svc";
    testOAuth2ServiceRootURL = "http://localhost:9080/stub/StaticService/V40/OAuth2.svc";

    edmClient = ODataClientFactory.getEdmEnabledV4(testStaticServiceRootURL);

    edmClient.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    client.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
  }

  @Override
  protected ODataClient getClient() {
    return client;
  }

  protected ODataEntity read(final ODataFormat format, final URI editLink) {
    final ODataEntityRequest<ODataEntity> req = getClient().getRetrieveRequestFactory().getEntityRequest(editLink);
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);

    if (ODataFormat.JSON_FULL_METADATA == format || ODataFormat.ATOM == format) {
      assertEquals(req.getURI(), entity.getEditLink());
    }

    return entity;
  }

  protected void createAndDeleteOrder(final String serviceRoot, final ODataFormat format, final int id) {
    final ODataEntity order = new ODataEntityImpl(
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    final ODataProperty orderId = getClient().getObjectFactory().newPrimitiveProperty("OrderID",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id));
    order.getProperties().add(orderId);

    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(2011, 2, 4, 16, 3, 57);
    final ODataProperty orderDate = getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
            getClient().getObjectFactory().newPrimitiveValueBuilder()
            .setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build());
    order.getProperties().add(orderDate);

    final ODataProperty shelfLife = getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setValue(BigDecimal.TEN.scaleByPowerOfTen(7)).build());
    order.getProperties().add(shelfLife);

    final ODataCollectionValue<ODataValue> orderShelfLifesValue = getClient().getObjectFactory().
            newCollectionValue("Collection(Duration)");
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000001")).build());
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000002")).build());
    final ODataProperty orderShelfLifes = getClient().getObjectFactory().
            newCollectionProperty("OrderShelfLifes", orderShelfLifesValue);
    order.getProperties().add(orderShelfLifes);

    final ODataEntityCreateRequest<ODataEntity> req = getClient().getCUDRequestFactory().getEntityCreateRequest(
            getClient().newURIBuilder(serviceRoot).
            appendEntitySetSegment("Orders").build(), order);
    req.setFormat(format);
    final ODataEntity created = req.execute().getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperty("OrderShelfLifes").getCollectionValue().size());

    final URI deleteURI = getClient().newURIBuilder(serviceRoot).
            appendEntitySetSegment("Orders").appendKeySegment(id).build();
    final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(deleteURI);
    final ODataDeleteResponse deleteRes = deleteReq.execute();
    assertEquals(204, deleteRes.getStatusCode());
  }
}
