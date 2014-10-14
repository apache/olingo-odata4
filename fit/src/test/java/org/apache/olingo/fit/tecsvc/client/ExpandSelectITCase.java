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
import static org.junit.Assert.assertNull;

import java.util.List;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public final class ExpandSelectITCase extends AbstractBaseTestITCase {

  @Test
  public void readSelect() {
    final CommonODataClient<?> client = getClient();
    final ODataEntityRequest<ODataEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESAllPrim").appendKeySegment(Short.MAX_VALUE)
            .select("PropertyInt32,PropertyInt16")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ODataEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNull(entity.getProperty("PropertyString"));

    ODataProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(Integer.valueOf(Short.MAX_VALUE), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyInt32");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(Integer.MAX_VALUE, property.getPrimitiveValue().toValue());
  }

  @Test
  public void readExpandSelect() {
    final CommonODataClient<?> client = getClient();
    final ODataEntityRequest<ODataEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(-365)
            .expand("NavPropertyETAllPrimMany($select=PropertyTimeOfDay,PropertySByte)")
            .select("PropertyString")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ODataEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);

    assertNull(entity.getProperty("PropertyInt16"));

    final ODataProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());

    assertNull(entity.getNavigationLink("NavPropertyETAllPrimOne"));

    final ODataLink link = entity.getNavigationLink("NavPropertyETAllPrimMany");
    assertNotNull(link);
    assertEquals(ODataLinkType.ENTITY_SET_NAVIGATION, link.getType());
    final ODataInlineEntitySet inlineEntitySet = link.asInlineEntitySet();
    assertNotNull(inlineEntitySet);
    final List<? extends CommonODataEntity> entities = inlineEntitySet.getEntitySet().getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    final CommonODataEntity inlineEntity = entities.get(0);
    assertEquals(2, inlineEntity.getProperties().size());
    assertEquals(-128, inlineEntity.getProperty("PropertySByte").getPrimitiveValue().toValue());
    assertEquals(new java.sql.Timestamp(85754000),
        inlineEntity.getProperty("PropertyTimeOfDay").getPrimitiveValue().toValue());
  }

  @Test
  public void readExpandTwoLevels() {
    final CommonODataClient<?> client = getClient();
    final ODataEntityRequest<ODataEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
            .expand("NavPropertyETAllPrimOne($expand=NavPropertyETTwoPrimOne)")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ODataEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ODataEntity entity = response.getBody();
    assertNotNull(entity);
    assertEquals(2, entity.getProperties().size());

    assertNull(entity.getNavigationLink("NavPropertyETAllPrimMany"));

    final ODataLink link = entity.getNavigationLink("NavPropertyETAllPrimOne");
    assertNotNull(link);
    assertEquals(ODataLinkType.ENTITY_NAVIGATION, link.getType());
    final ODataInlineEntity inlineEntity = link.asInlineEntity();
    assertNotNull(inlineEntity);
    assertEquals(16, inlineEntity.getEntity().getProperties().size());

    final ODataLink innerLink = inlineEntity.getEntity().getNavigationLink("NavPropertyETTwoPrimOne");
    assertNotNull(innerLink);
    assertEquals(ODataLinkType.ENTITY_NAVIGATION, innerLink.getType());
    final CommonODataEntity innerEntity = innerLink.asInlineEntity().getEntity();
    assertNotNull(innerEntity);
    assertEquals(2, innerEntity.getProperties().size());
    assertEquals(32767, innerEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test String4", innerEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Override
  protected CommonODataClient<?> getClient() {
    return ODataClientFactory.getEdmEnabledV4(TecSvcConst.BASE_URI);
  }
}
