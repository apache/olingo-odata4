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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.request.v4.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.communication.response.v4.AsyncResponseWrapper;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.commons.api.domain.CommonODataProperty;
import org.apache.olingo.commons.api.domain.ODataInlineEntity;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.domain.v4.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class AsyncTestITCase extends AbstractTestITCase {

  @Test
  public void clientAsync() throws InterruptedException, ExecutionException {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers");
    final Future<ODataRetrieveResponse<ODataEntitySet>> futureRes =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build()).asyncExecute();
    assertNotNull(futureRes);

    while (!futureRes.isDone()) {
      Thread.sleep(1000L);
    }

    final ODataRetrieveResponse<ODataEntitySet> res = futureRes.get();
    assertNotNull(res);
    assertEquals(200, res.getStatusCode());
    assertFalse(res.getBody().getEntities().isEmpty());
  }

  private void withInlineEntry(final ODataFormat format) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ODataEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(format);

    final AsyncRequestWrapper<ODataRetrieveResponse<ODataEntity>> async =
        client.getAsyncRequestFactory().<ODataRetrieveResponse<ODataEntity>> getAsyncRequestWrapper(req);

    final AsyncResponseWrapper<ODataRetrieveResponse<ODataEntity>> responseWrapper = async.execute();

    assertFalse(responseWrapper.isPreferenceApplied());

    final ODataRetrieveResponse<ODataEntity> res = responseWrapper.getODataResponse();
    final ODataEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertEquals(testStaticServiceRootURL + "/Customers(1)", entity.getEditLink().toASCIIString());

    assertEquals(3, entity.getNavigationLinks().size());

    if (ODataFormat.ATOM == format) {
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

  @Test
  public void withInlineEntryAsAtom() {
    withInlineEntry(ODataFormat.ATOM);
  }

  @Test
  public void withInlineEntryAsJSON() {
    // this needs to be full, otherwise there is no mean to recognize links
    withInlineEntry(ODataFormat.JSON_FULL_METADATA);
  }

  private void asyncOrders(final ODataFormat format) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("async").appendEntitySetSegment("Orders");

    final ODataEntitySetRequest<ODataEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(format);

    final AsyncRequestWrapper<ODataRetrieveResponse<ODataEntitySet>> async =
        client.getAsyncRequestFactory().<ODataRetrieveResponse<ODataEntitySet>> getAsyncRequestWrapper(req);
    async.callback(URI.create("http://client.service.it/callback/endpoint"));

    final AsyncResponseWrapper<ODataRetrieveResponse<ODataEntitySet>> responseWrapper = async.execute();

    assertTrue(responseWrapper.isPreferenceApplied());
    assertTrue(responseWrapper.isDone());

    final ODataRetrieveResponse<ODataEntitySet> res = responseWrapper.getODataResponse();
    final ODataEntitySet entitySet = res.getBody();

    assertFalse(entitySet.getEntities().isEmpty());
  }

  @Test
  public void asyncOrdersAsAtom() {
    asyncOrders(ODataFormat.ATOM);
  }

  @Test
  public void asyncOrdersAsJSON() {
    asyncOrders(ODataFormat.JSON);
  }
}
