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

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.Action;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.AliasInfo;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
import org.apache.olingo.commons.api.edm.provider.EdmProvider;
import org.apache.olingo.commons.api.edm.provider.EntityContainer;
import org.apache.olingo.commons.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.EntitySet;
import org.apache.olingo.commons.api.edm.provider.EntityType;
import org.apache.olingo.commons.api.edm.provider.EnumType;
import org.apache.olingo.commons.api.edm.provider.Function;
import org.apache.olingo.commons.api.edm.provider.FunctionImport;
import org.apache.olingo.commons.api.edm.provider.Schema;
import org.apache.olingo.commons.api.edm.provider.Singleton;
import org.apache.olingo.commons.api.edm.provider.Term;
import org.apache.olingo.commons.api.edm.provider.TypeDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EdmTechProvider extends EdmProvider {

  public static final String nameSpace = "olingo.odata.test1";

  private final SchemaProvider schemaProvider;
  private final EntityTypeProvider entityTypeProvider;
  private final ContainerProvider containerProvider;
  private final ComplexTypeProvider complexTypeProvider;
  private final EnumTypeProvider enumTypeProvider;
  private final ActionProvider actionProvider;
  private final FunctionProvider functionProvider;
  private final TypeDefinitionProvider typeDefinitionProvider;

  public EdmTechProvider() {
    this(Collections.<EdmxReference>emptyList());
  }

  public EdmTechProvider(List<EdmxReference> references) {
    containerProvider = new ContainerProvider(this);
    entityTypeProvider = new EntityTypeProvider();
    complexTypeProvider = new ComplexTypeProvider();
    enumTypeProvider = new EnumTypeProvider();
    actionProvider = new ActionProvider();
    functionProvider = new FunctionProvider();
    typeDefinitionProvider = new TypeDefinitionProvider();
    schemaProvider = new SchemaProvider(this);
  }

  @Override
  public List<AliasInfo> getAliasInfos() throws ODataException {
    return Arrays.asList(
        new AliasInfo().setAlias("Namespace1_Alias").setNamespace(nameSpace)
        );
  }

  @Override
  public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    return enumTypeProvider.getEnumType(enumTypeName);
  }

  @Override
  public TypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
    return typeDefinitionProvider.getTypeDefinition(typeDefinitionName);
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    return entityTypeProvider.getEntityType(entityTypeName);
  }

  @Override
  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    return complexTypeProvider.getComplexType(complexTypeName);
  }

  @Override
  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    return actionProvider.getActions(actionName);
  }

  @Override
  public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {
    return functionProvider.getFunctions(functionName);
  }

  @Override
  public Term getTerm(final FullQualifiedName termName) throws ODataException {
    return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    return containerProvider.getEntitySet(entityContainer, entitySetName);
  }

  @Override
  public Singleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException {
    return containerProvider.getSingleton(entityContainer, singletonName);
  }

  @Override
  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException {
    return containerProvider.getActionImport(entityContainer, actionImportName);
  }

  @Override
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException {
    return containerProvider.getFunctionImport(entityContainer, functionImportName);
  }

  @Override
  public List<Schema> getSchemas() throws ODataException {
    return schemaProvider.getSchemas();
  }

  @Override
  public EntityContainer getEntityContainer() throws ODataException {
    return containerProvider.getEntityContainer();
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    return containerProvider.getEntityContainerInfo(entityContainerName);
  }
}
