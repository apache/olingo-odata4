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
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerException.MessageKeys;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.DeserializerResultImpl;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilder;
import org.apache.olingo.server.core.deserializer.helper.ExpandTreeBuilderImpl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ODataJsonDeserializer implements ODataDeserializer {

  private static final String AN_IO_EXCEPTION_OCCURRED_MSG = "An IOException occurred";
  private static final String DUPLICATE_JSON_PROPERTY_DETECTED_MSG = "Duplicate json property detected";
  private static final String AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG = "A JsonParseException occurred";
  private static final String ODATA_ANNOTATION_MARKER = "@";
  private static final String ODATA_CONTROL_INFORMATION_PREFIX = "@odata.";
  private static final EdmPrimitiveType EDM_INT64 = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64);
  private static final EdmPrimitiveType EDM_DECIMAL = EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal);
  private final boolean isIEEE754Compatible;

  public ODataJsonDeserializer(final ContentType contentType) {
    isIEEE754Compatible = isODataIEEE754Compatible(contentType);
  }

  @Override
  public DeserializerResult entityCollection(final InputStream stream, final EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);

      return DeserializerResultImpl.with().entityCollection(consumeEntitySetNode(edmEntityType, tree, null))
          .build();
    } catch (JsonParseException e) {
      throw new DeserializerException(AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException(DUPLICATE_JSON_PROPERTY_DETECTED_MSG, e,
          DeserializerException.MessageKeys.DUPLICATE_JSON_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException(AN_IO_EXCEPTION_OCCURRED_MSG, e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private EntityCollection consumeEntitySetNode(final EdmEntityType edmEntityType, final ObjectNode tree,
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

  private List<Entity> consumeEntitySetArray(final EdmEntityType edmEntityType, final JsonNode jsonNode,
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
  public DeserializerResult entity(final InputStream stream, final EdmEntityType edmEntityType)
      throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);
      final ExpandTreeBuilderImpl expandBuilder = new ExpandTreeBuilderImpl();

      return DeserializerResultImpl.with().entity(consumeEntityNode(edmEntityType, tree, expandBuilder))
          .expandOption(expandBuilder.build())
          .build();

    } catch (JsonParseException e) {
      throw new DeserializerException(AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException(DUPLICATE_JSON_PROPERTY_DETECTED_MSG, e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException(AN_IO_EXCEPTION_OCCURRED_MSG, e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }

  }

  private Entity consumeEntityNode(final EdmEntityType edmEntityType, final ObjectNode tree,
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
  public DeserializerResult actionParameters(final InputStream stream, final EdmAction edmAction)
      throws DeserializerException {
    try {
      ObjectNode tree = parseJsonTree(stream);
      Map<String, Parameter> parameters = consumeParameters(edmAction, tree);

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
      return DeserializerResultImpl.with().actionParameters(parameters).build();

    } catch (final JsonParseException e) {
      throw new DeserializerException(AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (final JsonMappingException e) {
      throw new DeserializerException(DUPLICATE_JSON_PROPERTY_DETECTED_MSG, e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (final IOException e) {
      throw new DeserializerException(AN_IO_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private ObjectNode parseJsonTree(final InputStream stream) throws IOException, DeserializerException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
    JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
    final ObjectNode tree = parser.getCodec().readTree(parser);
    if (tree == null) {
      throw new DeserializerException("Invalid JSON syntax.",
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    }
    return tree;
  }

  private Map<String, Parameter> consumeParameters(final EdmAction edmAction, final ObjectNode node)
      throws DeserializerException {
    List<String> parameterNames = edmAction.getParameterNames();
    if (edmAction.isBound()) {
      // The binding parameter must not occur in the payload.
      parameterNames = parameterNames.subList(1, parameterNames.size());
    }
    Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();
    for (final String paramName : parameterNames) {
      final EdmParameter edmParameter = edmAction.getParameter(paramName);

      switch (edmParameter.getType().getKind()) {
      case PRIMITIVE:
      case DEFINITION:
      case ENUM:
      case COMPLEX:
        Parameter parameter = createParameter(node.get(paramName), paramName, edmParameter);
        parameters.put(paramName, parameter);
        node.remove(paramName);
        break;
      case ENTITY:
        throw new DeserializerException("Entity parameters are not allowed",
            DeserializerException.MessageKeys.INVALID_ACTION_PARAMETER_TYPE);
      default:
        throw new DeserializerException("Invalid type kind " + edmParameter.getType().getKind().toString()
            + " for action parameter: " + paramName, DeserializerException.MessageKeys.INVALID_ACTION_PARAMETER_TYPE,
            paramName);
      }
    }
    return parameters;
  }

  private Parameter createParameter(JsonNode node, String paramName, EdmParameter edmParameter) throws
      DeserializerException {
    Parameter parameter = new Parameter();
    parameter.setName(paramName);
    if (node == null || node.isNull()) {
      if (!edmParameter.isNullable()) {
        throw new DeserializerException("Non-nullable parameter not present or null",
            MessageKeys.INVALID_NULL_PARAMETER, paramName);
      }
      if (edmParameter.isCollection()) {
        throw new DeserializerException("Collection must not be null for parameter: " + paramName,
            MessageKeys.INVALID_NULL_PARAMETER, paramName);
      }
      parameter.setValue(ValueType.PRIMITIVE, null);
    } else {
      Property consumePropertyNode =
          consumePropertyNode(edmParameter.getName(), edmParameter.getType(), edmParameter.isCollection(),
              edmParameter.isNullable(), edmParameter.getMaxLength(), edmParameter.getPrecision(), edmParameter
                  .getScale(), true, edmParameter.getMapping(), node);
      parameter.setValue(consumePropertyNode.getValueType(), consumePropertyNode.getValue());
    }
    return parameter;
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
            edmProperty.isCollection(), edmProperty.isNullable(), edmProperty.getMaxLength(),
            edmProperty.getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), edmProperty.getMapping(),
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
        checkNotNullOrValidNull(jsonNode, edmNavigationProperty);

        Link link = createLink(expandBuilder, navigationPropertyName, jsonNode, edmNavigationProperty);
        entity.getNavigationLinks().add(link);
        node.remove(navigationPropertyName);
      }
    }
  }

  /**
   * Check if jsonNode is not null or if null but nullable or collection navigationProperty
   *
   * @param jsonNode related json node
   * @param edmNavigationProperty related navigation property
   * @throws DeserializerException if jsonNode is not null or if null but nullable or collection navigationProperty
   */
  private void checkNotNullOrValidNull(JsonNode jsonNode,
      EdmNavigationProperty edmNavigationProperty) throws DeserializerException {
    boolean isNullable = edmNavigationProperty.isNullable();
    if ((jsonNode.isNull() && !isNullable) || (jsonNode.isNull() && edmNavigationProperty.isCollection())) {
      throw new DeserializerException("Property: " + edmNavigationProperty.getName() + " must not be null.",
          MessageKeys.INVALID_NULL_PROPERTY, edmNavigationProperty.getName());
    }
  }

  private Link createLink(ExpandTreeBuilder expandBuilder, String navigationPropertyName, JsonNode jsonNode,
      EdmNavigationProperty edmNavigationProperty) throws DeserializerException {
    Link link = new Link();
    link.setTitle(navigationPropertyName);
    final ExpandTreeBuilder childExpandBuilder = (expandBuilder != null) ?
        expandBuilder.expand(edmNavigationProperty) : null;
    if (jsonNode.isArray() && edmNavigationProperty.isCollection()) {
      link.setType(Constants.ENTITY_SET_NAVIGATION_LINK_TYPE);
      EntityCollection inlineEntitySet = new EntityCollection();
      inlineEntitySet.getEntities().addAll(
          consumeEntitySetArray(edmNavigationProperty.getType(), jsonNode, childExpandBuilder));
      link.setInlineEntitySet(inlineEntitySet);
    } else if (!jsonNode.isArray() && (!jsonNode.isValueNode() || jsonNode.isNull())
        && !edmNavigationProperty.isCollection()) {
      link.setType(Constants.ENTITY_NAVIGATION_LINK_TYPE);
      if (!jsonNode.isNull()) {
        Entity inlineEntity = consumeEntityNode(edmNavigationProperty.getType(), (ObjectNode) jsonNode,
            childExpandBuilder);
        link.setInlineEntity(inlineEntity);
      }
    } else {
      throw new DeserializerException("Invalid value: " + jsonNode.getNodeType()
          + " for expanded navigation property: " + navigationPropertyName,
          MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, navigationPropertyName);
    }
    return link;
  }

  private Link consumeBindingLink(final String key, final JsonNode jsonNode, final EdmEntityType edmEntityType)
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
      bindingLink.setType(Constants.ENTITY_COLLECTION_BINDING_LINK_TYPE);
      bindingLink.setBindingLinks(bindingLinkStrings);
    } else {
      assertIsNullNode(key, jsonNode);
      if (!jsonNode.isValueNode()) {
        throw new DeserializerException("Binding annotation: " + key + " must be a string value.",
            DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE, key);
      }
      bindingLink.setBindingLink(jsonNode.asText());
      bindingLink.setType(Constants.ENTITY_BINDING_LINK_TYPE);
    }
    return bindingLink;
  }

  private void assertIsNullNode(final String key, final JsonNode jsonNode) throws DeserializerException {
    if (jsonNode.isNull()) {
      throw new DeserializerException("Annotation: " + key + "must not have a null value.",
          DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION, key);
    }
  }

  private Property consumePropertyNode(final String name, final EdmType type, final boolean isCollection,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode) throws DeserializerException {
    Property property = new Property();
    property.setName(name);
    property.setType(type.getFullQualifiedName().getFullQualifiedNameAsString());
    if (isCollection) {
      consumePropertyCollectionNode(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping, jsonNode,
          property);
    } else {
      consumePropertySingleNode(name, type, isNullable, maxLength, precision, scale, isUnicode, mapping, jsonNode,
          property);
    }
    return property;
  }

  private void consumePropertySingleNode(final String name, final EdmType type,
      final boolean isNullable, final Integer maxLength, final Integer precision, final Integer scale,
      final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode, final Property property)
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

  private Object readComplexNode(final String name, final EdmType type, final boolean isNullable,
      final JsonNode jsonNode)
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
      final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode, final Property property)
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
      final boolean isNullable, final JsonNode jsonNode) throws DeserializerException {
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
      final boolean isNullable, final EdmMapping mapping, final JsonNode jsonNode) throws DeserializerException {
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
      final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode) throws DeserializerException {
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
      final boolean isUnicode, final EdmMapping mapping, final JsonNode jsonNode) throws DeserializerException {
    checkForValueNode(name, jsonNode);
    if (isValidNull(name, isNullable, jsonNode)) {
      return null;
    }
    try {
      EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) type;
      checkJsonTypeBasedOnPrimitiveType(name, edmPrimitiveType.getName(), jsonNode);
      Class<?> javaClass = getJavaClassForPrimitiveType(mapping, edmPrimitiveType);
      String jsonNodeAsText = jsonNode.asText();

      if (isIEEE754Compatible
          && (edmPrimitiveType.equals(EDM_INT64) || edmPrimitiveType.equals(EDM_DECIMAL))
              && jsonNodeAsText.length() == 0) {
        throw new DeserializerException("IEEE754Compatible values must not be of length 0",
            MessageKeys.INVALID_NULL_PROPERTY, name);
      }

      return edmPrimitiveType.valueOfString(jsonNodeAsText, isNullable, maxLength, precision, scale, isUnicode,
          javaClass);
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Invalid value: " + jsonNode.asText() + " for property: " + name, e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, name);
    }
  }

  /**
   * This method either returns the primitive types default class or the manually mapped class if present.
   * @param mapping
   * @param edmPrimitiveType
   * @return the java class to be used during deserialization
   */
  private Class<?> getJavaClassForPrimitiveType(final EdmMapping mapping, final EdmPrimitiveType edmPrimitiveType) {
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
  private void assertJsonNodeIsEmpty(final JsonNode node) throws DeserializerException {
    if (node.size() != 0) {
      final String unknownField = node.fieldNames().next();
      throw new DeserializerException("Tree should be empty but still has content left: " + unknownField,
          DeserializerException.MessageKeys.UNKNOWN_CONTENT, unknownField);
    }
  }

  private void checkJsonTypeBasedOnPrimitiveType(final String propertyName, final String edmPrimitiveTypeName,
      final JsonNode jsonNode)
      throws DeserializerException {

    EdmPrimitiveTypeKind primKind;
    try {
      primKind = EdmPrimitiveTypeKind.valueOf(edmPrimitiveTypeName);
    } catch (IllegalArgumentException e) {
      throw new DeserializerException("Unknown Primitive Type: " + edmPrimitiveTypeName, e,
          DeserializerException.MessageKeys.UNKNOWN_PRIMITIVE_TYPE, edmPrimitiveTypeName, propertyName);
    }

    boolean valid = matchTextualCase(jsonNode, primKind);
    valid |= matchNumberCase(jsonNode, primKind);
    valid |= matchBooleanCase(jsonNode, primKind);
    valid |= matchIEEENumberCase(jsonNode, primKind);

    if (!valid) {
      throw new DeserializerException("Invalid json type: " + jsonNode.getNodeType() + " for edm " + primKind
          + " property: " + propertyName, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, propertyName);
    }
  }

  private boolean matchIEEENumberCase(JsonNode node, EdmPrimitiveTypeKind primKind) {
    switch (primKind) {
    case Int64:
    case Decimal:
      // Numbers (either numbers or string)
      if (isIEEE754Compatible) {
        return node.isTextual();
      } else {
        return node.isNumber();
      }
    default:
      return false;
    }
  }

  private boolean matchBooleanCase(JsonNode node, EdmPrimitiveTypeKind primKind) {
    if (node.isBoolean()) {
      switch (primKind) {
      case Boolean:
        return true;
      default:
        return false;
      }
    }
    return false;
  }

  private boolean matchNumberCase(JsonNode node, EdmPrimitiveTypeKind primKind) {
    if (node.isNumber()) {
      switch (primKind) {
      // Numbers (must be numbers)
      case Int16:
      case Int32:
      case Byte:
      case SByte:
      case Single:
      case Double:
        return true;
      default:
        return false;
      }
    }
    return false;
  }

  private boolean matchTextualCase(JsonNode node, EdmPrimitiveTypeKind primKind) {
    if (node.isTextual()) {
      switch (primKind) {
      case String:
      case Binary:
      case Date:
      case DateTimeOffset:
      case Duration:
      case Guid:
      case TimeOfDay:
        return true;
      default:
        return false;
      }
    }
    return false;
  }

  @Override
  public DeserializerResult property(final InputStream stream, final EdmProperty edmProperty)
      throws DeserializerException {
    try {
      final ObjectNode tree = parseJsonTree(stream);

      final Property property;
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
      throw new DeserializerException(AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException(DUPLICATE_JSON_PROPERTY_DETECTED_MSG, e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException(AN_IO_EXCEPTION_OCCURRED_MSG, e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  @Override
  public DeserializerResult entityReferences(final InputStream stream) throws DeserializerException {
    try {
      ArrayList<URI> parsedValues = new ArrayList<URI>();
      final ObjectNode tree = parseJsonTree(stream);
      final String key = Constants.JSON_ID;
      JsonNode jsonNode = tree.get(Constants.VALUE);
      if (jsonNode != null) {
        if (jsonNode.isArray()) {
          ArrayNode arrayNode = (ArrayNode) jsonNode;
          Iterator<JsonNode> it = arrayNode.iterator();
          while (it.hasNext()) {
            final JsonNode next = it.next();
            if (next.has(key)) {
              parsedValues.add(new URI(next.get(key).asText()));
            }
          }
        } else {
          throw new DeserializerException("Value must be an array", DeserializerException.MessageKeys.UNKNOWN_CONTENT);
        }
        tree.remove(Constants.VALUE);
        return DeserializerResultImpl.with().entityReferences(parsedValues).build();
      }
      if (tree.get(key) != null) {
        parsedValues.add(new URI(tree.get(key).asText()));
      } else {
        throw new DeserializerException("Missing entity reference", DeserializerException.MessageKeys.UNKNOWN_CONTENT);
      }
      return DeserializerResultImpl.with().entityReferences(parsedValues).build();
    } catch (JsonParseException e) {
      throw new DeserializerException(AN_JSON_PARSE_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException(DUPLICATE_JSON_PROPERTY_DETECTED_MSG, e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException(AN_IO_EXCEPTION_OCCURRED_MSG, e,
          DeserializerException.MessageKeys.IO_EXCEPTION);
    } catch (URISyntaxException e) {
      throw new DeserializerException("failed to read @odata.id", e,
          DeserializerException.MessageKeys.UNKNOWN_CONTENT);
    }
  }

  private boolean isODataIEEE754Compatible(final ContentType contentType) {
    return contentType.getParameters().containsKey(ContentType.PARAMETER_IEEE754_COMPATIBLE)
        && Boolean.TRUE.toString().equalsIgnoreCase(
            contentType.getParameter(ContentType.PARAMETER_IEEE754_COMPATIBLE));
  }
}
