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
package myservice.mynamespace.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlAction;
import org.apache.olingo.commons.api.edm.provider.CsdlActionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlFunction;
import org.apache.olingo.commons.api.edm.provider.CsdlFunctionImport;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlParameter;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlReturnType;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

/*
 * this class is supposed to declare the metadata of the OData service
 * it is invoked by the Olingo framework e.g. when the metadata document of the service is invoked
 * e.g. http://localhost:8080/ExampleService1/ExampleService1.svc/$metadata
 */
public class DemoEdmProvider extends CsdlAbstractEdmProvider {

  // Service Namespace
  public static final String NAMESPACE = "OData.Demo";

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

  // Entity Types Names
  public static final String ET_PRODUCT_NAME = "Product";
  public static final FullQualifiedName ET_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ET_PRODUCT_NAME);

  public static final String ET_CATEGORY_NAME = "Category";
  public static final FullQualifiedName ET_CATEGORY_FQN = new FullQualifiedName(NAMESPACE, ET_CATEGORY_NAME);
  
  // Entity Set Names
  public static final String ES_PRODUCTS_NAME = "Products";
  public static final String ES_CATEGORIES_NAME = "Categories";
  
  // Action
  public static final String ACTION_RESET = "Reset";
  public static final FullQualifiedName ACTION_RESET_FQN = new FullQualifiedName(NAMESPACE, ACTION_RESET);
  
  //Bound Action
  public static final String ACTION_PROVIDE_DISCOUNT = "DiscountProducts";
  public static final FullQualifiedName ACTION_PROVIDE_DISCOUNT_FQN = new FullQualifiedName(NAMESPACE, ACTION_PROVIDE_DISCOUNT);
  
  //Bound Action
  public static final String ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT = "DiscountProduct";
  public static final FullQualifiedName ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT);
  
  // Function
  public static final String FUNCTION_COUNT_CATEGORIES = "CountCategories";
  public static final FullQualifiedName FUNCTION_COUNT_CATEGORIES_FQN 
                                                = new FullQualifiedName(NAMESPACE, FUNCTION_COUNT_CATEGORIES);
  //Bound Function
  public static final String FUNCTION_PROVIDE_DISCOUNT = "GetDiscountProducts";
  public static final FullQualifiedName FUNCTION_PROVIDE_DISCOUNT_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_PROVIDE_DISCOUNT);
  
  public static final String FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT = "GetDiscountProduct";
  public static final FullQualifiedName FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN = new FullQualifiedName(NAMESPACE, FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT);
  
  // Function/Action Parameters
  public static final String PARAMETER_AMOUNT = "Amount";
  
  //Bound Action Binding Parameter
  public static final String PARAMETER_CATEGORY = "ParamCategory";
  
  //Bound Function Binding Parameter
  public static final String PARAMETER_BIND = "BindingParameter";
  
  @Override
  public List<CsdlAction> getActions(final FullQualifiedName actionName) {
 // It is allowed to overload actions, so we have to provide a list of Actions for each action name
    final List<CsdlAction> actions = new ArrayList<CsdlAction>();
    if(actionName.equals(ACTION_RESET_FQN)) {
      
      // Create parameters
      final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
      final CsdlParameter parameter = new CsdlParameter();
      parameter.setName(PARAMETER_AMOUNT);
      parameter.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      parameters.add(parameter);
      
      // Create the Csdl Action
      final CsdlAction action = new CsdlAction();
      action.setName(ACTION_RESET_FQN.getName());
      action.setParameters(parameters);
      actions.add(action);
      
      return actions;
    } else if (actionName.equals(ACTION_PROVIDE_DISCOUNT_FQN)) {
      // Create parameters
      final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
      CsdlParameter parameter = new CsdlParameter();
      parameter.setName(PARAMETER_CATEGORY);
      parameter.setType(ET_CATEGORY_FQN);
      parameter.setCollection(true);
      parameters.add(parameter);
      parameter = new CsdlParameter();
      parameter.setName(PARAMETER_AMOUNT);
      parameter.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      parameters.add(parameter);
      
      // Create the Csdl Action
      final CsdlAction action = new CsdlAction();
      action.setName(ACTION_PROVIDE_DISCOUNT_FQN.getName());
      action.setBound(true);
      action.setParameters(parameters);
      action.setReturnType(new CsdlReturnType().setType(ET_PRODUCT_FQN).setCollection(true));
      actions.add(action);
      
      return actions;
    } else if (actionName.equals(ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN)) {
      // Create parameters
      final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
      CsdlParameter parameter = new CsdlParameter();
      parameter.setName(PARAMETER_CATEGORY);
      parameter.setType(ET_CATEGORY_FQN);
      parameter.setCollection(false);
      parameters.add(parameter);
      parameter = new CsdlParameter();
      parameter.setName(PARAMETER_AMOUNT);
      parameter.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      parameters.add(parameter);
      
      // Create the Csdl Action
      final CsdlAction action = new CsdlAction();
      action.setName(ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN.getName());
      action.setBound(true);
      action.setParameters(parameters);
      action.setReturnType(new CsdlReturnType().setType(ET_PRODUCT_FQN).setCollection(false));
      actions.add(action);
      
      return actions;
    }
    
    return null;
  }
  
  @Override
  public CsdlActionImport getActionImport(final FullQualifiedName entityContainer, final String actionImportName) {
    if(entityContainer.equals(CONTAINER)) {
      if(actionImportName.equals(ACTION_RESET_FQN.getName())) {
        return new CsdlActionImport()
            .setName(actionImportName)
            .setAction(ACTION_RESET_FQN);
      }
    }
    
    return null;
  }
  
  @Override
  public List<CsdlFunction> getFunctions(final FullQualifiedName functionName) {
    if (functionName.equals(FUNCTION_COUNT_CATEGORIES_FQN)) {
      // It is allowed to overload functions, so we have to provide a list of functions for each function name
      final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

      // Create the parameter for the function
      final CsdlParameter parameterAmount = new CsdlParameter();
      parameterAmount.setName(PARAMETER_AMOUNT);
      parameterAmount.setNullable(false);
      parameterAmount.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      
      // Create the return type of the function
      final CsdlReturnType returnType = new CsdlReturnType();
      returnType.setCollection(true);
      returnType.setType(ET_CATEGORY_FQN);
      
      // Create the function
      final CsdlFunction function = new CsdlFunction();
      function.setName(FUNCTION_COUNT_CATEGORIES_FQN.getName())
              .setParameters(Arrays.asList(parameterAmount))
              .setReturnType(returnType);
      functions.add(function);

      return functions;
    } else if (functionName.equals(FUNCTION_PROVIDE_DISCOUNT_FQN)) {

      final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

      // Create the parameter for the function
      final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
      CsdlParameter parameter = new CsdlParameter();
      parameter.setName(PARAMETER_BIND);
      parameter.setNullable(false);
      parameter.setType(ET_CATEGORY_FQN);
      parameter.setCollection(true);
      parameters.add(parameter);
      parameter = new CsdlParameter();
      parameter.setName(PARAMETER_AMOUNT);
      parameter.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      parameters.add(parameter);

      // Create the return type of the function
      final CsdlReturnType returnType = new CsdlReturnType();
      returnType.setCollection(true).setType(ET_PRODUCT_FQN);

      // Create the function
      final CsdlFunction function = new CsdlFunction();
      function.setName(FUNCTION_PROVIDE_DISCOUNT_FQN.getName())
          .setParameters(parameters)
          .setBound(true).setReturnType(returnType);
      functions.add(function);

      return functions;

    } else if (functionName.equals(FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN)) {

      final List<CsdlFunction> functions = new ArrayList<CsdlFunction>();

      // Create the parameter for the function
      final List<CsdlParameter> parameters = new ArrayList<CsdlParameter>();
      CsdlParameter parameter = new CsdlParameter();
      parameter.setName(PARAMETER_BIND);
      parameter.setNullable(false);
      parameter.setType(ET_CATEGORY_FQN);
      parameters.add(parameter);
      parameter = new CsdlParameter();
      parameter.setName(PARAMETER_AMOUNT);
      parameter.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      parameters.add(parameter);

      // Create the return type of the function
      final CsdlReturnType returnType = new CsdlReturnType();
      returnType.setType(ET_PRODUCT_FQN);

      // Create the function
      final CsdlFunction function = new CsdlFunction();
      function.setName(FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN.getName())
          .setParameters(parameters)
          .setBound(true).setReturnType(returnType);
      functions.add(function);

      return functions;

    }

    return null;
  }
  
  @Override
  public CsdlFunctionImport getFunctionImport(FullQualifiedName entityContainer, String functionImportName) {
    if(entityContainer.equals(CONTAINER)) {
      if(functionImportName.equals(FUNCTION_COUNT_CATEGORIES_FQN.getName())) {
        return new CsdlFunctionImport()
                      .setName(functionImportName)
                      .setFunction(FUNCTION_COUNT_CATEGORIES_FQN)
                      .setEntitySet(ES_CATEGORIES_NAME)
                      .setIncludeInServiceDocument(true);
      }
    }
    
    return null;
  }
  
  public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) {

    // this method is called for each EntityType that are configured in the Schema
    CsdlEntityType entityType = null;

    if (entityTypeName.equals(ET_PRODUCT_FQN)) {
      // create EntityType properties
      CsdlProperty id = new CsdlProperty().setName("ID")
                                          .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      CsdlProperty name = new CsdlProperty().setName("Name")
                                            .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty description = new CsdlProperty().setName("Description")
                                                   .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty price = new CsdlProperty().setName("Price")
          .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

      // create PropertyRef for Key element
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("ID");

      // navigation property: many-to-one, null not allowed (product must have a category)
      CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName("Category")
                                                                   .setType(ET_CATEGORY_FQN).setNullable(true)
                                                                   .setPartner("Products");
      List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
      navPropList.add(navProp);

      // configure EntityType
      entityType = new CsdlEntityType();
      entityType.setName(ET_PRODUCT_NAME);
      entityType.setProperties(Arrays.asList(id, name, description, price));
      entityType.setKey(Arrays.asList(propertyRef));
      entityType.setNavigationProperties(navPropList);

    } else if (entityTypeName.equals(ET_CATEGORY_FQN)) {
      // create EntityType properties
      CsdlProperty id = new CsdlProperty().setName("ID")
                                          .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      CsdlProperty name = new CsdlProperty().setName("Name")
                                            .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

      // create PropertyRef for Key element
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("ID");

      // navigation property: one-to-many
      CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName("Products")
                                                                   .setType(ET_PRODUCT_FQN).setCollection(true)
                                                                   .setPartner("Category");
      List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
      navPropList.add(navProp);

      // configure EntityType
      entityType = new CsdlEntityType();
      entityType.setName(ET_CATEGORY_NAME);
      entityType.setProperties(Arrays.asList(id, name));
      entityType.setKey(Arrays.asList(propertyRef));
      entityType.setNavigationProperties(navPropList);
    }

    return entityType;
  }

  @Override
  public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) {

    CsdlEntitySet entitySet = null;

    if (entityContainer.equals(CONTAINER)) {

      if (entitySetName.equals(ES_PRODUCTS_NAME)) {

        entitySet = new CsdlEntitySet();
        entitySet.setName(ES_PRODUCTS_NAME);
        entitySet.setType(ET_PRODUCT_FQN);

        // navigation
        CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
        navPropBinding.setTarget("Categories"); // the target entity set, where the navigation property points to
        navPropBinding.setPath("Category"); // the path from entity type to navigation property
        List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
        navPropBindingList.add(navPropBinding);
        entitySet.setNavigationPropertyBindings(navPropBindingList);

      } else if (entitySetName.equals(ES_CATEGORIES_NAME)) {

        entitySet = new CsdlEntitySet();
        entitySet.setName(ES_CATEGORIES_NAME);
        entitySet.setType(ET_CATEGORY_FQN);

        // navigation
        CsdlNavigationPropertyBinding navPropBinding = new CsdlNavigationPropertyBinding();
        navPropBinding.setTarget("Products"); // the target entity set, where the navigation property points to
        navPropBinding.setPath("Products"); // the path from entity type to navigation property
        List<CsdlNavigationPropertyBinding> navPropBindingList = new ArrayList<CsdlNavigationPropertyBinding>();
        navPropBindingList.add(navPropBinding);
        entitySet.setNavigationPropertyBindings(navPropBindingList);
      }
    }

    return entitySet;

  }

  @Override
  public List<CsdlSchema> getSchemas() {

    // create Schema
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);

    // add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    entityTypes.add(getEntityType(ET_PRODUCT_FQN));
    entityTypes.add(getEntityType(ET_CATEGORY_FQN));
    schema.setEntityTypes(entityTypes);
    
    // add actions
    List<CsdlAction> actions = new ArrayList<CsdlAction>();
    actions.addAll(getActions(ACTION_RESET_FQN));
    actions.addAll(getActions(ACTION_PROVIDE_DISCOUNT_FQN));
    actions.addAll(getActions(ACTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN));
    schema.setActions(actions);
    
    // add functions
    List<CsdlFunction> functions = new ArrayList<CsdlFunction>();
    functions.addAll(getFunctions(FUNCTION_COUNT_CATEGORIES_FQN));
    functions.addAll(getFunctions(FUNCTION_PROVIDE_DISCOUNT_FQN));
    functions.addAll(getFunctions(FUNCTION_PROVIDE_DISCOUNT_FOR_PRODUCT_FQN));
    schema.setFunctions(functions);
    
    // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

    // finally
    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    schemas.add(schema);

    return schemas;
  }
  
  public CsdlEntityContainer getEntityContainer() {
    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));
    entitySets.add(getEntitySet(CONTAINER, ES_CATEGORIES_NAME));
    
    // Create function imports
    List<CsdlFunctionImport> functionImports = new ArrayList<CsdlFunctionImport>();
    functionImports.add(getFunctionImport(CONTAINER, FUNCTION_COUNT_CATEGORIES));
    
    // Create action imports
    List<CsdlActionImport> actionImports = new ArrayList<CsdlActionImport>();
    actionImports.add(getActionImport(CONTAINER, ACTION_RESET));
    
    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);
    entityContainer.setFunctionImports(functionImports);
    entityContainer.setActionImports(actionImports);
    
    return entityContainer;

  }

  @Override
  public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) {

    // This method is invoked when displaying the service document at
    // e.g. http://localhost:8080/DemoService/DemoService.svc
    if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
      CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
      entityContainerInfo.setContainerName(CONTAINER);
      return entityContainerInfo;
    }
    return null;
  }
}
