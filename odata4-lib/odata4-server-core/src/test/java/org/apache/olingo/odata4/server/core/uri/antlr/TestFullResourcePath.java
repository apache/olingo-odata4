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
package org.apache.olingo.odata4.server.core.uri.antlr;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.olingo.odata4.commons.api.ODataApplicationException;
import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.UriResourceKind;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.odata4.server.api.uri.queryoption.expression.MethodCallKind;
import org.apache.olingo.odata4.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.server.core.testutil.EdmTechProvider;
import org.apache.olingo.odata4.server.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.server.core.testutil.FilterValidator;
import org.apache.olingo.odata4.server.core.testutil.ResourceValidator;
import org.apache.olingo.odata4.server.core.testutil.UriValidator;
import org.apache.olingo.odata4.server.core.uri.UriParserException;
import org.junit.Test;

public class TestFullResourcePath {
  Edm edm = null;
  UriValidator testUri = null;
  ResourceValidator testRes = null;
  FilterValidator testFilter = null;

  public TestFullResourcePath() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());
    testUri = new UriValidator().setEdm(edm);
    testRes = new ResourceValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);
  }

  @Test
  public void test() throws UriParserException {
    // testUri.log("ESAllPrim?$orderby=PropertyDouble eq 3.5E+38");
  }

  @Test
  public void testFunctionBound_varOverloading() {
    // on ESTwoKeyNav
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void runBfuncBnCpropCastRtEs() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav")
        .isType(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnCpropCollRtEs() {
    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim");

    testUri
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnCpropRtEs() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnEntityRtEs() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isFunction("BFCETTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEntityCastRtEs() {
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");

    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='(''2'')')"
            + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'(''2'')'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEsCastRtEs() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey")
        .isType(EdmTechProvider.nameETBaseTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav"
        + "/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void runBfuncBnEsRtCprop() {
    testUri.run("ESAllPrim/com.sap.odata.test1.BFCESAllPrimRTCTAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFCESAllPrimRTCTAllPrim")
        .isType(EdmTechProvider.nameCTAllPrim);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameCTBase);
  }

  @Test
  public void runBfuncBnEsRtCpropColl() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnEsRtEntityPpNp() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.ref);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/NavPropertyETMediaOne/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isNavProperty("NavPropertyETMediaOne", EdmTechProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp)
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTAllPrim);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);
  }

  @Test
  public void runBfuncBnEsRtEntyPpNpCast() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()"
        + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri
        .run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runBfuncBnEsRtEntityPpCp() {

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp)
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComplex/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComplex")
        .isType(EdmTechProvider.nameCTNavFiveProp)
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEntyPpCpCast() {

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);
  }

  @Test
  public void runBfuncBnEsRtEntityPpSp() {
    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEs() {

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'3'")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runBfuncBnEsRtEsBa() {

    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComplex"
        + "/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .n()
        .isAction("BAESAllPrimRTETAllPrim");

  }

  @Test
  public void runBfuncBnEsRtPrim() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTString()/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString")
        .isType(EdmTechProvider.nameString)
        .n()
        .isValue();
  }

  @Test
  public void runbfuncBnEsRtPrimColl() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(EdmTechProvider.nameString, true);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTCollString()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(EdmTechProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropCollRtEs() {
    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropRtEs() {

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();

    testUri.run("ESKeyNav(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
  }

  @Test
  public void runBfuncBnSingleRtEs() {

    testUri.run("SINav/com.sap.odata.test1.BFCSINavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isFunction("BFCSINavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnSingleCastRtEs() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/com.sap.odata.test1.BFCETBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESBaseTwoKey");
  }

  @Test
  public void runActionBound_on_EntityEntry() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");

    testUri.run("ESKeyNav(PropertyInt16=1)/com.sap.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runActionBound_on_EntityCollection() {
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BAESTwoKeyNavRTESTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isAction("BAESTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runFunctionBound_on_var_Types() {

    // on primitive
    testUri.run("ESAllPrim(1)/PropertyString/com.sap.odata.test1.BFCStringRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETAllPrim)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(EdmTechProvider.nameString);

    // on collection of primitive
    testUri.run("ESCollAllPrim(1)/CollPropertyString/com.sap.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETCollAllPrim)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(EdmTechProvider.nameString);

    // on complex
    testUri.run("ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')"
        + "/PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .at(2)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // on collection of complex
    testUri.run("ESKeyNav(1)/CollPropertyComplex/com.sap.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .at(1)
        .isType(EdmTechProvider.nameCTPrimComp)
        .isCollection(true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETAllPrim);

    // on entity
    testUri.run("ESTwoKeyNav(ParameterInt16=1,PropertyString='ABC')"
        + "/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);

    // on collection of entity
    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isCollection(true)
        .at(1).isUriPathInfoKind(UriResourceKind.function)
        .isType(EdmTechProvider.nameETTwoKeyNav);
  }

  @Test
  public void runActionBound_on_EntityCast() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isAction("BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/com.sap.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isAction("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");
  }

  @Test
  public void runCrossjoin() {
    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav", "ESTwoKeyNav"));
  }

  @Test
  public void runCrossjoinError() {
    testUri.runEx("$crossjoin").isExSyntax(0);
    testUri.runEx("$crossjoin/error").isExSyntax(0);
    testUri.runEx("$crossjoin()").isExSyntax(0);
    // testUri.runEx("$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid").isExSyntax(0);
  }

  @Test
  public void runEntityId() {
    testUri.run("$entity?$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isIdText("ESKeyNav(1)");
    testUri.run("$entity/com.sap.odata.test1.ETKeyNav?$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EdmTechProvider.nameETKeyNav)
        .isIdText("ESKeyNav(1)");
  }

  @Test
  public void runEntityIdError() {
    // TODO planned: move to validator
    // testUri.runEx("$entity").isExSyntax(0);
    // testUri.runEx("$entity?$idfalse=ESKeyNav(1)").isExSyntax(0);
    // testUri.runEx("$entity/com.sap.odata.test1.invalidType?$id=ESKeyNav(1)").isExSemantic(0);
    // testUri.runEx("$entity/invalid?$id=ESKeyNav(1)").isExSyntax(0);
  }

  @Test
  public void runEsName() {
    testUri.run("ESAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true);

    testUri.run("ESAllPrim/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EdmTechProvider.nameETAllPrim, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNameError() {

    testUri.runEx("ESAllPrim/$count/$ref").isExSemantic(0);
    testUri.runEx("ESAllPrim/$ref/$count").isExSemantic(0);
    testUri.runEx("ESAllPrim/$ref/invalid").isExSemantic(0);
    testUri.runEx("ESAllPrim/$count/invalid").isExSemantic(0);
    testUri.runEx("ESAllPrim(1)/whatever").isExSemantic(0);
    // testUri.runEx("ESAllPrim(PropertyInt16='1')").isExSemantic(0);
    testUri.runEx("ESAllPrim(PropertyInt16)").isExSemantic(0);
    testUri.runEx("ESAllPrim(PropertyInt16=)").isExSyntax(0);
    testUri.runEx("ESAllPrim(PropertyInt16=1,Invalid='1')").isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim").isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETAllKey")
        .isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim('1')/com.sap.odata.test1.ETAllKey")
        .isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)")
        .isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim/com.sap.odata.test1.ETAllKey")
        .isExSemantic(0);

    testUri.runEx("ETBaseTwoKeyTwoPrim()")
        .isExSemantic(0);

    testUri.runEx("ESAllNullable(1)/CollPropertyString/$value")
        .isExSemantic(0);

    testUri.runEx("ETMixPrimCollComp(1)/ComplexProperty/$value").isExSemantic(0);
  }

  @Test
  public void runEsNameCast() {
    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(-32768)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testUri.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

  }

  @Test
  public void runEsNamePpSpCast() {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);
  }

  @Test
  public void runEsNameKey() {
    testUri.run("ESCollAllPrim(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");

    testUri.run("ESCollAllPrim(PropertyInt16=1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");

    testUri.run("ESFourKeyAlias(PropertyInt16=1,KeyAlias1=2,KeyAlias2='3',KeyAlias3='4')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESFourKeyAlias")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "KeyAlias1", "2")
        .isKeyPredicate(2, "KeyAlias2", "'3'")
        .isKeyPredicate(3, "KeyAlias3", "'4'");

    testUri.run("ESCollAllPrim(null)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim");
  }

  @Test
  public void runEsNameParaKeys() throws UnsupportedEncodingException {
    testUri.run(encode("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
        + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
        + "PropertyInt64=-9223372036854775808,PropertyDecimal=0.1,PropertyDate=2013-09-25,"
        + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
        + "PropertyDuration=duration'P10DT5H34M21.123456789012S',"
        + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
        + "PropertyTimeOfDay=12:34:55.123456789012)"))
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllKey")
        .isKeyPredicate(0, "PropertyString", "'O''Neil'")
        .isKeyPredicate(1, "PropertyBoolean", "true")
        .isKeyPredicate(2, "PropertyByte", "255")
        .isKeyPredicate(3, "PropertySByte", "-128")
        .isKeyPredicate(4, "PropertyInt16", "-32768")
        .isKeyPredicate(5, "PropertyInt32", "-2147483648")
        .isKeyPredicate(6, "PropertyInt64", "-9223372036854775808")
        .isKeyPredicate(7, "PropertyDecimal", "0.1")
        .isKeyPredicate(8, "PropertyDate", "2013-09-25")
        .isKeyPredicate(9, "PropertyDateTimeOffset", "2002-10-10T12:00:00-05:00")
        .isKeyPredicate(10, "PropertyDuration", "duration'P10DT5H34M21.123456789012S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55.123456789012");
  }

  @Test
  public void runEsNameKeyCast() {

    testUri.runEx("xESTwoPrim(1)/com.sap.odata.test1.ETBase(1)")
        .isExSemantic(0);

    testUri.runEx("xESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase(1)")
        .isExSemantic(0);

    testUri.runEx("xESBase/com.sap.odata.test1.ETTwoPrim(1)")
        .isExSemantic(0);

    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim(1)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETBase(1)/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBase);

    testUri.run("ESTwoPrim/com.sap.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EdmTechProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBase);
  }

  @Test
  public void runEsNameParaKeysCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void run_EsNamePpCp() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");
  }

  @Test
  public void runEsNamePpCpColl() {
    testUri.run("ESMixPrimCollComp(5)/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "5")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTTwoPrim, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComplex/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNamePpCpCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")

        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTBase);

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTTwoBase);
  }

  @Test
  public void runNsNamePpNp() {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETKeyNavMany(PropertyInt16=2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavMany(4)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "4");

    testUri.run("ESKeyNav(1)/PropertyComplex/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='(3)')"
        + "/PropertyComplex/PropertyComplex/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'(3)'")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)/NavPropertyETMediaMany(2)/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETMediaMany", EdmTechProvider.nameETMedia, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isValue();

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/NavPropertyETMediaOne/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .n()
        .isNavProperty("NavPropertyETMediaOne", EdmTechProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .n()
        .isRef();
  }

  @Test
  public void runEsNamePpNpCast() {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany(3)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav(PropertyInt16=3,PropertyString='4')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3")
        .isKeyPredicate(1, "PropertyString", "'4'")
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETBaseTwoKeyNavMany", EdmTechProvider.nameETBaseTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/"
        + "NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')/"
        + "NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);
  }

  @Test
  public void runEsNamePpNpRc() {
    // checks for using referential constrains to fill missing keys
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany('2')").goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");

    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETTwoKeyNavMany(PropertyString='2')").goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runEsNamePpSp() {
    testUri.run("ESAllPrim(1)/PropertyByte")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", EdmTechProvider.nameByte, false);

    testUri.run("ESAllPrim(1)/PropertyByte/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", EdmTechProvider.nameByte, false)
        .n()
        .isValue();

    testUri.run("ESMixPrimCollComp(1)/PropertyComplex/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);
  }

  @Test
  public void runEsNamePpSpColl() {
    testUri.run("ESCollAllPrim(1)/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .n()
        .isCount();

  }

  @Test
  public void runEsNameRef() {
    testUri.run("ESAllPrim/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isRef();

    testUri.run("ESAllPrim(-32768)/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .n()
        .isRef();
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isRef();
  }

  @Test
  public void runFunctionImpBf() {

    testUri.run("FICRTString()/com.sap.odata.test1.BFCStringRTESTwoKeyNav()");
  }

  @Test
  public void runFunctionImpCastBf() {

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runFunctionImpEntity() {

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTETMedia()/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETMedia")
        .isFunction("UFCRTETMedia")
        .n()
        .isValue();

    testUri.run("FICRTETKeyNav()/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .n()
        .isRef();
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .n()
        .isRef();

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");
  }

  @Test
  public void runFunctionImpEs() {
    /**/
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp);

    testUri.run("FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FINRTESMixPrimCollCompTwoParam")
        .isFunction("UFNRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp);

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EdmTechProvider.nameETMixPrimCollComp)
        .n()
        .isCount();
  }

  @Test
  public void runFunctionImpError() {
    testUri.runEx("FICRTCollCTTwoPrimParam()").isExSemantic(0);
    testUri.runEx("FICRTCollCTTwoPrimParam(invalidParam=2)").isExSemantic(0);
  }

  @Test
  public void runFunctionImpEsAlias() {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)/$count?@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@invalidAlias)?@validAlias=1");
  }

  @Test
  public void runFunctionImpEsCast() {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/com.sap.odata.test1.ETBaseTwoKeyNav/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isCount();

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isTypeFilterOnEntry(EdmTechProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runSingletonEntityValue() {
    testUri.run("SIMedia/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SIMedia")
        .n().isValue();
  }

  @Test
  public void runSingletonPpNpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

  }

  @Test
  public void runSingletonPpCpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexTwoPrim/com.sap.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim)
        .isTypeFilter(EdmTechProvider.nameCTBase);

  }

  @Test
  public void runSingletonPpSpCast() {
    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("SINav/com.sap.odata.test1.ETBaseTwoKeyNav/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilter(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .isType(EdmTechProvider.nameString, true);

  }

  @Test
  public void runSingletonEntityPpNp() {
    testUri.run("SINav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("SINav/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runSingletonEntityPpCp() {
    testUri.run("SINav/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComplex");

    testUri.run("SINav/PropertyComplex/PropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComplex")
        .n()
        .isComplex("PropertyComplex");

  }

  @Test
  public void runSingletonEntityPpCpColl() {
    testUri.run("SINav/CollPropertyComplex")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true);

    testUri.run("SINav/CollPropertyComplex/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComplex")
        .isType(EdmTechProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runSingletonEntityPpSp() {
    testUri.run("SINav/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);
  }

  @Test
  public void runSingletonEntityPpSpColl() {
    testUri.run("SINav/CollPropertyString")

        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true);
    testUri.run("SINav/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void runExpand() {

    testUri.run("ESKeyNav(1)?$expand=*")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar(0);

    testUri.run("ESKeyNav(1)?$expand=*/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar(0)
        .isSegmentRef(1);

    testUri.run("ESKeyNav(1)?$expand=*/$ref,NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar(0).isSegmentRef(1)
        .next()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)?$expand=*($levels=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar(0)
        .isLevelText("3");

    testUri.run("ESKeyNav(1)?$expand=*($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar(0)
        .isLevelText("max");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n().isRef();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator().isFilterSerialized("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1;$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isCount();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n().isCount();

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .n().isCount()
        .goUpExpandValidator()
        .isFilterSerialized("<<PropertyInt16> gt <1>>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isFilterSerialized("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString")
        .goSelectItem(0).isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=5)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("5");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString")
        .goSelectItem(0).isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavOne($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("max");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1;$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany($skip=1%3b$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?$expand=NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?"
        + "$expand=com.sap.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexNav")
        .isType(EdmTechProvider.nameCTBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESTwoKeyNav?$expand=com.sap.odata.test1.ETBaseTwoKeyNav/PropertyComplexNav"
        + "/com.sap.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath().first()
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComplexNav")
        .isType(EdmTechProvider.nameCTBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)?$expand=NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath()
        .first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .next()
        .goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("2")
        .isTopText("1");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')?$expand=com.sap.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/com.sap.odata.test1.ETTwoBaseTwoKeyNav($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .goPath().first()
        .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EdmTechProvider.nameETTwoBaseTwoKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany("
        + "$expand=NavPropertyETKeyNavOne))")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, true)
        .isType(EdmTechProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav)
        .goUpExpandValidator()
        .isSelectText("PropertyInt16")
        .goSelectItem(0).isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testUri.run("ESKeyNav?$expand=NavPropertyETKeyNavOne($select=PropertyComplex/PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .isType(EdmTechProvider.nameETKeyNav)
        .goUpExpandValidator()
        .isSelectText("PropertyComplex/PropertyInt16");
  }

  @Test
  public void runTop() {
    // top
    testUri.run("ESKeyNav?$top=1")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("1");

    testUri.run("ESKeyNav?$top=0")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("0");

    testUri.run("ESKeyNav?$top=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("-3");
  }

  @Test
  public void runFormat() {
    // format
    testUri.run("ESKeyNav(1)?$format=atom")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("atom");
    testUri.run("ESKeyNav(1)?$format=json")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("json");
    testUri.run("ESKeyNav(1)?$format=xml")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("xml");
    testUri.run("ESKeyNav(1)?$format=IANA_content_type/must_contain_a_slash")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("IANA_content_type/must_contain_a_slash");
    testUri.run("ESKeyNav(1)?$format=Test_all_valid_signsSpecified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("Test_all_valid_signsSpecified_for_format_signs&-._~$@'/Aa123&-._~$@'");
  }

  @Test
  public void runCount() {
    // count
    testUri.run("ESAllPrim?$count=true")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("true");
    testUri.run("ESAllPrim?$count=false")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("false");

    // TODO planned: move to validator
    // testUri.runEx("ESAllPrim?$count=foo").isExSyntax(0);
  }

  @Test
  public void skip() {
    // skip
    testUri.run("ESAllPrim?$skip=3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("3");
    testUri.run("ESAllPrim?$skip=0")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("0");
    testUri.run("ESAllPrim?$skip=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("-3");
  }

  @Test
  public void skiptoken() {

    testUri.run("ESAllPrim?$skiptoken=foo")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipTokenText("foo");
  }

  @Test
  public void misc() {

    testUri.run("");

    testUri.run("$all")
        .isKind(UriInfoKind.all);

    testUri.run("$metadata")
        .isKind(UriInfoKind.metadata);

    testUri.run("$batch")
        .isKind(UriInfoKind.batch);

    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("ESKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav");
    testUri.run("ESKeyNav(1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");

    testUri.run("SINav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isSingleton("SINav");

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isType(EdmTechProvider.nameETMixPrimCollComp)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTETKeyNav")
        .isType(EdmTechProvider.nameETKeyNav);

    testUri.run("FICRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isType(EdmTechProvider.nameCTTwoPrim);

    testUri.run("FICRTCTAllPrimTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isType(EdmTechProvider.nameCTAllPrim)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTCollStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(EdmTechProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("AIRTETParam")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport("AIRTETParam");

    testUri.run("AIRTPrimParam")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport("AIRTPrimParam");

    testUri.run("ESKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isCount();

    testUri.run("ESKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isRef();

    testUri.run("ESKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .n().isCount();

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav/$count")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isRef();

    testUri.run("ESKeyNav(1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");

    testUri.run("ESKeyNav(1)/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isRef();

    testUri.run("ESMedia(1)/$value")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESMedia")
        .n()
        .isValue();

    testUri.run("ESAllPrim/com.sap.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/com.sap.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isRef();

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/com.sap.odata.test1.ETBaseTwoKeyNav/$value")
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EdmTechProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isValue();

  }

  @Test
  public void testFilter() throws UriParserException {

    testFilter.runOnETTwoKeyNav("PropertyString")
        .is("<PropertyString>")
        .isType(EdmTechProvider.nameString);

    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyInt16")
        .is("<PropertyComplex/PropertyInt16>")
        .isType(EdmTechProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate")
        .is("<PropertyComplex/PropertyComplex/PropertyDate>")
        .isType(EdmTechProvider.nameDate);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne")
        .is("<NavPropertyETTwoKeyNavOne>")
        .isType(EdmTechProvider.nameETTwoKeyNav);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyString")
        .is("<NavPropertyETTwoKeyNavOne/PropertyString>")
        .isType(EdmTechProvider.nameString);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComplex>")
        .isType(EdmTechProvider.nameCTPrimComp);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyComplex>")
        .isType(EdmTechProvider.nameCTAllPrim);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16>")
        .isType(EdmTechProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16 eq 1")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyComplex/PropertyInt16> eq <1>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isLiteral("1");

    testFilter.runOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/"
        + "PropertyString eq 'SomeString'")
        .is("<<NavPropertyETKeyNavMany/NavPropertyETTwoKeyNavMany/PropertyString> eq <'SomeString'>>")
        .root().left()
        .isType(EdmTechProvider.nameString)
        .isMember().goPath()
        .first()
        .isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().right();

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<com.sap.odata.test1.ETTwoKeyNav/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .isMember().goPath()
        .first().isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        .isType(EdmTechProvider.nameETTwoKeyNav).isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .n().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("2013-11-12");

    testFilter.runOnCTTwoPrim("com.sap.odata.test1.CTBase/AdditionalPropString eq 'SomeString'")
        .is("<<com.sap.odata.test1.CTTwoPrim/com.sap.odata.test1.CTBase/AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(EdmTechProvider.nameString)
        .isMember().goPath()
        .first().isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        .isType(EdmTechProvider.nameCTTwoPrim).isTypeFilterOnEntry(EdmTechProvider.nameCTBase)
        .n().isPrimitiveProperty("AdditionalPropString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("'SomeString'");

    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<NavPropertyETTwoKeyNavOne/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isLiteral("2013-11-12");

    testFilter
        .runOnETTwoKeyNav("PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase/AdditionalPropString eq 'SomeString'")
        .is("<<PropertyComplexTwoPrim/com.sap.odata.test1.CTTwoBase/AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(EdmTechProvider.nameString)
        .root().right()
        .isLiteral("'SomeString'");

    // testFilter.runOnETTwoKeyNavEx("invalid").isExSemantic(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplex/invalid").isExSemantic(0);
    testFilter.runOnETTwoKeyNavEx("concat('a','b')/invalid").isExSyntax(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplex/concat('a','b')").isExSyntax(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplexAllPrim/PropertyInt16 eq '1'").isExSemantic(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplexAllPrim/PropertyDate eq 1").isExSemantic(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplexAllPrim/PropertyString eq 1").isExSemantic(0);
    testFilter.runOnETTwoKeyNavEx("PropertyComplexAllPrim/PropertyDate eq 1").isExSemantic(0);

    testFilter.runOnETAllPrim("PropertySByte eq PropertySByte")
        .is("<<PropertySByte> eq <PropertySByte>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte ne PropertySByte")
        .is("<<PropertySByte> ne <PropertySByte>>")
        .isBinary(BinaryOperatorKind.NE)
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte add PropertySByte")
        .is("<<PropertySByte> add <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte add PropertyByte")
        .is("<<PropertyByte> add <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 add PropertyInt16")
        .is("<<PropertyInt16> add <PropertyInt16>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isType(EdmTechProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 add PropertyInt32")
        .is("<<PropertyInt32> add <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt32)
        .root().right()
        .isType(EdmTechProvider.nameInt32);

    testFilter.runOnETAllPrim("PropertyInt64 add PropertyInt64")
        .is("<<PropertyInt64> add <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle add PropertySingle")
        .is("<<PropertySingle> add <PropertySingle>>")
        .root().left()
        .isType(EdmTechProvider.nameSingle)
        .root().right()
        .isType(EdmTechProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble add PropertyDouble")
        .is("<<PropertyDouble> add <PropertyDouble>>")
        .root().left()
        .isType(EdmTechProvider.nameDouble)
        .root().right()
        .isType(EdmTechProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal add PropertyDecimal")
        .is("<<PropertyDecimal> add <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyDecimal")
        .is("<<PropertySByte> add <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt32")
        .is("<<PropertySByte> add <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt64")
        .is("<<PropertySByte> add <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset add PropertyDuration")
        .is("<<PropertyDateTimeOffset> add <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDateTimeOffset)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration add PropertyDuration")
        .is("<<PropertyDuration> add <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDuration)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate add PropertyDuration")
        .is("<<PropertyDate> add <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertySByte sub PropertySByte")
        .is("<<PropertySByte> sub <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte sub PropertyByte")
        .is("<<PropertyByte> sub <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 sub PropertyInt16")
        .is("<<PropertyInt16> sub <PropertyInt16>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isType(EdmTechProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 sub PropertyInt32")
        .is("<<PropertyInt32> sub <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt32)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 sub PropertyInt64")
        .is("<<PropertyInt64> sub <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle sub PropertySingle")
        .is("<<PropertySingle> sub <PropertySingle>>")
        .root().left()
        .isType(EdmTechProvider.nameSingle)
        .root().right()
        .isType(EdmTechProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble sub PropertyDouble")
        .is("<<PropertyDouble> sub <PropertyDouble>>")
        .root().left()
        .isType(EdmTechProvider.nameDouble)
        .root().right()
        .isType(EdmTechProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyDecimal")
        .is("<<PropertyDecimal> sub <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt32")
        .is("<<PropertyDecimal> sub <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt64")
        .is("<<PropertyDecimal> sub <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyByte")
        .is("<<PropertyDecimal> sub <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDuration")
        .is("<<PropertyDateTimeOffset> sub <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDateTimeOffset)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration sub PropertyDuration")
        .is("<<PropertyDuration> sub <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDuration)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDateTimeOffset")
        .is("<<PropertyDateTimeOffset> sub <PropertyDateTimeOffset>>")
        .root().left()
        .isType(EdmTechProvider.nameDateTimeOffset)
        .root().right()
        .isType(EdmTechProvider.nameDateTimeOffset);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDuration")
        .is("<<PropertyDate> sub <PropertyDuration>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isType(EdmTechProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDate")
        .is("<<PropertyDate> sub <PropertyDate>>")
        .root().left()
        .isType(EdmTechProvider.nameDate)
        .root().right()
        .isType(EdmTechProvider.nameDate);
    testFilter.runOnETAllPrim("PropertySByte mul PropertySByte")
        .is("<<PropertySByte> mul <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mul PropertyByte")
        .is("<<PropertyByte> mul <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mul PropertyInt16")
        .is("<<PropertyInt16> mul <PropertyInt16>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isType(EdmTechProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mul PropertyInt32")
        .is("<<PropertyInt32> mul <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt32)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt64")
        .is("<<PropertyInt64> mul <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mul PropertySingle")
        .is("<<PropertySingle> mul <PropertySingle>>")
        .root().left()
        .isType(EdmTechProvider.nameSingle)
        .root().right()
        .isType(EdmTechProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mul PropertyDouble")
        .is("<<PropertyDouble> mul <PropertyDouble>>")
        .root().left()
        .isType(EdmTechProvider.nameDouble)
        .root().right()
        .isType(EdmTechProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mul PropertyDecimal")
        .is("<<PropertyDecimal> mul <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt32")
        .is("<<PropertyInt64> mul <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertySByte")
        .is("<<PropertyInt64> mul <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyDecimal")
        .is("<<PropertyInt64> mul <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte div PropertySByte")
        .is("<<PropertySByte> div <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte div PropertyByte")
        .is("<<PropertyByte> div <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 div PropertyInt16")
        .is("<<PropertyInt16> div <PropertyInt16>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isType(EdmTechProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 div PropertyInt32")
        .is("<<PropertyInt32> div <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt32)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 div PropertyInt64")
        .is("<<PropertyInt64> div <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle div PropertySingle")
        .is("<<PropertySingle> div <PropertySingle>>")
        .root().left()
        .isType(EdmTechProvider.nameSingle)
        .root().right()
        .isType(EdmTechProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble div PropertyDouble")
        .is("<<PropertyDouble> div <PropertyDouble>>")
        .root().left()
        .isType(EdmTechProvider.nameDouble)
        .root().right()
        .isType(EdmTechProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal div PropertyDecimal")
        .is("<<PropertyDecimal> div <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertyInt32")
        .is("<<PropertyByte> div <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyByte div PropertyDecimal")
        .is("<<PropertyByte> div <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertySByte")
        .is("<<PropertyByte> div <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte div 0")
        .is("<<PropertyByte> div <0>>");

    testFilter.runOnETAllPrim("0 div 0")
        .is("<<0> div <0>>");

    testFilter.runOnETAllPrim("PropertySByte mod PropertySByte")
        .is("<<PropertySByte> mod <PropertySByte>>")
        .root().left()
        .isType(EdmTechProvider.nameSByte)
        .root().right()
        .isType(EdmTechProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mod PropertyByte")
        .is("<<PropertyByte> mod <PropertyByte>>")
        .root().left()
        .isType(EdmTechProvider.nameByte)
        .root().right()
        .isType(EdmTechProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mod PropertyInt16")
        .is("<<PropertyInt16> mod <PropertyInt16>>")
        .root().left()
        .isType(EdmTechProvider.nameInt16)
        .root().right()
        .isType(EdmTechProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mod PropertyInt32")
        .is("<<PropertyInt32> mod <PropertyInt32>>")
        .root().left()
        .isType(EdmTechProvider.nameInt32)
        .root().right()
        .isType(EdmTechProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mod PropertyInt64")
        .is("<<PropertyInt64> mod <PropertyInt64>>")
        .root().left()
        .isType(EdmTechProvider.nameInt64)
        .root().right()
        .isType(EdmTechProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mod PropertySingle")
        .is("<<PropertySingle> mod <PropertySingle>>")
        .root().left()
        .isType(EdmTechProvider.nameSingle)
        .root().right()
        .isType(EdmTechProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mod PropertyDouble")
        .is("<<PropertyDouble> mod <PropertyDouble>>")
        .root().left()
        .isType(EdmTechProvider.nameDouble)
        .root().right()
        .isType(EdmTechProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mod PropertyDecimal")
        .is("<<PropertyDecimal> mod <PropertyDecimal>>")
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);

    // DODO not synced
    testFilter.runOnETAllPrim("PropertyDecimal ge PropertyDecimal")
        .is("<<PropertyDecimal> ge <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.GE)
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal lt PropertyDecimal")
        .is("<<PropertyDecimal> lt <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LT)
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal le PropertyDecimal")
        .is("<<PropertyDecimal> le <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LE)
        .root().left()
        .isType(EdmTechProvider.nameDecimal)
        .root().right()
        .isType(EdmTechProvider.nameDecimal);
  }

  @Test
  public void testFilterProperties() throws UriParserException {
    testFilter.runOnETAllPrim("PropertyByte mod 0")
        .is("<<PropertyByte> mod <0>>");

    testFilter.runOnETAllPrim("com.sap.odata.test1.UFCRTETTwoKeyNavParamCTTwoPrim(ParameterCTTwoPrim=@ParamAlias)")
        .is("<UFCRTETTwoKeyNavParamCTTwoPrim>")
        .goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParamCTTwoPrim")
        .isParameterAlias(0, "ParameterCTTwoPrim", "ParamAlias");

    testFilter.runOnETTwoKeyNav("PropertyComplex"
        + "/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam"
        + "(ParameterString=PropertyComplex/PropertyComplex/PropertyString)(PropertyInt16=1,PropertyString='2')"
        + "/PropertyString eq 'SomeString'")
        .is("<<PropertyComplex/BFCCTPrimCompRTESTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNavParam")
        .isParameter(0, "ParameterString", "PropertyComplex/PropertyComplex/PropertyString")
        .goParameter(0)
        .isMember()
        .goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .goUpToResourceValidator()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTETTwoKeyNavParam"
        + "(ParameterString=null)/PropertyString eq 'SomeString'")
        .is("<<PropertyComplex/BFCCTPrimCompRTETTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTETTwoKeyNavParam")
        .goParameter(0)
        .isNull()
        .goUpToResourceValidator()
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany/com.sap.odata.test1.BFCESTwoKeyNavRTString()"
        + " eq 'SomeString'")
        .is("<<NavPropertyETTwoKeyNavMany/BFCESTwoKeyNavRTString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n()
        .isFunction("BFCESTwoKeyNavRTString");

    testFilter.runOnETTwoKeyNav("$it/com.sap.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()/PropertyString eq 'SomeString'")
        .is("<<$it/BFESTwoKeyNavRTESTwoKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isIt()
        .n()
        .isFunction("BFESTwoKeyNavRTESTwoKeyNav")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()/PropertyString eq 'SomeString'")
        .is("<<BFESTwoKeyNavRTESTwoKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isFunction("BFESTwoKeyNavRTESTwoKeyNav")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/com.sap.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()"
        + "/PropertyComplex/PropertyComplex/PropertyString eq 'Walldorf'")
        .is("<<NavPropertyETTwoKeyNavOne/BFCETTwoKeyNavRTETTwoKeyNav/PropertyComplex/PropertyComplex/PropertyString> "
            + "eq <'Walldorf'>>")
        .root().left().goPath()
        .first()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n()
        .isFunction("BFCETTwoKeyNavRTETTwoKeyNav")
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComplex/com.sap.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam"
        + "(ParameterString='1')"
        + "/com.sap.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/PropertyString eq 'SomeString'")
        .is("<<PropertyComplex/BFCCTPrimCompRTESTwoKeyNavParam/com.sap.odata.test1.ETBaseTwoKeyNav/PropertyString> "
            + "eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNavParam")
        .isTypeFilterOnCollection(EdmTechProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNavSingle("$it/com.sap.odata.test1.BFCETTwoKeyNavRTCTTwoPrim()/com.sap.odata.test1.CTBase"
        + "/PropertyString eq 'SomeString'")
        .is("<<$it/BFCETTwoKeyNavRTCTTwoPrim/com.sap.odata.test1.CTBase/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isIt()
        .n()
        .isFunction("BFCETTwoKeyNavRTCTTwoPrim")
        .isTypeFilterOnEntry(EdmTechProvider.nameCTBase)
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16 eq 2")
        .is("<<UFCRTETTwoKeyNavParam/PropertyInt16> eq <2>>")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=@Param1Alias)"
        + "/PropertyInt16 eq 2")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameterAlias(0, "ParameterInt16", "Param1Alias")
        .n()
        .isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)"
        + "/PropertyComplex/PropertyComplex/PropertyString eq 'SomeString'")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("com.sap.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=PropertyInt16)"
        + "/PropertyComplex/PropertyComplex/PropertyString eq 'SomeString'")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "PropertyInt16")
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

  }

  @Test
  public void testFilterPMethods() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETKeyNav("indexof(PropertyString,'47') eq 5")
        .is("<<indexof(<PropertyString>,<'47'>)> eq <5>>")
        .root().left()
        .isMethod(MethodCallKind.INDEXOF, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'47'>");

    testFilter.runOnETKeyNav("tolower(PropertyString) eq 'foo'")
        .is("<<tolower(<PropertyString>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodCallKind.TOLOWER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("toupper(PropertyString) eq 'FOO'")
        .is("<<toupper(<PropertyString>)> eq <'FOO'>>")
        .root().left()
        .isMethod(MethodCallKind.TOUPPER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("trim(PropertyString) eq 'fooba'")
        .is("<<trim(<PropertyString>)> eq <'fooba'>>")
        .root().left()
        .isMethod(MethodCallKind.TRIM, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodCallKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodCallKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,2,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<2>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodCallKind.SUBSTRING, 3)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<2>")
        .isParameterText(2, "<4>");

    testFilter.runOnETKeyNav("concat(PropertyString,PropertyComplexTwoPrim/PropertyString) eq 'foo'")
        .is("<<concat(<PropertyString>,<PropertyComplexTwoPrim/PropertyString>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodCallKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<PropertyComplexTwoPrim/PropertyString>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .root().left()
        .isMethod(MethodCallKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .root().left()
        .isMethod(MethodCallKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("concat(PropertyString, cast(PropertyComplexAllPrim/PropertyInt16,Edm.String))")
        .is("<concat(<PropertyString>,<cast(<PropertyComplexAllPrim/PropertyInt16>,<Edm.String>)>)>")
        .isMethod(MethodCallKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<cast(<PropertyComplexAllPrim/PropertyInt16>,<Edm.String>)>")
        .goParameter(1)
        .isMethod(MethodCallKind.CAST, 2)
        .isParameterText(0, "<PropertyComplexAllPrim/PropertyInt16>")
        .isParameterText(1, "<Edm.String>");

    testFilter.runOnETKeyNav("length(PropertyString) eq 32")
        .is("<<length(<PropertyString>)> eq <32>>")
        .root().left()
        .isMethod(MethodCallKind.LENGTH, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETAllPrim("year(PropertyDate) eq 2013")
        .is("<<year(<PropertyDate>)> eq <2013>>")
        .root().left()
        .isMethod(MethodCallKind.YEAR, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("year(2013-09-25) eq 2013")
        .is("<<year(<2013-09-25>)> eq <2013>>")
        .root().left()
        .isMethod(MethodCallKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("year(PropertyDateTimeOffset) eq 2013")
        .is("<<year(<PropertyDateTimeOffset>)> eq <2013>>")
        .root().left()
        .isMethod(MethodCallKind.YEAR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("year(2013-09-25T12:34:56.123456789012-10:24) eq 2013")
        .is("<<year(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013>>")
        .root().left()
        .isMethod(MethodCallKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("month(PropertyDate) eq 9")
        .is("<<month(<PropertyDate>)> eq <9>>")
        .root().left()
        .isMethod(MethodCallKind.MONTH, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("month(2013-09-25) eq 9")
        .is("<<month(<2013-09-25>)> eq <9>>")
        .root().left()
        .isMethod(MethodCallKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("month(PropertyDateTimeOffset) eq 9")
        .is("<<month(<PropertyDateTimeOffset>)> eq <9>>")
        .root().left()
        .isMethod(MethodCallKind.MONTH, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("month(2013-09-25T12:34:56.123456789012-10:24) eq 9")
        .is("<<month(<2013-09-25T12:34:56.123456789012-10:24>)> eq <9>>")
        .root().left()
        .isMethod(MethodCallKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("day(PropertyDate) eq 25")
        .is("<<day(<PropertyDate>)> eq <25>>")
        .root().left()
        .isMethod(MethodCallKind.DAY, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("day(2013-09-25) eq 25")
        .is("<<day(<2013-09-25>)> eq <25>>")
        .root().left()
        .isMethod(MethodCallKind.DAY, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("day(PropertyDateTimeOffset) eq 25")
        .is("<<day(<PropertyDateTimeOffset>)> eq <25>>")
        .root().left()
        .isMethod(MethodCallKind.DAY, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("day(2013-09-25T12:34:56.123456789012-10:24) eq 25")
        .is("<<day(<2013-09-25T12:34:56.123456789012-10:24>)> eq <25>>")
        .root().left()
        .isMethod(MethodCallKind.DAY, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .root().left()
        .isMethod(MethodCallKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .root().left()
        .isMethod(MethodCallKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(2013-09-25T12:34:56.123456789012-10:24) eq 2")
        .is("<<hour(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2>>")
        .root().left()
        .isMethod(MethodCallKind.HOUR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyTimeOfDay) eq 2")
        .is("<<hour(<PropertyTimeOfDay>)> eq <2>>")
        .root().left()
        .isMethod(MethodCallKind.HOUR, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("hour(12:34:55.123456789012) eq 12")
        .is("<<hour(<12:34:55.123456789012>)> eq <12>>")
        .root().left()
        .isMethod(MethodCallKind.HOUR, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("minute(PropertyDateTimeOffset) eq 34")
        .is("<<minute(<PropertyDateTimeOffset>)> eq <34>>")
        .root().left()
        .isMethod(MethodCallKind.MINUTE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("minute(2013-09-25T12:34:56.123456789012-10:24) eq 34")
        .is("<<minute(<2013-09-25T12:34:56.123456789012-10:24>)> eq <34>>")
        .root().left()
        .isMethod(MethodCallKind.MINUTE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("minute(PropertyTimeOfDay) eq 34")
        .is("<<minute(<PropertyTimeOfDay>)> eq <34>>")
        .root().left()
        .isMethod(MethodCallKind.MINUTE, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("minute(12:34:55.123456789012) eq 34")
        .is("<<minute(<12:34:55.123456789012>)> eq <34>>")
        .root().left()
        .isMethod(MethodCallKind.MINUTE, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("second(PropertyDateTimeOffset) eq 56")
        .is("<<second(<PropertyDateTimeOffset>)> eq <56>>")
        .root().left()
        .isMethod(MethodCallKind.SECOND, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("second(2013-09-25T12:34:56.123456789012-10:24) eq 56")
        .is("<<second(<2013-09-25T12:34:56.123456789012-10:24>)> eq <56>>")
        .root().left()
        .isMethod(MethodCallKind.SECOND, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("second(PropertyTimeOfDay) eq 56")
        .is("<<second(<PropertyTimeOfDay>)> eq <56>>")
        .root().left()
        .isMethod(MethodCallKind.SECOND, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("second(12:34:55.123456789012) eq 56")
        .is("<<second(<12:34:55.123456789012>)> eq <56>>")
        .root().left()
        .isMethod(MethodCallKind.SECOND, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyDateTimeOffset) eq 123456789012")
        .is("<<fractionalseconds(<PropertyDateTimeOffset>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("fractionalseconds(2013-09-25T12:34:56.123456789012-10:24) eq 123456789012")
        .is("<<fractionalseconds(<2013-09-25T12:34:56.123456789012-10:24>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyTimeOfDay) eq 123456789012")
        .is("<<fractionalseconds(<PropertyTimeOfDay>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("fractionalseconds(12:34:55.123456789012) eq 123456789012")
        .is("<<fractionalseconds(<12:34:55.123456789012>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("totalseconds(PropertyDuration) eq 4711")
        .is("<<totalseconds(<PropertyDuration>)> eq <4711>>")
        .root().left()
        .isMethod(MethodCallKind.TOTALSECONDS, 1)
        .isParameterText(0, "<PropertyDuration>");

    testFilter.runOnETAllPrim("totalseconds(duration'P10DT5H34M21.123456789012S') eq 4711")
        .is("<<totalseconds(<duration'P10DT5H34M21.123456789012S'>)> eq <4711>>")
        .root().left()
        .isMethod(MethodCallKind.TOTALSECONDS, 1)
        .isParameterText(0, "<duration'P10DT5H34M21.123456789012S'>");

    testFilter.runOnETAllPrim("date(PropertyDateTimeOffset) eq 2013-09-25")
        .is("<<date(<PropertyDateTimeOffset>)> eq <2013-09-25>>")
        .root().left()
        .isMethod(MethodCallKind.DATE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("date(2013-09-25T12:34:56.123456789012-10:24) eq 2013-09-25")
        .is("<<date(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013-09-25>>")
        .root().left()
        .isMethod(MethodCallKind.DATE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("time(PropertyDateTimeOffset) eq 12:34:55.123456789012")
        .is("<<time(<PropertyDateTimeOffset>)> eq <12:34:55.123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.TIME, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("time(2013-09-25T12:34:56.123456789012-10:24) eq 12:34:55.123456789012")
        .is("<<time(<2013-09-25T12:34:56.123456789012-10:24>)> eq <12:34:55.123456789012>>")
        .root().left()
        .isMethod(MethodCallKind.TIME, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("round(PropertyDouble) eq 17")
        .is("<<round(<PropertyDouble>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.ROUND, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("round(17.45e1) eq 17")
        .is("<<round(<17.45e1>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.ROUND, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("round(PropertyDecimal) eq 17")
        .is("<<round(<PropertyDecimal>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.ROUND, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("round(17.45) eq 17")
        .is("<<round(<17.45>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.ROUND, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("floor(PropertyDouble) eq 17")
        .is("<<floor(<PropertyDouble>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("floor(17.45e1) eq 17")
        .is("<<floor(<17.45e1>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.FLOOR, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("floor(PropertyDecimal) eq 17")
        .is("<<floor(<PropertyDecimal>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("floor(17.45) eq 17")
        .is("<<floor(<17.45>)> eq <17>>")
        .root().left()
        .isMethod(MethodCallKind.FLOOR, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("ceiling(PropertyDouble) eq 18")
        .is("<<ceiling(<PropertyDouble>)> eq <18>>")
        .root().left()
        .isMethod(MethodCallKind.CEILING, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("ceiling(17.55e1) eq 18")
        .is("<<ceiling(<17.55e1>)> eq <18>>")
        .root().left()
        .isMethod(MethodCallKind.CEILING, 1)
        .isParameterText(0, "<17.55e1>");

    testFilter.runOnETAllPrim("ceiling(PropertyDecimal) eq 18")
        .is("<<ceiling(<PropertyDecimal>)> eq <18>>")
        .root().left()
        .isMethod(MethodCallKind.CEILING, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("ceiling(17.55) eq 18")
        .is("<<ceiling(<17.55>)> eq <18>>")
        .root().left()
        .isMethod(MethodCallKind.CEILING, 1)
        .isParameterText(0, "<17.55>");

    testFilter.runOnETAllPrim("totaloffsetminutes(PropertyDateTimeOffset) eq 4711")
        .is("<<totaloffsetminutes(<PropertyDateTimeOffset>)> eq <4711>>")
        .root().left()
        .isMethod(MethodCallKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("totaloffsetminutes(2013-09-25T12:34:56.123456789012-10:24) eq 4711")
        .is("<<totaloffsetminutes(<2013-09-25T12:34:56.123456789012-10:24>)> eq <4711>>")
        .root().left()
        .isMethod(MethodCallKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("mindatetime()")
        .is("<mindatetime()>")
        .isMethod(MethodCallKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("mindatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<mindatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodCallKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime()")
        .is("<maxdatetime()>")
        .isMethod(MethodCallKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<maxdatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodCallKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("now()")
        .is("<now()>")
        .isMethod(MethodCallKind.NOW, 0);

    testFilter.runOnETAllPrim("now() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<now()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodCallKind.NOW, 0);

    testFilter.runOnETTwoKeyNav("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnCTTwoPrim("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameCTTwoPrim, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOnString("$it eq 'Walldorf'")
        .is("<<$it> eq <'Walldorf'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameString, false);

    testFilter.runOnString("endswith($it,'sap.com')")
        .is("<endswith(<$it>,<'sap.com'>)>")
        .isMethod(MethodCallKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameString, false);

    testFilter.runOnString("endswith($it,'sap.com') eq false")
        .is("<<endswith(<$it>,<'sap.com'>)> eq <false>>")
        .root().left()
        .isMethod(MethodCallKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("endswith($it/CollPropertyString,'sap.com')")
        .is("<endswith(<$it/CollPropertyString>,<'sap.com'>)>")
        .isMethod(MethodCallKind.ENDSWITH, 2)
        .isParameterText(0, "<$it/CollPropertyString>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EdmTechProvider.nameETTwoKeyNav, true)
        .n().isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true);

    testFilter.runOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyInt16 eq $root"
        + "/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyInt16")
        .is("<<PropertyComplex/PropertyComplex/PropertyInt16> eq <$root/ESTwoKeyNav/PropertyInt16>>")
        .root().left()
        .goPath()
        .first().isComplex("PropertyComplex").isType(EdmTechProvider.nameCTPrimComp, false)
        .n().isComplex("PropertyComplex").isType(EdmTechProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testFilter.runOnETKeyNav("cast(com.sap.odata.test1.ETBaseTwoKeyNav)")
        .is("<cast(<com.sap.odata.test1.ETBaseTwoKeyNav>)>")
        .root()
        .isMethod(MethodCallKind.CAST, 1)
        .isParameterText(0, "<com.sap.odata.test1.ETBaseTwoKeyNav>")
        .goParameter(0)
        .isTypedLiteral(EdmTechProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("cast(PropertyComplexTwoPrim,com.sap.odata.test1.CTBase)")
        .is("<cast(<PropertyComplexTwoPrim>,<com.sap.odata.test1.CTBase>)>")
        .root()
        .isMethod(MethodCallKind.CAST, 2)
        .isParameterText(0, "<PropertyComplexTwoPrim>")
        .isParameterText(1, "<com.sap.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isComplex("PropertyComplexTwoPrim").isType(EdmTechProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
        .root()
        .goParameter(1)
        .isTypedLiteral(EdmTechProvider.nameCTBase);

    testFilter.runOnETKeyNav("cast($it,com.sap.odata.test1.CTBase)")
        .is("<cast(<$it>,<com.sap.odata.test1.CTBase>)>")
        .root()
        .isMethod(MethodCallKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<com.sap.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EdmTechProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameCTBase);

    testFilter.runOnETKeyNav("cast($it,com.sap.odata.test1.CTBase) eq cast($it,com.sap.odata.test1.CTBase)"
        )
        .is("<<cast(<$it>,<com.sap.odata.test1.CTBase>)> eq <cast(<$it>,<com.sap.odata.test1.CTBase>)>>")
        .root().left()
        .isMethod(MethodCallKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<com.sap.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EdmTechProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().left()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameCTBase)
        .root().right()
        .isMethod(MethodCallKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<com.sap.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EdmTechProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().right()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameCTBase);

    testFilter.runOnInt32("cast(Edm.Int32)")
        .is("<cast(<Edm.Int32>)>")
        .isMethod(MethodCallKind.CAST, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameInt32);

    testFilter.runOnDateTimeOffset("cast(Edm.DateTimeOffset)")
        .is("<cast(<Edm.DateTimeOffset>)>")
        .isMethod(MethodCallKind.CAST, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameDateTimeOffset);

    testFilter.runOnDuration("cast(Edm.Duration)")
        .is("<cast(<Edm.Duration>)>")
        .isMethod(MethodCallKind.CAST, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameDuration);

    testFilter.runOnTimeOfDay("cast(Edm.TimeOfDay)")
        .is("<cast(<Edm.TimeOfDay>)>")
        .isMethod(MethodCallKind.CAST, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(CollPropertyInt16,Edm.Int32)")
        .is("<cast(<CollPropertyInt16>,<Edm.Int32>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath().first()
        .isPrimitiveProperty("CollPropertyInt16", EdmTechProvider.nameInt16, true)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameInt32);

    testFilter.runOnETTwoKeyNav(
        "cast(PropertyComplex/PropertyComplex/PropertyDateTimeOffset,Edm.DateTimeOffset)")
        .is("<cast(<PropertyComplex/PropertyComplex/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDateTimeOffset", EdmTechProvider.nameDateTimeOffset, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("cast(PropertyComplex/PropertyComplex/PropertyDuration,Edm.Duration)")
        .is("<cast(<PropertyComplex/PropertyComplex/PropertyDuration>,<Edm.Duration>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDuration", EdmTechProvider.nameDuration, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("cast(PropertyComplex/PropertyComplex/PropertyTimeOfDay,Edm.TimeOfDay)")
        .is("<cast(<PropertyComplex/PropertyComplex/PropertyTimeOfDay>,<Edm.TimeOfDay>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComplex", EdmTechProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyTimeOfDay", EdmTechProvider.nameTimeOfDay, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(PropertyComplexAllPrim,com.sap.odata.test1.CTTwoPrim)")
        .is("<cast(<PropertyComplexAllPrim>,<com.sap.odata.test1.CTTwoPrim>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComplexAllPrim", EdmTechProvider.nameCTAllPrim, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoPrim);

    // testFilter.runOnETKeyNav(" Xcast(PropertyComplexTwoPrim,com.sap.odata.test1.CTAllPrim)");

    testFilter.runOnETKeyNav("cast(NavPropertyETKeyNavOne,com.sap.odata.test1.ETKeyPrimNav)")
        .is("<cast(<NavPropertyETKeyNavOne>,<com.sap.odata.test1.ETKeyPrimNav>)>")
        .isMethod(MethodCallKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EdmTechProvider.nameETKeyPrimNav);

    testFilter.runOnETKeyNavEx("cast(NavPropertyETKeyPrimNavOne,com.sap.odata.test1.ETKeyNav)").isExSemantic(0);
    testFilter.runOnETKeyNav("any()")
        .isMember().goPath().first().isUriPathInfoKind(UriResourceKind.lambdaAny);

  }

  @Test
  public void runLamdbaFunctions() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETKeyNav("any(d:d/PropertyInt16 eq 1)")
        .is("<<ANY;<<d/PropertyInt16> eq <1>>>>")
        .root().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString')")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<d/PropertyString> eq <'SomeString'>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    // TODO planned: lambda does not check if the previous path segment is a collection
    // testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/any(d:d/PropertyString eq 'SomeString')");

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any()")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;>>");

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/CollPropertyString/any(d:d eq 'SomeString')")
        .is("<NavPropertyETTwoKeyNavOne/CollPropertyString/<ANY;<<d> eq <'SomeString'>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameString, false);

    testFilter.runOnETKeyNav(" NavPropertyETTwoKeyNavOne/com.sap.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()"
        + "/any(d:d/PropertyComplex/PropertyInt16 eq 6)")
        .is("<NavPropertyETTwoKeyNavOne/BFCETTwoKeyNavRTESTwoKeyNav/<ANY;<<d/PropertyComplex/PropertyInt16> eq <6>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EdmTechProvider.nameETTwoKeyNav, false)
        .n().isFunction("BFCETTwoKeyNavRTESTwoKeyNav")
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    // TODO lambda does not check if the previous path segment is a collection
    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or d/any"
        + "(e:e/CollPropertyString eq 'SomeString'))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyInt16> eq <1>> or "
            + "<d/<ANY;<<e/CollPropertyString> eq <'SomeString'>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.OR)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("CollPropertyString", EdmTechProvider.nameString, true);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or d/CollPropertyString/any"
        + "(e:e eq 'SomeString'))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyInt16> eq <1>> or "
            + "<d/CollPropertyString/<ANY;<<e> eq <'SomeString'>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.OR)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isType(EdmTechProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameString, false);

    testFilter
        .runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString' and d/CollPropertyString/any"
            + "(e:e eq d/PropertyString))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyString> eq <'SomeString'>> and "
            + "<d/CollPropertyString/<ANY;<<e> eq <d/PropertyString>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.AND)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameETTwoKeyNav, false)
        .n().isType(EdmTechProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().right().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

  }

  @Test
  public void runIsOf() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETKeyNav("isof(com.sap.odata.test1.ETTwoKeyNav)")
        .is("<isof(<com.sap.odata.test1.ETTwoKeyNav>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameETTwoKeyNav);

    testFilter.runOnETKeyNav("isof(com.sap.odata.test1.ETBaseTwoKeyNav) eq true")
        .is("<<isof(<com.sap.odata.test1.ETBaseTwoKeyNav>)> eq <true>>")
        .root().isBinary(BinaryOperatorKind.EQ)
        .left()
        .isMethod(MethodCallKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameETBaseTwoKeyNav);

    testFilter
        .runOnETKeyNav("isof(com.sap.odata.test1.ETBaseTwoKeyNav) eq true and PropertyComplex/PropertyInt16 eq 1")
        .is("<<<isof(<com.sap.odata.test1.ETBaseTwoKeyNav>)> eq <true>> and <<PropertyComplex/PropertyInt16> eq <1>>>")
        .root().isBinary(BinaryOperatorKind.AND)
        .left().isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EdmTechProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("isof(NavPropertyETKeyNavOne, com.sap.odata.test1.ETKeyNav) eq true")
        .is("<<isof(<NavPropertyETKeyNavOne>,<com.sap.odata.test1.ETKeyNav>)> eq <true>>")
        .root().isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(EdmTechProvider.nameETKeyNav);

    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoPrim)")
        .is("<isof(<PropertyComplexTwoPrim>,<com.sap.odata.test1.CTTwoPrim>)>")
        .root().isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyComplexTwoPrim").goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoBase)")
        .is("<isof(<PropertyComplexTwoPrim>,<com.sap.odata.test1.CTTwoBase>)>")
        .root().isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyComplexTwoPrim").goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoBase);

    testFilter.runOnETKeyNav("isof(PropertyComplexTwoPrim,com.sap.odata.test1.CTTwoPrim) eq true")
        .is("<<isof(<PropertyComplexTwoPrim>,<com.sap.odata.test1.CTTwoPrim>)> eq <true>>")
        .root().left().isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyComplexTwoPrim").goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof($it,com.sap.odata.test1.CTTwoPrim)")
        .is("<isof(<$it>,<com.sap.odata.test1.CTTwoPrim>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isIt().goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof($it,com.sap.odata.test1.CTTwoBase) eq false")
        .is("<<isof(<$it>,<com.sap.odata.test1.CTTwoBase>)> eq <false>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath().isIt().goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(EdmTechProvider.nameCTTwoBase);

    testFilter.runOnETKeyNav("isof(PropertyComplex/PropertyInt16,Edm.Int32)")
        .is("<isof(<PropertyComplex/PropertyInt16>,<Edm.Int32>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameInt32);

    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyDateTimeOffset,Edm.DateTimeOffset)")
        .is("<isof(<PropertyComplex/PropertyComplex/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDateTimeOffset", EdmTechProvider.nameDateTimeOffset, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyTimeOfDay,Edm.TimeOfDay)")
        .is("<isof(<PropertyComplex/PropertyComplex/PropertyTimeOfDay>,<Edm.TimeOfDay>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyTimeOfDay", EdmTechProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameTimeOfDay);

    testFilter.runOnETTwoKeyNav(" isof(PropertyComplex/PropertyComplex/PropertyDuration,Edm.Duration)")
        .is("<isof(<PropertyComplex/PropertyComplex/PropertyDuration>,<Edm.Duration>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDuration", EdmTechProvider.nameDuration, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyString,Edm.String)")
        .is("<isof(<PropertyComplex/PropertyComplex/PropertyString>,<Edm.String>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameString);

    // TODO cross check with abap coding
    testFilter.runOnETTwoKeyNav("isof(PropertyComplex/PropertyComplex/PropertyString,Edm.Guid)")
        .is("<isof(<PropertyComplex/PropertyComplex/PropertyString>,<Edm.Guid>)>")
        .root()
        .isMethod(MethodCallKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EdmTechProvider.nameGuid);
  }

  @Test
  public void testHas() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETTwoKeyNav("PropertyEnumString has com.sap.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> has <com.sap.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString has com.sap.odata.test1.ENString'String2'")
        .is("<<PropertyComplexEnum/PropertyEnumString> has <com.sap.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyComplexEnum")
        .n().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav(
        "PropertyComplexEnum/PropertyEnumString has com.sap.odata.test1.ENString'String2' eq true")
        .is("<<<PropertyComplexEnum/PropertyEnumString> has <com.sap.odata.test1.ENString<String2>>> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().left().goPath()
        .first().isComplex("PropertyComplexEnum")
        .n().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().left().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has com.sap.odata.test1.ENString'String3'")
        .is("<<PropertyEnumString> has <com.sap.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String3"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has com.sap.odata.test1.ENString'String,String3'")
        .is("<<PropertyEnumString> has <com.sap.odata.test1.ENString<String,String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String", "String3"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has null")
        .is("<<PropertyEnumString> has <null>>")
        .root()
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString).goUpFilterValidator()
        .root().right().isNull();

    testFilter.runOnETTwoKeyNav("endswith(PropertyComplex/PropertyComplex/PropertyString,'dorf')")
        .is("<endswith(<PropertyComplex/PropertyComplex/PropertyString>,<'dorf'>)>")
        .isMethod(MethodCallKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith(PropertyComplex/PropertyComplex/PropertyString,'dorf') eq true")
        .is("<<endswith(<PropertyComplex/PropertyComplex/PropertyString>,<'dorf'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf')")
        .is("<endswith(<'Walldorf'>,<'dorf'>)>")
        .isMethod(MethodCallKind.ENDSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf') eq true")
        .is("<<endswith(<'Walldorf'>,<'dorf'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.ENDSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETKeyNav("startswith(PropertyComplexAllPrim/PropertyString,'Wall')")
        .is("<startswith(<PropertyComplexAllPrim/PropertyString>,<'Wall'>)>")
        .isMethod(MethodCallKind.STARTSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplexAllPrim")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith(PropertyComplexAllPrim/PropertyString,'Wall') eq true")
        .is("<<startswith(<PropertyComplexAllPrim/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.STARTSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplexAllPrim")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith('Walldorf','Wall')")
        .is("<startswith(<'Walldorf'>,<'Wall'>)>")
        .isMethod(MethodCallKind.STARTSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith('Walldorf','Wall') eq true")
        .is("<<startswith(<'Walldorf'>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.STARTSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains(PropertyComplex/PropertyComplex/PropertyString,'Wall')")
        .is("<contains(<PropertyComplex/PropertyComplex/PropertyString>,<'Wall'>)>")
        .isMethod(MethodCallKind.CONTAINS, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains(PropertyComplex/PropertyComplex/PropertyString,'Wall') eq true")
        .is("<<contains(<PropertyComplex/PropertyComplex/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.CONTAINS, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall')")
        .is("<contains(<'Walldorf'>,<'Wall'>)>")
        .isMethod(MethodCallKind.CONTAINS, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall') eq true")
        .is("<<contains(<'Walldorf'>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodCallKind.CONTAINS, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETAllPrim("com.sap.odata.test1.UFCRTCTTwoPrimParam(ParameterInt16=null,ParameterString=null)")
        .goPath()
        .isFunction("UFCRTCTTwoPrimParam")
        .isParameter(0, "ParameterInt16", "null")
        .isParameter(1, "ParameterString", "null");

    testFilter.runOnETAllPrim("PropertyBoolean eq true")
        .is("<<PropertyBoolean> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyBoolean", EdmTechProvider.nameBoolean, false)
        .goUpFilterValidator()
        .root().right().isTrue();

    testFilter.runOnETAllPrim("PropertyBoolean eq 2")
        .is("<<PropertyBoolean> eq <2>>");

    testFilter.runOnETAllPrim("PropertyDecimal eq 1.25")
        .is("<<PropertyDecimal> eq <1.25>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDecimal", EdmTechProvider.nameDecimal, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.25");

    testFilter.runOnETAllPrim("PropertyDouble eq 1.5")
        .is("<<PropertyDouble> eq <1.5>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDouble", EdmTechProvider.nameDouble, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.5");

    testFilter.runOnETAllPrim("PropertySingle eq 1.5")
        .is("<<PropertySingle> eq <1.5>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertySingle", EdmTechProvider.nameSingle, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.5");

    testFilter.runOnETAllPrim("PropertySByte eq -128")
        .is("<<PropertySByte> eq <-128>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertySByte", EdmTechProvider.nameSByte, false)
        .goUpFilterValidator()
        .root().right().isLiteral("-128");

    testFilter.runOnETAllPrim("PropertyByte eq 255")
        .is("<<PropertyByte> eq <255>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyByte",
            EdmTechProvider.nameByte, false).goUpFilterValidator()
        .root().right().isLiteral("255");

    testFilter.runOnETAllPrim("PropertyInt16 eq 32767")
        .is("<<PropertyInt16> eq <32767>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right().isLiteral("32767");

    testFilter.runOnETAllPrim("PropertyInt32 eq 2147483647")
        .is("<<PropertyInt32> eq <2147483647>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt32", EdmTechProvider.nameInt32, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2147483647");

    testFilter.runOnETAllPrim("PropertyInt64 eq 9223372036854775807")
        .is("<<PropertyInt64> eq <9223372036854775807>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt64", EdmTechProvider.nameInt64, false)
        .goUpFilterValidator()
        .root().right().isLiteral("9223372036854775807");

    testFilter.runOnETAllPrim("PropertyDate eq 2013-09-25")
        .is("<<PropertyDate> eq <2013-09-25>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2013-09-25");

    testFilter.runOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<PropertyDateTimeOffset> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .isPrimitiveProperty("PropertyDateTimeOffset", EdmTechProvider.nameDateTimeOffset, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S'")
        .is("<<PropertyDuration> eq <duration'P10DT5H34M21.123456789012S'>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDuration", EdmTechProvider.nameDuration, false)
        .goUpFilterValidator()
        .root().right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33")
        .is("<<PropertyGuid> eq <005056A5-09B1-1ED3-89BD-FB81372CCB33>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyGuid", EdmTechProvider.nameGuid, false)
        .goUpFilterValidator()
        .root().right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOnETAllPrim("PropertyString eq 'somestring'")
        .is("<<PropertyString> eq <'somestring'>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .root().right().isLiteral("'somestring'");

    testFilter.runOnETAllPrim("PropertyTimeOfDay eq 12:34:55.12345678901")
        .is("<<PropertyTimeOfDay> eq <12:34:55.12345678901>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyTimeOfDay", EdmTechProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .root().right().isLiteral("12:34:55.12345678901");

    testFilter.runOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> eq <com.sap.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String2'")
        .is("<<PropertyEnumString> eq <com.sap.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq com.sap.odata.test1.ENString'String3'")
        .is("<<PropertyComplexEnum/PropertyEnumString> eq <com.sap.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyComplexEnum")
        .n().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString).goUpFilterValidator()
        .root().right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String3"));

    testFilter.runOnETTwoKeyNav("PropertyComplexEnum/PropertyEnumString eq PropertyComplexEnum/PropertyEnumString")
        .is("<<PropertyComplexEnum/PropertyEnumString> eq <PropertyComplexEnum/PropertyEnumString>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyComplexEnum")
        .n().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString).goUpFilterValidator()
        .root().right().goPath()
        .first().isComplex("PropertyComplexEnum")
        .n().isComplex("PropertyEnumString").isType(EdmTechProvider.nameENString).goUpFilterValidator();

  }

  @Test
  public void testOrderby() throws UriParserException, UnsupportedEncodingException {

    testFilter.runOrderByOnETTwoKeyNav("com.sap.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString'")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("com.sap.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' asc")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("com.sap.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("com.sap.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc"
        + ", PropertyString eq '1'")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'")
        .isSortOrder(1, false)
        .goOrder(1).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .goOrder(1).right().isLiteral("'1'");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate eq "
        + "$root/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComplex/PropertyComplex/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .n().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyString")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyDate "
        + "eq 2013-11-12 desc, PropertyString eq 'SomeString' desc")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false).goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-11-12")
        .isSortOrder(1, true)
        .goOrder(1).left().goPath().first().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(1).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComplex");
    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex/PropertyComplex")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex desc, PropertyComplex/PropertyInt16 eq 1")
        .isSortOrder(0, true)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComplex").goUpFilterValidator()
        .isSortOrder(1, false)
        .goOrder(1).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false).goUpFilterValidator()
        .goOrder(1).right().isLiteral("1");

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne/PropertyString")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne/PropertyComplex")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EdmTechProvider.nameETKeyNav, false)
        .n().isComplex("PropertyComplex");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComplex/PropertyComplex/PropertyInt16 eq 1")
        .isSortOrder(0, false).goOrder(0).left().goPath()
        .first().isComplex("PropertyComplex")
        .n().isComplex("PropertyComplex")
        .n().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')"
        + "/PropertyString eq 'SomeString'")
        .isSortOrder(0, false).goOrder(0).left().goPath()
        .first().isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')"
        + "/PropertyString eq 'SomeString1' desc,PropertyString eq 'SomeString2' asc")
        .isSortOrder(0, true).goOrder(0).left().goPath()
        .first().isNavProperty("NavPropertyETKeyNavMany", EdmTechProvider.nameETKeyNav, false)
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EdmTechProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false).goUpFilterValidator()
        .isSortOrder(1, false).goOrder(1).left().goPath()
        .first().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false);

    testFilter.runOrderByOnETAllPrim("PropertyBoolean eq true")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBoolean", EdmTechProvider.nameBoolean, false)
        .goUpFilterValidator()
        .goOrder(0).right().isTrue();

    testFilter.runOrderByOnETAllPrim("PropertyBoolean eq true desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBoolean", EdmTechProvider.nameBoolean, false)
        .goUpFilterValidator()
        .goOrder(0).right().isTrue();

    testFilter.runOrderByOnETAllPrim(encode("PropertyDouble eq 3.5E+38"))
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDouble", EdmTechProvider.nameDouble, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("3.5E+38");

    testFilter.runOrderByOnETAllPrim(encode("PropertyDouble eq 3.5E+38 desc")).isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDouble", EdmTechProvider.nameDouble, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("3.5E+38");

    testFilter.runOrderByOnETAllPrim("PropertySingle eq 1.5")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySingle", EdmTechProvider.nameSingle, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("1.5");

    testFilter.runOrderByOnETAllPrim("PropertySingle eq 1.5 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySingle", EdmTechProvider.nameSingle, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("1.5");

    testFilter.runOrderByOnETAllPrim("PropertySByte eq -128")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySByte", EdmTechProvider.nameSByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("-128");

    testFilter.runOrderByOnETAllPrim("PropertySByte eq -128 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySByte", EdmTechProvider.nameSByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("-128");

    testFilter.runOrderByOnETAllPrim("PropertyByte eq 255")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyByte", EdmTechProvider.nameByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("255");

    testFilter.runOrderByOnETAllPrim("PropertyByte eq 255 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyByte", EdmTechProvider.nameByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("255");

    testFilter.runOrderByOnETAllPrim("PropertyInt16 eq 32767")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("32767");

    testFilter.runOrderByOnETAllPrim("PropertyInt16 eq 32767 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt16", EdmTechProvider.nameInt16, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("32767");

    testFilter.runOrderByOnETAllPrim("PropertyInt32 eq 2147483647")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt32", EdmTechProvider.nameInt32, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2147483647");

    testFilter.runOrderByOnETAllPrim("PropertyInt32 eq 2147483647 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt32", EdmTechProvider.nameInt32, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2147483647");

    testFilter.runOrderByOnETAllPrim("PropertyInt64 eq 9223372036854775807")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt64", EdmTechProvider.nameInt64, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("9223372036854775807");

    testFilter.runOrderByOnETAllPrim("PropertyInt64 eq 9223372036854775807 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt64", EdmTechProvider.nameInt64, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("9223372036854775807");

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'0FAB7B'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", EdmTechProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'0FAB7B'");

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'0FAB7B' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", EdmTechProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'0FAB7B'");

    testFilter.runOrderByOnETAllPrim("PropertyDate eq 2013-09-25")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25");

    testFilter.runOrderByOnETAllPrim("PropertyDate eq 2013-09-25 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDate", EdmTechProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25");

    testFilter.runOrderByOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDateTimeOffset", EdmTechProvider.nameDateTimeOffset,
            false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOrderByOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDateTimeOffset", EdmTechProvider.nameDateTimeOffset,
            false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOrderByOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDuration", EdmTechProvider.nameDuration, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOrderByOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDuration", EdmTechProvider.nameDuration, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOrderByOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyGuid", EdmTechProvider.nameGuid, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOrderByOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyGuid", EdmTechProvider.nameGuid, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOrderByOnETAllPrim("PropertyString eq 'somestring'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("'somestring'");

    testFilter.runOrderByOnETAllPrim("PropertyString eq 'somestring' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyString", EdmTechProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("'somestring'");

    testFilter.runOrderByOnETAllPrim("PropertyTimeOfDay eq 12:34:55.123456789012")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyTimeOfDay", EdmTechProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("12:34:55.123456789012");

    testFilter.runOrderByOnETAllPrim("PropertyTimeOfDay eq 12:34:55.123456789012 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyTimeOfDay", EdmTechProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("12:34:55.123456789012");

    testFilter.runOrderByOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String1'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isComplex("PropertyEnumString").goUpFilterValidator()
        .goOrder(0).right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOrderByOnETTwoKeyNav("PropertyEnumString eq com.sap.odata.test1.ENString'String1' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isComplex("PropertyEnumString").goUpFilterValidator()
        .goOrder(0).right().isEnum(EdmTechProvider.nameENString, Arrays.asList("String1"));

    // TODO
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 1").isExSyntax(0);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16, PropertyInt32 PropertyDuration").isExSyntax(0);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 PropertyInt32, PropertyDuration desc").isExSyntax(0);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 asc, PropertyInt32 PropertyDuration desc").isExSyntax(0);
  }

  public void testSearch() {

    testUri.run("ESTwoKeyNav?$search=abc");
    testUri.run("ESTwoKeyNav?$search=NOT abc");

    testUri.run("ESTwoKeyNav?$search=abc AND def");
    testUri.run("ESTwoKeyNav?$search=abc  OR def");
    testUri.run("ESTwoKeyNav?$search=abc     def");

    testUri.run("ESTwoKeyNav?$search=abc AND def AND ghi");
    testUri.run("ESTwoKeyNav?$search=abc AND def  OR ghi");
    testUri.run("ESTwoKeyNav?$search=abc AND def     ghi");

    testUri.run("ESTwoKeyNav?$search=abc  OR def AND ghi");
    testUri.run("ESTwoKeyNav?$search=abc  OR def  OR ghi");
    testUri.run("ESTwoKeyNav?$search=abc  OR def     ghi");

    testUri.run("ESTwoKeyNav?$search=abc     def AND ghi");
    testUri.run("ESTwoKeyNav?$search=abc     def  OR ghi");
    testUri.run("ESTwoKeyNav?$search=abc     def     ghi");

    // mixed not
    testUri.run("ESTwoKeyNav?$search=    abc         def AND     ghi");
    testUri.run("ESTwoKeyNav?$search=NOT abc  NOT    def  OR NOT ghi");
    testUri.run("ESTwoKeyNav?$search=    abc         def     NOT ghi");

    // parenthesis
    testUri.run("ESTwoKeyNav?$search= (abc)");
    testUri.run("ESTwoKeyNav?$search= (abc AND  def)");
    testUri.run("ESTwoKeyNav?$search= (abc AND  def)   OR  ghi ");
    testUri.run("ESTwoKeyNav?$search= (abc AND  def)       ghi ");
    testUri.run("ESTwoKeyNav?$search=  abc AND (def    OR  ghi)");
    testUri.run("ESTwoKeyNav?$search=  abc AND (def        ghi)");
  }

  public static String encode(final String decoded) throws UnsupportedEncodingException {
    return URLEncoder.encode(decoded, "UTF-8");

  }

}
