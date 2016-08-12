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
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class PropertyTestITCase extends AbstractTestITCase {

  private void _enum(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("Products").appendKeySegment(5).appendPropertySegment("CoverColors");
    final ODataPropertyRequest<ClientProperty> req = client.getRetrieveRequestFactory().
        getPropertyRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ClientProperty prop = req.execute().getBody();
    assertNotNull(prop);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Collection(Microsoft.Test.OData.Services.ODataWCFService.Color)",
        ((ClientValuable) prop).getValue().getTypeName());
  }

  @Test
  public void enumFromXML() {
    _enum(client, ContentType.APPLICATION_XML);
  }

  @Test
  public void enumFromJSON() {
    _enum(edmClient, ContentType.JSON);
  }

  @Test
  public void enumFromFullJSON() {
    _enum(client, ContentType.JSON_FULL_METADATA);
  }

  private void geospatial(final ODataClient client, final ContentType contentType) {
    final URIBuilder uriBuilder = client.newURIBuilder(testStaticServiceRootURL).
        appendEntitySetSegment("People").appendKeySegment(5).appendPropertySegment("Home");
    final ODataPropertyRequest<ClientProperty> req = client.getRetrieveRequestFactory().
        getPropertyRequest(uriBuilder.build());
    req.setFormat(contentType);

    final ClientProperty prop = req.execute().getBody();
    assertNotNull(prop);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Edm.GeographyPoint", ((ClientValuable) prop).getValue().getTypeName());
  }

  @Test
  public void geospatialFromXML() {
    geospatial(client, ContentType.APPLICATION_XML);
  }

  @Test
  public void geospatialFromJSON() {
    geospatial(edmClient, ContentType.JSON);
  }

  @Test
  public void geospatialFromFullJSON() {
    geospatial(client, ContentType.JSON_FULL_METADATA);
  }
}
