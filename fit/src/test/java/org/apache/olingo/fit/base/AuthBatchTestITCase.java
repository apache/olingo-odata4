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

import java.net.URI;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.junit.Test;

public class AuthBatchTestITCase extends AbstractTestITCase {

  private final static ContentType ACCEPT = ContentType.APPLICATION_OCTET_STREAM;

  @Test
  public void clean() throws EdmPrimitiveTypeException {
    final ODataClient authclient = ODataClientFactory.getClient();
    batchRequest(authclient, testStaticServiceRootURL);
  }

  @Test
  public void authorized() throws EdmPrimitiveTypeException {
    final ODataClient authclient = ODataClientFactory.getClient();
    authclient.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("odatajclient", "odatajclient"));
    batchRequest(authclient, testAuthServiceRootURL);
  }

  @Test(expected = HttpClientException.class)
  public void unauthorized() throws EdmPrimitiveTypeException {
    final ODataClient unauthclient = ODataClientFactory.getClient();
    unauthclient.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("not_auth", "not_auth"));
    batchRequest(unauthclient, testAuthServiceRootURL);
  }

  private void batchRequest(final ODataClient client, final String baseURL) throws EdmPrimitiveTypeException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(baseURL);
    request.setAccept(ACCEPT.toContentTypeString());
    request.addCustomHeader("User-Agent", "Apache Olingo OData Client");
    request.addCustomHeader(HttpHeader.ACCEPT_CHARSET, "UTF-8");

    final BatchManager streamManager = request.payloadManager();

    // -------------------------------------------
    // Add retrieve item
    // -------------------------------------------
    // prepare URI
    URIBuilder targetURI = client.newURIBuilder(baseURL);
    targetURI.appendEntitySetSegment("Customers").appendKeySegment(1);

    // create new request
    ODataEntityRequest<ClientEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());
    queryReq.setFormat(ContentType.JSON);

    streamManager.addRequest(queryReq);
    // -------------------------------------------

    // -------------------------------------------
    // Add changeset item
    // -------------------------------------------
    final ODataChangeset changeset = streamManager.addChangeset();

    // Update Customer into the changeset
    targetURI = client.newURIBuilder(baseURL).appendEntitySetSegment("Customers").appendKeySegment(1);
    final URI editLink = targetURI.build();

    final ClientEntity patch = client.getObjectFactory().newEntity(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));
    patch.setEditLink(editLink);

    patch.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
        "LastName",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("new last name")));

    final ODataEntityUpdateRequest<ClientEntity> changeReq =
        client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);
    changeReq.setFormat(ContentType.JSON_FULL_METADATA);

    changeset.addRequest(changeReq);
    // -------------------------------------------

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(200, response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());
  }
}
