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
import java.util.List;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DataProviderTest {

  private final Edm edm = OData.newInstance().createEdm(new EdmTechProvider());
  private final EdmEntityContainer entityContainer = edm.getEntityContainer(
          new FullQualifiedName("olingo.odata.test1", "Container"));

  private final EdmEntitySet esAllPrim;
  private final EdmEntitySet esCompAllPrim;
  private final EdmEntitySet esCollAllPrim;
  private final EdmEntitySet esMixPrimCollAllPrim;

  public DataProviderTest() {
    esAllPrim = entityContainer.getEntitySet("ESAllPrim");
    esCompAllPrim = entityContainer.getEntitySet("ESCompAllPrim");
    esCollAllPrim = entityContainer.getEntitySet("ESCollAllPrim");
    esMixPrimCollAllPrim = entityContainer.getEntitySet("ESMixPrimCollComp");
  }

  @Test
  public void esAllPrimEntity() throws Exception {
    final DataProvider dataProvider = new DataProvider();
    Entity first = dataProvider.readAll(esAllPrim).getEntities().get(2);
    Assert.assertEquals(16, first.getProperties().size());

    UriParameter parameter = Mockito.mock(UriParameter.class);
    Mockito.when(parameter.getName()).thenReturn("PropertyInt16");
    Mockito.when(parameter.getText()).thenReturn("-0");
    Assert.assertEquals(first, dataProvider.read(esAllPrim, Arrays.asList(parameter)));
  }

  @Test
  public void esAllPrim() throws Exception {
    final DataProvider data = new DataProvider();
    EntitySet outSet = data.readAll(esAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());

    Entity first = outSet.getEntities().get(0);
    Assert.assertEquals(16, first.getProperties().size());
    Assert.assertEquals(2, first.getNavigationLinks().size());
    final EntitySet target = first.getNavigationLink("NavPropertyETTwoPrimMany").getInlineEntitySet();
    Assert.assertNotNull(target);
    Assert.assertEquals(1, target.getEntities().size());
    Assert.assertEquals(data.readAll(entityContainer.getEntitySet("ESTwoPrim")).getEntities().get(1),
        target.getEntities().get(0));

    Assert.assertEquals(16, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(16, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esCollAllPrim() throws Exception {
    EntitySet outSet = new DataProvider().readAll(esCollAllPrim);

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
    EntitySet outSet = new DataProvider().readAll(esCompAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());
    Assert.assertEquals(2, outSet.getEntities().get(0).getProperties().size());
    Property complex = outSet.getEntities().get(0).getProperties().get(1);
    Assert.assertTrue(complex.isLinkedComplex());
    Assert.assertEquals(16, complex.asLinkedComplex().getValue().size());
    Assert.assertEquals(2, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(2, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esMixPrimCollComp() throws Exception {
    EntitySet outSet = new DataProvider().readAll(esMixPrimCollAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());
    Assert.assertEquals(4, outSet.getEntities().get(0).getProperties().size());
    Property complex = outSet.getEntities().get(0).getProperties().get(2);
    Assert.assertTrue(complex.isLinkedComplex());
    Assert.assertEquals(2, complex.asLinkedComplex().getValue().size());
    Property complexCollection = outSet.getEntities().get(0).getProperties().get(3);
    Assert.assertTrue(complexCollection.isCollection());
    List<?> linkedComplexValues = complexCollection.asCollection();
    Assert.assertEquals(3, linkedComplexValues.size());
    LinkedComplexValue linkedComplexValue = (LinkedComplexValue) linkedComplexValues.get(0);
    Assert.assertEquals(2, linkedComplexValue.getValue().size());
    Property lcProp = linkedComplexValue.getValue().get(0);
    Assert.assertFalse(lcProp.isCollection());
    Assert.assertEquals(123, lcProp.getValue());
    //
    Assert.assertEquals(4, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(4, outSet.getEntities().get(2).getProperties().size());
  }
}
