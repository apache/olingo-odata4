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
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.ODataServerError;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerException;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.apache.olingo.server.core.serializer.utils.ContextURLBuilder;
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
      throw new ODataSerializerException("An I/O exception occurred.", e, ODataSerializerException.IO_EXCEPTION);
    } finally {
      if (gen != null) {
        try {
          gen.close();
        } catch (IOException e) {
          throw new ODataSerializerException("An I/O exception occurred.", e, ODataSerializerException.IO_EXCEPTION);
        }
      }
    }
  }

  @Override
  public InputStream metadataDocument(final Edm edm) throws ODataSerializerException {
    throw new ODataSerializerException("Metadata in JSON format not supported!",
        ODataSerializerException.JSON_METADATA);
  }

  @Override
  public InputStream error(final ODataServerError error) throws ODataSerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      new ODataErrorSerializer().writeErrorDocument(json, error);
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e, ODataSerializerException.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entitySet(final EdmEntitySet edmEntitySet, final EntitySet entitySet,
      final ContextURL contextURL) throws ODataSerializerException {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (format != ODataFormat.JSON_NO_METADATA) {
        if (contextURL == null) {
          throw new ODataSerializerException("ContextURL null!", ODataSerializerException.NO_CONTEXT_URL);
        } else {
          json.writeStringField(Constants.JSON_CONTEXT, ContextURLBuilder.create(contextURL).toASCIIString());
        }
      }
      if (entitySet.getCount() != null) {
        json.writeNumberField(Constants.JSON_COUNT, entitySet.getCount());
      }
      json.writeFieldName(Constants.VALUE);
      json.writeStartArray();
      for (Entity entity : entitySet.getEntities()) {
        writeEntity(edmEntitySet, entity, null, json);
      }
      json.writeEndArray();
      if (entitySet.getNext() != null) {
        json.writeStringField(Constants.JSON_NEXT_LINK, entitySet.getNext().toASCIIString());
      }
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e, ODataSerializerException.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  @Override
  public InputStream entity(final EdmEntitySet edmEntitySet, final Entity entity, final ContextURL contextURL)
      throws ODataSerializerException {
    if (format != ODataFormat.JSON_NO_METADATA && contextURL == null) {
      throw new ODataSerializerException("ContextURL null!", ODataSerializerException.NO_CONTEXT_URL);
    }
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      writeEntity(edmEntitySet, entity, contextURL, json);
      json.close();
    } catch (final IOException e) {
      throw new ODataSerializerException("An I/O exception occurred.", e, ODataSerializerException.IO_EXCEPTION);
    }
    return buffer.getInputStream();
  }

  protected void writeEntity(final EdmEntitySet entitySet, final Entity entity, final ContextURL contextURL,
      final JsonGenerator json) throws IOException, ODataSerializerException {
    final EdmEntityType entityType = entitySet.getEntityType();
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
    for (final String propertyName : entityType.getPropertyNames()) {
      final EdmProperty edmProperty = (EdmProperty) entityType.getProperty(propertyName);
      final Property property = entity.getProperty(propertyName);
      writeProperty(edmProperty, property, json);
    }
    json.writeEndObject();
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property, final JsonGenerator json)
      throws IOException, ODataSerializerException {
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new ODataSerializerException("Non-nullable property not present!",
            ODataSerializerException.MISSING_PROPERTY, edmProperty.getName());
      } else {
        json.writeNull();
      }
    } else {
      try {
        if (edmProperty.isCollection()) {
          writeCollection(edmProperty, property, json);
        } else if (edmProperty.isPrimitive()) {
          writePrimitive(edmProperty, property, json);
        } else if (property.isLinkedComplex()) {
          writeComplexValue(edmProperty, property.asLinkedComplex().getValue(), json);
        } else if (property.isComplex()) {
          writeComplexValue(edmProperty, property.asComplex(), json);
        } else {
          throw new ODataSerializerException("Property type not yet supported!",
              ODataSerializerException.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
        }
      } catch (final EdmPrimitiveTypeException e) {
        throw new ODataSerializerException("Wrong value for property!", e,
            ODataSerializerException.WRONG_PROPERTY_VALUE, edmProperty.getName(), property.getValue().toString());
      }
    }
  }

  private void writeCollection(EdmProperty edmProperty, Property property, JsonGenerator json)
          throws IOException, EdmPrimitiveTypeException, ODataSerializerException {
    json.writeStartArray();
    for (Object value : property.asCollection()) {
      switch (property.getValueType()) {
      case COLLECTION_PRIMITIVE:
        writePrimitiveValue(edmProperty, value, json);
        break;
      case COLLECTION_GEOSPATIAL:
        throw new ODataSerializerException("Property type not yet supported!",
            ODataSerializerException.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
      case COLLECTION_ENUM:
        json.writeString(value.toString());
        break;
      case COLLECTION_LINKED_COMPLEX:
        writeComplexValue(edmProperty, ((LinkedComplexValue) value).getValue(), json);
        break;
      case COLLECTION_COMPLEX:
        writeComplexValue(edmProperty, property.asComplex(), json);
        break;
      default:
        throw new ODataSerializerException("Property type not yet supported!",
            ODataSerializerException.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
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
          ODataSerializerException.UNSUPPORTED_PROPERTY_TYPE, edmProperty.getName());
    } else if (property.isEnum()) {
      writePrimitiveValue(edmProperty, property.asEnum(), json);
    } else {
      throw new ODataSerializerException("Inconsistent property type!",
          ODataSerializerException.INCONSISTENT_PROPERTY_TYPE, edmProperty.getName());
    }
  }

  protected void writePrimitiveValue(final EdmProperty edmProperty, final Object primitiveValue,
      final JsonGenerator json)
      throws EdmPrimitiveTypeException, IOException {
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

  private void writeComplexValue(final EdmProperty edmProperty, final List<Property> properties,
      JsonGenerator json) throws IOException, EdmPrimitiveTypeException, ODataSerializerException {
    final EdmComplexType type = (EdmComplexType) edmProperty.getType();
    json.writeStartObject();
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      writeProperty((EdmProperty) type.getProperty(propertyName), property, json);
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
}
