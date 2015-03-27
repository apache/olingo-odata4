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
package org.apache.olingo.commons.core.edm.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.commons.api.edm.provider.Singleton;

public class EdmEntityContainerImpl extends AbstractEdmNamed implements EdmEntityContainer {

  private final EdmProvider provider;
  private EntityContainer container;

  private final FullQualifiedName entityContainerName;
  private final FullQualifiedName parentContainerName;

  private final Map<String, EdmSingleton> singletonCache = new LinkedHashMap<String, EdmSingleton>();
  private List<EdmSingleton> singletons;

  private final Map<String, EdmEntitySet> entitySetCache = new LinkedHashMap<String, EdmEntitySet>();
  private List<EdmEntitySet> entitySets;

  private final Map<String, EdmActionImport> actionImportCache = new LinkedHashMap<String, EdmActionImport>();
  private List<EdmActionImport> actionImports;

  private final Map<String, EdmFunctionImport> functionImportCache = new LinkedHashMap<String, EdmFunctionImport>();
  private List<EdmFunctionImport> functionImports;

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider,
      final EntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName().getName(), null);
    this.provider = provider;
    this.entityContainerName = entityContainerInfo.getContainerName();
    this.parentContainerName = entityContainerInfo.getExtendsContainer();
  }

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider, final FullQualifiedName containerFQN,
      final EntityContainer entityContainer) {
    super(edm, containerFQN.getName(), entityContainer);
    this.provider = provider;
    container = entityContainer;
    this.entityContainerName = containerFQN;
    this.parentContainerName = entityContainer.getExtendsContainerFQN();
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
      final Singleton providerSingleton = provider.getSingleton(entityContainerName, singletonName);
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
      final EntitySet providerEntitySet = provider.getEntitySet(entityContainerName, entitySetName);
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
      final ActionImport providerImport = provider.getActionImport(entityContainerName, actionImportName);
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
      final FunctionImport providerImport = provider.getFunctionImport(entityContainerName, functionImportName);
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
    List<EntitySet> providerEntitySets = container.getEntitySets();
    entitySets = new ArrayList<EdmEntitySet>();
    if (providerEntitySets != null) {
      for (EntitySet entitySet : providerEntitySets) {
        EdmEntitySetImpl impl = new EdmEntitySetImpl(edm, this, entitySet);
        entitySetCache.put(impl.getName(), impl);
        entitySets.add(impl);
      }
    }
  }

  protected void loadAllFunctionImports() {
    loadContainer();
    List<FunctionImport> providerFunctionImports = container.getFunctionImports();
    functionImports = new ArrayList<EdmFunctionImport>();
    if (providerFunctionImports != null) {
      for (FunctionImport functionImport : providerFunctionImports) {
        EdmFunctionImportImpl impl = new EdmFunctionImportImpl(edm, this, functionImport);
        functionImportCache.put(impl.getName(), impl);
        functionImports.add(impl);
      }
    }

  }

  protected void loadAllSingletons() {
    loadContainer();
    List<Singleton> providerSingletons = container.getSingletons();
    singletons = new ArrayList<EdmSingleton>();
    if (providerSingletons != null) {
      for (Singleton singleton : providerSingletons) {
        EdmSingletonImpl impl = new EdmSingletonImpl(edm, this, singleton);
        singletonCache.put(singleton.getName(), impl);
        singletons.add(impl);
      }
    }

  }

  protected void loadAllActionImports() {
    loadContainer();
    List<ActionImport> providerActionImports = container.getActionImports();
    actionImports = new ArrayList<EdmActionImport>();
    if (providerActionImports != null) {
      for (ActionImport actionImport : providerActionImports) {
        EdmActionImportImpl impl = new EdmActionImportImpl(edm, this, actionImport);
        actionImportCache.put(actionImport.getName(), impl);
        actionImports.add(impl);
      }
    }

  }

  private void loadContainer() {
    if (container == null) {
      try {
        container = provider.getEntityContainer();
        if (container == null) {
          container = new EntityContainer().setName(getName());
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
  }

  @Override
  public String getAnnotationsTargetPath() {
    return null;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return getFullQualifiedName();
  }

  @Override
  public TargetType getAnnotationsTargetType() {
    return TargetType.EntityContainer;
  }
}
