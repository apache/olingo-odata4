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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
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
import org.apache.olingo.commons.api.edm.provider.Action;
import org.apache.olingo.commons.api.edm.provider.Annotation;
import org.apache.olingo.commons.api.edm.provider.Annotations;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.EnumType;
import org.apache.olingo.commons.api.edm.provider.Function;
import org.apache.olingo.commons.api.edm.provider.Schema;
import org.apache.olingo.commons.api.edm.provider.Term;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;

public class EdmSchemaImpl implements EdmSchema {

  private final Schema schema;
  private final Edm edm;
  private final EdmProvider provider;

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

  public EdmSchemaImpl(final Edm edm, final EdmProvider provider, final Schema schema) {
    this.edm = edm;
    this.provider = provider;
    this.schema = schema;
    this.namespace = schema.getNamespace();
    this.alias = schema.getAlias();
  }

  @Override
  public List<EdmEnumType> getEnumTypes() {
    if (enumTypes == null) {
      enumTypes = createEnumTypes();
    }
    return Collections.unmodifiableList(enumTypes);
  }

  @Override
  public List<EdmEntityType> getEntityTypes() {
    if (entityTypes == null) {
      entityTypes = createEntityTypes();
    }
    return Collections.unmodifiableList(entityTypes);
  }

  @Override
  public List<EdmComplexType> getComplexTypes() {
    if (complexTypes == null) {
      complexTypes = createComplexTypes();
    }
    return Collections.unmodifiableList(complexTypes);
  }

  @Override
  public List<EdmAction> getActions() {
    if (actions == null) {
      actions = createActions();
    }
    return Collections.unmodifiableList(actions);
  }

  @Override
  public List<EdmFunction> getFunctions() {
    if (functions == null) {
      functions = createFunctions();
    }
    return Collections.unmodifiableList(functions);
  }

  @Override
  public List<EdmTypeDefinition> getTypeDefinitions() {
    if (typeDefinitions == null) {
      typeDefinitions = createTypeDefinitions();
    }
    return Collections.unmodifiableList(typeDefinitions);
  }

  @Override
  public List<EdmTerm> getTerms() {
    if (terms == null) {
      terms = createTerms();
    }
    return Collections.unmodifiableList(terms);
  }

  @Override
  public List<EdmAnnotations> getAnnotationGroups() {
    if (annotationGroups == null) {
      annotationGroups = createAnnotationGroups();
    }
    return Collections.unmodifiableList(annotationGroups);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    if (annotations == null) {
      annotations = createAnnotations();
    }
    return Collections.unmodifiableList(annotations);
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
      return Collections.<EdmEntityContainer> emptyList();
    } else {
      return Collections.unmodifiableList(Collections.singletonList(getEntityContainer()));
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

  protected EdmEntityContainer createEntityContainer() {
    if (schema.getEntityContainer() != null) {
      FullQualifiedName containerFQN = new FullQualifiedName(namespace, schema.getEntityContainer().getName());
      return new EdmEntityContainerImpl(edm, provider, containerFQN, schema.getEntityContainer());
    }
    return null;
  }

  protected List<EdmTypeDefinition> createTypeDefinitions() {
    final List<EdmTypeDefinition> typeDefinitions = new ArrayList<EdmTypeDefinition>();
    final List<TypeDefinition> providerTypeDefinitions = schema.getTypeDefinitions();
    if (providerTypeDefinitions != null) {
      for (TypeDefinition def : providerTypeDefinitions) {
        typeDefinitions.add(new EdmTypeDefinitionImpl(edm, new FullQualifiedName(namespace, def.getName()), def));
      }
    }
    return typeDefinitions;
  }

  protected List<EdmEnumType> createEnumTypes() {
    final List<EdmEnumType> enumTypes = new ArrayList<EdmEnumType>();
    final List<EnumType> providerEnumTypes = schema.getEnumTypes();
    if (providerEnumTypes != null) {
      for (EnumType enumType : providerEnumTypes) {
        enumTypes.add(new EdmEnumTypeImpl(edm, new FullQualifiedName(namespace, enumType.getName()), enumType));
      }
    }
    return enumTypes;
  }

  protected List<EdmEntityType> createEntityTypes() {
    final List<EdmEntityType> entityTypes = new ArrayList<EdmEntityType>();
    final List<EntityType> providerEntityTypes = schema.getEntityTypes();
    if (providerEntityTypes != null) {
      for (EntityType entityType : providerEntityTypes) {
        entityTypes.add(new EdmEntityTypeImpl(edm, new FullQualifiedName(namespace, entityType.getName()),
            entityType));
      }
    }
    return entityTypes;
  }

  protected List<EdmComplexType> createComplexTypes() {
    final List<EdmComplexType> complexTypes = new ArrayList<EdmComplexType>();
    final List<ComplexType> providerComplexTypes = schema.getComplexTypes();
    if (providerComplexTypes != null) {
      for (ComplexType complexType : providerComplexTypes) {
        complexTypes.add(new EdmComplexTypeImpl(edm, new FullQualifiedName(namespace, complexType.getName()),
            complexType));
      }
    }
    return complexTypes;
  }

  protected List<EdmAction> createActions() {
    final List<EdmAction> actions = new ArrayList<EdmAction>();
    final List<Action> providerActions = schema.getActions();
    if (providerActions != null) {
      for (Action action : providerActions) {
        actions.add(new EdmActionImpl(edm, new FullQualifiedName(namespace, action.getName()), action));
      }
    }
    return actions;
  }

  protected List<EdmFunction> createFunctions() {
    final List<EdmFunction> functions = new ArrayList<EdmFunction>();
    final List<Function> providerFunctions = schema.getFunctions();
    if (providerFunctions != null) {
      for (Function function : providerFunctions) {
        functions.add(new EdmFunctionImpl(edm, new FullQualifiedName(namespace, function.getName()), function));
      }
    }
    return functions;
  }

  protected List<EdmTerm> createTerms() {
    final List<EdmTerm> terms = new ArrayList<EdmTerm>();
    final List<Term> providerTerms = schema.getTerms();
    if (providerTerms != null) {
      for (Term term : providerTerms) {
        terms.add(new EdmTermImpl(edm, getNamespace(), term));
      }
    }
    return terms;
  }

  protected List<EdmAnnotations> createAnnotationGroups() {
    final List<EdmAnnotations> annotationGroups = new ArrayList<EdmAnnotations>();
    final List<Annotations> providerAnnotations =
        schema.getAnnotationGroups();
    if (providerAnnotations != null) {
      for (Annotations annotationGroup : providerAnnotations) {
        annotationGroups.add(new EdmAnnotationsImpl(edm, this, annotationGroup));
      }
    }
    return annotationGroups;
  }

  protected List<EdmAnnotation> createAnnotations() {
    final List<EdmAnnotation> annotations = new ArrayList<EdmAnnotation>();
    final List<Annotation> providerAnnotations =
        schema.getAnnotations();
    if (providerAnnotations != null) {
      for (Annotation annotation : providerAnnotations) {
        annotations.add(new EdmAnnotationImpl(edm, annotation));
      }
    }
    return annotations;
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    EdmAnnotation result = null;
    for (EdmAnnotation annotation : getAnnotations()) {
      if (term.getFullQualifiedName().equals(annotation.getTerm().getFullQualifiedName())) {
        result = annotation;
      }
    }

    return result;
  }
}
