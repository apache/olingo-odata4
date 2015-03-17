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
package org.apache.olingo.server.tecsvc.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt16;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt32;
import org.apache.olingo.commons.core.edm.primitivetype.EdmInt64;
import org.apache.olingo.commons.core.edm.primitivetype.EdmString;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.uri.UriParameter;

public class DataProvider {

  protected static final String MEDIA_PROPERTY_NAME = "$value";

  final private Map<String, EntitySet> data;
  private Edm edm;
  private OData odata;

  public DataProvider() {
    data = new DataCreator().getData();
  }

  public EntitySet readAll(final EdmEntitySet edmEntitySet) throws DataProviderException {
    return data.get(edmEntitySet.getName());
  }

  public Entity read(final EdmEntitySet edmEntitySet, final List<UriParameter> keys) throws DataProviderException {
    final EntitySet entitySet = readAll(edmEntitySet);
    return entitySet == null ? null : read(edmEntitySet.getEntityType(), entitySet, keys);
  }

  public Entity read(final EdmEntityType edmEntityType, final EntitySet entitySet, final List<UriParameter> keys)
      throws DataProviderException {
    try {
      for (final Entity entity : entitySet.getEntities()) {
        boolean found = true;
        for (final UriParameter key : keys) {
          final EdmProperty property = (EdmProperty) edmEntityType.getProperty(key.getName());
          final EdmPrimitiveType type = (EdmPrimitiveType) property.getType();
          final Object value = entity.getProperty(key.getName()).getValue();
          final Object keyValue = type.valueOfString(type.fromUriLiteral(key.getText()),
              property.isNullable(), property.getMaxLength(), property.getPrecision(), property.getScale(),
              property.isUnicode(),
              Calendar.class.isAssignableFrom(value.getClass()) ? Calendar.class : value.getClass());
          if (!value.equals(keyValue)) {
            found = false;
            break;
          }
        }
        if (found) {
          return entity;
        }
      }
      return null;
    } catch (final EdmPrimitiveTypeException e) {
      throw new DataProviderException("Wrong key!", e);
    }
  }

  public void delete(final EdmEntitySet edmEntitySet, final Entity entity) throws DataProviderException {
    deleteLinksTo(entity);
    readAll(edmEntitySet).getEntities().remove(entity);
  }

  public void deleteLinksTo(final Entity to) throws DataProviderException {
    for (final String entitySetName : data.keySet()) {
      for (final Entity entity : data.get(entitySetName).getEntities()) {
        for (Iterator<Link> linkIterator = entity.getNavigationLinks().iterator(); linkIterator.hasNext();) {
          final Link link = linkIterator.next();
          if (to.equals(link.getInlineEntity())) {
            linkIterator.remove();
          } else if (link.getInlineEntitySet() != null) {
            for (Iterator<Entity> iterator = link.getInlineEntitySet().getEntities().iterator(); iterator.hasNext();) {
              if (to.equals(iterator.next())) {
                iterator.remove();
              }
            }
            if (link.getInlineEntitySet().getEntities().isEmpty()) {
              linkIterator.remove();
            }
          }
        }
      }
    }
  }

  public Entity create(final EdmEntitySet edmEntitySet) throws DataProviderException {
    final EdmEntityType edmEntityType = edmEntitySet.getEntityType();
    final EntitySet entitySet = readAll(edmEntitySet);
    final List<Entity> entities = entitySet.getEntities();
    final Map<String, Object> newKey = findFreeComposedKey(entities, edmEntitySet.getEntityType());
    final Entity newEntity = new EntityImpl();

    for (final String keyName : edmEntityType.getKeyPredicateNames()) {
      newEntity.addProperty(DataCreator.createPrimitive(keyName, newKey.get(keyName)));
    }

    createProperties(edmEntityType, newEntity.getProperties());
    entities.add(newEntity);

    return newEntity;
  }

  private Map<String, Object> findFreeComposedKey(final List<Entity> entities, final EdmEntityType entityType)
      throws DataProviderException {
    // Weak key construction
    // 3e € entity: (V k € keys: k !€ e.ki) => e.(k1, k2, k3) !€ entitySet
    final HashMap<String, Object> keys = new HashMap<String, Object>();
    for (final String keyName : entityType.getKeyPredicateNames()) {
      final EdmType type = entityType.getProperty(keyName).getType();
      Object newValue = null;

      if (type instanceof EdmInt16 || type instanceof EdmInt32 || type instanceof EdmInt64) {
        // Integer keys
        newValue = Integer.valueOf(1);

        while (!isFree(newValue, keyName, entities)) {
          newValue = ((Integer) newValue) + 1;
        }
      } else if (type instanceof EdmString) {
        // String keys
        newValue = String.valueOf(1);
        int i = 0;

        while (!isFree(newValue, keyName, entities)) {
          newValue = String.valueOf(i);
          i++;
        }
      } else {
        throw new DataProviderException("Key type not supported", HttpStatusCode.NOT_IMPLEMENTED);
      }

      keys.put(keyName, newValue);
    }

    return keys;
  }

  private boolean isFree(final Object value, final String keyPropertyName, final List<Entity> entities) {
    for (final Entity entity : entities) {
      if (value != null && value.equals(entity.getProperty(keyPropertyName).getValue())) {
        return false;
      }
    }

    return true;
  }

  private void createProperties(final EdmStructuredType type, List<Property> properties) throws DataProviderException {
    final List<String> keyNames = type instanceof EdmEntityType ?
        ((EdmEntityType) type).getKeyPredicateNames() : Collections.<String> emptyList();
    for (final String propertyName : type.getPropertyNames()) {
      if (!keyNames.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        Property newProperty;
        if (edmProperty.isPrimitive()) {
          newProperty = edmProperty.isCollection() ?
              DataCreator.createPrimitiveCollection(propertyName) :
              DataCreator.createPrimitive(propertyName, null);
        } else {
          if (edmProperty.isCollection()) {
            @SuppressWarnings("unchecked")
            Property newProperty2 = DataCreator.createComplexCollection(propertyName);
            newProperty = newProperty2;
          } else {
            newProperty = DataCreator.createComplex(propertyName);
            createProperties((EdmComplexType) edmProperty.getType(), newProperty.asComplex().getValue());
          }
        }
        properties.add(newProperty);
      }
    }
  }

  public void update(final String rawBaseUri, final EdmEntitySet edmEntitySet, Entity entity,
      final Entity changedEntity, final boolean patch, final boolean isInsert) throws DataProviderException {

    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final List<String> keyNames = entityType.getKeyPredicateNames();

    // Update Properties
    for (final String propertyName : entityType.getPropertyNames()) {
      if (!keyNames.contains(propertyName)) {
        updateProperty(entityType.getStructuralProperty(propertyName),
            entity.getProperty(propertyName),
            changedEntity.getProperty(propertyName),
            patch);
      }
    }

    // Deep insert (only if not an update)
    if (isInsert) {
      handleDeepInsert(rawBaseUri, edmEntitySet, entity, changedEntity);
    } else {
      handleDeleteSingleNavigationProperties(edmEntitySet, entity, changedEntity);
    }

    final boolean navigationBindingsAvailable = !changedEntity.getNavigationBindings().isEmpty();
    if (navigationBindingsAvailable) {
      applyNavigationBinding(rawBaseUri, edmEntitySet, entity, changedEntity.getNavigationBindings());
    }
  }

  private void handleDeleteSingleNavigationProperties(EdmEntitySet edmEntitySet, Entity entity, Entity changedEntity)
      throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();

    for (final String navPropertyName : navigationPropertyNames) {
      final Link navigationLink = changedEntity.getNavigationLink(navPropertyName);
      final EdmNavigationProperty navigationProperty = entityType.getNavigationProperty(navPropertyName);
      if (!navigationProperty.isCollection() && navigationLink != null && navigationLink.getInlineEntity() == null) {
        
        // Check if partner is available
        if (navigationProperty.getPartner() != null && entity.getNavigationLink(navPropertyName) != null) {
          Entity partnerEntity =  entity.getNavigationLink(navPropertyName).getInlineEntity();
          removeLink(navigationProperty.getPartner(), partnerEntity);
        }
        
        // Remove link
        removeLink(navigationProperty, entity);
      }
    }
  }

  private void applyNavigationBinding(final String rawBaseUri, final EdmEntitySet edmEntitySet,
      final Entity entity, final List<Link> navigationBindings) throws DataProviderException {

    for (final Link link : navigationBindings) {
      final EdmNavigationProperty edmNavProperty = edmEntitySet.getEntityType().getNavigationProperty(link.getTitle());
      final EdmEntitySet edmTargetEntitySet =
          (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(edmNavProperty.getName());

      if (edmNavProperty.isCollection()) {
        for (final String bindingLink : link.getBindingLinks()) {
          final Entity destEntity = getEntityByURI(rawBaseUri, edmTargetEntitySet, bindingLink);
          createLink(edmNavProperty, entity, destEntity);
        }
      } else {
        final String bindingLink = link.getBindingLink();
        final Entity destEntity = getEntityByURI(rawBaseUri, edmTargetEntitySet, bindingLink);
        createLink(edmNavProperty, entity, destEntity);
      }
    }
  }

  private Entity getEntityByURI(final String rawBaseUri, final EdmEntitySet edmEntitySetTarget,
      final String bindingLink) throws DataProviderException {

    try {
      final List<UriParameter> keys = odata.createUriHelper()
          .getKeyPredicatesFromEntityLink(edm, bindingLink, rawBaseUri);
      final Entity entity = read(edmEntitySetTarget, keys);

      if (entity == null) {
        throw new DataProviderException("Entity " + bindingLink + " not found", HttpStatusCode.NOT_FOUND);
      }

      return entity;
    } catch (DeserializerException e) {
      throw new DataProviderException("Invalid entity binding link", HttpStatusCode.BAD_REQUEST);
    }
  }

  private void handleDeepInsert(final String rawBaseUri, final EdmEntitySet edmEntitySet, final Entity entity,
      final Entity changedEntity) throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();

    for (final String navPropertyName : entityType.getNavigationPropertyNames()) {
      final Link navigationLink = changedEntity.getNavigationLink(navPropertyName);

      if (navigationLink != null) {
        // Deep inserts are not allowed in update operations, so we can be sure, that we do not override
        // a navigation link!
        final EdmNavigationProperty navigationProperty = entityType.getNavigationProperty(navPropertyName);
        final EdmEntitySet target = (EdmEntitySet) edmEntitySet.getRelatedBindingTarget(navPropertyName);

        if (navigationProperty.isCollection()) {
          final List<Entity> entities =
              createInlineEntities(rawBaseUri, target, navigationLink.getInlineEntitySet());

          for (final Entity inlineEntity : entities) {
            createLink(navigationProperty, entity, inlineEntity);
          }
        } else if (!navigationProperty.isCollection() && navigationLink.getInlineEntity() != null) {
          final Entity inlineEntity = createInlineEntity(rawBaseUri, target, navigationLink.getInlineEntity());
          createLink(navigationProperty, entity, inlineEntity);
        }
      }
    }
  }

  private void removeLink(EdmNavigationProperty navigationProperty, Entity entity) {
    final Link link = entity.getNavigationLink(navigationProperty.getName());
    if(link != null) {
      entity.getNavigationLinks().remove(link);
    }
  }

  private List<Entity> createInlineEntities(final String rawBaseUri, final EdmEntitySet targetEntitySet,
      final EntitySet changedEntitySet) throws DataProviderException {
    List<Entity> entities = new ArrayList<Entity>();

    for (final Entity newEntity : changedEntitySet.getEntities()) {
      entities.add(createInlineEntity(rawBaseUri, targetEntitySet, newEntity));
    }

    return entities;
  }

  private Entity createInlineEntity(final String rawBaseUri, final EdmEntitySet targetEntitySet,
      final Entity changedEntity) throws DataProviderException {

    final Entity inlineEntity = create(targetEntitySet);
    update(rawBaseUri, targetEntitySet, inlineEntity, changedEntity, false, true);

    return inlineEntity;
  }

  private void createLink(final EdmNavigationProperty navigationProperty, final Entity srcEntity,
      final Entity destEntity) {
    setLink(navigationProperty, srcEntity, destEntity);

    final EdmNavigationProperty partnerNavigationProperty = navigationProperty.getPartner();
    if (partnerNavigationProperty != null) {
      setLink(partnerNavigationProperty, destEntity, srcEntity);
    }
  }

  private void setLink(final EdmNavigationProperty navigationProperty, Entity srcEntity, final Entity targetEntity) {
    if (navigationProperty.isCollection()) {
      DataCreator.setLinks(srcEntity, navigationProperty.getName(), targetEntity);
    } else {
      DataCreator.setLink(srcEntity, navigationProperty.getName(), targetEntity);
    }
  }

  public void updateProperty(final EdmProperty edmProperty, Property property, final Property newProperty,
      final boolean patch) throws DataProviderException {
    if (edmProperty.isPrimitive()) {
      if (newProperty != null || !patch) {
        final Object value = newProperty == null ? null : newProperty.getValue();
        if (value == null && !edmProperty.isNullable()) {
          throw new DataProviderException("Cannot null non-nullable property!", HttpStatusCode.BAD_REQUEST);
        }
        property.setValue(property.getValueType(), value);
      }
    } else if (edmProperty.isCollection()) {
      if (newProperty != null && !newProperty.asCollection().isEmpty()) {
        throw new DataProviderException("Update of a complex-collection property not supported!",
            HttpStatusCode.NOT_IMPLEMENTED);
      } else {
        property.asCollection().clear();
      }
    } else {
      final EdmComplexType type = (EdmComplexType) edmProperty.getType();
      for (final String propertyName : type.getPropertyNames()) {
        final List<Property> newProperties = newProperty == null || newProperty.asComplex() == null ? null :
            newProperty.asComplex().getValue();
        updateProperty(type.getStructuralProperty(propertyName),
            findProperty(propertyName, property.asComplex().getValue()),
            newProperties == null ? null : findProperty(propertyName, newProperties),
            patch);
      }
    }
  }

  private Property findProperty(final String propertyName, final List<Property> properties) {
    for (final Property property : properties) {
      if (propertyName.equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  public byte[] readMedia(final Entity entity) {
    return (byte[]) entity.getProperty(MEDIA_PROPERTY_NAME).asPrimitive();
  }

  public void setMedia(Entity entity, byte[] media, String type) {
    entity.getProperties().remove(entity.getProperty(MEDIA_PROPERTY_NAME));
    entity.addProperty(DataCreator.createPrimitive(MEDIA_PROPERTY_NAME, media));
    entity.setMediaContentType(type);
  }

  public EntitySet readFunctionEntitySet(final EdmFunction function, final List<UriParameter> parameters)
      throws DataProviderException {
    return FunctionData.entityCollectionFunction(function.getName(), parameters, data);
  }

  public Entity readFunctionEntity(final EdmFunction function, final List<UriParameter> parameters)
      throws DataProviderException {
    return FunctionData.entityFunction(function.getName(), parameters, data);
  }

  public Property readFunctionPrimitiveComplex(final EdmFunction function, final List<UriParameter> parameters)
      throws DataProviderException {
    return FunctionData.primitiveComplexFunction(function.getName(), parameters, data);
  }

  public void setEdm(final Edm edm) {
    this.edm = edm;
  }

  public void setOData(final OData odata) {
    this.odata = odata;
  }

  public static class DataProviderException extends ODataApplicationException {
    private static final long serialVersionUID = 5098059649321796156L;

    public DataProviderException(final String message, final Throwable throwable) {
      super(message, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, throwable);
    }

    public DataProviderException(final String message) {
      super(message, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }

    public DataProviderException(final String message, HttpStatusCode statusCode) {
      super(message, statusCode.getStatusCode(), Locale.ROOT);
    }
  }
}
