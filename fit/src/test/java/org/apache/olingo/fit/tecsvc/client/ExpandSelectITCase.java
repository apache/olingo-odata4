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
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Assert;
import org.junit.Test;

public class ExpandSelectITCase extends AbstractBaseTestITCase {

  void assertShortOrInt(int value, Object n) {
    if (n instanceof Number) {
      assertEquals(value, ((Number)n).intValue());
    } else {
      Assert.fail();
    }
  }
  
  @Test
  public void readSelect() {
    final ODataClient client = getClient();
    final ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESAllPrim").appendKeySegment(Short.MAX_VALUE)
            .select("PropertyInt32,PropertyInt16")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNotNull(entity.getProperties());
    assertEquals(2, entity.getProperties().size());
    assertNull(entity.getProperty("PropertyString"));

    ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(Integer.valueOf(Short.MAX_VALUE), property.getPrimitiveValue().toValue());

    property = entity.getProperty("PropertyInt32");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals(Integer.MAX_VALUE, property.getPrimitiveValue().toValue());
  }

  private boolean isJson() {
    return getClient().getConfiguration().getDefaultPubFormat().equals(ContentType.JSON);
  }
  
  @Test
  public void readExpandSelect() {
    final ODataClient client = getClient();
    final ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(-365)
            .expand("NavPropertyETAllPrimMany($select=PropertyTimeOfDay,PropertySByte)")
            .select("PropertyString")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);

    assertNull(entity.getProperty("PropertyInt16"));

    final ClientProperty property = entity.getProperty("PropertyString");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertEquals("Test String2", property.getPrimitiveValue().toValue());

    if(isJson()) {
      assertNull(entity.getNavigationLink("NavPropertyETAllPrimOne"));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      Assert.assertFalse(entity.getNavigationLink("NavPropertyETAllPrimOne") instanceof ClientInlineEntity);
    }

    final ClientLink link = entity.getNavigationLink("NavPropertyETAllPrimMany");
    assertNotNull(link);
    assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, link.getType());
    final ClientInlineEntitySet inlineEntitySet = link.asInlineEntitySet();
    assertNotNull(inlineEntitySet);
    final List<? extends ClientEntity> entities = inlineEntitySet.getEntitySet().getEntities();
    assertNotNull(entities);
    assertEquals(2, entities.size());
    final ClientEntity inlineEntity = entities.get(0);
    assertEquals(2, inlineEntity.getProperties().size());
    assertShortOrInt(-128, inlineEntity.getProperty("PropertySByte").getPrimitiveValue().toValue());
    assertEquals(new java.sql.Timestamp(85754000),
        inlineEntity.getProperty("PropertyTimeOfDay").getPrimitiveValue().toValue());
  }

  @Test
  public void readExpandTwoLevels() {
    final ODataClient client = getClient();
    final ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESTwoPrim").appendKeySegment(32767)
            .expand("NavPropertyETAllPrimOne($expand=NavPropertyETTwoPrimOne)")
            .build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    assertEquals(2, entity.getProperties().size());

    if(isJson()) {
      assertNull(entity.getNavigationLink("NavPropertyETAllPrimMany"));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      Assert.assertFalse(entity.getNavigationLink("NavPropertyETAllPrimMany") instanceof ClientInlineEntity);
    }

    final ClientLink link = entity.getNavigationLink("NavPropertyETAllPrimOne");
    assertNotNull(link);
    assertEquals(ClientLinkType.ENTITY_NAVIGATION, link.getType());
    final ClientInlineEntity inlineEntity = link.asInlineEntity();
    assertNotNull(inlineEntity);
    assertEquals(16, inlineEntity.getEntity().getProperties().size());

    final ClientLink innerLink = inlineEntity.getEntity().getNavigationLink("NavPropertyETTwoPrimOne");
    assertNotNull(innerLink);
    assertEquals(ClientLinkType.ENTITY_NAVIGATION, innerLink.getType());
    final ClientEntity innerEntity = innerLink.asInlineEntity().getEntity();
    assertNotNull(innerEntity);
    assertEquals(2, innerEntity.getProperties().size());
    assertShortOrInt(32767, innerEntity.getProperty("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals("Test String4", innerEntity.getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void expandSingleValuedNavigationPropertyWithNullValue() {
    final ODataClient client = getClient();
    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(TecSvcConst.BASE_URI)
            .appendEntitySetSegment("ESKeyNav").appendKeySegment(3).expand("NavPropertyETKeyNavOne").build())
            .execute();
    
    if(isJson()) {
      // this will be only true in the json;metadata=minimal case not always
      assertEquals(0, response.getBody().getNavigationLinks().size());
      assertNull(response.getBody().getNavigationLink("NavPropertyETKeyNavOne"));
    } else {
      // in xml the links will be always present; but the content will not be if no $expand unlike 
      // json;metadata=minimal; json=full is same as application/xml
      assertEquals(6, response.getBody().getNavigationLinks().size());
      Assert.assertFalse(response.getBody()
          .getNavigationLink("NavPropertyETKeyNavOne") instanceof ClientInlineEntity);
    }    
  }

  @Override
  protected ODataClient getClient() {
    return ODataClientFactory.getEdmEnabledClient(TecSvcConst.BASE_URI, ContentType.JSON);
  }
  
  protected EdmEnabledODataClient getClient(String serviceURI) {
    return ODataClientFactory.getEdmEnabledClient(serviceURI, ContentType.JSON);
  } 
}
