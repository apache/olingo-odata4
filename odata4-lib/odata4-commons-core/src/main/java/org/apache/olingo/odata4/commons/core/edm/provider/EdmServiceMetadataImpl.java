/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.commons.core.edm.provider;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntitySetInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImportInfo;
import org.apache.olingo.odata4.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.odata4.commons.api.edm.EdmSingletonInfo;
import org.apache.olingo.odata4.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.odata4.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.odata4.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.odata4.commons.api.edm.provider.EntitySet;
import org.apache.olingo.odata4.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.odata4.commons.api.edm.provider.Schema;
import org.apache.olingo.odata4.commons.api.edm.provider.Singleton;
import org.apache.olingo.odata4.commons.api.exception.ODataException;

public class EdmServiceMetadataImpl implements EdmServiceMetadata {

  private EdmProvider provider;
  private ArrayList<EdmEntitySetInfo> entitySetInfos;
  private ArrayList<EdmFunctionImportInfo> functionImportInfos;
  private ArrayList<EdmSingletonInfo> singletonInfos;
  private List<Schema> schemas;

  public EdmServiceMetadataImpl(final EdmProvider provider) {
    this.provider = provider;
  }

  @Override
  public InputStream getMetadata() {
    throw new RuntimeException("Not yet implemented");
  }

  @Override
  public String getDataServiceVersion() {
    return ODataServiceVersion.V40;
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
          EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            List<EntitySet> entitySets = entityContainer.getEntitySets();
            if (entitySets != null) {
              for (EntitySet set : entitySets) {
                entitySetInfos.add(new EdmEntitySetInfoImpl(entityContainer, set));
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
          EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            List<Singleton> singletons = entityContainer.getSingletons();
            if (singletons != null) {
              for (Singleton singleton : singletons) {
                singletonInfos.add(new EdmSingletonInfoImpl(entityContainer, singleton));
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
          EntityContainer entityContainer = schema.getEntityContainer();
          if (entityContainer != null) {
            List<FunctionImport> functionImports = entityContainer.getFunctionImports();
            if (functionImports != null) {
              for (FunctionImport functionImport : functionImports) {
                functionImportInfos.add(new EdmFunctionImportInfoImpl(entityContainer, functionImport));
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

}
