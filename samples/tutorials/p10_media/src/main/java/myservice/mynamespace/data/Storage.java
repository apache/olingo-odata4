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
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import myservice.mynamespace.service.DemoEdmProvider;
import myservice.mynamespace.util.Util;

public class Storage {
  /** Special property to store the media content **/
  private static final String MEDIA_PROPERTY_NAME = "$value";

  private List<Entity> productList;
  private List<Entity> categoryList;
  private List<Entity> advertisements;
  
  
  public Storage() {
    
    productList = new ArrayList<Entity>();
    categoryList = new ArrayList<Entity>();
    advertisements = new ArrayList<Entity>();
    
    initProductSampleData();
    initCategorySampleData();
    initAdvertisementSampleData();
  }

  /* PUBLIC FACADE */
  
  public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) throws ODataApplicationException {

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntityCollection(productList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntityCollection(categoryList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      return getEntityCollection(advertisements);
    }

    return null;
  }

  public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    
    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return getEntity(edmEntityType, keyParams, productList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return getEntity(edmEntityType, keyParams, categoryList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      return getEntity(edmEntityType, keyParams, advertisements);
    }

    return null;
  }

  public Entity createEntityData(EdmEntitySet edmEntitySet, Entity entityToCreate) {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      return createEntity(edmEntityType, entityToCreate, productList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      return createEntity(edmEntityType, entityToCreate, categoryList);
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
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, productList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, categoryList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      updateEntity(edmEntityType, keyParams, updateEntity, httpMethod, advertisements);
    }
  }

  public void deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
      throws ODataApplicationException {

    EdmEntityType edmEntityType = edmEntitySet.getEntityType();

    if (edmEntitySet.getName().equals(DemoEdmProvider.ES_PRODUCTS_NAME)) {
      deleteEntity(edmEntityType, keyParams, productList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_CATEGORIES_NAME)) {
      deleteEntity(edmEntityType, keyParams, categoryList);
    } else if(edmEntitySet.getName().equals(DemoEdmProvider.ES_ADVERTISEMENTS_NAME)) {
      deleteEntity(edmEntityType, keyParams, advertisements);
    }
  }
  
  // Navigation
  public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType) {
    EntityCollection collection = getRelatedEntityCollection(entity, relatedEntityType);
    if (collection.getEntities().isEmpty()) {
      return null;
    }
    return collection.getEntities().get(0);
  }

  public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType, List<UriParameter> keyPredicates) 
      throws ODataApplicationException {

    EntityCollection relatedEntities = getRelatedEntityCollection(entity, relatedEntityType);
    return Util.findEntity(relatedEntityType, relatedEntities, keyPredicates);
  }

  public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) {
    EntityCollection navigationTargetEntityCollection = new EntityCollection();

    FullQualifiedName relatedEntityFqn = targetEntityType.getFullQualifiedName();
    String sourceEntityFqn = sourceEntity.getType();

    if (sourceEntityFqn.equals(DemoEdmProvider.ET_PRODUCT_FQN.getFullQualifiedNameAsString())
        && relatedEntityFqn.equals(DemoEdmProvider.ET_CATEGORY_FQN)) {
      // relation Products->Category (result all categories)
      int productID = (Integer) sourceEntity.getProperty("ID").getValue();
      if (productID == 0 || productID == 1) {
        navigationTargetEntityCollection.getEntities().add(categoryList.get(0));
      } else if (productID == 2 || productID == 3) {
        navigationTargetEntityCollection.getEntities().add(categoryList.get(1));
      } else if (productID == 4 || productID == 5) {
        navigationTargetEntityCollection.getEntities().add(categoryList.get(2));
      }
    } else if (sourceEntityFqn.equals(DemoEdmProvider.ET_CATEGORY_FQN.getFullQualifiedNameAsString())
        && relatedEntityFqn.equals(DemoEdmProvider.ET_PRODUCT_FQN)) {
      // relation Category->Products (result all products)
      int categoryID = (Integer) sourceEntity.getProperty("ID").getValue();
      if (categoryID == 0) {
        // the first 2 products are notebooks
        navigationTargetEntityCollection.getEntities().addAll(productList.subList(0, 2));
      } else if (categoryID == 1) {
        // the next 2 products are organizers
        navigationTargetEntityCollection.getEntities().addAll(productList.subList(2, 4));
      } else if (categoryID == 2) {
        // the first 2 products are monitors
        navigationTargetEntityCollection.getEntities().addAll(productList.subList(4, 6));
      }
    }

    return navigationTargetEntityCollection;
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
      
      advertisements.add(entity);
    }
    
    return entity;
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
  
  private Entity createEntity(EdmEntityType edmEntityType, Entity entity, List<Entity> entityList) {
    
    // the ID of the newly created entity is generated automatically
    int newId = 1;
    while (entityIdExists(newId, entityList)) {
      newId++;
    }
  
    Property idProperty = entity.getProperty("ID");
    if (idProperty != null) {
      idProperty.setValue(ValueType.PRIMITIVE, Integer.valueOf(newId));
    } else {
      // as of OData v4 spec, the key property can be omitted from the POST request body
      entity.getProperties().add(new Property(null, "ID", ValueType.PRIMITIVE, newId));
    }
    entity.setId(createId(entity, "ID"));
    entityList.add(entity);
  
    return entity;
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
      throw new ODataApplicationException("Entity not found", HttpStatusCode.NOT_FOUND.getStatusCode(), 
          Locale.ENGLISH);
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
    } else if (DemoEdmProvider.ET_ADVERTISEMENT_FQN.getFullQualifiedNameAsString().equals(entity.getType())) {
      return DemoEdmProvider.ES_ADVERTISEMENTS_NAME;
    }
    return entity.getType();
  }
}
