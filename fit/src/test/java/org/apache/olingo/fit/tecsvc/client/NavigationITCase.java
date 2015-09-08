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
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class NavigationITCase extends AbstractParamTecSvcITCase {

  @Test
  public void navigationToEntityWithRelativeContextUrl() throws Exception {
    // zero navigation
    final InputStream zeroLevelResponse = getClient().getRetrieveRequestFactory().getEntityRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767).build())
        .rawExecute();

    final String zeroLevelResponseBody = IOUtils.toString(zeroLevelResponse);
    assertTrue(zeroLevelResponseBody.contains("\"$metadata#ESAllPrim/$entity\""));

    // one navigation
    final InputStream oneLevelResponse = getClient().getRetrieveRequestFactory().getEntityRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
            .appendNavigationSegment("NavPropertyETTwoPrimOne").build())
        .rawExecute();

    final String oneLevelResponseBody = IOUtils.toString(oneLevelResponse);
    assertTrue(oneLevelResponseBody.contains("\"../$metadata#ESTwoPrim/$entity\""));

    // two navigation
    final InputStream twoLevelResponse = getClient().getRetrieveRequestFactory().getEntityRequest(
        getClient().newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
            .appendNavigationSegment("NavPropertyETAllPrimOne")
            .appendNavigationSegment("NavPropertyETTwoPrimMany").appendKeySegment(-365).build())
        .rawExecute();

    final String twoLevelResponseBody = IOUtils.toString(twoLevelResponse);
    assertTrue(twoLevelResponseBody.contains("\"../../$metadata#ESTwoPrim/$entity\""));
  }

  @Test
  public void oneLevelToEntity() throws Exception {
    final ODataRetrieveResponse<ClientEntity> response =
        getClient().getRetrieveRequestFactory().getEntityRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETTwoPrimOne").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String4", property.getPrimitiveValue().toValue());
  }

  @Test
  public void oneLevelToEntityWithKey() throws Exception {
    final ODataRetrieveResponse<ClientEntity> response =
        getClient().getRetrieveRequestFactory().getEntityRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETTwoPrimMany").appendKeySegment(-365).build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToEntityWithKey() throws Exception {
    final ODataRetrieveResponse<ClientEntity> response =
        getClient().getRetrieveRequestFactory().getEntityRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETAllPrimOne")
                .appendNavigationSegment("NavPropertyETTwoPrimMany").appendKeySegment(-365).build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToEntitySet() throws Exception {
    final ODataRetrieveResponse<ClientEntitySet> response =
        getClient().getRetrieveRequestFactory().getEntitySetRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
                .appendNavigationSegment("NavPropertyETAllPrimOne")
                .appendNavigationSegment("NavPropertyETTwoPrimMany").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);
    assertEquals(1, entitySet.getEntities().size());
    final ClientEntity entity = entitySet.getEntities().get(0);
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());
  }

  @Test
  public void twoLevelsToProperty() throws Exception {
    final ODataRetrieveResponse<ClientProperty> response =
        getClient().getRetrieveRequestFactory().getPropertyRequest(
            getClient().newURIBuilder(TecSvcConst.BASE_URI)
                .appendEntitySetSegment("ESKeyNav").appendKeySegment(1)
                .appendNavigationSegment("NavPropertyETKeyNavOne")
                .appendNavigationSegment("NavPropertyETKeyNavMany").appendKeySegment(3)
                .appendPropertySegment("PropertyCompNav").appendPropertySegment("PropertyInt16").build())
            .execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(1, property.getPrimitiveValue().toValue());
  }
}
