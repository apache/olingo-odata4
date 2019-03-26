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
package org.apache.olingo.server.core.serializer.json;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.DeletedEntity;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.DeltaLink;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EdmDeltaSerializer;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.apache.olingo.server.core.uri.UriHelperImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class JsonDeltaSerializer implements EdmDeltaSerializer {

  private static final String LINK = "/$link";
  private static final String DELETEDLINK = "/$deletedLink";
  private static final String DELTA = "/$delta";
  private static final String HASH = "#";
  private static final String DELETEDENTITY = "/$deletedEntity";
  private static final String ENTITY = "/$entity";
  private static final String REASON = "Reason";
  private static final String IO_EXCEPTION_TEXT = "An I/O exception occurred.";
  private final boolean isIEEE754Compatible;
  private final boolean isODataMetadataNone;
  private final boolean isODataMetadataFull;

  public JsonDeltaSerializer(final ContentType contentType) {
    isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
    isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
  }

  @Override
  public SerializerResult entityCollection(ServiceMetadata metadata, EdmEntityType referencedEntityType, Delta delta,
      EntityCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      boolean pagination = false;
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      writeContextURL(contextURL, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeInlineCount(delta.getCount(), json);
      }
      json.writeFieldName(Constants.VALUE);
      writeEntitySet(metadata, referencedEntityType, delta, options, json);

      pagination = writeNextLink(delta, json);
      writeDeltaLink(delta, json, pagination);

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }

  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Delta entitySet, final EntityCollectionSerializerOptions options,
      final JsonGenerator json) throws IOException,
      SerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet.getEntities()) {
      writeAddedUpdatedEntity(metadata, entityType, entity, options.getExpand(),
          options.getSelect(), options.getContextURL(), false, options.getContextURL()
              .getEntitySetOrSingletonOrType(), json);
    }
    for (final DeletedEntity deletedEntity : entitySet.getDeletedEntities()) {
      writeDeletedEntity(deletedEntity, options, json);
    }
    for (final DeltaLink addedLink : entitySet.getAddedLinks()) {
      writeLink(addedLink, options, json, true);
    }
    for (final DeltaLink deletedLink : entitySet.getDeletedLinks()) {
      writeLink(deletedLink, options, json, false);
    }
    json.writeEndArray();
  }

  private void writeLink(DeltaLink link, EntityCollectionSerializerOptions options,
      JsonGenerator json, boolean isAdded) throws IOException, SerializerException {
    try {
      json.writeStartObject();
      String entityId = options.getContextURL().getEntitySetOrSingletonOrType();
      String operation = isAdded ? LINK : DELETEDLINK;
      json.writeStringField(Constants.JSON_CONTEXT, HASH + entityId + operation);
      if (link != null) {
        if (link.getSource() != null) {
          json.writeStringField(Constants.ATTR_SOURCE, link.getSource().toString());
        } else {
          throw new SerializerException("DeltaLink source is null.", 
              SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, "Source");
        }
        if (link.getRelationship() != null) {
          json.writeStringField(Constants.ATTR_RELATIONSHIP, link.getRelationship().toString());
        } else {
          throw new SerializerException("DeltaLink relationship is null.",
              SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, "Relationship");
        }
        if (link.getTarget() != null) {
          json.writeStringField(Constants.ERROR_TARGET, link.getTarget().toString());
        } else {
          throw new SerializerException("DeltaLink target is null.", 
              SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, "Target");
        }
      } else {
        throw new SerializerException("DeltaLink is null.", 
            SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, "Delta Link");
      }
      json.writeEndObject();
    } catch (IOException e) {
      throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
    }
  }

  private void writeDeletedEntity(DeletedEntity deletedEntity,
      EntityCollectionSerializerOptions options, JsonGenerator json) throws IOException, SerializerException {
    if (deletedEntity.getId() == null) {
      throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
    }
    if (deletedEntity.getReason() == null) {
      throw new SerializerException("DeletedEntity reason is null.", 
          SerializerException.MessageKeys.MISSING_DELTA_PROPERTY, REASON);
    }
    json.writeStartObject();
    json.writeStringField(Constants.JSON_CONTEXT, HASH + deletedEntity.getId().toASCIIString() + DELETEDENTITY);
    json.writeStringField(Constants.ATOM_ATTR_ID, deletedEntity.getId().toASCIIString());
    json.writeStringField(Constants.ELEM_REASON, deletedEntity.getReason().name());
    json.writeEndObject();

  }

  public void writeAddedUpdatedEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ExpandOption expand, final SelectOption select, final ContextURL url,
      final boolean onlyReference, String name, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    if (entity.getId() != null && url != null) {
      String entityId = entity.getId().toString();
      name = url.getEntitySetOrSingletonOrType();
      if (!entityId.contains(name)) {
        String entityName = entityId.substring(0, entityId.indexOf("("));
        if (!entityName.equals(name)) {
          json.writeStringField(Constants.JSON_CONTEXT, HASH + entityName + ENTITY);
        }
      }
    }
    json.writeStringField(Constants.JSON_ID, getEntityId(entity, entityType, name));
    writeProperties(metadata, entityType, entity.getProperties(), select, json);
    json.writeEndObject();

  }

  private Property findProperty(final String propertyName, final List<Property> properties) {
    for (final Property property : properties) {
      if (propertyName.equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  protected void writeProperty(final ServiceMetadata metadata,
      final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    boolean isStreamProperty = isStreamProperty(edmProperty);
    if (property != null) {
      if (!isStreamProperty) {
        json.writeFieldName(edmProperty.getName());
      }
      writePropertyValue(metadata, edmProperty, property, selectedPaths, json);
    }

  }

  private boolean isStreamProperty(EdmProperty edmProperty) {
    final EdmType type = edmProperty.getType();
    return (edmProperty.isPrimitive() && type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Stream));
  }

  private void writePropertyValue(final ServiceMetadata metadata, final EdmProperty edmProperty,
      final Property property, final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    final EdmType type = edmProperty.getType();
    try {
      if (edmProperty.isPrimitive()
          || type.getKind() == EdmTypeKind.ENUM || type.getKind() == EdmTypeKind.DEFINITION) {
        if (edmProperty.isCollection()) {
          writePrimitiveCollection((EdmPrimitiveType) type, property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
        } else {
          writePrimitive((EdmPrimitiveType) type, property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), json);
        }
      } else if (property.isComplex()) {
        if (edmProperty.isCollection()) {
          writeComplexCollection(metadata, (EdmComplexType) type, property, selectedPaths, json);
        } else {
          writeComplex(metadata, (EdmComplexType) type, property, selectedPaths, json);
        }
      } else {
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      }
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          edmProperty.getName(), property.getValue().toString());
    }
  }

  protected EdmComplexType resolveComplexType(final ServiceMetadata metadata, final EdmComplexType baseType,
      final String derivedTypeName) throws SerializerException {

    String fullQualifiedName = baseType.getFullQualifiedName().getFullQualifiedNameAsString();
    if (derivedTypeName == null ||
        fullQualifiedName.equals(derivedTypeName)) {
      return baseType;
    }
    EdmComplexType derivedType = metadata.getEdm().getComplexType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("Complex Type not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmComplexType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().equals(baseType.getFullQualifiedName())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName,
        baseType.getFullQualifiedName().getFullQualifiedNameAsString());
  }

  private void writeComplex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    String derivedName = property.getType();
    final EdmComplexType resolvedType = resolveComplexType(metadata, (EdmComplexType) type, derivedName);
    if (!isODataMetadataNone && !resolvedType.equals(type) || isODataMetadataFull) {
      json.writeStringField(Constants.JSON_TYPE, "#" + property.getType());
    }
    writeComplexValue(metadata, resolvedType, property.asComplex().getValue(), selectedPaths,
        json);
    json.writeEndObject();
  }

  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
      case COLLECTION_ENUM:
        try {
          writePrimitiveValue(property.getName(), type, value, isNullable,
              maxLength, precision, scale, isUnicode, json);
        } catch (EdmPrimitiveTypeException e) {
          throw new SerializerException("Wrong value for property!", e,
              SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
              property.getName(), property.getValue().toString());
        }
        break;
      case COLLECTION_GEOSPATIAL:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writeComplexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        json.writeStartObject();
        if (isODataMetadataFull) {
          json.writeStringField(Constants.JSON_TYPE, "#" +
              type.getFullQualifiedName().getFullQualifiedNameAsString());
        }
        writeComplexValue(metadata, type, ((ComplexValue) value).getValue(), selectedPaths, json);
        json.writeEndObject();
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writePrimitive(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json)
      throws EdmPrimitiveTypeException, IOException, SerializerException {
    if (property.isPrimitive()) {
      writePrimitiveValue(property.getName(), type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else if (property.isGeospatial()) {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
    } else if (property.isEnum()) {
      writePrimitiveValue(property.getName(), type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final String name, final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode, final JsonGenerator json) throws EdmPrimitiveTypeException, IOException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (value == null) {
      json.writeNull();
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
      json.writeBoolean(Boolean.parseBoolean(value));
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)
        || (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)
            || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64))
            && !isIEEE754Compatible) {
      json.writeNumber(value);
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Stream)) {
      if (primitiveValue instanceof Link) {
        Link stream = (Link) primitiveValue;
        if (!isODataMetadataNone) {
          if (stream.getMediaETag() != null) {
            json.writeStringField(name + Constants.JSON_MEDIA_ETAG, stream.getMediaETag());
          }
          if (stream.getType() != null) {
            json.writeStringField(name + Constants.JSON_MEDIA_CONTENT_TYPE, stream.getType());
          }
        }
        if (isODataMetadataFull) {
          if (stream.getRel() != null && stream.getRel().equals(Constants.NS_MEDIA_READ_LINK_REL)) {
            json.writeStringField(name + Constants.JSON_MEDIA_READ_LINK, stream.getHref());
          }
          if (stream.getRel() == null || stream.getRel().equals(Constants.NS_MEDIA_EDIT_LINK_REL)) {
            json.writeStringField(name + Constants.JSON_MEDIA_EDIT_LINK, stream.getHref());
          }
        }
      }
    } else {
      json.writeString(value);
    }
  }

  protected void writeComplexValue(final ServiceMetadata metadata,
      final EdmComplexType type, final List<Property> properties,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {

    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty(metadata, (EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, propertyName),
            json);
      }
    }
  }

  protected void writeProperties(final ServiceMetadata metadata, final EdmStructuredType type,
      final List<Property> properties,
      final SelectOption select, final JsonGenerator json)
      throws IOException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? new HashSet<String>() : ExpandSelectHelper.getSelectedPropertyNames(select
        .getSelectItems());
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null : ExpandSelectHelper
            .getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(metadata, edmProperty, property, selectedPaths, json);
      }
    }
  }

  /**
   * Get the ascii representation of the entity id
   * or thrown an {@link SerializerException} if id is <code>null</code>.
   *
   * @param entity the entity
   * @return ascii representation of the entity id
   */
  private String getEntityId(Entity entity, EdmEntityType entityType, String name) throws SerializerException {
    try {
      if (entity != null) {
        if (entity.getId() == null) {
          if (entityType == null || entityType.getKeyPredicateNames() == null
              || name == null) {
            throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
          } else {
            final UriHelper uriHelper = new UriHelperImpl();
            entity.setId(URI.create(name + '(' + uriHelper.buildKeyPredicate(entityType, entity) + ')'));
            return entity.getId().toASCIIString();
          }
        } else {
          return entity.getId().toASCIIString();
        }
      } 
      return null;
    } catch (Exception e) {
      throw new SerializerException("Entity id is null.", SerializerException.MessageKeys.MISSING_ID);
    }
  }

  void writeInlineCount(final Integer count, final JsonGenerator json)
      throws IOException {
    if (count != null) {
      String countValue = isIEEE754Compatible ? String.valueOf(count) : String.valueOf(count);
      json.writeStringField(Constants.JSON_COUNT, countValue);
    }
  }

  ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (isODataMetadataNone) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  void writeContextURL(final ContextURL contextURL, final JsonGenerator json) throws IOException {
    if (!isODataMetadataNone && contextURL != null) {
      json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString() + DELTA);
    }
  }

  boolean writeNextLink(final AbstractEntityCollection entitySet, final JsonGenerator json)
      throws IOException {
    if (entitySet.getNext() != null) {
      json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
      return true;
    } else {
      return false;
    }
  }

  void writeDeltaLink(final AbstractEntityCollection entitySet, final JsonGenerator json, boolean pagination)
      throws IOException {
    if (entitySet.getDeltaLink() != null && !pagination) {
      json.writeStringField(Constants.JSON_DELTA_LINK, entitySet.getDeltaLink().toASCIIString());
    }
  }
  
  protected void closeCircleStreamBufferOutput(final OutputStream outputStream,
      final SerializerException cachedException)
      throws SerializerException {
    if (outputStream != null) {
      try {
        outputStream.close();
      } catch (IOException e) {
        if (cachedException != null) {
          throw cachedException;
        } else {
          throw new SerializerException(IO_EXCEPTION_TEXT, e,
              SerializerException.MessageKeys.IO_EXCEPTION);
        }
      }
    }
  }
}
