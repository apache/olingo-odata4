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

import org.apache.commons.io.IOUtils;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.ContextURL.Suffix;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.tecsvc.data.DataProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.net.URI;

public class ODataJsonSerializerTest {

  private final Edm edm = OData.newInstance().createEdm(new EdmTechProvider());
  private final EdmEntityContainer entityContainer = edm.getEntityContainer(
      new FullQualifiedName("com.sap.odata.test1", "Container"));
  private final DataProvider data = new DataProvider(edm);
  private ODataSerializer serializer = new ODataJsonSerializer(ODataFormat.JSON);

  @Test
  public void entitySimple() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = serializer.entity(edmEntitySet, entity,
        ContextURL.create().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build());
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
  public void entitySetAllPrim() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESAllPrim");
    EntitySet entitySet = data.readAll(edmEntitySet);
    entitySet.setCount(entitySet.getEntities().size());
    entitySet.setNext(URI.create("/next"));
    InputStream result = serializer.entitySet(edmEntitySet, entitySet, null);
    final String resultString = IOUtils.toString(result);

    Assert.assertTrue(resultString.matches("\\{"
        + "\"@odata\\.context\":\"\\$metadata#ESAllPrim\","
        + "\"@odata\\.count\":3,"
        + "\"value\":\\[.*\\],"
        + "\"@odata\\.nextLink\":\"/next\""
        + "\\}"));

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
        ContextURL.create().serviceRoot(URI.create("http://host/service/"))
            .entitySet(edmEntitySet).suffix(Suffix.ENTITY).build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"http://host/service/$metadata#ESCollAllPrim/$entity\","
        + "\"PropertyInt16\":1,"
        + "\"CollPropertyString\":[\"spiderman@comic.com\",\"spidermaus@comic.com\",\"spidergirl@comic.com\"],"
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
    InputStream result = serializer.entity(edmEntitySet, entity, null);
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
    InputStream result = serializer.entity(edmEntitySet, entity, null);
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{"
        + "\"@odata.context\":\"$metadata#ESMixPrimCollComp/$entity\","
        + "\"PropertyInt16\":32767,"
        + "\"CollPropertyString\":[\"spiderman@comic.com\",\"spidermaus@comic.com\",\"spidergirl@comic.com\"],"
        + "\"PropertyComp\":{\"PropertyInt16\":111,\"PropertyString\":\"TEST A\"},"
        + "\"CollPropertyComp\":["
        + "{\"PropertyInt16\":123,\"PropertyString\":\"TEST 1\"},"
        + "{\"PropertyInt16\":456,\"PropertyString\":\"TEST 2\"},"
        + "{\"PropertyInt16\":789,\"PropertyString\":\"TEST 3\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entityTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final Entity entity = data.readAll(edmEntitySet).getEntities().get(0);
    InputStream result = new ODataJsonSerializer(ODataFormat.JSON_NO_METADATA)
        .entity(edmEntitySet, entity, ContextURL.create().entitySet(edmEntitySet).suffix(Suffix.ENTITY).build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"}";
    Assert.assertEquals(expectedResult, resultString);
  }

  @Test
  public void entitySetTwoPrimNoMetadata() throws Exception {
    final EdmEntitySet edmEntitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EntitySet entitySet = data.readAll(edmEntitySet);
    InputStream result = new ODataJsonSerializer(ODataFormat.JSON_NO_METADATA)
        .entitySet(edmEntitySet, entitySet, ContextURL.create().entitySet(edmEntitySet).build());
    final String resultString = IOUtils.toString(result);
    final String expectedResult = "{\"value\":["
        + "{\"PropertyInt16\":32766,\"PropertyString\":\"Test String1\"},"
        + "{\"PropertyInt16\":-365,\"PropertyString\":\"Test String2\"},"
        + "{\"PropertyInt16\":-32766,\"PropertyString\":\"Test String3\"},"
        + "{\"PropertyInt16\":32767,\"PropertyString\":\"Test String4\"}]}";
    Assert.assertEquals(expectedResult, resultString);
  }
}
