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
package org.apache.olingo.fit.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientInlineEntitySet;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientLinkType;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTestITCase {

  private void withInlineEntity(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().
        getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertTrue(entity.getProperty("Home").hasPrimitiveValue());
    assertEquals("Edm.GeographyPoint", entity.getProperty("Home").getPrimitiveValue().getTypeName());

    // In JSON with minimal metadata, links are not provided
    if (contentType.isCompatible(ContentType.APPLICATION_ATOM_SVC)
        || contentType.isCompatible(ContentType.APPLICATION_ATOM_XML) 
        || (contentType.isCompatible(ContentType.JSON) 
              && ContentType.VALUE_ODATA_METADATA_FULL
              .equals(contentType.getParameter(ContentType.PARAMETER_ODATA_METADATA)))) {
      assertEquals(testStaticServiceRootURL + "/Customers(1)", entity.getEditLink().toASCIIString());
      assertEquals(3, entity.getNavigationLinks().size());

      if (ContentType.APPLICATION_ATOM_XML == contentType) {
        assertTrue(entity.getAssociationLinks().isEmpty());
        // In JSON, association links for each $ref link will exist.
      }

      boolean found = false;

      for (ClientLink link : entity.getNavigationLinks()) {
        if (link instanceof ClientInlineEntity) {
          final ClientEntity inline = ((ClientInlineEntity) link).getEntity();
          assertNotNull(inline);

          final List<? extends ClientProperty> properties = inline.getProperties();
          assertEquals(5, properties.size());

          assertTrue(properties.get(0).getName().equals("CompanyID")
              || properties.get(1).getName().equals("CompanyID")
              || properties.get(2).getName().equals("CompanyID")
              || properties.get(3).getName().equals("CompanyID")
              || properties.get(4).getName().equals("CompanyID"));
          assertTrue(properties.get(0).getValue().toString().equals("0")
              || properties.get(1).getValue().toString().equals("0")
              || properties.get(2).getValue().toString().equals("0")
              || properties.get(3).getValue().toString().equals("0")
              || properties.get(4).getValue().toString().equals("0"));

          found = true;
        }
      }

      assertTrue(found);
    }
  }

  @Ignore
  @Test
  public void withInlineEntityFromAtom() {
    withInlineEntity(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void withInlineEntityFromFullJSON() {
    withInlineEntity(client, ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void withInlineEntityFromJSON() {
    withInlineEntity(edmClient, ContentType.JSON);
  }

  private void withInlineEntitySet(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Orders");

    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().
        getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());

    // In JSON with minimal metadata, links are not provided
    if (contentType.equals(ContentType.APPLICATION_ATOM_XML) || contentType.equals(ContentType.JSON_FULL_METADATA)) {
      boolean found = false;
      for (ClientLink link : entity.getNavigationLinks()) {
        if (link instanceof ClientInlineEntitySet) {
          final ClientEntitySet inline = ((ClientInlineEntitySet) link).getEntitySet();
          assertNotNull(inline);

          found = true;
        }
      }
      assertTrue(found);
    }
  }

  @Ignore
  @Test
  public void withInlineEntitySetFromAtom() {
    withInlineEntitySet(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void withInlineEntitySetFromFullJSON() {
    withInlineEntitySet(client, ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void withInlineEntitySetFromJSON() {
    withInlineEntitySet(edmClient, ContentType.JSON);
  }

  private void rawRequest(final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5);

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(contentType.toContentTypeString());

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ClientEntitySet> entitySet = res.getBodyAs(ClientEntitySet.class);
    assertNull(entitySet);

    final ResWrap<ClientEntity> entity = res.getBodyAs(ClientEntity.class);
    assertTrue(entity.getPayload().getId().toASCIIString().endsWith("/StaticService/V40/Static.svc/People(5)"));
  }

  @Test
  public void rawRequestAsAtom() {
    rawRequest(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void rawRequestAsJSON() {
    // this needs to be full, otherwise reference will not be provided
    rawRequest(ContentType.JSON_FULL_METADATA);
  }

  private void multiKey(final ContentType contentType) throws EdmPrimitiveTypeException {
    final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("ProductID", "6");
    multiKey.put("ProductDetailID", 1);

    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("ProductDetails").appendKeySegment(multiKey);

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals(Integer.valueOf(1),
        entity.getProperty("ProductDetailID").getPrimitiveValue().toCastValue(Integer.class));
  }

  @Test
  public void multiKeyAsAtom() throws EdmPrimitiveTypeException {
    multiKey(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void multiKeyAsJSON() throws EdmPrimitiveTypeException {
    multiKey(ContentType.JSON_FULL_METADATA);
  }

  private void checkForETag(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder =
        client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Orders").appendKeySegment(8);

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final String etag = res.getETag();
    assertTrue(StringUtils.isNotBlank(etag));

    final ClientEntity order = res.getBody();
    assertEquals(etag, order.getETag());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Order", order.getTypeName().toString());
    assertEquals("Edm.Int32", order.getProperty("OrderID").getPrimitiveValue().getTypeName());
    assertEquals("Edm.DateTimeOffset", order.getProperty("OrderDate").getPrimitiveValue().getTypeName());
    assertEquals("Edm.Duration", order.getProperty("ShelfLife").getPrimitiveValue().getTypeName());
    assertEquals("Collection(Edm.Duration)", order.getProperty("OrderShelfLifes").getCollectionValue().getTypeName());
  }

  @Test
  public void checkForETagAsAtom() {
    checkForETag(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void checkForETagAsFullJSON() {
    checkForETag(client, ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void checkForETagAsJSON() {
    checkForETag(edmClient, ContentType.JSON);
  }

  @Test(expected = IllegalArgumentException.class)
  public void issue99() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Orders");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON);

    // this statement should cause an IllegalArgumentException bearing JsonParseException
    // since we are attempting to parse an EntitySet as if it was an Entity
    req.execute().getBody();
  }

  private void reference(final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Orders").appendKeySegment(8).appendNavigationSegment("CustomerForOrder").
        appendRefSegment();

    ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertNotNull(res);

    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertTrue(entity.getId().toASCIIString().endsWith("/StaticService/V40/Static.svc/Customers(PersonID=1)"));

    final URI referenceURI = client.newURIBuilder(testStaticServiceRootURL).
        appendEntityIdSegment(entity.getId().toASCIIString()).build();

    req = client.getRetrieveRequestFactory().getEntityRequest(referenceURI);
    req.setFormat(contentType);

    res = req.execute();
    assertNotNull(res);
    assertNotNull(res.getBody());
  }

  @Test
  public void atomReference() {
    reference(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonReference() {
    reference(ContentType.JSON_FULL_METADATA);
  }

  private void contained(final ODataClient client, final ContentType contentType) throws EdmPrimitiveTypeException {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).
        appendNavigationSegment("MyPaymentInstruments").appendKeySegment(101902).build();
    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(contentType);

    final ClientEntity contained = req.execute().getBody();
    assertNotNull(contained);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument", contained.getTypeName().toString());
    assertEquals(101902,
        contained.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
    assertEquals("Edm.DateTimeOffset", contained.getProperty("CreatedDate").getPrimitiveValue().getTypeName());
    assertNotNull(contained.getProperty("CreatedDate").getPrimitiveValue().toCastValue(Timestamp.class));
  }

  @Test
  public void containedFromAtom() throws EdmPrimitiveTypeException {
    contained(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void containedFromFullJSON() throws EdmPrimitiveTypeException {
    contained(client, ContentType.JSON_FULL_METADATA);
  }

  @Test
  public void containedFromJSON() throws EdmPrimitiveTypeException {
    contained(edmClient, ContentType.JSON);
  }

  private void entitySetNavigationLink(final ODataClient client, final ContentType contentType) {
    final URI uri = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Accounts").appendKeySegment(101).build();
    final ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(contentType);

    final ClientEntity entity = req.execute().getBody();
    assertNotNull(entity);

    // With JSON, entity set navigation links are only recognizable via Edm
    if (contentType.equals(ContentType.APPLICATION_ATOM_XML) || client instanceof EdmEnabledODataClient) {
      assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("MyPaymentInstruments").getType());
      assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("ActiveSubscriptions").getType());
    }
  }

  @Test
  public void entitySetNavigationLinkFromAtom() {
    entitySetNavigationLink(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void entitySetNavigationLinkFromJSON() {
    // only JSON_FULL_METADATA has links, only Edm can recognize entity set navigation
    entitySetNavigationLink(edmClient, ContentType.JSON_FULL_METADATA);
  }

}
