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
package org.apache.olingo.commons.api.edm.provider;

import java.util.List;

import org.apache.olingo.commons.api.edm.helper.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;
import org.apache.olingo.commons.api.edm.helper.AliasInfo;
import org.apache.olingo.commons.api.exception.ODataException;

//TODO: Finish
/**
 * Default EDM Provider which is to be extended by the application
 */
public abstract class EdmProviderAdapter implements EdmProvider {

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

  // Revisit signature
  @Override
  public Action getAction(final FullQualifiedName actionName, final FullQualifiedName bindingPatameterTypeName,
      final Boolean isBindingParameterCollection) throws ODataException {
     return null;
  }

  // Revisit Signature
  @Override
  public Function getFunction(final FullQualifiedName functionName, final FullQualifiedName bindingPatameterTypeName,
      final Boolean isBindingParameterCollection, final List<String> parameterNames) throws ODataException {
     return null;
  }

  @Override
  public Term getTerm(final FullQualifiedName termName) throws ODataException {
     return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String name) throws ODataException {
     return null;
  }

  @Override
  public Singleton getSingleton(final FullQualifiedName entityContainer, final String name) throws ODataException {
     return null;
  }

  @Override
  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String name) throws ODataException
  {
     return null;
  }

  @Override
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String name)
      throws ODataException {
     return null;
  }

  // There are no other containers
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
}
