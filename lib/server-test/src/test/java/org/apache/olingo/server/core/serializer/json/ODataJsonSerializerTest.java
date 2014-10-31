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
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.core.data.PropertyImpl;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.ODataSerializerOptions;
import org.apache.olingo.server.api.serializer.SerializerException;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.ExpandSelectMock;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ODataJsonSerializerTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
          new EdmTechProvider(), Collections.<EdmxReference>emptyList()).getEdm();
  private static final EdmEntityContainer entityContainer = edm.getEntityContainer(
      new FullQualifiedName("olingo.odata.test1", "Container"));
  private final DataProvider data = new DataProvider();
  private final ODataSerializer serializer = new ODataJsonSerializer(ODataFormat.JSON);

  @Test
  public void entitySimple() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESAllPrim/$entity\","
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
  public void entityAllPrimAllNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().retainAll(Arrays.asList(entity.getProperties().get(0)));
    final String resultString = IOUtils.toString(serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()));
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESAllPrim/$entity\","
        + "\"PropertyInt16\":32767,"
        + "\"PropertyString\":null,\"PropertyBoolean\":null,"
        + "\"PropertyByte\":null,\"PropertySByte\":null,"
        + "\"PropertyInt32\":null,\"PropertyInt64\":null,"
        + "\"PropertySingle\":null,\"PropertyDouble\":null,\"PropertyDecimal\":null,"
        + "\"PropertyBinary\":null,"
        + "\"PropertyDate\":null,\"PropertyDateTimeOffset\":null,\"PropertyDuration\":null,"
        + "\"PropertyGuid\":null,\"PropertyTimeOfDay\":null}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test(expected = SerializerException.class)
  public void entityAllPrimKeyNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().clear();
    serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
  }

  @Test
  public void entityWrongData() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().get(0).setValue(ValueType.PRIMITIVE, false);
    try {
      serializer.entity(edmEntitySet, entity,
          ODataSerializerOptions.with()
              .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
              .build());
      Assert.fail("Expected exception not thrown!");
    } catch (final SerializerException e) {
      Assert.assertEquals(SerializerException.MessageKeys.WRONG_PROPERTY_VALUE, e.getMessageKey());
       final String message = e.getLocalizedMessage();
       Assert.assertThat(message, CoreMatchers.containsString("PropertyInt16"));
       Assert.assertThat(message, CoreMatchers.containsString("false"));
    }
  }

  @Test
  public void entitySetAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    EntitySet entitySet = data.readAll(edmEntitySet);
    entitySet.setCount(entitySet.getEntities().size());
    entitySet.setNext(URI.create("/next"));
    CountOption countOption = Mockito.mock(CountOption.class);
    Mockito.when(countOption.getValue()).thenReturn(true);
    InputStream result = serializer.entitySet(edmEntitySet, entitySet,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).build())
            .count(countOption)
            .build());
    final String resultString = IOUtils.toString(result);

    Assert.assertThat(resultString, CoreMatchers.startsWith("{"
        + "\"@odata.context\":\"$metadata#ESAllPrim\","
        + "\"@odata.count\":3,\"value\":["));
    Assert.assertThat(resultString, CoreMatchers.endsWith("],"
        + "\"@odata.nextLink\":\"/next\"}"));

    int count = 0;
    int index = -1;
    while ((index = resultString.indexOf("PropertyInt16\":", ++index)) > 0) {
      count++;
    }
    Assert.assertEquals(3, count);
  }

  @Test
  public void entityCollAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().serviceRoot(URI.create("http://host/service/"))
                .entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"http://host/service/$metadata#ESCollAllPrim/$entity\","
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
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityCompAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESCompAllPrim/$entity\","
        + "\"PropertyInt16\":32767,"
        + "\"PropertyComp\":{"
        + "\"PropertyString\":\"First Resource - first\","
        + "\"PropertyBinary\":\"ASNFZ4mrze8=\","
        + "\"PropertyBoolean\":true,"
        + "\"PropertyByte\":255,"
        + "\"PropertyDate\":\"2012-10-03\","
        + "\"PropertyDateTimeOffset\":\"2012-10-03T07:16:23.1234567Z\","
        + "\"PropertyDecimal\":34.27,"
        + "\"PropertySingle\":1.79E20,"
        + "\"PropertyDouble\":-1.79E19,"
        + "\"PropertyDuration\":\"PT6S\","
        + "\"PropertyGuid\":\"01234567-89ab-cdef-0123-456789abcdef\","
        + "\"PropertyInt16\":32767,"
        + "\"PropertyInt32\":2147483647,"
        + "\"PropertyInt64\":9223372036854775807,"
        + "\"PropertySByte\":127,"
        + "\"PropertyTimeOfDay\":\"01:00:01\""
        + "}}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityMixPrimCollComp() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESMixPrimCollComp/$entity\","
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":"
        + "[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityMixPrimCollCompAllNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.getProperties().retainAll(Arrays.asList(entity.getProperties().get(0)));
    final String resultString = IOUtils.toString(serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()));
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESMixPrimCollComp/$entity\","
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":null,\"PropertyComp\":null,\"CollPropertyComp\":null}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = new ODataJsonSerializer(ODataFormat.JSON_NO_METADATA)
        .entity(edmEntitySet, entity, null);
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entitySetTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EntitySet entitySet = data.readAll(edmEntitySet);
    InputStream result = new ODataJsonSerializer(ODataFormat.JSON_NO_METADATA)
        .entitySet(edmEntitySet, entitySet,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet).build()).build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"value\":["
        + "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"},"
        + "{\"PropertyInt16\":-365,\"PropertyString\":\"Test String2\"},"
        + "{\"PropertyInt16\":-32766,\"PropertyString\":null},"
        + "{\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityMedia() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMedia");
    Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    entity.setMediaETag("theMediaETag");
    final String resultString = IOUtils.toString(serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .build()));
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESMedia/$entity\","
        + "\"@odata.mediaEtag\":\"theMediaETag\",\"@odata.mediaContentType\":\"image/png\","
        + "\"PropertyInt16\":1}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entitySetMedia() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMedia");
    final EntitySet entitySet = data.readAll(edmEntitySet);
    final String resultString = IOUtils.toString(serializer.entitySet(edmEntitySet, entitySet,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).build()).build()));
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESMedia\",\"value\":["
        + "{\"@odata.mediaContentType\":\"image/png\",\"PropertyInt16\":1},"
        + "{\"@odata.mediaContentType\":\"image/bmp\",\"PropertyInt16\":2},"
        + "{\"@odata.mediaContentType\":\"image/jpeg\",\"PropertyInt16\":3},"
        + "{\"@odata.mediaContentType\":\"foo\",\"PropertyInt16\":4}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void select() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyDate");
    final SelectItem selectItem2 = ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyBoolean");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem1, selectItem2, selectItem2));
    InputStream result = serializer
        .entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, null, select))
                    .suffix(Suffix.ENTITY).build())
                .select(select)
                .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESAllPrim(PropertyBoolean,PropertyDate)/$entity\","
        + "\"PropertyBoolean\":true,\"PropertyDate\":\"2012-12-03\"}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void selectAll() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyString");
    SelectItem selectItem2 = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem2.isStar()).thenReturn(true);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem1, selectItem2));
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .select(select)
            .build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\","
        + "\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void selectComplex() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompComp");
    final EntitySet entitySet = data.readAll(edmEntitySet);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp", "PropertyString")));
    InputStream result = serializer
        .entitySet(edmEntitySet, entitySet,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, null, select))
                    .build())
                .select(select)
                .build());
    final String resultString = IOUtils.toString(result);
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESCompComp(PropertyComp/PropertyComp/PropertyString)\","
            + "\"value\":["
            + "{\"PropertyComp\":{\"PropertyComp\":{\"PropertyString\":\"String 1\"}}},"
            + "{\"PropertyComp\":{\"PropertyComp\":{\"PropertyString\":\"String 2\"}}}]}",
        resultString);
  }

  @Test
  public void selectComplexTwice() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCompComp");
    final EntitySet entitySet = data.readAll(edmEntitySet);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp", "PropertyString"),
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyComp", "PropertyComp")));
    final String resultString = IOUtils.toString(serializer
        .entitySet(edmEntitySet, entitySet,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, null, select))
                    .build())
                .select(select)
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESCompComp(PropertyComp/PropertyComp)\","
            + "\"value\":["
            + "{\"PropertyComp\":{\"PropertyComp\":{\"PropertyInt16\":123,\"PropertyString\":\"String 1\"}}},"
            + "{\"PropertyComp\":{\"PropertyComp\":{\"PropertyInt16\":987,\"PropertyString\":\"String 2\"}}}]}",
        resultString);
  }

  @Test
  public void expand() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(3);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimOne")));
    InputStream result = serializer.entity(edmEntitySet, entity,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build())
            .expand(expand)
            .build());
    final String resultString = IOUtils.toString(result);
    Assert.assertEquals("{\"@odata.context\":\"$metadata#ESTwoPrim/$entity\","
            + "\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\","
            + "\"NavPropertyETAllPrimOne\":{"
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
            + "\"PropertyTimeOfDay\":\"03:26:05\"}}",
        resultString);
  }

  @Test
  public void expandSelect() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(3);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(entityContainer.getEntitySet("ESAllPrim"), "PropertyDate")));
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getSelectOption()).thenReturn(select);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final String resultString = IOUtils.toString(serializer
        .entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESTwoPrim(NavPropertyETAllPrimOne(PropertyDate))/$entity\","
            + "\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\","
            + "\"NavPropertyETAllPrimOne\":{\"PropertyDate\":\"2012-12-03\"}}",
        resultString);
  }

  @Test
  public void expandAll() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    final ExpandItem expandItem = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETTwoPrimOne");
    ExpandItem expandItemAll = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemAll.isStar()).thenReturn(true);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        expandItem, expandItem, expandItemAll));
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertySByte")));
    final String resultString = IOUtils.toString(serializer
        .entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .select(select)
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESAllPrim(PropertySByte)/$entity\","
            + "\"PropertySByte\":127,"
            + "\"NavPropertyETTwoPrimOne\":{\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\"},"
            + "\"NavPropertyETTwoPrimMany\":[{\"PropertyInt16\":-365,\"PropertyString\":\"Test String2\"}]}",
        resultString);
  }

  @Test
  public void expandNoData() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(1);
    ExpandItem expandItemAll = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemAll.isStar()).thenReturn(true);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemAll));
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(edmEntitySet, "PropertyTimeOfDay")));
    final String resultString = IOUtils.toString(serializer
        .entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .select(select)
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESAllPrim(PropertyTimeOfDay)/$entity\","
            + "\"PropertyTimeOfDay\":\"23:49:14\","
            + "\"NavPropertyETTwoPrimOne\":null,\"NavPropertyETTwoPrimMany\":[]}",
        resultString);
  }

  @Test
  public void expandTwoLevels() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(1);
    ExpandItem expandItemSecond = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemSecond.isStar()).thenReturn(true);
    final ExpandOption expandInner = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemSecond));
    ExpandItem expandItemFirst = ExpandSelectMock.mockExpandItem(edmEntitySet, "NavPropertyETAllPrimMany");
    Mockito.when(expandItemFirst.getExpandOption()).thenReturn(expandInner);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(innerEntitySet, "PropertyInt32")));
    Mockito.when(expandItemFirst.getSelectOption()).thenReturn(select);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemFirst));
    final String resultString = IOUtils.toString(serializer
        .entity(edmEntitySet, entity,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with().entitySet(edmEntitySet)
                    .selectList(serializer.buildContextURLSelectList(edmEntitySet, expand, select))
                    .suffix(Suffix.ENTITY).build())
                .expand(expand)
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESTwoPrim(NavPropertyETAllPrimMany(PropertyInt32))/$entity\","
            + "\"PropertyInt16\":-365,\"PropertyString\":\"Test String2\","
            + "\"NavPropertyETAllPrimMany\":["
            + "{\"PropertyInt32\":-2147483648,\"NavPropertyETTwoPrimOne\":null,\"NavPropertyETTwoPrimMany\":[]},"
            + "{\"PropertyInt32\":0,\"NavPropertyETTwoPrimOne\":null,"
            + "\"NavPropertyETTwoPrimMany\":["
            + "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"},"
            + "{\"PropertyInt16\":-32766,\"PropertyString\":null},"
            + "{\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\"}]}]}",
        resultString);
  }

  @Test
  public void primitiveProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyString");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty(edmProperty.getName());
    final String resultString = IOUtils.toString(serializer
        .entityProperty(edmProperty, property,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("32767").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESAllPrim(32767)/PropertyString\","
            + "\"value\":\"First Resource - positive values\"}",
        resultString);
  }

  @Test(expected = SerializerException.class)
  public void primitivePropertyNull() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyString");
    final Property property = new PropertyImpl("Edm.String", edmProperty.getName(), ValueType.PRIMITIVE, null);
    serializer.entityProperty(edmProperty, property,
        ODataSerializerOptions.with()
            .contextURL(ContextURL.with()
                .entitySet(edmEntitySet).keyPath("4242").navOrPropertyPath(edmProperty.getName())
                .build())
            .build());
  }

  @Test
  public void primitiveCollectionProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESCollAllPrim");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("CollPropertyString");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty(edmProperty.getName());

    final String resultString = IOUtils.toString(serializer
        .entityProperty(edmProperty, property,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("1").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESCollAllPrim(1)/CollPropertyString\","
            + "\"value\":[\"Employee1@company.example\",\"Employee2@company.example\",\"Employee3@company.example\"]}",
        resultString);
  }

  @Test
  public void complexProperty() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESMixPrimCollComp");
    final EdmProperty edmProperty = (EdmProperty) edmEntitySet.getEntityType().getProperty("PropertyComp");
    final Property property = data.readAll(edmEntitySet).getEntities().get(0).getProperty("PropertyComp");

    final String resultString = IOUtils.toString(serializer
        .entityProperty(edmProperty, property,
            ODataSerializerOptions.with()
                .contextURL(ContextURL.with()
                    .entitySet(edmEntitySet).keyPath("32767").navOrPropertyPath(edmProperty.getName())
                    .build())
                .build()));
    Assert.assertEquals("{"
            + "\"@odata.context\":\"$metadata#ESMixPrimCollComp(32767)/PropertyComp\","
            + "\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"}",
        resultString);
  }
}
