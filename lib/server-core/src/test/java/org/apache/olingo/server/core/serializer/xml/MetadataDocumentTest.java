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
package org.apache.olingo.server.core.serializer.xml;

import static org.mockito.Mockito.mock;

import java.io.InputStream;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.testutil.EdmTechProvider;
import org.apache.olingo.server.core.testutil.StringUtils;
import org.junit.Test;

public class MetadataDocumentTest {

  @Test(expected = ODataRuntimeException.class)
  public void metadataOnJsonResultsInException() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.JSON);
    serializer.metadataDocument(mock(Edm.class));
  }

  @Test
  public void writeMetadataWithMockedEdm() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.XML);
    Edm edm = mock(Edm.class);
    serializer.metadataDocument(edm);
  }

  @Test
  public void writeMetadataWithTechnicalScenario() {
    ODataSerializer serializer = ODataServer.newInstance().getSerializer(ODataFormat.XML);
    EdmProviderImpl edm = new EdmProviderImpl(new EdmTechProvider());
    InputStream metadata = serializer.metadataDocument(edm);
    System.out.println(StringUtils.inputStreamToString(metadata, true));
  }

}
