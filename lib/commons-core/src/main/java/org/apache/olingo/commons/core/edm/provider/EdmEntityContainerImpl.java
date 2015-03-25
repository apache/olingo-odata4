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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.commons.api.edm.provider.Singleton;

public class EdmEntityContainerImpl extends EdmNamedImpl implements EdmEntityContainer {

  private final EdmProvider provider;
  private EntityContainer container;
  private EdmAnnotationHelperImpl helper;

  protected final FullQualifiedName entityContainerName;
  private final FullQualifiedName parentContainerName;

  protected final Map<String, EdmSingleton> singletons = new HashMap<String, EdmSingleton>();
  private boolean allSingletonsLoaded = false;

  protected final Map<String, EdmEntitySet> entitySets = new HashMap<String, EdmEntitySet>();
  private boolean allEntitySetsLoaded = false;

  protected final Map<String, EdmActionImport> actionImports = new HashMap<String, EdmActionImport>();
  private boolean allActionImportsLoaded = false;

  protected final Map<String, EdmFunctionImport> functionImports = new HashMap<String, EdmFunctionImport>();
  private boolean allFunctionImportsLoaded = false;

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider, 
      final EntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName().getName());
    this.provider = provider;
    this.entityContainerName = entityContainerInfo.getContainerName();
    this.parentContainerName = entityContainerInfo.getExtendsContainer();
  }

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider, final FullQualifiedName containerFQN,
      final EntityContainer entityContainer) {
    super(edm, containerFQN.getName());
    this.provider = provider;
    container = entityContainer;
    this.entityContainerName = containerFQN;
    this.parentContainerName = entityContainer.getExtendsContainerFQN();
    this.helper = new EdmAnnotationHelperImpl(edm, entityContainer);
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
    EdmSingleton singleton = singletons.get(singletonName);
    if (singleton == null) {
      singleton = createSingleton(singletonName);
      if (singleton != null) {
        singletons.put(singletonName, singleton);
      }
    }
    return singleton;
  }

  @Override
  public EdmEntitySet getEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = entitySets.get(entitySetName);
    if (entitySet == null) {
      entitySet = createEntitySet(entitySetName);
      if (entitySet != null) {
        entitySets.put(entitySetName, entitySet);
      }
    }
    return entitySet;
  }

  @Override
  public EdmActionImport getActionImport(final String actionImportName) {
    EdmActionImport actionImport = actionImports.get(actionImportName);
    if (actionImport == null) {
      actionImport = createActionImport(actionImportName);
      if (actionImport != null) {
        actionImports.put(actionImportName, actionImport);
      }
    }
    return actionImport;
  }

  @Override
  public EdmFunctionImport getFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = functionImports.get(functionImportName);
    if (functionImport == null) {
      functionImport = createFunctionImport(functionImportName);
      if (functionImport != null) {
        functionImports.put(functionImportName, functionImport);
      }
    }
    return functionImport;
  }

  @Override
  public List<EdmEntitySet> getEntitySets() {
    if (!allEntitySetsLoaded) {
      loadAllEntitySets();
      allEntitySetsLoaded = true;
    }
    return new ArrayList<EdmEntitySet>(entitySets.values());
  }

  @Override
  public List<EdmFunctionImport> getFunctionImports() {
    if (!allFunctionImportsLoaded) {
      loadAllFunctionImports();
      allFunctionImportsLoaded = true;
    }
    return new ArrayList<EdmFunctionImport>(functionImports.values());
  }

  @Override
  public List<EdmSingleton> getSingletons() {
    if (!allSingletonsLoaded) {
      loadAllSingletons();
      allSingletonsLoaded = true;
    }
    return new ArrayList<EdmSingleton>(singletons.values());
  }

  @Override
  public List<EdmActionImport> getActionImports() {
    if (!allActionImportsLoaded) {
      loadAllActionImports();
      allActionImportsLoaded = true;
    }
    return new ArrayList<EdmActionImport>(actionImports.values());
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
    if (providerEntitySets != null) {
      for (EntitySet entitySet : providerEntitySets) {
        if (!entitySets.containsKey(entitySet.getName())) {
          EdmEntitySetImpl impl = new EdmEntitySetImpl(edm, this, entitySet);
          entitySets.put(impl.getName(), impl);
        }
      }
    }
  }

  protected void loadAllFunctionImports() {
    loadContainer();
    List<FunctionImport> providerFunctionImports = container.getFunctionImports();
    if (providerFunctionImports != null) {
      for (FunctionImport functionImport : providerFunctionImports) {
        String functionName = functionImport.getName();
        if (!functionImports.containsKey(functionName)) {
          functionImports.put(functionName,
              new EdmFunctionImportImpl(edm, this, functionImport));
        }
      }
    }

  }

  protected void loadAllSingletons() {
    loadContainer();
    List<Singleton> providerSingletons = container.getSingletons();
    if (providerSingletons != null) {
      for (Singleton singleton : providerSingletons) {
        if (!singletons.containsKey(singleton.getName())) {
          EdmSingletonImpl impl = new EdmSingletonImpl(edm, this, singleton);
          singletons.put(singleton.getName(), impl);
        }
      }
    }

  }

  protected void loadAllActionImports() {
    loadContainer();
    List<ActionImport> providerActionImports = container.getActionImports();
    if (providerActionImports != null) {
      for (ActionImport actionImport : providerActionImports) {
        if (!actionImports.containsKey(actionImport.getName())) {
          EdmActionImportImpl impl = new EdmActionImportImpl(edm, this, actionImport);
          actionImports.put(actionImport.getName(), impl);
        }
      }
    }

  }

  private void loadContainer() {
    if (container == null) {
      try {
        container = provider.getEntityContainer();
        if (container == null) {
          // TODO: Should we throw an exception here?
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

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper == null ? null : helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper == null ? Collections.<EdmAnnotation> emptyList() : helper.getAnnotations();
  }
}
