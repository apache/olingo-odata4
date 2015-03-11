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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataObjectFactory;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.domain.ODataValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Test;

public class BindingITCase extends AbstractBaseTestITCase {
  private static final String SERVICE_URI = TecSvcConst.BASE_URI;

  private static final String SERVICE_NAMESPACE = "olingo.odata.test1";
  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ET_KEY_NAV_NAME = "ETKeyNav";
  private static final FullQualifiedName ET_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, ET_KEY_NAV_NAME);
  private static final String CT_PRIM_COMP = "CTPrimComp";
  private static final String CT_TWO_PRIM = "CTTwoPrim";
  private static final String CT_ALL_PRIM = "CTAllPrim";
  private static final String CT_NAV_FIVE_PROP = "CTNavFiveProp";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";
  private static final String PROPERTY_COMP = "PropertyComp";
  private static final String PROPERTY_COMP_COMP = "PropertyCompComp";
  private static final String PROPERTY_COMP_TWO_PRIM = "PropertyCompTwoPrim";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";
  private static final String NAV_PROPERTY_ET_KEY_NAV_MANY = "NavPropertyETKeyNavMany";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";

  @Test
  public void testCreateBindingSimple() throws EdmPrimitiveTypeException {
    final ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ODataObjectFactory of = client.getObjectFactory();

    // Create entity (EntitySet: ESKeyNav, Type: ETKeyNav)
    final ODataEntity entity = of.newEntity(ET_KEY_NAV);
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
            .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_NAV_FIVE_PROP)
                .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Bind existing entities via binding synatx
    final ODataLink navLinkOne =
        of.newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(
                ES_KEY_NAV).appendKeySegment(1).build());
    final ODataLink navLinkMany1 =
        of.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(
                ES_KEY_NAV).appendKeySegment(2).build());
    final ODataLink navLinkMany2 =
        of.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(
                ES_KEY_NAV).appendKeySegment(3).build());

    final HashMap<String, Object> combinedKey = new HashMap<String, Object>();
    combinedKey.put(PROPERTY_INT16, 1);
    combinedKey.put(PROPERTY_STRING, "1");
    final ODataLink navLink2Many =
        of.newEntitySetNavigationLink(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(combinedKey).build());

    entity.addLink(navLinkOne);
    entity.addLink(navLinkMany1);
    entity.addLink(navLinkMany2);
    entity.addLink(navLink2Many);

    final ODataEntityCreateResponse<ODataEntity> createResponse =
        client.getCUDRequestFactory().getEntityCreateRequest(createURI, entity).execute();
    final String cookie = createResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short entityInt16Key =
        createResponse.getBody().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class);

    // Check the just created entity
    final URI entityGetURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(entityInt16Key).expand(
            NAV_PROPERTY_ET_KEY_NAV_ONE, NAV_PROPERTY_ET_KEY_NAV_MANY, NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).build();

    final ODataEntityRequest<ODataEntity> entityGetRequest =
        client.getRetrieveRequestFactory().getEntityRequest(entityGetURI);
    entityGetRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> entityGetResponse = entityGetRequest.execute();

    // NAV_PROPERTY_ET_KEY_NAV_ONE
    assertEquals(1, entityGetResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());

    // NAV_PROPERTY_ET_KEY_NAV_MANY(0)
    Iterator<ODataValue> iterator =
        entityGetResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_MANY).getCollectionValue().iterator();
    assertEquals(2, iterator.next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // NAV_PROPERTY_ET_KEY_NAV_MANY(1)
    assertEquals(3, iterator.next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // NAV_PROPERTY_ET_TWO_KEY_NAV_MANY(0)
    assertEquals(1, entityGetResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).getCollectionValue()
        .iterator().next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals("1", entityGetResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).getCollectionValue()
        .iterator().next().asComplex().get(PROPERTY_STRING).getPrimitiveValue().toValue());

    // Check if partner navigation link has been set up
    final URI etTwoKeyNavEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(combinedKey).expand(
            NAV_PROPERTY_ET_KEY_NAV_ONE).build();
    final ODataEntityRequest<ODataEntity> etTwoKeyNavEntityRequest =
        client.getRetrieveRequestFactory().getEntityRequest(etTwoKeyNavEntityURI);
    etTwoKeyNavEntityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> etTwoKeyNavEntityResponse = etTwoKeyNavEntityRequest.execute();

    assertEquals(entityInt16Key, etTwoKeyNavEntityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE)
        .getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class));
  }

  @Test
  public void testUpdateBinding() {
    // The entity MUST NOT contain related entities as inline content. It MAY contain binding information
    // for navigation properties. For single-valued navigation properties this replaces the relationship.
    // For collection-valued navigation properties this adds to the relationship.

    final ODataClient client = getClient();
    final URI entityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    final ODataObjectFactory of = client.getObjectFactory();

    // ESKeyNav(1).NavPropertyETKeyNavOne = ESKeyNav(2)
    // ESKeyNav(1).NavPropertyETKeyNavMany = { ESKeyNav(1), ESKeyNav(2) }
    // => Replace NavPropertyETKeyNavOne with ESKeyNav(3)
    // => Add to NavPropertyETKeyNavOne ESKeyNav(3)
    final ODataEntity entity = of.newEntity(ET_KEY_NAV);
    final ODataLink navLinkOne =
        of.newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    final ODataLink navLinkMany =
        of.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    entity.addLink(navLinkOne);
    entity.addLink(navLinkMany);

    final ODataEntityUpdateResponse<ODataEntity> updateResponse =
        client.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
    final String cookie = updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    // Check if update was successful
    final URI entityGetURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).expand(
            NAV_PROPERTY_ET_KEY_NAV_ONE, NAV_PROPERTY_ET_KEY_NAV_MANY).build();
    final ODataEntityRequest<ODataEntity> entityRequest =
        client.getRetrieveRequestFactory().getEntityRequest(entityGetURI);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> entityResponse = entityRequest.execute();

    assertEquals(3, entityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(3, entityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_MANY).getCollectionValue().size());

    Iterator<ODataValue> iterator =
        entityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_MANY).getCollectionValue().iterator();
    assertEquals(1, iterator.next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(2, iterator.next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(3, iterator.next().asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
  }

  @Test
  @SuppressWarnings("unused")
  public void testMissingEntity() {
    // Update an existing entity, use a URI to a not existing entity
    // Perform the request to a single navigation property and a collection navigation property as well.
    // Expected: Not Found (404)

    final ODataClient client = getClient();
    final URI entityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    final ODataObjectFactory of = client.getObjectFactory();

    // Request to single (non collection) navigation property
    ODataEntity entity = of.newEntity(ET_KEY_NAV);
    final ODataLink navLinkOne =
        of.newEntityNavigationLink(NAV_PROPERTY_ET_KEY_NAV_ONE, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(42).build());
    entity.addLink(navLinkOne);

    ODataEntityUpdateResponse<ODataEntity> updateResponse = null;
    try {
      updateResponse =
          client.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }

    // Request to collection navigation propetry
    entity = of.newEntity(ET_KEY_NAV);
    final ODataLink navLinkMany =
        of.newEntitySetNavigationLink(NAV_PROPERTY_ET_KEY_NAV_MANY, client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build());
    entity.addLink(navLinkMany);

    try {
      updateResponse =
          client.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void testUpdateSingleNavigationPropertyWithNull() {
    final ODataClient client = getClient();
    final URI entityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    final ODataObjectFactory of = client.getObjectFactory();

    // Request to single (non collection) navigation property
    ODataEntity entity = of.newEntity(ET_KEY_NAV);
    final ODataProperty navPropery = of.newComplexProperty(NAV_PROPERTY_ET_KEY_NAV_ONE, null);
    entity.getProperties().add(navPropery);

    ODataEntityUpdateResponse<ODataEntity> updateResponse =
        client.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), updateResponse.getStatusCode());

    final ODataEntityRequest<ODataEntity> getRequest =
        client.getRetrieveRequestFactory().getEntityRequest(
            client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).expand(
                NAV_PROPERTY_ET_KEY_NAV_ONE).build());
    getRequest.addCustomHeader(HttpHeader.COOKIE, updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    final ODataRetrieveResponse<ODataEntity> getResponse = getRequest.execute();

    ODataProperty property = getResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE);
    assertEquals(null, property.getPrimitiveValue());
  }
  
  @Test
  public void testUpdateCollectionNavigationPropertyWithNull() {
    final ODataClient client = getClient();
    final URI entityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    final ODataObjectFactory of = client.getObjectFactory();

    // Request to single (non collection) navigation property
    ODataEntity entity = of.newEntity(ET_KEY_NAV);
    final ODataProperty navPropery = of.newComplexProperty(NAV_PROPERTY_ET_KEY_NAV_MANY, null);
    entity.getProperties().add(navPropery);

    try {
      client.getCUDRequestFactory().getEntityUpdateRequest(entityURI, UpdateType.PATCH, entity).execute();
      fail();
    } catch(ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
