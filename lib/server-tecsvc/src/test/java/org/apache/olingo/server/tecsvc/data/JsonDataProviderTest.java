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

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntitySet;
import org.apache.olingo.commons.api.data.LinkedComplexValue;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmElement;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmStructuredType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializer;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 */
public class JsonDataProviderTest {

  private final Edm edm = OData.newInstance().createEdm(new EdmTechProvider());
  private final EdmEntityContainer entityContainer = edm.getEntityContainer(
          new FullQualifiedName("com.sap.odata.test1", "Container"));

  private final EdmEntitySet esAllPrim;
  private final EdmEntitySet esCompAllPrim;
  private final EdmEntitySet esCollAllPrim;
  private final EdmEntitySet esMixPrimCollAllPrim;

  public JsonDataProviderTest() {
    esAllPrim = entityContainer.getEntitySet("ESAllPrim");
    esCompAllPrim = entityContainer.getEntitySet("ESCompAllPrim");
    esCollAllPrim = entityContainer.getEntitySet("ESCollAllPrim");
    esMixPrimCollAllPrim = entityContainer.getEntitySet("ESMixPrimCollComp");
  }

  @Test
  public void doRoundTrip() throws Exception {
    doRoundTrip(entityContainer.getEntitySet("ESAllPrim"), 1401);
    doRoundTrip(entityContainer.getEntitySet("ESCompAllPrim"), 1592);
    doRoundTrip(entityContainer.getEntitySet("ESCollAllPrim"), 2855);
    doRoundTrip(entityContainer.getEntitySet("ESMixPrimCollComp"), 1032);
  }

  @Test
  public void esAllPrimEntity() throws Exception {
    DataProvider jdp = getDataProvider();
    Entity first = jdp.readAll(esAllPrim).getEntities().get(0);

    Assert.assertEquals(16, first.getProperties().size());
  }

  @Test
  public void esAllPrim() throws Exception {
    DataProvider jdp = getDataProvider();
    EntitySet outSet = jdp.readAll(esAllPrim);

    Assert.assertEquals(3, outSet.getEntities().size());
    Entity first = outSet.getEntities().get(0);
    Assert.assertEquals(16, first.getProperties().size());
    Assert.assertEquals(16, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(16, outSet.getEntities().get(2).getProperties().size());
  }

  @Test
  public void esCollAllPrim() throws Exception {
    DataProvider jdp = getDataProvider();
    EntitySet outSet = jdp.readAll(esCollAllPrim);

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
    DataProvider jdp = getDataProvider();
    EntitySet outSet = jdp.readAll(esCompAllPrim);

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
    DataProvider jdp = getDataProvider();
    EntitySet outSet = jdp.readAll(esMixPrimCollAllPrim);

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
    Assert.assertEquals(Integer.valueOf("123"), lcProp.getValue());
    //
    Assert.assertEquals(4, outSet.getEntities().get(1).getProperties().size());
    Assert.assertEquals(4, outSet.getEntities().get(2).getProperties().size());
  }

  private DataProvider getDataProvider() throws DataProvider.DataProviderException {
    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmTechProvider());
    return new DataProvider(edm);
  }

  @Test
  public void edm() {
    OData odata = OData.newInstance();
    Edm edm = odata.createEdm(new EdmTechProvider());
    EdmEntitySet edmEntitySet =
        edm.getEntityContainer(ContainerProvider.nameContainer).getEntitySet("ESCompAllPrim");

    EdmEntityType et = edmEntitySet.getEntityType();
    printType(edm, et);
  }

  private void printType(Edm edm, EdmStructuredType type) {

    List<String> propNames = type.getPropertyNames();

    for (String propName : propNames) {
      EdmElement element = type.getProperty(propName);
      if(element instanceof EdmProperty) {
        EdmProperty property = (EdmProperty) element;
        if(property.isPrimitive()) {
          System.out.println("Primitive name/type: " + property.getName() + "/" + property.getType());
        } else {
          // recursion
          EdmComplexType complex = edm.getComplexType(property.getType().getFullQualifiedName());
          System.out.println("Complex name/type [" + property.getName() + "/" + property.getType() + "]");
          printType(edm, complex);
        }
      }
    }
  }

  private void doRoundTrip(EdmEntitySet entitySet, int expectedLength) throws Exception {
    DataProvider jdp = new DataProvider(edm);
    EntitySet outSet = jdp.readAll(entitySet);


    ODataJsonSerializer serializer = new ODataJsonSerializer();
    ContextURL contextUrl = null;
    InputStream is = serializer.entitySet(entitySet, outSet, contextUrl);

    StringHelper.Stream stream = StringHelper.toStream(is);

//    System.out.println("========== " + entitySet.getName() + " =================");
//    stream.print();
//    System.out.println("\n========== " + entitySet.getName() + " =================");

    Assert.assertEquals(expectedLength, stream.asString().length());
  }
}
