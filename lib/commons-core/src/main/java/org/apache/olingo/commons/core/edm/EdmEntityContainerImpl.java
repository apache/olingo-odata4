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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAliasInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlOperationImport;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSingleton;
import org.apache.olingo.commons.api.ex.ODataException;

public class EdmEntityContainerImpl extends AbstractEdmNamed implements EdmEntityContainer {

  private final CsdlEdmProvider provider;
  private CsdlEntityContainer container;

  private final FullQualifiedName entityContainerName;
  private final FullQualifiedName parentContainerName;

  private List<EdmSingleton> singletons;
  private final Map<String, EdmSingleton> singletonCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmSingleton>());
  private List<EdmEntitySet> entitySets;
  private final Map<String, EdmEntitySet> entitySetCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmEntitySet>());
  private List<EdmActionImport> actionImports;
  private final Map<String, EdmActionImport> actionImportCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmActionImport>());
  private List<EdmFunctionImport> functionImports;
  private final Map<String, EdmFunctionImport> functionImportCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmFunctionImport>());
	  private boolean isAnnotationsIncluded = false;
  private final Map<String, EdmEntitySet> entitySetWithAnnotationsCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmEntitySet>());
  private final Map<String, EdmSingleton> singletonWithAnnotationsCache = Collections.synchronizedMap(
      new LinkedHashMap<String, EdmSingleton>());
  private boolean isSingletonAnnotationsIncluded = false;
  private final String SLASH = "/";
  private final String DOT = ".";

  public EdmEntityContainerImpl(final Edm edm, final CsdlEdmProvider provider,
      final CsdlEntityContainerInfo entityContainerInfo) {
    super(edm, entityContainerInfo.getContainerName().getName(), null);
    this.provider = provider;
    entityContainerName = entityContainerInfo.getContainerName();
    parentContainerName = entityContainerInfo.getExtendsContainer();
  }

  public EdmEntityContainerImpl(final Edm edm, final CsdlEdmProvider provider, final FullQualifiedName containerFQN,
      final CsdlEntityContainer entityContainer) {
    super(edm, containerFQN.getName(), entityContainer);
    this.provider = provider;
    container = entityContainer;
    entityContainerName = containerFQN;
    parentContainerName = entityContainer == null ? null : 
      entityContainer.getExtendsContainerFQN();
  }

  @Override
  public String getNamespace() {
    return entityContainerName.getNamespace();
  }

  @Override
  public FullQualifiedName getFullQualifiedName() {
    return entityContainerName;
  }

  @Override
  public EdmSingleton getSingleton(final String singletonName) {
    EdmSingleton singleton = singletonWithAnnotationsCache.get(singletonName);
    if (singleton == null) {
      singleton = singletonCache.get(singletonName);
      if (singleton == null) {
        singleton = createSingleton(singletonName);
        if (singleton != null) {
          if (isSingletonAnnotationsIncluded) {
            singletonWithAnnotationsCache.put(singletonName, singleton);
          } else {
            singletonCache.put(singletonName, singleton);
          }
        }
      }
    }
    return singleton;
  }

  @Override
  public EdmEntitySet getEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = entitySetWithAnnotationsCache.get(entitySetName);
    if (entitySet == null) {
      entitySet = entitySetCache.get(entitySetName);
      if (entitySet == null) {
        entitySet = createEntitySet(entitySetName);
        if (entitySet != null) {
          if (isAnnotationsIncluded) {
            entitySetWithAnnotationsCache.put(entitySetName, entitySet);
          } else {
            entitySetCache.put(entitySetName, entitySet);
          }
        }
      }
    }
    ((EdmProviderImpl)edm).setIsPreviousES(true);
    return entitySet;
  }

  @Override
  public EdmActionImport getActionImport(final String actionImportName) {
    EdmActionImport actionImport = actionImportCache.get(actionImportName);
    if (actionImport == null) {
      actionImport = createActionImport(actionImportName);
      if (actionImport != null) {
        actionImportCache.put(actionImportName, actionImport);
      }
    }
    return actionImport;
  }

  @Override
  public EdmFunctionImport getFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = functionImportCache.get(functionImportName);
    if (functionImport == null) {
      functionImport = createFunctionImport(functionImportName);
      if (functionImport != null) {
        functionImportCache.put(functionImportName, functionImport);
      }
    }
    return functionImport;
  }

  @Override
  public List<EdmEntitySet> getEntitySets() {
    if (entitySets == null) {
      loadAllEntitySets();
    }
    return Collections.unmodifiableList(entitySets);
  }

  @Override
  public List<EdmEntitySet> getEntitySetsWithAnnotations() {
    loadAllEntitySets();
    return Collections.unmodifiableList(entitySets);
  }
  
  @Override
  public List<EdmFunctionImport> getFunctionImports() {
    if (functionImports == null) {
      loadAllFunctionImports();
    }
    return Collections.unmodifiableList(functionImports);
  }

  @Override
  public List<EdmSingleton> getSingletons() {
    if (singletons == null) {
      loadAllSingletons();
    }
    return Collections.unmodifiableList(singletons);
  }

  @Override
  public List<EdmActionImport> getActionImports() {
    if (actionImports == null) {
      loadAllActionImports();
    }
    return Collections.unmodifiableList(actionImports);
  }

  @Override
  public FullQualifiedName getParentContainerName() {
    return parentContainerName;
  }

  protected EdmSingleton createSingleton(final String singletonName) {
    EdmSingleton singleton = null;

    try {
      final CsdlSingleton providerSingleton = provider.getSingleton(entityContainerName, singletonName);
      if (providerSingleton != null) {
        addSingletonAnnotations(providerSingleton, entityContainerName);
        singleton = new EdmSingletonImpl(edm, this, providerSingleton);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return singleton;
  }

  private void addSingletonAnnotations(CsdlSingleton singleton, FullQualifiedName entityContainerName) {
    CsdlEntityType entityType = fetchEntityTypeFromSingleton(singleton);
    if (entityType == null) {
      return;
    }
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName + SLASH + singleton.getName());
    addAnnotationsOnSingleton(singleton, annotations);
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + DOT + entityContainerName.getName() + SLASH + singleton.getName());
    addAnnotationsOnSingleton(singleton, annotationsOnAlias);
    addAnnotationsToPropertiesDerivedFromSingleton(singleton, entityType, entityContainerName);
   }

  /**
   * Adds annotations on singleton
   * @param singleton
   * @param annotations
   */
  private void addAnnotationsOnSingleton(CsdlSingleton singleton, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      isSingletonAnnotationsIncluded = true;
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(singleton.getAnnotations(), annotation)) {
          singleton.getAnnotations().add(annotation);
        }
      }
    }
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
          if (aliasInfo.getNamespace().equalsIgnoreCase(namespace)) {
            return aliasInfo.getAlias();
          }
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }
    return null;
  }
  
  /** adds annotations to entity type properties derived from singleton
   * E.g of target paths
   * MySchema.MyEntityContainer/MySingleton/MyComplexProperty/MyNavigationProperty
   * @param singleton
   * @param isPropAnnotationsCleared
   * @param isNavPropAnnotationsCleared
   * @param entityType
   * @param entityContainerName
   * @param annotationGrp
   */
  private void addAnnotationsToPropertiesDerivedFromSingleton(CsdlSingleton singleton, 
      CsdlEntityType entityType, FullQualifiedName entityContainerName) {
    String entitySetName = null;
    String schemaName = null;
    String containerName = null;
    try {
      List<CsdlEntitySet> entitySets = this.provider.getEntityContainer() != null ? 
          this.provider.getEntityContainer().getEntitySets() : new ArrayList<CsdlEntitySet>();
      for (CsdlEntitySet entitySet : entitySets) {
        entitySetName = entitySet.getName();
        String entityTypeName = entitySet.getTypeFQN().getFullQualifiedNameAsString();
        if ((null != entityTypeName && entityTypeName.equalsIgnoreCase(
            entitySet.getTypeFQN().getNamespace() + DOT + entityType.getName()))) {
          containerName = this.provider.getEntityContainer().getName();
          schemaName = entitySet.getTypeFQN().getNamespace();
          for (CsdlProperty property : entityType.getProperties()) {
            if (isPropertyComplex(property)) {
              CsdlComplexType complexType = getComplexTypeFromProperty(property);
              addAnnotationsToComplexTypeIncludedFromSingleton(singleton, property, complexType);
            }
            removeAnnotationsAddedToPropertiesOfEntityType(entityType, property, entityContainerName);
            removeAnnotationsAddedToPropertiesViaEntitySet(entityType, property, 
                schemaName, containerName, entitySetName);
          }
        }
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }
  
  /**
   * If annotations are added to properties via Entity set then remove them
   * @param entityType
   * @param property
   * @param schemaName
   * @param containerName
   * @param entitySetName
   */
  private void removeAnnotationsAddedToPropertiesViaEntitySet(CsdlEntityType entityType, CsdlProperty property,
      String schemaName, String containerName, String entitySetName) {
    List<CsdlAnnotation> annotPropDerivedFromES = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        schemaName + DOT + 
        containerName + SLASH +  entitySetName + SLASH + property.getName());
    removeAnnotationsOnPropertiesDerivedFromES(entityType, property, annotPropDerivedFromES);
    String aliasName = getAliasInfo(schemaName);
    List<CsdlAnnotation> annotPropDerivedFromESOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        aliasName + DOT + 
        containerName + SLASH +  entitySetName + SLASH + property.getName());
    removeAnnotationsOnPropertiesDerivedFromES(entityType, property, annotPropDerivedFromESOnAlias);
  }

  /**
   * Removes the annotations added on properties via Entity Set in case of singleton flow
   * @param entityType
   * @param property
   * @param annotPropDerivedFromES
   */
  private void removeAnnotationsOnPropertiesDerivedFromES(CsdlEntityType entityType, CsdlProperty property,
      List<CsdlAnnotation> annotPropDerivedFromES) {
    if (null != annotPropDerivedFromES && !annotPropDerivedFromES.isEmpty()) {
      for (CsdlAnnotation annotation : annotPropDerivedFromES) {
        entityType.getProperty(property.getName()).getAnnotations().remove(annotation);
      }
    }
  }

  /**
   * @param singleton
   * @return
   */
  private CsdlEntityType fetchEntityTypeFromSingleton(CsdlSingleton singleton) {
    CsdlEntityType entityType;
    try {
      entityType = singleton.getTypeFQN() != null ? this.provider.getEntityType(new FullQualifiedName(
          singleton.getTypeFQN().getFullQualifiedNameAsString())) : null;
   } catch (ODataException e) {
     throw new EdmException(e);
   }
    return entityType;
  }
  
  /**
   * 
   * @param singleton
   * @param entityContainerName2
   * @param annotationGrp
   * @param propertyName
   * @param isComplexNavPropAnnotationsCleared
   * @param complexType
   */
  private void addAnnotationsToComplexTypeIncludedFromSingleton(CsdlSingleton singleton,
      CsdlProperty propertyName, CsdlComplexType complexType) {
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    for (CsdlProperty complexPropertyName : complexType.getProperties()) {
      removeAnnotationAddedToPropertiesOfComplexType(complexType, complexPropertyName, entityContainerName);
      
      List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          entityContainerName + SLASH + 
              singleton.getName() + SLASH + 
              propertyName.getName() + SLASH + complexPropertyName.getName());
      addAnnotationsOnComplexTypeProperties(complexType, complexPropertyName, annotations);
      List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          aliasName + DOT + entityContainerName.getName() + SLASH + 
              singleton.getName() + SLASH + 
              propertyName.getName() + SLASH + complexPropertyName.getName());
      addAnnotationsOnComplexTypeProperties(complexType, complexPropertyName, annotationsOnAlias);
    }
    for (CsdlNavigationProperty complexNavPropertyName : complexType.getNavigationProperties()) {
      checkAnnotationAddedToNavPropertiesOfComplexType(complexType, complexNavPropertyName, entityContainerName);
      
      List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(entityContainerName +
          SLASH + singleton.getName() + SLASH + 
          propertyName.getName() + SLASH + complexNavPropertyName.getName());
      addAnnotationsOnComplexTypeNavProperties(complexType, complexNavPropertyName, annotations);
      List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          aliasName + DOT + entityContainerName.getName() +
          SLASH + singleton.getName() + SLASH + 
          propertyName.getName() + SLASH + complexNavPropertyName.getName());
      addAnnotationsOnComplexTypeNavProperties(complexType, complexNavPropertyName, annotationsOnAlias);
    }    
  }
  
  /**
   * Adds annotations on complex type navigation properties
   * @param complexType
   * @param complexNavProperty
   * @param annotations
   */
  private void addAnnotationsOnComplexTypeNavProperties(CsdlComplexType complexType,
      CsdlNavigationProperty complexNavProperty, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      isAnnotationsIncluded = true;
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(complexType.getNavigationProperty(
            complexNavProperty.getName()).getAnnotations(), annotation)) {
          complexType.getNavigationProperty(complexNavProperty.getName()).getAnnotations().add(annotation); 
        }
      }
    }
  }

  /**
   * Adds annotations on complex type properties
   * @param complexType
   * @param complexProperty
   * @param annotations
   */
  private void addAnnotationsOnComplexTypeProperties(CsdlComplexType complexType, CsdlProperty complexProperty,
      List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      isAnnotationsIncluded = true;
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(complexType.getProperty(
            complexProperty.getName()).getAnnotations(), annotation)) {
          complexType.getProperty(complexProperty.getName()).getAnnotations().add(annotation); 
        }
      }
    }
  }
  
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = null;

    try {
      final CsdlEntitySet providerEntitySet = provider.getEntitySet(entityContainerName, entitySetName);
      if (providerEntitySet != null) {
		addEntitySetAnnotations(providerEntitySet, entityContainerName);
        entitySet = new EdmEntitySetImpl(edm, this, providerEntitySet);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return entitySet;
  }

  private void addEntitySetAnnotations(CsdlEntitySet entitySet, FullQualifiedName entityContainerName) {
    CsdlEntityType entityType = getCsdlEntityTypeFromEntitySet(entitySet);
    if (entityType == null) {
      return;
    }
    
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName + SLASH + entitySet.getName());
    addAnnotationsOnEntitySet(entitySet, annotations);
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + DOT + entityContainerName.getName() + SLASH + entitySet.getName());
    addAnnotationsOnEntitySet(entitySet, annotationsOnAlias);
    addAnnotationsToPropertiesIncludedFromES(entitySet, entityContainerName, entityType);
  }

  /**
   * Adds annotations on entity sets
   * @param entitySet
   * @param annotations
   */
  private void addAnnotationsOnEntitySet(CsdlEntitySet entitySet, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      isAnnotationsIncluded = true;
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(entitySet.getAnnotations(), annotation)) {
          entitySet.getAnnotations().add(annotation);
        }
      }
    }
  }

  /**
   * @param entitySet
   * @return
   */
  private CsdlEntityType getCsdlEntityTypeFromEntitySet(CsdlEntitySet entitySet) {
    CsdlEntityType entityType;
    try {
      entityType = entitySet.getTypeFQN() != null ? this.provider.getEntityType(new FullQualifiedName(
          entitySet.getTypeFQN().getFullQualifiedNameAsString())) : null;
   } catch (ODataException e) {
     throw new EdmException(e);
   }
    return entityType;
  }

  /** Adds annotations to Entity type Properties derived from entity set
   * E.g of target paths 
   * MySchema.MyEntityContainer/MyEntitySet/MyProperty
   * MySchema.MyEntityContainer/MyEntitySet/MyNavigationProperty
   * MySchema.MyEntityContainer/MyEntitySet/MyComplexProperty/MyProperty
   * MySchema.MyEntityContainer/MyEntitySet/MyComplexProperty/MyNavigationProperty
   * @param entitySet
   * @param entityContainerName
   * @param entityType 
   * @return
   */
  private void addAnnotationsToPropertiesIncludedFromES(CsdlEntitySet entitySet,
      FullQualifiedName entityContainerName, CsdlEntityType entityType) {
    for (CsdlProperty property : entityType.getProperties()) {
      removeAnnotationsAddedToPropertiesOfEntityType(entityType, property, entityContainerName);
        if (isPropertyComplex(property)) {
          CsdlComplexType complexType = getComplexTypeFromProperty(property);
          addAnnotationsToComplexTypeIncludedFromES(entitySet, entityContainerName,
             property, complexType);
        } else {
          addAnnotationsToETProperties(entitySet, entityContainerName, entityType, property);
        }
      }
     for (CsdlNavigationProperty navProperty : entityType.getNavigationProperties()) {
       removeAnnotationAddedToNavProperties(entityType, navProperty, entityContainerName);
       addAnnotationsToETNavProperties(entitySet, entityContainerName, entityType, navProperty);
     }
  }

  /**
   * @param entitySet
   * @param entityContainerName
   * @param entityType
   * @param property
   */
  private void addAnnotationsToETProperties(CsdlEntitySet entitySet, FullQualifiedName entityContainerName,
      CsdlEntityType entityType, CsdlProperty property) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        entityContainerName + SLASH + entitySet.getName() + SLASH + 
        property.getName());
    addAnnotationsOnETProperties(entityType, property, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        aliasName + DOT + entityContainerName.getName() + SLASH + entitySet.getName() + SLASH + 
        property.getName());
    addAnnotationsOnETProperties(entityType, property, annotationsOnAlias);
  }

  /**
   * Adds annotations to Entity type Properties derived from entity set
   * @param entityType
   * @param property
   * @param annotations
   */
  private void addAnnotationsOnETProperties(CsdlEntityType entityType, CsdlProperty property,
      List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      isAnnotationsIncluded = true;
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(entityType.getProperty(
            property.getName()).getAnnotations(), annotation)) {
          entityType.getProperty(property.getName()).getAnnotations().add(annotation); 
        }
      }
    }
  }

  /**
   * Adds annotations to Entity type Navigation Properties derived from entity set
   * @param entitySet
   * @param entityContainerName
   * @param entityType
   * @param navProperty
   */
  private void addAnnotationsToETNavProperties(CsdlEntitySet entitySet, FullQualifiedName entityContainerName,
      CsdlEntityType entityType, CsdlNavigationProperty navProperty) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(
         entityContainerName + SLASH + entitySet.getName() + SLASH + 
             navProperty.getName());
     addAnnotationsOnETNavProperties(entityType, navProperty, annotations);
     
     String aliasName = getAliasInfo(entityContainerName.getNamespace());
     List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
         aliasName + DOT + entityContainerName.getName() + SLASH + entitySet.getName() + SLASH + 
             navProperty.getName());
     addAnnotationsOnETNavProperties(entityType, navProperty, annotationsOnAlias);
  }

  /**
   * @param entityType
   * @param navProperty
   * @param annotations
   */
  private void addAnnotationsOnETNavProperties(CsdlEntityType entityType, CsdlNavigationProperty navProperty,
      List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
       isAnnotationsIncluded = true;
       for (CsdlAnnotation annotation : annotations) {
         if (!compareAnnotations(entityType.getNavigationProperty(
             navProperty.getName()).getAnnotations(), annotation)) {
           entityType.getNavigationProperty(navProperty.getName()).getAnnotations().add(annotation); 
         }
       }
     }
  }

  /**
   * If annotations are added to properties via entity type path, then remove it
   * @param type
   * @param property
   * @param entityContainerName
   */
  private void removeAnnotationsAddedToPropertiesOfEntityType(CsdlEntityType type, CsdlProperty property, 
      FullQualifiedName entityContainerName) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName.getNamespace() + 
        DOT + type.getName() + SLASH + property.getName());
    removeAnnotationsOnETProperties(property, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + DOT + entityContainerName.getName() + 
        DOT + type.getName() + SLASH + property.getName());
    removeAnnotationsOnETProperties(property, annotationsOnAlias);
  }

  /**
   * Removes the annotations added on Entity type
   * properties when there is a target path on entity type
   * @param property
   * @param annotations
   */
  private void removeAnnotationsOnETProperties(CsdlProperty property, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      for (CsdlAnnotation annotation : annotations) {
        property.getAnnotations().remove(annotation); 
      }
    }
  }
  
  private void removeAnnotationAddedToNavProperties(CsdlEntityType entityType, 
      CsdlNavigationProperty navProperty, FullQualifiedName entityContainerName) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        entityContainerName.getNamespace() + 
        DOT + entityType.getName() + SLASH + navProperty.getName());
    removeAnnotationsOnNavProperties(navProperty, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
        aliasName + DOT + entityContainerName.getName() + 
        DOT + entityType.getName() + SLASH + navProperty.getName());
    removeAnnotationsOnNavProperties(navProperty, annotationsOnAlias);
  }

  /**
   * Removes the annotations added on Entity type
   * navigation properties when there is a target path on entity type
   * @param property
   * @param annotations
   */
  private void removeAnnotationsOnNavProperties(CsdlNavigationProperty property, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      for (CsdlAnnotation annotation : annotations) {
        property.getAnnotations().remove(annotation); 
      }
    }
  }

  /**
   * @param propertyName
   * @return
   */
  private CsdlComplexType getComplexTypeFromProperty(CsdlProperty propertyName) {
    CsdlComplexType complexType;
     try {
      complexType = this.provider.getComplexType(propertyName.getTypeAsFQNObject());
    } catch (ODataException e) {
      throw new EdmException(e);
    }
    return complexType;
  }

  /**
   * @param entitySet
   * @param entityContainerName
   * @param complexProperty
   * @param complexType 
   * @return
   */
  private void addAnnotationsToComplexTypeIncludedFromES(CsdlEntitySet entitySet,
      FullQualifiedName entityContainerName, CsdlProperty complexProperty, 
      CsdlComplexType complexType) {
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    for (CsdlProperty complexPropertyName : complexType.getProperties()) {
      removeAnnotationAddedToPropertiesOfComplexType(complexType, complexPropertyName, entityContainerName);
      
      List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(entityContainerName + SLASH + 
      entitySet.getName() + SLASH + 
      complexProperty.getName() + SLASH + complexPropertyName.getName());
      addAnnotationsOnComplexTypeProperties(complexType, complexPropertyName, annotations);
      
      List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          aliasName + DOT + entityContainerName.getName() + SLASH + 
          entitySet.getName() + SLASH + 
          complexProperty.getName() + SLASH + complexPropertyName.getName());
      addAnnotationsOnComplexTypeProperties(complexType, complexPropertyName, annotationsOnAlias);
    }
    for (CsdlNavigationProperty complexNavProperty : complexType.getNavigationProperties()) {
      checkAnnotationAddedToNavPropertiesOfComplexType(complexType, complexNavProperty, entityContainerName);
      
      List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          entityContainerName + SLASH + entitySet.getName() + SLASH + 
          complexProperty.getName() + SLASH + complexNavProperty.getName());
      addAnnotationsOnComplexTypeNavProperties(complexType, complexNavProperty, annotations);
      
      List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().get(
          aliasName + DOT + entityContainerName.getName() + SLASH + entitySet.getName() + SLASH + 
          complexProperty.getName() + SLASH + complexNavProperty.getName());
      addAnnotationsOnComplexTypeNavProperties(complexType, complexNavProperty, annotationsOnAlias);
    }
  }

  private void checkAnnotationAddedToNavPropertiesOfComplexType(CsdlComplexType complexType,
      CsdlNavigationProperty complexNavProperty, FullQualifiedName entityContainerName) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName.getNamespace() + 
        DOT + complexType.getName() + SLASH + complexNavProperty.getName());
    removeAnnotationsOnNavProperties(complexNavProperty, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + 
        DOT + complexType.getName() + SLASH + complexNavProperty.getName());
    removeAnnotationsOnNavProperties(complexNavProperty, annotationsOnAlias);
  }

  private void removeAnnotationAddedToPropertiesOfComplexType(CsdlComplexType complexType,
      CsdlProperty complexPropertyName, FullQualifiedName entityContainerName) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName.getNamespace() + 
        DOT + complexType.getName() + SLASH + complexPropertyName.getName());
    removeAnnotationsOnETProperties(complexPropertyName, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + DOT + entityContainerName.getName() + 
        DOT + complexType.getName() + SLASH + complexPropertyName.getName());
    removeAnnotationsOnETProperties(complexPropertyName, annotationsOnAlias);
  }

  private boolean isPropertyComplex(CsdlProperty propertyName) {
    try {
      return this.provider.getComplexType(propertyName.getTypeAsFQNObject()) != null ? true : false;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }
  
  protected EdmActionImport createActionImport(final String actionImportName) {
    EdmActionImport actionImport = null;

    try {
      final CsdlActionImport providerImport = provider.getActionImport(entityContainerName, actionImportName);
      if (providerImport != null) {
        addOperationImportAnnotations(providerImport, entityContainerName);
        actionImport = new EdmActionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return actionImport;
  }

  private void addOperationImportAnnotations(CsdlOperationImport operationImport, 
      FullQualifiedName entityContainerName) {
    List<CsdlAnnotation> annotations = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(entityContainerName + SLASH + operationImport.getName());
    addAnnotationsOnOperationImport(operationImport, annotations);
    
    String aliasName = getAliasInfo(entityContainerName.getNamespace());
    List<CsdlAnnotation> annotationsOnAlias = ((EdmProviderImpl)edm).getAnnotationsMap().
        get(aliasName + DOT + entityContainerName.getName() + SLASH + operationImport.getName());
    addAnnotationsOnOperationImport(operationImport, annotationsOnAlias);
   }
   
  /**
   * Adds annotations on action import
   * @param operationImport
   * @param annotations
   */
  private void addAnnotationsOnOperationImport(CsdlOperationImport operationImport, List<CsdlAnnotation> annotations) {
    if (null != annotations && !annotations.isEmpty()) {
      for (CsdlAnnotation annotation : annotations) {
        if (!compareAnnotations(operationImport.getAnnotations(), annotation)) {
          operationImport.getAnnotations().add(annotation);
        }
      }
    }
  }
  
  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = null;

    try {
      final CsdlFunctionImport providerImport = provider.getFunctionImport(entityContainerName, functionImportName);
      if (providerImport != null) {
        addOperationImportAnnotations(providerImport, entityContainerName);
        functionImport = new EdmFunctionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return functionImport;
  }
  
  protected void loadAllEntitySets() {
    loadContainer();
    final List<CsdlEntitySet> providerEntitySets = container.getEntitySets();
    final List<EdmEntitySet> entitySetsLocal = new ArrayList<EdmEntitySet>();

    if (providerEntitySets != null) {
      for (CsdlEntitySet entitySet : providerEntitySets) {
		addEntitySetAnnotations(entitySet, entityContainerName);
        final EdmEntitySetImpl impl = new EdmEntitySetImpl(edm, this, entitySet);
        if (isAnnotationsIncluded) {
          entitySetWithAnnotationsCache.put(impl.getName(), impl);
        } else {
          entitySetCache.put(impl.getName(), impl);
        }
        entitySetsLocal.add(impl);
      }
      entitySets = entitySetsLocal;
	  ((EdmProviderImpl)edm).setIsPreviousES(true);
    }
  }

  protected void loadAllFunctionImports() {
    loadContainer();
    final List<CsdlFunctionImport> providerFunctionImports = container.getFunctionImports();
    final ArrayList<EdmFunctionImport> functionImportsLocal = new ArrayList<EdmFunctionImport>();

    if (providerFunctionImports != null) {
      for (CsdlFunctionImport functionImport : providerFunctionImports) {
        addOperationImportAnnotations(functionImport, entityContainerName);
        EdmFunctionImportImpl impl = new EdmFunctionImportImpl(edm, this, functionImport);
        functionImportCache.put(impl.getName(), impl);
        functionImportsLocal.add(impl);
      }
      functionImports = functionImportsLocal;
    }
  }

  protected void loadAllSingletons() {
    loadContainer();
    final List<CsdlSingleton> providerSingletons = container.getSingletons();
    final List<EdmSingleton> singletonsLocal = new ArrayList<EdmSingleton>();

    if (providerSingletons != null) {
      for (CsdlSingleton singleton : providerSingletons) {
        addSingletonAnnotations(singleton, entityContainerName);
        final EdmSingletonImpl impl = new EdmSingletonImpl(edm, this, singleton);
        singletonCache.put(singleton.getName(), impl);
        singletonsLocal.add(impl);
      }
      singletons = singletonsLocal;
    }
  }

  protected void loadAllActionImports() {
    loadContainer();
    final List<CsdlActionImport> providerActionImports = container.getActionImports();
    final List<EdmActionImport> actionImportsLocal = new ArrayList<EdmActionImport>();

    if (providerActionImports != null) {
      for (CsdlActionImport actionImport : providerActionImports) {
        addOperationImportAnnotations(actionImport, entityContainerName);
		final EdmActionImportImpl impl = new EdmActionImportImpl(edm, this, actionImport);
        actionImportCache.put(actionImport.getName(), impl);
        actionImportsLocal.add(impl);
      }
      actionImports = actionImportsLocal;
    }

  }

  private void loadContainer() {
    if (container == null) {
      try {
        CsdlEntityContainer containerLocal = provider.getEntityContainer();
        if (containerLocal == null) {
          containerLocal = new CsdlEntityContainer().setName(getName());
        }
		((EdmProviderImpl)edm).addEntityContainerAnnotations(containerLocal, entityContainerName);
        container = containerLocal;
      } catch (ODataException e) {
        throw new EdmException(e);
      }
    }
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
