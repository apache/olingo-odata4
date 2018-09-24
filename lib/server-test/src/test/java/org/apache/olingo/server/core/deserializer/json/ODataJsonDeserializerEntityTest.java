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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.geo.Geospatial;
import org.apache.olingo.commons.api.edm.geo.GeospatialCollection;
import org.apache.olingo.commons.api.edm.geo.LineString;
import org.apache.olingo.commons.api.edm.geo.MultiLineString;
import org.apache.olingo.commons.api.edm.geo.MultiPoint;
import org.apache.olingo.commons.api.edm.geo.MultiPolygon;
import org.apache.olingo.commons.api.edm.geo.Point;
import org.apache.olingo.commons.api.edm.geo.Polygon;
import org.apache.olingo.commons.api.edm.provider.CsdlMapping;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataJsonDeserializerEntityTest extends AbstractODataDeserializerTest {

  private static final ContentType CONTENT_TYPE_JSON_IEEE754Compatible =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true");
  private static final OData odata = OData.newInstance();

  @Test
  public void emptyEntity() throws Exception {
    final String entityString = "{}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
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
    final Entity entity = deserialize(entityString, "ETAllPrim");
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
  public void derivedEntityETTwoPrim() throws Exception {
    String entityString =
        "{\"@odata.type\":\"#olingo.odata.test1.ETBase\","+
            "\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"AdditionalPropertyString_5\":\"Additional\"}";
    final Entity entity = deserialize(entityString, "ETTwoPrim");
    assertNotNull(entity);
    assertEquals("olingo.odata.test1.ETBase", entity.getType());
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(3, properties.size());
    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());
    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertNotNull(entity.getProperty("AdditionalPropertyString_5").getValue());
  }
  
  @Test(expected=DeserializerException.class)
  public void derivedEntityETTwoPrimError() throws Exception {
    String entityString =
        "{  \"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"AdditionalPropertyString_5\":\"Additional\"}";
   deserialize(entityString, "ETTwoPrim");
  }

  @Test
  public void simpleEntityETAllPrimWithDefaultNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertNull(entity.getProperty("PropertyBoolean").getValue());
    assertNull(entity.getProperty("PropertyDate").getValue());
  }

  @Test
  public void simpleEntityETAllPrimNoTAllPropertiesPresent() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"" +
            "}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
  }

  @Test
  public void simpleEntityETNoneNullable() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":9223372036854775807," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":34," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(16, properties.size());

    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").getValue());
    assertNull(entity.getProperty("PropertyBoolean").getValue());
    assertNull(entity.getProperty("PropertyDate").getValue());
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
    final Entity entity = deserialize(entityString, "ETCompAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    assertEquals(new Short((short) 32767), entity.getProperty("PropertyInt16").getValue());

    assertNotNull(entity.getProperty("PropertyComp"));
    assertNotNull(entity.getProperty("PropertyComp") instanceof List);
    List<Property> complexProperties = entity.getProperty("PropertyComp").asComplex().getValue();
    assertEquals(16, complexProperties.size());
  }

  @Test
  public void extendedComplexProperty() throws Exception {
    
    final String payload = "{"
        + "\"@odata.context\":\"$metadata#ESCompComp/$entity\","
        + "\"@odata.metadataEtag\":\"W/\\\"metadataETag\\\"\","
        + "\"@odata.etag\":\"W/\\\"32767\\\"\","
        + "\"PropertyInt16\":32767,"
        + "\"PropertyComp\":{"
            +  "\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
            +  "\"PropertyInt16\":11,"
            +  "\"PropertyString\":\"Num11\""
        +  "},"
        +  "\"PropertyCompComp\":{"
            +  "\"@odata.type\":\"#olingo.odata.test1.CTCompComp\","
            +  "\"PropertyComp\":{"
            +  "\"@odata.type\":\"#olingo.odata.test1.CTBase\","
            +  "\"PropertyInt16\":32767,"
            +  "\"PropertyString\":\"Num111\","
            +  "\"AdditionalPropString\":\"Test123\""
        +  "}}}";
    final Entity result = deserialize(payload, "ETFourKeyAlias");
    
    Assert.assertNotNull(result);
    Property propertyCompComp = result.getProperty("PropertyCompComp");
    Assert.assertEquals("PropertyCompComp", propertyCompComp.getName());   
    Assert.assertEquals("olingo.odata.test1.CTCompComp", propertyCompComp.getType());
    Assert.assertTrue(propertyCompComp.isComplex());
    
    ComplexValue complexValuePropComp = propertyCompComp.asComplex();    
    Property propertyComp = getCVProperty(complexValuePropComp, "PropertyComp");
    Assert.assertEquals("PropertyComp", propertyComp.getName()); 
    Assert.assertEquals("olingo.odata.test1.CTBase", propertyComp.getType());
    Assert.assertTrue(propertyComp.isComplex());  
    
    final ComplexValue cvAdditionalString = propertyComp.asComplex();
    Assert.assertEquals("Test123",getCVProperty(cvAdditionalString, "AdditionalPropString").asPrimitive());
  }  
  
  @Test
  public void extendedEntityProperty() throws Exception {
    final String payload = "{\n" + 
        "   \"@odata.context\":\"$metadata#ETKeyPrimNav/$entity\",\n" + 
        "   \"@odata.metadataEtag\":\"W/metadataETag\",\n" + 
        "   \"@odata.etag\":\"W/32767\",\n" + 
        "   \"PropertyInt16\":32767,\n" + 
        "   \"PropertyString\":\"string\",\n" + 
        "   \"NavPropertyETKeyPrimNavOne\":\n" + 
        "      {\n" + 
        "         \"@odata.type\":\"#olingo.odata.test1.ETKeyPrimNavDerived\",\n" + 
        "         \"PropertyInt16\":32767,\n" + 
        "         \"PropertyString\":\"First Resource - first\",\n" + 
        "         \"PropertyBoolean\":true\n" + 
        "      }\n" + 
        "   \n" + 
        "}";
    final Entity result = deserialize(payload, "ETKeyPrimNav");
    Assert.assertNotNull(result);
    Link navProperty = result.getNavigationLink("NavPropertyETKeyPrimNavOne");
    Assert.assertNotNull(navProperty);
    Entity e = navProperty.getInlineEntity();
    Assert.assertNotNull(e);
    Assert.assertEquals("olingo.odata.test1.ETKeyPrimNavDerived", e.getType());
    Assert.assertEquals(new Short((short)32767), e.getProperty("PropertyInt16").getValue());
    Assert.assertEquals("First Resource - first", e.getProperty("PropertyString").getValue());
    Assert.assertEquals(true, e.getProperty("PropertyBoolean").getValue());
  }
    
  @Test
  public void derivedEntityESCompCollDerived() throws Exception {
    final String payload = "{\n" +
        "   \"@odata.context\": \"$metadata#ESCompCollDerived/$entity\",\n" +
        "   \"@odata.metadataEtag\":\"W/metadataETag\",\n" +
        "   \"@odata.etag\":\"W/32767\",\n" +
        "   \"PropertyInt16\":32767,\n" +
        "   \"CollPropertyCompAno@odata.type\": \"#Collection(olingo.odata.test1.CTTwoPrimAno)\",\n" +
        "   \"CollPropertyCompAno\": [\n" +
        "      {\n" +
        "     \"@odata.type\": \"#olingo.odata.test1.CTBaseAno\",\n" +
        "            \"PropertyString\": \"TEST9876\",\n" +
        "            \"AdditionalPropString\": \"Additional9876\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"@odata.type\": \"#olingo.odata.test1.CTTwoPrimAno\",\n" +
        "            \"PropertyString\": \"TEST1234\"\n" +
        "        }\n" +
        "    ]\n" +
        "   \n" +
        "}";
    final Entity entity = deserialize(payload, "ETDeriveCollComp");
    Assert.assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
    assertEquals("PropertyInt16=32767", properties.get(0).toString());
    for (Property prop : properties) {
      assertNotNull(prop);
      if (prop.isCollection()) {
        Property property = entity.getProperty("CollPropertyCompAno");
        assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

        assertTrue(property.getValue() instanceof List);
        List<? extends Object> asCollection = property.asCollection();
        assertEquals(2, asCollection.size());

        for (Object arrayElement : asCollection) {
          assertTrue(arrayElement instanceof ComplexValue);
          List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
          if(castedArrayElement.size() == 1){
            assertEquals("PropertyString=TEST1234", castedArrayElement.get(0).toString());
            assertEquals("olingo.odata.test1.CTTwoPrimAno", ((ComplexValue) arrayElement).getTypeName());
          }else{
            assertEquals(2, castedArrayElement.size());
            assertEquals("AdditionalPropString=Additional9876", castedArrayElement.get(1).toString());
            assertEquals("olingo.odata.test1.CTBaseAno", ((ComplexValue) arrayElement).getTypeName());
          }
        }
      }
    }
  }
  
  @Test
  public void derivedEntityESCompCollDerivedEmptyNull() throws Exception {
    final String payload = "{\n" +
        "   \"@odata.context\": \"$metadata#ESCompCollDerived/$entity\",\n" +
        "   \"@odata.metadataEtag\":\"W/metadataETag\",\n" +
        "   \"@odata.etag\":\"W/32767\",\n" +
        "   \"PropertyInt16\":32767,\n" +
        "   \"PropertyCompAno\":null,\n"+
        "   \"CollPropertyCompAno@odata.type\": \"#Collection(olingo.odata.test1.CTTwoPrimAno)\",\n" +
        "   \"CollPropertyCompAno\": [\n" +
        "      {\n" +
        "     \"@odata.type\": \"#olingo.odata.test1.CTBaseAno\",\n" +
        "            \"PropertyString\": null,\n" +
        "            \"AdditionalPropString\": null\n" +
        "        },\n" +
        "        {\n" +
        "        }\n" +
        "    ]\n" +
        "   \n" +
        "}";
    final Entity entity = deserialize(payload, "ETDeriveCollComp");
    Assert.assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(3, properties.size());
    assertEquals("PropertyInt16=32767", properties.get(0).toString());
    assertEquals("PropertyCompAno=null", properties.get(1).toString());
    for (Property prop : properties) {
      assertNotNull(prop);
      if (prop.isCollection()) {
        Property property = entity.getProperty("CollPropertyCompAno");
        assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

        assertTrue(property.getValue() instanceof List);
        List<? extends Object> asCollection = property.asCollection();
        assertEquals(2, asCollection.size());

        for (Object arrayElement : asCollection) {
          assertTrue(arrayElement instanceof ComplexValue);
          List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
         if(castedArrayElement.size() == 2){
            assertEquals(2, castedArrayElement.size());
            assertEquals("AdditionalPropString=null", castedArrayElement.get(1).toString());
          }else{
            assertEquals(0, castedArrayElement.size());
          }
        }
      }
    }
  }
  
  @Test(expected = DeserializerException.class)
  public void derivedEntityESCompCollDerivedError() throws Exception {
    final String payload = "{\n" +
        "   \"@odata.context\": \"$metadata#ESCompCollDerived/$entity\",\n" +
        "   \"@odata.metadataEtag\":\"W/metadataETag\",\n" +
        "   \"@odata.etag\":\"W/32767\",\n" +
        "   \"PropertyInt16\":32767,\n" +
        "   \"CollPropertyCompAno@odata.type\": \"#Collection(olingo.odata.test1.CTTwoPrimAno)\",\n" +
        "   \"CollPropertyCompAno\": [\n" +
        "      {\n" +
        "            \"PropertyString\": \"TEST9876\",\n" +
        "            \"AdditionalPropString\": \"Additional9876\"\n" +
        "        },\n" +
        "        {\n" +
        "            \"PropertyString\": \"TEST1234\"\n" +
        "        }\n" +
        "    ]\n" +
        "   \n" +
        "}";
     deserialize(payload, "ETDeriveCollComp");
  }
  private Property getCVProperty(ComplexValue cv, String name) {
    for (Property p : cv.getValue()) {
      if (p.getName().equals(name)) {
        return p;
      }
    }
    return null;
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
    final Entity entity = deserialize(entityString, "ETCollAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(17, properties.size());

    // All properties need 3 entries.
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
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
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
      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      assertEquals(2, castedArrayElement.size());
    }
  }

  @Test
  public void deriveEntityETMixPrimCollComp() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"@odata.type\": \"#olingo.odata.test1.CTBase\","
        + "\"PropertyInt16\":111,\"PropertyString\":\"TEST A\",\"AdditionalPropString\":\"Additional\"},"
        + "\"CollPropertyComp\":["
        + "{\"@odata.type\": \"#olingo.odata.test1.CTBase\",\n"
        + "\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\",\"AdditionalPropString\":\"Additional\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(4, properties.size());
    Property prop = entity.getProperty("PropertyComp");
    ComplexValue asComp = prop.asComplex();
    assertEquals(3,asComp.getValue().size());
    assertEquals("olingo.odata.test1.CTBase",prop.getType());
    Property property = entity.getProperty("CollPropertyComp");
    assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

    assertTrue(property.getValue() instanceof List);
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(3, asCollection.size());
    assertEquals(3,((ComplexValue)asCollection.get(0)).getValue().size());
    assertEquals(2,((ComplexValue)asCollection.get(1)).getValue().size());
    assertEquals(2,((ComplexValue)asCollection.get(2)).getValue().size());
  }
  
  @Test
  public void deriveEntityESAllPrimDeepInsert() throws Exception {
    final String entityString =  "{\"PropertyInt16\": 32767,"
        + "\"PropertyString\": \"First Resource - positive values\","
        + "\"PropertyBoolean\": true,"
        + "\"PropertyByte\": 255,"
        + "\"PropertySByte\": 127,"
        + "\"PropertyInt32\": 2147483647,"
        + "\"PropertyInt64\": 9223372036854775807,"
        + "\"PropertyDecimal\": 34,"
        + "\"PropertyBinary\": \"ASNFZ4mrze8=\","
        + "\"PropertyDate\": \"2012-12-03\","
        + "\"PropertyDateTimeOffset\": \"2012-12-03T07:16:23Z\","
        + "\"PropertyDuration\": \"PT6S\","
        + "\"PropertyGuid\": \"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyTimeOfDay\": \"03:26:05\","
        + "\"NavPropertyETTwoPrimMany\": [{\"@odata.type\": \"#olingo.odata.test1.ETBase\","
        + "\"PropertyInt16\": -365,\"PropertyString\": \"Test String2\","
        + "\"AdditionalPropertyString_5\": \"Test String2\"},"
        + "{\"PropertyInt16\": -365,\"PropertyString\": \"Test String2\"}],"
        + "\"NavPropertyETTwoPrimOne\":"
        + "{\"@odata.type\": \"#olingo.odata.test1.ETBase\",\"PropertyInt16\": 32767,"
        + "\"PropertyString\": \"Test String4\","
        + "\"AdditionalPropertyString_5\": \"Test String2\"}}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(14, properties.size());
    List<Link> navProperties = entity.getNavigationLinks();
    assertNotNull(navProperties);
    assertEquals(2, navProperties.size());
    assertNotNull(navProperties.get(1).getInlineEntitySet()
        .getEntities().get(0).getProperty("AdditionalPropertyString_5"));
    assertNotNull(navProperties.get(0).getInlineEntity().getProperty("AdditionalPropertyString_5"));
  }

  
  @Test
  public void eTMixPrimCollCompMissingPropertyInComplexType() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":{\"PropertyInt16\":111},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123},"
        + "{\"PropertyInt16\":456},"
        + "{\"PropertyInt16\":789}]}";
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    Property complexProperty = entity.getProperty("PropertyComp");
    assertEquals(ValueType.COMPLEX, complexProperty.getValueType());
    List<Property> complexPropertyValues = complexProperty.asComplex().getValue();
    assertEquals(1, complexPropertyValues.size());

    Property property = entity.getProperty("CollPropertyComp");
    assertEquals(ValueType.COLLECTION_COMPLEX, property.getValueType());

    assertTrue(property.getValue() instanceof List);
    List<? extends Object> asCollection = property.asCollection();
    assertEquals(3, asCollection.size());

    for (Object arrayElement : asCollection) {
      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      assertEquals(1, castedArrayElement.size());
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
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
  }

  @Test
  public void ignoreSomeAnnotationsInEntityTypes() throws Exception {
    // We have to ignore @odata.navigation, @odata.association and @odata.type annotations on server side
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString@odata.type\":\"test\","
            + "\"Navigation@odata.navigationLink\":\"test\","
            + "\"Association@odata.associationLink\":\"test\","
            + "\"PropertyString\":\"First Resource - positive values\""
            + "}";
    deserialize(entityString, "ETAllPrim");
  }

  @Test
  public void ignoreSomeAnnotationsInComplexTypes() throws Exception {
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
    deserialize(entityString, "ETMixPrimCollComp");
  }

  @Test
  public void odataControlInformationIsIgnoredForRequests() throws Exception {
    String entityString =
        "{"
            + "\"@odata.context\":\"http://localhost:8080\","
            + "\"@odata.metadataEtag\":\"metadataEtag\","
            + "\"@odata.id\":\"value\","
            + "\"@odata.editLink\":\"value\","
            + "\"@odata.readLink\":\"value\","
            + "\"@odata.etag\":\"value\","
            + "\"@odata.mediaEtag\":\"value\","
            + "\"@odata.mediaReadLink\":\"value\","
            + "\"@odata.mediaEditLink\":\"value\","
            + "\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\""
            + "}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());
  }

  @Test
  public void etAllPrimBindingOperation() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":\"ESTwoPrim(2)\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[\"ESTwoPrim(2)\",\"ESTwoPrim(3)\"]"
            + "}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);

    Link bindingToOne = entity.getNavigationBinding("NavPropertyETTwoPrimOne");
    assertNotNull(bindingToOne);
    assertEquals("NavPropertyETTwoPrimOne", bindingToOne.getTitle());
    assertEquals("ESTwoPrim(2)", bindingToOne.getBindingLink());
    assertEquals(Constants.ENTITY_BINDING_LINK_TYPE, bindingToOne.getType());
    assertTrue(bindingToOne.getBindingLinks().isEmpty());
    assertNull(bindingToOne.getHref());
    assertNull(bindingToOne.getRel());

    Link bindingToMany = entity.getNavigationBinding("NavPropertyETTwoPrimMany");
    assertNotNull(bindingToMany);
    assertEquals("NavPropertyETTwoPrimMany", bindingToMany.getTitle());
    assertNotNull(bindingToMany.getBindingLinks());
    assertEquals(2, bindingToMany.getBindingLinks().size());
    assertEquals(Constants.ENTITY_COLLECTION_BINDING_LINK_TYPE, bindingToMany.getType());
    assertNull(bindingToMany.getBindingLink());
    assertNull(bindingToMany.getHref());
    assertNull(bindingToMany.getRel());
  }

  @Test
  public void etAllPrimBindingOperationEmptyArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[]"
            + "}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    Link bindingToMany = entity.getNavigationBinding("NavPropertyETTwoPrimMany");
    assertNotNull(bindingToMany);
    assertTrue(bindingToMany.getBindingLinks().isEmpty());
  }

  @Test
  public void eTMixEnumDefCollCompTest() throws Exception {
    InputStream stream = getFileAsStream("EntityETMixEnumDefCollComp.json");
    final Entity entity = deserialize(stream, "ETMixEnumDefCollComp", ContentType.JSON);

    assertEquals(6, entity.getProperties().size());

    Property enumProperty = entity.getProperty("PropertyEnumString");
    assertNotNull(enumProperty);
    assertEquals((short) 2, enumProperty.getValue());

    Property defProperty = entity.getProperty("PropertyDefString");
    assertNotNull(defProperty);
    assertEquals("string", defProperty.getValue());

    Property complexProperty = entity.getProperty("PropertyCompMixedEnumDef");
    List<Property> value = complexProperty.asComplex().getValue();
    assertEquals((short) 2, value.get(0).getValue());

    defProperty = ((ComplexValue) entity.getProperty("CollPropertyCompMixedEnumDef").asCollection().get(1))
        .getValue().get(2);
    assertEquals("string", defProperty.getValue());
    stream.close();
  }
  
  @Test
  public void eTMixEnumDefCollCompTestWithEnumStrings() throws Exception {
    InputStream stream = getFileAsStream("EntityETMixEnumDefCollCompWithEnumStrings.json");
    final Entity entity = deserialize(stream, "ETMixEnumDefCollComp", ContentType.JSON);

    assertEquals(6, entity.getProperties().size());

    Property enumProperty = entity.getProperty("PropertyEnumString");
    assertNotNull(enumProperty);
    assertEquals((short) 2, enumProperty.getValue());

    Property defProperty = entity.getProperty("PropertyDefString");
    assertNotNull(defProperty);
    assertEquals("def", defProperty.getValue());

    Property complexProperty = entity.getProperty("PropertyCompMixedEnumDef");
    List<Property> value = complexProperty.asComplex().getValue();
    assertEquals((short) 2, value.get(0).getValue());

    defProperty = ((ComplexValue) entity.getProperty("CollPropertyCompMixedEnumDef").asCollection().get(1))
        .getValue().get(2);
    assertEquals("def", defProperty.getValue());
    stream.close();
  }

  @Test
  public void eTCollAllPrimWithNullValue() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"CollPropertyBoolean\":[true,null,false],"
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
    final Entity entity = deserialize(entityString, "ETCollAllPrim");
    assertTrue((Boolean) entity.getProperty("CollPropertyBoolean").asCollection().get(0));
    assertNull(entity.getProperty("CollPropertyBoolean").asCollection().get(1));
    assertFalse((Boolean) entity.getProperty("CollPropertyBoolean").asCollection().get(2));
  }

  @Test
  public void validJsonValueForComplexTypeNull() throws Exception {
    final String entityString = "{\"PropertyComp\":null}";
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
    assertNull(entity.getProperty("PropertyComp").getValue());
  }

  @Test
  public void validJsonValueForComplexCollectionNullValue() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyComp\":["
        + "null,"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
    List<?> collPropertyComp = entity.getProperty("CollPropertyComp").asCollection();
    assertNull(collPropertyComp.get(0));
    List<Property> complexPropertyProperties = ((ComplexValue) collPropertyComp.get(1)).getValue();
    assertEquals(Short.valueOf((short) 789), complexPropertyProperties.get(0).getValue());
    assertEquals("TEST 3", complexPropertyProperties.get(1).getValue());
  }

  @Test
  public void validJsonValueForPrimPropertyInComplexTypeNull() throws Exception {
    final String entityString = "{"
        + "\"PropertyComp\":{\"PropertyString\":\"TEST A\",\"PropertyInt16\":null}"
        + "}";
    final Entity entity = deserialize(entityString, "ETCompAllPrim");
    assertEquals("TEST A", entity.getProperty("PropertyComp").asComplex().getValue().get(0).getValue());
    assertNull(entity.getProperty("PropertyComp").asComplex().getValue().get(1).getValue());
  }

  @Test
  public void eTMixEnumDefCollCompNavValidComplexEnumValueNull() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"String2\","
        + "\"PropertyCompMixedEnumDef\" : {"
        + "\"PropertyEnumString\" : null"
        + "}}";
    final Entity entity = deserialize(entityString, "ETMixEnumDefCollComp");
    assertEquals((short) 2, entity.getProperty("PropertyEnumString").getValue());
    Property propertyCompMixedEnumDef = entity.getProperty("PropertyCompMixedEnumDef");
    assertNull(propertyCompMixedEnumDef.asComplex().getValue().get(0).getValue());
  }

  @Test
  public void eTMixEnumDefCollCompMultipleValuesForEnum() throws Exception {
    final String entityString = "{\"PropertyEnumString\": \"String1,String2\"}";
    final Entity entity = deserialize(entityString, "ETMixEnumDefCollComp");
    assertEquals((short) 3, entity.getProperty("PropertyEnumString").getValue());
  }

  @Test
  public void geoPoint() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryPoint);
    final String preamble = "{\"" + entityType.getPropertyNames().get(0) + "\":{";
    final Entity entity = deserialize(preamble + "\"type\":\"Point\",\"coordinates\":[1.25,2.75]," +
    		"\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:42\"}}}}",
        entityType);
    assertEquals(1, entity.getProperties().size());
    assertTrue(entity.getProperties().get(0).getValue() instanceof Point);
    final Point point = (Point) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOMETRY, point.getDimension());
    assertEquals("42", point.getSrid().toString());
    assertEquals(1.25, point.getX(), 0);
    assertEquals(2.75, point.getY(), 0);

    expectException(preamble + "}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":1}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"point\",\"coordinates\":null}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"LineString\"}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\"}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coord\":[]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":\"1 2\"}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":{\"x\":1,\"y\":2}}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[1]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[\"1\",2]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[1,\"2\"]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[1,2,\"3\"]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[1,2,3,4]}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[12345678901234567,2]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException(preamble + "\"type\":\"Point\",\"coordinates\":[1,2],\"extra\":\"extra\"}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void geoMultiPoint() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeographyMultiPoint);
    final Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"MultiPoint\",\"coordinates\":[[2.5,3.125,99],[3.5,4.125],[4.5,5.125]]}}",
        entityType);
    assertTrue(entity.getProperties().get(0).getValue() instanceof MultiPoint);
    final MultiPoint multiPoint = (MultiPoint) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOGRAPHY, multiPoint.getDimension());
    Iterator<Point> iterator = multiPoint.iterator();
    final Point point1 = iterator.next();
    assertEquals(Geospatial.Dimension.GEOGRAPHY, point1.getDimension());
    assertEquals(2.5, point1.getX(), 0);
    assertEquals(3.125, point1.getY(), 0);
    assertEquals(99, point1.getZ(), 0);
    final Point point2 = iterator.next();
    assertEquals(Geospatial.Dimension.GEOGRAPHY, point2.getDimension());
    assertEquals(3.5, point2.getX(), 0);
    assertEquals(4.125, point2.getY(), 0);
    final Point point3 = iterator.next();
    assertEquals(Geospatial.Dimension.GEOGRAPHY, point3.getDimension());
    assertEquals(4.5, point3.getX(), 0);
    assertEquals(5.125, point3.getY(), 0);
    assertFalse(iterator.hasNext());

    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"MultiPoint\",\"coordinates\":[{\"x\":1,\"y\":2}]}}",
        entityType, ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void geoLineString() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryLineString);
    final Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"LineString\",\"coordinates\":[[1.0,1.0],[2.0,2.0]]}}",
        entityType);
    assertTrue(entity.getProperties().get(0).getValue() instanceof LineString);
    final LineString lineString = (LineString) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOMETRY, lineString.getDimension());
    Iterator<Point> iterator = lineString.iterator();
    final Point point1 = iterator.next();
    assertEquals(Geospatial.Dimension.GEOMETRY, point1.getDimension());
    assertEquals(1, point1.getX(), 0);
    assertEquals(1, point1.getY(), 0);
    final Point point2 = iterator.next();
    assertEquals(Geospatial.Dimension.GEOMETRY, point2.getDimension());
    assertEquals(2, point2.getX(), 0);
    assertEquals(2, point2.getY(), 0);
    assertFalse(iterator.hasNext());

    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{\"type\":\"LineString\"}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    // A proper line string has at least two points but the OData specification has another opinion
    // so the following negative test would fail.
    //    expectException("{\"" + entityType.getPropertyNames().get(0)
    //        + "\":{\"type\":\"LineString\",\"coordinates\":[[1,2]]}}", entityType, ContentType.JSON,
    //        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0)
        + "\":{\"type\":\"LineString\",\"coordinates\":null}}", entityType, ContentType.JSON,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void geoMultiLineString() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryMultiLineString);
    final Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"MultiLineString\",\"coordinates\":["
        + "[[1.0,1.0],[2.0,2.0],[3.0,3.0],[4.0,4.0],[5.0,5.0]],"
        + "[[99.5,101.5],[150.0,151.25]]]}}",
        entityType);
    assertTrue(entity.getProperties().get(0).getValue() instanceof MultiLineString);
    final MultiLineString multiLineString = (MultiLineString) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOMETRY, multiLineString.getDimension());
    assertEquals(1, multiLineString.iterator().next().iterator().next().getY(), 0);

    expectException("{\"" + entityType.getPropertyNames().get(0)
        + "\":{\"type\":\"MultiLineString\",\"coordinates\":null}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0)
        + "\":{\"type\":\"MultiLineString\",\"coordinates\":\"1 2 3 4\"}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0)
        + "\":{\"type\":\"MultiLineString\",\"coordinates\":[{\"first\":[[1,2],[3,4]]}]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void geoPolygon() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryPolygon);
    Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":[[[0.0,0.0],[3.0,0.0],[3.0,3.0],[0.0,3.0],[0.0,0.0]],"
        + "[[1.0,1.0],[1.0,2.0],[2.0,2.0],[2.0,1.0],[1.0,1.0]]]}}",
        entityType);
    assertTrue(entity.getProperties().get(0).getValue() instanceof Polygon);
    Polygon polygon = (Polygon) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOMETRY, polygon.getDimension());
    assertEquals(0, polygon.getExterior().iterator().next().getX(), 0);
    assertEquals(1, polygon.getInterior(0).iterator().next().getY(), 0);

    entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":[[[0,0],[3,0],[3,3],[0,3],[0,0]]]}}",
        entityType);
    polygon = (Polygon) entity.getProperties().get(0).getValue();
    assertEquals(0, polygon.getNumberOfInteriorRings());

    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":{\"ext\":[[0,0],[3,0],[0,3],[0,0]]}}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":[[[0,0],[3,0],[3,3],[0,3]]]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":[[[0,0],[3,0],[3,3],[0,3],[42,87]]]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"Polygon\",\"coordinates\":[[[0,0],[3,0],[3,3],[0,3],[0,0]],"
        + "[[1,1],[1,2],[2,2],[2,1],[1,1]],"
        + "[[1,1],[1,2],[2,2],[2,1],[1,1]]]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void geoMultiPolygon() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryMultiPolygon);
    final Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"MultiPolygon\",\"coordinates\":["
        + "[[[0.0,0.0],[3.0,0.0],[3.0,3.0],[0.0,3.0],[0.0,0.0]],"
        + "[[1.0,1.0],[1.0,2.0],[2.0,2.0],[2.0,1.0],[1.0,1.0]]],"
        + "[[[0.0,0.0],[30.0,0.0],[0.0,30.0],[0.0,0.0]]]]}}",
        entityType);
    final MultiPolygon multiPolygon = (MultiPolygon) entity.getProperties().get(0).getValue();
    assertEquals(1, multiPolygon.iterator().next().getInterior(0).iterator().next().getX(), 0);

    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"MultiPolygon\",\"coordinates\":[{\"first\":[[[0,0],[3,0],[3,3],[0,3],[0,0]]]}]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void geoCollection() throws Exception {
    final EdmEntityType entityType = mockEntityType(EdmPrimitiveTypeKind.GeometryCollection);
    final Entity entity = deserialize("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"GeometryCollection\",\"geometries\":["
        + "{\"type\":\"Point\",\"coordinates\":[100.0,0.0]},"
        + "{\"type\":\"LineString\",\"coordinates\":[[101.0,0.0],[102.0,1.0]]}]}}",
        entityType);
    assertTrue(entity.getProperties().get(0).getValue() instanceof GeospatialCollection);
    GeospatialCollection collection = (GeospatialCollection) entity.getProperties().get(0).getValue();
    assertEquals(Geospatial.Dimension.GEOMETRY, collection.getDimension());
    Iterator<Geospatial> iterator = collection.iterator();
    final Geospatial point = iterator.next();
    assertEquals(Geospatial.Dimension.GEOMETRY, point.getDimension());
    assertEquals(Geospatial.Type.POINT, point.getGeoType());
    assertEquals(100, ((Point) point).getX(), 0);
    final Geospatial line = iterator.next();
    assertEquals(Geospatial.Dimension.GEOMETRY, line.getDimension());
    assertEquals(Geospatial.Type.LINESTRING, line.getGeoType());
    assertEquals(101, ((LineString) line).iterator().next().getX(), 0);

    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"GeometryCollection\",\"coordinates\":[0,0]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.UNKNOWN_CONTENT);
    expectException("{\"" + entityType.getPropertyNames().get(0) + "\":{"
        + "\"type\":\"GeometryCollection\",\"geometries\":[[0,0]]}}", entityType,
        ContentType.JSON, DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  private EdmEntityType mockEntityType(final EdmPrimitiveTypeKind typeKind) {
    EdmProperty property = Mockito.mock(EdmProperty.class);
    final String name = "Property" + typeKind.name();
    Mockito.when(property.getType()).thenReturn(odata.createPrimitiveTypeInstance(typeKind));
    EdmEntityType entityType = Mockito.mock(EdmEntityType.class);
    Mockito.when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName(NAMESPACE, "entityType"));
    Mockito.when(entityType.getPropertyNames()).thenReturn(Arrays.asList(name));
    Mockito.when(entityType.getProperty(name)).thenReturn(property);
    return entityType;
  }

  @Test
  public void mappingTest() throws Exception {
    EdmEntityType entityType = mock(EdmEntityType.class);
    when(entityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("namespace", "name"));
    List<String> propertyNames = new ArrayList<String>();
    propertyNames.add("PropertyDate");
    propertyNames.add("PropertyDateTimeOffset");
    when(entityType.getPropertyNames()).thenReturn(propertyNames);
    CsdlMapping mapping = new CsdlMapping().setMappedJavaClass(Date.class);

    EdmProperty propertyDate = mock(EdmProperty.class);
    when(propertyDate.getName()).thenReturn("PropertyDate");
    when(propertyDate.getMapping()).thenReturn(mapping);
    when(propertyDate.getType()).thenReturn(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date));
    when(entityType.getProperty("PropertyDate")).thenReturn(propertyDate);

    EdmProperty propertyDateTimeOffset = mock(EdmProperty.class);
    when(propertyDateTimeOffset.getName()).thenReturn("PropertyDateTimeOffset");
    when(propertyDateTimeOffset.getMapping()).thenReturn(mapping);
    when(propertyDateTimeOffset.getType()).thenReturn(
        odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset));
    when(entityType.getProperty("PropertyDateTimeOffset")).thenReturn(propertyDateTimeOffset);

    String entityString =
        "{\"PropertyDate\":\"2012-12-03\","
            + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"}";
    InputStream stream = new ByteArrayInputStream(entityString.getBytes());
    ODataDeserializer deserializer = odata.createDeserializer(ContentType.JSON, metadata);
    Entity entity = deserializer.entity(stream, entityType).getEntity();
    assertNotNull(entity);
    List<Property> properties = entity.getProperties();
    assertNotNull(properties);
    assertEquals(2, properties.size());

    assertNotNull(entity.getProperty("PropertyDate").getValue());
    assertEquals(Date.class, entity.getProperty("PropertyDate").getValue().getClass());
    assertNotNull(entity.getProperty("PropertyDateTimeOffset").getValue());
    assertEquals(Date.class, entity.getProperty("PropertyDateTimeOffset").getValue().getClass());
  }

  // ---------------------------------- Negative Tests -----------------------------------------------------------

  @Test
  public void emptyInput() throws Exception {
    expectException("", "ETAllPrim", DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void nonJsonInput() throws Exception {
    expectException("0", "ETAllPrim", DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    expectException("[]", "ETAllPrim", DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
    expectException("}{", "ETAllPrim", DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void etAllPrimWithInvalidNullValue() throws Exception {
    String entityString =
        "{\"PropertyInt16\":null," +
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
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void doublePrimitiveProperty() throws Exception {
    final String entityString = "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\"," +
        "\"PropertyInt16\":32766,\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";
    expectException(entityString, "ETTwoPrim",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
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
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
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
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
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
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.DUPLICATE_PROPERTY);
  }

  @Test
  public void customAnnotationInEntityLeadToNotImplemented() throws Exception {
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
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Test
  public void customAnnotationInComplexValueLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111," +
        "\"CollPropertyString@custom.annotation\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Test
  public void customAnnotationInComplexCollectionValueLeadToNotImplemented() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"CollPropertyString@custom.annotation\": 12,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.NOT_IMPLEMENTED);
  }

  @Test
  public void unknownContentInEntity() throws Exception {
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
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void unknownContentInComplexProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"unknown\": 12,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void unknownContentInComplexCollectionProperty() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"unknown\": 12,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.UNKNOWN_CONTENT);
  }

  @Test
  public void propertyInt16JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt16\":\"32767\"}");
    checkPropertyJsonType("{\"PropertyInt16\":true}");
  }

  @Test
  public void propertyInt32JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt32\":\"2147483647\"}");
    checkPropertyJsonType("{\"PropertyInt32\":true}");
  }

  @Test
  public void propertyInt64JsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyInt64\":\"9223372036854775807\"}");
    checkPropertyJsonType("{\"PropertyInt64\":true}");
  }

  @Test
  public void propertyStringJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyString\":32767}");
    checkPropertyJsonType("{\"PropertyString\":true}");
  }

  @Test
  public void propertyBooleanJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyBoolean\":\"true\"}");
    checkPropertyJsonType("{\"PropertyBoolean\":123}");
  }

  @Test
  public void propertyByteJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyByte\":\"255\"}");
    checkPropertyJsonType("{\"PropertyByte\":true}");
  }

  @Test
  public void propertySByteJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertySByte\":\"127\"}");
    checkPropertyJsonType("{\"PropertySByte\":true}");
  }

  @Test
  public void propertySingleJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertySingle\":\"1.79E20\"}");
    checkPropertyJsonType("{\"PropertySingle\":true}");
  }

  @Test
  public void propertyDoubleJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDouble\":\"-1.79E19\"}");
    checkPropertyJsonType("{\"PropertyDouble\":true}");
  }

  @Test
  public void propertyDecimalJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDecimal\":\"34\"}");
    checkPropertyJsonType("{\"PropertyDecimal\":true}");
  }

  @Test
  public void propertyBinaryJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyBinary\":32767}");
    checkPropertyJsonType("{\"PropertyBinary\":true}");
  }

  @Test
  public void propertyDateJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDate\":32767}");
    checkPropertyJsonType("{\"PropertyDate\":true}");
  }

  @Test
  public void propertyDateTimeOffsetJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDateTimeOffset\":32767}");
    checkPropertyJsonType("{\"PropertyDateTimeOffset\":true}");
  }

  @Test
  public void propertyDurationJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyDuration\":32767}");
    checkPropertyJsonType("{\"PropertyDuration\":true}");
  }

  @Test
  public void propertyGuidTimeOffsetJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyGuid\":32767}");
    checkPropertyJsonType("{\"PropertyGuid\":true}");
  }

  @Test
  public void propertyTimeOfDayJsonTypesNegativeCheck() throws Exception {
    checkPropertyJsonType("{\"PropertyTimeOfDay\":32767}");
    checkPropertyJsonType("{\"PropertyTimeOfDay\":true}");
  }

  @Test
  public void bindOperationWrongJsonTypeForToOne() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":[\"ESTwoPrim(2)\"]"
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE);
  }

  @Test
  public void bindOperationWrongJsonTypeForToMany() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":\"ESTwoPrim(2)\""
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE);
  }

  @Test
  public void bindOperationWrongJsonTypeForToManyNumberInArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[123,456]"
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_ANNOTATION_TYPE);
  }

  @Test
  public void bindOperationWrongAnnotationFormat() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"@odata.bind\":\"ESTwoPrim(2)\""
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.NAVIGATION_PROPERTY_NOT_FOUND);
  }

  @Test
  public void bindingOperationNullOnToOneNonNull() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimOne@odata.bind\":null"
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION);    
  }
  
  @Test
  public void bindingOperationNullOnToOneNull() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETAllPrimOne@odata.bind\":null"
            + "}";
    
    final Entity entity = deserialize(entityString, "ETTwoPrim");
    assertEquals("First Resource - positive values", entity.getProperty("PropertyString").asPrimitive());
    assertNull(entity.getNavigationBinding("NavPropertyETAllPrimOne").getBindingLink());
  }  
  @Test
  public void bindingOperationNullOnToMany() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":null"
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION);
  }

  @Test
  public void bindingOperationNullInArray() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767,"
            + "\"PropertyString\":\"First Resource - positive values\","
            + "\"NavPropertyETTwoPrimMany@odata.bind\":[null]"
            + "}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_NULL_ANNOTATION);
  }

  @Test
  public void invalidJsonSyntax() throws Exception {
    final String entityString = "{\"PropertyInt16\":32767,}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.JSON_SYNTAX_EXCEPTION);
  }

  @Test
  public void invalidJsonValueForPrimTypeArray() throws Exception {
    final String entityString = "{\"PropertyInt16\":[]}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void invalidJsonValueForPrimTypeObject() throws Exception {
    final String entityString = "{\"PropertyInt16\":{}}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void invalidJsonValueForPrimCollectionTypeObject() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyString\":"
        + "{\"Employee1@company.example\":1234}"
        + "}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void invalidJsonValueForComplexTypeTypeString() throws Exception {
    final String entityString = "{\"PropertyComp\":\"InvalidString\"}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void invalidNullValueForComplexTypeNullableFalse() throws Exception {
    final String entityString = "{\"PropertyComp\":null}";
    expectException(entityString, "ETTwoKeyNav",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void invalidNullValueForPrimBeforeComplexTypeNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\": null, \"PropertyString\": \"321\", "
        + "\"PropertyComp\":{\"PropertyInt16\": null, "
        + "\"PropertyComp\": {\"PropertyString\":\"StringValue\"}}"
        + "}";
    expectException(entityString, "ETTwoKeyNav",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void invalidNullValueForComplexTypePropertyNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"PropertyInt16\": 123, "
        + "\"PropertyCompTwoPrim\":{\"PropertyInt16\": null, \"PropertyString\":\"StringValue\"}"
        + "}";
    expectException(entityString, "ETKeyNav",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void invalidNullValueForPrimCollectionNullableFalse() throws Exception {
    final String entityString = "{"
        + "\"CollPropertyString\":["
        + "null,"
        + "\"StringValue_1\",\"TEST 3\"]}";
    expectException(entityString, "ETCollAllPrim",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void invalidNullValueForPrimIntCollectionNullableFalse() throws Exception {
    final String entityString = "{\"CollPropertyInt16\":[123,\"null\",4711]}";
    expectException(entityString, "ETCollAllPrim",
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void provokedPrimitiveTypeException() throws Exception {
    final String entityString = "{\"PropertyInt16\":32767000000000000000000000000000000000000}";
    expectException(entityString, "ETMixPrimCollComp",
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void eTMixEnumDefCollCompInvalidEnumValueNull() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : null,"
        + "\"PropertyCompMixedEnumDef\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";
    expectException(entityString, "ETMixEnumDefCollComp",
        DeserializerException.MessageKeys.INVALID_NULL_PROPERTY);
  }

  @Test
  public void eTMixEnumDefCollCompInvalidEnumValueArray() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : [],"
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";
    expectException(entityString, "ETMixEnumDefCollComp",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void eTMixEnumDefCollCompInvalidEnumValueObject() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : {},"
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";
    expectException(entityString, "ETMixEnumDefCollComp",
        DeserializerException.MessageKeys.INVALID_JSON_TYPE_FOR_PROPERTY);
  }

  @Test
  public void eTMixEnumDefCollCompInvalidEnumValue() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"invalid\","
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";
    expectException(entityString, "ETMixEnumDefCollComp",
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void eTMixEnumDefCollCompInvalidEnumValueByPrimitiveTypeException() throws Exception {
    String entityString = "{"
        + "\"PropertyEnumString\" : \"18\","
        + "\"PropertyCompEnum\" : {"
        + "\"PropertyEnumString\" : \"2\""
        + "}}";
    expectException(entityString, "ETMixEnumDefCollComp",
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void ieee754Compatible() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"9223372036854775807\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"34\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    final Entity entity = deserialize(entityString, "ETAllPrim", CONTENT_TYPE_JSON_IEEE754Compatible);

    assertEquals(9223372036854775807L, entity.getProperty("PropertyInt64").asPrimitive());
    assertEquals(BigDecimal.valueOf(34), entity.getProperty("PropertyDecimal").asPrimitive());
  }

  @Test
  public void ieee754CompatibleNull() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":null," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":null," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    final Entity entity = deserialize(entityString, "ETAllPrim", CONTENT_TYPE_JSON_IEEE754Compatible);

    assertTrue(entity.getProperty("PropertyInt64").isNull());
    assertTrue(entity.getProperty("PropertyDecimal").isNull());
  }

  @Test
  public void ieee754CompatibleEmptyString() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\" \"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    expectException(entityString, "ETAllPrim", CONTENT_TYPE_JSON_IEEE754Compatible,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void ieee754CompatibleNullAsString() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"null\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    expectException(entityString, "ETAllPrim", CONTENT_TYPE_JSON_IEEE754Compatible,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void ieee754CompatibleAsNumber() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":123," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    expectException(entityString, "ETAllPrim", CONTENT_TYPE_JSON_IEEE754Compatible,
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  @Test
  public void ieee754NotCompatibleAsString() throws Exception {
    String entityString =
        "{\"PropertyInt16\":32767," +
            "\"PropertyString\":\"First Resource - positive values\"," +
            "\"PropertyBoolean\":null," +
            "\"PropertyByte\":255," +
            "\"PropertySByte\":127," +
            "\"PropertyInt32\":2147483647," +
            "\"PropertyInt64\":\"123\"," +
            "\"PropertySingle\":1.79E20," +
            "\"PropertyDouble\":-1.79E19," +
            "\"PropertyDecimal\":\"null\"," +
            "\"PropertyBinary\":\"ASNFZ4mrze8=\"," +
            "\"PropertyDate\":null," +
            "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\"," +
            "\"PropertyDuration\":\"PT6S\"," +
            "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\"," +
            "\"PropertyTimeOfDay\":\"03:26:05\"}";
    expectException(entityString, "ETAllPrim",
        DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  protected static Entity deserialize(final InputStream stream, final String entityTypeName,
      final ContentType contentType) throws DeserializerException {
    return deserializeWithResult(stream, entityTypeName, contentType).getEntity();
  }

  protected static DeserializerResult deserializeWithResult(final InputStream stream, final String entityTypeName,
      final ContentType contentType) throws DeserializerException {
    final EdmEntityType entityType = edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName));
    return deserializeWithResult(stream, entityType, contentType);
  }

  protected static DeserializerResult deserializeWithResult(final InputStream stream, final EdmEntityType entityType,
      final ContentType contentType) throws DeserializerException {
    return odata.createDeserializer(contentType, metadata).entity(stream, entityType);
  }

  private static Entity deserialize(final String entityString, final String entityTypeName,
      final ContentType contentType) throws DeserializerException {
    return deserialize(new ByteArrayInputStream(entityString.getBytes()), entityTypeName, contentType);
  }

  protected static Entity deserialize(final String entityString, final String entityTypeName)
      throws DeserializerException {
    return deserialize(entityString, entityTypeName, ContentType.JSON);
  }

  private Entity deserialize(final String entityString, final EdmEntityType entityType)
      throws DeserializerException {
    return deserializeWithResult(new ByteArrayInputStream(entityString.getBytes()), entityType, ContentType.JSON)
        .getEntity();
  }

  private static void checkPropertyJsonType(final String entityString) throws DeserializerException {
    expectException(entityString, "ETAllPrim", DeserializerException.MessageKeys.INVALID_VALUE_FOR_PROPERTY);
  }

  protected static void expectException(final String entityString, final String entityTypeName,
      final ContentType contentType, final DeserializerException.MessageKeys messageKey) {
    expectException(entityString,
        edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName)), contentType, messageKey);
  }

  protected static void expectException(final String entityString, final String entityTypeName,
      final DeserializerException.MessageKeys messageKey) {
    expectException(entityString, entityTypeName, ContentType.JSON, messageKey);
  }

  private static void expectException(final String entityString, final EdmEntityType entityType,
      final ContentType contentType, final DeserializerException.MessageKeys messageKey) {
    try {
      deserializeWithResult(new ByteArrayInputStream(entityString.getBytes()), entityType, contentType);
      fail("Expected exception not thrown.");
    } catch (final DeserializerException e) {
      assertEquals(messageKey, e.getMessageKey());
    }
  }

  public static DeserializerResult deserializeWithResultv401(InputStream stream, EdmEntityType entityType,
      ContentType contentType) throws DeserializerException {
    List<String> version = new ArrayList<String>();
    version.add("4.01");
    return odata.createDeserializer(contentType, metadata, version).entity(stream, entityType);
  }
}
