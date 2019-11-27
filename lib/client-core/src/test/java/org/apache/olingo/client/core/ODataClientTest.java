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

import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.uri.SearchFactory;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

public class ODataClientTest {

  @Test
  public void before() {
    ODataClient client = ODataClientFactory.getClient();
    assertNotNull(client);
    assertEquals(ODataServiceVersion.V40, client.getServiceVersion());
  }
  
  @Test
  public void clientImplTest() {
    ODataClientImpl client = (ODataClientImpl) ODataClientFactory.getClient();
    assertNotNull(client);
    assertNotNull(client.newPreferences());
    assertNotNull(client.getAsyncRequestFactory());
    assertNotNull(client.getRetrieveRequestFactory());
    assertNotNull(client.getCUDRequestFactory());
    assertNotNull(client.getInvokeRequestFactory());
    assertNotNull(client.getBatchRequestFactory());
    assertEquals(ODataServiceVersion.V40, client.getServiceVersion());
  }
  
  @Test
  public void clientFactoryTest() {
    assertNotNull(ODataClientFactory.getClient());
    assertNotNull(ODataClientFactory.getEdmEnabledClient(null));
    assertNotNull(ODataClientFactory.getEdmEnabledClient(null, null));
    assertNotNull(ODataClientFactory.getEdmEnabledClient(null, null, null));
    assertNotNull(ODataClientFactory.getEdmEnabledClient(null, null, null, null));
  }
  
  @Test
  public void searchTest() {
    ODataClient client = ODataClientFactory.getClient();
    assertNotNull(client);
    SearchFactory searchFactory = client.getSearchFactory();
    assertNotNull(searchFactory);
    LiteralSearch literal = (LiteralSearch) searchFactory.literal("test");
    assertNotNull(literal);
    assertEquals("test", literal.build());
    AndSearch and = (AndSearch) searchFactory.and(literal, literal);
    assertNotNull(and);
    assertEquals("(test AND test)", and.build());
    OrSearch or = (OrSearch) searchFactory.or(literal, literal);
    assertNotNull(or);
    assertEquals("(test OR test)", or.build());
    NotSearch not = (NotSearch) searchFactory.not(literal);
    assertNotNull(not);
    assertEquals("NOT (test)", not.build());
  }
  
  @Test
  public void configurationTest() {
    ODataClient client = ODataClientFactory.getClient();
    assertNotNull(client);
    ConfigurationImpl config = (ConfigurationImpl) client.getConfiguration();
    assertNotNull(config);
    assertNotNull(config.getDefaultBatchAcceptFormat());
    assertNotNull(config.getDefaultFormat());
    assertNotNull(config.getDefaultMediaFormat());
    assertNotNull(config.getDefaultPubFormat());
    assertNotNull(config.getDefaultValueFormat());
    assertNotNull(config.getExecutor());
    assertNotNull(config.getHttpClientFactory());
    assertNotNull(config.getHttpUriRequestFactory());
    config.setAddressingDerivedTypes(true);
    assertEquals(true, config.isAddressingDerivedTypes());
    config.setContinueOnError(true);
    assertEquals(true, config.isContinueOnError());
    config.setGzipCompression(true);
    assertEquals(true, config.isGzipCompression());
    config.setKeyAsSegment(true);
    assertEquals(true, config.isKeyAsSegment());
    config.setUseChuncked(true);
    assertEquals(true, config.isUseChuncked());
    config.setUseUrlOperationFQN(true);
    assertEquals(true, config.isUseUrlOperationFQN());
    config.setUseXHTTPMethod(true);
    assertEquals(true, config.isUseXHTTPMethod());
    config.setDefaultBatchAcceptFormat(ContentType.APPLICATION_ATOM_SVC);
    assertEquals(ContentType.APPLICATION_ATOM_SVC, config.getDefaultBatchAcceptFormat());
    config.setDefaultMediaFormat(ContentType.APPLICATION_ATOM_SVC);
    assertEquals(ContentType.APPLICATION_ATOM_SVC, config.getDefaultMediaFormat());
    config.setDefaultPubFormat(ContentType.APPLICATION_ATOM_SVC);
    assertEquals(ContentType.APPLICATION_ATOM_SVC, config.getDefaultPubFormat());
    config.setDefaultValueFormat(ContentType.APPLICATION_ATOM_SVC);
    assertEquals(ContentType.APPLICATION_ATOM_SVC, config.getDefaultValueFormat());
    config.setExecutor(null);
    assertNull(config.getExecutor());
    config.setHttpClientFactory(null);
    assertNull(config.getHttpClientFactory());
    config.setHttpUriRequestFactory(null);
    assertNull(config.getHttpUriRequestFactory());
    config.setProperty("key", "value");
    assertEquals("value", config.getProperty("key", "value"));
  }
}
