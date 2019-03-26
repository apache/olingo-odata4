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

import org.apache.olingo.client.api.communication.request.AsyncRequestFactory;
import org.apache.olingo.client.api.communication.request.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataDeltaRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.junit.Test;

/**
 * 13.2 Interoperable OData Clients
 * <br />
 * http://docs.oasis-open.org/odata/odata/v4.0/os/part1-protocol/odata-v4.0-os-part1-protocol.html#_Toc372793762
 */
public class ConformanceTestITCase extends AbstractTestITCase {

  /**
   * 4. MUST follow redirects (section 9.1.5).
   */
  @Test
  public void item4() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("redirect").
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    assertEquals("4.0", req.getHeader(HttpHeader.ODATA_MAX_VERSION));

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    final ClientEntity entity = res.getBody();

    assertNotNull(entity);
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Customer", entity.getTypeName().toString());
    assertTrue(entity.getProperty("Home").hasPrimitiveValue());
    assertEquals("Edm.GeographyPoint", entity.getProperty("Home").getPrimitiveValue().getTypeName());
  }

  /**
   * 6. MUST support instances returning properties and navigation properties not specified in metadata (section 11.2).
   */
  @Test
  public void item6() {
    final Integer id = 2000;

    ClientEntity rowIndex = getClient().getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.OpenTypesServiceV4.RowIndex"));
    getClient().getBinder().add(rowIndex,
        getClient().getObjectFactory().newPrimitiveProperty("Id",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt32(id)));

    // add property not in metadata
    getClient().getBinder().add(rowIndex,
        getClient().getObjectFactory().newPrimitiveProperty("aString",
            getClient().getObjectFactory().newPrimitiveValueBuilder().buildString("string")));

    // add navigation property not in metadata
    rowIndex.addLink(client.getObjectFactory().newEntityNavigationLink(
        "Row", URI.create(testOpenTypeServiceRootURL + "/Row(71f7d0dc-ede4-45eb-b421-555a2aa1e58f)")));

    final ODataEntityCreateRequest<ClientEntity> createReq = getClient().getCUDRequestFactory().
        getEntityCreateRequest(getClient().newURIBuilder(testOpenTypeServiceRootURL).
            appendEntitySetSegment("RowIndex").build(), rowIndex);

    final ODataEntityCreateResponse<ClientEntity> createRes = createReq.execute();
    assertEquals(201, createRes.getStatusCode());

    final URIBuilder builder = getClient().newURIBuilder(testOpenTypeServiceRootURL).
        appendEntitySetSegment("RowIndex").appendKeySegment(id);

    rowIndex = read(ContentType.JSON_FULL_METADATA, builder.build());
    assertNotNull(rowIndex);
    assertEquals(EdmPrimitiveTypeKind.Int32, rowIndex.getProperty("Id").getPrimitiveValue().getTypeKind());
    assertEquals(EdmPrimitiveTypeKind.String, rowIndex.getProperty("aString").getPrimitiveValue().getTypeKind());
    assertNotNull(rowIndex.getNavigationLink("Row"));

    final ODataDeleteResponse deleteRes = getClient().getCUDRequestFactory().
        getDeleteRequest(rowIndex.getEditLink()).execute();
    assertEquals(204, deleteRes.getStatusCode());
  }

  /**
   * 10. MAY support deleted entities, link entities, deleted link entities in a delta response (section 11.3).
   */
  @Test
  public void item10() {
    final ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(
        client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customers").build());
    req.setPrefer(client.newPreferences().trackChanges());

    final ClientEntitySet customers = req.execute().getBody();
    assertNotNull(customers);
    assertNotNull(customers.getDeltaLink());

    final ODataDeltaRequest deltaReq = client.getRetrieveRequestFactory().getDeltaRequest(customers.getDeltaLink());
    deltaReq.setFormat(ContentType.JSON_FULL_METADATA);

    final ClientDelta delta = deltaReq.execute().getBody();
    assertNotNull(delta);

    assertNotNull(delta.getDeltaLink());
    assertTrue(delta.getDeltaLink().isAbsolute());
    assertEquals(5, delta.getCount(), 0);

    assertEquals(1, delta.getDeletedEntities().size());
    assertTrue(delta.getDeletedEntities().get(0).getId().isAbsolute());
    assertTrue(delta.getDeletedEntities().get(0).getId().toASCIIString().endsWith("Customers('ANTON')"));

    assertEquals(1, delta.getAddedLinks().size());
    assertTrue(delta.getAddedLinks().get(0).getSource().isAbsolute());
    assertTrue(delta.getAddedLinks().get(0).getSource().toASCIIString().endsWith("Customers('BOTTM')"));
    assertEquals("Orders", delta.getAddedLinks().get(0).getRelationship());

    assertEquals(1, delta.getDeletedLinks().size());
    assertTrue(delta.getDeletedLinks().get(0).getSource().isAbsolute());
    assertTrue(delta.getDeletedLinks().get(0).getSource().toASCIIString().endsWith("Customers('ALFKI')"));
    assertEquals("Orders", delta.getDeletedLinks().get(0).getRelationship());

    assertEquals(2, delta.getEntities().size());
    ClientProperty property = delta.getEntities().get(0).getProperty("ContactName");
    assertNotNull(property);
    assertTrue(property.hasPrimitiveValue());
    property = delta.getEntities().get(1).getProperty("ShippingAddress");
    assertNotNull(property);
    assertTrue(property.hasComplexValue());
  }

  
  /**
   * 10. MAY support deleted entities, link entities, deleted link entities in a delta response for asynch req.
   */
  @Test
  public void itemAsynch10() {

    final ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(
        client.newURIBuilder(testStaticServiceRootURL).appendEntitySetSegment("Customers").build());
    req.setPrefer(client.newPreferences().trackChanges());
 
    final ClientEntitySet customers = req.execute().getBody();
    assertNotNull(customers);
    assertNotNull(customers.getDeltaLink());
 
    final ODataDeltaRequest deltaReq = client.getRetrieveRequestFactory().getDeltaRequest(customers.getDeltaLink());
    
    AsyncRequestFactory asyncRequestFactory = client.getAsyncRequestFactory();
    AsyncRequestWrapper<ODataRetrieveResponse<ClientDelta>> asyncRequestWrapper =
            asyncRequestFactory
                    .<ODataRetrieveResponse<ClientDelta>>getAsyncRequestWrapper(deltaReq);
    
    AsyncResponseWrapper<ODataRetrieveResponse<ClientDelta>> responseWrapper =
            asyncRequestWrapper
                    .execute();
    if (responseWrapper.isPreferenceApplied()) {
        int waitInSec = 5;
        while (!responseWrapper.isDone()) {
            try {
                Thread.sleep(waitInSec);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    boolean done = responseWrapper.isDone();
    ODataRetrieveResponse<ClientDelta> res = responseWrapper.getODataResponse();
    ClientDelta delta = res.getBody(); // NPE !!!
    assertNotNull(delta);
  }
  
  /**
   * 11. MAY support asynchronous responses (section 9.1.3).
   */
  @Test
  public void item11() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("async").appendEntitySetSegment("Orders");

    final ODataEntitySetRequest<ClientEntitySet> req =
        client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON_FULL_METADATA);

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

  /**
   * 12. MAY support odata.metadata=minimal in a JSON response (see [OData-JSON]).
   */
  @Test
  public void item12() {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Customers").appendKeySegment(1).expand("Company");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
    req.setFormat(ContentType.JSON);

    assertEquals("application/json;odata.metadata=minimal", req.getHeader("Accept"));
    assertEquals("application/json;odata.metadata=minimal", req.getAccept());

    final ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertTrue(res.getContentType().startsWith("application/json; odata.metadata=minimal"));

    assertNotNull(res.getBody());
  }
}
