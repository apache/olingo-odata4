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
package org.apache.olingo.server.core.deserializer.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.PropertyType;
import org.apache.olingo.commons.api.data.Valuable;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
import org.apache.olingo.server.api.deserializer.DeserializerException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ODataJsonInstanceAnnotationDeserializer {

	private static final String ODATA_CONTROL_INFORMATION_PREFIX = "@odata.";
	
	/**
	 * Consume the instance annotation of an entity or a property
	 * @param key String
	 * @param value JsonNode
	 * @return Annotation of an entity
	 * @throws DeserializerException
	 */
	public Annotation consumeInstanceAnnotation(String key, JsonNode value) 
			throws DeserializerException {
		Annotation annotation = new Annotation();
		annotation.setTerm(key);
		try {
			value(annotation, value);
		} catch (EdmPrimitiveTypeException | IOException e) {
			throw new DeserializerException("Property: " + key,
					DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, key);
		}
		return annotation;
	}

	private void value(final Valuable valuable, final JsonNode node) 
			throws IOException, EdmPrimitiveTypeException {

		EdmTypeInfo typeInfo = null;

		final Map.Entry<PropertyType, EdmTypeInfo> guessed = guessPropertyType(node);
		if (typeInfo == null) {
			typeInfo = guessed.getValue();
		}

		final PropertyType propType = typeInfo == null ? guessed.getKey()
				: typeInfo.isCollection() ? PropertyType.COLLECTION
				: typeInfo.isPrimitiveType() ? PropertyType.PRIMITIVE
				: node.isValueNode() ? PropertyType.ENUM : PropertyType.COMPLEX;

		switch (propType) {
		case COLLECTION:
			fromCollection(valuable, node.elements(), typeInfo);
			break;

		case COMPLEX:
			if (node.has(Constants.JSON_TYPE)) {
				valuable.setType(node.get(Constants.JSON_TYPE).asText());
				((ObjectNode) node).remove(Constants.JSON_TYPE);
			}
			final Object value = fromComplex((ObjectNode) node);
			if (value instanceof ComplexValue) {
				((ComplexValue) value).setTypeName(valuable.getType());
			}
			valuable.setValue(ValueType.COMPLEX, value);
			break;

		case ENUM:
			if (!node.isNull()) {
				valuable.setValue(ValueType.ENUM, node.asText());
			}
			break;

		case PRIMITIVE:
			if (valuable.getType() == null && typeInfo != null) {
				valuable.setType(typeInfo.getFullQualifiedName().toString());
			}
			final Object primitiveValue = fromPrimitive(node, typeInfo);
			valuable.setValue(primitiveValue instanceof Geospatial ? 
					ValueType.GEOSPATIAL : ValueType.PRIMITIVE,
					primitiveValue);
			break;

		case EMPTY:
		default:
			valuable.setValue(ValueType.PRIMITIVE, "");
		}
	}

	private Object fromPrimitive(final JsonNode node, final EdmTypeInfo typeInfo) 
			throws EdmPrimitiveTypeException {
		return node.isNull() ? null
				: typeInfo == null ? node.asText()
				: ((EdmPrimitiveType) typeInfo.getType()).valueOfString(node.asText(), true, null,
				Constants.DEFAULT_PRECISION, Constants.DEFAULT_SCALE, true,
				((EdmPrimitiveType) typeInfo.getType()).getDefaultType());
	}

	private Object fromComplex(final ObjectNode node) throws IOException, EdmPrimitiveTypeException {

		final ComplexValue complexValue = new ComplexValue();
		final Set<String> toRemove = new HashSet<>();
		for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
			final Map.Entry<String, JsonNode> field = itor.next();
			if (field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)) {
				toRemove.add(field.getKey());
			} else {
				Property property = new Property();
				property.setName(field.getKey());
				value(property, field.getValue());
				complexValue.getValue().add(property);
			}
		}
		node.remove(toRemove);
		return complexValue;
	}

	private void fromCollection(final Valuable valuable, final Iterator<JsonNode> nodeItor, 
			final EdmTypeInfo typeInfo)
			throws IOException, EdmPrimitiveTypeException {

		final List<Object> values = new ArrayList<>();
		ValueType valueType = ValueType.COLLECTION_PRIMITIVE;

		final EdmTypeInfo type = typeInfo == null ? null
				: new EdmTypeInfo.Builder().setTypeExpression(
						typeInfo.getFullQualifiedName().toString()).build();

		while (nodeItor.hasNext()) {
			final JsonNode child = nodeItor.next();

			if (child.isValueNode()) {
				if (typeInfo == null || typeInfo.isPrimitiveType()) {
					final Object value = fromPrimitive(child, type);
					valueType = value instanceof Geospatial ? ValueType.COLLECTION_GEOSPATIAL
							: ValueType.COLLECTION_PRIMITIVE;
					values.add(value);
				} else {
					valueType = ValueType.COLLECTION_ENUM;
					values.add(child.asText());
				}
			} else if (child.isContainerNode()) {
				EdmTypeInfo childType = null;
				if (child.has(Constants.JSON_TYPE)) {
					String typeName = child.get(Constants.JSON_TYPE).asText();
					childType = typeName == null ? null : new EdmTypeInfo.Builder()
							.setTypeExpression(typeName).build();
					((ObjectNode) child).remove(Constants.JSON_TYPE);
				}
				final Object value = fromComplex((ObjectNode) child);
				if (childType != null) {
					((ComplexValue) value).setTypeName(childType.external());
				}
				valueType = ValueType.COLLECTION_COMPLEX;
				values.add(value);
			}
		}
		valuable.setValue(valueType, values);
	}

	private Map.Entry<PropertyType, EdmTypeInfo> guessPropertyType(final JsonNode node) {
		PropertyType type;
		String typeExpression = null;

		if (node.isValueNode() || node.isNull()) {
			type = PropertyType.PRIMITIVE;
			typeExpression = guessPrimitiveTypeKind(node).getFullQualifiedName().toString();
		} else if (node.isArray()) {
			type = PropertyType.COLLECTION;
			if (node.has(0) && node.get(0).isValueNode()) {
				typeExpression = "Collection(" + guessPrimitiveTypeKind(node.get(0)) + ')';
			}
		} else if (node.isObject()) {
			if (node.has(Constants.ATTR_TYPE)) {
				type = PropertyType.PRIMITIVE;
				typeExpression = "Edm.Geography" + node.get(Constants.ATTR_TYPE).asText();
			} else {
				type = PropertyType.COMPLEX;
			}
		} else {
			type = PropertyType.EMPTY;
		}

		final EdmTypeInfo typeInfo = typeExpression == null ? null
				: new EdmTypeInfo.Builder().setTypeExpression(typeExpression).build();
		return new SimpleEntry<>(type, typeInfo);
	}

	private EdmPrimitiveTypeKind guessPrimitiveTypeKind(final JsonNode node) {
		return node.isShort() ? EdmPrimitiveTypeKind.Int16
				: node.isInt() ? EdmPrimitiveTypeKind.Int32
				: node.isLong() ? EdmPrimitiveTypeKind.Int64
				: node.isBoolean() ? EdmPrimitiveTypeKind.Boolean
				: node.isFloat() ? EdmPrimitiveTypeKind.Single
				: node.isDouble() ? EdmPrimitiveTypeKind.Double
				: node.isBigDecimal() ? EdmPrimitiveTypeKind.Decimal
				: EdmPrimitiveTypeKind.String;
	}
}
