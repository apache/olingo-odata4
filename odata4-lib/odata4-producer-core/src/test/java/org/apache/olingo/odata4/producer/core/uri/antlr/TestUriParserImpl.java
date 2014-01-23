/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.odata4.producer.core.uri.antlr;

// TODO after adding the external API to the URI processing class this unit test require a mayor rework

import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriResourceKind;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SystemQueryOptionEnum;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.testutil.FilterValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriResourceValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriValidator;
import org.junit.Test;

public class TestUriParserImpl {
  Edm edm = null;
  private final String PropertyBoolean = "PropertyBoolean=true";
  private final String PropertyByte = "PropertyByte=1";

  private final String PropertyDate = "PropertyDate=2013-09-25";
  private final String PropertyDateTimeOffset = "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00";
  private final String PropertyDecimal = "PropertyDecimal=12";
  private final String PropertyDuration = "PropertyDuration=duration'P10DT5H34M21.123456789012S'";
  private final String PropertyGuid = "PropertyGuid=12345678-1234-1234-1234-123456789012";
  private final String PropertyInt16 = "PropertyInt16=1";
  private final String PropertyInt32 = "PropertyInt32=12";
  private final String PropertyInt64 = "PropertyInt64=64";
  private final String PropertySByte = "PropertySByte=1";
  private final String PropertyString = "PropertyString='ABC'";
  private final String PropertyTimeOfDay = "PropertyTimeOfDay=12:34:55.123456789012";

  private final String allKeys = PropertyString + "," + PropertyInt16 + "," + PropertyBoolean + "," + PropertyByte
      + "," + PropertySByte + "," + PropertyInt32 + "," + PropertyInt64 + "," + PropertyDecimal + "," + PropertyDate
      + "," + PropertyDateTimeOffset + "," + PropertyDuration + "," + PropertyGuid + "," + PropertyTimeOfDay;
  FilterValidator testFilter = null;
  UriResourceValidator testPath = null;
  UriValidator testUri = null;

  public TestUriParserImpl() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());

    testUri = new UriValidator().setEdm(edm);
    testPath = new UriResourceValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);

  }

  @Test
  public void test() {
    // use this method for error analysis
    testPath.run("ESAllKey(" + allKeys + ")")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyString", "'ABC'")
        .isKeyPredicate(1, "PropertyInt16", "1");
  }

  @Test
  public void testActionImport() {
    
    testPath.run("AIRTPrimParam")
        .isUriPathInfoKind(UriResourceKind.action)
        .isType(EdmTechProvider.nameString);

    testPath.run("AIRTPrimCollParam")
        .isUriPathInfoKind(UriResourceKind.action)
        .isType(EdmTechProvider.nameString)
        .isCollection(true);

    testPath.run("AIRTETParam")
        .isUriPathInfoKind(UriResourceKind.action)
        .isType(EdmTechProvider.nameETTwoKeyTwoPrim)
        .isCollection(false);

    testPath.run("AIRTETCollAllPrimParam")
        .isUriPathInfoKind(UriResourceKind.action)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isCollection(true);

  }

  @Test
  public void testAll() {
    testUri.run("$all")
        .isKind(UriInfoKind.all);
  }

  @Test
  public void testBatch() {
    testUri.run("$batch").isKind(UriInfoKind.batch);
  }

  //@Test
  public void testBoundFunctionImport_VarBinding() {

    // on primitive

    testPath.run("ESAllPrim(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETAllPrim)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.simpleProperty)
        .isType(EdmTechProvider.nameString);

    // on collection of primitive
    testPath.run("ESCollAllPrim(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.simpleProperty)
        .isType(EdmTechProvider.nameString);

    // on complex
    testPath.run("ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // on collection of complex
    testPath.run("ESKeyNav(1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameCTPrimComp)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETAllPrim);

    // on entity
    testPath.run("ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')"
        + "/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // on collection of entity
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1).isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void testBoundFunction_Overloading() {
    // on ESTwoKeyNav
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with string parameter
    testPath.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with string parameter
    testPath.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void testBoundFunctionImport_VarParameters() {
    String esTwoKeyNav = "ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')";

    // no input
    testPath.run("ESKeyNav(1)/com.sap.odata.test1.BFCETKeyNavRTETKeyNav()")
        .at(0).isUriPathInfoKind(UriResourceKind.entitySet)
        .at(1).isUriPathInfoKind(UriResourceKind.function);

    // one input
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')")
        .at(0).isUriPathInfoKind(UriResourceKind.entitySet)
        .at(1).isUriPathInfoKind(UriResourceKind.function);

    // two input
    /* TODO extend technical reference scenario */
  }

  //@Test
  public void testBoundFunctionImport_VarRetruning() {
    
    String esTwoKeyNav = "ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')";

    // returning primitive
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(false);

    // returning collection of primitive
    testPath.run(esTwoKeyNav + "/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(true);

    // returning single complex
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isCollection(false);

    // returning collection of complex
    testPath.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isCollection(true);

    // returning single entity
    testPath.run(
        esTwoKeyNav + "/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false);

    // returning collection of entity (aka entitySet)
    testPath.run(esTwoKeyNav + "/com.sap.odata.test1.BFCSINavRTESTwoKeyNav()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true);
  }

  @Test
  public void testCount() {
    // count entity set

    testPath.run("ESAllPrim/$count")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETAllPrim)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of complex

    testPath.run("ESKeyNav(1)/CollPropertyComplex/$count")
        .at(0)
        .isType(EdmTechProvider.nameETKeyNav)
        .at(1)
        .isType(EdmTechProvider.nameCTPrimComp)
        .isCollection(true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of primitive
    testPath.run("ESCollAllPrim(1)/CollPropertyString/$count")
        .at(1)
        .isType(EdmTechProvider.nameString)
        .isCollection(true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void testCrossJoin() {
    testUri.run("$crossjoin(ESAllKey)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESAllKey"));

    testUri.run("$crossjoin(ESAllKey,ESTwoPrim)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESAllKey", "ESTwoPrim"));
  }

  //@Test
  public void testEntity() {

    // simple entity set
    testUri.run("$entity?$id=ESAllPrim").isKind(UriInfoKind.entityId)
        .isKind(UriInfoKind.entityId)
        .isSQO_Id("ESAllPrim");

    // simple entity set; $format before $id
    testUri.run("$entity?$format=xml&$id=ETAllPrim").isKind(UriInfoKind.entityId)
        .isSQO_Format("xml")
        .isSQO_Id("ETAllPrim");
    testUri.run("$entity?$format=xml&abc=123&$id=ESAllKey").isKind(UriInfoKind.entityId)
        .isSQO_Format("xml")
        .isCustomParameter(0, "abc", "123")
        .isSQO_Id("ETAllPrim");

    // simple entity set; $format after $id
    testUri.run("$entity?$id=ETAllPrim&$format=xml").isKind(UriInfoKind.entityId)
        .isSQO_Id("ETAllPrim")
        .isSQO_Format("xml");

    // simple entity set; $format and custom parameter after $id
    testUri.run("$entity?$id=ETAllPrim&$format=xml&abc=123").isKind(UriInfoKind.entityId)
        .isSQO_Id("ETAllPrim")
        .isSQO_Format("xml")
        .isCustomParameter(0, "abc", "123");

    // simple entity set; $format before $id and custom parameter after $id
    testUri.run("$entity?$format=xml&$id=ETAllPrim&abc=123").isKind(UriInfoKind.entityId)
        .isSQO_Format("xml")
        .isSQO_Id("ETAllPrim")
        .isCustomParameter(0, "abc", "123");

    // simple entity set; with qualifiedentityTypeName
    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?$id=ESBase")
        .isSQO_Id("ESBase");

    // simple entity set; with qualifiedentityTypeName; with filter
    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?$filter=PropertyInt16 eq 123&$id=ESAllKey")
        .isSQO_Id("ESAllKey")
        .goFilter(0)
        .is("<PropertyInt16 eq 123>");

    // simple entity set; with qualifiedentityTypeName;
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim")
        .isKind(UriInfoKind.entityId)
        .isSQO_Id("ESTwoPrim")
        .isEntityType(EdmTechProvider.nameETBase);

    // simple entity set; with qualifiedentityTypeName; with format
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$format=atom");

    // simple entity set; with qualifiedentityTypeName; with select
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$select=*");

    // simple entity set; with qualifiedentityTypeName; with expand
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$expand=*");

    // simple entity set; with qualifiedentityTypeName; with 2xformat(before and after), expand, filter

    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?"
        + "$format=xml&$expand=*&abc=123&$id=ESBase&xyz=987&$filter=PropertyInt16 eq 123&$format=atom&$select=*")
        .isSQO_Format("xml")
        /* .isQueryParameter(SystemQueryParameter.EXPAND.toString(), 0, "*") */
        .isCustomParameter(0, "abc", "123")
        .isSQO_Id("ESAllKey")
        .isCustomParameter(1, "xyz", "987");

    /* .isQueryParameter(SsystemQueryParameter.SELECT.toString(), 0, "*") *//* ; */
  }

  @Test
  public void testEntitySet() {

    // plain entity set
    testPath.run("ESAllPrim")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(new FullQualifiedName("com.sap.odata.test1", "ETAllPrim"));

    // with one key; simple key notation
    testPath.run("ESAllPrim(1)")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(new FullQualifiedName("com.sap.odata.test1", "ETAllPrim"))
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with one key; name value key notation
    testPath.run("ESAllPrim(PropertyInt16=1)")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with two keys
    testPath.run("ESTwoKeyTwoPrim(PropertyInt16=1, PropertyString='ABC')")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'");

    // with all keys
    testPath.run("ESAllKey(" + allKeys + ")")
        .isUriPathInfoKind(UriResourceKind.entitySet)
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
        .isKeyPredicate(10, "PropertyDuration", "duration'P10DT5H34M21.123456789012S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55.123456789012");
  }

  //@Test
  public void testEntitySet_NavigationPropperty() {

    // plain entity set ...

    // with navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        /*.isProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)*/
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with navigation property -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isNav("NavPropertyETTwoKeyNavOne")
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(2)
        .isType(EdmTechProvider.nameString)
        .isProperty("PropertyString", EdmTechProvider.nameString);

    // with navigation property -> navigation property -> navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isType(EdmTechProvider.nameETKeyNav);

    // with navigation property(key)
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)");

    // with navigation property(key) -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)/PropertyString");

    // with navigation property(key) -> navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)/NavPropertyETKeyNavOne");

    // with navigation property(key) -> navigation property(key)
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)/NavPropertyETKeyNavMany(1)");

    // with navigation property(key) -> navigation property -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)/NavPropertyETKeyNavOne/PropertyString")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isNav("NavPropertyETTwoKeyNavMany")
        .at(1)
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isType(EdmTechProvider.nameETTwoKeyNav)
         //.isType(EdmTechProvider.nameETKeyNav)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.navigationProperty)
        .isType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameString)
        .at(3)
        .isProperty("PropertyString", EdmTechProvider.nameString);

    // with navigation property(key) -> navigation property(key) -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(1)/NavPropertyETKeyNavMany(1)/PropertyString");

  }

  public void testEntitySet_Property() {

    // plain entity set ...

    // with property
    testPath.run("ESAllPrim(1)/PropertyString")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isProperty("PropertyString", EdmTechProvider.nameInt16);

    // with complex property
    testPath.run("ESCompAllPrim(1)/PropertyComplex")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim);

    // with two properties
    testPath.run("ESCompAllPrim(1)/PropertyComplex/PropertyString")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim)
        .at(2)
        .isProperty("PropertyString", EdmTechProvider.nameString);
  }

  //@Test
  public void testEntitySet_TypeFilter() {

    // filter
    testPath.run("ESTwoPrim/com.sap.odata.test1.ETBase")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isCollection(true);

    // filter before key predicate
    testPath.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .at(0)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    // filter before key predicate; property of sub type
    testPath.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)/AdditionalPropertyString_5")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isType(EdmTechProvider.nameString)
        .isProperty("AdditionalPropertyString_5", EdmTechProvider.nameString)
        .isCollection(false);

    // filter after key predicate
    testPath.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(null)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    // filter after key predicate; property of sub type
    testPath.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase/AdditionalPropertyString_5")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(null)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty("AdditionalPropertyString_5", EdmTechProvider.nameString)
        .at(1)
        .isCollection(false)
        .isType(EdmTechProvider.nameString);

  }

  @Test
  public void testErrors() {
    // the following is wrong and must throw an error behind an Action are not () allowed
    // test.run("AIRTPrimParam()");

  }

  @Test
  public void testFilter() {
    testPath.run("ESAllPrim?$filter=1")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isFilterString("1");

  }

  //@Test
  public void testFilterComplexMixedPriority() {
    testFilter.runESabc("a      or c      and e     ").isCompr("< a       or < c       and  e      >>");
    testFilter.runESabc("a      or c      and e eq f").isCompr("< a       or < c       and <e eq f>>>");
    testFilter.runESabc("a      or c eq d and e     ").isCompr("< a       or <<c eq d> and  e      >>");
    testFilter.runESabc("a      or c eq d and e eq f").isCompr("< a       or <<c eq d> and <e eq f>>>");
    testFilter.runESabc("a eq b or c      and e     ").isCompr("<<a eq b> or < c       and  e      >>");
    testFilter.runESabc("a eq b or c      and e eq f").isCompr("<<a eq b> or < c       and <e eq f>>>");
    testFilter.runESabc("a eq b or c eq d and e     ").isCompr("<<a eq b> or <<c eq d> and  e      >>");
    testFilter.runESabc("a eq b or c eq d and e eq f").isCompr("<<a eq b> or <<c eq d> and <e eq f>>>");
  }

  //@Test
  public void testFilterSimpleSameBinaryBinaryBinaryPriority() {

    testFilter.runESabc("1 add 2 add 3 add 4").is("<<<1 add 2> add 3> add 4>");
    testFilter.runESabc("1 add 2 add 3 div 4").is("<<1 add 2> add <3 div 4>>");
    testFilter.runESabc("1 add 2 div 3 add 4").is("<<1 add <2 div 3>> add 4>");
    testFilter.runESabc("1 add 2 div 3 div 4").is("<1 add <<2 div 3> div 4>>");
    testFilter.runESabc("1 div 2 add 3 add 4").is("<<<1 div 2> add 3> add 4>");
    testFilter.runESabc("1 div 2 add 3 div 4").is("<<1 div 2> add <3 div 4>>");
    testFilter.runESabc("1 div 2 div 3 add 4").is("<<<1 div 2> div 3> add 4>");
    testFilter.runESabc("1 div 2 div 3 div 4").is("<<<1 div 2> div 3> div 4>");

  }

  @Test
  public void testFunctionImport_VarParameters() {

    // no input
    testPath.run("FINRTInt16()")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString);

    // one input
    testPath.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // two input
    testPath.run("FICRTStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString);
  }

  @Test
  public void testFunctionImport_VarRetruning() {
    // returning primitive
    testPath.run("FINRTInt16()")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(false);

    // returning collection of primitive
    testPath.run("FICRTCollStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(true);

    // returning single complex
    testPath.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTAllPrim)
        .isCollection(false);

    // returning collection of complex
    testPath.run("FICRTCollCTTwoPrim()")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isCollection(true);

    // returning single entity
    testPath.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false);

    // returning collection of entity (aka entitySet)
    testPath.run("FICRTESTwoKeyNavParam(ParameterInt16=1)")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true);
  }

  //@Test
  public void testFunctionImportChain() {
    // test chain; returning single complex
    testPath.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)/PropertyInt16")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTAllPrim)
        /*.isProperty("PropertyInt16", EdmTechProvider.nameInt16)*/
        .isCollection(false);

    // test chains; returning single entity
    testPath.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameInt16)
        .isProperty("PropertyInt16", EdmTechProvider.nameInt16)
        .isCollection(false);

    // test chains; returning collection of entity (aka entitySet)
    testPath.run("FICRTESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false);

    // test chains; returning collection of entity (aka entitySet)
    testPath.run("FICRTESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')/PropertyInt16")
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameInt16)
        .isCollection(false);

  }

  //@Test
  public void testMetaData() {
    testUri.run("$metadata")
        .isKind(UriInfoKind.metadata);

    testUri.run("$metadata?$format=atom")
        .isKind(UriInfoKind.metadata)
        .isSQO_Format("atom");

    /*
     * // with context (client usage)
     * testUri.run("$metadata?$format=atom#$ref");
     * testUri.run("$metadata?$format=atom#Collection($ref)");
     * testUri.run("$metadata?$format=atom#Collection(Edm.EntityType)");
     * testUri.run("$metadata?$format=atom#Collection(Edm.ComplexType)");
     * 
     * testUri.run("$metadata?$format=atom#SINav");
     * testUri.run("$metadata?$format=atom#SINav/PropertyInt16");
     * 
     * testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavOne");
     * testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavMany(1)");
     * 
     * testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavOne/PropertyInt16");
     * testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavMany(1)/PropertyInt16");
     * 
     * testUri.run("$metadata?$format=atom#SINav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");
     * testUri.run("$metadata?$format=atom#SINav/
     * com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16")
     * ;
     * 
     * testUri.run("$metadata?$format=atom#com.sap.odata.test1.ETAllKey");
     * 
     * testUri.run("$metadata?$format=atom#ESTwoPrim/$deletedEntity");
     * testUri.run("$metadata?$format=atom#ESTwoPrim/$link");
     * testUri.run("$metadata?$format=atom#ESTwoPrim/$deletedLink");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav");
     * testUri.run("$metadata?$format=atom#ESKeyNav/PropertyInt16");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavOne");
     * testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavMany(1)");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavOne/PropertyInt16");
     * testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavMany(1)/PropertyInt16");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav/com.sap.odata.
     * test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");
     * testUri.run("$metadata?$format=atom"
     * + "#ESKeyNav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav(PropertyInt16,PropertyString)");
     * testUri.run("$metadata?$format=atom#ESKeyNav/$entity");
     * testUri.run("$metadata?$format=atom#ESKeyNav/$delta");
     * 
     * testUri.run("$metadata?$format=atom#ESKeyNav/(PropertyInt16,PropertyString)/$delta");
     */
  }

  @Test
  public void testRef() {
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/$ref");
  }

  @Test
  public void testSingleton() {
    // plain singleton
    testPath.run("SINav")
        .isUriPathInfoKind(UriResourceKind.singleton)
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void testSingleton_NavigationProperty() {

    // plain entity set ...

    // with navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne");

    // with navigation property -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString");

    // with navigation property -> navigation property -> navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne");

    // with navigation property(key)
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')");

    // with navigation property(key) -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/PropertyString");

    // with navigation property(key) -> navigation property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/NavPropertyETKeyNavOne");

    // with navigation property(key) -> navigation property(key)
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)");

    // with navigation property(key) -> navigation property -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavOne/PropertyString");

    // with navigation property(key) -> navigation property(key) -> property
    testPath.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)/PropertyString");
  }

  //@Test
  public void testSingleton_Property() {

    // plain singleton ...

    // with property
    testPath.run("SINav/PropertyInt16")
        .isUriPathInfoKind(UriResourceKind.singleton)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isType(EdmTechProvider.nameInt16);

    // with complex property
    testPath.run("SINav/PropertyComplex");

    // with two properties
    testPath.run("SINav/PropertyComplex/PropertyString");

  }

  @Test
  public void testValue() {
    testPath.run("ESAllPrim(1)/PropertyString/$value");
  }

  @Test
  public void textFilterMember() {
    
    // TODO extend
  }

  @Test
  public void textFilterMethodCall() {
    testFilter.runESabc("concat('a','b')").is("<concat('a','b')>");
    // TODO extend
  }

}
