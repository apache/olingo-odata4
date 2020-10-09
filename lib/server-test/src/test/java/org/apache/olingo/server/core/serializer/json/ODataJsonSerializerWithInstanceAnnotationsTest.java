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
package org.apache.olingo.server.core.serializer.json;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriHelper;
import org.apache.olingo.server.tecsvc.MetadataETagSupport;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;

public class ODataJsonSerializerWithInstanceAnnotationsTest {
  private static final OData odata = OData.newInstance();
  private static final ServiceMetadata metadata = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList(), new MetadataETagSupport("W/\"metadataETag\""));
  private static final EdmEntityContainer entityContainer = metadata.getEdm().getEntityContainer();
  private final DataProvider data = new DataProvider(odata, metadata.getEdm());
  private final ODataSerializer serializer = new ODataJsonSerializer(ContentType.JSON);
  private final ODataSerializer serializerNoMetadata = new ODataJsonSerializer(ContentType.JSON_NO_METADATA);
  private final ODataSerializer serializerFullMetadata = new ODataJsonSerializer(ContentType.JSON_FULL_METADATA);
  private final ODataSerializer serializerIEEECompatible =
      new ODataJsonSerializer(ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true"));
  private final UriHelper helper = odata.createUriHelper();
  
  @Test
  public void entityWithInstanceAnnotations() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    Annotation annotation = new Annotation();
    annotation.setTerm("com.contoso.display.highlight");
    annotation.setType("Boolean");
    annotation.setValue(ValueType.PRIMITIVE, true);
    entity.getAnnotations().add(annotation);
    annotation = new Annotation();
    annotation.setTerm("com.contoso.PersonalInfo.PhoneNumbers");
    annotation.setType("String");
    annotation.setValue(ValueType.COLLECTION_PRIMITIVE, Arrays.asList("(203)555-1718", "(203)555-1719"));
    entity.getAnnotations().add(annotation);
    InputStream result = serializer.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESAllPrim/$entity\","
        + "\"@odata.metadataEtag\":\"W/\\\"metadataETag\\\"\","
        + "\"@com.contoso.display.highlight\":true,"
        + "\"@com.contoso.PersonalInfo.PhoneNumbers\":"
        + "[\"(203)555-1718\",\"(203)555-1719\"],"
        + "\"PropertyInt16\":32767,"
        + "\"PropertyString\":\"First Resource - positive values\","
        + "\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,"
        + "\"PropertySByte\":127,"
        + "\"PropertyInt32\":2147483647,"
        + "\"PropertyInt64\":9223372036854775807,"
        + "\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E19,"
        + "\"PropertyDecimal\":34,"
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\","
        + "\"PropertyDate\":\"2012-12-03\","
        + "\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
        + "\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyTimeOfDay\":\"03:26:05\""
        + "}";
    Assert.assertEquals(expectedResult, resultString);
  }
  
  @Test
  public void entityPropertyWithInstanceAnnotations() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    Annotation annotation = new Annotation();
    annotation.setTerm("com.contoso.display.style");
    annotation.setType("com.contoso.display.styleType");
    List<Property> properties = new ArrayList<>();
    properties.add(new Property("Boolean", "title", ValueType.PRIMITIVE, true));
    properties.add(new Property("Int16", "Order", ValueType.PRIMITIVE, 1));
    ComplexValue complexValue = new ComplexValue();
    complexValue.setTypeName("com.contoso.display.styleType");
    complexValue.getValue().addAll(properties);
    annotation.setValue(ValueType.COMPLEX, complexValue);
    
    Property property = entity.getProperty("PropertyString");
    property.getAnnotations().add(annotation);
    
    InputStream result = serializerFullMetadata.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
	+	"\"@odata.context\":\"$metadata#ESAllPrim/$entity\","
	+	"\"@odata.metadataEtag\":\"W/\\\"metadataETag\\\"\","
	+	"\"@odata.type\":\"#olingo.odata.test1.ETAllPrim\","
	+	"\"@odata.id\":\"ESAllPrim(32767)\","
	+	"\"PropertyInt16@odata.type\":\"#Int16\","
	+	"\"PropertyInt16\":32767,"
	+	"\"PropertyString@com.contoso.display.style\":{"
	+		"\"@odata.type\":\"#com.contoso.display.styleType\","
	+		"\"title@odata.type\":\"#Boolean\","
	+		"\"title\":true,"
	+		"\"Order@odata.type\":\"#Int16\","
	+		"\"Order\":1"
	+	"},"
	+	"\"PropertyString\":\"First Resource - positive values\","
	+	"\"PropertyBoolean\":true,"
	+	"\"PropertyByte@odata.type\":\"#Byte\","
	+	"\"PropertyByte\":255,"
	+	"\"PropertySByte@odata.type\":\"#SByte\","
	+	"\"PropertySByte\":127,"
	+	"\"PropertyInt32@odata.type\":\"#Int32\","
	+	"\"PropertyInt32\":2147483647,"
	+	"\"PropertyInt64@odata.type\":\"#Int64\","
	+	"\"PropertyInt64\":9223372036854775807,"
	+	"\"PropertySingle@odata.type\":\"#Single\","
	+	"\"PropertySingle\":1.79E20,"
	+	"\"PropertyDouble\":-1.79E19,"
	+	"\"PropertyDecimal@odata.type\":\"#Decimal\","
	+	"\"PropertyDecimal\":34,"
	+	"\"PropertyBinary@odata.type\":\"#Binary\","
	+	"\"PropertyBinary\":\"ASNFZ4mrze8=\","
	+	"\"PropertyDate@odata.type\":\"#Date\","
	+	"\"PropertyDate\":\"2012-12-03\","
	+	"\"PropertyDateTimeOffset@odata.type\":\"#DateTimeOffset\","
	+	"\"PropertyDateTimeOffset\":\"2012-12-03T07:16:23Z\","
	+	"\"PropertyDuration@odata.type\":\"#Duration\","
	+	"\"PropertyDuration\":\"PT6S\","
	+	"\"PropertyGuid@odata.type\":\"#Guid\","
	+	"\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
	+	"\"PropertyTimeOfDay@odata.type\":\"#TimeOfDay\","
	+	"\"PropertyTimeOfDay\":\"03:26:05\","
	+	"\"NavPropertyETTwoPrimOne@odata.navigationLink\":\"ESTwoPrim(32767)\","
	+	"\"NavPropertyETTwoPrimMany@odata.navigationLink\":\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\","
	+	"\"#olingo.odata.test1.BAETAllPrimRT\":{"
	+		"\"title\":\"olingo.odata.test1.BAETAllPrimRT\","
	+		"\"target\":\"ESAllPrim(32767)/olingo.odata.test1.BAETAllPrimRT\""
	+	"}"
	+"}";
    Assert.assertEquals(expectedResult, resultString);
  }
  
  @Test
  public void entityComplexPropertyWithInstanceAnnotations() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    Annotation annotation = new Annotation();
    annotation.setTerm("com.contoso.display.style");
    annotation.setType("com.contoso.display.styleType");
    List<Property> properties = new ArrayList<>();
    properties.add(new Property("Boolean", "title", ValueType.PRIMITIVE, true));
    
    List<ComplexValue> complexValues = new ArrayList<>();
    ComplexValue orderComplexValue = new ComplexValue();
    orderComplexValue.setTypeName("com.contoso.display.orderDetails");
    complexValues.add(orderComplexValue);
    
    orderComplexValue = new ComplexValue();
    orderComplexValue.setTypeName("com.contoso.display.orderDetails");
    List<Property> orderProperties = new ArrayList<>();
    orderProperties.add(new Property("String", "name", ValueType.PRIMITIVE, "Cars"));
    orderProperties.add(new Property("String", "brand", ValueType.PRIMITIVE, "BMW"));
    orderComplexValue.getValue().addAll(orderProperties);
    complexValues.add(orderComplexValue);
    properties.add(new Property("Order", "Order", ValueType.COLLECTION_COMPLEX, complexValues));
    
    
    ComplexValue complexValue = new ComplexValue();
    complexValue.setTypeName("com.contoso.display.styleType");
    complexValue.getValue().addAll(properties);
    annotation.setValue(ValueType.COMPLEX, complexValue);
    
    Property property = entity.getProperty("PropertyComp");
    property.getAnnotations().add(annotation);
    
    InputStream result = serializerFullMetadata.entity(metadata, edmEntitySet.getEntityType(), entity,
        EntitySerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()).getContent();
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
    		+	"\"@odata.context\":\"$metadata#ESMixPrimCollComp/$entity\","
    		+	"\"@odata.metadataEtag\":\"W/\\\"metadataETag\\\"\","
    		+	"\"@odata.type\":\"#olingo.odata.test1.ETMixPrimCollComp\","
    		+	"\"@odata.id\":\"ESMixPrimCollComp(32767)\","
    		+	"\"PropertyInt16@odata.type\":\"#Int16\","
    		+	"\"PropertyInt16\":32767,"
    		+	"\"CollPropertyString@odata.type\":\"#Collection(String)\","
    		+	"\"CollPropertyString\":[\"Employee1@company.example\","
    		+ "\"Employee2@company.example\",\"Employee3@company.example\"],"
    		+	"\"PropertyComp@com.contoso.display.style\":{"
    		+		"\"@odata.type\":\"#com.contoso.display.styleType\","
    		+		"\"title@odata.type\":\"#Boolean\","
    		+		"\"title\":true,"
    		+		"\"Order@odata.type\":\"#Collection(Order)\","
    		+		"\"Order\":[{"
    		+			"\"@odata.type\":\"#com.contoso.display.orderDetails\""
    		+		"},{"
    		+			"\"@odata.type\":\"#com.contoso.display.orderDetails\","
    		+			"\"name@odata.type\":\"#String\","
    		+			"\"name\":\"Cars\","
    		+			"\"brand@odata.type\":\"#String\","
    		+			"\"brand\":\"BMW\""
    		+		"}]"
    		+	"},"
    		+	"\"PropertyComp\":{"
    		+		"\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
    		+		"\"PropertyInt16@odata.type\":\"#Int16\","
    		+		"\"PropertyInt16\":111,"
    		+		"\"PropertyString\":\"TEST A\","
    		+		"\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":"
    		+ "\"ESTwoKeyNav(PropertyInt16=1,PropertyString='1')\""
    		+	"},"
    		+	"\"CollPropertyComp@odata.type\":\"#Collection(olingo.odata.test1.CTTwoPrim)\","
    		+	"\"CollPropertyComp\":[{"
    		+		"\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
    		+		"\"PropertyInt16@odata.type\":\"#Int16\","
    		+		"\"PropertyInt16\":123,"
    		+		"\"PropertyString\":\"TEST 1\","
    		+		"\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":"
    		+ "\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\""
    		+	"},{"
    		+		"\"@odata.type\":\"#olingo.odata.test1.CTTwoPrim\","
    		+		"\"PropertyInt16@odata.type\":\"#Int16\","
    		+		"\"PropertyInt16\":456,"
    		+		"\"PropertyString\":\"TEST 2\","
    		+		"\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":"
    		+ "\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\""
    		+	"},{"
    		+		"\"@odata.type\":\"#olingo.odata.test1.CTBase\","
    		+		"\"PropertyInt16@odata.type\":\"#Int16\","
    		+		"\"PropertyInt16\":789,"
    		+		"\"PropertyString\":\"TEST 3\","
    		+		"\"AdditionalPropString\":\"ADD TEST\","
    		+		"\"NavPropertyETTwoKeyNavOne@odata.navigationLink\":"
    		+ "\"ESTwoKeyNav(PropertyInt16=1,PropertyString='2')\""
    		+	"}],"
    		+	"\"#olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\":{"
    		+		"\"title\":\"olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\","
    		+		"\"target\":"
    		+ "\"ESMixPrimCollComp(32767)/olingo.odata.test1.BAETMixPrimCollCompRTCTTwoPrim\""
    		+	"}"
    		+"}";
    Assert.assertEquals(expectedResult, resultString);
  }
}
