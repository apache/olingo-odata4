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
  private final EdmProviderImpl edm;
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

  public EdmSchemaImpl(final EdmProviderImpl edm, final EdmProvider provider, final Schema schema) {
    this.edm = edm;
    this.provider = provider;
    this.schema = schema;
    this.namespace = schema.getNamespace();
    this.alias = schema.getAlias();

    if (alias != null) {
      edm.cacheAliasNamespaceInfo(alias, namespace);
    }

    enumTypes = createEnumTypes();
    typeDefinitions = createTypeDefinitions();
    entityTypes = createEntityTypes();
    complexTypes = createComplexTypes();
    actions = createActions();
    functions = createFunctions();
    entityContainer = createEntityContainer();
    annotationGroups = createAnnotationGroups();
    annotations = createAnnotations();
    terms = createTerms();
  }

  @Override
  public List<EdmEnumType> getEnumTypes() {
    return Collections.unmodifiableList(enumTypes);
  }

  @Override
  public List<EdmEntityType> getEntityTypes() {
    return Collections.unmodifiableList(entityTypes);
  }

  @Override
  public List<EdmComplexType> getComplexTypes() {
    return Collections.unmodifiableList(complexTypes);
  }

  @Override
  public List<EdmAction> getActions() {
    return Collections.unmodifiableList(actions);
  }

  @Override
  public List<EdmFunction> getFunctions() {
    return Collections.unmodifiableList(functions);
  }

  @Override
  public List<EdmTypeDefinition> getTypeDefinitions() {
    return Collections.unmodifiableList(typeDefinitions);
  }

  @Override
  public List<EdmTerm> getTerms() {
    return Collections.unmodifiableList(terms);
  }

  @Override
  public List<EdmAnnotations> getAnnotationGroups() {
    return Collections.unmodifiableList(annotationGroups);
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    return Collections.unmodifiableList(annotations);
  }

  @Override
  public EdmEntityContainer getEntityContainer() {
    return entityContainer;
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
      EdmEntityContainer impl = new EdmEntityContainerImpl(edm, provider, containerFQN, schema.getEntityContainer());
      edm.cacheEntityContainer(containerFQN, impl);
      edm.cacheEntityContainer(null, impl);
      return impl;
    }
    return null;
  }

  protected List<EdmTypeDefinition> createTypeDefinitions() {
    final List<EdmTypeDefinition> typeDefinitions = new ArrayList<EdmTypeDefinition>();
    final List<TypeDefinition> providerTypeDefinitions = schema.getTypeDefinitions();
    if (providerTypeDefinitions != null) {
      for (TypeDefinition def : providerTypeDefinitions) {
        FullQualifiedName typeDefName = new FullQualifiedName(namespace, def.getName());
        EdmTypeDefinitionImpl typeDefImpl = new EdmTypeDefinitionImpl(edm, typeDefName, def);
        typeDefinitions.add(typeDefImpl);
        edm.cacheTypeDefinition(typeDefName, typeDefImpl);
      }
    }
    return typeDefinitions;
  }

  protected List<EdmEnumType> createEnumTypes() {
    final List<EdmEnumType> enumTypes = new ArrayList<EdmEnumType>();
    final List<EnumType> providerEnumTypes = schema.getEnumTypes();
    if (providerEnumTypes != null) {
      for (EnumType enumType : providerEnumTypes) {
        FullQualifiedName enumName = new FullQualifiedName(namespace, enumType.getName());
        EdmEnumType enumTypeImpl = new EdmEnumTypeImpl(edm, enumName, enumType);
        enumTypes.add(enumTypeImpl);
        edm.cacheEnumType(enumName, enumTypeImpl);
      }
    }
    return enumTypes;
  }

  protected List<EdmEntityType> createEntityTypes() {
    final List<EdmEntityType> entityTypes = new ArrayList<EdmEntityType>();
    final List<EntityType> providerEntityTypes = schema.getEntityTypes();
    if (providerEntityTypes != null) {
      for (EntityType entityType : providerEntityTypes) {
        FullQualifiedName entityTypeName = new FullQualifiedName(namespace, entityType.getName());
        EdmEntityTypeImpl entityTypeImpl = new EdmEntityTypeImpl(edm, entityTypeName, entityType);
        entityTypes.add(entityTypeImpl);
        edm.cacheEntityType(entityTypeName, entityTypeImpl);
      }
    }
    return entityTypes;
  }

  protected List<EdmComplexType> createComplexTypes() {
    final List<EdmComplexType> complexTypes = new ArrayList<EdmComplexType>();
    final List<ComplexType> providerComplexTypes = schema.getComplexTypes();
    if (providerComplexTypes != null) {
      for (ComplexType complexType : providerComplexTypes) {
        FullQualifiedName comlexTypeName = new FullQualifiedName(namespace, complexType.getName());
        EdmComplexTypeImpl complexTypeImpl = new EdmComplexTypeImpl(edm, comlexTypeName, complexType);
        complexTypes.add(complexTypeImpl);
        edm.cacheComplexType(comlexTypeName, complexTypeImpl);
      }
    }
    return complexTypes;
  }

  protected List<EdmAction> createActions() {
    final List<EdmAction> actions = new ArrayList<EdmAction>();
    final List<Action> providerActions = schema.getActions();
    if (providerActions != null) {
      for (Action action : providerActions) {
        FullQualifiedName actionName = new FullQualifiedName(namespace, action.getName());
        EdmActionImpl edmActionImpl = new EdmActionImpl(edm, actionName, action);
        actions.add(edmActionImpl);
        edm.cacheAction(actionName, edmActionImpl);
      }
    }
    return actions;
  }

  protected List<EdmFunction> createFunctions() {
    final List<EdmFunction> functions = new ArrayList<EdmFunction>();
    final List<Function> providerFunctions = schema.getFunctions();
    if (providerFunctions != null) {
      for (Function function : providerFunctions) {
        FullQualifiedName functionName = new FullQualifiedName(namespace, function.getName());
        EdmFunctionImpl functionImpl = new EdmFunctionImpl(edm, functionName, function);
        functions.add(functionImpl);
        edm.cacheFunction(functionName, functionImpl);
      }
    }
    return functions;
  }

  protected List<EdmTerm> createTerms() {
    final List<EdmTerm> terms = new ArrayList<EdmTerm>();
    final List<Term> providerTerms = schema.getTerms();
    if (providerTerms != null) {
      for (Term term : providerTerms) {
        FullQualifiedName termName = new FullQualifiedName(namespace, term.getName());
        EdmTermImpl termImpl = new EdmTermImpl(edm, getNamespace(), term);
        terms.add(termImpl);
        edm.cacheTerm(termName, termImpl);
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
        FullQualifiedName annotationsGroupName;
        if (annotationGroup.getTarget().contains(".")) {
          annotationsGroupName = new FullQualifiedName(annotationGroup.getTarget());
        } else {
          annotationsGroupName = new FullQualifiedName(namespace, annotationGroup.getTarget());
        }
        EdmAnnotationsImpl annotationsImpl = new EdmAnnotationsImpl(edm, this, annotationGroup);
        annotationGroups.add(annotationsImpl);
        edm.cacheAnnotationGroup(annotationsGroupName, annotationsImpl);
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
        EdmAnnotationImpl annotationImpl = new EdmAnnotationImpl(edm, annotation);
        annotations.add(annotationImpl);
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
