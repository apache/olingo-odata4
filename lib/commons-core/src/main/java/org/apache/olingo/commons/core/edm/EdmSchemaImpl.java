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
package org.apache.olingo.commons.core.edm;

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
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;

public class EdmSchemaImpl extends AbstractEdmAnnotatable implements EdmSchema {

  private final CsdlSchema schema;
  private final EdmProviderImpl edm;
  private final CsdlEdmProvider provider;

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

  public EdmSchemaImpl(final EdmProviderImpl edm, final CsdlEdmProvider provider, final CsdlSchema schema) {
    super(edm, schema);
    this.edm = edm;
    this.provider = provider;
    this.schema = schema;
    namespace = schema.getNamespace();
    alias = schema.getAlias();

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
    final List<EdmTypeDefinition> typeDefns = new ArrayList<EdmTypeDefinition>();
    final List<CsdlTypeDefinition> providerTypeDefinitions = schema.getTypeDefinitions();
    if (providerTypeDefinitions != null) {
      for (CsdlTypeDefinition def : providerTypeDefinitions) {
        FullQualifiedName typeDefName = new FullQualifiedName(namespace, def.getName());
        EdmTypeDefinitionImpl typeDefImpl = new EdmTypeDefinitionImpl(edm, typeDefName, def);
        typeDefns.add(typeDefImpl);
        edm.cacheTypeDefinition(typeDefName, typeDefImpl);
      }
    }
    return typeDefns;
  }

  protected List<EdmEnumType> createEnumTypes() {
    final List<EdmEnumType> enumTyps = new ArrayList<EdmEnumType>();
    final List<CsdlEnumType> providerEnumTypes = schema.getEnumTypes();
    if (providerEnumTypes != null) {
      for (CsdlEnumType enumType : providerEnumTypes) {
        FullQualifiedName enumName = new FullQualifiedName(namespace, enumType.getName());
        EdmEnumType enumTypeImpl = new EdmEnumTypeImpl(edm, enumName, enumType);
        enumTyps.add(enumTypeImpl);
        edm.cacheEnumType(enumName, enumTypeImpl);
      }
    }
    return enumTyps;
  }

  protected List<EdmEntityType> createEntityTypes() {
    final List<EdmEntityType> edmEntityTypes = new ArrayList<EdmEntityType>();
    final List<CsdlEntityType> providerEntityTypes = schema.getEntityTypes();
    if (providerEntityTypes != null) {
      for (CsdlEntityType entityType : providerEntityTypes) {
        FullQualifiedName entityTypeName = new FullQualifiedName(namespace, entityType.getName());
        EdmEntityTypeImpl entityTypeImpl = new EdmEntityTypeImpl(edm, entityTypeName, entityType);
        edmEntityTypes.add(entityTypeImpl);
        edm.cacheEntityType(entityTypeName, entityTypeImpl);
      }
    }
    return edmEntityTypes;
  }

  protected List<EdmComplexType> createComplexTypes() {
    final List<EdmComplexType> edmComplexTypes = new ArrayList<EdmComplexType>();
    final List<CsdlComplexType> providerComplexTypes = schema.getComplexTypes();
    if (providerComplexTypes != null) {
      for (CsdlComplexType complexType : providerComplexTypes) {
        FullQualifiedName comlexTypeName = new FullQualifiedName(namespace, complexType.getName());
        EdmComplexTypeImpl complexTypeImpl = new EdmComplexTypeImpl(edm, comlexTypeName, complexType);
        edmComplexTypes.add(complexTypeImpl);
        edm.cacheComplexType(comlexTypeName, complexTypeImpl);
      }
    }
    return edmComplexTypes;
  }

  protected List<EdmAction> createActions() {
    final List<EdmAction> edmActions = new ArrayList<EdmAction>();
    final List<CsdlAction> providerActions = schema.getActions();
    if (providerActions != null) {
      for (CsdlAction action : providerActions) {
        FullQualifiedName actionName = new FullQualifiedName(namespace, action.getName());
        EdmActionImpl edmActionImpl = new EdmActionImpl(edm, actionName, action);
        edmActions.add(edmActionImpl);
        edm.cacheAction(actionName, edmActionImpl);
      }
    }
    return edmActions;
  }

  protected List<EdmFunction> createFunctions() {
    final List<EdmFunction> edmFunctions = new ArrayList<EdmFunction>();
    final List<CsdlFunction> providerFunctions = schema.getFunctions();
    if (providerFunctions != null) {
      for (CsdlFunction function : providerFunctions) {
        FullQualifiedName functionName = new FullQualifiedName(namespace, function.getName());
        EdmFunctionImpl functionImpl = new EdmFunctionImpl(edm, functionName, function);
        edmFunctions.add(functionImpl);
        edm.cacheFunction(functionName, functionImpl);
      }
    }
    return edmFunctions;
  }

  protected List<EdmTerm> createTerms() {
    final List<EdmTerm> edmTerms = new ArrayList<EdmTerm>();
    final List<CsdlTerm> providerTerms = schema.getTerms();
    if (providerTerms != null) {
      for (CsdlTerm term : providerTerms) {
        FullQualifiedName termName = new FullQualifiedName(namespace, term.getName());
        EdmTermImpl termImpl = new EdmTermImpl(edm, getNamespace(), term);
        edmTerms.add(termImpl);
        edm.cacheTerm(termName, termImpl);
      }
    }
    return edmTerms;
  }

  protected List<EdmAnnotations> createAnnotationGroups() {
    final List<EdmAnnotations> edmAnnotationGroups = new ArrayList<EdmAnnotations>();
    final List<CsdlAnnotations> providerAnnotations =
        schema.getAnnotationGroups();
    if (providerAnnotations != null) {
      for (CsdlAnnotations annotationGroup : providerAnnotations) {
        FullQualifiedName targetName;
        if (annotationGroup.getTarget().contains(".")) {
          targetName = new FullQualifiedName(annotationGroup.getTarget());
        } else {
          targetName = new FullQualifiedName(namespace, annotationGroup.getTarget());
        }
        EdmAnnotationsImpl annotationsImpl = new EdmAnnotationsImpl(edm, annotationGroup);
        edmAnnotationGroups.add(annotationsImpl);
        edm.cacheAnnotationGroup(targetName, annotationsImpl);
      }
    }
    return edmAnnotationGroups;
  }

  protected List<EdmAnnotation> createAnnotations() {
    final List<EdmAnnotation> edmAnnotations = new ArrayList<EdmAnnotation>();
    final List<CsdlAnnotation> providerAnnotations =
        schema.getAnnotations();
    if (providerAnnotations != null) {
      for (CsdlAnnotation annotation : providerAnnotations) {
        EdmAnnotationImpl annotationImpl = new EdmAnnotationImpl(edm, annotation);
        edmAnnotations.add(annotationImpl);
      }
    }
    return edmAnnotations;
  }
}
