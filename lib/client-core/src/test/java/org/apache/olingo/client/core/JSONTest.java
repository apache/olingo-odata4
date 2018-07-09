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
package org.apache.olingo.client.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientAnnotation;
import org.apache.olingo.client.api.domain.ClientCollectionValue;
import org.apache.olingo.client.api.domain.ClientComplexValue;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEnumValue;
import org.apache.olingo.client.api.domain.ClientLink;
import org.apache.olingo.client.api.domain.ClientOperation;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.domain.ClientValue;
import org.apache.olingo.client.core.domain.ClientAnnotationImpl;
import org.apache.olingo.client.core.serialization.JsonDeserializer;
import org.apache.olingo.client.core.serialization.JsonSerializer;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JSONTest extends AbstractTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  protected ContentType getODataPubFormat() {
    return ContentType.JSON;
  }

  protected ContentType getODataFormat() {
    return ContentType.JSON;
  }
  
  protected ContentType getODataMetadataFullFormat() {
    return ContentType.JSON_FULL_METADATA;
  }
  
  protected ContentType getODataMetadataNoneFormat() {
    return ContentType.JSON_NO_METADATA;
  }

  private void cleanup(final ObjectNode node, boolean isServerMode) {
    if (!isServerMode) {
      if (node.has(Constants.JSON_CONTEXT)) {
        node.remove(Constants.JSON_CONTEXT);
      }
      if (node.has(Constants.JSON_ETAG)) {
        node.remove(Constants.JSON_ETAG);
      }
      if (node.has(Constants.JSON_COUNT)) {
        node.remove(Constants.JSON_COUNT);
      }
    }
    if (node.has(Constants.JSON_TYPE)) {
      node.remove(Constants.JSON_TYPE);
    }
    if (node.has(Constants.JSON_EDIT_LINK)) {
      node.remove(Constants.JSON_EDIT_LINK);
    }
    if (node.has(Constants.JSON_READ_LINK)) {
      node.remove(Constants.JSON_READ_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_EDIT_LINK)) {
      node.remove(Constants.JSON_MEDIA_EDIT_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_READ_LINK)) {
      node.remove(Constants.JSON_MEDIA_READ_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      node.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    if (node.has(Constants.JSON_ID)) {
      node.remove(Constants.JSON_ID);
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      final String key = field.getKey();
      if (key.charAt(0) == '#'
          || key.endsWith(Constants.JSON_TYPE)
          || key.endsWith(Constants.JSON_MEDIA_EDIT_LINK)
          || key.endsWith(Constants.JSON_MEDIA_CONTENT_TYPE)
          || key.endsWith(Constants.JSON_ASSOCIATION_LINK)
          || key.endsWith(Constants.JSON_MEDIA_ETAG)
          || key.endsWith(Constants.JSON_BIND_LINK_SUFFIX)) {

        toRemove.add(key);
      } else if (field.getValue().isObject()) {
        cleanup((ObjectNode) field.getValue(), false);
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> arrayItems = field.getValue().elements(); arrayItems.hasNext();) {
          final JsonNode arrayItem = arrayItems.next();
          if (arrayItem.isObject()) {
            cleanup((ObjectNode) arrayItem, false);
          }
        }
      }
    }
    node.remove(toRemove);
  }

  private void cleanupWithFullMetadata(final ObjectNode node, boolean isServerMode) {
    if (!isServerMode) {
      if (node.has(Constants.JSON_CONTEXT)) {
        node.remove(Constants.JSON_CONTEXT);
      }
      if (node.has(Constants.JSON_ETAG)) {
        node.remove(Constants.JSON_ETAG);
      }
      if (node.has(Constants.JSON_COUNT)) {
        node.remove(Constants.JSON_COUNT);
      }
      if (node.has(Constants.JSON_EDIT_LINK)) {
        node.remove(Constants.JSON_EDIT_LINK);
      }
      if (node.has(Constants.JSON_MEDIA_READ_LINK)) {
        node.remove(Constants.JSON_MEDIA_READ_LINK);
      }
    }
    
    if (node.has(Constants.JSON_READ_LINK)) {
      node.remove(Constants.JSON_READ_LINK);
    }
    
    if (node.has(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      node.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    if (node.has(Constants.JSON_MEDIA_ETAG)) {
      node.remove(Constants.JSON_MEDIA_ETAG);
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      final String key = field.getKey();
      if (key.charAt(0) == '#'
          || (!isServerMode && key.endsWith(Constants.JSON_TYPE))
          || (!isServerMode && key.endsWith(Constants.JSON_MEDIA_EDIT_LINK))
          || key.endsWith(Constants.JSON_MEDIA_CONTENT_TYPE)
          || (!isServerMode && key.endsWith(Constants.JSON_ASSOCIATION_LINK))
          || key.endsWith(Constants.JSON_MEDIA_ETAG)) {

        toRemove.add(key);
      } else if (field.getValue().isObject()) {
        cleanup((ObjectNode) field.getValue(), false);
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> arrayItems = field.getValue().elements(); arrayItems.hasNext();) {
          final JsonNode arrayItem = arrayItems.next();
          if (arrayItem.isObject()) {
            cleanup((ObjectNode) arrayItem, false);
          }
        }
      }
    }
    node.remove(toRemove);
  }

  
  private void cleanupWithNoMetadata(final ObjectNode node, boolean isServerMode) {
    if (node.has(Constants.JSON_CONTEXT)) {
      node.remove(Constants.JSON_CONTEXT);
    }
    if (node.has(Constants.JSON_ETAG)) {
      node.remove(Constants.JSON_ETAG);
    }
    if (node.has(Constants.JSON_ID)) {
      node.remove(Constants.JSON_ID);
    }
    if (node.has(Constants.JSON_EDIT_LINK)) {
      node.remove(Constants.JSON_EDIT_LINK);
    }
    if (node.has(Constants.JSON_MEDIA_READ_LINK)) {
      node.remove(Constants.JSON_MEDIA_READ_LINK);
    }
    if (node.has(Constants.JSON_READ_LINK)) {
      node.remove(Constants.JSON_READ_LINK);
    }
    
    if (node.has(Constants.JSON_MEDIA_CONTENT_TYPE)) {
      node.remove(Constants.JSON_MEDIA_CONTENT_TYPE);
    }
    final List<String> toRemove = new ArrayList<String>();
    for (final Iterator<Map.Entry<String, JsonNode>> itor = node.fields(); itor.hasNext();) {
      final Map.Entry<String, JsonNode> field = itor.next();

      final String key = field.getKey();
      if (key.charAt(0) == '#'
          || key.endsWith(Constants.JSON_TYPE)
          || key.endsWith(Constants.JSON_MEDIA_EDIT_LINK)
          || key.endsWith(Constants.JSON_MEDIA_CONTENT_TYPE)
          || key.endsWith(Constants.JSON_ASSOCIATION_LINK)
          || key.endsWith(Constants.JSON_MEDIA_ETAG)
          || key.endsWith(Constants.JSON_BIND_LINK_SUFFIX)) {

        toRemove.add(key);
      } else if (field.getValue().isObject()) {
        cleanup((ObjectNode) field.getValue(), false);
      } else if (field.getValue().isArray()) {
        for (final Iterator<JsonNode> arrayItems = field.getValue().elements(); arrayItems.hasNext();) {
          final JsonNode arrayItem = arrayItems.next();
          if (arrayItem.isObject()) {
            cleanup((ObjectNode) arrayItem, false);
          }
        }
      }
    }
    node.remove(toRemove);
  }
  
  protected void assertSimilar(final String filename, final String actual, 
      boolean isServerMode) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
        replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected, isServerMode);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanup(actualNode, isServerMode);
    assertEquals(expected, actualNode);
  }
  
  protected void assertSimilarWithFullMetadata(final String filename, final String actual, 
      boolean isServerMode) throws Exception {
    String value = IOUtils.toString(getClass().getResourceAsStream(filename));
    final JsonNode expected = isServerMode ? OBJECT_MAPPER.readTree(value.
        replace(Constants.JSON_MEDIA_EDIT_LINK, Constants.JSON_MEDIA_READ_LINK)) :
    OBJECT_MAPPER.readTree(value.
        replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanupWithFullMetadata((ObjectNode) expected, isServerMode);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanupWithFullMetadata(actualNode, isServerMode);
    assertEquals(expected, actualNode);
  }
  
  protected void assertSimilarWithNoMetadata(final String filename, final String actual, 
      boolean isServerMode) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
        replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanupWithNoMetadata((ObjectNode) expected, isServerMode);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanupWithNoMetadata(actualNode, isServerMode);
    assertEquals(expected, actualNode);
  }

  private void assertJSONSimilar(final String filename, final String actual) throws Exception {
    final JsonNode expected = OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream(filename)).
        replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected, false);
    final ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    cleanup(actualNode, false);
    assertEquals(expected, actualNode);
  }
  
  protected void entitySet(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), false);
  }

  protected void entitySetWithFullMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }
  
  protected void entitySetWithNoMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }
  
  protected void entitySetInServerModeWithFullMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));

    assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  protected void entitySetInServerModeWithNoMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));

    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  protected void entitySetInServerMode(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    if (contentType == ContentType.JSON) {
      new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
          getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));
    } else {
      client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntitySet(
          getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    }
    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  @Test
  public void entitySets() throws Exception {
    entitySet("Customers", getODataPubFormat());
	entitySetInServerMode("Customers", getODataPubFormat());
    entitySet("collectionOfEntityReferences", getODataPubFormat());
	entitySetInServerMode("collectionOfEntityReferences", getODataPubFormat());
  }
  
  @Test
  public void entitySetsWithFullMetadata() throws Exception {
    entitySetWithFullMetadata("Customers", getODataMetadataFullFormat());
    entitySetWithFullMetadata("collectionOfEntityReferences", getODataMetadataFullFormat());
  }
  
  @Test
  public void entitySetsWithFullMetadataInServerMode() throws Exception {
    entitySetInServerModeWithFullMetadata("Customers_InServerMode", getODataMetadataFullFormat());
  }
  
  @Test
  public void entitySetsWithNoMetadata() throws Exception {
    entitySetWithNoMetadata("Customers", getODataMetadataFullFormat());
    entitySetInServerModeWithNoMetadata("Customers", getODataMetadataFullFormat());
    entitySetWithNoMetadata("collectionOfEntityReferences", getODataMetadataFullFormat());
    entitySetInServerModeWithNoMetadata("collectionOfEntityReferences", getODataMetadataFullFormat());
  }

  protected void entity(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), false);
  }

  protected void entityWithFullMetadata(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }
  
  protected void entityWithNoMetadata(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }

  
  protected void entityInServerMode(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    if (contentType == ContentType.JSON) {
      new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntity(
          getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));
    } else {
      client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).toEntity(
          getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    }
    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  protected void entityWithFullMetadataInServerMode(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));
    assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  protected void entityWithNoMetadataInServerMode(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).toEntity(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));
    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  @Test
  public void additionalEntities() throws Exception {
    entity("entity.minimal", getODataPubFormat());
    entity("entity.primitive", getODataPubFormat());
    entity("entity.complex", getODataPubFormat());
    entity("entity.collection.primitive", getODataPubFormat());
    entity("entity.collection.complex", getODataPubFormat());
  }

  @Test
  public void entities() throws Exception {
    entity("Products_5", getODataPubFormat());
    entityInServerMode("Products_5", getODataPubFormat());
    entity("VipCustomer", getODataPubFormat());
    entityInServerMode("VipCustomer", getODataPubFormat());
    entity("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", getODataPubFormat());
    entityInServerMode("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", getODataPubFormat());
    entity("entityReference", getODataPubFormat());
    entityInServerMode("entityReference", getODataPubFormat());
    entity("entity.withcomplexnavigation", getODataPubFormat());
    entityInServerMode("entity.withcomplexnavigation", getODataPubFormat());
    entity("annotated", getODataPubFormat());
    entityInServerMode("annotated", getODataPubFormat());
  }
  
  @Test
  public void entitiesWithMetadataFull() throws Exception {
    entityWithFullMetadata("Products_5", getODataMetadataFullFormat());
    entityWithFullMetadata("VipCustomer", getODataMetadataFullFormat());
    entityWithFullMetadata("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", 
        getODataMetadataFullFormat());
    entityWithFullMetadata("entityReference", getODataMetadataFullFormat());
    entityWithFullMetadata("entity.withcomplexnavigation", getODataMetadataFullFormat());
    entityWithFullMetadata("annotated", getODataMetadataFullFormat());
  }
  
  @Test
  public void entitiesWithMetadataFullInServerMode() throws Exception {
    entityWithFullMetadataInServerMode("Products_5_InServerMode", getODataMetadataFullFormat());
  }
  
  @Test
  public void entitiesWithMetadataNone() throws Exception {
    entityWithNoMetadata("Products_5", getODataMetadataNoneFormat());
    entityWithNoMetadataInServerMode("Products_5", getODataMetadataNoneFormat());
    entityWithNoMetadata("VipCustomer", getODataMetadataNoneFormat());
    entityWithNoMetadataInServerMode("VipCustomer", getODataMetadataNoneFormat());
    entityWithNoMetadata("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", 
        getODataMetadataNoneFormat());
    entityWithNoMetadataInServerMode("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7", 
        getODataMetadataNoneFormat());
    entityWithNoMetadata("entityReference", getODataMetadataNoneFormat());
    entityWithNoMetadataInServerMode("entityReference", getODataMetadataNoneFormat());
    entityWithNoMetadata("entity.withcomplexnavigation", getODataMetadataNoneFormat());
    entityWithNoMetadataInServerMode("entity.withcomplexnavigation", getODataMetadataNoneFormat());
  }

  protected void property(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), false);
  }

  protected void propertyWithNoMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }
  
  protected void propertyWithFullMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());

    assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), false);
  }
  
  protected void propertyInServerModeWithNoMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));

    assertSimilarWithNoMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  protected void propertyInServerModeWithFullMetadata(final String filename, 
      final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).
        toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));

    if (filename.equals("Products_5_SkinColor_NullType")) {
      assertEquals(writer.toString(), "{\"@odata.context\":"
          + "\"http://odatae2etest.azurewebsites.net/javatest/DefaultService/$metadata#Products(5)/SkinColor\","
          + "\"@odata.type\":\"String\",\"odata.null\":true}");
    } else {
      assertSimilarWithFullMetadata(filename + "." + getSuffix(contentType), writer.toString(), true);
    }
  }
  
  protected void propertyInServerMode(final String filename, final ContentType contentType) throws Exception {
    final StringWriter writer = new StringWriter();
    if (contentType == ContentType.JSON) {
      new JsonSerializer(true, contentType).write(writer, client.getDeserializer(contentType).
          toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))));
    } else {
      client.getSerializer(contentType).write(writer, client.getDeserializer(contentType).
          toProperty(getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload());
    }

    assertSimilar(filename + "." + getSuffix(contentType), writer.toString(), true);
  }
  
  @Test
  public void properties() throws Exception {
    property("Products_5_SkinColor", getODataFormat());
    propertyInServerMode("Products_5_SkinColor", getODataFormat());
    property("Products_5_CoverColors", getODataFormat());
    propertyInServerMode("Products_5_CoverColors", getODataFormat());
    property("Employees_3_HomeAddress", getODataFormat());
    propertyInServerMode("Employees_3_HomeAddress", getODataFormat());
  }
  
  @Test
  public void propertiesWithNoMetadata() throws Exception {
    propertyWithNoMetadata("Products_5_SkinColor", getODataMetadataNoneFormat());
    propertyInServerModeWithNoMetadata("Products_5_SkinColor", getODataMetadataNoneFormat());
    propertyWithNoMetadata("Products_5_CoverColors", getODataMetadataNoneFormat());
    propertyInServerModeWithNoMetadata("Products_5_CoverColors", getODataMetadataNoneFormat());
    propertyWithNoMetadata("Employees_3_HomeAddress", getODataMetadataNoneFormat());
    propertyInServerModeWithNoMetadata("Employees_3_HomeAddress", getODataMetadataNoneFormat());
  }
  
  @Test
  public void propertiesWithFullMetadata() throws Exception {
    propertyWithFullMetadata("Products_5_SkinColor", getODataMetadataFullFormat());    
    propertyWithFullMetadata("Products_5_CoverColors", getODataMetadataFullFormat());
    propertyInServerModeWithFullMetadata("Products_5_CoverColors", getODataMetadataFullFormat());
    propertyWithFullMetadata("Employees_3_HomeAddress", getODataMetadataFullFormat());
  }
  
  @Test
  public void propertiesWithFullMetadataInServerMode() throws Exception {
    propertyInServerModeWithFullMetadata("Employees_3_HomeAddress_InServerMode", getODataMetadataFullFormat());
    propertyInServerModeWithFullMetadata("Products_5_SkinColor_Null", getODataMetadataFullFormat());
    propertyInServerModeWithFullMetadata("Products_5_SkinColor_NullType", getODataMetadataFullFormat());
    propertyInServerModeWithFullMetadata("Products_5_SkinColor_PrimitiveType", getODataMetadataFullFormat());
  }

  @Test
  public void crossjoin() throws Exception {
    assertNotNull(client.getDeserializer(ContentType.JSON_FULL_METADATA).toEntitySet(
        getClass().getResourceAsStream("crossjoin.json")));
  }

  protected void delta(final String filename, final ContentType contentType) throws Exception {
    final Delta delta = client.getDeserializer(contentType).toDelta(
        getClass().getResourceAsStream(filename + "." + getSuffix(contentType))).getPayload();
    assertNotNull(delta);
    assertNotNull(delta.getDeltaLink());
    assertEquals(5, delta.getCount(), 0);

    assertEquals(1, delta.getDeletedEntities().size());
    assertTrue(delta.getDeletedEntities().get(0).getId().toASCIIString().endsWith("Customers('ANTON')"));

    assertEquals(1, delta.getAddedLinks().size());
    assertTrue(delta.getAddedLinks().get(0).getSource().toASCIIString().endsWith("Customers('BOTTM')"));
    assertEquals("Orders", delta.getAddedLinks().get(0).getRelationship());

    assertEquals(1, delta.getDeletedLinks().size());
    assertTrue(delta.getDeletedLinks().get(0).getSource().toASCIIString().endsWith("Customers('ALFKI')"));
    assertEquals("Orders", delta.getDeletedLinks().get(0).getRelationship());

    assertEquals(2, delta.getEntities().size());
    Property property = delta.getEntities().get(0).getProperty("ContactName");
    assertNotNull(property);
    assertTrue(property.isPrimitive());
    property = delta.getEntities().get(1).getProperty("ShippingAddress");
    assertNotNull(property);
    assertTrue(property.isComplex());
  }

  @Test
  public void deltas() throws Exception {
    delta("delta", getODataPubFormat());
  }

  @Test
  public void issueOLINGO390() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Exchange.Services.OData.Model.Message"));

    final ClientComplexValue toRecipient = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipient.add(client.getObjectFactory().newPrimitiveProperty("Name",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    toRecipient.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));
    final ClientCollectionValue<ClientValue> toRecipients = client.getObjectFactory().
        newCollectionValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipients.add(toRecipient);
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("ToRecipients", toRecipients));

    final ClientComplexValue body =
        client.getObjectFactory().newComplexValue("Microsoft.Exchange.Services.OData.Model.ItemBody");
    body.add(client.getObjectFactory().newPrimitiveProperty("Content",
        client.getObjectFactory().newPrimitiveValueBuilder().
            buildString("this is a simple email body content")));
    body.add(client.getObjectFactory().newEnumProperty("ContentType",
        client.getObjectFactory().newEnumValue("Microsoft.Exchange.Services.OData.Model.BodyType", "text")));
    message.getProperties().add(client.getObjectFactory().newComplexProperty("Body", body));

    String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo390.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected, false);
    ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
    
    actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON_FULL_METADATA));
    expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo390.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  
  @Test
  public void issue1OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.Exchange.Services.OData.Model.Entity"));
    
    final ClientComplexValue complType1 = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.ComplexType1");
    complType1.add(client.getObjectFactory().newPrimitiveProperty("Name1",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    complType1.add(client.getObjectFactory().newPrimitiveProperty("Address1",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));

    final ClientComplexValue complType2 = client.getObjectFactory().
        newComplexValue("Microsoft.Exchange.Services.OData.Model.ComplexType2");
    complType2.add(client.getObjectFactory().newPrimitiveProperty("Name2",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challen_olingo_client")));
    complType2.add(client.getObjectFactory().newPrimitiveProperty("Address2",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("challenh@microsoft.com")));
    final ClientCollectionValue<ClientValue> toRecipients = client.getObjectFactory().
        newCollectionValue("Microsoft.Exchange.Services.OData.Model.Recipient");
    toRecipients.add(complType1);
    toRecipients.add(complType2);
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("ToRecipients", toRecipients));

	String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected, false);
    ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
    
    actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON_FULL_METADATA));
    expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  
  @Test
  public void issue2OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"));
    
    final ClientComplexValue cityComplexType = getCityComplexType();
    
    final ClientComplexValue locationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    locationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln.")));
    locationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));

    final ClientComplexValue eventLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.EventLocation");
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("BuildingInfo",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientComplexValue airportLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.AirportLocation");
    airportLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln123.")));
    airportLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientCollectionValue<ClientValue> collectionAddressInfo = client.getObjectFactory().
        newCollectionValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    collectionAddressInfo.add(locationComplexType);
    collectionAddressInfo.add(eventLocationComplexType);
    collectionAddressInfo.add(airportLocationComplexType);
    
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UserName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("russellwhyte")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Whyte")));
    
    final ClientCollectionValue<ClientValue> emailCollectionValue = client.getObjectFactory().
        newCollectionValue("String");
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@example.com"));
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@contoso.com"));
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails", emailCollectionValue));
    
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("AddressInfo", collectionAddressInfo));
    message.getProperties().add(client.getObjectFactory().newEnumProperty("Gender", 
        client.getObjectFactory().newEnumValue("Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));

    String actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON));
    JsonNode expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073_1.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    cleanup((ObjectNode) expected, false);
    ObjectNode actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
    
    actual = IOUtils.toString(client.getWriter().writeEntity(message, ContentType.JSON_FULL_METADATA));
    expected =
        OBJECT_MAPPER.readTree(IOUtils.toString(getClass().getResourceAsStream("olingo1073_1.json")).
            replace(Constants.JSON_NAVIGATION_LINK, Constants.JSON_BIND_LINK_SUFFIX));
    actualNode = (ObjectNode) OBJECT_MAPPER.readTree(new ByteArrayInputStream(actual.getBytes()));
    assertEquals(expected, actualNode);
  }
  

  /**
   * @return ClientComplexValue
   */
  private ClientComplexValue getCityComplexType() {
    final ClientComplexValue cityComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.City");
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("CountryRegion",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("United States")));
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("Name",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Boise")));
    cityComplexType.add(client.getObjectFactory().newPrimitiveProperty("Region",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ID")));
    return cityComplexType;
  }
  
  @Test
  public void issue3OLINGO1073() throws Exception {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"));
    
    final ClientComplexValue cityComplexType = getCityComplexType();
    
    final ClientComplexValue locationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    locationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln.")));
    locationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));

    final ClientComplexValue eventLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.EventLocation");
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("BuildingInfo",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientComplexValue airportLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.AirportLocation");
    airportLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln123.")));
    airportLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientCollectionValue<ClientValue> collectionAddressInfo = client.getObjectFactory().
        newCollectionValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    collectionAddressInfo.add(locationComplexType);
    collectionAddressInfo.add(eventLocationComplexType);
    collectionAddressInfo.add(airportLocationComplexType);
    
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UserName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("russellwhyte")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Whyte")));
    
    final ClientCollectionValue<ClientValue> emailCollectionValue = client.getObjectFactory().
        newCollectionValue("String");
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@example.com"));
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@contoso.com"));
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails", emailCollectionValue));
    
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("AddressInfo", collectionAddressInfo));
    message.getProperties().add(client.getObjectFactory().newEnumProperty("Gender", 
        client.getObjectFactory().newEnumValue(
            "Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));

    InputStream inputStream = client.getWriter().writeEntity(message, ContentType.JSON_FULL_METADATA);
    ResWrap<Entity> entity = new JsonDeserializer(true).toEntity(inputStream);
    assertNotNull(entity);
    assertEquals(7, entity.getPayload().getProperties().size());
    assertEquals(3, entity.getPayload().getProperty("AddressInfo").asCollection().size());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Location", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(0)).getTypeName());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.EventLocation", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(1)).getTypeName());
    assertEquals("#Microsoft.OData.SampleService.Models.TripPin.AirportLocation", 
        ((ComplexValue)entity.getPayload().getProperty("AddressInfo").asCollection().get(2)).getTypeName());
    assertEquals("Collection(Microsoft.OData.SampleService.Models.TripPin.Location)", 
        entity.getPayload().getProperty("AddressInfo").getType());
  }
  
  @Test
  public void issue4OLINGO1073_WithAnnotations() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream(
        "olingo1073_2" + "." + getSuffix(ContentType.APPLICATION_JSON));
    ClientEntity entity = client.getReader().readEntity(inputStream, ContentType.APPLICATION_JSON);
    assertNotNull(entity);
    assertEquals(7, entity.getProperties().size());
    assertEquals(1, entity.getAnnotations().size());
    assertEquals("com.contoso.PersonalInfo.PhoneNumbers", entity.getAnnotations().get(0).getTerm());
    assertEquals(2, entity.getAnnotations().get(0).getCollectionValue().size());
    
    assertEquals("com.contoso.display.style", entity.getProperty("LastName").
        getAnnotations().get(0).getTerm());
    assertEquals(2, entity.getProperty("LastName").
        getAnnotations().get(0).getComplexValue().asComplex().asJavaMap().size());
    
    assertEquals(3, entity.getProperty("AddressInfo").getCollectionValue().asCollection().size());
    assertEquals("Collection(Microsoft.OData.SampleService.Models.TripPin.Location)", 
        entity.getProperty("AddressInfo").getCollectionValue().asCollection().getTypeName());
    assertEquals(true, entity.getProperty("AddressInfo").getCollectionValue().isCollection());
    ClientCollectionValue<ClientValue> collectionValue = entity.getProperty("AddressInfo").
        getCollectionValue().asCollection();
    int i = 0;
    for (ClientValue _value : collectionValue) {
      if (i == 0) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.Location", _value.getTypeName());
        assertEquals(2, _value.asComplex().asJavaMap().size());
        assertEquals("Microsoft.OData.SampleService.Models.TripPin.City", 
            _value.asComplex().get("City").getComplexValue().getTypeName());
      } else if (i == 1) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.EventLocation", _value.getTypeName());
        assertEquals(3, _value.asComplex().asJavaMap().size());
        assertEquals("com.contoso.display.style", _value.asComplex().get("Address").getAnnotations().get(0).getTerm());
        assertEquals(2, _value.asComplex().get("Address").getAnnotations().get(0).getComplexValue().asJavaMap().size());
      } else if (i == 2) {
        assertEquals("#Microsoft.OData.SampleService.Models.TripPin.AirportLocation", _value.getTypeName());
        assertEquals(3, _value.asComplex().asJavaMap().size());
      }
      i++;
    }
  }
  
  @Test
  public void issueOLINGO1152() throws Exception {
    InputStream inputStream = getClass().getResourceAsStream(
        "olingo1152" + "." + getSuffix(ContentType.APPLICATION_JSON));
    ClientEntity entity = client.getReader().readEntity(inputStream, ContentType.APPLICATION_JSON);
    assertNotNull(entity);
    ClientProperty prop = entity.getProperty("Gender");
    assertNotNull(prop);
    ClientValue value = prop.getValue();
    assertNotNull(value);
    assertTrue(value.asEnum() == null);

  }
  
  @Test
  public void issue2OLINGO1073_WithEntitySet() throws Exception {
    final ClientEntity message = createClientEntity();
    
    InputStream inputStream = client.getWriter().writeEntity(message, ContentType.APPLICATION_JSON);
    ResWrap<Entity> entity = new JsonDeserializer(false).toEntity(inputStream);
    assertNotNull(entity);
    
    StringWriter writer = new StringWriter();
    setNavigationBindingLinkOnEntity(entity);
    
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, entity);
    assertNotNull(writer.toString());
    writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, 
        new ResWrap<URI>(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/"), null, 
            URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')")));
    assertNotNull(writer.toString());
    assertEquals("{\"@odata.context\":\"http://services.odata.org/V4/("
        + "S(fe5rsnxo3fkkkk2bvmh1nl1y))/TripPinServiceRW/\",\"@odata.id\":"
        + "\"http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/TripPinServiceRW/"
        + "People('russellwhyte')\"}", writer.toString());
    
    writer = new StringWriter();
    Link linkPayload = new Link();
    linkPayload.setBindingLink("Photos");
    linkPayload.setMediaETag("xyz");
    linkPayload.setInlineEntity(createEntity());
    linkPayload.setTitle("Photos");
    linkPayload.setHref("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/Photos");
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, 
        new ResWrap<Link>(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/"), linkPayload.getMediaETag(), linkPayload));
    assertNotNull(writer.toString());
    assertEquals("{\"url\":\"http://services.odata.org/V4/"
        + "(S(fe5rsnxo3fkkkk2bvmh1nl1y))/TripPinServiceRW/Photos\"}", writer.toString());
  }

  /**
   * @return
   */
  private ClientEntity createClientEntity() {
    final ClientEntity message = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Person"));
    
    final ClientComplexValue cityComplexType = getCityComplexType();
    
    final ClientComplexValue locationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    locationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln.")));
    locationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));

    final ClientComplexValue eventLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.EventLocation");
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("BuildingInfo",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln12.")));
    eventLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientComplexValue airportLocationComplexType = client.getObjectFactory().
        newComplexValue("Microsoft.OData.SampleService.Models.TripPin.AirportLocation");
    airportLocationComplexType.add(client.getObjectFactory().newPrimitiveProperty("Address",
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("187 Suffolk Ln123.")));
    airportLocationComplexType.add(client.getObjectFactory().newComplexProperty("City",cityComplexType));
    
    final ClientCollectionValue<ClientValue> collectionAddressInfo = client.getObjectFactory().
        newCollectionValue("Microsoft.OData.SampleService.Models.TripPin.Location");
    collectionAddressInfo.add(locationComplexType);
    collectionAddressInfo.add(eventLocationComplexType);
    collectionAddressInfo.add(airportLocationComplexType);
    
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("UserName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("russellwhyte")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("FirstName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("LastName", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("Whyte")));
    final ClientLink messageLink1 = client.getObjectFactory().newEntityNavigationLink("Photo", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Photo"));
    final ClientAnnotation messageLink1Annotation = createAnnotation();
    messageLink1.getAnnotations().add(messageLink1Annotation);
    
    final ClientLink messageLink2 = client.getObjectFactory().newEntitySetNavigationLink("Friends", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Friends"));
    final ClientAnnotation messageLink2Annotation = createAnnotation();
    messageLink2.getAnnotations().add(messageLink2Annotation);
    
    final ClientLink messageLink3 = client.getObjectFactory().newEntitySetNavigationLink("Trips", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Trips"));
    final ClientAnnotation messageLink3Annotation = createAnnotation();
    messageLink3.getAnnotations().add(messageLink3Annotation);
        
    message.getNavigationLinks().add(messageLink1);
    message.getNavigationLinks().add(messageLink2);
    message.getNavigationLinks().add(messageLink3);
    
    final ClientAnnotation messageAnnotation = createAnnotation();
    message.getAnnotations().add(messageAnnotation);
    
    final ClientCollectionValue<ClientValue> emailCollectionValue = client.getObjectFactory().
        newCollectionValue("String");
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@example.com"));
    emailCollectionValue.add(client.getObjectFactory().newPrimitiveValueBuilder().buildString("Russell@contoso.com"));
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("Emails", emailCollectionValue));
    
    message.getProperties().add(client.getObjectFactory().newCollectionProperty("AddressInfo", collectionAddressInfo));
    message.getProperties().add(client.getObjectFactory().newEnumProperty("Gender", 
        client.getObjectFactory().newEnumValue(
            "Microsoft.OData.SampleService.Models.TripPin.PersonGender", "Male")));
    message.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Concurrency", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf("636293755917400747"))));
    message.setId(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    message.setETag("W/\"08D491CCBE417AAB\"");
    message.setEditLink(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')"));
    
    final ClientEntity innerEntity = client.getObjectFactory().
        newEntity(new FullQualifiedName("Microsoft.OData.SampleService.Models.TripPin.Photo"));
    innerEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Id", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildInt64(Long.valueOf(123))));
    innerEntity.getProperties().add(client.getObjectFactory().newPrimitiveProperty("Name", 
        client.getObjectFactory().newPrimitiveValueBuilder().buildString("ABC")));
    innerEntity.getAnnotations().add(createAnnotation());
    final ClientLink link = client.getObjectFactory().newDeepInsertEntity("Photos", innerEntity);
    final ClientAnnotation linkAnnotation = createAnnotation();
    link.getAnnotations().add(linkAnnotation);
    message.getNavigationLinks().add(link);
    
    final ClientLink assoLink = client.getObjectFactory().newAssociationLink("Photos", 
        URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/People('russellwhyte')/Photo"));
    final ClientAnnotation assoLinkAnnotation = createAnnotation();
    assoLink.getAnnotations().add(assoLinkAnnotation);

    message.getAssociationLinks().add(assoLink);
    final ClientOperation operation = new ClientOperation();
    operation.setTarget(URI.create("http://services.odata.org/V4/(S(fe5rsnxo3fkkkk2bvmh1nl1y))/"
        + "TripPinServiceRW/Photos"));
    operation.setTitle("Photos");
    message.getOperations().add(operation);
    return message;
  }

  /**
   * @param entity
   */
  private void setNavigationBindingLinkOnEntity(ResWrap<Entity> entity) {
    Link entityLink = new Link();
    Entity en = createEntity();
    
    entityLink.setBindingLink("Photos");
    entityLink.setInlineEntity(en);
    entityLink.setType("Microsoft.OData.SampleService.Models.TripPin.Photos");
    
    Link entityColLink = new Link();
    EntityCollection enCol = new EntityCollection();
    enCol.getEntities().add(en);
    
    entityColLink.setBindingLink("Friends");
    entityColLink.setInlineEntitySet(enCol);
    entityColLink.setType("Microsoft.OData.SampleService.Models.TripPin.Friends");
    
    Link link = new Link();
    link.setBindingLink("Trips");
    link.setType("Microsoft.OData.SampleService.Models.TripPin.Trips");
    
    entity.getPayload().getNavigationBindings().add(entityLink);
    entity.getPayload().getNavigationBindings().add(entityColLink);
    entity.getPayload().getNavigationBindings().add(link);
  }

  /**
   * @return
   */
  private Entity createEntity() {
    Entity en = new Entity();
    Property p1 = new Property();
    p1.setName("Id");
    p1.setType("Int64");
    p1.setValue(ValueType.PRIMITIVE, Long.valueOf(123));
    en.addProperty(p1);
    
    Property p2 = new Property();
    p2.setName("Name");
    p2.setType("String");
    p2.setValue(ValueType.PRIMITIVE, "ABC");
    en.addProperty(p2);
    return en;
  }

  /**
   * @return
   */
  private ClientAnnotation createAnnotation() {
    final ClientAnnotation messageAnnotation = 
        new ClientAnnotationImpl("Org.OData.Core.V1.Permissions", new ClientPrimitiveValue() {
      
      @Override
      public boolean isPrimitive() {
        return false;
      }
      
      @Override
      public boolean isEnum() {
        return true;
      }
      
      @Override
      public boolean isComplex() {
        return false;
      }
      
      @Override
      public boolean isCollection() {
        return false;
      }
      
      @Override
      public String getTypeName() {
        return "String";
      }
      
      @Override
      public ClientPrimitiveValue asPrimitive() {
        return null;
      }
      
      @Override
      public ClientEnumValue asEnum() {
        return client.getObjectFactory().newEnumValue("Org.OData.Core.V1.Permissions", "Read");
      }
      
      @Override
      public ClientComplexValue asComplex() {
        return null;
      }
      
      @Override
      public <T extends ClientValue> ClientCollectionValue<T> asCollection() {
        return null;
      }
      
      @Override
      public Object toValue() {
        return client.getObjectFactory().newEnumValue("Org.OData.Core.V1.Permissions", "Read");
      }
      
      @Override
      public <T> T toCastValue(Class<T> reference) throws EdmPrimitiveTypeException {
        return null;
      }
      
      @Override
      public EdmPrimitiveTypeKind getTypeKind() {
        return null;
      }
      
      @Override
      public EdmPrimitiveType getType() {
        return null;
      }
    });
    return messageAnnotation;
  }
  
  protected void property1(final String filename) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, 
        client.getDeserializer(ContentType.APPLICATION_JSON).
        toProperty(getClass().getResourceAsStream(filename + ".json")));

    assertJSONSimilar(filename + ".json", writer.toString());
  }

  @Test
  public void properties1() throws Exception {
    property1("Products_5_SkinColor");
    property1("Products_5_CoverColors");
    property1("Employees_3_HomeAddress");
    property1("Employees_3_HomeAddress");
  }
  
  protected void entity1(final String filename) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, client.getDeserializer(
        ContentType.APPLICATION_JSON).toEntity(
        getClass().getResourceAsStream(filename + ".json")));
    assertJSONSimilar(filename + ".json", writer.toString());
  }

  @Test
  public void additionalEntities1() throws Exception {
    entity1("entity.minimal");
    entity1("entity.primitive");
    entity1("entity.complex");
    entity1("entity.collection.primitive");
    entity1("entity.collection.complex");
  }

  @Test
  public void entities1() throws Exception {
    entity1("Products_5");
    entity1("VipCustomer");
    entity1("Advertisements_f89dee73-af9f-4cd4-b330-db93c25ff3c7");
    entity1("entityReference");
    entity1("entity.withcomplexnavigation");
    entity1("annotated");
  }
  
  protected void entitySet1(final String filename) throws Exception {
    final StringWriter writer = new StringWriter();
    client.getSerializer(ContentType.APPLICATION_JSON).write(writer, 
        client.getDeserializer(ContentType.APPLICATION_JSON).toEntitySet(
        getClass().getResourceAsStream(filename + ".json")));

    assertJSONSimilar(filename + ".json", writer.toString());
  }

  @Test
  public void entitySets1() throws Exception {
    entitySet1("Customers");
    entitySet1("collectionOfEntityReferences");
  }
}
