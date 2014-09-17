/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.server.core.serializer.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.json.ODataJsonSerializerTest;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLHelperTest {

  private static final Edm edm = OData.newInstance().createEdm(new EdmTechProvider());
  private static final EdmEntityContainer entityContainer = edm.getEntityContainer(
      new FullQualifiedName("olingo.odata.test1", "Container"));

  @Test
  public void buildSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESAllPrim");
    final SelectItem selectItem1 = ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyString");
    final SelectItem selectItem2 = ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyInt16");
    final SelectOption select = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(
        selectItem1, selectItem2, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESAllPrim(PropertyInt16,PropertyString)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildSelectAll() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESAllPrim");
    final SelectItem selectItem1 = ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyGuid");
    SelectItem selectItem2 = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem2.isStar()).thenReturn(true);
    final SelectOption select = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(selectItem1, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESAllPrim(*)", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildSelectComplex() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESCompMixPrimCollComp");
    final SelectOption select = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(
        ODataJsonSerializerTest.mockSelectItem(entitySet,
            "PropertyMixedPrimCollComp", "PropertyComp", "PropertyString"),
        ODataJsonSerializerTest.mockSelectItem(entitySet,
            "PropertyMixedPrimCollComp", "PropertyComp", "PropertyInt16"),
        ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyMixedPrimCollComp", "CollPropertyString"),
        ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyMixedPrimCollComp", "CollPropertyComp"),
        ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyInt16")));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESCompMixPrimCollComp("
        + "PropertyInt16,"
        + "PropertyMixedPrimCollComp/CollPropertyString,"
        + "PropertyMixedPrimCollComp/PropertyComp/PropertyInt16,"
        + "PropertyMixedPrimCollComp/PropertyComp/PropertyString,"
        + "PropertyMixedPrimCollComp/CollPropertyComp)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandAll() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    ExpandItem expandItem = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItem.isStar()).thenReturn(true);
    final ExpandOption expand = ODataJsonSerializerTest.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandNoSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final ExpandOption expand = ODataJsonSerializerTest.mockExpandOption(Arrays.asList(
        ODataJsonSerializerTest.mockExpandItem(entitySet, "NavPropertyETAllPrimOne")));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final ExpandItem expandItem1 = ODataJsonSerializerTest.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    ExpandItem expandItem2 = ODataJsonSerializerTest.mockExpandItem(entitySet, "NavPropertyETAllPrimMany");
    final SelectOption innerSelect = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(
        ODataJsonSerializerTest.mockSelectItem(innerEntitySet, "PropertyInt32")));
    Mockito.when(expandItem2.getSelectOption()).thenReturn(innerSelect);
    final ExpandOption expand = ODataJsonSerializerTest.mockExpandOption(Arrays.asList(
        expandItem1, expandItem2));
    final SelectItem selectItem = ODataJsonSerializerTest.mockSelectItem(entitySet, "PropertyString");
    final SelectOption select = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, select)).build();
    assertEquals("$metadata#ESTwoPrim(PropertyString,NavPropertyETAllPrimMany(PropertyInt32))",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandSelectTwoLevels() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    ExpandItem expandItemInner = ODataJsonSerializerTest.mockExpandItem(innerEntitySet, "NavPropertyETTwoPrimOne");
    SelectItem innerSelectItem = Mockito.mock(SelectItem.class);
    Mockito.when(innerSelectItem.isStar()).thenReturn(true);
    final SelectOption innerSelect = ODataJsonSerializerTest.mockSelectOption(Arrays.asList(innerSelectItem));
    Mockito.when(expandItemInner.getSelectOption()).thenReturn(innerSelect);
    final ExpandOption innerExpand = ODataJsonSerializerTest.mockExpandOption(Arrays.asList(expandItemInner));
    ExpandItem expandItem = ODataJsonSerializerTest.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getExpandOption()).thenReturn(innerExpand);
    final ExpandOption expand = ODataJsonSerializerTest.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne(NavPropertyETTwoPrimOne(*)))",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
}
