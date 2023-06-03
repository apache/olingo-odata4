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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.fit.CXFOAuth2HttpClientFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class OAuth2TestITCase extends AbstractTestITCase {

  private static final URI OAUTH2_GRANT_SERVICE_URI =
      URI.create("http://localhost:9080/stub/StaticService/oauth2/authorize");

  private static final URI OAUTH2_TOKEN_SERVICE_URI =
      URI.create("http://localhost:9080/stub/StaticService/oauth2/token");

  private EdmEnabledODataClient _edmClient;

  @BeforeClass
  public static void enableOAuth2() {
    client.getConfiguration().setHttpClientFactory(
        new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI));
  }

  @AfterClass
  public static void disableOAuth2() {
    client.getConfiguration().setHttpClientFactory(new DefaultHttpClientFactory());
  }

  protected ODataClient getLocalClient() {
    ODataClient localClient = ODataClientFactory.getClient();
    localClient.getConfiguration().setHttpClientFactory(
        new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI));
    return localClient;
  }

  protected EdmEnabledODataClient getEdmClient() {
    if (_edmClient == null) {
      _edmClient = ODataClientFactory.getEdmEnabledClient(testOAuth2ServiceRootURL, ContentType.JSON);
      _edmClient.getConfiguration().setHttpClientFactory(
          new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI));
    }

    return _edmClient;
  }

  private void read(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder =
        client.newURIBuilder(testOAuth2ServiceRootURL).appendEntitySetSegment("Orders").appendKeySegment(8);

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
  public void testOAuth() {
    try {
      readAsAtom();
    } catch (RuntimeException e) {
      fail("failed for readAsAtom");
    }

    try {
      readAsFullJSON();
    } catch (RuntimeException e) {
      fail("failed for readAsFullJSON");
    }

    try {
      readAsJSON();
    } catch (RuntimeException e) {
      fail("failed for readAsJSON");
    }

    try {
      createAndDelete();
    } catch (RuntimeException e) {
      fail("failed for createAndDelete");
    }
  }

  public void readAsAtom() {
    read(getLocalClient(), ContentType.APPLICATION_ATOM_XML);
  }

  public void readAsFullJSON() {
    read(getLocalClient(), ContentType.JSON_FULL_METADATA);
  }

  public void readAsJSON() {
    read(getEdmClient(), ContentType.JSON);
  }

  public void createAndDelete() {
    createAndDeleteOrder(testOAuth2ServiceRootURL, ContentType.JSON, 1002);
  }

}
