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

import org.apache.olingo.client.api.EdmEnabledODataClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.ODataClientBuilder;
import org.apache.olingo.commons.api.edm.Edm;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The ODataClientBuilderTest must be done in the core library because for test reason it is necessary
 * that the <code></code>ODataClientImpl</code> and <code>EdmEnabledODataClientImpl</code> classes are in the class
 * path.
 * Furthermore the <code>client-core</code> must have the dependency to the <code>client-api</code>
 * so that is ensured that the ODataClientBuilder is available.
 */
public class ODataClientBuilderTest {

  @Test
  public void testDefault() {
    ODataClient client = ODataClientBuilder.createClient();
    assertNotNull(client);
    assertTrue(client instanceof ODataClientImpl);
    assertFalse(client instanceof EdmEnabledODataClientImpl);

    EdmEnabledODataClient edmClient = ODataClientBuilder.with("http://serviceRoot").createClient();
    assertNotNull(client);
    assertTrue(edmClient instanceof ODataClientImpl);
    assertTrue(edmClient instanceof EdmEnabledODataClientImpl);
  }

  @Test
  public void testSystemProperty() {
    //CHECKSTYLE:OFF
    System.setProperty(ODataClientBuilder.ODATA_CLIENT_IMPL_SYS_PROPERTY, MyODataClient.class.getName());
    ODataClient client = ODataClientBuilder.createClient();
    assertNotNull(client);
    assertTrue(client instanceof ODataClientImpl);
    assertFalse(client instanceof EdmEnabledODataClientImpl);
    assertTrue(client instanceof MyODataClient);

    System.setProperty(ODataClientBuilder.ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY, MyEdmODataClient.class.getName());
    EdmEnabledODataClient edmClient = ODataClientBuilder.with("http://serviceRoot").createClient();
    assertNotNull(client);
    assertTrue(edmClient instanceof ODataClientImpl);
    assertTrue(edmClient instanceof EdmEnabledODataClientImpl);
    assertTrue(edmClient instanceof MyEdmODataClient);
    assertNull(edmClient.getCachedEdm());

    Edm edm = Mockito.mock(Edm.class);
    edmClient = ODataClientBuilder.with("http://serviceRoot").edm(edm).metadataETag("ETAG").createClient();
    assertNotNull(client);
    assertTrue(edmClient instanceof ODataClientImpl);
    assertTrue(edmClient instanceof EdmEnabledODataClientImpl);
    assertTrue(edmClient instanceof MyEdmODataClient);
    assertNotNull(edmClient.getCachedEdm());

    System.clearProperty(ODataClientBuilder.ODATA_CLIENT_IMPL_SYS_PROPERTY);
    System.clearProperty(ODataClientBuilder.ODATA_EMD_CLIENT_IMPL_SYS_PROPERTY);
    //CHECKSTYLE:ON
  }

  public static class MyODataClient extends ODataClientImpl {
  }

  public static class MyEdmODataClient extends EdmEnabledODataClientImpl {
    private Edm myEdm;
    public MyEdmODataClient(String serviceRoot, Edm edm, String metadataETag) {
      super(serviceRoot, edm, metadataETag);
      this.myEdm = edm;
    }

    @Override
    public Edm getCachedEdm() {
      return myEdm;
    }
  }
}
