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
import org.apache.olingo.odata4.producer.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.testutil.FilterValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriResourceValidator;
import org.apache.olingo.odata4.producer.core.testutil.UriValidator;
import org.apache.olingo.odata4.producer.core.uri.UriParserException;
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

  UriValidator testUri = null;
  UriResourceValidator testRes = null;
  FilterValidator testFilter = null;

  public TestUriParserImpl() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());
    testUri = new UriValidator().setEdm(edm);
    testRes = new UriResourceValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);
  }

  @Test
  public void testBoundFunctionImport_VarParameters() {

    // no input
    testRes.run("ESKeyNav(1)/com.sap.odata.test1.BFCETKeyNavRTETKeyNav()")
        .at(0).isUriPathInfoKind(UriResourceKind.entitySet)
        .at(1).isUriPathInfoKind(UriResourceKind.function);

    // one input
    testRes.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')")
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

    String esTwoKeyNav = "ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')";

    // returning primitive
    testRes.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(false);

    // returning collection of primitive
    testRes.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameString)
        .isCollection(true);

    // returning single complex
    testRes.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isCollection(false);

    // returning collection of complex
    testRes.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isCollection(true);

    // returning single entity
    testRes.run(
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
    testRes.run(esTwoKeyNav + "/com.sap.odata.test1.BFCSINavRTESTwoKeyNav()")
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
  public void runActionImport_VarReturnType() {

    testRes.run("AIRTPrimParam").isKind(UriInfoKind.resource)
        .first()
        .isActionImport("AIRTPrimParam")
        .isAction("UARTPrimParam")
        .isType(EdmTechProvider.nameString, false);

    testRes.run("AIRTPrimCollParam").isKind(UriInfoKind.resource)
        .first()
        .isActionImport("AIRTPrimCollParam")
        .isAction("UARTPrimCollParam")
        .isType(EdmTechProvider.nameString, true);

    testRes.run("AIRTCompParam").isKind(UriInfoKind.resource)
        .first()
        .isActionImport("AIRTCompParam")
        .isAction("UARTCompParam")
        .isType(EdmTechProvider.nameCTTwoPrim, false);

    testRes.run("AIRTCompCollParam").isKind(UriInfoKind.resource)
        .first()
        .isActionImport("AIRTCompCollParam")
        .isAction("UARTCompCollParam")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testRes.run("AIRTETParam").isKind(UriInfoKind.resource)
        .first()
        .isActionImport("AIRTETParam")
        .isAction("UARTETParam")
        .isType(EdmTechProvider.nameETTwoKeyTwoPrim, false);

    // TODO add error test
    // testUri.run("AIRTPrimParam/invalidElement").isKind(UriInfoKind.resource).goPath().
    // isUriPathInfoKind(UriResourceKind.action);
    // testUri.run("InvalidAction");
  }

  @Test
  public void runCount() {

    // count entity set
    testRes.run("ESAllPrim/$count")
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETAllPrim)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of complex
    testRes.run("ESKeyNav(1)/CollPropertyComplex/$count")
        .at(0)
        .isType(EdmTechProvider.nameETKeyNav)
        .at(1)
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.count);

    // count on collection of primitive
    testRes.run("ESCollAllPrim(1)/CollPropertyString/$count")
        .at(1)
        .isType(EdmTechProvider.nameString, true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runCrossJoin() {
    testUri.run("$crossjoin(ESAllKey)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESAllKey"));

    testUri.run("$crossjoin(ESAllKey,ESTwoPrim)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESAllKey", "ESTwoPrim"));
  }

  @Test
  public void testEntity() {

    // simple entity set
    testUri.run("$entity?$id=ESAllPrim").isKind(UriInfoKind.entityId)
        .isKind(UriInfoKind.entityId)
        .isIdText("ESAllPrim");

    // simple entity set; $format before $id
    testUri.run("$entity?$format=xml&$id=ETAllPrim").isKind(UriInfoKind.entityId)
        .isFormatText("xml")
        .isIdText("ETAllPrim");

    testUri.run("$entity?$format=xml&abc=123&$id=ESAllKey").isKind(UriInfoKind.entityId)
        .isFormatText("xml")
        .isCustomParameter(0, "abc", "123")
        .isIdText("ESAllKey");

    // simple entity set; $format after $id
    testUri.run("$entity?$id=ETAllPrim&$format=xml").isKind(UriInfoKind.entityId)
        .isIdText("ETAllPrim")
        .isFormatText("xml");

    // simple entity set; $format and custom parameter after $id
    testUri.run("$entity?$id=ETAllPrim&$format=xml&abc=123").isKind(UriInfoKind.entityId)
        .isIdText("ETAllPrim")
        .isFormatText("xml")
        .isCustomParameter(0, "abc", "123");

    // simple entity set; $format before $id and custom parameter after $id
    testUri.run("$entity?$format=xml&$id=ETAllPrim&abc=123").isKind(UriInfoKind.entityId)
        .isFormatText("xml")
        .isIdText("ETAllPrim")
        .isCustomParameter(0, "abc", "123");

    // simple entity set; with qualifiedentityTypeName
    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?$id=ESBase")
        .isEntityType(EdmTechProvider.nameETTwoPrim)
        .isIdText("ESBase");

    // simple entity set; with qualifiedentityTypeName; with filter
    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?$filter=PropertyInt16 eq 123&$id=ESAllKey")
        .isIdText("ESAllKey")
        .goFilter().is("<<PropertyInt16> eq <123>>");

    // simple entity set; with qualifiedentityTypeName;
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim")
        .isEntityType(EdmTechProvider.nameETBase)
        .isKind(UriInfoKind.entityId)
        .isIdText("ESTwoPrim");

    // simple entity set; with qualifiedentityTypeName; with format
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$format=atom")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EdmTechProvider.nameETBase)
        .isIdText("ESTwoPrim")
        .isFormatText("atom");

    // simple entity set; with qualifiedentityTypeName; with select
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$select=*")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EdmTechProvider.nameETBase)
        .isIdText("ESTwoPrim")
        .isSelectText("*");

    // simple entity set; with qualifiedentityTypeName; with expand
    testUri.run("$entity/com.sap.odata.test1.ETBase?$id=ESTwoPrim&$expand=*")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EdmTechProvider.nameETBase)
        .isIdText("ESTwoPrim")
        .isExpandText("*");

    // simple entity set; with qualifiedentityTypeName; with 2xformat(before and after), expand, filter
    testUri.run("$entity/com.sap.odata.test1.ETTwoPrim?"
        + "$format=xml&$expand=*&abc=123&$id=ESBase&xyz=987&$filter=PropertyInt16 eq 123&$format=atom&$select=*")
        .isFormatText("atom")
        .isCustomParameter(0, "abc", "123")
        .isIdText("ESBase")
        .isCustomParameter(1, "xyz", "987");
  }

  @Test
  public void testEntitySet() {

    // plain entity set
    testRes.run("ESAllPrim")
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim);

    // with one key; simple key notation
    testRes.run("ESAllPrim(1)")
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with one key; name value key notation
    testRes.run("ESAllPrim(PropertyInt16=1)")
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with two keys
    testRes.run("ESTwoKeyTwoPrim(PropertyInt16=1, PropertyString='ABC')")
        .isEntitySet("ESTwoKeyTwoPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'");

    // with all keys
    testRes.run("ESAllKey(" + allKeys + ")")
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
        .isKeyPredicate(10, "PropertyDuration", "duration'P10DT5H34M21.123456789012S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55.123456789012");
  }

  @Test
  public void testEntitySet_NavigationPropperty() {

    // plain entity set ...

    // with navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
        .at(0)
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
        .at(0)
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .at(2)
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property -> navigation property -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
        .at(0)
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(2)
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav);

    // with navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)")
        .at(0)
        .isEntitySet("ESKeyNav")
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/PropertyString").at(0)
        .at(0)
        .isEntitySet("ESKeyNav")
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(2)
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property(key) -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavOne")
        .isEntitySet("ESKeyNav")
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(2)
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    // with navigation property(key) -> navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavMany(1)")
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav)
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(2)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavOne/PropertyString")
        .at(0)
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav)
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(2)
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav)
        .at(3)
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property(key) -> navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETKeyNavMany(1)/NavPropertyETKeyNavMany(1)/PropertyString")
        .at(0)
        .isEntitySet("ESKeyNav")
        .isType(EdmTechProvider.nameETKeyNav)
        .at(1)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(2)
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(3)
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

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
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with complex property
    testRes.run("ESCompAllPrim(1)/PropertyComplex")
        .at(0)
        .isEntitySet("ESCompAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false);

    // with two properties
    testRes.run("ESCompAllPrim(1)/PropertyComplex/PropertyString")
        .at(0)
        .isEntitySet("ESCompAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .at(2)
        .isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);
  }

  @Test
  public void testEntitySet_TypeFilter() {

    // filter
    testRes.run("ESTwoPrim/com.sap.odata.test1.ETBase")
        .at(0)
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .isCollection(true);

    // filter before key predicate
    testRes.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)")
        .at(0)
        .isEntitySet("ESTwoPrim")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .at(0)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    // filter before key predicate; property of sub type
    testRes.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)/AdditionalPropertyString_5")
        .at(0)
        .isEntitySet("ESTwoPrim")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isTypeFilterOnEntry(null)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isType(EdmTechProvider.nameString)
        .isSimpleProperty("AdditionalPropertyString_5", EdmTechProvider.nameString, false);

    // filter after key predicate
    testRes.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase")
        .at(0)
        .isEntitySet("ESTwoPrim")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(null)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    // filter after key predicate; property of sub type
    testRes.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase/AdditionalPropertyString_5")
        .at(0)
        .isEntitySet("ESTwoPrim")
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(null)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(1)
        .isSimpleProperty("AdditionalPropertyString_5", EdmTechProvider.nameString, false)
        .isType(EdmTechProvider.nameString);

  }

  @Test
  public void testFilterComplexMixedPriority() throws UriParserException {
    testFilter.runESabc("a      or c      and e     ").isCompr("< <a>         or < <c>         and  <e>      >>");
    testFilter.runESabc("a      or c      and e eq f").isCompr("< <a>         or < <c>         and <<e> eq <f>>>>");
    testFilter.runESabc("a      or c eq d and e     ").isCompr("< <a>         or <<<c> eq <d>> and  <e>      >>");
    testFilter.runESabc("a      or c eq d and e eq f").isCompr("< <a>         or <<<c> eq <d>> and <<e> eq <f>>>>");
    testFilter.runESabc("a eq b or c      and e     ").isCompr("<<<a> eq <b>> or < <c>         and  <e>      >>");
    testFilter.runESabc("a eq b or c      and e eq f").isCompr("<<<a> eq <b>> or < <c>         and <<e> eq <f>>>>");
    testFilter.runESabc("a eq b or c eq d and e     ").isCompr("<<<a> eq <b>> or <<<c> eq <d>> and  <e>      >>");
    testFilter.runESabc("a eq b or c eq d and e eq f").isCompr("<<<a> eq <b>> or <<<c> eq <d>> and <<e> eq <f>>>>");
  }

  @Test
  public void testFilterSimpleSameBinaryBinaryBinaryPriority() throws UriParserException {

    testFilter.runESabc("1 add 2 add 3 add 4").isCompr("<<< <1> add   <2>> add  <3>>  add <4>>");
    testFilter.runESabc("1 add 2 add 3 div 4").isCompr("<<  <1> add   <2>> add <<3>   div <4>>>");
    testFilter.runESabc("1 add 2 div 3 add 4").isCompr("<<  <1> add  <<2>  div  <3>>> add <4>>");
    testFilter.runESabc("1 add 2 div 3 div 4").isCompr("<   <1> add <<<2>  div  <3>>  div <4>>>");
    testFilter.runESabc("1 div 2 add 3 add 4").isCompr("<<< <1> div   <2>> add  <3>>  add <4>>");
    testFilter.runESabc("1 div 2 add 3 div 4").isCompr("<<  <1> div   <2>> add <<3>   div <4>>>");
    testFilter.runESabc("1 div 2 div 3 add 4").isCompr("<<< <1> div   <2>> div  <3>>  add <4>>");
    testFilter.runESabc("1 div 2 div 3 div 4").isCompr("<<< <1> div   <2>> div  <3>>  div <4>>");

  }

  @Test
  public void testFunctionImport_VarParameters() {

    // no input
    testRes.run("FINRTInt16()")
        .isFunctionImport("FINRTInt16")
        .isFunction("UFNRTInt16")
        .isType(EdmTechProvider.nameString);

    // one input
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // two input
    testRes.run("FICRTStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(EdmTechProvider.nameString);
  }

  @Test
  public void testFunctionImport_VarRetruning() {
    // returning primitive
    testRes.run("FINRTInt16()")
        .isFunctionImport("FINRTInt16")
        .isFunction("UFNRTInt16")
        .isType(EdmTechProvider.nameString, false);

    // returning collection of primitive
    testRes.run("FICRTCollStringTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isFunctionImport("FICRTCollStringTwoParam")
        .isFunction("UFCRTCollStringTwoParam")
        .isType(EdmTechProvider.nameString, true);

    // returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)")
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isFunction("UFCRTCTAllPrimTwoParam")
        .isType(EdmTechProvider.nameCTAllPrim, false);

    // returning collection of complex
    testRes.run("FICRTCollCTTwoPrim()")
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isFunction("UFCRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    // returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav, false);

    // returning collection of entity (aka entitySet)
    testRes.run("FICRTESTwoKeyNavParam(ParameterInt16=1)")
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav, true);
  }

  @Test
  public void testFunctionImportChain() {

    // test chain; returning single complex
    testRes.run("FICRTCTAllPrimTwoParam(ParameterString='ABC',ParameterInt16=1)/PropertyInt16")
        .at(0)
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isFunction("UFCRTCTAllPrimTwoParam")
        .isType(EdmTechProvider.nameCTAllPrim, false)
        .isParameter(0, "ParameterString", "'ABC'")
        .isParameter(1, "ParameterInt16", "1")
        .at(1)
        .isSimpleProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    // test chains; returning single entity
    testRes.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16")
        .at(0)
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isParameter(0, "ParameterInt16", "1")
        .at(1)
        .isSimpleProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')")
        .at(0)
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'");

    // test chains; returning collection of entity (aka entitySet)
    testRes.run("FICRTESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='ABC')/PropertyInt16")
        .at(0)
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'")
        .at(1)
        .isSimpleProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

  }

  @Test
  public void testMetaData() {

    // Parsing the fragment may be used if a uri has to be parsed on the consumer side.
    // On the producer side this feature is currently not supported, so the context fragment
    // part is only available as text.

    testUri.run("$metadata")
        .isKind(UriInfoKind.metadata);

    testUri.run("$metadata?$format=atom")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom");

    // with context (client usage)

    testUri.run("$metadata#$ref")
        .isKind(UriInfoKind.metadata)
        .isFragmentText("$ref");

    testUri.run("$metadata?$format=atom#$ref")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("$ref");

    testUri.run("$metadata?$format=atom#Collection($ref)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("Collection($ref)");

    testUri.run("$metadata?$format=atom#Collection(Edm.EntityType)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("Collection(Edm.EntityType)");

    testUri.run("$metadata?$format=atom#Collection(Edm.ComplexType)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("Collection(Edm.ComplexType)");

    testUri.run("$metadata?$format=atom#SINav")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav");

    testUri.run("$metadata?$format=atom#SINav/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/PropertyInt16");

    testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/NavPropertyETKeyNavOne");

    testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavMany(1)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/NavPropertyETKeyNavMany(1)");

    testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavOne/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata?$format=atom#SINav/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata?$format=atom#SINav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata?$format=atom#SINav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("SINav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata?$format=atom#com.sap.odata.test1.ETAllKey")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("com.sap.odata.test1.ETAllKey");

    testUri.run("$metadata?$format=atom#ESTwoPrim/$deletedEntity")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESTwoPrim/$deletedEntity");

    testUri.run("$metadata?$format=atom#ESTwoPrim/$link")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESTwoPrim/$link");

    testUri.run("$metadata?$format=atom#ESTwoPrim/$deletedLink")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESTwoPrim/$deletedLink");

    testUri.run("$metadata?$format=atom#ESKeyNav")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav");

    testUri.run("$metadata?$format=atom#ESKeyNav/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/PropertyInt16");

    testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/NavPropertyETKeyNavOne");

    testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavMany(1)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/NavPropertyETKeyNavMany(1)");

    testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavOne/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run("$metadata?$format=atom#ESKeyNav/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata?$format=atom#ESKeyNav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavOne/PropertyInt16");

    testUri.run(
        "$metadata?$format=atom#ESKeyNav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/com.sap.odata.test1.ETTwoPrim/NavPropertyETKeyNavMany(1)/PropertyInt16");

    testUri.run("$metadata?$format=atom#ESKeyNav(PropertyInt16,PropertyString)")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav(PropertyInt16,PropertyString)");

    testUri.run("$metadata?$format=atom#ESKeyNav/$entity")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/$entity");

    testUri.run("$metadata?$format=atom#ESKeyNav/$delta")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/$delta");

    testUri.run("$metadata?$format=atom#ESKeyNav/(PropertyInt16,PropertyString)/$delta")
        .isKind(UriInfoKind.metadata)
        .isFormatText("atom")
        .isFragmentText("ESKeyNav/(PropertyInt16,PropertyString)/$delta");

  }

  @Test
  public void testRef() {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/$ref");
  }

  @Test
  public void testSingleton() {
    // plain singleton
    testRes.run("SINav")
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void testNavigationProperty() {

    // plain entity set ...

    // with navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    // with navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .at(2).isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property -> navigation property -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .at(2).isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    // with navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'");

    // with navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property(key) -> navigation property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')/NavPropertyETKeyNavOne")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    // with navigation property(key) -> navigation property(key)
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with navigation property(key) -> navigation property -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavOne/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .at(3).isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);

    // with navigation property(key) -> navigation property(key) -> property
    testRes.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='1')"
        + "/NavPropertyETKeyNavMany(1)/PropertyString")
        .at(0).isEntitySet("ESKeyNav")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'")
        .at(2).isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .at(3).isSimpleProperty("PropertyString", EdmTechProvider.nameString, false);
  }

  @Test
  public void testSingleton_Property() {

    // plain singleton ...

    // with property
    testRes.run("SINav/PropertyInt16")
        .at(0)
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isSimpleProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    // with complex property
    testRes.run("SINav/PropertyComplex")
        .at(0)
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false);

    // with two properties
    testRes.run("SINav/PropertyComplex/PropertyInt16")
        .at(0)
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .at(2)
        .isSimpleProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

  }

  @Test
  public void testValue() {
    testUri.run("ESAllPrim(1)/PropertyString/$value");
  }

}
