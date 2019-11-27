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
package org.apache.olingo.client.core.communication.request.retrieve;

import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.olingo.client.core.ODataClientFactory;
import org.apache.olingo.client.core.ODataClientImpl;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class RetrieveRequestTest {

  @Test
  public void testEdmMetadata() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    EdmMetadataRequestImpl edmMetadata = (EdmMetadataRequestImpl) factory
        .getMetadataRequest("metadata");
    assertNotNull(edmMetadata);
    assertNotNull(edmMetadata.addCustomHeader("name", "value"));
    assertNotNull(edmMetadata.getDefaultFormat());
    assertNotNull(edmMetadata.setAccept(ContentType.VALUE_ODATA_METADATA_FULL));
    assertNotNull(edmMetadata.setContentType(ContentType.VALUE_ODATA_METADATA_FULL));
  }
  
  @Test
  public void testXmlMetadata() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    XMLMetadataRequestImpl metadata = (XMLMetadataRequestImpl) factory
        .getXMLMetadataRequest("test");
    assertNotNull(metadata);
  }
  
  @Test
  public void testDeltaRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataDeltaRequestImpl delta = (ODataDeltaRequestImpl) factory
        .getDeltaRequest(uri);
    assertNotNull(delta);
    assertNotNull(delta.getDefaultFormat());
   }
  
  @Test
  public void testEntityRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataEntityRequestImpl req = (ODataEntityRequestImpl) factory
        .getEntityRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testEntitySetIteratorRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataEntitySetIteratorRequestImpl req = (ODataEntitySetIteratorRequestImpl) factory
        .getEntitySetIteratorRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testEntitySetRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataEntitySetRequestImpl req = (ODataEntitySetRequestImpl) factory
        .getEntitySetRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testMediaRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost","8080","","","$value");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataMediaRequestImpl req = (ODataMediaRequestImpl) factory
        .getMediaEntityRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testPropertyRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataPropertyRequestImpl req = (ODataPropertyRequestImpl) factory
        .getPropertyRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testRawRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataRawRequestImpl req = (ODataRawRequestImpl) factory
        .getRawRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testServiceDocumentRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataServiceDocumentRequestImpl req = (ODataServiceDocumentRequestImpl) factory
        .getServiceDocumentRequest("doc");
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
  @Test
  public void testValueRequest() throws URISyntaxException {

    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    URI uri = new URI("localhost:8080");
    RetrieveRequestFactoryImpl factory = (RetrieveRequestFactoryImpl) client
        .getRetrieveRequestFactory();
    assertNotNull(factory);
    ODataValueRequestImpl req = (ODataValueRequestImpl) factory
        .getValueRequest(uri);
    assertNotNull(req);
    assertNotNull(req.getDefaultFormat());
   }
  
}
