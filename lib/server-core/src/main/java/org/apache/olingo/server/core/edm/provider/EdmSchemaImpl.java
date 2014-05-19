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
package org.apache.olingo.server.core.edm.provider;

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
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.AbstractEdmSchema;
import org.apache.olingo.server.api.edm.provider.Action;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.EnumType;
import org.apache.olingo.server.api.edm.provider.Function;
import org.apache.olingo.server.api.edm.provider.Schema;
import org.apache.olingo.server.api.edm.provider.TypeDefinition;

public class EdmSchemaImpl extends AbstractEdmSchema {

  private final Schema schema;

  private final Edm edm;

  private final EdmProvider provider;

  public EdmSchemaImpl(final Edm edm, final EdmProvider provider, final Schema schema) {
    super(schema.getNamespace(), schema.getAlias());
    this.edm = edm;
    this.provider = provider;
    this.schema = schema;
  }

  @Override
  protected EdmEntityContainer createEntityContainer() {
    if (schema.getEntityContainer() != null) {
      FullQualifiedName containerFQN = new FullQualifiedName(namespace, schema.getEntityContainer().getName());
      return new EdmEntityContainerImpl(edm, provider, containerFQN, schema.getEntityContainer());
    }
    return null;
  }

  @Override
  protected List<EdmTypeDefinition> createTypeDefinitions() {
    final List<EdmTypeDefinition> typeDefinitions = new ArrayList<EdmTypeDefinition>();
    final List<TypeDefinition> providerTypeDefinitions = schema.getTypeDefinitions();
    if (providerTypeDefinitions != null) {
      for (TypeDefinition def : providerTypeDefinitions) {
        typeDefinitions.add(new EdmTypeDefinitionImpl(edm, new FullQualifiedName("namespace", def.getName()), def));
      }
    }
    return typeDefinitions;
  }

  @Override
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

  @Override
  protected List<EdmEntityType> createEntityTypes() {
    final List<EdmEntityType> entityTypes = new ArrayList<EdmEntityType>();
    final List<EntityType> providerEntityTypes = schema.getEntityTypes();
    if (providerEntityTypes != null) {
      for (EntityType entityType : providerEntityTypes) {
        entityTypes.add(EdmEntityTypeImpl.getInstance(edm, new FullQualifiedName(namespace, entityType.getName()),
            entityType));
      }
    }
    return entityTypes;
  }

  @Override
  protected List<EdmComplexType> createComplexTypes() {
    final List<EdmComplexType> complexTypes = new ArrayList<EdmComplexType>();
    final List<ComplexType> providerComplexTypes = schema.getComplexTypes();
    if (providerComplexTypes != null) {
      for (ComplexType complexType : providerComplexTypes) {
        complexTypes.add(EdmComplexTypeImpl.getInstance(edm, new FullQualifiedName(namespace, complexType.getName()),
            complexType));
      }
    }
    return complexTypes;
  }

  @Override
  protected List<EdmAction> createActions() {
    final List<EdmAction> actions = new ArrayList<EdmAction>();
    final List<Action> providerActions = schema.getActions();
    if (providerActions != null) {
      for (Action action : providerActions) {
        actions.add(EdmActionImpl.getInstance(edm, new FullQualifiedName(namespace, action.getName()), action));
      }
    }
    return actions;
  }

  @Override
  protected List<EdmFunction> createFunctions() {
    final List<EdmFunction> functions = new ArrayList<EdmFunction>();
    final List<Function> providerFunctions = schema.getFunctions();
    if (providerFunctions != null) {
      for (Function function : providerFunctions) {
        functions.add(EdmFunctionImpl.getInstance(edm, new FullQualifiedName(namespace, function.getName()), function));
      }
    }
    return functions;
  }

  @Override
  protected List<EdmTerm> createTerms() {
    // TODO: implement
    return Collections.emptyList();
  }

  @Override
  protected List<EdmAnnotations> createAnnotationGroups() {
    // TODO: implement
    return Collections.emptyList();
  }

  @Override
  protected List<EdmAnnotation> createAnnotations() {
    // TODO: implement
    return Collections.emptyList();
  }

  @Override
  public EdmAnnotation getAnnotation(final EdmTerm term) {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<EdmAnnotation> getAnnotations() {
    // TODO: implement
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
