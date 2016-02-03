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

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

public class DataProvider {

  protected static final String MEDIA_PROPERTY_NAME = "$value";
  private AtomicInteger KEY_INT_16 = new AtomicInteger(0);
  private AtomicInteger KEY_INT_32 = new AtomicInteger(0);
  private AtomicLong KEY_INT_64 = new AtomicLong(0);
  private AtomicLong KEY_STRING = new AtomicLong(0);

  private Map<String, EntityCollection> data;
  private final OData odata;
  private final Edm edm;

  public DataProvider(final OData odata, final Edm edm) {
    this.odata = odata;
    this.edm = edm;
    data = new DataCreator(odata, edm).getData();
  }

  public EntityCollection readAll(final EdmEntitySet edmEntitySet) throws DataProviderException {
    EntityCollection entityCollection = data.get(edmEntitySet.getName());
    if (entityCollection == null) {
      entityCollection = new EntityCollection();
      data.put(edmEntitySet.getName(), entityCollection);
    }
    return entityCollection;
  }

  public Entity read(final EdmEntitySet edmEntitySet, final List<UriParameter> keys) throws DataProviderException {
    final EntityCollection entitySet = readAll(edmEntitySet);
    return entitySet == null ? null : read(edmEntitySet.getEntityType(), entitySet, keys);
  }

  public Entity read(final EdmEntityType edmEntityType, final EntityCollection entitySet,
      final List<UriParameter> keys) throws DataProviderException {
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
      throw new DataProviderException("Wrong key!", HttpStatusCode.BAD_REQUEST, e);
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
    EntityCollection entitySet = readAll(edmEntitySet);
    final List<Entity> entities = entitySet.getEntities();
    final Map<String, Object> newKey = findFreeComposedKey(entities, edmEntityType);
    Entity newEntity = new Entity();
    newEntity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());
    for (final String keyName : edmEntityType.getKeyPredicateNames()) {
      newEntity.addProperty(DataCreator.createPrimitive(keyName, newKey.get(keyName)));
    }

    createProperties(edmEntityType, newEntity.getProperties());
    try {
      newEntity.setId(URI.create(odata.createUriHelper().buildCanonicalURL(edmEntitySet, newEntity)));
    } catch (final SerializerException e) {
      throw new DataProviderException("Unable to set entity ID!", HttpStatusCode.INTERNAL_SERVER_ERROR, e);
    }
    entities.add(newEntity);

    return newEntity;
  }

  private Map<String, Object> findFreeComposedKey(final List<Entity> entities, final EdmEntityType entityType)
      throws DataProviderException {
    // Weak key construction
    final HashMap<String, Object> keys = new HashMap<String, Object>();
    List<String> keyPredicateNames = entityType.getKeyPredicateNames();
    for (final String keyName : keyPredicateNames) {
      EdmType type = entityType.getProperty(keyName).getType();
      FullQualifiedName typeName = type.getFullQualifiedName();
      if (type instanceof EdmTypeDefinition) {
        typeName = ((EdmTypeDefinition) type).getUnderlyingType().getFullQualifiedName();
      }
      Object newValue;

      if (EdmPrimitiveTypeKind.Int16.getFullQualifiedName().equals(typeName)) {
        newValue = (short) KEY_INT_16.incrementAndGet();

        while (!isFree(newValue, keyName, entities)) {
          newValue = (short) KEY_INT_16.incrementAndGet();
        }
      } else if (EdmPrimitiveTypeKind.Int32.getFullQualifiedName().equals(typeName)) {
        newValue = KEY_INT_32.incrementAndGet();

        while (!isFree(newValue, keyName, entities)) {
          newValue = KEY_INT_32.incrementAndGet();
        }
      } else if (EdmPrimitiveTypeKind.Int64.getFullQualifiedName().equals(typeName)) {
        // Integer keys
        newValue = KEY_INT_64.incrementAndGet();

        while (!isFree(newValue, keyName, entities)) {
          newValue = KEY_INT_64.incrementAndGet();
        }
      } else if (EdmPrimitiveTypeKind.String.getFullQualifiedName().equals(typeName)) {
        // String keys
        newValue = String.valueOf(KEY_STRING.incrementAndGet());

        while (!isFree(newValue, keyName, entities)) {
          newValue = String.valueOf(KEY_STRING.incrementAndGet());
        }
      } else if (type instanceof EdmEnumType) {
        /* In case of an enum key we only support composite keys. This way we can 0 as a key */
        if (keyPredicateNames.size() <= 1) {
          throw new DataProviderException("Single Enum as key not supported", HttpStatusCode.NOT_IMPLEMENTED);
        }
        newValue = new Short((short) 1);
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

  private void createProperties(final EdmStructuredType type, final List<Property> properties)
      throws DataProviderException {
    final List<String> keyNames = type instanceof EdmEntityType ?
        ((EdmEntityType) type).getKeyPredicateNames() : Collections.<String> emptyList();
    for (final String propertyName : type.getPropertyNames()) {
      if (!keyNames.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        properties.add(createProperty(edmProperty, propertyName));
      }
    }
  }

  private Property createProperty(final EdmProperty edmProperty, final String propertyName)
      throws DataProviderException {
    final EdmType type = edmProperty.getType();
    Property newProperty;
    if (edmProperty.isPrimitive()
        || type.getKind() == EdmTypeKind.ENUM || type.getKind() == EdmTypeKind.DEFINITION) {
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
        createProperties((EdmComplexType) type, newProperty.asComplex().getValue());
      }
    }
    return newProperty;
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

    // For insert operations collection navigation property bind operations and deep insert operations can be combined.
    // In this case, the bind operations MUST appear before the deep insert operations in the payload.
    // => Apply bindings first
    final boolean navigationBindingsAvailable = !changedEntity.getNavigationBindings().isEmpty();
    if (navigationBindingsAvailable) {
      applyNavigationBinding(rawBaseUri, edmEntitySet, entity, changedEntity.getNavigationBindings());
    }

    // Deep insert (only if not an update)
    if (isInsert) {
      handleDeepInsert(rawBaseUri, edmEntitySet, entity, changedEntity);
    } else {
      handleDeleteSingleNavigationProperties(edmEntitySet, entity, changedEntity);
    }

    // Update the ETag if present.
    updateETag(entity);
  }

  public void updateETag(Entity entity) {
    if (entity.getETag() != null) {
      entity.setETag("W/\"" + UUID.randomUUID() + "\"");
    }
  }

  private void handleDeleteSingleNavigationProperties(final EdmEntitySet edmEntitySet, final Entity entity,
      final Entity changedEntity) throws DataProviderException {
    final EdmEntityType entityType = edmEntitySet.getEntityType();
    final List<String> navigationPropertyNames = entityType.getNavigationPropertyNames();

    for (final String navPropertyName : navigationPropertyNames) {
      final Link navigationLink = changedEntity.getNavigationLink(navPropertyName);
      final EdmNavigationProperty navigationProperty = entityType.getNavigationProperty(navPropertyName);
      if (!navigationProperty.isCollection() && navigationLink != null && navigationLink.getInlineEntity() == null) {

        // Check if partner is available
        if (navigationProperty.getPartner() != null && entity.getNavigationLink(navPropertyName) != null) {
          Entity partnerEntity = entity.getNavigationLink(navPropertyName).getInlineEntity();
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

      if (edmNavProperty.isCollection()) {
        for (final String bindingLink : link.getBindingLinks()) {
          final Entity destEntity = getEntityByReference(bindingLink, rawBaseUri);
          createLink(edmNavProperty, entity, destEntity);
        }
      } else {
        final String bindingLink = link.getBindingLink();
        final Entity destEntity = getEntityByReference(bindingLink, rawBaseUri);
        createLink(edmNavProperty, entity, destEntity);
      }
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

  private void removeLink(final EdmNavigationProperty navigationProperty, final Entity entity) {
    final Link link = entity.getNavigationLink(navigationProperty.getName());
    if (link != null) {
      entity.getNavigationLinks().remove(link);
    }
  }

  private List<Entity> createInlineEntities(final String rawBaseUri, final EdmEntitySet targetEntitySet,
      final EntityCollection changedEntitySet) throws DataProviderException {
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

  public void setLink(final EdmNavigationProperty navigationProperty, final Entity srcEntity,
      final Entity targetEntity) {
    if (navigationProperty.isCollection()) {
      DataCreator.setLinks(srcEntity, navigationProperty.getName(), targetEntity);
    } else {
      DataCreator.setLink(srcEntity, navigationProperty.getName(), targetEntity);
    }
  }

  @SuppressWarnings("unchecked")
  public void updateProperty(final EdmProperty edmProperty, Property property, final Property newProperty,
      final boolean patch) throws DataProviderException {
    final EdmType type = edmProperty.getType();
    if (edmProperty.isCollection()) {
      // Updating collection properties means replacing all entries with the given ones.
      property.asCollection().clear();

      if (newProperty != null) {
        if (type.getKind() == EdmTypeKind.COMPLEX) {
          // Create each complex value.
          for (final ComplexValue complexValue : (List<ComplexValue>) newProperty.asCollection()) {
            ((List<ComplexValue>) property.asCollection()).add(createComplexValue(edmProperty, complexValue, patch));
          }
        } else {
          // Primitive type
          ((List<Object>) property.asCollection()).addAll(newProperty.asCollection());
        }
      }
    } else if (type.getKind() == EdmTypeKind.COMPLEX) {
      for (final String propertyName : ((EdmComplexType) type).getPropertyNames()) {
        final List<Property> newProperties = newProperty == null || newProperty.asComplex() == null ? null :
            newProperty.asComplex().getValue();
        updateProperty(((EdmComplexType) type).getStructuralProperty(propertyName),
            findProperty(propertyName, property.asComplex().getValue()),
            newProperties == null ? null : findProperty(propertyName, newProperties),
            patch);
      }
    } else {
      if (newProperty != null || !patch) {
        final Object value = newProperty == null ? null : newProperty.getValue();
        updatePropertyValue(property, value);
      }
    }
  }

  public void updatePropertyValue(Property property, final Object value) {
    property.setValue(property.getValueType(), value);
  }

  private ComplexValue createComplexValue(final EdmProperty edmProperty, final ComplexValue complexValue,
      final boolean patch) throws DataProviderException {
    final ComplexValue result = new ComplexValue();
    final EdmComplexType edmType = (EdmComplexType) edmProperty.getType();
    final List<Property> givenProperties = complexValue.getValue();

    // Create ALL properties, even if no value is given. Check if null is allowed
    for (final String propertyName : edmType.getPropertyNames()) {
      final EdmProperty innerEdmProperty = (EdmProperty) edmType.getProperty(propertyName);
      final Property currentProperty = findProperty(propertyName, givenProperties);
      final Property newProperty = createProperty(innerEdmProperty, propertyName);
      result.getValue().add(newProperty);

      if (currentProperty != null) {
        updateProperty(innerEdmProperty, newProperty, currentProperty, patch);
      } else {
        if (innerEdmProperty.isNullable()) {
          // Check complex properties ... may be null is not allowed
          if (edmProperty.getType().getKind() == EdmTypeKind.COMPLEX) {
            updateProperty(innerEdmProperty, newProperty, null, patch);
          }
        }
      }
    }

    return result;
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

  public void setMedia(final Entity entity, final byte[] media, final String type) {
    entity.getProperties().remove(entity.getProperty(MEDIA_PROPERTY_NAME));
    entity.addProperty(DataCreator.createPrimitive(MEDIA_PROPERTY_NAME, media));
    entity.setMediaContentType(type);
    entity.setMediaETag("W/\"" + UUID.randomUUID() + "\"");
  }

  public EntityCollection readFunctionEntityCollection(final EdmFunction function, final List<UriParameter> parameters,
      final UriInfoResource uriInfo) throws DataProviderException {
    return FunctionData.entityCollectionFunction(function.getName(),
        getFunctionParameters(function, parameters, uriInfo),
        data);
  }

  public Entity readFunctionEntity(final EdmFunction function, final List<UriParameter> parameters,
      final UriInfoResource uriInfo) throws DataProviderException {
    return FunctionData.entityFunction(function.getName(),
        getFunctionParameters(function, parameters, uriInfo),
        data);
  }

  public Property readFunctionPrimitiveComplex(final EdmFunction function, final List<UriParameter> parameters,
      final UriInfoResource uriInfo) throws DataProviderException {
    return FunctionData.primitiveComplexFunction(function.getName(),
        getFunctionParameters(function, parameters, uriInfo),
        data);
  }

  private Map<String, Parameter> getFunctionParameters(final EdmFunction function,
      final List<UriParameter> parameters, final UriInfoResource uriInfo) throws DataProviderException {
    Map<String, Parameter> values = new HashMap<String, Parameter>();
    for (final UriParameter parameter : parameters) {
      final EdmParameter edmParameter = function.getParameter(parameter.getName());
      final String text = parameter.getAlias() == null ?
          parameter.getText() :
          uriInfo.getValueForAlias(parameter.getAlias());
      if (text != null) {
        try {
          values.put(parameter.getName(),
              odata.createFixedFormatDeserializer().parameter(text, edmParameter));
        } catch (final DeserializerException e) {
          throw new DataProviderException("Invalid function parameter.", HttpStatusCode.BAD_REQUEST, e);
        }
      }
    }
    return values;
  }

  public Property processActionPrimitive(final String name, final Map<String, Parameter> actionParameters)
      throws DataProviderException {
    return ActionData.primitiveAction(name, actionParameters);
  }

  public Property processActionComplex(final String name, final Map<String, Parameter> actionParameters)
      throws DataProviderException {
    return ActionData.complexAction(name, actionParameters);
  }

  public Property processActionComplexCollection(final String name, final Map<String, Parameter> actionParameters)
      throws DataProviderException {
    return ActionData.complexCollectionAction(name, actionParameters);
  }

  public Property processActionPrimitiveCollection(final String name, final Map<String, Parameter> actionParameters)
      throws DataProviderException {
    return ActionData.primitiveCollectionAction(name, actionParameters, odata);
  }

  public EntityActionResult processActionEntity(final String name, final Map<String, Parameter> actionParameters)
      throws DataProviderException {
    return ActionData.entityAction(name, actionParameters, data, odata, edm);
  }

  public EntityCollection processActionEntityCollection(final String name,
      final Map<String, Parameter> actionParameters) throws DataProviderException {
    return ActionData.entityCollectionAction(name, actionParameters, odata, edm);
  }

  public void createReference(final Entity entity, final EdmNavigationProperty navigationProperty, final URI entityId,
      final String rawServiceRoot) throws DataProviderException {
    setLink(navigationProperty, entity, getEntityByReference(entityId.toASCIIString(), rawServiceRoot));
  }

  public void deleteReference(final Entity entity, final EdmNavigationProperty navigationProperty,
      final String entityId, final String rawServiceRoot) throws DataProviderException {

    if (navigationProperty.isCollection()) {
      final Entity targetEntity = getEntityByReference(entityId, rawServiceRoot);
      final Link navigationLink = entity.getNavigationLink(navigationProperty.getName());

      if (navigationLink != null && navigationLink.getInlineEntitySet() != null
          && navigationLink.getInlineEntitySet().getEntities().contains(targetEntity)) {

        // Remove partner single-valued navigation property
        if (navigationProperty.getPartner() != null) {
          final EdmNavigationProperty edmPartnerNavigationProperty = navigationProperty.getPartner();
          if (!edmPartnerNavigationProperty.isCollection() && !edmPartnerNavigationProperty.isNullable()) {
            throw new DataProviderException("Navigation property must not be null", HttpStatusCode.BAD_REQUEST);
          } else if (!edmPartnerNavigationProperty.isCollection()) {
            removeLink(edmPartnerNavigationProperty, targetEntity);
          } else if (edmPartnerNavigationProperty.isCollection()
              && edmPartnerNavigationProperty.getPartner() != null) {
            // Bidirectional referential constraint
            final Link partnerNavigationLink = targetEntity.getNavigationLink(edmPartnerNavigationProperty.getName());
            if (partnerNavigationLink != null && partnerNavigationLink.getInlineEntitySet() != null) {
              partnerNavigationLink.getInlineEntitySet().getEntities().remove(entity);
            }
          }
        }

        // Remove target entity from collection-valued navigation property
        navigationLink.getInlineEntitySet().getEntities().remove(targetEntity);
      } else {
        throw new DataProviderException("Entity not found", HttpStatusCode.NOT_FOUND);
      }
    } else {
      if (navigationProperty.isNullable()) {
        removeLink(navigationProperty, entity);
      } else {
        throw new DataProviderException("Navigation property must not be null", HttpStatusCode.BAD_REQUEST);
      }
    }
  }

  protected Entity getEntityByReference(final String entityId, final String rawServiceRoot)
      throws DataProviderException {
    try {
      final UriResourceEntitySet uriResource = odata.createUriHelper().parseEntityId(edm, entityId, rawServiceRoot);
      final Entity targetEntity = read(uriResource.getEntitySet(), uriResource.getKeyPredicates());

      if (targetEntity != null) {
        return targetEntity;
      } else {
        throw new DataProviderException("Entity not found", HttpStatusCode.NOT_FOUND);
      }
    } catch (DeserializerException e) {
      throw new DataProviderException("Invalid entity-id", HttpStatusCode.BAD_REQUEST, e);
    }
  }

  public static class DataProviderException extends ODataApplicationException {
    private static final long serialVersionUID = 5098059649321796156L;

    public DataProviderException(final String message, final HttpStatusCode statusCode) {
      super(message, statusCode.getStatusCode(), Locale.ROOT);
    }

    public DataProviderException(final String message, final HttpStatusCode statusCode, final Throwable throwable) {
      super(message, statusCode.getStatusCode(), Locale.ROOT, throwable);
    }
  }

  //CHECKSTYLE:OFF
  public static void setDefaultTimeZone(String tz) {
    System.setProperty("defaultTimeZoneForEdmDateTypes", tz);
  }
  //CHECKSTYLE:ON
}
