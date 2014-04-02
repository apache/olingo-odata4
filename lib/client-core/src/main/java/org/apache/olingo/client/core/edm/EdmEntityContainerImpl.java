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
package org.apache.olingo.client.core.edm;

import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import java.util.List;

import org.apache.olingo.client.api.v3.UnsupportedInV3Exception;
import org.apache.olingo.client.api.edm.xml.CommonFunctionImport;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.EntitySet;
import org.apache.olingo.client.api.edm.xml.Schema;
import org.apache.olingo.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v4.ActionImport;
import org.apache.olingo.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.client.core.edm.v3.EdmActionImportProxy;
import org.apache.olingo.client.core.edm.v3.EdmEntitySetProxy;
import org.apache.olingo.client.core.edm.v3.EdmFunctionImportProxy;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmEntityContainer;

public class EdmEntityContainerImpl extends AbstractEdmEntityContainer {

  private final EntityContainer xmlEntityContainer;

  private final List<? extends Schema> xmlSchemas;

  public EdmEntityContainerImpl(final Edm edm, final FullQualifiedName entityContainerName,
          final EntityContainer xmlEntityContainer, final List<? extends Schema> xmlSchemas) {

    super(edm, entityContainerName, getFullQualifiedName(xmlEntityContainer.getExtends()));

    this.xmlEntityContainer = xmlEntityContainer;
    this.xmlSchemas = xmlSchemas;
  }

  private static FullQualifiedName getFullQualifiedName(String parent) {
    if (parent != null) {
      return new FullQualifiedName(parent);
    }
    return null;
  }

  @Override
  protected EdmSingleton createSingleton(final String singletonName) {
    if (!(xmlEntityContainer instanceof org.apache.olingo.client.api.edm.xml.v4.EntityContainer)) {
      throw new UnsupportedInV3Exception();
    }

    final Singleton singleton = ((org.apache.olingo.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).
            getSingleton(singletonName);
    if (singleton == null) {
      throw new EdmException("Singleton named '" + singletonName + "' not found in " + entityContainerName);
    }
    return new EdmSingletonImpl(edm, this, singletonName, new EdmTypeInfo.Builder().
            setTypeExpression(singleton.getEntityType()).setDefaultNamespace(entityContainerName.getNamespace()).
            build().getFullQualifiedName(), singleton);
  }

  @Override
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    final EntitySet entitySet = xmlEntityContainer.getEntitySet(entitySetName);
    if (entitySet == null) {
      throw new EdmException("EntitySet named '" + entitySetName + "' not found in " + entityContainerName);
    }

    final FullQualifiedName entityType = new EdmTypeInfo.Builder().setTypeExpression(entitySet.getEntityType()).
            setDefaultNamespace(entityContainerName.getNamespace()).build().getFullQualifiedName();
    if (entitySet instanceof org.apache.olingo.client.api.edm.xml.v4.EntitySet) {
      return new EdmEntitySetImpl(edm, this, entitySetName, entityType,
              (org.apache.olingo.client.api.edm.xml.v4.EntitySet) entitySet);
    } else {
      return new EdmEntitySetProxy(edm, this, entitySetName, entityType, xmlSchemas);
    }
  }

  @Override
  protected EdmActionImport createActionImport(final String actionImportName) {
    if (xmlEntityContainer instanceof org.apache.olingo.client.api.edm.xml.v4.EntityContainer) {
      final ActionImport actionImport = ((org.apache.olingo.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).
              getActionImport(actionImportName);
      if (actionImport == null) {
        throw new EdmException("ActionImport named '" + actionImportName + "' not found in " + entityContainerName);
      }
      return new EdmActionImportImpl(edm, this, actionImportName, actionImport);
    } else {
      final FunctionImport functionImport = (FunctionImport) xmlEntityContainer.getFunctionImport(actionImportName);
      if (functionImport == null) {
        throw new EdmException("FunctionImport named '" + actionImportName + "' not found in " + entityContainerName);
      }
      return new EdmActionImportProxy(edm, this, actionImportName, functionImport);
    }
  }

  @Override
  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    final CommonFunctionImport functionImport = xmlEntityContainer.getFunctionImport(functionImportName);
    if (functionImport == null) {
      throw new EdmException("FunctionImport named '" + functionImportName + "' not found in " + entityContainerName);
    }

    if (functionImport instanceof org.apache.olingo.client.api.edm.xml.v4.FunctionImport) {
      return new EdmFunctionImportImpl(edm, this, functionImportName,
              (org.apache.olingo.client.api.edm.xml.v4.FunctionImport) functionImport);
    } else {
      return new EdmFunctionImportProxy(edm, this, functionImportName,
              (org.apache.olingo.client.api.edm.xml.v3.FunctionImport) functionImport);
    }
  }

  @Override
  protected void loadAllEntitySets() {
    List<? extends EntitySet> localEntitySets = xmlEntityContainer.getEntitySets();
    if (localEntitySets != null) {
      for (EntitySet entitySet : localEntitySets) {
        EdmEntitySet edmSet;
        final FullQualifiedName entityType = new EdmTypeInfo.Builder().setTypeExpression(entitySet.getEntityType()).
                setDefaultNamespace(entityContainerName.getNamespace()).build().getFullQualifiedName();
        if (entitySet instanceof org.apache.olingo.client.api.edm.xml.v4.EntitySet) {
          edmSet =
                  new EdmEntitySetImpl(edm, this, entitySet.getName(), entityType,
                          (org.apache.olingo.client.api.edm.xml.v4.EntitySet) entitySet);
        } else {
          edmSet = new EdmEntitySetProxy(edm, this, entitySet.getName(), entityType, xmlSchemas);
        }
        entitySets.put(edmSet.getName(), edmSet);
      }
    }

  }

  @Override
  protected void loadAllFunctionImports() {
    final List<? extends CommonFunctionImport> localFunctionImports = xmlEntityContainer.getFunctionImports();
    if (localFunctionImports != null) {
      for (CommonFunctionImport functionImport : localFunctionImports) {
        EdmFunctionImport edmFunctionImport;
        if (functionImport instanceof org.apache.olingo.client.api.edm.xml.v4.FunctionImport) {
          edmFunctionImport = new EdmFunctionImportImpl(edm, this, functionImport.getName(),
                  (org.apache.olingo.client.api.edm.xml.v4.FunctionImport) functionImport);
        } else {
          edmFunctionImport = new EdmFunctionImportProxy(edm, this, functionImport.getName(),
                  (org.apache.olingo.client.api.edm.xml.v3.FunctionImport) functionImport);
        }
        functionImports.put(edmFunctionImport.getName(), edmFunctionImport);
      }
    }
  }

  @Override
  protected void loadAllSingletons() {
    if (!(xmlEntityContainer instanceof org.apache.olingo.client.api.edm.xml.v4.EntityContainer)) {
      throw new UnsupportedInV3Exception();
    }

    final List<Singleton> localSingletons =
            ((org.apache.olingo.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).getSingletons();
    if (localSingletons != null) {
      for (Singleton singleton : localSingletons) {
        singletons.put(singleton.getName(), new EdmSingletonImpl(edm, this, singleton.getName(),
                new EdmTypeInfo.Builder().
                setTypeExpression(singleton.getEntityType()).setDefaultNamespace(entityContainerName.getNamespace()).
                build().getFullQualifiedName(), singleton));
      }
    }
  }

  @Override
  protected void loadAllActionImports() {
    if (xmlEntityContainer instanceof org.apache.olingo.client.api.edm.xml.v4.EntityContainer) {
      final List<ActionImport> localActionImports =
              ((org.apache.olingo.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).getActionImports();
      if (actionImports != null) {
        for (ActionImport actionImport : localActionImports) {
          actionImports.put(actionImport.getName(),
                  new EdmActionImportImpl(edm, this, actionImport.getName(), actionImport));
        }
      }
    } else {
      @SuppressWarnings("unchecked")
      final List<FunctionImport> localFunctionImports = (List<FunctionImport>) xmlEntityContainer.getFunctionImports();
      if (localFunctionImports != null) {
        for (FunctionImport functionImport : localFunctionImports) {
          actionImports.put(functionImport.getName(),
                  new EdmActionImportProxy(edm, this, functionImport.getName(), functionImport));
        }
      }
    }
  }
}
