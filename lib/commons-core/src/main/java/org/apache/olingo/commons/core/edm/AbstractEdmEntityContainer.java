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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public abstract class AbstractEdmEntityContainer extends EdmNamedImpl implements EdmEntityContainer {

  protected final FullQualifiedName entityContainerName;

  protected final Map<String, EdmSingleton> singletons = new HashMap<String, EdmSingleton>();
  private boolean allSingletonsLoaded = false;

  protected final Map<String, EdmEntitySet> entitySets = new HashMap<String, EdmEntitySet>();
  private boolean allEntitySetsLoaded = false;

  protected final Map<String, EdmActionImport> actionImports = new HashMap<String, EdmActionImport>();
  private boolean allActionImportsLoaded = false;

  protected final Map<String, EdmFunctionImport> functionImports = new HashMap<String, EdmFunctionImport>();
  private boolean allFunctionImportsLoaded = false;

  public AbstractEdmEntityContainer(final Edm edm, final FullQualifiedName entityContainerName) {
    super(edm, entityContainerName.getName());
    this.entityContainerName = entityContainerName;
  }

  @Override
  public String getNamespace() {
    return entityContainerName.getNamespace();
  }

  protected abstract EdmSingleton createSingleton(String singletonName);

  @Override
  public EdmSingleton getSingleton(final String singletonName) {
    EdmSingleton singleton = singletons.get(singletonName);
    if (singleton == null) {
      singleton = createSingleton(singletonName);
      singletons.put(singletonName, singleton);
    }
    return singleton;
  }

  protected abstract EdmEntitySet createEntitySet(String entitySetName);

  @Override
  public EdmEntitySet getEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = entitySets.get(entitySetName);
    if (entitySet == null) {
      entitySet = createEntitySet(entitySetName);
      entitySets.put(entitySetName, entitySet);
    }
    return entitySet;
  }

  protected abstract EdmActionImport createActionImport(String actionImportName);

  @Override
  public EdmActionImport getActionImport(final String actionImportName) {
    EdmActionImport actionImport = actionImports.get(actionImportName);
    if (actionImport == null) {
      actionImport = createActionImport(actionImportName);
      actionImports.put(actionImportName, actionImport);
    }
    return actionImport;
  }

  protected abstract EdmFunctionImport createFunctionImport(String functionImportName);

  @Override
  public EdmFunctionImport getFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = functionImports.get(functionImportName);
    if (functionImport == null) {
      functionImport = createFunctionImport(functionImportName);
      functionImports.put(functionImportName, functionImport);
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

  protected abstract void loadAllEntitySets();

  @Override
  public List<EdmFunctionImport> getFunctionImports() {
    if (!allFunctionImportsLoaded) {
      loadAllFunctionImports();
      allFunctionImportsLoaded = true;
    }
    return new ArrayList<EdmFunctionImport>(functionImports.values());
  }

  protected abstract void loadAllFunctionImports();

  @Override
  public List<EdmSingleton> getSingletons() {
    if (!allSingletonsLoaded) {
      loadAllSingletons();
      allSingletonsLoaded = true;
    }
    return new ArrayList<EdmSingleton>(singletons.values());
  }

  protected abstract void loadAllSingletons();

  @Override
  public List<EdmActionImport> getActionImports() {
    if (!allActionImportsLoaded) {
      loadAllActionImports();
      allActionImportsLoaded = true;
    }
    return new ArrayList<EdmActionImport>(actionImports.values());
  }

  protected abstract void loadAllActionImports();
}
