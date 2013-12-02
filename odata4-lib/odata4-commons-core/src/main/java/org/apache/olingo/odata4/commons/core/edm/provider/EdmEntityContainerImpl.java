/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.odata4.commons.api.edm.EdmActionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.helper.EntityContainerInfo;
import org.apache.olingo.odata4.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.odata4.commons.api.edm.provider.ActionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.exception.ODataException;


public class EdmEntityContainerImpl extends EdmNamedImpl implements EdmEntityContainer {

  private final FullQualifiedName entityContainerName;
  private final EdmProvider provider;
  private final Map<String, EdmSingleton> singletons = new HashMap<String, EdmSingleton>();
  private final Map<String, EdmEntitySet> entitySets = new HashMap<String, EdmEntitySet>();
  private final Map<String, EdmActionImport> actionImports = new HashMap<String, EdmActionImport>();
  private final Map<String, EdmFunctionImport> functionImports = new HashMap<String, EdmFunctionImport>();

  public EdmEntityContainerImpl(final EdmProviderImpl edm, final EdmProvider provider,
      final EntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName().getName());
    this.provider = provider;
    entityContainerName = entityContainerInfo.getContainerName();
  }

  @Override
  public String getNamespace() {
    return entityContainerName.getNamespace();
  }

  @Override
  public EdmSingleton getSingleton(final String singletonName) {
    EdmSingleton singleton = singletons.get(singletonName);
    if (singleton == null) {
      try {
        Singleton providerSingleton = provider.getSingleton(entityContainerName, singletonName);
        if (providerSingleton != null) {
          singleton = new EdmSingletonImpl(edm, this, providerSingleton);
          singletons.put(singletonName, singleton);
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return singleton;
  }

  @Override
  public EdmEntitySet getEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = entitySets.get(entitySetName);
    if (entitySet == null) {
      try {
        EntitySet providerEntitySet = provider.getEntitySet(entityContainerName, entitySetName);
        if (providerEntitySet != null) {
          entitySet = new EdmEntitySetImpl(edm, this, providerEntitySet);
          entitySets.put(entitySetName, entitySet);
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return entitySet;
  }

  @Override
  public EdmActionImport getActionImport(final String actionImportName) {
    EdmActionImport actionImport = actionImports.get(actionImportName);
    if (actionImport == null) {
      try {
        ActionImport providerImport = provider.getActionImport(entityContainerName, actionImportName);
        if (providerImport != null) {
          actionImport = new EdmActionImportImpl(edm, actionImportName, this, providerImport);
          actionImports.put(actionImportName, actionImport);
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return actionImport;
  }

  @Override
  public EdmFunctionImport getFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = functionImports.get(functionImportName);
    if (functionImport == null) {
      try {
        FunctionImport providerImport = provider.getFunctionImport(entityContainerName, functionImportName);
        if (providerImport != null) {
          functionImport = new EdmFunctionImportImpl(edm, functionImportName, this, providerImport);
          functionImports.put(functionImportName, functionImport);
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return functionImport;
  }

}
