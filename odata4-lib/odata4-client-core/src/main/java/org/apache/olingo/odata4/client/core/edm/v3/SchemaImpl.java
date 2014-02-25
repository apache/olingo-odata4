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
package org.apache.olingo.odata4.client.core.edm.v3;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.Schema;
import org.apache.olingo.odata4.client.core.edm.AbstractSchema;

public class SchemaImpl extends AbstractSchema implements Schema {

  private static final long serialVersionUID = 4453992249818796144L;

  private final List<AnnotationsImpl> annotationList = new ArrayList<AnnotationsImpl>();

  private final List<AssociationImpl> associations = new ArrayList<AssociationImpl>();

  private final List<ComplexTypeImpl> complexTypes = new ArrayList<ComplexTypeImpl>();

  private final List<EntityContainerImpl> entityContainers = new ArrayList<EntityContainerImpl>();

  private final List<EntityTypeImpl> entityTypes = new ArrayList<EntityTypeImpl>();

  private final List<EnumTypeImpl> enumTypes = new ArrayList<EnumTypeImpl>();

  private final List<UsingImpl> usings = new ArrayList<UsingImpl>();

  private final List<ValueTermImpl> valueTerms = new ArrayList<ValueTermImpl>();

  public AssociationImpl getAssociation(final String name) {
    return getOneByName(name, getAssociations());
  }

  @Override
  public List<AnnotationsImpl> getAnnotationsList() {
    return annotationList;
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

  public List<AssociationImpl> getAssociations() {
    return associations;
  }

  public List<UsingImpl> getUsings() {
    return usings;
  }

  public List<ValueTermImpl> getValueTerms() {
    return valueTerms;
  }

  @Override
  public List<EntityContainerImpl> getEntityContainers() {
    return entityContainers;
  }

  @Override
  public EntityContainerImpl getDefaultEntityContainer() {
    EntityContainerImpl result = null;
    for (EntityContainerImpl container : getEntityContainers()) {
      if (container.isDefaultEntityContainer()) {
        result = container;
      }
    }
    return result;
  }

  @Override
  public EntityContainerImpl getEntityContainer(final String name) {
    return getOneByName(name, getEntityContainers());
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
