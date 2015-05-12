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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ODataInvokeRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class FunctionImportITCase extends AbstractBaseTestITCase {

  @Test
  public void entity() throws Exception {
    final ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTESTwoKeyNav").build(),
            ClientEntity.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertEquals(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityWithoutEntitySet() throws Exception {
    final ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTETKeyNav").build(),
            ClientEntity.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertEquals(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollection() {
    final ODataInvokeRequest<ClientEntitySet> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendOperationCallSegment("FICRTCollESTwoKeyNavParam").build(),
            ClientEntitySet.class,
            Collections.<String, ClientValue> singletonMap("ParameterInt16",
                getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(2)));
    assertNotNull(request);

    final ODataInvokeResponse<ClientEntitySet> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);
    final List<ClientEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    final ClientEntity entity = entities.get(1);
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionWithAppendedKey() {
    // .../odata.svc/FICRTCollESMedia()(1)
    final ODataInvokeRequest<ClientEntity> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollESMedia")
                .appendKeySegment(getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(1))
                .build(),
            ClientEntity.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(1, property.getPrimitiveValue().toValue());
  }

  @Test
  public void entityCollectionWithAppendedKeyAndProperty() {
    // .../odata.svc/FICRTCollESMedia()(2)/PropertyInt16
    final ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollESMedia")
                .appendKeySegment(getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(2))
                .appendPropertySegment("PropertyInt16")
                .build(),
            ClientProperty.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(2, property.getPrimitiveValue().toValue());
  }

  @Test
  public void countEntityCollection() throws Exception {
    final ODataRawRequest request = getClient().getRetrieveRequestFactory()
        .getRawRequest(getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollESMedia")
            .count().build());
    final ODataRawResponse response = request.execute();
    assertEquals("4", IOUtils.toString(response.getRawResponse()));
  }

  @Test
  public void complexWithPath() throws Exception {
    final ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCTTwoPrim")
                .appendPropertySegment("PropertyInt16").build(),
            ClientProperty.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertEquals(16, property.getPrimitiveValue().toValue());
  }

  @Test
  public void primitiveCollection() throws Exception {
    final ODataInvokeRequest<ClientProperty> request = getClient().getInvokeRequestFactory()
        .getFunctionInvokeRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCollString").build(),
            ClientProperty.class);
    assertNotNull(request);

    final ODataInvokeResponse<ClientProperty> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    assertEquals("Employee1@company.example", iterator.next().asPrimitive().toValue());
    assertEquals("Employee2@company.example", iterator.next().asPrimitive().toValue());
    assertEquals("Employee3@company.example", iterator.next().asPrimitive().toValue());
  }

  @Test
  public void primitiveValue() throws Exception {
    final ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getPropertyValueRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTString")
                .appendValueSegment().build());
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    assertEquals("UFCRTString string value", response.getBody().toValue());
  }

  @Test
  public void primitiveValueWithPath() throws Exception {
    final ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getPropertyValueRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI).appendOperationCallSegment("FICRTCTTwoPrim")
                .appendPropertySegment("PropertyString").appendValueSegment().build());
    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    assertEquals("UFCRTCTTwoPrim string value", response.getBody().toValue());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
