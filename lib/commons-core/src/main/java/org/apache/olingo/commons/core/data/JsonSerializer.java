/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.commons.core.data;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotatable;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.PrimitiveValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.api.op.ODataSerializer;
import org.apache.olingo.commons.api.op.ODataSerializerException;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

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

  private final JSONGeoValueSerializer geoSerializer = new JSONGeoValueSerializer();

  public JsonSerializer(final ODataServiceVersion version, final boolean serverMode) {
    this.version = version;
    this.serverMode = serverMode;
  }

  @Override
  public <T> void write(Writer writer, T obj) throws ODataSerializerException {
    try {
      JsonGenerator json = new JsonFactory().createGenerator(writer);
      if (obj instanceof EntitySet) {
        new JSONEntitySetSerializer(version, serverMode).doSerialize((EntitySet) obj, json);
      } else if (obj instanceof Entity) {
        new JSONEntitySerializer(version, serverMode).doSerialize((Entity) obj, json);
      } else if (obj instanceof Property) {
        new JSONPropertySerializer(version, serverMode).doSerialize((Property) obj, json);
      } else if (obj instanceof Link) {
        link((Link) obj, json);
      }
      json.flush();
    } catch (final IOException e) {
      throw new ODataSerializerException(e);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void write(Writer writer, ResWrap<T> container) throws ODataSerializerException {
    final T obj = container == null ? null : container.getPayload();

    try {
      JsonGenerator json = new JsonFactory().createGenerator(writer);
      if (obj instanceof EntitySet) {
        new JSONEntitySetSerializer(version, serverMode).doContainerSerialize((ResWrap<EntitySet>) container, json);
      } else if (obj instanceof Entity) {
        new JSONEntitySerializer(version, serverMode).doContainerSerialize((ResWrap<Entity>) container, json);
      } else if (obj instanceof Property) {
        new JSONPropertySerializer(version, serverMode).doContainerSerialize((ResWrap<Property>) container, json);
      } else if (obj instanceof Link) {
        link((Link) obj, json);
      }
      json.flush();
    } catch (final IOException e) {
      throw new ODataSerializerException(e);
    }
  }

  protected void link(final Link link, JsonGenerator jgen) throws IOException {
    jgen.writeStartObject();
    jgen.writeStringField(Constants.JSON_URL, link.getHref());
    jgen.writeEndObject();
  }

  protected void links(final Linked linked, final JsonGenerator jgen) throws IOException {
    if (serverMode) {
      serverLinks(linked, jgen);
    } else {
      clientLinks(linked, jgen);
    }
  }

  protected void clientLinks(final Linked linked, final JsonGenerator jgen) throws IOException {
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
        new JSONEntitySerializer(version, serverMode).doSerialize(link.getInlineEntity(), jgen);
      } else if (link.getInlineEntitySet() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        JSONEntitySerializer entitySerializer = new JSONEntitySerializer(version, serverMode);
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

  protected void serverLinks(final Linked linked, final JsonGenerator jgen) throws IOException {
    if (linked instanceof Entity) {
      for (Link link : ((Entity) linked).getMediaEditLinks()) {
        if (StringUtils.isNotBlank(link.getHref())) {
          jgen.writeStringField(
                  link.getTitle() + StringUtils.prependIfMissing(
                          version.getJSONMap().get(ODataServiceVersion.JSON_MEDIAEDIT_LINK), "@"),
                  link.getHref());
        }
      }
    }

    for (Link link : linked.getAssociationLinks()) {
      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
                link.getTitle() + version.getJSONMap().get(ODataServiceVersion.JSON_ASSOCIATION_LINK),
                link.getHref());
      }
    }

    for (Link link : linked.getNavigationLinks()) {
      for (Annotation annotation : link.getAnnotations()) {
        valuable(jgen, annotation, link.getTitle() + "@" + annotation.getTerm());
      }

      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
                link.getTitle() + version.getJSONMap().get(ODataServiceVersion.JSON_NAVIGATION_LINK),
                link.getHref());
      }

      if (link.getInlineEntity() != null) {
        jgen.writeFieldName(link.getTitle());
        new JSONEntitySerializer(version, serverMode).doSerialize(link.getInlineEntity(), jgen);
      } else if (link.getInlineEntitySet() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        JSONEntitySerializer entitySerializer = new JSONEntitySerializer(version, serverMode);
        for (Entity subEntry : link.getInlineEntitySet().getEntities()) {
          entitySerializer.doSerialize(subEntry, jgen);
        }
        jgen.writeEndArray();
      }
    }
  }

  private void collection(final JsonGenerator jgen, final String itemType, final CollectionValue value)
          throws IOException {

    jgen.writeStartArray();
    for (Value item : value.get()) {
      value(jgen, itemType, item);
    }
    jgen.writeEndArray();
  }

  protected void primitiveValue(final JsonGenerator jgen, final EdmTypeInfo typeInfo, final PrimitiveValue value)
          throws IOException {

    final boolean isNumber = typeInfo == null
            ? NumberUtils.isNumber(value.get())
            : ArrayUtils.contains(NUMBER_TYPES, typeInfo.getPrimitiveTypeKind());
    final boolean isBoolean = typeInfo == null
            ? (value.get().equalsIgnoreCase(Boolean.TRUE.toString())
            || value.get().equalsIgnoreCase(Boolean.FALSE.toString()))
            : typeInfo.getPrimitiveTypeKind() == EdmPrimitiveTypeKind.Boolean;

    if (isNumber) {
      jgen.writeNumber(value.get());
    } else if (isBoolean) {
      jgen.writeBoolean(BooleanUtils.toBoolean(value.get()));
    } else {
      jgen.writeString(value.get());
    }
  }

  private void value(final JsonGenerator jgen, final String type, final Value value) throws IOException {
    final EdmTypeInfo typeInfo = type == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(type).build();

    if (value == null || value.isNull()) {
      jgen.writeNull();
    } else if (value.isPrimitive()) {
      primitiveValue(jgen, typeInfo, value.asPrimitive());
    } else if (value.isEnum()) {
      jgen.writeString(value.asEnum().get());
    } else if (value.isGeospatial()) {
      jgen.writeStartObject();
      geoSerializer.serialize(jgen, value.asGeospatial().get());
      jgen.writeEndObject();
    } else if (value.isCollection()) {
      collection(jgen, typeInfo == null ? null : typeInfo.getFullQualifiedName().toString(), value.asCollection());
    } else if (value.isComplex()) {
      jgen.writeStartObject();

      if (typeInfo != null) {
        jgen.writeStringField(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE), typeInfo.external(version));
      }

      for (Property property : value.asComplex().get()) {
        valuable(jgen, property, property.getName());
      }
      if (value.isLinkedComplex()) {
        links(value.asLinkedComplex(), jgen);
      }

      jgen.writeEndObject();
    }
  }

  protected void valuable(final JsonGenerator jgen, final Valuable valuable, final String name) throws IOException {
    if (!Constants.VALUE.equals(name) && !(valuable instanceof Annotation) && !valuable.getValue().isComplex()) {
      String type = valuable.getType();
      if (StringUtils.isBlank(type) && valuable.getValue().isPrimitive() || valuable.getValue().isNull()) {
        type = EdmPrimitiveTypeKind.String.getFullQualifiedName().toString();
      }
      if (StringUtils.isNotBlank(type)) {
        jgen.writeFieldName(
                name + StringUtils.prependIfMissing(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE), "@"));
        jgen.writeString(new EdmTypeInfo.Builder().setTypeExpression(type).build().external(version));
      }
    }

    if (valuable instanceof Annotatable) {
      for (Annotation annotation : ((Annotatable) valuable).getAnnotations()) {
        valuable(jgen, annotation, name + "@" + annotation.getTerm());
      }
    }

    jgen.writeFieldName(name);
    value(jgen, valuable.getType(), valuable.getValue());
  }
}
