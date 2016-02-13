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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Test;

public class ODataDeserializerEntityCollectionTest extends AbstractODataDeserializerTest {

  @Test
  public void esAllPrim() throws Exception {
    final EntityCollection entitySet = deserialize(getFileAsStream("ESAllPrim.json"), "ETAllPrim");
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
    final EntityCollection entitySet = deserialize(getFileAsStream("ESCompCollComp.json"), "ETCompCollComp");
    assertNotNull(entitySet);
    assertEquals(2, entitySet.getEntities().size());

    // Since entity deserialization is called we do not check all entities here excplicitly
  }

  @Test
  public void esAllPrimODataAnnotationsAreIgnored() throws Exception {
    deserialize(getFileAsStream("ESAllPrimWithODataAnnotations.json"), "ETAllPrim");
  }

  @Test
  public void emptyETAllPrim() throws Exception {
    String entityCollectionString = "{\"value\" : []}";
    final EntityCollection entityCollection = deserialize(entityCollectionString, "ETAllPrim");
    assertNotNull(entityCollection.getEntities());
    assertTrue(entityCollection.getEntities().isEmpty());
  }

  @Test
  public void esAllPrimCustomAnnotationsLeadToNotImplemented() throws Exception {
    expectException(getFileAsStream("ESAllPrimWithCustomAnnotations.json"), "ETAllPrim",
        DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Test
  public void esAllPrimDoubleKeysLeadToException() throws Exception {
    expectException(getFileAsStream("ESAllPrimWithDoubleKey.json"), "ETAllPrim",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
  public void wrongValueTagJsonValueNull() throws Exception {
    expectException("{\"value\" : null}", "ETAllPrim",
        DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
  }

  @Test
  public void wrongValueTagJsonValueNumber() throws Exception {
    expectException("{\"value\" : 1234}", "ETAllPrim",
        DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
  }

  @Test
  public void wrongValueTagJsonValueObject() throws Exception {
    expectException("{\"value\" : {}}", "ETAllPrim",
        DeserializerException.MessageKeys.VALUE_TAG_MUST_BE_AN_ARRAY);
  }

  @Test
  public void valueTagMissing() throws Exception {
    expectException("{}", "ETAllPrim",
        DeserializerException.MessageKeys.VALUE_ARRAY_NOT_PRESENT);
  }

  @Test
  public void wrongValueInValueArrayNumber() throws Exception {
    expectException("{\"value\" : [1234,123]}", "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_ENTITY);
  }

  @Test
  public void wrongValueInValueArrayNestedArray() throws Exception {
    expectException("{\"value\" : [[],[]]}", "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_ENTITY);
  }

  @Test
  public void invalidJsonSyntax() throws Exception {
    expectException("{\"value\" : }", "ETAllPrim",
        DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void emptyInput() throws Exception {
    expectException("", "ETAllPrim", DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void unknownContentInCollection() throws Exception {
    expectException("{\"value\":[],\"unknown\":null}", "ETAllPrim",
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void customAnnotationNotSupportedYet() throws Exception {
    expectException("{\"value\": [], \"@custom.annotation\": null}", "ETAllPrim",
        DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  private EntityCollection deserialize(final InputStream stream, final String entityTypeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.JSON, metadata)
        .entityCollection(stream, edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName)))
        .getEntityCollection();
  }

  private EntityCollection deserialize(final String input, final String entityTypeName)
      throws DeserializerException {
    return OData.newInstance().createDeserializer(ContentType.JSON, metadata)
        .entityCollection(new ByteArrayInputStream(input.getBytes()),
            edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName)))
        .getEntityCollection();
  }

  private void expectException(final InputStream stream, final String entityTypeName,
      final DeserializerException.MessageKeys messageKey) {
    try {
      deserialize(stream, entityTypeName);
      fail("Expected exception not thrown.");
    } catch (final DeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }

  private void expectException(final String entityCollectionString, final String entityTypeName,
    final DeserializerException.MessageKeys messageKey) {
    expectException(new ByteArrayInputStream(entityCollectionString.getBytes()), entityTypeName, messageKey);
  }
}
