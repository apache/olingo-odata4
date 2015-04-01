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

public class Schema extends AbstractEdmItem implements Annotatable{
  
  private static final long serialVersionUID = -1527213201328056750L;

  private String namespace;

  private String alias;

  private List<EnumType> enumTypes = new ArrayList<EnumType>();

  private List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();

  private List<EntityType> entityTypes = new ArrayList<EntityType>();

  private List<ComplexType> complexTypes = new ArrayList<ComplexType>();

  private List<Action> actions = new ArrayList<Action>();

  private List<Function> functions = new ArrayList<Function>();

  private EntityContainer entityContainer;

  private List<Term> terms = new ArrayList<Term>();
  
  private final List<Annotations> annotationGroups = new ArrayList<Annotations>();
  
  private final List<Annotation> annotations = new ArrayList<Annotation>();
  
  private Map<String, Annotatable> annotatables;


  public String getNamespace() {
    return namespace;
  }

  public Schema setNamespace(final String namespace) {
    this.namespace = namespace;
    return this;
  }

  public String getAlias() {
    return alias;
  }

  public Schema setAlias(final String alias) {
    this.alias = alias;
    return this;
  }
  
  public List<EnumType> getEnumTypes() {
    return enumTypes;
  }
  
  public EnumType getEnumType(final String name) {
    return getOneByName(name, getEnumTypes());
  }

  public Schema setEnumTypes(final List<EnumType> enumTypes) {
    this.enumTypes = enumTypes;
    return this;
  }

  public List<TypeDefinition> getTypeDefinitions() {
    return typeDefinitions;
  }

  public TypeDefinition getTypeDefinition(final String name) {
    return getOneByName(name, getTypeDefinitions());
  }
  
  public Schema setTypeDefinitions(final List<TypeDefinition> typeDefinitions) {
    this.typeDefinitions = typeDefinitions;
    return this;
  }

  public List<EntityType> getEntityTypes() {
    return entityTypes;
  }

  public EntityType getEntityType(final String name) {
    return getOneByName(name, getEntityTypes());
  }
  
  public Schema setEntityTypes(final List<EntityType> entityTypes) {
    this.entityTypes = entityTypes;
    return this;
  }

  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public ComplexType getComplexType(final String name) {
    return getOneByName(name, getComplexTypes());
  }

  public Schema setComplexTypes(final List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
    return this;
  }

  public List<Action> getActions() {
    return actions;
  }

  /**
   * All actions with the given name
   * @param name
   * @return a list of actions
   */
  public List<Action> getActions(final String name) {
    return getAllByName(name, getActions());
  }
  
  public Schema setActions(final List<Action> actions) {
    this.actions = actions;
    return this;
  }

  public List<Function> getFunctions() {
    return functions;
  }
  
  /**
   * All functions with the given name
   * @param name
   * @return a list of functions
   */
  public List<Function> getFunctions(final String name) {
    return getAllByName(name, getFunctions());
  }

  public Schema setFunctions(final List<Function> functions) {
    this.functions = functions;
    return this;
  }

  public EntityContainer getEntityContainer() {
    return entityContainer;
  }

  public Schema setEntityContainer(final EntityContainer entityContainer) {
    this.entityContainer = entityContainer;
    return this;
  }

  public List<Term> getTerms() {
    return terms;
  }
  
  public Term getTerm(final String name) {
    return getOneByName(name, getTerms());
  }


  public Schema setTerms(final List<Term> terms) {
    this.terms = terms;
    return this;
  }
  
  public List<Annotations> getAnnotationGroups() {
    return annotationGroups;
  }

  public Annotations getAnnotationGroup(final String target) {
    Annotations result = null;
    for (Annotations annots : getAnnotationGroups()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

  public Annotation getAnnotation(final String term) {
    Annotation result = null;
    for (Annotation annot : getAnnotations()) {
      if (term.equals(annot.getTerm())) {
        result = annot;
      }
    }
    return result;
  }

  public List<Annotation> getAnnotations() {
    return annotations;
  }
  
  public Map<String, Annotatable> getAnnotatables() {
    if (annotatables == null) {
      annotatables = new HashMap<String, Annotatable>();
      for (Annotations annotationGroup : getAnnotationGroups()) {
        annotatables.put(null, annotationGroup);
      }
      for (Annotation annotation : getAnnotations()) {
        annotatables.put(annotation.getTerm(), annotation);
      }
      for (Action action : getActions()) {
        annotatables.put(action.getName(), action);
      }
      for (ComplexType complexType : getComplexTypes()) {
        annotatables.put(complexType.getName(), complexType);
      }
      for (EntityType entityType : getEntityTypes()) {
        annotatables.put(entityType.getName(), entityType);
      }
      for (EnumType enumType : getEnumTypes()) {
        annotatables.put(enumType.getName(), enumType);
      }
      for (Function function : getFunctions()) {
        annotatables.put(function.getName(), function);
      }
      for (Term term : getTerms()) {
        annotatables.put(term.getName(), term);
      }
      for (TypeDefinition typedef : getTypeDefinitions()) {
        annotatables.put(typedef.getName(), typedef);
      }
      if (entityContainer != null) {
        annotatables.put(entityContainer.getName(), entityContainer);
        for (Annotation annotation : entityContainer.getAnnotations()) {
          annotatables.put(annotation.getTerm(), annotation);
        }
        for (ActionImport actionImport : entityContainer.getActionImports()) {
          annotatables.put(actionImport.getName(), actionImport);
        }
        for (FunctionImport functionImport : entityContainer.getFunctionImports()) {
          annotatables.put(functionImport.getName(), functionImport);
        }
        for (EntitySet entitySet : entityContainer.getEntitySets()) {
          annotatables.put(entitySet.getName(), entitySet);
        }
        for (Singleton singleton : entityContainer.getSingletons()) {
          annotatables.put(singleton.getName(), singleton);
        }
      }
    }
    return annotatables;
  }
}
