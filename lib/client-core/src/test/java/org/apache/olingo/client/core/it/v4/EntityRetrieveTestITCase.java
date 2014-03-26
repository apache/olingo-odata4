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
package org.apache.olingo.client.core.it.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.ODataProperty;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.core.op.ResourceFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * This is the unit test class to check entity retrieve operations.
 */
public class EntityRetrieveTestITCase extends AbstractTestITCase {

  protected String getServiceRoot() {
    return testStaticServiceRootURL;
  }

  private void withInlineEntry(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("#Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getName());
    assertEquals(getServiceRoot() + "/Customers(PersonID=1)", entity.getEditLink().toASCIIString());

    assertEquals(3, entity.getNavigationLinks().size());
    assertTrue(entity.getAssociationLinks().isEmpty());

    boolean found = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if (link instanceof ODataInlineEntity) {
        final ODataEntity inline = ((ODataInlineEntity) link).getEntity();
        assertNotNull(inline);

        debugEntry(client.getBinder().getEntry(
                inline, ResourceFactory.entryClassForFormat(format == ODataPubFormat.ATOM)), "Just read");

        final List<ODataProperty> properties = inline.getProperties();
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

  @Test
  public void withInlineEntryFromAtom() {
    withInlineEntry(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void withInlineEntryFromJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineEntry(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void withInlineFeed(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customers").appendKeySegment(1).expand("Orders");

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);

    boolean found = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if (link instanceof ODataInlineEntitySet) {
        final ODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
        assertNotNull(inline);

        debugFeed(client.getBinder().getFeed(inline, ResourceFactory.feedClassForFormat(
                format == ODataPubFormat.ATOM)), "Just read");

        found = true;
      }
    }

    assertTrue(found);
  }

  @Test
  public void withInlineFeedFromAtom() {
    withInlineFeed(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void withInlineFeedFromJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineFeed(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void rawRequest(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("People").appendKeySegment(5);

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ODataEntitySet entitySet = res.getBodyAs(ODataEntitySet.class);
    assertNull(entitySet);

    final ODataEntity entity = res.getBodyAs(ODataEntity.class);
    assertTrue(entity.getReference().endsWith("/StaticService/V40/Static.svc/People(5)"));
  }

  @Test
  public void rawRequestAsAtom() {
    rawRequest(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void rawRequestAsJSON() {
    // this needs to be full, otherwise actions will not be provided
    rawRequest(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void multiKey(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("ProductID", "6");
    multiKey.put("ProductDetailID", 1);

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("ProductDetails").appendKeySegment(multiKey);

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
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
  @Ignore
  public void multiKeyAsJSON() throws EdmPrimitiveTypeException {
    multiKey(ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void checkForETagAsATOM() {
    checkForETag(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void checkForETagAsJSON() {
    checkForETag(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void checkForETag(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder =
            client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Orders").appendKeySegment(8);

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final String etag = res.getEtag();
    assertTrue(StringUtils.isNotBlank(etag));

    final ODataEntity order = res.getBody();
    assertEquals(etag, order.getETag());
  }

  @Test(expected = IllegalArgumentException.class)
  @Ignore
  public void issue99() {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Car");

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.JSON);

    // this statement should cause an IllegalArgumentException bearing JsonParseException
    // since we are attempting to parse an EntitySet as if it was an Entity
    req.execute().getBody();
  }

  @Test
  public void retrieveEntityReferenceAsAtom() {
    retrieveEntityReference(ODataPubFormat.ATOM);
  }

  @Test
  @Ignore
  public void retrieveEntityReferenceAsJSON() {
    retrieveEntityReference(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void retrieveEntityReference(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Orders").appendKeySegment(8).appendNavigationSegment("CustomerForOrder").
            appendRefSegment();

    final ODataEntityRequest req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertNotNull(res);

    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertTrue(entity.getReference().endsWith("/StaticService/V40/Static.svc/Customers(PersonID=1)"));
  }
}
