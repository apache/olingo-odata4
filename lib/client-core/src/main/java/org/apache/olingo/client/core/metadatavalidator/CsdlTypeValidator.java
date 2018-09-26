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
package org.apache.olingo.client.core.metadatavalidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlStructuralType;

public class CsdlTypeValidator {

  private Map<String, String> aliasNamespaceMap = new HashMap<String, String>();
  private Map<FullQualifiedName, CsdlEntityContainer> csdlContainersMap = 
      new HashMap<FullQualifiedName, CsdlEntityContainer>();
  private Map<FullQualifiedName, CsdlEntityType> csdlEntityTypesMap = 
      new HashMap<FullQualifiedName, CsdlEntityType>();
  private Map<FullQualifiedName, CsdlComplexType> csdlComplexTypesMap = 
      new HashMap<FullQualifiedName, CsdlComplexType>();
  private Map<FullQualifiedName, CsdlAction> csdlActionsMap = 
      new HashMap<FullQualifiedName, CsdlAction>();
  private Map<FullQualifiedName, CsdlFunction> csdlFunctionsMap = 
      new HashMap<FullQualifiedName, CsdlFunction>();
  private static final String V4_SCHEMA_XMLNS = 
		  "http://docs.oasis-open.org/odata/ns/edm";
  
  public CsdlTypeValidator(){
	  
  }
  /**
   * 
   * @param aliasNamespaceMap
   * @param csdlContainersMap
   * @param csdlEntityTypesMap
   * @param csdlComplexTypesMap
   * @param csdlActionsMap
   * @param csdlFunctionsMap
   * @param csdlTermsMap
   */
  public CsdlTypeValidator(Map<String, String> aliasNamespaceMap, 
      Map<FullQualifiedName, CsdlEntityContainer> csdlContainersMap,
      Map<FullQualifiedName, CsdlEntityType> csdlEntityTypesMap,
      Map<FullQualifiedName, CsdlComplexType> csdlComplexTypesMap,
      Map<FullQualifiedName, CsdlAction> csdlActionsMap,
      Map<FullQualifiedName, CsdlFunction> csdlFunctionsMap) {
    this.aliasNamespaceMap = aliasNamespaceMap;
    this.csdlContainersMap = csdlContainersMap;
    this.csdlEntityTypesMap = csdlEntityTypesMap;
    this.csdlComplexTypesMap = csdlComplexTypesMap;
    this.csdlActionsMap = csdlActionsMap;
    this.csdlFunctionsMap = csdlFunctionsMap;
  }
  
  /**
   * Validates metadata
   */
  public void validateMetadataXML() {
    validateCsdlEntityTypes();
    validateCsdlComplexTypes();
    validateCsdlEntitySet();
    validateCsdlActionImport();
    validateCsdlFunctionImport();
  }
  
  /**
   * This method validates Csdl Entity types.
   * Looks for correct namespace aliases and correct base types
   */
  private void validateCsdlEntityTypes() {
    for (Map.Entry<FullQualifiedName, CsdlEntityType> entityTypes : csdlEntityTypesMap.entrySet()) {
      if (entityTypes.getValue() != null && entityTypes.getKey() != null) {
        CsdlEntityType entityType = entityTypes.getValue();
        if (entityType.getBaseType() != null) {
          CsdlEntityType baseEntityType;
          FullQualifiedName baseTypeFQName = entityType.getBaseTypeFQN();
          if (!csdlEntityTypesMap.containsKey(baseTypeFQName)) {
            FullQualifiedName fqName = validateCsdlEntityTypeWithAlias(baseTypeFQName);
            baseEntityType = fetchLastCsdlBaseType(fqName);
          } else {
            baseEntityType = fetchLastCsdlBaseType(baseTypeFQName);
          }
          if (baseEntityType != null && (baseEntityType.getKey() == null || 
              baseEntityType.getKey().isEmpty())) {
            throw new RuntimeException("Missing key for EntityType " + baseEntityType.getName());
          }
        } else if (entityType.getKey() == null || entityType.getKey().isEmpty()) {
          throw new RuntimeException("Missing key for EntityType " + entityType.getName());
        }
      }
    }
  }

  /**
   * This fetches the last Base Type entity from a hierarchy of base type derived types
   * @param baseTypeFQName
   * @return CsdlEntityType
   */
  private CsdlEntityType fetchLastCsdlBaseType(FullQualifiedName baseTypeFQName) {
    CsdlEntityType baseEntityType = null;
    while (baseTypeFQName != null) {
      if (!(csdlEntityTypesMap.containsKey(baseTypeFQName))) {
        baseTypeFQName = validateCsdlEntityTypeWithAlias(baseTypeFQName);
      }
      baseEntityType = csdlEntityTypesMap.get(baseTypeFQName);
      if (baseEntityType != null) {
        if (baseEntityType.getKey() != null) {
          break;
        } else if (baseEntityType.getBaseType() != null) {
          baseTypeFQName = baseEntityType.getBaseTypeFQN();
        } else if (baseEntityType.getBaseType() == null) {
          break;
        }
      }
    }
    return baseEntityType;
  }
  
  /**
   * This fetches the last Base Type entity from a hierarchy of base type derived types
   * @param baseTypeFQName
   * @return CsdlNavigationProperty
   */
  private CsdlNavigationProperty fetchLastBaseEntityHavingNavigationProperty(
      FullQualifiedName baseTypeFQName, String navBindingProperty) {
    CsdlEntityType baseEntityType = null;
    while (baseTypeFQName != null) {
      if (!(csdlEntityTypesMap.containsKey(baseTypeFQName))) {
        baseTypeFQName = validateCsdlEntityTypeWithAlias(baseTypeFQName);
      }
      baseEntityType = csdlEntityTypesMap.get(baseTypeFQName);
      if (baseEntityType != null) {
        if (baseEntityType.getNavigationProperty(navBindingProperty) != null) {
          break;
        } else if (baseEntityType.getBaseType() != null) {
          baseTypeFQName = baseEntityType.getBaseTypeFQN();
        } else if (baseEntityType.getBaseType() == null) {
          break;
        }
      }
    }
    if (baseEntityType == null) {
      throw new RuntimeException("Entity Type is null with fully qualified name:" + baseTypeFQName);
    }
    return baseEntityType.getNavigationProperty(navBindingProperty);
  }

  /**
   * This validates the namespace to alias mapping
   * @param fQName
   * @return FullQualifiedName
   */
  private FullQualifiedName validateCsdlEntityTypeWithAlias(FullQualifiedName fQName) {
    String namespace = aliasNamespaceMap.get(fQName.getNamespace());
    FullQualifiedName fqName = new FullQualifiedName(namespace, fQName.getName());
    if (!csdlEntityTypesMap.containsKey(fqName)) {
      throw new RuntimeException("Invalid Entity Type " + fQName);
    }
    return fqName;
  }
  
  /**
   * This validates the namespace to alias mapping
   * @param fqName
   * @return FullQualifiedName
   */
  private FullQualifiedName fetchCorrectNamespaceFromAlias(FullQualifiedName fqName) {
    if (aliasNamespaceMap.containsKey(fqName.getNamespace())) {
      String namespace = aliasNamespaceMap.get(fqName.getNamespace());
      fqName = new FullQualifiedName(namespace, fqName.getName());
    }
    return fqName;
  }
  
  /**
   * This method validates Csdl Complex types.
   * Looks for correct namespace aliases and correct complex base types
   */
  private void validateCsdlComplexTypes() {
    for (Map.Entry<FullQualifiedName, CsdlComplexType> complexTypes : csdlComplexTypesMap.entrySet()) {
      if (complexTypes.getValue() != null && complexTypes.getKey() != null) {
        CsdlComplexType complexType = complexTypes.getValue();
        if (complexType.getBaseType() != null) {
          FullQualifiedName baseTypeFQName = complexType.getBaseTypeFQN();
          if (!csdlComplexTypesMap.containsKey(baseTypeFQName)) {
            validateCsdlComplexTypeWithAlias(baseTypeFQName);
          }
        }
      }
    }
  }

  /**
   * This validates the namespace to alias mapping
   * @param aliasName
   * @return
   */
  private FullQualifiedName validateCsdlComplexTypeWithAlias(FullQualifiedName aliasName) {
    String namespace = aliasNamespaceMap.get(aliasName.getNamespace());
    FullQualifiedName fqName = new FullQualifiedName(namespace, aliasName.getName());
    if (!csdlComplexTypesMap.containsKey(fqName)) {
      throw new RuntimeException("Invalid Complex BaseType " + aliasName);
    }
    return fqName;
  }
  
  /**
   * This method validates Csdl entity sets.
   * It checks if entity sets are part of correct container and 
   * entity types defined for entity sets are correct.
   */
  private void validateCsdlEntitySet() {
    for (Map.Entry<FullQualifiedName, CsdlEntityContainer> container : csdlContainersMap.entrySet()) {
      for (CsdlEntitySet entitySet : container.getValue().getEntitySets()) {
        FullQualifiedName entityType = entitySet.getTypeFQN();
        if (!(csdlEntityTypesMap.containsKey(entityType))) {
          validateCsdlEntityTypeWithAlias(entityType);
        }
        validateNavigationBindingPaths(entitySet, container);
      }
    }
  }
  
  /**
   * This method checks if the target entity of the navigation binding path is defined.
   * It checks if the type of navigation property of the source entity and target entity is the same
   * @param container 
   * @param CsdlEntitySet
   */
  private void validateNavigationBindingPaths(CsdlEntitySet entitySet, 
      Entry<FullQualifiedName, CsdlEntityContainer> container) {
    List<CsdlNavigationPropertyBinding> navigationPropertyBindings = entitySet.getNavigationPropertyBindings();
    if (!navigationPropertyBindings.isEmpty()) {
      for (CsdlNavigationPropertyBinding navigationPropertyBinding : navigationPropertyBindings) {
        String navBindingPath = navigationPropertyBinding.getPath();
        String navBindingTarget = navigationPropertyBinding.getTarget();
        CsdlEntityType sourceEntityType = null;
        if (!(csdlEntityTypesMap.containsKey(new FullQualifiedName(entitySet.getType())))) {
          sourceEntityType = csdlEntityTypesMap.get(
              validateCsdlEntityTypeWithAlias(new FullQualifiedName(entitySet.getType())));
        } else {
          sourceEntityType = csdlEntityTypesMap.get(new FullQualifiedName(entitySet.getType()));
        }
        
        CsdlNavigationProperty navProperty = null;
        String targetType = null;
        if (navBindingPath.contains("/")) {
          navProperty = findLastQualifiedNameHavingNavigationProperty(navBindingPath, sourceEntityType);
        } else {
          navProperty = (CsdlNavigationProperty) sourceEntityType.
              getNavigationProperty(navBindingPath);
          if (navProperty == null) {
            navProperty = fetchLastBaseEntityHavingNavigationProperty(
                sourceEntityType.getBaseTypeFQN(), navBindingPath);
          }
        }
        if (navBindingTarget.contains("/")) {
          targetType = findLastQualifiedTargetName(navBindingTarget);
        } else {
          if (container.getValue().getEntitySet(navBindingTarget) == null) {
            if (container.getValue().getSingleton(navBindingTarget) != null) {
              throw new RuntimeException("Validations of Singletons are not supported: "+ navBindingTarget);
            } else {
              throw new RuntimeException("Navigation Property Target " + navBindingTarget + 
                  " is not part of the same container " + container.getKey());
            }
          }
          FullQualifiedName fqName = container.getValue().getEntitySet(navBindingTarget).getTypeFQN();
          if (!(csdlEntityTypesMap.containsKey(fqName))) {
            fqName = validateCsdlEntityTypeWithAlias(fqName);
          }
          targetType = fqName.getFullQualifiedNameAsString();
        }
        FullQualifiedName navFQName = fetchCorrectNamespaceFromAlias(navProperty.getTypeFQN());
        validateReferentialConstraint(sourceEntityType, 
            csdlEntityTypesMap.get(new FullQualifiedName(targetType)), navProperty);
        if (!(navFQName.getFullQualifiedNameAsString().equals(targetType))
            && !(csdlEntityTypesMap.get(navFQName).getBaseTypeFQN() != null &&
                fetchCorrectNamespaceFromAlias(csdlEntityTypesMap.get(navFQName).
                    getBaseTypeFQN()).getFullQualifiedNameAsString().equals(targetType))) {
          throw new RuntimeException("Navigation Property Type " +  
              navFQName +" does not match "
                  + "the binding target type " + targetType);
        }
      }
    }
  }
  
  /**
   * @param sourceEntityType
   * @param targetEntityType
   * @param navProperty
   */
  private void validateReferentialConstraint(CsdlEntityType sourceEntityType, CsdlEntityType targetEntityType,
      CsdlNavigationProperty navProperty) {
    if (!navProperty.getReferentialConstraints().isEmpty()) {
      String propertyName = navProperty.getReferentialConstraints().get(0).getProperty();
      if (sourceEntityType.getProperty(propertyName) == null) {
        throw new RuntimeException("Property name " + propertyName + " not part of the source entity.");
      }
      String referencedPropertyName = navProperty.getReferentialConstraints().get(0).getReferencedProperty();
      if (targetEntityType.getProperty(referencedPropertyName) == null) {
        throw new RuntimeException("Property name " + referencedPropertyName + " not part of the target entity.");
      }
    }
  }
  
  /**
   * This looks for the correct entity set 
   * when the target entity set is part of some other namespace
   * e.g <NavigationPropertyBinding Path="Products" Target="SomeModel.SomeContainer/SomeSet" />
   * @param navBindingTarget
   * @return String
   */
  private String findLastQualifiedTargetName(String navBindingTarget) {
    String[] targetPaths = navBindingTarget.split("/");
    CsdlEntityContainer csdlContainer = csdlContainersMap.containsKey(new FullQualifiedName(targetPaths[0])) ?
      csdlContainersMap.get(new FullQualifiedName(targetPaths[0])) : 
        csdlContainersMap.get(fetchCorrectNamespaceFromAlias(new FullQualifiedName(targetPaths[0])));
    if (csdlContainer == null) {
      throw new RuntimeException("Container with FullyQualifiedName " + targetPaths[0] + " not found.");
    }
    String targetEntitySetName = targetPaths[1];
    CsdlEntitySet csdlEntitySet = csdlContainer.getEntitySet(targetEntitySetName);
    if (csdlEntitySet == null) {
      throw new RuntimeException("Target Entity Set mentioned in navigationBindingProperty "
          + "not found in the container " + csdlContainer.getName());
    }
    FullQualifiedName fqName = csdlEntitySet.getTypeFQN();
    if (!(csdlEntityTypesMap.containsKey(fqName))) {
      fqName = validateCsdlEntityTypeWithAlias(fqName);
    }
    return fqName.getFullQualifiedNameAsString();
  }
  
  /**
   * This looks for the last fully qualified identifier to fetch the navigation property
   * e.g if navigation property path is Microsoft.Exchange.Services.OData.Model.ItemAttachment/Item 
   * then it fetches the entity ItemAttachment and fetches the navigation property Item
   * if navigation property path is EntityType/ComplexType/OData.Model.DerivedComplexType/Item
   * then it fetches the complex type DerivedComplexType and fetches the navigation property Item
   * @param navBindingPath
   * @return CsdlNavigationProperty
   */
  private CsdlNavigationProperty findLastQualifiedNameHavingNavigationProperty(String navBindingPath,
      CsdlEntityType sourceEntityType) {
    String[] paths = navBindingPath.split("/");
    String lastFullQualifiedName = "";
    for (String path : paths) {
      if (path.contains(".")) {
        lastFullQualifiedName = path;
      }
    }
    String strNavProperty = paths[paths.length - 1];
    String remainingPath = navBindingPath.substring(navBindingPath.indexOf(lastFullQualifiedName) 
        + lastFullQualifiedName.length() + (lastFullQualifiedName.length() == 0 ? 0 : 1), 
        navBindingPath.lastIndexOf(strNavProperty));
    if (remainingPath.length() > 0) {
      remainingPath = remainingPath.substring(0, remainingPath.length() - 1);
    }
    CsdlNavigationProperty navProperty = null;
    CsdlEntityType sourceEntityTypeHavingNavProp = lastFullQualifiedName.length() == 0 ? sourceEntityType : 
      (csdlEntityTypesMap.containsKey(new FullQualifiedName(lastFullQualifiedName)) ? 
          csdlEntityTypesMap.get(new FullQualifiedName(lastFullQualifiedName)) : 
            csdlEntityTypesMap.get(fetchCorrectNamespaceFromAlias(new FullQualifiedName(lastFullQualifiedName))));
    if (sourceEntityTypeHavingNavProp == null) {
      CsdlComplexType sourceComplexTypeHavingNavProp = 
          csdlComplexTypesMap.containsKey(new FullQualifiedName(lastFullQualifiedName)) ?
          csdlComplexTypesMap.get(new FullQualifiedName(lastFullQualifiedName)) : 
            csdlComplexTypesMap.get(fetchCorrectNamespaceFromAlias(new FullQualifiedName(lastFullQualifiedName)));
      if (sourceComplexTypeHavingNavProp == null) {
        throw new RuntimeException("The fully Qualified type " + lastFullQualifiedName + 
            " mentioned in navigation binding path not found ");
      }
      navProperty = remainingPath.length() > 0 ? fetchNavigationProperty(remainingPath, strNavProperty, 
          sourceComplexTypeHavingNavProp) : sourceComplexTypeHavingNavProp.getNavigationProperty(strNavProperty);
    } else {
      navProperty = remainingPath.length() > 0 ? fetchNavigationProperty(remainingPath, strNavProperty, 
          sourceEntityTypeHavingNavProp) : sourceEntityTypeHavingNavProp.getNavigationProperty(strNavProperty);
    }
    return navProperty;
  }
  
  /**
   * fetch the actual navigation property from the remaning path
   * @param remainingPath
   * @param strNavProperty
   * @param sourceTypeHavingNavProp
   * @return CsdlNavigationProperty
   */
  private CsdlNavigationProperty fetchNavigationProperty(String remainingPath,
      String strNavProperty, CsdlStructuralType sourceTypeHavingNavProp) {
    String[] paths = remainingPath.split("/");
    for (String path : paths) {
      FullQualifiedName fqName = null;
      if (sourceTypeHavingNavProp instanceof CsdlComplexType) {
        fqName = ((CsdlComplexType)sourceTypeHavingNavProp).getProperty(path).getTypeAsFQNObject();
      } else if (sourceTypeHavingNavProp instanceof CsdlEntityType) {
        fqName = ((CsdlEntityType)sourceTypeHavingNavProp).getProperty(path).getTypeAsFQNObject();
      }
      if (fqName != null) {
        String namespace = aliasNamespaceMap.get(fqName.getNamespace());
        fqName = namespace != null ? new FullQualifiedName(namespace, fqName.getName()) : fqName;
      }
      
      sourceTypeHavingNavProp = csdlEntityTypesMap.get(fqName) != null ? 
          csdlEntityTypesMap.get(fqName) : 
            csdlComplexTypesMap.get(fqName);
    }
    return sourceTypeHavingNavProp.getNavigationProperty(strNavProperty);
  }

  /**
   * This method validates Csdl action import.
   * It checks if action imports are part of correct container and
   * actions defined for action imports are correct
   */
  private void validateCsdlActionImport() {
    for (Map.Entry<FullQualifiedName, CsdlEntityContainer> container : csdlContainersMap.entrySet()) {
      for (CsdlActionImport actionImport : container.getValue().getActionImports()) {
        FullQualifiedName fqaction = actionImport.getActionFQN();
        if (!(csdlActionsMap.containsKey(fqaction))) {
          validateCsdlActionsWithAlias(fqaction);
        }
      }
    }
  }

  /**
   * This validates the namespace to alias mapping
   * @param aliasName
   * @return FullQualifiedName
   */
  private FullQualifiedName validateCsdlActionsWithAlias(FullQualifiedName aliasName) {
    String namespace = aliasNamespaceMap.get(aliasName.getNamespace());
    FullQualifiedName fqName = new FullQualifiedName(namespace, aliasName.getName());
    if (!csdlActionsMap.containsKey(fqName)) {
      throw new RuntimeException("Invalid Action " + aliasName);
    }
    return fqName;
  }

  /**
   * This methods validates csdl function imports.
   * It checks if function imports are part of correct container and
   * functions defined for function imports are correct
   */
  private void validateCsdlFunctionImport() {
    for (Map.Entry<FullQualifiedName, CsdlEntityContainer> container : csdlContainersMap.entrySet()) {
      for (CsdlFunctionImport functionImport : container.getValue().getFunctionImports()) {
        FullQualifiedName fqaction = functionImport.getFunctionFQN();
        if (!(csdlFunctionsMap.containsKey(fqaction))) {
          validateCsdlFunctionsWithAlias(fqaction);
        }
      }
    }
    
  }

  /**
   * This validates the namespace to alias mapping
   * @param aliasName
   * @return FullQualifiedName
   */
  private FullQualifiedName validateCsdlFunctionsWithAlias(FullQualifiedName aliasName) {
    String namespace = aliasNamespaceMap.get(aliasName.getNamespace());
    FullQualifiedName fqName = new FullQualifiedName(namespace, aliasName.getName());
    if (!csdlFunctionsMap.containsKey(fqName)) {
      throw new RuntimeException("Invalid Function " + aliasName);
    }
    return fqName;
  }
  /**
   * This checks if XmlMetadata is V4 OData version.
   * @param xmlMetadata
   * @return boolean
   * @throws Exception
   */
  public boolean isV4MetaData(XMLMetadata xmlMetadata) throws Exception {
	boolean isV4doc = true;
	List<List<String>>schemaNameSpaces = xmlMetadata.getSchemaNamespaces();
	if (schemaNameSpaces == null || schemaNameSpaces.isEmpty()) {
		throw new Exception("Cannot determine if v4 metadata," 
				+ "No schemanamespaces found in XMLMetadata");
	}
	for(List<String> nameSpaces:schemaNameSpaces){
		if(!nameSpaces.contains(V4_SCHEMA_XMLNS)){
			isV4doc = false;
		}
	}
	return isV4doc;
}
  /**
   * This checks if XMLMetadata is a service document.
   * @param xmlMetadata
   * @return boolean
   */
  public boolean isServiceDocument(XMLMetadata xmlMetadata){
	boolean isServDoc = false;
	List<CsdlSchema> schemas = xmlMetadata.getSchemas();
	for (CsdlSchema schema : schemas) {
		// for metadata to be a service document it should have an entity
		// container
		if (schema.getEntityContainer() != null) {
			isServDoc = true;
			break;
		}
	}
	return isServDoc;  
  }
}
