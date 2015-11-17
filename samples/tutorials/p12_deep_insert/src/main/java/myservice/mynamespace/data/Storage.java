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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.UriResourceNavigation;

import myservice.mynamespace.service.DemoEdmProvider;
import myservice.mynamespace.util.Util;

public class Storage {

  private OData odata;
  private Edm edm;
  
  final private TransactionalEntityManager manager;
  
  public Storage(final OData odata, final Edm edm) {
    
    this.odata = odata;
    this.edm = edm;
    this.manager = new TransactionalEntityManager(edm);
    
    initProductSampleData();
    initCategorySampleData();
    linkProductsAndCategories();
  }

  /* PUBLIC FACADE */
  
  public void beginTransaction() throws ODataApplicationException {
    manager.beginTransaction();
  }

  public void rollbackTransaction() throws ODataApplicationException {
    manager.rollbackTransaction();
  }
  
  public void commitTransaction() throws ODataApplicationException {
    manager.commitTransaction();
  }
  
  public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntityCollection(manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntityCollection(manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    }

    return null;
  }

  public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    }

    return null;
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
   * */
  public void updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams, Entity updateEntity,
      HttpMethod httpMethod) throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, 
          manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, 
          manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    }
  }

  public void deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      deleteEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME));
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      deleteEntity(edmEntityType, keyParams, manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME));
    }
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
    final Link link = entity.getNavigationLink(navigationResource.getProperty().getName());
    return link == null ? new EntityCollection() : link.getInlineEntitySet();
  }
  
  /* INTERNAL */

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
      throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
    }

    entityList.remove(entity);
  }
  
  /* HELPER */

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
  
  private void linkProductsAndCategories() {
    final List<Entity> productList = manager.getEntityCollection(DemoEdmProvider.ES_PRODUCTS_NAME);
    final List<Entity> categoryList = manager.getEntityCollection(DemoEdmProvider.ES_CATEGORIES_NAME);
    
    setLink(productList.get(0), "Category", categoryList.get(0));
    setLink(productList.get(1), "Category", categoryList.get(0));
    setLink(productList.get(2), "Category", categoryList.get(1));
    setLink(productList.get(3), "Category", categoryList.get(1));
    setLink(productList.get(4), "Category", categoryList.get(2));
    setLink(productList.get(5), "Category", categoryList.get(2));
    
    setLinks(categoryList.get(0), "Products", productList.subList(0, 2).toArray(new Entity[0]));
    setLinks(categoryList.get(1), "Products", productList.subList(2, 4).toArray(new Entity[0]));
    setLinks(categoryList.get(2), "Products", productList.subList(4, 6).toArray(new Entity[0]));
  }
  
  private URI createId(Entity entity, String idPropertyName) {
    return createId(entity, idPropertyName, null);
  }

  private URI createId(Entity entity, String idPropertyName, String navigationName) {
    try {
      StringBuilder sb = new StringBuilder(getEntitySetName(entity)).append("(");
      final Property property = entity.getProperty(idPropertyName);
      sb.append(property.asPrimitive()).append(")");
      if(navigationName != null) {
        sb.append("/").append(navigationName);
      }
      return new URI(sb.toString());
    } catch (URISyntaxException e) {
      throw new ODataRuntimeException("Unable to create (Atom) id for entity: " + entity, e);
    }
  }

  private String getEntitySetName(Entity entity) {
    if(DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_CATEGORIES_NAME;
    } else if(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_PRODUCTS_NAME;
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
