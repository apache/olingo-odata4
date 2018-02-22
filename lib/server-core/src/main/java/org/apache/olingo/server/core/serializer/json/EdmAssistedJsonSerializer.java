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
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.AbstractODataObject;
import org.apache.olingo.commons.api.data.Annotatable;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EdmAssistedSerializer;
import org.apache.olingo.server.api.serializer.EdmAssistedSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerException.MessageKeys;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class EdmAssistedJsonSerializer implements EdmAssistedSerializer {

  private static final String IO_EXCEPTION_TEXT = "An I/O exception occurred.";

  protected final boolean isIEEE754Compatible;
  protected final boolean isODataMetadataNone;
  protected final boolean isODataMetadataFull;

  public EdmAssistedJsonSerializer(final ContentType contentType) {
    this.isIEEE754Compatible = ContentTypeHelper.isODataIEEE754Compatible(contentType);
    this.isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
    this.isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata, final EdmEntityType entityType,
      final AbstractEntityCollection entityCollection, final EdmAssistedSerializerOptions options)
      throws SerializerException {
    return serialize(metadata, entityType, entityCollection, options == null ? null : options.getContextURL());
  }

  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType, final Entity entity,
      final EdmAssistedSerializerOptions options) throws SerializerException {
    return serialize(metadata, entityType, entity, options == null ? null : options.getContextURL());
  }

  protected SerializerResult serialize(final ServiceMetadata metadata, final EdmEntityType entityType,
      final AbstractODataObject obj, final ContextURL contextURL) throws SerializerException {
    final String metadataETag =
        isODataMetadataNone || metadata == null || metadata.getServiceMetadataETagSupport() == null ? null : metadata
            .getServiceMetadataETagSupport().getMetadataETag();
    final String contextURLString = isODataMetadataNone || contextURL == null ? null : ContextURLBuilder.create(
        contextURL).toASCIIString();
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      if (obj instanceof AbstractEntityCollection) {
        doSerialize(entityType, (AbstractEntityCollection) obj, contextURLString, metadataETag, json);
      } else if (obj instanceof Entity) {
        doSerialize(entityType, (Entity) obj, contextURLString, metadataETag, json);
      } else {
        throw new SerializerException("Input type not supported.", MessageKeys.NOT_IMPLEMENTED);
      }
      json.flush();
      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException = new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (final IOException e) {
          throw cachedException == null ? new SerializerException(IO_EXCEPTION_TEXT, e,
              SerializerException.MessageKeys.IO_EXCEPTION) : cachedException;
        }
      }
    }
  }

  protected void doSerialize(final EdmEntityType entityType, final AbstractEntityCollection entityCollection,
      final String contextURLString, final String metadataETag, JsonGenerator json)
      throws IOException, SerializerException {

    json.writeStartObject();

    metadata(contextURLString, metadataETag, null, null, entityCollection.getId(), false, json);

    if (entityCollection.getCount() != null) {
      if (isIEEE754Compatible) {
        json.writeStringField(Constants.JSON_COUNT, Integer.toString(entityCollection.getCount()));
      } else {
        json.writeNumberField(Constants.JSON_COUNT, entityCollection.getCount());
      }
    }
    if (entityCollection.getDeltaLink() != null) {
      json.writeStringField(Constants.JSON_DELTA_LINK, entityCollection.getDeltaLink().toASCIIString());
    }

    for (final Annotation annotation : entityCollection.getAnnotations()) {
      valuable(json, annotation, '@' + annotation.getTerm(), null, null);
    }

    json.writeArrayFieldStart(Constants.VALUE);
    for (final Entity entity : entityCollection) {
      doSerialize(entityType, entity, null, null, json);
    }
    json.writeEndArray();

    if (entityCollection.getNext() != null) {
      json.writeStringField(Constants.JSON_NEXT_LINK, entityCollection.getNext().toASCIIString());
    }

    json.writeEndObject();
  }

  protected void doSerialize(final EdmEntityType entityType, final Entity entity,
      final String contextURLString, final String metadataETag, JsonGenerator json)
      throws IOException, SerializerException {

    json.writeStartObject();

    final String typeName = entity.getType() == null ? null : new EdmTypeInfo.Builder().setTypeExpression(entity
        .getType()).build().external();
    metadata(contextURLString, metadataETag, entity.getETag(), typeName, entity.getId(), true, json);

    for (final Annotation annotation : entity.getAnnotations()) {
      valuable(json, annotation, '@' + annotation.getTerm(), null, null);
    }

    for (final Property property : entity.getProperties()) {
      final String name = property.getName();
      final EdmProperty edmProperty = entityType == null || entityType.getStructuralProperty(name) == null ? null
          : entityType.getStructuralProperty(name);
      valuable(json, property, name, edmProperty == null ? null : edmProperty.getType(), edmProperty);
    }

    if (!isODataMetadataNone &&
        entity.getEditLink() != null && entity.getEditLink().getHref() != null) {
      json.writeStringField(Constants.JSON_EDIT_LINK, entity.getEditLink().getHref());

      if (entity.isMediaEntity()) {
        json.writeStringField(Constants.JSON_MEDIA_READ_LINK, entity.getEditLink().getHref() + "/$value");
      }
    }

    links(entity, entityType, json);

    json.writeEndObject();
  }

  private void metadata(final String contextURLString, final String metadataETag, final String eTag,
      final String type, final URI id, final boolean writeNullId, JsonGenerator json)
      throws IOException, SerializerException {
    if (!isODataMetadataNone) {
      if (contextURLString != null) {
        json.writeStringField(Constants.JSON_CONTEXT, contextURLString);
      }
      if (metadataETag != null) {
        json.writeStringField(Constants.JSON_METADATA_ETAG, metadataETag);
      }
      if (eTag != null) {
        json.writeStringField(Constants.JSON_ETAG, eTag);
      }
      if(isODataMetadataFull){
        if (type != null) {
          json.writeStringField(Constants.JSON_TYPE, type);
        }
        if (id == null) {
          if (writeNullId) {
            json.writeNullField(Constants.JSON_ID);
          }
        } else {
          json.writeStringField(Constants.JSON_ID, id.toASCIIString());
        }
      }
    }
  }

  private void links(final Linked linked, final EdmEntityType entityType, JsonGenerator json)
      throws IOException, SerializerException {

    for (final Link link : linked.getNavigationLinks()) {
      final String name = link.getTitle();
      for (final Annotation annotation : link.getAnnotations()) {
        valuable(json, annotation, name + '@' + annotation.getTerm(), null, null);
      }

      final EdmEntityType targetType =
          entityType == null || name == null || entityType.getNavigationProperty(name) == null ? null : entityType
              .getNavigationProperty(name).getType();
      if (link.getInlineEntity() != null) {
        json.writeFieldName(name);
        doSerialize(targetType, link.getInlineEntity(), null, null, json);
      } else if (link.getInlineEntitySet() != null) {
        json.writeArrayFieldStart(name);
        for (final Entity subEntry : link.getInlineEntitySet().getEntities()) {
          doSerialize(targetType, subEntry, null, null, json);
        }
        json.writeEndArray();
      }
    }
  }

  private void collection(final JsonGenerator json, final EdmType itemType, final String typeName,
      final EdmProperty edmProperty, final ValueType valueType, final List<?> value)
      throws IOException, SerializerException {

    json.writeStartArray();

    for (final Object item : value) {
      switch (valueType) {
      case COLLECTION_PRIMITIVE:
        primitiveValue(json, (EdmPrimitiveType) itemType, typeName, edmProperty, item);
        break;

      case COLLECTION_GEOSPATIAL:
      case COLLECTION_ENUM:
        throw new SerializerException("Geo and enum types are not supported.", MessageKeys.NOT_IMPLEMENTED);

      case COLLECTION_COMPLEX:
        complexValue(json, (EdmComplexType) itemType, typeName, (ComplexValue) item);
        break;

      default:
      }
    }

    json.writeEndArray();
  }

  protected void primitiveValue(final JsonGenerator json, final EdmPrimitiveType valueType, final String typeName,
      final EdmProperty edmProperty, final Object value) throws IOException, SerializerException {

    EdmPrimitiveType type = valueType;
    if (type == null) {
      final EdmPrimitiveTypeKind kind =
          typeName == null ? EdmTypeInfo.determineTypeKind(value) : new EdmTypeInfo.Builder().setTypeExpression(
              typeName).build().getPrimitiveTypeKind();
      type = kind == null ? null : EdmPrimitiveTypeFactory.getInstance(kind);
    }

    if (value == null) {
      json.writeNull();
    } else if (type == null) {
      throw new SerializerException("The primitive type could not be determined.",
          MessageKeys.INCONSISTENT_PROPERTY_TYPE, "");
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
      json.writeBoolean((Boolean) value);
    } else {
      String serialized = null;
      try {
        serialized = type.valueToString(value,
            edmProperty == null ? null : edmProperty.isNullable(),
            edmProperty == null ? null : edmProperty.getMaxLength(),
            edmProperty == null ? Constants.DEFAULT_PRECISION : edmProperty.getPrecision(),
            edmProperty == null ? Constants.DEFAULT_SCALE : edmProperty.getScale(),
            edmProperty == null ? null : edmProperty.isUnicode());
      } catch (final EdmPrimitiveTypeException e) {
        final String name = edmProperty == null ? "" : edmProperty.getName();
        throw new SerializerException("Wrong value for property '" + name + "'!", e,
            SerializerException.MessageKeys.WRONG_PROPERTY_VALUE, name, value.toString());
      }
      if (isIEEE754Compatible &&
          (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64)
              || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal))
          || type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64)
              && type != EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)) {
        json.writeString(serialized);
      } else {
        json.writeNumber(serialized);
      }
    }
  }

  private void complexValue(final JsonGenerator json, final EdmComplexType valueType, final String typeName,
      final ComplexValue value) throws IOException, SerializerException {
    json.writeStartObject();

    if (typeName != null && isODataMetadataFull) {
      json.writeStringField(Constants.JSON_TYPE, typeName);
    }

    for (final Property property : value.getValue()) {
      final String name = property.getName();
      final EdmProperty edmProperty = valueType == null || valueType.getStructuralProperty(name) == null ? null
          : valueType.getStructuralProperty(name);
      valuable(json, property, name, edmProperty == null ? null : edmProperty.getType(), edmProperty);
    }
    links(value, null, json);

    json.writeEndObject();
  }

  private void value(final JsonGenerator json, final Valuable value, final EdmType type, final EdmProperty edmProperty)
      throws IOException, SerializerException {
    final String typeName = value.getType() == null ? null : new EdmTypeInfo.Builder().setTypeExpression(value
        .getType()).build().external();

    if (value.isNull()) {
      json.writeNull();
    } else if (value.isCollection()) {
      collection(json, type, typeName, edmProperty, value.getValueType(), value.asCollection());
    } else if (value.isPrimitive()) {
      primitiveValue(json, (EdmPrimitiveType) type, typeName, edmProperty, value.asPrimitive());
    } else if (value.isComplex()) {
      complexValue(json, (EdmComplexType) type, typeName, value.asComplex());
    } else if (value.isEnum() || value.isGeospatial()) {
      throw new SerializerException("Geo and enum types are not supported.", MessageKeys.NOT_IMPLEMENTED);
    }
  }

  protected void valuable(JsonGenerator json, final Valuable valuable, final String name, final EdmType type,
      final EdmProperty edmProperty) throws IOException, SerializerException {

    if (isODataMetadataFull
        && !(valuable instanceof Annotation) && !valuable.isComplex()) {

      String typeName = valuable.getType();
      if (typeName == null && type == null && valuable.isPrimitive()) {
        if (valuable.isCollection()) {
          if (!valuable.asCollection().isEmpty()) {
            final EdmPrimitiveTypeKind kind = EdmTypeInfo.determineTypeKind(valuable.asCollection().get(0));
            if (kind != null) {
              typeName = "Collection(" + kind.getFullQualifiedName().getFullQualifiedNameAsString() + ')';
            }
          }
        } else {
          final EdmPrimitiveTypeKind kind = EdmTypeInfo.determineTypeKind(valuable.asPrimitive());
          if (kind != null) {
            typeName = kind.getFullQualifiedName().getFullQualifiedNameAsString();
          }
        }
      }
      
      if (typeName != null) {
        json.writeStringField(name + Constants.JSON_TYPE, constructTypeExpression(typeName));
      }
    }

    for (final Annotation annotation : ((Annotatable) valuable).getAnnotations()) {
      valuable(json, annotation, name + '@' + annotation.getTerm(), null, null);
    }

    json.writeFieldName(name);
    value(json, valuable, type, edmProperty);
  }

  private String constructTypeExpression(String typeName) {
    EdmTypeInfo typeInfo = new EdmTypeInfo.Builder().setTypeExpression(typeName).build();
    StringBuilder stringBuilder = new StringBuilder();

    if (typeInfo.isCollection()) {
      stringBuilder.append("#Collection(");
    } else {
      stringBuilder.append('#');
    }

    stringBuilder.append(typeInfo.isPrimitiveType() ? typeInfo.getFullQualifiedName().getName() : typeInfo
        .getFullQualifiedName().getFullQualifiedNameAsString());

    if (typeInfo.isCollection()) {
      stringBuilder.append(')');
    }

    return stringBuilder.toString();
  }
}
