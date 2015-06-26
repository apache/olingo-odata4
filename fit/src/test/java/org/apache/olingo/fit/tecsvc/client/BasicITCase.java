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

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.ODataClientErrorException;
import org.apache.olingo.client.api.communication.header.HeaderName;
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
import org.apache.olingo.client.api.domain.ClientObjectFactory;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.api.edm.xml.Reference;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.commons.api.ODataError;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.fit.AbstractBaseTestITCase;
import org.apache.olingo.fit.tecsvc.TecSvcConst;
import org.junit.Ignore;
import org.junit.Test;

public class BasicITCase extends AbstractBaseTestITCase {
  
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

  private static final String SERVICE_URI = TecSvcConst.BASE_URI;
  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_TWO_PRIM = "ESTwoPrim";
  private static final String ES_KEY_NAV = "ESKeyNav";

  @Test
  public void readServiceDocument() {
    ODataServiceDocumentRequest request = getClient().getRetrieveRequestFactory()
        .getServiceDocumentRequest(SERVICE_URI);
    assertNotNull(request);

    ODataRetrieveResponse<ClientServiceDocument> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    ClientServiceDocument serviceDocument = response.getBody();
    assertNotNull(serviceDocument);

    assertThat(serviceDocument.getEntitySetNames(), hasItem("ESAllPrim"));
    assertThat(serviceDocument.getFunctionImportNames(), hasItem("FICRTCollCTTwoPrim"));
    assertThat(serviceDocument.getSingletonNames(), hasItem("SIMedia"));
  }

  @Test
  public void readMetadata() {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);

    ODataRetrieveResponse<Edm> response = request.execute();
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
    XMLMetadataRequest request = getClient().getRetrieveRequestFactory().getXMLMetadataRequest(SERVICE_URI);
    assertNotNull(request);

    ODataRetrieveResponse<XMLMetadata> response = request.execute();
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
    final ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp").build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertThat(response.getContentType(), containsString(ContentType.APPLICATION_JSON.toContentTypeString()));

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
    assertEquals(0, property.getPrimitiveValue().toValue());
  }

  @Test
  public void readEntityCollectionCount() {
    final ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getValueRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESServerSidePaging").appendCountSegment().build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientPrimitiveValue> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals(ContentType.TEXT_PLAIN.toContentTypeString(), response.getContentType());

    final ClientPrimitiveValue value = response.getBody();
    assertNotNull(value);
    assertEquals("503", value.toValue());
  }

  @Test
  public void readException() throws Exception {
    final ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESMixPrimCollComp").appendKeySegment("42").build());
    assertNotNull(request);

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
    final ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCollAllPrim").appendKeySegment(1).build());
    assertNotNull(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertThat(response.getContentType(), containsString(ContentType.APPLICATION_JSON.toContentTypeString()));

    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("CollPropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    assertEquals(1000, iterator.next().asPrimitive().toValue());
    assertEquals(2000, iterator.next().asPrimitive().toValue());
    assertEquals(30112, iterator.next().asPrimitive().toValue());
  }

  @Test
  public void deleteEntity() throws Exception {
    final ODataClient client = getClient();
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
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity patchEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("new")));
    patchEntity.getProperties().add(factory.newPrimitiveProperty("PropertyDecimal",
        factory.newPrimitiveValueBuilder().buildDouble(42.875)));
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
    assertEquals(42.875, property2.getPrimitiveValue().toValue());
    final ClientProperty property3 = entity.getProperty("PropertyInt64");
    assertNotNull(property3);
    assertNull(property3.getPrimitiveValue());
    final ClientProperty property4 = entity.getProperty("PropertyDuration");
    assertNotNull(property4);
    assertEquals("PT6S", property4.getPrimitiveValue().toValue());
  }

  @Test
  public void updateEntity() throws Exception {
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt32(42)));

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
    assertEquals(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = entity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue());
  }

  @Test
  public void patchEntityWithComplex() throws Exception {
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity patchEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETCompComp"));
    patchEntity.getProperties().add(factory.newComplexProperty("PropertyComp",
        factory.newComplexValue("olingo.odata.test1.CTCompComp").add(
            factory.newComplexProperty("PropertyComp",
                factory.newComplexValue("olingo.odata.test1.CTTwoPrim").add(
                    factory.newPrimitiveProperty("PropertyInt16",
                        factory.newPrimitiveValueBuilder().buildInt32(42)))))));
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
    assertEquals(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = complex.get("PropertyString");
    assertNotNull(property2);
    assertEquals("String 1", property2.getPrimitiveValue().toValue());
  }

  @Test
  public void updateEntityWithComplex() throws Exception {
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
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
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyInt64",
        factory.newPrimitiveValueBuilder().buildInt32(42)));
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
    assertEquals(SERVICE_URI + "/ESAllPrim(1)", createResponse.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity createdEntity = createResponse.getBody();
    assertNotNull(createdEntity);
    final ClientProperty property1 = createdEntity.getProperty("PropertyInt64");
    assertNotNull(property1);
    assertEquals(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = createdEntity.getProperty("PropertyDecimal");
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue());
  }

  @Test
  public void createEntityMinimalResponse() throws Exception {
    final ODataClient client = getClient();
    final ClientObjectFactory factory = client.getObjectFactory();
    ClientEntity newEntity = factory.newEntity(new FullQualifiedName("olingo.odata.test1", "ETTwoPrim"));
    newEntity.getProperties().add(factory.newPrimitiveProperty("PropertyString",
        factory.newPrimitiveValueBuilder().buildString("new")));
    ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").build(),
        newEntity);
    request.setPrefer(getClient().newPreferences().returnMinimal());

    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HeaderName.preferenceApplied).iterator().next());
    final String location = SERVICE_URI + "/ESTwoPrim(1)";
    assertEquals(location, response.getHeader(HttpHeader.LOCATION).iterator().next());
    assertEquals(location, response.getHeader(HttpHeader.ODATA_ENTITY_ID).iterator().next());
  }

  @Test
  public void readEntityWithExpandedNavigationProperty() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);

    final URI uri = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
        .build();

    final ODataRetrieveResponse<ClientEntity> response = client.getRetrieveRequestFactory()
        .getEntityRequest(uri)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check if all inlined entities are available
    // NavPropertyETKeyNavOne
    assertNotNull(response.getBody().getNavigationLink("NavPropertyETKeyNavOne"));
    final ClientInlineEntity inlineEntity = response.getBody()
        .getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity();
    assertNotNull(inlineEntity);
    assertEquals(Integer.valueOf(2), inlineEntity.getEntity()
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    // NavPropertyETKeyNavMany
    assertNotNull(response.getBody().getNavigationLink("NavPropertyETKeyNavMany"));
    final ClientInlineEntitySet inlineEntitySet = response.getBody()
        .getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet();
    assertNotNull(inlineEntitySet);
    assertEquals(2, inlineEntitySet.getEntitySet().getEntities().size());
    assertEquals(1, inlineEntitySet.getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(2, inlineEntitySet.getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  public void updateCollectionOfComplexCollection() {
    final ClientObjectFactory of = getClient().getObjectFactory();
    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));

    entity.getProperties().add(
        of.newCollectionProperty("CollPropertyComp",
            of.newCollectionValue("CTPrimComp")
                .add(of.newComplexValue("CTPrimComp")
                    .add(of.newPrimitiveProperty("PropertyInt16",
                        of.newPrimitiveValueBuilder().buildInt16((short) 42)))
                    .add(of.newComplexProperty("PropertyComp",
                        of.newComplexValue("CTAllPrim")
                            .add(of.newPrimitiveProperty("PropertyString",
                                of.newPrimitiveValueBuilder().buildString("42"))))))
                .add(of.newComplexValue("CTPrimComp")
                    .add(of.newPrimitiveProperty("PropertyInt16",
                        of.newPrimitiveValueBuilder().buildInt16((short) 43)))
                    .add(of.newComplexProperty("PropertyComp",
                        of.newComplexValue("CTAllPrim")
                            .add(of.newPrimitiveProperty("PropertyString",
                                of.newPrimitiveValueBuilder().buildString("43"))))))));

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(3)
        .build();

    final ODataEntityUpdateResponse<ClientEntity> response = getClient().getCUDRequestFactory()
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
    assertEquals(42, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
    assertNotNull(complexProperty.get("PropertyComp"));

    ClientComplexValue innerComplexProperty = complexProperty.get("PropertyComp").getComplexValue();
    assertEquals("42", innerComplexProperty.get("PropertyString").getPrimitiveValue().toValue());

    complexProperty = collectionIterator.next().asComplex();
    assertEquals(43, complexProperty.get("PropertyInt16").getPrimitiveValue().toValue());
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
    final ClientObjectFactory of = getClient().getObjectFactory();
    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.getProperties().add(
        of.newPrimitiveProperty("PropertyString",
            of.newPrimitiveValueBuilder().buildString("Complex collection test")));
    entity.getProperties().add(of.newComplexProperty("PropertyCompTwoPrim",
        of.newComplexValue("CTTwoPrim")
            .add(of.newPrimitiveProperty("PropertyInt16", of.newPrimitiveValueBuilder().buildInt16((short) 1)))
            .add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder().buildString("1")))));

    entity.getProperties().add(of.newCollectionProperty("CollPropertyComp",
        of.newCollectionValue("CTPrimComp")
            .add(of.newComplexValue("CTPrimComp")
                .add(of.newPrimitiveProperty("PropertyInt16", of.newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(of.newComplexProperty("PropertyComp", of.newComplexValue("CTAllPrim")
                    .add(of.newPrimitiveProperty("PropertyString",
                        of.newPrimitiveValueBuilder().buildString("1"))))))
            .add(of.newComplexValue("CTPrimComp")
                .add(of.newComplexProperty("PropertyComp", of.newComplexValue("CTAllPrim")
                    .add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder().buildString
                        ("2")))
                    .add(of.newPrimitiveProperty("PropertyInt16", of.newPrimitiveValueBuilder().buildInt16(
                        (short) 2)))
                    .add(of.newPrimitiveProperty("PropertySingle",
                        of.newPrimitiveValueBuilder().buildSingle(2.0f))))))));

    entity.addLink(of.newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESTwoKeyNav")
        .appendKeySegment(new LinkedHashMap<String, Object>() {
          private static final long serialVersionUID = 1L;

          {
            put("PropertyInt16", 1);
            put("PropertyString", "1");
          }
        }).build()));

    final ODataEntityCreateResponse<ClientEntity> response = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESKeyNav").build(),
        entity).execute();

    // Check if not declared fields are also available
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    final ClientEntity newEntity = response.getBody();

    assertEquals(2, newEntity.getProperty("CollPropertyComp").getCollectionValue().size());
    final Iterator<ClientValue> iter = newEntity.getProperty("CollPropertyComp").getCollectionValue().iterator();
    final ClientComplexValue complexProperty1 = iter.next().asComplex();
    assertEquals(1, complexProperty1.get("PropertyInt16").getPrimitiveValue().toValue());
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
    assertEquals(2, innerComplexProperty2.get("PropertyInt16").getPrimitiveValue().toValue());
    assertEquals(Double.valueOf(2), innerComplexProperty2.get("PropertySingle").getPrimitiveValue().toValue());
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
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    // PropertyComp is null, but the primitive values in PropertyComp must not be null
    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETMixPrimCollComp"));
    final URI targetURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESMixPrimCollComp").build();

    try {
      client.getCUDRequestFactory().getEntityCreateRequest(targetURI, entity).execute();
      fail("Expecting bad request");
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void upsert() throws EdmPrimitiveTypeException {
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETTwoPrim"));
    entity.getProperties().add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder()
        .buildString("Test")));

    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESTwoPrim").appendKeySegment(33).build();
    final ODataEntityUpdateResponse<ClientEntity> updateResponse =
        client.getCUDRequestFactory().getEntityUpdateRequest(uri, UpdateType.PATCH, entity).execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), updateResponse.getStatusCode());
    assertEquals("Test", updateResponse.getBody().getProperty("PropertyString").getPrimitiveValue().toValue());

    final String cookie = updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short key = updateResponse.getBody().getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toCastValue(Short.class);

    final ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(client.newURIBuilder()
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
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESAllPrim")
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETAllPrim"));
    entity.getProperties().add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder()
        .buildString(null)));

    ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity);
    request.setPrefer(getClient().newPreferences().returnRepresentation());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("return=representation", response.getHeader(HeaderName.preferenceApplied).iterator().next());
    assertTrue(response.getBody().getProperty("PropertyString").hasNullValue());
    assertEquals(34, response.getBody().getProperty("PropertyDecimal").getPrimitiveValue().toValue());
  }

  @Test(expected = ODataClientErrorException.class)
  public void updatePropertyWithNullNotAllowed() {
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.getProperties().add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder()
        .buildString(null)));

    client.getCUDRequestFactory().getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity).execute();
  }

  @Test
  public void updateMerge() {
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .build();

    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.addLink(of.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(of.newEntitySetNavigationLink("NavPropertyETKeyNavMany", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav").appendKeySegment(3).build()));
    entity.getProperties().add(of.newCollectionProperty("CollPropertyString", of.newCollectionValue("Edm.String")
        .add(of.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(of.newComplexProperty("PropertyCompAllPrim",
        of.newComplexValue("CTAllPrim")
        .add(of.newPrimitiveProperty("PropertyString",
                of.newPrimitiveValueBuilder().buildString("Changed")))));

    final ODataEntityUpdateResponse<ClientEntity> response = client.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(
            client.newURIBuilder()
                .appendEntitySetSegment("ESKeyNav")
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertEquals(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
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

    assertEquals(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
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
    final EdmEnabledODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final ClientObjectFactory of = client.getObjectFactory();

    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav")
        .appendKeySegment(1)
        .build();

    final ClientEntity entity = of.newEntity(new FullQualifiedName("olingo.odata.test1", "ETKeyNav"));
    entity.addLink(of.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(of.newEntitySetNavigationLink("NavPropertyETKeyNavMany", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment("ESKeyNav").appendKeySegment(3).build()));
    entity.getProperties().add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder()
        .buildString("Must not be null")));
    entity.getProperties().add(of.newComplexProperty("PropertyCompTwoPrim", of.newComplexValue("CTTwoPrim")
        .add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder()
            .buildString("Must not be null")))
            .add(of.newPrimitiveProperty("PropertyInt16", of.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties().add(of.newCollectionProperty("CollPropertyString", of.newCollectionValue("Edm.String")
        .add(of.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(of.newComplexProperty("PropertyCompAllPrim",
        of.newComplexValue("CTAllPrim")
        .add(of.newPrimitiveProperty("PropertyString", of.newPrimitiveValueBuilder().buildString("Changed")))));

    ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.REPLACE, entity);
    request.setPrefer(getClient().newPreferences().returnMinimal());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HeaderName.preferenceApplied).iterator().next());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = client.getRetrieveRequestFactory()
        .getEntityRequest(
            client.newURIBuilder()
                .appendEntitySetSegment("ESKeyNav")
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertEquals(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
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

    assertEquals(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(0)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty("PropertyInt16")
        .getPrimitiveValue()
        .toValue());

    assertEquals(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
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
    assertEquals(42, propCompTwoPrim.get("PropertyInt16").getPrimitiveValue().toValue());

    assertNotNull(entityResponse.getBody().getProperty("PropertyCompNav").getComplexValue());
    assertTrue(entityResponse.getBody()
        .getProperty("PropertyCompNav")
        .getComplexValue()
        .get("PropertyInt16")
        .hasNullValue());
  }
  
  @Test
  public void createEntityWithIEEE754CompatibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
                                                         .appendKeySegment(32767).build();
    
    final ClientEntity newEntity = of.newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(of.newPrimitiveProperty(PROPERTY_INT64, 
        of.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    newEntity.getProperties().add(of.newPrimitiveProperty(PROPERTY_DECIMAL, 
        of.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(34))));
    newEntity.addLink(of.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));
    
    final ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
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
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
                                                         .appendKeySegment(32767).build();
    
    final ClientEntity newEntity = of.newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(of.newPrimitiveProperty(PROPERTY_INT64, 
        of.newPrimitiveValueBuilder().buildInt64(null)));
    newEntity.getProperties().add(of.newPrimitiveProperty(PROPERTY_DECIMAL, 
        of.newPrimitiveValueBuilder().buildDecimal(null)));
    newEntity.addLink(of.newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));
    
    final ODataEntityCreateRequest<ClientEntity> request = client.getCUDRequestFactory()
                                                                 .getEntityCreateRequest(uri, newEntity);
    request.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    
    assertTrue(response.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(response.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }
  
  @Test
  public void updateEntityWithIEEE754CompatibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = of.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_INT64, 
        of.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_DECIMAL, 
        of.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(Long.MAX_VALUE))));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = client.getCUDRequestFactory()
                                                                 .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();
    
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uri);
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
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = of.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_INT64, 
        of.newPrimitiveValueBuilder().buildInt64(null)));
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_DECIMAL, 
        of.newPrimitiveValueBuilder().buildDecimal(null)));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = client.getCUDRequestFactory()
                                                                 .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();
    
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataEntityRequest<ClientEntity> requestGet = client.getRetrieveRequestFactory().getEntityRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();
    
    assertTrue(responseGet.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(responseGet.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }
  
  @Test
  public void updateEntityWithIEEE754CompatibleParameterWithNullString() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();
    
    final ClientEntity entity = of.newEntity(ET_ALL_PRIM);
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_INT64, 
        of.newPrimitiveValueBuilder().buildString("null")));
    entity.getProperties().add(of.newPrimitiveProperty(PROPERTY_DECIMAL, 
        of.newPrimitiveValueBuilder().buildString("null")));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = client.getCUDRequestFactory()
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
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .appendKeySegment(0)
                                                     .appendPropertySegment(PROPERTY_INT64).build();
    
    final ODataPropertyUpdateRequest requestUpdate = client.getCUDRequestFactory()
                                                     .getPropertyPrimitiveValueUpdateRequest(uri, 
                                                         of.newPrimitiveProperty(PROPERTY_INT64, 
                                                            of.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataPropertyRequest<ClientProperty> requestGet = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    assertEquals(Long.MAX_VALUE, responseGet.getBody().getPrimitiveValue().toValue());
  }
  
  @Test
  public void updateComplexPropertyWithIEEE754CompatibleParamter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM).build();
    
    final ODataPropertyUpdateRequest requestUpdate = client.getCUDRequestFactory()
        .getPropertyComplexValueUpdateRequest(uri, UpdateType.PATCH,
            of.newComplexProperty(PROPERTY_COMP_ALL_PRIM,
                of.newComplexValue("CTAllPrim")
                    .add(of.newPrimitiveProperty(PROPERTY_INT64,
                        of.newPrimitiveValueBuilder().buildInt64(Long.MIN_VALUE)))
                    .add(of.newPrimitiveProperty(PROPERTY_DECIMAL,
                        of.newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(12345678912L))))
                    .add(of.newPrimitiveProperty(PROPERTY_INT16,
                        of.newPrimitiveValueBuilder().buildInt16((short) 2)))));
                                                         
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataPropertyRequest<ClientProperty> requestGet = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    final ClientComplexValue complexValue = responseGet.getBody().getComplexValue();
    
    assertEquals(Long.MIN_VALUE, complexValue.get(PROPERTY_INT64).getPrimitiveValue().toValue());
    assertEquals(BigDecimal.valueOf(12345678912L), complexValue.get(PROPERTY_DECIMAL).getPrimitiveValue().toValue());
    assertEquals(2, complexValue.get(PROPERTY_INT16).getPrimitiveValue().toValue());
  }
  
  @Test
  public void updateProperyEdmDecimaltWithIEE754CompatibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    client.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    final ClientObjectFactory of = client.getObjectFactory();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .appendKeySegment(0)
                                                     .appendPropertySegment(PROPERTY_DECIMAL).build();
    
    final ODataPropertyUpdateRequest requestUpdate = client.getCUDRequestFactory()
                                                     .getPropertyPrimitiveValueUpdateRequest(uri, 
                                                         of.newPrimitiveProperty(PROPERTY_DECIMAL, 
                                                            of.newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    
    final ODataPropertyRequest<ClientProperty> requestGet = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();
    
    assertEquals(BigDecimal.valueOf(Long.MAX_VALUE), responseGet.getBody().getPrimitiveValue().toValue());
  }
  
  @Test
  public void readESAllPrimCollectionWithIEEE754CompatibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
                                                     .orderBy(PROPERTY_INT16)
                                                     .build();
    
    final ODataEntitySetRequest<ClientEntitySet> request = client.getRetrieveRequestFactory().getEntitySetRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    
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
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
    
    final ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory().getEntityRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    
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
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendNavigationSegment(PROPERTY_COMP_ALL_PRIM)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
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
  public void readEdmInt64PropertyWithIEEE754ComaptibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
                                                     .appendPropertySegment(PROPERTY_INT64)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    
    assertEquals(Long.MAX_VALUE, response.getBody().getPrimitiveValue().toValue());
  }
  
  @Test
  @Ignore
  public void readEdmDecimalPropertyWithIEEE754ComaptibleParameter() {
    final ODataClient client = ODataClientFactory.getEdmEnabledClient(SERVICE_URI);
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
                                                     .appendKeySegment(1)
                                                     .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
                                                     .appendPropertySegment(PROPERTY_DECIMAL)
                                                     .build();
    ODataPropertyRequest<ClientProperty> request = client.getRetrieveRequestFactory().getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    
    assertEquals(BigDecimal.valueOf(34), response.getBody().getPrimitiveValue().toValue());
  }

  @Override
  protected ODataClient getClient() {
    ODataClient odata = ODataClientFactory.getClient();
    odata.getConfiguration().setDefaultPubFormat(ContentType.JSON);
    return odata;
  }
}
