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

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEntityContainer;
import org.apache.olingo.server.api.edm.provider.ActionImport;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.FunctionImport;
import org.apache.olingo.server.api.edm.provider.Singleton;

import java.util.List;

public class EdmEntityContainerImpl extends AbstractEdmEntityContainer {

  private final EdmProvider provider;

  private EntityContainer container;

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider,
      final EntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName(), entityContainerInfo.getExtendsContainer());
    this.provider = provider;
  }

  public EdmEntityContainerImpl(final Edm edm, final EdmProvider provider, final FullQualifiedName containerFQN,
      final EntityContainer entityContainer) {
    super(edm, containerFQN, entityContainer.getExtendsContainer());
    this.provider = provider;
    container = entityContainer;
  }

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  @Override
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

  @Override
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
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
