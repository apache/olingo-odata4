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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.ODataRuntimeException;
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
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.core.serializer.utils.CircleStreamBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class ODataJsonSerializer implements ODataSerializer {

  private static final Logger log = LoggerFactory.getLogger(ODataJsonSerializer.class);

  @Override
  public InputStream serviceDocument(final Edm edm, final String serviceRoot) {
    CircleStreamBuffer buffer;
    BufferedWriter writer;
    JsonFactory factory;
    JsonGenerator gen = null;

    // TODO: move stream initialization into separate method
    try {
      buffer = new CircleStreamBuffer();
      writer = new BufferedWriter(new OutputStreamWriter(buffer.getOutputStream(), DEFAULT_CHARSET));
      factory = new JsonFactory();

      gen = factory.createGenerator(writer);
      gen.setPrettyPrinter(new DefaultPrettyPrinter());

      ServiceDocumentJsonSerializer serializer = new ServiceDocumentJsonSerializer(edm, serviceRoot);
      serializer.writeServiceDocument(gen);

      gen.close();

      // TODO: Check correct stream handling
      // writer.flush();
      // buffer.closeWrite();

      return buffer.getInputStream();

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new ODataRuntimeException(e);
    } finally {
      if (gen != null) {
        try {
          gen.close();
        } catch (IOException e) {
          throw new ODataRuntimeException(e);
        }
      }
    }
  }

  @Override
  public InputStream metadataDocument(final Edm edm) {
    throw new ODataRuntimeException("Metadata in JSON format not supported!");
  }

  @Override
  public InputStream entity(final EdmEntityType edmEntityType, final Entity entity, final ContextURL contextURL) {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      writeEntity(edmEntityType, entity, contextURL, json);
      json.close();
    } catch (final IOException e) {
      throw new ODataRuntimeException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataRuntimeException(e);
    }
    return buffer.getInputStream();
  }

  protected void writeEntity(final EdmEntityType entityType, final Entity entity, final ContextURL contextURL,
      JsonGenerator json) throws IOException, EdmPrimitiveTypeException {
    json.writeStartObject();
    if (contextURL != null) {
      json.writeStringField(Constants.JSON_CONTEXT, contextURL.getURI().toASCIIString());
    }
    if (entity.getETag() != null) {
      json.writeStringField("@odata.etag", entity.getETag());
    }
    if (entity.getMediaETag() != null) {
      json.writeStringField("@odata.mediaEtag", entity.getMediaETag());
    }
    if (entity.getMediaContentType() != null) {
      json.writeStringField("@odata.mediaContentType", entity.getMediaContentType());
    }
    for (final String propertyName : entityType.getPropertyNames()) {
      final EdmProperty edmProperty = (EdmProperty) entityType.getProperty(propertyName);
      final Property property = entity.getProperty(propertyName);
      writeProperty(edmProperty, property, json);
    }
    json.writeEndObject();
  }

  protected void writeProperty(final EdmProperty edmProperty, final Property property, JsonGenerator json)
      throws IOException, EdmPrimitiveTypeException {
    json.writeFieldName(edmProperty.getName());
    if (property == null || property.isNull()) {
      if (edmProperty.isNullable() == Boolean.FALSE) {
        throw new ODataRuntimeException("Non-nullable property not present!");
      } else {
        json.writeNull();
      }
    } else {
      if (edmProperty.isPrimitive()) {
        if (property.isPrimitive()) {
          writePrimitiveValue(edmProperty, property.asPrimitive(), json);
        } else if (property.isGeospatial()) {
          throw new ODataRuntimeException("Property type not yet supported!");
        } else if (property.isEnum()) {
          json.writeString(property.asEnum().toString());
        } else {
          throw new ODataRuntimeException("Inconsistent property type!");
        }
      } else if (edmProperty.isCollection()) {
        json.writeStartArray();
        for (Object value : property.asCollection()) {
          switch (property.getValueType()) {
          case COLLECTION_PRIMITIVE:
            writePrimitiveValue(edmProperty, value, json);
            break;
          case COLLECTION_GEOSPATIAL:
            throw new ODataRuntimeException("Property type not yet supported!");
          case COLLECTION_ENUM:
            json.writeString(value.toString());
            break;
          case COLLECTION_LINKED_COMPLEX:
            writeLinkedComplexValue(edmProperty, (LinkedComplexValue) value, json);
            break;
          default:
            throw new ODataRuntimeException("Property type not yet supported!");
          }
        }
        json.writeEndArray();
      } else {
        if (property.isLinkedComplex()) {
          writeLinkedComplexValue(edmProperty, property.asLinkedComplex(), json);
        } else {
          throw new ODataRuntimeException("Property type not yet supported!");
        }
      }
    }
  }

  protected void writePrimitiveValue(final EdmProperty edmProperty, final Object primitiveValue, JsonGenerator json)
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

  private void writeLinkedComplexValue(final EdmProperty edmProperty, final LinkedComplexValue linkedComplexValue,
      JsonGenerator json) throws IOException, EdmPrimitiveTypeException {
    final EdmComplexType type = (EdmComplexType) edmProperty.getType();
    final List<Property> properties = linkedComplexValue.getValue();
    json.writeStartObject();
    for (final String propertyName : type.getPropertyNames()) {
      final Property property = findProperty(propertyName, properties);
      writeProperty((EdmProperty) type.getProperty(propertyName), property, json);
    }
    json.writeEndObject();
  }

  private Property findProperty(String propertyName, List<Property> properties) {
    for (final Property property : properties) {
      if (propertyName.equals(property.getName())) {
        return property;
      }
    }
    return null;
  }

  @Override
  public InputStream entitySet(final EdmEntitySet edmEntitySet, final EntitySet entitySet,
      final ContextURL contextURL) {
    CircleStreamBuffer buffer = new CircleStreamBuffer();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(buffer.getOutputStream());
      json.writeStartObject();
      if (contextURL != null) {
        json.writeStringField(Constants.JSON_CONTEXT, contextURL.getURI().toASCIIString());
      }
      if (entitySet.getCount() != null) {
        json.writeNumberField("@odata.count", entitySet.getCount());
      }
      json.writeFieldName(Constants.VALUE);
      json.writeStartArray();
      for (Entity entity : entitySet.getEntities()) {
        writeEntity(edmEntitySet.getEntityType(), entity, null, json);
      }
      json.writeEndArray();
      if (entitySet.getNext() != null) {
        json.writeStringField("@odata.nextLink", entitySet.getNext().toASCIIString());
      }
      json.close();
    } catch (final IOException e) {
      throw new ODataRuntimeException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataRuntimeException(e);
    }
    return buffer.getInputStream();
  }
}
