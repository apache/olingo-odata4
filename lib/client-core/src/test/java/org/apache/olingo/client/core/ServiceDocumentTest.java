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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.client.api.domain.ClientServiceDocument;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class ServiceDocumentTest extends AbstractTest {

  private ClientServiceDocument parse(final ContentType contentType) throws ODataDeserializerException {
    ResWrap<ServiceDocument> service = client.getDeserializer(contentType).toServiceDocument(
        getClass().getResourceAsStream("serviceDocument." + getSuffix(contentType)));

    assertEquals(URI.create("http://host/service/$metadata"), service.getContextURL());
    assertEquals("W/\"MjAxMy0wNS0xM1QxNDo1NFo=\"", service.getMetadataETag());

    final ClientServiceDocument serviceDocument = client.getBinder().getODataServiceDocument(service.getPayload());
    assertNotNull(serviceDocument);

    assertTrue(serviceDocument.getEntitySetNames().contains("Order Details"));
    assertEquals(URI.create("http://host/service/TopProducts"),
        serviceDocument.getFunctionImportURI("TopProducts"));
    assertEquals(URI.create("http://host/HR/"),
        serviceDocument.getRelatedServiceDocumentsURIs().iterator().next());

    return serviceDocument;
  }

  @Test
  public void json() throws Exception {
    parse(ContentType.JSON);
  }

  @Test
  public void xml() throws Exception {
    parse(ContentType.APPLICATION_XML);
  }
}
