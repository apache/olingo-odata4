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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.AbstractEntityCollection;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityIterator;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ReferenceSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.serializer.SerializerStreamResult;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.ODataWritableContent;
import org.apache.olingo.server.core.serializer.AbstractODataSerializer;
import org.apache.olingo.server.core.serializer.SerializerResultImpl;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContentTypeHelper;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.apache.olingo.server.core.uri.UriHelperImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ODataJsonSerializer extends AbstractODataSerializer {

  private final boolean isIEEE754Compatible;
  private final boolean isODataMetadataNone;
  private final boolean isODataMetadataFull;

  public ODataJsonSerializer(final ContentType contentType) {
    isIEEE754Compatible = isODataIEEE754Compatible(contentType);
    isODataMetadataNone = ContentTypeHelper.isODataMetadataNone(contentType);
    isODataMetadataFull = ContentTypeHelper.isODataMetadataFull(contentType);
  }

  @Override
  public SerializerResult serviceDocument(final ServiceMetadata metadata, final String serviceRoot)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ServiceDocumentJsonSerializer(metadata, serviceRoot, isODataMetadataNone).writeServiceDocument(json);

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

  @Override
  public SerializerResult metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    throw new SerializerException("Metadata in JSON format not supported!",
        SerializerException.MessageKeys.JSON_METADATA);
  }

  @Override
  public SerializerResult error(final ODataServerError error) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      new ODataErrorSerializer().writeErrorDocument(json, error);

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

  @Override
  public SerializerResult entityCollection(final ServiceMetadata metadata,
      final EdmEntityType entityType, final AbstractEntityCollection entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      writeContextURL(contextURL, json);

      writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeCount(entitySet, json);
      }
      json.writeFieldName(Constants.VALUE);
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, contextURL, null, null, false, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet, contextURL,
            options.getExpand(), options.getSelect(), options.getWriteOnlyReferences(), json);
      }
      writeNextLink(entitySet, json);

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

  @Override
  public SerializerStreamResult entityCollectionStreamed(ServiceMetadata metadata, EdmEntityType entityType,
      EntityIterator entities, EntityCollectionSerializerOptions options) throws SerializerException {

    return ODataWritableContent.with(entities, entityType, this, metadata, options).build();
  }

  public void entityCollectionIntoStream(final ServiceMetadata metadata,
      final EdmEntityType entityType, final EntityIterator entitySet,
      final EntityCollectionSerializerOptions options, final OutputStream outputStream)
      throws SerializerException {

    SerializerException cachedException;
    try {
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      writeContextURL(contextURL, json);

      writeMetadataETag(metadata, json);

      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeCount(entitySet, json);
      }
      json.writeFieldName(Constants.VALUE);
      if (options == null) {
        writeEntitySet(metadata, entityType, entitySet, contextURL, null, null, false, json);
      } else {
        writeEntitySet(metadata, entityType, entitySet, contextURL,
            options.getExpand(), options.getSelect(), options.getWriteOnlyReferences(), json);
      }
      // next link not supported by default for streaming results
//      writeNextLink(entitySet, json);

      json.close();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    }
  }

  @Override
  public SerializerResult entity(final ServiceMetadata metadata, final EdmEntityType entityType,
      final Entity entity, final EntitySerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      writeEntity(metadata, entityType, entity, contextURL,
          options == null ? null : options.getExpand(),
          options == null ? null : options.getSelect(),
          options == null ? false : options.getWriteOnlyReferences(),
          true, json);

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

  ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (isODataMetadataNone) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  protected void writeEntitySet(final ServiceMetadata metadata, final EdmEntityType entityType,
      final AbstractEntityCollection entitySet, final ContextURL contextURL,
      final ExpandOption expand, final SelectOption select,
      final boolean onlyReference, final JsonGenerator json) throws IOException,
      SerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet) {
      if (onlyReference) {
        json.writeStartObject();
        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
        json.writeEndObject();
      } else {
        writeEntity(metadata, entityType, entity, contextURL, expand, select, false, false, json);
      }
    }
    json.writeEndArray();
  }

  private boolean areKeyPredicateNamesSelected(SelectOption select, EdmEntityType type) {
    if (select == null || ExpandSelectHelper.isAll(select)) {
      return true;
    }
    final Set<String> selected = ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (String key : type.getKeyPredicateNames()) {
      if (!selected.contains(key)) {
        return false;
      }
    }
    return true;
  }

  public void writeEntity(final ServiceMetadata metadata, final EdmEntityType entityType, final Entity entity,
      final ContextURL contextURL, final ExpandOption expand, final SelectOption select, final boolean onlyReference,
      final boolean isTopEntity, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();
    if (!isODataMetadataNone) {
      if (isTopEntity) { // top-level entity
        writeContextURL(contextURL, json);
        writeMetadataETag(metadata, json);
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
      }
    }
    if (onlyReference) {
      json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
    } else {
      final EdmEntityType resolvedType = resolveEntityType(metadata, entityType, entity.getType());
      if (!isODataMetadataNone && !resolvedType.equals(entityType)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + entity.getType());
      } else if(isODataMetadataFull) {
        json.writeStringField(Constants.JSON_TYPE,
                "#" + resolvedType.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      if (!isODataMetadataNone && !areKeyPredicateNamesSelected(select, resolvedType)) {
        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
      }
      if (isODataMetadataFull){
        json.writeStringField(Constants.JSON_ID, entity.getId().toASCIIString());
        if (entity.getSelfLink() != null){
          json.writeStringField(Constants.JSON_READ_LINK, entity.getSelfLink().getHref());
        }
        if (entity.getEditLink() != null){
          json.writeStringField(Constants.JSON_EDIT_LINK, entity.getEditLink().getHref());
        }
      }
      writeProperties(metadata, resolvedType, entity.getProperties(), select, json);
      writeNavigationProperties(metadata, resolvedType, entity, contextURL, expand, json);
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

  protected void writeProperties(final ServiceMetadata metadata, final EdmStructuredType type,
      final List<Property> properties,
      final SelectOption select, final JsonGenerator json)
      throws IOException, SerializerException {
    final boolean all = ExpandSelectHelper.isAll(select);
    final Set<String> selected = all ? new HashSet<String>() :
        ExpandSelectHelper.getSelectedPropertyNames(select.getSelectItems());
    for (final String propertyName : type.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = type.getStructuralProperty(propertyName);
        final Property property = findProperty(propertyName, properties);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(select.getSelectItems(), propertyName);
        writeProperty(metadata, edmProperty, property, selectedPaths, json);
      }
    }
  }

  protected void writeNavigationProperties(final ServiceMetadata metadata,
      final EdmStructuredType type, final Linked linked, final ContextURL contextURL, final ExpandOption expand,
      final JsonGenerator json) throws SerializerException, IOException {
    if (ExpandSelectHelper.hasExpand(expand)) {
      final boolean expandAll = ExpandSelectHelper.isExpandAll(expand);
      final Set<String> expanded = expandAll ? new HashSet<String>() :
          ExpandSelectHelper.getExpandedPropertyNames(expand.getExpandItems());
      for (final String propertyName : type.getNavigationPropertyNames()) {
        if (expandAll || expanded.contains(propertyName)) {
          final EdmNavigationProperty property = type.getNavigationProperty(propertyName);
          final Link navigationLink = linked.getNavigationLink(property.getName());
          final ExpandItem innerOptions = expandAll ? null :
              ExpandSelectHelper.getExpandItem(expand.getExpandItems(), propertyName);
          if (innerOptions != null && innerOptions.getLevelsOption() != null) {
            throw new SerializerException("Expand option $levels is not supported.",
                SerializerException.MessageKeys.NOT_IMPLEMENTED);
          }
          writeExpandedNavigationProperty(metadata, property, contextURL, navigationLink,
              innerOptions == null ? null : innerOptions.getExpandOption(),
              innerOptions == null ? null : innerOptions.getSelectOption(),
              innerOptions == null ? null : innerOptions.getCountOption(),
              innerOptions == null ? false : innerOptions.hasCountPath(),
              innerOptions == null ? false : innerOptions.isRef(),
              json);
        }
      }
    } else {
      if (isODataMetadataFull && linked instanceof Entity){
        String entityLink;
        if(linked.getId() != null) {
          entityLink = linked.getId().toString();
        } else {
          EdmEntitySet edmEntitySet=metadata.getEdm().getEntityContainer()
                  .getEntitySet(contextURL.getEntitySetOrSingletonOrType());
          entityLink = new UriHelperImpl().buildCanonicalURL(edmEntitySet, (Entity) linked);
        }
        for (final String propertyName : type.getNavigationPropertyNames()){
          String nav = entityLink + "/" +propertyName;
          String assoc = nav + "/" + ContextURL.Suffix.REFERENCE.getRepresentation();
          json.writeStringField(propertyName + Constants.JSON_ASSOCIATION_LINK, assoc);
          json.writeStringField(propertyName + Constants.JSON_NAVIGATION_LINK, nav);
        }
      }
    }
  }

  protected void writeExpandedNavigationProperty(
      final ServiceMetadata metadata, final EdmNavigationProperty property,
      final ContextURL contextURL,
      final Link navigationLink, final ExpandOption innerExpand,
      final SelectOption innerSelect, final CountOption innerCount,
      final boolean writeOnlyCount, final boolean writeOnlyRef,
      final JsonGenerator json) throws IOException, SerializerException {

    if (property.isCollection()) {
      if (writeOnlyCount) {
        if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
          writeInlineCount(property.getName(), 0, json);
        } else {
          writeInlineCount(property.getName(), navigationLink.getInlineEntitySet().getCount(), json);
        }
      } else {
        if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
          if (innerCount != null && innerCount.getValue()) {
            writeInlineCount(property.getName(), 0, json);
          }
          json.writeFieldName(property.getName());
          json.writeStartArray();
          json.writeEndArray();
        } else {
          if (innerCount != null && innerCount.getValue()) {
            writeInlineCount(property.getName(), navigationLink.getInlineEntitySet().getCount(), json);
          }
          json.writeFieldName(property.getName());
          writeEntitySet(metadata, property.getType(), navigationLink.getInlineEntitySet(), contextURL, innerExpand,
              innerSelect, writeOnlyRef, json);
        }
      }
    } else {
      json.writeFieldName(property.getName());
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(metadata, property.getType(), navigationLink.getInlineEntity(), contextURL,
            innerExpand, innerSelect, writeOnlyRef, false, json);
      }
    }
  }

  protected void writeProperty(final ServiceMetadata metadata, final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    if (isODataMetadataFull) {
      String key = edmProperty.getName() + Constants.JSON_TYPE;
      if (edmProperty.isPrimitive()) {
        if (edmProperty.isCollection()) {
          json.writeStringField(key, "#Collection(" + edmProperty.getType().getName() + ")");
        }else{
          json.writeStringField(key, "#" + edmProperty.getType().getName());
        }
      } else {
        if (edmProperty.isCollection()) {
          json.writeStringField(key,
                  "#Collection(" + edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString() + ")");
        } else {
          json.writeStringField(key, "#" + edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());
        }
      }
    }
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new SerializerException("Non-nullable property not present!",
            SerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        if (edmProperty.isCollection()) {
          json.writeStartArray();
          json.writeEndArray();
        } else {
          json.writeNull();
        }
      }
    } else {
      writePropertyValue(metadata, edmProperty, property, selectedPaths, json);
    }
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
          writeComplexValue(metadata, property, (EdmComplexType) type, property.asComplex().getValue(), selectedPaths,
              json);
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
          writePrimitiveValue(type, value, isNullable, maxLength, precision, scale, isUnicode, json);
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
        writeComplexValue(metadata, property, type, ((ComplexValue) value).getValue(), selectedPaths, json);
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
    } else {
      json.writeString(value);
    }
  }

  protected void writeComplexValue(final ServiceMetadata metadata, final Property complexProperty,
      final EdmComplexType type, final List<Property> properties,
      final Set<List<String>> selectedPaths, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartObject();

    final EdmComplexType resolvedType = resolveComplexType(metadata,
        type, complexProperty.getType());
    if (!isODataMetadataNone && !resolvedType.equals(type)) {
      json.writeStringField(Constants.JSON_TYPE,
          "#" + complexProperty.getType());
    } else if (isODataMetadataFull){
      json.writeStringField(Constants.JSON_TYPE, "#" + type.getFullQualifiedName().getFullQualifiedNameAsString());
    }

    for (final String propertyName : resolvedType.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || ExpandSelectHelper.isSelected(selectedPaths, propertyName)) {
        writeProperty(metadata, (EdmProperty) resolvedType.getProperty(propertyName), property,
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
  public SerializerResult primitive(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (property.isNull()) {
        throw new SerializerException("Property value can not be null.", SerializerException.MessageKeys.NULL_INPUT);
      } else {
        if (isODataMetadataFull){
          json.writeStringField(Constants.JSON_TYPE, "#" + type.getName());
        }
        json.writeFieldName(Constants.VALUE);
        writePrimitive(type, property,
            options == null ? null : options.isNullable(),
            options == null ? null : options.getMaxLength(),
            options == null ? null : options.getPrecision(),
            options == null ? null : options.getScale(),
            options == null ? null : options.isUnicode(), json);
      }
      json.writeEndObject();

      json.close();
      outputStream.close();
      return SerializerResultImpl.with().content(buffer.getInputStream()).build();
    } catch (final IOException e) {
      cachedException =
          new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
      throw cachedException;
    } catch (final EdmPrimitiveTypeException e) {
      cachedException = new SerializerException("Wrong value for property!", e,
          SerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
          property.getName(), property.getValue().toString());
      throw cachedException;
    } finally {
      closeCircleStreamBufferOutput(outputStream, cachedException);
    }
  }

  @Override
  public SerializerResult complex(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      final EdmComplexType resolvedType = resolveComplexType(metadata, type, property.getType());
      if (!isODataMetadataNone && !resolvedType.equals(type)) {
        json.writeStringField(Constants.JSON_TYPE, "#" + property.getType());
      } else if (isODataMetadataFull) {
        json.writeStringField(Constants.JSON_TYPE, "#" + type.getFullQualifiedName().getFullQualifiedNameAsString());
      }
      final List<Property> values =
          property.isNull() ? Collections.<Property> emptyList() : property.asComplex().getValue();
      writeProperties(metadata, type, values, options == null ? null : options.getSelect(), json);
      if (!property.isNull() && property.isComplex()) {
        writeNavigationProperties(metadata, type, property.asComplex(), contextURL,
            options == null ? null : options.getExpand(), json);
      }
      json.writeEndObject();

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

  @Override
  public SerializerResult primitiveCollection(final ServiceMetadata metadata, final EdmPrimitiveType type,
      final Property property, final PrimitiveSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (isODataMetadataFull){
        json.writeStringField(Constants.JSON_TYPE, "#Collection("+ type.getName() +")");
      }
      json.writeFieldName(Constants.VALUE);
      writePrimitiveCollection(type, property,
          options == null ? null : options.isNullable(),
          options == null ? null : options.getMaxLength(),
          options == null ? null : options.getPrecision(),
          options == null ? null : options.getScale(),
          options == null ? null : options.isUnicode(), json);
      json.writeEndObject();

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

  @Override
  public SerializerResult complexCollection(final ServiceMetadata metadata, final EdmComplexType type,
      final Property property, final ComplexSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;
    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      outputStream = buffer.getOutputStream();
      JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();
      writeContextURL(contextURL, json);
      writeMetadataETag(metadata, json);
      if (isODataMetadataFull){
        json.writeStringField(Constants.JSON_TYPE,
                "#Collection(" + type.getFullQualifiedName().getFullQualifiedNameAsString() + ")");
      }
      json.writeFieldName(Constants.VALUE);
      writeComplexCollection(metadata, type, property, null, json);
      json.writeEndObject();

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

  @Override
  public SerializerResult reference(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final Entity entity, final ReferenceSerializerOptions options) throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);

      json.writeStartObject();
      writeContextURL(contextURL, json);
      json.writeStringField(Constants.JSON_ID, uriHelper.buildCanonicalURL(edmEntitySet, entity));
      json.writeEndObject();

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

  @Override
  public SerializerResult referenceCollection(final ServiceMetadata metadata, final EdmEntitySet edmEntitySet,
      final AbstractEntityCollection entityCollection, final ReferenceCollectionSerializerOptions options)
      throws SerializerException {
    OutputStream outputStream = null;
    SerializerException cachedException = null;

    try {
      final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
      CircleStreamBuffer buffer = new CircleStreamBuffer();
      final UriHelper uriHelper = new UriHelperImpl();
      outputStream = buffer.getOutputStream();
      final JsonGenerator json = new JsonFactory().createGenerator(outputStream);
      json.writeStartObject();

      writeContextURL(contextURL, json);
      if (options != null && options.getCount() != null && options.getCount().getValue()) {
        writeCount(entityCollection, json);
      }

      json.writeArrayFieldStart(Constants.VALUE);
      for (final Entity entity : entityCollection) {
        json.writeStartObject();
        json.writeStringField(Constants.JSON_ID, uriHelper.buildCanonicalURL(edmEntitySet, entity));
        json.writeEndObject();
      }
      json.writeEndArray();

      writeNextLink(entityCollection, json);

      json.writeEndObject();

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

  void writeContextURL(final ContextURL contextURL, final JsonGenerator json) throws IOException {
    if (!isODataMetadataNone && contextURL != null) {
      json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
    }
  }

  void writeMetadataETag(final ServiceMetadata metadata, final JsonGenerator json) throws IOException {
    if (!isODataMetadataNone
        && metadata != null
        && metadata.getServiceMetadataETagSupport() != null
        && metadata.getServiceMetadataETagSupport().getMetadataETag() != null) {
      json.writeStringField(Constants.JSON_METADATA_ETAG,
          metadata.getServiceMetadataETagSupport().getMetadataETag());
    }
  }

  void writeCount(final AbstractEntityCollection entityCollection, final JsonGenerator json) throws IOException {
    if (entityCollection.getCount() != null) {
      if (isIEEE754Compatible) {
        json.writeStringField(Constants.JSON_COUNT, entityCollection.getCount().toString());
      } else {
        json.writeNumberField(Constants.JSON_COUNT, entityCollection.getCount());
      }
    }
  }

  void writeInlineCount(final String propertyName,
      final Integer count, final JsonGenerator json)
      throws IOException {
    if (count != null) {
      if (isIEEE754Compatible) {
        json.writeStringField(propertyName + Constants.JSON_COUNT, String.valueOf(count));
      } else {
        json.writeNumberField(propertyName + Constants.JSON_COUNT, count);
      }
    }
  }

  void writeNextLink(final AbstractEntityCollection entitySet, final JsonGenerator json) throws IOException {
    if (entitySet.getNext() != null) {
      json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
    }
  }

  private boolean isODataIEEE754Compatible(final ContentType contentType) {
    return contentType.getParameters().containsKey(ContentType.PARAMETER_IEEE754_COMPATIBLE)
        && Boolean.TRUE.toString().equalsIgnoreCase(
            contentType.getParameter(ContentType.PARAMETER_IEEE754_COMPATIBLE).toLowerCase());
  }
}
