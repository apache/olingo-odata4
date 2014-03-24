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
package org.apache.olingo.client.core.v4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.client.core.AbstractTest;
import org.junit.Test;

public class ServiceDocumentTest extends AbstractTest {

  @Override
  protected ODataClient getClient() {
    return v4Client;
  }

  private String getFileExtension(final ODataFormat format) {
    return format == ODataFormat.XML ? "xml" : "json";
  }

  private ODataServiceDocument parse(final ODataFormat format) {
    final ODataServiceDocument serviceDocument = getClient().getReader().readServiceDocument(
            getClass().getResourceAsStream("serviceDocument." + getFileExtension(format)), format);
    assertNotNull(serviceDocument);
    assertEquals(URI.create("http://host/service/$metadata"), serviceDocument.getMetadataContext());
    assertTrue(serviceDocument.getEntitySetTitles().contains("Order Details"));
    assertEquals(URI.create("http://host/service/TopProducts"),
            serviceDocument.getFunctionImportURI("Best-Selling Products"));
    assertEquals(URI.create("http://host/HR/"),
            serviceDocument.getRelatedServiceDocumentsURIs().iterator().next());

    return serviceDocument;
  }

  @Test
  public void json() {
    parse(ODataFormat.JSON);
  }

  @Test
  public void xml() {
    final ODataServiceDocument serviceDocument = parse(ODataFormat.XML);
    assertEquals("W/\"MjAxMy0wNS0xM1QxNDo1NFo=\"", serviceDocument.getMetadataETag());
  }
}
