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
package org.apache.olingo.odata4.client.core.edm.xml.v4;

import org.apache.olingo.odata4.client.api.edm.xml.v4.AnnotatedEdmItem;
import java.util.ArrayList;
import java.util.List;
import org.apache.olingo.odata4.client.api.edm.xml.v4.Annotation;
import org.apache.olingo.odata4.client.api.edm.xml.v4.EntityContainer;
import org.apache.olingo.odata4.client.core.edm.xml.AbstractEntityContainer;

public class EntityContainerImpl extends AbstractEntityContainer implements AnnotatedEdmItem, EntityContainer {

  private static final long serialVersionUID = 2526002525927260320L;

  private final List<EntitySetImpl> entitySets = new ArrayList<EntitySetImpl>();

  private final List<SingletonImpl> singletons = new ArrayList<SingletonImpl>();

  private final List<ActionImportImpl> actionImports = new ArrayList<ActionImportImpl>();

  private final List<FunctionImportImpl> functionImports = new ArrayList<FunctionImportImpl>();

  private AnnotationImpl annotation;

  @Override
  public void setDefaultEntityContainer(final boolean defaultEntityContainer) {
    // no action: a single entity container MUST be available as per OData 4.0
  }

  @Override
  public boolean isDefaultEntityContainer() {
    return true;
  }

  @Override
  public EntitySetImpl getEntitySet(final String name) {
    return (EntitySetImpl) super.getEntitySet(name);
  }

  @Override
  public List<EntitySetImpl> getEntitySets() {
    return entitySets;
  }

  public List<SingletonImpl> getSingletons() {
    return singletons;
  }

  public SingletonImpl getSingleton(final String name) {
    return getOneByName(name, getSingletons());
  }

  @Override
  public FunctionImportImpl getFunctionImport(final String name) {
    return (FunctionImportImpl) super.getFunctionImport(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<FunctionImportImpl> getFunctionImports(final String name) {
    return (List<FunctionImportImpl>) super.getFunctionImports(name);
  }

  /**
   * Gets the first action import with given name.
   *
   * @param name name.
   * @return action import.
   */
  @Override
  public ActionImportImpl getActionImport(final String name) {
    return getOneByName(name, getActionImports());
  }

  /**
   * Gets all action imports with given name.
   *
   * @param name name.
   * @return action imports.
   */
  @Override
  public List<ActionImportImpl> getActionImports(final String name) {
    return getAllByName(name, getActionImports());
  }

  @Override
  public List<ActionImportImpl> getActionImports() {
    return actionImports;
  }

  @Override
  public List<FunctionImportImpl> getFunctionImports() {
    return functionImports;
  }

  @Override
  public AnnotationImpl getAnnotation() {
    return annotation;
  }

  @Override
  public void setAnnotation(final Annotation annotation) {
    this.annotation = (AnnotationImpl) annotation;
  }

}
