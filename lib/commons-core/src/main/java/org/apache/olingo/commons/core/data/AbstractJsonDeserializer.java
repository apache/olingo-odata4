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

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.data.Linked;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.domain.ODataPropertyType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

abstract class AbstractJsonDeserializer<T> extends ODataJacksonDeserializer<ResWrap<T>> {

  private JSONGeoValueDeserializer geoDeserializer;

  private JSONGeoValueDeserializer getGeoDeserializer() {
    if (geoDeserializer == null) {
      geoDeserializer = new JSONGeoValueDeserializer(version);
    }
    return geoDeserializer;
  }

  protected String getTitle(final Map.Entry<String, JsonNode> entry) {
    return entry.getKey().substring(0, entry.getKey().indexOf('@'));
  }

  protected String setInline(final String name, final String suffix, final JsonNode tree,
          final ObjectCodec codec, final LinkImpl link) throws IOException {

    final String entityNamePrefix = name.substring(0, name.indexOf(suffix));
    if (tree.has(entityNamePrefix)) {
      final JsonNode inline = tree.path(entityNamePrefix);

      if (inline instanceof ObjectNode) {
        link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());

        link.setInlineEntity(inline.traverse(codec).<ResWrap<JSONEntityImpl>>readValueAs(
                new TypeReference<JSONEntityImpl>() {
                }).getPayload());
      }

      if (inline instanceof ArrayNode) {
        link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());

        final JSONEntitySetImpl entitySet = new JSONEntitySetImpl();
        final Iterator<JsonNode> entries = ((ArrayNode) inline).elements();
        while (entries.hasNext()) {
          entitySet.getEntities().add(entries.next().traverse(codec).<ResWrap<JSONEntityImpl>>readValuesAs(
                  new TypeReference<JSONEntityImpl>() {
                  }).next().getPayload());
        }

        link.setInlineEntitySet(entitySet);
      }
    }
    return entityNamePrefix;
  }

  protected void links(final Map.Entry<String, JsonNode> field, final Linked linked, final Set<String> toRemove,
          final JsonNode tree, final ObjectCodec codec) throws IOException {
    if (serverMode) {
      serverLinks(field, linked, toRemove, tree, codec);
    } else {
      clientLinks(field, linked, toRemove, tree, codec);
    }
  }

  private void clientLinks(final Map.Entry<String, JsonNode> field, final Linked linked, final Set<String> toRemove,
          final JsonNode tree, final ObjectCodec codec) throws IOException {

    if (field.getKey().endsWith(jsonNavigationLink)) {
      final LinkImpl link = new LinkImpl();
      link.setTitle(getTitle(field));
      link.setRel(version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL) + getTitle(field));

      if (field.getValue().isValueNode()) {
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
      }

      linked.getNavigationLinks().add(link);

      toRemove.add(field.getKey());
      toRemove.add(setInline(field.getKey(), jsonNavigationLink, tree, codec, link));
    } else if (field.getKey().endsWith(jsonAssociationLink)) {
      final LinkImpl link = new LinkImpl();
      link.setTitle(getTitle(field));
      link.setRel(version.getNamespaceMap().get(ODataServiceVersion.ASSOCIATION_LINK_REL) + getTitle(field));
      link.setHref(field.getValue().textValue());
      link.setType(ODataLinkType.ASSOCIATION.toString());
      linked.getAssociationLinks().add(link);

      toRemove.add(field.getKey());
    }
  }

  private void serverLinks(final Map.Entry<String, JsonNode> field, final Linked linked, final Set<String> toRemove,
          final JsonNode tree, final ObjectCodec codec) throws IOException {

    if (field.getKey().endsWith(Constants.JSON_BIND_LINK_SUFFIX)
            || field.getKey().endsWith(jsonNavigationLink)) {

      if (field.getValue().isValueNode()) {
        final String suffix = field.getKey().replaceAll("^.*@", "@");

        final LinkImpl link = new LinkImpl();
        link.setTitle(getTitle(field));
        link.setRel(version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL) + getTitle(field));
        link.setHref(field.getValue().textValue());
        link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
        linked.getNavigationLinks().add(link);

        toRemove.add(setInline(field.getKey(), suffix, tree, codec, link));
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> itor = field.getValue().elements(); itor.hasNext();) {
          final JsonNode node = itor.next();

          final LinkImpl link = new LinkImpl();
          link.setTitle(getTitle(field));
          link.setRel(version.getNamespaceMap().get(ODataServiceVersion.NAVIGATION_LINK_REL) + getTitle(field));
          link.setHref(node.asText());
          link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());
          linked.getNavigationLinks().add(link);
          toRemove.add(setInline(field.getKey(), Constants.JSON_BIND_LINK_SUFFIX, tree, codec, link));
        }
      }
      toRemove.add(field.getKey());
    }
  }

  private Map.Entry<ODataPropertyType, EdmTypeInfo> guessPropertyType(final JsonNode node) {
    ODataPropertyType type;
    EdmTypeInfo typeInfo = null;

    if (node.isValueNode() || node.isNull()) {
      type = ODataPropertyType.PRIMITIVE;

      EdmPrimitiveTypeKind kind = EdmPrimitiveTypeKind.String;
      if (node.isShort()) {
        kind = EdmPrimitiveTypeKind.Int16;
      } else if (node.isIntegralNumber()) {
        kind = EdmPrimitiveTypeKind.Int32;
      } else if (node.isLong()) {
        kind = EdmPrimitiveTypeKind.Int64;
      } else if (node.isBigDecimal()) {
        kind = EdmPrimitiveTypeKind.Decimal;
      } else if (node.isBoolean()) {
        kind = EdmPrimitiveTypeKind.Boolean;
      } else if (node.isFloat()) {
        kind = EdmPrimitiveTypeKind.Single;
      } else if (node.isDouble()) {
        kind = EdmPrimitiveTypeKind.Double;
      }
      typeInfo = new EdmTypeInfo.Builder().
              setTypeExpression(kind.getFullQualifiedName().toString()).build();
    } else if (node.isArray()) {
      type = ODataPropertyType.COLLECTION;
    } else if (node.isObject()) {
      if (node.has(Constants.ATTR_TYPE) && node.has(Constants.JSON_CRS)) {
        type = ODataPropertyType.PRIMITIVE;
        typeInfo = new EdmTypeInfo.Builder().
                setTypeExpression("Edm.Geography" + node.get(Constants.ATTR_TYPE).asText()).build();
      } else {
        type = ODataPropertyType.COMPLEX;
      }
    } else {
      type = ODataPropertyType.EMPTY;
    }

    return new SimpleEntry<ODataPropertyType, EdmTypeInfo>(type, typeInfo);
  }

  private Value fromPrimitive(final JsonNode node, final EdmTypeInfo typeInfo) {
    final Value value;

    if (node.isNull()) {
      value = new NullValueImpl();
    } else {
      if (typeInfo != null && typeInfo.getPrimitiveTypeKind().isGeospatial()) {
        value = new GeospatialValueImpl(getGeoDeserializer().deserialize(node, typeInfo));
      } else {
        value = new PrimitiveValueImpl(node.asText());
      }
    }

    return value;
  }

  private ComplexValue fromComplex(final ObjectNode node, final ObjectCodec codec) throws IOException {
    final ComplexValue value = version.compareTo(ODataServiceVersion.V40) < 0
            ? new ComplexValueImpl()
            : new LinkedComplexValueImpl();

    if (value.isLinkedComplex()) {
      final Set<String> toRemove = new HashSet<String>();
      for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
        final Map.Entry<String, JsonNode> field = itor.next();

        links(field, value.asLinkedComplex(), toRemove, node, codec);
      }
      node.remove(toRemove);
    }

    String type = null;
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (type == null && field.getKey().endsWith(getJSONAnnotation(jsonType))) {
        type = field.getValue().asText();
      } else {
        final JSONPropertyImpl property = new JSONPropertyImpl();
        property.setName(field.getKey());
        property.setType(type);
        type = null;

        value(property, field.getValue(), codec);
        value.get().add(property);
      }
    }

    return value;
  }

  private CollectionValue fromCollection(final Iterator<JsonNode> nodeItor, final EdmTypeInfo typeInfo,
          final ObjectCodec codec) throws IOException {

    final CollectionValueImpl value = new CollectionValueImpl();

    final EdmTypeInfo type = typeInfo == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(typeInfo.getFullQualifiedName().toString()).build();

    while (nodeItor.hasNext()) {
      final JsonNode child = nodeItor.next();

      if (child.isValueNode()) {
        if (typeInfo == null || typeInfo.isPrimitiveType()) {
          value.get().add(fromPrimitive(child, type));
        } else {
          value.get().add(new EnumValueImpl(child.asText()));
        }
      } else if (child.isContainerNode()) {
        if (child.has(jsonType)) {
          ((ObjectNode) child).remove(jsonType);
        }
        value.get().add(fromComplex((ObjectNode) child, codec));
      }
    }

    return value;
  }

  protected void value(final JSONPropertyImpl property, final JsonNode node, final ObjectCodec codec)
          throws IOException {

    EdmTypeInfo typeInfo = StringUtils.isBlank(property.getType())
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();

    final Map.Entry<ODataPropertyType, EdmTypeInfo> guessed = guessPropertyType(node);
    if (typeInfo == null) {
      typeInfo = guessed.getValue();
    }

    final ODataPropertyType propType = typeInfo == null
            ? guessed.getKey()
            : typeInfo.isCollection()
            ? ODataPropertyType.COLLECTION
            : typeInfo.isPrimitiveType()
            ? ODataPropertyType.PRIMITIVE
            : node.isValueNode()
            ? ODataPropertyType.ENUM
            : ODataPropertyType.COMPLEX;

    switch (propType) {
      case COLLECTION:
        property.setValue(fromCollection(node.elements(), typeInfo, codec));
        break;

      case COMPLEX:
        if (node.has(jsonType)) {
          property.setType(node.get(jsonType).asText());
          ((ObjectNode) node).remove(jsonType);
        }
        property.setValue(fromComplex((ObjectNode) node, codec));
        break;

      case ENUM:
        property.setValue(new EnumValueImpl(node.asText()));
        break;

      case PRIMITIVE:
        if (property.getType() == null && typeInfo != null) {
          property.setType(typeInfo.getFullQualifiedName().toString());
        }
        property.setValue(fromPrimitive(node, typeInfo));
        break;

      case EMPTY:
      default:
        property.setValue(new PrimitiveValueImpl(StringUtils.EMPTY));
    }
  }
}
