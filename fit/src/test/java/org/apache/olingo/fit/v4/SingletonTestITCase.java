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

import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataSingleton;
import org.apache.olingo.commons.api.domain.v4.ODataValuable;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

public class SingletonTestITCase extends AbstractTestITCase {

  private void read(final ODataClient client, final ODataFormat format) throws EdmPrimitiveTypeException {
    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<ODataSingleton> singleton =
        client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(format);
    final ODataSingleton company = singleton.execute().getBody();
    assertNotNull(company);

    assertEquals(0, company.getProperty("CompanyID").getPrimitiveValue().toCastValue(Integer.class), 0);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory",
        ((ODataValuable) company.getProperty("CompanyCategory")).getValue().getTypeName());
    assertTrue(company.getProperty("CompanyCategory").hasEnumValue());
  }

  @Test
  public void readFromAtom() throws EdmPrimitiveTypeException {
    read(client, ODataFormat.ATOM);
  }

  @Test
  public void readFromJSON() throws EdmPrimitiveTypeException {
    read(edmClient, ODataFormat.JSON);
  }

  @Test
  public void readfromJSONFull() throws EdmPrimitiveTypeException {
    read(client, ODataFormat.JSON_FULL_METADATA);
  }

  private void readWithAnnotations(final ODataClient client, final ODataFormat format)
      throws EdmPrimitiveTypeException {

    final URIBuilder builder = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Boss");
    final ODataEntityRequest<ODataSingleton> singleton =
        client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(format);
    singleton.setPrefer(client.newPreferences().includeAnnotations("*"));
    final ODataSingleton boss = singleton.execute().getBody();
    assertNotNull(boss);

    assertFalse(boss.getAnnotations().isEmpty());
    final ODataAnnotation isBoss = boss.getAnnotations().get(0);
    assertTrue(isBoss.getPrimitiveValue().toCastValue(Boolean.class));
  }

  @Test
  public void readWithAnnotationsFromAtom() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ODataFormat.ATOM);
  }

  @Test
  public void readWithAnnotationsFromJSON() throws EdmPrimitiveTypeException {
    readWithAnnotations(edmClient, ODataFormat.JSON);
  }

  @Test
  public void readWithAnnotationsFromJSONFull() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ODataFormat.JSON_FULL_METADATA);
  }

  private void update(final ODataFormat format) throws EdmPrimitiveTypeException {
    final ODataSingleton changes = getClient().getObjectFactory().newSingleton(
        new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"));
    changes.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Revenue",
        getClient().getObjectFactory().newPrimitiveValueBuilder().buildInt64(132520L)));

    final URI uri = client.newURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company").build();
    final ODataEntityUpdateRequest<ODataSingleton> req = getClient().getCUDRequestFactory().
        getSingletonUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(format);

    final ODataEntityUpdateResponse<ODataSingleton> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final ODataSingleton updated = getClient().getRetrieveRequestFactory().getSingletonRequest(uri).execute().getBody();
    assertNotNull(updated);
    assertEquals(132520, updated.getProperty("Revenue").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomUpdate() throws EdmPrimitiveTypeException {
    update(ODataFormat.ATOM);
  }

  @Test
  public void jsonUpdate() throws EdmPrimitiveTypeException {
    update(ODataFormat.JSON);
  }

}
