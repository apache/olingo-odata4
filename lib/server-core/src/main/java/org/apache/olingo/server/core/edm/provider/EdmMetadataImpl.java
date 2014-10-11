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
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmActionImportInfoImpl;
import org.apache.olingo.commons.core.edm.EdmEntitySetInfoImpl;
import org.apache.olingo.commons.core.edm.EdmFunctionImportInfoImpl;
import org.apache.olingo.commons.core.edm.EdmSingletonInfoImpl;
import org.apache.olingo.server.api.edm.provider.*;

import java.util.ArrayList;
import java.util.List;

public class EdmMetadataImpl implements EdmMetadata {

  private EdmProvider provider;

  private List<Schema> schemas;

  private List<EdmEntitySetInfo> entitySetInfos;

  private List<EdmSingletonInfo> singletonInfos;

  private List<EdmActionImportInfo> actionImportInfos;

  private List<EdmFunctionImportInfo> functionImportInfos;

  public EdmMetadataImpl(final EdmProvider provider) {
    this.provider = provider;
  }

  @Override
  public List<EdmEntitySetInfo> getEntitySetInfos() {
    if (entitySetInfos == null) {
      try {
        entitySetInfos = new ArrayList<EdmEntitySetInfo>();
        if (schemas == null) {
          schemas = provider.getSchemas();
          if (schemas == null) {
            throw new EdmException("Provider doe not define any schemas.");
          }
        }
        for (Schema schema : schemas) {
          final EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            final List<EntitySet> entitySets = entityContainer.getEntitySets();
            if (entitySets != null) {
              for (EntitySet set : entitySets) {
                entitySetInfos.add(new EdmEntitySetInfoImpl(entityContainer.getName(), set.getName()));
              }
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return entitySetInfos;
  }

  @Override
  public List<EdmSingletonInfo> getSingletonInfos() {
    if (singletonInfos == null) {
      try {
        singletonInfos = new ArrayList<EdmSingletonInfo>();
        if (schemas == null) {
          schemas = provider.getSchemas();
          if (schemas == null) {
            throw new EdmException("Provider doe not define any schemas.");
          }
        }
        for (Schema schema : schemas) {
          final EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            final List<Singleton> singletons = entityContainer.getSingletons();
            if (singletons != null) {
              for (Singleton singleton : singletons) {
                singletonInfos.add(new EdmSingletonInfoImpl(entityContainer.getName(), singleton.getName()));
              }
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return singletonInfos;
  }

  @Override
  public List<EdmActionImportInfo> getActionImportInfos() {
    if (actionImportInfos == null) {
      try {
        actionImportInfos = new ArrayList<EdmActionImportInfo>();
        if (schemas == null) {
          schemas = provider.getSchemas();
          if (schemas == null) {
            throw new EdmException("Provider doe not define any schemas.");
          }
        }
        for (Schema schema : schemas) {
          final EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            final List<ActionImport> actionImports = entityContainer.getActionImports();
            if (actionImports != null) {
              for (ActionImport actionImport : actionImports) {
                actionImportInfos.add(new EdmActionImportInfoImpl(entityContainer.getName(), actionImport.getName()));
              }
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return actionImportInfos;
  }

  @Override
  public List<EdmFunctionImportInfo> getFunctionImportInfos() {
    if (functionImportInfos == null) {
      try {
        functionImportInfos = new ArrayList<EdmFunctionImportInfo>();
        if (schemas == null) {
          schemas = provider.getSchemas();
          if (schemas == null) {
            throw new EdmException("Provider doe not define any schemas.");
          }
        }
        for (Schema schema : schemas) {
          final EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            final List<FunctionImport> functionImports = entityContainer.getFunctionImports();
            if (functionImports != null) {
              for (FunctionImport functionImport : functionImports) {
                functionImportInfos.add(
                    new EdmFunctionImportInfoImpl(entityContainer.getName(), functionImport.getName()));
              }
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    return functionImportInfos;
  }

  @Override
  public ODataServiceVersion getDataServiceVersion() {
    return ODataServiceVersion.V40;
  }

}
