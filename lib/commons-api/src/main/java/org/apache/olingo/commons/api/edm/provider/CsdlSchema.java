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
package org.apache.olingo.commons.api.edm.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The type Csdl schema.
 */
public class CsdlSchema extends CsdlAbstractEdmItem implements CsdlAnnotatable {

  private static final long serialVersionUID = -1527213201328056750L;

  private String namespace;

  private String alias;

  private List<CsdlEnumType> enumTypes = new ArrayList<CsdlEnumType>();

  private List<CsdlTypeDefinition> typeDefinitions = new ArrayList<CsdlTypeDefinition>();

  private List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();

  private List<CsdlComplexType> complexTypes = new ArrayList<CsdlComplexType>();

  private List<CsdlAction> actions = new ArrayList<CsdlAction>();

  private List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

  private CsdlEntityContainer entityContainer;

  private List<CsdlTerm> terms = new ArrayList<CsdlTerm>();

  private final List<CsdlAnnotations> annotationGroups = new ArrayList<CsdlAnnotations>();

  private final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  private Map<String, CsdlAnnotatable> annotatables;

  /**
   * Gets namespace.
   *
   * @return the namespace
   */
  public String getNamespace() {
    return namespace;
  }

  /**
   * Sets namespace.
   *
   * @param namespace the namespace
   * @return the namespace
   */
  public CsdlSchema setNamespace(final String namespace) {
    this.namespace = namespace;
    return this;
  }

  /**
   * Gets alias.
   *
   * @return the alias
   */
  public String getAlias() {
    return alias;
  }

  /**
   * Sets alias.
   *
   * @param alias the alias
   * @return the alias
   */
  public CsdlSchema setAlias(final String alias) {
    this.alias = alias;
    return this;
  }

  /**
   * Gets enum types.
   *
   * @return the enum types
   */
  public List<CsdlEnumType> getEnumTypes() {
    return enumTypes;
  }

  /**
   * Gets enum type.
   *
   * @param name the name
   * @return the enum type
   */
  public CsdlEnumType getEnumType(final String name) {
    return getOneByName(name, getEnumTypes());
  }

  /**
   * Sets enum types.
   *
   * @param enumTypes the enum types
   * @return the enum types
   */
  public CsdlSchema setEnumTypes(final List<CsdlEnumType> enumTypes) {
    this.enumTypes = enumTypes;
    return this;
  }

  /**
   * Gets type definitions.
   *
   * @return the type definitions
   */
  public List<CsdlTypeDefinition> getTypeDefinitions() {
    return typeDefinitions;
  }

  /**
   * Gets type definition.
   *
   * @param name the name
   * @return the type definition
   */
  public CsdlTypeDefinition getTypeDefinition(final String name) {
    return getOneByName(name, getTypeDefinitions());
  }

  /**
   * Sets type definitions.
   *
   * @param typeDefinitions the type definitions
   * @return the type definitions
   */
  public CsdlSchema setTypeDefinitions(final List<CsdlTypeDefinition> typeDefinitions) {
    this.typeDefinitions = typeDefinitions;
    return this;
  }

  /**
   * Gets entity types.
   *
   * @return the entity types
   */
  public List<CsdlEntityType> getEntityTypes() {
    return entityTypes;
  }

  /**
   * Gets entity type.
   *
   * @param name the name
   * @return the entity type
   */
  public CsdlEntityType getEntityType(final String name) {
    return getOneByName(name, getEntityTypes());
  }

  /**
   * Sets entity types.
   *
   * @param entityTypes the entity types
   * @return the entity types
   */
  public CsdlSchema setEntityTypes(final List<CsdlEntityType> entityTypes) {
    this.entityTypes = entityTypes;
    return this;
  }

  /**
   * Gets complex types.
   *
   * @return the complex types
   */
  public List<CsdlComplexType> getComplexTypes() {
    return complexTypes;
  }

  /**
   * Gets complex type.
   *
   * @param name the name
   * @return the complex type
   */
  public CsdlComplexType getComplexType(final String name) {
    return getOneByName(name, getComplexTypes());
  }

  /**
   * Sets complex types.
   *
   * @param complexTypes the complex types
   * @return the complex types
   */
  public CsdlSchema setComplexTypes(final List<CsdlComplexType> complexTypes) {
    this.complexTypes = complexTypes;
    return this;
  }

  /**
   * Gets actions.
   *
   * @return the actions
   */
  public List<CsdlAction> getActions() {
    return actions;
  }

  /**
   * All actions with the given name
   * @param name the name
   * @return a list of actions
   */
  public List<CsdlAction> getActions(final String name) {
    return getAllByName(name, getActions());
  }

  /**
   * Sets actions.
   *
   * @param actions the actions
   * @return the actions
   */
  public CsdlSchema setActions(final List<CsdlAction> actions) {
    this.actions = actions;
    return this;
  }

  /**
   * Gets functions.
   *
   * @return the functions
   */
  public List<CsdlFunction> getFunctions() {
    return functions;
  }

  /**
   * All functions with the given name
   * @param name the name
   * @return a list of functions
   */
  public List<CsdlFunction> getFunctions(final String name) {
    return getAllByName(name, getFunctions());
  }

  /**
   * Sets functions.
   *
   * @param functions the functions
   * @return the functions
   */
  public CsdlSchema setFunctions(final List<CsdlFunction> functions) {
    this.functions = functions;
    return this;
  }

  /**
   * Gets entity container.
   *
   * @return the entity container
   */
  public CsdlEntityContainer getEntityContainer() {
    return entityContainer;
  }

  /**
   * Sets entity container.
   *
   * @param entityContainer the entity container
   * @return the entity container
   */
  public CsdlSchema setEntityContainer(final CsdlEntityContainer entityContainer) {
    this.entityContainer = entityContainer;
    return this;
  }

  /**
   * Gets terms.
   *
   * @return the terms
   */
  public List<CsdlTerm> getTerms() {
    return terms;
  }

  /**
   * Gets term.
   *
   * @param name the name
   * @return the term
   */
  public CsdlTerm getTerm(final String name) {
    return getOneByName(name, getTerms());
  }

  /**
   * Sets terms.
   *
   * @param terms the terms
   * @return the terms
   */
  public CsdlSchema setTerms(final List<CsdlTerm> terms) {
    this.terms = terms;
    return this;
  }

  /**
   * Gets annotation groups.
   *
   * @return the annotation groups
   */
  public List<CsdlAnnotations> getAnnotationGroups() {
    return annotationGroups;
  }

  /**
   * Gets annotation group.
   *
   * @param target the target
   * @return the annotation group
   */
  public CsdlAnnotations getAnnotationGroup(final String target) {
    CsdlAnnotations result = null;
    for (CsdlAnnotations annots : getAnnotationGroups()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

  /**
   * Gets annotation.
   *
   * @param term the term
   * @return the annotation
   */
  public CsdlAnnotation getAnnotation(final String term) {
    CsdlAnnotation result = null;
    for (CsdlAnnotation annot : getAnnotations()) {
      if (term.equals(annot.getTerm())) {
        result = annot;
      }
    }
    return result;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

  /**
   * Gets annotatables.
   *
   * @return the annotatables
   */
  public Map<String, CsdlAnnotatable> getAnnotatables() {
    if (annotatables == null) {
      annotatables = new HashMap<String, CsdlAnnotatable>();
      for (CsdlAnnotations annotationGroup : getAnnotationGroups()) {
        annotatables.put(null, annotationGroup);
      }
      for (CsdlAnnotation annotation : getAnnotations()) {
        annotatables.put(annotation.getTerm(), annotation);
      }
      for (CsdlAction action : getActions()) {
        annotatables.put(action.getName(), action);
      }
      for (CsdlComplexType complexType : getComplexTypes()) {
        annotatables.put(complexType.getName(), complexType);
      }
      for (CsdlEntityType entityType : getEntityTypes()) {
        annotatables.put(entityType.getName(), entityType);
      }
      for (CsdlEnumType enumType : getEnumTypes()) {
        annotatables.put(enumType.getName(), enumType);
      }
      for (CsdlFunction function : getFunctions()) {
        annotatables.put(function.getName(), function);
      }
      for (CsdlTerm term : getTerms()) {
        annotatables.put(term.getName(), term);
      }
      for (CsdlTypeDefinition typedef : getTypeDefinitions()) {
        annotatables.put(typedef.getName(), typedef);
      }
      if (entityContainer != null) {
        annotatables.put(entityContainer.getName(), entityContainer);
        for (CsdlAnnotation annotation : entityContainer.getAnnotations()) {
          annotatables.put(annotation.getTerm(), annotation);
        }
        for (CsdlActionImport actionImport : entityContainer.getActionImports()) {
          annotatables.put(actionImport.getName(), actionImport);
        }
        for (CsdlFunctionImport functionImport : entityContainer.getFunctionImports()) {
          annotatables.put(functionImport.getName(), functionImport);
        }
        for (CsdlEntitySet entitySet : entityContainer.getEntitySets()) {
          annotatables.put(entitySet.getName(), entitySet);
        }
        for (CsdlSingleton singleton : entityContainer.getSingletons()) {
          annotatables.put(singleton.getName(), singleton);
        }
      }
    }
    return annotatables;
  }
}
