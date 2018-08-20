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
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlEnumType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
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

  public EdmProviderImpl(final CsdlEdmProvider provider) {
    this.provider = provider;
  }
  
  public EdmProviderImpl(final CsdlEdmProvider provider, final List<CsdlSchema> termSchemaDefinition) {
    this.provider = provider;
    this.termSchemaDefinition = termSchemaDefinition;
  }

  @Override
  public EdmEntityContainer createEntityContainer(final FullQualifiedName containerName) {
    try {
      CsdlEntityContainerInfo entityContainerInfo = provider.getEntityContainerInfo(containerName);
      if (entityContainerInfo != null) {
		addAnnotations(provider.getEntityContainer(), entityContainerInfo.getContainerName());
        return new EdmEntityContainerImpl(this, provider, entityContainerInfo.getContainerName(), 
            provider.getEntityContainer());
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addAnnotations(CsdlEntityContainer csdlEntityContainer, FullQualifiedName containerName) {
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(containerName.getFullQualifiedNameAsString())) {
          for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
            if (!compareAnnotations(csdlEntityContainer.getAnnotations(), annotation)) {
              csdlEntityContainer.getAnnotations().addAll(annotationGrp.getAnnotations());
            }
          }
          break;
        }
      }
    }
  }
  
  @Override
  public EdmEnumType createEnumType(final FullQualifiedName enumName) {
    try {
      CsdlEnumType enumType = provider.getEnumType(enumName);
      if (enumType != null) {
		addAnnotations(enumType, enumName);
        return new EdmEnumTypeImpl(this, enumName, enumType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addAnnotations(CsdlEnumType enumType, FullQualifiedName enumName) {
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(enumName.getFullQualifiedNameAsString())) {
          for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
            if (!compareAnnotations(enumType.getAnnotations(), annotation)) {
              enumType.getAnnotations().addAll(annotationGrp.getAnnotations());
            }
          }
          break;
        }
      }
    }
  }
  
  @Override
  public EdmTypeDefinition createTypeDefinition(final FullQualifiedName typeDefinitionName) {
    try {
      CsdlTypeDefinition typeDefinition = provider.getTypeDefinition(typeDefinitionName);
      if (typeDefinition != null) {
		addAnnotations(typeDefinition, typeDefinitionName);
        return new EdmTypeDefinitionImpl(this, typeDefinitionName, typeDefinition);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }
  
  public void addAnnotations(CsdlTypeDefinition typeDefinition, FullQualifiedName typeDefinitionName) {
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(typeDefinitionName.getFullQualifiedNameAsString())) {
          for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
            if (!compareAnnotations(typeDefinition.getAnnotations(), annotation)) {
              typeDefinition.getAnnotations().addAll(annotationGrp.getAnnotations());
            }
          }
          break;
        }
      }
    }
  }

  @Override
  public EdmEntityType createEntityType(final FullQualifiedName entityTypeName) {
    try {
      CsdlEntityType entityType = provider.getEntityType(entityTypeName);
      if (entityType != null) {
		  if (!isEntityDerivedFromES()) {
          addAnnotations(entityType, entityTypeName);
        }
        return new EdmEntityTypeImpl(this, entityTypeName, entityType);
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  /**
   * Add the annotations defined in an external file to the property/
   * navigation property and the entity
   * @param structuralType
   * @param typeName
   */
  public void addAnnotations(CsdlStructuralType structuralType, FullQualifiedName typeName) {
    boolean isPropAnnotationsCleared = false;
    boolean isNavPropAnnotationsCleared = false;
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(typeName.getFullQualifiedNameAsString())) {
          addAnnotationsToStructuralTypes(structuralType, annotationGrp);
        } else {
          checkAnnotationsOnStructuralProperties(structuralType, typeName, isPropAnnotationsCleared, annotationGrp);
          checkAnnotationsOnStructuralNavProperties(structuralType, typeName, isNavPropAnnotationsCleared,
              annotationGrp);
          isPropAnnotationsCleared = true;
          isNavPropAnnotationsCleared = true;
        }
      }
    }
  }

  /** Check if annotations are added on navigation properties of a structural type
   * @param structuralType
   * @param typeName
   * @param isNavPropAnnotationsCleared
   * @param annotationGrp
   */
  private void checkAnnotationsOnStructuralNavProperties(CsdlStructuralType structuralType, FullQualifiedName typeName,
      boolean isNavPropAnnotationsCleared, CsdlAnnotations annotationGrp) {
    List<CsdlNavigationProperty> navProperties = structuralType.getNavigationProperties();
    for (CsdlNavigationProperty navProperty : navProperties) {
      if (!isNavPropAnnotationsCleared) {
        structuralType.getNavigationProperty(navProperty.getName()).getAnnotations().clear();
      }
      if (annotationGrp.getTarget().equalsIgnoreCase(typeName + "/" + navProperty.getName())) {
        addAnnotationsToStructuralTypeNavProperties(structuralType, annotationGrp, navProperty);
      }
    }
  }

  /** Check if annotations are added on properties of a structural type
   * @param structuralType
   * @param typeName
   * @param isPropAnnotationsCleared
   * @param annotationGrp
   */
  private void checkAnnotationsOnStructuralProperties(CsdlStructuralType structuralType, FullQualifiedName typeName,
      boolean isPropAnnotationsCleared, CsdlAnnotations annotationGrp) {
    List<CsdlProperty> properties = structuralType.getProperties();
    for (CsdlProperty property : properties) {
      if (!isPropAnnotationsCleared) {
        structuralType.getProperty(property.getName()).getAnnotations().clear();
      }
      if (annotationGrp.getTarget().equalsIgnoreCase(
          typeName.getFullQualifiedNameAsString() + "/" + property.getName())) {
        addAnnotationsToStructuralTypeProperties(structuralType, annotationGrp, property);
      }
    }
  }

  /**
   * @param structuralType
   * @param annotationGrp
   * @param navProperty
   */
  private void addAnnotationsToStructuralTypeNavProperties(CsdlStructuralType structuralType,
      CsdlAnnotations annotationGrp, CsdlNavigationProperty navProperty) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(structuralType.getNavigationProperty(
          navProperty.getName()).getAnnotations(), annotation)) {
        structuralType.getNavigationProperty(navProperty.getName()).getAnnotations().
        add(annotation);
      }
    }
  }

  /**
   * @param structuralType
   * @param annotationGrp
   * @param property
   */
  private void addAnnotationsToStructuralTypeProperties(CsdlStructuralType structuralType,
      CsdlAnnotations annotationGrp, CsdlProperty property) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(structuralType.getProperty(
          property.getName()).getAnnotations(), annotation)) {
        structuralType.getProperty(property.getName()).getAnnotations().add(annotation); 
      }
    }
  }

  /**
   * @param structuralType
   * @param annotationGrp
   */
  private void addAnnotationsToStructuralTypes(CsdlStructuralType structuralType, CsdlAnnotations annotationGrp) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(structuralType.getAnnotations(), annotation)) {
        structuralType.getAnnotations().addAll(annotationGrp.getAnnotations());
      }
    }
  }
  
  @Override
  public EdmComplexType createComplexType(final FullQualifiedName complexTypeName) {
    try {
      final CsdlComplexType complexType = provider.getComplexType(complexTypeName);
      if (complexType != null) {
		  if (!isComplexDerivedFromES()) {
          addAnnotations(complexType, complexTypeName);
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
			addAnnotations(action, actionName);
            return new EdmActionImpl(this, actionName, action);
          }

        }
      }
      return null;
    } catch (ODataException e) {
      throw new EdmException(e);
    }
  }

  public void addAnnotations(CsdlAction action, FullQualifiedName actionName) {
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(
            actionName.getFullQualifiedNameAsString())) {
          addAnnotationsToActions(action, annotationGrp);
        } else {
          addAnnotationsToParamsOfActions(action, actionName, annotationGrp);
        }
      }
    }
  }
  
  /** Adds annotations to actions
   * @param action
   * @param actionName
   * @param annotationGrp
   */
  private void addAnnotationsToParamsOfActions(CsdlAction action, FullQualifiedName actionName,
      CsdlAnnotations annotationGrp) {
    final List<CsdlParameter> parameters = action.getParameters();
    for (CsdlParameter parameter : parameters) {
      if (annotationGrp.getTarget().equalsIgnoreCase(
          actionName.getFullQualifiedNameAsString() + "/" + parameter.getName())) {
        for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
          if (!compareAnnotations(action.getParameter(parameter.getName()).getAnnotations(), annotation)) {
            action.getParameter(parameter.getName()).getAnnotations().add(annotation);
          }
        }
      }
    }
  }

  /** Adds annotations to parameters of action
   * @param action
   * @param annotationGrp
   */
  private void addAnnotationsToActions(CsdlAction action, CsdlAnnotations annotationGrp) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(action.getAnnotations(), annotation)) {
        action.getAnnotations().add(annotation);
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
			  addAnnotations(function, functionName);
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

  public void addAnnotations(CsdlFunction function, FullQualifiedName functionName) {
    for (CsdlSchema schema : termSchemaDefinition) {
      List<CsdlAnnotations> annotationGrps = schema.getAnnotationGroups();
      for (CsdlAnnotations annotationGrp : annotationGrps) {
        if (annotationGrp.getTarget().equalsIgnoreCase(
            functionName.getFullQualifiedNameAsString())) {
          addAnnotationsToFunctions(function, annotationGrp);
        } else {
          addAnnotationsToParamsOFunctions(function, functionName, annotationGrp);
        }
      }
    }
  }

  /** Adds annotations of function parameters
   * @param function
   * @param functionName
   * @param annotationGrp
   */
  private void addAnnotationsToParamsOFunctions(CsdlFunction function, FullQualifiedName functionName,
      CsdlAnnotations annotationGrp) {
    final List<CsdlParameter> parameters = function.getParameters();
    for (CsdlParameter parameter : parameters) {
      if (annotationGrp.getTarget().equalsIgnoreCase(
          functionName.getFullQualifiedNameAsString() + "/" + parameter.getName())) {
        for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
          if (!compareAnnotations(function.getParameter(parameter.getName()).getAnnotations(), annotation)) {
            function.getParameter(parameter.getName()).getAnnotations().add(annotation);
          }
        }
      }
    }
  }

  /**Add annotations to functions
   * @param function
   * @param annotationGrp
   */
  private void addAnnotationsToFunctions(CsdlFunction function, CsdlAnnotations annotationGrp) {
    for (CsdlAnnotation annotation : annotationGrp.getAnnotations()) {
      if (!compareAnnotations(function.getAnnotations(), annotation)) {
        function.getAnnotations().add(annotation);
      }
    }
  }
  
  @Override
  protected Map<String, String> createAliasToNamespaceInfo() {
    final Map<String, String> aliasToNamespaceInfos = new HashMap<String, String>();
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
    List<EdmFunction> result = new ArrayList<EdmFunction>();

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
            final List<String> functionParameterNames = new ArrayList<String>();
            for (CsdlParameter parameter : providerParameters) {
              functionParameterNames.add(parameter.getName());
            }

            if (parameterNamesCopy.containsAll(functionParameterNames)) {
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
      final Map<String, EdmSchema> providerSchemas = new LinkedHashMap<String, EdmSchema>();
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
