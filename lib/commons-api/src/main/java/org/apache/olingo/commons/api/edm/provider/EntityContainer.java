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
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;

public class EntityContainer extends AbstractEdmItem implements Named, Annotatable {

  private static final long serialVersionUID = 5384682515007129458L;

  private String name;

  private FullQualifiedName extendsContainer;

  private List<EntitySet> entitySets = new ArrayList<EntitySet>();

  private List<ActionImport> actionImports = new ArrayList<ActionImport>();

  private List<FunctionImport> functionImports = new ArrayList<FunctionImport>();

  private List<Singleton> singletons = new ArrayList<Singleton>();

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  // Annotations
  public String getName() {
    return name;
  }

  public EntityContainer setName(final String name) {
    this.name = name;
    return this;
  }

  public String getExtendsContainer() {
    if (extendsContainer != null) {
      return extendsContainer.getFullQualifiedNameAsString();
    }
    return null;
  }

  public FullQualifiedName getExtendsContainerFQN() {
    return extendsContainer;
  }

  public EntityContainer setExtendsContainer(final String extendsContainer) {
    this.extendsContainer = new FullQualifiedName(extendsContainer);
    return this;
  }

  public List<EntitySet> getEntitySets() {
    return entitySets;
  }

  public EntitySet getEntitySet(final String name) {
    return getOneByName(name, getEntitySets());
  }

  public EntityContainer setEntitySets(final List<EntitySet> entitySets) {
    this.entitySets = entitySets;
    return this;
  }

  public List<ActionImport> getActionImports() {
    return actionImports;
  }

  /**
   * Gets the first action import with given name.
   *
   * @param name name.
   * @return action import.
   */
  public ActionImport getActionImport(final String name) {
    return getOneByName(name, getActionImports());
  }

  /**
   * Gets all action imports with given name.
   *
   * @param name name.
   * @return action imports.
   */
  public List<ActionImport> getActionImports(final String name) {
    return getAllByName(name, getActionImports());
  }

  public EntityContainer setActionImports(final List<ActionImport> actionImports) {
    this.actionImports = actionImports;
    return this;
  }

  public List<FunctionImport> getFunctionImports() {
    return functionImports;
  }

  /**
   * Gets the first function import with given name.
   *
   * @param name name.
   * @return function import.
   */
  public FunctionImport getFunctionImport(final String name) {
    return getOneByName(name, getFunctionImports());
  }

  /**
   * Gets all function imports with given name.
   *
   * @param name name.
   * @return function imports.
   */
  public List<FunctionImport> getFunctionImports(final String name) {
    return getAllByName(name, getFunctionImports());
  }

  public EntityContainer setFunctionImports(final List<FunctionImport> functionImports) {
    this.functionImports = functionImports;
    return this;
  }

  public List<Singleton> getSingletons() {
    return singletons;
  }

  public Singleton getSingleton(final String name) {
    return getOneByName(name, getSingletons());
  }

  public EntityContainer setSingletons(final List<Singleton> singletons) {
    this.singletons = singletons;
    return this;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
