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
package org.apache.olingo.client.core.edm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.AbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.Action;
import org.apache.olingo.commons.api.edm.provider.ActionImport;
import org.apache.olingo.commons.api.edm.provider.AliasInfo;
import org.apache.olingo.commons.api.edm.provider.Annotatable;
import org.apache.olingo.commons.api.edm.provider.Annotations;
import org.apache.olingo.commons.api.edm.provider.ComplexType;
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

public class ClientEdmProvider extends AbstractEdmProvider {

  private final Map<String, Schema> xmlSchemas;

  public ClientEdmProvider(Map<String, Schema> xmlSchemas) {
    this.xmlSchemas = xmlSchemas;
  }

  @Override
  public EnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    Schema schema = xmlSchemas.get(enumTypeName.getNamespace());
    if (schema != null) {
      return schema.getEnumType(enumTypeName.getName());
    }
    return null;
  }

  @Override
  public TypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
    Schema schema = xmlSchemas.get(typeDefinitionName.getNamespace());
    if (schema != null) {
      return schema.getTypeDefinition(typeDefinitionName.getName());
    }
    return null;
  }

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    Schema schema = xmlSchemas.get(entityTypeName.getNamespace());
    if (schema != null) {
      return schema.getEntityType(entityTypeName.getName());
    }
    return null;
  }

  @Override
  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    Schema schema = xmlSchemas.get(complexTypeName.getNamespace());
    if (schema != null) {
      return schema.getComplexType(complexTypeName.getName());
    }
    return null;
  }

  @Override
  public List<Action> getActions(final FullQualifiedName actionName) throws ODataException {
    Schema schema = xmlSchemas.get(actionName.getNamespace());
    if (schema != null) {
      return schema.getActions(actionName.getName());
    }
    return null;
  }

  @Override
  public List<Function> getFunctions(final FullQualifiedName functionName) throws ODataException {
    Schema schema = xmlSchemas.get(functionName.getNamespace());
    if (schema != null) {
      return schema.getFunctions(functionName.getName());
    }
    return null;
  }

  @Override
  public Term getTerm(final FullQualifiedName termName) throws ODataException {
    Schema schema = xmlSchemas.get(termName.getNamespace());
    if (schema != null) {
      return schema.getTerm(termName.getName());
    }
    return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    Schema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getEntitySet(entitySetName);
    }
    return null;
  }

  @Override
  public Singleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException {
    Schema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getSingleton(singletonName);
    }
    return null;
  }

  @Override
  public ActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException {
    Schema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getActionImport(actionImportName);
    }
    return null;
  }

  @Override
  public FunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException {
    Schema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getFunctionImport(functionImportName);
    }
    return null;
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    for (Schema schema : xmlSchemas.values()) {
      if (schema.getEntityContainer() != null) {
        return new EntityContainerInfo().setContainerName(entityContainerName).setExtendsContainer(
            schema.getEntityContainer().getExtendsContainerFQN());
      }
    }
    return null;
  }

  @Override
  public List<AliasInfo> getAliasInfos() throws ODataException {
    ArrayList<AliasInfo> aliasInfo = new ArrayList<AliasInfo>();
    for (Schema schema : xmlSchemas.values()) {
      if (schema.getAlias() != null) {
        aliasInfo.add(new AliasInfo().setNamespace(schema.getNamespace()).setAlias(schema.getAlias()));
      }
    }
    return aliasInfo;
  }

  @Override
  public List<Schema> getSchemas() throws ODataException {
    return new ArrayList<Schema>(xmlSchemas.values());
  }

  @Override
  public EntityContainer getEntityContainer() throws ODataException {
    for (Schema schema : xmlSchemas.values()) {
      if (schema.getEntityContainer() != null) {
        return schema.getEntityContainer();
      }
    }
    return null;
  }

  @Override
  public Annotations getAnnotationsGroup(FullQualifiedName targetName) throws ODataException {
    Schema schema = xmlSchemas.get(targetName.getNamespace());
    if (schema != null) {
      return schema.getAnnotationGroup(targetName.getName());
    }
    return null;
  }

  @Override
  public Annotatable getAnnoatatable(FullQualifiedName annotatedName) throws ODataException {
    final Schema schema = xmlSchemas.get(annotatedName.getNamespace());
    if (schema != null) {
      return schema.getAnnotatables().get(annotatedName.getName());
    }
    return null;
  }
}
