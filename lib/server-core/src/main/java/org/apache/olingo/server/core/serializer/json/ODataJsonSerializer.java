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
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ClientServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.olingo.server.core.serializer.utils.JsonEntityMetadataBuilder;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class ODataJsonSerializer implements ODataSerializer {

  private static final Logger log = LoggerFactory.getLogger(ODataJsonSerializer.class);

  private final ODataFormat format;

  public ODataJsonSerializer(final ODataFormat format) {
    this.format = format;
  }

  @Override
  public SerializerResult serviceDocument(final Edm edm, final String serviceRoot) throws SerializerException {
    CircleStreamBuffer buffer;
    JsonGenerator gen = null;

    try {
      buffer = new CircleStreamBuffer();
      gen = new JsonFactory().createGenerator(buffer.getOutputStream())
          .setPrettyPrinter(new DefaultPrettyPrinter());

      new ServiceDocumentJsonSerializer(edm, serviceRoot).writeServiceDocument(gen);

      gen.close();

      return SerializerResultImpl.with().content(buffer.getInputStream()).build();

    } catch (final IOException e) {
      log.error(e.getMessage(), e);
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    } finally {
      if (gen != null) {
        try {
          gen.close();
        } catch (IOException e) {
          throw new SerializerException("An I/O exception occurred.", e,
              SerializerException.MessageKeys.IO_EXCEPTION);
        }
      }
    }
  }

  @Override
  public SerializerResult metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    throw new SerializerException("Metadata in JSON format not supported!",
        SerializerException.MessageKeys.JSON_METADATA);
  }

  @Override
  public SerializerResult error(final ClientServerError error) throws SerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      new ODataErrorSerializer().writeErrorDocument(json, error);
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final EntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
    if (format == ODataFormat.JSON_FULL_METADATA){
      JsonEntityMetadataBuilder.setContextURL(options.getContextURL());
    }
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT,
            ContextURLBuilder.create(contextURL).toASCIIString());
      }

      if (options != null && options.getCount() != null && options.getCount().getValue()
          && entitySet.getCount() != null) {
        json.writeNumberField(Constants.JSON_COUNT, entitySet.getCount());
      }
      json.writeFieldName(Constants.VALUE);
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, null, null, false, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet,
            options.getExpand(), options.getSelect(), options.onlyReferences(), json);
      }
      if (entitySet.getNext() != null) {
        json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
      }
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    if (format == ODataFormat.JSON_FULL_METADATA){
      JsonEntityMetadataBuilder.setContextURL(options.getContextURL());
    }
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          options == null ? null : options.getSelect(),
          options == null ? false : options.onlyReferences(), json);
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  private ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (format == ODataFormat.JSON_NO_METADATA) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final EntityCollection entitySet, final ExpandOption expand, final SelectOption select,
      final boolean onlyReference, final JsonGenerator json) throws IOException,
      SerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet.getEntities()) {
      if (onlyReference) {
        json.writeStartObject();
        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
        json.writeEndObject();
      } else {
        writeEntity(metadata, entityType, entity, null, expand, select, false, json);
      }
    }
    json.writeEndArray();
  }

  protected void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final ContextURL contextURL, final ExpandOption expand,
      final SelectOption select, final boolean onlyReference, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    if (format != ODataFormat.JSON_NO_METADATA) {
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      if (entity.getETag() != null) {
        json.writeStringField(Constants.JSON_ETAG, entity.getETag());
      }
      if (entityType.hasStream()) {
        if (entity.getMediaETag() != null) {
          json.writeStringField(Constants.JSON_MEDIA_ETAG, entity.getMediaETag());
        }
        if (entity.getMediaContentType() != null) {
          json.writeStringField(Constants.JSON_MEDIA_CONTENT_TYPE, entity.getMediaContentType());
        }
        if (entity.getMediaContentSource() != null) {
          json.writeStringField(Constants.JSON_MEDIA_READ_LINK, entity.getMediaContentSource().toString());
        }
        if (entity.getMediaEditLinks() != null && !entity.getMediaEditLinks().isEmpty()) {
          json.writeStringField(Constants.JSON_MEDIA_EDIT_LINK, entity.getMediaEditLinks().get(0).getHref());
        }
        if (format == ODataFormat.JSON_FULL_METADATA){
          json.writeStringField(Constants.JSON_MEDIA_READ_LINK,
                  JsonEntityMetadataBuilder.getMediaReadLinkValue(metadata,entity));
          json.writeStringField(Constants.JSON_MEDIA_EDIT_LINK,
                  JsonEntityMetadataBuilder.getMediaEditLinkValue(metadata,entity));
        }
      }
    }

    if (onlyReference) {
      json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
    } else {
      EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
      if (!resolvedType.equals(entityType)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + entity.getType());
      }
      if (format == ODataFormat.JSON_FULL_METADATA){
        json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getEntityTypeValue(resolvedType));
        json.writeStringField(Constants.JSON_ID,
                    JsonEntityMetadataBuilder.getEntityIDValue(metadata, entity));
        if (entity.getSelfLink()!=null){
          json.writeStringField(Constants.JSON_READ_LINK,
                  JsonEntityMetadataBuilder.getEntityReadLinkValue(entity));
        }
        if (entity.getEditLink()!=null){
          json.writeStringField(Constants.JSON_EDIT_LINK,
                  JsonEntityMetadataBuilder.getEntityEditLinkValue(entity));
        }
      }
      writeProperties(resolvedType, entity.getProperties(), select, json);
      writeNavigationProperties(metadata, resolvedType, entity, expand, contextURL, json);
      json.writeEndObject();
    }
  }

  protected EdmEntityType resolveEntityType(final ServiceMetadata metadata, final EdmEntityType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmEntityType derivedType = metadata.getEdm().getEntityType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("EntityType not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmEntityType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected EdmComplexType resolveComplexType(final ServiceMetadata metadata, final EdmComplexType baseType,
      final String derivedTypeName) throws SerializerException {
    if (derivedTypeName == null ||
        baseType.getFullQualifiedName().getFullQualifiedNameAsString().equals(derivedTypeName)) {
      return baseType;
    }
    EdmComplexType derivedType = metadata.getEdm().getComplexType(new FullQualifiedName(derivedTypeName));
    if (derivedType == null) {
      throw new SerializerException("Complex Type not found",
          SerializerException.MessageKeys.UNKNOWN_TYPE, derivedTypeName);
    }
    EdmComplexType type = derivedType.getBaseType();
    while (type != null) {
      if (type.getFullQualifiedName().getFullQualifiedNameAsString()
          .equals(baseType.getFullQualifiedName().getFullQualifiedNameAsString())) {
        return derivedType;
      }
      type = type.getBaseType();
    }
    throw new SerializerException("Wrong base type",
        SerializerException.MessageKeys.WRONG_BASE_TYPE, derivedTypeName, baseType
            .getFullQualifiedName().getFullQualifiedNameAsString());
  }

  protected void writeProperties(final EdmStructuredType type, final List<Property> properties,
      final SelectOption select, final JsonGenerator json) throws IOException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? null :
        ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(edmProperty, property, selectedPaths, json);
      }
    }
  }

  protected void writeNavigationProperties(final ServiceMetadata metadata,
      final EdmStructuredType type, final Linked linked, final ExpandOption expand,final ContextURL contextURL,
      final JsonGenerator json) throws SerializerException, IOException {
    if (ExpandSelectHelper.hasExpand(expand)) {
      final boolean expandAll = ExpandSelectHelper.isExpandAll(expand);
      final Set<String> expanded = expandAll ? null :
          ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
      for (final String propertyName : type.getNavigationPropertyNames()) {
        if (expandAll || expanded.contains(propertyName)) {
          final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
          final Link navigationLink = linked.getNavigationLink(property.getName());
          final ExpandItem innerOptions = expandAll ? null :
              ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
          if (innerOptions != null && (innerOptions.isRef() || innerOptions.getLevelsOption() != null)) {
            throw new SerializerException("Expand options $ref and $levels are not supported.",
                SerializerException.MessageKeys.NOT_IMPLEMENTED);
          }
          writeExpandedNavigationProperty(metadata, property, navigationLink,
              innerOptions == null ? null : innerOptions.getExpandOption(),
              innerOptions == null ? null : innerOptions.getSelectOption(),
              json);
        }
      }
    }else {
      if (format == ODataFormat.JSON_FULL_METADATA){
        for (final String propertyName : type.getNavigationPropertyNames()){
          json.writeStringField(JsonEntityMetadataBuilder.getAssociationPropertyKey(propertyName),
                     JsonEntityMetadataBuilder.getAssociationPropertyValue(metadata, propertyName, linked));
          json.writeStringField(JsonEntityMetadataBuilder.getNavigationPropertyKey(propertyName),
                     JsonEntityMetadataBuilder.getNavigationPropertyValue(metadata, propertyName, linked));
        }
      }
    }
  }

  protected void writeExpandedNavigationProperty(final ServiceMetadata metadata,
      final EdmNavigationProperty property, final Link navigationLink,
      final ExpandOption innerExpand, final SelectOption innerSelect, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeFieldName(property.getName());
    if (property.isCollection()) {
      if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
        json.writeStartArray();
        json.writeEndArray();
      } else {
        writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), innerExpand,
            innerSelect, false, json);
      }
    } else {
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), null,
            innerExpand, innerSelect, false, json);
      }
    }
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json) throws IOException, SerializerException {
    if (format == ODataFormat.JSON_FULL_METADATA) {
      if (edmProperty.isPrimitive()) {
        if (edmProperty.isCollection()) {
          json.writeStringField(JsonEntityMetadataBuilder.getPropertyTypeKey(edmProperty),
                          JsonEntityMetadataBuilder.getPrimitiveCollectionTypeValue(edmProperty));
        }else{
          json.writeStringField(JsonEntityMetadataBuilder.getPropertyTypeKey(edmProperty),
                          JsonEntityMetadataBuilder.getPrimitivePropertyTypeValue(edmProperty));
        }
      } else {
        if (edmProperty.isCollection()) {
          json.writeStringField(JsonEntityMetadataBuilder.getPropertyTypeKey(edmProperty),
                          JsonEntityMetadataBuilder.getComplexCollectionTypeValue(edmProperty));
        }else{
          json.writeStringField(JsonEntityMetadataBuilder.getPropertyTypeKey(edmProperty),
                          JsonEntityMetadataBuilder.getComplexPropertyTypeValue(edmProperty));
        }
      }
    }
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        json.writeNull();
      }
    } else {
      writePropertyValue(edmProperty, property, selectedPaths, json);
    }
  }

  private void writePropertyValue(final EdmProperty edmProperty,
      final Property property, final Set<List<String>> selectedPaths,
      final JsonGenerator json) throws IOException, SerializerException {
    try {
      if (edmProperty.isPrimitive()) {
        if (edmProperty.isCollection()) {
          writePrimitiveCollection((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              json);
        } else {
          writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
              edmProperty.isNullable(), edmProperty.getMaxLength(),
              edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
              json);
        }
      } else if (edmProperty.isCollection()) {
        writeComplexCollection((EdmComplexType) edmProperty.getType(), property, selectedPaths, json);
      } else if (property.isComplex()) {
        writeComplexValue((EdmComplexType) edmProperty.getType(), property.asComplex().getValue(),
            selectedPaths, json);
      } else if (property.isEnum()) {
        writePrimitive((EdmPrimitiveType) edmProperty.getType(), property,
            edmProperty.isNullable(), edmProperty.getMaxLength(),
            edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(),
            json);
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

  private void writePrimitiveCollection(final EdmPrimitiveType type, final Property property,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode,
      final JsonGenerator json) throws IOException, EdmPrimitiveTypeException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
        writePrimitiveValue(type, value, isNullable, maxLength, precision, scale, isUnicode, json);
        break;
      case COLLECTION_GEOSPATIAL:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      case COLLECTION_ENUM:
        json.writeString(value.toString());
        break;
      default:
        throw new SerializerException("Property type not yet supported!",
            SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
      }
    }
    json.writeEndArray();
  }

  private void writeComplexCollection(final EdmComplexType type, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException, SerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_COMPLEX:
        writeComplexValue(type, ((ComplexValue) value).getValue(), selectedPaths, json);
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
      writePrimitiveValue(type, property.asPrimitive(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else if (property.isGeospatial()) {
      throw new SerializerException("Property type not yet supported!",
          SerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, property.getName());
    } else if (property.isEnum()) {
      writePrimitiveValue(type, property.asEnum(),
          isNullable, maxLength, precision, scale, isUnicode, json);
    } else {
      throw new SerializerException("Inconsistent property type!",
          SerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, property.getName());
    }
  }

  protected void writePrimitiveValue(final EdmPrimitiveType type, final Object primitiveValue,
      final Boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final Boolean isUnicode,
      final JsonGenerator json) throws EdmPrimitiveTypeException, IOException {
    final String value = type.valueToString(primitiveValue,
        isNullable, maxLength, precision, scale, isUnicode);
    if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)) {
      json.writeBoolean(Boolean.parseBoolean(value));
    } else if (type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Byte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Double)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int16)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int32)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)
        || type == EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Single)) {
      json.writeNumber(value);
    } else {
      json.writeString(value);
    }
  }

  protected void writeComplexValue(final EdmComplexType type, final List<Property> properties,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException, SerializerException {
    json.writeStartObject();
    if (format == ODataFormat.JSON_FULL_METADATA){
      json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getComplexPropertyTypeValue(type));
    }
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty((EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : ExpandSelectHelper.getReducedSelectedPaths(selectedPaths, propertyName),
            json);
      }
    }
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

  @Override
  public SerializerResult primitive(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      if (format == ODataFormat.JSON_FULL_METADATA){
        json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getPrimitivePropertyTypeValue(type));
      }
      if (property.isNull()) {
        throw new SerializerException("Property value can not be null.", SerializerException.MessageKeys.NULL_INPUT);
      } else {
        json.writeFieldName(Constants.VALUE);
        writePrimitive(type, property,
            options.isNullable(), options.getMaxLength(), options.getPrecision(), options.getScale(),
            options.isUnicode(),
            json);
      }
      json.writeEndObject();
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  @Override
  public SerializerResult complex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      EdmComplexType resolvedType = resolveComplexType(metadata, type, property.getType());
      if (!resolvedType.equals(type)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + property.getType());
      }
      if (format == ODataFormat.JSON_FULL_METADATA){
        json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getComplexPropertyTypeValue(resolvedType));
      }
      final List<Property> values =
          property.isNull() ? Collections.<Property> emptyList() : property.asComplex().getValue();
      writeProperties(type, values, options == null ? null : options.getSelect(), json);
      if (!property.isNull() && property.isComplex()) {
        writeNavigationProperties(metadata, type, property.asComplex(),
            options == null ? null : options.getExpand(),contextURL, json);
      }
      json.writeEndObject();
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  @Override
  public SerializerResult primitiveCollection(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      if (format == ODataFormat.JSON_FULL_METADATA){
        json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getPrimitiveCollectionTypeValue(type));
      }
      json.writeFieldName(Constants.VALUE);
      writePrimitiveCollection(type, property,
          options.isNullable(), options.getMaxLength(), options.getPrecision(), options.getScale(),
          options.isUnicode(),
          json);
      json.writeEndObject();
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }

  @Override
  public SerializerResult complexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      if (format == ODataFormat.JSON_FULL_METADATA){
        json.writeStringField(Constants.JSON_TYPE, JsonEntityMetadataBuilder.getComplexCollectionTypeValue(type));
      }
      json.writeFieldName(Constants.VALUE);
      writeComplexCollection(type, property, null, json);
      json.writeEndObject();
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    } catch (final EdmPrimitiveTypeException e) {
      throw new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
    }
    return SerializerResultImpl.with().content(buffer.getInputStream()).build();
  }
}
