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
package org.apache.olingo.server.core.uri.antlr;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.testutil.FilterValidator;
import org.apache.olingo.server.core.uri.testutil.ResourceValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.junit.Ignore;
import org.junit.Test;

public class TestUriParserImpl {
  private final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();
  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);
  private final ResourceValidator testRes = new ResourceValidator().setEdm(edm);
  private final FilterValidator testFilter = new FilterValidator().setEdm(edm);

  private final String PropertyBoolean = "PropertyBoolean=true";
  private final String PropertyByte = "PropertyByte=1";
  private final String PropertyDate = "PropertyDate=2013-09-25";
  private final String PropertyDateTimeOffset = "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00";
  private final String PropertyDecimal = "PropertyDecimal=12";
  private final String PropertyDuration = "PropertyDuration=duration'P50903316DT2H25M4S'";
  private final String PropertyGuid = "PropertyGuid=12345678-1234-1234-1234-123456789012";
  private final String PropertyInt16 = "PropertyInt16=1";
  private final String PropertyInt32 = "PropertyInt32=12";
  private final String PropertyInt64 = "PropertyInt64=64";
  private final String PropertySByte = "PropertySByte=1";
  private final String PropertyString = "PropertyString='ABC'";
  private final String PropertyTimeOfDay = "PropertyTimeOfDay=12:34:55";

  private final String allKeys = PropertyString + "," + PropertyInt16 + "," + PropertyBoolean + "," + PropertyByte
      + "," + PropertySByte + "," + PropertyInt32 + "," + PropertyInt64 + "," + PropertyDecimal + "," + PropertyDate
      + "," + PropertyDateTimeOffset + "," + PropertyDuration + "," + PropertyGuid + "," + PropertyTimeOfDay;

  @Test
  public void testBoundFunctionImport_VarParameters() {

    // no input
    testRes.run("ESKeyNav(1)/olingo.odata.test1.BFCETKeyNavRTETKeyNav()")
    .at(0).isUriPathInfoKind(UriResourceKind.entitySet)
    .at(1).isUriPathInfoKind(UriResourceKind.function);

    // one input
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')")
    .at(0).isUriPathInfoKind(UriResourceKind.entitySet)
    .at(1).isUriPathInfoKind(UriResourceKind.function)
    .isParameter(0, "ParameterString", "'ABC'");

    // two input
    testRes.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.function)
    .isParameter(0, "ParameterInt16", "1")
    .isParameter(1, "ParameterString", "'2'");
  }

  @Test
  public void testFunctionBound_varReturnType() {

    String esTwoKeyNav = "ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')";

    // returning primitive
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoKeyNav, true)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.function)
    .isType(PropertyProvider.nameString, false);

    // returning collection of primitive
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoKeyNav, true)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.function)
    .isType(PropertyProvider.nameString, true);

    // returning single complex
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoKeyNav, true)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.function)
    .isType(ComplexTypeProvider.nameCTTwoPrim, false);

    // returning collection of complex
    testRes.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoKeyNav, true)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.function)
    .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    // returning single entity
    testRes.run(
        esTwoKeyNav + "/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false);

    // returning collection of entity (aka entitySet)
    testRes.run(esTwoKeyNav + "/olingo.odata.test1.BFCSINavRTESTwoKeyNav()")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.function)
    .isType(EntityTypeProvider.nameETTwoKeyNav, true);
  }

  @Test
  public void runActionImport_VarReturnType() {

    testRes.run(ContainerProvider.AIRT_STRING).isKind(UriInfoKind.resource)
    .first()
    .isActionImport(ContainerProvider.AIRT_STRING)
    .isAction(ActionProvider.nameUARTString.getName())
    .isType(PropertyProvider.nameString, false);

    testRes.run(ContainerProvider.AIRT_COLL_STRING_TWO_PARAM).isKind(UriInfoKind.resource)
        .first()
        .isActionImport(ContainerProvider.AIRT_COLL_STRING_TWO_PARAM)
        .isAction(ActionProvider.nameUARTCollStringTwoParam.getName())
        .isType(PropertyProvider.nameString, true);

    testRes.run(ContainerProvider.AIRTCT_TWO_PRIM_PARAM).isKind(UriInfoKind.resource)
    .first()
    .isActionImport(ContainerProvider.AIRTCT_TWO_PRIM_PARAM)
    .isAction(ActionProvider.nameUARTCTTwoPrimParam.getName())
    .isType(ComplexTypeProvider.nameCTTwoPrim, false);

    testRes.run(ContainerProvider.AIRT_COLL_CT_TWO_PRIM_PARAM).isKind(UriInfoKind.resource)
    .first()
    .isActionImport(ContainerProvider.AIRT_COLL_CT_TWO_PRIM_PARAM)
    .isAction(ActionProvider.nameUARTCollCTTwoPrimParam.getName())
    .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    testRes.run(ContainerProvider.AIRTET_TWO_KEY_TWO_PRIM_PARAM).isKind(UriInfoKind.resource)
    .first()
    .isActionImport(ContainerProvider.AIRTET_TWO_KEY_TWO_PRIM_PARAM)
    .isAction(ActionProvider.nameUARTETTwoKeyTwoPrimParam.getName())
    .isType(EntityTypeProvider.nameETTwoKeyTwoPrim, false);

    testUri.runEx(ContainerProvider.AIRT_STRING + "/invalidElement")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
  }

  @Test
  public void count() {

    // count entity set
    testRes.run("ESAllPrim/$count")
    .at(0)
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETAllPrim, true)
    .at(1)
    .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of complex
    testRes.run("ESKeyNav(1)/CollPropertyComp/$count")
    .at(0)
    .isType(EntityTypeProvider.nameETKeyNav)
    .at(1)
    .isType(ComplexTypeProvider.nameCTPrimComp, true)
    .at(2)
    .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of primitive
    testRes.run("ESCollAllPrim(1)/CollPropertyString/$count")
    .at(1)
    .isType(PropertyProvider.nameString, true)
    .at(2)
    .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runCrossJoin() throws Exception {
    testUri.run("$crossjoin(ESAllKey)")
    .isKind(UriInfoKind.crossjoin)
    .isCrossJoinEntityList(Arrays.asList("ESAllKey"));

    testUri.run("$crossjoin(ESAllKey,ESTwoPrim)")
    .isKind(UriInfoKind.crossjoin)
    .isCrossJoinEntityList(Arrays.asList("ESAllKey", "ESTwoPrim"));
  }

  @Test(expected = UriValidationException.class)
  public void testEntityFailOnValidation1() throws Exception {
    // simple entity set; with qualifiedentityTypeName; with filter
    testUri.run("$entity/olingo.odata.test1.ETTwoPrim", "$filter=PropertyInt16 eq 123&$id=ESAllKey")
    .isIdText("ESAllKey")
    .goFilter().is("<<PropertyInt16> eq <123>>");
  }

  @Test(expected = UriParserSyntaxException.class)
  public void testEntityFailOnValidation2() throws Exception {
    // simple entity set; with qualifiedentityTypeName; with 2xformat(before and after), expand, filter
    testUri.run("$entity/olingo.odata.test1.ETTwoPrim",
        "$format=xml&$expand=*&abc=123&$id=ESBase&xyz=987&$filter=PropertyInt16 eq 123&$format=atom&$select=*")
        .isFormatText("atom")
        .isCustomParameter(0, "abc", "123")
        .isIdText("ESBase")
        .isCustomParameter(1, "xyz", "987")
        .isSelectItemStar(0);
  }

  @Test
  public void testEntity() throws Exception {

    // simple entity set
    testUri.run("$entity", "$id=ESAllPrim").isKind(UriInfoKind.entityId)
    .isKind(UriInfoKind.entityId)
    .isIdText("ESAllPrim");

    // simple entity set; $format before $id
    testUri.run("$entity", "$format=xml&$id=ETAllPrim").isKind(UriInfoKind.entityId)
    .isFormatText("xml")
    .isIdText("ETAllPrim");

    testUri.run("$entity", "$format=xml&abc=123&$id=ESAllKey").isKind(UriInfoKind.entityId)
    .isFormatText("xml")
    .isCustomParameter(0, "abc", "123")
    .isIdText("ESAllKey");

    // simple entity set; $format after $id
    testUri.run("$entity", "$id=ETAllPrim&$format=xml").isKind(UriInfoKind.entityId)
    .isIdText("ETAllPrim")
    .isFormatText("xml");

    // simple entity set; $format and custom parameter after $id
    testUri.run("$entity", "$id=ETAllPrim&$format=xml&abc=123").isKind(UriInfoKind.entityId)
    .isIdText("ETAllPrim")
    .isFormatText("xml")
    .isCustomParameter(0, "abc", "123");

    // simple entity set; $format before $id and custom parameter after $id
    testUri.run("$entity", "$format=xml&$id=ETAllPrim&abc=123").isKind(UriInfoKind.entityId)
    .isFormatText("xml")
    .isIdText("ETAllPrim")
    .isCustomParameter(0, "abc", "123");

    // simple entity set; with qualifiedentityTypeName
    testUri.run("$entity/olingo.odata.test1.ETTwoPrim", "$id=ESBase")
    .isEntityType(EntityTypeProvider.nameETTwoPrim)
    .isIdText("ESBase");

    // simple entity set; with qualifiedentityTypeName;
    testUri.run("$entity/olingo.odata.test1.ETBase", "$id=ESTwoPrim")
    .isEntityType(EntityTypeProvider.nameETBase)
    .isKind(UriInfoKind.entityId)
    .isIdText("ESTwoPrim");

    // simple entity set; with qualifiedentityTypeName; with format
    testUri.run("$entity/olingo.odata.test1.ETBase", "$id=ESTwoPrim&$format=atom")
    .isKind(UriInfoKind.entityId)
    .isEntityType(EntityTypeProvider.nameETBase)
    .isIdText("ESTwoPrim")
    .isFormatText("atom");

    // simple entity set; with qualifiedentityTypeName; with select
    testUri.run("$entity/olingo.odata.test1.ETBase", "$id=ESTwoPrim&$select=*")
    .isKind(UriInfoKind.entityId)
    .isEntityType(EntityTypeProvider.nameETBase)
    .isIdText("ESTwoPrim")
    .isSelectItemStar(0);

    // simple entity set; with qualifiedentityTypeName; with expand
    testUri.run("$entity/olingo.odata.test1.ETBase", "$id=ESTwoPrim&$expand=*")
    .isKind(UriInfoKind.entityId)
    .isEntityType(EntityTypeProvider.nameETBase)
    .isIdText("ESTwoPrim")
    .isExpandText("*")
    .goExpand().first().isSegmentStar();

  }

  @Test
  public void entitySet() throws Exception {

    // plain entity set
    testRes.run("ESAllPrim")
    .isEntitySet("ESAllPrim")
    .isType(EntityTypeProvider.nameETAllPrim);

    // with one key; simple key notation
    testRes.run("ESAllPrim(1)")
    .isEntitySet("ESAllPrim")
    .isType(EntityTypeProvider.nameETAllPrim)
    .isKeyPredicate(0, "PropertyInt16", "1");

    // with one key; name value key notation
    testRes.run("ESAllPrim(PropertyInt16=1)")
    .isEntitySet("ESAllPrim")
    .isKeyPredicate(0, "PropertyInt16", "1");

    // with two keys
    testRes.run("ESTwoKeyTwoPrim(PropertyInt16=1,PropertyString='ABC')")
    .isEntitySet("ESTwoKeyTwoPrim")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'ABC'");

    // with all keys
    testRes.run("ESAllKey(" + encode(allKeys) + ")")
    .isEntitySet("ESAllKey")
    .isKeyPredicate(0, "PropertyString", "'ABC'")
    .isKeyPredicate(1, "PropertyInt16", "1")
    .isKeyPredicate(2, "PropertyBoolean", "true")
    .isKeyPredicate(3, "PropertyByte", "1")
    .isKeyPredicate(4, "PropertySByte", "1")
    .isKeyPredicate(5, "PropertyInt32", "12")
    .isKeyPredicate(6, "PropertyInt64", "64")
    .isKeyPredicate(7, "PropertyDecimal", "12")
    .isKeyPredicate(8, "PropertyDate", "2013-09-25")
    .isKeyPredicate(9, "PropertyDateTimeOffset", "2002-10-10T12:00:00-05:00")
    .isKeyPredicate(10, "PropertyDuration", "duration'P50903316DT2H25M4S'")
    .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
    .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55");
  }

  @Test
  public void testEntitySet_NavigationProperty() {

    // plain entity set ...

    // with navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
    .at(0)
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
    .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
    .at(0)
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
    .at(2)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property -> navigation property -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
    .at(0)
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
    .isType(EntityTypeProvider.nameETTwoKeyNav)
    .at(2)
    .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
    .isType(EntityTypeProvider.nameETKeyNav);

    // with navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)")
    .at(0)
    .isEntitySet("ESKeyNav")
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/PropertyString").at(0)
    .at(0)
    .isEntitySet("ESKeyNav")
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(2)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property(key) -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavOne")
    .isEntitySet("ESKeyNav")
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(2)
    .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    // with navigation property(key) -> navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavMany(1)")
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav)
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(2)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavOne/PropertyString")
    .at(0)
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav)
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(2)
    .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
    .isType(EntityTypeProvider.nameETKeyNav)
    .at(3)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property(key) -> navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavMany(1)/PropertyString")
    .at(0)
    .isEntitySet("ESKeyNav")
    .isType(EntityTypeProvider.nameETKeyNav)
    .at(1)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(2)
    .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(3)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

  }

  @Test
  public void testEntitySet_Property() {

    // plain entity set ...

    // with property
    testRes.run("ESAllPrim(1)/PropertyString")
    .at(0)
    .isEntitySet("ESAllPrim")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with complex property
    testRes.run("ESCompAllPrim(1)/PropertyComp")
    .at(0)
    .isEntitySet("ESCompAllPrim")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    // with two properties
    testRes.run("ESCompAllPrim(1)/PropertyComp/PropertyString")
    .at(0)
    .isEntitySet("ESCompAllPrim")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
    .at(2)
    .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void testEntitySet_TypeFilter() {

    // filter
    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase")
    .at(0)
    .isEntitySet("ESTwoPrim")
    .isType(EntityTypeProvider.nameETTwoPrim, true)
    .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
    .isTypeFilterOnEntry(null);

    // filter before key predicate
    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase(PropertyInt16=1)")
    .at(0)
    .isEntitySet("ESTwoPrim")
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoPrim)
    .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
    .isTypeFilterOnEntry(null)
    .at(0)
    .isType(EntityTypeProvider.nameETTwoPrim, false)
    .isKeyPredicate(0, "PropertyInt16", "1");

    // filter before key predicate; property of sub type
    testRes.run("ESTwoPrim/olingo.odata.test1.ETBase(PropertyInt16=1)/AdditionalPropertyString_5")
    .at(0)
    .isEntitySet("ESTwoPrim")
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoPrim)
    .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
    .isTypeFilterOnEntry(null)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isType(PropertyProvider.nameString)
    .isPrimitiveProperty("AdditionalPropertyString_5", PropertyProvider.nameString, false);

    // filter after key predicate
    testRes.run("ESTwoPrim(PropertyInt16=1)/olingo.odata.test1.ETBase")
    .at(0)
    .isEntitySet("ESTwoPrim")
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoPrim, false)
    .isTypeFilterOnCollection(null)
    .isTypeFilterOnEntry(EntityTypeProvider.nameETBase)
    .isKeyPredicate(0, "PropertyInt16", "1");

    // filter after key predicate; property of sub type
    testRes.run("ESTwoPrim(PropertyInt16=1)/olingo.odata.test1.ETBase/AdditionalPropertyString_5")
    .at(0)
    .isEntitySet("ESTwoPrim")
    .isUriPathInfoKind(UriResourceKind.entitySet)
    .isType(EntityTypeProvider.nameETTwoPrim)
    .isTypeFilterOnCollection(null)
    .isTypeFilterOnEntry(EntityTypeProvider.nameETBase)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .at(1)
    .isPrimitiveProperty("AdditionalPropertyString_5", PropertyProvider.nameString, false)
    .isType(PropertyProvider.nameString);

  }

  @Test
  public void unary() throws Exception {
    testFilter.runOnETAllPrim("not PropertyBoolean").isCompr("<not <PropertyBoolean>>");
    testFilter.runOnETAllPrim("-PropertyInt16 eq PropertyInt16").isCompr("<<- <PropertyInt16>> eq <PropertyInt16>>");
  }

  // TODO: Use correct types.
  @Test
  @Ignore
  public void filterComplexMixedPriority() throws Exception {
    testFilter.runOnETAllPrim("PropertyInt16 or PropertyInt32 and PropertyInt64")
        .isCompr("<<PropertyInt16> or <<PropertyInt32> and <PropertyInt64>>>");
    testFilter.runOnETAllPrim("PropertyInt16 or PropertyInt32 and PropertyInt64 eq PropertyByte")
        .isCompr("<<PropertyInt16> or <<PropertyInt32> and <<PropertyInt64> eq <PropertyByte>>>>");
    testFilter.runOnETAllPrim("PropertyInt16 or PropertyInt32 eq PropertyInt64 and PropertyByte")
        .isCompr("<<PropertyInt16> or <<<PropertyInt32> eq <PropertyInt64>> and <PropertyByte>>>");
    testFilter.runOnETAllPrim("PropertyInt16 or PropertyInt32 eq PropertyInt64 and PropertyByte eq PropertySByte")
        .isCompr("<<PropertyInt16> or <<<PropertyInt32> eq <PropertyInt64>> "
            + "and <<PropertyByte> eq <PropertySByte>>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 and PropertyByte")
        .isCompr("<<<PropertyInt16> eq <PropertyInt32>> or <<PropertyInt64> and <PropertyByte>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 and PropertyByte eq PropertySByte")
        .isCompr("<<<PropertyInt16> eq <PropertyInt32>> "
            + "or <<PropertyInt64> and <<PropertyByte> eq <PropertySByte>>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 eq PropertyByte and PropertySByte")
        .isCompr("<<<PropertyInt16> eq <PropertyInt32>> "
            + "or <<<PropertyInt64> eq <PropertyByte>> and <PropertySByte>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 eq PropertyByte "
        + "and PropertySByte eq PropertyDecimal")
        .isCompr("<<<PropertyInt16> eq <PropertyInt32>> or <<<PropertyInt64> eq <PropertyByte>> "
            + "and <<PropertySByte> eq <PropertyDecimal>>>>");
  }

  @Test
  public void filterSimpleSameBinaryBinaryBinaryPriority() throws Exception {
    testFilter.runOnETAllPrim("1 add 2 add 3 add 4").isCompr("<<< <1> add   <2>> add  <3>>  add <4>>");
    testFilter.runOnETAllPrim("1 add 2 add 3 div 4").isCompr("<<  <1> add   <2>> add <<3>   div <4>>>");
    testFilter.runOnETAllPrim("1 add 2 div 3 add 4").isCompr("<<  <1> add  <<2>  div  <3>>> add <4>>");
    testFilter.runOnETAllPrim("1 add 2 div 3 div 4").isCompr("<   <1> add <<<2>  div  <3>>  div <4>>>");
    testFilter.runOnETAllPrim("1 div 2 add 3 add 4").isCompr("<<< <1> div   <2>> add  <3>>  add <4>>");
    testFilter.runOnETAllPrim("1 div 2 add 3 div 4").isCompr("<<  <1> div   <2>> add <<3>   div <4>>>");
    testFilter.runOnETAllPrim("1 div 2 div 3 add 4").isCompr("<<< <1> div   <2>> div  <3>>  add <4>>");
    testFilter.runOnETAllPrim("1 div 2 div 3 div 4").isCompr("<<< <1> div   <2>> div  <3>>  div <4>>");
  }

  @Test
  public void testFunctionImport_VarParameters() {

    // no input
    testRes.run("FINRTInt16()")
    .isFunctionImport("FINRTInt16")
    .isFunction("UFNRTInt16")
    .isType(PropertyProvider.nameInt16);

    // one input
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
    .isFunctionImport("FICRTETTwoKeyNavParam")
    .isFunction("UFCRTETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav);

    // two input
    testRes.run("FICRTStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
    .isFunctionImport("FICRTStringTwoParam")
    .isFunction("UFCRTStringTwoParam")
    .isType(PropertyProvider.nameString);
  }

  @Test
  public void testFunctionImport_VarReturning() {
    // returning primitive
    testRes.run("FINRTInt16()")
    .isFunctionImport("FINRTInt16")
    .isFunction("UFNRTInt16")
    .isType(PropertyProvider.nameInt16, false);

    // returning collection of primitive
    testRes.run("FICRTCollStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
    .isFunctionImport("FICRTCollStringTwoParam")
    .isFunction("UFCRTCollStringTwoParam")
    .isType(PropertyProvider.nameString, true);

    // returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)")
    .isFunctionImport("FICRTCTAllPrimTwoParam")
    .isFunction("UFCRTCTAllPrimTwoParam")
    .isType(ComplexTypeProvider.nameCTAllPrim, false);

    // returning collection of complex
    testRes.run("FICRTCollCTTwoPrim()")
    .isFunctionImport("FICRTCollCTTwoPrim")
    .isFunction("UFCRTCollCTTwoPrim")
    .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    // returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
    .isFunctionImport("FICRTETTwoKeyNavParam")
    .isFunction("UFCRTETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false);

    // returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)")
    .isFunctionImport("FICRTCollESTwoKeyNavParam")
    .isFunction("UFCRTCollETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, true);
  }

  @Test
  public void testFunctionImportChain() {

    // test chain; returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTCTAllPrimTwoParam")
    .isFunction("UFCRTCTAllPrimTwoParam")
    .isType(ComplexTypeProvider.nameCTAllPrim, false)
    .isParameter(0, "ParameterString", "'ABC'")
    .isParameter(1, "ParameterInt16", "1")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    // test chains; returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTETTwoKeyNavParam")
    .isFunction("UFCRTETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')")
    .at(0)
    .isFunctionImport("FICRTCollESTwoKeyNavParam")
    .isFunction("UFCRTCollETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'ABC'");

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')/PropertyInt16")
    .at(0)
    .isFunctionImport("FICRTCollESTwoKeyNavParam")
    .isFunction("UFCRTCollETTwoKeyNavParam")
    .isType(EntityTypeProvider.nameETTwoKeyNav, false)
    .isParameter(0, "ParameterInt16", "1")
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'ABC'")
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

  }

  @Test
  public void testMetaData() throws Exception {

    // Parsing the fragment may be used if a uri has to be parsed on the consumer side.
    // On the producer side this feature is currently not supported, so the context fragment
    // part is only available as text.

    testUri.run("$metadata")
    .isKind(UriInfoKind.metadata);

    testUri.run("$metadata", "$format=atom")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom");

    // with context (client usage)

    testUri.run("$metadata", null, "$ref")
    .isKind(UriInfoKind.metadata)
    .isFragmentText("$ref");

    testUri.run("$metadata", "$format=atom", "$ref")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("$ref");

    testUri.run("$metadata", "$format=atom", "Collection($ref)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("Collection($ref)");

    testUri.run("$metadata", "$format=atom", "Collection(Edm.EntityType)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("Collection(Edm.EntityType)");

    testUri.run("$metadata", "$format=atom", "Collection(Edm.ComplexType)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("Collection(Edm.ComplexType)");

    testUri.run("$metadata", "$format=atom", "SINav")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav");

    testUri.run("$metadata", "$format=atom", "SINav/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "SINav/NavPropertyETKeyNavOne")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/NavPropertyETKeyNavOne");

    testUri.run("$metadata", "$format=atom", "SINav/NavPropertyETKeyNavMany(1)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/NavPropertyETKeyNavMany(1)");

    testUri.run("$metadata", "$format=atom", "SINav/NavPropertyETKeyNavOne/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "SINav/NavPropertyETKeyNavMany(1)/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "SINav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("SINav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata", "$format=atom",
        "SINav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "olingo.odata.test1.ETAllKey")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("olingo.odata.test1.ETAllKey");

    testUri.run("$metadata", "$format=atom", "ESTwoPrim/$deletedEntity")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESTwoPrim/$deletedEntity");

    testUri.run("$metadata", "$format=atom", "ESTwoPrim/$link")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESTwoPrim/$link");

    testUri.run("$metadata", "$format=atom", "ESTwoPrim/$deletedLink")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESTwoPrim/$deletedLink");

    testUri.run("$metadata", "$format=atom", "ESKeyNav")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/NavPropertyETKeyNavOne")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/NavPropertyETKeyNavOne");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/NavPropertyETKeyNavMany(1)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/NavPropertyETKeyNavMany(1)");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/NavPropertyETKeyNavOne/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/NavPropertyETKeyNavMany(1)/PropertyInt16")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata", "$format=atom",
        "ESKeyNav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run(
        "$metadata", "$format=atom", "ESKeyNav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/olingo.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata", "$format=atom", "ESKeyNav(PropertyInt16,PropertyString)")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav(PropertyInt16,PropertyString)");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/$entity")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/$entity");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/$delta")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/$delta");

    testUri.run("$metadata", "$format=atom", "ESKeyNav/(PropertyInt16,PropertyString)/$delta")
    .isKind(UriInfoKind.metadata)
    .isFormatText("atom")
    .isFragmentText("ESKeyNav/(PropertyInt16,PropertyString)/$delta");

  }

  @Test
  public void testRef() throws Exception {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/$ref");
  }

  @Test
  public void testSingleton() {
    // plain singleton
    testRes.run("SINav")
    .isSingleton("SINav")
    .isType(EntityTypeProvider.nameETTwoKeyNav);
  }

  @Test
  public void testNavigationProperty() {

    // plain entity set ...

    // with navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    // with navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
    .at(2).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property -> navigation property -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
    .at(2).isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    // with navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'1'");

    // with navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/PropertyString")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'1'")
    .at(2).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property(key) -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/NavPropertyETKeyNavOne")
    .at(0).isEntitySet("ESKeyNav")
    .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
    .isKeyPredicate(0, "PropertyInt16", "1")
    .isKeyPredicate(1, "PropertyString", "'1'")
    .at(2).isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    // with navigation property(key) -> navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavOne/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .at(3).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // with navigation property(key) -> navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(3).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void testSingleton_Property() {

    // plain singleton ...

    // with property
    testRes.run("SINav/PropertyInt16")
    .at(0)
    .isSingleton("SINav")
    .isType(EntityTypeProvider.nameETTwoKeyNav)
    .at(1)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    // with complex property
    testRes.run("SINav/PropertyComp")
    .at(0)
    .isSingleton("SINav")
    .isType(EntityTypeProvider.nameETTwoKeyNav)
    .at(1)
    .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    // with two properties
    testRes.run("SINav/PropertyComp/PropertyInt16")
    .at(0)
    .isSingleton("SINav")
    .isType(EntityTypeProvider.nameETTwoKeyNav)
    .at(1)
    .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
    .at(2)
    .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

  }

  @Test
  public void testValue() throws Exception {
    testUri.run("ESAllPrim(1)/PropertyString/$value");
  }

  @Test(expected = UriValidationException.class)
  public void testMemberStartingWithCastFailOnValidation1() throws Exception {
    // on EntityType entry
    testUri.run("ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')",
        "$filter=olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .goFilter().root().isMember()
        .isMemberStartType(EntityTypeProvider.nameETBaseTwoKeyNav).goPath()
        // .at(0)
        // .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        // .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .at(0).isType(PropertyProvider.nameDate);
  }

  @Test(expected = UriValidationException.class)
  public void testMemberStartingWithCastFailOnValidation2() throws Exception {
    testUri.run("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')",
        "$filter=olingo.odata.test1.CTBase/AdditionalPropString")
        .goFilter().root().isMember()
        .isMemberStartType(ComplexTypeProvider.nameCTBase).goPath()
        // .at(0)
        // .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(ComplexTypeProvider.nameCTTwoPrim, false)
        // .isTypeFilterOnEntry(ComplexTypeProvider.nameCTBase)
        .at(0).isType(PropertyProvider.nameString);
  }

  @Test
  public void testMemberStartingWithCast() throws Exception {

    // on EntityType collection
    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate")
    .goFilter().root().isMember()
    .isMemberStartType(EntityTypeProvider.nameETBaseTwoKeyNav).goPath()
    // .at(0)
    // .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
    // .isType(EntityTypeProvider.nameETTwoKeyNav, true)
    // .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
    .at(0).isType(PropertyProvider.nameDate);

    // on Complex collection
    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')",
        "$filter=olingo.odata.test1.CTBase/AdditionalPropString")
        .goFilter().root().isMember()
        .isMemberStartType(ComplexTypeProvider.nameCTBase).goPath()
        // .at(0)
        // .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(ComplexTypeProvider.nameCTTwoPrim, true)
        // .isTypeFilterOnCollection(ComplexTypeProvider.nameCTBase)
        .at(0).isType(PropertyProvider.nameString);

  }

  @Test
  public void testComplexTypeCastFollowingAsCollection() throws Exception {
    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')/olingo.odata.test1.CTBase");
  }

  @Test
  public void testAlias() throws Exception {
    testUri.run("ESAllPrim", "$filter=PropertyInt16 eq @p1&@p1=1")
    .goFilter().is("<<PropertyInt16> eq <@p1>>");
  }

  @Test
  public void lambda() throws Exception {
    testFilter.runOnETTwoKeyNav("CollPropertyComp/all(l:true)")
        .is("<CollPropertyComp/<ALL;<true>>>");

    testFilter.runOnETTwoKeyNav("CollPropertyComp/all(x:x/PropertyInt16 eq 2)")
        .is("<CollPropertyComp/<ALL;<<x/PropertyInt16> eq <2>>>>");

    testFilter.runOnETTwoKeyNav("CollPropertyComp/any(l:true)")
        .is("<CollPropertyComp/<ANY;<true>>>");
    testFilter.runOnETTwoKeyNav("CollPropertyComp/any()")
        .is("<CollPropertyComp/<ANY;>>");
  }

  @Test
  public void customQueryOption() throws Exception {
    testUri.run("ESTwoKeyNav", "custom")
    .isCustomParameter(0, "custom", "");
    testUri.run("ESTwoKeyNav", "custom=ABC")
    .isCustomParameter(0, "custom", "ABC");
  }

  @Test
  @Ignore("Geo types are not supported yet.")
  public void geo() throws Exception {
    testFilter.runOnETAllPrim("geo.distance(PropertySByte,PropertySByte)")
    .is("<geo.distance(<PropertySByte>,<PropertySByte>)>")
    .isMethod(MethodKind.GEODISTANCE, 2);
    testFilter.runOnETAllPrim("geo.length(PropertySByte)")
    .is("<geo.length(<PropertySByte>)>")
    .isMethod(MethodKind.GEOLENGTH, 1);
    testFilter.runOnETAllPrim("geo.intersects(PropertySByte,PropertySByte)")
    .is("<geo.intersects(<PropertySByte>,<PropertySByte>)>")
    .isMethod(MethodKind.GEOINTERSECTS, 2);
  }

  private final String encode(final String uriPart) {
    return uriPart.replaceAll(":", "%3A");
  }
}
