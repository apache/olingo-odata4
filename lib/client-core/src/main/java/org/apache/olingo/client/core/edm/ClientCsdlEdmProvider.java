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

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
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

public class ClientCsdlEdmProvider extends CsdlAbstractEdmProvider {

  private final Map<String, CsdlSchema> xmlSchemas;

  public ClientCsdlEdmProvider(Map<String, CsdlSchema> xmlSchemas) {
    this.xmlSchemas = xmlSchemas;
  }

  @Override
  public CsdlEnumType getEnumType(final FullQualifiedName enumTypeName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(enumTypeName.getNamespace());
    if (schema != null) {
      return schema.getEnumType(enumTypeName.getName());
    }
    return null;
  }

  @Override
  public CsdlTypeDefinition getTypeDefinition(final FullQualifiedName typeDefinitionName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(typeDefinitionName.getNamespace());
    if (schema != null) {
      return schema.getTypeDefinition(typeDefinitionName.getName());
    }
    return null;
  }

  @Override
  public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(entityTypeName.getNamespace());
    if (schema != null) {
      return schema.getEntityType(entityTypeName.getName());
    }
    return null;
  }

  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(complexTypeName.getNamespace());
    if (schema != null) {
      return schema.getComplexType(complexTypeName.getName());
    }
    return null;
  }

  @Override
  public List<CsdlAction> getActions(final FullQualifiedName actionName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(actionName.getNamespace());
    if (schema != null) {
      return schema.getActions(actionName.getName());
    }
    return null;
  }

  @Override
  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(functionName.getNamespace());
    if (schema != null) {
      return schema.getFunctions(functionName.getName());
    }
    return null;
  }

  @Override
  public CsdlTerm getTerm(final FullQualifiedName termName) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(termName.getNamespace());
    if (schema != null) {
      return schema.getTerm(termName.getName());
    }
    return null;
  }

  @Override
  public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    CsdlSchema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getEntitySet(entitySetName);
    }
    return null;
  }

  @Override
  public CsdlSingleton getSingleton(final FullQualifiedName entityContainer, final String singletonName)
      throws ODataException {
    CsdlSchema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getSingleton(singletonName);
    }
    return null;
  }

  @Override
  public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName)
      throws ODataException {
    CsdlSchema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getActionImport(actionImportName);
    }
    return null;
  }

  @Override
  public CsdlFunctionImport getFunctionImport(final FullQualifiedName entityContainer, final String functionImportName)
      throws ODataException {
    CsdlSchema schema = xmlSchemas.get(entityContainer.getNamespace());
    if (schema != null) {
      return schema.getEntityContainer().getFunctionImport(functionImportName);
    }
    return null;
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName)
      throws ODataException {
    for (CsdlSchema schema : xmlSchemas.values()) {
      CsdlEntityContainer entityContainer = schema.getEntityContainer();
      if (entityContainer != null) {
        FullQualifiedName containerFQN;
        if (entityContainerName == null) {
          containerFQN = new FullQualifiedName(schema.getNamespace(), entityContainer.getName());
        } else {
          containerFQN = entityContainerName;
        }
        return new CsdlEntityContainerInfo().setContainerName(containerFQN).setExtendsContainer(
            entityContainer.getExtendsContainerFQN());
      }
    }
    return null;
  }

  @Override
  public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
    ArrayList<CsdlAliasInfo> aliasInfo = new ArrayList<CsdlAliasInfo>();
    for (CsdlSchema schema : xmlSchemas.values()) {
      if (schema.getAlias() != null) {
        aliasInfo.add(new CsdlAliasInfo().setNamespace(schema.getNamespace()).setAlias(schema.getAlias()));
      }
    }
    return aliasInfo;
  }

  @Override
  public List<CsdlSchema> getSchemas() throws ODataException {
    return new ArrayList<CsdlSchema>(xmlSchemas.values());
  }

  @Override
  public CsdlEntityContainer getEntityContainer() throws ODataException {
    for (CsdlSchema schema : xmlSchemas.values()) {
      if (schema.getEntityContainer() != null) {
        return schema.getEntityContainer();
      }
    }
    return null;
  }

  @Override
  public CsdlAnnotations getAnnotationsGroup(FullQualifiedName targetName, String qualifier) throws ODataException {
    CsdlSchema schema = xmlSchemas.get(targetName.getNamespace());
    if (schema != null) {
      return schema.getAnnotationGroup(targetName.getName(), qualifier);
    }
    return null;
  }
}
