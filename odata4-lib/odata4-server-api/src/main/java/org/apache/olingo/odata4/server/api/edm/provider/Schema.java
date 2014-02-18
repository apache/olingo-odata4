/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.server.api.edm.provider;

import java.util.List;

public class Schema {

  private String namespace;
  private String alias;
  private List<EnumType> enumTypes;
  private List<TypeDefinition> typeDefinitions;
  private List<EntityType> entityTypes;
  private List<ComplexType> complexTypes;
  private List<Action> actions;
  private List<Function> functions;
  private EntityContainer entityContainer;
  private List<Term> terms;

  // Annotations

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

  public Schema setEnumTypes(final List<EnumType> enumTypes) {
    this.enumTypes = enumTypes;
    return this;
  }

  public List<TypeDefinition> getTypeDefinitions() {
    return typeDefinitions;
  }

  public Schema setTypeDefinitions(final List<TypeDefinition> typeDefinitions) {
    this.typeDefinitions = typeDefinitions;
    return this;
  }

  public List<EntityType> getEntityTypes() {
    return entityTypes;
  }

  public Schema setEntityTypes(final List<EntityType> entityTypes) {
    this.entityTypes = entityTypes;
    return this;
  }

  public List<ComplexType> getComplexTypes() {
    return complexTypes;
  }

  public Schema setComplexTypes(final List<ComplexType> complexTypes) {
    this.complexTypes = complexTypes;
    return this;
  }

  public List<Action> getActions() {
    return actions;
  }

  public Schema setActions(final List<Action> actions) {
    this.actions = actions;
    return this;
  }

  public List<Function> getFunctions() {
    return functions;
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

  public Schema setTerms(final List<Term> terms) {
    this.terms = terms;
    return this;
  }
}
