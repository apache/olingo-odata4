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
package org.apache.olingo.commons.core.edm;

import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotation;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public abstract class AbstractEdmSchema implements EdmSchema {

  protected final String namespace;

  private final String alias;

  private List<EdmEnumType> enumTypes;

  private List<EdmEntityType> entityTypes;

  private List<EdmComplexType> complexTypes;

  private List<EdmAction> actions;

  private List<EdmFunction> functions;

  private List<EdmTypeDefinition> typeDefinitions;

  private List<EdmTerm> terms;

  private List<EdmAnnotations> annotationGroups;

  private List<EdmAnnotation> annotations;

  private EdmEntityContainer entityContainer;

  public AbstractEdmSchema(final String namespace, final String alias) {
    this.namespace = namespace;
    this.alias = alias;
  }

  protected abstract EdmEntityContainer createEntityContainer();

  protected abstract List<EdmEnumType> createEnumTypes();

  protected abstract List<EdmEntityType> createEntityTypes();

  protected abstract List<EdmComplexType> createComplexTypes();

  protected abstract List<EdmAction> createActions();

  protected abstract List<EdmFunction> createFunctions();

  protected abstract List<EdmTypeDefinition> createTypeDefinitions();

  protected abstract List<EdmTerm> createTerms();

  protected abstract List<EdmAnnotations> createAnnotationGroups();

  protected abstract List<EdmAnnotation> createAnnotations();

  @Override
  public List<EdmEnumType> getEnumTypes() {
    if (enumTypes == null) {
      enumTypes = createEnumTypes();
    }
    return enumTypes;
  }

  @Override
  public List<EdmEntityType> getEntityTypes() {
    if (entityTypes == null) {
      entityTypes = createEntityTypes();
    }
    return entityTypes;
  }

  @Override
  public List<EdmComplexType> getComplexTypes() {
    if (complexTypes == null) {
      complexTypes = createComplexTypes();
    }
    return complexTypes;
  }

  @Override
  public List<EdmAction> getActions() {
    if (actions == null) {
      actions = createActions();
    }
    return actions;
  }

  @Override
  public List<EdmFunction> getFunctions() {
    if (functions == null) {
      functions = createFunctions();
    }
    return functions;
  }

  @Override
  public List<EdmTypeDefinition> getTypeDefinitions() {
    if (typeDefinitions == null) {
      typeDefinitions = createTypeDefinitions();
    }
    return typeDefinitions;
  }

  @Override
  public List<EdmTerm> getTerms() {
    if (terms == null) {
      terms = createTerms();
    }
    return terms;
  }

  @Override
  public List<EdmAnnotations> getAnnotationGroups() {
    if (annotationGroups == null) {
      annotationGroups = createAnnotationGroups();
    }
    return annotationGroups;
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    if (annotations == null) {
      annotations = createAnnotations();
    }
    return annotations;
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    if (entityContainer == null) {
      entityContainer = createEntityContainer();
    }
    return entityContainer;
  }

  @Override
  public List<EdmEntityContainer> getEntityContainers() {
    if (getEntityContainer() == null) {
      return Collections.<EdmEntityContainer>emptyList();
    } else {
      return Collections.singletonList(getEntityContainer());
    }
  }

  @Override
  public EdmEntityContainer getEntityContainer(final FullQualifiedName name) {
    return getEntityContainer() == null
            ? null
            : name == null
            ? getEntityContainer()
            : name.equals(getEntityContainer().getFullQualifiedName())
            ? getEntityContainer()
            : null;
  }

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public String getAlias() {
    return alias;
  }
}
