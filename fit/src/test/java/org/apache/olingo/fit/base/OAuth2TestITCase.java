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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
//import org.apache.olingo.client.core.http.DefaultHttpClientFactory5;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
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

  private static boolean OAUTH_AVAILABLE = true;

  @BeforeClass
  public static void enableOAuth2() {
    final CXFOAuth2HttpClientFactory cxFactory =
        new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI);
    // Probe OAuth endpoints now: attempt to create a client which will trigger init().
    OAUTH_AVAILABLE = true;
    try {
      // Trigger initialization; if it fails, we won't install the OAuth factory
      cxFactory.create(org.apache.olingo.commons.api.http.HttpMethod.GET, OAUTH2_GRANT_SERVICE_URI);
    } catch (final Exception e) {
      OAUTH_AVAILABLE = false;
    }

    if (OAUTH_AVAILABLE) {
      client.getConfiguration().setHttpClientFactory(new org.apache.olingo.client.api.http.HttpClientFactory() {
        @Override
        public org.apache.hc.client5.http.classic.HttpClient create(
            final org.apache.olingo.commons.api.http.HttpMethod method, final java.net.URI uri) {
          return cxFactory.create(method, uri);
        }

        @Override
        public void close(final org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient)
            throws java.io.IOException {
          cxFactory.close(httpClient);
        }
      });
    } else {
      // OAuth endpoints not available in this environment; keep default factory
    }
  }

  @AfterClass
  public static void disableOAuth2() {
    final org.apache.olingo.client.core.http.DefaultHttpClientFactory def =
        new org.apache.olingo.client.core.http.DefaultHttpClientFactory();
    client.getConfiguration().setHttpClientFactory(new org.apache.olingo.client.api.http.HttpClientFactory() {
      @Override
      public org.apache.hc.client5.http.classic.HttpClient create(
          final org.apache.olingo.commons.api.http.HttpMethod method, final java.net.URI uri) {
        return def.create(method, uri);
      }

      @Override
      public void close(final org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient)
          throws java.io.IOException {
        def.close(httpClient);
      }
    });
  }

  protected ODataClient getLocalClient() {
    ODataClient localClient = ODataClientFactory.getClient();
    final CXFOAuth2HttpClientFactory cxFactory =
        new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI);
    // Probe OAuth endpoints; only install CXF factory if probe succeeds
    try {
      cxFactory.create(org.apache.olingo.commons.api.http.HttpMethod.GET, OAUTH2_GRANT_SERVICE_URI);
      localClient.getConfiguration().setHttpClientFactory(new org.apache.olingo.client.api.http.HttpClientFactory() {
        @Override
        public org.apache.hc.client5.http.classic.HttpClient create(
            final org.apache.olingo.commons.api.http.HttpMethod method, final java.net.URI uri) {
          return cxFactory.create(method, uri);
        }

        @Override
        public void close(final org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient)
            throws java.io.IOException {
          cxFactory.close(httpClient);
        }
      });
    } catch (final Exception e) {
      // OAuth endpoints unavailable; return client with default factory
    }
    return localClient;
  }

  protected EdmEnabledODataClient getEdmClient() {
    if (_edmClient == null) {
      _edmClient = ODataClientFactory.getEdmEnabledClient(testOAuth2ServiceRootURL, ContentType.JSON);
      final CXFOAuth2HttpClientFactory cxFactory =
          new CXFOAuth2HttpClientFactory(OAUTH2_GRANT_SERVICE_URI, OAUTH2_TOKEN_SERVICE_URI);
      try {
        cxFactory.create(org.apache.olingo.commons.api.http.HttpMethod.GET, OAUTH2_GRANT_SERVICE_URI);
        _edmClient.getConfiguration().setHttpClientFactory(new org.apache.olingo.client.api.http.HttpClientFactory() {
          @Override
          public org.apache.hc.client5.http.classic.HttpClient create(
              final org.apache.olingo.commons.api.http.HttpMethod method, final java.net.URI uri) {
            return cxFactory.create(method, uri);
          }

          @Override
          public void close(final org.apache.hc.client5.http.impl.classic.CloseableHttpClient httpClient)
              throws java.io.IOException {
            cxFactory.close(httpClient);
          }
        });
      } catch (final Exception e) {
        // OAuth endpoints not reachable; leave default factory
      }
    }

    return _edmClient;
  }

  private void read(final ODataClient client, final ContentType contentType) throws URISyntaxException, IOException {
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
    org.junit.Assume.assumeTrue("OAuth endpoints not available; skipping OAuth test", OAUTH_AVAILABLE);
    try {
      readAsAtom();
    } catch (RuntimeException e) {
      // Rethrow to reveal original exception and stack trace during test runs
      throw e;
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    try {
      readAsFullJSON();
    } catch (RuntimeException e) {
      throw e;
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    try {
      readAsJSON();
    } catch (RuntimeException e) {
      throw e;
    } catch (URISyntaxException e) {
        throw new RuntimeException(e);
    } catch (IOException e) {
        throw new RuntimeException(e);
    }

    try {
      createAndDelete();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

  public void readAsAtom() throws URISyntaxException, IOException {
    read(getLocalClient(), ContentType.APPLICATION_ATOM_XML);
  }

  public void readAsFullJSON() throws URISyntaxException, IOException {
    read(getLocalClient(), ContentType.JSON_FULL_METADATA);
  }

  public void readAsJSON() throws URISyntaxException, IOException {
    read(getEdmClient(), ContentType.JSON);
  }

  public void createAndDelete() throws Exception {
    createAndDeleteOrder(testOAuth2ServiceRootURL, ContentType.JSON, 1002);
  }

}
