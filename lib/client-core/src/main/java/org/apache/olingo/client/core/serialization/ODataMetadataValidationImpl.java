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
package org.apache.olingo.client.core.serialization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.serialization.ODataMetadataValidation;
import org.apache.olingo.client.core.metadatavalidator.CsdlTypeValidator;
import org.apache.olingo.client.core.metadatavalidator.EdmTypeValidator;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

public class ODataMetadataValidationImpl implements ODataMetadataValidation {

  @Override
  public void validateMetadata(Edm edm) {
    Map<FullQualifiedName, EdmEntityType> edmEntityTypesMap = new HashMap<>();
    Map<FullQualifiedName, EdmComplexType> edmComplexTypesMap = new HashMap<>();
    Map<FullQualifiedName, EdmFunction> edmFunctionsMap = new HashMap<>();
    Map<FullQualifiedName, EdmEntityContainer> edmContainersMap = new HashMap<>();
    Map<String, String> aliasNamespaceMap = new HashMap<>();
    List<EdmSchema> edmSchemas = edm.getSchemas();
    for (EdmSchema edmSchema : edmSchemas) {
      List<EdmEntityType> edmEntityTypes = edmSchema.getEntityTypes();
      for (EdmEntityType edmEntityType : edmEntityTypes) {
        edmEntityTypesMap.put(edmEntityType.getFullQualifiedName(), edmEntityType);
      }
      List<EdmComplexType> edmComplexTypes = edmSchema.getComplexTypes();
      for (EdmComplexType edmComplexType : edmComplexTypes) {
        edmComplexTypesMap.put(edmComplexType.getFullQualifiedName(), edmComplexType);
      }
      List<EdmFunction> edmFunctions = edmSchema.getFunctions();
      for (EdmFunction edmFunction : edmFunctions) {
        edmFunctionsMap.put(edmFunction.getFullQualifiedName(), edmFunction);
      }
      aliasNamespaceMap.put(edmSchema.getAlias(), edmSchema.getNamespace());
      if (edmSchema.getEntityContainer() != null) {
        edmContainersMap.put(edmSchema.getEntityContainer().getFullQualifiedName(), edmSchema.getEntityContainer());
      }
    }
    EdmTypeValidator edmTypeValidator = new EdmTypeValidator(aliasNamespaceMap, edmContainersMap, 
        edmEntityTypesMap, edmComplexTypesMap, edmFunctionsMap);
    edmTypeValidator.validateEdm();
  }

  @Override
  public void validateMetadata(XMLMetadata xmlMetadata) {
    Map<FullQualifiedName, CsdlEntityType> csdlEntityTypesMap = new HashMap<>();
    Map<FullQualifiedName, CsdlComplexType> csdlComplexTypesMap = new HashMap<>();
    Map<FullQualifiedName, CsdlAction> csdlActionsMap = new HashMap<>();
    Map<FullQualifiedName, CsdlFunction> csdlFunctionsMap = new HashMap<>();
    Map<FullQualifiedName, CsdlEntityContainer> csdlContainersMap = 
        new HashMap<>();
    Map<String, String> aliasNamespaceMap = new HashMap<>();
    List<CsdlSchema> csdlSchemas = xmlMetadata.getSchemas();
    for (CsdlSchema csdlSchema : csdlSchemas) {
      List<CsdlEntityType> csdlEntityTypes = csdlSchema.getEntityTypes();
      for (CsdlEntityType csdlEntityType : csdlEntityTypes) {
        csdlEntityTypesMap.put(new FullQualifiedName(
            csdlSchema.getNamespace(), csdlEntityType.getName()), csdlEntityType);
      }
      List<CsdlComplexType> csdlComplexTypes = csdlSchema.getComplexTypes();
      for (CsdlComplexType csdlComplexType : csdlComplexTypes) {
        csdlComplexTypesMap.put(new FullQualifiedName(
            csdlSchema.getNamespace(), csdlComplexType.getName()), csdlComplexType);
      }
      List<CsdlAction> csdlActions = csdlSchema.getActions();
      for (CsdlAction csdlAction : csdlActions) {
        csdlActionsMap.put(new FullQualifiedName(
            csdlSchema.getNamespace(), csdlAction.getName()), csdlAction);
      }
      List<CsdlFunction> csdlFunctions = csdlSchema.getFunctions();
      for (CsdlFunction csdlFunction : csdlFunctions) {
        csdlFunctionsMap.put(
            new FullQualifiedName(csdlSchema.getNamespace(), csdlFunction.getName()), csdlFunction);
      }
      aliasNamespaceMap.put(csdlSchema.getAlias(), csdlSchema.getNamespace());
      if (csdlSchema.getEntityContainer() != null) {
        csdlContainersMap.put(new FullQualifiedName(
            csdlSchema.getNamespace(), csdlSchema.getEntityContainer().getName()), csdlSchema.getEntityContainer());
      }
    }
    CsdlTypeValidator csdlTypeValidator = new CsdlTypeValidator(aliasNamespaceMap, csdlContainersMap, 
        csdlEntityTypesMap, csdlComplexTypesMap, csdlActionsMap, csdlFunctionsMap);
    csdlTypeValidator.validateMetadataXML();
  }
  
 @Override
  public boolean isV4Metadata(XMLMetadata xmlMetadata) throws Exception{
	return  new CsdlTypeValidator().isV4MetaData(xmlMetadata);
  }
 
  @Override
  public boolean isServiceDocument(XMLMetadata xmlMetadata){
	  return  new CsdlTypeValidator().isServiceDocument(xmlMetadata);
  }
}
