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
package org.apache.olingo.server.tecsvc.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.apache.olingo.commons.api.data.ComplexValue;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DataProviderTest {

  private final OData oData = OData.newInstance();
  private final Edm edm =
      oData.createServiceMetadata(new EdmTechProvider(), Collections.<EdmxReference> emptyList())
      .getEdm();
  private final EdmEntityContainer entityContainer = edm.getEntityContainer();

  private final EdmEntitySet esAllPrim = entityContainer.getEntitySet("ESAllPrim");
  private final EdmEntitySet esAllKey = entityContainer.getEntitySet("ESAllKey");
  private final EdmEntitySet esCompAllPrim = entityContainer.getEntitySet("ESCompAllPrim");
  private final EdmEntitySet esCollAllPrim = entityContainer.getEntitySet("ESCollAllPrim");
  private final EdmEntitySet esMixPrimCollComp = entityContainer.getEntitySet("ESMixPrimCollComp");
  private final EdmEntitySet esMedia = entityContainer.getEntitySet("ESMedia");

  @Before
  public void setup() {
    DataProvider.setDefaultTimeZone("GMT");
  }
  
  @After
  public void teardown() {
    DataProvider.setDefaultTimeZone(TimeZone.getDefault().getID());
  }
  
  @Test
  public void esAllPrimEntity() throws Exception {
    final DataProvider dataProvider = new DataProvider(oData, edm);
    final Entity entity = dataProvider.readAll(esAllPrim).getEntities().get(2);
    Assert.assertEquals(16, entity.getProperties().size());

    Assert.assertEquals(entity,
        dataProvider.read(esAllPrim, Arrays.asList(mockParameter("PropertyInt16", "-0"))));
  }

  @Test
  public void esAllKeyEntity() throws Exception {
    final DataProvider dataProvider = new DataProvider(oData, edm);
    final Entity entity = dataProvider.readAll(esAllKey).getEntities().get(0);
    Assert.assertEquals(13, entity.getProperties().size());

    Assert.assertEquals(entity, dataProvider.read(esAllKey, Arrays.asList(
        mockParameter("PropertyBoolean", "true"),
        mockParameter("PropertyByte", "255"),
        mockParameter("PropertyDate", "2012-12-03"),
        mockParameter("PropertyDateTimeOffset", "2012-12-03T07:16:23Z"),
        mockParameter("PropertyDecimal", "34"),
        mockParameter("PropertyDuration", "duration'PT6S'"),
        mockParameter("PropertyGuid", "01234567-89AB-CDEF-0123-456789ABCDEF"),
        mockParameter("PropertyInt16", "32767"),
        mockParameter("PropertyInt32", "2147483647"),
        mockParameter("PropertyInt64", "9223372036854775807"),
        mockParameter("PropertySByte", "127"),
        mockParameter("PropertyString", "'First'"),
        mockParameter("PropertyTimeOfDay", "02:48:21"))));
    
  }

  @Test
  public void esAllPrim() throws Exception {
    final DataProvider data = new DataProvider(oData, edm);
    EntityCollection outSet = data.readAll(esAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());

    Entity first = outSet.getEntities().get(0);
    Assert.assertEquals(16, first.getProperties().size());
    Assert.assertEquals(2, first.getNavigationLinks().size());
    final EntityCollection target = first.getNavigationLink("NavPropertyETTwoPrimMany").getInlineEntitySet();
    Assert.assertNotNull(target);
    Assert.assertEquals(1, target.getEntities().size());
    Assert.assertEquals(data.readAll(entityContainer.getEntitySet("ESTwoPrim")).getEntities().get(1),
        target.getEntities().get(0));

    Assert.assertEquals(16, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(16, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esCollAllPrim() throws Exception {
    final DataProvider dataProvider = new DataProvider(oData, edm);
    EntityCollection outSet = dataProvider.readAll(esCollAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());
    Assert.assertEquals(17, outSet.getEntities().get(0).getProperties().size());
    Property list = outSet.getEntities().get(0).getProperties().get(1);
    Assert.assertTrue(list.isCollection());
    Assert.assertEquals(3, list.asCollection().size());
    Assert.assertEquals(17, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(17, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esCompAllPrim() throws Exception {
    final DataProvider dataProvider = new DataProvider(oData, edm);
    
    EntityCollection outSet = dataProvider.readAll(esCompAllPrim);

    Assert.assertEquals(4, outSet.getEntities().size());
    Assert.assertEquals(2, outSet.getEntities().get(0).getProperties().size());
    Property complex = outSet.getEntities().get(0).getProperties().get(1);
    Assert.assertTrue(complex.isComplex());
    Assert.assertEquals(16, complex.asComplex().getValue().size());
    Assert.assertEquals(2, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(2, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esMixPrimCollComp() throws Exception {
    final DataProvider dataProvider = new DataProvider(oData, edm);
    
    EntityCollection outSet = dataProvider.readAll(esMixPrimCollComp);

    Assert.assertEquals(3, outSet.getEntities().size());
    Assert.assertEquals(4, outSet.getEntities().get(0).getProperties().size());
    Property complex = outSet.getEntities().get(0).getProperties().get(2);
    Assert.assertTrue(complex.isComplex());
    Assert.assertEquals(2, complex.asComplex().getValue().size());
    Property complexCollection = outSet.getEntities().get(0).getProperties().get(3);
    Assert.assertTrue(complexCollection.isCollection());
    List<?> linkedComplexValues = complexCollection.asCollection();
    Assert.assertEquals(3, linkedComplexValues.size());
    ComplexValue linkedComplexValue = (ComplexValue) linkedComplexValues.get(0);
    Assert.assertEquals(2, linkedComplexValue.getValue().size());
    Property lcProp = linkedComplexValue.getValue().get(0);
    Assert.assertFalse(lcProp.isCollection());
    Assert.assertEquals((short) 123, lcProp.getValue());
    //
    Assert.assertEquals(4, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(4, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esMedia() throws Exception {
    DataProvider dataProvider = new DataProvider(oData, edm);

    Entity entity = dataProvider.read(esMedia, Arrays.asList(mockParameter("PropertyInt16", "3")));
    Assert.assertNotNull(dataProvider.readMedia(entity));
    dataProvider.delete(esMedia, entity);
    Assert.assertEquals(3, dataProvider.readAll(esMedia).getEntities().size());
    entity = dataProvider.create(esMedia);
    Assert.assertEquals((short) 3, entity.getProperty("PropertyInt16").getValue());
    dataProvider.setMedia(entity, new byte[] { 1, 2, 3, 4 }, "x/y");
    Assert.assertArrayEquals(new byte[] { 1, 2, 3, 4 }, dataProvider.readMedia(entity));
    Assert.assertEquals("x/y", entity.getMediaContentType());
  }

  private static UriParameter mockParameter(final String name, final String text) {
    UriParameter parameter = Mockito.mock(UriParameter.class);
    Mockito.when(parameter.getName()).thenReturn(name);
    Mockito.when(parameter.getText()).thenReturn(text);
    return parameter;
  }
}
