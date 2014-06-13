/*
 * Copyright 2014 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.v4.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.commons.api.domain.v4.ODataEntity;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class AuthBatchTestITCase extends AbstractTestITCase {

  private final static String ACCEPT = ContentType.APPLICATION_OCTET_STREAM;

  @Test
  public void clean() throws EdmPrimitiveTypeException {
    final ODataClient authclient = ODataClientFactory.getV4();
    batchRequest(authclient, testStaticServiceRootURL);
  }

  @Test
  public void authorized() throws EdmPrimitiveTypeException {
    final ODataClient authclient = ODataClientFactory.getV4();
    authclient.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("odatajclient", "odatajclient"));
    batchRequest(authclient, testAuthServiceRootURL);
  }

  @Test(expected = HttpClientException.class)
  public void unauthorized() throws EdmPrimitiveTypeException {
    final ODataClient unauthclient = ODataClientFactory.getV4();
    unauthclient.getConfiguration().setHttpClientFactory(new BasicAuthHttpClientFactory("not_auth", "not_auth"));
    batchRequest(unauthclient, testAuthServiceRootURL);
  }

  private void batchRequest(final ODataClient client, final String baseURL) throws EdmPrimitiveTypeException {
    // create your request
    final ODataBatchRequest request = client.getBatchRequestFactory().getBatchRequest(baseURL);
    request.setAccept(ACCEPT);
    request.addCustomHeader("User-Agent", "Microsoft ADO.NET Data Client xxx");
    request.addCustomHeader(HeaderName.acceptCharset, "UTF-8");

    final BatchManager streamManager = request.payloadManager();

    // -------------------------------------------
    // Add retrieve item
    // -------------------------------------------
    // prepare URI
    URIBuilder targetURI = client.newURIBuilder(baseURL);
    targetURI.appendEntitySetSegment("Customers").appendKeySegment(1);

    // create new request
    ODataEntityRequest<ODataEntity> queryReq = client.getRetrieveRequestFactory().getEntityRequest(targetURI.build());
    queryReq.setFormat(ODataFormat.JSON);

    streamManager.addRequest(queryReq);
    // -------------------------------------------

    // -------------------------------------------
    // Add changeset item
    // -------------------------------------------
    final ODataChangeset changeset = streamManager.addChangeset();

    // Update Customer into the changeset
    targetURI = client.newURIBuilder(baseURL).appendEntitySetSegment("Customers").appendKeySegment(1);
    final URI editLink = targetURI.build();

    final ODataEntity patch = client.getObjectFactory().newEntity(
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Customer"));
    patch.setEditLink(editLink);

    patch.getProperties().add(client.getObjectFactory().newPrimitiveProperty(
            "LastName",
            client.getObjectFactory().newPrimitiveValueBuilder().buildString("new last name")));

    final ODataEntityUpdateRequest<ODataEntity> changeReq =
            client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);
    changeReq.setFormat(ODataFormat.JSON_FULL_METADATA);

    changeset.addRequest(changeReq);
    // -------------------------------------------

    final ODataBatchResponse response = streamManager.getResponse();
    assertEquals(200, response.getStatusCode());
    assertEquals("OK", response.getStatusMessage());
  }
}
