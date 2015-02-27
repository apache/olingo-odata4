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
package org.apache.olingo.client.core.edm.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.client.api.edm.xml.ActionImport;
import org.apache.olingo.client.api.edm.xml.Annotation;
import org.apache.olingo.client.api.edm.xml.EntityContainer;
import org.apache.olingo.client.api.edm.xml.EntitySet;
import org.apache.olingo.client.api.edm.xml.FunctionImport;
import org.apache.olingo.client.api.edm.xml.Singleton;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EntityContainerDeserializer.class)
public class EntityContainerImpl extends AbstractEdmItem implements EntityContainer {

  private static final long serialVersionUID = 5631432527646955795L;

  private final List<EntitySet> entitySets = new ArrayList<EntitySet>();

  private final List<Singleton> singletons = new ArrayList<Singleton>();

  private final List<ActionImport> actionImports = new ArrayList<ActionImport>();

  private final List<FunctionImport> functionImports = new ArrayList<FunctionImport>();

  private final List<Annotation> annotations = new ArrayList<Annotation>();

  private String name;

  private String _extends;

  private boolean lazyLoadingEnabled;

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public String getExtends() {
    return _extends;
  }

  public void setExtends(final String _extends) {
    this._extends = _extends;
  }

  @Override
  public boolean isLazyLoadingEnabled() {
    return lazyLoadingEnabled;
  }

  public void setLazyLoadingEnabled(final boolean lazyLoadingEnabled) {
    this.lazyLoadingEnabled = lazyLoadingEnabled;
  }

  @Override
  public EntitySet getEntitySet(final String name) {
    return getOneByName(name, getEntitySets());
  }
  
  /**
   * Gets the first function import with given name.
   *
   * @param name name.
   * @return function import.
   */
  @Override
  public FunctionImport getFunctionImport(final String name) {
    return getOneByName(name, getFunctionImports());
  }

  /**
   * Gets all function imports with given name.
   *
   * @param name name.
   * @return function imports.
   */
  @Override
  public List<FunctionImport> getFunctionImports(final String name) {
    return getAllByName(name, getFunctionImports());
  }
  
  //TODO: No default container in V4 so we should delete this.
  @Override
  public boolean isDefaultEntityContainer() {
    return true;
  }

  @Override
  public List<EntitySet> getEntitySets() {
    return entitySets;
  }

  @Override
  public List<Singleton> getSingletons() {
    return singletons;
  }

  @Override
  public Singleton getSingleton(final String name) {
    return getOneByName(name, getSingletons());
  }

  /**
   * Gets the first action import with given name.
   *
   * @param name name.
   * @return action import.
   */
  @Override
  public ActionImport getActionImport(final String name) {
    return getOneByName(name, getActionImports());
  }

  /**
   * Gets all action imports with given name.
   *
   * @param name name.
   * @return action imports.
   */
  @Override
  public List<ActionImport> getActionImports(final String name) {
    return getAllByName(name, getActionImports());
  }

  @Override
  public List<ActionImport> getActionImports() {
    return actionImports;
  }

  @Override
  public List<FunctionImport> getFunctionImports() {
    return functionImports;
  }

  @Override
  public List<Annotation> getAnnotations() {
    return annotations;
  }

}
