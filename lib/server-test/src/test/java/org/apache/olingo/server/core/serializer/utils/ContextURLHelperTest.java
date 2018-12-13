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
package org.apache.olingo.server.core.serializer.utils;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmAction;
import org.apache.olingo.commons.api.edm.EdmComplexType;
import org.apache.olingo.commons.api.edm.EdmEntityContainer;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.core.serializer.ExpandSelectMock;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.junit.Test;
import org.mockito.Mockito;

public class ContextURLHelperTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();
  private static final EdmEntityContainer entityContainer = edm.getEntityContainer();

  @Test
  public void buildSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESAllPrim");
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(entitySet, "PropertyString");
    final SelectItem selectItem2 = ExpandSelectMock.mockSelectItem(entitySet, "PropertyInt16");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem1, selectItem2, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESAllPrim(PropertyInt16,PropertyString)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildSelectAll() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESAllPrim");
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(entitySet, "PropertyGuid");
    SelectItem selectItem2 = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem2.isStar()).thenReturn(true);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem1, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESAllPrim(*)", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildSelectComplex() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESCompMixPrimCollComp");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(entitySet,
            "PropertyMixedPrimCollComp", "PropertyComp", "PropertyString"),
            ExpandSelectMock.mockSelectItem(entitySet,
                "PropertyMixedPrimCollComp", "PropertyComp", "PropertyInt16"),
                ExpandSelectMock.mockSelectItem(entitySet, "PropertyMixedPrimCollComp", "CollPropertyString"),
                ExpandSelectMock.mockSelectItem(entitySet, "PropertyMixedPrimCollComp", "CollPropertyComp"),
                ExpandSelectMock.mockSelectItem(entitySet, "PropertyInt16")));
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
  public void buildEntity() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("$metadata#ESTwoPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  // /odata.svc/ESAllPrim(32767)/NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne/
  //                            NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne/NavPropertyETTwoPrimOne
  // @odata.context: "../../../../../$metadata#ESTwoPrim/$entity"
  @Test
  public void buildRelativeFiveNavigation() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    String odataPath = "ESAllPrim(32767)/NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne/" +
                        "NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne/NavPropertyETTwoPrimOne";
    ContextURL contextURL = ContextURL.with()
            .oDataPath("/" + odataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../../../../$metadata#ESTwoPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());

    // removed leading '/'
    contextURL = ContextURL.with()
            .oDataPath(odataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../../../../$metadata#ESTwoPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  // odata.svc/ESAllPrim(32767)/NavPropertyETTwoPrimOne
  // @odata.context: "$metadata#ESTwoPrim/$entity",
  @Test
  public void buildRelativeNavigation() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final String oDataPath = "ESAllPrim(32767)/NavPropertyETTwoPrimOne";
    ContextURL contextURL = ContextURL.with().oDataPath("/" + oDataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../$metadata#ESTwoPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());

    // without leading '/'
    contextURL = ContextURL.with().oDataPath(oDataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../$metadata#ESTwoPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  // /odata.svc/ESAllPrim(32767)/NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne
  // @odata.context: "$metadata#ESAllPrim/$entity",
  @Test
  public void buildRelativeTwoNavigation() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESAllPrim");
    String oDataPath = "ESAllPrim(32767)/NavPropertyETTwoPrimOne/NavPropertyETAllPrimOne";
    ContextURL contextURL = ContextURL.with()
            .oDataPath("/" + oDataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../$metadata#ESAllPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());

    // without leading '/'
    contextURL = ContextURL.with().oDataPath(oDataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../$metadata#ESAllPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());

    // without unnecessary '///'
    contextURL = ContextURL.with().oDataPath("///" + oDataPath)
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../$metadata#ESAllPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());
    // without unnecessary '///' between
    contextURL = ContextURL.with().oDataPath(oDataPath.replace("/", "///"))
            .entitySet(entitySet).suffix(ContextURL.Suffix.ENTITY).build();
    assertEquals("../../$metadata#ESAllPrim/$entity", ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandAll() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    ExpandItem expandItem = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItem.isStar()).thenReturn(true);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne(),NavPropertyETAllPrimMany())", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandNoSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimOne")));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne())", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildExpandSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final ExpandItem expandItem1 = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    ExpandItem expandItem2 = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimMany");
    final SelectOption innerSelect = ExpandSelectMock.mockSelectOption(Arrays.asList(
        ExpandSelectMock.mockSelectItem(innerEntitySet, "PropertyInt32")));
    Mockito.when(expandItem2.getSelectOption()).thenReturn(innerSelect);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        expandItem1, expandItem2));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItem(entitySet, "PropertyString");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, select)).build();
    assertEquals("$metadata#ESTwoPrim(PropertyInt16,PropertyString,NavPropertyETAllPrimOne(),"
        + "NavPropertyETAllPrimMany(PropertyInt16,PropertyInt32))",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandTwoLevels() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    final ExpandOption innerExpand = ExpandSelectMock.mockExpandOption(Arrays.asList(
        ExpandSelectMock.mockExpandItem(innerEntitySet, "NavPropertyETTwoPrimOne")));
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getExpandOption()).thenReturn(innerExpand);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne(NavPropertyETTwoPrimOne()))", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandTwoLevelsInnerAll() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    ExpandItem expandItemInner = Mockito.mock(ExpandItem.class);
    Mockito.when(expandItemInner.isStar()).thenReturn(true);
    final ExpandOption innerExpand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemInner));
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getExpandOption()).thenReturn(innerExpand);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne())", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildExpandSelectTwoLevels() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmEntitySet innerEntitySet = entityContainer.getEntitySet("ESAllPrim");
    ExpandItem expandItemInner = ExpandSelectMock.mockExpandItem(innerEntitySet, "NavPropertyETTwoPrimOne");
    SelectItem innerSelectItem = Mockito.mock(SelectItem.class);
    Mockito.when(innerSelectItem.isStar()).thenReturn(true);
    final SelectOption innerSelect = ExpandSelectMock.mockSelectOption(Arrays.asList(innerSelectItem));
    Mockito.when(expandItemInner.getSelectOption()).thenReturn(innerSelect);
    final ExpandOption innerExpand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItemInner));
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETAllPrimOne");
    Mockito.when(expandItem.getExpandOption()).thenReturn(innerExpand);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESTwoPrim(NavPropertyETAllPrimOne(NavPropertyETTwoPrimOne(*)))",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildSingleKey() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoPrim");
    final EdmProperty edmProperty = entitySet.getEntityType().getStructuralProperty("PropertyInt16");
    UriParameter key = Mockito.mock(UriParameter.class);
    Mockito.when(key.getName()).thenReturn(edmProperty.getName());
    Mockito.when(key.getText()).thenReturn("42");
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .keyPath(ContextURLHelper.buildKeyPredicate(Arrays.asList(key)))
        .navOrPropertyPath(edmProperty.getName()).build();
    assertEquals("../$metadata#ESTwoPrim(42)/PropertyInt16",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }

  @Test
  public void buildCompoundKey() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmProperty edmProperty = entitySet.getEntityType().getStructuralProperty("PropertyInt16");
    UriParameter key1 = Mockito.mock(UriParameter.class);
    Mockito.when(key1.getName()).thenReturn(edmProperty.getName());
    Mockito.when(key1.getText()).thenReturn("1");
    UriParameter key2 = Mockito.mock(UriParameter.class);
    Mockito.when(key2.getName()).thenReturn("PropertyString");
    Mockito.when(key2.getText()).thenReturn("'2'");
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .keyPath(ContextURLHelper.buildKeyPredicate(Arrays.asList(key1, key2)))
        .navOrPropertyPath(edmProperty.getName()).build();
    assertEquals("../$metadata#ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyInt16",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildKeyAlias() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESFourKeyAlias");
    final EdmProperty edmProperty = entitySet.getEntityType().getStructuralProperty("PropertyComp");
    UriParameter key1 = Mockito.mock(UriParameter.class);
    Mockito.when(key1.getName()).thenReturn("PropertyInt16");
    Mockito.when(key1.getText()).thenReturn("1");
    UriParameter key2 = Mockito.mock(UriParameter.class);
    Mockito.when(key2.getName()).thenReturn("KeyAlias1");
    Mockito.when(key2.getText()).thenReturn("11");
    UriParameter key3 = Mockito.mock(UriParameter.class);
    Mockito.when(key3.getName()).thenReturn("KeyAlias2");
    Mockito.when(key3.getText()).thenReturn("'Num11'");
    UriParameter key4 = Mockito.mock(UriParameter.class);
    Mockito.when(key4.getName()).thenReturn("KeyAlias3");
    Mockito.when(key4.getText()).thenReturn("'Num111'");
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .keyPath(ContextURLHelper.buildKeyPredicate(Arrays.asList(key1, key2, key3, key4)))
        .navOrPropertyPath(edmProperty.getName()).build();
    assertEquals("../$metadata#ESFourKeyAlias"
            + "(PropertyInt16=1,KeyAlias1=11,KeyAlias2='Num11',KeyAlias3='Num111')/PropertyComp",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildComplexDerivedTypeInSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESCompCollDerived");
    final EdmComplexType derivedComplexType = edm.getComplexType(
        new FullQualifiedName("olingo.odata.test1.CTBaseAno"));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemOnDerivedComplexTypes(entitySet, 
        "PropertyCompAno", derivedComplexType, "AdditionalPropString");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESCompCollDerived(PropertyInt16,PropertyCompAno/"
        + "olingo.odata.test1.CTBaseAno/AdditionalPropString)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildMultiLevelComplexDerivedTypeInSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESCompCollComp");
    final EdmComplexType derivedComplexType = edm.getComplexType(
        new FullQualifiedName("olingo.odata.test1.CTBase"));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemMultiLevelOnDerivedComplexTypes(
        entitySet, "PropertyComp", "CollPropertyComp", derivedComplexType, "AdditionalPropString");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESCompCollComp(PropertyInt16,PropertyComp/CollPropertyComp/"
        + "olingo.odata.test1.CTBase/AdditionalPropString)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildEntityTypeCastInSelect1() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmEntityType derivedEntityType = edm.getEntityType(
        new FullQualifiedName("olingo.odata.test1.ETBaseTwoKeyNav"));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemOnDerivedEntityTypes( 
        "NavPropertyETBaseTwoKeyNavOne", derivedEntityType);
    Mockito.when(selectItem.getStartTypeFilter()).thenReturn(derivedEntityType);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.ETBaseTwoKeyNav/"
        + "NavPropertyETBaseTwoKeyNavOne)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildEntityTypeCastInSelect2() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmEntityType derivedEntityType = edm.getEntityType(
        new FullQualifiedName("olingo.odata.test1.ETBaseTwoKeyNav"));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemOnDerivedEntityTypes( 
        "PropertyDate", derivedEntityType);
    Mockito.when(selectItem.getStartTypeFilter()).thenReturn(derivedEntityType);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildMultipleSelectWithEntityTypeCastInSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmEntityType derivedEntityType = edm.getEntityType(
        new FullQualifiedName("olingo.odata.test1.ETBaseTwoKeyNav"));
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(entitySet, "CollPropertyComp");
    final SelectItem selectItem2 = ExpandSelectMock.mockSelectItemOnDerivedEntityTypes( 
        "NavPropertyETTwoBaseTwoKeyNavOne", derivedEntityType);
    Mockito.when(selectItem2.getStartTypeFilter()).thenReturn(derivedEntityType);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem1, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "CollPropertyComp,olingo.odata.test1.ETBaseTwoKeyNav/"
        + "NavPropertyETTwoBaseTwoKeyNavOne)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildEntityTypeCastAndComplexTypeCastInSelect() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmEntityType derivedEntityType = edm.getEntityType(
        new FullQualifiedName("olingo.odata.test1.ETBaseTwoKeyNav"));
    final EdmComplexType derivedComplexType = edm.getComplexType(
        new FullQualifiedName("olingo.odata.test1.CTBasePrimCompNav"));
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemOnDerivedEntityAndComplexTypes( 
        "CollPropertyComp", derivedEntityType, derivedComplexType, "NavPropertyETTwoKeyNavOne");
    Mockito.when(selectItem.getStartTypeFilter()).thenReturn(derivedEntityType);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.ETBaseTwoKeyNav/CollPropertyComp/"
        + "olingo.odata.test1.CTBasePrimCompNav/NavPropertyETTwoKeyNavOne)", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildExpandWithSelectHavingDerivedEntityType() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESKeyNavCont");
    final EdmEntityType derivedEntityType = edm.getEntityType(
        new FullQualifiedName("olingo.odata.test1.ETBaseTwoKeyNav"));
    final EdmProperty edmProperty = derivedEntityType.getStructuralProperty("PropertyDate");
    ExpandItem expandItem = ExpandSelectMock.mockExpandItem(entitySet, "NavPropertyETTwoKeyNavContOne");
    SelectItem selectItem = Mockito.mock(SelectItem.class);
    Mockito.when(selectItem.getStartTypeFilter()).thenReturn(derivedEntityType);
    final UriInfoResource resource = ExpandSelectMock.mockComplexTypeResource(edmProperty);
    Mockito.when(selectItem.getResourcePath()).thenReturn(resource);
    final SelectOption selectOption = ExpandSelectMock.mockSelectOption(Arrays.asList(selectItem));
    Mockito.when(expandItem.getSelectOption()).thenReturn(selectOption);
    final ExpandOption expand = ExpandSelectMock.mockExpandOption(Arrays.asList(expandItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), expand, null)).build();
    assertEquals("$metadata#ESKeyNavCont(NavPropertyETTwoKeyNavContOne("
        + "PropertyInt16,PropertyString,olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate))", 
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildSelectWithComplexPropertyWithNav() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemOnComplexTypesWithNav(
        entitySet, "CollPropertyCompNav", "NavPropertyETTwoKeyNavMany");
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "CollPropertyCompNav/NavPropertyETTwoKeyNavMany)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildSelectWithAction() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmAction action = edm.getBoundAction(
        new FullQualifiedName("olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav"), 
        new FullQualifiedName("olingo.odata.test1.ETTwoKeyNav"), true);
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemHavingAction(
        entitySet, action);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildSelectWithPropertyAndAction() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmAction action = edm.getBoundAction(
        new FullQualifiedName("olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav"), 
        new FullQualifiedName("olingo.odata.test1.ETTwoKeyNav"), true);
    final SelectItem selectItem1 = ExpandSelectMock.mockSelectItem(
        entitySet, "PropertyString");
    final SelectItem selectItem2 = ExpandSelectMock.mockSelectItemHavingAction(
        entitySet, action);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem1, selectItem2));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
  
  @Test
  public void buildSelectWithFunction() throws Exception {
    final EdmEntitySet entitySet = entityContainer.getEntitySet("ESTwoKeyNav");
    final EdmFunction function = edm.getBoundFunction(
        new FullQualifiedName("olingo.odata.test1.BFCESTwoKeyNavRTString"), 
        new FullQualifiedName("olingo.odata.test1.ETTwoKeyNav"), true, new ArrayList<String>());
    final SelectItem selectItem = ExpandSelectMock.mockSelectItemHavingFunction(
        entitySet, function);
    final SelectOption select = ExpandSelectMock.mockSelectOption(Arrays.asList(
        selectItem));
    final ContextURL contextURL = ContextURL.with().entitySet(entitySet)
        .selectList(ContextURLHelper.buildSelectList(entitySet.getEntityType(), null, select)).build();
    assertEquals("$metadata#ESTwoKeyNav(PropertyInt16,PropertyString,"
        + "olingo.odata.test1.BFCESTwoKeyNavRTString)",
        ContextURLBuilder.create(contextURL).toASCIIString());
  }
}
