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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.domain.ClientLinkType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.junit.Test;

public class ODataDeserializerDeepInsertTest extends AbstractODataDeserializerTest {

  @Test
  public void esAllPrimExpandedToOne() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimOne.json");
    Entity entity = OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType).getEntity();
    Link navigationLink = entity.getNavigationLink("NavPropertyETTwoPrimOne");
    assertNotNull(navigationLink);

    assertEquals("NavPropertyETTwoPrimOne", navigationLink.getTitle());
    assertEquals(ClientLinkType.ENTITY_NAVIGATION.toString(), navigationLink.getType());
    assertNotNull(navigationLink.getInlineEntity());
    assertNull(navigationLink.getInlineEntitySet());
  }

  @Test
  public void esAllPrimExpandedToOneWithODataAnnotations() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimOneWithODataAnnotations.json");
    OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
  }

  @Test
  public void esAllPrimExpandedToMany() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimMany.json");
    Entity entity = OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType).getEntity();

    Link navigationLink = entity.getNavigationLink("NavPropertyETTwoPrimMany");
    assertNotNull(navigationLink);

    assertEquals("NavPropertyETTwoPrimMany", navigationLink.getTitle());
    assertEquals(ClientLinkType.ENTITY_SET_NAVIGATION.toString(), navigationLink.getType());
    assertNull(navigationLink.getInlineEntity());
    assertNotNull(navigationLink.getInlineEntitySet());
    assertEquals(1, navigationLink.getInlineEntitySet().getEntities().size());
  }

  @Test
  public void esAllPrimExpandedToManyWithODataAnnotations() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimManyWithODataAnnotations.json");
    OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
  }

  @Test(expected = DeserializerException.class)
  public void esAllPrimExpandedToOneWithCustomAnnotations() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimOneWithCustomAnnotations.json");
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void esAllPrimExpandedToManyWithCustomAnnotations() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("EntityESAllPrimExpandedNavPropertyETTwoPrimManyWithCustomAnnotations.json");
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void expandedToOneInvalidNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"NavPropertyETTwoPrimOne\":null"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }
  
  @Test
  public void expandedToOneValidNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"NavPropertyETAllPrimOne\":null"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETTwoPrim"));
    final Entity entity = OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType)
                                                                                  .getEntity();
  
    assertEquals(1, entity.getNavigationLinks().size());
    final Link link = entity.getNavigationLinks().get(0);
    
    assertEquals("NavPropertyETAllPrimOne", link.getTitle());
    assertNull(link.getInlineEntity());
    assertNull(link.getInlineEntitySet());
  }
  
  @Test(expected = DeserializerException.class)
  public void expandedToOneInvalidStringValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"NavPropertyETTwoPrimOne\":\"First Resource - positive values\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, e.getMessageKey());
      throw e;
    }
  }
  
  @Test(expected = DeserializerException.class)
  public void expandedToManyInvalidNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"NavPropertyETTwoPrimMany\":null"
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_NULL_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void expandedToManyInvalidStringValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"NavPropertyETTwoPrimMany\":\"First Resource - positive values\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entity(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_NAVIGATION_PROPERTY, e.getMessageKey());
      throw e;
    }
  }
}
