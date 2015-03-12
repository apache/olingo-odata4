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
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class ExpandWithSystemQueryOptionsITCase extends AbstractBaseTestITCase {

  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String NAV_PROPERTY_ET_KEY_NAV_MANY = "NavPropertyETKeyNavMany";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";

  @Test
  public void testFilter() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.FILTER, "PropertyString eq '2'");

    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ODataEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ODataEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1) && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.equals(1) && propString.equals("2")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.equals(2) && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.equals(3) && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else {
        fail();
      }
    }
  }

  @Test
  public void testOrderBy() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.ORDERBY, "PropertyString desc");

    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ODataEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ODataEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1) && propString.equals("1")) {
        assertEquals(2, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity1 = inlineEntitySet.getEntities().get(0);
        final ODataEntity inlineEntity2 = inlineEntitySet.getEntities().get(1);

        assertEquals(1, inlineEntity1.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity1.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

        assertEquals(1, inlineEntity2.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("1", inlineEntity2.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      }
    }
  }

  @Test
  public void testSkip() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.SKIP, "1");

    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_KEY_NAV, NAV_PROPERTY_ET_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    assertEquals(3, entities.size());

    for (final ODataEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final ODataEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(2, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(2)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(3, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(3)) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      }
    }
  }

  @Test
  public void testTop() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.TOP, "1");

    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_KEY_NAV, NAV_PROPERTY_ET_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    assertEquals(3, entities.size());

    for (final ODataEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final ODataEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(2)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(2, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(3)) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      }
    }
  }

  @Test
  public void testCombinedSystemQueryOptions() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.SELECT, "PropertyInt16,PropertyString");
    options.put(QueryOption.FILTER, "PropertyInt16 eq 1");
    options.put(QueryOption.SKIP, "1");

    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ODataEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ODataEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1) && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ODataEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.equals(1) && propString.equals("2")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.equals(2) && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.equals(3) && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else {
        fail();
      }
    }
  }
  
  @Test
  public void testURIEscaping() {
    final Map<QueryOption, Object> options = new HashMap<QueryOption, Object>();
    options.put(QueryOption.FILTER, "PropertyInt16 eq 1" 
    + " and PropertyComp/PropertyComp/PropertyDuration eq duration'PT1S' and length(PropertyString) gt 4");
    final ODataRetrieveResponse<ODataEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options);
    final List<ODataEntity> entities = response.getBody().getEntities();
    
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(4, entities.size());
  }
  
  private ODataRetrieveResponse<ODataEntitySet> buildRequest(final String entitySet, final String navigationProperty,
      final Map<QueryOption, Object> expandOptions) {
    return buildRequest(entitySet, navigationProperty, expandOptions, null);
  }

  private ODataRetrieveResponse<ODataEntitySet> buildRequest(final String entitySet, final String navigationProperty,
      final Map<QueryOption, Object> expandOptions, final String cookie) {
    final ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(entitySet)
        .expandWithOptions(navigationProperty, expandOptions)
        .build();

    final ODataEntitySetRequest<ODataEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);

    if (cookie != null) {
      request.addCustomHeader(HttpHeader.COOKIE, cookie);
    }

    return request.execute();
  }

  @Override
  protected ODataClient getClient() {
    EdmEnabledODataClient odata = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
