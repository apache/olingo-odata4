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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.CollectionValue;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Value;
import org.apache.olingo.commons.api.domain.ODataPropertyType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;

abstract class AbstractJsonDeserializer<T> extends ODataJacksonDeserializer<T> {

  private JSONGeoValueDeserializer geoDeserializer;

  private JSONGeoValueDeserializer getGeoDeserializer() {
    if (geoDeserializer == null) {
      geoDeserializer = new JSONGeoValueDeserializer(version);
    }
    return geoDeserializer;
  }

  protected EdmPrimitiveTypeKind getPrimitiveType(final JsonNode node) {
    EdmPrimitiveTypeKind result = EdmPrimitiveTypeKind.String;

    if (node.isIntegralNumber()) {
      result = EdmPrimitiveTypeKind.Int32;
    } else if (node.isBoolean()) {
      result = EdmPrimitiveTypeKind.Boolean;
    } else if (node.isFloatingPointNumber()) {
      result = EdmPrimitiveTypeKind.Double;
    }

    return result;
  }

  private ODataPropertyType guessPropertyType(final JsonNode node) {
    final ODataPropertyType type;

    if (node.isValueNode() || node.isNull()) {
      type = ODataPropertyType.PRIMITIVE;
    } else if (node.isArray()) {
      type = ODataPropertyType.COLLECTION;
    } else if (node.isObject()) {
      type = ODataPropertyType.COMPLEX;
    } else {
      type = ODataPropertyType.EMPTY;
    }

    return type;
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

  private ComplexValue fromComplex(final JsonNode node) {
    final ComplexValue value = new ComplexValueImpl();

    String type = null;
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      if (type == null && field.getKey().endsWith(Constants.JSON_TYPE_SUFFIX)) {
        type = field.getValue().asText();
      } else {
        final JSONPropertyImpl property = new JSONPropertyImpl();
        property.setName(field.getKey());
        property.setType(type);
        type = null;

        value(property, field.getValue());
        value.get().add(property);
      }
    }

    return value;
  }

  private CollectionValue fromCollection(final Iterator<JsonNode> nodeItor, final EdmTypeInfo typeInfo) {
    final CollectionValueImpl value = new CollectionValueImpl();

    final EdmTypeInfo type = typeInfo == null
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(typeInfo.getFullQualifiedName().toString()).build();

    while (nodeItor.hasNext()) {
      final JsonNode child = nodeItor.next();

      if (child.isValueNode()) {
        value.get().add(fromPrimitive(child, type));
      } else if (child.isContainerNode()) {
        if (child.has(Constants.JSON_TYPE)) {
          ((ObjectNode) child).remove(Constants.JSON_TYPE);
        }
        value.get().add(fromComplex(child));
      }
    }

    return value;
  }

  protected void value(final JSONPropertyImpl property, final JsonNode node) {
    final EdmTypeInfo typeInfo = StringUtils.isBlank(property.getType())
            ? null
            : new EdmTypeInfo.Builder().setTypeExpression(property.getType()).build();

    final ODataPropertyType propType = typeInfo == null
            ? guessPropertyType(node)
            : typeInfo.isCollection()
            ? ODataPropertyType.COLLECTION
            : typeInfo.isPrimitiveType()
            ? ODataPropertyType.PRIMITIVE
            : ODataPropertyType.COMPLEX;

    switch (propType) {
      case COLLECTION:
        property.setValue(fromCollection(node.elements(), typeInfo));
        break;

      case COMPLEX:
        if (node.has(Constants.JSON_TYPE)) {
          property.setType(node.get(Constants.JSON_TYPE).asText());
          ((ObjectNode) node).remove(Constants.JSON_TYPE);
        }
        property.setValue(fromComplex(node));
        break;

      case PRIMITIVE:
        if (property.getType() == null) {
          property.setType(getPrimitiveType(node).getFullQualifiedName().toString());
        }
        property.setValue(fromPrimitive(node, typeInfo));
        break;

      case EMPTY:
      default:
        property.setValue(new PrimitiveValueImpl(StringUtils.EMPTY));
    }
  }
}
