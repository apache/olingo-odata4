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
package org.apache.olingo.client.core.edm.xml.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.edm.xml.EnumType;
import org.apache.olingo.client.api.edm.xml.v4.Action;
import org.apache.olingo.client.api.edm.xml.v4.ActionImport;
import org.apache.olingo.client.api.edm.xml.v4.Annotatable;
import org.apache.olingo.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.client.api.edm.xml.v4.Annotations;
import org.apache.olingo.client.api.edm.xml.v4.ComplexType;
import org.apache.olingo.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.client.api.edm.xml.v4.EntitySet;
import org.apache.olingo.client.api.edm.xml.v4.EntityType;
import org.apache.olingo.client.api.edm.xml.v4.Function;
import org.apache.olingo.client.api.edm.xml.v4.FunctionImport;
import org.apache.olingo.client.api.edm.xml.v4.Schema;
import org.apache.olingo.client.api.edm.xml.v4.Singleton;
import org.apache.olingo.client.api.edm.xml.v4.Term;
import org.apache.olingo.client.api.edm.xml.v4.TypeDefinition;
import org.apache.olingo.client.core.edm.xml.AbstractSchema;

public class SchemaImpl extends AbstractSchema implements Schema {

  private static final long serialVersionUID = 1911087363912024939L;

  private final List<Action> actions = new ArrayList<Action>();

  private final List<Annotations> annotationGroups = new ArrayList<Annotations>();

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();

  private EntityContainer entityContainer;

  private final List<EnumType> enumTypes = new ArrayList<EnumType>();

  private final List<EntityType> entityTypes = new ArrayList<EntityType>();

  private final List<Function> functions = new ArrayList<Function>();

  private final List<Term> terms = new ArrayList<Term>();

  private final List<TypeDefinition> typeDefinitions = new ArrayList<TypeDefinition>();

  private Map<String, Annotatable> annotatables;

  @Override
  public List<Action> getActions() {
    return actions;
  }

  @Override
  public List<Action> getActions(final String name) {
    return getAllByName(name, getActions());
  }

  @Override
  public List<Annotations> getAnnotationGroups() {
    return annotationGroups;
  }

  @Override
  public Annotations getAnnotationGroup(final String target) {
    Annotations result = null;
    for (Annotations annots : getAnnotationGroups()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

  @Override
  public Annotation getAnnotation(final String term) {
    Annotation result = null;
    for (Annotation annot : getAnnotations()) {
      if (term.equals(annot.getTerm())) {
        result = annot;
      }
    }
    return result;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

  @Override
  public List<Function> getFunctions() {
    return functions;
  }

  @Override
  public List<Function> getFunctions(final String name) {
    return getAllByName(name, getFunctions());
  }

  @Override
  public Term getTerm(final String name) {
    return getOneByName(name, getTerms());
  }

  @Override
  public List<Term> getTerms() {
    return terms;
  }

  @Override
  public TypeDefinition getTypeDefinition(final String name) {
    return getOneByName(name, getTypeDefinitions());
  }

  @Override
  public List<TypeDefinition> getTypeDefinitions() {
    return typeDefinitions;
  }

  @Override
  public EntityContainer getEntityContainer() {
    return entityContainer;
  }

  public void setEntityContainer(final EntityContainerImpl entityContainer) {
    this.entityContainer = entityContainer;
  }

  @Override
  public List<EntityContainer> getEntityContainers() {
    return entityContainer == null
            ? Collections.<EntityContainer>emptyList() : Collections.singletonList(entityContainer);
  }

  @Override
  public EntityContainer getDefaultEntityContainer() {
    return entityContainer;
  }

  @Override
  public EntityContainer getEntityContainer(final String name) {
    if (entityContainer != null && name.equals(entityContainer.getName())) {
      return entityContainer;
    }
    return null;
  }

  @Override
  public List<EnumType> getEnumTypes() {
    return enumTypes;
  }

  @Override
  public ComplexType getComplexType(final String name) {
    return (ComplexType) super.getComplexType(name);
  }

  @Override
  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  @Override
  public EntityType getEntityType(final String name) {
    return (EntityType) super.getEntityType(name);
  }

  @Override
  public List<EntityType> getEntityTypes() {
    return entityTypes;
  }

  @Override
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
        annotatables.put(enumType.getName(), (EnumTypeImpl) enumType);
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
