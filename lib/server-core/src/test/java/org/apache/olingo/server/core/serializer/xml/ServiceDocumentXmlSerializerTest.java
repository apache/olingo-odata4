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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.core.ServiceMetadataImpl;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServiceDocumentXmlSerializerTest {
  private static ODataSerializer serializer;

  @BeforeClass
  public static void init() throws SerializerException {
    serializer = OData.newInstance().createSerializer(ContentType.APPLICATION_ATOM_XML);
  }

  @Test
  public void writeServiceWithEmptyMockedEdm() throws Exception {
    final Edm edm = mock(Edm.class);
    EdmEntityContainer container = mock(EdmEntityContainer.class);
    when(container.getFullQualifiedName()).thenReturn(new FullQualifiedName("service", "test"));
    when(container.getEntitySets()).thenReturn(Collections.<EdmEntitySet> emptyList());
    when(container.getFunctionImports()).thenReturn(Collections.<EdmFunctionImport> emptyList());
    when(container.getSingletons()).thenReturn(Collections.<EdmSingleton> emptyList());
    when(edm.getEntityContainer()).thenReturn(container);
    ServiceMetadata metadata = mock(ServiceMetadata.class);
    when(metadata.getEdm()).thenReturn(edm);

    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<app:service xmlns:atom=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:app=\"http://www.w3.org/2007/app\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "metadata:context=\"http://host/svc/$metadata\">"
        + "<app:workspace><atom:title>service.test</atom:title></app:workspace>"
        + "</app:service>",
        IOUtils.toString(serializer.serviceDocument(metadata, "http://host/svc").getContent()));
  }

  @Test
  public void writeServiceDocument() throws Exception {
    CsdlEdmProvider provider = new MetadataDocumentXmlSerializerTest.LocalProvider();
    ServiceMetadata serviceMetadata = new ServiceMetadataImpl(provider,
        Collections.<EdmxReference> emptyList(), null);
    InputStream metadataStream = serializer.serviceDocument(serviceMetadata, "http://host/svc").getContent();
    String metadata = IOUtils.toString(metadataStream);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<app:service xmlns:atom=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:app=\"http://www.w3.org/2007/app\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "metadata:context=\"http://host/svc/$metadata\">"
        + "<app:workspace>"
        + "<atom:title>org.olingo.container</atom:title>"
        + "<app:collection href=\"ESAllPrim\" metadata:name=\"ESAllPrim\">"
        + "<atom:title>ESAllPrim</atom:title>"
        + "</app:collection>"
        + "<metadata:function-import href=\"FINRTInt16\" metadata:name=\"FINRTInt16\">"
        + "<atom:title>FINRTInt16</atom:title>"
        + "</metadata:function-import>"
        + "<metadata:singleton href=\"SI\" metadata:name=\"SI\">"
        + "<atom:title>SI</atom:title>"
        + "</metadata:singleton>"
        + "</app:workspace>"
        + "</app:service>",
        metadata);
  }
}
