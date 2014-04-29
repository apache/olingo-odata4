/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.EdmEnabledODataClient;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTestITCase {

  private void withInlineEntry(final ODataClient client, final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().
            getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertTrue(entity.getProperty("Home").hasPrimitiveValue());
    assertEquals("Edm.GeographyPoint", entity.getProperty("Home").getPrimitiveValue().getTypeName());
    assertEquals(testStaticServiceRootURL + "/Customers(1)", entity.getEditLink().toASCIIString());

    // In JSON with minimal metadata, links are not provided
    if (format == ODataPubFormat.ATOM || format == ODataPubFormat.JSON_FULL_METADATA) {
      assertEquals(3, entity.getNavigationLinks().size());

      if (ODataPubFormat.ATOM == format) {
        assertTrue(entity.getAssociationLinks().isEmpty());
        // In JSON, association links for each $ref link will exist.
      }

      boolean found = false;

      for (ODataLink link : entity.getNavigationLinks()) {
        if (link instanceof ODataInlineEntity) {
          final CommonODataEntity inline = ((ODataInlineEntity) link).getEntity();
          assertNotNull(inline);

          final List<? extends CommonODataProperty> properties = inline.getProperties();
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

  @Test
  public void withInlineEntryFromAtom() {
    withInlineEntry(client, ODataPubFormat.ATOM);
  }

  @Test
  public void withInlineEntryFromFullJSON() {
    withInlineEntry(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void withInlineEntryFromJSON() {
    withInlineEntry(edmClient, ODataPubFormat.JSON);
  }

  private void withInlineFeed(final ODataClient client, final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(1).expand("Orders");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().
            getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());

    // In JSON with minimal metadata, links are not provided
    if (format == ODataPubFormat.ATOM || format == ODataPubFormat.JSON_FULL_METADATA) {
      boolean found = false;
      for (ODataLink link : entity.getNavigationLinks()) {
        if (link instanceof ODataInlineEntitySet) {
          final CommonODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
          assertNotNull(inline);

          found = true;
        }
      }
      assertTrue(found);
    }
  }

  @Test
  public void withInlineFeedFromAtom() {
    withInlineFeed(client, ODataPubFormat.ATOM);
  }

  @Test
  public void withInlineFeedFromFullJSON() {
    withInlineFeed(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void withInlineFeedFromJSON() {
    withInlineFeed(edmClient, ODataPubFormat.JSON);
  }

  private void rawRequest(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5);

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ODataEntitySet> entitySet = res.getBodyAs(ODataEntitySet.class);
    assertNull(entitySet);

    final ResWrap<ODataEntity> entity = res.getBodyAs(ODataEntity.class);
    assertTrue(entity.getPayload().getReference().endsWith("/StaticService/V40/Static.svc/People(5)"));
  }

  @Test
  public void rawRequestAsAtom() {
    rawRequest(ODataPubFormat.ATOM);
  }

  @Test
  public void rawRequestAsJSON() {
    // this needs to be full, otherwise reference will not be provided
    rawRequest(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void multiKey(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("ProductID", "6");
    multiKey.put("ProductDetailID", 1);

    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("ProductDetails").appendKeySegment(multiKey);

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals(Integer.valueOf(1),
            entity.getProperty("ProductDetailID").getPrimitiveValue().toCastValue(Integer.class));
  }

  @Test
  public void multiKeyAsAtom() throws EdmPrimitiveTypeException {
    multiKey(ODataPubFormat.ATOM);
  }

  @Test
  public void multiKeyAsJSON() throws EdmPrimitiveTypeException {
    multiKey(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void checkForETag(final ODataClient client, final ODataPubFormat format) {
    final URIBuilder uriBuilder =
            client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Orders").appendKeySegment(8);

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final String etag = res.getETag();
    assertTrue(StringUtils.isNotBlank(etag));

    final ODataEntity order = res.getBody();
    assertEquals(etag, order.getETag());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Order", order.getTypeName().toString());
    assertEquals("Edm.Int32", order.getProperty("OrderID").getPrimitiveValue().getTypeName());
    assertEquals("Edm.DateTimeOffset", order.getProperty("OrderDate").getPrimitiveValue().getTypeName());
    assertEquals("Edm.Duration", order.getProperty("ShelfLife").getPrimitiveValue().getTypeName());
    assertEquals("Collection(Edm.Duration)", order.getProperty("OrderShelfLifes").getCollectionValue().getTypeName());
  }

  @Test
  public void checkForETagAsATOM() {
    checkForETag(client, ODataPubFormat.ATOM);
  }

  @Test
  public void checkForETagAsFullJSON() {
    checkForETag(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void checkForETagAsJSON() {
    checkForETag(edmClient, ODataPubFormat.JSON);
  }

  @Test(expected = IllegalArgumentException.class)
  public void issue99() {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Orders");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.JSON);

    // this statement should cause an IllegalArgumentException bearing JsonParseException
    // since we are attempting to parse an EntitySet as if it was an Entity
    req.execute().getBody();
  }

  private void reference(final ODataPubFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Orders").appendKeySegment(8).appendNavigationSegment("CustomerForOrder").
            appendRefSegment();

    ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertNotNull(res);

    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertTrue(entity.getReference().endsWith("/StaticService/V40/Static.svc/Customers(PersonID=1)"));

    final URI referenceURI =
            client.getURIBuilder(testStaticServiceRootURL).appendEntityIdSegment(entity.getReference()).build();

    req = client.getRetrieveRequestFactory().getEntityRequest(referenceURI);
    req.setFormat(format);

    res = req.execute();
    assertNotNull(res);
    assertNotNull(res.getBody());
  }

  @Test
  public void atomReference() {
    reference(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonReference() {
    reference(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void contained(final ODataClient client, final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final URI uri = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101).
            appendNavigationSegment("MyPaymentInstruments").appendKeySegment(101901).build();
    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(format);

    final ODataEntity contained = req.execute().getBody();
    assertNotNull(contained);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.PaymentInstrument", contained.getTypeName().toString());
    assertEquals(101901,
            contained.getProperty("PaymentInstrumentID").getPrimitiveValue().toCastValue(Integer.class), 0);
    assertEquals("Edm.DateTimeOffset", contained.getProperty("CreatedDate").getPrimitiveValue().getTypeName());
  }

  @Test
  public void containedFromAtom() throws EdmPrimitiveTypeException {
    contained(client, ODataPubFormat.ATOM);
  }

  @Test
  public void containedFromFullJSON() throws EdmPrimitiveTypeException {
    contained(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void containedFromJSON() throws EdmPrimitiveTypeException {
    contained(edmClient, ODataPubFormat.JSON);
  }

  private void entitySetNavigationLink(final ODataClient client, final ODataPubFormat format) {
    final URI uri = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Accounts").appendKeySegment(101).build();
    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uri);
    req.setFormat(format);

    final ODataEntity entity = req.execute().getBody();
    assertNotNull(entity);

    // With JSON, entity set navigation links are only recognizable via Edm
    if (format == ODataPubFormat.ATOM || client instanceof EdmEnabledODataClient) {
      assertEquals(ODataLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("MyPaymentInstruments").getType());
      assertEquals(ODataLinkType.ENTITY_SET_NAVIGATION, entity.getNavigationLink("ActiveSubscriptions").getType());
    }
  }

  @Test
  public void entitySetNavigationLinkFromAtom() {
    entitySetNavigationLink(client, ODataPubFormat.ATOM);
  }

  @Test
  public void entitySetNavigationLinkFromJSON() {
    // only JSON_FULL_METADATA has links, only Edm can recognize entity set navigation
    entitySetNavigationLink(edmClient, ODataPubFormat.JSON_FULL_METADATA);
  }

}
