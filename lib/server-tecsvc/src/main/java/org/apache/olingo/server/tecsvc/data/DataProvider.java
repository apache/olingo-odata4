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

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

public class DataProvider {

  protected static final String MEDIA_PROPERTY_NAME = "$value";

  private Map<String, EntitySet> data;

  public DataProvider() {
    data = new DataCreator().getData();
  }

  public EntitySet readAll(final EdmEntitySet edmEntitySet) throws DataProviderException {
    return data.get(edmEntitySet.getName());
  }

  public Entity read(final EdmEntitySet edmEntitySet, final List<UriParameter> keys) throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final EntitySet entitySet = data.get(edmEntitySet.getName());
    if (entitySet == null) {
      return null;
    } else {
      try {
        for (final Entity entity : entitySet.getEntities()) {
          boolean found = true;
          for (final UriParameter key : keys) {
            final EdmProperty property = (EdmProperty) entityType.getProperty(key.getName());
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
  }

  public void delete(final EdmEntitySet edmEntitySet, final Entity entity) throws DataProviderException {
    deleteLinksTo(entity);
    data.get(edmEntitySet.getName()).getEntities().remove(entity);
  }

  public void deleteLinksTo(final Entity to) throws DataProviderException {
    for (final String entitySet : data.keySet()) {
      for (final Entity entity : data.get(entitySet).getEntities()) {
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
    List<Entity> entities = readAll(edmEntitySet).getEntities();
    Entity entity = new EntityImpl();
    final List<String> keyNames = edmEntitySet.getEntityType().getKeyPredicateNames();
    if (keyNames.size() == 1 && keyNames.get(0).equals("PropertyInt16")) {
      entity.addProperty(DataCreator.createPrimitive("PropertyInt16",
          entities.isEmpty() ? 1 :
              (Integer) entities.get(entities.size() - 1).getProperty("PropertyInt16").getValue() + 1));
    } else {
      throw new DataProviderException("Key construction not supported!");
    }
    entities.add(entity);
    return entity;
  }

  public void update(final EdmEntitySet edmEntitySet, Entity entity, final Entity changedEntity, final boolean patch)
      throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final List<String> keyNames = entityType.getKeyPredicateNames();
    for (final String propertyName : entityType.getPropertyNames()) {
      if (!keyNames.contains(propertyName)) {
        updateProperty(entityType.getStructuralProperty(propertyName),
            entity.getProperty(propertyName),
            changedEntity.getProperty(propertyName),
            patch);
      }
    }
    if (!changedEntity.getNavigationBindings().isEmpty()) {
      throw new DataProviderException("Binding operations are not yet supported.");
    }
  }

  public void updateProperty(final EdmProperty edmProperty, Property property, final Property newProperty,
      final boolean patch) throws DataProviderException {
    if (edmProperty.isCollection() && !edmProperty.isPrimitive()) {
      throw new DataProviderException("Complex-collection properties are not yet supported.");
    } else if (property.isPrimitive()) {
      if (newProperty != null || !patch) {
        final Object value = newProperty == null ? null : newProperty.getValue();
        if (value == null && edmProperty.isNullable() != null && !edmProperty.isNullable()) {
          throw new DataProviderException("Cannot null non-nullable property!");
        }
        property.setValue(property.getValueType(), value);
      }
    } else {
      final EdmComplexType type = (EdmComplexType) edmProperty.getType();
      for (final String propertyName : type.getPropertyNames()) {
        final List<Property> newProperties = newProperty == null ? null :
            newProperty.isComplex() ? newProperty.asComplex() : newProperty.asLinkedComplex().getValue();
        updateProperty(type.getStructuralProperty(propertyName),
            findProperty(propertyName, property.asLinkedComplex().getValue()),
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

  public static class DataProviderException extends ODataApplicationException {
    private static final long serialVersionUID = 5098059649321796156L;

    public DataProviderException(final String message, final Throwable throwable) {
      super(message, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT, throwable);
    }

    public DataProviderException(final String message) {
      super(message, HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ROOT);
    }
  }
}
