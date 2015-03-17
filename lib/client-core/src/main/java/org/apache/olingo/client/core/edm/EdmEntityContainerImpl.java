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

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.commons.api.edm.provider.Singleton;
import org.apache.olingo.commons.core.edm.AbstractEdmEntityContainer;
import org.apache.olingo.commons.core.edm.EdmAnnotationHelper;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

public class EdmEntityContainerImpl extends AbstractEdmEntityContainer {

  private final EntityContainer xmlEntityContainer;

  private EdmAnnotationHelper helper;

  public EdmEntityContainerImpl(final Edm edm, final FullQualifiedName entityContainerName,
      final EntityContainer xmlEntityContainer) {

    super(edm, entityContainerName, xmlEntityContainer.getExtendsContainer() == null
        ? null : new FullQualifiedName(xmlEntityContainer.getExtendsContainer()));

    this.xmlEntityContainer = xmlEntityContainer;
    this.helper = new EdmAnnotationHelperImpl(edm, xmlEntityContainer);
  }

  @Override
  public boolean isDefault() {
    return true;
  }

  @Override
  protected EdmSingleton createSingleton(final String singletonName) {
    final Singleton singleton = xmlEntityContainer.getSingleton(singletonName);
    return singleton == null
        ? null
        : new EdmSingletonImpl(edm, this, singletonName, new EdmTypeInfo.Builder().
            setTypeExpression(singleton.getType()).
            setDefaultNamespace(entityContainerName.getNamespace()).
            build().getFullQualifiedName(), singleton);
  }

  @Override
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    EdmEntitySet result = null;

    final EntitySet entitySet = xmlEntityContainer.getEntitySet(entitySetName);
    if (entitySet != null) {
      final FullQualifiedName entityType = new EdmTypeInfo.Builder().setTypeExpression(entitySet.getType()).
          setDefaultNamespace(entityContainerName.getNamespace()).build().getFullQualifiedName();
      result = new EdmEntitySetImpl(edm, this, entitySetName, entityType, entitySet);
    }

    return result;
  }

  @Override
  protected EdmActionImport createActionImport(final String actionImportName) {
    EdmActionImport result = null;

    final ActionImport actionImport = xmlEntityContainer.getActionImport(actionImportName);
    if (actionImport != null) {
      result = new EdmActionImportImpl(edm, this, actionImportName, actionImport);
    }
    return result;
  }

  @Override
  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    EdmFunctionImport result = null;

    final FunctionImport functionImport = xmlEntityContainer.getFunctionImport(functionImportName);
    if (functionImport != null) {
      result = new EdmFunctionImportImpl(edm, this, functionImportName, functionImport);
    }

    return result;
  }

  @Override
  protected void loadAllEntitySets() {
    List<? extends EntitySet> localEntitySets = xmlEntityContainer.getEntitySets();
    if (localEntitySets != null) {
      for (EntitySet entitySet : localEntitySets) {
        EdmEntitySet edmSet;
        final FullQualifiedName entityType = new EdmTypeInfo.Builder().setTypeExpression(entitySet.getType()).
            setDefaultNamespace(entityContainerName.getNamespace()).build().getFullQualifiedName();
        edmSet = new EdmEntitySetImpl(edm, this, entitySet.getName(), entityType, entitySet);
        entitySets.put(edmSet.getName(), edmSet);
      }
    }
  }

  @Override
  protected void loadAllFunctionImports() {
    final List<? extends FunctionImport> localFunctionImports = xmlEntityContainer.getFunctionImports();
    for (FunctionImport functionImport : localFunctionImports) {
      EdmFunctionImport edmFunctionImport;
      edmFunctionImport = new EdmFunctionImportImpl(edm, this, functionImport.getName(), functionImport);
      functionImports.put(edmFunctionImport.getName(), edmFunctionImport);
    }
  }

  @Override
  protected void loadAllSingletons() {
    final List<Singleton> localSingletons = xmlEntityContainer.getSingletons();
    if (localSingletons != null) {
      for (Singleton singleton : localSingletons) {
        singletons.put(singleton.getName(), new EdmSingletonImpl(edm, this, singleton.getName(),
            new EdmTypeInfo.Builder().
                setTypeExpression(singleton.getType()).setDefaultNamespace(entityContainerName.getNamespace()).
                build().getFullQualifiedName(), singleton));
      }
    }
  }

  @Override
  protected void loadAllActionImports() {
    final List<ActionImport> localActionImports = xmlEntityContainer.getActionImports();
    if (actionImports != null) {
      for (ActionImport actionImport : localActionImports) {
        actionImports.put(actionImport.getName(), new EdmActionImportImpl(edm, this, actionImport.getName(),
            actionImport));
      }
    }
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
