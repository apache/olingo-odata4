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
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataServerErrorException;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;

public class ExpandWithSystemQueryOptionsITCase extends AbstractParamTecSvcITCase {

  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String NAV_PROPERTY_ET_KEY_NAV_MANY = "NavPropertyETKeyNavMany";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";

  @Test
  public void filter() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
            Collections.singletonMap(QueryOption.FILTER, (Object) "PropertyString eq '2'"));

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Number propInt16 = (Number)entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.intValue() == 1 && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertShortOrInt(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.intValue() == 1 && propString.equals("2")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.intValue() == 2 && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertShortOrInt(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.intValue() == 3 && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else {
        fail();
      }
    }
  }

  @Test
  public void orderBy() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
            Collections.<QueryOption, Object> singletonMap(QueryOption.ORDERBY, "PropertyString desc"));
    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1) && propString.equals("1")) {
        assertEquals(2, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity1 = inlineEntitySet.getEntities().get(0);
        final ClientEntity inlineEntity2 = inlineEntitySet.getEntities().get(1);

        assertEquals(1, inlineEntity1.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity1.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

        assertEquals(1, inlineEntity2.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("1", inlineEntity2.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      }
    }
  }

  @Test
  public void skip() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_KEY_NAV, NAV_PROPERTY_ET_KEY_NAV_MANY,
            Collections.singletonMap(QueryOption.SKIP, (Object) "1"));
    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(3, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final ClientEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(2, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(2)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(3, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(3)) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      }
    }
  }

  @Test
  public void top() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_KEY_NAV, NAV_PROPERTY_ET_KEY_NAV_MANY,
            Collections.<QueryOption, Object> singletonMap(QueryOption.TOP, "1"));
    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(3, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final ClientEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.equals(1)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(2)) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertEquals(2, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
      } else if (propInt16.equals(3)) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      }
    }
  }

  @Test
  public void combinedSystemQueryOptions() {
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);
    options.put(QueryOption.SELECT, "PropertyInt16,PropertyString");
    options.put(QueryOption.FILTER, "PropertyInt16 eq 1");
    options.put(QueryOption.SKIP, "1");

    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options);
    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Number propInt16 = (Number)entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet inlineEntitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if (propInt16.intValue() == 1 && propString.equals("1")) {
        assertEquals(1, inlineEntitySet.getEntities().size());
        final ClientEntity inlineEntity = inlineEntitySet.getEntities().get(0);

        assertShortOrInt(1, inlineEntity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
        assertEquals("2", inlineEntity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
      } else if (propInt16.intValue() == 1 && propString.equals("2")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.intValue() == 2 && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else if (propInt16.intValue() == 3 && propString.equals("1")) {
        assertEquals(0, inlineEntitySet.getEntities().size());
      } else {
        fail();
      }
    }
  }

  @Test
  public void count() throws Exception{
    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);
    options.put(QueryOption.SELECT, "PropertyInt16");
    options.put(QueryOption.COUNT, true);

    final URI uri =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).expandWithOptions(
            NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options).addQueryOption(QueryOption.SELECT,
                "PropertyInt16,PropertyString").build();
       
    final ODataRetrieveResponse<ClientEntitySet> response =
        client.getRetrieveRequestFactory().getEntitySetRequest(uri).execute();

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet entitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(2), entitySet.getCount());
      } else if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("2")) {
        assertEquals(Integer.valueOf(1), entitySet.getCount());
      } else if ((propInt16.equals(2) ||propInt16.equals((short)2)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(1), entitySet.getCount());
      } else if ((propInt16.equals(3) ||propInt16.equals((short)3)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(0), entitySet.getCount());
      } else {
        fail();
      }
    }
  }

  @Test
  public void countOnly() throws Exception {
    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);

    final URI uri =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).expandWithOptions(
            NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, false, true, options).addQueryOption(QueryOption.SELECT,
                "PropertyInt16,PropertyString").build();
    final ODataRetrieveResponse<ClientEntitySet> response =
        client.getRetrieveRequestFactory().getEntitySetRequest(uri).execute();

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet entitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(2), entitySet.getCount());
      } else if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("2")) {
        assertEquals(Integer.valueOf(1), entitySet.getCount());
      } else if ((propInt16.equals(2) ||propInt16.equals((short)2)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(1), entitySet.getCount());
      } else if ((propInt16.equals(3) ||propInt16.equals((short)3)) && propString.equals("1")) {
        assertEquals(Integer.valueOf(0), entitySet.getCount());
      } else {
        fail();
      }
    }
  }  
  
  @Test
  public void reference() throws Exception {
    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);

    final URI uri =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).expandWithOptions(
            NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, true, false, options).addQueryOption(QueryOption.SELECT,
                "PropertyInt16,PropertyString").build();
    final ODataRetrieveResponse<ClientEntitySet> response =
        client.getRetrieveRequestFactory().getEntitySetRequest(uri).execute();

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(4, entities.size());

    for (final ClientEntity entity : entities) {
      final Object propInt16 = entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue();
      final Object propString = entity.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue();
      final ClientEntitySet entitySet =
          entity.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();

      if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("1")) {
        assertEquals(2, entitySet.getEntities().size());
        assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')", 
            entitySet.getEntities().get(0).getId().toString());
        assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')", 
            entitySet.getEntities().get(1).getId().toString());        
      } else if ((propInt16.equals(1) ||propInt16.equals((short)1)) && propString.equals("2")) {
        assertEquals(1, entitySet.getEntities().size());
        assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')", 
            entitySet.getEntities().get(0).getId().toString());
      } else if ((propInt16.equals(2) ||propInt16.equals((short)2)) && propString.equals("1")) {
        assertEquals(1, entitySet.getEntities().size());
        assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')", 
            entitySet.getEntities().get(0).getId().toString());
      } else if ((propInt16.equals(3) ||propInt16.equals((short)3)) && propString.equals("1")) {
        assertEquals(0, entitySet.getEntities().size());
      } else {
        fail();
      }
    }
  }  
  
  @Test
  public void singleEntityWithExpand() {
    /* A single entity request will be dispatched to a different processor method than entity set request */
    final ODataClient client = getEdmEnabledClient();
    Map<String, Object> keys = new HashMap<String, Object>();
    keys.put("PropertyInt16", 1);
    keys.put("PropertyString", "1");

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(keys)
        .expandWithOptions(NAV_PROPERTY_ET_KEY_NAV_MANY,
            Collections.singletonMap(QueryOption.FILTER, (Object) "PropertyInt16 lt 2"))
        .build();
    final ODataRetrieveResponse<ClientEntity> response =
            client.getRetrieveRequestFactory().getEntityRequest(uri).execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final ClientEntitySet entitySet =
        response.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet().getEntitySet();
    assertEquals(1, entitySet.getEntities().size());
    assertShortOrInt(1, entitySet.getEntities().get(0).getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
  }

  @Test
  public void URIEscaping() {
    final ODataRetrieveResponse<ClientEntitySet> response =
        buildRequest(ES_TWO_KEY_NAV, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
            Collections.<QueryOption, Object> singletonMap(QueryOption.FILTER,
                "PropertyInt16 eq 1"
                + " and PropertyComp/PropertyComp/PropertyDuration eq duration'PT1S'"
                + " and length(PropertyString) gt 4"));
    final List<ClientEntity> entities = response.getBody().getEntities();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(4, entities.size());
  }

  @Test
  public void cyclicExpand() {
    // Expand entity in the following order
    // 1 => 2 => 1
    // Entity with Key (PropertyInt16=1, PrroperyString='1') holds references to (PropertyInt16=1, PropertyString='1')
    // and (PropertyInt16=1, PropertyString='2')
    // Entity with Key (PropertyInt16=1, PropertyString='2') holds references to (PropertyInt16=1, PropertyString='1')
    // Define filters to select explicit the entities at any level => Circle

    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);
    options.put(QueryOption.EXPAND, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY
        + "($expand=" + NAV_PROPERTY_ET_TWO_KEY_NAV_MANY
        + "($expand=" + NAV_PROPERTY_ET_TWO_KEY_NAV_MANY + "))");
    options.put(QueryOption.FILTER, "PropertyString eq '2'");

    Map<String, Object> keys = new HashMap<String, Object>();
    keys.put(PROPERTY_INT16, 1);
    keys.put(PROPERTY_STRING, "1");

    final URI uri = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(keys)
        .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options)
        .build();

    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
            .getEntityRequest(uri)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    assertEquals(1, response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    final ClientEntity entitySecondLevel = response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0);

    assertShortOrInt(1, entitySecondLevel.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", entitySecondLevel.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    assertNotNull(entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    assertEquals(1, entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    final ClientEntity entityThirdLevel = entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0);

    // cycle happens here
    assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')", entityThirdLevel.getId().toASCIIString());
    assertEquals(0, entityThirdLevel.getProperties().size());
  }

  @Test
  public void systemQueryOptionOnThirdLevel() {
    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> options = new EnumMap<QueryOption, Object>(QueryOption.class);
    options.put(QueryOption.EXPAND, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY
        + "($expand=" + NAV_PROPERTY_ET_TWO_KEY_NAV_MANY
        + "($expand=" + NAV_PROPERTY_ET_TWO_KEY_NAV_MANY
        + ";$filter=PropertyString eq '1'))");
    options.put(QueryOption.FILTER, "PropertyString eq '2'");

    Map<String, Object> keys = new HashMap<String, Object>();
    keys.put(PROPERTY_INT16, 1);
    keys.put(PROPERTY_STRING, "1");

    final URI uri = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(keys)
        .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, options)
        .build();

    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
            .getEntityRequest(uri)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    assertEquals(1, response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    final ClientEntity entitySecondLevel = response.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0);

    assertShortOrInt(1, entitySecondLevel.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("2", entitySecondLevel.getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    assertNotNull(entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    assertEquals(1, entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    final ClientEntity entityThirdLevel = entitySecondLevel.getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0);

    // cycle happens here
    assertEquals("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')", entityThirdLevel.getId().toASCIIString());
    assertEquals(0, entityThirdLevel.getProperties().size());
  }

  @Test
  public void expandWithSearchQuery() {
    final ODataClient client = getEdmEnabledClient();
    Map<QueryOption, Object> expandOptions = new EnumMap<QueryOption, Object>(QueryOption.class);
    expandOptions.put(QueryOption.SEARCH, "abc");
    expandOptions.put(QueryOption.FILTER, "PropertyInt16 eq 1");
    
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, expandOptions)
        .build();

    final ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    
    try {
      request.execute();
    } catch(ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }
  }
 
  @Test
  public void expandWithLevels() {
    final ODataClient client = getEdmEnabledClient();

    // expand=*($levels=2)
    URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
            .expandWithOptions("*", Collections.<QueryOption, Object> singletonMap(QueryOption.LEVELS, 2))
            .build();

    try {
      client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    } catch (ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }

    // expand=NavPropertyETTwoKeyNavMany($levels=2)
    uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
            .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
                Collections.<QueryOption, Object> singletonMap(QueryOption.LEVELS, 2))
            .build();

    try {
      client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    } catch (ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }

    // expand=NavPropertyETTwoKeyNavMany($expand=NavPropertyETTwoKeyNavMany($levels=2))
    uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
            .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
                Collections.<QueryOption, Object> singletonMap(QueryOption.EXPAND,
                    NAV_PROPERTY_ET_TWO_KEY_NAV_MANY + "($levels=2)"))
            .build();

    try {
      client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    } catch (ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }

    // expand=NavPropertyETTwoKeyNavMany($expand=NavPropertyETTwoKeyNavMany($levels=2);$levels=3)
    uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
            .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY,
                Collections.<QueryOption, Object> singletonMap(QueryOption.LEVELS, 2))
            .build();

    try {
      client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    } catch (ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }

    // expand=NavPropertyETTwoKeyNavMany($expand=NavPropertyETTwoKeyNavMany($levels=2))
    Map<QueryOption, Object> expandOptions = new EnumMap<QueryOption, Object>(QueryOption.class);
    expandOptions.put(QueryOption.EXPAND, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY + "($levels=2)");
    expandOptions.put(QueryOption.LEVELS, 3);
    uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
            .expandWithOptions(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, expandOptions)
            .build();

    try {
      client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    } catch (ODataServerErrorException e) {
      assertEquals("HTTP/1.1 501 Not Implemented", e.getMessage());
    }
  }

  private ODataRetrieveResponse<ClientEntitySet> buildRequest(final String entitySet, final String navigationProperty,
      final Map<QueryOption, Object> expandOptions) {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(entitySet)
        .expandWithOptions(navigationProperty, expandOptions)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request =
        getEdmEnabledClient().getRetrieveRequestFactory().getEntitySetRequest(uri);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    return response;
  }
}
