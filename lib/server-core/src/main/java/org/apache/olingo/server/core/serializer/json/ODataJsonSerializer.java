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
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
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
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.ComplexSerializerOptions;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.PrimitiveSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
import org.apache.olingo.server.core.serializer.utils.ExpandSelectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  public InputStream serviceDocument(final Edm edm, final String serviceRoot) throws SerializerException {
    CircleStreamBuffer buffer;
    JsonGenerator gen = null;

    // TODO: move stream initialization into separate method
    try {
      buffer = new CircleStreamBuffer();
      gen = new JsonFactory().createGenerator(buffer.getOutputStream())
          .setPrettyPrinter(new DefaultPrettyPrinter());

      new ServiceDocumentJsonSerializer(edm, serviceRoot).writeServiceDocument(gen);

      gen.close();

      // TODO: Check correct stream handling
      // writer.flush();
      // buffer.closeWrite();

      return buffer.getInputStream();

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
  public InputStream metadataDocument(final ServiceMetadata serviceMetadata) throws SerializerException {
    throw new SerializerException("Metadata in JSON format not supported!",
        SerializerException.MessageKeys.JSON_METADATA);
  }

  @Override
  public InputStream error(final ODataServerError error) throws SerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      new ODataErrorSerializer().writeErrorDocument(json, error);
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entityCollection(final EdmEntityType entityType, final EntitySet entitySet,
      final EntityCollectionSerializerOptions options) throws SerializerException {
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
      writeEntitySet(entityType, entitySet,
          options == null ? null : options.getExpand(), options == null ? null : options.getSelect(), json);
      if (entitySet.getNext() != null) {
        json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
      }
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entity(final EdmEntityType entityType, final Entity entity,
      final EntitySerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      writeEntity(entityType, entity, contextURL,
          options == null ? null : options.getExpand(), options == null ? null : options.getSelect(), json);
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  private ContextURL checkContextURL(final ContextURL contextURL) throws SerializerException {
    if (format == ODataFormat.JSON_NO_METADATA) {
      return null;
    } else if (contextURL == null) {
      throw new SerializerException("ContextURL null!", SerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    return contextURL;
  }

  protected void writeEntitySet(final EdmEntityType entityType, final EntitySet entitySet,
      final ExpandOption expand, final SelectOption select, final JsonGenerator json)
      throws IOException, SerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet.getEntities()) {
      writeEntity(entityType, entity, null, expand, select, json);
    }
    json.writeEndArray();
  }

  protected void writeEntity(final EdmEntityType entityType, final Entity entity, final ContextURL contextURL,
      final ExpandOption expand, final SelectOption select, final JsonGenerator json)
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
      }
    }
    writeProperties(entityType, entity.getProperties(), select, json);
    writeNavigationProperties(entityType, entity, expand, json);
    json.writeEndObject();
  }

  protected void writeProperties(final EdmStructuredType type, final List<Property> properties,
      final SelectOption select, JsonGenerator json) throws IOException, SerializerException {
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

  protected void writeNavigationProperties(final EdmStructuredType type, final Linked linked,
      final ExpandOption expand, final JsonGenerator json) throws SerializerException, IOException {
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
          writeExpandedNavigationProperty(property, navigationLink,
              innerOptions == null ? null : innerOptions.getExpandOption(),
              innerOptions == null ? null : innerOptions.getSelectOption(),
              json);
        }
      }
    }
  }

  protected void writeExpandedNavigationProperty(final EdmNavigationProperty property, final Link navigationLink,
      final ExpandOption innerExpand, final SelectOption innerSelect, JsonGenerator json)
      throws IOException, SerializerException {
    json.writeFieldName(property.getName());
    if (property.isCollection()) {
      if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
        json.writeStartArray();
        json.writeEndArray();
      } else {
        writeEntitySet(property.getType(), navigationLink.getInlineEntitySet(), innerExpand, innerSelect, json);
      }
    } else {
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(property.getType(), navigationLink.getInlineEntity(), null, innerExpand, innerSelect, json);
      }
    }
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json) throws IOException, SerializerException {
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
      JsonGenerator json) throws IOException, EdmPrimitiveTypeException, SerializerException {
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
      final Set<List<String>> selectedPaths, JsonGenerator json)
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
      final Boolean isUnicode, JsonGenerator json)
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
      final Set<List<String>> selectedPaths, JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException, SerializerException {
    json.writeStartObject();
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
  public InputStream primitive(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
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
    return buffer.getInputStream();
  }

  @Override
  public InputStream complex(final EdmComplexType type, final Property property,
      final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
      }
      final List<Property> values =
          property.isNull() ? Collections.<Property> emptyList() : property.asComplex().getValue();
      writeProperties(type, values, options == null ? null : options.getSelect(), json);
      if (!property.isNull() && property.isComplex()) {
        writeNavigationProperties(type, property.asComplex(),
            options == null ? null : options.getExpand(), json);
      }
      json.writeEndObject();
      json.close();
    } catch (final IOException e) {
      throw new SerializerException("An I/O exception occurred.", e,
          SerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream primitiveCollection(final EdmPrimitiveType type, final Property property,
      final PrimitiveSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
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
    return buffer.getInputStream();
  }

  @Override
  public InputStream complexCollection(final EdmComplexType type, final Property property,
      final ComplexSerializerOptions options) throws SerializerException {
    final ContextURL contextURL = checkContextURL(options == null ? null : options.getContextURL());
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
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
    return buffer.getInputStream();
  }
}
