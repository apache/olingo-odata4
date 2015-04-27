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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Parameter;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmMapping;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmParameter;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.DeserializerResultImpl;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilder;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilderImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ODataJsonDeserializer implements ODataDeserializer {

  private static final String ODATA_ANNOTATION_MARKER = "@";
  private static final String ODATA_CONTROL_INFORMATION_PREFIX = "@odata.";

  @Override
  public DeserializerResult entityCollection(InputStream stream, EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);

      return DeserializerResultImpl.with().entityCollection(consumeEntitySetNode(edmEntityType, tree, null))
          .build();
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

  private EntityCollection consumeEntitySetNode(EdmEntityType edmEntityType, final ObjectNode tree,
      final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    EntityCollection entitySet = new EntityCollection();

    // Consume entities
    JsonNode jsonNode = tree.get(Constants.VALUE);
    if (jsonNode != null) {
      if (!jsonNode.isArray()) {
        throw new DeserializerException("The content of the value tag must be an Array but is not. ",
            DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
      }

      entitySet.getEntities().addAll(consumeEntitySetArray(edmEntityType, jsonNode, expandBuilder));
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

  private List<Entity> consumeEntitySetArray(EdmEntityType edmEntityType, JsonNode jsonNode,
      final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    List<Entity> entities = new ArrayList<Entity>();
    for (JsonNode arrayElement : jsonNode) {
      if (arrayElement.isArray() || arrayElement.isValueNode()) {
        throw new DeserializerException("Nested Arrays and primitive values are not allowed for an entity value.",
            DeserializerException.MessageKeys.INVALID_ENTITY);
      }

      entities.add(consumeEntityNode(edmEntityType, (ObjectNode) arrayElement, expandBuilder));
    }
    return entities;
  }

  @Override
  public DeserializerResult entity(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);
      final ExpandTreeBuilderImpl expandBuilder = new ExpandTreeBuilderImpl();

      return DeserializerResultImpl.with().entity(consumeEntityNode(edmEntityType, tree, expandBuilder))
          .expandOption(expandBuilder.build())
          .build();

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

  private Entity consumeEntityNode(EdmEntityType edmEntityType, final ObjectNode tree,
      final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    Entity entity = new Entity();
    entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

    // Check and consume all Properties
    consumeEntityProperties(edmEntityType, tree, entity);

    // Check and consume all expanded Navigation Properties
    consumeExpandedNavigationProperties(edmEntityType, tree, entity, expandBuilder);

    // consume remaining json node fields
    consumeRemainingJsonNodeFields(edmEntityType, tree, entity);

    assertJsonNodeIsEmpty(tree);

    return entity;
  }

  @Override
  public DeserializerResult actionParameters(InputStream stream, final EdmAction edmAction)
      throws DeserializerException {
    try {
      ObjectNode tree = parseJsonTree(stream);
      Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();
      if (tree != null) {
        consumeParameters(edmAction, tree, parameters);
        
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
      }
      return DeserializerResultImpl.with().actionParameters(parameters).build();

    } catch (final JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (final JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (final IOException e) {
      throw new DeserializerException("An IOException occurred", e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private ObjectNode parseJsonTree(InputStream stream)
      throws IOException, JsonParseException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
    JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
    ObjectNode tree = parser.getCodec().readTree(parser);
    return tree;
  }

  private void consumeParameters(final EdmAction edmAction, ObjectNode node, Map<String, Parameter> parameters)
      throws DeserializerException {
    List<String> parameterNames = edmAction.getParameterNames();
    if (edmAction.isBound()) {
      // The binding parameter must not occur in the payload.
      parameterNames = parameterNames.subList(1, parameterNames.size());
    }
    for (final String paramName : parameterNames) {
      final EdmParameter edmParameter = edmAction.getParameter(paramName);
      Parameter parameter = new Parameter();
      parameter.setName(paramName);
      JsonNode jsonNode = node.get(paramName);

      switch (edmParameter.getType().getKind()) {
      case PRIMITIVE:
      case DEFINITION:
      case ENUM:
        if (jsonNode == null || jsonNode.isNull()) {
          if (!edmParameter.isNullable()) {
            throw new DeserializerException("Non-nullable parameter not present or null",
                DeserializerException.MessageKeys.INVALID_NULL_PARAMETER, paramName);
          }
          if (edmParameter.isCollection()) {
            throw new DeserializerException("Collection must not be null for parameter: " + paramName,
                DeserializerException.MessageKeys.INVALID_NULL_PARAMETER, paramName);
          }
          parameter.setValue(ValueType.PRIMITIVE, null);
        } else {
          Property consumePropertyNode =
              consumePropertyNode(edmParameter.getName(), edmParameter.getType(), edmParameter.isCollection(),
                  edmParameter.isNullable(), edmParameter.getMaxLength(), edmParameter.getPrecision(), edmParameter
                      .getScale(), true, edmParameter.getMapping(), jsonNode);
          parameter.setValue(consumePropertyNode.getValueType(), consumePropertyNode.getValue());
          parameters.put(paramName, parameter);
          node.remove(paramName);
        }
        break;
      case COMPLEX:
      case ENTITY:
        throw new DeserializerException("Entity an complex parameters currently not Implemented",
            DeserializerException.MessageKeys.NOT_IMPLEMENTED);
      default:
        throw new DeserializerException("Invalid type kind " + edmParameter.getType().getKind().toString()
            + " for action parameter: " + paramName, DeserializerException.MessageKeys.INVALID_ACTION_PARAMETER_TYPE,
            paramName);
      }
    }
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
      final Entity entity) throws DeserializerException {
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

  private void consumeEntityProperties(final EdmEntityType edmEntityType, final ObjectNode node,
      final Entity entity) throws DeserializerException {
    List<String> propertyNames = edmEntityType.getPropertyNames();
    for (String propertyName : propertyNames) {
      JsonNode jsonNode = node.get(propertyName);
      if (jsonNode != null) {
        EdmProperty edmProperty = (EdmProperty) edmEntityType.getProperty(propertyName);
        if (jsonNode.isNull() && !edmProperty.isNullable()) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        Property property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
            edmProperty.isCollection(),
            edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
            edmProperty.isUnicode(), edmProperty.getMapping(),
            jsonNode);
        entity.addProperty(property);
        node.remove(propertyName);
      }
    }
  }

  private void consumeExpandedNavigationProperties(final EdmEntityType edmEntityType, final ObjectNode node,
      final Entity entity, final ExpandTreeBuilder expandBuilder) throws DeserializerException {
    List<String> navigationPropertyNames = edmEntityType.getNavigationPropertyNames();
    for (String navigationPropertyName : navigationPropertyNames) {
      // read expanded navigation property
      JsonNode jsonNode = node.get(navigationPropertyName);
      if (jsonNode != null) {
        EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(navigationPropertyName);
        boolean isNullable = edmNavigationProperty.isNullable();
        if ((jsonNode.isNull() && !isNullable) || (jsonNode.isNull() && edmNavigationProperty.isCollection())) {
          throw new DeserializerException("Property: " + navigationPropertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, navigationPropertyName);
        }

        Link link = new Link();
        link.setTitle(navigationPropertyName);
        final ExpandTreeBuilder childExpandBuilder = (expandBuilder != null) ?
            expandBuilder.expand(edmNavigationProperty) : null;
        if (jsonNode.isArray() && edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());
          EntityCollection inlineEntitySet = new EntityCollection();
          inlineEntitySet.getEntities().addAll(consumeEntitySetArray(edmNavigationProperty.getType(), jsonNode,
              childExpandBuilder));
          link.setInlineEntitySet(inlineEntitySet);
        } else if (!jsonNode.isArray() && (!jsonNode.isValueNode() || jsonNode.isNull())
            && !edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
          if (!jsonNode.isNull()) {
            Entity inlineEntity = consumeEntityNode(edmNavigationProperty.getType(), (ObjectNode) jsonNode,
                childExpandBuilder);
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
    Link bindingLink = new Link();
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

  private Property consumePropertyNode(final String name, final EdmType type, final boolean isCollection,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, JsonNode jsonNode) throws DeserializerException {
    Property property = new Property();
    property.setName(name);
    property.setType(type.getFullQualifiedName().getFullQualifiedNameAsString());
    if (isCollection) {
      consumePropertyCollectionNode(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
          jsonNode, property);
    } else {
      consumePropertySingleNode(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
          jsonNode, property);
    }
    return property;
  }

  private void consumePropertySingleNode(final String name, final EdmType type,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, JsonNode jsonNode, Property property)
      throws DeserializerException {
    switch (type.getKind()) {
    case PRIMITIVE:
      Object value = readPrimitiveValue(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
          jsonNode);
      property.setValue(ValueType.PRIMITIVE, value);
      break;
    case DEFINITION:
      value = readTypeDefinitionValue(name, type, isNullable, mapping, jsonNode);
      property.setValue(ValueType.PRIMITIVE, value);
      break;
    case ENUM:
      value = readEnumValue(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
          jsonNode);
      property.setValue(ValueType.ENUM, value);
      break;
    case COMPLEX:
      value = readComplexNode(name, type, isNullable, jsonNode);
      property.setValue(ValueType.COMPLEX, value);

      break;
    default:
      throw new DeserializerException("Invalid Type Kind for a property found: " + type.getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
  }

  private Object readComplexNode(final String name, final EdmType type, final boolean isNullable, JsonNode jsonNode)
      throws DeserializerException {
    // read and add all complex properties
    ComplexValue value = readComplexValue(name, type, isNullable, jsonNode);

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

  private void consumePropertyCollectionNode(final String name, final EdmType type,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, JsonNode jsonNode, Property property)
      throws DeserializerException {
    if (!jsonNode.isArray()) {
      throw new DeserializerException("Value for property: " + name + " must be an array but is not.",
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
    List<Object> valueArray = new ArrayList<Object>();
    Iterator<JsonNode> iterator = jsonNode.iterator();
    switch (type.getKind()) {
    case PRIMITIVE:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readPrimitiveValue(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
            arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_PRIMITIVE, valueArray);
      break;
    case DEFINITION:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readTypeDefinitionValue(name, type, isNullable, mapping, arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_PRIMITIVE, valueArray);
      break;
    case ENUM:
      while (iterator.hasNext()) {
        JsonNode arrayElement = iterator.next();
        Object value = readEnumValue(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping,
            arrayElement);
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_ENUM, valueArray);
      break;
    case COMPLEX:
      while (iterator.hasNext()) {
        // read and add all complex properties
        Object value = readComplexNode(name, type, isNullable, iterator.next());
        valueArray.add(value);
      }
      property.setValue(ValueType.COLLECTION_COMPLEX, valueArray);
      break;
    default:
      throw new DeserializerException("Invalid Type Kind for a property found: " + type.getKind(),
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
  }

  private ComplexValue readComplexValue(final String name, final EdmType type,
      final boolean isNullable, JsonNode jsonNode) throws DeserializerException {
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    if (jsonNode.isArray() || !jsonNode.isContainerNode()) {
      throw new DeserializerException(
          "Invalid value for property: " + name + " must not be an array or primitive value.",
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
    }
    // Even if there are no properties defined we have to give back an empty list
    ComplexValue complexValue = new ComplexValue();
    EdmComplexType edmType = (EdmComplexType) type;
    // Check and consume all Properties
    for (String propertyName : edmType.getPropertyNames()) {
      JsonNode subNode = jsonNode.get(propertyName);
      if (subNode != null) {
        EdmProperty edmProperty = (EdmProperty) edmType.getProperty(propertyName);
        if (subNode.isNull() && !edmProperty.isNullable()) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        Property property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
            edmProperty.isCollection(),
            edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
            edmProperty.isUnicode(), edmProperty.getMapping(),
            subNode);
        complexValue.getValue().add(property);
        ((ObjectNode) jsonNode).remove(propertyName);
      }
    }
    return complexValue;
  }

  private Object readTypeDefinitionValue(final String name, final EdmType type,
      final boolean isNullable, final EdmMapping mapping, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(name, jsonNode);
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    try {
      EdmTypeDefinition edmTypeDefinition = (EdmTypeDefinition) type;
      checkJsonTypeBasedOnPrimitiveType(name, edmTypeDefinition.getUnderlyingType().getName(),
          jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(mapping, edmTypeDefinition.getUnderlyingType());
      return edmTypeDefinition.valueOfString(jsonNode.asText(), isNullable,
          edmTypeDefinition.getMaxLength(),
          edmTypeDefinition.getPrecision(), edmTypeDefinition.getScale(), edmTypeDefinition.isUnicode(),
          javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + name, e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
    }
  }

  private boolean isValidNull(final String name, final boolean isNullable, final JsonNode jsonNode)
      throws DeserializerException {
    if (jsonNode.isNull()) {
      if (isNullable) {
        return true;
      } else {
        throw new DeserializerException("Property: " + name + " must not be null.",
            DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, name);
      }

    }
    return false;
  }

  private Object readEnumValue(final String name, final EdmType type,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(name, jsonNode);
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    try {
      EdmEnumType edmEnumType = (EdmEnumType) type;
      // Enum values must be strings
      if (!jsonNode.isTextual()) {
        throw new DeserializerException("Invalid json type: " + jsonNode.getNodeType() + " for enum property: " + name,
            DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
      }

      Class<?> javaClass = getJavaClassForPrimitiveType(mapping, edmEnumType.getUnderlyingType());
      return edmEnumType.valueOfString(jsonNode.asText(),
          isNullable, maxLength, precision, scale, isUnicode, javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + name, e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
    }
  }

  private Object readPrimitiveValue(final String name, final EdmType type,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(name, jsonNode);
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    try {
      EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) type;
      checkJsonTypeBasedOnPrimitiveType(name, edmPrimitiveType.getName(), jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(mapping, edmPrimitiveType);
      return edmPrimitiveType.valueOfString(jsonNode.asText(),
          isNullable, maxLength, precision, scale, isUnicode, javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + name, e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
    }
  }

  /**
   * This method either returns the primitive types default class or the manually mapped class if present.
   * @param edmMapping
   * @param edmPrimitiveType
   * @return the java class to be used during deserialization
   */
  private Class<?> getJavaClassForPrimitiveType(EdmMapping mapping, EdmPrimitiveType edmPrimitiveType) {
    return mapping == null || mapping.getMappedJavaClass() == null ?
        edmPrimitiveType.getDefaultType() :
        mapping.getMappedJavaClass();
  }

  /**
   * Check if JsonNode is a value node (<code>jsonNode.isValueNode()</code>) and if not throw
   * an DeserializerException.
   * @param name name of property which is checked
   * @param jsonNode node which is checked
   * @throws DeserializerException is thrown if json node is not a value node
   */
  private void checkForValueNode(final String name, final JsonNode jsonNode) throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException("Invalid value for property: " + name + " must not be an object or array.",
          DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY, name);
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
      primKind = EdmPrimitiveTypeKind.valueOf(edmPrimitiveTypeName);
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
  public DeserializerResult property(InputStream stream, EdmProperty edmProperty)
      throws DeserializerException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      Property property = null;
      JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
            edmProperty.isCollection(),
            edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
            edmProperty.isUnicode(), edmProperty.getMapping(),
            jsonNode);
        tree.remove(Constants.VALUE);
      } else {
        property = consumePropertyNode(edmProperty.getName(), edmProperty.getType(),
            edmProperty.isCollection(),
            edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
            edmProperty.isUnicode(), edmProperty.getMapping(),
            tree);
      }
      return DeserializerResultImpl.with().property(property).build();
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

  public DeserializerResult entityReferences(InputStream stream) throws DeserializerException {
    try {
      ArrayList<URI> parsedValues = new ArrayList<URI>();
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);
      final String key = "@odata.id";
      JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        if (jsonNode.isArray()) {
          ArrayNode arrayNode = (ArrayNode) jsonNode;
          Iterator<JsonNode> it = arrayNode.iterator();
          while (it.hasNext()) {
            parsedValues.add(new URI(it.next().get(key).asText()));
          }
        } else {
          parsedValues.add(new URI(jsonNode.asText()));
        }
        tree.remove(Constants.VALUE);
        // if this is value there can be only one property
        return DeserializerResultImpl.with().entityReferences(parsedValues).build();
      }
      parsedValues.add(new URI(tree.get(key).asText()));
      return DeserializerResultImpl.with().entityReferences(parsedValues).build();
    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occurred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occurred", e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (URISyntaxException e) {
      throw new DeserializerException("failed to read @odata.id", e,
          DeserializerException.MessageKeys.UNKOWN_CONTENT);
    }
  }
}
