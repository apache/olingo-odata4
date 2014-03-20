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
package org.apache.olingo.client.core.v3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.domain.ODataEntitySet;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.apache.olingo.client.core.op.impl.ResourceFactory;
import org.junit.Test;

public class EntitySetTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v3Client;
  }

  private void read(final ODataPubFormat format) throws IOException {
    final InputStream input = getClass().getResourceAsStream("Customer." + getSuffix(format));
    final ODataEntitySet entitySet = getClient().getBinder().getODataEntitySet(
            getClient().getDeserializer().toFeed(input, format));
    assertNotNull(entitySet);

    assertEquals(2, entitySet.getEntities().size());
    assertNotNull(entitySet.getNext());

    final ODataEntitySet written = getClient().getBinder().getODataEntitySet(getClient().
            getBinder().getFeed(entitySet, ResourceFactory.feedClassForFormat(format == ODataPubFormat.ATOM)));
    assertEquals(entitySet, written);
  }

  @Test
  public void fromAtom() throws IOException {
    read(ODataPubFormat.ATOM);
  }

  @Test
  public void fromJSON() throws IOException {
    read(ODataPubFormat.JSON);
  }
}
