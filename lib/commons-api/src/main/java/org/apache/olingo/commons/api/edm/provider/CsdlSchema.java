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

  public String getNamespace() {
    return namespace;
  }

  public CsdlSchema setNamespace(final String namespace) {
    this.namespace = namespace;
    return this;
  }

  public String getAlias() {
    return alias;
  }

  public CsdlSchema setAlias(final String alias) {
    this.alias = alias;
    return this;
  }

  public List<CsdlEnumType> getEnumTypes() {
    return enumTypes;
  }

  public CsdlEnumType getEnumType(final String name) {
    return getOneByName(name, getEnumTypes());
  }

  public CsdlSchema setEnumTypes(final List<CsdlEnumType> enumTypes) {
    this.enumTypes = enumTypes;
    return this;
  }

  public List<CsdlTypeDefinition> getTypeDefinitions() {
    return typeDefinitions;
  }

  public CsdlTypeDefinition getTypeDefinition(final String name) {
    return getOneByName(name, getTypeDefinitions());
  }

  public CsdlSchema setTypeDefinitions(final List<CsdlTypeDefinition> typeDefinitions) {
    this.typeDefinitions = typeDefinitions;
    return this;
  }

  public List<CsdlEntityType> getEntityTypes() {
    return entityTypes;
  }

  public CsdlEntityType getEntityType(final String name) {
    return getOneByName(name, getEntityTypes());
  }

  public CsdlSchema setEntityTypes(final List<CsdlEntityType> entityTypes) {
    this.entityTypes = entityTypes;
    return this;
  }

  public List<CsdlComplexType> getComplexTypes() {
    return complexTypes;
  }

  public CsdlComplexType getComplexType(final String name) {
    return getOneByName(name, getComplexTypes());
  }

  public CsdlSchema setComplexTypes(final List<CsdlComplexType> complexTypes) {
    this.complexTypes = complexTypes;
    return this;
  }

  public List<CsdlAction> getActions() {
    return actions;
  }

  /**
   * All actions with the given name
   * @param name
   * @return a list of actions
   */
  public List<CsdlAction> getActions(final String name) {
    return getAllByName(name, getActions());
  }

  public CsdlSchema setActions(final List<CsdlAction> actions) {
    this.actions = actions;
    return this;
  }

  public List<CsdlFunction> getFunctions() {
    return functions;
  }

  /**
   * All functions with the given name
   * @param name
   * @return a list of functions
   */
  public List<CsdlFunction> getFunctions(final String name) {
    return getAllByName(name, getFunctions());
  }

  public CsdlSchema setFunctions(final List<CsdlFunction> functions) {
    this.functions = functions;
    return this;
  }

  public CsdlEntityContainer getEntityContainer() {
    return entityContainer;
  }

  public CsdlSchema setEntityContainer(final CsdlEntityContainer entityContainer) {
    this.entityContainer = entityContainer;
    return this;
  }

  public List<CsdlTerm> getTerms() {
    return terms;
  }

  public CsdlTerm getTerm(final String name) {
    return getOneByName(name, getTerms());
  }

  public CsdlSchema setTerms(final List<CsdlTerm> terms) {
    this.terms = terms;
    return this;
  }

  public List<CsdlAnnotations> getAnnotationGroups() {
    return annotationGroups;
  }

  public CsdlAnnotations getAnnotationGroup(final String target) {
    CsdlAnnotations result = null;
    for (CsdlAnnotations annots : getAnnotationGroups()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

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
