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
package org.apache.olingo.commons.core.edm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmAnnotations;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmSchema;
import org.apache.olingo.commons.api.edm.EdmTerm;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlOperation;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.edm.provider.CsdlStructuralType;
import org.apache.olingo.commons.api.edm.provider.CsdlTerm;
import org.apache.olingo.commons.api.edm.provider.CsdlTypeDefinition;
import org.apache.olingo.commons.api.ex.ODataException;

public class EdmProviderImpl extends AbstractEdm {

  private final CsdlEdmProvider provider;
  private final Map<FullQualifiedName, List<CsdlAction>> actionsMap =
      Collections.synchronizedMap(new HashMap<FullQualifiedName, List<CsdlAction>>());
  private final Map<FullQualifiedName, List<CsdlFunction>> functionsMap =
      Collections.synchronizedMap(new HashMap<FullQualifiedName, List<CsdlFunction>>());
  private List<CsdlSchema> termSchemaDefinition = new ArrayList<CsdlSchema>();

  private final String SLASH = "/";
  private final String DOT = ".";
  
  public EdmProviderImpl(final CsdlEdmProvider provider) {
    this.provider = provider;
  }
  
  public EdmProviderImpl(final CsdlEdmProvider provider, final List<CsdlSchema> termSchemaDefinition) {
    this.provider = provider;
    this.termSchemaDefinition = termSchemaDefinition;
    populateAnnotationMap();
  }

  @Override
  public EdmEntityContainer createEntityContainer(final FullQualifiedName containerName) {
    try {
      CsdlEntityContainerInfo entityContainerInfo = provider.getEntityContainerInfo(containerName);
      if (entityContainerInfo != null) {
        CsdlEntityContainer entityContainer = provider.getEntityContainer();
        addEntityContainerAnnotations(entityContainer, entityContainerInfo.getContainerName());
        return new EdmEntityContainerImpl(this, provider, entityContainerInfo.getContainerName(), 
            entityContainer);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addEntityContainerAnnotations(CsdlEntityContainer csdlEntityContainer, FullQualifiedName containerName) {
    String aliasName = getAliasInfo(containerName.getNamespace());
    List<CsdlAnnotation> annotations = getAnnotationsMap().get(containerName.getFullQualifiedNameAsString());
    List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + containerName.getName());
    addAnnotationsOnEntityContainer(csdlEntityContainer, annotations);
    addAnnotationsOnEntityContainer(csdlEntityContainer, annotationsOnAlias);
  }
  
  /**
   * @param csdlEntityContainer
   * @param annotations
   */
  private void addAnnotationsOnEntityContainer(CsdlEntityContainer csdlEntityContainer,
      List<CsdlAnnotation> annotations) {
    if (null != annotations) {
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(csdlEntityContainer.getAnnotations(), annotation)) {
          csdlEntityContainer.getAnnotations().add(annotation);
        } 
      }
    }
  }
  
  @Override
  public EdmEnumType createEnumType(final FullQualifiedName enumName) {
    try {
      CsdlEnumType enumType = provider.getEnumType(enumName);
      if (enumType != null) {
        addEnumTypeAnnotations(enumType, enumName);
        return new EdmEnumTypeImpl(this, enumName, enumType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addEnumTypeAnnotations(CsdlEnumType enumType, FullQualifiedName enumName) {
    String aliasName = getAliasInfo(enumName.getNamespace());
    List<CsdlAnnotation> annotations = getAnnotationsMap().get(enumName.getFullQualifiedNameAsString());
    List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + enumName.getName());
    addAnnotationsOnEnumTypes(enumType, annotations);
    addAnnotationsOnEnumTypes(enumType, annotationsOnAlias);
  }
  
  /**
   * @param enumType
   * @param annotations
   */
  private void addAnnotationsOnEnumTypes(CsdlEnumType enumType, List<CsdlAnnotation> annotations) {
    if (null != annotations) {
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(enumType.getAnnotations(), annotation)) {
          enumType.getAnnotations().add(annotation);
        } 
      }
    }
  }
  
  @Override
  public EdmTypeDefinition createTypeDefinition(final FullQualifiedName typeDefinitionName) {
    try {
      CsdlTypeDefinition typeDefinition = provider.getTypeDefinition(typeDefinitionName);
      if (typeDefinition != null) {
        addTypeDefnAnnotations(typeDefinition, typeDefinitionName);
        return new EdmTypeDefinitionImpl(this, typeDefinitionName, typeDefinition);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }
  
  public void addTypeDefnAnnotations(CsdlTypeDefinition typeDefinition, FullQualifiedName typeDefinitionName) {
    String aliasName = getAliasInfo(typeDefinitionName.getNamespace());
    List<CsdlAnnotation> annotations = getAnnotationsMap().get(typeDefinitionName.getFullQualifiedNameAsString());
    List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + typeDefinitionName.getName());
    addAnnotationsOnTypeDefinitions(typeDefinition, annotations);
    addAnnotationsOnTypeDefinitions(typeDefinition, annotationsOnAlias);
  }
  
  /**
   * @param typeDefinition
   * @param annotations
   */
  private void addAnnotationsOnTypeDefinitions(CsdlTypeDefinition typeDefinition, List<CsdlAnnotation> annotations) {
    if (null != annotations) {
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(typeDefinition.getAnnotations(), annotation)) {
          typeDefinition.getAnnotations().add(annotation);
        } 
      }
    }
  }

  @Override
  public EdmEntityType createEntityType(final FullQualifiedName entityTypeName) {
    try {
      CsdlEntityType entityType = provider.getEntityType(entityTypeName);
      if (entityType != null) {
        List<CsdlAnnotation> annotations = getAnnotationsMap().get(entityTypeName.getFullQualifiedNameAsString());
        String aliasName = getAliasInfo(entityTypeName.getNamespace());
        List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + entityTypeName.getName());
        addAnnotationsOnStructuralType(entityType, annotations);
        addAnnotationsOnStructuralType(entityType, annotationsOnAlias);
        
		  if (!isEntityDerivedFromES()) {
          addStructuralTypeAnnotations(entityType, entityTypeName, this.provider.getEntityContainer());
        }
        return new EdmEntityTypeImpl(this, entityTypeName, entityType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  /**
   * Add annoations to entity types and complex types
   * @param entityType
   * @param annotations
   */
  private void addAnnotationsOnStructuralType(CsdlStructuralType structuralType, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(structuralType.getAnnotations(), annotation)) {
          structuralType.getAnnotations().add(annotation);
        }
      }
    }
  }

  /**
   * Populates a map of String (annotation target) and List of CsdlAnnotations
   * Reads both term definition schema (external schema) and 
   * provider schema (actual metadata file)
   */
  private void populateAnnotationMap() {
    for (CsdlSchema schema : termSchemaDefinition) {
      fetchAnnotationsInMetadataAndExternalFile(schema);
    }
    try {
      if (null != provider.getSchemas()) {
        for (CsdlSchema schema : provider.getSchemas()) {
          fetchAnnotationsInMetadataAndExternalFile(schema);
        }
      }
    } catch (ODataException e) {
        throw new EdmException(e);
      }
  }

  /**
   * @param schema
   */
  private void fetchAnnotationsInMetadataAndExternalFile(CsdlSchema schema) {
    List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
    for (CsdlAnnotations annotationGrp : annotationGrps) {
      if (!getAnnotationsMap().containsKey(annotationGrp.getTarget())) {
        getAnnotationsMap().put(annotationGrp.getTarget(), annotationGrp.getAnnotations());
      } else {
        List<CsdlAnnotation> annotations = getAnnotationsMap().get(annotationGrp.getTarget());
        List<CsdlAnnotation> newAnnotations = new ArrayList<CsdlAnnotation>();
        for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
          if (!compareAnnotations(annotations, annotation)) {
            newAnnotations.add(annotation);
          }
        }
        if (!newAnnotations.isEmpty()) {
          getAnnotationsMap().get(annotationGrp.getTarget()).addAll(newAnnotations);
        }
      }
    }
  }
  
  /**
   * Add the annotations defined in an external file to the property/
   * navigation property and the entity
   * @param structuralType
   * @param typeName
   * @param csdlEntityContainer 
   */
  public void addStructuralTypeAnnotations(CsdlStructuralType structuralType, FullQualifiedName typeName, 
      CsdlEntityContainer csdlEntityContainer) {
    updateAnnotationsOnStructuralProperties(structuralType, typeName, csdlEntityContainer);
    updateAnnotationsOnStructuralNavProperties(structuralType, typeName, csdlEntityContainer);
  }
  
  /**
   * Get alias name given the namespace from the alias info 
   * @param namespace
   * @return
   */
  private String getAliasInfo(String namespace) {
    try {
      if (null != provider.getAliasInfos()) {
        for (CsdlAliasInfo aliasInfo : provider.getAliasInfos()) {
          if (null != aliasInfo.getNamespace() && 
              aliasInfo.getNamespace().equalsIgnoreCase(namespace)) {
            return aliasInfo.getAlias();
          }
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }
    return null;
  }
  /** Check if annotations are added on navigation properties of a structural type
   * @param structuralType
   * @param typeName
   * @param csdlEntityContainer 
   * @param isNavPropAnnotationsCleared
   * @param annotationGrp
   */
  private void updateAnnotationsOnStructuralNavProperties(CsdlStructuralType structuralType, 
      FullQualifiedName typeName, CsdlEntityContainer csdlEntityContainer) {
    List<CsdlNavigationProperty> navProperties = structuralType.getNavigationProperties();
    String containerName = null;
    String schemaName = null;
    String entitySetName = null;
    List<CsdlEntitySet> entitySets = csdlEntityContainer != null ? 
        csdlEntityContainer.getEntitySets() : new ArrayList<CsdlEntitySet>();
    if (structuralType instanceof CsdlComplexType) {
      removeAnnotationsAddedToCTNavPropFromES(structuralType, typeName, csdlEntityContainer, navProperties, entitySets);
    } else {
      for (CsdlEntitySet entitySet : entitySets) {
        entitySetName = entitySet.getName();
        String entityTypeName = entitySet.getTypeFQN().getFullQualifiedNameAsString();
        if (null != entityTypeName && entityTypeName.equalsIgnoreCase(typeName.getFullQualifiedNameAsString())) {
          containerName = csdlEntityContainer.getName();
          schemaName = typeName.getNamespace();
          break;
        }
      }
      for (CsdlNavigationProperty navProperty : navProperties) {
        List<CsdlAnnotation> annotPropDerivedFromES = getAnnotationsMap().get(schemaName + DOT + 
            containerName + SLASH +  entitySetName + SLASH + navProperty.getName());
        removeAnnotationsOnNavPropDerivedFromEntitySet(structuralType, navProperty, annotPropDerivedFromES);
        String aliasName = getAliasInfo(schemaName);
        List<CsdlAnnotation> annotPropDerivedFromESOnAlias = getAnnotationsMap().get(aliasName + DOT + 
            containerName + SLASH +  entitySetName + SLASH + navProperty.getName());
        removeAnnotationsOnNavPropDerivedFromEntitySet(structuralType, navProperty, annotPropDerivedFromESOnAlias);
        
        List<CsdlAnnotation> navPropAnnotations = getAnnotationsMap().get(
            typeName + SLASH + navProperty.getName());
        addAnnotationsOnNavProperties(structuralType, navProperty, navPropAnnotations);
        aliasName = getAliasInfo(typeName.getNamespace());
        List<CsdlAnnotation> navPropAnnotationsOnAlias = getAnnotationsMap().get(
            aliasName + DOT + typeName.getName() + SLASH + navProperty.getName());
        addAnnotationsOnNavProperties(structuralType, navProperty, navPropAnnotationsOnAlias);
      }
    }
  }

  /**
   * Adds annotations to navigation properties of entity and complex types
   * @param structuralType
   * @param navProperty
   * @param navPropAnnotations
   */
  private void addAnnotationsOnNavProperties(CsdlStructuralType structuralType, CsdlNavigationProperty navProperty,
      List<CsdlAnnotation> navPropAnnotations) {
    if (null != navPropAnnotations && !navPropAnnotations.isEmpty()) {
      for (CsdlAnnotation annotation : navPropAnnotations) {
        if (!compareAnnotations(structuralType.getNavigationProperty(
            navProperty.getName()).getAnnotations(), annotation)) {
          structuralType.getNavigationProperty(navProperty.getName()).getAnnotations().
          add(annotation);
        }
      }
    }
  }

  /**
   * Removes the annotations added to properties of structural types
   * if annotation was added before via EntitySet path
   * @param structuralType
   * @param navProperty
   * @param annotPropDerivedFromES
   */
  private void removeAnnotationsOnNavPropDerivedFromEntitySet(CsdlStructuralType structuralType,
      CsdlNavigationProperty navProperty, List<CsdlAnnotation> annotPropDerivedFromES) {
    if (null != annotPropDerivedFromES && !annotPropDerivedFromES.isEmpty()) {
      for (CsdlAnnotation annotation : annotPropDerivedFromES) {
        List<CsdlAnnotation> propAnnot = structuralType.getNavigationProperty(
            navProperty.getName()).getAnnotations();
        if (propAnnot.contains(annotation)) {
          propAnnot.remove(annotation);
        }
      }
    }
  }

  /**
   * Remove the annotations added to navigation properties 
   * of a complex type loaded via entity set path
   * @param structuralType
   * @param typeName
   * @param csdlEntityContainer
   * @param navProperties
   * @param entitySets
   */
  private void removeAnnotationsAddedToCTNavPropFromES(CsdlStructuralType structuralType, FullQualifiedName typeName,
      CsdlEntityContainer csdlEntityContainer, List<CsdlNavigationProperty> navProperties,
      List<CsdlEntitySet> entitySets) {
    String containerName;
    String schemaName;
    String complexPropName;
    for (CsdlEntitySet entitySet : entitySets) {
      try {
        CsdlEntityType entType = provider.getEntityType(entitySet.getTypeFQN());
        List<CsdlProperty> entTypeProperties = null != entType ? 
            entType.getProperties() : new ArrayList<>();
        for (CsdlProperty entTypeProperty : entTypeProperties) {
          if (null != entTypeProperty.getType() && 
              entTypeProperty.getType().equalsIgnoreCase(typeName.getFullQualifiedNameAsString())) {
            complexPropName = entTypeProperty.getName();
            containerName = csdlEntityContainer.getName();
            schemaName = typeName.getNamespace();
            for (CsdlNavigationProperty navProperty : navProperties) { 
              List<CsdlAnnotation> annotPropDerivedFromES = getAnnotationsMap().get(schemaName + DOT + 
                  containerName + SLASH +  entitySet.getName() + SLASH + 
                  complexPropName + SLASH + navProperty.getName());
              removeAnnotationsOnNavPropDerivedFromEntitySet(structuralType, navProperty, annotPropDerivedFromES);
              String aliasName = getAliasInfo(schemaName);
              List<CsdlAnnotation> annotPropDerivedFromESOnAlias = getAnnotationsMap().get(aliasName + DOT + 
                  containerName + SLASH +  entitySet.getName() + SLASH + 
                  complexPropName + SLASH + navProperty.getName());
              removeAnnotationsOnNavPropDerivedFromEntitySet(structuralType, 
                  navProperty, annotPropDerivedFromESOnAlias);
              
              List<CsdlAnnotation> propAnnotations = getAnnotationsMap().
                  get(typeName.getFullQualifiedNameAsString() + SLASH + navProperty.getName());
              addAnnotationsOnNavProperties(structuralType, navProperty, propAnnotations);
              aliasName = getAliasInfo(typeName.getNamespace());
              List<CsdlAnnotation> propAnnotationsOnAlias = getAnnotationsMap().
                  get(aliasName + DOT + typeName.getName() + SLASH + navProperty.getName());
              addAnnotationsOnNavProperties(structuralType, navProperty, propAnnotationsOnAlias);
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
    for (CsdlNavigationProperty navProperty : structuralType.getNavigationProperties()) {
      List<CsdlAnnotation> propAnnotations = getAnnotationsMap().
          get(typeName.getFullQualifiedNameAsString() + SLASH + navProperty.getName());
      addAnnotationsOnNavProperties(structuralType, navProperty, propAnnotations);
      String aliasName = getAliasInfo(typeName.getNamespace());
      List<CsdlAnnotation> propAnnotationsOnAlias = getAnnotationsMap().
          get(aliasName + DOT + typeName.getName() + SLASH + navProperty.getName());
      addAnnotationsOnNavProperties(structuralType, navProperty, propAnnotationsOnAlias);
    }
  }

  /** Check if annotations are added on properties of a structural type
   * @param structuralType
   * @param typeName
   * @param csdlEntityContainer
   */
  private void updateAnnotationsOnStructuralProperties(CsdlStructuralType structuralType, FullQualifiedName typeName,
      CsdlEntityContainer csdlEntityContainer) {
    List<CsdlProperty> properties = structuralType.getProperties();
    String containerName = null;
    String schemaName = null;
    String entitySetName = null;
    List<CsdlEntitySet> entitySets = null != csdlEntityContainer ? 
        csdlEntityContainer.getEntitySets() : new ArrayList<>();
    if (structuralType instanceof CsdlComplexType) {
      removeAnnotationsAddedToCTTypePropFromES(structuralType, typeName, csdlEntityContainer, properties, entitySets);
    } else {
      for (CsdlEntitySet entitySet : entitySets) {
        entitySetName = entitySet.getName();
        String entityTypeName = entitySet.getTypeFQN().getFullQualifiedNameAsString();
        if (null != entityTypeName && entityTypeName.equalsIgnoreCase(typeName.getFullQualifiedNameAsString())) {
          containerName = csdlEntityContainer.getName();
          schemaName = typeName.getNamespace();
          break;
        }
      }
      for (CsdlProperty property : properties) {
        List<CsdlAnnotation> annotPropDerivedFromES = getAnnotationsMap().get(schemaName + DOT + 
            containerName + SLASH +  entitySetName + SLASH + property.getName());
        removeAnnotationsOnPropDerivedFromEntitySet(structuralType, property, annotPropDerivedFromES);
        String aliasName = getAliasInfo(schemaName);
        List<CsdlAnnotation> annotPropDerivedFromESOnAlias = getAnnotationsMap().get(aliasName + DOT + 
            containerName + SLASH +  entitySetName + SLASH + property.getName());
        removeAnnotationsOnPropDerivedFromEntitySet(structuralType, property, annotPropDerivedFromESOnAlias);
        List<CsdlAnnotation> propAnnotations = getAnnotationsMap().
            get(typeName.getFullQualifiedNameAsString() + SLASH + property.getName());
        addAnnotationsOnPropertiesOfStructuralType(structuralType, property, propAnnotations);
        aliasName = getAliasInfo(typeName.getNamespace());
        List<CsdlAnnotation> propAnnotationsOnAlias = getAnnotationsMap().
            get(aliasName + DOT + typeName.getName() + SLASH + property.getName());
        addAnnotationsOnPropertiesOfStructuralType(structuralType, property, propAnnotationsOnAlias);
      }
    }
  }

  /**
   * Adds annotations to properties of entity type and complex type
   * @param structuralType
   * @param property
   * @param propAnnotations
   */
  private void addAnnotationsOnPropertiesOfStructuralType(CsdlStructuralType structuralType, CsdlProperty property,
      List<CsdlAnnotation> propAnnotations) {
    if (null != propAnnotations && !propAnnotations.isEmpty()) {
      for (CsdlAnnotation annotation : propAnnotations) {
        if (!compareAnnotations(structuralType.getProperty(
            property.getName()).getAnnotations(), annotation)) {
          structuralType.getProperty(property.getName()).getAnnotations().add(annotation); 
        }
      }
    }
  }
  
  /**
   * Removes the annotations added to properties of entity type when added via entity set
   * @param structuralType
   * @param property
   * @param annotPropDerivedFromESOnAlias
   */
  private void removeAnnotationsOnPropDerivedFromEntitySet(CsdlStructuralType structuralType, CsdlProperty property,
      List<CsdlAnnotation> annotPropDerivedFromES) {
    if (null != annotPropDerivedFromES && !annotPropDerivedFromES.isEmpty()) {
      for (CsdlAnnotation annotation : annotPropDerivedFromES) {
        List<CsdlAnnotation> propAnnot = structuralType.getProperty(
            property.getName()).getAnnotations();
        if (propAnnot.contains(annotation)) {
          propAnnot.remove(annotation);
        }
      }
    }
  }

  /**
   * Removes the annotation added on complex type property via Entity Set
   * @param structuralType
   * @param typeName
   * @param csdlEntityContainer
   * @param properties
   * @param entitySets
   */
  private void removeAnnotationsAddedToCTTypePropFromES(CsdlStructuralType structuralType, FullQualifiedName typeName,
      CsdlEntityContainer csdlEntityContainer, List<CsdlProperty> properties, List<CsdlEntitySet> entitySets) {
    String containerName;
    String schemaName;
    String complexPropName;
    for (CsdlEntitySet entitySet : entitySets) {
      try {
        CsdlEntityType entType = provider.getEntityType(entitySet.getTypeFQN());
        List<CsdlProperty> entTypeProperties = null != entType ? 
            entType.getProperties() : new ArrayList<>();
        for (CsdlProperty entTypeProperty : entTypeProperties) {
          if (null != entTypeProperty.getType() && 
              entTypeProperty.getType().endsWith(DOT + structuralType.getName())) {
            complexPropName = entTypeProperty.getName();
            containerName = csdlEntityContainer.getName();
            schemaName = typeName.getNamespace();
            for (CsdlProperty property : properties) { 
              List<CsdlAnnotation> annotPropDerivedFromES = getAnnotationsMap().get(schemaName + DOT + 
                  containerName + SLASH +  entitySet.getName() + SLASH + complexPropName + SLASH + property.getName());
              removeAnnotationsOnPropDerivedFromEntitySet(structuralType, property, annotPropDerivedFromES);
              String aliasName = getAliasInfo(schemaName);
              List<CsdlAnnotation> annotPropDerivedFromESOnAlias = getAnnotationsMap().get(aliasName + DOT + 
                  containerName + SLASH +  entitySet.getName() + SLASH + complexPropName + SLASH + property.getName());
              removeAnnotationsOnPropDerivedFromEntitySet(structuralType, property, annotPropDerivedFromESOnAlias);
              
              List<CsdlAnnotation> propAnnotations = getAnnotationsMap().
                  get(typeName.getFullQualifiedNameAsString() + SLASH + property.getName());
              addAnnotationsOnPropertiesOfStructuralType(structuralType, property, propAnnotations);
              aliasName = getAliasInfo(typeName.getNamespace());
              List<CsdlAnnotation> propAnnotationsOnAlias = getAnnotationsMap().
                  get(typeName.getName() + SLASH + property.getName());
              addAnnotationsOnPropertiesOfStructuralType(structuralType, property, propAnnotationsOnAlias);
            }
          }
        }
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
  }
  
  @Override
  public EdmComplexType createComplexType(final FullQualifiedName complexTypeName) {
    try {
      final CsdlComplexType complexType = provider.getComplexType(complexTypeName);
      if (complexType != null) {
        List<CsdlAnnotation> annotations = getAnnotationsMap().get(complexTypeName.getFullQualifiedNameAsString());
        if (null != annotations && !annotations.isEmpty()) {
          addAnnotationsOnStructuralType(complexType, annotations);
        }
        String aliasName = getAliasInfo(complexTypeName.getNamespace());
        List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + complexTypeName.getName());
        if (null != annotationsOnAlias && !annotationsOnAlias.isEmpty()) {
          addAnnotationsOnStructuralType(complexType, annotationsOnAlias);
        }
        
		  if (!isComplexDerivedFromES()) {
          addStructuralTypeAnnotations(complexType, complexTypeName, provider.getEntityContainer());
        }
        return new EdmComplexTypeImpl(this, complexTypeName, complexType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  public EdmAction createBoundAction(final FullQualifiedName actionName,
      final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection) {

    try {
      List<CsdlAction> actions = actionsMap.get(actionName);
      if (actions == null) {
        actions = provider.getActions(actionName);
        if (actions == null) {
          return null;
        } else {
          actionsMap.put(actionName, actions);
        }
      }
      // Search for bound action where binding parameter matches
      for (CsdlAction action : actions) {
        if (action.isBound()) {
          final List<CsdlParameter> parameters = action.getParameters();
          final CsdlParameter parameter = parameters.get(0);
          if ((bindingParameterTypeName.equals(parameter.getTypeFQN()) || 
              isEntityPreviousTypeCompatibleToBindingParam(bindingParameterTypeName, parameter) ||
              isComplexPreviousTypeCompatibleToBindingParam(bindingParameterTypeName, parameter, 
                  isBindingParameterCollection))
              && isBindingParameterCollection.booleanValue() == parameter.isCollection()) {
            addOperationsAnnotations(action, actionName);
            return new EdmActionImpl(this, actionName, action);
          }

        }
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addOperationsAnnotations(CsdlOperation operation, FullQualifiedName actionName) {
    String aliasName = getAliasInfo(actionName.getNamespace());
    List<CsdlAnnotation> annotations = getAnnotationsMap().get(actionName.getFullQualifiedNameAsString());
    List<CsdlAnnotation> annotationsOnAlias = getAnnotationsMap().get(aliasName + DOT + actionName.getName());
    if (null != annotations) {
      addAnnotationsToOperations(operation, annotations);
    }
    if (null != annotationsOnAlias) {
      addAnnotationsToOperations(operation, annotationsOnAlias);
    }
    addAnnotationsToParamsOfOperations(operation, actionName);
  }
  
  /** Adds annotations to action parameters
   * @param operation
   * @param actionName
   * @param annotations
   */
  private void addAnnotationsToParamsOfOperations(CsdlOperation operation, FullQualifiedName actionName) {
    final List<CsdlParameter> parameters = operation.getParameters();
    for (CsdlParameter parameter : parameters) {
      List<CsdlAnnotation> annotsToParams = getAnnotationsMap().get(
          actionName.getFullQualifiedNameAsString() + SLASH + parameter.getName());
      if (null != annotsToParams && !annotsToParams.isEmpty()) {
        for (CsdlAnnotation annotation : annotsToParams) {
          if (!compareAnnotations(operation.getParameter(parameter.getName()).getAnnotations(), annotation)) {
            operation.getParameter(parameter.getName()).getAnnotations().add(annotation);
          }
        }
      }
      String aliasName = getAliasInfo(actionName.getNamespace());
      List<CsdlAnnotation> annotsToParamsOnAlias = getAnnotationsMap().get(
          aliasName + DOT + actionName.getName() + SLASH + parameter.getName());
      if (null != annotsToParamsOnAlias && !annotsToParamsOnAlias.isEmpty()) {
        for (CsdlAnnotation annotation : annotsToParamsOnAlias) {
          if (!compareAnnotations(operation.getParameter(parameter.getName()).getAnnotations(), annotation)) {
            operation.getParameter(parameter.getName()).getAnnotations().add(annotation);
          }
        }
      }
    }
  }

  /** Adds annotations to action
   * @param operation
   * @param annotationsOnAlias
   */
  private void addAnnotationsToOperations(CsdlOperation operation, List<CsdlAnnotation> annotations) {
    for (CsdlAnnotation annotation : annotations) {
      if (!compareAnnotations(operation.getAnnotations(), annotation)) {
        operation.getAnnotations().add(annotation);
      }
    }
  }
  
  /**
   * @param bindingParameterTypeName
   * @param parameter 
   * @param isBindingParameterCollection 
   * @return
   * @throws ODataException
   */
  private boolean isComplexPreviousTypeCompatibleToBindingParam(
      final FullQualifiedName bindingParameterTypeName, final CsdlParameter parameter, 
      Boolean isBindingParameterCollection)
      throws ODataException {
    CsdlComplexType complexType = provider.getComplexType(bindingParameterTypeName);
    if(provider.getEntityType(parameter.getTypeFQN()) == null){
      return false;
    }
    List<CsdlProperty> properties = provider.getEntityType(parameter.getTypeFQN()).getProperties();
    for (CsdlProperty property : properties) {
      String paramPropertyTypeName = property.getTypeAsFQNObject().getFullQualifiedNameAsString();
      if ((complexType != null && complexType.getBaseType() != null && 
          complexType.getBaseTypeFQN().getFullQualifiedNameAsString().equals(paramPropertyTypeName)) || 
          paramPropertyTypeName.equals(bindingParameterTypeName.getFullQualifiedNameAsString()) && 
          isBindingParameterCollection.booleanValue() == property.isCollection()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param bindingParameterTypeName
   * @param parameter
   * @return
   * @throws ODataException
   */
  private boolean isEntityPreviousTypeCompatibleToBindingParam(final FullQualifiedName bindingParameterTypeName,
      final CsdlParameter parameter) throws ODataException {
    return provider.getEntityType(bindingParameterTypeName) != null && 
    provider.getEntityType(bindingParameterTypeName).getBaseTypeFQN() != null && 
    provider.getEntityType(bindingParameterTypeName).getBaseTypeFQN().equals(parameter.getTypeFQN());
  }

  @Override
  public EdmFunction createBoundFunction(final FullQualifiedName functionName,
      final FullQualifiedName bindingParameterTypeName, final Boolean isBindingParameterCollection,
      final List<String> parameterNames) {

    try {
      List<CsdlFunction> functions = functionsMap.get(functionName);
      if (functions == null) {
        functions = provider.getFunctions(functionName);
        if (functions == null) {
          return null;
        } else {
          functionsMap.put(functionName, functions);
        }
      }
      final List<String> parameterNamesCopy =
          parameterNames == null ? Collections.<String> emptyList() : parameterNames;
      for (CsdlFunction function : functions) {
        if (function.isBound()) {
          List<CsdlParameter> providerParameters = function.getParameters();
          if (providerParameters == null || providerParameters.isEmpty()) {
            throw new EdmException("No parameter specified for bound function: " + functionName);
          }
          final CsdlParameter bindingParameter = providerParameters.get(0);
          if ((bindingParameterTypeName.equals(bindingParameter.getTypeFQN())
              ||isEntityPreviousTypeCompatibleToBindingParam(bindingParameterTypeName, bindingParameter) ||
              isComplexPreviousTypeCompatibleToBindingParam(bindingParameterTypeName, bindingParameter, 
                  isBindingParameterCollection))
              && isBindingParameterCollection.booleanValue() == bindingParameter.isCollection()
              && parameterNamesCopy.size() == providerParameters.size() - 1) {

            final List<String> providerParameterNames = new ArrayList<String>();
            for (int i = 1; i < providerParameters.size(); i++) {
              providerParameterNames.add(providerParameters.get(i).getName());
            }
            if (parameterNamesCopy.containsAll(providerParameterNames)) {
              addOperationsAnnotations(function, functionName);
              return new EdmFunctionImpl(this, functionName, function);
            }
          }
        }
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  
  @Override
  protected Map<String, String> createAliasToNamespaceInfo() {
    final Map<String, String> aliasToNamespaceInfos = new HashMap<>();
    try {
      final List<CsdlAliasInfo> aliasInfos = provider.getAliasInfos();
      if (aliasInfos != null) {
        for (CsdlAliasInfo info : aliasInfos) {
          aliasToNamespaceInfos.put(info.getAlias(), info.getNamespace());
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }
    return aliasToNamespaceInfos;
  }

  @Override
  protected EdmAction createUnboundAction(final FullQualifiedName actionName) {
    try {
      List<CsdlAction> actions = actionsMap.get(actionName);
      if (actions == null) {
        actions = provider.getActions(actionName);
        if (actions == null) {
          return null;
        } else {
          actionsMap.put(actionName, actions);
        }
      }
      // Search for first unbound action
      for (CsdlAction action : actions) {
        if (!action.isBound()) {
          addOperationsAnnotations(action, actionName);
          return new EdmActionImpl(this, actionName, action);
        }
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  protected List<EdmFunction> createUnboundFunctions(final FullQualifiedName functionName) {
    List<EdmFunction> result = new ArrayList<>();

    try {
      List<CsdlFunction> functions = functionsMap.get(functionName);
      if (functions == null) {
        functions = provider.getFunctions(functionName);
        if (functions != null) {
          functionsMap.put(functionName, functions);
        }
      }
      if (functions != null) {
        for (CsdlFunction function : functions) {
          if (!function.isBound()) {
            addOperationsAnnotations(function, functionName);
            result.add(new EdmFunctionImpl(this, functionName, function));
          }
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return result;
  }

  @Override
  protected EdmFunction createUnboundFunction(final FullQualifiedName functionName, final List<String> parameterNames) {
    try {
      List<CsdlFunction> functions = functionsMap.get(functionName);
      if (functions == null) {
        functions = provider.getFunctions(functionName);
        if (functions == null) {
          return null;
        } else {
          functionsMap.put(functionName, functions);
        }
      }

      final List<String> parameterNamesCopy =
          parameterNames == null ? Collections.<String> emptyList() : parameterNames;
      for (CsdlFunction function : functions) {
        if (!function.isBound()) {
          List<CsdlParameter> providerParameters = function.getParameters();
          if (providerParameters == null) {
            providerParameters = Collections.emptyList();
          }
          if (parameterNamesCopy.size() == providerParameters.size()) {
            final List<String> functionParameterNames = new ArrayList<>();
            for (CsdlParameter parameter : providerParameters) {
              functionParameterNames.add(parameter.getName());
            }

            if (parameterNamesCopy.containsAll(functionParameterNames)) {
              addOperationsAnnotations(function, functionName);
              addAnnotationsToParamsOfOperations(function, functionName);
              return new EdmFunctionImpl(this, functionName, function);
            }
          }
        }
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  protected Map<String, EdmSchema> createSchemas() {
    try {
      final Map<String, EdmSchema> providerSchemas = new LinkedHashMap<>();
      List<CsdlSchema> localSchemas = provider.getSchemas();
      if (localSchemas != null) {
        for (CsdlSchema schema : localSchemas) {
          providerSchemas.put(schema.getNamespace(), new EdmSchemaImpl(this, provider, schema));
        }
      }
	  for (CsdlSchema termSchemaDefn : termSchemaDefinition) {
        providerSchemas.put(termSchemaDefn.getNamespace(), 
            new EdmSchemaImpl(this, provider, termSchemaDefn));
      }
      return providerSchemas;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  protected EdmTerm createTerm(final FullQualifiedName termName) {
    try {
      CsdlTerm providerTerm = provider.getTerm(termName);
      if (providerTerm != null) {
        return new EdmTermImpl(this, termName.getNamespace(), providerTerm);
      } else {
          for (CsdlSchema schema : termSchemaDefinition) {
              if (schema.getNamespace().equalsIgnoreCase(termName.getNamespace()) ||
                  (null != schema.getAlias() && 
                  schema.getAlias().equalsIgnoreCase(termName.getNamespace()))) {
                List<CsdlTerm> terms = schema.getTerms();
                for (CsdlTerm term : terms) {
                  if (term.getName().equals(termName.getName())) {
                    return new EdmTermImpl(this, termName.getNamespace(), term);
                  }
                }
              }
            }
        }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  @Override
  protected EdmAnnotations createAnnotationGroup(final FullQualifiedName targetName, String qualifier) {
    try {
      CsdlAnnotations providerGroup = provider.getAnnotationsGroup(targetName, qualifier);
      if (null == providerGroup) {
        for(CsdlSchema schema : termSchemaDefinition) {
          providerGroup = schema.getAnnotationGroup(targetName.getFullQualifiedNameAsString(), qualifier);
          break;
        }
      }
      if (providerGroup != null) {
        return new EdmAnnotationsImpl(this, providerGroup);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }
  
  public List<CsdlSchema> getTermSchemaDefinitions() {
    return termSchemaDefinition;
  }
  
  private boolean compareAnnotations(List<CsdlAnnotation> annotations, CsdlAnnotation annotation) {
    for (CsdlAnnotation annot : annotations) {
      if (annot.equals(annotation)) {
        return true;
      }
    }
    return false;
  }
}
