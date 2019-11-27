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
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlNavigationPropertyBinding;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;

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
  
  public static final String ET_SUPPLIER_NAME = "Supplier";
  public static final FullQualifiedName ET_SUPPLIER_FQN = new FullQualifiedName(NAMESPACE, ET_SUPPLIER_NAME);
  
  public static final String CT_ADDRESS_NAME = "Address";
  public static final FullQualifiedName CT_ADDRESS_FQN = new FullQualifiedName(NAMESPACE, CT_ADDRESS_NAME);

  // Entity Set Names
  public static final String ES_PRODUCTS_NAME = "Products";
  public static final String ES_CATEGORIES_NAME = "Categories";
  public static final String ES_SUPPLIERS_NAME = "Suppliers";

  @Override
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

      // create PropertyRef for Key element
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("ID");

      // navigation property: many-to-one, null not allowed (product must have a category)
      CsdlNavigationProperty navProp = new CsdlNavigationProperty().setName("Category")
          .setType(ET_CATEGORY_FQN).setNullable(false).setPartner("Products");
      CsdlNavigationProperty contNavProp = new CsdlNavigationProperty().setName("Suppliers")
          .setType(ET_SUPPLIER_FQN).setContainsTarget(true).setCollection(true);
      List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
      navPropList.add(navProp);
      navPropList.add(contNavProp);

      // configure EntityType
      entityType = new CsdlEntityType();
      entityType.setName(ET_PRODUCT_NAME);
      entityType.setProperties(Arrays.asList(id, name, description));
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
          .setType(ET_PRODUCT_FQN).setCollection(true).setPartner("Category");
      List<CsdlNavigationProperty> navPropList = new ArrayList<CsdlNavigationProperty>();
      navPropList.add(navProp);

      // configure EntityType
      entityType = new CsdlEntityType();
      entityType.setName(ET_CATEGORY_NAME);
      entityType.setProperties(Arrays.asList(id, name));
      entityType.setKey(Arrays.asList(propertyRef));
      entityType.setNavigationProperties(navPropList);
    } else if (entityTypeName.equals(ET_SUPPLIER_FQN)) {
      // create EntityType properties
      CsdlProperty supplierId = new CsdlProperty().setName("SupplierID")
          .setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
      CsdlProperty companyName = new CsdlProperty().setName("CompanyName")
          .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty fax = new CsdlProperty().setName("Fax")
          .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
      CsdlProperty address = new CsdlProperty().setName(CT_ADDRESS_NAME)
          .setType(CT_ADDRESS_FQN);

      // create PropertyRef for Key element
      CsdlPropertyRef propertyRef = new CsdlPropertyRef();
      propertyRef.setName("SupplierID");

      // configure EntityType
      entityType = new CsdlEntityType();
      entityType.setName(ET_SUPPLIER_NAME);
      entityType.setProperties(Arrays.asList(supplierId, companyName, fax, address));
      entityType.setKey(Arrays.asList(propertyRef));
    }

    return entityType;

  }
  
  @Override
  public CsdlComplexType getComplexType(final FullQualifiedName complexTypeName) {
    CsdlComplexType complexType = null;
    if (complexTypeName.equals(CT_ADDRESS_FQN)) {
      complexType = new CsdlComplexType().setName(CT_ADDRESS_NAME)
          .setProperties(Arrays.asList(
              new CsdlProperty()
                  .setName("City")
                  .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
              new CsdlProperty()
              .setName("Country")
              .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())));
    }
    return complexType;
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

  @Override
  public List<CsdlSchema> getSchemas() {
    // create Schema
    CsdlSchema schema = new CsdlSchema();
    schema.setNamespace(NAMESPACE);

    // add EntityTypes
    List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
    entityTypes.add(getEntityType(ET_PRODUCT_FQN));
    entityTypes.add(getEntityType(ET_CATEGORY_FQN));
    entityTypes.add(getEntityType(ET_SUPPLIER_FQN));
    schema.setEntityTypes(entityTypes);

    // add Complex Types
    List<CsdlComplexType> complexTypes = new ArrayList<CsdlComplexType>();
    complexTypes.add(getComplexType(CT_ADDRESS_FQN));
    schema.setComplexTypes(complexTypes);
    
    // add EntityContainer
    schema.setEntityContainer(getEntityContainer());

    // finally
    List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
    schemas.add(schema);

    return schemas;
  }

  @Override
  public CsdlEntityContainer getEntityContainer() {

    // create EntitySets
    List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
    entitySets.add(getEntitySet(CONTAINER, ES_PRODUCTS_NAME));
    entitySets.add(getEntitySet(CONTAINER, ES_CATEGORIES_NAME));

    // create EntityContainer
    CsdlEntityContainer entityContainer = new CsdlEntityContainer();
    entityContainer.setName(CONTAINER_NAME);
    entityContainer.setEntitySets(entitySets);

    return entityContainer;
  }
}
