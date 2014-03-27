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
package org.apache.olingo.client.core.it.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.it.AbstractMetadataTestITCase;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.junit.Test;

public class MetadataTestITCase extends AbstractMetadataTestITCase {

  @Override
  protected ODataClient getClient() {
    return ODataClientFactory.getV4();
  }

  @Test
  public void retrieve() {
    final Edm metadata = getClient().getRetrieveRequestFactory().
            getMetadataRequest(getTestServiceRoot()).execute().getBody();
    assertNotNull(metadata);
  }

  @Test
  public void include() {
    final Edm metadata = getClient().getRetrieveRequestFactory().
            getMetadataRequest(getNorthwindServiceRoot()).execute().getBody();
    assertNotNull(metadata);

    final EdmEntityContainer container = metadata.getEntityContainer(
            new FullQualifiedName("ODataWebExperimental.Northwind.Model", "NorthwindEntities"));
    assertNotNull(container);

    final EdmEntitySet categories = container.getEntitySet("Categories");
    assertNotNull(categories);
    assertEquals("NorthwindModel", categories.getEntityType().getNamespace());
  }
}
