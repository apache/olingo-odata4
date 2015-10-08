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

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Link;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlComplexType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.core.edm.EdmComplexTypeImpl;
import org.apache.olingo.commons.core.edm.EdmPropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmBinary;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDate;
import org.apache.olingo.commons.core.edm.primitivetype.EdmDateTimeOffset;
import org.apache.olingo.commons.core.edm.primitivetype.EdmTimeOfDay;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataXmlDeserializerTest extends AbstractODataDeserializerTest {

  private static final EdmEntityContainer entityContainer = edm.getEntityContainer();
  private final ODataDeserializer deserializer = new ODataXmlDeserializer();

  @BeforeClass
  public static void setup() {
    XMLUnit.setIgnoreComments(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setNormalizeWhitespace(true);
    XMLUnit.setCompareUnmatched(false);
  }

  protected byte[] edmBinary(String value) throws EdmPrimitiveTypeException {
    return EdmBinary.getInstance().valueOfString(value, true, null, null, null, true,
        byte[].class);
  }
  protected Object edmDate(String value) throws EdmPrimitiveTypeException {
    return EdmDate.getInstance().valueOfString(value, true, null, null, null, true,
        EdmDate.getInstance().getDefaultType());
  }
  protected Object edmDateTimeOffset(String value) throws EdmPrimitiveTypeException {
    return EdmDateTimeOffset.getInstance().valueOfString(value, true, null, null, null, true, 
        EdmDateTimeOffset.getInstance().getDefaultType());
  }  
  protected Object edmTimeOfDay(String value) throws EdmPrimitiveTypeException {
    return EdmTimeOfDay.getInstance().valueOfString(value, true, null, null, null, true, 
        EdmTimeOfDay.getInstance().getDefaultType());
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
    Assert.assertArrayEquals(edmBinary("ASNFZ4mrze8="), (byte[]) result.getProperty("PropertyBinary").asPrimitive());
    Assert.assertEquals(edmDate("2012-12-03"), result.getProperty("PropertyDate").asPrimitive());
    Assert.assertEquals(edmDateTimeOffset("2012-12-03T07:16:23Z"), result.getProperty("PropertyDateTimeOffset")
        .asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(6), result.getProperty("PropertyDuration").asPrimitive());
    Assert.assertEquals(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"),
        result.getProperty("PropertyGuid").asPrimitive());
    Assert.assertEquals(edmTimeOfDay("03:26:05"), result.getProperty("PropertyTimeOfDay").asPrimitive());
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
    Assert.assertArrayEquals(edmBinary("ASNFZ4mrze8="), (byte[]) result.getProperty("PropertyBinary").asPrimitive());
    Assert.assertEquals(edmDate("2012-12-03"), result.getProperty("PropertyDate").asPrimitive());
    Assert.assertEquals(edmDateTimeOffset("2012-12-03T07:16:23Z"), result.getProperty("PropertyDateTimeOffset")
        .asPrimitive());
    Assert.assertEquals(BigDecimal.valueOf(6), result.getProperty("PropertyDuration").asPrimitive());
    Assert.assertEquals(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"),
        result.getProperty("PropertyGuid").asPrimitive());
    Assert.assertEquals(edmTimeOfDay("03:26:05"), result.getProperty("PropertyTimeOfDay").asPrimitive());
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
    ComplexValue cv = (ComplexValue)comp.getValue();
    
    Assert.assertEquals(16, cv.getValue().size());
    
    Assert.assertEquals((short) 32767, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertEquals("First Resource - first", getCVProperty(cv, "PropertyString").asPrimitive());
    Assert.assertEquals((short) 255, getCVProperty(cv, "PropertyByte").asPrimitive());
    Assert.assertEquals((byte) 127, getCVProperty(cv, "PropertySByte").asPrimitive());
    Assert.assertEquals(2147483647, getCVProperty(cv, "PropertyInt32").asPrimitive());
    Assert.assertEquals(9223372036854775807L, getCVProperty(cv, "PropertyInt64").asPrimitive());
  }  

  private Property getCVProperty(ComplexValue cv, String name) {
    for (Property p:cv.getValue()) {
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
    ComplexValue cv = (ComplexValue)comp.getValue();
    
    Assert.assertEquals(2, cv.getValue().size());
    Assert.assertEquals((short) 111, getCVProperty(cv, "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST A", getCVProperty(cv, "PropertyString").asPrimitive());
    
    comp = result.getProperty("CollPropertyComp");
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrim)", comp.getType());
    @SuppressWarnings("unchecked")
    List<ComplexValue> properties = (List<ComplexValue>)comp.getValue();
    
    Assert.assertEquals(3, properties.size());
    
    Assert.assertEquals((short) 123,
        getCVProperty(properties.get(0), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 1",
        getCVProperty(properties.get(0), "PropertyString").asPrimitive());

    Assert.assertEquals((short) 789, getCVProperty(properties.get(2), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 3", getCVProperty(properties.get(2), "PropertyString")
        .asPrimitive());    
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
    Assert.assertEquals(edmDate("2012-12-03"), inline.getProperty("PropertyDate").asPrimitive());
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
    Edm edm = Mockito.mock(Edm.class);

    CsdlProperty street = new CsdlProperty().setName("Street")
        .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
    CsdlProperty city = new CsdlProperty().setName("City")
        .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
    CsdlProperty region = new CsdlProperty().setName("Region")
        .setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
    CsdlProperty postalcode = new CsdlProperty().setName("PostalCode")
        .setType(EdmPrimitiveTypeKind.Int64.getFullQualifiedName());
    
    CsdlComplexType ct = new CsdlComplexType()
        .setName("Model.Address")
        .setProperties(Arrays.asList(street, city, region, postalcode));
    EdmComplexTypeImpl complexType = new EdmComplexTypeImpl(edm, new FullQualifiedName("Model.Address"), ct);
    
    Mockito.stub(edm.getComplexType(new FullQualifiedName("Model.Address"))).toReturn(complexType);
    
    CsdlProperty prop = new CsdlProperty();
    prop.setName("ShipTo");
    prop.setType(new FullQualifiedName("Model.Address"));
    EdmPropertyImpl edmProperty = new EdmPropertyImpl(edm, prop);

    String payload = "<data:ShipTo xmlns:data=\"http://docs.oasis-open.org/odata/ns/data\" " +
        " xmlns:metadata=\"http://docs.oasis-open.org/odata/ns/metadata\"\n" +
        " metadata:type=\"#Model.Address\">\n" + 
        "  <data:Street>Obere Str. 57</data:Street>\n" + 
        "  <data:City>Berlin</data:City>\n" + 
        "  <data:Region metadata:null=\"true\"/>\n" + 
        "  <data:PostalCode>12209</data:PostalCode>\n" + 
        "</data:ShipTo>";
    
    Property result = deserializer.property(new ByteArrayInputStream(payload.getBytes()), edmProperty).getProperty();

    Assert.assertEquals("ShipTo", result.getName());
    Assert.assertTrue(result.getValue() instanceof ComplexValue);
    ComplexValue cv = (ComplexValue)result.getValue();
    Assert.assertEquals("Model.Address", result.getType());
    Assert.assertEquals("Berlin", getCVProperty(cv, "City").asPrimitive());
    Assert.assertEquals("Obere Str. 57", getCVProperty(cv, "Street").asPrimitive());    
  }
  
  @SuppressWarnings("unchecked")
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

    List<ComplexValue> complex = (List<ComplexValue>)result.getValue();
    
    Assert.assertEquals(3, complex.size());
    Assert.assertEquals("Collection(olingo.odata.test1.CTTwoPrim)", result.getType());
    Assert.assertEquals((short) 123, getCVProperty(complex.get(0), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 1", getCVProperty(complex.get(0), "PropertyString").asPrimitive());
    Assert.assertEquals((short) 789, getCVProperty(complex.get(2), "PropertyInt16").asPrimitive());
    Assert.assertEquals("TEST 3", getCVProperty(complex.get(2), "PropertyString").asPrimitive());
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
