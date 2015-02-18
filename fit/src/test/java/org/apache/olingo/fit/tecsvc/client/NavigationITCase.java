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
package org.apache.olingo.fit.tecsvc.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public final class NavigationITCase extends AbstractBaseTestITCase {

  private final CommonODataClient<?> client = getClient();

  @Test
  public void oneLevelToEntity() throws Exception {
    final ODataRetrieveResponse<ODataEntity> response =
        client.getRetrieveRequestFactory().<ODataEntity> getEntityRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETTwoPrimOne").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String4", property.getPrimitiveValue().toValue());
  }

  @Test
  public void oneLevelToEntityWithKey() throws Exception {
    final ODataRetrieveResponse<ODataEntity> response =
        client.getRetrieveRequestFactory().<ODataEntity> getEntityRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETTwoPrimMany").appendKeySegment(-365).build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToEntityWithKey() throws Exception {
    final ODataRetrieveResponse<ODataEntity> response =
        client.getRetrieveRequestFactory().<ODataEntity> getEntityRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETAllPrimOne")
                .appendNavigationSegment("NavPropertyETTwoPrimMany").appendKeySegment(-365).build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToEntitySet() throws Exception {
    final ODataRetrieveResponse<ODataEntitySet> response =
        client.getRetrieveRequestFactory().<ODataEntitySet> getEntitySetRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETAllPrimOne")
                .appendNavigationSegment("NavPropertyETTwoPrimMany").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);
    assertEquals(1, entitySet.getEntities().size());
    final ODataEntity entity = entitySet.getEntities().get(0);
    assertNotNull(entity);
    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToProperty() throws Exception {
    final ODataRetrieveResponse<ODataProperty> response =
        client.getRetrieveRequestFactory().<ODataProperty> getPropertyRequest(
            client.newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESKeyNav").appendKeySegment(1)
                .appendNavigationSegment("NavPropertyETKeyNavOne")
                .appendNavigationSegment("NavPropertyETKeyNavMany").appendKeySegment(3)
                .appendPropertySegment("PropertyComp").appendPropertySegment("PropertyInt16").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(1, property.getPrimitiveValue().toValue());
  }

  @Override
  protected CommonODataClient<?> getClient() {
    ODataClient odata = ODataClientFactory.getV4();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
