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
package org.apache.olingo.server.core.deserializer.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ODataXmlDeserializerTest extends AbstractODataDeserializerTest {

  private static final EdmEntityContainer entityContainer = edm.getEntityContainer();
  private final ODataDeserializer deserializer = new ODataXmlDeserializer(metadata);

  @BeforeClass
  public static void setup() {
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setCompareUnmatched(false);
  }

  protected Object valueOf(final String value, final EdmPrimitiveTypeKind kind) throws EdmPrimitiveTypeException {
    final EdmPrimitiveType type = OData.newInstance().createPrimitiveTypeInstance(kind);
    return type.valueOfString(value, true, null, null, null, true, type.getDefaultType());
  }

  @Test
  public void entitySimple() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    
    String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\"> " +        
        "  <atom:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"id\" />\n" + 
        "  <atom:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"id\" />\n" + 
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16>32767</data:PropertyInt16>\n" + 
        "      <data:PropertyString>First Resource - positive values</data:PropertyString>\n" + 
        "      <data:PropertyBoolean>true</data:PropertyBoolean>\n" + 
        "      <data:PropertyByte>255</data:PropertyByte>\n" + 
        "      <data:PropertySByte>127</data:PropertySByte>\n" + 
        "      <data:PropertyInt32>2147483647</data:PropertyInt32>\n" + 
        "      <data:PropertyInt64>9223372036854775807</data:PropertyInt64>\n" + 
        "      <data:PropertySingle>1.79E20</data:PropertySingle>\n" + 
        "      <data:PropertyDouble>-1.79E19</data:PropertyDouble>\n" + 
        "      <data:PropertyDecimal>34</data:PropertyDecimal>\n" + 
        "      <data:PropertyBinary>ASNFZ4mrze8=</data:PropertyBinary>\n" + 
        "      <data:PropertyDate>2012-12-03</data:PropertyDate>\n" + 
        "      <data:PropertyDateTimeOffset>2012-12-03T07:16:23Z</data:PropertyDateTimeOffset>\n" + 
        "      <data:PropertyDuration>PT6S</data:PropertyDuration>\n" + 
        "      <data:PropertyGuid>01234567-89ab-cdef-0123-456789abcdef</data:PropertyGuid>\n" + 
        "      <data:PropertyTimeOfDay>03:26:05</data:PropertyTimeOfDay>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(16, result.getProperties().size());
    Assert.assertEquals(2, result.getNavigationBindings().size());
    
    Assert.assertEquals((short) 32767, result.getProperty("PropertyInt16").asPrimitive());
    Assert.assertEquals("First Resource - positive values", result.getProperty("PropertyString").asPrimitive());
    Assert.assertEquals((short) 255, result.getProperty("PropertyByte").asPrimitive());
    Assert.assertEquals((byte) 127, result.getProperty("PropertySByte").asPrimitive());
    Assert.assertEquals(2147483647, result.getProperty("PropertyInt32").asPrimitive());
    Assert.assertEquals(9223372036854775807L, result.getProperty("PropertyInt64").asPrimitive());
    Assert.assertEquals(1.79E20F, result.getProperty("PropertySingle").asPrimitive());
    Assert.assertEquals(-1.79E19, result.getProperty("PropertyDouble").asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(34), result.getProperty("PropertyDecimal").asPrimitive());
    Assert.assertArrayEquals((byte[]) valueOf("ASNFZ4mrze8=", EdmPrimitiveTypeKind.Binary),
        (byte[]) result.getProperty("PropertyBinary").asPrimitive());
    Assert.assertEquals(valueOf("2012-12-03", EdmPrimitiveTypeKind.Date),
        result.getProperty("PropertyDate").asPrimitive());
    Assert.assertEquals(valueOf("2012-12-03T07:16:23Z", EdmPrimitiveTypeKind.DateTimeOffset),
        result.getProperty("PropertyDateTimeOffset").asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(6), result.getProperty("PropertyDuration").asPrimitive());
    Assert.assertEquals(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"),
        result.getProperty("PropertyGuid").asPrimitive());
    Assert.assertEquals(valueOf("03:26:05", EdmPrimitiveTypeKind.TimeOfDay),
        result.getProperty("PropertyTimeOfDay").asPrimitive());
  }
  
  @Test
  public void derivedEntityETTwoPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    
    String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\"> " +        
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETBase\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16>32767</data:PropertyInt16>\n" + 
        "      <data:PropertyString>First Resource - positive values</data:PropertyString>\n" + 
        "      <data:AdditionalPropertyString_5>Additional</data:AdditionalPropertyString_5>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals("olingo.odata.test1.ETBase", result.getType());
    Assert.assertEquals(3, result.getProperties().size());
    Assert.assertEquals((short) 32767, result.getProperty("PropertyInt16").asPrimitive());
    Assert.assertEquals("First Resource - positive values", result.getProperty("PropertyString").asPrimitive());
    Assert.assertNotNull(
        result.getProperty("AdditionalPropertyString_5").asPrimitive());
  }
  
  @Test
  public void derivedEntityETTwoPrimNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    
    String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\"> " +        
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETBase\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16/>\n" + 
        "      <data:PropertyString/>\n" + 
        "      <data:AdditionalPropertyString_5/>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(3, result.getProperties().size());
    
    Assert.assertNull(result.getProperty("PropertyInt16").asPrimitive());
    Assert.assertNull(result.getProperty("PropertyString").asPrimitive());
    Assert.assertNull(
        result.getProperty("AdditionalPropertyString_5").asPrimitive());
  }
  
  
  @Test
  public void entitySimpleWithTypes() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    
    String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\"> " +        
        "  <atom:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "    type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "    href=\"id\" />\n" + 
        "  <atom:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "    href=\"id\" />\n" + 
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16 metadata:type=\"Int16\">32767</data:PropertyInt16>\n" + 
        "      <data:PropertyString>First Resource - positive values</data:PropertyString>\n" + 
        "      <data:PropertyBoolean metadata:type=\"Boolean\">true</data:PropertyBoolean>\n" + 
        "      <data:PropertyByte metadata:type=\"Byte\">255</data:PropertyByte>\n" + 
        "      <data:PropertySByte metadata:type=\"SByte\">127</data:PropertySByte>\n" + 
        "      <data:PropertyInt32 metadata:type=\"Int32\">2147483647</data:PropertyInt32>\n" + 
        "      <data:PropertyInt64 metadata:type=\"Int64\">9223372036854775807</data:PropertyInt64>\n" + 
        "      <data:PropertySingle metadata:type=\"Single\">1.79E20</data:PropertySingle>\n" + 
        "      <data:PropertyDouble metadata:type=\"Double\">-1.79E19</data:PropertyDouble>\n" + 
        "      <data:PropertyDecimal metadata:type=\"Decimal\">34</data:PropertyDecimal>\n" + 
        "      <data:PropertyBinary metadata:type=\"Binary\">ASNFZ4mrze8=</data:PropertyBinary>\n" + 
        "      <data:PropertyDate metadata:type=\"Date\">2012-12-03</data:PropertyDate>\n" + 
        "      <data:PropertyDateTimeOffset metadata:type=\"DateTimeOffset\">2012-12-03T07:16:23Z"
        + "</data:PropertyDateTimeOffset>\n" + 
        "      <data:PropertyDuration metadata:type=\"Duration\">PT6S</data:PropertyDuration>\n" + 
        "      <data:PropertyGuid metadata:type=\"GUID\">01234567-89ab-cdef-0123-456789abcdef"
        + "</data:PropertyGuid>\n" + 
        "      <data:PropertyTimeOfDay metadata:type=\"TimeOfDay\">03:26:05</data:PropertyTimeOfDay>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(16, result.getProperties().size());
    Assert.assertEquals(2, result.getNavigationBindings().size());
    
    Assert.assertEquals((short) 32767, result.getProperty("PropertyInt16").asPrimitive());
    Assert.assertEquals("First Resource - positive values", result.getProperty("PropertyString").asPrimitive());
    Assert.assertEquals((short) 255, result.getProperty("PropertyByte").asPrimitive());
    Assert.assertEquals((byte) 127, result.getProperty("PropertySByte").asPrimitive());
    Assert.assertEquals(2147483647, result.getProperty("PropertyInt32").asPrimitive());
    Assert.assertEquals(9223372036854775807L, result.getProperty("PropertyInt64").asPrimitive());
    Assert.assertEquals(1.79E20F, result.getProperty("PropertySingle").asPrimitive());
    Assert.assertEquals(-1.79E19, result.getProperty("PropertyDouble").asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(34), result.getProperty("PropertyDecimal").asPrimitive());
    Assert.assertArrayEquals((byte[]) valueOf("ASNFZ4mrze8=", EdmPrimitiveTypeKind.Binary),
        (byte[]) result.getProperty("PropertyBinary").asPrimitive());
    Assert.assertEquals(valueOf("2012-12-03", EdmPrimitiveTypeKind.Date),
        result.getProperty("PropertyDate").asPrimitive());
    Assert.assertEquals(valueOf("2012-12-03T07:16:23Z", EdmPrimitiveTypeKind.DateTimeOffset),
        result.getProperty("PropertyDateTimeOffset").asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(6), result.getProperty("PropertyDuration").asPrimitive());
    Assert.assertEquals(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"),
        result.getProperty("PropertyGuid").asPrimitive());
    Assert.assertEquals(valueOf("03:26:05", EdmPrimitiveTypeKind.TimeOfDay),
        result.getProperty("PropertyTimeOfDay").asPrimitive());
  }  

  @Test
  public void entityCompAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompAllPrim");

    String payload = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "metadata:etag=\"W/&quot;32767&quot;\">"
          + "<atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" "
          + "term=\"#olingo.odata.test1.ETCompAllPrim\"/>"
          + "<atom:content type=\"application/xml\">"
            + "<metadata:properties>"
              + "<data:PropertyInt16>32767</data:PropertyInt16>"
              + "<data:PropertyComp metadata:type=\"#olingo.odata.test1.CTAllPrim\">"
                + "<data:PropertyString>First Resource - first</data:PropertyString>"
                + "<data:PropertyBinary>ASNFZ4mrze8=</data:PropertyBinary>"
                + "<data:PropertyBoolean>true</data:PropertyBoolean>"
                + "<data:PropertyByte>255</data:PropertyByte>"
                + "<data:PropertyDate>2012-10-03</data:PropertyDate>"
                + "<data:PropertyDateTimeOffset>2012-10-03T07:16:23.1234567Z</data:PropertyDateTimeOffset>"
                + "<data:PropertyDecimal>34.27</data:PropertyDecimal>"
                + "<data:PropertySingle>1.79E20</data:PropertySingle>"
                + "<data:PropertyDouble>-1.79E19</data:PropertyDouble>"
                + "<data:PropertyDuration>PT6S</data:PropertyDuration>"
                + "<data:PropertyGuid>01234567-89ab-cdef-0123-456789abcdef</data:PropertyGuid>"
                + "<data:PropertyInt16>32767</data:PropertyInt16>"
                + "<data:PropertyInt32>2147483647</data:PropertyInt32>"
                + "<data:PropertyInt64>9223372036854775807</data:PropertyInt64>"
                + "<data:PropertySByte>127</data:PropertySByte>"
                + "<data:PropertyTimeOfDay>01:00:01</data:PropertyTimeOfDay>"
              + "</data:PropertyComp>"
            + "</metadata:properties>"
          + "</atom:content>"
        + "</atom:entry>";
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals("olingo.odata.test1.ETCompAllPrim",result.getType());
    
    Assert.assertEquals(2, result.getProperties().size());
    Assert.assertEquals(0, result.getNavigationLinks().size());

    Assert.assertEquals((short) 32767, result.getProperty("PropertyInt16").asPrimitive());

    Assert.assertNotNull(result.getProperty("PropertyComp"));
    Property comp = result.getProperty("PropertyComp");
    Assert.assertEquals("olingo.odata.test1.CTAllPrim", comp.getType());
    ComplexValue cv = comp.asComplex();
    
    Assert.assertEquals(16, cv.getValue().size());
    
    Assert.assertEquals((short) 32767, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertEquals("First Resource - first", getCVProperty(cv, "PropertyString").asPrimitive());
    Assert.assertEquals((short) 255, getCVProperty(cv, "PropertyByte").asPrimitive());
    Assert.assertEquals((byte) 127, getCVProperty(cv, "PropertySByte").asPrimitive());
    Assert.assertEquals(2147483647, getCVProperty(cv, "PropertyInt32").asPrimitive());
    Assert.assertEquals(9223372036854775807L, getCVProperty(cv, "PropertyInt64").asPrimitive());
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
  public void entityMixPrimCollComp() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" \n" + 
        "  metadata:metadata-etag=\"WmetadataETag\">\n" + 
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETMixPrimCollComp\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16>32767</data:PropertyInt16>\n" + 
        "      <data:CollPropertyString type=\"#Collection(String)\">\n" + 
        "        <metadata:element>Employee1@company.example</metadata:element>\n" + 
        "        <metadata:element>Employee2@company.example</metadata:element>\n" + 
        "        <metadata:element>Employee3@company.example</metadata:element>\n" + 
        "      </data:CollPropertyString>\n" + 
        "      <data:PropertyComp metadata:type=\"#olingo.odata.test1.CTTwoPrim\">\n" + 
        "        <data:PropertyInt16>111</data:PropertyInt16>\n" + 
        "        <data:PropertyString>TEST A</data:PropertyString>\n" + 
        "      </data:PropertyComp>\n" + 
        "       <data:CollPropertyComp metadata:type=\"#Collection(olingo.odata.test1.CTTwoPrim)\">\n" + 
        "          <metadata:element>\n" + 
        "            <data:PropertyInt16>123</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 1</data:PropertyString>\n" + 
        "          </metadata:element>\n" + 
        "          <metadata:element>\n" + 
        "            <data:PropertyInt16>456</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 2</data:PropertyString>\n" + 
        "          </metadata:element>\n" + 
        "          <metadata:element>\n" + 
        "            <data:PropertyInt16>789</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 3</data:PropertyString>\n" + 
        "          </metadata:element>\n" + 
        "        </data:CollPropertyComp>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 

    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(4, result.getProperties().size());
    Assert.assertEquals(0, result.getNavigationLinks().size());

    Assert.assertEquals(Arrays.asList("Employee1@company.example", "Employee2@company.example",
        "Employee3@company.example"), result.getProperty("CollPropertyString").getValue());

    Property comp = result.getProperty("PropertyComp");
    Assert.assertEquals("olingo.odata.test1.CTTwoPrim", comp.getType());
    ComplexValue cv = comp.asComplex();
    
    Assert.assertEquals(2, cv.getValue().size());
    Assert.assertEquals((short) 111, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST A", getCVProperty(cv, "PropertyString").asPrimitive());
    
    comp = result.getProperty("CollPropertyComp");
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrim)", comp.getType());

    List<?> properties = comp.asCollection();
    Assert.assertEquals(3, properties.size());
    
    Assert.assertEquals((short) 123, getCVProperty((ComplexValue) properties.get(0), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 1", getCVProperty((ComplexValue) properties.get(0), "PropertyString").asPrimitive());

    Assert.assertEquals((short) 789, getCVProperty((ComplexValue) properties.get(2), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 3", getCVProperty((ComplexValue) properties.get(2), "PropertyString").asPrimitive());
  }
  

  @Test
  public void derivedEntityMixPrimCollComp() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" \n" + 
        "  metadata:metadata-etag=\"WmetadataETag\">\n" + 
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETMixPrimCollComp\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16>32767</data:PropertyInt16>\n" + 
        "      <data:CollPropertyString type=\"#Collection(String)\">\n" + 
        "        <metadata:element>Employee1@company.example</metadata:element>\n" + 
        "        <metadata:element>Employee2@company.example</metadata:element>\n" + 
        "        <metadata:element>Employee3@company.example</metadata:element>\n" + 
        "      </data:CollPropertyString>\n" + 
        "      <data:PropertyComp metadata:type=\"#olingo.odata.test1.CTBase\">\n" + 
        "        <data:PropertyInt16>111</data:PropertyInt16>\n" + 
        "        <data:PropertyString>TEST A</data:PropertyString>\n" + 
        "        <data:AdditionalPropString>Additional</data:AdditionalPropString>\n" + 
        "      </data:PropertyComp>\n" + 
        "       <data:CollPropertyComp metadata:type=\"#Collection(olingo.odata.test1.CTTwoPrim)\">\n" + 
        "          <metadata:element  metadata:type=\"olingo.odata.test1.CTBase\">\n" + 
        "            <data:PropertyInt16>123</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 1</data:PropertyString>\n" + 
        "            <data:AdditionalPropString>Additional test</data:AdditionalPropString>\n" + 
        "          </metadata:element>\n" + 
        "          <metadata:element>\n" + 
        "            <data:PropertyInt16>456</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 2</data:PropertyString>\n" + 
        "          </metadata:element>\n" + 
        "          <metadata:element>\n" + 
        "            <data:PropertyInt16>789</data:PropertyInt16>\n" + 
        "            <data:PropertyString>TEST 3</data:PropertyString>\n" + 
        "          </metadata:element>\n" + 
        "        </data:CollPropertyComp>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n"; 

    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(4, result.getProperties().size());
    Assert.assertEquals(0, result.getNavigationLinks().size());

    Assert.assertEquals(Arrays.asList("Employee1@company.example", "Employee2@company.example",
        "Employee3@company.example"), result.getProperty("CollPropertyString").getValue());

    Property comp = result.getProperty("PropertyComp");
    Assert.assertEquals("olingo.odata.test1.CTBase", comp.getType());
    ComplexValue cv = comp.asComplex();
    
    Assert.assertEquals(3, cv.getValue().size());
    Assert.assertEquals((short) 111, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST A", getCVProperty(cv, "PropertyString").asPrimitive());
    Assert.assertEquals("Additional", getCVProperty(cv, "AdditionalPropString").asPrimitive());
    
    comp = result.getProperty("CollPropertyComp");
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrim)", comp.getType());

    List<?> properties = comp.asCollection();
    Assert.assertEquals(3, properties.size());
    
    Assert.assertEquals((short) 123, getCVProperty((ComplexValue) properties.get(0), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 1", getCVProperty((ComplexValue) properties.get(0), "PropertyString").asPrimitive());
    Assert.assertEquals("Additional test", getCVProperty((ComplexValue) properties.get(0), "AdditionalPropString")
        .asPrimitive());

    Assert.assertEquals((short) 789, getCVProperty((ComplexValue) properties.get(2), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 3", getCVProperty((ComplexValue) properties.get(2), "PropertyString").asPrimitive());
  }
  
  @Test
  public void deriveEntityESAllPrimDeepInsert() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" \n" + 
        "xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\" \n" + 
        "xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" m:context=\"$metadata#ESAllPrim/$entity\" \n" + 
        "m:metadata-etag=\"W/&quot;f67e7bc4-37c8-46a8-bcd5-d77875906099&quot;\">\n" + 
        "    <a:id>ESAllPrim(32767)</a:id>\n" + 

        "    <a:link rel=\"edit\" href=\"ESAllPrim(32767)\"/>\n" + 
        "    <a:link rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\" \n" + 
        " type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\" \n" + 
        " href=\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\">\n" + 
        "       <m:inline>\n" + 
        "          <a:feed>\n" + 
        "             <a:entry>\n" + 
        "                <a:id>ESTwoPrim(-365)</a:id>           \n" +         
        "                    <a:link rel=\"edit\" href=\"ESTwoPrim(-365)\"/>\n" + 
        "                    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "         term=\"#olingo.odata.test1.ETTwoPrim\"/>\n" + 
        "                    <a:content type=\"application/xml\">\n" + 
        "                        <m:properties>\n" + 
        "                            <d:PropertyInt16 m:type=\"Int16\">-365</d:PropertyInt16>\n" + 
        "                            <d:PropertyString>Test String2</d:PropertyString>\n" + 
        "                        </m:properties>\n" + 
        "                    </a:content>\n" + 
        "       </a:entry>\n" + 
        "        <a:entry>\n" + 
        "                    <a:id>ESTwoPrim(-365)</a:id>    \n" +                
        "                    <a:link rel=\"edit\" href=\"ESTwoPrim(-365)\"/>\n" + 
        "                    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" \n" + 
        "         term=\"#olingo.odata.test1.ETBase\"/>\n" + 
        "                    <a:content type=\"application/xml\">\n" + 
        "                        <m:properties>\n" + 
        "                            <d:PropertyInt16 m:type=\"Int16\">-365</d:PropertyInt16>\n" + 
        "                            <d:PropertyString>Test String2</d:PropertyString>\n" + 
        "                            <d:AdditionalPropertyString_5>Test String2</d:AdditionalPropertyString_5>\n" + 
        "                        </m:properties>\n" + 
        "                    </a:content>\n" + 
        "       </a:entry>\n" + 
        "            </a:feed>\n" + 
        "        </m:inline>\n" + 
        "   </a:link>\n" + 
        "   <a:link rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\" \n" + 
        "   type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\" href=\"ESTwoPrim(32767)\">\n" + 
        "        <m:inline>\n" + 
        "            <a:entry>\n" + 
        "                <a:id>ESTwoPrim(32767)</a:id>\n" + 
        "                <a:link rel=\"edit\" href=\"ESTwoPrim(32767)\"/>\n" + 
        "                <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" \n" + 
        "                 term=\"#olingo.odata.test1.ETBase\"/>\n" + 
        "                <a:content type=\"application/xml\">\n" + 
        "                    <m:properties>\n" + 
        "                        <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "                        <d:PropertyString>Test String4</d:PropertyString>\n" + 
        "                        <d:AdditionalPropertyString_5>Test String2</d:AdditionalPropertyString_5>\n" + 
        "                    </m:properties>\n" + 
        "                </a:content>\n" + 
        "            </a:entry>\n" + 
        "        </m:inline>\n" + 
        "    </a:link>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "     term=\"#olingo.odata.test1.ETAllPrim\"/>\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "        <m:properties>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "            <d:PropertyString>First Resource - positive values</d:PropertyString>\n" + 
        "            <d:PropertyBoolean m:type=\"Boolean\">true</d:PropertyBoolean>\n" + 
        "            <d:PropertyByte m:type=\"Byte\">255</d:PropertyByte>\n" + 
        "            <d:PropertySByte m:type=\"SByte\">127</d:PropertySByte>\n" + 
        "            <d:PropertyInt32 m:type=\"Int32\">2147483647</d:PropertyInt32>\n" + 
        "            <d:PropertyInt64 m:type=\"Int64\">9223372036854775807</d:PropertyInt64>\n" + 
        "            <d:PropertySingle m:type=\"Single\">1.79E20</d:PropertySingle>\n" + 
        "            <d:PropertyDouble m:type=\"Double\">-1.79E19</d:PropertyDouble>\n" + 
        "            <d:PropertyDecimal m:type=\"Decimal\">34</d:PropertyDecimal>\n" + 
        "            <d:PropertyBinary m:type=\"Binary\">ASNFZ4mrze8=</d:PropertyBinary>\n" + 
        "            <d:PropertyDate m:type=\"Date\">2012-12-03</d:PropertyDate>\n"+
        "            <d:PropertyDuration m:type=\"Duration\">PT6S</d:PropertyDuration>\n" + 
        "            <d:PropertyGuid m:type=\"Guid\">01234567-89ab-cdef-0123-456789abcdef</d:PropertyGuid>\n" + 
        "            <d:PropertyTimeOfDay m:type=\"TimeOfDay\">03:26:05</d:PropertyTimeOfDay>\n" + 
        "        </m:properties>\n" + 
        "    </a:content>\n" + 
        "</a:entry>\n" ;

    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();
    Assert.assertEquals(15, result.getProperties().size());
    Assert.assertEquals(2, result.getNavigationLinks().size());
    assertNotNull(result.getNavigationLinks().get(0).getInlineEntitySet()
        .getEntities().get(1).getProperty("AdditionalPropertyString_5"));
    assertNotNull(result.getNavigationLinks().get(1).getInlineEntity().getProperty("AdditionalPropertyString_5"));
  }

  @Test
  public void derivedEntityESCompCollDerived() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompCollDerived");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" m:context=\"$metadata#ESCompCollDerived/$entity\"\n" +  
        "m:metadata-etag=\"W/&quot;1c2796fd-da13-4741-9da2-99cac365f296&quot;\">\n" + 
        "    <a:id>ESCompCollDerived(32767)</a:id>\n" + 
        "    <a:title/>\n" + 
        "    <a:summary/>\n" + 
        "    <a:updated>2017-07-18T13:18:13Z</a:updated>\n" + 
        "    <a:author>\n" + 
        "        <a:name/>\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompCollDerived(32767)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" +
        "    term=\"#olingo.odata.test1.ETDeriveCollComp\"/>\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "        <m:properties>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "            <d:CollPropertyCompAno>\n" + 
        "                <m:element m:type=\"#olingo.odata.test1.CTTwoPrimAno\">\n" + 
        "                    <d:PropertyString>TEST9876</d:PropertyString>\n" + 
        "                </m:element>\n" + 
        "                <m:element  m:type=\"#olingo.odata.test1.CTBaseAno\">\n" + 
        "                    <d:AdditionalPropString>TEST9889</d:AdditionalPropString>\n" + 
        "                    <d:PropertyString>TEST9889</d:PropertyString>\n" + 
        "                </m:element>\n" + 
        "            </d:CollPropertyCompAno>\n" + 
        "        </m:properties>\n" + 
        "    </a:content>\n" + 
        "</a:entry>\n" ;

    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(2, result.getProperties().size());
    Assert.assertEquals(0, result.getNavigationLinks().size());

    Assert.assertEquals(("[[PropertyString=TEST9876], [AdditionalPropString=TEST9889, PropertyString=TEST9889]]"), 
        result.getProperty("CollPropertyCompAno").getValue().toString());

    Property comp = result.getProperty("CollPropertyCompAno");
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrimAno)", comp.getType());
    List<? extends Object> cv = comp.asCollection();
    
    Assert.assertEquals(2, cv.size());
    for (Object arrayElement : cv) {

      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      if(castedArrayElement.size() == 1){
        assertEquals("PropertyString=TEST9876", castedArrayElement.get(0).toString());
      }else{
        assertEquals(2, castedArrayElement.size());
        assertEquals("AdditionalPropString=TEST9889", castedArrayElement.get(0).toString());
      }
    
    }
  }
  
  @Test
  public void derivedEntityESCompCollDerivedNullEmpty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompCollDerived");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<a:entry xmlns:a=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "xmlns:d=\"http://docs.oasis-open.org/odata/ns/data\" m:context=\"$metadata#ESCompCollDerived/$entity\"\n" +  
        "m:metadata-etag=\"W/&quot;1c2796fd-da13-4741-9da2-99cac365f296&quot;\">\n" + 
        "    <a:id>ESCompCollDerived(32767)</a:id>\n" + 
        "    <a:title/>\n" + 
        "    <a:summary/>\n" + 
        "    <a:updated>2017-07-18T13:18:13Z</a:updated>\n" + 
        "    <a:author>\n" + 
        "        <a:name/>\n" + 
        "    </a:author>\n" + 
        "    <a:link rel=\"edit\" href=\"ESCompCollDerived(32767)\"/>\n" + 
        "    <a:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" +
        "    term=\"#olingo.odata.test1.ETDeriveCollComp\"/>\n" + 
        "    <a:content type=\"application/xml\">\n" + 
        "        <m:properties>\n" + 
        "            <d:PropertyInt16 m:type=\"Int16\">32767</d:PropertyInt16>\n" + 
        "            <d:CollPropertyCompAno>\n" + 
        "                <m:element m:type=\"#olingo.odata.test1.CTBaseAno\">\n" + 
        "                    <d:PropertyString/>\n" + 
        "                   <d:AdditionalPropString/>\n" +
        "                </m:element>\n" + 
        "                <m:element  m:type=\"#olingo.odata.test1.CTBaseAno\">\n" + 
        "                </m:element>\n" + 
        "            </d:CollPropertyCompAno>\n" + 
        "        </m:properties>\n" + 
        "    </a:content>\n" + 
        "</a:entry>\n" ;

    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(2, result.getProperties().size());
    Assert.assertEquals(0, result.getNavigationLinks().size());


    Property comp = result.getProperty("CollPropertyCompAno");
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrimAno)", comp.getType());
    List<? extends Object> cv = comp.asCollection();
    
    Assert.assertEquals(2, cv.size());
    for (Object arrayElement : cv) {

      assertTrue(arrayElement instanceof ComplexValue);
      List<Property> castedArrayElement = ((ComplexValue) arrayElement).getValue();
      if(castedArrayElement.size()>0){
        assertEquals(2, castedArrayElement.size());
        assertNull(castedArrayElement.get(0).getValue());
      }
    }
  }
   
  @Test
  public void entityMixEnumDefCollComp() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixEnumDefCollComp");
    final String payload = "<?xml version='1.0' encoding='UTF-8'?>\n"
        + "<a:entry xmlns:a=\"" + Constants.NS_ATOM + "\""
        + "  xmlns:m=\"" + Constants.NS_METADATA + "\" xmlns:d=\"" + Constants.NS_DATASERVICES + "\">\n"
        + "  <a:content type=\"application/xml\">\n"
        + "    <m:properties>\n"
        + "      <d:PropertyEnumString m:type=\"#olingo.odata.test1.ENString\">String2,String3"
        + "</d:PropertyEnumString>\n"
        + "      <d:CollPropertyEnumString m:type=\"#Collection(olingo.odata.test1.ENString)\">\n"
        + "        <m:element>String2</m:element>\n"
        + "        <m:element>String3</m:element>\n"
        + "        <m:element>String2,String3</m:element>\n"
        + "      </d:CollPropertyEnumString>\n"
        + "      <d:PropertyDefString m:type=\"#olingo.odata.test1.TDString\">Test</d:PropertyDefString>\n"
        + "      <d:CollPropertyDefString m:type=\"#Collection(olingo.odata.test1.TDString)\">\n"
        + "        <m:element>Test1</m:element>\n"
        + "        <m:element>Test2</m:element>\n"
        + "      </d:CollPropertyDefString>\n"
        + "      <d:PropertyCompMixedEnumDef m:type=\"#olingo.odata.test1.CTMixEnumDef\">\n"
        + "        <d:PropertyEnumString m:type=\"#olingo.odata.test1.ENString\">String2,String3"
        + "</d:PropertyEnumString>\n"
        + "        <d:CollPropertyEnumString m:type=\"#Collection(olingo.odata.test1.ENString)\">\n"
        + "          <m:element>String2</m:element>\n"
        + "          <m:element>String3</m:element>\n"
        + "          <m:element>String2,String3</m:element>\n"
        + "        </d:CollPropertyEnumString>\n"
        + "        <d:PropertyDefString m:type=\"#olingo.odata.test1.TDString\">Test</d:PropertyDefString>\n"
        + "        <d:CollPropertyDefString m:type=\"#Collection(olingo.odata.test1.TDString)\">\n"
        + "          <m:element>Test1</m:element>\n"
        + "          <m:element>Test2</m:element>\n"
        + "        </d:CollPropertyDefString>\n"
        + "      </d:PropertyCompMixedEnumDef>\n"
        + "      <d:CollPropertyCompMixedEnumDef m:type=\"#Collection(olingo.odata.test1.CTMixEnumDef)\">\n"
        + "        <m:element>\n"
        + "          <d:PropertyEnumString m:type=\"#olingo.odata.test1.ENString\">String2,String3"
        + "</d:PropertyEnumString>\n"
        + "          <d:CollPropertyEnumString m:type=\"#Collection(olingo.odata.test1.ENString)\">\n"
        + "            <m:element>String2</m:element>\n"
        + "            <m:element>String3</m:element>\n"
        + "            <m:element>String2,String3</m:element>\n"
        + "          </d:CollPropertyEnumString>\n"
        + "          <d:PropertyDefString m:type=\"#olingo.odata.test1.TDString\">Test</d:PropertyDefString>\n"
        + "          <d:CollPropertyDefString m:type=\"#Collection(olingo.odata.test1.TDString)\">\n"
        + "            <m:element>Test1</m:element>\n"
        + "            <m:element>Test2</m:element>\n"
        + "          </d:CollPropertyDefString>\n"
        + "        </m:element>\n"
        + "      </d:CollPropertyCompMixedEnumDef>\n"
        + "    </m:properties>\n"
        + "  </a:content>\n"
        + "</a:entry>";
    final Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(6, result.getProperties().size());
    
    Assert.assertEquals((short) 6, result.getProperty("PropertyEnumString").asEnum());
    Assert.assertEquals(3, result.getProperty("CollPropertyEnumString").asCollection().size());
    Assert.assertEquals("Test", result.getProperty("PropertyDefString").asPrimitive());
    Assert.assertEquals(2, result.getProperty("CollPropertyDefString").asCollection().size());
    Assert.assertEquals(4, result.getProperty("PropertyCompMixedEnumDef").asComplex().getValue().size());
    Assert.assertEquals(1, result.getProperty("CollPropertyCompMixedEnumDef").asCollection().size());
  }

  @Test
  public void entityWithNavigation() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");

    final String payload  = "<?xml version='1.0' encoding='UTF-8'?>\n" + 
        "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\"\n" + 
        "  xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "  xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\">" + 
        "  <atom:link\n" + 
        "    rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne\"\n" + 
        "    type=\"application/atom+xml;type=entry\" title=\"NavPropertyETAllPrimOne\"\n" + 
        "    href=\"id\">\n" + 
        "    <metadata:inline>\n" + 
        "      <atom:entry>\n" + 
        "        <atom:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimMany\"\n" + 
        "          type=\"application/atom+xml;type=feed\" title=\"NavPropertyETTwoPrimMany\"\n" + 
        "          href=\"id\" />\n" + 
        "        <atom:link\n" + 
        "          rel=\"http://docs.oasis-open.org/odata/ns/related/NavPropertyETTwoPrimOne\"\n" + 
        "          type=\"application/atom+xml;type=entry\" title=\"NavPropertyETTwoPrimOne\"\n" + 
        "          href=\"id\" />\n" + 
        "        <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "          term=\"#olingo.odata.test1.ETAllPrim\" />\n" + 
        "        <atom:content type=\"application/xml\">\n" + 
        "          <metadata:properties>\n" + 
        "            <data:PropertyDate>2012-12-03</data:PropertyDate>\n" + 
        "          </metadata:properties>\n" + 
        "        </atom:content>\n" + 
        "      </atom:entry>\n" + 
        "    </metadata:inline>\n" + 
        "  </atom:link>\n" + 
        "  <atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\"\n" + 
        "    term=\"#olingo.odata.test1.ETTwoPrim\" />\n" + 
        "  <atom:content type=\"application/xml\">\n" + 
        "    <metadata:properties>\n" + 
        "      <data:PropertyInt16>32767</data:PropertyInt16>\n" + 
        "      <data:PropertyString>Test String4</data:PropertyString>\n" + 
        "    </metadata:properties>\n" + 
        "  </atom:content>\n" + 
        "</atom:entry>\n" + 
        "";
    
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), 
        edmEntitySet.getEntityType()).getEntity();

    Assert.assertEquals(2, result.getProperties().size());
    Assert.assertEquals(1, result.getNavigationLinks().size());
    
    Assert.assertEquals((short) 32767, result.getProperty("PropertyInt16").asPrimitive());
    Assert.assertEquals("Test String4", result.getProperty("PropertyString").asPrimitive());    
    
    Assert.assertEquals(1, result.getNavigationLinks().size());
    Link navLink = result.getNavigationLinks().get(0);
    Assert.assertEquals("http://docs.oasis-open.org/odata/ns/related/NavPropertyETAllPrimOne", navLink.getRel());
    Assert.assertEquals("id", navLink.getBindingLink());
    Assert.assertEquals("NavPropertyETAllPrimOne", navLink.getTitle());
    
    Entity inline = navLink.getInlineEntity();
    Assert.assertEquals(1, inline.getProperties().size());
    Assert.assertEquals(2, inline.getNavigationBindings().size());
    Assert.assertEquals(valueOf("2012-12-03", EdmPrimitiveTypeKind.Date),
        inline.getProperty("PropertyDate").asPrimitive());
  } 

  @Test
  public void primitiveProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyInt16");
   
    String payload = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<metadata:value xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\">"
        + "234</metadata:value>";

    Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty).getProperty();
    Assert.assertEquals((short) 234, result.getValue()); 
  }

  @Test
  public void primitivePropertyNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyString");
    String payload = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<metadata:value xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "metadata:null=\"true\"/>";

    Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty).getProperty();
    Assert.assertNull(result.getValue());    
  }
  
  @Test
  public void primitiveCollectionProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("CollPropertyString");
    String payload = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<metadata:value xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"> "
        + "<metadata:element>Employee1@company.example</metadata:element>"
        + "<metadata:element>Employee2@company.example</metadata:element>"
        + "<metadata:element>Employee3@company.example</metadata:element>"
        + "</metadata:value>";
    Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty).getProperty();
    Assert.assertEquals(Arrays.asList("Employee1@company.example", "Employee2@company.example",
        "Employee3@company.example"), result.getValue());
  }

  @Test
  public void complexProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyComp");
    final String payload = "<data:PropertyComp xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + " xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n"
        + " metadata:type=\"#olingo.odata.test1.CTTwoPrim\">\n"
        + "  <data:PropertyInt16>123</data:PropertyInt16>\n" 
        + "  <data:PropertyString metadata:null=\"true\"/>\n"
        + "</data:PropertyComp>";

    final Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty)
        .getProperty();

    Assert.assertEquals("PropertyComp", result.getName());
    Assert.assertTrue(result.isComplex());
    final ComplexValue cv = result.asComplex();
    Assert.assertEquals("olingo.odata.test1.CTTwoPrim", result.getType());
    Assert.assertEquals((short) 123, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertTrue(getCVProperty(cv, "PropertyString").isNull());    
  }

  @Test
  public void extendedComplexProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESFourKeyAlias");
    
    String payload = "<?xml version='1.0' encoding='UTF-8'?>"
        + "<atom:entry xmlns:atom=\"http://www.w3.org/2005/Atom\" "
        + "xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\" "
        + "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\">"
          + "<atom:category scheme=\"http://docs.oasis-open.org/odata/ns/scheme\" "
          + "term=\"#olingo.odata.test1.ETFourKeyAlias\"/>"
          +  "<atom:content type=\"application/xml\">"
                +  "<metadata:properties>"
                    +  "<data:PropertyInt16 metadata:type=\"Int16\">1</data:PropertyInt16>"
                    +  "<data:PropertyComp metadata:type=\"#olingo.odata.test1.CTTwoPrim\">"
                        +  "<data:PropertyInt16 metadata:type=\"Int16\">11</data:PropertyInt16>"
                        +  "<data:PropertyString>Num11</data:PropertyString>"
                    +  "</data:PropertyComp>"
                    +  "<data:PropertyCompComp metadata:type=\"#olingo.odata.test1.CTCompComp\">"
                        +  "<data:PropertyComp metadata:type=\"#olingo.odata.test1.CTBase\">"
                            +  "<data:PropertyInt16 metadata:type=\"Int16\">111</data:PropertyInt16>"
                            +  "<data:PropertyString>Num111</data:PropertyString>"
                            +  "<data:AdditionalPropString>Test123</data:AdditionalPropString>"
                        +  "</data:PropertyComp>"
                    +  "</data:PropertyCompComp>"
                +  "</metadata:properties>"
              +  "</atom:content>"
          +  "</atom:entry>";
    
    EdmEntityType entityType = edmEntitySet.getEntityType();
    Entity result = deserializer.entity(new ByteArrayInputStream(payload.getBytes()), entityType)
            .getEntity();

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
  public void complexCollectionProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("CollPropertyComp");
    String payload = "<metadata:value xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" "
        + "metadata:type=\"#Collection(olingo.odata.test1.CTTwoPrim)\">\n"+ 
        "  <metadata:element>\n" + 
        "    <data:PropertyInt16>123</data:PropertyInt16>\n" + 
        "    <data:PropertyString>TEST 1</data:PropertyString>\n" + 
        "  </metadata:element>\n" + 
        "  <metadata:element>\n" + 
        "    <data:PropertyInt16>456</data:PropertyInt16>\n" + 
        "    <data:PropertyString>TEST 2</data:PropertyString>\n" + 
        "  </metadata:element>\n" + 
        "  <metadata:element>\n" + 
        "    <data:PropertyInt16>789</data:PropertyInt16>\n" + 
        "    <data:PropertyString>TEST 3</data:PropertyString>\n" + 
        "  </metadata:element>\n" + 
        "</metadata:value>";
    Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty).getProperty();

    List<?> complexCollection = result.asCollection();

    Assert.assertEquals(3, complexCollection.size());
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrim)", result.getType());
    Assert.assertEquals((short) 123,
        getCVProperty((ComplexValue) complexCollection.get(0), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 1",
        getCVProperty((ComplexValue) complexCollection.get(0), "PropertyString").asPrimitive());
    Assert.assertEquals((short) 789,
        getCVProperty((ComplexValue) complexCollection.get(2), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 3",
        getCVProperty((ComplexValue) complexCollection.get(2), "PropertyString").asPrimitive());
  }

  @Test
  public void entityReference() throws Exception {
    String payload = "<metadata:ref xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "              metadata:context=\"http://host/service/$metadata#$ref\"\n" + 
        "              xmlns=\"http://www.w3.org/2005/Atom\" "+
        "              id=\"http://host/service/Orders(10643)\" />";
    
    List<URI> result = deserializer.entityReferences(new ByteArrayInputStream(payload.getBytes()))
        .getEntityReferences();    
    Assert.assertEquals(1, result.size());
    Assert.assertEquals("http://host/service/Orders(10643)", result.get(0).toASCIIString());
  }

  @Test
  public void entityReferences() throws Exception {
    String payload = "<feed xmlns=\"http://www.w3.org/2005/Atom\"\n" + 
        "      xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" + 
        "      metadata:context=\"http://host/service/$metadata#Collection($ref)\" >\n" + 
        "  <metadata:ref id=\"http://host/service/Orders(10643)\" />\n" + 
        "  <metadata:ref id=\"http://host/service/Orders(10759)\" />\n" + 
        "</feed>";

    List<URI> result = deserializer.entityReferences(new ByteArrayInputStream(payload.getBytes()))
        .getEntityReferences();    
    Assert.assertEquals(2, result.size());
    Assert.assertEquals("http://host/service/Orders(10643)", result.get(0).toASCIIString());
    Assert.assertEquals("http://host/service/Orders(10759)", result.get(1).toASCIIString());
  }  
}
