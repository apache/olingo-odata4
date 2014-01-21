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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.provider.FullQualifiedName;
import org.apache.olingo.odata4.commons.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriPathInfoKind;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.producer.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.producer.core.testutil.FilterTreeToText;
import org.apache.olingo.odata4.producer.core.testutil.UriResourcePathValidator;
import org.apache.olingo.odata4.producer.core.uri.SystemQueryParameter;
import org.apache.olingo.odata4.producer.core.uri.UriInfoImplPath;
import org.apache.olingo.odata4.producer.core.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata4.producer.core.uri.expression.Expression;
import org.junit.Test;

public class TestUriParserImpl {
  UriResourcePathValidator test = null;
  Edm edm = null;

  private final String PropertyString = "PropertyString='ABC'";
  private final String PropertyInt16 = "PropertyInt16=1";
  private final String PropertyBoolean = "PropertyBoolean=true";
  private final String PropertyByte = "PropertyByte=1";
  private final String PropertySByte = "PropertySByte=1";
  private final String PropertyInt32 = "PropertyInt32=12";
  private final String PropertyInt64 = "PropertyInt64=64";
  private final String PropertyDecimal = "PropertyDecimal=12";
  private final String PropertyDate = "PropertyDate=2013-09-25";
  private final String PropertyDateTimeOffset = "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00";
  private final String PropertyDuration = "PropertyDuration=duration'P10DT5H34M21.123456789012S'";
  private final String PropertyGuid = "PropertyGuid=12345678-1234-1234-1234-123456789012";
  private final String PropertyTimeOfDay = "PropertyTimeOfDay=12:34:55.123456789012";
  private final String allKeys = PropertyString + "," + PropertyInt16 + "," + PropertyBoolean + "," + PropertyByte
      + "," + PropertySByte + "," + PropertyInt32 + "," + PropertyInt64 + "," + PropertyDecimal + "," + PropertyDate
      + "," + PropertyDateTimeOffset + "," + PropertyDuration + "," + PropertyGuid + "," + PropertyTimeOfDay;

  public TestUriParserImpl() {
    test = new UriResourcePathValidator();
    edm = new EdmProviderImpl(new EdmTechTestProvider());
    test.setEdm(edm);
  }

  @Test
  public void test() {

    test.run("ESAllKey(" + allKeys + ")")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyString", "'ABC'")
        .isKeyPredicate(1, "PropertyInt16", "1");
  }

  @Test
  public void testShortUris() {
    //TODO create on validator for these URIs, because the will more complicated in future
    //test.run("$batch").isKind(UriInfoKind.batch);
    //test.run("$all").isKind(UriInfoKind.all);
    //test.run("$crossjoin(abc)").isKind(UriInfoKind.crossjoin);
  }

  @Test
  public void testDollarEntity() {
    // TODO
  }

  @Test
  public void testEntitySet() {
    test.run("ESAllPrim")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isType(new FullQualifiedName("com.sap.odata.test1", "ETAllPrim"));

    // with one key
    test.run("ESAllPrim(1)")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isType(new FullQualifiedName("com.sap.odata.test1", "ETAllPrim"))
        .isKeyPredicate(0, "PropertyInt16", "1");

    test.run("ESAllPrim(PropertyInt16=1)")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1");

    // with two keys
    test.run("ESTwoKeyTwoPrim(PropertyInt16=1, PropertyString='ABC')")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'ABC'");

    // with all keys
    test.run("ESAllKey(" + allKeys + ")")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
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

  public void testEntitySet_Prop() {
    // with property
    test.run("ESAllPrim(1)/PropertyString")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperties(Arrays.asList("PropertyString"));

    // with complex property
    test.run("ESCompAllPrim(1)/PropertyComplex")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "PropertyComplex", EdmTechProvider.nameCTAllPrim);

    // with two properties
    test.run("ESCompAllPrim(1)/PropertyComplex/PropertyString")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "PropertyComplex", EdmTechProvider.nameCTAllPrim)
        .isProperty(1, "PropertyString", EdmTechProvider.nameString);
  }

  @Test
  public void testEntitySet_NavProp() {

    test.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isInitialType(EdmTechProvider.nameETKeyNav)

        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    test.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/PropertyString")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameString)
        .isProperty(0, "PropertyString", EdmTechProvider.nameString);

    test.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)
        .at(2)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameETKeyNav);

    test.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/NavPropertyETKeyNavOne/PropertyString")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav)
        .at(1)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)
        .at(2)
        .isUriPathInfoKind(UriPathInfoKind.navEntitySet)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameString)
        .isProperty(0, "PropertyString", EdmTechProvider.nameString);

  }

  @Test
  public void testEntitySet_TypeFilter() {

    // filter
    test.run("ESTwoPrim/com.sap.odata.test1.ETBase")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isCollectionTypeFilter(EdmTechProvider.nameETBase)
        .isSingleTypeFilter(null)
        .isType(EdmTechProvider.nameETBase)
        .isCollection(true);

    // filter before key predicate
    test.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isCollectionTypeFilter(EdmTechProvider.nameETBase)
        .isSingleTypeFilter(null)
        .isType(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    test.run("ESTwoPrim/com.sap.odata.test1.ETBase(PropertyInt16=1)/AdditionalPropertyString_5")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isCollectionTypeFilter(EdmTechProvider.nameETBase)
        .isSingleTypeFilter(null)
        .isType(EdmTechProvider.nameString)

        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "AdditionalPropertyString_5", EdmTechProvider.nameString)
        .isCollection(false);

    // filter after key predicate
    test.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isCollectionTypeFilter(null)
        .isSingleTypeFilter(EdmTechProvider.nameETBase)
        .isType(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);

    test.run("ESTwoPrim(PropertyInt16=1)/com.sap.odata.test1.ETBase/AdditionalPropertyString_5")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .isType(EdmTechProvider.nameString)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isCollectionTypeFilter(null)
        .isSingleTypeFilter(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isProperty(0, "AdditionalPropertyString_5", EdmTechProvider.nameString)
        .isCollection(false);
  }

  @Test
  public void testSingleton() {
    test.run("SI")
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isType(EdmTechProvider.nameETTwoPrim);

    test.run("SI/PropertyInt16")
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isInitialType(EdmTechProvider.nameETTwoPrim)
        .isType(EdmTechProvider.nameInt16)
        .isProperty(0, "PropertyInt16", EdmTechProvider.nameInt16);

    test.run("SI/com.sap.odata.test1.ETBase/AdditionalPropertyString_5")
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isType(new FullQualifiedName("Edm", "String"))
        .isProperties(Arrays.asList("AdditionalPropertyString_5"))
        .isProperty(0, "AdditionalPropertyString_5", EdmTechProvider.nameString);

    test.run("SINav/NavPropertyETKeyNavOne")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)
        .isCollection(false)
        .at(1)
        .isType(EdmTechProvider.nameETKeyNav);

    test.run("SINav/NavPropertyETKeyNavOne/PropertyInt16")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)
        .isCollection(false)
        .at(1)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameInt16);

    test.run("SINav/NavPropertyETKeyNavMany(1)")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)

        .isCollection(true)
        .at(1)
        .isType(EdmTechProvider.nameETKeyNav);

    test.run("SINav/NavPropertyETKeyNavMany(1)/PropertyInt16")
        .at(0)
        .isUriPathInfoKind(UriPathInfoKind.singleton)
        .isInitialType(EdmTechProvider.nameETTwoKeyNav)
        .isType(EdmTechProvider.nameETKeyNav)
        .isCollection(true)
        .at(1)
        .isInitialType(EdmTechProvider.nameETKeyNav)
        .isType(EdmTechProvider.nameInt16);

  }

  @Test
  public void testActionImport() {

    test.run("AIRTPrimParam")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameString);

    test.run("AIRTPrimCollParam")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameString)
        .isCollection(true);

    test.run("AIRTETParam")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameETTwoKeyTwoPrim)
        .isCollection(false);

    test.run("AIRTETCollAllPrimParam")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isCollection(true);

    //the parser can to this, but should not, as defined per ABNF
    /*
    test.run("AIRTETCollAllPrimParam(1)")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);
    test.run("AIRTETCollAllPrimParam(ParameterInt16=1)")
        .isUriPathInfoKind(UriPathInfoKind.action)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isCollection(false);
        */
  }

  /*
   * //@Test
   * public void testFunctionImport() {
   * test.run("MaximalAge").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.functioncall);
   * }
   */
  /*
   * //@Test
   * public void testBoundFunctions() {
   * 
   * test.run("Employees/RefScenario.bf_entity_set_rt_entity(NonBindingParameter='1')").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * test.run("Employees('1')/EmployeeName/RefScenario.bf_pprop_rt_entity_set()").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * test.run("Company/RefScenario.bf_singleton_rt_entity_set()('1')").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * // testUri("Company/RefScenario.bf_singleton_rt_entity_set()('1')/EmployeeName/"
   * // +"RefScenario.bf_pprop_rt_entity_set()",
   * // UriPathInfoImpl.UriPathInfoKind.boundFunctioncall);
   * }
   */
  /*
   * //@Test
   * public void testBoundActions() {
   * test.run("Employees('1')/RefScenario.ba_entity_rt_pprop")
   * .isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.boundActionImport);
   * test.run("Employees('1')/EmployeeName/RefScenario.ba_pprop_rt_entity_set").isUriPathInfoKind(
   * UriPathInfoImpl.UriPathInfoKind.boundActionImport);
   * }
   */
  /*
   * //@Test
   * public void testNavigationFunction() {
   * test.run("Employees('1')/ne_Manager").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * test.run("Teams('1')/nt_Employees('1')").isUriPathInfoKind(UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * // testUri("Teams('1')/nt_Employees('1')/EmployeeName", UriPathInfoImpl.UriPathInfoKind.navicationProperty);
   * }
   */

  @Test
  public void testErrors() {
    // the following is wrong and must throw an error behind an Action are not () allowed
    // test.run("AIRTPrimParam()");

  }

  @Test
  public void testFilter() {
    test.run("ESAllPrim?$filter=1")
        .isUriPathInfoKind(UriPathInfoKind.entitySet)
        .hasQueryParameter(SystemQueryParameter.FILTER.toString(), 1)
        .isFilterString("1");

  }

  @Test
  public void testFilterSimpleSameBinaryBinaryBinaryPriority() {

    test.runFilter("1 add 2 add 3 add 4").is("<<<1 add 2> add 3> add 4>");
    test.runFilter("1 add 2 add 3 div 4").is("<<1 add 2> add <3 div 4>>");
    test.runFilter("1 add 2 div 3 add 4").is("<<1 add <2 div 3>> add 4>");
    test.runFilter("1 add 2 div 3 div 4").is("<1 add <<2 div 3> div 4>>");
    test.runFilter("1 div 2 add 3 add 4").is("<<<1 div 2> add 3> add 4>");
    test.runFilter("1 div 2 add 3 div 4").is("<<1 div 2> add <3 div 4>>");
    test.runFilter("1 div 2 div 3 add 4").is("<<<1 div 2> div 3> add 4>");
    test.runFilter("1 div 2 div 3 div 4").is("<<<1 div 2> div 3> div 4>");

  }

  @Test
  public void testFilterComplexMixedPriority() {
    test.runFilter("a      or c      and e     ").isCompr("< a       or < c       and  e      >>");
    test.runFilter("a      or c      and e eq f").isCompr("< a       or < c       and <e eq f>>>");
    test.runFilter("a      or c eq d and e     ").isCompr("< a       or <<c eq d> and  e      >>");
    test.runFilter("a      or c eq d and e eq f").isCompr("< a       or <<c eq d> and <e eq f>>>");
    test.runFilter("a eq b or c      and e     ").isCompr("<<a eq b> or < c       and  e      >>");
    test.runFilter("a eq b or c      and e eq f").isCompr("<<a eq b> or < c       and <e eq f>>>");
    test.runFilter("a eq b or c eq d and e     ").isCompr("<<a eq b> or <<c eq d> and  e      >>");
    test.runFilter("a eq b or c eq d and e eq f").isCompr("<<a eq b> or <<c eq d> and <e eq f>>>");
  }

  @Test
  public void textFilterMember() {
    test.runFilter("a").is("a");
  }
}
