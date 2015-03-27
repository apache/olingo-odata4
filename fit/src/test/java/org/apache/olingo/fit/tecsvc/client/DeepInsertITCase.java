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

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
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
import org.junit.AfterClass;
import org.junit.Test;

public class DeepInsertITCase extends AbstractBaseTestITCase {

  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String SERVICE_NAMESPACE = "olingo.odata.test1";
  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_TWO_KEY_NAV = "ESTwoKeyNav";
  private static final String ET_KEY_NAV_NAME = "ETKeyNav";
  private static final String ET_TWO_KEY_NAV_NAME = "ETTwoKeyNav";
  private static final FullQualifiedName ET_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, ET_KEY_NAV_NAME);
  private static final FullQualifiedName ET_TWO_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, ET_TWO_KEY_NAV_NAME);
  private static final String CT_PRIM_COMP = "CTPrimComp";
  private static final String CT_TWO_PRIM = "CTTwoPrim";
  private static final String CT_ALL_PRIM = "CTAllPrim";
  private static final String CT_NAV_FIVE_PROP = "CTNavFiveProp";
  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_STRING = "PropertyString";
  private static final String PROPERTY_COMP = "PropertyComp";
  private static final String PROPERTY_COMP_NAV = "PropertyCompNav";
  private static final String PROPERTY_COMP_COMP_NAV = "PropertyCompCompNav";
  private static final String PROPERTY_COMP_TWO_PRIM = "PropertyCompTwoPrim";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_KEY_NAV_ONE = "NavPropertyETKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_ONE = "NavPropertyETTwoKeyNavOne";
  private static final String NAV_PROPERTY_ET_TWO_KEY_NAV_MANY = "NavPropertyETTwoKeyNavMany";

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  //Nothing needed here.
  }

  @Test
  public void testSimpleDeepInsert() throws EdmPrimitiveTypeException {
    final ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ODataObjectFactory of = client.getObjectFactory();
    final ODataEntity entity = client.getObjectFactory().newEntity(ET_KEY_NAV);

    // Prepare entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
            .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_NAV_FIVE_PROP)
                .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Non collection navigation property
    // Create related entity(EntitySet: ESTwoKeyNav, Type: ETTwoKeyNav, Nav. Property: NavPropertyETTwoKeyNavOne)
    final ODataEntity inlineEntitySingle = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntitySingle.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)));
    inlineEntitySingle.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("43")));
    inlineEntitySingle.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))));
    inlineEntitySingle.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 432)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("432")))));

    // Collection navigation property
    // The navigation property has a partner navigation property named "NavPropertyETKeyNavOne"
    // Create related entity(EntitySet: ESTwoKeyNav, Type: NavPropertyETTwoKeyNavMany
    final ODataEntity inlineEntityCol1 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol1.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 44)));
    inlineEntityCol1.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("44")));
    inlineEntityCol1.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 441)))));
    inlineEntityCol1.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 442)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("442")))));

    final ODataEntity inlineEntityCol2 = client.getObjectFactory().newEntity(ET_TWO_KEY_NAV);
    inlineEntityCol2.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 45)));
    inlineEntityCol2.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("45")));
    inlineEntityCol2.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 451)))));
    inlineEntityCol2.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 452)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("452")))));

    final ODataInlineEntity newDeepInsertEntityLink =
        of.newDeepInsertEntity(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE, inlineEntitySingle);
    final ODataEntitySet newDeepInsertEntitySet = of.newEntitySet();
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol1);
    newDeepInsertEntitySet.getEntities().add(inlineEntityCol2);
    final ODataInlineEntitySet newDeepInsertEntitySetLink =
        of.newDeepInsertEntitySet(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY, newDeepInsertEntitySet);

    entity.addLink(newDeepInsertEntityLink);
    entity.addLink(newDeepInsertEntitySetLink);

    // Perform create request
    final ODataEntityCreateResponse<ODataEntity> responseCreate = client.getCUDRequestFactory()
        .getEntityCreateRequest(createURI, entity)
        .execute();
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), responseCreate.getStatusCode());

    final String cookie = responseCreate.getHeader(HttpHeader.SET_COOKIE).toString();

    // Fetch ESKeyNav entity with expand of NavPropertyETTwoKeyNavOne nav. property
    ODataProperty propertyInt16 = responseCreate.getBody().getProperty(PROPERTY_INT16);
    final URI esKeyNavURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(
            propertyInt16.getPrimitiveValue().toValue()).expand(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE,
            NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).build();

    final ODataEntityRequest<ODataEntity> esKeyNavRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esKeyNavURI);
    esKeyNavRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> esKeyNavResponse = esKeyNavRequest.execute();

    // Check nav. property NavPropertyETTwoKeyNavOne
    assertNotNull(esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE));
    assertEquals(431, esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE).getComplexValue().get(
        PROPERTY_COMP).getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check nav. property NavPropertyETTwoKeyNavMany
    assertNotNull(esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY));
    assertEquals(2, esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).getCollectionValue()
        .size());
    Iterator<ODataValue> twoKeyNavManyIterator =
        esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_MANY).getCollectionValue().iterator();
    final ODataValue firstTwoKeyNavEnity = twoKeyNavManyIterator.next(); // First entity
    assertEquals(441, firstTwoKeyNavEnity.asComplex().get(PROPERTY_COMP).getValue().asComplex().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());
    final ODataValue secondTwoKeyNavEnity = twoKeyNavManyIterator.next(); // Second entity
    assertEquals(451, secondTwoKeyNavEnity.asComplex().get(PROPERTY_COMP).getValue().asComplex().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());

    // Fetch ESTwoKeyNav entities and check if available and the partner relation have been set up
    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavOne)
    Map<String, Object> composedKey = new HashMap<String, Object>();
    composedKey.put(PROPERTY_INT16, esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE)
        .getComplexValue().get(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, esKeyNavResponse.getBody().getProperty(NAV_PROPERTY_ET_TWO_KEY_NAV_ONE)
        .getComplexValue().get(PROPERTY_STRING)
        .getPrimitiveValue().toValue());

    final URI esTwoKeyNavEntitySingleURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_TWO_KEY_NAV)
        .appendKeySegment(composedKey)
        .build();

    final ODataEntityRequest<ODataEntity> esTwoKeyNavSingleRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(esTwoKeyNavEntitySingleURI);
    esTwoKeyNavSingleRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> esTwoKeyNavSingleResponse = esTwoKeyNavSingleRequest.execute();
    assertEquals(431, esTwoKeyNavSingleResponse.getBody().getProperty(PROPERTY_COMP).getComplexValue().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(0))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, firstTwoKeyNavEnity.asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, firstTwoKeyNavEnity.asComplex().get(PROPERTY_STRING).getPrimitiveValue()
        .toValue());

    URI esTwoKeyNavEntityManyOneURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
            .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ODataEntity> esTwoKeyNavManyOneRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyOneURI);
    esTwoKeyNavManyOneRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> esTwoKeyNavManyOneResponse = esTwoKeyNavManyOneRequest.execute();

    assertEquals(441, esTwoKeyNavManyOneResponse.getBody().getProperty(PROPERTY_COMP).getComplexValue().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(esTwoKeyNavManyOneResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue());
    assertEquals(propertyInt16.getPrimitiveValue().toValue(), esTwoKeyNavManyOneResponse.getBody().getProperty(
        NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    // Check ESTwoKeyNav(Created via NavPropertyETTwoKeyNavMany(1))
    composedKey.clear();
    composedKey.put(PROPERTY_INT16, secondTwoKeyNavEnity.asComplex().get(PROPERTY_INT16).getPrimitiveValue().toValue());
    composedKey.put(PROPERTY_STRING, secondTwoKeyNavEnity.asComplex().get(PROPERTY_STRING).getPrimitiveValue()
        .toValue());

    URI esTwoKeyNavEntityManyTwoURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_KEY_NAV).appendKeySegment(composedKey)
            .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    final ODataEntityRequest<ODataEntity> esTwoKeyNavManyTwoRequest =
        client.getRetrieveRequestFactory().getEntityRequest(esTwoKeyNavEntityManyTwoURI);
    esTwoKeyNavManyTwoRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> esTwoKeyNavManyTwoResponse = esTwoKeyNavManyTwoRequest.execute();

    assertEquals(451, esTwoKeyNavManyTwoResponse.getBody().getProperty(PROPERTY_COMP).getComplexValue().get(
        PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(esTwoKeyNavManyTwoResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue());
    assertEquals(propertyInt16.getPrimitiveValue().toValue(), esTwoKeyNavManyTwoResponse.getBody().getProperty(
        NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());
  }

  @Test
  public void testDeepInsertSameEntitySet() throws EdmPrimitiveTypeException {
    final ODataClient client = getClient();
    final URI createURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build();
    final ODataObjectFactory of = client.getObjectFactory();
    final ODataEntity entity = client.getObjectFactory().newEntity(ET_KEY_NAV);

    // Prepare entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)));
    entity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))));
    entity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("42")))
            .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_NAV_FIVE_PROP)
                .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 42)))))));

    // Prepare inline entity(EntitySet: ESKeyNav, Type: ETKeyNav)
    final ODataEntity innerEntity = of.newEntity(ET_KEY_NAV);
    innerEntity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 43)));
    innerEntity.getProperties()
        .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("43")));
    innerEntity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_NAV, of.newComplexValue(CT_NAV_FIVE_PROP)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))));
    innerEntity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_ALL_PRIM, of.newComplexValue(CT_ALL_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))));
    innerEntity.getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_TWO_PRIM, of.newComplexValue(CT_TWO_PRIM)
            .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder().buildInt16((short) 431)))
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))));
    innerEntity
        .getProperties()
        .add(of.newComplexProperty(PROPERTY_COMP_COMP_NAV, of.newComplexValue(CT_PRIM_COMP)
            .add(of.newPrimitiveProperty(PROPERTY_STRING, of.newPrimitiveValueBuilder().buildString("431")))
            .add(of.newComplexProperty(PROPERTY_COMP, of.newComplexValue(CT_NAV_FIVE_PROP)
                .add(of.newPrimitiveProperty(PROPERTY_INT16, of.newPrimitiveValueBuilder()
                    .buildInt16((short) 431)))))));

    ODataInlineEntity inlineEntity = of.newDeepInsertEntity(NAV_PROPERTY_ET_KEY_NAV_ONE, innerEntity);
    entity.addLink(inlineEntity);

    final ODataEntityCreateResponse<ODataEntity> responseCreate =
        client.getCUDRequestFactory().getEntityCreateRequest(createURI, entity).execute();
    final String cookie = responseCreate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short esKeyNavEntityKey =
        responseCreate.getBody().getProperty(PROPERTY_INT16).getPrimitiveValue().toCastValue(Short.class);

    // Fetch Entity
    URI fetchEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(esKeyNavEntityKey)
            .expand(NAV_PROPERTY_ET_KEY_NAV_ONE).build();

    ODataEntityRequest<ODataEntity> entityRequest = client.getRetrieveRequestFactory().getEntityRequest(fetchEntityURI);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ODataEntity> entityResponse = entityRequest.execute();

    // Check values
    assertEquals(431, entityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(
        PROPERTY_COMP_NAV).getComplexValue().get(PROPERTY_INT16).getPrimitiveValue().toValue());

    Short innerEntityInt16Key =
        entityResponse.getBody().getProperty(NAV_PROPERTY_ET_KEY_NAV_ONE).getComplexValue().get(PROPERTY_INT16)
            .getPrimitiveValue().toCastValue(Short.class);

    final URI innerEntityURI =
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(innerEntityInt16Key)
            .build();
    final ODataEntityRequest<ODataEntity> innerRequest =
        client.getRetrieveRequestFactory().getEntityRequest(innerEntityURI);
    innerRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    ODataRetrieveResponse<ODataEntity> innerResponse = innerRequest.execute();

    assertEquals(431, innerResponse.getBody().getProperty(PROPERTY_COMP_NAV).getComplexValue().get(PROPERTY_INT16)
        .getPrimitiveValue().toValue());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ODataFormat.JSON);
    return odata;
  }
}
