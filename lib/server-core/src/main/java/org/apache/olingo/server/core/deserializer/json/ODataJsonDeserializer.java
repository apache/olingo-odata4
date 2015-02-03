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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ODataJsonDeserializer implements ODataDeserializer {

  private static final String ODATA_ANNOTATION_MARKER = "@";
  private static final String ODATA_CONTROL_INFORMATION_PREFIX = "@odata.";

  @Override
  public EntitySet entityCollection(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      return consumeEntitySetNode(edmEntityType, tree);
    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate json property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_JSON_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occurred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private EntitySet consumeEntitySetNode(EdmEntityType edmEntityType, final ObjectNode tree)
      throws DeserializerException {
    EntitySetImpl entitySet = new EntitySetImpl();

    // Consume entities
    JsonNode jsonNode = tree.get(Constants.VALUE);
    if (jsonNode != null) {
      if (!jsonNode.isArray()) {
        throw new DeserializerException("The content of the value tag must be an Array but is not. ",
            DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
      }

      entitySet.getEntities().addAll(consumeEntitySetArray(edmEntityType, jsonNode));
      tree.remove(Constants.VALUE);
    } else {
      throw new DeserializerException("Could not find value array.",
          DeserializerException.MessageKeys.VALUE_ARRAY_NOT_PRESENT);
    }

    final List<String> toRemove = new ArrayList<String>();
    Iterator<Entry<String, JsonNode>> fieldsIterator = tree.fields();
    while (fieldsIterator.hasNext()) {
      Map.Entry<String, JsonNode> field = fieldsIterator.next();

      if (field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)) {
        // Control Information is ignored for requests as per specification chapter "4.5 Control Information"
        toRemove.add(field.getKey());
      } else if (field.getKey().contains(ODATA_ANNOTATION_MARKER)) {
        throw new DeserializerException("Custom annotation with field name: " + field.getKey() + " not supported",
            DeserializerException.MessageKeys.NOT_IMPLEMENTED);
      }
    }
    // remove here to avoid iterator issues.
    tree.remove(toRemove);
    assertJsonNodeIsEmpty(tree);

    return entitySet;
  }

  private List<Entity> consumeEntitySetArray(EdmEntityType edmEntityType, JsonNode jsonNode)
      throws DeserializerException {
    List<Entity> entities = new ArrayList<Entity>();
    for (JsonNode arrayElement : jsonNode) {
      if (arrayElement.isArray() || arrayElement.isValueNode()) {
        throw new DeserializerException("Nested Arrays and primitive values are not allowed for an entity value.",
            DeserializerException.MessageKeys.INVALID_ENTITY);
      }

      entities.add(consumeEntityNode(edmEntityType, (ObjectNode) arrayElement));
    }
    return entities;
  }

  @Override
  public Entity entity(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      return consumeEntityNode(edmEntityType, tree);

    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occurred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }

  }

  private Entity consumeEntityNode(EdmEntityType edmEntityType, final ObjectNode tree) throws DeserializerException {
    EntityImpl entity = new EntityImpl();
    entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

    // Check and consume all Properties
    consumeEntityProperties(edmEntityType, tree, entity);

    // Check and consume all expanded Navigation Properties
    consumeExpandedNavigationProperties(edmEntityType, tree, entity);

    // consume remaining json node fields
    consumeRemainingJsonNodeFields(edmEntityType, tree, entity);

    assertJsonNodeIsEmpty(tree);

    return entity;
  }

  /**
   * Consume all remaining fields of Json ObjectNode and try to map found values
   * to according Entity fields and omit to be ignored OData fields (e.g. control information).
   *
   * @param edmEntityType edm entity type which for which the json node is consumed
   * @param node json node which is consumed
   * @param entity entity instance which is filled
   * @throws DeserializerException if an exception during consummation occurs
   */
  private void consumeRemainingJsonNodeFields(final EdmEntityType edmEntityType, final ObjectNode node,
      final EntityImpl
      entity) throws DeserializerException {
    final List<String> toRemove = new ArrayList<String>();
    Iterator<Entry<String, JsonNode>> fieldsIterator = node.fields();
    while (fieldsIterator.hasNext()) {
      Entry<String, JsonNode> field = fieldsIterator.next();

      if (field.getKey().contains(Constants.JSON_BIND_LINK_SUFFIX)) {
        Link bindingLink = consumeBindingLink(field.getKey(), field.getValue(), edmEntityType);
        entity.getNavigationBindings().add(bindingLink);
        toRemove.add(field.getKey());
      } else if (field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)) {
        // Control Information is ignored for requests as per specification chapter "4.5 Control Information"
        toRemove.add(field.getKey());
      } else if (field.getKey().contains(ODATA_ANNOTATION_MARKER)) {
        throw new DeserializerException("Custom annotation with field name: " + field.getKey() + " not supported",
            DeserializerException.MessageKeys.NOT_IMPLEMENTED);
      }
    }
    // remove here to avoid iterator issues.
    node.remove(toRemove);
  }

  private void consumeEntityProperties(final EdmEntityType edmEntityType, final ObjectNode node, final EntityImpl
      entity) throws DeserializerException {
    List<String> propertyNames = edmEntityType.getPropertyNames();
    for (String propertyName : propertyNames) {
      JsonNode jsonNode = node.get(propertyName);
      if (jsonNode != null) {
        EdmProperty edmProperty = (EdmProperty) edmEntityType.getProperty(propertyName);
        if (jsonNode.isNull() && !isNullable(edmProperty)) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        Property property = consumePropertyNode(edmProperty, jsonNode);
        entity.addProperty(property);
        node.remove(propertyName);
      }
    }
  }

  private void consumeExpandedNavigationProperties(final EdmEntityType edmEntityType, final ObjectNode node,
      final EntityImpl entity) throws DeserializerException {
    List<String> navigationPropertyNames = edmEntityType.getNavigationPropertyNames();
    for (String navigationPropertyName : navigationPropertyNames) {
      // read expanded navigation property
      JsonNode jsonNode = node.get(navigationPropertyName);
      if (jsonNode != null) {
        EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(navigationPropertyName);
        boolean isNullable = edmNavigationProperty.isNullable() == null ? true : edmNavigationProperty.isNullable();
        if (jsonNode.isNull() && !isNullable) {
          throw new DeserializerException("Property: " + navigationPropertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, navigationPropertyName);
        }

        LinkImpl link = new LinkImpl();
        link.setTitle(navigationPropertyName);
        if (jsonNode.isArray() && edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());
          EntitySetImpl inlineEntitySet = new EntitySetImpl();
          inlineEntitySet.getEntities().addAll(consumeEntitySetArray(edmNavigationProperty.getType(), jsonNode));
          link.setInlineEntitySet(inlineEntitySet);
        } else if (!jsonNode.isArray() && !jsonNode.isValueNode() && !edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
          if (!jsonNode.isNull()) {
            Entity inlineEntity = consumeEntityNode(edmNavigationProperty.getType(), (ObjectNode) jsonNode);
            link.setInlineEntity(inlineEntity);
          }
        } else {
          throw new DeserializerException("Invalid value: " + jsonNode.getNodeType()
              + " for expanded navigation property: " + navigationPropertyName,
              DeserializerException.MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, navigationPropertyName);
        }
        entity.getNavigationLinks().add(link);
        node.remove(navigationPropertyName);
      }
    }
  }

  private Link consumeBindingLink(String key, JsonNode jsonNode, EdmEntityType edmEntityType)
      throws DeserializerException {
    String[] splitKey = key.split("@");
    String navigationPropertyName = splitKey[0];
    EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(navigationPropertyName);
    if (edmNavigationProperty == null) {
      throw new DeserializerException("Invalid navigationPropertyName: " + navigationPropertyName,
          DeserializerException.MessageKeys.NAVIGATION_PROPERTY_NOT_FOUND, navigationPropertyName);
    }
    LinkImpl bindingLink = new LinkImpl();
    bindingLink.setTitle(navigationPropertyName);

    if (edmNavigationProperty.isCollection()) {
      assertIsNullNode(key, jsonNode);
      if (!jsonNode.isArray()) {
        throw new DeserializerException("Binding annotation: " + key + " must be an array.",
            DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
      }
      List<String> bindingLinkStrings = new ArrayList<String>();
      for (JsonNode arrayValue : jsonNode) {
        assertIsNullNode(key, arrayValue);
        if (!arrayValue.isTextual()) {
          throw new DeserializerException("Binding annotation: " + key + " must have string valued array.",
              DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
        }
        bindingLinkStrings.add(arrayValue.asText());
      }
      bindingLink.setType(ODataLinkType.ENTITY_COLLECTION_BINDING.toString());
      bindingLink.setBindingLinks(bindingLinkStrings);
    } else {
      assertIsNullNode(key, jsonNode);
      if (!jsonNode.isValueNode()) {
        throw new DeserializerException("Binding annotation: " + key + " must be a string value.",
            DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
      }
      bindingLink.setBindingLink(jsonNode.asText());
      bindingLink.setType(ODataLinkType.ENTITY_BINDING.toString());
    }
    return bindingLink;
  }

  private void assertIsNullNode(String key, JsonNode jsonNode) throws DeserializerException {
    if (jsonNode.isNull()) {
      throw new DeserializerException("Annotation: " + key + "must not have a null value.",
          DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, key);
    }
  }

  private Property consumePropertyNode(final EdmProperty edmProperty, final JsonNode jsonNode)
      throws DeserializerException {
    Property property = new PropertyImpl();
    property.setName(edmProperty.getName());
    property.setType(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());
    if (edmProperty.isCollection()) {
      consumePropertyCollectionNode(edmProperty, jsonNode, property);
    } else {
      consumePropertySingleNode(edmProperty, jsonNode, property);
    }
    return property;
  }

  private void consumePropertySingleNode(final EdmProperty edmProperty,
      final JsonNode jsonNode, final Property property)
      throws DeserializerException {
    switch (edmProperty.getType().getKind()) {
    case PRIMITIVE:
      Object value = readPrimitiveValue(edmProperty, jsonNode);
      property.setValue(ValueType.PRIMITIVE, value);
      break;
    case DEFINITION:
      value = readTypeDefinitionValue(edmProperty, jsonNode);
      property.setValue(ValueType.PRIMITIVE, value);
      break;
    case ENUM:
      value = readEnumValue(edmProperty, jsonNode);
      property.setValue(ValueType.ENUM, value);
      break;
    case COMPLEX:
      value = readComplexNode(edmProperty, jsonNode);
      property.setValue(ValueType.COMPLEX, value);

      break;
    default:
      throw new DeserializerException("Invalid Type Kind for a property found: " + edmProperty.getType().getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private Object readComplexNode(final EdmProperty edmProperty, final JsonNode jsonNode)
      throws DeserializerException {
    // read and add all complex properties
    Object value = readComplexValue(edmProperty, jsonNode);

    final List<String> toRemove = new ArrayList<String>();
    Iterator<Entry<String, JsonNode>> fieldsIterator = jsonNode.fields();
    while (fieldsIterator.hasNext()) {
      Entry<String, JsonNode> field = fieldsIterator.next();

      if (field.getKey().contains(ODATA_CONTROL_INFORMATION_PREFIX)) {
        // Control Information is ignored for requests as per specification chapter "4.5 Control Information"
        toRemove.add(field.getKey());
      } else if (field.getKey().contains(ODATA_ANNOTATION_MARKER)) {
        throw new DeserializerException("Custom annotation with field name: " + field.getKey() + " not supported",
            DeserializerException.MessageKeys.NOT_IMPLEMENTED);
      }
    }
    // remove here to avoid iterator issues.
    if (!jsonNode.isNull()) {
      ((ObjectNode) jsonNode).remove(toRemove);
    }
    // Afterwards the node must be empty
    assertJsonNodeIsEmpty(jsonNode);

    return value;
  }

  private void consumePropertyCollectionNode(final EdmProperty edmProperty, final JsonNode jsonNode,
      final Property property) throws DeserializerException {
    if (!jsonNode.isArray()) {
      throw new DeserializerException("Value for property: " + edmProperty.getName()
          + " must be an array but is not.", DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY,
          edmProperty.getName());
    }
    List<Object> valueArray = new ArrayList<Object>();
    Iterator<JsonNode> iterator = jsonNode.iterator();
    switch (edmProperty.getType().getKind()) {
    case PRIMITIVE:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readPrimitiveValue(edmProperty, arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_PRIMITIVE, valueArray);
      break;
    case DEFINITION:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readTypeDefinitionValue(edmProperty, arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_PRIMITIVE, valueArray);
      break;
    case ENUM:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readEnumValue(edmProperty, arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_ENUM, valueArray);
      break;
    case COMPLEX:
      while (iterator.hasNext()) {
        // read and add all complex properties
        Object value = readComplexNode(edmProperty, iterator.next());
        Property complex = new PropertyImpl();
        complex.setName(edmProperty.getName());
        complex.setType(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());
        complex.setValue(ValueType.COMPLEX, value);
        valueArray.add(complex);
      }
      property.setValue(ValueType.COLLECTION_COMPLEX, valueArray);
      break;
    default:
      throw new DeserializerException("Invalid Type Kind for a property found: " + edmProperty.getType().getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private Object readComplexValue(EdmProperty edmComplexProperty, JsonNode jsonNode) throws DeserializerException {
    if (isValidNull(edmComplexProperty, jsonNode)) {
      return null;
    }
    if (jsonNode.isArray() || !jsonNode.isContainerNode()) {
      throw new DeserializerException(
          "Invalid value for property: " + edmComplexProperty.getName() + " must not be an array or primitive value.",
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, edmComplexProperty.getName());
    }
    // Even if there are no properties defined we have to give back an empty list
    List<Property> propertyList = new ArrayList<Property>();
    EdmComplexType edmType = (EdmComplexType) edmComplexProperty.getType();
    // Check and consume all Properties
    for (String propertyName : edmType.getPropertyNames()) {
      JsonNode subNode = jsonNode.get(propertyName);
      if (subNode != null) {
        EdmProperty edmProperty = (EdmProperty) edmType.getProperty(propertyName);
        if (subNode.isNull() && !isNullable(edmProperty)) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        Property property = consumePropertyNode(edmProperty, subNode);
        propertyList.add(property);
        ((ObjectNode) jsonNode).remove(propertyName);
      }
    }
    return propertyList;
  }

  private boolean isNullable(EdmProperty edmProperty) {
    return edmProperty.isNullable() == null ? true : edmProperty.isNullable();
  }

  private Object readTypeDefinitionValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(edmProperty, jsonNode);
    if (isValidNull(edmProperty, jsonNode)) {
      return null;
    }
    try {
      EdmTypeDefinition edmTypeDefinition = (EdmTypeDefinition) edmProperty.getType();
      checkJsonTypeBasedOnPrimitiveType(edmProperty.getName(), edmTypeDefinition.getUnderlyingType().getName(),
          jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(edmProperty, edmTypeDefinition.getUnderlyingType());
      return edmTypeDefinition.valueOfString(jsonNode.asText(), edmProperty.isNullable(),
          edmTypeDefinition.getMaxLength(),
          edmTypeDefinition.getPrecision(), edmTypeDefinition.getScale(), edmTypeDefinition.isUnicode(),
          javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private boolean isValidNull(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    if (jsonNode.isNull()) {
      if (isNullable(edmProperty)) {
        return true;
      } else {
        throw new DeserializerException("Property: " + edmProperty.getName() + " must not be null.",
            DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, edmProperty.getName());
      }

    }
    return false;
  }

  private Object readEnumValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(edmProperty, jsonNode);
    if (isValidNull(edmProperty, jsonNode)) {
      return null;
    }
    try {
      EdmEnumType edmEnumType = (EdmEnumType) edmProperty.getType();
      checkJsonTypeBasedOnPrimitiveType(edmProperty.getName(), edmEnumType.getUnderlyingType().getName(), jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(edmProperty, edmEnumType.getUnderlyingType());
      return edmEnumType
          .valueOfString(jsonNode.asText(), edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty
              .getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private Object readPrimitiveValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(edmProperty, jsonNode);
    if (isValidNull(edmProperty, jsonNode)) {
      return null;
    }
    try {
      EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmProperty.getType();
      checkJsonTypeBasedOnPrimitiveType(edmProperty.getName(), edmPrimitiveType.getName(), jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(edmProperty, edmPrimitiveType);
      return edmPrimitiveType.valueOfString(jsonNode.asText(), edmProperty.isNullable(),
          edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
          edmProperty.isUnicode(), javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  /**
   * This method either returns the primitive types default class or the manually mapped class if present.
   * @param edmProperty
   * @param edmPrimitiveType
   * @return the java class to be used during deserialization
   */
  private Class<?> getJavaClassForPrimitiveType(EdmProperty edmProperty, EdmPrimitiveType edmPrimitiveType) {
    Class<?> javaClass = null;
    if (edmProperty.getMapping() != null && edmProperty.getMapping().getMappedJavaClass() != null) {
      javaClass = edmProperty.getMapping().getMappedJavaClass();
    } else {
      javaClass = edmPrimitiveType.getDefaultType();
    }

    edmPrimitiveType.getDefaultType();
    return javaClass;
  }

  /**
   * Check if JsonNode is a value node (<code>jsonNode.isValueNode()</code>) and if not throw
   * an DeserializerException.
   *
   * @param edmProperty property which is checked
   * @param jsonNode node which is checked
   * @throws DeserializerException is thrown if json node is not a value node
   */
  private void checkForValueNode(final EdmProperty edmProperty, final JsonNode jsonNode)
      throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException(
          "Invalid value for property: " + edmProperty.getName() + " must not be an object or array.",
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  /**
   * Validate that node is empty (<code>node.size == 0</code>) and if not throw
   * an <code>DeserializerException</code>.
   *
   * @param node node to be checked
   * @throws DeserializerException if node is not empty
   */
  private void assertJsonNodeIsEmpty(JsonNode node) throws DeserializerException {
    if (node.size() != 0) {
      final String unknownField = node.fieldNames().next();
      throw new DeserializerException("Tree should be empty but still has content left: " + unknownField,
          DeserializerException.MessageKeys.UNKOWN_CONTENT, unknownField);
    }
  }

  private void checkJsonTypeBasedOnPrimitiveType(String propertyName, String edmPrimitiveTypeName, JsonNode jsonNode)
      throws DeserializerException {
    EdmPrimitiveTypeKind primKind;
    try {
      primKind = EdmPrimitiveTypeKind.valueOf(ODataServiceVersion.V40, edmPrimitiveTypeName);
    } catch (IllegalArgumentException e) {
      throw new DeserializerException("Unknown Primitive Type: " + edmPrimitiveTypeName, e,
          DeserializerException.MessageKeys.UNKNOWN_PRIMITIVE_TYPE, edmPrimitiveTypeName, propertyName);
    }
    switch (primKind) {
    // Booleans
    case Boolean:
      if (!jsonNode.isBoolean()) {
        throw new DeserializerException("Invalid json type: " + jsonNode.getNodeType() + " for edm " + primKind
            + " property: " + propertyName, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, propertyName);
      }
      break;
    // Numbers
    case Int16:
    case Int32:
    case Int64:
    case Byte:
    case SByte:
    case Single:
    case Double:
    case Decimal:
      if (!jsonNode.isNumber()) {
        throw new DeserializerException("Invalid json type: " + jsonNode.getNodeType() + " for edm " + primKind
            + " property: " + propertyName, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, propertyName);
      }
      break;
    // Strings
    case String:
    case Binary:
    case Date:
    case DateTimeOffset:
    case Duration:
    case Guid:
    case TimeOfDay:
      if (!jsonNode.isTextual()) {
        throw new DeserializerException("Invalid json type: " + jsonNode.getNodeType() + " for edm " + primKind
            + " property: " + propertyName, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, propertyName);
      }
      break;
    default:
      throw new DeserializerException("Unsupported Edm Primitive Type: " + primKind,
          DeserializerException.MessageKeys.NOT_IMPLEMENTED);
    }
  }

  @Override
  public Property property(InputStream stream, EdmProperty edmProperty)
      throws DeserializerException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      Property property = null;
      JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        property = consumePropertyNode(edmProperty, jsonNode);
        tree.remove(Constants.VALUE);
      } else {
        property = consumePropertyNode(edmProperty, tree);
      }
      return property;
    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occurred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  public Map<String, String> read(InputStream stream, String... keys) throws DeserializerException {
    try {
      HashMap<String, String> parsedValues = new HashMap<String, String>();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      for (String key:keys) {
        JsonNode jsonNode = tree.get(Constants.VALUE);
        if (jsonNode != null) {
          parsedValues.put(key, jsonNode.asText());
          tree.remove(Constants.VALUE);
          // if this is value there can be only one property
          return parsedValues;
        }
        parsedValues.put(key, tree.get(key).asText());
      }
      return parsedValues;
    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occurred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }
}
