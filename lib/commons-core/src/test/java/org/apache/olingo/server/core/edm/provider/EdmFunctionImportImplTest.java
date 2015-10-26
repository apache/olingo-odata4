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
package org.apache.olingo.server.core.edm.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.core.edm.EdmEntityContainerImpl;
import org.apache.olingo.commons.core.edm.EdmFunctionImportImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Test;

public class EdmFunctionImportImplTest {

  @Test
  public void functionImport() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    EdmProviderImpl edm = new EdmProviderImpl(provider);

    final FullQualifiedName functionName = new FullQualifiedName("ns", "function");
    final CsdlFunction functionProvider = new CsdlFunction()
        .setName(functionName.getName())
        .setParameters(Collections.<CsdlParameter> emptyList())
        .setBound(false)
        .setComposable(false)
        .setReturnType(new CsdlReturnType().setType(EdmPrimitiveTypeKind.Boolean.getFullQualifiedName()));
    when(provider.getFunctions(functionName)).thenReturn(Arrays.asList(functionProvider));

    final FullQualifiedName containerName = new FullQualifiedName("ns", "container");
    final CsdlEntityContainerInfo containerInfo = new CsdlEntityContainerInfo().setContainerName(containerName);
    when(provider.getEntityContainerInfo(containerName)).thenReturn(containerInfo);
    final EdmEntityContainer entityContainer = new EdmEntityContainerImpl(edm, provider, containerInfo);

    final String functionImportName = "functionImport";
    final CsdlFunctionImport functionImportProvider = new CsdlFunctionImport()
        .setName(functionImportName)
        .setTitle("title")
        .setFunction(functionName)
        .setIncludeInServiceDocument(true);
    when(provider.getFunctionImport(containerName, functionImportName)).thenReturn(functionImportProvider);

    final EdmFunctionImport functionImport = new EdmFunctionImportImpl(edm, entityContainer, functionImportProvider);
    assertEquals(functionImportName, entityContainer.getFunctionImport(functionImportName).getName());
    assertEquals("functionImport", functionImport.getName());
    assertEquals("title", functionImport.getTitle());
    assertEquals(new FullQualifiedName("ns", functionImportName), functionImport.getFullQualifiedName());
    assertTrue(functionImport.isIncludeInServiceDocument());
    final EdmFunction function = functionImport.getUnboundFunction(Collections.<String> emptyList());
    assertEquals(functionName.getNamespace(), function.getNamespace());
    assertEquals(functionName.getName(), function.getName());
    assertEquals(functionName, function.getFullQualifiedName());
    assertFalse(function.isBound());
    assertFalse(function.isComposable());
    assertEquals(EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean),
        function.getReturnType().getType());
    assertEquals(entityContainer, functionImport.getEntityContainer());
    assertNull(functionImport.getReturnedEntitySet());
    
    List<EdmFunction> functions = functionImport.getUnboundFunctions();
    assertNotNull(functions);
    assertEquals(1, functions.size());
  }
}
