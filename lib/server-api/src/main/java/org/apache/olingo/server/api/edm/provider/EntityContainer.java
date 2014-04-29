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
package org.apache.olingo.server.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EntityContainer {

  private String name;

  private FullQualifiedName extendsContainer;

  private List<EntitySet> entitySets;

  private List<ActionImport> actionImports;

  private List<FunctionImport> functionImports;

  private List<Singleton> singletons;

  // Annotations
  public String getName() {
    return name;
  }

  public EntityContainer setName(final String name) {
    this.name = name;
    return this;
  }

  public FullQualifiedName getExtendsContainer() {
    return extendsContainer;
  }

  public EntityContainer setExtendsContainer(final FullQualifiedName extendsContainer) {
    this.extendsContainer = extendsContainer;
    return this;
  }

  public List<EntitySet> getEntitySets() {
    return entitySets;
  }

  public EntityContainer setEntitySets(final List<EntitySet> entitySets) {
    this.entitySets = entitySets;
    return this;
  }

  public List<ActionImport> getActionImports() {
    return actionImports;
  }

  public EntityContainer setActionImports(final List<ActionImport> actionImports) {
    this.actionImports = actionImports;
    return this;
  }

  public List<FunctionImport> getFunctionImports() {
    return functionImports;
  }

  public EntityContainer setFunctionImports(final List<FunctionImport> functionImports) {
    this.functionImports = functionImports;
    return this;
  }

  public List<Singleton> getSingletons() {
    return singletons;
  }

  public EntityContainer setSingletons(final List<Singleton> singletons) {
    this.singletons = singletons;
    return this;
  }
}
