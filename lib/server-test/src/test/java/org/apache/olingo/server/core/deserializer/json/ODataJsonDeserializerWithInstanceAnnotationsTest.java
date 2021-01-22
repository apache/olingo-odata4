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
import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.commons.api.data.Annotation;
import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.deserializer.DeserializerException;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.core.deserializer.AbstractODataDeserializerTest;
import org.junit.Test;

public class ODataJsonDeserializerWithInstanceAnnotationsTest extends AbstractODataDeserializerTest {

  private static final ContentType CONTENT_TYPE_JSON_IEEE754Compatible =
      ContentType.create(ContentType.JSON, ContentType.PARAMETER_IEEE754_COMPATIBLE, "true");
  private static final OData odata = OData.newInstance();

  @Test
  public void instanceAnnotOnEntity() throws Exception {
	  final String entityString = "{"
		        + "\"@context\":\"$metadata#ESAllPrim/$entity\","
		        + "\"@metadataEtag\":\"W/\\\"metadataETag\\\"\","
		        + "\"@com.contoso.display.highlight\":true,"
		        + "\"@com.contoso.PersonalInfo.PhoneNumbers\":"
		        + "[\"(203)555-1718\",\"(203)555-1719\"],"
		        + "\"PropertyInt16\":32767,"
		        + "\"NavPropertyETTwoPrimOne@bind\": \"ETTwoPrim(1)\","
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
  final Entity entity = deserializeWithResultWithConstantV401(
  		new ByteArrayInputStream(entityString.getBytes()), 
  		"ETAllPrim", ContentType.APPLICATION_JSON).getEntity();
    assertNotNull(entity);
    List<Annotation> annotations = entity.getAnnotations();
    assertEquals(2, annotations.size());
    assertEquals("com.contoso.display.highlight", annotations.get(0).getTerm());
    assertTrue((Boolean)annotations.get(0).getValue());
    assertEquals(ValueType.PRIMITIVE, annotations.get(0).getValueType());
    assertEquals("com.contoso.PersonalInfo.PhoneNumbers", annotations.get(1).getTerm());
    assertEquals(ValueType.COLLECTION_PRIMITIVE, annotations.get(1).getValueType());
    assertEquals(2, annotations.get(1).asCollection().size());
    assertEquals(1, entity.getNavigationBindings().size());
    assertEquals("NavPropertyETTwoPrimOne", entity
    		.getNavigationBinding("NavPropertyETTwoPrimOne").getTitle());
  }
  
  @Test
  public void instanceAnnotOnEntityProperty() throws Exception {
	  final String entityString = "{"
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
				+	"\"NavPropertyETTwoPrimMany@odata.navigationLink\":"
				+ "\"ESAllPrim(32767)/NavPropertyETTwoPrimMany\""
				+"}";
    final Entity entity = deserialize(entityString, "ETAllPrim");
    assertNotNull(entity);
    Property property = entity.getProperties().get(1);
    List<Annotation> annotations = property.getAnnotations();
    assertEquals(1, annotations.size());
    assertEquals("com.contoso.display.style", annotations.get(0).getTerm());
    assertEquals(ValueType.COMPLEX, annotations.get(0).getValueType());
    ComplexValue value = annotations.get(0).asComplex();
    assertEquals("#com.contoso.display.styleType", value.getTypeName());
    List<Property> complxProperties = value.getValue();
    assertEquals(2, complxProperties.size());
    assertEquals("title", complxProperties.get(0).getName());
    assertTrue((Boolean)complxProperties.get(0).getValue());
    assertEquals("Order", complxProperties.get(1).getName());
    assertEquals(1, complxProperties.get(1).getValue());
  }
  
  @Test
  public void instanceAnnotOnComplexProperty() throws Exception {
	  final String entityString = "{"
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
	    		+	"}]"
	    		+"}";
    final Entity entity = deserialize(entityString, "ETMixPrimCollComp");
    assertNotNull(entity);
    Property property = entity.getProperties().get(2);
    List<Annotation> annotations = property.getAnnotations();
    assertEquals(1, annotations.size());
    assertEquals("com.contoso.display.style", annotations.get(0).getTerm());
    assertEquals(ValueType.COMPLEX, annotations.get(0).getValueType());
    ComplexValue value = annotations.get(0).asComplex();
    assertEquals("#com.contoso.display.styleType", value.getTypeName());
    List<Property> complxProperties = value.getValue();
    assertEquals(2, complxProperties.size());
    assertEquals("title", complxProperties.get(0).getName());
    assertTrue((Boolean)complxProperties.get(0).getValue());
    assertEquals("Order", complxProperties.get(1).getName());
    assertEquals(ValueType.COLLECTION_COMPLEX, complxProperties.get(1).getValueType());
    assertEquals(2, complxProperties.get(1).asCollection().size());
  }

	@Test
	public void instanceAnnotationForStreamProperty() throws DeserializerException {
		final String entityString = "{"
			+	"\"@odata.context\":\"$metadata#ETWithStream/$entity\","
			+	"\"PropertyInt16@odata.type\":\"#Int16\","
			+	"\"PropertyInt16\":32767,"
			+	"\"PropertyStream@odata.mediaReadLink\":\"http://mockReadLink1\","
			+ "\"PropertyStream@odata.mediaEditLink\":\"http://mockEditLink1\","
			+ "\"PropertyStream@odata.mediaMimeType\":\"image/png\","
			+	"\"PropertyStream@mediaReadLink\":\"http://mockReadLink2\","
			+ "\"PropertyStream@mediaEditLink\":\"http://mockEditLink2\","
			+ "\"PropertyStream@mediaMimeType\":\"image/jpeg\""
			+"}";
		final Entity entity = deserialize(entityString, "ETWithStream");
		assertNotNull(entity);
		Property propertyStream = entity.getProperty("PropertyStream");
		assertNotNull(propertyStream);
		List<Annotation> annotations = propertyStream.getAnnotations();
		assertEquals(6, annotations.size());
		for(Annotation annotation : annotations) {
			if("odata.mediaReadLink".equals(annotation.getTerm())) {
				assertEquals("http://mockReadLink1", annotation.getValue().toString());
			} else if("odata.mediaEditLink".equals(annotation.getTerm())) {
				assertEquals("http://mockEditLink1", annotation.getValue().toString());
			} else if("odata.mediaMimeType".equals(annotation.getTerm())) {
				assertEquals("image/png", annotation.getValue().toString());
			} else if("mediaReadLink".equals(annotation.getTerm())) {
				assertEquals("http://mockReadLink2", annotation.getValue().toString());
			} else if("mediaEditLink".equals(annotation.getTerm())) {
				assertEquals("http://mockEditLink2", annotation.getValue().toString());
			} else if("mediaMimeType".equals(annotation.getTerm())) {
				assertEquals("image/jpeg", annotation.getValue().toString());
			}
		}
	}

  protected static DeserializerResult deserializeWithResultWithConstantV401(final InputStream stream, 
		  final String entityTypeName, final ContentType contentType) 
				  throws DeserializerException {
    final EdmEntityType entityType = edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName));
    List<String> odataVersions = new ArrayList<>();
    odataVersions.add("4.01");
    return odata.createDeserializer(contentType, metadata, odataVersions).entity(stream, entityType);
  }
  
  protected static Entity deserialize(final InputStream stream, final String entityTypeName,
	      final ContentType contentType) throws DeserializerException {
	    return deserializeWithResult(stream, entityTypeName, contentType).getEntity();
	  }

  protected static DeserializerResult deserializeWithResult(final InputStream stream, 
		  final String entityTypeName, final ContentType contentType) 
				  throws DeserializerException {
    final EdmEntityType entityType = edm.getEntityType(new FullQualifiedName(NAMESPACE, entityTypeName));
    return deserializeWithResult(stream, entityType, contentType);
  }

  protected static DeserializerResult deserializeWithResult(final InputStream stream, 
		  final EdmEntityType entityType, final ContentType contentType) 
				  throws DeserializerException {
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
}
