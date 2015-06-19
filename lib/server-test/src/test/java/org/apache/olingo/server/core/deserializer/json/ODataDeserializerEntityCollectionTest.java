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
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.junit.Test;

public class ODataDeserializerEntityCollectionTest extends AbstractODataDeserializerTest {

  @Test
  public void esAllPrim() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("ESAllPrim.json");
    EntityCollection entitySet =
        OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType)
            .getEntityCollection();

    assertNotNull(entitySet);
    assertEquals(3, entitySet.getEntities().size());

    // Check first entity
    Entity entity = entitySet.getEntities().get(0);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());
    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertEquals(new Boolean(true), entity.getProperty("PropertyBoolean").getValue());
    assertEquals(new Short((short) 255), entity.getProperty("PropertyByte").getValue());
    assertEquals(new Byte((byte) 127), entity.getProperty("PropertySByte").getValue());
    assertEquals(new Integer(2147483647), entity.getProperty("PropertyInt32").getValue());
    assertEquals(new Long(9223372036854775807l), entity.getProperty("PropertyInt64").getValue());
    assertEquals(new Float(1.79E20), entity.getProperty("PropertySingle").getValue());
    assertEquals(new Double(-1.79E19), entity.getProperty("PropertyDouble").getValue());
    assertEquals(new BigDecimal(34), entity.getProperty("PropertyDecimal").getValue());
    assertNotNull(entity.getProperty("PropertyBinary").getValue());
    assertNotNull(entity.getProperty("PropertyDate").getValue());
    assertNotNull(entity.getProperty("PropertyDateTimeOffset").getValue());
    assertNotNull(entity.getProperty("PropertyDuration").getValue());
    assertNotNull(entity.getProperty("PropertyGuid").getValue());
    assertNotNull(entity.getProperty("PropertyTimeOfDay").getValue());
  }

  @Test
  public void eSCompCollComp() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCompCollComp"));
    InputStream stream = getFileAsStream("ESCompCollComp.json");
    EntityCollection entitySet =
        OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType)
            .getEntityCollection();

    assertNotNull(entitySet);
    assertEquals(2, entitySet.getEntities().size());

    // Since entity deserialization is called we do not check all entities here excplicitly
  }

  @Test
  public void esAllPrimODataAnnotationsAreIgnored() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("ESAllPrimWithODataAnnotations.json");
    OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
  }

  @Test
  public void emptyETAllPrim() throws Exception {
    String entityCollectionString = "{\"value\" : []}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    EntityCollection entityCollection =
        OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType)
            .getEntityCollection();
    assertNotNull(entityCollection.getEntities());
    assertTrue(entityCollection.getEntities().isEmpty());
  }

  @Test(expected = DeserializerException.class)
  public void esAllPrimCustomAnnotationsLeadToNotImplemented() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("ESAllPrimWithCustomAnnotations.json");
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void esAllPrimDoubleKeysLeadToException() throws Exception {
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    InputStream stream = getFileAsStream("ESAllPrimWithDoubleKey.json");
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_JSON_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void wrongValueTagJsonValueNull() throws Exception {
    String entityCollectionString = "{\"value\" : null}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void wrongValueTagJsonValueNumber() throws Exception {
    String entityCollectionString = "{\"value\" : 1234}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void wrongValueTagJsonValueObject() throws Exception {
    String entityCollectionString = "{\"value\" : {}}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void valueTagMissing() throws Exception {
    String entityCollectionString = "{}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.VALUE_ARRAY_NOT_PRESENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void wrongValueInValueArrayNumber() throws Exception {
    String entityCollectionString = "{\"value\" : [1234,123]}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_ENTITY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void wrongValueInValueArrayNestedArray() throws Exception {
    String entityCollectionString = "{\"value\" : [[],[]]}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_ENTITY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void invalidJsonSyntax() throws Exception {
    String entityCollectionString = "{\"value\" : }";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unknownContentInCollection() throws Exception {
    String entityCollectionString = "{\"value\" : [],"
        + "\"unknown\":null"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKNOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void customAnnotationNotSupportedYet() throws Exception {
    String entityCollectionString = "{\"value\" : [],"
        + "\"@custom.annotation\":null"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityCollectionString.getBytes());
    EdmEntityType edmEntityType = edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim"));
    try {
      OData.newInstance().createDeserializer(ODataFormat.JSON).entityCollection(stream, edmEntityType);
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }
}
