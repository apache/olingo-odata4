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

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class EntitySetTest extends AbstractTest {

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
}
