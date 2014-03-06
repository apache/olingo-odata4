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
package org.apache.olingo.odata4.client.core.edm.xml.v3;

import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.ComplexType;
import org.apache.olingo.odata4.client.api.edm.xml.EntityContainer;
import org.apache.olingo.odata4.client.api.edm.xml.EntityType;
import org.apache.olingo.odata4.client.api.edm.xml.EnumType;
import org.apache.olingo.odata4.client.api.edm.xml.Schema;
import org.apache.olingo.odata4.client.api.edm.xml.v3.Annotations;
import org.apache.olingo.odata4.client.api.edm.xml.v3.Association;
import org.apache.olingo.odata4.client.api.edm.xml.v3.Using;
import org.apache.olingo.odata4.client.api.edm.xml.v3.ValueTerm;
import org.apache.olingo.odata4.client.core.edm.xml.AbstractSchema;

public class SchemaImpl extends AbstractSchema implements Schema {

  private static final long serialVersionUID = 4453992249818796144L;

  private final List<Annotations> annotationList = new ArrayList<Annotations>();

  private final List<Association> associations = new ArrayList<Association>();

  private final List<ComplexType> complexTypes = new ArrayList<ComplexType>();

  private final List<EntityContainer> entityContainers = new ArrayList<EntityContainer>();

  private final List<EntityType> entityTypes = new ArrayList<EntityType>();

  private final List<EnumType> enumTypes = new ArrayList<EnumType>();

  private final List<Using> usings = new ArrayList<Using>();

  private final List<ValueTerm> valueTerms = new ArrayList<ValueTerm>();

  public Association getAssociation(final String name) {
    return getOneByName(name, getAssociations());
  }

  @Override
  public List<Annotations> getAnnotationsList() {
    return annotationList;
  }

  @Override
  public Annotations getAnnotationsList(final String target) {
    Annotations result = null;
    for (Annotations annots : getAnnotationsList()) {
      if (target.equals(annots.getTarget())) {
        result = annots;
      }
    }
    return result;
  }

  public List<Association> getAssociations() {
    return associations;
  }

  public List<Using> getUsings() {
    return usings;
  }

  public List<ValueTerm> getValueTerms() {
    return valueTerms;
  }

  @Override
  public List<EntityContainer> getEntityContainers() {
    return entityContainers;
  }

  @Override
  public EntityContainer getDefaultEntityContainer() {
    EntityContainer result = null;
    for (EntityContainer container : getEntityContainers()) {
      if (container.isDefaultEntityContainer()) {
        result = container;
      }
    }
    return result;
  }

  @Override
  public EntityContainer getEntityContainer(final String name) {
    return getOneByName(name, getEntityContainers());
  }

  @Override
  public EnumTypeImpl getEnumType(final String name) {
    return (EnumTypeImpl) super.getEnumType(name);
  }

  @Override
  public List<EnumType> getEnumTypes() {
    return enumTypes;
  }

  @Override
  public ComplexTypeImpl getComplexType(final String name) {
    return (ComplexTypeImpl) super.getComplexType(name);
  }

  @Override
  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  @Override
  public EntityTypeImpl getEntityType(final String name) {
    return (EntityTypeImpl) super.getEntityType(name);
  }

  @Override
  public List<EntityType> getEntityTypes() {
    return entityTypes;
  }

}
