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
package org.apache.olingo.server.tecsvc.provider;

import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.api.ex.ODataException;

public class EdmTechProvider extends CsdlAbstractEdmProvider {

  private final SchemaProvider schemaProvider;
  private final EntityTypeProvider entityTypeProvider;
  private final ContainerProvider containerProvider;
  private final ComplexTypeProvider complexTypeProvider;
  private final EnumTypeProvider enumTypeProvider;
  private final ActionProvider actionProvider;
  private final FunctionProvider functionProvider;
  private final TypeDefinitionProvider typeDefinitionProvider;
  private final TermProvider termProvider;

  public EdmTechProvider() {
    containerProvider = new ContainerProvider(this);
    entityTypeProvider = new EntityTypeProvider();
    complexTypeProvider = new ComplexTypeProvider();
    enumTypeProvider = new EnumTypeProvider();
    actionProvider = new ActionProvider();
    functionProvider = new FunctionProvider();
    typeDefinitionProvider = new TypeDefinitionProvider();
    schemaProvider = new SchemaProvider(this);
    termProvider = new TermProvider();
  }

  @Override
  public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
    return Arrays.asList(
        new CsdlAliasInfo().setAlias(SchemaProvider.NAMESPACE_ALIAS).setNamespace(SchemaProvider.NAMESPACE),
        new CsdlAliasInfo().setAlias("Core").setNamespace(TermProvider.CORE_VOCABULARY_NAMESPACE));
  }

  @Override
  public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    return enumTypeProvider.getEnumType(enumTypeName);
  }

  @Override
  public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
    return typeDefinitionProvider.getTypeDefinition(typeDefinitionName);
  }

  @Override
  public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    return entityTypeProvider.getEntityType(entityTypeName);
  }

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    return complexTypeProvider.getComplexType(complexTypeName);
  }

  @Override
  public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
    return actionProvider.getActions(actionName);
  }

  @Override
  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
    return functionProvider.getFunctions(functionName);
  }

  @Override
  public CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException {
    return termProvider.getTerm(termName);
  }

  @Override
  public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    return containerProvider.getEntitySet(entityContainer, entitySetName);
  }

  @Override
  public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException {
    return containerProvider.getSingleton(entityContainer, singletonName);
  }

  @Override
  public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException {
    return containerProvider.getActionImport(entityContainer, actionImportName);
  }

  @Override
  public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException {
    return containerProvider.getFunctionImport(entityContainer, functionImportName);
  }

  @Override
  public List<CsdlSchema> getSchemas() throws ODataException {
    return schemaProvider.getSchemas();
  }

  @Override
  public CsdlEntityContainer getEntityContainer() throws ODataException {
    return containerProvider.getEntityContainer();
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
      throws ODataException {
    return containerProvider.getEntityContainerInfo(entityContainerName);
  }
}
