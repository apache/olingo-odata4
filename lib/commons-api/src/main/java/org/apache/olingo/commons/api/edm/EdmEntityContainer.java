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
package org.apache.olingo.commons.api.edm;

import java.util.List;

/**
 * A CSDL EntityContainer element.
 *
 * <br/>
 * EdmEntityContainer hold the information of EntitySets, Singletons, ActionImports and FunctionImports contained
 */
public interface EdmEntityContainer extends EdmNamed, EdmAnnotatable {

  /**
   * @return namespace of this entity container
   */
  String getNamespace();

  /**
   * @return full qualified name of this entity container
   */
  FullQualifiedName getFullQualifiedName();

  /**
   * Get contained Singleton by name.
   *
   * @param name name of contained Singleton
   * @return {@link EdmSingleton}
   */
  EdmSingleton getSingleton(String name);

  /**
   * Get contained EntitySet by name.
   *
   * @param name name of contained EntitySet
   * @return {@link EdmEntitySet}
   */
  EdmEntitySet getEntitySet(String name);

  /**
   * Get contained ActionImport by name.
   *
   * @param name name of contained ActionImport
   * @return {@link EdmActionImport}
   */
  EdmActionImport getActionImport(String name);

  /**
   * Get contained FunctionImport by name.
   *
   * @param name name of contained FunctionImport
   * @return {@link EdmFunctionImport}
   */
  EdmFunctionImport getFunctionImport(String name);

  /**
   * This method <b>DOES NOT</b> support lazy loading
   *
   * @return returns all entity sets for this container.
   */
  List<EdmEntitySet> getEntitySets();
  
  /**
   * This method <b>DOES NOT</b> support lazy loading
   *
   * @return returns all entity sets for this container with 
   * annotations defined in external file.
   */
  List<EdmEntitySet> getEntitySetsWithAnnotations();

  /**
   * This method <b>DOES NOT</b> support lazy loading
   *
   * @return returns all function imports for this container.
   */
  List<EdmFunctionImport> getFunctionImports();

  /**
   * This method <b>DOES NOT</b> support lazy loading
   *
   * @return returns all singletons for this container.
   */
  List<EdmSingleton> getSingletons();

  /**
   * This method <b>DOES NOT</b> support lazy loading
   *
   * @return returns all action imports for this container.
   */
  List<EdmActionImport> getActionImports();

  /**
   * @return the {@link FullQualifiedName} of the parentContainer or null if no parent is specified
   */
  FullQualifiedName getParentContainerName();
}
