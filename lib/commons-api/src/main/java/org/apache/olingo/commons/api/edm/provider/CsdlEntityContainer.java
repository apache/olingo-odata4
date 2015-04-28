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

public class CsdlEntityContainer extends CsdlAbstractEdmItem implements CsdlNamed, CsdlAnnotatable {

  private static final long serialVersionUID = 5384682515007129458L;

  private String name;

  private FullQualifiedName extendsContainer;

  private List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();

  private List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();

  private List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();

  private List<CsdlSingleton> singletons = new ArrayList<CsdlSingleton>();

  private final List<CsdlAnnotation> annotations = new ArrayList<CsdlAnnotation>();

  // Annotations
  public String getName() {
    return name;
  }

  public CsdlEntityContainer setName(final String name) {
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

  public CsdlEntityContainer setExtendsContainer(final String extendsContainer) {
    this.extendsContainer = new FullQualifiedName(extendsContainer);
    return this;
  }

  public List<CsdlEntitySet> getEntitySets() {
    return entitySets;
  }

  public CsdlEntitySet getEntitySet(final String name) {
    return getOneByName(name, getEntitySets());
  }

  public CsdlEntityContainer setEntitySets(final List<CsdlEntitySet> entitySets) {
    this.entitySets = entitySets;
    return this;
  }

  public List<CsdlActionImport> getActionImports() {
    return actionImports;
  }

  /**
   * Gets the first action import with given name.
   *
   * @param name name.
   * @return action import.
   */
  public CsdlActionImport getActionImport(final String name) {
    return getOneByName(name, getActionImports());
  }

  /**
   * Gets all action imports with given name.
   *
   * @param name name.
   * @return action imports.
   */
  public List<CsdlActionImport> getActionImports(final String name) {
    return getAllByName(name, getActionImports());
  }

  public CsdlEntityContainer setActionImports(final List<CsdlActionImport> actionImports) {
    this.actionImports = actionImports;
    return this;
  }

  public List<CsdlFunctionImport> getFunctionImports() {
    return functionImports;
  }

  /**
   * Gets the first function import with given name.
   *
   * @param name name.
   * @return function import.
   */
  public CsdlFunctionImport getFunctionImport(final String name) {
    return getOneByName(name, getFunctionImports());
  }

  /**
   * Gets all function imports with given name.
   *
   * @param name name.
   * @return function imports.
   */
  public List<CsdlFunctionImport> getFunctionImports(final String name) {
    return getAllByName(name, getFunctionImports());
  }

  public CsdlEntityContainer setFunctionImports(final List<CsdlFunctionImport> functionImports) {
    this.functionImports = functionImports;
    return this;
  }

  public List<CsdlSingleton> getSingletons() {
    return singletons;
  }

  public CsdlSingleton getSingleton(final String name) {
    return getOneByName(name, getSingletons());
  }

  public CsdlEntityContainer setSingletons(final List<CsdlSingleton> singletons) {
    this.singletons = singletons;
    return this;
  }

  @Override
  public List<CsdlAnnotation> getAnnotations() {
    return annotations;
  }

}
