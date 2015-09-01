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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientSingleton;
import org.apache.olingo.client.api.domain.ClientValuable;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class SingletonTestITCase extends AbstractTestITCase {

  private void read(final ODataClient client, final ContentType contentType) throws EdmPrimitiveTypeException {
    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<ClientSingleton> singleton =
        client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(contentType);
    final ClientSingleton company = singleton.execute().getBody();
    assertNotNull(company);

    assertEquals(0, company.getProperty("CompanyID").getPrimitiveValue().toCastValue(Integer.class), 0);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory",
        ((ClientValuable) company.getProperty("CompanyCategory")).getValue().getTypeName());
    assertTrue(company.getProperty("CompanyCategory").hasEnumValue());
  }

  @Test
  public void readFromAtom() throws EdmPrimitiveTypeException {
    read(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void readFromJSON() throws EdmPrimitiveTypeException {
    read(edmClient, ContentType.JSON);
  }

  @Test
  public void readfromJSONFull() throws EdmPrimitiveTypeException {
    read(client, ContentType.JSON_FULL_METADATA);
  }

  private void readWithAnnotations(final ODataClient client, final ContentType contentType)
      throws EdmPrimitiveTypeException {

    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Boss");
    final ODataEntityRequest<ClientSingleton> singleton =
        client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(contentType);
    singleton.setPrefer(client.newPreferences().includeAnnotations("*"));
    final ClientSingleton boss = singleton.execute().getBody();
    assertNotNull(boss);

    assertFalse(boss.getAnnotations().isEmpty());
    final ClientAnnotation isBoss = boss.getAnnotations().get(0);
    assertTrue(isBoss.getPrimitiveValue().toCastValue(Boolean.class));
  }

  @Test
  public void readWithAnnotationsFromAtom() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void readWithAnnotationsFromJSON() throws EdmPrimitiveTypeException {
    readWithAnnotations(edmClient, ContentType.JSON);
  }

  @Test
  public void readWithAnnotationsFromJSONFull() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ContentType.JSON_FULL_METADATA);
  }

  private void update(final ContentType contentType) throws EdmPrimitiveTypeException {
    final ClientSingleton changes = getClient().getObjectFactory().newSingleton(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"));
    changes.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Revenue",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt64(132520L)));

    final URI uri = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company").build();
    final ODataEntityUpdateRequest<ClientSingleton> req = getClient().getCUDRequestFactory().
        getSingletonUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(contentType);

    final ODataEntityUpdateResponse<ClientSingleton> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ClientSingleton updated =
        getClient().getRetrieveRequestFactory().getSingletonRequest(uri).execute().getBody();
    assertNotNull(updated);
    assertEquals(132520, updated.getProperty("Revenue").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomUpdate() throws EdmPrimitiveTypeException {
    update(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonUpdate() throws EdmPrimitiveTypeException {
    update(ContentType.JSON);
  }

}
