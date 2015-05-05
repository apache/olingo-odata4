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

import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;

public interface CsdlEdmProvider {

  /**
   * This method should return an {@link CsdlEnumType} or <b>null</b> if nothing is found
   *
   * @param enumTypeName
   * @return {@link CsdlEnumType} for given name
   * @throws ODataException
   */
  public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException;

  /**
   * This method should return an {@link CsdlTypeDefinition} or <b>null</b> if nothing is found
   *
   * @param typeDefinitionName
   * @return {@link CsdlTypeDefinition} for given name
   * @throws ODataException
   */
  public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException;

  /**
   * This method should return an {@link CsdlEntityType} or <b>null</b> if nothing is found
   *
   * @param entityTypeName
   * @return {@link CsdlEntityType} for the given name
   * @throws ODataException
   */
  public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException;

  /**
   * This method should return a {@link CsdlComplexType} or <b>null</b> if nothing is found.
   *
   * @param complexTypeName
   * @return {@link CsdlComplexType} for the given name
   * @throws ODataException
   */
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException;

  /**
   * This method should return a list of all {@link CsdlAction} for the FullQualifiedname
   * or <b>null</b> if nothing is found
   *
   * @param actionName
   * @return List of {@link CsdlAction} or null
   * @throws ODataException
   */
  public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException;

  /**
   * This method should return a list of all {@link CsdlFunction} for the FullQualifiedname or <b>null</b> if nothing is
   * found
   *
   * @param functionName
   * @return List of {@link CsdlFunction} or null
   * @throws ODataException
   */
  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException;

  /**
   * This method should return a {@link CsdlTerm} for the FullQualifiedName or <b>null</b> if nothing is found.
   * @param termName the name of the Term
   * @return {@link CsdlTerm} or null
   * @throws ODataException
   */
  public CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException;

  /**
   * This method should return an {@link CsdlEntitySet} or <b>null</b> if nothing is found
   *
   * @param entityContainer this EntitySet is contained in
   * @param entitySetName
   * @return {@link CsdlEntitySet} for the given container and entityset name
   * @throws ODataException
   */
  public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlSingleton} or <b>null</b> if nothing is found
   *
   * @param entityContainer this Singleton is contained in
   * @param singletonName
   * @return {@link CsdlSingleton} for given container and singleton name
   * @throws ODataException
   */
  public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlActionImport} or <b>null</b> if nothing is found
   *
   * @param entityContainer this ActionImport is contained in
   * @param actionImportName
   * @return {@link CsdlActionImport} for the given container and ActionImport name
   * @throws ODataException
   */
  public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException;

  /**
   * This method should return a {@link CsdlFunctionImport} or <b>null</b> if nothing is found
   *
   * @param entityContainer this FunctionImport is contained in
   * @param functionImportName
   * @return {@link CsdlFunctionImport} for the given container name and function import name
   * @throws ODataException
   */
  public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException;

  /**
   * This method should return an {@link CsdlEntityContainerInfo} or <b>null</b> if nothing is found
   *
   * @param entityContainerName (null for default container)
   * @return {@link CsdlEntityContainerInfo} for the given name
   * @throws ODataException
   */
  public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
      throws ODataException;

  /**
   * This method should return a list of all namespaces which have an alias
   *
   * @return List of alias info
   * @throws ODataException
   */
  public List<CsdlAliasInfo> getAliasInfos() throws ODataException;

  /**
   * This method should return a collection of all {@link CsdlSchema}
   *
   * @return List<{@link Schema}>
   * @throws ODataException
   */
  public List<CsdlSchema> getSchemas() throws ODataException;

  /**
   * Returns the entity container of this edm
   * @return {@link CsdlEntityContainer} of this edm
   */
  public CsdlEntityContainer getEntityContainer() throws ODataException;

  /**
   * @param targetName
   * @return {@link CsdlAnnotations} group for the given Target
   */
  public CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName) throws ODataException;

  /**
   * @param annotatedName
   * @return Annotatble element by target name
   */
  public CsdlAnnotatable getAnnoatatable(FullQualifiedName annotatedName) throws ODataException;
}
