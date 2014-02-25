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
package org.apache.olingo.odata4.client.core.edm.v4;

import org.apache.olingo.odata4.client.api.edm.v4.AnnotatedEdmItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.Schema;
import org.apache.olingo.odata4.client.api.edm.v4.Annotation;
import org.apache.olingo.odata4.client.core.edm.AbstractSchema;

public class SchemaImpl extends AbstractSchema implements Schema, AnnotatedEdmItem {

  private static final long serialVersionUID = 4453992249818796144L;

  private final List<ActionImpl> actions = new ArrayList<ActionImpl>();

  private final List<AnnotationsImpl> annotationsList = new ArrayList<AnnotationsImpl>();

  private final List<AnnotationImpl> annotations = new ArrayList<AnnotationImpl>();

  private final List<ComplexTypeImpl> complexTypes = new ArrayList<ComplexTypeImpl>();

  private EntityContainerImpl entityContainer;

  private final List<EnumTypeImpl> enumTypes = new ArrayList<EnumTypeImpl>();

  private final List<EntityTypeImpl> entityTypes = new ArrayList<EntityTypeImpl>();

  private final List<FunctionImpl> functions = new ArrayList<FunctionImpl>();

  private final List<TermImpl> terms = new ArrayList<TermImpl>();

  private final List<TypeDefinitionImpl> typeDefinitions = new ArrayList<TypeDefinitionImpl>();

  private AnnotationImpl annotation;

  public List<ActionImpl> getActions() {
    return actions;
  }

  public List<ActionImpl> getActions(final String name) {
    return getAllByName(name, getActions());
  }

  @Override
  public List<AnnotationsImpl> getAnnotationsList() {
    return annotationsList;
  }

  @Override
  public AnnotationsImpl getAnnotationsList(final String target) {
    AnnotationsImpl result = null;
    for (AnnotationsImpl annots : getAnnotationsList()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

  public List<AnnotationImpl> getAnnotations() {
    return annotations;
  }

  public List<FunctionImpl> getFunctions() {
    return functions;
  }

  public List<FunctionImpl> getFunctions(final String name) {
    return getAllByName(name, getFunctions());
  }

  public List<TermImpl> getTerms() {
    return terms;
  }

  public List<TypeDefinitionImpl> getTypeDefinitions() {
    return typeDefinitions;
  }

  public EntityContainerImpl getEntityContainer() {
    return entityContainer;
  }

  public void setEntityContainer(final EntityContainerImpl entityContainer) {
    this.entityContainer = entityContainer;
  }

  @Override
  public List<EntityContainerImpl> getEntityContainers() {
    return entityContainer == null
            ? Collections.<EntityContainerImpl>emptyList() : Collections.singletonList(entityContainer);
  }

  @Override
  public EntityContainerImpl getDefaultEntityContainer() {
    return entityContainer;
  }

  @Override
  public EntityContainerImpl getEntityContainer(final String name) {
    if (entityContainer != null && name.equals(entityContainer.getName())) {
      return entityContainer;
    }
    throw new IllegalArgumentException("No EntityContainer found with name " + name);
  }

  @Override
  public AnnotationImpl getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = (AnnotationImpl) annotation;
  }

  @Override
  public EnumTypeImpl getEnumType(final String name) {
    return (EnumTypeImpl) super.getEnumType(name);
  }

  @Override
  public List<EnumTypeImpl> getEnumTypes() {
    return enumTypes;
  }

  @Override
  public ComplexTypeImpl getComplexType(final String name) {
    return (ComplexTypeImpl) super.getComplexType(name);
  }

  @Override
  public List<ComplexTypeImpl> getComplexTypes() {
    return complexTypes;
  }

  @Override
  public EntityTypeImpl getEntityType(final String name) {
    return (EntityTypeImpl) super.getEntityType(name);
  }

  @Override
  public List<EntityTypeImpl> getEntityTypes() {
    return entityTypes;
  }
}
