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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.server.api.ODataServer;
import org.apache.olingo.server.api.serializer.ODataFormat;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.junit.Before;
import org.junit.Test;

public class ServiceDocumentTest {

  private Edm edm;

  @Before
  public void before() {

    EdmEntitySet edmEntitySet1 = mock(EdmEntitySet.class);
    when(edmEntitySet1.getName()).thenReturn("entitySetName1");
    when(edmEntitySet1.isIncludeInServiceDocument()).thenReturn(true);

    EdmEntitySet edmEntitySet2 = mock(EdmEntitySet.class);
    when(edmEntitySet2.getName()).thenReturn("entitySetName2");
    when(edmEntitySet2.isIncludeInServiceDocument()).thenReturn(true);

    EdmEntitySet edmEntitySet3 = mock(EdmEntitySet.class);
    when(edmEntitySet3.getName()).thenReturn("entitySetName3");
    when(edmEntitySet3.isIncludeInServiceDocument()).thenReturn(false);

    List<EdmEntitySet> entitySets = new ArrayList<EdmEntitySet>();
    entitySets.add(edmEntitySet1);
    entitySets.add(edmEntitySet2);
    entitySets.add(edmEntitySet3);

    EdmFunctionImport functionImport1 = mock(EdmFunctionImport.class);
    when(functionImport1.getName()).thenReturn("functionImport1");
    when(functionImport1.isIncludeInServiceDocument()).thenReturn(true);

    EdmFunctionImport functionImport2 = mock(EdmFunctionImport.class);
    when(functionImport2.getName()).thenReturn("functionImport2");
    when(functionImport2.isIncludeInServiceDocument()).thenReturn(true);

    EdmFunctionImport functionImport3 = mock(EdmFunctionImport.class);
    when(functionImport3.getName()).thenReturn("functionImport3");
    when(functionImport3.isIncludeInServiceDocument()).thenReturn(false);

    List<EdmFunctionImport> functionImports = new ArrayList<EdmFunctionImport>();
    functionImports.add(functionImport1);
    functionImports.add(functionImport2);
    functionImports.add(functionImport3);

    EdmSingleton singleton1 = mock(EdmSingleton.class);
    when(singleton1.getName()).thenReturn("singleton1");

    EdmSingleton singleton2 = mock(EdmSingleton.class);
    when(singleton2.getName()).thenReturn("singleton2");

    EdmSingleton singleton3 = mock(EdmSingleton.class);
    when(singleton3.getName()).thenReturn("singleton3");

    List<EdmSingleton> singletons = new ArrayList<EdmSingleton>();
    singletons.add(singleton1);
    singletons.add(singleton2);
    singletons.add(singleton3);

    EdmEntityContainer edmEntityContainer = mock(EdmEntityContainer.class);
    when(edmEntityContainer.getEntitySets()).thenReturn(entitySets);
    when(edmEntityContainer.getFunctionImports()).thenReturn(functionImports);
    when(edmEntityContainer.getSingletons()).thenReturn(singletons);

    edm = mock(Edm.class);
    when(edm.getEntityContainer(null)).thenReturn(edmEntityContainer);
  }

  @Test
  public void writeServiceDocumentJson() throws Exception {
    String serviceRoot = "http://localhost:8080/odata.svc";

    ODataServer server = ODataServer.newInstance();
    assertNotNull(server);

    ODataSerializer serializer = server.getSerializer(ODataFormat.JSON);
    assertNotNull(serializer);

    InputStream result = serializer.serviceDocument(edm, serviceRoot);
    assertNotNull(result);
    String jsonString = IOUtils.toString(result);

    assertTrue(jsonString.contains("entitySetName1"));
    assertTrue(jsonString.contains("entitySetName2"));
    assertFalse(jsonString.contains("entitySetName3"));

    assertTrue(jsonString.contains("functionImport1"));
    assertTrue(jsonString.contains("functionImport2"));
    assertFalse(jsonString.contains("functionImport3"));

    assertTrue(jsonString.contains("singleton1"));
    assertTrue(jsonString.contains("singleton2"));
    assertTrue(jsonString.contains("singleton3"));
  }
}
