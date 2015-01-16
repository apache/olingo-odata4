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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.domain.ODataLinkType;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmTypeDefinition;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.LinkImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.EdmTypeInfo;
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

  @Override
  public EntitySet entityCollection(InputStream stream, EdmEntityType edmEntityType) throws DeserializerException {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);
      JsonParser parser = new JsonFactory(objectMapper).createParser(stream);
      final ObjectNode tree = parser.getCodec().readTree(parser);

      return consumeEntitySetNode(edmEntityType, tree);
    } catch (JsonParseException e) {
      throw new DeserializerException("An JsonParseException occourred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occourred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }
  }

  private EntitySet consumeEntitySetNode(EdmEntityType edmEntityType, final ObjectNode tree)
      throws DeserializerException {
    EntitySetImpl entitySet = new EntitySetImpl();
    // Consume entitySet annotations
    consumeODataEntitySetAnnotations(tree, entitySet);

    // Consume entities
    JsonNode jsonNode = tree.get(Constants.VALUE);
    if (jsonNode != null) {
      if (jsonNode.isArray() == false) {
        throw new DeserializerException("The content of the value tag must be an Array but is not. ",
            DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
      }

      consumeEntitySetArray(entitySet, edmEntityType, jsonNode);
      tree.remove(Constants.VALUE);
    } else {
      throw new DeserializerException("Could not find value array.",
          DeserializerException.MessageKeys.VALUE_ARRAY_NOT_PRESENT);
    }

    if (tree.size() != 0) {
      throw new DeserializerException("Tree should be empty but still has content left.",
          DeserializerException.MessageKeys.TREE_NOT_EMPTY);
    }
    return entitySet;
  }

  private void consumeEntitySetArray(EntitySetImpl entitySet, EdmEntityType edmEntityType, JsonNode jsonNode)
      throws DeserializerException {
    Iterator<JsonNode> arrayIterator = jsonNode.iterator();
    while (arrayIterator.hasNext()) {
      JsonNode arrayElement = (JsonNode) arrayIterator.next();
      if (arrayElement.isContainerNode() && arrayElement.isArray()) {
        throw new DeserializerException("Nested Arrays and primitive values are not allowed for an entity value.",
            DeserializerException.MessageKeys.INVALID_ENTITY);
      }

      entitySet.getEntities().add(consumeEntityNode(edmEntityType, (ObjectNode) arrayElement));
    }
  }

  private void consumeODataEntitySetAnnotations(final ObjectNode tree, EntitySetImpl entitySet) {
    URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      entitySet.setBaseURI(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA));
    }

    if (tree.hasNonNull(Constants.JSON_COUNT)) {
      entitySet.setCount(tree.get(Constants.JSON_COUNT).asInt());
      tree.remove(Constants.JSON_COUNT);
    }
    if (tree.hasNonNull(Constants.JSON_NEXT_LINK)) {
      entitySet.setNext(URI.create(tree.get(Constants.JSON_NEXT_LINK).textValue()));
      tree.remove(Constants.JSON_NEXT_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_DELTA_LINK)) {
      entitySet.setDeltaLink(URI.create(tree.get(Constants.JSON_DELTA_LINK).textValue()));
      tree.remove(Constants.JSON_DELTA_LINK);
    }
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
      throw new DeserializerException("An JsonParseException occourred", e,
          DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    } catch (JsonMappingException e) {
      throw new DeserializerException("Duplicate property detected", e,
          DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
    } catch (IOException e) {
      throw new DeserializerException("An IOException occourred", e, DeserializerException.MessageKeys.IO_EXCEPTION);
    }

  }

  private Entity consumeEntityNode(EdmEntityType edmEntityType, final ObjectNode tree) throws DeserializerException {
    EntityImpl entity = new EntityImpl();
    entity.setType(edmEntityType.getFullQualifiedName().getFullQualifiedNameAsString());

    // Check and consume all Properties
    List<String> propertyNames = edmEntityType.getPropertyNames();
    for (String propertyName : propertyNames) {
      JsonNode jsonNode = tree.get(propertyName);
      if (jsonNode != null) {
        EdmProperty edmProperty = (EdmProperty) edmEntityType.getProperty(propertyName);
        if (jsonNode.isNull() == true && edmProperty.isNullable() != null
            && edmProperty.isNullable().booleanValue() == false) {
          throw new DeserializerException("Property: " + propertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, propertyName);
        }
        Property property = consumePropertyNode(edmProperty, jsonNode);
        entity.addProperty(property);
        tree.remove(propertyName);
      }
    }

    // Check and consume all expanded Navigation Properties
    List<String> navigationPropertyNames = edmEntityType.getNavigationPropertyNames();
    for (String navigationPropertyName : navigationPropertyNames) {
      // read expanded navigation property
      JsonNode jsonNode = tree.get(navigationPropertyName);
      if (jsonNode != null) {
        EdmNavigationProperty edmNavigationProperty = edmEntityType.getNavigationProperty(navigationPropertyName);
        if (jsonNode.isNull() == true && edmNavigationProperty.isNullable() != null
            && edmNavigationProperty.isNullable().booleanValue() == false) {
          throw new DeserializerException("Property: " + navigationPropertyName + " must not be null.",
              DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, navigationPropertyName);
        }

        LinkImpl link = new LinkImpl();
        link.setTitle(navigationPropertyName);
        if (jsonNode.isArray() && edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_SET_NAVIGATION.toString());
          EntitySetImpl inlineEntitySet = new EntitySetImpl();
          consumeEntitySetArray(inlineEntitySet, edmNavigationProperty.getType(), jsonNode);
          link.setInlineEntitySet(inlineEntitySet);
        } else if (!jsonNode.isArray() && !jsonNode.isValueNode() && !edmNavigationProperty.isCollection()) {
          link.setType(ODataLinkType.ENTITY_NAVIGATION.toString());
          if (!jsonNode.isNull()) {
            Entity inlineEntity = consumeEntityNode(edmNavigationProperty.getType(), (ObjectNode) jsonNode);
            link.setInlineEntity(inlineEntity);
          }
        } else {
          throw new DeserializerException("Invalid value: " + jsonNode.getNodeType()
              + " for expanded navigation porperty: " + navigationPropertyName,
              DeserializerException.MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, navigationPropertyName);
        }
        entity.getNavigationLinks().add(link);
        tree.remove(navigationPropertyName);
      }
    }

    // Check and consume all Annotations
    consumeODataEntityAnnotations(tree, entity, edmEntityType);

    final List<String> toRemove = new ArrayList<String>();
    Iterator<Entry<String, JsonNode>> fieldsIterator = tree.fields();
    // TODO: Add custom annotation support
    while (fieldsIterator.hasNext()) {
      Map.Entry<String, JsonNode> field = (Map.Entry<String, JsonNode>) fieldsIterator.next();

      if (field.getKey().endsWith(Constants.JSON_NAVIGATION_LINK)
          || field.getKey().endsWith(Constants.JSON_ASSOCIATION_LINK) || field.getKey().endsWith(Constants.JSON_TYPE)) {
        //navigation links, association links and type information have to be ignored in requests.
        toRemove.add(field.getKey());
      }
    }

    // remove here to avoid iterator issues.
    tree.remove(toRemove);

    if (tree.size() != 0) {
      throw new DeserializerException("Tree should be empty but still has content left.",
          DeserializerException.MessageKeys.TREE_NOT_EMPTY);
    }

    return entity;
  }

  private void consumeODataEntityAnnotations(ObjectNode tree, EntityImpl entity, EdmEntityType edmEntityType) {
    final URI contextURL;
    if (tree.hasNonNull(Constants.JSON_CONTEXT)) {
      contextURL = URI.create(tree.get(Constants.JSON_CONTEXT).textValue());
      tree.remove(Constants.JSON_CONTEXT);
    } else if (tree.hasNonNull(Constants.JSON_METADATA)) {
      contextURL = URI.create(tree.get(Constants.JSON_METADATA).textValue());
      tree.remove(Constants.JSON_METADATA);
    } else {
      contextURL = null;
    }
    if (contextURL != null) {
      entity.setBaseURI(StringUtils.substringBefore(contextURL.toASCIIString(), Constants.METADATA));
    }

    if (tree.hasNonNull(Constants.JSON_ETAG)) {
      entity.setETag(tree.get(Constants.JSON_ETAG).textValue());
      tree.remove(Constants.JSON_ETAG);
    }

    if (tree.hasNonNull(Constants.JSON_TYPE)) {
      entity.setType(new EdmTypeInfo.Builder().setTypeExpression(tree.get(Constants.JSON_TYPE).textValue()).build()
          .internal());
      tree.remove(Constants.JSON_TYPE);
    }

    if (tree.hasNonNull(Constants.JSON_ID)) {
      entity.setId(URI.create(tree.get(Constants.JSON_ID).textValue()));
      tree.remove(Constants.JSON_ID);
    }

    if (tree.hasNonNull(Constants.JSON_READ_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(Constants.SELF_LINK_REL);
      link.setHref(tree.get(Constants.JSON_READ_LINK).textValue());
      entity.setSelfLink(link);

      tree.remove(Constants.JSON_READ_LINK);
    }

    if (tree.hasNonNull(Constants.JSON_EDIT_LINK)) {
      final LinkImpl link = new LinkImpl();
      link.setRel(Constants.EDIT_LINK_REL);
      link.setHref(tree.get(Constants.JSON_EDIT_LINK).textValue());
      entity.setEditLink(link);

      tree.remove(Constants.JSON_EDIT_LINK);
    }

    // TODO: Should we check if this is a media resource?
    if (tree.hasNonNull(Constants.JSON_MEDIA_READ_LINK)) {
      entity.setMediaContentSource(URI.create(tree.get(Constants.JSON_MEDIA_READ_LINK).textValue()));
      tree.remove(Constants.JSON_MEDIA_READ_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_MEDIA_EDIT_LINK)) {
      entity.setMediaContentSource(URI.create(tree.get(Constants.JSON_MEDIA_EDIT_LINK).textValue()));
      tree.remove(Constants.JSON_MEDIA_EDIT_LINK);
    }
    if (tree.hasNonNull(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      entity.setMediaContentType(tree.get(Constants.JSON_MEDIA_CONTENT_TYPE).textValue());
      tree.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    if (tree.hasNonNull(Constants.JSON_MEDIA_ETAG)) {
      entity.setMediaETag(tree.get(Constants.JSON_MEDIA_ETAG).textValue());
      tree.remove(Constants.JSON_MEDIA_ETAG);
    }

  }

  private Property consumePropertyNode(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    Property property = new PropertyImpl();
    property.setName(edmProperty.getName());
    property.setType(edmProperty.getType().getFullQualifiedName().getFullQualifiedNameAsString());
    if (edmProperty.isCollection()) {
      if (!jsonNode.isArray()) {
        throw new DeserializerException("Value for property: " + edmProperty.getName()
            + " must be an arrat but is not.", DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY,
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
          JsonNode arrayElement = iterator.next();
          // read and add all complex properties
          Object value = readComplexValue(edmProperty, arrayElement);
          valueArray.add(value);
          // If navigationProperties are present we have to consume them and create a LinkedComplexValue Object
          // TODO: Complex Type Navigation Deserialization
          // read and add all annotations
          // TODO: read Complex Type Annotations
          // Afterwards the node must be empty
          if (arrayElement.size() != 0) {
            throw new DeserializerException("Tree should be empty but still has content left.",
                DeserializerException.MessageKeys.TREE_NOT_EMPTY);
          }
        }
        property.setValue(ValueType.COLLECTION_COMPLEX, valueArray);
        break;
      default:
        throw new DeserializerException("Invalid Type Kind for a property found: " + edmProperty.getType().getKind(),
            DeserializerException.MessageKeys.INVALID_TYPE_FOR_PROPERTY, edmProperty.getName());
      }

    } else {
      switch (edmProperty.getType().getKind()) {
      case PRIMITIVE:
        Object value = readPrimitiveValue(edmProperty, jsonNode);
        property.setValue(ValueType.PRIMITIVE, value);
        break;
      case DEFINITION:
        value = readTypeDefinitionValue(edmProperty, jsonNode);
        property.setValue(ValueType.PRIMITIVE, value);
      case ENUM:
        value = readEnumValue(edmProperty, jsonNode);
        property.setValue(ValueType.PRIMITIVE, value);
        break;
      case COMPLEX:
        // read and add all complex properties
        value = readComplexValue(edmProperty, jsonNode);
        property.setValue(ValueType.COMPLEX, value);

        // read and add all navigation properties
        // TODO: Complex Type Navigation Deserialization
        // read and add all annotations
        // TODO: read Complex Type Annotations
        // Afterwards the node must be empty
        if (jsonNode.size() != 0) {
          throw new DeserializerException("Tree should be empty but still has content left.",
              DeserializerException.MessageKeys.TREE_NOT_EMPTY);
        }
        break;
      default:
        throw new DeserializerException("Invalid Type Kind for a property found: " + edmProperty.getType().getKind(),
            DeserializerException.MessageKeys.INVALID_TYPE_FOR_PROPERTY, edmProperty.getName());
      }
    }
    return property;
  }

  private Object readComplexValue(EdmProperty edmComplexProperty, JsonNode jsonNode) throws DeserializerException {
    if (jsonNode.isArray() || !jsonNode.isContainerNode()) {
      throw new DeserializerException(
          "Inavlid value for property: " + edmComplexProperty.getName() + " must not be an array or primitive value.",
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmComplexProperty.getName());
    }
    // Even if there are no properties defined we have to give back an empty list
    List<Property> propertyList = new ArrayList<Property>();
    EdmComplexType edmType = (EdmComplexType) edmComplexProperty.getType();
    // Check and consume all Properties
    for (String propertyName : edmType.getPropertyNames()) {
      EdmProperty edmProperty = (EdmProperty) edmType.getProperty(propertyName);
      JsonNode subNode = jsonNode.get(propertyName);
      if (subNode != null) {
        if (subNode.isNull() && edmProperty.isNullable() != null && edmProperty.isNullable().booleanValue() == false) {
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

  private Object readTypeDefinitionValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException(
          "Inavlid value for property: " + edmProperty.getName() + " must not be an object or array.",
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
    try {
      EdmTypeDefinition edmTypeDefinition = (EdmTypeDefinition) edmProperty.getType();
      Object value =
          edmTypeDefinition.valueOfString(jsonNode.asText(), edmProperty.isNullable(),
              edmTypeDefinition.getMaxLength(),
              edmTypeDefinition.getPrecision(), edmTypeDefinition.getScale(), edmTypeDefinition.isUnicode(),
              edmTypeDefinition.getDefaultType());
      return value;
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Inavlid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private Object readEnumValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException(
          "Inavlid value for property: " + edmProperty.getName() + " must not be an object or array.",
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
    try {
      EdmEnumType edmEnumType = (EdmEnumType) edmProperty.getType();
      Object value =
          edmEnumType
              .valueOfString(jsonNode.asText(), edmProperty.isNullable(), edmProperty.getMaxLength(), edmProperty
                  .getPrecision(), edmProperty.getScale(), edmProperty.isUnicode(), edmEnumType.getDefaultType());
      return value;
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Inavlid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

  private Object readPrimitiveValue(EdmProperty edmProperty, JsonNode jsonNode) throws DeserializerException {
    if (!jsonNode.isValueNode()) {
      throw new DeserializerException(
          "Inavlid value for property: " + edmProperty.getName() + " must not be an object or array.",
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
    try {
      EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmProperty.getType();
      Object value =
          edmPrimitiveType.valueOfString(jsonNode.asText(), edmProperty.isNullable(),
              edmProperty.getMaxLength(), edmProperty.getPrecision(), edmProperty.getScale(),
              edmProperty.isUnicode(), edmPrimitiveType.getDefaultType());
      return value;
    } catch (EdmPrimitiveTypeException e) {
      throw new DeserializerException(
          "Inavlid value: " + jsonNode.asText() + " for property: " + edmProperty.getName(), e,
          DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, edmProperty.getName());
    }
  }

//  @Override
//  public Entity entity(InputStream stream, EdmEntityType edmEntityType)
//      throws DeserializerException {
//    try {
//      return new JsonDeserializer(ODataServiceVersion.V40, true).toEntity(stream).getPayload();
//    } catch (Exception e) {
//      throw new DeserializerException("", e, DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
//    }
//  }
}
