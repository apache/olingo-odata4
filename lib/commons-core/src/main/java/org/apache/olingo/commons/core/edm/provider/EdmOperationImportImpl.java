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

import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmOperationImport;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.commons.api.edm.provider.OperationImport;

public abstract class EdmOperationImportImpl extends EdmNamedImpl implements EdmOperationImport {

  protected final EdmEntityContainer container;
  private final Target entitySet;
  private EdmEntitySet returnedEntitySet;
  private final EdmAnnotationHelperImpl helper;

  public EdmOperationImportImpl(final Edm edm, final EdmEntityContainer container,
      final OperationImport operationImport) {
    super(edm, operationImport.getName());
    this.container = container;
    this.helper = new EdmAnnotationHelperImpl(edm, operationImport);
    this.entitySet = new Target.Builder(operationImport.getEntitySet(), container).build();
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return new FullQualifiedName(container.getNamespace(), getName());
  }

  @Override
  public EdmEntitySet getReturnedEntitySet() {
    if (entitySet != null && returnedEntitySet == null) {
      EdmEntityContainer entityContainer = edm.getEntityContainer(entitySet.getEntityContainer());
      if (entityContainer == null) {
        throw new EdmException("Can´t find entity container with name: " + entitySet.getEntityContainer());
      }
      returnedEntitySet = entityContainer.getEntitySet(entitySet.getTargetName());
      if (returnedEntitySet == null) {
        throw new EdmException("Can´t find entity set with name: " + entitySet.getTargetName());
      }
    }
    return returnedEntitySet;
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return container;
  }

  @Override
  public FullQualifiedName getAnnotationsTargetFQN() {
    return container.getFullQualifiedName();
  }

  @Override
  public String getAnnotationsTargetPath() {
    return getName();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    return helper.getAnnotation(term);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return helper.getAnnotations();
  }
}
