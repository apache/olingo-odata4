/*
 * Copyright 2016 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.olingo.fit.tecsvc.client;

import java.net.URI;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.core.http.BasicAuthHttpClientFactory;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

/**
 * The test cases in this class are inspired by client conformance criteria defined in the <a
 * href="http://docs.oasis-open.org/odata/odata-json-format/v4.0/errata03/os
 * /odata-json-format-v4.0-errata03-os-complete.html#_Toc453766671">specs
 * 
 * </a>.
 */
public class ConformanceITCase extends AbstractParamTecSvcITCase {

  private static final String ODATA_MAX_VERSION_NUMBER = "4.0";
  
  private static final FullQualifiedName ET_TWO_PRIM = new FullQualifiedName(SERVICE_NAMESPACE, "ETTwoPrim");
  private static final FullQualifiedName ET_ALL_PRIM = new FullQualifiedName(SERVICE_NAMESPACE, "ETAllPrim");

  private static final String PROPERTY_INT64 = "PropertyInt64";

  private static final String NAV_PROPERTY_ET_TWO_PRIM_ONE = "NavPropertyETTwoPrimOne";

  private static final String ES_ALL_PRIM = "ESAllPrim";
  private static final String ES_TWO_PRIM = "ESTwoPrim";

  /**
   * 1. MUST specify the OData-MaxVersion header in requests (section 8.2.6).
   */
  @Test
  public void isOdataMaxVersion() {
    final ODataClient client = getClient();
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESAllPrim")
        .appendKeySegment(32767);

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    assertEquals(ODATA_MAX_VERSION_NUMBER, req.getHeader(HttpHeader.ODATA_MAX_VERSION));

    assertNotNull(req.execute().getBody());
  }

  /**
   * 2. MUST specify OData-Version (section 8.1.5) and Content-Type (section 8.1.1) in any request with a payload.
   */
  @Test
  public void checkClientWithPayloadHeader() {
    assumeTrue("json conformance test with content type", isJson());

    ClientEntity newEntity = getFactory().newEntity(ET_ALL_PRIM);
    newEntity.getProperties().add(getFactory().newPrimitiveProperty(PROPERTY_INT64,
        getFactory().newPrimitiveValueBuilder().buildInt64((long) 42)));
    final ODataClient client = getClient();
    newEntity.addLink(getFactory().newEntityNavigationLink(NAV_PROPERTY_ET_TWO_PRIM_ONE,
        client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32766)
            .build()));

    final ODataEntityCreateRequest<ClientEntity> createRequest = client.getCUDRequestFactory().getEntityCreateRequest(
        client.newURIBuilder(SERVICE_URI).appendEntitySetSegment(ES_ALL_PRIM).build(),
        newEntity);
    assertNotNull(createRequest);
    createRequest.setFormat(contentType);
    // check for OData-Version
    assertEquals(ODATA_MAX_VERSION_NUMBER, createRequest.getHeader(HttpHeader.ODATA_VERSION));

    // check for Content-Type
    assertEquals(
        ContentType.APPLICATION_JSON.toContentTypeString(),
        createRequest.getHeader(HttpHeader.CONTENT_TYPE));
    assertEquals(
        ContentType.APPLICATION_JSON.toContentTypeString(),
        createRequest.getContentType());

    final ODataEntityCreateResponse<ClientEntity> createResponse = createRequest.execute();

    assertEquals(HttpStatusCode.CREATED.getStatusCode(), createResponse.getStatusCode());
  }

  /**
   * 5. MUST correctly handle next links (section 11.2.5.7).
   */
  @Test
  public void handleNextLinks() {
    final ODataClient client = getClient();
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI).appendEntitySetSegment("ESServerSidePaging");

    ODataEntitySetRequest<ClientEntitySet> req = client.getRetrieveRequestFactory().getEntitySetRequest(uriBuilder
        .build());
    req.setPrefer(client.newPreferences().maxPageSize(5));

    ODataRetrieveResponse<ClientEntitySet> res = req.execute();
    ClientEntitySet feed = res.getBody();
    assertNotNull(feed);
    assertEquals(5, feed.getEntities().size());
    assertNotNull(feed.getNext());

    final URI expected = URI.create(SERVICE_URI + "ESServerSidePaging?%24skiptoken=1%2A5");
    final URI found = URIUtils.getURI(SERVICE_URI, feed.getNext().toASCIIString());
    assertEquals(expected, found);
    
    req = client.getRetrieveRequestFactory().getEntitySetRequest(found);
    assertNotNull(req);
    res = req.execute();
    assertNotNull(res);
    feed = res.getBody();
    assertNotNull(feed);
  }

  /**
   * 7. MUST generate PATCH requests for updates, if the client supports updates (section 11.4.3).
   **/
  @Test
  public void patchEntityRequestForUpdates() {
    final ODataClient client = getClient();
    final ClientEntity patch = client.getObjectFactory().newEntity(ET_TWO_PRIM);     
    final URI uri = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32766)
            .build();
    patch.setEditLink(uri);
    final String newString = "Seconds: (" + System.currentTimeMillis() + ")";
    patch.getProperties().add(client.getObjectFactory().newPrimitiveProperty("PropertyString",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString(newString)));

    final ODataEntityUpdateRequest<ClientEntity> req =            
        client.getCUDRequestFactory().getEntityUpdateRequest(UpdateType.PATCH, patch);
    assertNotNull(req);
    assertEquals(ODATA_MAX_VERSION_NUMBER, req.getHeader(HttpHeader.ODATA_MAX_VERSION));
    
    final ODataEntityUpdateResponse<ClientEntity> res = req.execute();
    assertNotNull(res);
    assertEquals(200, res.getStatusCode());   
            
    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertEquals(newString, entity.getProperty("PropertyString").getPrimitiveValue().toString());
  }

  /**
   * 8. SHOULD support basic authentication as specified in [RFC2617] over HTTPS.
   * <br />
   * Unfortunately no service over HTTPs is available yet.
   * (test is copied from ConformanceTest of the static service)
   * TODO: correct client FIT test for https authentication
   */  
  @Test
  public void basicHttpsAuthentication() {
    final ODataClient client = getClient();
    client.getConfiguration()
            .setHttpClientFactory(new BasicAuthHttpClientFactory("odatajclient", "odatajclient"));

    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32767)
            .expand("NavPropertyETAllPrimOne");

    final ODataEntityRequest<ClientEntity> req =
        client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());
 
    assertNotNull(req.execute().getBody());

    client.getConfiguration().setHttpClientFactory(new DefaultHttpClientFactory());
  }
  
   /**
   * 9. MAY request entity references in place of entities previously returned in the response (section 11.2.7).
   */
  @Test
  public void entityNavigationReference() {
    final ODataClient client = getClient();
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32767)
            .appendNavigationSegment("NavPropertyETAllPrimOne")
            .appendRefSegment();

    ODataEntityRequest<ClientEntity> req = client.getRetrieveRequestFactory().getEntityRequest(uriBuilder.build());

    ODataRetrieveResponse<ClientEntity> res = req.execute();
    assertNotNull(res);

    final ClientEntity entity = res.getBody();
    assertNotNull(entity);
    assertTrue(entity.getId().toASCIIString().endsWith("ESAllPrim(32767)"));
  } 
  
    /**
   * 12. MAY support odata.metadata=minimal in a JSON response (see [OData-JSON]).
   */
  @Test
  public void supportMetadataMinimal() {
    assumeTrue("format should be json", isJson());
    ODataClient client = getClient();
    final URIBuilder uriBuilder = client.newURIBuilder(SERVICE_URI)
            .appendEntitySetSegment(ES_TWO_PRIM)
            .appendKeySegment(32767)
            .expand("NavPropertyETAllPrimOne");

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