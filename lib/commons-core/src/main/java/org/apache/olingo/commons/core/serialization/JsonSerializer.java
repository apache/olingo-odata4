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
package org.apache.olingo.commons.core.serialization;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotatable;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.serialization.ODataSerializer;
import org.apache.olingo.commons.api.serialization.ODataSerializerException;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class JsonSerializer implements ODataSerializer {

  protected ODataServiceVersion version;
  protected boolean serverMode;

  private static final EdmPrimitiveTypeKind[] NUMBER_TYPES = {
      EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
      EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double,
      EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64,
      EdmPrimitiveTypeKind.Decimal
  };

  private final JsonGeoValueSerializer geoSerializer = new JsonGeoValueSerializer();

  public JsonSerializer(final ODataServiceVersion version, final boolean serverMode) {
    this.version = version;
    this.serverMode = serverMode;
  }

  @Override
  public <T> void write(final Writer writer, final T obj) throws ODataSerializerException {
    try {
      JsonGenerator json = new JsonFactory().createGenerator(writer);
      if (obj instanceof EntitySet) {
        new JsonEntitySetSerializer(version, serverMode).doSerialize((EntitySet) obj, json);
      } else if (obj instanceof Entity) {
        new JsonEntitySerializer(version, serverMode).doSerialize((Entity) obj, json);
      } else if (obj instanceof Property) {
        new JsonPropertySerializer(version, serverMode).doSerialize((Property) obj, json);
      } else if (obj instanceof Link) {
        link((Link) obj, json);
      }
      json.flush();
    } catch (final IOException e) {
      throw new ODataSerializerException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataSerializerException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void write(final Writer writer, final ResWrap<T> container) throws ODataSerializerException {
    final T obj = container == null ? null : container.getPayload();
    try {
      JsonGenerator json = new JsonFactory().createGenerator(writer);
      if (obj instanceof EntitySet) {
        new JsonEntitySetSerializer(version, serverMode).doContainerSerialize((ResWrap<EntitySet>) container, json);
      } else if (obj instanceof Entity) {
        new JsonEntitySerializer(version, serverMode).doContainerSerialize((ResWrap<Entity>) container, json);
      } else if (obj instanceof Property) {
        new JsonPropertySerializer(version, serverMode).doContainerSerialize((ResWrap<Property>) container, json);
      } else if (obj instanceof Link) {
        link((Link) obj, json);
      }
      json.flush();
    } catch (final IOException e) {
      throw new ODataSerializerException(e);
    } catch (final EdmPrimitiveTypeException e) {
      throw new ODataSerializerException(e);
    }
  }

  protected void link(final Link link, final JsonGenerator jgen) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField(Constants.JSON_URL, link.getHref());
    jgen.writeEndObject();
  }

  protected void links(final Linked linked, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {
    if (serverMode) {
      serverLinks(linked, jgen);
    } else {
      clientLinks(linked, jgen);
    }
  }

  protected void clientLinks(final Linked linked, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {
    final Map<String, List<String>> entitySetLinks = new HashMap<String, List<String>>();
    for (Link link : linked.getNavigationLinks()) {
      for (Annotation annotation : link.getAnnotations()) {
        valuable(jgen, annotation, link.getTitle() + "@" + annotation.getTerm());
      }

      ODataLinkType type = null;
      try {
        type = ODataLinkType.fromString(version, link.getRel(), link.getType());
      } catch (IllegalArgumentException e) {
        // ignore
      }

      if (type == ODataLinkType.ENTITY_SET_NAVIGATION) {
        final List<String> uris;
        if (entitySetLinks.containsKey(link.getTitle())) {
          uris = entitySetLinks.get(link.getTitle());
        } else {
          uris = new ArrayList<String>();
          entitySetLinks.put(link.getTitle(), uris);
        }
        if (StringUtils.isNotBlank(link.getHref())) {
          uris.add(link.getHref());
        }
      } else {
        if (StringUtils.isNotBlank(link.getHref())) {
          jgen.writeStringField(link.getTitle() + Constants.JSON_BIND_LINK_SUFFIX, link.getHref());
        }
      }

      if (link.getInlineEntity() != null) {
        jgen.writeFieldName(link.getTitle());
        new JsonEntitySerializer(version, serverMode).doSerialize(link.getInlineEntity(), jgen);
      } else if (link.getInlineEntitySet() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        JsonEntitySerializer entitySerializer = new JsonEntitySerializer(version, serverMode);
        for (Entity subEntry : link.getInlineEntitySet().getEntities()) {
          entitySerializer.doSerialize(subEntry, jgen);
        }
        jgen.writeEndArray();
      }
    }
    for (Map.Entry<String, List<String>> entitySetLink : entitySetLinks.entrySet()) {
      if (!entitySetLink.getValue().isEmpty()) {
        jgen.writeArrayFieldStart(entitySetLink.getKey() + Constants.JSON_BIND_LINK_SUFFIX);
        for (String uri : entitySetLink.getValue()) {
          jgen.writeString(uri);
        }
        jgen.writeEndArray();
      }
    }
  }

  protected void serverLinks(final Linked linked, final JsonGenerator jgen)
      throws IOException, EdmPrimitiveTypeException {
    if (linked instanceof Entity) {
      for (Link link : ((Entity) linked).getMediaEditLinks()) {
        if (StringUtils.isNotBlank(link.getHref())) {
          jgen.writeStringField(
              link.getTitle() + StringUtils.prependIfMissing(
                  version.getJsonName(ODataServiceVersion.JsonKey.MEDIA_EDIT_LINK), "@"),
              link.getHref());
        }
      }
    }

    for (Link link : linked.getAssociationLinks()) {
      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
            link.getTitle() + version.getJsonName(ODataServiceVersion.JsonKey.ASSOCIATION_LINK),
            link.getHref());
      }
    }

    for (Link link : linked.getNavigationLinks()) {
      for (Annotation annotation : link.getAnnotations()) {
        valuable(jgen, annotation, link.getTitle() + "@" + annotation.getTerm());
      }

      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
            link.getTitle() + version.getJsonName(ODataServiceVersion.JsonKey.NAVIGATION_LINK),
            link.getHref());
      }

      if (link.getInlineEntity() != null) {
        jgen.writeFieldName(link.getTitle());
        new JsonEntitySerializer(version, serverMode).doSerialize(link.getInlineEntity(), jgen);
      } else if (link.getInlineEntitySet() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        JsonEntitySerializer entitySerializer = new JsonEntitySerializer(version, serverMode);
        for (Entity subEntry : link.getInlineEntitySet().getEntities()) {
          entitySerializer.doSerialize(subEntry, jgen);
        }
        jgen.writeEndArray();
      }
    }
  }

  private void collection(final JsonGenerator jgen, final EdmTypeInfo typeInfo,
      final ValueType valueType, final List<?> value) throws IOException, EdmPrimitiveTypeException {
    jgen.writeStartArray();
    for (Object item : value) {
      final EdmTypeInfo itemTypeInfo = typeInfo == null ? null :
          new EdmTypeInfo.Builder().setTypeExpression(typeInfo.getFullQualifiedName().toString()).build();
      switch (valueType) {
      case COLLECTION_PRIMITIVE:
        primitiveValue(jgen, itemTypeInfo, item);
        break;
      case COLLECTION_GEOSPATIAL:
        jgen.writeStartObject();
        geoSerializer.serialize(jgen, (Geospatial) item);
        jgen.writeEndObject();
        break;
      case COLLECTION_ENUM:
        jgen.writeString(item.toString());
        break;
      case COLLECTION_COMPLEX:
        @SuppressWarnings("unchecked")
        final List<Property> complexItem = (List<Property>) item;
        complexValue(jgen, itemTypeInfo, complexItem, null);
        break;
      case COLLECTION_LINKED_COMPLEX:
        final LinkedComplexValue complexItem2 = (LinkedComplexValue) item;
        complexValue(jgen, itemTypeInfo, complexItem2.getValue(), complexItem2);
      default:
        break;
      }
    }
    jgen.writeEndArray();
  }

  protected void primitiveValue(final JsonGenerator jgen, final EdmTypeInfo typeInfo, final Object value)
      throws IOException, EdmPrimitiveTypeException {
    final EdmPrimitiveTypeKind kind = typeInfo == null ? null : typeInfo.getPrimitiveTypeKind();
    final boolean isNumber = kind == null ? value instanceof Number : ArrayUtils.contains(NUMBER_TYPES, kind);
    final boolean isBoolean = kind == null ? value instanceof Boolean : kind == EdmPrimitiveTypeKind.Boolean;

    if (value == null) {
      jgen.writeNull();
    } else if (isBoolean) {
      jgen.writeBoolean((Boolean) value);
    } else {
      final String serialized = kind == null ? value.toString() :
          EdmPrimitiveTypeFactory.getInstance(kind) // TODO: add facets
              .valueToString(value, null, null, Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, null);
      if (isNumber) {
        jgen.writeNumber(serialized);
      } else {
        jgen.writeString(serialized);
      }
    }
  }

  private void complexValue(final JsonGenerator jgen, final EdmTypeInfo typeInfo,
      final List<Property> value, final Linked linked)
      throws IOException, EdmPrimitiveTypeException {
    jgen.writeStartObject();

    if (typeInfo != null) {
      jgen.writeStringField(version.getJsonName(ODataServiceVersion.JsonKey.TYPE), typeInfo.external(version));
    }

    for (Property property : value) {
      valuable(jgen, property, property.getName());
    }
    if (linked != null) {
      links(linked, jgen);
    }

    jgen.writeEndObject();
  }

  private void value(final JsonGenerator jgen, final String type, final Valuable value)
      throws IOException, EdmPrimitiveTypeException {
    final EdmTypeInfo typeInfo = type == null ? null : new EdmTypeInfo.Builder().setTypeExpression(type).build();

    if (value.isNull()) {
      jgen.writeNull();
    } else if (value.isPrimitive()) {
      primitiveValue(jgen, typeInfo, value.asPrimitive());
    } else if (value.isEnum()) {
      jgen.writeString(value.asEnum().toString());
    } else if (value.isGeospatial()) {
      jgen.writeStartObject();
      geoSerializer.serialize(jgen, value.asGeospatial());
      jgen.writeEndObject();
    } else if (value.isCollection()) {
      collection(jgen, typeInfo, value.getValueType(), value.asCollection());
    } else if (value.isLinkedComplex()) {
      complexValue(jgen, typeInfo, value.asLinkedComplex().getValue(), value.asLinkedComplex());
    } else if (value.isComplex()) {
      complexValue(jgen, typeInfo, value.asComplex(), null);
    }
  }

  protected void valuable(final JsonGenerator jgen, final Valuable valuable, final String name)
      throws IOException, EdmPrimitiveTypeException {
    if (!Constants.VALUE.equals(name) && !(valuable instanceof Annotation) && !valuable.isComplex()) {
      String type = valuable.getType();
      if (StringUtils.isBlank(type) && valuable.isPrimitive() || valuable.isNull()) {
        type = EdmPrimitiveTypeKind.String.getFullQualifiedName().toString();
      }
      if (StringUtils.isNotBlank(type)) {
        jgen.writeFieldName(
            name + StringUtils.prependIfMissing(version.getJsonName(ODataServiceVersion.JsonKey.TYPE), "@"));
        jgen.writeString(new EdmTypeInfo.Builder().setTypeExpression(type).build().external(version));
      }
    }

    if (valuable instanceof Annotatable) {
      for (Annotation annotation : ((Annotatable) valuable).getAnnotations()) {
        valuable(jgen, annotation, name + "@" + annotation.getTerm());
      }
    }

    jgen.writeFieldName(name);
    value(jgen, valuable.getType(), valuable);
  }
}
