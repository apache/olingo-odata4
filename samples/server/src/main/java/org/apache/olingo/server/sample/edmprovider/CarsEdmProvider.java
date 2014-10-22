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
package org.apache.olingo.server.sample.edmprovider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.ODataException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.Target;
import org.apache.olingo.server.api.edm.provider.ComplexType;
import org.apache.olingo.server.api.edm.provider.EdmProvider;
import org.apache.olingo.server.api.edm.provider.EntityContainer;
import org.apache.olingo.server.api.edm.provider.EntityContainerInfo;
import org.apache.olingo.server.api.edm.provider.EntitySet;
import org.apache.olingo.server.api.edm.provider.EntityType;
import org.apache.olingo.server.api.edm.provider.NavigationProperty;
import org.apache.olingo.server.api.edm.provider.NavigationPropertyBinding;
import org.apache.olingo.server.api.edm.provider.Property;
import org.apache.olingo.server.api.edm.provider.PropertyRef;
import org.apache.olingo.server.api.edm.provider.Schema;

public class CarsEdmProvider extends EdmProvider {

  // Service Namespace
  public static final String NAMESPACE = "olingo.odata.sample";

  // EDM Container
  public static final String CONTAINER_NAME = "Container";
  public static final FullQualifiedName CONTAINER_FQN = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

  // Entity Types Names
  public static final FullQualifiedName ET_CAR = new FullQualifiedName(NAMESPACE, "Car");
  public static final FullQualifiedName ET_MANUFACTURER = new FullQualifiedName(NAMESPACE, "Manufacturer");

  // Complex Type Names
  public static final FullQualifiedName CT_ADDRESS = new FullQualifiedName(NAMESPACE, "Address");

  // Entity Set Names
  public static final String ES_CARS_NAME = "Cars";
  public static final String ES_MANUFACTURER_NAME = "Manufacturers";

  @Override
  public EntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {
    if (ET_CAR.equals(entityTypeName)) {
      return new EntityType()
          .setName(ET_CAR.getName())
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("Id")))
          .setProperties(
              Arrays.asList(
                  new Property().setName("Id").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()),
                  new Property().setName("Model").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
                  new Property().setName("ModelYear").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                      .setMaxLength(4),
                  new Property().setName("Price").setType(EdmPrimitiveTypeKind.Decimal.getFullQualifiedName())
                      .setScale(2),
                  new Property().setName("Currency").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                      .setMaxLength(3)
                  )
          ).setNavigationProperties(Arrays.asList(
              new NavigationProperty().setName("Manufacturer").setType(ET_MANUFACTURER)
              )
          );

    } else if (ET_MANUFACTURER.equals(entityTypeName)) {
      return new EntityType()
          .setName(ET_MANUFACTURER.getName())
          .setKey(Arrays.asList(
              new PropertyRef().setPropertyName("Id")))
          .setProperties(Arrays.asList(
              new Property().setName("Id").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()),
              new Property().setName("Name").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
              new Property().setName("Address").setType(CT_ADDRESS))
          ).setNavigationProperties(Arrays.asList(
              new NavigationProperty().setName("Cars").setType(ET_CAR).setCollection(true)
              )
          );
    }

    return null;
  }

  public ComplexType getComplexType(final FullQualifiedName complexTypeName) throws ODataException {
    if (CT_ADDRESS.equals(complexTypeName)) {
      return new ComplexType().setName(CT_ADDRESS.getName()).setProperties(Arrays.asList(
          new Property().setName("Street").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
          new Property().setName("City").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
          new Property().setName("ZipCode").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName()),
          new Property().setName("Country").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
          ));
    }
    return null;
  }

  @Override
  public EntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName)
      throws ODataException {
    if (CONTAINER_FQN.equals(entityContainer)) {
      if (ES_CARS_NAME.equals(entitySetName)) {
        return new EntitySet()
            .setName(ES_CARS_NAME)
            .setType(ET_CAR)
            .setNavigationPropertyBindings(
                Arrays.asList(
                    new NavigationPropertyBinding().setPath("Manufacturer").setTarget(
                        new Target().setTargetName(ES_MANUFACTURER_NAME).setEntityContainer(CONTAINER_FQN))));
      } else if (ES_MANUFACTURER_NAME.equals(entitySetName)) {
        return new EntitySet()
            .setName(ES_MANUFACTURER_NAME)
            .setType(ET_MANUFACTURER).setNavigationPropertyBindings(
                Arrays.asList(
                    new NavigationPropertyBinding().setPath("Cars").setTarget(
                        new Target().setTargetName(ES_CARS_NAME).setEntityContainer(CONTAINER_FQN))));
      }
    }

    return null;
  }

  @Override
  public List<Schema> getSchemas() throws ODataException {
    List<Schema> schemas = new ArrayList<Schema>();
    Schema schema = new Schema();
    schema.setNamespace(NAMESPACE);
    // EntityTypes
    List<EntityType> entityTypes = new ArrayList<EntityType>();
    entityTypes.add(getEntityType(ET_CAR));
    entityTypes.add(getEntityType(ET_MANUFACTURER));
    schema.setEntityTypes(entityTypes);

    // ComplexTypes
    List<ComplexType> complexTypes = new ArrayList<ComplexType>();
    complexTypes.add(getComplexType(CT_ADDRESS));
    schema.setComplexTypes(complexTypes);

    // EntityContainer
    schema.setEntityContainer(getEntityContainer());
    schemas.add(schema);

    return schemas;
  }

  @Override
  public EntityContainer getEntityContainer() throws ODataException {
    EntityContainer container = new EntityContainer();
    container.setName(CONTAINER_FQN.getName());

    // EntitySets
    List<EntitySet> entitySets = new ArrayList<EntitySet>();
    container.setEntitySets(entitySets);
    entitySets.add(getEntitySet(CONTAINER_FQN, ES_CARS_NAME));
    entitySets.add(getEntitySet(CONTAINER_FQN, ES_MANUFACTURER_NAME));

    return container;
  }

  @Override
  public EntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {
    if (entityContainerName == null || CONTAINER_FQN.equals(entityContainerName)) {
      return new EntityContainerInfo().setContainerName(CONTAINER_FQN);
    }
    return null;
  }
}
