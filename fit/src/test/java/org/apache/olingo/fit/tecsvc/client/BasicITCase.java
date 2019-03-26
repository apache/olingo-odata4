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
import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
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
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
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
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.api.uri.FilterArgFactory;
import org.apache.olingo.client.api.uri.FilterFactory;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataError;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.Ignore;
import org.junit.Test;

public class BasicITCase extends AbstractParamTecSvcITCase {

  private static final String CONTENT_TYPE_JSON_IEEE754_COMPATIBLE =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true").toContentTypeString();
  private static final FullQualifiedName ET_ALL_PRIM = new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim");
  private static final FullQualifiedName ET_KEY_NAV = new FullQualifiedName(SERVICE_NAMESPACE, "ETKeyNav");

  private static final String PROPERTY_INT16 = "PropertyInt16";
  private static final String PROPERTY_INT64 = "PropertyInt64";
  private static final String PROPERTY_DECIMAL = "PropertyDecimal";
  private static final String PROPERTY_STRING = "PropertyString";
  private static final String PROPERTY_COMP = "PropertyComp";
  private static final String PROPERTY_COMP_ALL_PRIM = "PropertyCompAllPrim";
  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";

  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_TWO_PRIM = "ESTwoPrim";
  private static final String ES_KEY_NAV = "ESKeyNav";
  private static final String ES_MIX_PRIM_COLL_COMP = "ESMixPrimCollComp";
  private static final String PROPERTY_COMP_NAV = "CollPropertyCompNav";
  private static final String COL_PROPERTY_COMP = "CollPropertyComp";
  private static final String PROPERTY_COMP_TWO_PRIM = "PropertyCompTwoPrim";

  private static final String SERVICE_ROOT_URL = "http://localhost:9080/odata-server-tecsvc/";
  
  @Test
  public void readServiceDocument() {
    ODataServiceDocumentRequest request = getClient().getRetrieveRequestFactory()
        .getServiceDocumentRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<ClientServiceDocument> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    ClientServiceDocument serviceDocument = response.getBody();
    assertNotNull(serviceDocument);
    assertThat(serviceDocument.getEntitySetNames(), hasItem(ES_ALL_PRIM));
    assertThat(serviceDocument.getFunctionImportNames(), hasItem("FICRTCollCTTwoPrim"));
    assertThat(serviceDocument.getSingletonNames(), hasItem("SIMedia"));
  }

  @Test
  public void readMetadata() {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    assertNotNull(edm);
    
    final EdmEntityContainer container = edm.getEntityContainer(
        new FullQualifiedName("olingo.odata.test1", "Container"));
    assertNotNull(container);
    
    final EdmEntitySet esAllPrim = container.getEntitySet("ESAllPrim");
    assertNotNull(esAllPrim);
    assertEquals("olingo.odata.test1", esAllPrim.getEntityType().getNamespace());
    
    assertEquals(2, edm.getSchemas().size());
    assertEquals(SERVICE_NAMESPACE, edm.getSchema(SERVICE_NAMESPACE).getNamespace());
    assertEquals("Namespace1_Alias", edm.getSchema(SERVICE_NAMESPACE).getAlias());
    assertEquals("Org.OData.Core.V1", edm.getSchema("Org.OData.Core.V1").getNamespace());
    assertEquals("Core", edm.getSchema("Org.OData.Core.V1").getAlias());
  }

  @Test
  public void readMetadataWithTerm() {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    assertNotNull(edm);
    
    final EdmTerm descriptionTerm = edm.getTerm(new FullQualifiedName("Core", "Description"));
    assertNotNull(descriptionTerm);
    assertEquals(descriptionTerm.getFullQualifiedName(),
        edm.getTerm(new FullQualifiedName("Org.OData.Core.V1", "Description")).getFullQualifiedName());  
  }
  
  @Test
  public void readMetadataWithAnnotations() {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();

    assertNotNull(edm);
    EdmEntitySet entitySet = edm.getEntityContainer().getEntitySet("ESAllPrim");
    List<EdmAnnotation> annotations = entitySet.getAnnotations();
    assertNotNull(annotations);
    // Just one is necessary to not make the test too strict
    assertTrue(annotations.size() > 1);
    EdmAnnotation annotation =
        entitySet.getAnnotation(edm.getTerm(new FullQualifiedName("Org.OData.Core.V1", "Description")), null);
    assertNotNull(annotation);
    
    assertEquals("Contains entities with all primitive types",
        annotation.getExpression().asConstant().getValueAsString());
    
    EdmActionImport actionImport = edm.getEntityContainer().getActionImport("AIRTString");
    annotations = actionImport.getAnnotations();
    assertNotNull(annotations);
    // Just one is necessary to not make the test too strict
    assertTrue(annotations.size() > 1);
    annotation =
        entitySet.getAnnotation(edm.getTerm(new FullQualifiedName("Org.OData.Core.V1", "Description")), null);
    assertNotNull(annotation);
  }

  @Test
  public void readViaXmlMetadata() {
    XMLMetadataRequest request = getClient().getRetrieveRequestFactory().getXMLMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRetrieveResponse<XMLMetadata> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    XMLMetadata xmlMetadata = response.getBody();

    assertNotNull(xmlMetadata);
    assertEquals(2, xmlMetadata.getSchemas().size());
    assertEquals(SERVICE_NAMESPACE, xmlMetadata.getSchema(SERVICE_NAMESPACE).getNamespace());
    final List<Reference> references = xmlMetadata.getReferences();
    assertEquals(1, references.size());
    assertThat(references.get(0).getUri().toASCIIString(), containsString("vocabularies/Org.OData.Core.V1"));
  }

  @Test
  public void readEntitySet() {
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_MIX_PRIM_COLL_COMP).build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertNotNull(response.getHeaderNames());
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
    final ClientProperty property = entity.getProperty(PROPERTY_INT16);
    assertNotNull(property);
    assertNotNull(property.getPrimitiveValue());
    assertShortOrInt(0, property.getPrimitiveValue().toValue());
  }
  
  @Test
  public void readEntitySetWitInlineCount() {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESAllPrim").count(true);

    final ODataRawRequest req = getClient().getRetrieveRequestFactory().getRawRequest(uriBuilder.build());

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ClientEntitySet> entitySet = res.getBodyAs(ClientEntitySet.class);
    assertEquals(3, entitySet.getPayload().getEntities().size());
  }
  
  @Test
  public void readEntitySetWitNext() {
    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESServerSidePaging");

    final ODataEntitySetRequest<ClientEntitySet> req = getClient().getRetrieveRequestFactory().
        getEntitySetRequest(uriBuilder.build());

    final ODataRetrieveResponse<ClientEntitySet> res = req.execute();
    assertNotNull(res.getRawResponse());
    final ClientEntitySet feed = res.getBody();
    assertNotNull(feed);

    assertEquals(10, feed.getEntities().size());
    assertNotNull(feed.getNext());

    final URI expected = URI.create(SERVICE_URI + "ESServerSidePaging?%24skiptoken=1%2A10");
    final URI found = URIUtils.getURI(SERVICE_URI, feed.getNext().toASCIIString());

    assertEquals(expected, found);
  }
  
  @Test
  public void readEntityCollectionCount() {
    ODataValueRequest request = getClient().getRetrieveRequestFactory()
        .getValueRequest(getClient().newURIBuilder(SERVICE_URI)
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
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_MIX_PRIM_COLL_COMP).appendKeySegment("42").build());
    assertNotNull(request);
    setCookieHeader(request);

    try {
      request.execute();
      fail("Expected Exception not thrown!");
    } catch (final ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
      final ODataError error = e.getODataError();
      assertThat(error.getMessage(), containsString("key"));
    }    
  }
 
  @Test
  public void readEntity() throws Exception {
    ODataEntityRequest<ClientEntity> request = getClient().getRetrieveRequestFactory()
        .getEntityRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCollAllPrim").appendKeySegment(1).build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntity> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());
    assertNotNull(response.getRawResponse());
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property = entity.getProperty("CollPropertyInt16");
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    assertShortOrInt(1000, iterator.next().asPrimitive().toValue());
    assertShortOrInt(2000, iterator.next().asPrimitive().toValue());
    assertShortOrInt(30112, iterator.next().asPrimitive().toValue());
  }

  @Test
  public void readEntityProperty() throws Exception {
    ODataPropertyRequest<ClientProperty> request = getClient().getRetrieveRequestFactory()
        .getPropertyRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESCollAllPrim").appendKeySegment(1)
            .appendPropertySegment("CollPropertyInt16").build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertContentType(response.getContentType());
    assertNotNull(response.getRawResponse());
    final ClientProperty property = response.getBody();
    assertNotNull(property);
    assertNotNull(property.getCollectionValue());
    assertEquals(3, property.getCollectionValue().size());
    Iterator<ClientValue> iterator = property.getCollectionValue().iterator();
    assertShortOrInt(1000, iterator.next().asPrimitive().toValue());
    assertShortOrInt(2000, iterator.next().asPrimitive().toValue());
    assertShortOrInt(30112, iterator.next().asPrimitive().toValue());
  }
  
  @Test
  public void deleteEntity() throws Exception {
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
        .build();
    final ODataDeleteRequest request = getClient().getCUDRequestFactory().getDeleteRequest(uri);
    final ODataDeleteResponse response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());

    // Check that the deleted entity is really gone.
    // This check has to be in the same session in order to access the same data provider.
    ODataEntityRequest<ClientEntity> entityRequest = getClient().getRetrieveRequestFactory().getEntityRequest(uri);
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
    ClientEntity patchEntity = getFactory().newEntity(ET_ALL_PRIM);
    patchEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString("new")));
    patchEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildDecimal(new BigDecimal(42.875))));
    patchEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64(null)));
    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
        .build();
    final ODataEntityUpdateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.PATCH, patchEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the patched properties have changed and the other properties not.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property1 = entity.getProperty(PROPERTY_STRING);
    assertNotNull(property1);
    assertEquals("new", property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = entity.getProperty(PROPERTY_DECIMAL);
    assertNotNull(property2);
    assertEquals(isJson() ? 42.875 : new BigDecimal(42.875),
        property2.getPrimitiveValue().toValue());
    final ClientProperty property3 = entity.getProperty(PROPERTY_INT64);
    assertNotNull(property3);
    assertNull(property3.getPrimitiveValue().toValue());
    final ClientProperty property4 = entity.getProperty("PropertyDuration");
    assertNotNull(property4);
    assertEquals(isJson() ? "PT6S" : BigDecimal.valueOf(6), property4.getPrimitiveValue().toValue());
  }


  @Test
  public void readUpdatepdateEntity() throws Exception {
    
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    assertNotNull(edm);
    
    final EdmEntityContainer container = edm.getEntityContainer(
        new FullQualifiedName("olingo.odata.test1", "Container"));
    assertNotNull(container);
    
    final EdmEntitySet esAllPrim = container.getEntitySet("ESAllPrim");
    assertNotNull(esAllPrim);
    assertEquals("olingo.odata.test1", esAllPrim.getEntityType().getNamespace());
    
    assertEquals(2, edm.getSchemas().size());
    assertEquals(SERVICE_NAMESPACE, edm.getSchema(SERVICE_NAMESPACE).getNamespace());
    assertEquals("Namespace1_Alias", edm.getSchema(SERVICE_NAMESPACE).getAlias());
    assertEquals("Org.OData.Core.V1", edm.getSchema("Org.OData.Core.V1").getNamespace());
    assertEquals("Core", edm.getSchema("Org.OData.Core.V1").getAlias());
    
    ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64((long) 42)));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
        .build();
    final ODataEntityUpdateRequest<ClientEntity> request2 = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.REPLACE, newEntity);
    HttpUriRequest req = request2.getHttpRequest();
    final ODataEntityUpdateResponse<ClientEntity> response2 = request2.execute();
    assertNotNull(req);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response2.getStatusCode());

    // Check that the updated properties have changed and that other properties have their default values.
 
    StringWriter writer = new StringWriter();
    InputStream stream = response2.getRawResponse();
    IOUtils.copy(stream, writer);
    assertNotNull(writer.toString());  
    final ClientEntity entity = response2.getBody();
    assertNotNull(entity);
    final ClientProperty property1 = entity.getProperty(PROPERTY_INT64);
    assertNotNull(property1);
    assertShortOrInt(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = entity.getProperty(PROPERTY_DECIMAL);
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue().toValue());
  }
  
  @Test
  public void updateEntity() throws Exception {
    ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64((long) 42)));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(32767)
        .build();
    final ODataEntityUpdateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.REPLACE, newEntity);
    HttpUriRequest req = request.getHttpRequest();
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertNotNull(req);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the updated properties have changed and that other properties have their default values.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientProperty property1 = entity.getProperty(PROPERTY_INT64);
    assertNotNull(property1);
    assertShortOrInt(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = entity.getProperty(PROPERTY_DECIMAL);
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue().toValue());
  }

  @Test
  public void patchEntityWithComplex() throws Exception {
    ClientEntity patchEntity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETCompComp"));
    patchEntity.getProperties().add(getFactory().newComplexProperty(PROPERTY_COMP,
        getFactory().newComplexValue(SERVICE_NAMESPACE + ".CTCompComp").add(
            getFactory().newComplexProperty(PROPERTY_COMP,
                getFactory().newComplexValue(SERVICE_NAMESPACE + ".CTTwoPrim").add(
                    getFactory().newPrimitiveProperty(PROPERTY_INT16,
                        getFactory().newPrimitiveValueBuilder().buildInt16((short) 42)))))));
    ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESCompComp").appendKeySegment(1).build();
    final ODataEntityUpdateRequest<ClientEntity> request = client.getCUDRequestFactory().getEntityUpdateRequest(
        uri, UpdateType.PATCH, patchEntity);
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    // Check that the patched properties have changed and the other properties not.
    final ClientEntity entity = response.getBody();
    assertNotNull(entity);
    final ClientComplexValue complex = entity.getProperty(PROPERTY_COMP).getComplexValue()
        .get(PROPERTY_COMP).getComplexValue();
    assertNotNull(complex);
    final ClientProperty property1 = complex.get(PROPERTY_INT16);
    assertNotNull(property1);
    assertShortOrInt(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = complex.get(PROPERTY_STRING);
    assertNotNull(property2);
    assertEquals("String 1", property2.getPrimitiveValue().toValue());
  }

  @Test
  public void updateEntityWithComplex() throws Exception {
    ClientEntity newEntity = getFactory().newEntity(ET_KEY_NAV);
    newEntity.getProperties().add(getFactory().newComplexProperty("PropertyCompCompNav", null));
    // The following properties must not be null
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString("Test")));
    newEntity.getProperties().add(
        getFactory().newComplexProperty("PropertyCompTwoPrim",
            getFactory().newComplexValue(SERVICE_NAMESPACE+".CTTwoPrim")
                .add(getFactory().newPrimitiveProperty(
                    PROPERTY_INT16,
                    getFactory().newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(getFactory().newPrimitiveProperty(
                    PROPERTY_STRING,
                    getFactory().newPrimitiveValueBuilder().buildString("Test2")))));

    ODataClient client = getClient();
    final URI uri = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();
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
    final ClientProperty property = complex.get(PROPERTY_INT16);
    assertNotNull(property);
    assertNull(property.getPrimitiveValue().toValue());
  }

  @Test
  public void createEntity() throws Exception {
    ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64((long) 42)));
    final ODataClient client = getClient();
    newEntity.addLink(getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE,
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32766)
            .build()));

    final ODataEntityCreateRequest<ClientEntity> createRequest = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build(),
        newEntity);
    assertNotNull(createRequest);
    final ODataEntityCreateResponse<ClientEntity> createResponse = createRequest.execute();
    assertNotNull(createRequest.getHttpRequest());
    assertNotNull(((AbstractODataBasicRequest)createRequest).getPayload());
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), createResponse.getStatusCode());
    assertEquals(SERVICE_URI + ES_ALL_PRIM + "(1)", createResponse.getHeader(HttpHeader.LOCATION).iterator().next());
    final ClientEntity createdEntity = createResponse.getBody();
    assertNotNull(createdEntity);
    final ClientProperty property1 = createdEntity.getProperty(PROPERTY_INT64);
    assertNotNull(property1);
    assertShortOrInt(42, property1.getPrimitiveValue().toValue());
    final ClientProperty property2 = createdEntity.getProperty(PROPERTY_DECIMAL);
    assertNotNull(property2);
    assertNull(property2.getPrimitiveValue().toValue());
  }

  @Test
  public void createEntityMinimalResponse() throws Exception {
    ClientEntity newEntity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETTwoPrim"));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString("new")));
    ODataEntityCreateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM).build(),
        newEntity);
    request.setPrefer(getClient().newPreferences().returnMinimal());

    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    final String location = SERVICE_URI + ES_TWO_PRIM + "(1)";
    assertEquals(location, response.getHeader(HttpHeader.LOCATION).iterator().next());
    assertEquals(location, response.getHeader(HttpHeader.ODATA_ENTITY_ID).iterator().next());
  }

  @Test
  public void createEntityWithEnumAndTypeDefinition() throws Exception {
    ClientEntity newEntity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETMixEnumDefCollComp"));
    final ODataEntityCreateRequest<ClientEntity> request = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESMixEnumDefCollComp").build(),
        newEntity);
    final ODataEntityCreateResponse<ClientEntity> response = request.execute();
    final ClientEntity createdEntity = response.getBody();
    assertNotNull(createdEntity);
    ClientProperty property = createdEntity.getProperty("PropertyEnumString");
    assertNotNull(property);
    // TODO: Improve client value types.
    assertEquals("String1", isJson() ? property.getPrimitiveValue().toValue() : property.getEnumValue().getValue());
    property = createdEntity.getProperty("PropertyDefString");
    assertNotNull(property);
    // TODO: Improve client value types.
    assertEquals("1", isJson() ?
        property.getPrimitiveValue().toValue() :
        property.getEnumValue().getValue());
  }

  @Test
  public void readEntityWithExpandedNavigationProperty() {
    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
        .build();

    ODataEntityRequest<ClientEntity> request = getEdmEnabledClient().getRetrieveRequestFactory().getEntityRequest(uri);
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
    assertShortOrInt(2, inlineEntity.getEntity().getProperty(PROPERTY_INT16).getPrimitiveValue().toValue());

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
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, inlineEntitySet.getEntitySet()
        .getEntities()
        .get(1)
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());
  }

  @Test
  public void readPropertyValueFromEntityWithAlias() {
    Map<String, Object> segmentValues = new LinkedHashMap<String, Object>();
    segmentValues.put("PropertyInt16", 1);
    segmentValues.put("KeyAlias1", 11);
    segmentValues.put("KeyAlias2", "Num11");
    segmentValues.put("KeyAlias3", "Num111");

    final URIBuilder uriBuilder = getClient().newURIBuilder(SERVICE_URI).
        appendEntitySetSegment("ESFourKeyAlias")
            .appendKeySegment(segmentValues)
            .appendPropertySegment("PropertyCompComp");
    final ODataPropertyRequest<ClientProperty> req = getClient().getRetrieveRequestFactory().
        getPropertyRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ClientProperty prop = req.execute().getBody();
    assertNotNull(prop);
    final ClientComplexValue complexValue = prop.getComplexValue();
    assertNotNull(complexValue);
    final ClientValue propertyComp = complexValue.get("PropertyComp").getValue();
    assertNotNull(propertyComp);
    final ClientProperty propertyInt = propertyComp.asComplex().get("PropertyInt16");
    assertNotNull(propertyInt);
    final ClientPrimitiveValue clientValue = propertyInt.getPrimitiveValue();
    assertNotNull(clientValue);
    assertEquals("111", clientValue.toString());
  }
  
  @Test
  public void updateCollectionOfComplexCollection() {
    final ClientEntity entity = getFactory().newEntity(ET_KEY_NAV);

    entity.getProperties().add(
        getFactory().newCollectionProperty("CollPropertyComp",
            getFactory().newCollectionValue(SERVICE_NAMESPACE+".CTPrimComp")
                .add(getFactory().newComplexValue(SERVICE_NAMESPACE+".CTPrimComp")
                    .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                        getFactory().newPrimitiveValueBuilder().buildInt16((short) 42)))
                    .add(getFactory().newComplexProperty(PROPERTY_COMP,
                        getFactory().newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
                            .add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
                                getFactory().newPrimitiveValueBuilder().buildString("42"))))))
                .add(getFactory().newComplexValue(SERVICE_NAMESPACE+".CTPrimComp")
                    .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                        getFactory().newPrimitiveValueBuilder().buildInt16((short) 43)))
                    .add(getFactory().newComplexProperty(PROPERTY_COMP,
                        getFactory().newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
                            .add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
                                getFactory().newPrimitiveValueBuilder().buildString("43"))))))));

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
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
    assertShortOrInt(42, complexProperty.get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(complexProperty.get(PROPERTY_COMP));

    ClientComplexValue innerComplexProperty = complexProperty.get(PROPERTY_COMP).getComplexValue();
    assertEquals("42", innerComplexProperty.get(PROPERTY_STRING).getPrimitiveValue().toValue());

    complexProperty = collectionIterator.next().asComplex();
    assertShortOrInt(43, complexProperty.get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(complexProperty.get(PROPERTY_COMP));

    innerComplexProperty = complexProperty.get(PROPERTY_COMP).getComplexValue();
    assertEquals("43", innerComplexProperty.get(PROPERTY_STRING).getPrimitiveValue().toValue());
  }

  @Test
  public void createCollectionOfComplexCollection() {
    /*
     * Create a new entity which contains a collection of complex collections
     * Check if all not filled fields are created by the server
     */
    final ClientEntity entity = getFactory().newEntity(ET_KEY_NAV);
    entity.getProperties().add(
        getFactory().newPrimitiveProperty(PROPERTY_STRING,
            getFactory().newPrimitiveValueBuilder().buildString("Complex collection test")));
    entity.getProperties().add(getFactory().newComplexProperty("PropertyCompTwoPrim",
        getFactory().newComplexValue(SERVICE_NAMESPACE+".CTTwoPrim")
            .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                getFactory().newPrimitiveValueBuilder().buildInt16((short) 1)))
            .add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
                getFactory().newPrimitiveValueBuilder().buildString("1")))));

    entity.getProperties().add(getFactory().newCollectionProperty("CollPropertyComp",
        getFactory().newCollectionValue(SERVICE_NAMESPACE+".CTPrimComp")
            .add(getFactory().newComplexValue(SERVICE_NAMESPACE+".CTPrimComp")
                .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                    getFactory().newPrimitiveValueBuilder().buildInt16((short) 1)))
                .add(getFactory().newComplexProperty(PROPERTY_COMP, 
                    getFactory().newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
                    .add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
                        getFactory().newPrimitiveValueBuilder().buildString("1"))))))
            .add(getFactory().newComplexValue(SERVICE_NAMESPACE+".CTPrimComp")
                .add(getFactory().newComplexProperty(PROPERTY_COMP, 
                    getFactory().newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
                    .add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
                        getFactory().newPrimitiveValueBuilder().buildString("2")))
                    .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                        getFactory().newPrimitiveValueBuilder().buildInt16((short) 2)))
                    .add(getFactory().newPrimitiveProperty("PropertySingle",
                        getFactory().newPrimitiveValueBuilder().buildSingle(2.0f))))))));

    entity.addLink(getFactory().newEntityNavigationLink("NavPropertyETTwoKeyNavOne",
        getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment("ESTwoKeyNav")
            .appendKeySegment(new LinkedHashMap<String, Object>() {
              private static final long serialVersionUID = 1L;

              {
                put(PROPERTY_INT16, 1);
                put(PROPERTY_STRING, "1");
              }
            }).build()));

    final ODataEntityCreateResponse<ClientEntity> response = getClient().getCUDRequestFactory().getEntityCreateRequest(
        getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV).build(), entity)
        .execute();

    // Check if not declared fields are also available
    assertEquals(HttpStatusCode.CREATED.getStatusCode(), response.getStatusCode());
    final ClientEntity newEntity = response.getBody();

    assertEquals(2, newEntity.getProperty("CollPropertyComp").getCollectionValue().size());
    final Iterator<ClientValue> iter = newEntity.getProperty("CollPropertyComp").getCollectionValue().iterator();
    final ClientComplexValue complexProperty1 = iter.next().asComplex();
    assertShortOrInt(1, complexProperty1.get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertNotNull(complexProperty1.get(PROPERTY_COMP));
    final ClientComplexValue innerComplexProperty1 = complexProperty1.get(PROPERTY_COMP).getComplexValue();
    assertEquals("1", innerComplexProperty1.get(PROPERTY_STRING).getPrimitiveValue().toValue());
    assertTrue(innerComplexProperty1.get("PropertyBinary").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyBoolean").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyByte").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDate").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDateTimeOffset").hasNullValue());
    assertTrue(innerComplexProperty1.get(PROPERTY_DECIMAL).hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDouble").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyDuration").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyGuid").hasNullValue());
    assertTrue(innerComplexProperty1.get(PROPERTY_INT16).hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyInt32").hasNullValue());
    assertTrue(innerComplexProperty1.get(PROPERTY_INT64).hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertySByte").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertyTimeOfDay").hasNullValue());
    assertTrue(innerComplexProperty1.get("PropertySingle").hasNullValue());

    final ClientComplexValue complexProperty2 = iter.next().asComplex();
    assertTrue(complexProperty2.get(PROPERTY_INT16).hasNullValue());
    assertNotNull(complexProperty2.get(PROPERTY_COMP));
    final ClientComplexValue innerComplexProperty2 = complexProperty2.get(PROPERTY_COMP).getComplexValue();
    assertEquals("2", innerComplexProperty2.get(PROPERTY_STRING).getPrimitiveValue().toValue());
    assertShortOrInt(2, innerComplexProperty2.get(PROPERTY_INT16).getPrimitiveValue().toValue());
    assertEquals(isJson() ? 2.0 : (Number) 2.0F,
        innerComplexProperty2.get("PropertySingle").getPrimitiveValue().toValue());

    assertTrue(innerComplexProperty2.get("PropertyBinary").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyBoolean").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyByte").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDate").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDateTimeOffset").hasNullValue());
    assertTrue(innerComplexProperty2.get(PROPERTY_DECIMAL).hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDouble").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyDuration").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyGuid").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyInt32").hasNullValue());
    assertTrue(innerComplexProperty2.get(PROPERTY_INT64).hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertySByte").hasNullValue());
    assertTrue(innerComplexProperty2.get("PropertyTimeOfDay").hasNullValue());

    // Check if not available properties return null
    assertNull(innerComplexProperty2.get("NotAvailableProperty"));
  }

  @Test
  public void complexPropertyWithNotNullablePrimitiveValue() {
    // PropertyComp is null, but the primitive values in PropertyComp must not be null
    final ClientEntity entity = getFactory().newEntity(
        new FullQualifiedName(SERVICE_NAMESPACE, "ETMixPrimCollComp"));
    final URI targetURI = getEdmEnabledClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_MIX_PRIM_COLL_COMP).build();

    try {
      getEdmEnabledClient().getCUDRequestFactory().getEntityCreateRequest(targetURI, entity).execute();
      fail("Expecting bad request");
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void upsert() throws EdmPrimitiveTypeException {
    final ClientEntity entity = getFactory().newEntity(new FullQualifiedName(SERVICE_NAMESPACE, "ETTwoPrim"));
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString("Test")));

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM).appendKeySegment(33)
        .build();
    final ODataEntityUpdateResponse<ClientEntity> updateResponse =
        getEdmEnabledClient().getCUDRequestFactory().getEntityUpdateRequest(uri, UpdateType.PATCH, entity).execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), updateResponse.getStatusCode());
    assertEquals("Test", updateResponse.getBody().getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());

    final String cookie = updateResponse.getHeader(HttpHeader.SET_COOKIE).iterator().next();
    final Short key = updateResponse.getBody().getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toCastValue(Short.class);

    final ODataEntityRequest<ClientEntity> entityRequest = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(getEdmEnabledClient().newURIBuilder()
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(key)
            .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> responseEntityRequest = entityRequest.execute();
    assertEquals(HttpStatusCode.OK.getStatusCode(), responseEntityRequest.getStatusCode());
    assertEquals("Test", responseEntityRequest.getBody().getProperty(PROPERTY_STRING).getPrimitiveValue().toValue());
  }

  @Test
  public void updatePropertyWithNull() {
    final URI targetURI = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = getFactory().newEntity(ET_ALL_PRIM);
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString(null)));

    ODataEntityUpdateRequest<ClientEntity> request = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity);
    request.setPrefer(getClient().newPreferences().returnRepresentation());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertEquals("return=representation", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    assertTrue(response.getBody().getProperty(PROPERTY_STRING).hasNullValue());
    assertShortOrInt(34, response.getBody().getProperty(PROPERTY_DECIMAL).getPrimitiveValue().toValue());
  }

  @Test(expected = ODataClientErrorException.class)
  public void updatePropertyWithNullNotAllowed() {
    final URI targetURI = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(32767)
        .build();

    final ClientEntity entity = getFactory().newEntity(ET_KEY_NAV);
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_STRING,
        getFactory().newPrimitiveValueBuilder().buildString(null)));

    getEdmEnabledClient().getCUDRequestFactory().getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity).execute();
  }

  @Test
  public void updateMerge() {
    final URI targetURI = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .build();

    final ClientObjectFactory factory = getFactory();
    final ClientEntity entity = factory.newEntity(ET_KEY_NAV);
    entity.addLink(factory.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(factory.newEntitySetNavigationLink("NavPropertyETKeyNavMany", getClient().newURIBuilder
        (SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build()));
    entity.getProperties().add(factory.newCollectionProperty("CollPropertyString",
        factory.newCollectionValue("Edm.String").add(
            factory.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(factory.newComplexProperty(PROPERTY_COMP_ALL_PRIM,
        factory.newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
            .add(factory.newPrimitiveProperty(PROPERTY_STRING,
                factory.newPrimitiveValueBuilder().buildString("Changed")))));

    final ODataEntityUpdateResponse<ClientEntity> response = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.PATCH, entity)
        .execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(
            getEdmEnabledClient().newURIBuilder()
                .appendEntitySetSegment(ES_KEY_NAV)
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity()
        .getEntity()
        .getProperty(PROPERTY_INT16)
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
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(2)
        .getProperty(PROPERTY_INT16)
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
        .getProperty(PROPERTY_COMP_ALL_PRIM)
        .getComplexValue();

    assertEquals("Changed", complexValue.get(PROPERTY_STRING).getPrimitiveValue().toValue());
  }

  @Test
  public void updateReplace() {
    final ODataClient client = getClient();
    final URI targetURI = client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .build();

    final ClientObjectFactory factory = getFactory();
    final ClientEntity entity = factory.newEntity(ET_KEY_NAV);
    entity.addLink(factory.newEntityNavigationLink("NavPropertyETKeyNavOne", targetURI));
    entity.addLink(factory.newEntitySetNavigationLink("NavPropertyETKeyNavMany", client.newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(3).build()));
    entity.getProperties().add(factory.newPrimitiveProperty(PROPERTY_STRING, factory
        .newPrimitiveValueBuilder()
        .buildString("Must not be null")));
    entity.getProperties().add(factory.newComplexProperty("PropertyCompTwoPrim", factory.newComplexValue
        (SERVICE_NAMESPACE+".CTTwoPrim")
        .add(factory.newPrimitiveProperty(PROPERTY_STRING, factory.newPrimitiveValueBuilder()
            .buildString("Must not be null")))
        .add(factory.newPrimitiveProperty(PROPERTY_INT16,
            factory.newPrimitiveValueBuilder().buildInt16((short) 42)))));
    entity.getProperties().add(factory.newCollectionProperty("CollPropertyString",
        factory.newCollectionValue("Edm.String")
            .add(factory.newPrimitiveValueBuilder().buildString("Single entry!"))));
    entity.getProperties().add(factory.newComplexProperty(PROPERTY_COMP_ALL_PRIM,
        factory.newComplexValue(SERVICE_NAMESPACE+".CTAllPrim").add(
            factory.newPrimitiveProperty(PROPERTY_STRING,
                factory.newPrimitiveValueBuilder().buildString("Changed")))));

    ODataEntityUpdateRequest<ClientEntity> request = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(targetURI, UpdateType.REPLACE, entity);
    request.setPrefer(client.newPreferences().returnMinimal());
    final ODataEntityUpdateResponse<ClientEntity> response = request.execute();
    assertEquals(HttpStatusCode.NO_CONTENT.getStatusCode(), response.getStatusCode());
    assertEquals("return=minimal", response.getHeader(HttpHeader.PREFERENCE_APPLIED).iterator().next());
    final String cookie = response.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> entityRequest = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(
            getEdmEnabledClient().newURIBuilder()
                .appendEntitySetSegment(ES_KEY_NAV)
                .appendKeySegment(1)
                .expand("NavPropertyETKeyNavOne", "NavPropertyETKeyNavMany")
                .build());
    entityRequest.addCustomHeader(HttpHeader.COOKIE, cookie);
    final ODataRetrieveResponse<ClientEntity> entityResponse = entityRequest.execute();

    assertEquals(HttpStatusCode.OK.getStatusCode(), entityResponse.getStatusCode());
    assertShortOrInt(1, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavOne")
        .asInlineEntity()
        .getEntity()
        .getProperty(PROPERTY_INT16)
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
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(2, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(1)
        .getProperty(PROPERTY_INT16)
        .getPrimitiveValue()
        .toValue());

    assertShortOrInt(3, entityResponse.getBody().getNavigationLink("NavPropertyETKeyNavMany")
        .asInlineEntitySet()
        .getEntitySet()
        .getEntities()
        .get(2)
        .getProperty(PROPERTY_INT16)
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
        .getProperty(PROPERTY_COMP_ALL_PRIM)
        .getComplexValue();

    assertEquals("Changed", propCompAllPrim.get(PROPERTY_STRING).getPrimitiveValue().toValue());
    assertTrue(propCompAllPrim.get(PROPERTY_INT16).hasNullValue());
    assertTrue(propCompAllPrim.get("PropertyDate").hasNullValue());

    final ClientComplexValue propCompTwoPrim = entityResponse.getBody()
        .getProperty("PropertyCompTwoPrim")
        .getComplexValue();

    assertEquals("Must not be null", propCompTwoPrim.get(PROPERTY_STRING).getPrimitiveValue().toValue());
    assertShortOrInt(42, propCompTwoPrim.get(PROPERTY_INT16).getPrimitiveValue().toValue());

    assertNotNull(entityResponse.getBody().getProperty("PropertyCompNav").getComplexValue());
    assertTrue(entityResponse.getBody()
        .getProperty("PropertyCompNav")
        .getComplexValue()
        .get(PROPERTY_INT16)
        .hasNullValue());
  }

  @Test
  public void createEntityWithIEEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
        .appendKeySegment(32767).build();

    final ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(34))));
    newEntity.addLink(getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));

    final ODataEntityCreateRequest<ClientEntity> request = getEdmEnabledClient().getCUDRequestFactory()
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
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build();
    final URI linkURI = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_TWO_PRIM)
        .appendKeySegment(32767).build();

    final ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64(null)));
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildDecimal(null)));
    newEntity.addLink(getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE, linkURI));

    final ODataEntityCreateRequest<ClientEntity> request = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityCreateRequest(uri, newEntity);
    request.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityCreateResponse<ClientEntity> response = request.execute();

    assertTrue(response.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(response.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }

  @Test
  public void updateEntityWithIEEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();

    final ClientEntity entity = getFactory().newEntity(ET_ALL_PRIM);
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(Long.MAX_VALUE))));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();

    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
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
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();

    final ClientEntity entity = getFactory().newEntity(ET_ALL_PRIM);
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64(null)));
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildDecimal(null)));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataEntityUpdateResponse<ClientEntity> responseUpdate = requestUpdate.execute();

    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataEntityRequest<ClientEntity> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getEntityRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientEntity> responseGet = requestGet.execute();

    assertTrue(responseGet.getBody().getProperty(PROPERTY_INT64).hasNullValue());
    assertTrue(responseGet.getBody().getProperty(PROPERTY_DECIMAL).hasNullValue());
  }

  @Test
  public void updateEntityWithIEEE754CompatibleParameterWithNullString() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_ALL_PRIM).appendKeySegment(0).build();

    final ClientEntity entity = getFactory().newEntity(ET_ALL_PRIM);
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildString("null")));
    entity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
        getFactory().newPrimitiveValueBuilder().buildString("null")));

    final ODataEntityUpdateRequest<ClientEntity> requestUpdate = getEdmEnabledClient().getCUDRequestFactory()
        .getEntityUpdateRequest(uri, UpdateType.PATCH, entity);
    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);

    try {
      requestUpdate.execute();
      fail();
    } catch (ODataClientErrorException e) {
      assertEquals(HttpStatusCode.BAD_REQUEST.getStatusCode(), e.getStatusLine().getStatusCode());
    }
  }

  @Test
  public void updateEdmInt64PropertyWithIEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(0)
        .appendPropertySegment(PROPERTY_INT64).build();

    final ODataPropertyUpdateRequest requestUpdate =
        getEdmEnabledClient().getCUDRequestFactory().getPropertyPrimitiveValueUpdateRequest(uri,
            getFactory().newPrimitiveProperty(PROPERTY_INT64,
                getFactory().newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));

    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataPropertyRequest<ClientProperty> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();

    assertEquals(Long.MAX_VALUE, responseGet.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void updateComplexPropertyWithIEEE754CompatibleParamter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .appendPropertySegment(PROPERTY_COMP_ALL_PRIM).build();

    final ODataPropertyUpdateRequest requestUpdate = getEdmEnabledClient().getCUDRequestFactory()
        .getPropertyComplexValueUpdateRequest(uri, UpdateType.PATCH,
            getFactory().newComplexProperty(PROPERTY_COMP_ALL_PRIM,
                getFactory().newComplexValue(SERVICE_NAMESPACE+".CTAllPrim")
                    .add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
                        getFactory().newPrimitiveValueBuilder().buildInt64(Long.MIN_VALUE)))
                    .add(getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
                        getFactory().newPrimitiveValueBuilder().buildDecimal(BigDecimal.valueOf(12345678912L))))
                    .add(getFactory().newPrimitiveProperty(PROPERTY_INT16,
                        getFactory().newPrimitiveValueBuilder().buildInt16((short) 2)))));

    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataPropertyRequest<ClientProperty> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
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
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
        .appendKeySegment(0)
        .appendPropertySegment(PROPERTY_DECIMAL).build();

    final ODataPropertyUpdateRequest requestUpdate = getEdmEnabledClient().getCUDRequestFactory()
        .getPropertyPrimitiveValueUpdateRequest(uri,
            getFactory().newPrimitiveProperty(PROPERTY_DECIMAL,
                getFactory().newPrimitiveValueBuilder().buildInt64(Long.MAX_VALUE)));

    requestUpdate.setContentType(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    requestUpdate.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataPropertyUpdateResponse responseUpdate = requestUpdate.execute();
    String cookie = responseUpdate.getHeader(HttpHeader.SET_COOKIE).iterator().next();

    final ODataPropertyRequest<ClientProperty> requestGet = getEdmEnabledClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    requestGet.addCustomHeader(HttpHeader.COOKIE, cookie);
    requestGet.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    final ODataRetrieveResponse<ClientProperty> responseGet = requestGet.execute();

    assertEquals(BigDecimal.valueOf(Long.MAX_VALUE), responseGet.getBody().getPrimitiveValue().toValue());
  }

  @Test
  public void readESAllPrimCollectionWithIEEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM)
        .orderBy(PROPERTY_INT16)
        .build();

    ODataEntitySetRequest<ClientEntitySet> request = getEdmEnabledClient().getRetrieveRequestFactory()
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
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI)
        .appendEntitySetSegment(ES_KEY_NAV).appendKeySegment(1).build();

    ODataEntityRequest<ClientEntity> request = getEdmEnabledClient().getRetrieveRequestFactory().getEntityRequest(uri);
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
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .appendNavigationSegment(PROPERTY_COMP_ALL_PRIM)
        .build();
    ODataPropertyRequest<ClientProperty> request = getEdmEnabledClient().getRetrieveRequestFactory()
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
  @Ignore("The client does not recognize the IEEE754Compatible content-type parameter.")
  public void readEdmInt64PropertyWithIEEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
        .appendPropertySegment(PROPERTY_INT64)
        .build();
    ODataPropertyRequest<ClientProperty> request = getEdmEnabledClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    assertEquals(Long.MAX_VALUE, response.getBody().getPrimitiveValue().toValue());
  }

  @Test
  @Ignore("The client does not recognize the IEEE754Compatible content-type parameter.")
  public void readEdmDecimalPropertyWithIEEE754CompatibleParameter() {
    assumeTrue("There is no IEEE754Compatible content-type parameter in XML.", isJson());

    final URI uri = getClient().newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_KEY_NAV)
        .appendKeySegment(1)
        .appendPropertySegment(PROPERTY_COMP_ALL_PRIM)
        .appendPropertySegment(PROPERTY_DECIMAL)
        .build();
    ODataPropertyRequest<ClientProperty> request = getEdmEnabledClient().getRetrieveRequestFactory()
        .getPropertyRequest(uri);
    request.setAccept(CONTENT_TYPE_JSON_IEEE754_COMPATIBLE);
    setCookieHeader(request);
    final ODataRetrieveResponse<ClientProperty> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    assertEquals(BigDecimal.valueOf(34), response.getBody().getPrimitiveValue().toValue());
  }
  
  
  @Test
  public void test1Olingo1064() throws ODataDeserializerException {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    
    EdmEnabledODataClient odataClient = ODataClientFactory.getEdmEnabledClient(SERVICE_URI, edm, null);
    final InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("ESCompAllPrimWithValueForComplexProperty.json");
    ClientEntity entity = odataClient.getReader().readEntity(input, ContentType.JSON);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertNotNull(entity.getProperty(PROPERTY_COMP).getComplexValue());
    assertEquals("olingo.odata.test1.CTAllPrim", entity.getProperty(PROPERTY_COMP).getComplexValue().getTypeName());
    assertEquals(PROPERTY_COMP, entity.getProperty(PROPERTY_COMP).getName());
    assertNull(entity.getProperty(PROPERTY_COMP).getComplexValue().get("PropertyString").
        getPrimitiveValue().toValue());
    assertNull(entity.getProperty(PROPERTY_COMP).getComplexValue().get("PropertyBoolean").
        getPrimitiveValue().toValue());
  }
  
  @Test
  public void test2Olingo1064() throws ODataDeserializerException {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    
    EdmEnabledODataClient odataClient = ODataClientFactory.getEdmEnabledClient(SERVICE_URI, edm, null);
    final InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("ESCompAllPrimWithNullValueForComplexProperty.json");
    ClientEntity entity = odataClient.getReader().readEntity(input, ContentType.JSON);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());
    assertNotNull(entity.getProperty(PROPERTY_COMP).getComplexValue());
    assertEquals("olingo.odata.test1.CTAllPrim", entity.getProperty(PROPERTY_COMP).getComplexValue().getTypeName());
    assertEquals(PROPERTY_COMP, entity.getProperty(PROPERTY_COMP).getName());
    assertTrue(entity.getProperty(PROPERTY_COMP).hasNullValue());
  }
  
  @Test
  public void test3Olingo1064() throws ODataDeserializerException {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    
    EdmEnabledODataClient odataClient = ODataClientFactory.getEdmEnabledClient(SERVICE_URI, edm, null);
    final InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("ESCompAllPrimWithEmptyValueForComplexProperty.json");
    ClientEntity entity = odataClient.getReader().readEntity(input, ContentType.JSON);
    assertEquals("olingo.odata.test1.CTAllPrim", entity.getProperty(PROPERTY_COMP).getComplexValue().getTypeName());
    assertEquals(PROPERTY_COMP, entity.getProperty(PROPERTY_COMP).getName());
    assertTrue(entity.getProperty(PROPERTY_COMP).hasNullValue());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void test4Olingo1064() throws ODataDeserializerException {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    
    EdmEnabledODataClient odataClient = ODataClientFactory.getEdmEnabledClient(SERVICE_URI, edm, null);
    final InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("ESTwoKeyNavWithNestedComplexTypes.json");
    ClientEntity entity = odataClient.getReader().readEntity(input, ContentType.JSON);
    assertEquals("olingo.odata.test1.CTPrimComp", entity.getProperty(PROPERTY_COMP).getComplexValue().getTypeName());
    assertEquals(PROPERTY_COMP, entity.getProperty(PROPERTY_COMP).getName());
    Map<String, Object> map = entity.getProperty(PROPERTY_COMP).getComplexValue().asJavaMap();
    assertEquals(map.size(), 2);
    assertEquals(((Map<String, Object>)map.get(PROPERTY_COMP)).size(), 16);
    assertEquals("Collection(olingo.odata.test1.CTPrimComp)", entity.getProperty(COL_PROPERTY_COMP).
        getCollectionValue().getTypeName());
    assertEquals(0, entity.getProperty(COL_PROPERTY_COMP).getCollectionValue().size());
    assertEquals("olingo.odata.test1.CTNavFiveProp", entity.getProperty(PROPERTY_COMP_NAV).
        getComplexValue().getTypeName());
    assertEquals("olingo.odata.test1.CTTwoPrim", entity.getProperty(PROPERTY_COMP_TWO_PRIM).
        getComplexValue().getTypeName());
  }
  
  @Test
  public void testOLINGO975() throws ODataDeserializerException {
    EdmMetadataRequest request = getClient().getRetrieveRequestFactory().getMetadataRequest(SERVICE_URI);
    assertNotNull(request);
    setCookieHeader(request);    
    
    ODataRetrieveResponse<Edm> response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    Edm edm = response.getBody();
    
    EdmEnabledODataClient odataClient = ODataClientFactory.getEdmEnabledClient(SERVICE_URI, edm, null);
    final InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("OdataTypesInBaseAndDerivedTypes.json");
    ClientEntity entity = odataClient.getReader().readEntity(input, ContentType.JSON);
    assertEquals("NavPropertyETTwoPrimMany", entity.getNavigationLinks().get(0).getName());
    assertNotNull(entity.getNavigationLinks().get(0).asInlineEntitySet());
    assertEquals(2, entity.getNavigationLinks().get(0).asInlineEntitySet().getEntitySet().getEntities().size());
    assertEquals(3, entity.getNavigationLinks().get(0).asInlineEntitySet().getEntitySet().getEntities().get(0).
        getProperties().size());
    assertEquals(1, entity.getNavigationLinks().get(0).asInlineEntitySet().getEntitySet().getEntities().get(1).
        getProperties().size());
    assertEquals("#olingo.odata.test1.ETBase", entity.getNavigationLinks().get(0).asInlineEntitySet().getEntitySet().
        getEntities().get(0).getTypeName().toString());
    assertEquals("#olingo.odata.test1.ETBase", entity.getNavigationLinks().get(0).asInlineEntitySet().getEntitySet().
        getEntities().get(1).getTypeName().toString());
    assertEquals("olingo.odata.test1.ETAllPrim", entity.getTypeName().toString());
  }
  
  @Test
  public void readViaXmlMetadataAnnotation() throws URISyntaxException, IOException {
    InputStream input = Thread.currentThread().getContextClassLoader().
        getResourceAsStream("edmxWithCoreAnnotation.xml");
    final XMLMetadata metadata = getClient().getDeserializer(ContentType.APPLICATION_XML).toMetadata(input);
    String vocabUrl = metadata.getReferences().get(0).getUri().toString();
    vocabUrl = vocabUrl.substring(vocabUrl.indexOf("../") + 3);
    vocabUrl = SERVICE_ROOT_URL + vocabUrl;
    URI uri = new URI(vocabUrl);
    input.close();
    ODataRawRequest request = getClient().getRetrieveRequestFactory().getRawRequest(uri);
    assertNotNull(request);
    setCookieHeader(request);

    ODataRawResponse response = request.execute();
    saveCookieHeader(response);
    assertEquals(HttpStatusCode.OK.getStatusCode(), response.getStatusCode());

    List<InputStream> streams = new ArrayList<InputStream>();
    streams.add(response.getRawResponse());
    Edm edm = getClient().getReader().readMetadata(metadata, streams);
    assertNotNull(edm);
    final EdmEntityType person = edm.getEntityType(
        new FullQualifiedName("Microsoft.Exchange.Services.OData.Model", "Person"));
    assertNotNull(person);
    EdmProperty concurrency = (EdmProperty) person.getProperty("Concurrency");
    List<EdmAnnotation> annotations = concurrency.getAnnotations();
    for (EdmAnnotation annotation : annotations) {
      annotation.getExpression();
      EdmTerm term = annotation.getTerm();
      assertNotNull(term);
      assertEquals("Computed", term.getName());
      assertEquals("Org.OData.Core.V1.Computed",
          term.getFullQualifiedName().getFullQualifiedNameAsString());
      assertEquals(1, term.getAnnotations().size());
    }
    EdmProperty userName = (EdmProperty) person.getProperty("UserName");
    List<EdmAnnotation> userNameAnnotations = userName.getAnnotations();
    for (EdmAnnotation annotation : userNameAnnotations) {
      EdmTerm term = annotation.getTerm();
      assertNotNull(term);
      assertEquals("Permissions", term.getName());
      assertEquals("Org.OData.Core.V1.Permissions",
          term.getFullQualifiedName().getFullQualifiedNameAsString());
      org.apache.olingo.commons.api.edm.annotation.EdmExpression expression = annotation.getExpression();
      assertNotNull(expression);
      assertTrue(expression.isConstant());
      assertEquals("Org.OData.Core.V1.Permission/Read", expression.asConstant().getValueAsString());
      assertEquals("EnumMember", expression.getExpressionName());
  }
  }
  
  @Test
  public void issue1144() {
    FilterFactory filFactory = getClient().getFilterFactory();
    FilterArgFactory filArgFactory = filFactory.getArgFactory();
    URIFilter andFilExp = filFactory.and(filFactory.eq("d/olingo.odata.test1.CTBase/AdditionalPropString", "ADD TEST"), 
        filFactory.eq("d/olingo.odata.test1.CTBase/AdditionalPropString", "ADD TEST"));
    final URIFilter filter = filFactory.match(
        filArgFactory.any(filArgFactory.property("CollPropertyComp"), "d", andFilExp));
    String strFilter = filter.build();
    ODataEntitySetRequest<ClientEntitySet> request = getClient().getRetrieveRequestFactory()
        .getEntitySetRequest(getClient().newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_MIX_PRIM_COLL_COMP).filter(strFilter).build());
    assertNotNull(request);
    setCookieHeader(request);

    final ODataRetrieveResponse<ClientEntitySet> response = request.execute();
    saveCookieHeader(response);
    assertNotNull(response.getHeaderNames());
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
    final ClientProperty property = entity.getProperty("CollPropertyComp");
    assertNotNull(property);
    assertEquals(3, property.getCollectionValue().size());
  }
}
