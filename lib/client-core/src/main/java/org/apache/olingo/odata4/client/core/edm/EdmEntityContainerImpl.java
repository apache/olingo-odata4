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
import org.apache.olingo.odata4.client.api.edm.xml.EntitySet;
import org.apache.olingo.odata4.client.api.edm.xml.CommonFunctionImport;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.odata4.client.api.utils.EdmTypeInfo;
import org.apache.olingo.odata4.client.api.UnsupportedInV3Exception;
import org.apache.olingo.odata4.client.api.edm.xml.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.odata4.client.api.edm.xml.v3.FunctionImport;
import org.apache.olingo.odata4.client.core.edm.v3.EdmActionImportProxy;
import org.apache.olingo.odata4.client.core.edm.v3.EdmEntitySetProxy;
import org.apache.olingo.odata4.client.core.edm.v3.EdmFunctionImportProxy;
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

  private final XMLMetadata xmlMetadata;

  public EdmEntityContainerImpl(final Edm edm, final FullQualifiedName entityContainerName,
          final EntityContainer xmlEntityContainer, final XMLMetadata xmlMetadata) {

    super(edm, entityContainerName);

    this.xmlEntityContainer = xmlEntityContainer;
    this.xmlMetadata = xmlMetadata;
  }

  @Override
  protected EdmSingleton createSingleton(final String singletonName) {
    if (!(xmlEntityContainer instanceof org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer)) {
      throw new UnsupportedInV3Exception();
    }

    final Singleton singleton = ((org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).
            getSingleton(singletonName);
    if (singleton == null) {
      throw new EdmException("Singleton named '" + singletonName + "' not found in " + entityContainerName);
    }
    return new EdmSingletonImpl(edm, this, singletonName,
            new EdmTypeInfo(singleton.getEntityType(), entityContainerName.getNamespace()).getFullQualifiedName(),
            singleton);
  }

  @Override
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    final EntitySet entitySet = xmlEntityContainer.getEntitySet(entitySetName);
    if (entitySet == null) {
      throw new EdmException("EntitySet named '" + entitySetName + "' not found in " + entityContainerName);
    }

    if (entitySet instanceof org.apache.olingo.odata4.client.api.edm.xml.v4.EntitySet) {
      return new EdmEntitySetImpl(edm, this, entitySetName,
              new EdmTypeInfo(entitySet.getEntityType(), entityContainerName.getNamespace()).getFullQualifiedName(),
              (org.apache.olingo.odata4.client.api.edm.xml.v4.EntitySet) entitySet);
    } else {
      return new EdmEntitySetProxy(edm, this, entitySetName,
              new EdmTypeInfo(entitySet.getEntityType(), entityContainerName.getNamespace()).getFullQualifiedName(),
              xmlMetadata);
    }
  }

  @Override
  protected EdmActionImport createActionImport(final String actionImportName) {
    if (xmlEntityContainer instanceof org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer) {
      final ActionImport actionImport
              = ((org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer) xmlEntityContainer).
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

    if (functionImport instanceof org.apache.olingo.odata4.client.api.edm.xml.v4.FunctionImport) {
      return new EdmFunctionImportImpl(edm, this, functionImportName,
              (org.apache.olingo.odata4.client.api.edm.xml.v4.FunctionImport) functionImport);
    } else {
      return new EdmFunctionImportProxy(edm, this, functionImportName,
              (org.apache.olingo.odata4.client.api.edm.xml.v3.FunctionImport) functionImport);
    }
  }
}
