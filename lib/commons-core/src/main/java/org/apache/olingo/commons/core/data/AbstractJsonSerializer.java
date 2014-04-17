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

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.Entry;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.PrimitiveValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

abstract class AbstractJsonSerializer<T> extends ODataJacksonSerializer<T> {

  private static final EdmPrimitiveTypeKind[] NUMBER_TYPES = {
    EdmPrimitiveTypeKind.Byte, EdmPrimitiveTypeKind.SByte,
    EdmPrimitiveTypeKind.Single, EdmPrimitiveTypeKind.Double,
    EdmPrimitiveTypeKind.Int16, EdmPrimitiveTypeKind.Int32, EdmPrimitiveTypeKind.Int64
  };

  private final JSONGeoValueSerializer geoSerializer = new JSONGeoValueSerializer();

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

      if (link.getInlineEntry() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineEntry());
      } else if (link.getInlineFeed() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        for (Entry subEntry : link.getInlineFeed().getEntries()) {
          jgen.writeObject(subEntry);
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
    for (Link link : linked.getAssociationLinks()) {
      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
                link.getTitle() + version.getJSONMap().get(ODataServiceVersion.JSON_ASSOCIATION_LINK),
                link.getHref());
      }
    }

    for (Link link : linked.getNavigationLinks()) {
      if (StringUtils.isNotBlank(link.getHref())) {
        jgen.writeStringField(
                link.getTitle() + version.getJSONMap().get(ODataServiceVersion.JSON_NAVIGATION_LINK),
                link.getHref());
      }

      if (link.getInlineEntry() != null) {
        jgen.writeObjectField(link.getTitle(), link.getInlineEntry());
      } else if (link.getInlineFeed() != null) {
        jgen.writeArrayFieldStart(link.getTitle());
        for (Entry subEntry : link.getInlineFeed().getEntries()) {
          jgen.writeObject(subEntry);
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
      for (Property property : value.asComplex().get()) {
        property(jgen, property, property.getName());
      }
      if (value.isLinkedComplex()) {
        links(value.asLinkedComplex(), jgen);
      }
      jgen.writeEndObject();
    }
  }

  protected void property(final JsonGenerator jgen, final Property property, final String name) throws IOException {
    if (serverMode) {
      String type = property.getType();
      if (StringUtils.isBlank(type)
              && property.getValue().isPrimitive() || property.getValue().isNull()) {

        type = EdmPrimitiveTypeKind.String.getFullQualifiedName().toString();
      }
      if (StringUtils.isNotBlank(type)) {
        jgen.writeFieldName(
                name + StringUtils.prependIfMissing(version.getJSONMap().get(ODataServiceVersion.JSON_TYPE), "@"));
        jgen.writeString(new EdmTypeInfo.Builder().setTypeExpression(type).build().external(version));
      }
    }

    jgen.writeFieldName(name);
    value(jgen, property.getType(), property.getValue());
  }
}
