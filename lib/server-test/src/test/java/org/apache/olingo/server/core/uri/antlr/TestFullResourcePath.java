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

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.http.HttpContentType;
import org.apache.olingo.commons.core.Encoder;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.server.core.uri.parser.UriParserException;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException;
import org.apache.olingo.server.core.uri.parser.UriParserSyntaxException;
import org.apache.olingo.server.core.uri.testutil.EdmTechTestProvider;
import org.apache.olingo.server.core.uri.testutil.FilterValidator;
import org.apache.olingo.server.core.uri.testutil.ResourceValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.junit.Ignore;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TestFullResourcePath {
  Edm edm = null;
  TestUriValidator testUri = null;
  ResourceValidator testRes = null;
  FilterValidator testFilter = null;

  public TestFullResourcePath() {
    edm = new EdmProviderImpl(new EdmTechTestProvider());
    testUri = new TestUriValidator().setEdm(edm);
    testRes = new ResourceValidator().setEdm(edm);
    testFilter = new FilterValidator().setEdm(edm);
  }

  @Test
  public void testFunctionBound_varOverloading() throws Exception {
    // on ESTwoKeyNav
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='ABC')").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);
  }

  @Test
  public void runBfuncBnCpropCastRtEs() throws Exception {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESBaseTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESBaseTwoKeyNav")
        .isType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnCpropCollRtEs() throws Exception {
    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim");

    testUri
        .run("ESKeyNav(PropertyInt16=1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnCpropRtEs() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);

  }

  @Test
  public void runBfuncBnEntityRtEs() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isFunction("BFCETTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEntityCastRtEs() throws Exception {
    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");

    testUri
        .run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='(''2'')')"
            + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'(''2'')'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnEsCastRtEs() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCESBaseTwoKeyNavRTESBaseTwoKey()"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCESBaseTwoKeyNavRTESBaseTwoKey")
        .isType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav"
        + "/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void runBfuncBnEsRtCprop() throws Exception {
    testUri.run("ESAllPrim/olingo.odata.test1.BFCESAllPrimRTCTAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFCESAllPrimRTCTAllPrim")
        .isType(ComplexTypeProvider.nameCTAllPrim);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/olingo.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, false)
        .isTypeFilterOnEntry(ComplexTypeProvider.nameCTBase);
  }

  @Test
  public void runBfuncBnEsRtCpropColl() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollCTTwoPrim()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true)
        .n()
        .isUriPathInfoKind(UriResourceKind.count);
  }

  @Test
  public void runBfuncBnEsRtEntityPpNp() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isUriPathInfoKind(UriResourceKind.ref);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/NavPropertyETMediaOne/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isNavProperty("NavPropertyETMediaOne", EntityTypeProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp)
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTAllPrim);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavOne/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.runEx("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(WrongParameter='1')")
        .isExSemantic(UriParserSemanticException.MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString=wrong)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void runBfuncBnEsRtEntyPpNpCast() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTTwoKeyNav()"
        + "/NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTTwoKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri
        .run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runBfuncBnEsRtEntityPpCp() throws Exception {

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTNavFiveProp);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComp/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTNavFiveProp)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyComp/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyComp")
        .isType(ComplexTypeProvider.nameCTNavFiveProp)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEntyPpCpCast() throws Exception {

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isComplex("PropertyCompTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString='1')"
        + "/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isComplex("PropertyCompTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);
  }

  @Test
  public void runBfuncBnEsRtEntityPpSp() throws Exception {
    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .n()
        .isValue();

  }

  @Test
  public void runBfuncBnEsRtEs() throws Exception {

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav(ParameterString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isParameter(0, "ParameterString", "'3'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runBfuncBnEsRtEsBa() throws Exception {

    testUri.run("ESKeyNav(PropertyInt16=1)/CollPropertyComp"
        + "/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()/olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp)
        .n()
        .isFunction("BFCCollCTPrimCompRTESAllPrim")
        .n()
        .isAction("BAESAllPrimRTETAllPrim");

  }

  @Test
  public void runBfuncBnEsRtPrim() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTString()/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTString")
        .isType(PropertyProvider.nameString)
        .n()
        .isValue();
  }

  @Test
  public void runbfuncBnEsRtPrimColl() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(PropertyProvider.nameString, true);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTCollString()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFCESTwoKeyNavRTCollString")
        .isType(PropertyProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropCollRtEs() throws Exception {
    testUri.run("ESKeyNav(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isFunction("BFCCollStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();
  }

  @Test
  public void runBfuncBnPpropRtEs() throws Exception {

    testUri.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isCount();

    testUri.run("ESKeyNav(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .n()
        .isFunction("BFCStringRTESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
  }

  @Test
  public void runBfuncBnSingleRtEs() throws Exception {

    testUri.run("SINav/olingo.odata.test1.BFCSINavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isFunction("BFCSINavRTESTwoKeyNav");
  }

  @Test
  public void runBfuncBnSingleCastRtEs() throws Exception {
    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.BFCETBaseTwoKeyNavRTESBaseTwoKey()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTESBaseTwoKey");
  }

  @Test
  public void runActionBound_on_EntityEntry() throws Exception {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");

    testUri.run("ESKeyNav(PropertyInt16=1)/olingo.odata.test1.BAETTwoKeyNavRTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isAction("BAETTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runActionBound_on_EntityCollection() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isAction("BAESTwoKeyNavRTESTwoKeyNav");
  }

  @Test
  public void runFunctionBound_on_var_Types() throws Exception {

    // on primitive
    testUri.run("ESAllPrim(1)/PropertyString/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETAllPrim, false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(PropertyProvider.nameString);

    // on collection of primitive
    testUri.run("ESCollAllPrim(1)/CollPropertyString/olingo.odata.test1.BFCCollStringRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETCollAllPrim, false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isType(PropertyProvider.nameString);

    // on complex
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')"
        + "/PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.complexProperty)
        .at(2)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // on collection of complex
    testUri.run("ESKeyNav(1)/CollPropertyComp/olingo.odata.test1.BFCCollCTPrimCompRTESAllPrim()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .at(1)
        .isType(ComplexTypeProvider.nameCTPrimComp, true)
        .at(2)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETAllPrim);

    // on entity
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='ABC')"
        + "/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // on collection of entity
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1).isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);
  }

  @Test
  public void runActionBound_on_EntityCast() throws Exception {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BAETBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isAction("BAETBaseTwoKeyNavRTETBaseTwoKeyNav");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav/olingo.odata.test1.BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isAction("BAETTwoBaseTwoKeyNavRTETBaseTwoKeyNav");
  }

  @Test
  public void runCrossjoin() throws Exception {
    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("$crossjoin(ESKeyNav, ESTwoKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav", "ESTwoKeyNav"));
  }

  @Test
  public void runCrossjoinError() throws Exception {
    testUri.runEx("$crossjoin").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("$crossjoin/error").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$crossjoin()").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid")
        .isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
  }

  @Test
  public void runEntityId() throws Exception {
    testUri.run("$entity", "$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isIdText("ESKeyNav(1)");
    testUri.run("$entity/olingo.odata.test1.ETKeyNav", "$id=ESKeyNav(1)")
        .isKind(UriInfoKind.entityId)
        .isEntityType(EntityTypeProvider.nameETKeyNav)
        .isIdText("ESKeyNav(1)");
  }

  @Test
  public void runEntityIdError() {
    // TODO planned: move to validator
    // testUri.runEx("$entity").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    // testUri.runEx("$entity?$idfalse=ESKeyNav(1)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    // testUri.runEx("$entity/olingo.odata.test1.invalidType?$id=ESKeyNav(1)").isExSemantic();
    // testUri.runEx("$entity/invalid?$id=ESKeyNav(1)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void runEsName() throws Exception {
    testUri.run("ESAllPrim")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true);

    testUri.run("ESAllPrim/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isType(EntityTypeProvider.nameETAllPrim, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNameError() {

    testUri.runEx("ESAllPrim/$count/$ref")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PROPERTIES);
    testUri.runEx("ESAllPrim/$ref/$count")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESAllPrim/$ref/invalid")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_PART_ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESAllPrim/$count/invalid")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_PART_ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESAllPrim/PropertyString")
        .isExSemantic(UriParserSemanticException.MessageKeys.PROPERTY_AFTER_COLLECTION);
    testUri.runEx("ESAllPrim(1)/whatever")
        .isExSemantic(UriParserSemanticException.MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim(PropertyInt16)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESAllPrim(PropertyInt16=)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim(PropertyInt16=1,Invalid='1')")
        .isExSemantic(UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("ESBase/olingo.odata.test1.ETBase/PropertyInt16")
        .isExSemantic(UriParserSemanticException.MessageKeys.PROPERTY_AFTER_COLLECTION);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETAllKey")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim('1')/olingo.odata.test1.ETAllKey")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETAllKey")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim()")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ESAllNullable(1)/CollPropertyString/$value")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);

    testUri.runEx("ETMixPrimCollComp(1)/ComplexProperty/$value")
        .isExSemantic(UriParserSemanticException.MessageKeys.RESOURCE_NOT_FOUND);
  }

  @Test
  public void runEsNameCast() throws Exception {
    testUri.run("ESTwoPrim/olingo.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETBase(-32768)/olingo.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

    testUri.run("ESTwoPrim/Namespace1_Alias.ETTwoBase(-32768)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase)
        .isKeyPredicate(0, "PropertyInt16", "-32768");

  }

  @Test
  public void runEsNamePpSpCast() throws Exception {

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyComp/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  @Test
  public void runEsNameKey() throws Exception {
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

    testUri.runEx("ESTwoPrim(wrong)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESTwoPrim(PropertyInt16=wrong)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void runEsNameParaKeys() throws Exception {
    testUri.run(encode("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
        + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
        + "PropertyInt64=-9223372036854775808,PropertyDecimal=1,PropertyDate=2013-09-25,"
        + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
        + "PropertyDuration=duration'P50903316DT2H25M4S',"
        + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
        + "PropertyTimeOfDay=12:34:55)"))
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
        .isKeyPredicate(7, "PropertyDecimal", "1")
        .isKeyPredicate(8, "PropertyDate", "2013-09-25")
        .isKeyPredicate(9, "PropertyDateTimeOffset", "2002-10-10T12:00:00-05:00")
        .isKeyPredicate(10, "PropertyDuration", "duration'P50903316DT2H25M4S'")
        .isKeyPredicate(11, "PropertyGuid", "12345678-1234-1234-1234-123456789012")
        .isKeyPredicate(12, "PropertyTimeOfDay", "12:34:55");
  }

  @Test
  public void runEsNameKeyCast() throws Exception {
    // testUri.runEx("ESTwoPrim(1)/olingo.odata.test1.ETBase(1)")
    //    .isExSemantic(UriParserSemanticException.MessageKeys.xxx);

    // testUri.runEx("ESTwoPrim/olingo.odata.test1.ETBase(1)/olingo.odata.test1.ETTwoBase(1)")
    //     .isExSemantic(UriParserSemanticException.MessageKeys.xxx);

    testUri.runEx("ESBase/olingo.odata.test1.ETTwoPrim(1)")
        .isExSemantic(UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER);

    testUri.run("ESTwoPrim(1)/olingo.odata.test1.ETBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBase);

    testUri.run("ESTwoPrim(1)/olingo.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETTwoBase(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETBase(1)/olingo.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBase);

    testUri.run("ESTwoPrim/olingo.odata.test1.ETTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoPrim")
        .isType(EntityTypeProvider.nameETTwoPrim)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBase);
  }

  @Test
  public void runEsNameParaKeysCast() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");
  }

  @Test
  public void run_EsNamePpCp() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComp");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComp/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isComplex("PropertyComp");
  }

  @Test
  public void runEsNamePpCpColl() throws Exception {
    testUri.run("ESMixPrimCollComp(5)/CollPropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "5")
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTTwoPrim, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavOne/CollPropertyComp/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runEsNamePpCpCast() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyComp/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isComplex("PropertyComp");

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyCompTwoPrim/olingo.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")

        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyCompTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim)
        .isTypeFilter(ComplexTypeProvider.nameCTBase);

    testUri
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
            + "/PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyCompTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBase);
  }

  @Test
  public void runNsNamePpNp() throws Exception {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETKeyNavMany(PropertyInt16=2)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isComplex("PropertyComp");

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/NavPropertyETKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavMany(4)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "4");

    testUri.run("ESKeyNav(1)/PropertyComp/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='(3)')"
        + "/PropertyComp/PropertyComp/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'(3)'")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)/NavPropertyETMediaMany(2)/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETMediaMany", EntityTypeProvider.nameETMedia, false)
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
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isNavProperty("NavPropertyETMediaOne", EntityTypeProvider.nameETMedia, false)
        .n()
        .isValue();

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n()
        .isRef();
  }

  @Test
  public void runEsNamePpNpCast() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETKeyNavMany(3)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETTwoBaseTwoKeyNav(PropertyInt16=3,PropertyString='4')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "3")
        .isKeyPredicate(1, "PropertyString", "'4'")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETBaseTwoKeyNavMany", EntityTypeProvider.nameETBaseTwoKeyNav, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/"
        + "NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=4,PropertyString='5')/"
        + "NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "4")
        .isKeyPredicate(1, "PropertyString", "'5'")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);
  }

  @Test
  public void runEsNamePpNpRc() throws Exception {
    // checks for using referential constrains to fill missing keys
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany('2')").goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");

    testUri.run("ESKeyNav(PropertyInt16=1)/NavPropertyETTwoKeyNavMany(PropertyString='2')").goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runEsNamePpSp() throws Exception {
    testUri.run("ESAllPrim(1)/PropertyByte")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false);

    testUri.run("ESAllPrim(1)/PropertyByte/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false)
        .n()
        .isValue();

    testUri.run("ESMixPrimCollComp(1)/PropertyComp/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixPrimCollComp")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void runEsNamePpSpColl() throws Exception {
    testUri.run("ESCollAllPrim(1)/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESCollAllPrim")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=2,PropertyString='3')/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isCount();

  }

  @Test
  public void runEsNameRef() throws Exception {
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
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isRef();
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isRef();
  }

  @Test
  public void runFunctionImpBf() throws Exception {

    testUri.run("FICRTString()/olingo.odata.test1.BFCStringRTESTwoKeyNav()");
  }

  @Test
  public void runFunctionImpCastBf() throws Exception {

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav"
        + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");
  }

  @Test
  public void runFunctionImpEntity() throws Exception {

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .isType(EntityTypeProvider.nameETKeyNav);

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

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");
  }

  @Test
  public void runFunctionImpEs() throws Exception {
    /**/
    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testUri.run("FINRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FINRTESMixPrimCollCompTwoParam")
        .isFunction("UFNRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isFunction("UFCRTESMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp)
        .n()
        .isCount();
  }

  @Test
  public void runFunctionImpError() {
    testUri.runEx("FICRTCollCTTwoPrimParam()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);
    testUri.runEx("FICRTCollCTTwoPrimParam(invalidParam=2)")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);
  }

  @Test
  public void runFunctionImpEsAlias() throws Exception {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)", "@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@parameterAlias)/$count", "@parameterAlias=1");
    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=@invalidAlias)", "@validAlias=1");
  }

  @Test
  public void runFunctionImpEsCast() throws Exception {

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isCount();

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESTwoKeyNavParam")
        .isFunction("UFCRTESTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

  }

  @Test
  public void runSingletonEntityValue() throws Exception {
    testUri.run("SIMedia/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SIMedia")
        .n().isValue();
  }

  @Test
  public void runSingletonPpNpCast() throws Exception {
    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany(1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1");

  }

  @Test
  public void runSingletonPpCpCast() throws Exception {
    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp");

    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyComp/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyComp")
        .n()
        .isComplex("PropertyComp");

    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompTwoPrim/olingo.odata.test1.CTBase")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isComplex("PropertyCompTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim)
        .isTypeFilter(ComplexTypeProvider.nameCTBase);

  }

  @Test
  public void runSingletonPpSpCast() throws Exception {
    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("SINav/olingo.odata.test1.ETBaseTwoKeyNav/CollPropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilter(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .isType(PropertyProvider.nameString, true);

  }

  @Test
  public void runSingletonEntityPpNp() throws Exception {
    testUri.run("SINav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("SINav/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'");

  }

  @Test
  public void runSingletonEntityPpCp() throws Exception {
    testUri.run("SINav/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComp");

    testUri.run("SINav/PropertyComp/PropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("PropertyComp")
        .n()
        .isComplex("PropertyComp");

  }

  @Test
  public void runSingletonEntityPpCpColl() throws Exception {
    testUri.run("SINav/CollPropertyComp")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true);

    testUri.run("SINav/CollPropertyComp/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isComplex("CollPropertyComp")
        .isType(ComplexTypeProvider.nameCTPrimComp, true)
        .n()
        .isCount();
  }

  @Test
  public void runSingletonEntityPpSp() throws Exception {
    testUri.run("SINav/PropertyString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void runSingletonEntityPpSpColl() throws Exception {
    testUri.run("SINav/CollPropertyString")

        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);
    testUri.run("SINav/CollPropertyString/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isSingleton("SINav")
        .n()
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n()
        .isCount();
  }

  @Test
  public void runExpand() throws Exception {

    testUri.run("ESKeyNav(1)", "$expand=*")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar();

    testUri.run("ESKeyNav(1)", "$expand=*/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar()
        .isSegmentRef();

    testUri.run("ESKeyNav(1)", "$expand=*/$ref,NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar().isSegmentRef()
        .next()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)", "$expand=*($levels=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar()
        .isLevelText("3");

    testUri.run("ESKeyNav(1)", "$expand=*($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .isSegmentStar()
        .isLevelText("max");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .n().isRef();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator().isFilterSerialized("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1;$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isCount();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne/$count")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .n().isCount();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isCount()
        .goUpExpandValidator()
        .isFilterSerialized("<<PropertyInt16> gt <1>>");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isFilterSerialized("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($count=true)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString")
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne($levels=5)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("5");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSelectText("PropertyString")
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne($levels=max)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("max");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1;$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1%3b$top=2)")
        .isKind(UriInfoKind.resource).goPath().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')", "$expand=NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav"
             + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isComplex("PropertyCompNav")
        .isType(ComplexTypeProvider.nameCTBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav"
        + "/olingo.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n()
        .isComplex("PropertyCompNav")
        .isType(ComplexTypeProvider.nameCTBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)")
        .isKind(UriInfoKind.resource).goPath().first()
        .goExpand().first()
        .goPath()
        .first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .next()
        .goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("2")
        .isTopText("1");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')", "$expand=olingo.odata.test1.ETBaseTwoKeyNav"
        + "/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETTwoBaseTwoKeyNav($select=PropertyString)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        // .isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(EntityTypeProvider.nameETTwoKeyNav)
        // .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany("
        + "$expand=NavPropertyETKeyNavOne))")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')", "$select=olingo.odata.test1.ETBaseTwoKeyNav"
        + "/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isSelectStartType(0, EntityTypeProvider.nameETBaseTwoKeyNav)
        .goSelectItem(0)
        .first()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($select=PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .isSelectText("PropertyInt16")
        .goSelectItem(0).isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($select=PropertyComp/PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .isSelectText("PropertyComp/PropertyInt16");

    testUri.runEx("ESKeyNav", "$expand=undefined")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESTwoKeyNav", "$expand=PropertyCompNav/undefined")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void runTop() throws Exception {
    // top
    testUri.run("ESKeyNav", "$top=1")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("1");

    testUri.run("ESKeyNav", "$top=0")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("0");

    testUri.run("ESKeyNav", "$top=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isEntitySet("ESKeyNav")
        .isTopText("-3");

    testUri.runEx("ESKeyNav", "$top=undefined")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESKeyNav", "$top=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void runFormat() throws Exception {
    // format
    testUri.run("ESKeyNav(1)", "$format=atom")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("atom");
    testUri.run("ESKeyNav(1)", "$format=json")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("json");
    testUri.run("ESKeyNav(1)", "$format=xml")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("xml");
    testUri.run("ESKeyNav(1)", "$format=IANA_content_type/must_contain_a_slash")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("IANA_content_type/must_contain_a_slash");
    testUri.run("ESKeyNav(1)", "$format=Test_all_valid_signsSpecified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27")
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText("Test_all_valid_signsSpecified_for_format_signs&-._~$@'/Aa123&-._~$@'");
    testUri.run("ESKeyNav(1)", "$format=" + HttpContentType.APPLICATION_ATOM_XML_ENTRY_UTF8)
        .isKind(UriInfoKind.resource).goPath()
        .isFormatText(HttpContentType.APPLICATION_ATOM_XML_ENTRY_UTF8);
    testUri.runEx("ESKeyNav(1)", "$format=noSlash")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT);
    testUri.runEx("ESKeyNav(1)", "$format=slashAtEnd/")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT);
    testUri.runEx("ESKeyNav(1)", "$format=/startsWithSlash")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT);
    testUri.runEx("ESKeyNav(1)", "$format=two/Slashes/tooMuch")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT);
    testUri.runEx("ESKeyNav(1)", "$format=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION_FORMAT);
  }

  @Test
  public void runCount() throws Exception {
    // count
    testUri.run("ESAllPrim", "$count=true")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("true");
    testUri.run("ESAllPrim", "$count=false")
        .isKind(UriInfoKind.resource).goPath()
        .isInlineCountText("false");
    testUri.runEx("ESAllPrim", "$count=undefined")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESAllPrim", "$count=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void skip() throws Exception {
    // skip
    testUri.run("ESAllPrim", "$skip=3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("3");
    testUri.run("ESAllPrim", "$skip=0")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("0");
    testUri.run("ESAllPrim", "$skip=-3")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipText("-3");
    testUri.runEx("ESAllPrim", "$skip=F")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESAllPrim", "$skip=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void skiptoken() throws Exception {
    testUri.run("ESAllPrim", "$skiptoken=foo")
        .isKind(UriInfoKind.resource).goPath()
        .isSkipTokenText("foo");
  }

  @Test
  public void notExistingSystemQueryOption() throws Exception {
    testUri.runEx("ESAllPrim", "$wrong=error")
        .isExSyntax(UriParserSyntaxException.MessageKeys.UNKNOWN_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void misc() throws Exception {

    testUri.run("")
        .isKind(UriInfoKind.service);
    testUri.run("/")
        .isKind(UriInfoKind.service);

    testUri.run("$all")
        .isKind(UriInfoKind.all);

    testUri.run("$metadata")
        .isKind(UriInfoKind.metadata);

    testUri.run("$batch")
        .isKind(UriInfoKind.batch);

    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.runEx("$metadata/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$batch/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$crossjoin(ESKeyNav)/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$all/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$entity/olingo.odata.test1.ETKeyNav/$ref")
        .isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);

    testUri.runEx("$wrong").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("", "$wrong").isExSyntax(UriParserSyntaxException.MessageKeys.UNKNOWN_SYSTEM_QUERY_OPTION);

    testUri.run("ESKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav");
    testUri.run("ESKeyNav(1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1");
    testUri.runEx("ESKeyNav()").isExSemantic(UriParserSemanticException.MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.run("SINav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isSingleton("SINav");

    testUri.run("FICRTESMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath()
        .isFunctionImport("FICRTESMixPrimCollCompTwoParam")
        .isType(EntityTypeProvider.nameETMixPrimCollComp)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTETKeyNav")
        .isType(EntityTypeProvider.nameETKeyNav);

    testUri.run("FICRTCollCTTwoPrim()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollCTTwoPrim")
        .isType(ComplexTypeProvider.nameCTTwoPrim);

    testUri.run("FICRTCTAllPrimTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCTAllPrimTwoParam")
        .isType(ComplexTypeProvider.nameCTAllPrim)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTCollStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollStringTwoParam")
        .isType(PropertyProvider.nameString)
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(PropertyProvider.nameString)
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("FICRTStringTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTStringTwoParam")
        .isFunction("UFCRTStringTwoParam")
        .isType(PropertyProvider.nameString)
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

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESAllPrim/olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav);

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

    testUri.run("ESAllPrim/olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFCESTwoKeyNavRTESTwoKeyNav");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/$ref")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n().isRef();

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBaseTwoKeyNav/$value")
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n().isValue();

  }

  @Test
  public void testFilter() throws UriParserException {

    testFilter.runOnETTwoKeyNav("PropertyString")
        .is("<PropertyString>")
        .isType(PropertyProvider.nameString);

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyInt16")
        .is("<PropertyComp/PropertyInt16>")
        .isType(PropertyProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate")
        .is("<PropertyComp/PropertyComp/PropertyDate>")
        .isType(PropertyProvider.nameDate);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne")
        .is("<NavPropertyETTwoKeyNavOne>")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyString")
        .is("<NavPropertyETTwoKeyNavOne/PropertyString>")
        .isType(PropertyProvider.nameString);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComp>")
        .isType(ComplexTypeProvider.nameCTPrimComp);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp>")
        .isType(ComplexTypeProvider.nameCTAllPrim);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16")
        .is("<NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16>")
        .isType(PropertyProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16 eq 1")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16> eq <1>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isLiteral("1");

    testFilter.runOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/"
        + "PropertyString eq 'SomeString'")
        .is("<<NavPropertyETKeyNavMany/NavPropertyETTwoKeyNavMany/PropertyString> eq <'SomeString'>>")
        .root().left()
        .isType(PropertyProvider.nameString)
        .isMember().goPath()
        .first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicateRef(0, "PropertyInt16", "PropertyInt16")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right();

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(PropertyProvider.nameDate)
        .isMember().isMemberStartType(EntityTypeProvider.nameETBaseTwoKeyNav).goPath()
        // .first().isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(EntityTypeProvider.nameETTwoKeyNav).isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        // .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .first().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("2013-11-12");

    testFilter.runOnCTTwoPrim("olingo.odata.test1.CTBase/AdditionalPropString eq 'SomeString'")
        .is("<<AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(PropertyProvider.nameString)
        .isMember().isMemberStartType(ComplexTypeProvider.nameCTBase).goPath()
        // .first().isUriPathInfoKind(UriResourceKind.startingTypeFilter)
        // .isType(EntityTypeProvider.nameCTTwoPrim).isTypeFilterOnEntry(ComplexTypeProvider.nameCTBase)
        // .n().isPrimitiveProperty("AdditionalPropString", PropertyProvider.nameString, false)
        .first().isPrimitiveProperty("AdditionalPropString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("'SomeString'");

    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .root().left()
        .isType(PropertyProvider.nameDate)
        .root().right()
        .isLiteral("2013-11-12");

    testFilter
        .runOnETTwoKeyNav("PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase/AdditionalPropString eq 'SomeString'")
        .is("<<PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase/AdditionalPropString> eq <'SomeString'>>")
        .root().left()
        .isType(PropertyProvider.nameString)
        .root().right()
        .isLiteral("'SomeString'");

    testFilter.runOnETTwoKeyNavEx("invalid")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/invalid")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("concat('a','b')/invalid").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/concat('a','b')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyInt16 eq '1'")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyComp/PropertyDate eq 1")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyComp/PropertyString eq 1")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyInt64 eq 1")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);

    testFilter.runOnETAllPrim("PropertySByte eq PropertySByte")
        .is("<<PropertySByte> eq <PropertySByte>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte ne PropertySByte")
        .is("<<PropertySByte> ne <PropertySByte>>")
        .isBinary(BinaryOperatorKind.NE)
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte add PropertySByte")
        .is("<<PropertySByte> add <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte add PropertyByte")
        .is("<<PropertyByte> add <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 add PropertyInt16")
        .is("<<PropertyInt16> add <PropertyInt16>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 add PropertyInt32")
        .is("<<PropertyInt32> add <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt32)
        .root().right()
        .isType(PropertyProvider.nameInt32);

    testFilter.runOnETAllPrim("PropertyInt64 add PropertyInt64")
        .is("<<PropertyInt64> add <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle add PropertySingle")
        .is("<<PropertySingle> add <PropertySingle>>")
        .root().left()
        .isType(PropertyProvider.nameSingle)
        .root().right()
        .isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble add PropertyDouble")
        .is("<<PropertyDouble> add <PropertyDouble>>")
        .root().left()
        .isType(PropertyProvider.nameDouble)
        .root().right()
        .isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal add PropertyDecimal")
        .is("<<PropertyDecimal> add <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyDecimal")
        .is("<<PropertySByte> add <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt32")
        .is("<<PropertySByte> add <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt64")
        .is("<<PropertySByte> add <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset add PropertyDuration")
        .is("<<PropertyDateTimeOffset> add <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDateTimeOffset)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration add PropertyDuration")
        .is("<<PropertyDuration> add <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDuration)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate add PropertyDuration")
        .is("<<PropertyDate> add <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDate)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertySByte sub PropertySByte")
        .is("<<PropertySByte> sub <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte sub PropertyByte")
        .is("<<PropertyByte> sub <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 sub PropertyInt16")
        .is("<<PropertyInt16> sub <PropertyInt16>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 sub PropertyInt32")
        .is("<<PropertyInt32> sub <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt32)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 sub PropertyInt64")
        .is("<<PropertyInt64> sub <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle sub PropertySingle")
        .is("<<PropertySingle> sub <PropertySingle>>")
        .root().left()
        .isType(PropertyProvider.nameSingle)
        .root().right()
        .isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble sub PropertyDouble")
        .is("<<PropertyDouble> sub <PropertyDouble>>")
        .root().left()
        .isType(PropertyProvider.nameDouble)
        .root().right()
        .isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyDecimal")
        .is("<<PropertyDecimal> sub <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt32")
        .is("<<PropertyDecimal> sub <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt64")
        .is("<<PropertyDecimal> sub <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyByte")
        .is("<<PropertyDecimal> sub <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDuration")
        .is("<<PropertyDateTimeOffset> sub <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDateTimeOffset)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration sub PropertyDuration")
        .is("<<PropertyDuration> sub <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDuration)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDateTimeOffset")
        .is("<<PropertyDateTimeOffset> sub <PropertyDateTimeOffset>>")
        .root().left()
        .isType(PropertyProvider.nameDateTimeOffset)
        .root().right()
        .isType(PropertyProvider.nameDateTimeOffset);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDuration")
        .is("<<PropertyDate> sub <PropertyDuration>>")
        .root().left()
        .isType(PropertyProvider.nameDate)
        .root().right()
        .isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDate")
        .is("<<PropertyDate> sub <PropertyDate>>")
        .root().left()
        .isType(PropertyProvider.nameDate)
        .root().right()
        .isType(PropertyProvider.nameDate);
    testFilter.runOnETAllPrim("PropertySByte mul PropertySByte")
        .is("<<PropertySByte> mul <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mul PropertyByte")
        .is("<<PropertyByte> mul <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mul PropertyInt16")
        .is("<<PropertyInt16> mul <PropertyInt16>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mul PropertyInt32")
        .is("<<PropertyInt32> mul <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt32)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt64")
        .is("<<PropertyInt64> mul <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mul PropertySingle")
        .is("<<PropertySingle> mul <PropertySingle>>")
        .root().left()
        .isType(PropertyProvider.nameSingle)
        .root().right()
        .isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mul PropertyDouble")
        .is("<<PropertyDouble> mul <PropertyDouble>>")
        .root().left()
        .isType(PropertyProvider.nameDouble)
        .root().right()
        .isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mul PropertyDecimal")
        .is("<<PropertyDecimal> mul <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt32")
        .is("<<PropertyInt64> mul <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertySByte")
        .is("<<PropertyInt64> mul <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyDecimal")
        .is("<<PropertyInt64> mul <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte div PropertySByte")
        .is("<<PropertySByte> div <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte div PropertyByte")
        .is("<<PropertyByte> div <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 div PropertyInt16")
        .is("<<PropertyInt16> div <PropertyInt16>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 div PropertyInt32")
        .is("<<PropertyInt32> div <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt32)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 div PropertyInt64")
        .is("<<PropertyInt64> div <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle div PropertySingle")
        .is("<<PropertySingle> div <PropertySingle>>")
        .root().left()
        .isType(PropertyProvider.nameSingle)
        .root().right()
        .isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble div PropertyDouble")
        .is("<<PropertyDouble> div <PropertyDouble>>")
        .root().left()
        .isType(PropertyProvider.nameDouble)
        .root().right()
        .isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal div PropertyDecimal")
        .is("<<PropertyDecimal> div <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertyInt32")
        .is("<<PropertyByte> div <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyByte div PropertyDecimal")
        .is("<<PropertyByte> div <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertySByte")
        .is("<<PropertyByte> div <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte div 0")
        .is("<<PropertyByte> div <0>>");

    testFilter.runOnETAllPrim("0 div 0")
        .is("<<0> div <0>>");

    testFilter.runOnETAllPrim("PropertySByte mod PropertySByte")
        .is("<<PropertySByte> mod <PropertySByte>>")
        .root().left()
        .isType(PropertyProvider.nameSByte)
        .root().right()
        .isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mod PropertyByte")
        .is("<<PropertyByte> mod <PropertyByte>>")
        .root().left()
        .isType(PropertyProvider.nameByte)
        .root().right()
        .isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mod PropertyInt16")
        .is("<<PropertyInt16> mod <PropertyInt16>>")
        .root().left()
        .isType(PropertyProvider.nameInt16)
        .root().right()
        .isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mod PropertyInt32")
        .is("<<PropertyInt32> mod <PropertyInt32>>")
        .root().left()
        .isType(PropertyProvider.nameInt32)
        .root().right()
        .isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mod PropertyInt64")
        .is("<<PropertyInt64> mod <PropertyInt64>>")
        .root().left()
        .isType(PropertyProvider.nameInt64)
        .root().right()
        .isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mod PropertySingle")
        .is("<<PropertySingle> mod <PropertySingle>>")
        .root().left()
        .isType(PropertyProvider.nameSingle)
        .root().right()
        .isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mod PropertyDouble")
        .is("<<PropertyDouble> mod <PropertyDouble>>")
        .root().left()
        .isType(PropertyProvider.nameDouble)
        .root().right()
        .isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mod PropertyDecimal")
        .is("<<PropertyDecimal> mod <PropertyDecimal>>")
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);

    //
    testFilter.runOnETAllPrim("PropertyDecimal ge PropertyDecimal")
        .is("<<PropertyDecimal> ge <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.GE)
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal lt PropertyDecimal")
        .is("<<PropertyDecimal> lt <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LT)
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal le PropertyDecimal")
        .is("<<PropertyDecimal> le <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LE)
        .root().left()
        .isType(PropertyProvider.nameDecimal)
        .root().right()
        .isType(PropertyProvider.nameDecimal);

    testFilter.runOnETAllPrim("PropertyDecimal sub NaN")
        .right().isLiteral("NaN").isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal sub -INF")
        .right().isLiteral("-INF").isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal sub INF")
        .right().isLiteral("INF").isType(PropertyProvider.nameDecimal);
  }

  @Test
  public void testFilterProperties() throws UriParserException {
    testFilter.runOnETAllPrim("PropertyByte mod 0")
        .is("<<PropertyByte> mod <0>>");

    testFilter.runOnETAllPrim("olingo.odata.test1.UFCRTETTwoKeyNavParamCTTwoPrim(ParameterCTTwoPrim=@ParamAlias)")
        .is("<UFCRTETTwoKeyNavParamCTTwoPrim>")
        .goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParamCTTwoPrim")
        .isParameterAlias(0, "ParameterCTTwoPrim", "@ParamAlias");

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam"
        + "(ParameterString=PropertyComp/PropertyComp/PropertyString)(PropertyInt16=1,PropertyString='2')"
        + "/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTESTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNavParam")
        .isParameter(0, "ParameterString", "PropertyComp/PropertyComp/PropertyString")
        .goParameter(0)
        .isMember()
        .goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .goUpToResourceValidator()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTETTwoKeyNavParam"
        + "(ParameterString=null)/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTETTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTETTwoKeyNavParam")
        .goParameter(0)
        .isNull()
        .goUpToResourceValidator()
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany/olingo.odata.test1.BFCESTwoKeyNavRTString()"
        + " eq 'SomeString'")
        .is("<<NavPropertyETTwoKeyNavMany/BFCESTwoKeyNavRTString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n()
        .isFunction("BFCESTwoKeyNavRTString");

    testFilter.runOnETTwoKeyNav("$it/olingo.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()/PropertyString eq 'SomeString'")
        .is("<<$it/BFESTwoKeyNavRTESTwoKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isIt()
        .n()
        .isFunction("BFESTwoKeyNavRTESTwoKeyNav")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.BFESTwoKeyNavRTESTwoKeyNav()/PropertyString eq 'SomeString'")
        .is("<<BFESTwoKeyNavRTESTwoKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isFunction("BFESTwoKeyNavRTESTwoKeyNav")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/olingo.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()"
        + "/PropertyComp/PropertyComp/PropertyString eq 'Walldorf'")
        .is("<<NavPropertyETTwoKeyNavOne/BFCETTwoKeyNavRTETTwoKeyNav/PropertyComp/PropertyComp/PropertyString> "
            + "eq <'Walldorf'>>")
        .root().left().goPath()
        .first()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n()
        .isFunction("BFCETTwoKeyNavRTETTwoKeyNav")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNavParam"
        + "(ParameterString='1')"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTESTwoKeyNavParam/olingo.odata.test1.ETBaseTwoKeyNav/PropertyString> "
            + "eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTESTwoKeyNavParam")
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNavSingle("$it/olingo.odata.test1.BFCETTwoKeyNavRTCTTwoPrim()/olingo.odata.test1.CTBase"
        + "/PropertyString eq 'SomeString'")
        .is("<<$it/BFCETTwoKeyNavRTCTTwoPrim/olingo.odata.test1.CTBase/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isIt()
        .n()
        .isFunction("BFCETTwoKeyNavRTCTTwoPrim")
        .isTypeFilterOnEntry(ComplexTypeProvider.nameCTBase)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)/PropertyInt16 eq 2")
        .is("<<UFCRTETTwoKeyNavParam/PropertyInt16> eq <2>>")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=@Param1Alias)"
        + "/PropertyInt16 eq 2")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameterAlias(0, "ParameterInt16", "@Param1Alias")
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=1)"
        + "/PropertyComp/PropertyComp/PropertyString eq 'SomeString'")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=PropertyInt16)"
        + "/PropertyComp/PropertyComp/PropertyString eq 'SomeString'")
        .root().left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "PropertyInt16")
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
  }

  @Test
  public void testFilterPMethods() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETKeyNav("indexof(PropertyString,'47') eq 5")
        .is("<<indexof(<PropertyString>,<'47'>)> eq <5>>")
        .root().left()
        .isMethod(MethodKind.INDEXOF, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'47'>");

    testFilter.runOnETKeyNav("tolower(PropertyString) eq 'foo'")
        .is("<<tolower(<PropertyString>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodKind.TOLOWER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("toupper(PropertyString) eq 'FOO'")
        .is("<<toupper(<PropertyString>)> eq <'FOO'>>")
        .root().left()
        .isMethod(MethodKind.TOUPPER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("trim(PropertyString) eq 'fooba'")
        .is("<<trim(<PropertyString>)> eq <'fooba'>>")
        .root().left()
        .isMethod(MethodKind.TRIM, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,2,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<2>,<4>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodKind.SUBSTRING, 3)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<2>")
        .isParameterText(2, "<4>");

    testFilter.runOnETKeyNav("concat(PropertyString,PropertyCompTwoPrim/PropertyString) eq 'foo'")
        .is("<<concat(<PropertyString>,<PropertyCompTwoPrim/PropertyString>)> eq <'foo'>>")
        .root().left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<PropertyCompTwoPrim/PropertyString>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .root().left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .root().left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("concat(PropertyString, cast(PropertyCompAllPrim/PropertyInt16,Edm.String))")
        .is("<concat(<PropertyString>,<cast(<PropertyCompAllPrim/PropertyInt16>,<Edm.String>)>)>")
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<cast(<PropertyCompAllPrim/PropertyInt16>,<Edm.String>)>")
        .goParameter(1)
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<PropertyCompAllPrim/PropertyInt16>")
        .isParameterText(1, "<Edm.String>");

    testFilter.runOnETKeyNav("length(PropertyString) eq 32")
        .is("<<length(<PropertyString>)> eq <32>>")
        .root().left()
        .isMethod(MethodKind.LENGTH, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETAllPrim("year(PropertyDate) eq 2013")
        .is("<<year(<PropertyDate>)> eq <2013>>")
        .root().left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("year(2013-09-25) eq 2013")
        .is("<<year(<2013-09-25>)> eq <2013>>")
        .root().left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("year(PropertyDateTimeOffset) eq 2013")
        .is("<<year(<PropertyDateTimeOffset>)> eq <2013>>")
        .root().left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("year(2013-09-25T12:34:56.123456789012-10:24) eq 2013")
        .is("<<year(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013>>")
        .root().left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("month(PropertyDate) eq 9")
        .is("<<month(<PropertyDate>)> eq <9>>")
        .root().left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("month(2013-09-25) eq 9")
        .is("<<month(<2013-09-25>)> eq <9>>")
        .root().left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("month(PropertyDateTimeOffset) eq 9")
        .is("<<month(<PropertyDateTimeOffset>)> eq <9>>")
        .root().left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("month(2013-09-25T12:34:56.123456789012-10:24) eq 9")
        .is("<<month(<2013-09-25T12:34:56.123456789012-10:24>)> eq <9>>")
        .root().left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("day(PropertyDate) eq 25")
        .is("<<day(<PropertyDate>)> eq <25>>")
        .root().left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("day(2013-09-25) eq 25")
        .is("<<day(<2013-09-25>)> eq <25>>")
        .root().left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("day(PropertyDateTimeOffset) eq 25")
        .is("<<day(<PropertyDateTimeOffset>)> eq <25>>")
        .root().left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("day(2013-09-25T12:34:56.123456789012-10:24) eq 25")
        .is("<<day(<2013-09-25T12:34:56.123456789012-10:24>)> eq <25>>")
        .root().left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .root().left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .root().left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(2013-09-25T12:34:56.123456789012-10:24) eq 2")
        .is("<<hour(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2>>")
        .root().left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyTimeOfDay) eq 2")
        .is("<<hour(<PropertyTimeOfDay>)> eq <2>>")
        .root().left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("hour(12:34:55.123456789012) eq 12")
        .is("<<hour(<12:34:55.123456789012>)> eq <12>>")
        .root().left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("minute(PropertyDateTimeOffset) eq 34")
        .is("<<minute(<PropertyDateTimeOffset>)> eq <34>>")
        .root().left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("minute(2013-09-25T12:34:56.123456789012-10:24) eq 34")
        .is("<<minute(<2013-09-25T12:34:56.123456789012-10:24>)> eq <34>>")
        .root().left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("minute(PropertyTimeOfDay) eq 34")
        .is("<<minute(<PropertyTimeOfDay>)> eq <34>>")
        .root().left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("minute(12:34:55.123456789012) eq 34")
        .is("<<minute(<12:34:55.123456789012>)> eq <34>>")
        .root().left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("second(PropertyDateTimeOffset) eq 56")
        .is("<<second(<PropertyDateTimeOffset>)> eq <56>>")
        .root().left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("second(2013-09-25T12:34:56.123456789012-10:24) eq 56")
        .is("<<second(<2013-09-25T12:34:56.123456789012-10:24>)> eq <56>>")
        .root().left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("second(PropertyTimeOfDay) eq 56")
        .is("<<second(<PropertyTimeOfDay>)> eq <56>>")
        .root().left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("second(12:34:55.123456789012) eq 56")
        .is("<<second(<12:34:55.123456789012>)> eq <56>>")
        .root().left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyDateTimeOffset) eq 123456789012")
        .is("<<fractionalseconds(<PropertyDateTimeOffset>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("fractionalseconds(2013-09-25T12:34:56.123456789012-10:24) eq 123456789012")
        .is("<<fractionalseconds(<2013-09-25T12:34:56.123456789012-10:24>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyTimeOfDay) eq 123456789012")
        .is("<<fractionalseconds(<PropertyTimeOfDay>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("fractionalseconds(12:34:55.123456789012) eq 123456789012")
        .is("<<fractionalseconds(<12:34:55.123456789012>)> eq <123456789012>>")
        .root().left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("totalseconds(PropertyDuration) eq 4711")
        .is("<<totalseconds(<PropertyDuration>)> eq <4711>>")
        .root().left()
        .isMethod(MethodKind.TOTALSECONDS, 1)
        .isParameterText(0, "<PropertyDuration>");

    testFilter.runOnETAllPrim("totalseconds(duration'P10DT5H34M21.123456789012S') eq 4711")
        .is("<<totalseconds(<duration'P10DT5H34M21.123456789012S'>)> eq <4711>>")
        .root().left()
        .isMethod(MethodKind.TOTALSECONDS, 1)
        .isParameterText(0, "<duration'P10DT5H34M21.123456789012S'>");

    testFilter.runOnETAllPrim("date(PropertyDateTimeOffset) eq 2013-09-25")
        .is("<<date(<PropertyDateTimeOffset>)> eq <2013-09-25>>")
        .root().left()
        .isMethod(MethodKind.DATE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("date(2013-09-25T12:34:56.123456789012-10:24) eq 2013-09-25")
        .is("<<date(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013-09-25>>")
        .root().left()
        .isMethod(MethodKind.DATE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("time(PropertyDateTimeOffset) eq 12:34:55.123456789012")
        .is("<<time(<PropertyDateTimeOffset>)> eq <12:34:55.123456789012>>")
        .root().left()
        .isMethod(MethodKind.TIME, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("time(2013-09-25T12:34:56.123456789012-10:24) eq 12:34:55.123456789012")
        .is("<<time(<2013-09-25T12:34:56.123456789012-10:24>)> eq <12:34:55.123456789012>>")
        .root().left()
        .isMethod(MethodKind.TIME, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("round(PropertyDouble) eq 17")
        .is("<<round(<PropertyDouble>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("round(17.45e1) eq 17")
        .is("<<round(<17.45e1>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("round(PropertyDecimal) eq 17")
        .is("<<round(<PropertyDecimal>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("round(17.45) eq 17")
        .is("<<round(<17.45>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("floor(PropertyDouble) eq 17")
        .is("<<floor(<PropertyDouble>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("floor(17.45e1) eq 17")
        .is("<<floor(<17.45e1>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("floor(PropertyDecimal) eq 17")
        .is("<<floor(<PropertyDecimal>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("floor(17.45) eq 17")
        .is("<<floor(<17.45>)> eq <17>>")
        .root().left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("ceiling(PropertyDouble) eq 18")
        .is("<<ceiling(<PropertyDouble>)> eq <18>>")
        .root().left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("ceiling(17.55e1) eq 18")
        .is("<<ceiling(<17.55e1>)> eq <18>>")
        .root().left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<17.55e1>");

    testFilter.runOnETAllPrim("ceiling(PropertyDecimal) eq 18")
        .is("<<ceiling(<PropertyDecimal>)> eq <18>>")
        .root().left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("ceiling(17.55) eq 18")
        .is("<<ceiling(<17.55>)> eq <18>>")
        .root().left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<17.55>");

    testFilter.runOnETAllPrim("totaloffsetminutes(PropertyDateTimeOffset) eq 4711")
        .is("<<totaloffsetminutes(<PropertyDateTimeOffset>)> eq <4711>>")
        .root().left()
        .isMethod(MethodKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("totaloffsetminutes(2013-09-25T12:34:56.123456789012-10:24) eq 4711")
        .is("<<totaloffsetminutes(<2013-09-25T12:34:56.123456789012-10:24>)> eq <4711>>")
        .root().left()
        .isMethod(MethodKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("mindatetime()")
        .is("<mindatetime()>")
        .isMethod(MethodKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("mindatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<mindatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime()")
        .is("<maxdatetime()>")
        .isMethod(MethodKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<maxdatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("now()")
        .is("<now()>")
        .isMethod(MethodKind.NOW, 0);

    testFilter.runOnETAllPrim("now() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<now()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .root().left()
        .isMethod(MethodKind.NOW, 0);

    testFilter.runOnETTwoKeyNav("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnCTTwoPrim("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(ComplexTypeProvider.nameCTTwoPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnString("$it eq 'Walldorf'")
        .is("<<$it> eq <'Walldorf'>>")
        .root().left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnString("endswith($it,'sap.com')")
        .is("<endswith(<$it>,<'sap.com'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnString("endswith($it,'sap.com') eq false")
        .is("<<endswith(<$it>,<'sap.com'>)> eq <false>>")
        .root().left()
        .isMethod(MethodKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("endswith($it/CollPropertyString,'sap.com')")
        .is("<endswith(<$it/CollPropertyString>,<'sap.com'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .isParameterText(0, "<$it/CollPropertyString>")
        .isParameterText(1, "<'sap.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyInt16 eq $root"
        + "/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyInt16")
        .is("<<PropertyComp/PropertyComp/PropertyInt16> eq <$root/ESTwoKeyNav/PropertyInt16>>")
        .root().left()
        .goPath()
        .first().isComplex("PropertyComp").isType(ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplex("PropertyComp").isType(ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETKeyNav("cast(olingo.odata.test1.ETBaseTwoKeyNav)")
        .is("<cast(<olingo.odata.test1.ETBaseTwoKeyNav>)>")
        .root()
        .isMethod(MethodKind.CAST, 1)
        .isParameterText(0, "<olingo.odata.test1.ETBaseTwoKeyNav>")
        .goParameter(0)
        .isTypedLiteral(EntityTypeProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("cast(PropertyCompTwoPrim,olingo.odata.test1.CTBase)")
        .is("<cast(<PropertyCompTwoPrim>,<olingo.odata.test1.CTBase>)>")
        .root()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<PropertyCompTwoPrim>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isComplex("PropertyCompTwoPrim").isType(ComplexTypeProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
        .root()
        .goParameter(1)
        .isTypedLiteral(ComplexTypeProvider.nameCTBase);

    testFilter.runOnETKeyNav("cast($it,olingo.odata.test1.CTBase)")
        .is("<cast(<$it>,<olingo.odata.test1.CTBase>)>")
        .root()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTBase);

    testFilter.runOnETKeyNav("cast($it,olingo.odata.test1.CTBase) eq cast($it,olingo.odata.test1.CTBase)"
        )
        .is("<<cast(<$it>,<olingo.odata.test1.CTBase>)> eq <cast(<$it>,<olingo.odata.test1.CTBase>)>>")
        .root().left()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().left()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTBase)
        .root().right()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().right()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTBase);

    testFilter.runOnInt32("cast(Edm.Int32)")
        .is("<cast(<Edm.Int32>)>")
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnDateTimeOffset("cast(Edm.DateTimeOffset)")
        .is("<cast(<Edm.DateTimeOffset>)>")
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnDuration("cast(Edm.Duration)")
        .is("<cast(<Edm.Duration>)>")
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnTimeOfDay("cast(Edm.TimeOfDay)")
        .is("<cast(<Edm.TimeOfDay>)>")
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(CollPropertyInt16,Edm.Int32)")
        .is("<cast(<CollPropertyInt16>,<Edm.Int32>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath().first()
        .isPrimitiveProperty("CollPropertyInt16", PropertyProvider.nameInt16, true)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnETTwoKeyNav(
        "cast(PropertyComp/PropertyComp/PropertyDateTimeOffset,Edm.DateTimeOffset)")
        .is("<cast(<PropertyComp/PropertyComp/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("cast(PropertyComp/PropertyComp/PropertyDuration,Edm.Duration)")
        .is("<cast(<PropertyComp/PropertyComp/PropertyDuration>,<Edm.Duration>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("cast(PropertyComp/PropertyComp/PropertyTimeOfDay,Edm.TimeOfDay)")
        .is("<cast(<PropertyComp/PropertyComp/PropertyTimeOfDay>,<Edm.TimeOfDay>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(PropertyCompAllPrim,olingo.odata.test1.CTTwoPrim)")
        .is("<cast(<PropertyCompAllPrim>,<olingo.odata.test1.CTTwoPrim>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyCompAllPrim", ComplexTypeProvider.nameCTAllPrim, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    // testFilter.runOnETKeyNav(" Xcast(PropertyCompTwoPrim,olingo.odata.test1.CTAllPrim)");

    testFilter.runOnETKeyNav("cast(NavPropertyETKeyNavOne,olingo.odata.test1.ETKeyPrimNav)")
        .is("<cast(<NavPropertyETKeyNavOne>,<olingo.odata.test1.ETKeyPrimNav>)>")
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator().root()
        .goParameter(1).isTypedLiteral(EntityTypeProvider.nameETKeyPrimNav);

    testFilter.runOnETKeyNavEx("cast(NavPropertyETKeyPrimNavOne,olingo.odata.test1.ETKeyNav)")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
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
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString')")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<d/PropertyString> eq <'SomeString'>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    // TODO planned: lambda does not check if the previous path segment is a collection
    // testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/any(d:d/PropertyString eq 'SomeString')");

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any()")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;>>");

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/CollPropertyString/any(d:d eq 'SomeString')")
        .is("<NavPropertyETTwoKeyNavOne/CollPropertyString/<ANY;<<d> eq <'SomeString'>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnETKeyNav(" NavPropertyETTwoKeyNavOne/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()"
        + "/any(d:d/PropertyComp/PropertyInt16 eq 6)")
        .is("<NavPropertyETTwoKeyNavOne/BFCETTwoKeyNavRTESTwoKeyNav/<ANY;<<d/PropertyComp/PropertyInt16> eq <6>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isFunction("BFCETTwoKeyNavRTESTwoKeyNav")
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or d/any"
        + "(e:e/CollPropertyString eq 'SomeString'))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyInt16> eq <1>> or "
            + "<d/<ANY;<<e/CollPropertyString> eq <'SomeString'>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.OR)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or d/CollPropertyString/any"
        + "(e:e eq 'SomeString'))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyInt16> eq <1>> or "
            + "<d/CollPropertyString/<ANY;<<e> eq <'SomeString'>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.OR)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isType(PropertyProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(PropertyProvider.nameString, false);

    testFilter
        .runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString' and d/CollPropertyString/any"
            + "(e:e eq d/PropertyString))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyString> eq <'SomeString'>> and "
            + "<d/CollPropertyString/<ANY;<<e> eq <d/PropertyString>>>>>>>")
        .root().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().isBinary(BinaryOperatorKind.AND)
        .root().left()
        .isBinary(BinaryOperatorKind.EQ)
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isType(PropertyProvider.nameString, true)
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

  }

  @Test
  public void runIsOf() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETKeyNav("isof(olingo.odata.test1.ETTwoKeyNav)")
        .is("<isof(<olingo.odata.test1.ETTwoKeyNav>)>")
        .root()
        .isMethod(MethodKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EntityTypeProvider.nameETTwoKeyNav);

    testFilter.runOnETKeyNav("isof(olingo.odata.test1.ETBaseTwoKeyNav) eq true")
        .is("<<isof(<olingo.odata.test1.ETBaseTwoKeyNav>)> eq <true>>")
        .root().isBinary(BinaryOperatorKind.EQ)
        .left()
        .isMethod(MethodKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EntityTypeProvider.nameETBaseTwoKeyNav);

    testFilter
        .runOnETKeyNav("isof(olingo.odata.test1.ETBaseTwoKeyNav) eq true and PropertyComp/PropertyInt16 eq 1")
        .is("<<<isof(<olingo.odata.test1.ETBaseTwoKeyNav>)> eq <true>> and <<PropertyComp/PropertyInt16> eq <1>>>")
        .root().isBinary(BinaryOperatorKind.AND)
        .left().isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EntityTypeProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("isof(NavPropertyETKeyNavOne, olingo.odata.test1.ETKeyNav) eq true")
        .is("<<isof(<NavPropertyETKeyNavOne>,<olingo.odata.test1.ETKeyNav>)> eq <true>>")
        .root().isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(EntityTypeProvider.nameETKeyNav);

    testFilter.runOnETKeyNav("isof(PropertyCompTwoPrim,olingo.odata.test1.CTTwoPrim)")
        .is("<isof(<PropertyCompTwoPrim>,<olingo.odata.test1.CTTwoPrim>)>")
        .root().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyCompTwoPrim").goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof(PropertyCompTwoPrim,olingo.odata.test1.CTTwoBase)")
        .is("<isof(<PropertyCompTwoPrim>,<olingo.odata.test1.CTTwoBase>)>")
        .root().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyCompTwoPrim").goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoBase);

    testFilter.runOnETKeyNav("isof(PropertyCompTwoPrim,olingo.odata.test1.CTTwoPrim) eq true")
        .is("<<isof(<PropertyCompTwoPrim>,<olingo.odata.test1.CTTwoPrim>)> eq <true>>")
        .root().left().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isComplex("PropertyCompTwoPrim").goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof($it,olingo.odata.test1.CTTwoPrim)")
        .is("<isof(<$it>,<olingo.odata.test1.CTTwoPrim>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isIt().goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof($it,olingo.odata.test1.CTTwoBase) eq false")
        .is("<<isof(<$it>,<olingo.odata.test1.CTTwoBase>)> eq <false>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isIt().goUpFilterValidator()
        .root().left().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoBase);

    testFilter.runOnETKeyNav("isof(PropertyComp/PropertyInt16,Edm.Int32)")
        .is("<isof(<PropertyComp/PropertyInt16>,<Edm.Int32>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyDateTimeOffset,Edm.DateTimeOffset)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyTimeOfDay,Edm.TimeOfDay)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyTimeOfDay>,<Edm.TimeOfDay>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETTwoKeyNav(" isof(PropertyComp/PropertyComp/PropertyDuration,Edm.Duration)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyDuration>,<Edm.Duration>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyString,Edm.String)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyString>,<Edm.String>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameString);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyString,Edm.Guid)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyString>,<Edm.Guid>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameGuid);
  }

  @Test
  public void testHas() throws ExpressionVisitException, ODataApplicationException, UriParserException {

    testFilter.runOnETTwoKeyNav("PropertyEnumString has olingo.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETTwoKeyNav("PropertyCompEnum/PropertyEnumString has olingo.odata.test1.ENString'String2'")
        .is("<<PropertyCompEnum/PropertyEnumString> has <olingo.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyCompEnum")
        .n().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav(
        "PropertyCompEnum/PropertyEnumString has olingo.odata.test1.ENString'String2' eq true")
        .is("<<<PropertyCompEnum/PropertyEnumString> has <olingo.odata.test1.ENString<String2>>> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().left().goPath()
        .first().isComplex("PropertyCompEnum")
        .n().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().left().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has olingo.odata.test1.ENString'String3'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String3"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has olingo.odata.test1.ENString'String,String3'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String,String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String", "String3"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString has null")
        .is("<<PropertyEnumString> has <null>>")
        .root()
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString).goUpFilterValidator()
        .root().right().isNull();

    testFilter.runOnETTwoKeyNav("endswith(PropertyComp/PropertyComp/PropertyString,'dorf')")
        .is("<endswith(<PropertyComp/PropertyComp/PropertyString>,<'dorf'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith(PropertyComp/PropertyComp/PropertyString,'dorf') eq true")
        .is("<<endswith(<PropertyComp/PropertyComp/PropertyString>,<'dorf'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf')")
        .is("<endswith(<'Walldorf'>,<'dorf'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith('Walldorf','dorf') eq true")
        .is("<<endswith(<'Walldorf'>,<'dorf'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETKeyNav("startswith(PropertyCompAllPrim/PropertyString,'Wall')")
        .is("<startswith(<PropertyCompAllPrim/PropertyString>,<'Wall'>)>")
        .isMethod(MethodKind.STARTSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyCompAllPrim")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith(PropertyCompAllPrim/PropertyString,'Wall') eq true")
        .is("<<startswith(<PropertyCompAllPrim/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.STARTSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyCompAllPrim")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith('Walldorf','Wall')")
        .is("<startswith(<'Walldorf'>,<'Wall'>)>")
        .isMethod(MethodKind.STARTSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith('Walldorf','Wall') eq true")
        .is("<<startswith(<'Walldorf'>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.STARTSWITH, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains(PropertyComp/PropertyComp/PropertyString,'Wall')")
        .is("<contains(<PropertyComp/PropertyComp/PropertyString>,<'Wall'>)>")
        .isMethod(MethodKind.CONTAINS, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains(PropertyComp/PropertyComp/PropertyString,'Wall') eq true")
        .is("<<contains(<PropertyComp/PropertyComp/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.CONTAINS, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall')")
        .is("<contains(<'Walldorf'>,<'Wall'>)>")
        .isMethod(MethodKind.CONTAINS, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains('Walldorf','Wall') eq true")
        .is("<<contains(<'Walldorf'>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.CONTAINS, 2)
        .goParameter(0).isLiteral("'Walldorf'")
        .root().left().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETAllPrim("olingo.odata.test1.UFCRTCTTwoPrimParam(ParameterInt16=null,ParameterString=null)")
        .goPath()
        .isFunction("UFCRTCTTwoPrimParam")
        .isParameter(0, "ParameterInt16", "null")
        .isParameter(1, "ParameterString", "null");

    testFilter.runOnETAllPrim("PropertyBoolean eq true")
        .is("<<PropertyBoolean> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyBoolean", PropertyProvider.nameBoolean, false)
        .goUpFilterValidator()
        .root().right().isTrue();

    testFilter.runOnETAllPrim("PropertyBoolean eq 2")
        .is("<<PropertyBoolean> eq <2>>");

    testFilter.runOnETAllPrim("PropertyDecimal eq 1.25")
        .is("<<PropertyDecimal> eq <1.25>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDecimal", PropertyProvider.nameDecimal, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.25");

    testFilter.runOnETAllPrim("PropertyDouble eq 1.5")
        .is("<<PropertyDouble> eq <1.5>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDouble", PropertyProvider.nameDouble, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.5");

    testFilter.runOnETAllPrim("PropertySingle eq 1.5")
        .is("<<PropertySingle> eq <1.5>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertySingle", PropertyProvider.nameSingle, false)
        .goUpFilterValidator()
        .root().right().isLiteral("1.5");

    testFilter.runOnETAllPrim("PropertySByte eq -128")
        .is("<<PropertySByte> eq <-128>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertySByte", PropertyProvider.nameSByte, false)
        .goUpFilterValidator()
        .root().right().isLiteral("-128");

    testFilter.runOnETAllPrim("PropertyByte eq 255")
        .is("<<PropertyByte> eq <255>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyByte",
            PropertyProvider.nameByte, false).goUpFilterValidator()
        .root().right().isLiteral("255");

    testFilter.runOnETAllPrim("PropertyInt16 eq 32767")
        .is("<<PropertyInt16> eq <32767>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right().isLiteral("32767");

    testFilter.runOnETAllPrim("PropertyInt32 eq 2147483647")
        .is("<<PropertyInt32> eq <2147483647>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt32", PropertyProvider.nameInt32, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2147483647");

    testFilter.runOnETAllPrim("PropertyInt64 eq 9223372036854775807")
        .is("<<PropertyInt64> eq <9223372036854775807>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyInt64", PropertyProvider.nameInt64, false)
        .goUpFilterValidator()
        .root().right().isLiteral("9223372036854775807");

    testFilter.runOnETAllPrim("PropertyDate eq 2013-09-25")
        .is("<<PropertyDate> eq <2013-09-25>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2013-09-25");

    testFilter.runOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<PropertyDateTimeOffset> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset, false)
        .goUpFilterValidator()
        .root().right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S'")
        .is("<<PropertyDuration> eq <duration'P10DT5H34M21.123456789012S'>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator()
        .root().right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33")
        .is("<<PropertyGuid> eq <005056A5-09B1-1ED3-89BD-FB81372CCB33>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyGuid", PropertyProvider.nameGuid, false)
        .goUpFilterValidator()
        .root().right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOnETAllPrim("PropertyString eq 'somestring'")
        .is("<<PropertyString> eq <'somestring'>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right().isLiteral("'somestring'");

    testFilter.runOnETAllPrim("PropertyTimeOfDay eq 12:34:55.12345678901")
        .is("<<PropertyTimeOfDay> eq <12:34:55.12345678901>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .root().right().isLiteral("12:34:55.12345678901");

    testFilter.runOnETTwoKeyNav("PropertyEnumString eq olingo.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> eq <olingo.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETTwoKeyNav("PropertyEnumString eq olingo.odata.test1.ENString'String2'")
        .is("<<PropertyEnumString> eq <olingo.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETTwoKeyNav("PropertyCompEnum/PropertyEnumString eq olingo.odata.test1.ENString'String3'")
        .is("<<PropertyCompEnum/PropertyEnumString> eq <olingo.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyCompEnum")
        .n().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString).goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String3"));

    testFilter.runOnETTwoKeyNav("PropertyCompEnum/PropertyEnumString eq PropertyCompEnum/PropertyEnumString")
        .is("<<PropertyCompEnum/PropertyEnumString> eq <PropertyCompEnum/PropertyEnumString>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyCompEnum")
        .n().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString).goUpFilterValidator()
        .root().right().goPath()
        .first().isComplex("PropertyCompEnum")
        .n().isComplex("PropertyEnumString").isType(EnumTypeProvider.nameENString).goUpFilterValidator();

  }

  @Test
  public void testOrderby() throws UriParserException, UnsupportedEncodingException {

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString'")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' asc")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc"
        + ", PropertyString eq '1'")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'")
        .isSortOrder(1, false)
        .goOrder(1).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .goOrder(1).right().isLiteral("'1'");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate eq "
        + "$root/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyComp/PropertyComp/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .n().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyString")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate "
        + "eq 2013-11-12 desc, PropertyString eq 'SomeString' desc")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false).goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-11-12")
        .isSortOrder(1, true)
        .goOrder(1).left().goPath().first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(1).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp");
    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp desc, PropertyComp/PropertyInt16 eq 1")
        .isSortOrder(0, true)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp").goUpFilterValidator()
        .isSortOrder(1, false)
        .goOrder(1).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false).goUpFilterValidator()
        .goOrder(1).right().isLiteral("1");

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne/PropertyString")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne/PropertyComp")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isComplex("PropertyComp");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyInt16 eq 1")
        .isSortOrder(0, false).goOrder(0).left().goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')"
        + "/PropertyString eq 'SomeString'")
        .isSortOrder(0, false).goOrder(0).left().goPath()
        .first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')"
        + "/PropertyString eq 'SomeString1' desc,PropertyString eq 'SomeString2' asc")
        .isSortOrder(0, true).goOrder(0).left().goPath()
        .first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .n().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .isSortOrder(1, false).goOrder(1).left().goPath()
        .first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETAllPrim("PropertyBoolean eq true")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBoolean", PropertyProvider.nameBoolean, false)
        .goUpFilterValidator()
        .goOrder(0).right().isTrue();

    testFilter.runOrderByOnETAllPrim("PropertyBoolean eq true desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBoolean", PropertyProvider.nameBoolean, false)
        .goUpFilterValidator()
        .goOrder(0).right().isTrue();

    testFilter.runOrderByOnETAllPrim(encode("PropertyDouble eq 3.5E+38"))
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDouble", PropertyProvider.nameDouble, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("3.5E+38");

    testFilter.runOrderByOnETAllPrim(encode("PropertyDouble eq 3.5E+38 desc")).isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDouble", PropertyProvider.nameDouble, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("3.5E+38");

    testFilter.runOrderByOnETAllPrim("PropertySingle eq 1.5")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySingle", PropertyProvider.nameSingle, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("1.5");

    testFilter.runOrderByOnETAllPrim("PropertySingle eq 1.5 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySingle", PropertyProvider.nameSingle, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("1.5");

    testFilter.runOrderByOnETAllPrim("PropertySByte eq -128")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySByte", PropertyProvider.nameSByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("-128");

    testFilter.runOrderByOnETAllPrim("PropertySByte eq -128 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertySByte", PropertyProvider.nameSByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("-128");

    testFilter.runOrderByOnETAllPrim("PropertyByte eq 255")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("255");

    testFilter.runOrderByOnETAllPrim("PropertyByte eq 255 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("255");

    testFilter.runOrderByOnETAllPrim("PropertyInt16 eq 32767")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("32767");

    testFilter.runOrderByOnETAllPrim("PropertyInt16 eq 32767 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("32767");

    testFilter.runOrderByOnETAllPrim("PropertyInt32 eq 2147483647")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt32", PropertyProvider.nameInt32, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2147483647");

    testFilter.runOrderByOnETAllPrim("PropertyInt32 eq 2147483647 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt32", PropertyProvider.nameInt32, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2147483647");

    testFilter.runOrderByOnETAllPrim("PropertyInt64 eq 9223372036854775807")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt64", PropertyProvider.nameInt64, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("9223372036854775807");

    testFilter.runOrderByOnETAllPrim("PropertyInt64 eq 9223372036854775807 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyInt64", PropertyProvider.nameInt64, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("9223372036854775807");

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'0FAB7B'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", PropertyProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'0FAB7B'");

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'0FAB7B' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", PropertyProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'0FAB7B'");

    testFilter.runOrderByOnETAllPrim("PropertyDate eq 2013-09-25")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25");

    testFilter.runOrderByOnETAllPrim("PropertyDate eq 2013-09-25 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25");

    testFilter.runOrderByOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset,
            false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOrderByOnETAllPrim("PropertyDateTimeOffset eq 2013-09-25T12:34:56.123456789012-10:24 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset,
            false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-09-25T12:34:56.123456789012-10:24");

    testFilter.runOrderByOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOrderByOnETAllPrim("PropertyDuration eq duration'P10DT5H34M21.123456789012S' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("duration'P10DT5H34M21.123456789012S'");

    testFilter.runOrderByOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyGuid", PropertyProvider.nameGuid, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOrderByOnETAllPrim("PropertyGuid eq 005056A5-09B1-1ED3-89BD-FB81372CCB33 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyGuid", PropertyProvider.nameGuid, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("005056A5-09B1-1ED3-89BD-FB81372CCB33");

    testFilter.runOrderByOnETAllPrim("PropertyString eq 'somestring'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("'somestring'");

    testFilter.runOrderByOnETAllPrim("PropertyString eq 'somestring' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("'somestring'");

    testFilter.runOrderByOnETAllPrim("PropertyTimeOfDay eq 12:34:55.123456789012")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("12:34:55.123456789012");

    testFilter.runOrderByOnETAllPrim("PropertyTimeOfDay eq 12:34:55.123456789012 desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("12:34:55.123456789012");

    testFilter.runOrderByOnETTwoKeyNav("PropertyEnumString eq olingo.odata.test1.ENString'String1'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isComplex("PropertyEnumString").goUpFilterValidator()
        .goOrder(0).right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOrderByOnETTwoKeyNav("PropertyEnumString eq olingo.odata.test1.ENString'String1' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isComplex("PropertyEnumString").goUpFilterValidator()
        .goOrder(0).right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 1")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16, PropertyInt32 PropertyDuration")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 PropertyInt32, PropertyDuration desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 asc, PropertyInt32 PropertyDuration desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 asc desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("undefined")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyComp/undefined")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  @Ignore("$search currently not implemented")
  public void testSearch() throws Exception {

    testUri.run("ESTwoKeyNav", "$search=abc");
    testUri.run("ESTwoKeyNav", "$search=NOT abc");

    testUri.run("ESTwoKeyNav", "$search=abc AND def");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def");
    testUri.run("ESTwoKeyNav", "$search=abc     def");

    testUri.run("ESTwoKeyNav", "$search=abc AND def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc AND def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc AND def     ghi");

    testUri.run("ESTwoKeyNav", "$search=abc  OR def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc  OR def     ghi");

    testUri.run("ESTwoKeyNav", "$search=abc     def AND ghi");
    testUri.run("ESTwoKeyNav", "$search=abc     def  OR ghi");
    testUri.run("ESTwoKeyNav", "$search=abc     def     ghi");

    // mixed not
    testUri.run("ESTwoKeyNav", "$search=    abc         def AND     ghi");
    testUri.run("ESTwoKeyNav", "$search=NOT abc  NOT    def  OR NOT ghi");
    testUri.run("ESTwoKeyNav", "$search=    abc         def     NOT ghi");

    // parenthesis
    testUri.run("ESTwoKeyNav", "$search= (abc)");
    testUri.run("ESTwoKeyNav", "$search= (abc AND  def)");
    testUri.run("ESTwoKeyNav", "$search= (abc AND  def)   OR  ghi ");
    testUri.run("ESTwoKeyNav", "$search= (abc AND  def)       ghi ");
    testUri.run("ESTwoKeyNav", "$search=  abc AND (def    OR  ghi)");
    testUri.run("ESTwoKeyNav", "$search=  abc AND (def        ghi)");
  }

  @Test
  public void testErrors() {
    testUri.runEx("FICRTString(wrong1='ABC')/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);
    testUri.runEx("FICRTString(wrong1='ABC', wrong2=1)/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);

    // type filter for entity incompatible
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBase")
        .isExSemantic(UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER);

    // type filter for entity double on entry
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);
    // type filter for entity double on collection
    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);
    // type filter for entity double on non key pred
    testUri.runEx("SINav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex incompatible
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim"
        + "/olingo.odata.test1.CTCollAllPrim")
        .isExSemantic(UriParserSemanticException.MessageKeys.INCOMPATIBLE_TYPE_FILTER);

    // type filter for complex double on entry
    testUri.runEx("FICRTCTTwoPrimParam(ParameterInt16=1,ParameterString='2')"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex double on collection
    testUri.runEx("FICRTCollCTTwoPrimParam(ParameterInt16=1,ParameterString='2')"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex double on non key pred
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTESTwoKeyNav")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_PARAMETERS_EXPECTED);

    // $ref
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim/$ref")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_ENTITY_TYPES);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim/$count")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_COLLECTIONS);
  }

  @Test
  public void testAlias() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString=@A)", "@A='2'").goPath()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicateAlias(1, "PropertyString", "@A")
        .isInAliasToValueMap("@A", "'2'")
        .goUpUriValidator()
        .isCustomParameter(0, "@A", "'2'");
  }

  public static String encode(final String decoded) throws UnsupportedEncodingException {
    return Encoder.encode(decoded);
  }
}
