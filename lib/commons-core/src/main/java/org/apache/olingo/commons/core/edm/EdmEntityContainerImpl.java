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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;

public class EdmEntityContainerImpl extends AbstractEdmNamed implements EdmEntityContainer {

  private final CsdlEdmProvider provider;
  private CsdlEntityContainer container;

  private final FullQualifiedName entityContainerName;
  private final FullQualifiedName parentContainerName;

  private List<EdmSingleton> singletons;
  private final Map<String, EdmSingleton> singletonCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmSingleton>());
  private List<EdmEntitySet> entitySets;
  private final Map<String, EdmEntitySet> entitySetCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmEntitySet>());
  private List<EdmActionImport> actionImports;
  private final Map<String, EdmActionImport> actionImportCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmActionImport>());
  private List<EdmFunctionImport> functionImports;
  private final Map<String, EdmFunctionImport> functionImportCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmFunctionImport>());

  public EdmEntityContainerImpl(final Edm edm, final CsdlEdmProvider provider,
      final CsdlEntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName().getName(), null);
    this.provider = provider;
    entityContainerName = entityContainerInfo.getContainerName();
    parentContainerName = entityContainerInfo.getExtendsContainer();
  }

  public EdmEntityContainerImpl(final Edm edm, final CsdlEdmProvider provider, final FullQualifiedName containerFQN,
      final CsdlEntityContainer entityContainer) {
    super(edm, containerFQN.getName(), entityContainer);
    this.provider = provider;
    container = entityContainer;
    entityContainerName = containerFQN;
    parentContainerName = entityContainer.getExtendsContainerFQN();
  }

  @Override
  public String getNamespace() {
    return entityContainerName.getNamespace();
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return entityContainerName;
  }

  @Override
  public EdmSingleton getSingleton(final String singletonName) {
    EdmSingleton singleton = singletonCache.get(singletonName);
    if (singleton == null) {
      singleton = createSingleton(singletonName);
      if (singleton != null) {
        singletonCache.put(singletonName, singleton);
      }
    }
    return singleton;
  }

  @Override
  public EdmEntitySet getEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = entitySetCache.get(entitySetName);
    if (entitySet == null) {
      entitySet = createEntitySet(entitySetName);
      if (entitySet != null) {
        entitySetCache.put(entitySetName, entitySet);
      }
    }
    return entitySet;
  }

  @Override
  public EdmActionImport getActionImport(final String actionImportName) {
    EdmActionImport actionImport = actionImportCache.get(actionImportName);
    if (actionImport == null) {
      actionImport = createActionImport(actionImportName);
      if (actionImport != null) {
        actionImportCache.put(actionImportName, actionImport);
      }
    }
    return actionImport;
  }

  @Override
  public EdmFunctionImport getFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = functionImportCache.get(functionImportName);
    if (functionImport == null) {
      functionImport = createFunctionImport(functionImportName);
      if (functionImport != null) {
        functionImportCache.put(functionImportName, functionImport);
      }
    }
    return functionImport;
  }

  @Override
  public List<EdmEntitySet> getEntitySets() {
    if (entitySets == null) {
      loadAllEntitySets();
    }
    return Collections.unmodifiableList(entitySets);
  }

  @Override
  public List<EdmFunctionImport> getFunctionImports() {
    if (functionImports == null) {
      loadAllFunctionImports();
    }
    return Collections.unmodifiableList(functionImports);
  }

  @Override
  public List<EdmSingleton> getSingletons() {
    if (singletons == null) {
      loadAllSingletons();
    }
    return Collections.unmodifiableList(singletons);
  }

  @Override
  public List<EdmActionImport> getActionImports() {
    if (actionImports == null) {
      loadAllActionImports();
    }
    return Collections.unmodifiableList(actionImports);
  }

  @Override
  public FullQualifiedName getParentContainerName() {
    return parentContainerName;
  }

  protected EdmSingleton createSingleton(final String singletonName) {
    EdmSingleton singleton = null;

    try {
      final CsdlSingleton providerSingleton = provider.getSingleton(entityContainerName, singletonName);
      if (providerSingleton != null) {
        singleton = new EdmSingletonImpl(edm, this, providerSingleton);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return singleton;
  }

  protected EdmEntitySet createEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = null;

    try {
      final CsdlEntitySet providerEntitySet = provider.getEntitySet(entityContainerName, entitySetName);
      if (providerEntitySet != null) {
        entitySet = new EdmEntitySetImpl(edm, this, providerEntitySet);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return entitySet;
  }

  protected EdmActionImport createActionImport(final String actionImportName) {
    EdmActionImport actionImport = null;

    try {
      final CsdlActionImport providerImport = provider.getActionImport(entityContainerName, actionImportName);
      if (providerImport != null) {
        actionImport = new EdmActionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return actionImport;
  }

  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = null;

    try {
      final CsdlFunctionImport providerImport = provider.getFunctionImport(entityContainerName, functionImportName);
      if (providerImport != null) {
        functionImport = new EdmFunctionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return functionImport;
  }

  protected void loadAllEntitySets() {
    loadContainer();
    final List<CsdlEntitySet> providerEntitySets = container.getEntitySets();
    final List<EdmEntitySet> entitySetsLocal = new ArrayList<EdmEntitySet>();

    if (providerEntitySets != null) {
      for (CsdlEntitySet entitySet : providerEntitySets) {
        final EdmEntitySetImpl impl = new EdmEntitySetImpl(edm, this, entitySet);
        entitySetCache.put(impl.getName(), impl);
        entitySetsLocal.add(impl);
      }
      entitySets = entitySetsLocal;
    }
  }

  protected void loadAllFunctionImports() {
    loadContainer();
    final List<CsdlFunctionImport> providerFunctionImports = container.getFunctionImports();
    final ArrayList<EdmFunctionImport> functionImportsLocal = new ArrayList<EdmFunctionImport>();

    if (providerFunctionImports != null) {
      for (CsdlFunctionImport functionImport : providerFunctionImports) {
        EdmFunctionImportImpl impl = new EdmFunctionImportImpl(edm, this, functionImport);
        functionImportCache.put(impl.getName(), impl);
        functionImportsLocal.add(impl);
      }
      functionImports = functionImportsLocal;
    }
  }

  protected void loadAllSingletons() {
    loadContainer();
    final List<CsdlSingleton> providerSingletons = container.getSingletons();
    final List<EdmSingleton> singletonsLocal = new ArrayList<EdmSingleton>();

    if (providerSingletons != null) {
      for (CsdlSingleton singleton : providerSingletons) {
        final EdmSingletonImpl impl = new EdmSingletonImpl(edm, this, singleton);
        singletonCache.put(singleton.getName(), impl);
        singletonsLocal.add(impl);
      }
      singletons = singletonsLocal;
    }
  }

  protected void loadAllActionImports() {
    loadContainer();
    final List<CsdlActionImport> providerActionImports = container.getActionImports();
    final List<EdmActionImport> actionImportsLocal = new ArrayList<EdmActionImport>();

    if (providerActionImports != null) {
      for (CsdlActionImport actionImport : providerActionImports) {
        final EdmActionImportImpl impl = new EdmActionImportImpl(edm, this, actionImport);
        actionImportCache.put(actionImport.getName(), impl);
        actionImportsLocal.add(impl);
      }
      actionImports = actionImportsLocal;
    }

  }

  private void loadContainer() {
    if (container == null) {
      try {
        CsdlEntityContainer containerLocal = provider.getEntityContainer();
        if (containerLocal == null) {
          containerLocal = new CsdlEntityContainer().setName(getName());
        }

        container = containerLocal;
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
  }
}
