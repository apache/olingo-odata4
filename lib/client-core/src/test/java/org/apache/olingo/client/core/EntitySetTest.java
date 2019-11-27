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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class EntitySetTest extends AbstractTest {

private EdmEnabledODataClient getEdmEnabledClient1() {
    return new EdmEnabledODataClientImpl(null, null, null) {

      private Edm edm;

      @Override
      public Edm getEdm(final String metadataETag) {
        return getCachedEdm();
      }

      @Override
      public Edm getCachedEdm() {
        if (edm == null) {
          edm = getReader().readMetadata(getClass().getResourceAsStream("metadata_sample.xml"));
        }
        return edm;
      }

    };
  }
  
  private void read(final ContentType contentType) throws IOException, ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("Customers." + getSuffix(contentType));
    final ClientEntitySet entitySet = client.getBinder().getODataEntitySet(
        client.getDeserializer(contentType).toEntitySet(input));
    assertNotNull(entitySet);

    assertEquals(2, entitySet.getEntities().size());
    assertNull(entitySet.getNext());

    final ClientEntitySet written =
        client.getBinder().getODataEntitySet(new ResWrap<EntityCollection>((URI) null, null,
            client.getBinder().getEntitySet(entitySet)));
    assertEquals(entitySet, written);
  }

  @Test
  public void testOperations() throws Exception {
    final InputStream input = getClass().
        getResourceAsStream("CustomersWithOperations." + getSuffix(ContentType.APPLICATION_JSON));
    final ClientEntitySet entitySet = client.getBinder().getODataEntitySet(
        client.getDeserializer(ContentType.APPLICATION_JSON).toEntitySet(input));
    assertNotNull(entitySet);

    assertEquals(2, entitySet.getEntities().size());
    assertNull(entitySet.getNext());
    assertEquals(1, entitySet.getOperations().size());
    assertEquals("Microsoft.Test.OData.Services.ODataWCFService.BAESAllPrimRTETAllPrim",
        entitySet.getOperations().get(0).getTitle());
  }

  @Test
  public void fromAtom() throws Exception {
    read(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void fromJSON() throws Exception {
    read(ContentType.JSON);
  }

  private void ref(final ContentType contentType) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("collectionOfEntityReferences." + getSuffix(contentType));
    final ClientEntitySet entitySet = client.getBinder().getODataEntitySet(
        client.getDeserializer(contentType).toEntitySet(input));
    assertNotNull(entitySet);

    for (ClientEntity entity : entitySet.getEntities()) {
      assertNotNull(entity.getId());
    }
    entitySet.setCount(entitySet.getEntities().size());

    final ClientEntitySet written =
        client.getBinder().getODataEntitySet(new ResWrap<EntityCollection>((URI) null, null,
            client.getBinder().getEntitySet(entitySet)));
    assertEquals(entitySet, written);
  }

  @Test
  public void atomRef() throws Exception {
    ref(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void jsonRef() throws Exception {
    ref(ContentType.JSON);
  }
  
  @Test
  public void testContainmentNav() throws Exception {
    final InputStream input = getClass().getResourceAsStream("containmentNav1." + 
	getSuffix(ContentType.JSON_FULL_METADATA));
    final ClientEntitySet entity = getEdmEnabledClient1().getBinder().
	getODataEntitySet(client.getDeserializer(
	ContentType.JSON_FULL_METADATA).toEntitySet(input));
    assertNotNull(entity);
    assertEquals("olingo.odata.test1.ETTwoCont", 
        entity.getEntities().get(0).getTypeName().getFullQualifiedNameAsString());
    assertEquals("olingo.odata.test1.ETTwoCont", 
        entity.getEntities().get(1).getTypeName().getFullQualifiedNameAsString());
  }
  
  @Test
  public void testClientEntitySet() throws Exception {
    final EdmEnabledODataClientImpl client = new EdmEnabledODataClientImpl(null, 
        getEdmEnabledClient1().getCachedEdm(), null);
    assertNotNull(client);
    assertNull(client.getServiceRoot());
    client.newURIBuilder();
    assertNotNull(client.getCachedEdm());
    assertNotNull(client.getEdm(null));
    assertNotNull(client.getInvokeRequestFactory());
  }
  
  @Test
  public void testContainmentNavOnSingleton() throws Exception {
    final InputStream input = getClass().getResourceAsStream("containmentNav4." + 
  getSuffix(ContentType.JSON_FULL_METADATA));
    final ClientEntitySet entity = getEdmEnabledClient1().getBinder().
  getODataEntitySet(client.getDeserializer(
  ContentType.JSON_FULL_METADATA).toEntitySet(input));
    assertNotNull(entity);
    assertEquals("olingo.odata.test1.ETCont", 
        entity.getEntities().get(0).getTypeName().getFullQualifiedNameAsString());
    assertEquals("olingo.odata.test1.ETCont", 
        entity.getEntities().get(1).getTypeName().getFullQualifiedNameAsString());
  }
}
