package org.apache.olingo.producer.core.testutil;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmActionImport;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.EdmServiceMetadata;
import org.apache.olingo.commons.api.edm.EdmSingleton;
import org.apache.olingo.commons.api.edm.EdmStructuralType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.edm.helper.FullQualifiedName;

public class EdmMock implements Edm {
  public static final String NAMESPACE_SCHEMA = "RefScenario";
  public static final FullQualifiedName CONTAINER_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "Container1");

  public static final FullQualifiedName ACTION_IMPORT1_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "actionImport1");
  public static final FullQualifiedName COMPANY_SINGLETON_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "Company");
  public static final FullQualifiedName TEAMS_SET_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "Teams");
  public static final FullQualifiedName MANAGERS_SET_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "Managers");
  public static final FullQualifiedName EMPLOYEES_SET_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "Employees");
  public static final FullQualifiedName EMPLOYEES_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "EmployeeType");
  public static final FullQualifiedName TEAMS_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "TeamType");
  public static final FullQualifiedName MANAGERS_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "ManagerType");
  public static final FullQualifiedName COMPANY_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "CompanyType");
  public static final FullQualifiedName FUNCTION1_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "function1");
  public static final FullQualifiedName FUNCTION_MAXIMAL_AGE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "MaximalAge");
  public static final FullQualifiedName FUNCTION_EMPLOYEE_SEARCH_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "EmployeeSearch");
  public static final FullQualifiedName FUNCTION_ALL_USED_ROOMS_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "AllUsedRoomIds");
  public static final FullQualifiedName FUNCTION_MOST_COMMON_LOCATION_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "MostCommonLocation");
  public static final FullQualifiedName FUNCTION_ALL_LOCATIONS_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "AllLocations");
  public static final FullQualifiedName ACTION1_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "action1");
  public static final FullQualifiedName TYPE_DEF1_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "tdtypeDef1");
  public static final FullQualifiedName RATING_ENUM_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "eRating");
  public static final FullQualifiedName LOCATION_TYPE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA, "cLocation");
  public static final FullQualifiedName NON_BINDING_PARAMETER = new FullQualifiedName(NAMESPACE_SCHEMA,
      "NonBindingParameter");

  public static final FullQualifiedName FUNCTION_IMPORT1_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "functionImport1");
  public static final FullQualifiedName FUNCTION_IMPORT_EMPLOYEE_SEARCH_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "EmployeeSearch");
  public static final FullQualifiedName FUNCTION_IMPORT_MAXIMAL_AGE_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "MaximalAge");
  public static final FullQualifiedName FUNCTION_IMPORT_ALL_USED_ROOMS_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "AllUsedRoomIds");
  public static final FullQualifiedName FUNCTION_IMPORT_MOST_COMMON_LOCATION_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "MostCommonLocation");
  public static final FullQualifiedName FUNCTION_IMPORT_ALL_LOCATIONS_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "AllLocations");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_entity");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_SET_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_entity_set");
  public static final FullQualifiedName BOUND_FUNCTION_PPROP_RT_ENTITY_SET_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_pprop_rt_entity_set");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_PPROP_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_pprop");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_CPROP_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_cprop");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_CPROP_COLL_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_cprop_coll");
  public static final FullQualifiedName BOUND_FUNCTION_ENTITY_SET_RT_PPROP_COLL_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_entity_set_rt_pprop_coll");
  public static final FullQualifiedName BOUND_FUNCTION_SINGLETON_RT_ENTITY_SET_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "bf_singleton_rt_entity_set");
  public static final FullQualifiedName BOUND_ACTION_PPROP_RT_ENTITY_SET_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "ba_pprop_rt_entity_set");
  public static final FullQualifiedName BOUND_ACTION_ENTITY_RT_ENTITY_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "ba_entity_rt_entity");
  public static final FullQualifiedName BOUND_ACTION_ENTITY_RT_PPROP_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "ba_entity_rt_pprop");
  public static final FullQualifiedName BOUND_ACTION_ENTITY_RT_PPROP_COLL_NAME = new FullQualifiedName(
      NAMESPACE_SCHEMA, "ba_entity_rt_pprop_coll");
  public static final FullQualifiedName BOUND_ACTION_ENTITY_SET_RT_CPROP_NAME = new FullQualifiedName(NAMESPACE_SCHEMA,
      "ba_entity_set_rt_cprop");

  private final EdmEntityType companyType = mock(EdmEntityType.class);
  private final EdmEntityType managerType = mock(EdmEntityType.class);
  private final EdmEntityType employeeType = mock(EdmEntityType.class);
  private final EdmEntityType teamType = mock(EdmEntityType.class);

  private final EdmFunction function1 = mock(EdmFunction.class);
  private final EdmFunction maximalAgeFunction = mock(EdmFunction.class);
  private final EdmFunction mostCommonLocationFunction = mock(EdmFunction.class);
  private final EdmFunction allUsedRoomIdsFunction = mock(EdmFunction.class);
  private final EdmFunction employeeSearchFunction = mock(EdmFunction.class);
  private final EdmFunction allLocationsFunction = mock(EdmFunction.class);

  private final EdmFunction boundFunctionEntitySetRtEntity = mock(EdmFunction.class);
  private final EdmFunction boundEntityColFunction = mock(EdmFunction.class);
  private final EdmFunction boundFunctionPPropRtEntitySet = mock(EdmFunction.class);
  private final EdmFunction boundFunctionEntitySetRtPProp = mock(EdmFunction.class);
  private final EdmFunction boundFunctionEntitySetRtCProp = mock(EdmFunction.class);
  private final EdmFunction boundFunctionEntitySetRtCPropColl = mock(EdmFunction.class);
  private final EdmFunction boundFunctionEntitySetRtPPropColl = mock(EdmFunction.class);
  private final EdmFunction boundFunctionSingletonRtEntitySet = mock(EdmFunction.class);

  private final EdmAction action1 = mock(EdmAction.class);
  private final EdmAction boundActionPpropRtEntitySet = mock(EdmAction.class);
  private final EdmAction boundActionEntityRtEntity = mock(EdmAction.class);
  private final EdmAction boundActionEntityRtPProp = mock(EdmAction.class);
  private final EdmAction boundActionEntityRtPPropColl = mock(EdmAction.class);
  private final EdmAction boundActionEntitySetRtCProp = mock(EdmAction.class);
  private final EdmEnumType ratingEnumType = mock(EdmEnumType.class);
  private final EdmTypeDefinition typeDef1 = mock(EdmTypeDefinition.class);
  private final EdmComplexType locationType = mock(EdmComplexType.class);

  private final EdmEntitySet employeesSet = mock(EdmEntitySet.class);
  private final EdmEntitySet managersSet = mock(EdmEntitySet.class);
  private final EdmEntitySet teamsSet = mock(EdmEntitySet.class);
  private final EdmSingleton company = mock(EdmSingleton.class);
  private final EdmActionImport actionImport1 = mock(EdmActionImport.class);
  private final EdmFunctionImport functionImport1 = mock(EdmFunctionImport.class);
  private final EdmFunctionImport employeeSearchFunctionImport = mock(EdmFunctionImport.class);
  private final EdmFunctionImport maximalAgeFunctionImport = mock(EdmFunctionImport.class);
  private final EdmFunctionImport mostCommonLocationFunctionImport = mock(EdmFunctionImport.class);
  private final EdmFunctionImport allUsedRoomIdsFunctionImport = mock(EdmFunctionImport.class);
  private final EdmFunctionImport allLocationsFunctionImport = mock(EdmFunctionImport.class);
  private final EdmEntityContainer container1 = mock(EdmEntityContainer.class);

  public EdmMock() {
    enhanceEmployeesEntitySet();
    enhanceManagersEntitySet();
    enhanceTeamsEntitySet();
    enhanceCompany();
    enhanceContainer1();

    enhanceEmployeeType();
    enhanceManagerType();
    enhanceTeamType();
    enhanceCompanyType();
    enhanceLocationType();

    enhanceActionImport1();
    enhanceFunctionImport1();
    enhanceFunctionImportEmployeeSearch();
    enhanceMaximalAgeFunctionImport();
    enhanceMostCommonLocationFunctionImport();
    enhanceAllUsedRoomIdsFunctionImport();
    enhanceAllLocationsFunctionImport();

    enhanceAction1();
    enhanceFunction1();
    enhanceFunctionEmployeeSearch();
    enhanceMaximalAgeFunction();
    enhanceMostCommonLocationFunction();
    enhanceAllUsedRoomIdsFunction();
    enhanceAllLocationsFunction();
    enhanceBoundEntityFunction();
    enhanceBoundFunctionEntitySetRtEntitySet();
    enhanceBoundFunctionPPropRtEntitySet();
    enhanceBoundFunctionEntitySetRtPProp();
    enhanceBoundFunctionEntitySetRtPPropColl();
    enhanceBoundFunctionEntitySetRtCProp();
    enhanceBoundFunctionEntitySetRtCPropColl();
    enhanceBoundFunctionSingletonRtEntitySet();
    enhanceBoundActionPPropRtEntitySet();
    enhanceBoundActionEntityRtEntity();
    enhanceBoundActionEntityRtPProp();
    enhanceBoundActionEntityRtPPropColl();
    enhanceBoundActionEntitySetRtCProp();
  }

  private void enhanceTeamType() {
    when(teamType.getName()).thenReturn(TEAMS_TYPE_NAME.getName());
    when(teamType.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(teamType.getKind()).thenReturn(EdmTypeKind.ENTITY);
    when(teamType.hasStream()).thenReturn(false);
    List<String> keyPredicateNames = new ArrayList<String>();
    when(teamType.getKeyPredicateNames()).thenReturn(keyPredicateNames);
    List<EdmKeyPropertyRef> keyPropertyRefs = new ArrayList<EdmKeyPropertyRef>();
    when(teamType.getKeyPropertyRefs()).thenReturn(keyPropertyRefs);
    List<String> navigationNames = new ArrayList<String>();
    when(teamType.getNavigationPropertyNames()).thenReturn(navigationNames);
    List<String> propertyNames = new ArrayList<String>();
    when(teamType.getPropertyNames()).thenReturn(propertyNames);

    addKeyProperty(teamType, "Id");

    addNavigationProperty(teamType, "nt_Employees", true, employeeType);

    addProperty(teamType, "Name", true, mock(EdmPrimitiveType.class));
    addProperty(teamType, "IsScrumTeam", true, mock(EdmPrimitiveType.class));
    addProperty(teamType, "Rating", true, mock(EdmPrimitiveType.class));
  }

  private void enhanceManagerType() {
    when(managerType.getName()).thenReturn(MANAGERS_TYPE_NAME.getName());
    when(managerType.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(managerType.getKind()).thenReturn(EdmTypeKind.ENTITY);
    when(managerType.hasStream()).thenReturn(true);
    when(managerType.getBaseType()).thenReturn(employeeType);
    List<String> keyPredicateNames = new ArrayList<String>();
    when(managerType.getKeyPredicateNames()).thenReturn(keyPredicateNames);
    List<EdmKeyPropertyRef> keyPropertyRefs = new ArrayList<EdmKeyPropertyRef>();
    when(managerType.getKeyPropertyRefs()).thenReturn(keyPropertyRefs);
    List<String> navigationNames = new ArrayList<String>();
    when(managerType.getNavigationPropertyNames()).thenReturn(navigationNames);
    List<String> propertyNames = new ArrayList<String>();
    when(managerType.getPropertyNames()).thenReturn(propertyNames);

    addKeyProperty(managerType, "EmployeeId");

    addNavigationProperty(managerType, "ne_Manager", false, managerType);
    addNavigationProperty(managerType, "ne_Team", false, teamType);
    addNavigationProperty(managerType, "nm_Employees", true, employeeType);

    addProperty(managerType, "EmployeeName", true, mock(EdmPrimitiveType.class));
    addProperty(managerType, "ManagerId", true, mock(EdmPrimitiveType.class));
    addProperty(managerType, "Location", false, locationType);
    addProperty(managerType, "Age", true, mock(EdmPrimitiveType.class));
    addProperty(managerType, "EntryDate", true, mock(EdmPrimitiveType.class));
    addProperty(managerType, "ImageUrl", true, mock(EdmPrimitiveType.class));
  }

  // when().thenReturn();
  private void enhanceEmployeeType() {
    when(employeeType.getName()).thenReturn(EMPLOYEES_TYPE_NAME.getName());
    when(employeeType.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(employeeType.getKind()).thenReturn(EdmTypeKind.ENTITY);
    when(employeeType.hasStream()).thenReturn(true);
    List<String> keyPredicateNames = new ArrayList<String>();
    when(employeeType.getKeyPredicateNames()).thenReturn(keyPredicateNames);
    List<EdmKeyPropertyRef> keyPropertyRefs = new ArrayList<EdmKeyPropertyRef>();
    when(employeeType.getKeyPropertyRefs()).thenReturn(keyPropertyRefs);
    List<String> navigationNames = new ArrayList<String>();
    when(employeeType.getNavigationPropertyNames()).thenReturn(navigationNames);
    List<String> propertyNames = new ArrayList<String>();
    when(employeeType.getPropertyNames()).thenReturn(propertyNames);

    addKeyProperty(employeeType, "EmployeeId");

    addNavigationProperty(employeeType, "ne_Manager", false, managerType);
    addNavigationProperty(employeeType, "ne_Team", false, teamType);

    addProperty(employeeType, "EmployeeName", true, mock(EdmPrimitiveType.class));
    addProperty(employeeType, "ManagerId", true, mock(EdmPrimitiveType.class));
    addProperty(employeeType, "Location", false, locationType);
    addProperty(employeeType, "Age", true, mock(EdmPrimitiveType.class));
    addProperty(employeeType, "EntryDate", true, mock(EdmPrimitiveType.class));
    addProperty(employeeType, "ImageUrl", true, mock(EdmPrimitiveType.class));
  }

  private void enhanceLocationType() {
    addProperty(locationType, "Country", true, mock(EdmPrimitiveType.class));
    when(locationType.getName()).thenReturn(LOCATION_TYPE_NAME.getName());
  }

  private void enhanceCompanyType() {
    when(companyType.getName()).thenReturn(COMPANY_TYPE_NAME.getName());
    when(companyType.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(companyType.getKind()).thenReturn(EdmTypeKind.ENTITY);
  }

  private void addNavigationProperty(final EdmEntityType entityType, final String propertyName,
      final boolean isCollection, final EdmType type) {
    EdmNavigationProperty property = mock(EdmNavigationProperty.class);
    entityType.getNavigationPropertyNames().add(propertyName);
    when(property.getName()).thenReturn(propertyName);
    when(entityType.getProperty(propertyName)).thenReturn(property);
    when(property.isCollection()).thenReturn(isCollection);
    when(property.getType()).thenReturn(type);
  }

  private void addKeyProperty(final EdmEntityType entityType, final String propertyName) {
    entityType.getKeyPredicateNames().add(propertyName);
    EdmProperty keyProp = addProperty(entityType, propertyName, true, mock(EdmPrimitiveType.class));
    EdmKeyPropertyRef keyRef = mock(EdmKeyPropertyRef.class);
    when(keyRef.getKeyPropertyName()).thenReturn(propertyName);
    when(keyRef.getProperty()).thenReturn(keyProp);
    entityType.getKeyPropertyRefs().add(keyRef);
    when(entityType.getKeyPropertyRef(propertyName)).thenReturn(keyRef);
  }

  private EdmProperty addProperty(final EdmStructuralType structuralType, final String propertyName,
      final boolean isPrimitive, final EdmType type) {
    EdmProperty property = mock(EdmProperty.class);
    when(property.getName()).thenReturn(propertyName);
    structuralType.getPropertyNames().add(propertyName);
    when(structuralType.getProperty(propertyName)).thenReturn(property);
    when(property.isPrimitive()).thenReturn(isPrimitive);
    when(property.getType()).thenReturn(type);
    return property;
  }

  private void enhanceContainer1() {
    when(container1.getName()).thenReturn(CONTAINER_NAME.getName());
    when(container1.getNamespace()).thenReturn(NAMESPACE_SCHEMA);

    when(container1.getEntitySet(EMPLOYEES_SET_NAME.getName())).thenReturn(employeesSet);
    when(container1.getEntitySet(MANAGERS_SET_NAME.getName())).thenReturn(managersSet);
    when(container1.getEntitySet(TEAMS_SET_NAME.getName())).thenReturn(teamsSet);
    when(container1.getSingleton(COMPANY_SINGLETON_NAME.getName())).thenReturn(company);
    when(container1.getActionImport(ACTION_IMPORT1_NAME.getName())).thenReturn(actionImport1);
    when(container1.getFunctionImport(FUNCTION_IMPORT1_NAME.getName())).thenReturn(functionImport1);
    when(container1.getFunctionImport(FUNCTION_IMPORT_MAXIMAL_AGE_NAME.getName())).thenReturn(maximalAgeFunctionImport);
    when(container1.getFunctionImport(FUNCTION_IMPORT_MOST_COMMON_LOCATION_NAME.getName())).thenReturn(
        mostCommonLocationFunctionImport);
    when(container1.getFunctionImport(FUNCTION_IMPORT_ALL_USED_ROOMS_NAME.getName())).thenReturn(
        allUsedRoomIdsFunctionImport);
    when(container1.getFunctionImport(FUNCTION_IMPORT_EMPLOYEE_SEARCH_NAME.getName())).thenReturn(
        employeeSearchFunctionImport);
    when(container1.getFunctionImport(FUNCTION_IMPORT_ALL_LOCATIONS_NAME.getName())).thenReturn(
        allLocationsFunctionImport);

    when(container1.getElement(EMPLOYEES_SET_NAME.getName())).thenReturn(employeesSet);
    when(container1.getElement(TEAMS_SET_NAME.getName())).thenReturn(teamsSet);
    when(container1.getElement(COMPANY_SINGLETON_NAME.getName())).thenReturn(company);
    when(container1.getElement(ACTION_IMPORT1_NAME.getName())).thenReturn(actionImport1);
    when(container1.getElement(FUNCTION_IMPORT_MAXIMAL_AGE_NAME.getName())).thenReturn(maximalAgeFunctionImport);

  }

  private void enhanceActionImport1() {
    when(actionImport1.getName()).thenReturn(ACTION_IMPORT1_NAME.getName());
    when(actionImport1.getEntityContainer()).thenReturn(container1);
    when(actionImport1.getReturnedEntitySet()).thenReturn(employeesSet);
    when(actionImport1.getOperation()).thenReturn(action1);
  }

  private void enhanceFunctionImport1() {
    when(functionImport1.getName()).thenReturn(FUNCTION_IMPORT1_NAME.getName());
    when(functionImport1.getEntityContainer()).thenReturn(container1);
    when(functionImport1.getReturnedEntitySet()).thenReturn(teamsSet);
    when(functionImport1.getOperation()).thenReturn(function1);
  }

  private void enhanceFunctionImportEmployeeSearch() {
    when(employeeSearchFunctionImport.getName()).thenReturn(FUNCTION_IMPORT_EMPLOYEE_SEARCH_NAME.getName());
    when(employeeSearchFunctionImport.getEntityContainer()).thenReturn(container1);
    when(employeeSearchFunctionImport.getReturnedEntitySet()).thenReturn(teamsSet);
    when(employeeSearchFunctionImport.getOperation()).thenReturn(employeeSearchFunction);
  }

  private void enhanceFunctionEmployeeSearch() {
    when(employeeSearchFunction.getName()).thenReturn(FUNCTION1_NAME.getName());
    when(employeeSearchFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(employeeSearchFunction.getReturnType().isCollection()).thenReturn(true);
    when(employeeSearchFunction.getReturnType().getType()).thenReturn(employeeType);
  }

  private void enhanceMaximalAgeFunctionImport() {
    when(maximalAgeFunctionImport.getName()).thenReturn(FUNCTION_IMPORT_MAXIMAL_AGE_NAME.getName());
    when(maximalAgeFunctionImport.getEntityContainer()).thenReturn(container1);
    // TODO: getReturnedEntitySet()
    when(maximalAgeFunctionImport.getOperation()).thenReturn(maximalAgeFunction);
  }

  private void enhanceMaximalAgeFunction() {
    when(maximalAgeFunction.getName()).thenReturn(FUNCTION_MAXIMAL_AGE_NAME.getName());
    when(maximalAgeFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(maximalAgeFunction.getReturnType().isCollection()).thenReturn(false);
    when(maximalAgeFunction.getReturnType().getType()).thenReturn(mock(EdmPrimitiveType.class));

  }

  private void enhanceAllUsedRoomIdsFunctionImport() {
    when(allUsedRoomIdsFunctionImport.getName()).thenReturn(FUNCTION_IMPORT_ALL_USED_ROOMS_NAME.getName());
    when(allUsedRoomIdsFunctionImport.getEntityContainer()).thenReturn(container1);
    // TODO: getReturnedEntitySet()
    when(allUsedRoomIdsFunctionImport.getOperation()).thenReturn(allUsedRoomIdsFunction);
  }

  private void enhanceAllUsedRoomIdsFunction() {
    when(allUsedRoomIdsFunction.getName()).thenReturn(FUNCTION_ALL_USED_ROOMS_NAME.getName());
    when(allUsedRoomIdsFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(allUsedRoomIdsFunction.getReturnType().isCollection()).thenReturn(true);
    when(allUsedRoomIdsFunction.getReturnType().getType()).thenReturn(mock(EdmPrimitiveType.class));

  }

  private void enhanceMostCommonLocationFunctionImport() {
    when(mostCommonLocationFunctionImport.getName()).thenReturn(FUNCTION_IMPORT_MOST_COMMON_LOCATION_NAME.getName());
    when(mostCommonLocationFunctionImport.getEntityContainer()).thenReturn(container1);
    // TODO: getReturnedEntitySet()
    when(mostCommonLocationFunctionImport.getOperation()).thenReturn(mostCommonLocationFunction);
  }

  private void enhanceMostCommonLocationFunction() {
    when(mostCommonLocationFunction.getName()).thenReturn(FUNCTION_MOST_COMMON_LOCATION_NAME.getName());
    when(mostCommonLocationFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(mostCommonLocationFunction.getReturnType().isCollection()).thenReturn(false);
    when(mostCommonLocationFunction.getReturnType().getType()).thenReturn(locationType);

  }

  private void enhanceAllLocationsFunctionImport() {
    when(allLocationsFunctionImport.getName()).thenReturn(FUNCTION_IMPORT_ALL_LOCATIONS_NAME.getName());
    when(allLocationsFunctionImport.getEntityContainer()).thenReturn(container1);
    // TODO: getReturnedEntitySet()
    when(allLocationsFunctionImport.getOperation()).thenReturn(allLocationsFunction);
  }

  private void enhanceAllLocationsFunction() {
    when(allLocationsFunction.getName()).thenReturn(FUNCTION_ALL_LOCATIONS_NAME.getName());
    when(allLocationsFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(allLocationsFunction.getReturnType().isCollection()).thenReturn(true);
    when(allLocationsFunction.getReturnType().getType()).thenReturn(locationType);

  }

  private void enhanceBoundEntityFunction() {
    when(boundFunctionEntitySetRtEntity.getName()).thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_NAME.getName());
    when(boundFunctionEntitySetRtEntity.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionEntitySetRtEntity.getReturnType().isCollection()).thenReturn(false);
    when(boundFunctionEntitySetRtEntity.getReturnType().getType()).thenReturn(employeeType);
    when(boundFunctionEntitySetRtEntity.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionEntitySetRtEntity.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionEntitySetRtEntitySet() {
    when(boundEntityColFunction.getName()).thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_SET_NAME.getName());
    when(boundEntityColFunction.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundEntityColFunction.getReturnType().isCollection()).thenReturn(true);
    when(boundEntityColFunction.getReturnType().getType()).thenReturn(employeeType);
    when(boundEntityColFunction.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundEntityColFunction.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionPPropRtEntitySet() {
    when(boundFunctionPPropRtEntitySet.getName()).thenReturn(BOUND_FUNCTION_PPROP_RT_ENTITY_SET_NAME.getName());
    when(boundFunctionPPropRtEntitySet.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionPPropRtEntitySet.getReturnType().isCollection()).thenReturn(true);
    when(boundFunctionPPropRtEntitySet.getReturnType().getType()).thenReturn(employeeType);
    when(boundFunctionPPropRtEntitySet.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionPPropRtEntitySet.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionEntitySetRtPProp() {
    when(boundFunctionEntitySetRtPProp.getName()).thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_PPROP_NAME.getName());
    when(boundFunctionEntitySetRtPProp.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionEntitySetRtPProp.getReturnType().isCollection()).thenReturn(false);
    EdmPrimitiveType primitiveType = mock(EdmPrimitiveType.class);
    when(boundFunctionEntitySetRtPProp.getReturnType().getType()).thenReturn(primitiveType);
    when(boundFunctionEntitySetRtPProp.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionEntitySetRtPProp.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionEntitySetRtPPropColl() {
    when(boundFunctionEntitySetRtPPropColl.getName())
        .thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_PPROP_COLL_NAME.getName());
    when(boundFunctionEntitySetRtPPropColl.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionEntitySetRtPPropColl.getReturnType().isCollection()).thenReturn(true);
    EdmPrimitiveType primitiveType = mock(EdmPrimitiveType.class);
    when(boundFunctionEntitySetRtPPropColl.getReturnType().getType()).thenReturn(primitiveType);
    when(boundFunctionEntitySetRtPPropColl.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionEntitySetRtPPropColl.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionEntitySetRtCProp() {
    when(boundFunctionEntitySetRtCProp.getName()).thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_CPROP_NAME.getName());
    when(boundFunctionEntitySetRtCProp.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionEntitySetRtCProp.getReturnType().isCollection()).thenReturn(false);
    when(boundFunctionEntitySetRtCProp.getReturnType().getType()).thenReturn(locationType);
    when(boundFunctionEntitySetRtCProp.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionEntitySetRtCProp.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionEntitySetRtCPropColl() {
    when(boundFunctionEntitySetRtCPropColl.getName())
        .thenReturn(BOUND_FUNCTION_ENTITY_SET_RT_CPROP_COLL_NAME.getName());
    when(boundFunctionEntitySetRtCPropColl.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionEntitySetRtCPropColl.getReturnType().isCollection()).thenReturn(true);
    when(boundFunctionEntitySetRtCPropColl.getReturnType().getType()).thenReturn(locationType);
    when(boundFunctionEntitySetRtCPropColl.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionEntitySetRtCPropColl.isBound()).thenReturn(true);
  }

  private void enhanceBoundFunctionSingletonRtEntitySet() {
    when(boundFunctionSingletonRtEntitySet.getName()).thenReturn(BOUND_FUNCTION_SINGLETON_RT_ENTITY_SET_NAME.getName());
    when(boundFunctionSingletonRtEntitySet.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundFunctionSingletonRtEntitySet.getReturnType().isCollection()).thenReturn(true);
    when(boundFunctionSingletonRtEntitySet.getReturnType().getType()).thenReturn(employeeType);
    when(boundFunctionSingletonRtEntitySet.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundFunctionSingletonRtEntitySet.isBound()).thenReturn(true);
  }

  private void enhanceBoundActionPPropRtEntitySet() {
    when(boundActionPpropRtEntitySet.getName()).thenReturn(BOUND_ACTION_PPROP_RT_ENTITY_SET_NAME.getName());
    when(boundActionPpropRtEntitySet.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundActionPpropRtEntitySet.getReturnType().isCollection()).thenReturn(true);
    when(boundActionPpropRtEntitySet.getReturnType().getType()).thenReturn(employeeType);
    when(boundActionPpropRtEntitySet.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundActionPpropRtEntitySet.isBound()).thenReturn(true);
  }

  private void enhanceBoundActionEntityRtEntity() {
    when(boundActionEntityRtEntity.getName()).thenReturn(BOUND_ACTION_ENTITY_RT_ENTITY_NAME.getName());
    when(boundActionEntityRtEntity.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundActionEntityRtEntity.getReturnType().isCollection()).thenReturn(false);
    when(boundActionEntityRtEntity.getReturnType().getType()).thenReturn(employeeType);
    when(boundActionEntityRtEntity.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundActionEntityRtEntity.isBound()).thenReturn(true);
  }

  private void enhanceBoundActionEntityRtPProp() {
    when(boundActionEntityRtPProp.getName()).thenReturn(BOUND_ACTION_ENTITY_RT_PPROP_NAME.getName());
    when(boundActionEntityRtPProp.getReturnType()).thenReturn(mock(EdmReturnType.class));
    EdmPrimitiveType primitiveType = mock(EdmPrimitiveType.class);
    when(boundActionEntityRtPProp.getReturnType().isCollection()).thenReturn(false);
    when(boundActionEntityRtPProp.getReturnType().getType()).thenReturn(primitiveType);
    when(boundActionEntityRtPProp.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundActionEntityRtPProp.isBound()).thenReturn(true);
  }

  private void enhanceBoundActionEntityRtPPropColl() {
    when(boundActionEntityRtPPropColl.getName()).thenReturn(BOUND_ACTION_ENTITY_RT_PPROP_NAME.getName());
    when(boundActionEntityRtPPropColl.getReturnType()).thenReturn(mock(EdmReturnType.class));
    EdmPrimitiveType primitiveType = mock(EdmPrimitiveType.class);
    when(boundActionEntityRtPPropColl.getReturnType().isCollection()).thenReturn(true);
    when(boundActionEntityRtPPropColl.getReturnType().getType()).thenReturn(primitiveType);
    when(boundActionEntityRtPPropColl.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundActionEntityRtPPropColl.isBound()).thenReturn(true);
  }

  private void enhanceBoundActionEntitySetRtCProp() {
    when(boundActionEntitySetRtCProp.getName()).thenReturn(BOUND_ACTION_ENTITY_SET_RT_CPROP_NAME.getName());
    when(boundActionEntitySetRtCProp.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(boundActionEntitySetRtCProp.getReturnType().isCollection()).thenReturn(false);
    when(boundActionEntitySetRtCProp.getReturnType().getType()).thenReturn(locationType);
    when(boundActionEntitySetRtCProp.getNamespace()).thenReturn(NAMESPACE_SCHEMA);
    when(boundActionEntitySetRtCProp.isBound()).thenReturn(true);
  }

  private void enhanceFunction1() {
    when(function1.getName()).thenReturn(FUNCTION1_NAME.getName());
    when(function1.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(function1.getReturnType().isCollection()).thenReturn(false);
    when(function1.getReturnType().getType()).thenReturn(teamType);
  }

  private void enhanceAction1() {
    when(action1.getReturnType()).thenReturn(mock(EdmReturnType.class));
    when(action1.getReturnType().isCollection()).thenReturn(false);
    when(action1.getReturnType().getType()).thenReturn(employeeType);
  }

  private void enhanceCompany() {
    when(company.getName()).thenReturn(COMPANY_SINGLETON_NAME.getName());
    when(company.getEntityContainer()).thenReturn(container1);
    when(company.getEntityType()).thenReturn(companyType);
  }

  private void enhanceManagersEntitySet() {
    when(managersSet.getName()).thenReturn(MANAGERS_SET_NAME.getName());
    when(managersSet.getEntityContainer()).thenReturn(container1);
    when(managersSet.getEntityType()).thenReturn(managerType);
  }

  private void enhanceTeamsEntitySet() {
    when(teamsSet.getName()).thenReturn(TEAMS_SET_NAME.getName());
    when(teamsSet.getEntityContainer()).thenReturn(container1);
    when(teamsSet.getEntityType()).thenReturn(teamType);
  }

  private void enhanceEmployeesEntitySet() {
    when(employeesSet.getName()).thenReturn(EMPLOYEES_SET_NAME.getName());
    when(employeesSet.getEntityContainer()).thenReturn(container1);
    when(employeesSet.getEntityType()).thenReturn(employeeType);
  }

  @Override
  public EdmEntityContainer getEntityContainer(final FullQualifiedName fqn) {

    if (fqn == null || NAMESPACE_SCHEMA.equals(fqn.getNamespace()) && CONTAINER_NAME.equals(fqn.getName())) {
      return container1;
    }

    return null;
  }

  @Override
  public EdmEnumType getEnumType(final FullQualifiedName fqn) {
    if (RATING_ENUM_TYPE_NAME.equals(fqn)) {
      return ratingEnumType;
    }

    return null;
  }

  @Override
  public EdmTypeDefinition getTypeDefinition(final FullQualifiedName fqn) {
    if (TYPE_DEF1_NAME.equals(fqn)) {
      return typeDef1;
    }
    return null;
  }

  @Override
  public EdmEntityType getEntityType(final FullQualifiedName fqn) {
    if (NAMESPACE_SCHEMA.equals(fqn.getNamespace())) {
      if (EMPLOYEES_TYPE_NAME.equals(fqn)) {
        return employeeType;
      } else if (MANAGERS_TYPE_NAME.equals(fqn)) {
        return managerType;
      } else if (TEAMS_TYPE_NAME.equals(fqn)) {
        return teamType;
      } else if (COMPANY_TYPE_NAME.equals(fqn)) {
        return companyType;
      }
    }
    return null;
  }

  @Override
  public EdmComplexType getComplexType(final FullQualifiedName fqn) {
    if (LOCATION_TYPE_NAME.equals(fqn)) {
      return locationType;
    }
    return null;
  }

  @Override
  public EdmServiceMetadata getServiceMetadata() {
    return mock(EdmServiceMetadata.class);
  }

  @Override
  public EdmAction getAction(final FullQualifiedName actionFqn, final FullQualifiedName bindingParameterTypeFqn,
      final Boolean isBindingParameterTypeCollection) {
    if (NAMESPACE_SCHEMA.equals(actionFqn.getNamespace())) {
      if (ACTION1_NAME.equals(actionFqn)) {
        return action1;
      } else if (BOUND_ACTION_PPROP_RT_ENTITY_SET_NAME.equals(actionFqn)
          && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
        return boundActionPpropRtEntitySet;
      } else if (BOUND_ACTION_ENTITY_RT_ENTITY_NAME.equals(actionFqn)
          && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
          && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
        return boundActionEntityRtEntity;
      } else if (BOUND_ACTION_ENTITY_RT_PPROP_NAME.equals(actionFqn)
          && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
          && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
        return boundActionEntityRtPProp;
      } else if (BOUND_ACTION_ENTITY_RT_PPROP_COLL_NAME.equals(actionFqn)
          && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
          && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
        return boundActionEntityRtPPropColl;
      } else if (BOUND_ACTION_ENTITY_SET_RT_CPROP_NAME.equals(actionFqn)
          && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
          && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
        return boundActionEntitySetRtCProp;
      }
    }
    return null;
  }

  @Override
  public EdmFunction getFunction(final FullQualifiedName functionFqn,
      final FullQualifiedName bindingParameterTypeFqn,
      final Boolean isBindingParameterTypeCollection, final List<String> bindingParameterNames) {
    if (functionFqn != null) {
      if (NAMESPACE_SCHEMA.equals(functionFqn.getNamespace())) {
        if (FUNCTION1_NAME.equals(functionFqn)) {
          return function1;
        } else if (FUNCTION_ALL_LOCATIONS_NAME.equals(functionFqn)) {
          return allLocationsFunction;
        } else if (FUNCTION_EMPLOYEE_SEARCH_NAME.equals(functionFqn)) {
          return employeeSearchFunction;
        } else if (FUNCTION_MAXIMAL_AGE_NAME.equals(functionFqn)) {
          return maximalAgeFunction;
        } else if (FUNCTION_MOST_COMMON_LOCATION_NAME.equals(functionFqn)) {
          return mostCommonLocationFunction;
        } else if (FUNCTION_ALL_USED_ROOMS_NAME.equals(functionFqn)) {
          return allUsedRoomIdsFunction;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionEntitySetRtEntity;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_ENTITY_SET_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundEntityColFunction;
        } else if (BOUND_FUNCTION_PPROP_RT_ENTITY_SET_NAME.equals(functionFqn)
            && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionPPropRtEntitySet;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_PPROP_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionEntitySetRtPProp;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_PPROP_COLL_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionEntitySetRtPPropColl;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_CPROP_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionEntitySetRtCProp;
        } else if (BOUND_FUNCTION_ENTITY_SET_RT_CPROP_COLL_NAME.equals(functionFqn)
            && EMPLOYEES_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.TRUE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionEntitySetRtCPropColl;
        } else if (BOUND_FUNCTION_SINGLETON_RT_ENTITY_SET_NAME.equals(functionFqn)
            && COMPANY_TYPE_NAME.equals(bindingParameterTypeFqn)
            && Boolean.FALSE.equals(isBindingParameterTypeCollection)) {
          return boundFunctionSingletonRtEntitySet;
        }
      }
    }
    return null;
  }

}
