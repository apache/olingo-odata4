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
package org.apache.olingo.fit.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import org.apache.olingo.client.api.communication.request.cud.ODataPropertyUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataPropertyUpdateResponse;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.v4.ODataProperty;
import org.apache.olingo.commons.api.domain.v4.ODataValuable;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class PropertyTestITCase extends AbstractTestITCase {

  private void _enum(final ODataClient client, final ODataFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Products").appendKeySegment(5).appendPropertySegment("CoverColors");
    final ODataPropertyRequest<ODataProperty> req = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataProperty prop = req.execute().getBody();
    assertNotNull(prop);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Collection(Microsoft.Test.OData.Services.ODataWCFService.Color)",
            ((ODataValuable) prop).getValue().getTypeName());
  }

  @Test
  public void enumFromXML() {
    _enum(client, ODataFormat.XML);
  }

  @Test
  public void enumFromJSON() {
    _enum(edmClient, ODataFormat.JSON);
  }

  @Test
  public void enumFromFullJSON() {
    _enum(client, ODataFormat.JSON_FULL_METADATA);
  }

  private void geospatial(final ODataClient client, final ODataFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Home");
    final ODataPropertyRequest<ODataProperty> req = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataProperty prop = req.execute().getBody();
    assertNotNull(prop);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Edm.GeographyPoint", ((ODataValuable) prop).getValue().getTypeName());
  }

  @Test
  public void geospatialFromXML() {
    geospatial(client, ODataFormat.XML);
  }

  @Test
  public void geospatialFromJSON() {
    geospatial(edmClient, ODataFormat.JSON);
  }

  @Test
  public void geospatialFromFullJSON() {
    geospatial(client, ODataFormat.JSON_FULL_METADATA);
  }

  private void complex(final ODataClient client, final ODataFormat format) {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(2).appendPropertySegment("HomeAddress");
    final ODataPropertyRequest<ODataProperty> req = client.getRetrieveRequestFactory().
            getPropertyRequest(uriBuilder.build());
    req.setFormat(format);

    final ODataProperty prop = req.execute().getBody();
    assertNotNull(prop);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.Address", 
            ((ODataValuable) prop).getValue().getTypeName());
  }

  @Test
  public void complexFromXML() {
    complex(client, ODataFormat.XML);
  }

  @Test
  public void complexFromJSON() {
    complex(edmClient, ODataFormat.JSON);
  }

  @Test
  public void complexFromFullJSON() {
    complex(client, ODataFormat.JSON_FULL_METADATA);
  }

  private void updateComplexProperty(final ODataFormat format, final UpdateType type) throws IOException {
    final URIBuilder uriBuilder = client.getURIBuilder(testStaticServiceRootURL).
            appendEntitySetSegment("Customers").appendKeySegment(1).appendPropertySegment("HomeAddress");

    ODataPropertyRequest<ODataProperty> retrieveReq =
            client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    ODataRetrieveResponse<ODataProperty> retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    ODataProperty homeAddress = client.getObjectFactory().newComplexProperty("HomeAddress",
            client.getObjectFactory().newComplexValue(retrieveRes.getBody().getComplexValue().getTypeName()));

    homeAddress.getComplexValue().add(client.getObjectFactory().
            newPrimitiveProperty("City", client.getObjectFactory().newPrimitiveValueBuilder().buildString("Pescara")));

    final ODataPropertyUpdateRequest updateReq = client.getCUDRequestFactory().
            getPropertyComplexValueUpdateRequest(uriBuilder.build(), type, homeAddress);
    if (client.getConfiguration().isUseXHTTPMethod()) {
      assertEquals(HttpMethod.POST, updateReq.getMethod());
    } else {
      assertEquals(type.getMethod(), updateReq.getMethod());
    }
    updateReq.setFormat(format);

    final ODataPropertyUpdateResponse updateRes = updateReq.execute();
    assertEquals(204, updateRes.getStatusCode());

    retrieveReq = client.getRetrieveRequestFactory().getPropertyRequest(uriBuilder.build());
    retrieveReq.setFormat(format);

    retrieveRes = retrieveReq.execute();
    assertEquals(200, retrieveRes.getStatusCode());

    homeAddress = retrieveRes.getBody();
    assertEquals("Pescara", homeAddress.getComplexValue().get("City").getPrimitiveValue().toString());
  }

  @Test
  public void patchComplexPropertyAsJSON() throws IOException {
    updateComplexProperty(ODataFormat.JSON_FULL_METADATA, UpdateType.PATCH);
  }

}
