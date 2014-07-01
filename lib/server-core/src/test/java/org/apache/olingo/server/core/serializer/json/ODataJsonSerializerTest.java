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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.commons.api.edm.constants.EdmTypeKind;
import org.apache.olingo.commons.core.data.EntityImpl;
import org.apache.olingo.commons.core.data.EntitySetImpl;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ODataJsonSerializerTest {

  private static final String ETAllPrim = "ETAllPrim";
  private static final String ETCompAllPrim = "ETCompAllPrim";
  private static final String CTAllPrim = "CTAllPrim";
  private static final String CTAllPrim_Type = "com.sap.odata.test1.CTAllPrim";

  interface TechProperty {
    String getName();
    String getTypeName();
    EdmPrimitiveTypeKind getType();
  }

  public static class TecComplexProperty implements TechProperty {

    final String typeName;
    final String name;
    final List<EdmProperty> properties;

    public TecComplexProperty(String typeName, String name, List<EdmProperty> propertyNames) {
      this.typeName = typeName;
      this.name = name;
      this.properties = new ArrayList<EdmProperty>(propertyNames);
    }
    @Override
    public String getName() {
      return name;
    }
    @Override
    public String getTypeName() {
      return typeName;
    }
    @Override
    public EdmPrimitiveTypeKind getType() {
      return null;
    }
    public List<EdmProperty> getProperties() {
      return properties;
    }
  }

  enum TecSimpleProperty implements TechProperty {
    Int16("PropertyInt16", EdmPrimitiveTypeKind.Int16),
    String("PropertyString", EdmPrimitiveTypeKind.String),
    Boolean("PropertyBoolean", EdmPrimitiveTypeKind.Boolean),
    Byte("PropertyByte", EdmPrimitiveTypeKind.Byte),
    SByte("PropertySByte", EdmPrimitiveTypeKind.SByte),
    Int32("PropertyInt32", EdmPrimitiveTypeKind.Int32),
    Int64("PropertyInt64", EdmPrimitiveTypeKind.Int64),
    Single("PropertySingle", EdmPrimitiveTypeKind.Single),
    Double("PropertyDouble", EdmPrimitiveTypeKind.Double),
    Decimal("PropertyDecimal", EdmPrimitiveTypeKind.Decimal),
    Binary("PropertyBinary", EdmPrimitiveTypeKind.Binary),
    Date("PropertyDate", EdmPrimitiveTypeKind.Date),
    DateTimeOffset("PropertyDateTimeOffset", EdmPrimitiveTypeKind.DateTimeOffset),
    Duration("PropertyDuration", EdmPrimitiveTypeKind.Duration),
    Guid("PropertyGuid", EdmPrimitiveTypeKind.Guid),
    TimeOfDay("PropertyTimeOfDay", EdmPrimitiveTypeKind.TimeOfDay);
    //  <NavigationProperty Name="NavPropertyETTwoPrimOne" Type="test1.ETTwoPrim" Nullable="false"/>
//    NavETTwoPrimOne = "NavPropertyETTwoPrimOne", EdmPrimitiveTypeKind.),
    //  <NavigationProperty Name="NavPropertyETTwoPrimMany" Type="Collection(test1.ETTwoPrim)" Nullable="false"/>
//    NavETTwoPrimMany("NavPropertyETTwoPrimMany", EdmCom.);

    final String name;
    final EdmPrimitiveTypeKind type;

    TecSimpleProperty(String name, EdmPrimitiveTypeKind type) {
      this.name = name;
      this.type = type;
    }

    @Override
    public String getTypeName() {
      return type.name();
    }

    @Override
    public EdmPrimitiveTypeKind getType() {
      return type;
    }
    @Override
    public String getName() {
      return name;
    }
  }

  private ContextURL contextUrl;
  private EdmEntitySet edmESAllPrim;
  private EdmEntityType edmETAllPrim;
  private EdmEntityType edmETCompAllPrim;

  private ODataJsonSerializer serializer = new ODataJsonSerializer();

  @Before
  public void prepare() throws Exception {
    contextUrl = ContextURL.getInstance(new URI("http://localhost:8080/test.svc"));

    edmETAllPrim = Mockito.mock(EdmEntityType.class);
    Mockito.when(edmETAllPrim.getName()).thenReturn(ETAllPrim);
    List<EdmProperty> properties = Arrays.asList(
        mockProperty(TecSimpleProperty.Int16, false),
        mockProperty(TecSimpleProperty.String),
        mockProperty(TecSimpleProperty.Boolean),
        mockProperty(TecSimpleProperty.Byte),
        mockProperty(TecSimpleProperty.SByte),
        mockProperty(TecSimpleProperty.Int32),
        mockProperty(TecSimpleProperty.Int64),
        mockProperty(TecSimpleProperty.Single),
        mockProperty(TecSimpleProperty.Double),
        mockProperty(TecSimpleProperty.Decimal),
        mockProperty(TecSimpleProperty.Binary),
        mockProperty(TecSimpleProperty.Date),
        mockProperty(TecSimpleProperty.DateTimeOffset),
        mockProperty(TecSimpleProperty.Duration),
        mockProperty(TecSimpleProperty.Guid),
        mockProperty(TecSimpleProperty.TimeOfDay)
//        mockProperty(NavPropertyETTwoPrimOne, false),
//        mockProperty(NavPropertyETTwoPrimMany, false)
    );
    List<String> propertyNames = new ArrayList<String>();

    for (EdmProperty property : properties) {
      propertyNames.add(property.getName());
      Mockito.when(edmETAllPrim.getProperty(property.getName())).thenReturn(property);
    }
    Mockito.when(edmETAllPrim.getPropertyNames()).thenReturn(propertyNames);

    // Entity Set All Primitive
    edmESAllPrim = Mockito.mock(EdmEntitySet.class);
    Mockito.when(edmESAllPrim.getName()).thenReturn("ESAllPrim");
    Mockito.when(edmESAllPrim.getEntityType()).thenReturn(edmETAllPrim);

    // Entity Type Complex All Primitive
    edmETCompAllPrim = Mockito.mock(EdmEntityType.class);
    Mockito.when(edmETCompAllPrim.getName()).thenReturn(ETCompAllPrim);
    List<EdmProperty> capProperties = Arrays.asList(
        mockProperty(TecSimpleProperty.Int16, false),
        mockProperty(new TecComplexProperty(CTAllPrim_Type, CTAllPrim, properties), false)
    );
    List<String> capPropertyNames = new ArrayList<String>();

    for (EdmProperty property : capProperties) {
      capPropertyNames.add(property.getName());
      Mockito.when(edmETCompAllPrim.getProperty(property.getName())).thenReturn(property);
    }
    Mockito.when(edmETCompAllPrim.getPropertyNames()).thenReturn(capPropertyNames);
  }

  private EdmProperty mockProperty(TechProperty name) {
    return mockProperty(name, true);
  }

  private EdmProperty mockProperty(TechProperty tecProperty, boolean nullable) {
    EdmProperty edmElement = Mockito.mock(EdmProperty.class);
    Mockito.when(edmElement.getName()).thenReturn(tecProperty.getName());
    if (tecProperty instanceof TecComplexProperty) {
      TecComplexProperty complexProperty = (TecComplexProperty) tecProperty;
      Mockito.when(edmElement.isPrimitive()).thenReturn(false);
      EdmComplexType type = Mockito.mock(EdmComplexType.class);
      Mockito.when(type.getKind()).thenReturn(EdmTypeKind.COMPLEX);
      Mockito.when(type.getName()).thenReturn(tecProperty.getTypeName());
      Mockito.when(edmElement.getType()).thenReturn(type);

      List<String> propertyNames = new ArrayList<String>();
      List<EdmProperty> properties = complexProperty.getProperties();
      for (EdmProperty property : properties) {
        propertyNames.add(property.getName());
        Mockito.when(type.getProperty(property.getName())).thenReturn(property);
      }
      Mockito.when(type.getPropertyNames()).thenReturn(propertyNames);
    } else {
      Mockito.when(edmElement.isPrimitive()).thenReturn(true);
      // TODO: set default values
      Mockito.when(edmElement.getMaxLength()).thenReturn(40);
      Mockito.when(edmElement.getPrecision()).thenReturn(10);
      Mockito.when(edmElement.getScale()).thenReturn(10);
      Mockito.when(edmElement.getType()).thenReturn(EdmPrimitiveTypeFactory.getInstance(tecProperty.getType()));
    }
    Mockito.when(edmElement.isNullable()).thenReturn(nullable);
    return edmElement;
  }

  private PropertyImpl createProperty(TechProperty property, ValueType vType, Object value) {
    return new PropertyImpl(property.getTypeName(), property.getName(), vType, value);
  }

  private PropertyImpl createProperty(String type, TecSimpleProperty property, ValueType vType, Object value) {
    return new PropertyImpl(type, property.name, vType, value);
  }

  @Test
  public void entitySimple() throws Exception {
    Entity entity = createETAllPrim();

    InputStream result = serializer.entity(edmETAllPrim, entity, contextUrl);
    String resultString = streamToString(result);
    String expectedResult = "{" +
        "\"@odata.context\":\"http://localhost:8080/test.svc\"," +
        "\"PropertyInt16\":4711," +
        "\"PropertyString\":\"StringValue\"," +
        "\"PropertyBoolean\":true," +
        "\"PropertyByte\":19," +
        "\"PropertySByte\":1," +
        "\"PropertyInt32\":2147483647," +
        "\"PropertyInt64\":9223372036854775807," +
        "\"PropertySingle\":47.11," +
        "\"PropertyDouble\":4.711," +
        "\"PropertyDecimal\":4711.1174," +
        "\"PropertyBinary\":\"BAcBAQ==\"," +
        "\"PropertyDate\":\"2014-03-19\"," +
        "\"PropertyDateTimeOffset\":\"2014-03-19T10:12:00+01:00\"," +
        "\"PropertyDuration\":\"P16148383DT8H0S\"," +
        "\"PropertyGuid\":\"0000aaaa-00bb-00cc-00dd-000000ffffff\"," +
        "\"PropertyTimeOfDay\":\"10:12:00\"" +
        "}";
    Assert.assertEquals(expectedResult, resultString);
  }


  @Test
  public void entitySetETAllPrim() throws Exception {
    EdmEntitySet edmEntitySet = edmESAllPrim;
    EntitySetImpl entitySet = new EntitySetImpl();
    for (int i = 0; i < 100; i++) {
      entitySet.getEntities().add(createETAllPrim(i));
    }
    entitySet.setCount(entitySet.getEntities().size());
    entitySet.setNext(URI.create(contextUrl.getURI().toASCIIString() + "/next"));

    InputStream result = serializer.entitySet(edmEntitySet, entitySet, contextUrl);
    String resultString = streamToString(result);

    Assert.assertTrue(resultString.matches("\\{" +
        "\"@odata\\.context\":\"http://localhost:8080/test.svc\"," +
        "\"@odata\\.count\":100," +
        "\"value\":\\[.*\\]," +
        "\"@odata\\.nextLink\":\"http://localhost:8080/test.svc/next\"" +
        "\\}"));

    Matcher matcher = Pattern.compile("(\\{[a-z0-9:\\=\"\\-,\\.\\+]*\\})",
        Pattern.CASE_INSENSITIVE).matcher(resultString);
    int count = 0;
    while(matcher.find()) {
      Assert.assertTrue(matcher.group().contains("PropertyInt16\":" + count++));
    }
    Assert.assertEquals(100, count);
  }

  @Test
  public void entityETCompAllPrim() throws Exception {
    Entity complexCtAllPrim = createETAllPrim();

    Entity entity = new EntityImpl();
    entity.addProperty(new PropertyImpl("Edm.Int16", TecSimpleProperty.Int16.name, ValueType.PRIMITIVE, 4711));
    entity.addProperty(createProperty(
            new TecComplexProperty(CTAllPrim_Type, CTAllPrim, Collections.<EdmProperty>emptyList()),
            ValueType.COMPLEX, complexCtAllPrim.getProperties()));

    InputStream result = serializer.entity(edmETCompAllPrim, entity, contextUrl);
    String resultString = streamToString(result);
    String expectedResult = "{" +
        "\"@odata.context\":\"http://localhost:8080/test.svc\"," +
        "\"PropertyInt16\":4711," +
        "\"CTAllPrim\":{" +
        "\"PropertyInt16\":4711," +
        "\"PropertyString\":\"StringValue\"," +
        "\"PropertyBoolean\":true," +
        "\"PropertyByte\":19," +
        "\"PropertySByte\":1," +
        "\"PropertyInt32\":2147483647," +
        "\"PropertyInt64\":9223372036854775807," +
        "\"PropertySingle\":47.11," +
        "\"PropertyDouble\":4.711," +
        "\"PropertyDecimal\":4711.1174," +
        "\"PropertyBinary\":\"BAcBAQ==\"," +
        "\"PropertyDate\":\"2014-03-19\"," +
        "\"PropertyDateTimeOffset\":\"2014-03-19T10:12:00+01:00\"," +
        "\"PropertyDuration\":\"P16148383DT8H0S\"," +
        "\"PropertyGuid\":\"0000aaaa-00bb-00cc-00dd-000000ffffff\"," +
        "\"PropertyTimeOfDay\":\"10:12:00\"" +
        "}}";
    Assert.assertEquals(expectedResult, resultString);
  }

  private Entity createETAllPrim() {
    return createETAllPrim(4711);
  }

  private Entity createETAllPrim(int id) {
    Entity entity = new EntityImpl();
    Calendar date = Calendar.getInstance();
    date.set(2014, Calendar.MARCH, 19, 10, 12, 0);
    date.set(Calendar.MILLISECOND, 0);
    entity.addProperty(createProperty("Edm.Int16", TecSimpleProperty.Int16, ValueType.PRIMITIVE, id));
    entity.addProperty(createProperty("Edm.String", TecSimpleProperty.String, ValueType.PRIMITIVE, "StringValue"));
    entity.addProperty(createProperty("Edm.Boolean", TecSimpleProperty.Boolean, ValueType.PRIMITIVE, Boolean.TRUE));
    entity.addProperty(createProperty("Edm.Byte", TecSimpleProperty.Byte, ValueType.PRIMITIVE, Byte.valueOf("19")));
    entity.addProperty(createProperty("Edm.SByte", TecSimpleProperty.SByte, ValueType.PRIMITIVE, Short.valueOf("1")));
    entity.addProperty(createProperty("Edm.Int32", TecSimpleProperty.Int32, ValueType.PRIMITIVE, Integer.MAX_VALUE));
    entity.addProperty(createProperty("Edm.Int64", TecSimpleProperty.Int64, ValueType.PRIMITIVE, Long.MAX_VALUE));
    entity.addProperty(createProperty("Edm.Single", TecSimpleProperty.Single, ValueType.PRIMITIVE, 47.11));
    entity.addProperty(createProperty("Edm.Double", TecSimpleProperty.Double, ValueType.PRIMITIVE, 4.711));
    entity.addProperty(createProperty("Edm.Decimal", TecSimpleProperty.Decimal, ValueType.PRIMITIVE, 4711.1174));
    entity.addProperty(createProperty("Edm.Binary", TecSimpleProperty.Binary, ValueType.PRIMITIVE,
        new byte[]{0x04, 0x07, 0x01, 0x01}));
    entity.addProperty(createProperty("Edm.Date", TecSimpleProperty.Date, ValueType.PRIMITIVE, date));
    entity.addProperty(createProperty("Edm.DateTimeOffset", TecSimpleProperty.DateTimeOffset, ValueType.PRIMITIVE,
        date.getTime()));
    entity.addProperty(createProperty("Edm.Duration", TecSimpleProperty.Duration, ValueType.PRIMITIVE,
        date.getTimeInMillis()));
    entity.addProperty(createProperty("Edm.Guid", TecSimpleProperty.Guid, ValueType.PRIMITIVE,
        UUID.fromString("AAAA-BB-CC-DD-FFFFFF")));
    entity.addProperty(createProperty("Edm.TimeOfDay", TecSimpleProperty.TimeOfDay, ValueType.PRIMITIVE, date));
    return entity;
  }

  private String streamToString(InputStream result) throws IOException {
    byte[] buffer = new byte[8192];
    StringBuilder sb = new StringBuilder();

    int count = result.read(buffer);
    while (count >= 0) {
      sb.append(new String(buffer, 0, count, "UTF-8"));
      count = result.read(buffer);
    }

    return sb.toString();
  }
}
