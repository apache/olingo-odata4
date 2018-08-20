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

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmException;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotation;
import org.apache.olingo.commons.api.edm.provider.CsdlAnnotations;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
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
		addAnnotations(providerSingleton, entityContainerName);
        singleton = new EdmSingletonImpl(edm, this, providerSingleton);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return singleton;
  }

  private void addAnnotations(CsdlSingleton singleton, FullQualifiedName entityContainerName) {
    boolean isPropAnnotationsCleared = false;
    boolean isNavPropAnnotationsCleared = false;
    CsdlEntityType entityType = fetchEntityTypeFromSingleton(singleton);
    if (entityType == null) {
      return;
    }
    
    List<CsdlSchema> termSchemaDefinition = ((EdmProviderImpl)edm).getTermSchemaDefinitions();
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().
            equalsIgnoreCase(entityContainerName + "/" + singleton.getName())) {
          isSingletonAnnotationsIncluded = true;
          addAnnotationsToSingleton(singleton, annotationGrp);
        } else {
          addAnnotationsToPropertiesDerivedFromSingleton(singleton, isPropAnnotationsCleared,
              isNavPropAnnotationsCleared, entityType, annotationGrp);
          isPropAnnotationsCleared = true;
          isNavPropAnnotationsCleared = true;
        }
      }
    }
   }

  /** adds annotations to entity type properties derived from singleton
   * @param singleton
   * @param isPropAnnotationsCleared
   * @param isNavPropAnnotationsCleared
   * @param entityType
   * @param annotationGrp
   */
  private void addAnnotationsToPropertiesDerivedFromSingleton(CsdlSingleton singleton, boolean isPropAnnotationsCleared,
      boolean isNavPropAnnotationsCleared, CsdlEntityType entityType, CsdlAnnotations annotationGrp) {
    for (CsdlProperty propertyName : entityType.getProperties()) {
      if (!isPropAnnotationsCleared) {
        entityType.getProperty(propertyName.getName()).getAnnotations().clear();
      }
      if (isPropertyComplex(propertyName)) {
        CsdlComplexType complexType = getComplexTypeFromProperty(propertyName);
        addAnnotationsToComplexTypeIncludedFromSingleton(singleton, 
           annotationGrp, propertyName, isNavPropAnnotationsCleared, complexType);
      }
    }
  }

  /** Adds annotation to singleton
   * @param singleton
   * @param annotationGrp
   */
  private void addAnnotationsToSingleton(CsdlSingleton singleton, CsdlAnnotations annotationGrp) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(singleton.getAnnotations(), annotation)) {
        singleton.getAnnotations().add(annotation);
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
      CsdlAnnotations annotationGrp, CsdlProperty propertyName,
      boolean isComplexNavPropAnnotationsCleared, CsdlComplexType complexType) {
    for (CsdlNavigationProperty complexNavPropertyName : complexType.getNavigationProperties()) {
      if (!isComplexNavPropAnnotationsCleared) {
        complexType.getNavigationProperty(complexNavPropertyName.getName()).getAnnotations().clear();
      }
      if (annotationGrp.getTarget().
          equalsIgnoreCase(entityContainerName + "/" + singleton.getName() + "/" + 
      propertyName.getName() + "/" + complexNavPropertyName.getName())) {
        isSingletonAnnotationsIncluded = true;
        addAnnotationsToComplexTypeNavProperties(annotationGrp, complexType, complexNavPropertyName);
      }
    }    
  }
  
  protected EdmEntitySet createEntitySet(final String entitySetName) {
    EdmEntitySet entitySet = null;

    try {
      final CsdlEntitySet providerEntitySet = provider.getEntitySet(entityContainerName, entitySetName);
      if (providerEntitySet != null) {
		addAnnotations(providerEntitySet, entityContainerName);
        entitySet = new EdmEntitySetImpl(edm, this, providerEntitySet);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return entitySet;
  }

  private void addAnnotations(CsdlEntitySet entitySet, FullQualifiedName entityContainerName) {
    boolean isPropAnnotationsCleared = false;
    boolean isNavPropAnnotationsCleared = false;
    CsdlEntityType entityType = getCsdlEntityTypeFromEntitySet(entitySet);
    if (entityType == null) {
      return;
    }
    
   List<CsdlSchema> termSchemaDefinition = ((EdmProviderImpl)edm).getTermSchemaDefinitions();
   for (CsdlSchema schema : termSchemaDefinition) {
     List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
     for (CsdlAnnotations annotationGrp : annotationGrps) {
       if (annotationGrp.getTarget().
           equalsIgnoreCase(entityContainerName + "/" + entitySet.getName())) {
         isAnnotationsIncluded = true;
         addAnnotationsToEntitySet(entitySet, annotationGrp);
       } else {
         addAnnotationsToEntityTypeIncludedFromES(entitySet, entityContainerName,
             annotationGrp, isPropAnnotationsCleared, isNavPropAnnotationsCleared, entityType);
         isPropAnnotationsCleared = true;
         isNavPropAnnotationsCleared = true;
       }
     }
   }
  }

  /**
   * @param entitySet
   * @param annotationGrp
   */
  private void addAnnotationsToEntitySet(CsdlEntitySet entitySet, CsdlAnnotations annotationGrp) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
       if (!compareAnnotations(entitySet.getAnnotations(), annotation)) {
         entitySet.getAnnotations().add(annotation);
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
   * @param entitySet
   * @param entityContainerName
   * @param annotationGrp
   * @param entityType 
   * @param isNavPropAnnotationsCleared 
   * @param isPropAnnotationsCleared
   * @return
   */
  private void addAnnotationsToEntityTypeIncludedFromES(CsdlEntitySet entitySet,
      FullQualifiedName entityContainerName, CsdlAnnotations annotationGrp, 
      boolean isPropAnnotationsCleared, boolean isNavPropAnnotationsCleared, CsdlEntityType entityType) {
     for (CsdlProperty propertyName : entityType.getProperties()) {
       if (!isPropAnnotationsCleared) {
         entityType.getProperty(propertyName.getName()).getAnnotations().clear();
       }
       if (isPropertyComplex(propertyName)) {
         CsdlComplexType complexType = getComplexTypeFromProperty(propertyName);
         addAnnotationsToComplexTypeIncludedFromES(entitySet, entityContainerName,
            annotationGrp, propertyName, isPropAnnotationsCleared, isNavPropAnnotationsCleared, complexType);
       } else {
         if (annotationGrp.getTarget().
             equalsIgnoreCase(entityContainerName + "/" + entitySet.getName() + "/" + 
         propertyName.getName())) {
           isAnnotationsIncluded = true;
           addAnnotationsToEntityTypeProperties(annotationGrp, entityType, propertyName);
         }
       }
     }
     for (CsdlNavigationProperty navPropertyName : entityType.getNavigationProperties()) {
       if (!isNavPropAnnotationsCleared) {
         entityType.getNavigationProperty(navPropertyName.getName()).getAnnotations().clear();
       }
       if (annotationGrp.getTarget().
           equalsIgnoreCase(entityContainerName + "/" + entitySet.getName() + "/" + 
       navPropertyName.getName())) {
         isAnnotationsIncluded = true;
         addAnnotationsToEntityTypeNavProperties(annotationGrp, entityType, navPropertyName);
       }
     }
  }

  /** Adds annotations to Entity type Navigation Properties derived from entity set
   * @param annotationGrp
   * @param entityType
   * @param navPropertyName
   */
  private void addAnnotationsToEntityTypeNavProperties(CsdlAnnotations annotationGrp, CsdlEntityType entityType,
      CsdlNavigationProperty navPropertyName) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
       if (!compareAnnotations(entityType.getNavigationProperty(
           navPropertyName.getName()).getAnnotations(), annotation)) {
         entityType.getNavigationProperty(navPropertyName.getName()).getAnnotations().add(annotation); 
       }
     }
  }

  /** Adds annotations to Entity type Properties derived from entity set
   * @param annotationGrp
   * @param entityType
   * @param propertyName
   */
  private void addAnnotationsToEntityTypeProperties(CsdlAnnotations annotationGrp, CsdlEntityType entityType,
      CsdlProperty propertyName) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
       if (!compareAnnotations(entityType.getProperty(
           propertyName.getName()).getAnnotations(), annotation)) {
         entityType.getProperty(propertyName.getName()).getAnnotations().add(annotation); 
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
   * @param annotationGrp
   * @param propertyName
   * @param complexType 
   * @param isComplexNavPropAnnotationsCleared2 
   * @param isComplexPropAnnotationsCleared2 
   * @return
   */
  private void addAnnotationsToComplexTypeIncludedFromES(CsdlEntitySet entitySet,
      FullQualifiedName entityContainerName, CsdlAnnotations annotationGrp, 
      CsdlProperty propertyName, boolean isComplexPropAnnotationsCleared, 
      boolean isComplexNavPropAnnotationsCleared, CsdlComplexType complexType) {
     for (CsdlProperty complexPropertyName : complexType.getProperties()) {
       if (!isComplexPropAnnotationsCleared) {
         complexType.getProperty(complexPropertyName.getName()).getAnnotations().clear();
       }
       if (annotationGrp.getTarget().
           equalsIgnoreCase(entityContainerName + "/" + entitySet.getName() + "/" + 
       propertyName.getName() + "/" + complexPropertyName.getName())) {
         isAnnotationsIncluded = true;
         addAnnotationsToComplexTypeProperties(annotationGrp, complexType, complexPropertyName);
       }
     }
     for (CsdlNavigationProperty complexNavPropertyName : complexType.getNavigationProperties()) {
       if (!isComplexNavPropAnnotationsCleared) {
         complexType.getNavigationProperty(complexNavPropertyName.getName()).getAnnotations().clear();
       }
       if (annotationGrp.getTarget().
           equalsIgnoreCase(entityContainerName + "/" + entitySet.getName() + "/" + 
       propertyName.getName() + "/" + complexNavPropertyName.getName())) {
         isAnnotationsIncluded = true;
         addAnnotationsToComplexTypeNavProperties(annotationGrp, complexType, complexNavPropertyName);
       }
     }
  }

  /**
   * @param annotationGrp
   * @param complexType
   * @param complexNavPropertyName
   */
  private void addAnnotationsToComplexTypeNavProperties(CsdlAnnotations annotationGrp, CsdlComplexType complexType,
      CsdlNavigationProperty complexNavPropertyName) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
       if (!compareAnnotations(complexType.getNavigationProperty(
           complexNavPropertyName.getName()).getAnnotations(), annotation)) {
         complexType.getNavigationProperty(complexNavPropertyName.getName()).getAnnotations().add(annotation); 
       }
     }
  }

  /**
   * @param annotationGrp
   * @param complexType
   * @param complexPropertyName
   */
  private void addAnnotationsToComplexTypeProperties(CsdlAnnotations annotationGrp, CsdlComplexType complexType,
      CsdlProperty complexPropertyName) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
       if (!compareAnnotations(complexType.getProperty(
           complexPropertyName.getName()).getAnnotations(), annotation)) {
         complexType.getProperty(complexPropertyName.getName()).getAnnotations().add(annotation); 
       }
     }
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
		addAnnotations(providerImport, entityContainerName);
        actionImport = new EdmActionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return actionImport;
  }

  private void addAnnotations(CsdlActionImport actionImport, FullQualifiedName entityContainerName) {
    List<CsdlSchema> termSchemaDefinition = ((EdmProviderImpl)edm).getTermSchemaDefinitions();
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().
            equalsIgnoreCase(entityContainerName + "/" + actionImport.getName())) {
          for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
            if (!compareAnnotations(actionImport.getAnnotations(), annotation)) {
              actionImport.getAnnotations().add(annotation);
            }
          }
          break;
        }
      }
    }
   }
   
  protected EdmFunctionImport createFunctionImport(final String functionImportName) {
    EdmFunctionImport functionImport = null;

    try {
      final CsdlFunctionImport providerImport = provider.getFunctionImport(entityContainerName, functionImportName);
      if (providerImport != null) {
		addAnnotations(providerImport, entityContainerName);
        functionImport = new EdmFunctionImportImpl(edm, this, providerImport);
      }
    } catch (ODataException e) {
      throw new EdmException(e);
    }

    return functionImport;
  }

  private void addAnnotations(CsdlFunctionImport functionImport, FullQualifiedName entityContainerName) {
    List<CsdlSchema> termSchemaDefinition = ((EdmProviderImpl)edm).getTermSchemaDefinitions();
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().
            equalsIgnoreCase(entityContainerName + "/" + functionImport.getName())) {
          for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
            if (!compareAnnotations(functionImport.getAnnotations(), annotation)) {
              functionImport.getAnnotations().add(annotation);
            }
          }
          break;
        }
      }
    }
   }
   
  protected void loadAllEntitySets() {
    loadContainer();
    final List<CsdlEntitySet> providerEntitySets = container.getEntitySets();
    final List<EdmEntitySet> entitySetsLocal = new ArrayList<EdmEntitySet>();

    if (providerEntitySets != null) {
      for (CsdlEntitySet entitySet : providerEntitySets) {
		addAnnotations(entitySet, entityContainerName);
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
		addAnnotations(functionImport, entityContainerName);
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
		addAnnotations(singleton, entityContainerName);
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
        addAnnotations(actionImport, entityContainerName);
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
		((EdmProviderImpl)edm).addAnnotations(containerLocal, entityContainerName);
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
