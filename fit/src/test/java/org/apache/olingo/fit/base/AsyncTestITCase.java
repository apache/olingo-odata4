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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;

import org.apache.olingo.client.api.communication.request.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInlineEntity;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Ignore;
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  private void withInlineEntry(final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(contentType);

    final AsyncRequestWrapper<ODataRetrieveResponse<ClientEntity>> async =
        client.getAsyncRequestFactory().<ODataRetrieveResponse<ClientEntity>> getAsyncRequestWrapper(req);

    final AsyncResponseWrapper<ODataRetrieveResponse<ClientEntity>> responseWrapper = async.execute();

    assertFalse(responseWrapper.isPreferenceApplied());

    final ODataRetrieveResponse<ClientEntity> res = responseWrapper.getODataResponse();
    final ClientEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertEquals(testStaticServiceRootURL + "/Customers(1)", entity.getEditLink().toASCIIString());

    assertEquals(3, entity.getNavigationLinks().size());

    if (ContentType.APPLICATION_ATOM_XML.equals(contentType)) {
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

  @Ignore
  @Test
  public void withInlineEntryAsAtom() {
    withInlineEntry(ContentType.APPLICATION_ATOM_XML);
  }

  @Ignore
  @Test
  public void withInlineEntryAsJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineEntry(ContentType.JSON_FULL_METADATA);
  }

  private void asyncOrders(final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("async").appendEntitySetSegment("Orders");

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(contentType);

    final AsyncRequestWrapper<ODataRetrieveResponse<ClientEntitySet>> async =
        client.getAsyncRequestFactory().<ODataRetrieveResponse<ClientEntitySet>> getAsyncRequestWrapper(req);
    async.callback(URI.create("http://client.service.it/callback/endpoint"));

    final AsyncResponseWrapper<ODataRetrieveResponse<ClientEntitySet>> responseWrapper = async.execute();

    assertTrue(responseWrapper.isPreferenceApplied());
    assertTrue(responseWrapper.isDone());

    final ODataRetrieveResponse<ClientEntitySet> res = responseWrapper.getODataResponse();
    final ClientEntitySet entitySet = res.getBody();

    assertFalse(entitySet.getEntities().isEmpty());
  }

  @Test
  public void asyncOrdersAsAtom() {
    asyncOrders(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void asyncOrdersAsJSON() {
    asyncOrders(ContentType.JSON);
  }
}
