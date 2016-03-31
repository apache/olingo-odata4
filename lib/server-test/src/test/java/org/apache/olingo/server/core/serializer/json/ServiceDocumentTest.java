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
package org.apache.olingo.server.core.serializer.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;

public class ServiceDocumentTest {

  private static final String serviceRoot = "http://localhost:8080/odata.svc";
  private static final ServiceMetadata metadata = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList(),
      new ServiceMetadataETagSupport() {
        @Override
        public String getServiceDocumentETag() {
          return "W/\"serviceDocumentETag\"";
        }
        @Override
        public String getMetadataETag() {
          return "W/\"metadataETag\"";
        }
      });

  @Test
  public void writeServiceDocumentJson() throws Exception {
    OData server = OData.newInstance();
    assertNotNull(server);

    ODataSerializer serializer = server.createSerializer(ContentType.JSON);
    assertNotNull(serializer);

    InputStream result = serializer.serviceDocument(metadata, serviceRoot).getContent();
    assertNotNull(result);
    final String jsonString = IOUtils.toString(result);

    assertTrue(jsonString.contains(
        metadata.getServiceMetadataETagSupport().getMetadataETag().replace("\"", "\\\"")));

    assertTrue(jsonString.contains("ESAllPrim"));
    assertTrue(jsonString.contains("All PropertyTypes EntitySet"));
    assertTrue(jsonString.contains("ESCollAllPrim"));
    assertTrue(jsonString.contains("ESKeyNavCont"));
    assertFalse(jsonString.contains("ESInvisible"));

    assertTrue(jsonString.contains("FINRTInt16"));
    assertTrue(jsonString.contains("Simple FunctionImport"));
    assertTrue(jsonString.contains("FINRTCollETMixPrimCollCompTwoParam"));
    assertTrue(jsonString.contains("FICRTCollESKeyNavContParam"));
    assertFalse(jsonString.contains("FINInvisibleRTInt16"));
    assertTrue(jsonString.contains("FunctionImport"));

    assertTrue(jsonString.contains("SI"));
    assertTrue(jsonString.contains("Simple Singleton"));
    assertTrue(jsonString.contains("SINav"));
    assertTrue(jsonString.contains("SIMedia"));
    assertTrue(jsonString.contains("Singleton"));
  }

  @Test
  public void serviceDocumentNoMetadata() throws Exception {
    final String result = IOUtils.toString(
        OData.newInstance().createSerializer(ContentType.JSON_NO_METADATA)
            .serviceDocument(metadata, serviceRoot).getContent());
    assertFalse(result.contains("odata.context"));
    assertFalse(result.contains("odata.metadata"));
    assertTrue(result.contains("ESAllPrim"));
  }
}
