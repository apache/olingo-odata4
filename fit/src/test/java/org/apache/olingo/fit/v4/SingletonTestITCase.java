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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.request.cud.v4.UpdateType;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.v4.ODataAnnotation;
import org.apache.olingo.commons.api.domain.v4.ODataValuable;
import org.apache.olingo.commons.api.domain.v4.Singleton;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.junit.Test;

public class SingletonTestITCase extends AbstractTestITCase {

  private void read(final ODataClient client, final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company");
    final ODataEntityRequest<Singleton> singleton =
            client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(format);
    final Singleton company = singleton.execute().getBody();
    assertNotNull(company);

    assertEquals(0, company.getProperty("CompanyID").getPrimitiveValue().toCastValue(Integer.class), 0);
    // cast to workaround JDK 6 bug, fixed in JDK 7
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.CompanyCategory",
            ((ODataValuable) company.getProperty("CompanyCategory")).getValue().getTypeName());
    assertTrue(company.getProperty("CompanyCategory").hasEnumValue());
  }

  @Test
  public void readFromAtom() throws EdmPrimitiveTypeException {
    read(client, ODataPubFormat.ATOM);
  }

  @Test
  public void readFromJSON() throws EdmPrimitiveTypeException {
    read(edmClient, ODataPubFormat.JSON);
  }

  @Test
  public void readfromJSONFull() throws EdmPrimitiveTypeException {
    read(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  private void readWithAnnotations(final ODataClient client, final ODataPubFormat format) 
          throws EdmPrimitiveTypeException {
    
    final URIBuilder builder = client.getURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Boss");    
    final ODataEntityRequest<Singleton> singleton =
            client.getRetrieveRequestFactory().getSingletonRequest(builder.build());
    singleton.setFormat(format);
    singleton.setPrefer(client.newPreferences().includeAnnotations("*"));
    final Singleton boss = singleton.execute().getBody();
    assertNotNull(boss);

    assertFalse(boss.getAnnotations().isEmpty());
    final ODataAnnotation isBoss = boss.getAnnotations().get(0);
    assertTrue(isBoss.getPrimitiveValue().toCastValue(Boolean.class));
  }

  @Test
  public void readWithAnnotationsFromAtom() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ODataPubFormat.ATOM);
  }

  @Test
  public void readWithAnnotationsFromJSON() throws EdmPrimitiveTypeException {
    readWithAnnotations(edmClient, ODataPubFormat.JSON);
  }

  @Test
  public void readWithAnnotationsFromJSONFull() throws EdmPrimitiveTypeException {
    readWithAnnotations(client, ODataPubFormat.JSON_FULL_METADATA);
  }

  private void update(final ODataPubFormat format) throws EdmPrimitiveTypeException {
    final Singleton changes = getClient().getObjectFactory().newSingleton(
            new FullQualifiedName("Microsoft.Test.OData.Services.ODataWCFService.Company"));
    changes.getProperties().add(getClient().getObjectFactory().newPrimitiveProperty("Revenue",
            getClient().getObjectFactory().newPrimitiveValueBuilder().
            setType(EdmPrimitiveTypeKind.Int64).setText("132520").build()));

    final URI uri = client.getURIBuilder(testStaticServiceRootURL).appendSingletonSegment("Company").build();
    final ODataEntityUpdateRequest<Singleton> req = getClient().getCUDRequestFactory().
            getSingletonUpdateRequest(uri, UpdateType.PATCH, changes);
    req.setFormat(format);

    final ODataEntityUpdateResponse<Singleton> res = req.execute();
    assertEquals(204, res.getStatusCode());

    final Singleton updated = getClient().getRetrieveRequestFactory().getSingletonRequest(uri).execute().getBody();
    assertNotNull(updated);
    assertEquals(132520, updated.getProperty("Revenue").getPrimitiveValue().toCastValue(Integer.class), 0);
  }

  @Test
  public void atomUpdate() throws EdmPrimitiveTypeException {
    update(ODataPubFormat.ATOM);
  }

  @Test
  public void jsonUpdate() throws EdmPrimitiveTypeException {
    update(ODataPubFormat.JSON);
  }

}
