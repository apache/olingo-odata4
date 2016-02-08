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
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Test;

public class BindingITCase extends AbstractParamTecSvcITCase {

  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ET_KEY_NAV_NAME = "ETKeyNav";
  private static final FullQualifiedName ET_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, ET_KEY_NAV_NAME);
  private static final String CT_COMP_NAV = SERVICE_NAMESPACE+"."+"CTCompNav";
  private static final String CT_TWO_PRIM = SERVICE_NAMESPACE+"."+"CTTwoPrim";
  private static final String CT_ALL_PRIM = SERVICE_NAMESPACE+"."+"CTAllPrim";
  private static final String CT_NAV_FIVE_PROP = SERVICE_NAMESPACE+"."+"CTNavFiveProp";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";
  private static final String PROPERTY_COMP_NAV = "PropertyCompNav";
  private static final String PROPERTY_COMP_COMP_NAV = "PropertyCompCompNav";
  private static final String PROPERTY_COMP_TWO_PRIM = "PropertyCompTwoPrim";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";
  private static final String NAV_PROPERTY_ET_KEY_NAV_MANY = "NavPropertyETKeyNavMany";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_ONE = "NavPropertyETTwoKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";

  @Test
  public void createBindingSimple() throws EdmPrimitiveTypeException {
    ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();

    // Create entity (EntitySet: ESKeyNav, Type: ETKeyNav)
    ClientObjectFactory factory = getFactory();
    final ClientEntity entity = factory.newEntity(ET_KEY_NAV);
    entity.getProperties()
    .add(factory.newPrimitiveProperty(PROPERTY_INT16, factory.newPrimitiveValueBuilder().buildInt16((short)
            42)));
    entity.getProperties()
    .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
    .add(factory.newComplexProperty(PROPERTY_COMP_NAV, factory.newComplexValue(CT_NAV_FIVE_PROP)
            .add(factory.newPrimitiveProperty(PROPERTY_INT16,
                    factory.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
    .add(factory.newComplexProperty(PROPERTY_COMP_ALL_PRIM, factory.newComplexValue(CT_ALL_PRIM)
            .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(factory.newComplexProperty(PROPERTY_COMP_TWO_PRIM, factory.newComplexValue(CT_TWO_PRIM)
            .add(factory.newPrimitiveProperty(PROPERTY_INT16, factory.newPrimitiveValueBuilder().buildInt16
                    ((short)
                    42)))
            .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
    .add(factory.newComplexProperty(PROPERTY_COMP_COMP_NAV, factory.newComplexValue(CT_COMP_NAV)
            .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder()
                    .buildString("42")))
            .add(factory.newComplexProperty(PROPERTY_COMP_NAV, factory.newComplexValue(CT_NAV_FIVE_PROP)
                    .add(factory.newPrimitiveProperty(PROPERTY_INT16,
                            factory.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Bind existing entities via binding synatx
    entity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
            client.newURIBuilder(SERVICE_URI)
                    .appendEntitySetSegment(ES_TWO_KEY_NAV)
                    .appendKeySegment(new LinkedHashMap<String, Object>() {
                      private static final long serialVersionUID = 3109256773218160485L;

                      {
                        put(PROPERTY_INT16, 3);
                        put(PROPERTY_STRING, "1");
                      }
                    }).build()));

    final ClientLink navLinkOne =
        factory.newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, client.newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment(
                        ES_KEY_NAV).appendKeySegment(1).build());
    final ClientLink navLinkMany1 =
        factory.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment(
                        ES_KEY_NAV).appendKeySegment(2).build());
    final ClientLink navLinkMany2 =
        factory.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment(
                        ES_KEY_NAV).appendKeySegment(3).build());

    HashMap<String, Object> combinedKey = new HashMap<String, Object>();
    combinedKey.put(PROPERTY_INT16, 1);
    combinedKey.put(PROPERTY_STRING, "1");
    final ClientLink navLink2Many =
        factory.newEntitySetNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
                .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(combinedKey).build());

    entity.addLink(navLinkOne);
    entity.addLink(navLinkMany1);
    entity.addLink(navLinkMany2);
    entity.addLink(navLink2Many);

    EdmEnabledODataClient edmEnabledClient = getEdmEnabledClient();
    final ODataEntityCreateResponse<ClientEntity> createResponse =
        edmEnabledClient.getCUDRequestFactory().getEntityCreateRequest(createURI, entity).execute();
    final String cookie = createResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short entityInt16Key =
        createResponse.getBody().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class);

    // Check the just created entity
    final URI entityGetURI =
        edmEnabledClient.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(entityInt16Key)
        .expand(NAV_PROPERTY_ET_KEY_NAV_ONE, NAV_PROPERTY_ET_KEY_NAV_MANY, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).build();

    final ODataEntityRequest<ClientEntity> entityGetRequest =
        edmEnabledClient.getRetrieveRequestFactory().getEntityRequest(entityGetURI);
    entityGetRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityGetResponse = entityGetRequest.execute();

    // NAV_PROPERTY_ET_KEY_NAV_ONE
    assertShortOrInt(1, entityGetResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
        .asInlineEntity().getEntity().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class));

    // NAV_PROPERTY_ET_KEY_NAV_MANY(0)
    Iterator<ClientEntity> iterator = entityGetResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
        .asInlineEntitySet().getEntitySet().getEntities().iterator();
    assertShortOrInt(2, iterator.next().getProperty(PROPERTY_INT16).getPrimitiveValue()
        .toCastValue(Short.class));

    // NAV_PROPERTY_ET_KEY_NAV_MANY(1)
    assertShortOrInt(3, iterator.next().getProperty(PROPERTY_INT16).getPrimitiveValue()
        .toCastValue(Short.class));

    // NAV_PROPERTY_ET_TWO_KEY_NAV_MANY(0)
    assertShortOrInt(1, entityGetResponse.getBody()
        .getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).asInlineEntitySet().getEntitySet().getEntities()
        .iterator().next().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class));
    assertEquals("1", entityGetResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY)
        .asInlineEntitySet().getEntitySet().getEntities().iterator().next()
        .getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    // Check if partner navigation link has been set up
    final URI etTwoKeyNavEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(combinedKey).expand(
            NAV_PROPERTY_ET_KEY_NAV_ONE).build();
    final ODataEntityRequest<ClientEntity> etTwoKeyNavEntityRequest =
        edmEnabledClient.getRetrieveRequestFactory().getEntityRequest(etTwoKeyNavEntityURI);
    etTwoKeyNavEntityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> etTwoKeyNavEntityResponse = etTwoKeyNavEntityRequest.execute();

    assertEquals(entityInt16Key, etTwoKeyNavEntityResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
        .asInlineEntity().getEntity().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class));
  }

  @Test
  public void updateBinding() throws Exception {
    // The entity MUST NOT contain related entities as inline content. It MAY contain binding information
    // for navigation properties. For single-valued navigation properties this replaces the relationship.
    // For collection-valued navigation properties this adds to the relationship.

    final URI entityURI =
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();

    // ESKeyNav(1).NavPropertyETKeyNavOne = ESKeyNav(2)
    // ESKeyNav(1).NavPropertyETKeyNavMany = { ESKeyNav(1), ESKeyNav(2) }
    // => Replace NavPropertyETKeyNavOne with ESKeyNav(3)
    // => Add to NavPropertyETKeyNavOne ESKeyNav(3)
    final ClientEntity entity = getFactory().newEntity(ET_KEY_NAV);
    final ClientLink navLinkOne =
        getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    final ClientLink navLinkMany =
        getFactory().newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    entity.addLink(navLinkOne);
    entity.addLink(navLinkMany);

    final EdmEnabledODataClient edmEnabledClient = getEdmEnabledClient();
    final ODataEntityUpdateResponse<ClientEntity> updateResponse =
        edmEnabledClient.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
    final String cookie = updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // Check if update was successful
    final URI entityGetURI =
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).expand(
            NAV_PROPERTY_ET_KEY_NAV_ONE, NAV_PROPERTY_ET_KEY_NAV_MANY).build();
    final ODataEntityRequest<ClientEntity> entityRequest =
        edmEnabledClient.getRetrieveRequestFactory().getEntityRequest(entityGetURI);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertShortOrInt(3, entityResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
          .asInlineEntity().getEntity().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class));
    assertEquals(3, entityResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY).asInlineEntitySet()
        .getEntitySet().getEntities().size());

    Iterator<ClientEntity> iterator = entityResponse.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY)
        .asInlineEntitySet().getEntitySet().getEntities().iterator();
    assertEquals(Short.valueOf((short) 1), iterator.next().getProperty(PROPERTY_INT16).getPrimitiveValue()
                                                                                      .toCastValue(Short.class));
    assertEquals(Short.valueOf((short) 2), iterator.next().getProperty(PROPERTY_INT16).getPrimitiveValue()
                                                                                      .toCastValue(Short.class));
    assertEquals(Short.valueOf((short) 3), iterator.next().getProperty(PROPERTY_INT16).getPrimitiveValue()
                                                                                      .toCastValue(Short.class));
  }

  @Test
  public void missingEntity() {
    // Update an existing entity, use a URI to a not existing entity
    // Perform the request to a single navigation property and a collection navigation property as well.
    // Expected: Not Found (404)

    final URI entityURI =
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();

    // Request to single (non collection) navigation property
    ClientEntity entity = getFactory().newEntity(ET_KEY_NAV);
    final ClientLink navLinkOne =
        getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(42).build());
    entity.addLink(navLinkOne);

    try {
      getEdmEnabledClient().getCUDRequestFactory()
              .getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }

    // Request to collection navigation propetry
    entity = getFactory().newEntity(ET_KEY_NAV);
    final ClientLink navLinkMany =
        getFactory().newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    entity.addLink(navLinkMany);

    try {
      getEdmEnabledClient().getCUDRequestFactory()
              .getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void deepInsertWithBindingSameNavigationProperty() {
    final ClientObjectFactory factory = getFactory();
    final ClientEntity entity = factory.newEntity(ET_KEY_NAV);
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_STRING, factory
            .newPrimitiveValueBuilder()
            .buildString("1")));
    entity.getProperties().add(factory.newComplexProperty(PROPERTY_COMP_TWO_PRIM, factory.newComplexValue
            (CT_TWO_PRIM)
            .add(factory.newPrimitiveProperty(PROPERTY_INT16, factory.newPrimitiveValueBuilder().buildInt16
                    ((short) 1)))
            .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder().buildString("1")))));

    final ClientEntity innerEntity = factory.newEntity(ET_KEY_NAV);
    innerEntity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder()
            .buildString("2")));
    innerEntity.getProperties().add(factory.newComplexProperty(PROPERTY_COMP_TWO_PRIM,
            factory.newComplexValue(CT_TWO_PRIM)
                    .add(factory.newPrimitiveProperty(PROPERTY_INT16,
                            factory.newPrimitiveValueBuilder().buildInt16((short) 1)))
                    .add(factory.newPrimitiveProperty(PROPERTY_STRING,
                            factory.newPrimitiveValueBuilder().buildString("2")))));
    innerEntity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
            getClient().newURIBuilder(SERVICE_URI)
                    .appendEntitySetSegment(ES_TWO_KEY_NAV)
                    .appendKeySegment(new LinkedHashMap<String, Object>() {
                      private static final long serialVersionUID = 1L;

                      {
                        put(PROPERTY_INT16, 3);
                        put(PROPERTY_STRING, "1");
                      }
                    }).build()));

    final ClientInlineEntity inlineLink = factory.newDeepInsertEntity(NAV_PROPERTY_ET_KEY_NAV_ONE, innerEntity);
    entity.addLink(inlineLink);

    entity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
            getClient().newURIBuilder(SERVICE_URI)
                    .appendEntitySetSegment(ES_TWO_KEY_NAV)
                    .appendKeySegment(new LinkedHashMap<String, Object>() {
                      private static final long serialVersionUID = 3109256773218160485L;

                      {
                        put(PROPERTY_INT16, 3);
                        put(PROPERTY_STRING, "1");
                      }
                    }).build()));

    final URI bindingURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(3)
        .build();

    entity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, bindingURI));

    final URI targetURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ODataEntityCreateResponse<ClientEntity> response =
        getEdmEnabledClient().getCUDRequestFactory().getEntityCreateRequest(targetURI, entity).execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());

    if (isJson()) {
      assertEquals(1, response.getBody().getNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE)
          .asInlineEntity()
          .getEntity()
          .getProperty(PROPERTY_COMP_TWO_PRIM)
          .getComplexValue()
          .get(PROPERTY_INT16)
          .getPrimitiveValue()
          .toValue());
    }
  }
}
