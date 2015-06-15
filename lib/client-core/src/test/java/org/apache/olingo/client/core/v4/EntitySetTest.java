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
package org.apache.olingo.client.core.v4;

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class EntitySetTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private void read(final ODataFormat format) throws IOException, ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("Customers." + getSuffix(format));
    final ClientEntitySet entitySet = getClient().getBinder().getODataEntitySet(
        getClient().getDeserializer(format).toEntitySet(input));
    assertNotNull(entitySet);

    assertEquals(2, entitySet.getEntities().size());
    assertNull(entitySet.getNext());

    final ClientEntitySet written =
        getClient().getBinder().getODataEntitySet(new ResWrap<EntityCollection>((URI) null, null,
            getClient().getBinder().getEntitySet(entitySet)));
    assertEquals(entitySet, written);
  }

  @Test
  public void fromAtom() throws Exception {
    read(ODataFormat.ATOM);
  }

  @Test
  public void fromJSON() throws Exception {
    read(ODataFormat.JSON);
  }

  private void ref(final ODataFormat format) throws ODataDeserializerException {
    final InputStream input = getClass().getResourceAsStream("collectionOfEntityReferences." + getSuffix(format));
    final ClientEntitySet entitySet = getClient().getBinder().getODataEntitySet(
        getClient().getDeserializer(format).toEntitySet(input));
    assertNotNull(entitySet);

    for (ClientEntity entity : entitySet.getEntities()) {
      assertNotNull(entity.getId());
    }
    entitySet.setCount(entitySet.getEntities().size());

    final ClientEntitySet written =
        getClient().getBinder().getODataEntitySet(new ResWrap<EntityCollection>((URI) null, null,
            getClient().getBinder().getEntitySet(entitySet)));
    assertEquals(entitySet, written);
  }

  @Test
  public void atomRef() throws Exception {
    ref(ODataFormat.ATOM);
  }

  @Test
  public void jsonRef() throws Exception {
    ref(ODataFormat.JSON);
  }
}
