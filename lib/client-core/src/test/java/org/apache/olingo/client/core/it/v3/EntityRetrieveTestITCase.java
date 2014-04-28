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
package org.apache.olingo.client.core.it.v3;

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
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataEntitySet;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataInlineEntitySet;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.v3.ODataEntity;
import org.apache.olingo.commons.api.domain.v3.ODataEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.commons.core.op.ResourceFactory;
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
            appendEntitySetSegment("Customer").appendKeySegment(-10).expand("Info");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.AstoriaDefaultService.Customer", entity.getTypeName().toString());
    assertEquals(getServiceRoot() + "/Customer(-10)", entity.getEditLink().toASCIIString());

    assertEquals(5, entity.getNavigationLinks().size());
    assertTrue(entity.getAssociationLinks().isEmpty());

    boolean found = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if (link instanceof ODataInlineEntity) {
        final CommonODataEntity inline = ((ODataInlineEntity) link).getEntity();
        assertNotNull(inline);

        debugEntity(client.getBinder().getEntity(
                inline, ResourceFactory.entityClassForFormat(format == ODataPubFormat.ATOM)), "Just read");

        final List<? extends CommonODataProperty> properties = inline.getProperties();
        assertEquals(2, properties.size());

        assertTrue(properties.get(0).getName().equals("CustomerInfoId")
                || properties.get(1).getName().equals("CustomerInfoId"));
        assertTrue(properties.get(0).getValue().toString().equals("11")
                || properties.get(1).getValue().toString().equals("11"));

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
  public void withInlineEntryFromJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineEntry(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void withInlineFeed(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Customer").appendKeySegment(-10).expand("Orders");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);

    boolean found = false;

    for (ODataLink link : entity.getNavigationLinks()) {
      if (link instanceof ODataInlineEntitySet) {
        final CommonODataEntitySet inline = ((ODataInlineEntitySet) link).getEntitySet();
        assertNotNull(inline);

        debugEntitySet(client.getBinder().getEntitySet(inline, ResourceFactory.entitySetClassForFormat(
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
  public void withInlineFeedFromJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineFeed(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void rawRequest(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Car").appendKeySegment(16);

    final ODataRawRequest req = client.getRetrieveRequestFactory().getRawRequest(uriBuilder.build());
    req.setFormat(format.toString(client.getServiceVersion()));

    final ODataRawResponse res = req.execute();
    assertNotNull(res);

    final ResWrap<ODataEntitySet> entitySet = res.getBodyAs(ODataEntitySet.class);
    assertNull(entitySet);

    final ResWrap<ODataEntity> entity = res.getBodyAs(ODataEntity.class);
    assertNotNull(entity.getPayload());
  }

  @Test
  public void rawRequestAsAtom() {
    rawRequest(ODataPubFormat.ATOM);
  }

  @Test
  public void rawRequestAsJSON() {
    // this needs to be full, otherwise actions will not be provided
    rawRequest(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void multiKey(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final LinkedHashMap<String, Object> multiKey = new LinkedHashMap<String, Object>();
    multiKey.put("FromUsername", "1");
    multiKey.put("MessageId", -10);

    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).
            appendEntitySetSegment("Message").appendKeySegment(multiKey);

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    final ODataEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals("1", entity.getProperty("FromUsername").getPrimitiveValue().toCastValue(String.class));
  }

  @Test
  public void multiKeyAsAtom() throws EdmPrimitiveTypeException {
    multiKey(ODataPubFormat.ATOM);
  }

  @Test
  public void multiKeyAsJSON() throws EdmPrimitiveTypeException {
    multiKey(ODataPubFormat.JSON_FULL_METADATA);
  }

  @Test
  public void checkForETagAsATOM() {
    checkForETag(ODataPubFormat.ATOM);
  }

  @Test
  public void checkForETagAsJSON() {
    checkForETag(ODataPubFormat.JSON_FULL_METADATA);
  }

  private void checkForETag(final ODataPubFormat format) {
    final CommonURIBuilder<?> uriBuilder =
            client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Product").appendKeySegment(-10);

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataRetrieveResponse<ODataEntity> res = req.execute();
    assertEquals(200, res.getStatusCode());

    final String etag = res.getETag();
    assertTrue(StringUtils.isNotBlank(etag));

    final CommonODataEntity product = res.getBody();
    assertEquals(etag, product.getETag());
  }

  @Test(expected = IllegalArgumentException.class)
  public void issue99() {
    final CommonURIBuilder<?> uriBuilder = client.getURIBuilder(getServiceRoot()).appendEntitySetSegment("Car");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ODataPubFormat.JSON);

    // this statement should cause an IllegalArgumentException bearing JsonParseException
    // since we are attempting to parse an EntitySet as if it was an Entity
    req.execute().getBody();
  }
}
