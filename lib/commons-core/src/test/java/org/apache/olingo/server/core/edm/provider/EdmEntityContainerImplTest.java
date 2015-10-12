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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.core.edm.EdmEntityContainerImpl;
import org.apache.olingo.commons.core.edm.EdmProviderImpl;
import org.junit.Before;
import org.junit.Test;

public class EdmEntityContainerImplTest {

  EdmEntityContainer container;

  @Before
  public void setup() {
    CsdlEdmProvider provider = new CustomProvider();
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    CsdlEntityContainerInfo entityContainerInfo =
        new CsdlEntityContainerInfo().setContainerName(new FullQualifiedName("space", "name"));
    container = new EdmEntityContainerImpl(edm, provider, entityContainerInfo);
  }

  @Test
  public void getAllEntitySetInitial() {
    List<EdmEntitySet> entitySets = container.getEntitySets();
    assertNotNull(entitySets);
    assertEquals(2, entitySets.size());
  }

  @Test
  public void getAllEntitySetsAfterOneWasAlreadyLoaded() {
    container.getEntitySet("entitySetName");
    List<EdmEntitySet> entitySets = container.getEntitySets();
    assertNotNull(entitySets);
    assertEquals(2, entitySets.size());
  }

  @Test
  public void getAllSingletonsInitial() {
    List<EdmSingleton> singletons = container.getSingletons();
    assertNotNull(singletons);
    assertEquals(2, singletons.size());
  }

  @Test
  public void getAllSingletonsAfterOneWasAlreadyLoaded() {
    container.getSingleton("singletonName");
    List<EdmSingleton> singletons = container.getSingletons();
    assertNotNull(singletons);
    assertEquals(2, singletons.size());
  }

  @Test
  public void getAllActionImportsInitial() {
    List<EdmActionImport> actionImports = container.getActionImports();
    assertNotNull(actionImports);
    assertEquals(2, actionImports.size());
  }

  @Test
  public void getAllActionImportsAfterOneWasAlreadyLoaded() {
    container.getActionImport("actionImportName");
    List<EdmActionImport> actionImports = container.getActionImports();
    assertNotNull(actionImports);
    assertEquals(2, actionImports.size());
  }

  @Test
  public void getAllFunctionImportsInitial() {
    List<EdmFunctionImport> functionImports = container.getFunctionImports();
    assertNotNull(functionImports);
    assertEquals(2, functionImports.size());
  }

  @Test
  public void getAllFunctionImportsAfterOneWasAlreadyLoaded() {
    container.getFunctionImport("functionImportName");
    List<EdmFunctionImport> functionImports = container.getFunctionImports();
    assertNotNull(functionImports);
    assertEquals(2, functionImports.size());
  }

  @Test
  public void checkEdmExceptionConversion() throws Exception {
    CsdlEdmProvider provider = mock(CsdlEdmProvider.class);
    FullQualifiedName containerName = new FullQualifiedName("space", "name");
    when(provider.getEntitySet(containerName, null)).thenThrow(new ODataException("msg"));
    when(provider.getSingleton(containerName, null)).thenThrow(new ODataException("msg"));
    when(provider.getFunctionImport(containerName, null)).thenThrow(new ODataException("msg"));
    when(provider.getActionImport(containerName, null)).thenThrow(new ODataException("msg"));
    EdmProviderImpl edm = new EdmProviderImpl(provider);
    CsdlEntityContainerInfo entityContainerInfo =
        new CsdlEntityContainerInfo().setContainerName(containerName);
    EdmEntityContainer container = new EdmEntityContainerImpl(edm, provider, entityContainerInfo);
    try {
      container.getEntitySet(null);
      fail("Expected EdmException not thrown");
    } catch (EdmException e) {
    }
    try {
      container.getSingleton(null);
      fail("Expected EdmException not thrown");
    } catch (EdmException e) {
    }
    try {
      container.getActionImport(null);
      fail("Expected EdmException not thrown");
    } catch (EdmException e) {
    }
    try {
      container.getFunctionImport(null);
      fail("Expected EdmException not thrown");
    } catch (EdmException e) {
    }
  }

  @Test
  public void simpleContainerGetter() {
    assertEquals("name", container.getName());
    assertEquals("space", container.getNamespace());
    assertEquals(new FullQualifiedName("space.name"), container.getFullQualifiedName());
  }

  @Test
  public void getExistingFunctionImport() {
    EdmFunctionImport functionImport = container.getFunctionImport("functionImportName");
    assertNotNull(functionImport);
    assertEquals("functionImportName", functionImport.getName());
    // Caching
    assertTrue(functionImport == container.getFunctionImport("functionImportName"));
  }

  @Test
  public void getNonExistingFunctionImport() {
    assertNull(container.getFunctionImport(null));
  }

  @Test
  public void getExistingActionImport() {
    EdmActionImport actionImport = container.getActionImport("actionImportName");
    assertNotNull(actionImport);
    assertEquals("actionImportName", actionImport.getName());
    // Caching
    assertTrue(actionImport == container.getActionImport("actionImportName"));
  }

  @Test
  public void getNonExistingActionImport() {
    assertNull(container.getActionImport(null));
  }

  @Test
  public void getExistingSingleton() {
    EdmSingleton singleton = container.getSingleton("singletonName");
    assertNotNull(singleton);
    assertEquals("singletonName", singleton.getName());
    // Caching
    assertTrue(singleton == container.getSingleton("singletonName"));
  }

  @Test
  public void getNonExistingSingleton() {
    assertNull(container.getSingleton(null));
  }

  @Test
  public void getExistingEntitySet() {
    EdmEntitySet entitySet = container.getEntitySet("entitySetName");
    assertNotNull(entitySet);
    assertEquals("entitySetName", entitySet.getName());
    // Caching
    assertTrue(entitySet == container.getEntitySet("entitySetName"));
  }

  @Test
  public void getNonExistingEntitySet() {
    assertNull(container.getEntitySet(null));
  }

  private class CustomProvider extends CsdlAbstractEdmProvider {
    @Override
    public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
        throws ODataException {
      if (entitySetName != null) {
        return new CsdlEntitySet().setName("entitySetName");
      }
      return null;
    }

    @Override
    public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
        throws ODataException {
      if (singletonName != null) {
        return new CsdlSingleton().setName("singletonName");
      }
      return null;
    }

    @Override
    public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
        throws ODataException {
      if (actionImportName != null) {
        return new CsdlActionImport().setName("actionImportName");
      }
      return null;
    }

    @Override
    public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer,
        final String functionImportName)
        throws ODataException {
      if (functionImportName != null) {
        return new CsdlFunctionImport().setName("functionImportName");
      }
      return null;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
      CsdlEntityContainer container = new CsdlEntityContainer();
      List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
      entitySets.add(new CsdlEntitySet().setName("entitySetName"));
      entitySets.add(new CsdlEntitySet().setName("entitySetName2"));
      container.setEntitySets(entitySets);

      List<CsdlSingleton> singletons = new ArrayList<CsdlSingleton>();
      singletons.add(new CsdlSingleton().setName("singletonName"));
      singletons.add(new CsdlSingleton().setName("singletonName2"));
      container.setSingletons(singletons);

      List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();
      actionImports.add(new CsdlActionImport().setName("actionImportName"));
      actionImports.add(new CsdlActionImport().setName("actionImportName2"));
      container.setActionImports(actionImports);

      List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
      functionImports.add(new CsdlFunctionImport().setName("functionImportName"));
      functionImports.add(new CsdlFunctionImport().setName("functionImportName2"));
      container.setFunctionImports(functionImports);

      return container;
    }
  }
}
