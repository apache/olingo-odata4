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
package org.apache.olingo.ext.config.edm;

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

import java.util.ArrayList;
import java.util.List;

public class GenericEdmProvider extends CsdlAbstractEdmProvider {

  private String containerName = "default";

  private List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();

  // OData

  @Override
  public List<CsdlSchema> getSchemas() {
    return schemas;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {
    CsdlEntityContainer container = new CsdlEntityContainer();
    container.setName(containerName);

    // EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    container.setEntitySets(entitySets);

    // Load entity sets per index
    for (CsdlSchema schema : schemas) {

      if (schema.getEntityContainer() != null
          && schema.getEntityContainer().getEntitySets() != null) {
        for (CsdlEntitySet schemaEntitySet : schema.getEntityContainer()
            .getEntitySets()) {
          CsdlEntitySet entitySet = new CsdlEntitySet().setName(
              schemaEntitySet.getName()).setType(
              new FullQualifiedName(
                  schemaEntitySet.getTypeFQN().getNamespace(),
                  schemaEntitySet.getTypeFQN().getName()));
          entitySets.add(entitySet);
        }
      }
    }

    return container;
  }

  private CsdlSchema findSchema(String namespace) {
    for (CsdlSchema schema : schemas) {
      if (schema.getNamespace().equals(namespace)) {
        return schema;
      }
    }

    return null;
  }

  private CsdlEntityType findEntityType(CsdlSchema schema, String entityTypeName) {
    for (CsdlEntityType entityType : schema.getEntityTypes()) {
      if (entityType.getName().equals(entityTypeName)) {
        return entityType;
      }
    }

    return null;
  }

  @Override
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {
    CsdlSchema schema = findSchema(entityTypeName.getNamespace());
    return findEntityType(schema, entityTypeName.getName());
  }

  private CsdlEnumType findEnumType(CsdlSchema schema, String enumTypeName) {
    for (CsdlEnumType enumType : schema.getEnumTypes()) {
      if (enumType.getName().equals(enumTypeName)) {
        return enumType;
      }
    }

    return null;
  }

  @Override
  public CsdlEnumType getEnumType(FullQualifiedName enumTypeName) {
    CsdlSchema schema = findSchema(enumTypeName.getNamespace());
    return findEnumType(schema, enumTypeName.getName());
  }

  @Override
  public CsdlTypeDefinition getTypeDefinition(FullQualifiedName typeDefinitionName) throws ODataException {
    System.out.println(">> getTypeDefinition");
    // TODO Auto-generated method stub
    return super.getTypeDefinition(typeDefinitionName);
  }

  private CsdlComplexType findComplexType(CsdlSchema schema, String complexTypeName) {
    for (CsdlComplexType complexType : schema.getComplexTypes()) {
      if (complexType.getName().equals(complexTypeName)) {
        return complexType;
      }
    }

    return null;
  }

  @Override
  public CsdlComplexType getComplexType(FullQualifiedName complexTypeName) {
    CsdlSchema schema = findSchema(complexTypeName.getNamespace());
    return findComplexType(schema, complexTypeName.getName());
  }

  @Override
  public List<CsdlAction> getActions(FullQualifiedName actionName) throws ODataException {
    System.out.println(">> getActions");
    // TODO Auto-generated method stub
    return super.getActions(actionName);
  }

  @Override
  public List<CsdlFunction> getFunctions(FullQualifiedName functionName) throws ODataException {
    System.out.println(">> getFunctions");
    // TODO Auto-generated method stub
    return super.getFunctions(functionName);
  }

  @Override
  public CsdlTerm getTerm(FullQualifiedName termName) throws ODataException {
    System.out.println(">> getTerm");
    // TODO Auto-generated method stub
    return super.getTerm(termName);
  }

  private CsdlEntitySet findEntitySetInSchemas(String entitySetName) {
    List<CsdlSchema> schemas = getSchemas();
    for (CsdlSchema schema : schemas) {
      CsdlEntityContainer entityContainer = schema.getEntityContainer();
      List<CsdlEntitySet> entitySets = entityContainer.getEntitySets();
      for (CsdlEntitySet entitySet : entitySets) {
        if (entitySet.getName().equals(entitySetName)) {
          return entitySet;
        }
      }
    }
    return null;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer,
                                    String entitySetName) {
    return findEntitySetInSchemas(entitySetName);
  }

  @Override
  public CsdlSingleton getSingleton(FullQualifiedName entityContainer,
                                    String singletonName) throws ODataException {
    System.out.println(">> getSingleton");
    // TODO Auto-generated method stub
    return super.getSingleton(entityContainer, singletonName);
  }

  @Override
  public CsdlActionImport getActionImport(FullQualifiedName entityContainer,
                                          String actionImportName) throws ODataException {
    System.out.println(">> getActionImport");
    // TODO Auto-generated method stub
    return super.getActionImport(entityContainer, actionImportName);
  }

  @Override
  public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer,
                                              String functionImportName) throws ODataException {
    System.out.println(">> getFunctionImport");
    // TODO Auto-generated method stub
    return super.getFunctionImport(entityContainer, functionImportName);
  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(
      FullQualifiedName entityContainerName) {
    CsdlEntityContainer container = getEntityContainer();
    FullQualifiedName fqName = new FullQualifiedName(container.getName(),
        container.getName());
    CsdlEntityContainerInfo info = new CsdlEntityContainerInfo();
    info.setContainerName(fqName);
    return info;
  }

  @Override
  public List<CsdlAliasInfo> getAliasInfos() throws ODataException {
    System.out.println(">> getAliasInfos");
    // TODO Auto-generated method stub
    return super.getAliasInfos();
  }

  // DI

  public void setSchemas(List<CsdlSchema> schemas) {
    this.schemas = schemas;
  }

  public String getContainerName() {
    return containerName;
  }

  public void setContainerName(String containerName) {
    this.containerName = containerName;
  }

}
