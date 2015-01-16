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
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.junit.Ignore;
import org.junit.Test;

public class ODataJsonDeserializerEntityTest extends AbstractODataDeserializerTest {

  @Test
  public void emptyEntity() throws Exception {
    String entityString = "{}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(0, properties.size());
  }

  @Test
  public void simpleEntityETAllPrim() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    assertNotNull(entity);
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
  public void simpleEntityETAllPrimNoTAllPropertiesPresent() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"" +
            "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
  }

  @Test
  public void simpleEntityETCompAllPrim() throws Exception {
    String entityString = "{\"PropertyInt16\":32767," +
        "\"PropertyComp\":{" +
        "\"PropertyString\":\"First Resource - first\"," +
        "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
        "\"PropertyBoolean\":true," +
        "\"PropertyByte\":255," +
        "\"PropertyDate\":\"2012-10-03\"," +
        "\"PropertyDateTimeOffset\":\"2012-10-03T07:16:23.1234567Z\"," +
        "\"PropertyDecimal\":34.27," +
        "\"PropertySingle\":1.79E20," +
        "\"PropertyDouble\":-1.79E19," +
        "\"PropertyDuration\":\"PT6S\"," +
        "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
        "\"PropertyInt16\":32767," +
        "\"PropertyInt32\":2147483647," +
        "\"PropertyInt64\":9223372036854775807," +
        "\"PropertySByte\":127," +
        "\"PropertyTimeOfDay\":\"01:00:01\"}}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCompAllPrim")));
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());

    assertNotNull(entity.getProperty("PropertyComp"));
    assertNotNull(entity.getProperty("PropertyComp") instanceof List);
    List<Property> complexProperties = entity.getProperty("PropertyComp").asComplex();
    assertEquals(16, complexProperties.size());
  }

  @Test
  public void simpleEntityETCollAllPrim() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyBoolean\":[true,false,true],"
        + "\"CollPropertyByte\":[50,200,249],"
        + "\"CollPropertySByte\":[-120,120,126],"
        + "\"CollPropertyInt16\":[1000,2000,30112],"
        + "\"CollPropertyInt32\":[23232323,11223355,10000001],"
        + "\"CollPropertyInt64\":[929292929292,333333333333,444444444444],"
        + "\"CollPropertySingle\":[1790.0,26600.0,3210.0],"
        + "\"CollPropertyDouble\":[-17900.0,-2.78E7,3210.0],"
        + "\"CollPropertyDecimal\":[12,-2,1234],"
        + "\"CollPropertyBinary\":[\"q83v\",\"ASNF\",\"VGeJ\"],"
        + "\"CollPropertyDate\":[\"1958-12-03\",\"1999-08-05\",\"2013-06-25\"],"
        + "\"CollPropertyDateTimeOffset\":[\"2015-08-12T03:08:34Z\",\"1970-03-28T12:11:10Z\","
        + "\"1948-02-17T09:09:09Z\"],"
        + "\"CollPropertyDuration\":[\"PT13S\",\"PT5H28M0S\",\"PT1H0S\"],"
        + "\"CollPropertyGuid\":[\"ffffff67-89ab-cdef-0123-456789aaaaaa\",\"eeeeee67-89ab-cdef-0123-456789bbbbbb\","
        + "\"cccccc67-89ab-cdef-0123-456789cccccc\"],"
        + "\"CollPropertyTimeOfDay\":[\"04:14:13\",\"23:59:59\",\"01:12:33\"]"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim")));
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(17, properties.size());

    // All properties need 3 entires
    for (Property prop : properties) {
      if (!prop.getName().equals("PropertyInt16")) {
        assertEquals(ValueType.COLLECTION_PRIMITIVE, prop.getValueType());
        assertTrue(prop.getValue() instanceof List);
        List<? extends Object> asCollection = prop.asCollection();
        assertEquals(3, asCollection.size());
      }
    }
    Property property = entity.getProperty("CollPropertyBoolean");
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(true, asCollection.get(0));
    assertEquals(false, asCollection.get(1));
    assertEquals(true, asCollection.get(2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void simpleEntityETMixPrimCollComp() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(4, properties.size());

    Property property = entity.getProperty("CollPropertyComp");
    assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

    assertTrue(property.getValue() instanceof List);
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(3, asCollection.size());

    for (Object arrayElement : asCollection) {
      assertTrue(arrayElement instanceof List);
      List<Object> castedArrayElement = (List<Object>) arrayElement;
      assertEquals(2, castedArrayElement.size());
    }
  }

  @Test
  public void simpleEntityWithContextURL() throws Exception {
    String entityString =
        "{\"@odata.context\": \"$metadata#ESAllPrim/$entity\"," +
            "\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    Entity entity =
        deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    assertNotNull(entity);
  }

  @Test
  public void ingoreSomeAnnotationsInEntityTypes() throws Exception {
    // We have to ignore @odata.navigation, @odata.association and @odata.type annotations on server side
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString@odata.type\":\"test\","
            + "\"Navigation@odata.navigationLink\":\"test\","
            + "\"Association@odata.associationLink\":\"test\","
            + "\"PropertyString\":\"First Resource - positive values\""
            + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
  }

  @Test
  public void ingoreSomeAnnotationsInComplexTypes() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"Navigation@odata.navigationLink\": 12," +
        "\"Association@odata.associationLink\": 12,\"PropertyString@odata.type\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"Navigation@odata.navigationLink\": 12," +
        "\"Association@odata.associationLink\": 12,\"PropertyString@odata.type\": 12,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
    deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
  }

//  ---------------------------------- Negative Tests -----------------------------------------------------------

  @Test(expected = DeserializerException.class)
  public void etAllPrimWithNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":true," +
            "\"PropertyByte\":null," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":\"2012-12-03\"," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void eTCollAllPrimWithNullValue() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyBoolean\":[true,null,true],"
        + "\"CollPropertyByte\":[50,200,249],"
        + "\"CollPropertySByte\":[-120,120,126],"
        + "\"CollPropertyInt16\":[1000,2000,30112],"
        + "\"CollPropertyInt32\":[23232323,11223355,10000001],"
        + "\"CollPropertyInt64\":[929292929292,333333333333,444444444444],"
        + "\"CollPropertySingle\":[1790.0,26600.0,3210.0],"
        + "\"CollPropertyDouble\":[-17900.0,-2.78E7,3210.0],"
        + "\"CollPropertyDecimal\":[12,-2,1234],"
        + "\"CollPropertyBinary\":[\"q83v\",\"ASNF\",\"VGeJ\"],"
        + "\"CollPropertyDate\":[\"1958-12-03\",\"1999-08-05\",\"2013-06-25\"],"
        + "\"CollPropertyDateTimeOffset\":[\"2015-08-12T03:08:34Z\",\"1970-03-28T12:11:10Z\","
        + "\"1948-02-17T09:09:09Z\"],"
        + "\"CollPropertyDuration\":[\"PT13S\",\"PT5H28M0S\",\"PT1H0S\"],"
        + "\"CollPropertyGuid\":[\"ffffff67-89ab-cdef-0123-456789aaaaaa\",\"eeeeee67-89ab-cdef-0123-456789bbbbbb\","
        + "\"cccccc67-89ab-cdef-0123-456789cccccc\"],"
        + "\"CollPropertyTimeOfDay\":[\"04:14:13\",\"23:59:59\",\"01:12:33\"]"
        + "}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETCollAllPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doublePrimitiveProperty() throws Exception {
    final String entityString = "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\"," +
        "\"PropertyInt16\":32766,\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETTwoPrim")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doubleComplexProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST B\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doubleComplexPropertyCollection() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}],"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]"
        + "}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void doublePrimitivePropertyCollection() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.DUPLICATE_PROPERTY, e.getMessageKey());
      throw e;
    }
  }

  @Ignore
  @Test(expected = DeserializerException.class)
  public void customAnnotationsLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString@custom.annotation\": 12,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.NOT_IMPLEMENTED, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInEntity() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"unknown\": 12,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInComplexProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"unknown\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

  @Test(expected = DeserializerException.class)
  public void unkownContentInComplexCollectionProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"unknown\": 12,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";

    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    try {
      ODataDeserializer deserializer = OData.newInstance().createDeserializer(ODataFormat.JSON);
      deserializer.entity(stream, edm.getEntityType(new FullQualifiedName("Namespace1_Alias", "ETMixPrimCollComp")));
    } catch (DeserializerException e) {
      assertEquals(DeserializerException.MessageKeys.UNKOWN_CONTENT, e.getMessageKey());
      throw e;
    }
  }

}
