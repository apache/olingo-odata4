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
package myservice.mynamespace.data;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceFunction;
import org.apache.olingo.server.api.uri.UriResourceNavigation;

import myservice.mynamespace.service.DemoEdmProvider;
import myservice.mynamespace.util.Util;

public class Storage {

  /** Special property to store the media content **/
  private static final String MEDIA_PROPERTY_NAME = "$value";
  
  final private TransactionalEntityManager manager;
  final private Edm edm;
  final private OData odata;
  
  // represent our database
  public Storage(final OData odata, final Edm edm) {
    this.odata = odata;
    this.edm = edm;
    manager = new TransactionalEntityManager(edm);
    
    final List<Entity> productList = manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME);
    
    // creating some sample data
    initProductSampleData();
    initCategorySampleData();
    initAdvertisementSampleData();
    
    linkProductsAndCategories(productList.size());
  }

  /* PUBLIC FACADE */
  
  public void beginTransaction() {
    manager.beginTransaction();
  }

  public void rollbackTranscation() {
    manager.rollbackTransaction();
  }

  public void commitTransaction() {
    manager.commitTransaction();
  }
  
  public Entity readFunctionImportEntity(final UriResourceFunction uriResourceFunction,
      final ServiceMetadata serviceMetadata) throws ODataApplicationException {

    final EntityCollection entityCollection = readFunctionImportCollection(uriResourceFunction, serviceMetadata);
    final EdmEntityType edmEntityType = (EdmEntityType) uriResourceFunction.getFunction().getReturnType().getType();

    return Util.findEntity(edmEntityType, entityCollection, uriResourceFunction.getKeyPredicates());
  }

  public EntityCollection readFunctionImportCollection(final UriResourceFunction uriResourceFunction,
      final ServiceMetadata serviceMetadata) throws ODataApplicationException {

    if (DemoEdmProvider.FUNCTION_COUNT_CATEGORIES.equals(uriResourceFunction.getFunctionImport().getName())) {
      // Get the parameter of the function
      final UriParameter parameterAmount = uriResourceFunction.getParameters().get(0);
      // Try to convert the parameter to an Integer.
      // We have to take care, that the type of parameter fits to its EDM declaration
      int amount;
      try {
        amount = Integer.parseInt(parameterAmount.getText());
      } catch (NumberFormatException e) {
        throw new ODataApplicationException("Type of parameter Amount must be Edm.Int32", HttpStatusCode.BAD_REQUEST
            .getStatusCode(), Locale.ENGLISH);
      }

      final List<Entity> resultEntityList = new ArrayList<Entity>();

      // Loop over all categories and check how many products are linked
      for (final Entity category : manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME)) {
        final EntityCollection products = getRelatedEntityCollection(category, DemoEdmProvider.NAV_TO_PRODUCTS);
        if (products.getEntities().size() == amount) {
          resultEntityList.add(category);
        }
      }

      final EntityCollection resultCollection = new EntityCollection();
      resultCollection.getEntities().addAll(resultEntityList);
      return resultCollection;
    } else {
      throw new ODataApplicationException("Function not implemented", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(),
          Locale.ROOT);
    }
  }

  public void resetDataSet() {
    resetDataSet(Integer.MAX_VALUE);
  }

  public void resetDataSet(final int amount) {
    // Replace the old lists with empty ones
    manager.clear();
    
    // Create new sample data
    initProductSampleData();
    initCategorySampleData();
    
    final List<Entity> productList = manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME);
    final List<Entity> categoryList = manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME);

    // Truncate the lists
    if (amount < productList.size()) {
      final List<Entity> newProductList = new ArrayList<Entity>(productList.subList(0, amount));
      productList.clear();
      productList.addAll(newProductList);
      // Products 0, 1 are linked to category 0
      // Products 2, 3 are linked to category 1
      // Products 4, 5 are linked to category 2
      final List<Entity> newCategoryList = new ArrayList<Entity>(categoryList.subList(0, (amount / 2) + 1));
      categoryList.clear();
      categoryList.addAll(newCategoryList);
    }
    
    linkProductsAndCategories(amount);
  }

  public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntityCollection(manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if (edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntityCollection(manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      return getEntityCollection(manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME));
    }

    return null;
  }

  public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if (edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      return getEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME));
    }

    return null;
  }

  // Navigation
  
  public Entity getRelatedEntity(Entity entity, UriResourceNavigation navigationResource) 
      throws ODataApplicationException {
    
    final EdmNavigationProperty edmNavigationProperty = navigationResource.getProperty();
    
    if(edmNavigationProperty.isCollection()) {
      return Util.findEntity(edmNavigationProperty.getType(), getRelatedEntityCollection(entity, navigationResource), 
         navigationResource.getKeyPredicates());
    } else {
      final Link link = entity.getNavigationLink(edmNavigationProperty.getName());
      return link == null ? null : link.getInlineEntity();
    }
  }
  
  public EntityCollection getRelatedEntityCollection(Entity entity, UriResourceNavigation navigationResource) {
    return getRelatedEntityCollection(entity, navigationResource.getProperty().getName());
  }
  
  public EntityCollection getRelatedEntityCollection(Entity entity, String navigationPropertyName) {
    final Link link = entity.getNavigationLink(navigationPropertyName);
    return link == null ? new EntityCollection() : link.getInlineEntitySet();
  }
  
  public Entity createEntityData(EdmEntitySet edmEntitySet, Entity entityToCreate, String rawServiceUri) 
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return createEntity(edmEntitySet, edmEntityType, entityToCreate, 
          manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME), rawServiceUri);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return createEntity(edmEntitySet, edmEntityType, entityToCreate, 
          manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME), rawServiceUri);
    }

    return null;
  }

  /**
   * This method is invoked for PATCH or PUT requests
   */
  public void updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams, Entity updateEntity,
      HttpMethod httpMethod) throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    
    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, 
          manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if (edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, 
          manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, 
          manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME));
    }
  }

  public void deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      deleteEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if (edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      deleteEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      deleteEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME));
    }
  }
  
  public byte[] readMedia(final Entity entity) {
    return (byte[]) entity.getProperty(MEDIA_PROPERTY_NAME).asPrimitive();
  }
  
  public void updateMedia(final Entity entity, final String mediaContentType, final byte[] data) {
    entity.getProperties().remove(entity.getProperty(MEDIA_PROPERTY_NAME));
    entity.addProperty(new Property(null, MEDIA_PROPERTY_NAME, ValueType.PRIMITIVE, data));
    entity.setMediaContentType(mediaContentType);
  }
  
  public Entity createMediaEntity(final EdmEntityType edmEntityType, final String mediaContentType, 
      final byte[] data) {
    Entity entity = null;
    
    if(edmEntityType.getName().equals(DemoEdmProvider.ET_ADVERTISEMENT_NAME)) {
      entity = new Entity();
      entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, UUID.randomUUID()));
      entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, null));
      entity.addProperty(new Property(null, "AirDate", ValueType.PRIMITIVE, null));
      
      entity.setMediaContentType(mediaContentType);
      entity.addProperty(new Property(null, MEDIA_PROPERTY_NAME, ValueType.PRIMITIVE, data));
      
      manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME).add(entity);
    }
    
    return entity;
  }
  
  /* INTERNAL */

  private Entity createEntity(EdmEntitySet edmEntitySet, EdmEntityType edmEntityType, Entity entity, 
      List<Entity> entityList, final String rawServiceUri) throws ODataApplicationException {
    
    // 1.) Create the entity
    final Entity newEntity = new Entity();
    newEntity.setType(entity.getType());
    
    // Create the new key of the entity
    int newId = 1;
    while (entityIdExists(newId, entityList)) {
      newId++;
    }
    
    // Add all provided properties
    newEntity.getProperties().addAll(entity.getProperties());
    
    // Add the key property
    newEntity.getProperties().add(new Property(null, "ID", ValueType.PRIMITIVE, newId));
    newEntity.setId(createId(newEntity, "ID"));
    
    // 2.1.) Apply binding links
    for(final Link link : entity.getNavigationBindings()) {
      final EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(link.getTitle());
      final EdmEntitySet targetEntitySet = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(link.getTitle());
      
      if(edmNavigationProperty.isCollection() && link.getBindingLinks() != null) {
        for(final String bindingLink : link.getBindingLinks()) {
          final Entity relatedEntity = readEntityByBindingLink(bindingLink, targetEntitySet, rawServiceUri);
          createLink(edmNavigationProperty, newEntity, relatedEntity);
        }
      } else if(!edmNavigationProperty.isCollection() && link.getBindingLink() != null) {
        final Entity relatedEntity = readEntityByBindingLink(link.getBindingLink(), targetEntitySet, rawServiceUri);
        createLink(edmNavigationProperty, newEntity, relatedEntity);
      }
    }
    
    // 2.2.) Create nested entities
    for(final Link link : entity.getNavigationLinks()) {
      final EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(link.getTitle());
      final EdmEntitySet targetEntitySet = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(link.getTitle());
      
      if(edmNavigationProperty.isCollection() && link.getInlineEntitySet() != null) {
        for(final Entity nestedEntity : link.getInlineEntitySet().getEntities()) {
          final Entity newNestedEntity = createEntityData(targetEntitySet, nestedEntity, rawServiceUri);
          createLink(edmNavigationProperty, newEntity, newNestedEntity);
        }
      } else if(!edmNavigationProperty.isCollection() && link.getInlineEntity() != null){
        final Entity newNestedEntity = createEntityData(targetEntitySet, link.getInlineEntity(), rawServiceUri);
        createLink(edmNavigationProperty, newEntity, newNestedEntity);
      }
    }
    
    entityList.add(newEntity);
  
    return newEntity;
  }

  private Entity readEntityByBindingLink(final String entityId, final EdmEntitySet edmEntitySet, 
      final String rawServiceUri) throws ODataApplicationException {
    
    UriResourceEntitySet entitySetResource = null;
    try {
      entitySetResource = odata.createUriHelper().parseEntityId(edm, entityId, rawServiceUri);
      
      if(!entitySetResource.getEntitySet().getName().equals(edmEntitySet.getName())) {
        throw new ODataApplicationException("Execpted an entity-id for entity set " + edmEntitySet.getName() 
          + " but found id for entity set " + entitySetResource.getEntitySet().getName(), 
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
      }
    } catch (DeserializerException e) {
      throw new ODataApplicationException(entityId + " is not a valid entity-Id", 
          HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
    }

    return readEntityData(entitySetResource.getEntitySet(), entitySetResource.getKeyPredicates());
  }
  
  private EntityCollection getEntityCollection(final List<Entity> entityList) {

    EntityCollection retEntitySet = new EntityCollection();
    retEntitySet.getEntities().addAll(entityList);

    return retEntitySet;
  }

  private Entity getEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams, List<Entity> entityList)
      throws ODataApplicationException {

    // the list of entities at runtime
    EntityCollection entitySet = getEntityCollection(entityList);

    /* generic approach to find the requested entity */
    Entity requestedEntity = Util.findEntity(edmEntityType, entitySet, keyParams);

    if (requestedEntity == null) {
      // this variable is null if our data doesn't contain an entity for the requested key
      // Throw suitable exception
      throw new ODataApplicationException("Entity for requested key doesn't exist",
          HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
    }

    return requestedEntity;
  }

  private boolean entityIdExists(int id, List<Entity> entityList) {

    for (Entity entity : entityList) {
      Integer existingID = (Integer) entity.getProperty("ID").getValue();
      if (existingID.intValue() == id) {
        return true;
      }
    }

    return false;
  }
  
  private void updateEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams, Entity updateEntity,
      HttpMethod httpMethod, List<Entity> entityList) throws ODataApplicationException {
    
    Entity entity = getEntity(edmEntityType, keyParams, entityList);
    if (entity == null) {
      throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
    }

    // loop over all properties and replace the values with the values of the given payload
    // Note: ignoring ComplexType, as we don't have it in our odata model
    List<Property> existingProperties = entity.getProperties();
    for (Property existingProp : existingProperties) {
      String propName = existingProp.getName();

      // ignore the key properties, they aren't updateable
      if (isKey(edmEntityType, propName)) {
        continue;
      }

      Property updateProperty = updateEntity.getProperty(propName);
      // the request payload might not consider ALL properties, so it can be null
      if (updateProperty == null) {
        // if a property has NOT been added to the request payload
        // depending on the HttpMethod, our behavior is different
        if (httpMethod.equals(HttpMethod.PATCH)) {
          // as of the OData spec, in case of PATCH, the existing property is not touched
          continue; // do nothing
        } else if (httpMethod.equals(HttpMethod.PUT)) {
          // as of the OData spec, in case of PUT, the existing property is set to null (or to default value)
          existingProp.setValue(existingProp.getValueType(), null);
          continue;
        }
      }

      // change the value of the properties
      existingProp.setValue(existingProp.getValueType(), updateProperty.getValue());
    }
  }
  
  private void deleteEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams, List<Entity> entityList) 
      throws ODataApplicationException {
    
    Entity entity = getEntity(edmEntityType, keyParams, entityList);
    if (entity == null) {
      throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(), 
          Locale.ENGLISH);
    }

    entityList.remove(entity);
  }
  
  private boolean isKey(EdmEntityType edmEntityType, String propertyName) {
    List<EdmKeyPropertyRef> keyPropertyRefs = edmEntityType.getKeyPropertyRefs();
    for (EdmKeyPropertyRef propRef : keyPropertyRefs) {
      String keyPropertyName = propRef.getName();
      if (keyPropertyName.equals(propertyName)) {
        return true;
      }
    }
    return false;
  }

  private void initProductSampleData() {
    final List<Entity> productList = manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME);
    Entity entity = new Entity();

    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 0));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Basic 15"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "Notebook Basic, 1.7GHz - 15 XGA - 1024MB DDR2 SDRAM - 40GB"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);

    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebook Professional 17"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "Notebook Professional, 2.8GHz - 15 XGA - 8GB DDR3 RAM - 500GB"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);

    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "1UMTS PDA"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "Ultrafast 3G UMTS/HSDPA Pocket PC, supports GSM network"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);

    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 3));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Comfort Easy"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "32 GB Digital Assitant with high-resolution color screen"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);

    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 4));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Ergo Screen"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "19 Optimum Resolution 1024 x 768 @ 85Hz, resolution 1280 x 960"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);

    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 5));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Flat Basic"));
    entity.addProperty(new Property(null, "Description", ValueType.PRIMITIVE,
        "Optimum Hi-Resolution max. 1600 x 1200 @ 85Hz, Dot Pitch: 0.24mm"));
    entity.setType(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    productList.add(entity);
  }

  private void initCategorySampleData() {
    final List<Entity> categoryList = manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME);
    Entity entity = new Entity();
    
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 0));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Notebooks"));
    entity.setType(DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    categoryList.add(entity);
  
    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 1));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Organizers"));
    entity.setType(DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    categoryList.add(entity);
  
    entity = new Entity();
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE, 2));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Monitors"));
    entity.setType(DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString());
    entity.setId(createId(entity, "ID"));
    categoryList.add(entity);
  }
  
private void initAdvertisementSampleData() {
    final List<Entity> advertisements = manager.getEntityCollection(DemoEdmProvider.ES_ADVERTISEMENTS_NAME);

    Entity entity = new Entity();
    entity.setType(DemoEdmProvider.ET_ADVERTISEMENT_FQN.getFullQualifiedNameAsString());
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE,
        UUID.fromString("f89dee73-af9f-4cd4-b330-db93c25ff3c7")));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Old School Lemonade Store, Retro Style"));
    entity.addProperty(new Property(null, "AirDate", ValueType.PRIMITIVE, Timestamp.valueOf("2012-11-07 00:00:00")));
    entity.addProperty(new Property(null, MEDIA_PROPERTY_NAME, ValueType.PRIMITIVE, "Super content".getBytes()));
    entity.setMediaContentType(ContentType.parse("text/plain").toContentTypeString());
    entity.setId(createId(entity, "ID"));
    advertisements.add(entity);

    entity = new Entity();
    entity.setType(DemoEdmProvider.ET_ADVERTISEMENT_FQN.getFullQualifiedNameAsString());
    entity.addProperty(new Property(null, "ID", ValueType.PRIMITIVE,
        UUID.fromString("db2d2186-1c29-4d1e-88ef-a127f521b9c67")));
    entity.addProperty(new Property(null, "Name", ValueType.PRIMITIVE, "Early morning start, need coffee"));
    entity.addProperty(new Property(null, "AirDate", ValueType.PRIMITIVE, Timestamp.valueOf("2000-02-29 00:00:00")));
    entity.addProperty(new Property(null, MEDIA_PROPERTY_NAME, ValueType.PRIMITIVE, "Super content2".getBytes()));
    entity.setMediaContentType(ContentType.parse("text/plain").toContentTypeString());
    entity.setId(createId(entity, "ID"));
    advertisements.add(entity);
  }
  
  private void linkProductsAndCategories(final int numberOfProducts) {
    final List<Entity> productList = manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME);
    final List<Entity> categoryList = manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME);
    
    if(numberOfProducts >= 1) {
      setLink(productList.get(0), "Category", categoryList.get(0));
    }
    if(numberOfProducts >= 2) {
      setLink(productList.get(1), "Category", categoryList.get(0));
    }
    if(numberOfProducts >= 3) {
      setLink(productList.get(2), "Category", categoryList.get(1));
    }
    if(numberOfProducts >= 4) {
      setLink(productList.get(3), "Category", categoryList.get(1));
    }
    if(numberOfProducts >= 5) {
      setLink(productList.get(4), "Category", categoryList.get(2));
    }
    if(numberOfProducts >= 6) {
      setLink(productList.get(5), "Category", categoryList.get(2));
    }
    
    if (numberOfProducts >= 1) {
      setLinks(categoryList.get(0), "Products",
          productList.subList(0, Math.min(2, numberOfProducts)).toArray(new Entity[0]));
    }
    if (numberOfProducts >= 3) {
      setLinks(categoryList.get(1), "Products",
          productList.subList(2, Math.min(4, numberOfProducts)).toArray(new Entity[0]));
    }
    if (numberOfProducts >= 5) {
      setLinks(categoryList.get(2), "Products",
          productList.subList(4, Math.min(6, numberOfProducts)).toArray(new Entity[0]));
    }
  }

  private URI createId(Entity entity, String idPropertyName) {
    return createId(entity, idPropertyName, null);
  }

  private URI createId(Entity entity, String idPropertyName, String navigationName) {
    try {
      StringBuilder sb = new StringBuilder(getEntitySetName(entity)).append("(");
      final Property property = entity.getProperty(idPropertyName);
      sb.append(property.asPrimitive()).append(")");
      if (navigationName != null) {
        sb.append("/").append(navigationName);
      }
      return new URI(sb.toString());
    } catch (URISyntaxException e) {
      throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
    }
  }

  private String getEntitySetName(Entity entity) {
    if (DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_CATEGORIES_NAME;
    } else if (DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_PRODUCTS_NAME;
    } else if (DemoEdmProvider.ET_ADVERTISEMENT_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_ADVERTISEMENTS_NAME;
    }
    return entity.getType();
  }
  
  private void createLink(final EdmNavigationProperty navigationProperty, final Entity srcEntity,
      final Entity destEntity) {
    setLink(navigationProperty, srcEntity, destEntity);

    final EdmNavigationProperty partnerNavigationProperty = navigationProperty.getPartner();
    if (partnerNavigationProperty != null) {
      setLink(partnerNavigationProperty, destEntity, srcEntity);
    }
  }
  
  private void setLink(final EdmNavigationProperty navigationProperty, final Entity srcEntity,
      final Entity targetEntity) {
    if (navigationProperty.isCollection()) {
      setLinks(srcEntity, navigationProperty.getName(), targetEntity);
    } else {
      setLink(srcEntity, navigationProperty.getName(), targetEntity);
    }
  }
  
  private void setLink(final Entity entity, final String navigationPropertyName, final Entity target) {
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      link.setHref(target.getId().toASCIIString());
      
      entity.getNavigationLinks().add(link);
    }
    link.setInlineEntity(target);
  }

  private void setLinks(final Entity entity, final String navigationPropertyName, final Entity... targets) {
    if(targets.length == 0) {
      return;
    }
    
    Link link = entity.getNavigationLink(navigationPropertyName);
    if (link == null) {
      link = new Link();
      link.setRel(Constants.NS_NAVIGATION_LINK_REL + navigationPropertyName);
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      link.setTitle(navigationPropertyName);
      link.setHref(entity.getId().toASCIIString() + "/" + navigationPropertyName);

      EntityCollection target = new EntityCollection();
      target.getEntities().addAll(Arrays.asList(targets));
      link.setInlineEntitySet(target);
      
      entity.getNavigationLinks().add(link);
    } else {
      link.getInlineEntitySet().getEntities().addAll(Arrays.asList(targets));
    }
  }
}
