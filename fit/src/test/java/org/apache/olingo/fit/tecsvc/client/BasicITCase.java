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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Ignore;
import org.junit.Test;

public class BasicITCase extends AbstractTecSvcITCase {
  
  private static final String CONTENT_TYPE_JSON_IEEE754_COMPATIBLE =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true").toContentTypeString();
  private static final String SERVICE_NAMESPACE = "olingo.odata.test1";
  private static final String ET_ALL_PRIM_NAME = "ETAllPrim";
  private static final FullQualifiedName ET_ALL_PRIM = new FullQualifiedName(SERVICE_NAMESPACE, ET_ALL_PRIM_NAME);

  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_INT64 = "PropertyInt64";
  private static final String PROPERTY_DECIMAL = "PropertyDecimal";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";

  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_TWO_PRIM = "ESTwoPrim";
  private static final String ES_KEY_NAV = "ESKeyNav";

  @Test
  public void readServiceDocument() {
    ODataServiceDocumentRequest request = client.getRetrieveRequestFactory()
        .getServiceDocumentRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<ClientServiceDocument> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    ClientServiceDocument serviceDocument = response.getBody();
    assertNotNull(serviceDocument);
    assertThat(serviceDocument.getEntitySetNames(), hasItem("ESAllPrim"));
    assertThat(serviceDocument.getFunctionImportNames(), hasItem("FICRTCollCTTwoPrim"));
    assertThat(serviceDocument.getSingletonNames(), hasItem("SIMedia"));
  }

  @Test
  public void readMetadata() {
    EdmMetadataRequest request = client.getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();

    assertNotNull(edm);
    assertEquals(2, edm.getSchemas().size());
    assertEquals("olingo.odata.test1", edm.getSchema("olingo.odata.test1").getNamespace());
    assertEquals("Namespace1_Alias", edm.getSchema("olingo.odata.test1").getAlias());
    assertEquals("Org.OData.Core.V1", edm.getSchema("Org.OData.Core.V1").getNamespace());
    assertEquals("Core", edm.getSchema("Org.OData.Core.V1").getAlias());
  }

  @Test
  public void readViaXmlMetadata() {
    XMLMetadataRequest request = client.getRetrieveRequestFactory().getXMLMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<XMLMetadata> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    XMLMetadata xmlMetadata = response.getBody();

    assertNotNull(xmlMetadata);
    assertEquals(2, xmlMetadata.getSchemas().size());
    assertEquals("olingo.odata.test1", xmlMetadata.getSchema("olingo.odata.test1").getNamespace());
    final List<Reference> references = xmlMetadata.getReferences();
    assertEquals(1, references.size());
    assertThat(references.get(0).getUri().toASCIIString(), containsString("vocabularies/Org.OData.Core.V1"));
  }

  @Test
  public void readEntitySet() {
    ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory()
        .getEntitySetRequest(client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp").build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());

    final ClientEntitySet entitySet = response.getBody();
    assertNotNull(entitySet);

    assertNull(entitySet.getCount());
    assertNull(entitySet.getNext());
    assertEquals(Collections.<ClientAnnotation> emptyList(), entitySet.getAnnotations());
    assertNull(entitySet.getDeltaLink());

    final List<ClientEntity> entities = entitySet.getEntities();
    assertNotNull(entities);
    assertEquals(3, entities.size());
    final ClientEntity entity = entities.get(2);
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("PropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    if (isJson()) {
      assertEquals(0, property.getPrimitiveValue().toValue());
    } else {
      assertEquals((short)0, property.getPrimitiveValue().toValue());
    }
  }

  @Test
  public void readEntityCollectionCount() {
    ODataValueRequest request = client.getRetrieveRequestFactory()
        .getValueRequest(client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESServerSidePaging").appendCountSegment().build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), response.getContentType());

    final ClientPrimitiveValue value = response.getBody();
    assertNotNull(value);
    assertEquals("503", value.toValue());
  }

  @Test
  public void readException() throws Exception {
    ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment("42").build());
    assertNotNull(request);
    setCookieHeader(request);

    try {
      request.execute();
      fail("Expected Exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
      final ODataError error = e.getODataError();
      assertThat(error.getMessage(), containsString("key property"));
    }
  }

  @Test
  public void readEntity() throws Exception {
    ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCollAllPrim").appendKeySegment(1).build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("CollPropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    if(isJson()) {
      assertEquals(1000, iterator.next().asPrimitive().toValue());
      assertEquals(2000, iterator.next().asPrimitive().toValue());
      assertEquals(30112, iterator.next().asPrimitive().toValue());      
    } else {
      assertEquals((short)1000, iterator.next().asPrimitive().toValue());
      assertEquals((short)2000, iterator.next().asPrimitive().toValue());
      assertEquals((short)30112, iterator.next().asPrimitive().toValue());
    }
  }

  @Test
  public void deleteEntity() throws Exception {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
        .build();
    final ODataDeleteRequest request = client.getCUDRequestFactory().getDeleteRequest(uri);
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the deleted entity is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory().getEntityRequest(uri);
    entityRequest.addCustomHeader(HttpHeader.COOKIE, response.getHeader(HttpHeader.SET_COOKIE).iterator().next());
    try {
      entityRequest.execute();
      fail("Expected exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.NOT_FOUND.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void patchEntity() throws Exception {
    ClientEntity patchEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("new")));
    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyDecimal",
        factory.newPrimitiveValueBuilder().buildDecimal(new BigDecimal(42.875))));
    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt64(null)));
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
        .build();
    final ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.PATCH, patchEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the patched properties have changed and the other properties not.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property1 = entity.getProperty("PropertyString");
    assertNotNull(property1);
    assertEquals("new", property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = entity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    if (isJson()) {
      assertEquals(42.875, property2.getPrimitiveValue().toValue());
    } else {
      assertEquals(new BigDecimal(42.875), property2.getPrimitiveValue().toValue());
    }
    final ClientProperty property3 = entity.getProperty("PropertyInt64");
    assertNotNull(property3);
    assertNull(property3.getPrimitiveValue());
    final ClientProperty property4 = entity.getProperty("PropertyDuration");
    assertNotNull(property4);
    if (isJson()) {
      assertEquals("PT6S", property4.getPrimitiveValue().toValue());
    } else {
      assertEquals(new BigDecimal(6), property4.getPrimitiveValue().toValue());
    }
    
  }

  @Test
  public void updateEntity() throws Exception {
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt64((long)42)));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").appendKeySegment(32767)
        .build();
    final ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.REPLACE, newEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the updated properties have changed and that other properties have their default values.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property1 = entity.getProperty("PropertyInt64");
    assertNotNull(property1);
    if (isJson()) {
      assertEquals(42, property1.getPrimitiveValue().toValue());
    } else {
      assertEquals((long)42, property1.getPrimitiveValue().toValue());
    }
    final ClientProperty property2 = entity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue());
  }

  @Test
  public void patchEntityWithComplex() throws Exception {
    ClientEntity patchEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETCompComp"));
    patchEntity.getProperties().add(factory.newComplexProperty("PropertyComp",
        factory.newComplexValue("olingo.odata.test1.CTCompComp").add(
            factory.newComplexProperty("PropertyComp",
                factory.newComplexValue("olingo.odata.test1.CTTwoPrim").add(
                    factory.newPrimitiveProperty("PropertyInt16",
                        factory.newPrimitiveValueBuilder().buildInt16((short)42)))))));
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESCompComp").appendKeySegment(1).build();
    final ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.PATCH, patchEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the patched properties have changed and the other properties not.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientComplexValue complex = entity.getProperty("PropertyComp").getComplexValue()
        .get("PropertyComp").getComplexValue();
    assertNotNull(complex);
    final ClientProperty property1 = complex.get("PropertyInt16");
    assertNotNull(property1);
    if (isJson()) {
      assertEquals(42, property1.getPrimitiveValue().toValue());
    } else {
      assertEquals((short)42, property1.getPrimitiveValue().toValue());
    }
    final ClientProperty property2 = complex.get("PropertyString");
    assertNotNull(property2);
    assertEquals("String 1", property2.getPrimitiveValue().toValue());
  }

  @Test
  public void updateEntityWithComplex() throws Exception {
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    newEntity.getProperties().add(factory.newComplexProperty("PropertyCompCompNav", null));
    // The following properties must not be null
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("Test")));
    newEntity.getProperties().add(
        factory.newComplexProperty("PropertyCompTwoPrim",
            factory.newComplexValue("CTTwoPrim")
            .add(factory.newPrimitiveProperty(
                "PropertyInt16",
                factory.newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(factory.newPrimitiveProperty(
                    "PropertyString",
                    factory.newPrimitiveValueBuilder().buildString("Test2")))));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").appendKeySegment(1).build();
    final ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.REPLACE, newEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the complex-property hierarchy is still there and that all primitive values are now null.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientComplexValue complex = entity.getProperty("PropertyCompCompNav").getComplexValue()
        .get("PropertyCompNav").getComplexValue();
    assertNotNull(complex);
    final ClientProperty property = complex.get("PropertyInt16");
    assertNotNull(property);
    assertNull(property.getPrimitiveValue());
  }

  @Test
  public void createEntity() throws Exception {
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt64((long)42)));
    newEntity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE,
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoPrim")
        .appendKeySegment(32766)
        .build()));

    final ODataEntityCreateRequest<ClientEntity> createRequest = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim").build(),
        newEntity);
    assertNotNull(createRequest);
    final ODataEntityCreateResponse<ClientEntity> createResponse = createRequest.execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), createResponse.getStatusCode());
    assertEquals(SERVICE_URI + "ESAllPrim(1)", createResponse.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity createdEntity = createResponse.getBody();
    assertNotNull(createdEntity);
    final ClientProperty property1 = createdEntity.getProperty("PropertyInt64");
    assertNotNull(property1);
    if(isJson()) {
      assertEquals(42, property1.getPrimitiveValue().toValue());
    } else {
      assertEquals((long)42, property1.getPrimitiveValue().toValue());
    }
    final ClientProperty property2 = createdEntity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue());
  }

  @Test
  public void createEntityMinimalResponse() throws Exception {
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETTwoPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("new")));
    ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").build(),
        newEntity);
    request.setPrefer(client.newPreferences().returnMinimal());

    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    final String location = SERVICE_URI + "ESTwoPrim(1)";
    assertEquals(location, response.getHeader(HttpHeader.LOCATION).iterator().next());
    assertEquals(location, response.getHeader(HttpHeader.ODATA_ENTITY_ID).iterator().next());
  }

  @Test
  public void readEntityWithExpandedNavigationProperty() {
    final URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
        .build();

    ODataEntityRequest<ClientEntity> request = edmEnabledClient.getRetrieveRequestFactory().getEntityRequest(uri);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check if all inlined entities are available
    // NavPropertyETKeyNavOne
    assertNotNull(response.getBody().getNavigationLink("NavPropertyETKeyNavOne"));
    final ClientInlineEntity inlineEntity = response.getBody()
        .getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity();
    assertNotNull(inlineEntity);
    assertShortOrInt(2, inlineEntity.getEntity().getProperty("PropertyInt16").getPrimitiveValue().toValue());

    // NavPropertyETKeyNavMany
    assertNotNull(response.getBody().getNavigationLink("NavPropertyETKeyNavMany"));
    final ClientInlineEntitySet inlineEntitySet = response.getBody()
        .getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(2, inlineEntitySet.getEntitySet().getEntities().size());
    assertShortOrInt(1, inlineEntitySet.getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, inlineEntitySet.getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  public void updateCollectionOfComplexCollection() {
    final ClientEntity entity = factory.newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETKeyNav"));

    entity.getProperties().add(
        factory.newCollectionProperty("CollPropertyComp",
            factory.newCollectionValue("CTPrimComp")
                .add(factory.newComplexValue("CTPrimComp")
                    .add(factory.newPrimitiveProperty("PropertyInt16",
                        factory.newPrimitiveValueBuilder().buildInt16((short) 42)))
                    .add(factory.newComplexProperty("PropertyComp",
                        factory.newComplexValue("CTAllPrim")
                            .add(factory.newPrimitiveProperty("PropertyString",
                                factory.newPrimitiveValueBuilder().buildString("42"))))))
                .add(factory.newComplexValue("CTPrimComp")
                    .add(factory.newPrimitiveProperty("PropertyInt16",
                        factory.newPrimitiveValueBuilder().buildInt16((short) 43)))
                    .add(factory.newComplexProperty("PropertyComp",
                        factory.newComplexValue("CTAllPrim")
                            .add(factory.newPrimitiveProperty("PropertyString",
                                factory.newPrimitiveValueBuilder().buildString("43"))))))));

    final URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(3)
        .build();

    final ODataEntityUpdateResponse<ClientEntity> response = client.getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity)
            .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertNotNull(response.getBody().getProperty("CollPropertyComp"));
    assertEquals(2, response.getBody().getProperty("CollPropertyComp").getCollectionValue().size());

    final Iterator<ClientValue> collectionIterator = response.getBody()
        .getProperty("CollPropertyComp")
        .getCollectionValue()
        .iterator();

    ClientComplexValue complexProperty = collectionIterator.next().asComplex();
    if (isJson()) {
      assertEquals(42, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
    } else {
      assertEquals((short)42, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
    }
    assertNotNull(complexProperty.get("PropertyComp"));

    ClientComplexValue innerComplexProperty = complexProperty.get("PropertyComp").getComplexValue();
    assertEquals("42", innerComplexProperty.get("PropertyString").getPrimitiveValue().toValue());

    complexProperty = collectionIterator.next().asComplex();
    if (isJson()) {
      assertEquals(43, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
    } else {
      assertEquals((short)43, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
    }
    assertNotNull(complexProperty.get("PropertyComp"));

    innerComplexProperty = complexProperty.get("PropertyComp").getComplexValue();
    assertEquals("43", innerComplexProperty.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void createCollectionOfComplexCollection() {
    /*
     * Create a new entity which contains a collection of complex collections
     * Check if all not filled fields are created by the server
     */
    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.getProperties().add(
        factory.newPrimitiveProperty("PropertyString",
            factory.newPrimitiveValueBuilder().buildString("Complex collection test")));
    entity.getProperties().add(factory.newComplexProperty("PropertyCompTwoPrim",
        factory.newComplexValue("CTTwoPrim")
            .add(factory.newPrimitiveProperty("PropertyInt16",
                factory.newPrimitiveValueBuilder().buildInt16((short) 1)))
            .add(factory.newPrimitiveProperty("PropertyString",
                factory.newPrimitiveValueBuilder().buildString("1")))));

    entity.getProperties().add(factory.newCollectionProperty("CollPropertyComp",
        factory.newCollectionValue("CTPrimComp")
            .add(factory.newComplexValue("CTPrimComp")
                .add(factory.newPrimitiveProperty("PropertyInt16",
                    factory.newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(factory.newComplexProperty("PropertyComp", factory.newComplexValue("CTAllPrim")
                    .add(factory.newPrimitiveProperty("PropertyString",
                        factory.newPrimitiveValueBuilder().buildString("1"))))))
            .add(factory.newComplexValue("CTPrimComp")
                .add(factory.newComplexProperty("PropertyComp", factory.newComplexValue("CTAllPrim")
                    .add(factory.newPrimitiveProperty("PropertyString",
                        factory.newPrimitiveValueBuilder().buildString("2")))
                    .add(factory.newPrimitiveProperty("PropertyInt16",
                        factory.newPrimitiveValueBuilder().buildInt16((short) 2)))
                    .add(factory.newPrimitiveProperty("PropertySingle",
                        factory.newPrimitiveValueBuilder().buildSingle(2.0f))))))));

    entity.addLink(factory.newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoKeyNav")
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put("PropertyInt16", 1);
            put("PropertyString", "1");
          }
        }).build()));

    final ODataEntityCreateResponse<ClientEntity> response = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").build(), entity)
        .execute();

    // Check if not declared fields are also available
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    final ClientEntity newEntity = response.getBody();

    assertEquals(2, newEntity.getProperty("CollPropertyComp").getCollectionValue().size());
    final Iterator<ClientValue> iter = newEntity.getProperty("CollPropertyComp").getCollectionValue().iterator();
    final ClientComplexValue complexProperty1 = iter.next().asComplex();
    if (isJson()) {
      assertEquals(1, complexProperty1.get("PropertyInt16").getPrimitiveValue().toValue());
    } else {
      assertEquals((short)1, complexProperty1.get("PropertyInt16").getPrimitiveValue().toValue());
    }
    assertNotNull(complexProperty1.get("PropertyComp"));
    final ClientComplexValue innerComplexProperty1 = complexProperty1.get("PropertyComp").getComplexValue();
    assertEquals("1", innerComplexProperty1.get("PropertyString").getPrimitiveValue().toValue());
    assertTrue(innerComplexProperty1.get("PropertyBinary").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyBoolean").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyByte").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDate").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDateTimeOffset").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDecimal").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDouble").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDuration").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyGuid").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyInt16").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyInt32").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyInt64").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertySByte").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyTimeOfDay").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyInt16").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertySingle").hasNullValue());

    final ClientComplexValue complexProperty2 = iter.next().asComplex();
    assertTrue(complexProperty2.get("PropertyInt16").hasNullValue());
    assertNotNull(complexProperty2.get("PropertyComp"));
    final ClientComplexValue innerComplexProperty2 = complexProperty2.get("PropertyComp").getComplexValue();
    assertEquals("2", innerComplexProperty2.get("PropertyString").getPrimitiveValue().toValue());
    if(isJson()) {
      assertEquals(2, innerComplexProperty2.get("PropertyInt16").getPrimitiveValue().toValue());
      assertEquals(Double.valueOf(2), innerComplexProperty2.get("PropertySingle").getPrimitiveValue().toValue());
    } else {
      assertEquals((short)2, innerComplexProperty2.get("PropertyInt16").getPrimitiveValue().toValue());
      assertEquals(Float.valueOf(2), innerComplexProperty2.get("PropertySingle").getPrimitiveValue().toValue());
    }
    
    assertTrue(innerComplexProperty2.get("PropertyBinary").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyBoolean").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyByte").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDate").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDateTimeOffset").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDecimal").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDouble").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDuration").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyGuid").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyInt32").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyInt64").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertySByte").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyTimeOfDay").hasNullValue());

    // Check if not available properties return null
    assertNull(innerComplexProperty2.get("NotAvailableProperty"));
  }

  @Test
  public void complexPropertyWithNotNullablePrimitiveValue() {
    // PropertyComp is null, but the primitive values in PropertyComp must not be null
    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETMixPrimCollComp"));
    final URI targetURI = edmEnabledClient.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESMixPrimCollComp").build();

    try {
      edmEnabledClient.getCUDRequestFactory().getEntityCreateRequest(targetURI, entity).execute();
      fail("Expecting bad request");
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void upsert() throws EdmPrimitiveTypeException {
    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETTwoPrim"));
    entity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("Test")));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(33).build();
    final ODataEntityUpdateResponse<ClientEntity> updateResponse =
        edmEnabledClient.getCUDRequestFactory().getEntityUpdateRequest(uri, UpdateType.PATCH, entity).execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), updateResponse.getStatusCode());
    assertEquals("Test", updateResponse.getBody().getProperty("PropertyString").getPrimitiveValue().toValue());

    final String cookie = updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short key = updateResponse.getBody().getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toCastValue(Short.class);

    final ODataEntityRequest<ClientEntity> entityRequest = edmEnabledClient.getRetrieveRequestFactory()
        .getEntityRequest(edmEnabledClient.newURIBuilder()
            .appendEntitySetSegment("ESTwoPrim")
            .appendKeySegment(key)
            .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseEntityRequest = entityRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseEntityRequest.getStatusCode());
    assertEquals("Test", responseEntityRequest.getBody().getProperty("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void updatePropertyWithNull() {
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESAllPrim")
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    entity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString(null)));

    ODataEntityUpdateRequest<ClientEntity> request = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity);
    request.setPrefer(client.newPreferences().returnRepresentation());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("return=representation", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    assertTrue(response.getBody().getProperty("PropertyString").hasNullValue());
    assertShortOrInt(34, response.getBody().getProperty("PropertyDecimal").getPrimitiveValue().toValue());
  }

  @Test(expected = ODataClientErrorException.class)
  public void updatePropertyWithNullNotAllowed() {
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString(null)));

    edmEnabledClient.getCUDRequestFactory().getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity).execute();
  }

  @Test
  public void updateMerge() {
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .build();

    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.addLink(factory.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(factory.newEntitySetNavigationLink("NavPropertyETKeyNavMany", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav").appendKeySegment(3).build()));
    entity.getProperties().add(factory.newCollectionProperty("CollPropertyString",
        factory.newCollectionValue("Edm.String").add(
            factory.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(factory.newComplexProperty("PropertyCompAllPrim",
        factory.newComplexValue("CTAllPrim")
        .add(factory.newPrimitiveProperty("PropertyString",
                factory.newPrimitiveValueBuilder().buildString("Changed")))));

    final ODataEntityUpdateResponse<ClientEntity> response = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = edmEnabledClient.getRetrieveRequestFactory()
        .getEntityRequest(
            edmEnabledClient.newURIBuilder()
                .appendEntitySetSegment("ESKeyNav")
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity()
        .getEntity()
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(2)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    final Iterator<ClientValue> collectionIterator = entityResponse.getBody()
        .getProperty("CollPropertyString")
        .getCollectionValue()
        .iterator();
    assertTrue(collectionIterator.hasNext());
    assertEquals("Single entry!", collectionIterator.next().asPrimitive().toValue());
    assertFalse(collectionIterator.hasNext());

    final ClientComplexValue complexValue = entityResponse.getBody()
        .getProperty("PropertyCompAllPrim")
        .getComplexValue();

    assertEquals("Changed", complexValue.get("PropertyString").getPrimitiveValue().toValue());
  }

  @Test
  public void updateReplace() {
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .build();

    final ClientEntity entity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.addLink(factory.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(factory.newEntitySetNavigationLink("NavPropertyETKeyNavMany", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav").appendKeySegment(3).build()));
    entity.getProperties().add(factory.newPrimitiveProperty("PropertyString", factory.newPrimitiveValueBuilder()
        .buildString("Must not be null")));
    entity.getProperties().add(factory.newComplexProperty("PropertyCompTwoPrim", factory.newComplexValue("CTTwoPrim")
        .add(factory.newPrimitiveProperty("PropertyString", factory.newPrimitiveValueBuilder()
            .buildString("Must not be null")))
            .add(factory.newPrimitiveProperty("PropertyInt16",
                factory.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties().add(factory.newCollectionProperty("CollPropertyString",
        factory.newCollectionValue("Edm.String")
            .add(factory.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(factory.newComplexProperty("PropertyCompAllPrim",
        factory.newComplexValue("CTAllPrim").add(
            factory.newPrimitiveProperty("PropertyString",
                factory.newPrimitiveValueBuilder().buildString("Changed")))));

    ODataEntityUpdateRequest<ClientEntity> request = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.REPLACE, entity);
    request.setPrefer(client.newPreferences().returnMinimal());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = edmEnabledClient.getRetrieveRequestFactory()
        .getEntityRequest(
            edmEnabledClient.newURIBuilder()
                .appendEntitySetSegment("ESKeyNav")
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity()
        .getEntity()
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .size());

    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(2)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    final Iterator<ClientValue> collectionIterator = entityResponse.getBody()
        .getProperty("CollPropertyString")
        .getCollectionValue()
        .iterator();
    assertTrue(collectionIterator.hasNext());
    assertEquals("Single entry!", collectionIterator.next().asPrimitive().toValue());
    assertFalse(collectionIterator.hasNext());

    final ClientComplexValue propCompAllPrim = entityResponse.getBody()
        .getProperty("PropertyCompAllPrim")
        .getComplexValue();

    assertEquals("Changed", propCompAllPrim.get("PropertyString").getPrimitiveValue().toValue());
    assertTrue(propCompAllPrim.get("PropertyInt16").hasNullValue());
    assertTrue(propCompAllPrim.get("PropertyDate").hasNullValue());

    final ClientComplexValue propCompTwoPrim = entityResponse.getBody()
        .getProperty("PropertyCompTwoPrim")
        .getComplexValue();

    assertEquals("Must not be null", propCompTwoPrim.get("PropertyString").getPrimitiveValue().toValue());
    assertShortOrInt(42, propCompTwoPrim.get("PropertyInt16").getPrimitiveValue().toValue());

    assertNotNull(entityResponse.getBody().getProperty("PropertyCompNav").getComplexValue());
    assertTrue(entityResponse.getBody()
        .getProperty("PropertyCompNav")
        .getComplexValue()
        .get("PropertyInt16")
        .hasNullValue());
  }
  
  @Test
  public void createEntityWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
                                                         .appendKeySegment(32767).build();
    
    final ClientEntity newEntity = factory.newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_INT64, 
        factory.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    newEntity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_DECIMAL, 
        factory.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(34))));
    newEntity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));
    
    final ODataEntityCreateRequest<ClientEntity> request = edmEnabledClient.getCUDRequestFactory()
        .getEntityCreateRequest(uri, newEntity);
    request.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    
    assertEquals(Long.MAX_VALUE, response.getBody().getProperty(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(34), response.getBody().getProperty(PROPERTY_DECIMAL)
                                                           .getPrimitiveValue().toValue());
  }
  
  @Test
  public void createEntityWithIEEE754CompatibleParameterNull() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
        .appendKeySegment(32767).build();
    
    final ClientEntity newEntity = factory.newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_INT64, 
        factory.newPrimitiveValueBuilder().buildInt64(null)));
    newEntity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_DECIMAL, 
        factory.newPrimitiveValueBuilder().buildDecimal(null)));
    newEntity.addLink(factory.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));
    
    final ODataEntityCreateRequest<ClientEntity> request = edmEnabledClient.getCUDRequestFactory()
        .getEntityCreateRequest(uri, newEntity);
    request.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    
    assertTrue(response.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(response.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }
  
  @Test
  public void updateEntityWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = factory.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_INT64, 
        factory.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_DECIMAL, 
        factory.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(Long.MAX_VALUE))));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();
    
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataEntityRequest<ClientEntity> requestGet = edmEnabledClient.getRetrieveRequestFactory()
        .getEntityRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertEquals(Long.MAX_VALUE, responseGet.getBody().getProperty(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(Long.MAX_VALUE), responseGet.getBody().getProperty(PROPERTY_DECIMAL)
                                                                          .getPrimitiveValue()
                                                                          .toValue());
  }
  
  @Test
  public void updateEntityWithIEEE754CompatibleParameterNull() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = factory.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_INT64, 
        factory.newPrimitiveValueBuilder().buildInt64(null)));
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_DECIMAL, 
        factory.newPrimitiveValueBuilder().buildDecimal(null)));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();
    
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataEntityRequest<ClientEntity> requestGet = edmEnabledClient.getRetrieveRequestFactory()
        .getEntityRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertTrue(responseGet.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(responseGet.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }
  
  @Test
  public void updateEntityWithIEEE754CompatibleParameterWithNullString() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = factory.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_INT64, 
        factory.newPrimitiveValueBuilder().buildString("null")));
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_DECIMAL, 
        factory.newPrimitiveValueBuilder().buildString("null")));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = edmEnabledClient.getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    
    try {
      requestUpdate.execute();
      fail();
    } catch(ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }
  
  @Test
  public void updateEdmInt64PropertyWithIEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .appendKeySegment(0)
                                                     .appendPropertySegment(PROPERTY_INT64).build();
    
    final ODataPropertyUpdateRequest requestUpdate =
        edmEnabledClient.getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest(uri,
            factory.newPrimitiveProperty(PROPERTY_INT64,
            factory.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataPropertyRequest<ClientProperty> requestGet = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    assertEquals(Long.MAX_VALUE, responseGet.getBody().getPrimitiveValue().toValue());
  }
  
  @Test
  public void updateComplexPropertyWithIEEE754CompatibleParamter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM).build();
    
    final ODataPropertyUpdateRequest requestUpdate = edmEnabledClient.getCUDRequestFactory()
        .getPropertyComplexValueUpdateRequest(uri, UpdateType.PATCH,
            factory.newComplexProperty(PROPERTY_COMP_ALL_PRIM,
                factory.newComplexValue("CTAllPrim")
                    .add(factory.newPrimitiveProperty(PROPERTY_INT64,
                        factory.newPrimitiveValueBuilder().buildInt64(Long.MIN_VALUE)))
                    .add(factory.newPrimitiveProperty(PROPERTY_DECIMAL,
                        factory.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(12345678912L))))
                    .add(factory.newPrimitiveProperty(PROPERTY_INT16,
                        factory.newPrimitiveValueBuilder().buildInt16((short) 2)))));
                                                         
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataPropertyRequest<ClientProperty> requestGet = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    final ClientComplexValue complexValue = responseGet.getBody().getComplexValue();
    
    assertEquals(Long.MIN_VALUE, complexValue.get(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(12345678912L), complexValue.get(PROPERTY_DECIMAL).getPrimitiveValue().toValue());
    assertEquals(2, complexValue.get(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void updatePropertyEdmDecimalWithIEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .appendKeySegment(0)
                                                     .appendPropertySegment(PROPERTY_DECIMAL).build();
    
    final ODataPropertyUpdateRequest requestUpdate = edmEnabledClient.getCUDRequestFactory()
        .getPropertyPrimitiveValueUpdateRequest(uri,
            factory.newPrimitiveProperty(PROPERTY_DECIMAL,
                factory.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));

    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataPropertyRequest<ClientProperty> requestGet = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    assertEquals(BigDecimal.valueOf(Long.MAX_VALUE), responseGet.getBody().getPrimitiveValue().toValue());
  }
  
  @Test
  public void readESAllPrimCollectionWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .orderBy(PROPERTY_INT16)
                                                     .build();

    ODataEntitySetRequest<ClientEntitySet> request = edmEnabledClient.getRetrieveRequestFactory()
        .getEntitySetRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    final List<ClientEntity> entities = response.getBody().getEntities();
    assertEquals(3, entities.size());

    ClientEntity entity = entities.get(0);
    assertEquals(-32768, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(Long.MIN_VALUE, entity.getProperty(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(-34), entity.getProperty(PROPERTY_DECIMAL).getPrimitiveValue().toValue());

    entity = entities.get(1);
    assertEquals(0, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(0L, entity.getProperty(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(0), entity.getProperty(PROPERTY_DECIMAL).getPrimitiveValue().toValue());

    entity = entities.get(2);
    assertEquals(32767, entity.getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(Long.MAX_VALUE, entity.getProperty(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(34), entity.getProperty(PROPERTY_DECIMAL).getPrimitiveValue().toValue());
  }
  
  @Test
  public void readESKeyNavCheckComplexPropertyWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    
    ODataEntityRequest<ClientEntity> request = edmEnabledClient.getRetrieveRequestFactory().getEntityRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    assertEquals(1, response.getBody().getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());
    
    assertEquals(BigDecimal.valueOf(34), response.getBody().getProperty(PROPERTY_COMP_ALL_PRIM)
                                                           .getComplexValue()
                                                           .get(PROPERTY_DECIMAL)
                                                           .getPrimitiveValue()
                                                           .toValue());
    
    assertEquals(Long.MAX_VALUE, response.getBody().getProperty(PROPERTY_COMP_ALL_PRIM)
                                                   .getComplexValue()
                                                   .get(PROPERTY_INT64)
                                                   .getPrimitiveValue()
                                                   .toValue());
  }
  
  @Test
  public void readESKEyNavComplexPropertyWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_COMP_ALL_PRIM)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    
    assertEquals(BigDecimal.valueOf(34), response.getBody().getComplexValue()
                                                           .get(PROPERTY_DECIMAL)
                                                           .getPrimitiveValue()
                                                           .toValue());

    assertEquals(Long.MAX_VALUE, response.getBody().getComplexValue()
                                                   .get(PROPERTY_INT64)
                                                   .getPrimitiveValue()
                                                   .toValue());
  }
  
  @Test
  @Ignore
  public void readEdmInt64PropertyWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
                                                     .appendPropertySegment(PROPERTY_INT64)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    
    assertEquals(Long.MAX_VALUE, response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  @Ignore
  public void readEdmDecimalPropertyWithIEEE754CompatibleParameter() {
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
                                                     .appendPropertySegment(PROPERTY_DECIMAL)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = edmEnabledClient.getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    
    assertEquals(BigDecimal.valueOf(34), response.getBody().getPrimitiveValue().toValue());
  }
}
