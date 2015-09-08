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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.junit.BeforeClass;

public abstract class AbstractTestITCase extends AbstractBaseTestITCase {

  protected static final ODataClient client = ODataClientFactory.getClient();

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

    edmClient = ODataClientFactory.getEdmEnabledClient(testStaticServiceRootURL, ContentType.JSON);

    edmClient.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
    client.getConfiguration().setDefaultBatchAcceptFormat(ContentType.APPLICATION_OCTET_STREAM);
  }

  @Override
  protected ODataClient getClient() {
    return client;
  }

  protected ClientEntity read(final ContentType contentType, final URI editLink) {
    final ODataEntityRequest<ClientEntity> req = getClient().getRetrieveRequestFactory().getEntityRequest(editLink);
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();

    assertNotNull(entity);

    if (ContentType.JSON_FULL_METADATA == contentType || ContentType.APPLICATION_ATOM_XML == contentType) {
      assertEquals(req.getURI(), entity.getEditLink());
    }

    return entity;
  }

  protected void createAndDeleteOrder(final String serviceRoot, final ContentType contentType, final int id) {

    final ClientEntity order = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Order"));

    final ClientProperty orderId = getClient().getObjectFactory().newPrimitiveProperty("OrderID",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id));
    order.getProperties().add(orderId);

    Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    dateTime.set(2011, 2, 4, 16, 3, 57);
    final ClientProperty orderDate = getClient().getObjectFactory().newPrimitiveProperty("OrderDate",
        getClient().getObjectFactory().newPrimitiveValueBuilder()
        .setType(EdmPrimitiveTypeKind.DateTimeOffset).setValue(dateTime).build());
    order.getProperties().add(orderDate);

    final ClientProperty shelfLife = getClient().getObjectFactory().newPrimitiveProperty("ShelfLife",
        getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(BigDecimal.TEN.scaleByPowerOfTen(7)).build());
    order.getProperties().add(shelfLife);

    final ClientCollectionValue<ClientValue> orderShelfLifesValue = getClient().getObjectFactory().
        newCollectionValue("Collection(Duration)");
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000001")).build());
    orderShelfLifesValue.add(getClient().getObjectFactory().newPrimitiveValueBuilder().
        setType(EdmPrimitiveTypeKind.Duration).setValue(new BigDecimal("0.0000002")).build());
    final ClientProperty orderShelfLifes = getClient().getObjectFactory().
        newCollectionProperty("OrderShelfLifes", orderShelfLifesValue);
    order.getProperties().add(orderShelfLifes);

    final ODataEntityCreateRequest<ClientEntity> req = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(serviceRoot).
        appendEntitySetSegment("Orders").build(), order);
    req.setFormat(contentType);
    final ClientEntity created = req.execute().getBody();
    assertNotNull(created);
    assertEquals(2, created.getProperty("OrderShelfLifes").getCollectionValue().size());

    if (contentType.equals(ContentType.JSON_NO_METADATA)) {
      assertEquals(0, created.getNavigationLinks().size());
      assertNull(created.getEditLink());
    } else if (contentType.equals(ContentType.JSON_FULL_METADATA)) {
      assertEquals(3, created.getNavigationLinks().size());
      assertThat(created.getTypeName().getNamespace(), is("Microsoft.Test.OData.Services.ODataWCFService"));
      assertThat(created.getEditLink().toASCIIString(), startsWith("http://localhost:9080/stub/StaticService"));
    } else if (contentType.equals(ContentType.JSON) || contentType.equals(ContentType.APPLICATION_JSON)) {
      assertEquals(0, created.getNavigationLinks().size());
      assertNull(created.getEditLink());
    }

    final URI deleteURI = getClient().newURIBuilder(serviceRoot).
        appendEntitySetSegment("Orders").appendKeySegment(id).build();
    final ODataDeleteRequest deleteReq = getClient().getCUDRequestFactory().getDeleteRequest(deleteURI);
    final ODataDeleteResponse deleteRes = deleteReq.execute();
    assertEquals(204, deleteRes.getStatusCode());
  }
}
