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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerException;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
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
  public InputStream serviceDocument(final Edm edm, final String serviceRoot) throws ODataSerializerException {
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
      throw new ODataSerializerException("An I/O exception occurred.", e,
          ODataSerializerException.MessageKeys.IO_EXCEPTION);
    } finally {
      if (gen != null) {
        try {
          gen.close();
        } catch (IOException e) {
          throw new ODataSerializerException("An I/O exception occurred.", e,
              ODataSerializerException.MessageKeys.IO_EXCEPTION);
        }
      }
    }
  }

  @Override
  public InputStream metadataDocument(final Edm edm) throws ODataSerializerException {
    throw new ODataSerializerException("Metadata in JSON format not supported!",
        ODataSerializerException.MessageKeys.JSON_METADATA);
  }

  @Override
  public InputStream error(final ODataServerError error) throws ODataSerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      new ODataErrorSerializer().writeErrorDocument(json, error);
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e,
          ODataSerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entitySet(final EdmEntitySet edmEntitySet, final EntitySet entitySet,
      final ContextURL contextURL, final ExpandItem options) throws ODataSerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (format != ODataFormat.JSON_NO_METADATA) {
        if (contextURL == null) {
          throw new ODataSerializerException("ContextURL null!",
              ODataSerializerException.MessageKeys.NO_CONTEXT_URL);
        } else {
          json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
        }
      }
      if (entitySet.getCount() != null) {
        json.writeNumberField(Constants.JSON_COUNT, entitySet.getCount());
      }
      json.writeFieldName(Constants.VALUE);
      writeEntitySet(edmEntitySet.getEntityType(), entitySet, options, json);
      if (entitySet.getNext() != null) {
        json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
      }
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e,
          ODataSerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entity(final EdmEntitySet edmEntitySet, final Entity entity, final ContextURL contextURL,
      final ExpandItem options) throws ODataSerializerException {
    if (format != ODataFormat.JSON_NO_METADATA && contextURL == null) {
      throw new ODataSerializerException("ContextURL null!",
          ODataSerializerException.MessageKeys.NO_CONTEXT_URL);
    }
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      writeEntity(edmEntitySet.getEntityType(), entity, contextURL, options, json);
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e,
          ODataSerializerException.MessageKeys.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  protected void writeEntitySet(final EdmEntityType entityType, final EntitySet entitySet,
      final ExpandItem options, final JsonGenerator json) throws IOException, ODataSerializerException {
    json.writeStartArray();
    for (final Entity entity : entitySet.getEntities()) {
      writeEntity(entityType, entity, null, options, json);
    }
    json.writeEndArray();
  }

  protected void writeEntity(final EdmEntityType entityType, final Entity entity, final ContextURL contextURL,
      final ExpandItem options, final JsonGenerator json) throws IOException, ODataSerializerException {
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
    writeProperties(entityType, entity, options, json);
    writeNavigationProperties(entityType, entity, options, json);
    json.writeEndObject();
  }

  protected void writeProperties(final EdmEntityType entityType, final Entity entity, final ExpandItem options,
      final JsonGenerator json) throws IOException, ODataSerializerException {
    final boolean all = ExpandSelectHelper.isAll(options);
    final Set<String> selected = all ? null :
        ExpandSelectHelper.getSelectedPropertyNames(options.getSelectOption().getSelectItems());
    for (final String propertyName : entityType.getPropertyNames()) {
      if (all || selected.contains(propertyName)) {
        final EdmProperty edmProperty = (EdmProperty) entityType.getProperty(propertyName);
        final Property property = entity.getProperty(propertyName);
        final Set<List<String>> selectedPaths = all || edmProperty.isPrimitive() ? null :
            ExpandSelectHelper.getSelectedPaths(options.getSelectOption().getSelectItems(), propertyName);
        writeProperty(edmProperty, property, selectedPaths, json);
      }
    }
  }

  protected void writeNavigationProperties(final EdmEntityType entityType, final Entity entity,
      final ExpandItem options, final JsonGenerator json) throws ODataSerializerException, IOException {
    if (options != null && (options.isRef() || options.getLevelsOption() != null)) {
      throw new ODataSerializerException("Expand options $ref and $levels are not supported.",
          ODataSerializerException.MessageKeys.NOT_IMPLEMENTED);
    }
    if (ExpandSelectHelper.hasExpand(options)) {
      final boolean expandAll = ExpandSelectHelper.isExpandAll(options);
      final Set<String> expanded = expandAll ? null :
          ExpandSelectHelper.getExpandedPropertyNames(options.getExpandOption().getExpandItems());
      for (final String propertyName : entityType.getNavigationPropertyNames()) {
        if (expandAll || expanded.contains(propertyName)) {
          final EdmNavigationProperty property = entityType.getNavigationProperty(propertyName);
          final Link navigationLink = entity.getNavigationLink(property.getName());
          final ExpandItem innerOptions = expandAll ? null :
              ExpandSelectHelper.getExpandItem(options.getExpandOption().getExpandItems(), propertyName);
          writeExpandedNavigationProperty(property, navigationLink, innerOptions, json);
        }
      }
    }
  }

  protected void writeExpandedNavigationProperty(final EdmNavigationProperty property, final Link navigationLink,
      final ExpandItem innerOptions, JsonGenerator json) throws IOException, ODataSerializerException {
    json.writeFieldName(property.getName());
    if (property.isCollection()) {
      if (navigationLink == null || navigationLink.getInlineEntitySet() == null) {
        json.writeStartArray();
        json.writeEndArray();
      } else {
        writeEntitySet(property.getType(), navigationLink.getInlineEntitySet(), innerOptions, json);
      }
    } else {
      if (navigationLink == null || navigationLink.getInlineEntity() == null) {
        json.writeNull();
      } else {
        writeEntity(property.getType(), navigationLink.getInlineEntity(), null, innerOptions, json);
      }
    }
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, final JsonGenerator json) throws IOException, ODataSerializerException {
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new ODataSerializerException("Non-nullable property not present!",
            ODataSerializerException.MessageKeys.MISSING_PROPERTY, edmProperty.getName());
      } else {
        json.writeNull();
      }
    } else {
      try {
        if (edmProperty.isCollection()) {
          writeCollection(edmProperty, property, selectedPaths, json);
        } else if (edmProperty.isPrimitive()) {
          writePrimitive(edmProperty, property, json);
        } else if (property.isLinkedComplex()) {
          writeComplexValue(edmProperty, property.asLinkedComplex().getValue(), selectedPaths, json);
        } else if (property.isComplex()) {
          writeComplexValue(edmProperty, property.asComplex(), selectedPaths, json);
        } else {
          throw new ODataSerializerException("Property type not yet supported!",
              ODataSerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
        }
      } catch (final EdmPrimitiveTypeException e) {
        throw new ODataSerializerException("Wrong value for property!", e,
            ODataSerializerException.MessageKeys.WRONG_PROPERTY_VALUE,
            edmProperty.getName(), property.getValue().toString());
      }
    }
  }

  private void writeCollection(final EdmProperty edmProperty, final Property property,
      final Set<List<String>> selectedPaths, JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException, ODataSerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
        writePrimitiveValue(edmProperty, value, json);
        break;
      case COLLECTION_GEOSPATIAL:
        throw new ODataSerializerException("Property type not yet supported!",
            ODataSerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      case COLLECTION_ENUM:
        json.writeString(value.toString());
        break;
      case COLLECTION_LINKED_COMPLEX:
        writeComplexValue(edmProperty, ((LinkedComplexValue) value).getValue(), selectedPaths, json);
        break;
      case COLLECTION_COMPLEX:
        writeComplexValue(edmProperty, property.asComplex(), selectedPaths, json);
        break;
      default:
        throw new ODataSerializerException("Property type not yet supported!",
            ODataSerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      }
    }
    json.writeEndArray();
  }

  private void writePrimitive(EdmProperty edmProperty, Property property, JsonGenerator json)
      throws EdmPrimitiveTypeException, IOException, ODataSerializerException {
    if (property.isPrimitive()) {
      writePrimitiveValue(edmProperty, property.asPrimitive(), json);
    } else if (property.isGeospatial()) {
      throw new ODataSerializerException("Property type not yet supported!",
          ODataSerializerException.MessageKeys.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
    } else if (property.isEnum()) {
      writePrimitiveValue(edmProperty, property.asEnum(), json);
    } else {
      throw new ODataSerializerException("Inconsistent property type!",
          ODataSerializerException.MessageKeys.INCONSISTENT_PROPERTY_TYPE, edmProperty.getName());
    }
  }

  protected void writePrimitiveValue(final EdmProperty edmProperty, final Object primitiveValue,
      final JsonGenerator json) throws EdmPrimitiveTypeException, IOException {
    final EdmPrimitiveType type = (EdmPrimitiveType) edmProperty.getType();
    final String value = type.valueToString(primitiveValue,
        edmProperty.isNullable(), edmProperty.getMaxLength(),
        edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode());
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

  protected void writeComplexValue(final EdmProperty edmProperty, final List<Property> properties,
      final Set<List<String>> selectedPaths, JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException, ODataSerializerException {
    final EdmComplexType type = (EdmComplexType) edmProperty.getType();
    json.writeStartObject();
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      if (selectedPaths == null || isSelected(selectedPaths, propertyName)) {
        writeProperty((EdmProperty) type.getProperty(propertyName), property,
            selectedPaths == null ? null : getReducedSelectedPaths(selectedPaths, propertyName),
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

  private static boolean isSelected(final Set<List<String>> selectedPaths, final String propertyName) {
    for (final List<String> path : selectedPaths) {
      if (propertyName.equals(path.get(0))) {
        return true;
      }
    }
    return false;
  }

  private static Set<List<String>> getReducedSelectedPaths(final Set<List<String>> selectedPaths,
      final String propertyName) {
    Set<List<String>> reducedPaths = new HashSet<List<String>>();
    for (final List<String> path : selectedPaths) {
      if (path.size() > 1) {
        if (propertyName.equals(path.get(0))) {
          reducedPaths.add(path.subList(1, path.size()));
        }
      } else {
        return null;
      }
    }
    return reducedPaths.isEmpty() ? null : reducedPaths;
  }
}
