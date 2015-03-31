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

public abstract class AbstractEdmProvider implements EdmProvider {

  @Override
  public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    return null;
  }

  @Override
  public TypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
    return null;
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    return null;
  }

  @Override
  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    return null;
  }

  @Override
  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    return null;
  }

  @Override
  public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {
    return null;
  }

  @Override
  public Term getTerm(final FullQualifiedName termName) throws ODataException {
    return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    return null;
  }

  @Override
  public Singleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException {
    return null;
  }

  @Override
  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException {
    return null;
  }

  @Override
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException {
    return null;
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    return null;
  }

  @Override
  public List<AliasInfo> getAliasInfos() throws ODataException {
    return null;
  }

  @Override
  public List<Schema> getSchemas() throws ODataException {
    return null;
  }

  @Override
  public EntityContainer getEntityContainer() throws ODataException {
    return null;
  }

  @Override
  public Annotations getAnnotationsGroup(FullQualifiedName targetName) throws ODataException {
    return null;
  }

  @Override
  public Annotatable getAnnoatatable(FullQualifiedName annotatedName) throws ODataException {
    return null;
  }
}
