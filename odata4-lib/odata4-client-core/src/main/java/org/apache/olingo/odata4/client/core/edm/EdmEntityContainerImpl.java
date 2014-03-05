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
package org.apache.olingo.odata4.client.core.edm;

import org.apache.olingo.odata4.client.api.edm.xml.v4.ActionImport;
import org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.v4.EntitySet;
import org.apache.olingo.odata4.client.api.edm.xml.v4.FunctionImport;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.odata4.client.api.utils.EdmTypeInfo;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.EdmActionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmEntitySet;
import org.apache.olingo.odata4.commons.api.edm.EdmException;
import org.apache.olingo.odata4.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.odata4.commons.api.edm.EdmSingleton;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.AbstractEdmEntityContainer;

public class EdmEntityContainerImpl extends AbstractEdmEntityContainer {

  private final EntityContainer xmlEntityContainer;

  public EdmEntityContainerImpl(final Edm edm, final FullQualifiedName entityContainerName,
          final EntityContainer xmlEntityContainer) {

    super(edm, entityContainerName);
    this.xmlEntityContainer = xmlEntityContainer;
  }

  @Override
  protected EdmSingleton createSingleton(final String singletonName) {
    final Singleton singleton = xmlEntityContainer.getSingleton(singletonName);
    if (singleton == null) {
      throw new EdmException("Singleton named '" + singletonName + "' not found in " + entityContainerName);
    }
    return new EdmSingletonImpl(edm, this, singletonName,
            new EdmTypeInfo(singleton.getEntityType(), entityContainerName.getNamespace()).getFullQualifiedName(),
            singleton);
  }

  @Override
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    final EntitySet entitySet = (EntitySet) xmlEntityContainer.getEntitySet(entitySetName);
    if (entitySet == null) {
      throw new EdmException("EntitySet named '" + entitySetName + "' not found in " + entityContainerName);
    }
    return new EdmEntitySetImpl(edm, this, entitySetName,
            new EdmTypeInfo(entitySet.getEntityType(), entityContainerName.getNamespace()).getFullQualifiedName(),
            entitySet);
  }

  @Override
  protected EdmActionImport createActionImport(final String actionImportName) {
    final ActionImport actionImport = xmlEntityContainer.getActionImport(actionImportName);
    if (actionImport == null) {
      throw new EdmException("ActionImport named '" + actionImportName + "' not found in " + entityContainerName);
    }
    return new EdmActionImportImpl(edm, this, actionImportName, actionImport);
  }

  @Override
  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    final FunctionImport functionImport = (FunctionImport) xmlEntityContainer.getFunctionImport(functionImportName);
    if (functionImport == null) {
      throw new EdmException("FunctionImport named '" + functionImportName + "' not found in " + entityContainerName);
    }
    return new EdmFunctionImportImpl(edm, this, functionImportName, functionImport);
  }

}
