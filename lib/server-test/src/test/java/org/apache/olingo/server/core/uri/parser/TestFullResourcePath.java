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
package org.apache.olingo.server.core.uri.parser;

import java.util.Arrays;
import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.parser.search.SearchParserException;
import org.apache.olingo.server.core.uri.testutil.FilterValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.ActionProvider;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.ContainerProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.apache.olingo.server.tecsvc.provider.TypeDefinitionProvider;
import org.junit.Test;

public class TestFullResourcePath {

  private static final OData oData = OData.newInstance();
  private static final Edm edm = oData.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);
  private final FilterValidator testFilter = new FilterValidator().setEdm(edm);

  @Test
  public void allowedSystemQueryOptionsOnAll() throws Exception {
    testUri.run("$all", "$count=true&$format=json&$search=abc&$skip=5&$top=5&$skiptoken=abc")
        .isKind(UriInfoKind.all)
        .isInlineCountText("true")
        .isFormatText("json")
        .isSearchSerialized("'abc'")
        .isSkipText("5")
        .isTopText("5")
        .isSkipTokenText("abc");
  }

  @Test
  public void allowedSystemQueryOptionsOnCrossjoin() throws Exception {
    testUri.run("$crossjoin(ESAllPrim,ESTwoPrim)", "$count=true&$expand=ESAllPrim"
        + "&$filter=ESAllPrim/PropertyInt16 eq 2&$format=json&$orderby=ESAllPrim/PropertyInt16"
        + "&$search=abc&$skip=5&$top=5&$skiptoken=abc")
        .isKind(UriInfoKind.crossjoin)
        .isInlineCountText("true")
        .goExpand().goPath().isEntitySet("ESAllPrim")
        .goUpExpandValidator().goUpToUriValidator()
        .goFilter().left().goPath().first().isEntitySet("ESAllPrim")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().goUpToUriValidator()
        .isFormatText("json")
        .isSearchSerialized("'abc'")
        .isSkipText("5")
        .isTopText("5")
        .isSkipTokenText("abc");
  }

  @Test
  public void trimQueryOptionsValue() throws Exception {
    // OLINGO-846 trim query option value
    testUri.run("ESAllPrim", "$filter= PropertyInt16 eq 12 ")
        .isKind(UriInfoKind.resource).goPath()
        .first().isEntitySet("ESAllPrim");
    // OLINGO-846 trim query option value
    testUri.run("ESAllPrim", "$filter= PropertyInt16 eq 12 ")
        .isKind(UriInfoKind.resource).goFilter().isBinary(BinaryOperatorKind.EQ).is("<<PropertyInt16> eq <12>>");
  }

  @Test
  public void valueOnNonMediaEntity() throws Exception {
    testUri.runEx("ESAllPrim/$value").isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESAllPrim(1)/NavPropertyETTwoPrimMany/$value").isExSemantic(
        UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("FICRTCollESMedia()/$value")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_TYPED_PARTS);

    testUri.runEx("ESAllPrim(1)/$value").isExSemantic(UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
    testUri.runEx("ESAllPrim(1)/NavPropertyETTwoPrimOne/$value").isExSemantic(
        UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
    testUri.runEx("FICRTETKeyNav()/$value").isExSemantic(UriParserSemanticException.MessageKeys.NOT_A_MEDIA_RESOURCE);
  }

  @Test
  public void enumAndTypeDefAsKey() throws Exception {
    testUri
        .run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',PropertyDefString='abc')")
        .goPath()
        .at(0)
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'abc'");

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString has Namespace1_Alias.ENString'String1'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String1>>>");

    testUri
        .run("ESMixEnumDefCollComp(PropertyEnumString=Namespace1_Alias.ENString'String1',PropertyDefString='abc')")
        .goPath()
        .at(0)
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "Namespace1_Alias.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'abc'");
  }

  @Test
  public void functionBound_varOverloading() throws Exception {
    // on ESTwoKeyNav
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='ABC')").goPath()
        .at(0)
        .isUriPathInfoKind(UriResourceKind.entitySet)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .at(1)
        .isUriPathInfoKind(UriResourceKind.function)
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    // with string parameter
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()").goPath()
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
        + "/olingo.odata.test1.BFC_RTESTwoKeyNav_()"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETTwoBaseTwoKeyNav);
  }

  @Test
  public void runBfuncBnEsRtCprop() throws Exception {
    testUri.run("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESAllPrim")
        .n()
        .isFunction("BFNESAllPrimRTCTAllPrim")
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
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNavParam(ParameterString=wrong)")
        .isExSemantic(MessageKeys.INVALID_KEY_VALUE);
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
        .run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()(PropertyInt16=1,PropertyString='2')"
            + "/NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
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

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyCompNav")
        .isType(ComplexTypeProvider.nameCTNavFiveProp);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyCompNav")
        .isType(ComplexTypeProvider.nameCTNavFiveProp)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav/olingo.odata.test1.BFCESKeyNavRTETKeyNav()/PropertyCompNav/PropertyInt16/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFCESKeyNavRTETKeyNav")
        .n()
        .isComplex("PropertyCompNav")
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

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isParameter(0, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_(ParameterString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isParameter(0, "ParameterString", "'3'")
        .isType(EntityTypeProvider.nameETTwoKeyNav);

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .n()
        .isCount();

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()(PropertyInt16=1,PropertyString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .n()
        .isFunction("BFC_RTESTwoKeyNav_")
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

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.BA_RTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n()
        .isAction("BA_RTETTwoKeyNav");

    testUri.run("ESKeyNav(PropertyInt16=1)/olingo.odata.test1.BA_RTETTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isAction("BA_RTETTwoKeyNav");
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
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
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
  public void crossjoin() throws Exception {
    testUri.run("$crossjoin(ESKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav"));

    testUri.run("$crossjoin(ESKeyNav,ESTwoKeyNav)")
        .isKind(UriInfoKind.crossjoin)
        .isCrossJoinEntityList(Arrays.asList("ESKeyNav", "ESTwoKeyNav"));
  }

  @Test
  public void crossjoinFilter() throws Exception {
    testUri.run("$crossjoin(ESTwoPrim,ESMixPrimCollComp)",
        "$filter=ESTwoPrim/PropertyString eq ESMixPrimCollComp/PropertyComp/PropertyString")
        .goFilter()
        .isBinary(BinaryOperatorKind.EQ)
        .is("<<ESTwoPrim/PropertyString> eq <ESMixPrimCollComp/PropertyComp/PropertyString>>");
  }

  @Test
  public void crossjoinExpand() throws Exception {
    testUri.run("$crossjoin(ESTwoPrim,ESAllPrim)",
        "$expand=ESTwoPrim")
        .goExpand()
        .first().goPath().first().isEntitySet("ESTwoPrim");

    testUri.run("$crossjoin(ESTwoPrim,ESAllPrim)",
        "$expand=ESTwoPrim,ESAllPrim")
        .goExpand()
        .first().goPath().first().isEntitySet("ESTwoPrim")
        .goUpExpandValidator().next().goPath().first().isEntitySet("ESAllPrim");

    // TODO: Once crossjoin is implemented these tests should no longer result in errors
//    testUri.run("$crossjoin(ESTwoPrim,ESAllPrim)",
//        "$expand=ESAllPrim/NavPropertyETTwoPrimOne")
//        .goExpand()
//        .first().goPath().at(0).isEntitySet("ESAllPrim")
//        .at(1).isNavProperty("NavPropertyETTwoPrimOne", new FullQualifiedName("Namespace1_Alias.ETTwoPrim"), false);
  }

  @Test
  public void crossjoinError() throws Exception {
    testUri.runEx("$crossjoin").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("$crossjoin/error").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$crossjoin()").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("$crossjoin(ESKeyNav, ESTwoKeyNav)/invalid")
        .isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$crossjoin(ESKeyNav)/$ref")
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
  public void esNameError() {

    testUri.runEx("ESAllPrim/$count/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$ref/$count").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$ref/invalid").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/$count/invalid").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESAllPrim/PropertyString").isExSemantic(MessageKeys.PROPERTY_AFTER_COLLECTION);
    testUri.runEx("ESAllPrim(1)/whatever").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim('1')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESAllPrim(PropertyInt16)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim(PropertyInt16=)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim(PropertyInt16=1,Invalid='1')").isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("ESBase/olingo.odata.test1.ETBase/PropertyInt16")
        .isExSemantic(MessageKeys.PROPERTY_AFTER_COLLECTION);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETAllKey")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim('1')/olingo.odata.test1.ETAllKey")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim(1)/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETBaseTwoKeyTwoPrim"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyTwoPrim(1)")
        .isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);

    testUri.runEx("ETBaseTwoKeyTwoPrim/olingo.odata.test1.ETAllKey").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
    testUri.runEx("ETBaseTwoKeyTwoPrim()").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
    testUri.runEx("ESAllNullable(1)/CollPropertyString/$value").isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);

    testUri.runEx("ETMixPrimCollComp(1)/ComplexProperty/$value").isExSemantic(MessageKeys.RESOURCE_NOT_FOUND);
  }

  @Test
  public void resourcePathWithApostrophe() throws Exception {
    testUri.runEx("ESAllPrim'").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESAllPrim'InvalidStuff").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETKeyNavEx("PropertyInt16' eq 0").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETKeyNavEx("PropertyInt16 eq' 0").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETKeyNavEx("PropertyInt16 eq 0'")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testFilter.runOnETKeyNavEx("PropertyInt16 eq 'dsd''")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void runFunctionsWithKeyPredicates() throws Exception {
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1");

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16=0)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "0");

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(0)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "0");

    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16 eq 0)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    // PropertyInt32 does not exist
    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=0)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollETMixPrimCollCompTwoParam(ParameterString='1',ParameterInt16=1)"
        + "(PropertyInt16=0,PropertyInt16=1)")
        .isExValidation(UriValidationException.MessageKeys.DOUBLE_KEY_PROPERTY);

    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollCTTwoPrimTwoParam")
        .isFunction("UFCRTCollCTTwoPrimTwoParam")
        .isParameter(0, "ParameterString", "'1'")
        .isParameter(1, "ParameterInt16", "1");

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt16=1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterString='1',ParameterInt16=1)(PropertyInt32=1,PropertyInt16=2)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyString='1')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'1'");

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16 eq 1)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1)")
        .isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyInt32=1,PropertyString='1')")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)()")
        .isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,PropertyInt32=1)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=1)(PropertyInt16=1,Unkown=1)")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);

    testUri.run("FICRTCollString()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollString")
        .isFunction("UFCRTCollString");

    testUri.run("FICRTString()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTString")
        .isFunction("UFCRTString");

    testUri.runEx("FICRTCollString()(0)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("FICRTString()(0)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);
  }

  @Test
  public void runNonComposableFunctions() throws Exception {


    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skip=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$top=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')",
        "$filter=PropertyInt16 eq 1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skip=1");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$count=true");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$skiptoken=5");
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')", "$search=test");

    testUri.run("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .at(1)
        .isFunction("BFNESAllPrimRTCTAllPrim");

    testUri.runEx("ESAllPrim/olingo.odata.test1.BFNESAllPrimRTCTAllPrim()"
        + "/PropertyString")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    
    testUri.runEx("FINRTByteNineParam(ParameterEnum=Namespace1_Alias.ENString'String1',"
        + "CollParameterETTwoPrim=@collComp,ParameterComp=@comp,ParameterDef='key1',"
        + "ParameterETTwoPrim=@comp,CollParameterDef=@collDef,CollParameterByte=@collByte,"
        + "CollParameterComp=@collComp,CollParameterEnum=@collEnum)/$value?@comp={\"PropertyInt16\":1}"
        + "&@collByte=[1]&@collEnum=[\"String1,String1\"]&@collDef=[\"Test\"]&@collComp=[{\"PropertyInt16\":11}]")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
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

    testUri.runEx("ESTwoPrim('wrong')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
    testUri.runEx("ESTwoPrim(PropertyInt16='wrong')").isExSemantic(MessageKeys.INVALID_KEY_VALUE);
  }

  @Test
  public void runEsNameParaKeys() throws Exception {
    testUri.run("ESAllKey(PropertyString='O''Neil',PropertyBoolean=true,PropertyByte=255,"
        + "PropertySByte=-128,PropertyInt16=-32768,PropertyInt32=-2147483648,"
        + "PropertyInt64=-9223372036854775808,PropertyDecimal=1,PropertyDate=2013-09-25,"
        + "PropertyDateTimeOffset=2002-10-10T12:00:00-05:00,"
        + "PropertyDuration=duration'P50903316DT2H25M4S',"
        + "PropertyGuid=12345678-1234-1234-1234-123456789012,"
        + "PropertyTimeOfDay=12:34:55)")
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

    // Keys cannot be specified twice.
    testUri.runEx("ESTwoPrim(1)/olingo.odata.test1.ETBase(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);
    testUri.runEx("ESTwoPrim/olingo.odata.test1.ETBase(1)/olingo.odata.test1.ETTwoBase(1)")
        .isExSemantic(MessageKeys.KEY_NOT_ALLOWED);

    testUri.runEx("ESBase/olingo.odata.test1.ETTwoPrim(1)").isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
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

    testUri.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/PropertyEnumString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false);

    testUri.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/PropertyDefString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("PropertyDefString", TypeDefinitionProvider.nameTDString, false);
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

    testUri.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/CollPropertyEnumString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("CollPropertyEnumString", EnumTypeProvider.nameENString, true);

    testUri.run("ESMixEnumDefCollComp(PropertyEnumString=olingo.odata.test1.ENString'String1',"
        + "PropertyDefString='key1')/CollPropertyDefString")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESMixEnumDefCollComp")
        .isKeyPredicate(0, "PropertyEnumString", "olingo.odata.test1.ENString'String1'")
        .isKeyPredicate(1, "PropertyDefString", "'key1'")
        .n()
        .isPrimitiveProperty("CollPropertyDefString", TypeDefinitionProvider.nameTDString, true);
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

    testUri.run("ESKeyNav(1)/NavPropertyETKeyNavMany(2)/PropertyCompNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .n()
        .isComplex("PropertyCompNav");

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

    testUri.run("ESKeyNav(1)/PropertyCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isEntitySet("ESKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .n()
        .isComplex("PropertyCompNav")
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
    // checks for using referential constraints to fill missing keys
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

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n()
        .isFunction("BFCETBaseTwoKeyNavRTETTwoKeyNav");

    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=null)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollCTTwoPrimTwoParam")
        .isFunction("UFCRTCollCTTwoPrimTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", null);
  }

  @Test
  public void runFunctionImpEntity() throws Exception {

    testUri.run("FICRTETKeyNav()")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETKeyNav")
        .isFunction("UFCRTETKeyNav")
        .isType(EntityTypeProvider.nameETKeyNav);

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1");

    testUri.run("FICRTESMedia(ParameterInt16=1)/$value")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTESMedia")
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
    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/$ref")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
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

    testUri.run("FICRTETTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTETTwoKeyNavParam")
        .isFunction("UFCRTETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");
  }

  @Test
  public void runFunctionImpEs() throws Exception {
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp);

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'2'")
        .isType(EntityTypeProvider.nameETMixPrimCollComp)
        .n()
        .isCount();
    
    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'1'");

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='1')(0)")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
        .isFunction("UFCRTCollETMixPrimCollCompTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'1'");
  }

  @Test
  public void runFunctionImpError() {
    testUri.runEx("FICRTCollCTTwoPrimTwoParam")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("FICRTCollCTTwoPrimTwoParam()").isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);
    testUri.runEx("FICRTCollCTTwoPrimTwoParam(invalidParam=2)").isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);
  }

  @Test
  public void runFunctionImpEsCast() throws Exception {

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)/olingo.odata.test1.ETBaseTwoKeyNav/$count")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n()
        .isCount();

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
        .isParameter(0, "ParameterInt16", "1")
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'");

    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isFunctionImport("FICRTCollESTwoKeyNavParam")
        .isFunction("UFCRTCollETTwoKeyNavParam")
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
  public void expandStar() throws Exception {
    testUri.run("ESKeyNav(1)", "$expand=*")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .isSegmentStar();

    testUri.run("ESKeyNav(1)", "$expand=*/$ref")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .isSegmentStar()
        .isSegmentRef();

    testUri.run("ESKeyNav(1)", "$expand=*/$ref,NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .isSegmentStar().isSegmentRef()
        .next()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)", "$expand=*($levels=3)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .isSegmentStar()
        .isLevelText("3");

    testUri.run("ESKeyNav(1)", "$expand=*($levels=max)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .isSegmentStar()
        .isLevelText("max");
  }

  @Test
  public void expandNavigationRef() throws Exception {
    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne/$ref")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .n().isRef();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator().goFilter().is("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($top=2)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($count=true)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1;$top=3)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("3");
  }

  @Test
  public void expandNavigationCount() throws Exception {
    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$count")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isCount();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne/$count")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .n().isCount();

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .n().isCount()
        .goUpExpandValidator()
        .goFilter().is("<<PropertyInt16> gt <1>>");
  }

  @Test
  public void expandNavigationOptions() throws Exception {
    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator().goFilter().is("<<PropertyInt16> eq <1>>");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($orderby=PropertyInt16)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($top=2)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($count=true)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isInlineCountText("true");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne($levels=5)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("5");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($select=PropertyString)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavOne($levels=max)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelText("max");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1;$top=2)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($skip=1%3b$top=2)")
        .isKind(UriInfoKind.resource).goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkipText("1")
        .isTopText("2");

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany($search=Country AND Western)")
        .isKind(UriInfoKind.resource).goExpand()
        .first().goPath().first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSearchSerialized("{'Country' AND 'Western'}");

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')", "$expand=NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goUpUriValidator().goExpand()
        .first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .isType(EntityTypeProvider.nameETKeyNav, true);
  }

  @Test
  public void expandTypeCasts() throws Exception {
    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='Hugo')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'Hugo'")
        .goUpUriValidator().goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isType(EntityTypeProvider.nameETKeyNav)
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goUpUriValidator().goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$expand=olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isKind(UriInfoKind.resource).goPath().first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goUpUriValidator().goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav/*")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isSegmentStar()
        .goPath().last().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false);

    testUri.run("ESTwoKeyNav", "$expand=olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav"
        + "/olingo.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETTwoKeyNavMany/Namespace1_Alias.ETBaseTwoKeyNav"
        + "($expand=NavPropertyETBaseTwoKeyNavOne)")
        .isKind(UriInfoKind.resource)
        .goExpand().goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isType(EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goUpExpandValidator()
        // go to the expand options of the current expand
        .goExpand()
        .goPath().first()
        .isNavProperty("NavPropertyETBaseTwoKeyNavOne", EntityTypeProvider.nameETBaseTwoKeyNav, false);

    testUri.run("ESKeyNav(1)", "$expand=NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)")
        .isKind(UriInfoKind.resource)
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
        .goUpUriValidator().goExpand().first()
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath().first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isType(EntityTypeProvider.nameETTwoKeyNav)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany("
        + "$expand=NavPropertyETKeyNavOne))")
        .isKind(UriInfoKind.resource)
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

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($select=PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESKeyNav", "$expand=NavPropertyETKeyNavOne($select=PropertyCompNav/PropertyInt16)")
        .isKind(UriInfoKind.resource)
        .goExpand().first()
        .goPath().first()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .isType(EntityTypeProvider.nameETKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0)
        .first().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.runEx("ESKeyNav", "$expand=undefined")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESTwoKeyNav", "$expand=PropertyCompNav/undefined")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESTwoKeyNav", "$expand=PropertyCompNav/*+")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void duplicatedSystemQueryOptionsInExpand() throws Exception {
    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($select=PropertyInt16;$select=PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($filter=true;$filter=true)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($orderby=PropertyInt16;$orderby=PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($levels=2;$levels=3)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($expand=*;$expand=*)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($count=true;$count=true)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($top=1;$top=1)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($skip=2;$skip=2)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    testUri.runEx("ESKeyNav", "$expand=NavPropertyETKeyNavOne($search=Test;$search=Test)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void select() throws Exception {
    testUri.run("ESTwoKeyNav", "$select=*")
        .isSelectItemStar(0);

    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.*")
        .isSelectItemAllOp(0, new FullQualifiedName("olingo.odata.test1", "*"));
    testUri.run("ESTwoKeyNav", "$select=Namespace1_Alias.*")
        .isSelectItemAllOp(0, new FullQualifiedName("Namespace1_Alias", "*"));

    testUri.run("ESTwoKeyNav", "$select=PropertyString")
        .goSelectItemPath(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp")
        .goSelectItemPath(0).isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testUri.run("ESAllPrim", "$select=PropertyTimeOfDay,PropertyDate,NavPropertyETTwoPrimOne")
        .isKind(UriInfoKind.resource)
        .goSelectItemPath(0).first().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpUriValidator()
        .goSelectItemPath(1).first().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpUriValidator()
        .goSelectItemPath(2).first().isNavProperty("NavPropertyETTwoPrimOne", EntityTypeProvider.nameETTwoPrim, false);

    testUri.run("ESMixEnumDefCollComp",
        "$select=PropertyEnumString,PropertyDefString,CollPropertyEnumString,CollPropertyDefString")
        .isKind(UriInfoKind.resource)
        .goSelectItemPath(0).isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpUriValidator()
        .goSelectItemPath(1).isPrimitiveProperty("PropertyDefString", TypeDefinitionProvider.nameTDString, false)
        .goUpUriValidator()
        .goSelectItemPath(2).isPrimitiveProperty("CollPropertyEnumString", EnumTypeProvider.nameENString, true)
        .goUpUriValidator()
        .goSelectItemPath(3).isPrimitiveProperty("CollPropertyDefString", TypeDefinitionProvider.nameTDString, true);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp/PropertyInt16")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESTwoKeyNav", "$select=PropertyComp/PropertyComp")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.ETBaseTwoKeyNav")
        .isSelectStartType(0, EntityTypeProvider.nameETBaseTwoKeyNav);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')",
        "$select=olingo.odata.test1.ETBaseTwoKeyNav/PropertyInt16")
        .isKind(UriInfoKind.resource).goPath()
        .first()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goUpUriValidator()
        .isSelectStartType(0, EntityTypeProvider.nameETBaseTwoKeyNav)
        .goSelectItemPath(0)
        .first()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString='1')/PropertyCompNav",
        "$select=olingo.odata.test1.CTTwoBasePrimCompNav")
        .isSelectStartType(0, ComplexTypeProvider.nameCTTwoBasePrimCompNav);

    testUri.run("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoBasePrimCompNav")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav);

    testUri.run("ESTwoKeyNav", "$select=PropertyCompNav/Namespace1_Alias.CTTwoBasePrimCompNav/PropertyInt16")
        .goSelectItemPath(0)
        .first()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav)
        .n()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testUri.run("ESAllPrim", "$select=olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .goSelectItemPath(0)
        .first()
        .isAction(ActionProvider.nameBAESAllPrimRTETAllPrim.getName());
    testUri.run("ESTwoKeyNav", "$select=Namespace1_Alias.BFCESTwoKeyNavRTString")
        .goSelectItemPath(0)
        .first()
        .isFunction(FunctionProvider.nameBFCESTwoKeyNavRTString.getName());
    testUri.run("ESTwoKeyNav", "$select=olingo.odata.test1.BFCESTwoKeyNavRTStringParam(ParameterComp)")
        .goSelectItemPath(0)
        .first()
        .isFunction(FunctionProvider.nameBFCESTwoKeyNavRTStringParam.getName());

    testUri.runEx("ESMixPrimCollComp", "$select=wrong")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyComp/wrong")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyComp///PropertyInt16")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESMixPrimCollComp", "$select=/PropertyInt16")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESMixPrimCollComp", "$select=PropertyInt16+")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.1")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=unknown_namespace.*").isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.ETKeyNav")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoPrim")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTwrong")
        .isExSemantic(MessageKeys.UNKNOWN_TYPE);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/.")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=PropertyCompNav/olingo.odata.test1.CTTwoBasePrimCompNav/.")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("AIRT", "$select=wrong")
        .isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("AIRT", "$select=olingo.odata.test1.BAESAllPrimRT")
        .isExSemantic(MessageKeys.ONLY_FOR_TYPED_PARTS);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.BFwrong")
        .isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESTwoKeyNav", "$select=olingo.odata.test1.BFCESTwoKeyNavRTStringParam()")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testUri.runEx("ESTwoKeyNav", "$select=Namespace1_Alias.BFCESTwoKeyNavRTStringParam(ParameterComp,...)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void top() throws Exception {
    testUri.run("ESKeyNav", "$top=1")
        .isKind(UriInfoKind.resource)
        .goPath().isEntitySet("ESKeyNav")
        .goUpUriValidator().isTopText("1");

    testUri.run("ESKeyNav", "$top=0")
        .isKind(UriInfoKind.resource)
        .goPath().isEntitySet("ESKeyNav")
        .goUpUriValidator().isTopText("0");

    testUri.runEx("ESKeyNav", "$top=undefined")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESKeyNav", "$top=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESKeyNav", "$top=-3")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void format() throws Exception {
    testUri.run("ESKeyNav(1)", "$format=atom")
        .isKind(UriInfoKind.resource)
        .isFormatText("atom");
    testUri.run("ESKeyNav(1)", "$format=json")
        .isKind(UriInfoKind.resource)
        .isFormatText("json");
    testUri.run("ESKeyNav(1)", "$format=xml")
        .isKind(UriInfoKind.resource)
        .isFormatText("xml");
    testUri.run("ESKeyNav(1)", "$format=IANA_content_type/must_contain_a_slash")
        .isKind(UriInfoKind.resource)
        .isFormatText("IANA_content_type/must_contain_a_slash");
    testUri.run("ESKeyNav(1)", "$format=Test_all_valid_signsSpecified_for_format_signs%26-._~$@%27/Aa123%26-._~$@%27")
        .isKind(UriInfoKind.resource)
        .isFormatText("Test_all_valid_signsSpecified_for_format_signs&-._~$@'/Aa123&-._~$@'");
    testUri.run("ESKeyNav(1)", "$format=" + ContentType.APPLICATION_ATOM_XML_ENTRY_UTF8)
        .isKind(UriInfoKind.resource)
        .isFormatText(ContentType.APPLICATION_ATOM_XML_ENTRY_UTF8.toContentTypeString());
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
  public void count() throws Exception {
    testUri.run("ESAllPrim", "$count=true")
        .isKind(UriInfoKind.resource)
        .isInlineCountText("true");
    testUri.run("ESAllPrim", "$count=false")
        .isKind(UriInfoKind.resource)
        .isInlineCountText("false");
    testUri.runEx("ESAllPrim", "$count=undefined")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESAllPrim", "$count=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void skip() throws Exception {
    testUri.run("ESAllPrim", "$skip=3")
        .isKind(UriInfoKind.resource)
        .isSkipText("3");
    testUri.run("ESAllPrim", "$skip=0")
        .isKind(UriInfoKind.resource)
        .isSkipText("0");

    testUri.runEx("ESAllPrim", "$skip=F")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESAllPrim", "$skip=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testUri.runEx("ESAllPrim", "$skip=-3")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void skiptoken() throws Exception {
    testUri.run("ESAllPrim", "$skiptoken=foo")
        .isKind(UriInfoKind.resource)
        .isSkipTokenText("foo");

    testUri.runEx("ESAllPrim", "$skiptoken=")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
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

    testUri.runEx("//").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$metadata/").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("//$metadata").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("ESKeyNav//$count").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("$metadata/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
    testUri.runEx("$batch/$ref").isExSyntax(UriParserSyntaxException.MessageKeys.MUST_BE_LAST_SEGMENT);
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
    testUri.runEx("ESKeyNav()").isExSemantic(MessageKeys.WRONG_NUMBER_OF_KEY_PROPERTIES);

    testUri.run("SINav")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isSingleton("SINav");

    testUri.run("FICRTCollETMixPrimCollCompTwoParam(ParameterInt16=1,ParameterString='2')")
        .isKind(UriInfoKind.resource)
        .goPath()
        .isFunctionImport("FICRTCollETMixPrimCollCompTwoParam")
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

    testUri.run(ContainerProvider.AIRT_STRING)
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport(ContainerProvider.AIRT_STRING);

    testUri.run(ContainerProvider.AIRT_COLL_ES_ALL_PRIM_PARAM)
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport(ContainerProvider.AIRT_COLL_ES_ALL_PRIM_PARAM);

    testUri.run(ContainerProvider.AIRT)
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isActionImport(ContainerProvider.AIRT);

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

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFC_RTESTwoKeyNav_");

    testUri.run("ESAllPrim/olingo.odata.test1.BAESAllPrimRTETAllPrim")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESAllPrim")
        .n().isAction("BAESAllPrimRTETAllPrim");

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFC_RTESTwoKeyNav_");

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

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_()")
        .isKind(UriInfoKind.resource)
        .goPath().first()
        .isEntitySet("ESTwoKeyNav")
        .n().isFunction("BFC_RTESTwoKeyNav_");

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
  }

  @Test
  public void filter() throws Exception {
    testFilter.runOnETAllPrim("PropertyBoolean")
        .is("<PropertyBoolean>")
        .isType(PropertyProvider.nameBoolean);

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyInt16 gt 0")
        .is("<<PropertyComp/PropertyInt16> gt <0>>")
        .left().isType(PropertyProvider.nameInt16);

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate ne null")
        .is("<<PropertyComp/PropertyComp/PropertyDate> ne <null>>")
        .left().isType(PropertyProvider.nameDate);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne eq null")
        .is("<<NavPropertyETTwoKeyNavOne> eq <null>>")
        .left().isType(EntityTypeProvider.nameETTwoKeyNav);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyString eq ''")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyString> eq <''>>")
        .left().isType(PropertyProvider.nameString);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp eq null")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyComp> eq <null>>")
        .left().isType(ComplexTypeProvider.nameCTPrimComp);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp eq null")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyComp/PropertyComp> eq <null>>")
        .left().isType(ComplexTypeProvider.nameCTAllPrim);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16 eq 1")
        .is("<<NavPropertyETTwoKeyNavOne/PropertyComp/PropertyInt16> eq <1>>")
        .left().isType(PropertyProvider.nameInt16)
        .root().right().isLiteral("1");

    testFilter.runOnETTwoKeyNav("NavPropertyETKeyNavMany(1)/NavPropertyETTwoKeyNavMany(PropertyString='2')/"
        + "PropertyString eq 'SomeString'")
        .is("<<NavPropertyETKeyNavMany/NavPropertyETTwoKeyNavMany/PropertyString> eq <'SomeString'>>")
        .left()
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
        .left()
        .isType(PropertyProvider.nameDate)
        .isMember().isMemberStartType(EntityTypeProvider.nameETBaseTwoKeyNav).goPath()
        .first().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("2013-11-12");

    testFilter.runOnCTTwoPrim("olingo.odata.test1.CTBase/AdditionalPropString eq 'SomeString'")
        .is("<<AdditionalPropString> eq <'SomeString'>>")
        .left()
        .isType(PropertyProvider.nameString)
        .isMember().isMemberStartType(ComplexTypeProvider.nameCTBase).goPath()
        .first().isPrimitiveProperty("AdditionalPropString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().right()
        .isLiteral("'SomeString'");

    testFilter
        .runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate eq 2013-11-12")
        .is("<<NavPropertyETTwoKeyNavOne/olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate> eq <2013-11-12>>")
        .left()
        .isType(PropertyProvider.nameDate)
        .root().right()
        .isLiteral("2013-11-12");

    testFilter
        .runOnETTwoKeyNav("PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase/AdditionalPropString eq 'SomeString'")
        .is("<<PropertyCompTwoPrim/olingo.odata.test1.CTTwoBase/AdditionalPropString> eq <'SomeString'>>")
        .left()
        .isType(PropertyProvider.nameString)
        .root().right()
        .isLiteral("'SomeString'");

    testFilter.runOnETTwoKeyNavEx("invalid").isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/invalid").isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("concat('a','b')/invalid").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/concat('a','b')")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyInt16 eq '1'")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyComp/PropertyDate eq 1")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyComp/PropertyString eq 1")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("PropertyComp/PropertyInt64 eq 1")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETTwoKeyNavEx("NavPropertyETKeyNavMany/PropertyInt16 gt 42")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETTwoKeyNavEx("NavPropertyETKeyNavMany/NavPropertyETTwoKeyNavOne eq null")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
  }

  @Test
  public void filterBinaryOperators() throws Exception {
    testFilter.runOnETAllPrim("PropertySByte eq PropertySByte")
        .is("<<PropertySByte> eq <PropertySByte>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isType(PropertyProvider.nameSByte)
        .root().right().isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte ne PropertySByte")
        .is("<<PropertySByte> ne <PropertySByte>>")
        .isBinary(BinaryOperatorKind.NE)
        .left().isType(PropertyProvider.nameSByte)
        .root().right().isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertySByte add PropertySByte gt 0")
        .is("<<<PropertySByte> add <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte add PropertyByte gt 0")
        .is("<<<PropertyByte> add <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 add PropertyInt16 gt 0")
        .is("<<<PropertyInt16> add <PropertyInt16>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt16)
        .root().left().right().isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 add PropertyInt32 gt 0")
        .is("<<<PropertyInt32> add <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt32)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 add PropertyInt64 gt 0")
        .is("<<<PropertyInt64> add <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle add PropertySingle gt 0")
        .is("<<<PropertySingle> add <PropertySingle>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSingle)
        .root().left().right().isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble add PropertyDouble gt 0")
        .is("<<<PropertyDouble> add <PropertyDouble>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDouble)
        .root().left().right().isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal add PropertyDecimal gt 0")
        .is("<<<PropertyDecimal> add <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyDecimal gt 0")
        .is("<<<PropertySByte> add <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt32 gt 0")
        .is("<<<PropertySByte> add <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertySByte add PropertyInt64 gt 0")
        .is("<<<PropertySByte> add <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset add PropertyDuration ne null")
        .is("<<<PropertyDateTimeOffset> add <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDateTimeOffset)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration add PropertyDuration ne null")
        .is("<<<PropertyDuration> add <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDuration)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate add PropertyDuration ne null")
        .is("<<<PropertyDate> add <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDate)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertySByte sub PropertySByte gt 0")
        .is("<<<PropertySByte> sub <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte sub PropertyByte gt 0")
        .is("<<<PropertyByte> sub <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 sub PropertyInt16 gt 0")
        .is("<<<PropertyInt16> sub <PropertyInt16>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt16)
        .root().left().right().isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 sub PropertyInt32 gt 0")
        .is("<<<PropertyInt32> sub <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt32)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 sub PropertyInt64 gt 0")
        .is("<<<PropertyInt64> sub <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle sub PropertySingle gt 0")
        .is("<<<PropertySingle> sub <PropertySingle>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSingle)
        .root().left().right().isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble sub PropertyDouble gt 0")
        .is("<<<PropertyDouble> sub <PropertyDouble>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDouble)
        .root().left().right().isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyDecimal gt 0")
        .is("<<<PropertyDecimal> sub <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt32 gt 0")
        .is("<<<PropertyDecimal> sub <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyInt64 gt 0")
        .is("<<<PropertyDecimal> sub <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertyDecimal sub PropertyByte gt 0")
        .is("<<<PropertyDecimal> sub <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDuration ne null")
        .is("<<<PropertyDateTimeOffset> sub <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDateTimeOffset)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDuration sub PropertyDuration ne null")
        .is("<<<PropertyDuration> sub <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDuration)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDateTimeOffset sub PropertyDateTimeOffset ne null")
        .is("<<<PropertyDateTimeOffset> sub <PropertyDateTimeOffset>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDateTimeOffset)
        .root().left().right().isType(PropertyProvider.nameDateTimeOffset);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDuration ne null")
        .is("<<<PropertyDate> sub <PropertyDuration>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDate)
        .root().left().right().isType(PropertyProvider.nameDuration);
    testFilter.runOnETAllPrim("PropertyDate sub PropertyDate ne null")
        .is("<<<PropertyDate> sub <PropertyDate>> ne <null>>")
        .left().left().isType(PropertyProvider.nameDate)
        .root().left().right().isType(PropertyProvider.nameDate);
    testFilter.runOnETAllPrim("PropertySByte mul PropertySByte gt 0")
        .is("<<<PropertySByte> mul <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mul PropertyByte gt 0")
        .is("<<<PropertyByte> mul <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mul PropertyInt16 gt 0")
        .is("<<<PropertyInt16> mul <PropertyInt16>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt16)
        .root().left().right().isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mul PropertyInt32 gt 0")
        .is("<<<PropertyInt32> mul <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt32)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt64 gt 0")
        .is("<<<PropertyInt64> mul <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mul PropertySingle gt 0")
        .is("<<<PropertySingle> mul <PropertySingle>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSingle)
        .root().left().right().isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mul PropertyDouble gt 0")
        .is("<<<PropertyDouble> mul <PropertyDouble>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDouble)
        .root().left().right().isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mul PropertyDecimal gt 0")
        .is("<<<PropertyDecimal> mul <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyInt32 gt 0")
        .is("<<<PropertyInt64> mul <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertySByte gt 0")
        .is("<<<PropertyInt64> mul <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyInt64 mul PropertyDecimal gt 0")
        .is("<<<PropertyInt64> mul <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertySByte div PropertySByte gt 0")
        .is("<<<PropertySByte> div <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte div PropertyByte gt 0")
        .is("<<<PropertyByte> div <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 div PropertyInt16 gt 0")
        .is("<<<PropertyInt16> div <PropertyInt16>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt16)
        .root().left().right().isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 div PropertyInt32 gt 0")
        .is("<<<PropertyInt32> div <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt32)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 div PropertyInt64 gt 0")
        .is("<<<PropertyInt64> div <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle div PropertySingle gt 0")
        .is("<<<PropertySingle> div <PropertySingle>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSingle)
        .root().left().right().isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble div PropertyDouble gt 0")
        .is("<<<PropertyDouble> div <PropertyDouble>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDouble)
        .root().left().right().isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal div PropertyDecimal gt 0")
        .is("<<<PropertyDecimal> div <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertyInt32 gt 0")
        .is("<<<PropertyByte> div <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyByte div PropertyDecimal gt 0")
        .is("<<<PropertyByte> div <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyByte div PropertySByte gt 0")
        .is("<<<PropertyByte> div <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameSByte);

    testFilter.runOnETAllPrim("PropertyByte div 0 gt 0")
        .is("<<<PropertyByte> div <0>> gt <0>>");

    testFilter.runOnETAllPrim("0 div 0 gt 0")
        .is("<<<0> div <0>> gt <0>>");

    testFilter.runOnETAllPrim("PropertySByte mod PropertySByte gt 0")
        .is("<<<PropertySByte> mod <PropertySByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSByte)
        .root().left().right().isType(PropertyProvider.nameSByte);
    testFilter.runOnETAllPrim("PropertyByte mod PropertyByte gt 0")
        .is("<<<PropertyByte> mod <PropertyByte>> gt <0>>")
        .left().left().isType(PropertyProvider.nameByte)
        .root().left().right().isType(PropertyProvider.nameByte);
    testFilter.runOnETAllPrim("PropertyInt16 mod PropertyInt16 gt 0")
        .is("<<<PropertyInt16> mod <PropertyInt16>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt16)
        .root().left().right().isType(PropertyProvider.nameInt16);
    testFilter.runOnETAllPrim("PropertyInt32 mod PropertyInt32 gt 0")
        .is("<<<PropertyInt32> mod <PropertyInt32>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt32)
        .root().left().right().isType(PropertyProvider.nameInt32);
    testFilter.runOnETAllPrim("PropertyInt64 mod PropertyInt64 gt 0")
        .is("<<<PropertyInt64> mod <PropertyInt64>> gt <0>>")
        .left().left().isType(PropertyProvider.nameInt64)
        .root().left().right().isType(PropertyProvider.nameInt64);
    testFilter.runOnETAllPrim("PropertySingle mod PropertySingle gt 0")
        .is("<<<PropertySingle> mod <PropertySingle>> gt <0>>")
        .left().left().isType(PropertyProvider.nameSingle)
        .root().left().right().isType(PropertyProvider.nameSingle);
    testFilter.runOnETAllPrim("PropertyDouble mod PropertyDouble gt 0")
        .is("<<<PropertyDouble> mod <PropertyDouble>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDouble)
        .root().left().right().isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal mod PropertyDecimal gt 0")
        .is("<<<PropertyDecimal> mod <PropertyDecimal>> gt <0>>")
        .left().left().isType(PropertyProvider.nameDecimal)
        .root().left().right().isType(PropertyProvider.nameDecimal);

    testFilter.runOnETAllPrim("PropertyDecimal ge PropertyDecimal")
        .is("<<PropertyDecimal> ge <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.GE)
        .left().isType(PropertyProvider.nameDecimal)
        .root().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal lt PropertyDecimal")
        .is("<<PropertyDecimal> lt <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LT)
        .left().isType(PropertyProvider.nameDecimal)
        .root().right().isType(PropertyProvider.nameDecimal);
    testFilter.runOnETAllPrim("PropertyDecimal le PropertyDecimal")
        .is("<<PropertyDecimal> le <PropertyDecimal>>")
        .isBinary(BinaryOperatorKind.LE)
        .left().isType(PropertyProvider.nameDecimal)
        .root().right().isType(PropertyProvider.nameDecimal);

    // Numeric promotion: Double is considered the widest type.
    testFilter.runOnETAllPrim("PropertyDecimal ne NaN")
        .right().isLiteral("NaN").isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal gt -INF")
        .right().isLiteral("-INF").isType(PropertyProvider.nameDouble);
    testFilter.runOnETAllPrim("PropertyDecimal lt INF")
        .right().isLiteral("INF").isType(PropertyProvider.nameDouble);
  }

  @Test
  public void filterProperties() throws Exception {
    testFilter.runOnETAllPrim("PropertyBoolean eq true")
        .is("<<PropertyBoolean> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyBoolean", PropertyProvider.nameBoolean, false)
        .goUpFilterValidator()
        .root().right().isLiteral("true");

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

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString eq olingo.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> eq <olingo.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString eq olingo.odata.test1.ENString'String2'")
        .is("<<PropertyEnumString> eq <olingo.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETMixEnumDefCollComp(
        "PropertyCompMixedEnumDef/PropertyEnumString eq olingo.odata.test1.ENString'String3'")
        .is("<<PropertyCompMixedEnumDef/PropertyEnumString> eq <olingo.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyCompMixedEnumDef")
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String3"));

    testFilter
        .runOnETMixEnumDefCollComp(
            "PropertyCompMixedEnumDef/PropertyEnumString eq " +
                "PropertyCompMixedEnumDef/PropertyEnumString")
        .is("<<PropertyCompMixedEnumDef/PropertyEnumString> eq " +
            "<PropertyCompMixedEnumDef/PropertyEnumString>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left().goPath()
        .first().isComplex("PropertyCompMixedEnumDef")
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().goPath()
        .first().isComplex("PropertyCompMixedEnumDef")
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false);

    testFilter.runOnETAllPrim("PropertyByte mod 0 gt 0")
        .is("<<<PropertyByte> mod <0>> gt <0>>");
  }

  @Test
  public void filterFunctions() throws Exception {
    testFilter.runOnETAllPrim(
        "olingo.odata.test1.UFCRTETTwoKeyNavParamCTTwoPrim(ParameterCTTwoPrim=@ParamAlias) eq null"
            + "&@ParamAlias={}")
        .is("<<UFCRTETTwoKeyNavParamCTTwoPrim> eq <null>>")
        .left().goPath()
        .first()
        .isFunction("UFCRTETTwoKeyNavParamCTTwoPrim")
        .isParameterAlias(0, "ParameterCTTwoPrim", "@ParamAlias");

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTETTwoKeyNavParam"
        + "(ParameterString=PropertyComp/PropertyComp/PropertyString)/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTETTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isFunction("BFCCTPrimCompRTETTwoKeyNavParam")
        .goParameter(0).isMember()
        .goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator().goUpToResourceValidator()
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()"
        + "(PropertyInt16=1,PropertyString='2')/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTESTwoKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTETTwoKeyNavParam"
        + "(ParameterString='1')/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTETTwoKeyNavParam/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n()
        .isFunction("BFCCTPrimCompRTETTwoKeyNavParam")
        .isParameter(0, "ParameterString", "'1'")
        .n()
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany/olingo.odata.test1.BFCESTwoKeyNavRTString()"
        + " eq 'SomeString'")
        .is("<<NavPropertyETTwoKeyNavMany/BFCESTwoKeyNavRTString> eq <'SomeString'>>")
        .root().left().goPath()
        .first()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .n().isFunction("BFCESTwoKeyNavRTString");

    testFilter.runOnETKeyNav("$it/olingo.odata.test1.BFCETKeyNavRTETKeyNav()/PropertyString eq 'SomeString'")
        .is("<<$it/BFCETKeyNavRTETKeyNav/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first().isIt()
        .n().isFunction("BFCETKeyNavRTETKeyNav")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.BFCESTwoKeyNavRTCTTwoPrim()/PropertyString eq 'SomeString'")
        .is("<<BFCESTwoKeyNavRTCTTwoPrim/PropertyString> eq <'SomeString'>>")
        .root().left().goPath()
        .first().isFunction("BFCESTwoKeyNavRTCTTwoPrim")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavOne/olingo.odata.test1.BFCETTwoKeyNavRTETTwoKeyNav()"
        + "/PropertyComp/PropertyComp/PropertyString eq 'Walldorf'")
        .is("<<NavPropertyETTwoKeyNavOne/BFCETTwoKeyNavRTETTwoKeyNav/PropertyComp/PropertyComp/PropertyString> "
            + "eq <'Walldorf'>>")
        .root().left().goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isFunction("BFCETTwoKeyNavRTETTwoKeyNav")
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("PropertyComp/olingo.odata.test1.BFCCTPrimCompRTESTwoKeyNav()"
        + "/olingo.odata.test1.ETBaseTwoKeyNav(PropertyInt16=2,PropertyString='3')/PropertyString eq 'SomeString'")
        .is("<<PropertyComp/BFCCTPrimCompRTESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav/PropertyString> "
            + "eq <'SomeString'>>")
        .root().left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isFunction("BFCCTPrimCompRTESTwoKeyNav")
        .isTypeFilterOnEntry(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isKeyPredicate(0, "PropertyInt16", "2")
        .isKeyPredicate(1, "PropertyString", "'3'")
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

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
        + "/PropertyInt16 eq 2"
        + "&@Param1Alias=1")
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
        .goParameter(0)
        .isMember().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().goUpToResourceValidator()
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNavEx("olingo.odata.test1.UFCRTETTwoKeyNavParam(ParameterInt16=@alias)")
        .isExValidation(UriValidationException.MessageKeys.MISSING_ALIAS);
  }

  @Test
  public void methods() throws Exception {
    testFilter.runOnETKeyNav("indexof(PropertyString,'47') eq 5")
        .is("<<indexof(<PropertyString>,<'47'>)> eq <5>>")
        .left()
        .isMethod(MethodKind.INDEXOF, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'47'>");

    testFilter.runOnETKeyNav("tolower(PropertyString) eq 'foo'")
        .is("<<tolower(<PropertyString>)> eq <'foo'>>")
        .left()
        .isMethod(MethodKind.TOLOWER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("toupper(PropertyString) eq 'FOO'")
        .is("<<toupper(<PropertyString>)> eq <'FOO'>>")
        .left()
        .isMethod(MethodKind.TOUPPER, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("trim(PropertyString) eq 'fooba'")
        .is("<<trim(<PropertyString>)> eq <'fooba'>>")
        .left()
        .isMethod(MethodKind.TRIM, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .left()
        .isMethod(MethodKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<4>)> eq <'foo'>>")
        .left()
        .isMethod(MethodKind.SUBSTRING, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<4>");

    testFilter.runOnETKeyNav("substring(PropertyString,2,4) eq 'foo'")
        .is("<<substring(<PropertyString>,<2>,<4>)> eq <'foo'>>")
        .left()
        .isMethod(MethodKind.SUBSTRING, 3)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<2>")
        .isParameterText(2, "<4>");

    testFilter.runOnETKeyNav("concat(PropertyString,PropertyCompTwoPrim/PropertyString) eq 'foo'")
        .is("<<concat(<PropertyString>,<PropertyCompTwoPrim/PropertyString>)> eq <'foo'>>")
        .left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<PropertyCompTwoPrim/PropertyString>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("concat(PropertyString,'bar') eq 'foobar'")
        .is("<<concat(<PropertyString>,<'bar'>)> eq <'foobar'>>")
        .left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<'bar'>");

    testFilter.runOnETKeyNav("length(PropertyString) eq 32")
        .is("<<length(<PropertyString>)> eq <32>>")
        .left()
        .isMethod(MethodKind.LENGTH, 1)
        .isParameterText(0, "<PropertyString>");

    testFilter.runOnETAllPrim("year(PropertyDate) eq 2013")
        .is("<<year(<PropertyDate>)> eq <2013>>")
        .left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("year(2013-09-25) eq 2013")
        .is("<<year(<2013-09-25>)> eq <2013>>")
        .left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("year(PropertyDateTimeOffset) eq 2013")
        .is("<<year(<PropertyDateTimeOffset>)> eq <2013>>")
        .left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("year(2013-09-25T12:34:56.123456789012-10:24) eq 2013")
        .is("<<year(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013>>")
        .left()
        .isMethod(MethodKind.YEAR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("month(PropertyDate) eq 9")
        .is("<<month(<PropertyDate>)> eq <9>>")
        .left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("month(2013-09-25) eq 9")
        .is("<<month(<2013-09-25>)> eq <9>>")
        .left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("month(PropertyDateTimeOffset) eq 9")
        .is("<<month(<PropertyDateTimeOffset>)> eq <9>>")
        .left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("month(2013-09-25T12:34:56.123456789012-10:24) eq 9")
        .is("<<month(<2013-09-25T12:34:56.123456789012-10:24>)> eq <9>>")
        .left()
        .isMethod(MethodKind.MONTH, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("day(PropertyDate) eq 25")
        .is("<<day(<PropertyDate>)> eq <25>>")
        .left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<PropertyDate>");

    testFilter.runOnETAllPrim("day(2013-09-25) eq 25")
        .is("<<day(<2013-09-25>)> eq <25>>")
        .left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<2013-09-25>");

    testFilter.runOnETAllPrim("day(PropertyDateTimeOffset) eq 25")
        .is("<<day(<PropertyDateTimeOffset>)> eq <25>>")
        .left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("day(2013-09-25T12:34:56.123456789012-10:24) eq 25")
        .is("<<day(<2013-09-25T12:34:56.123456789012-10:24>)> eq <25>>")
        .left()
        .isMethod(MethodKind.DAY, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(PropertyDateTimeOffset) eq 2")
        .is("<<hour(<PropertyDateTimeOffset>)> eq <2>>")
        .left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("hour(2013-09-25T12:34:56.123456789012-10:24) eq 2")
        .is("<<hour(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2>>")
        .left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("hour(PropertyTimeOfDay) eq 2")
        .is("<<hour(<PropertyTimeOfDay>)> eq <2>>")
        .left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("hour(12:34:55.123456789012) eq 12")
        .is("<<hour(<12:34:55.123456789012>)> eq <12>>")
        .left()
        .isMethod(MethodKind.HOUR, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("minute(PropertyDateTimeOffset) eq 34")
        .is("<<minute(<PropertyDateTimeOffset>)> eq <34>>")
        .left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("minute(2013-09-25T12:34:56.123456789012-10:24) eq 34")
        .is("<<minute(<2013-09-25T12:34:56.123456789012-10:24>)> eq <34>>")
        .left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("minute(PropertyTimeOfDay) eq 34")
        .is("<<minute(<PropertyTimeOfDay>)> eq <34>>")
        .left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("minute(12:34:55.123456789012) eq 34")
        .is("<<minute(<12:34:55.123456789012>)> eq <34>>")
        .left()
        .isMethod(MethodKind.MINUTE, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("second(PropertyDateTimeOffset) eq 56")
        .is("<<second(<PropertyDateTimeOffset>)> eq <56>>")
        .left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("second(2013-09-25T12:34:56.123456789012-10:24) eq 56")
        .is("<<second(<2013-09-25T12:34:56.123456789012-10:24>)> eq <56>>")
        .left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("second(PropertyTimeOfDay) eq 56")
        .is("<<second(<PropertyTimeOfDay>)> eq <56>>")
        .left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("second(12:34:55.123456789012) eq 56")
        .is("<<second(<12:34:55.123456789012>)> eq <56>>")
        .left()
        .isMethod(MethodKind.SECOND, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyDateTimeOffset) eq 123456789012")
        .is("<<fractionalseconds(<PropertyDateTimeOffset>)> eq <123456789012>>")
        .left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("fractionalseconds(2013-09-25T12:34:56.123456789012-10:24) eq 123456789012")
        .is("<<fractionalseconds(<2013-09-25T12:34:56.123456789012-10:24>)> eq <123456789012>>")
        .left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("fractionalseconds(PropertyTimeOfDay) eq 123456789012")
        .is("<<fractionalseconds(<PropertyTimeOfDay>)> eq <123456789012>>")
        .left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<PropertyTimeOfDay>");

    testFilter.runOnETAllPrim("fractionalseconds(12:34:55.123456789012) eq 123456789012")
        .is("<<fractionalseconds(<12:34:55.123456789012>)> eq <123456789012>>")
        .left()
        .isMethod(MethodKind.FRACTIONALSECONDS, 1)
        .isParameterText(0, "<12:34:55.123456789012>");

    testFilter.runOnETAllPrim("totalseconds(PropertyDuration) eq 4711")
        .is("<<totalseconds(<PropertyDuration>)> eq <4711>>")
        .left()
        .isMethod(MethodKind.TOTALSECONDS, 1)
        .isParameterText(0, "<PropertyDuration>");

    testFilter.runOnETAllPrim("totalseconds(duration'P10DT5H34M21.123456789012S') eq 4711")
        .is("<<totalseconds(<duration'P10DT5H34M21.123456789012S'>)> eq <4711>>")
        .left()
        .isMethod(MethodKind.TOTALSECONDS, 1)
        .isParameterText(0, "<duration'P10DT5H34M21.123456789012S'>");

    testFilter.runOnETAllPrim("date(PropertyDateTimeOffset) eq 2013-09-25")
        .is("<<date(<PropertyDateTimeOffset>)> eq <2013-09-25>>")
        .left()
        .isMethod(MethodKind.DATE, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("date(2013-09-25T12:34:56.123456789012-10:24) eq 2013-09-25")
        .is("<<date(<2013-09-25T12:34:56.123456789012-10:24>)> eq <2013-09-25>>")
        .left()
        .isMethod(MethodKind.DATE, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("time(PropertyDateTimeOffset) eq 12:34:55.123456789012")
        .is("<<time(<PropertyDateTimeOffset>)> eq <12:34:55.123456789012>>")
        .left()
        .isMethod(MethodKind.TIME, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("time(2013-09-25T12:34:56.123456789012-10:24) eq 12:34:55.123456789012")
        .is("<<time(<2013-09-25T12:34:56.123456789012-10:24>)> eq <12:34:55.123456789012>>")
        .left()
        .isMethod(MethodKind.TIME, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("round(PropertyDouble) eq 17")
        .is("<<round(<PropertyDouble>)> eq <17>>")
        .left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("round(17.45e1) eq 17")
        .is("<<round(<17.45e1>)> eq <17>>")
        .left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("round(PropertyDecimal) eq 17")
        .is("<<round(<PropertyDecimal>)> eq <17>>")
        .left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("round(17.45) eq 17")
        .is("<<round(<17.45>)> eq <17>>")
        .left()
        .isMethod(MethodKind.ROUND, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("floor(PropertyDouble) eq 17")
        .is("<<floor(<PropertyDouble>)> eq <17>>")
        .left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("floor(17.45e1) eq 17")
        .is("<<floor(<17.45e1>)> eq <17>>")
        .left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<17.45e1>");

    testFilter.runOnETAllPrim("floor(PropertyDecimal) eq 17")
        .is("<<floor(<PropertyDecimal>)> eq <17>>")
        .left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("floor(17.45) eq 17")
        .is("<<floor(<17.45>)> eq <17>>")
        .left()
        .isMethod(MethodKind.FLOOR, 1)
        .isParameterText(0, "<17.45>");

    testFilter.runOnETAllPrim("ceiling(PropertyDouble) eq 18")
        .is("<<ceiling(<PropertyDouble>)> eq <18>>")
        .left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<PropertyDouble>");

    testFilter.runOnETAllPrim("ceiling(17.55e1) eq 18")
        .is("<<ceiling(<17.55e1>)> eq <18>>")
        .left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<17.55e1>");

    testFilter.runOnETAllPrim("ceiling(PropertyDecimal) eq 18")
        .is("<<ceiling(<PropertyDecimal>)> eq <18>>")
        .left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<PropertyDecimal>");

    testFilter.runOnETAllPrim("ceiling(17.55) eq 18")
        .is("<<ceiling(<17.55>)> eq <18>>")
        .left()
        .isMethod(MethodKind.CEILING, 1)
        .isParameterText(0, "<17.55>");

    testFilter.runOnETAllPrim("totaloffsetminutes(PropertyDateTimeOffset) eq 4711")
        .is("<<totaloffsetminutes(<PropertyDateTimeOffset>)> eq <4711>>")
        .left()
        .isMethod(MethodKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<PropertyDateTimeOffset>");

    testFilter.runOnETAllPrim("totaloffsetminutes(2013-09-25T12:34:56.123456789012-10:24) eq 4711")
        .is("<<totaloffsetminutes(<2013-09-25T12:34:56.123456789012-10:24>)> eq <4711>>")
        .left()
        .isMethod(MethodKind.TOTALOFFSETMINUTES, 1)
        .isParameterText(0, "<2013-09-25T12:34:56.123456789012-10:24>");

    testFilter.runOnETAllPrim("mindatetime() ne null")
        .is("<<mindatetime()> ne <null>>")
        .left().isMethod(MethodKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("mindatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<mindatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .left()
        .isMethod(MethodKind.MINDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime() ne null")
        .is("<<maxdatetime()> ne <null>>")
        .left().isMethod(MethodKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("maxdatetime() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<maxdatetime()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .left()
        .isMethod(MethodKind.MAXDATETIME, 0);

    testFilter.runOnETAllPrim("now() ne null")
        .is("<<now()> ne <null>>")
        .left().isMethod(MethodKind.NOW, 0);

    testFilter.runOnETAllPrim("now() eq 2013-09-25T12:34:56.123456789012-10:24")
        .is("<<now()> eq <2013-09-25T12:34:56.123456789012-10:24>>")
        .left()
        .isMethod(MethodKind.NOW, 0);

    testFilter.runOnETTwoKeyNav("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnCTTwoPrim("$it/PropertyString eq 'SomeString'")
        .is("<<$it/PropertyString> eq <'SomeString'>>")
        .left()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(ComplexTypeProvider.nameCTTwoPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOnString("$it eq 'Walldorf'")
        .is("<<$it> eq <'Walldorf'>>")
        .left()
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
        .left()
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
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true);

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

    testFilter.runOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyInt16 eq "
        + "$root/ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyInt16")
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
  }

  @Test
  public void castMethod() throws Exception {
    testFilter.runOnETKeyNav("cast(olingo.odata.test1.ETBaseTwoKeyNav) ne null")
        .is("<<cast(<olingo.odata.test1.ETBaseTwoKeyNav>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 1)
        .isParameterText(0, "<olingo.odata.test1.ETBaseTwoKeyNav>")
        .goParameter(0).isTypedLiteral(EntityTypeProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("cast(PropertyCompTwoPrim,olingo.odata.test1.CTBase) ne null")
        .is("<<cast(<PropertyCompTwoPrim>,<olingo.odata.test1.CTBase>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<PropertyCompTwoPrim>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
        .root().left()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTBase);

    testFilter.runOnETKeyNav("cast($it,olingo.odata.test1.CTBase) ne null")
        .is("<<cast(<$it>,<olingo.odata.test1.CTBase>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<olingo.odata.test1.CTBase>")
        .goParameter(0).goPath().first()
        .isIt().isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator()
        .root().left()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTBase);

    testFilter.runOnETKeyNav("concat(PropertyString,cast(PropertyCompAllPrim/PropertyInt16,Edm.String)) ne ''")
        .is("<<concat(<PropertyString>,<cast(<PropertyCompAllPrim/PropertyInt16>,<Edm.String>)>)> ne <''>>")
        .left()
        .isMethod(MethodKind.CONCAT, 2)
        .isParameterText(0, "<PropertyString>")
        .isParameterText(1, "<cast(<PropertyCompAllPrim/PropertyInt16>,<Edm.String>)>")
        .goParameter(1)
        .isMethod(MethodKind.CAST, 2)
        .isParameterText(0, "<PropertyCompAllPrim/PropertyInt16>")
        .isParameterText(1, "<Edm.String>");

    testFilter.runOnETKeyNav("cast($it,olingo.odata.test1.CTBase) eq cast($it,olingo.odata.test1.CTBase)")
        .is("<<cast(<$it>,<olingo.odata.test1.CTBase>)> eq <cast(<$it>,<olingo.odata.test1.CTBase>)>>")
        .left()
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

    testFilter.runOnInt32("cast(Edm.Int32) gt 0")
        .is("<<cast(<Edm.Int32>)> gt <0>>")
        .left()
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnDateTimeOffset("cast(Edm.DateTimeOffset) ne null")
        .is("<<cast(<Edm.DateTimeOffset>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnDuration("cast(Edm.Duration) ne null")
        .is("<<cast(<Edm.Duration>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnTimeOfDay("cast(Edm.TimeOfDay) ne null")
        .is("<<cast(<Edm.TimeOfDay>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 1)
        .goParameter(0).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(CollPropertyInt16,Edm.Int32) ne null")
        .is("<<cast(<CollPropertyInt16>,<Edm.Int32>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath().first()
        .isPrimitiveProperty("CollPropertyInt16", PropertyProvider.nameInt16, true)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnETTwoKeyNav(
        "cast(PropertyComp/PropertyComp/PropertyDateTimeOffset,Edm.DateTimeOffset) ne null")
        .is("<<cast(<PropertyComp/PropertyComp/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset, false)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("cast(PropertyComp/PropertyComp/PropertyDuration,Edm.Duration) ne null")
        .is("<<cast(<PropertyComp/PropertyComp/PropertyDuration>,<Edm.Duration>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("cast(PropertyComp/PropertyComp/PropertyTimeOfDay,Edm.TimeOfDay) ne null")
        .is("<<cast(<PropertyComp/PropertyComp/PropertyTimeOfDay>,<Edm.TimeOfDay>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETKeyNav("cast(PropertyCompAllPrim,olingo.odata.test1.CTTwoPrim) ne null")
        .is("<<cast(<PropertyCompAllPrim>,<olingo.odata.test1.CTTwoPrim>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyCompAllPrim", ComplexTypeProvider.nameCTAllPrim, false)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("cast(NavPropertyETKeyNavOne,olingo.odata.test1.ETKeyPrimNav) ne null")
        .is("<<cast(<NavPropertyETKeyNavOne>,<olingo.odata.test1.ETKeyPrimNav>)> ne <null>>")
        .left()
        .isMethod(MethodKind.CAST, 2)
        .goParameter(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpFilterValidator().root().left()
        .goParameter(1).isTypedLiteral(EntityTypeProvider.nameETKeyPrimNav);

    testFilter.runOnETAllPrim(
        "olingo.odata.test1.UFCRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='1') ne null")
        .left()
        .goPath()
        .isFunction("UFCRTCTTwoPrimTwoParam")
        .isParameter(0, "ParameterInt16", "1")
        .isParameter(1, "ParameterString", "'1'");

    testFilter.runOnETKeyNavEx("cast(NavPropertyETKeyPrimNavOne,olingo.odata.test1.ETKeyNav)")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void lambdaFunctions() throws Exception {
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

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavOne/olingo.odata.test1.BFCETTwoKeyNavRTESTwoKeyNav()"
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

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or "
        + "d/CollPropertyString/any(e:e eq 'SomeString'))")
        .is("<NavPropertyETTwoKeyNavMany/<ANY;<<<d/PropertyInt16> eq <1>>"
            + " or <d/CollPropertyString/<ANY;<<e> eq <'SomeString'>>>>>>>")
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
        .n().isUriPathInfoKind(UriResourceKind.primitiveProperty)
        .isPrimitiveProperty("CollPropertyString", PropertyProvider.nameString, true)
        .at(2).isUriPathInfoKind(UriResourceKind.lambdaAny)
        .goLambdaExpression()
        .root().left().goPath()
        .first().isUriPathInfoKind(UriResourceKind.lambdaVariable)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyInt16 eq 1 or "
        + "d/CollPropertyString/any(e:e eq 'SomeString'))")
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

    testFilter.runOnETKeyNav("NavPropertyETTwoKeyNavMany/any(d:d/PropertyString eq 'SomeString' and "
        + "d/CollPropertyString/any(e:e eq d/PropertyString))")
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

    testFilter.runOnETKeyNavEx("any()")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOnETKeyNavEx("any(d:d/PropertyInt16 eq 1)")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void isOfMethod() throws Exception {
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
        .runOnETKeyNav("isof(olingo.odata.test1.ETBaseTwoKeyNav) eq true and PropertyCompNav/PropertyInt16 eq 1")
        .is("<<<isof(<olingo.odata.test1.ETBaseTwoKeyNav>)> eq <true>> and <<PropertyCompNav/PropertyInt16> eq <1>>>")
        .root().isBinary(BinaryOperatorKind.AND)
        .left().isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ISOF, 1)
        .goParameter(0).isTypedLiteral(EntityTypeProvider.nameETBaseTwoKeyNav);

    testFilter.runOnETKeyNav("isof(NavPropertyETKeyNavOne,olingo.odata.test1.ETKeyNav) eq true")
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

    testFilter.runOnETKeyNav("isof(PropertyCompNav/PropertyInt16,Edm.Int32)")
        .is("<isof(<PropertyCompNav/PropertyInt16>,<Edm.Int32>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplex("PropertyCompNav")
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

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyDuration,Edm.Duration)")
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

    testFilter.runOnETMixEnumDefCollComp("isof(PropertyEnumString,Namespace1_Alias.ENString)")
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(EnumTypeProvider.nameENString);

    testFilter.runOnETMixEnumDefCollComp("isof(PropertyDefString,Namespace1_Alias.TDString)")
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isPrimitiveProperty("PropertyDefString", TypeDefinitionProvider.nameTDString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(TypeDefinitionProvider.nameTDString);
  }

  @Test
  public void has() throws Exception {

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString has olingo.odata.test1.ENString'String1'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String1>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOnETMixEnumDefCollComp(
        "PropertyCompMixedEnumDef/PropertyEnumString has olingo.odata.test1.ENString'String2'")
        .is("<<PropertyCompMixedEnumDef/PropertyEnumString> has <olingo.odata.test1.ENString<String2>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isComplex("PropertyCompMixedEnumDef")
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter
        .runOnETMixEnumDefCollComp(
            "PropertyCompMixedEnumDef/PropertyEnumString has olingo.odata.test1.ENString'String2' eq true")
        .is("<<<PropertyCompMixedEnumDef/PropertyEnumString> has " +
            "<olingo.odata.test1.ENString<String2>>> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .root().left()
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().left().goPath()
        .first().isComplex("PropertyCompMixedEnumDef")
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().left().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String2"));

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString has olingo.odata.test1.ENString'String3'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String3"));

    testFilter.runOnETMixEnumDefCollComp("PropertyEnumString has olingo.odata.test1.ENString'String1,String3'")
        .is("<<PropertyEnumString> has <olingo.odata.test1.ENString<String1,String3>>>")
        .isBinary(BinaryOperatorKind.HAS)
        .root().left().goPath()
        .first().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1", "String3"));

    testFilter.runUriEx("ESMixEnumDefCollComp", "$filter=PropertyEnumString has null")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runUriEx("ESMixEnumDefCollComp", "$filter=PropertyEnumString has ENString'String1'")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runUriEx("ESMixEnumDefCollComp", "$filter=PropertyEnumString has wrongNamespace.ENString'String1'")
        .isExSemantic(MessageKeys.UNKNOWN_TYPE);
    testFilter.runUriEx("ESMixEnumDefCollComp", "$filter=PropertyEnumString has olingo.odata.test1.Wrong'String1'")
        .isExSemantic(MessageKeys.UNKNOWN_TYPE);
  }

  @Test
  public void filterOnCountAndRef() throws Exception {
    testUri.run("ESKeyNav/$count", "$filter=PropertyInt16 ge 0")
        .goPath().isCount()
        .goUpUriValidator().goFilter().isBinary(BinaryOperatorKind.GE)
        .left().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().root().right().isLiteral("0");
    testUri.run("ESKeyNav/$ref", "$filter=PropertyInt16 ge 0")
        .goPath().isRef()
        .goUpUriValidator().goFilter().isBinary(BinaryOperatorKind.GE)
        .left().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().root().right().isLiteral("0");
  }

  @Test
  public void orderby() throws Exception {
    testFilter.runOrderByOnETTwoKeyNav("PropertyString")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplex("PropertyComp")
        .n().isComplex("PropertyComp")
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString'"
        + "&@ParamStringAlias='1'&@ParamInt16Alias=1")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' asc"
        + "&@ParamStringAlias='1'&@ParamInt16Alias=1")
        .isSortOrder(0, false)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc"
        + "&@ParamStringAlias='1'&@ParamInt16Alias=1")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ).left().goPath()
        .first().isFunction("UFCRTETAllPrimTwoParam").goUpFilterValidator()
        .goOrder(0).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("olingo.odata.test1.UFCRTETAllPrimTwoParam("
        + "ParameterString=@ParamStringAlias,ParameterInt16=@ParamInt16Alias)/PropertyString eq 'SomeString' desc,"
        + "PropertyString eq '1'"
        + "&@ParamStringAlias='1'&@ParamInt16Alias=1")
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

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate eq 2013-11-12 desc,"
        + "PropertyString eq 'SomeString' desc")
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

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp desc,PropertyComp/PropertyInt16 eq 1")
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

    testFilter.runOrderByOnETTwoKeyNav("NavPropertyETKeyNavOne/PropertyCompNav")
        .isSortOrder(0, false).goOrder(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isComplex("PropertyCompNav");

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
        .goOrder(0).right().isLiteral("true");

    testFilter.runOrderByOnETAllPrim("PropertyBoolean eq true desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBoolean", PropertyProvider.nameBoolean, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("true");

    testFilter.runOrderByOnETAllPrim("PropertyDouble eq 3.5E+38")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyDouble", PropertyProvider.nameDouble, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("3.5E+38");

    testFilter.runOrderByOnETAllPrim("PropertyDouble eq 3.5E+38 desc").isSortOrder(0, true)
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

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'VGVzdA=='")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", PropertyProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'VGVzdA=='");

    testFilter.runOrderByOnETAllPrim("PropertyBinary eq binary'VGVzdA==' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyBinary", PropertyProvider.nameBinary, false)
        .goUpFilterValidator()
        .goOrder(0).right().isLiteral("binary'VGVzdA=='");

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

    testFilter.runOrderByOnETMixEnumDefCollComp("PropertyEnumString eq olingo.odata.test1.ENString'String1'")
        .isSortOrder(0, false)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOrderByOnETMixEnumDefCollComp("PropertyEnumString eq olingo.odata.test1.ENString'String1' desc")
        .isSortOrder(0, true)
        .goOrder(0).left().goPath().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .goOrder(0).right().isEnum(EnumTypeProvider.nameENString, Arrays.asList("String1"));

    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 1")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16, PropertyInt32 PropertyDuration")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 PropertyInt32, PropertyDuration desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 asc, PropertyInt32 PropertyDuration desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyInt16 asc desc")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
    testFilter.runOrderByOnETTwoKeyNavEx("undefined")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    testFilter.runOrderByOnETTwoKeyNavEx("PropertyComp/undefined")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void search() throws Exception {
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
    testUri.run("ESTwoKeyNav", "$search=(abc)");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)   OR  ghi ");
    testUri.run("ESTwoKeyNav", "$search=(abc AND  def)       ghi ");
    testUri.run("ESTwoKeyNav", "$search=abc AND (def    OR  ghi)");
    testUri.run("ESTwoKeyNav", "$search=abc AND (def        ghi)");

    // search in function-import return value
    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=1)", "$search=test");

    // percent encoded characters
    testUri.run("ESTwoKeyNav", "$search=%41%42%43");
    testUri.run("ESTwoKeyNav", "$search=\"100%25\"");

    // escaped characters
    testUri.run("ESTwoKeyNav", "$search=\"abc\"");
    testUri.run("ESTwoKeyNav", "$search=\"a\\\"bc\"");
    testUri.run("ESTwoKeyNav", "$search=%22abc%22");
    testUri.run("ESTwoKeyNav", "$search=%22a%5C%22bc%22");
    testUri.run("ESTwoKeyNav", "$search=%22a%5C%5Cbc%22");

    // wrong escaped characters
    testUri.runEx("ESTwoKeyNav", "$search=%22a%22bc%22")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    testUri.runEx("ESTwoKeyNav", "$search=%22a%5Cbc%22")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    testUri.runEx("ESTwoKeyNav", "$search=not%27allowed")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
  }

  @Test
  public void searchTree() throws Exception {
    testUri.run("ESTwoKeyNav", "$expand=NavPropertyETKeyNavMany($search=(abc AND def) OR NOT ghi)")
        .goExpand().isSearchSerialized("{{'abc' AND 'def'} OR {NOT 'ghi'}}");
  }

  /**
   * See <a href=
   * "https://tools.oasis-open.org/version-control/browse/wsvn/odata/trunk/spec/ABNF/odata-abnf-testcases.xml">test
   * cases at OASIS</a>.
   */
  @Test
  public void searchQueryPhraseAbnfTestcases() throws Exception {
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    testUri.run("ESTwoKeyNav", "$search=\"blue%20green\"");
    // <TestCase Name="5.1.7 Search - simple phrase" Rule="queryOptions">
    testUri.run("ESTwoKeyNav", "$search=\"blue%20green%22");
    // <TestCase Name="5.1.7 Search - phrase with escaped double-quote" Rule="queryOptions">
    // <Input>$search="blue\"green"</Input>
    testUri.run("ESTwoKeyNav", "$search=\"blue\\\"green\"");

    // <TestCase Name="5.1.7 Search - phrase with escaped backslash" Rule="queryOptions">
    // <Input>$search="blue\\green"</Input>
    testUri.run("ESTwoKeyNav", "$search=\"blue\\\\green\"");
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="14">
    testUri.runEx("ESTwoKeyNav", "$search=\"blue\"green\"")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);
    // <TestCase Name="5.1.7 Search - phrase with unescaped double-quote" Rule="queryOptions" FailAt="16">
    testUri.runEx("ESTwoKeyNav", "$search=\"blue%22green\"")
        .isExceptionMessage(SearchParserException.MessageKeys.TOKENIZER_EXCEPTION);

    // <TestCase Name="5.1.7 Search - implicit AND" Rule="queryOptions">
    // <Input>$search=blue green</Input>
    // SearchassertQuery("\"blue%20green\"").resultsIn();
    testUri.run("ESTwoKeyNav", "$search=blue green");
    // <TestCase Name="5.1.7 Search - implicit AND, encoced" Rule="queryOptions">
    // SearchassertQuery("blue%20green").resultsIn();
    testUri.run("ESTwoKeyNav", "$search=blue%20green");

    // <TestCase Name="5.1.7 Search - AND" Rule="queryOptions">
    // <Input>$search=blue AND green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue AND green");

    // <TestCase Name="5.1.7 Search - OR" Rule="queryOptions">
    // <Input>$search=blue OR green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue OR green");

    // <TestCase Name="5.1.7 Search - NOT" Rule="queryOptions">
    // <Input>$search=blue NOT green</Input>
    testUri.run("ESTwoKeyNav", "$search=blue NOT green");

    // <TestCase Name="5.1.7 Search - only NOT" Rule="queryOptions">
    // <Input>$search=NOT blue</Input>
    testUri.run("ESTwoKeyNav", "$search=NOT blue");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=foo AND bar OR foo AND baz OR that AND bar OR that AND baz</Input>
    testUri.run("ESTwoKeyNav", "$search=foo AND bar OR foo AND baz OR that AND bar OR that AND baz");

    // <TestCase Name="5.1.7 Search - multiple" Rule="queryOptions">
    // <Input>$search=(foo OR that) AND (bar OR baz)</Input>
    testUri.run("ESTwoKeyNav", "$search=(foo OR that) AND (bar OR baz)");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=foo AND (bar OR baz)</Input>
    testUri.run("ESTwoKeyNav", "$search=foo AND (bar OR baz)");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(foo AND bar) OR baz</Input>
    testUri.run("ESTwoKeyNav", "$search=(foo AND bar) OR baz");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo) OR baz</Input>
    testUri.run("ESTwoKeyNav", "$search=(NOT foo) OR baz");

    // <TestCase Name="5.1.7 Search - grouping" Rule="queryOptions">
    // <Input>$search=(NOT foo)</Input>
    testUri.run("ESTwoKeyNav", "$search=(NOT foo)");

    // <TestCase Name="5.1.7 Search - on entity set" Rule="odataUri">
    // <Input>http://serviceRoot/Products?$search=blue</Input>
    testUri.run("ESTwoKeyNav", "$search=blue");

    // <TestCase Name="5.1.7 Search - on entity container" Rule="odataUri">
    // <Input>http://serviceRoot/Model.Container/$all?$search=blue</Input>
    testUri.run("$all", "$search=blue");

    // <TestCase Name="5.1.7 Search - on service" Rule="odataUri">
    // <Input>http://serviceRoot/$all?$search=blue</Input>
    testUri.run("$all", "$search=blue");
  }

  @Test
  public void errors() {
    testUri.runEx("FICRTString(wrong1='ABC')/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);
    testUri.runEx("FICRTString(wrong1='ABC',wrong2=1)/olingo.odata.test1.BFCStringRTESTwoKeyNav()")
        .isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);

    // type filter for entity incompatible
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/olingo.odata.test1.ETBase")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);

    // type filter for entity double on entry
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')"
        + "/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);
    // type filter for entity double on collection
    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);
    // type filter for entity double on non key pred
    testUri.runEx("SINav/olingo.odata.test1.ETBaseTwoKeyNav/olingo.odata.test1.ETBaseTwoKeyNav")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex incompatible
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim"
        + "/olingo.odata.test1.CTCollAllPrim")
        .isExSemantic(MessageKeys.INCOMPATIBLE_TYPE_FILTER);

    // type filter for complex double on entry
    testUri.runEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex double on collection
    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    // type filter for complex double on non key pred
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim"
        + "/olingo.odata.test1.CTBase/olingo.odata.test1.CTBase")
        .isExSemantic(MessageKeys.TYPE_FILTER_NOT_CHAINABLE);

    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.BFC_RTESTwoKeyNav_")
        .isExSemantic(MessageKeys.UNKNOWN_TYPE);

    // $ref
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim/$ref")
        .isExSemantic(MessageKeys.ONLY_FOR_ENTITY_TYPES);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/PropertyCompTwoPrim/$count")
        .isExSemantic(MessageKeys.ONLY_FOR_COLLECTIONS);

    // Actions must not be followed by anything.
    testUri.runEx(ContainerProvider.AIRT_STRING + "/$value")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx(ContainerProvider.AIRTCT_TWO_PRIM_PARAM + "/PropertyInt16")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')/"
        + "olingo.odata.test1.BA_RTETTwoKeyNav/olingo.odata.test1.ETTwoKeyNav")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.BAESTwoKeyNavRTESTwoKeyNav/$count")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
  }

  @Test
  public void doublePercentDecoding() throws Exception {
    testUri.runEx("ESAllPrim%252832767%29").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void multipleKeysInResourcePath() throws Exception {
    // See OLINGO-730
    testUri.runEx("ESAllPrim(32767)(1)(2)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void simpleKeyInExpandSystemQueryOption() throws Exception {
    testUri.runEx("ESAllPrim(0)", "$expand=NavPropertyETTwoPrimMany(-365)($filter=PropertyString eq 'Test String1')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void compoundKeyInExpandSystemQueryOption() throws Exception {
    testUri.runEx("ESAllPrim(0)", "$expand=NavPropertyETTwoPrimMany(PropertyInt16=1,PropertyString=2)"
        + "($filter=PropertyString eq 'Test String1')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void keyPredicatesInExpandFilter() throws Exception {
    testUri.run("ESKeyNav(0)", "$expand=NavPropertyETTwoKeyNavMany($filter=NavPropertyETTwoKeyNavMany"
        + "(PropertyInt16=1,PropertyString='2')/PropertyInt16 eq 1)").goExpand()
        .first().goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goFilter()
        .is("<<NavPropertyETTwoKeyNavMany/PropertyInt16> eq <1>>");
  }

  @Test
  public void keyPredicatesInDoubleExpandedFilter() throws Exception {
    testUri.run("ESKeyNav(0)", "$expand=NavPropertyETTwoKeyNavMany($expand=NavPropertyETTwoKeyNavMany"
        + "($filter=NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/PropertyInt16 eq 1))")
        .goExpand()
        .first().goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goExpand()
        .first().goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goFilter()
        .is("<<NavPropertyETTwoKeyNavMany/PropertyInt16> eq <1>>");
  }

  @Test
  public void filterSystemQueryOptionManyWithKeyAny() throws Exception {
    testFilter.runUriEx("ESAllPrim", "$filter=NavPropertyETTwoPrimMany(1)/any(d:d/PropertyInt16 eq 0)")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void filterSystemQueryOptionManyWithKeyAll() throws Exception {
    testFilter.runUriEx("ESAllPrim", "$filter=NavPropertyETTwoPrimMany(1)/all(d:d/PropertyInt16 eq 0)")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void navigationPropertyWithCount() throws Exception {
    testUri.run("ESKeyNav(1)/NavPropertyETTwoKeyNavMany/$count")
        .goPath().at(0).isEntitySet("ESKeyNav").isKeyPredicate(0, "PropertyInt16", "1")
        .at(1).isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .at(2).isCount();
  }

  @Test
  public void navigationWithMoreThanOneKey() throws Exception {
    testUri.runEx("ESKeyNav(1)/NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')"
        + "(PropertyInt16=1,PropertyString='2')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void startElementsInsteadOfNavigationProperties() {
    testUri.runEx("ESAllPrim(0)/ESAllPrim(0)/ESAllPrim(0)").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim(0)/SINav").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim(0)/FICRTString()").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("ESAllPrim(0)/AIRTString").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("SI/ESAllPrim(0)").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("SI/SINav").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("SI/FICRTString()").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("SI/AIRTString").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("FICRTETKeyNav()/ESAllPrim(0)").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("FICRTETKeyNav()/SINav").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("FICRTETKeyNav()/FICRTString()").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("FICRTETKeyNav()/AIRTString").isExSemantic(MessageKeys.PROPERTY_NOT_IN_TYPE);
    testUri.runEx("AIRTESAllPrimParam/ESAllPrim(0)")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx("AIRTESAllPrimParam/SINav")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx("AIRTESAllPrimParam/FICRTString()")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
    testUri.runEx("AIRTESAllPrimParam/AIRTString")
        .isExValidation(UriValidationException.MessageKeys.UNALLOWED_RESOURCE_PATH);
  }

  @Test
  public void invalidTypeCast() {
    testUri.runEx("ESAllPrim/namespace.Invalid").isExSemantic(MessageKeys.UNKNOWN_TYPE);
    testUri.runEx("ESAllPrim(0)/namespace.Invalid").isExSemantic(MessageKeys.UNKNOWN_TYPE);
  }

  @Test
  public void firstResourcePathWithNamespace() {
    testUri.runEx("olingo.odata.test1.ESAllPrim").isExSemantic(MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
    testUri.runEx("olingo.odata.test1.ESAllPrim(0)").isExSemantic(MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
    testUri.runEx("olingo.odata.test1.FINRTInt16()").isExSemantic(MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
    testUri.runEx("olingo.odata.test1.AIRTString").isExSemantic(MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
    testUri.runEx("olingo.odata.test1.SINav").isExSemantic(MessageKeys.NAMESPACE_NOT_ALLOWED_AT_FIRST_ELEMENT);
  }

  @Test
  public void filterLiteralTypes() throws Exception {
    testFilter.runOnETAllPrim("-1000 eq 42")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("-1000").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16))
        .root()
        .right().isLiteral("42").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte));

    testFilter.runOnETAllPrim("127 eq 128")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("127").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte))
        .root()
        .right().isLiteral("128").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Byte));

    testFilter.runOnETAllPrim("null eq 42.1")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("null").isLiteralType(null)
        .root()
        .right().isLiteral("42.1").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal));

    testFilter.runOnETAllPrim("15.6E300 eq 3.4E37")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("15.6E300")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double))
        .root()
        .right().isLiteral("3.4E37").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Double));

    testFilter.runOnETAllPrim("15.55555555555555555555555555555555555555555555 eq -12345678901234567890")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("15.55555555555555555555555555555555555555555555")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal))
        .root()
        .right().isLiteral("-12345678901234567890")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Decimal));

    testFilter.runOnETAllPrim("duration'PT1H2S' eq duration'PT3602S'")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("duration'PT1H2S'")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration))
        .root()
        .right().isLiteral("duration'PT3602S'")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Duration));

    testFilter.runOnETAllPrim("2013-11-02 ne 2012-12-03")
        .isBinary(BinaryOperatorKind.NE)
        .left().isLiteral("2013-11-02").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date))
        .root()
        .right().isLiteral("2012-12-03").isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Date));

    testFilter.runOnETAllPrim("null eq 2012-12-03T07:16:23Z")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("null")
        .isLiteralType(null)
        .root()
        .right().isLiteral("2012-12-03T07:16:23Z")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.DateTimeOffset));

    testFilter.runOnETAllPrim("07:59:59.999 eq null")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("07:59:59.999")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.TimeOfDay))
        .root()
        .right().isLiteral("null").isLiteralType(null);

    testFilter.runOnETAllPrim("null eq 01234567-89ab-cdef-0123-456789abcdef")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("null").isLiteralType(null)
        .root()
        .right().isLiteral("01234567-89ab-cdef-0123-456789abcdef")
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Guid));

    testFilter.runOnETAllPrim("binary'VGVzdA==' eq null")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral("binary'VGVzdA=='").isLiteralType(
            oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Binary))
        .root()
        .right().isLiteral("null").isLiteralType(null);

    testFilter.runOnETAllPrim(Short.MIN_VALUE + " eq " + Short.MAX_VALUE)
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral(Short.toString(Short.MIN_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16))
        .root()
        .right().isLiteral(Short.toString(Short.MAX_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int16));

    testFilter.runOnETAllPrim(Integer.MIN_VALUE + " eq " + Integer.MAX_VALUE)
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral(Integer.toString(Integer.MIN_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32))
        .root()
        .right().isLiteral(Integer.toString(Integer.MAX_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int32));

    testFilter.runOnETAllPrim(Long.MIN_VALUE + " eq " + Long.MAX_VALUE)
        .isBinary(BinaryOperatorKind.EQ)
        .left().isLiteral(Long.toString(Long.MIN_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64))
        .root()
        .right().isLiteral(Long.toString(Long.MAX_VALUE))
        .isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.Int64));
  }

  @Test
  public void alias() throws Exception {
    testUri.run("ESTwoKeyNav(PropertyInt16=1,PropertyString=@A)", "@A='2'").goPath()
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicateAlias(1, "PropertyString", "@A")
        .goUpUriValidator().isInAliasToValueMap("@A", "'2'");
    testUri.run("ESAllPrim(PropertyInt16=@p1)", "@p1=1").goPath()
        .isKeyPredicateAlias(0, "PropertyInt16", "@p1")
        .goUpUriValidator().isInAliasToValueMap("@p1", "1");
    testUri.run("ESAllPrim(@p1)", "@p1=-2").goPath()
        .isKeyPredicateAlias(0, "PropertyInt16", "@p1")
        .goUpUriValidator().isInAliasToValueMap("@p1", "-2");

    testFilter.runOnETAllPrim("PropertyInt16 gt @alias&@alias=1")
        .right().isAlias("@alias");
    testFilter.runOnETAllPrim("@alias&@alias=@otherAlias&@otherAlias=true")
        .isAlias("@alias");

    testUri.runEx("ESAllPrim(@p1)")
        .isExValidation(UriValidationException.MessageKeys.MISSING_ALIAS);
    testUri.runEx("ESAllPrim(PropertyInt16=@p1)", "@p1='ewe'").isExSemantic(MessageKeys.UNKNOWN_PART);
    testUri.runEx("ESAllPrim(PropertyInt16=@p1)", "@p1='ewe")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETKeyNavEx("PropertyInt16 gt @alias")
        .isInAliasToValueMap("@alias", null);
    testFilter.runOnETKeyNavEx("PropertyInt16 gt @alias&@alias=@alias")
        .isInAliasToValueMap("@alias", "@alias");
    testFilter.runOnETKeyNavEx("@alias&@alias=@alias2&@alias2=true or @alias")
        .isInAliasToValueMap("@alias", "@alias2");
  }

  @Test
  public void functionImportParameterAlias() throws Exception {
    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=@parameterAlias)", "@parameterAlias=1");
    testUri.run("FICRTCollESTwoKeyNavParam(ParameterInt16=@parameterAlias)/$count", "@parameterAlias=1");
    testUri.runEx("FICRTCollESTwoKeyNavParam(ParameterInt16=@invalidAlias)", "@validAlias=1")
        .isExValidation(UriValidationException.MessageKeys.MISSING_ALIAS);
  }

  @Test
  public void functionsWithComplexParameters() throws Exception {
    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1)", "@p1={\"PropertyInt16\":1,\"PropertyString\":\"1\"}")
        .goPath()
        .at(0).isEntitySet("ESTwoKeyNav")
        .at(1).isFunction("BFCESTwoKeyNavRTStringParam").isParameterAlias(0, "ParameterComp", "@p1")
        .goUpUriValidator().isInAliasToValueMap("@p1", "{\"PropertyInt16\":1,\"PropertyString\":\"1\"}");

    // Test JSON String lexer rule =\"3,Int16=abc},\\\nabc&test%test\b\f\r\t\u0022\\}\\{\\)\\(\\]\\[}
    final String stringValueEncoded = "=\\\"3,Int16=abc},\\\\\\nabc%26test%25test\\b\\f\\r\\t\\u0022\\\\}\\\\{\\\\)"
        + "\\\\(\\\\]\\\\[}";
    final String stringValueDecoded = "=\\\"3,Int16=abc},\\\\\\nabc&test%test\\b\\f\\r\\t\\u0022\\\\}\\\\{\\\\)"
        + "\\\\(\\\\]\\\\[}";

    testUri.run("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1)", "@p1={\"PropertyInt16\":1,\"PropertyString\":\"" + stringValueEncoded + "\"}")
        .goPath()
        .at(0).isEntitySet("ESTwoKeyNav")
        .at(1).isFunction("BFCESTwoKeyNavRTStringParam").isParameterAlias(0, "ParameterComp", "@p1")
        .goUpUriValidator()
        .isInAliasToValueMap("@p1", "{\"PropertyInt16\":1,\"PropertyString\":\"" + stringValueDecoded + "\"}");

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp={\"PropertyString\":\"Test\",\"PropertyInt16\":1}) eq 'Test'")
        .is("<<BFCESTwoKeyNavRTStringParam> eq <'Test'>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isParameterText(0, "{\"PropertyString\":\"Test\",\"PropertyInt16\":1}");

    testFilter.runOnETTwoKeyNav("olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp={\"PropertyString\":\"" + stringValueEncoded + "\",\"PropertyInt16\":1}) eq 'Test'")
        .is("<<BFCESTwoKeyNavRTStringParam> eq <'Test'>>")
        .left().isParameterText(0, "{\"PropertyString\":\"" + stringValueDecoded + "\",\"PropertyInt16\":1}");

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":1,\"PropertyString\":\"1\"}");

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":1,\"PropertyString\":null}")
        .goFilter().left().isParameterText(0, null);

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={}");

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[1,2,3],\"PropertyString\":\"1\"}");

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[\"1\",\"2\",\"3\"],\"PropertyString\":\"1\"}");

    testUri.run("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[{\"Prop1\":123,\"Prop2\":\"Test\",\"Prop3\":[1,2,3]},"
        + "{\"Prop1\":{\"Prop1\":[\"Prop\\\":{]\"]}}],\"PropertyString\":\"1\"}");

    testUri.run("FINRTByteNineParam(ParameterEnum=null,ParameterDef='x',ParameterComp=@c,"
        + "ParameterETTwoPrim=@c,CollParameterByte=@e,CollParameterEnum=@e,CollParameterDef=@e,"
        + "CollParameterComp=@e,CollParameterETTwoPrim=@e)",
        "@c={}&@e=[]");

    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1)", "@p1={\"PropertyInt16\":1,\"PropertyString\":'1'}")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("ESTwoKeyNav/olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp={\"PropertyInt16\":1,\"PropertyString\":\"Test\"})")
        .isExSemantic(MessageKeys.COMPLEX_PARAMETER_IN_RESOURCE_PATH);

    testUri.runEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=null)")
        .isExValidation(UriValidationException.MessageKeys.MISSING_PARAMETER);

    testUri.runEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)")
        .isExValidation(UriValidationException.MessageKeys.MISSING_ALIAS);

    testUri.runEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)", "@test=null")
        .isExValidation(UriValidationException.MessageKeys.MISSING_ALIAS);

    testUri.run("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)", "@test='null'");

    testUri.runEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test,UnknownParam=1)", "@test='null'")
        .isExSemantic(MessageKeys.FUNCTION_NOT_FOUND);

    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)", "@test='null'");
    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)", "@test=null");
    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)");
    testUri.run("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=null)");

    testUri.runEx("FICRTCollCTTwoPrimTwoParam(ParameterInt16=1,ParameterString=@test)", "@test=null&@test='1'")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DUPLICATED_ALIAS);

    testFilter.runOnETKeyNavEx("FINRTInt16() eq 0")
        .isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);

    testUri.runEx("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":1,\"PropertyString\":\"1\"")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":1,\"PropertyString\":\"1\"}}")
        .isExSemantic(MessageKeys.UNKNOWN_PART);

    testUri.runEx("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[1,2,3]],\"PropertyString\":\"1\"}")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[1,2,3,\"PropertyString\":\"1\"}")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);

    testUri.runEx("ESTwoKeyNav", "$filter=olingo.odata.test1.BFCESTwoKeyNavRTStringParam"
        + "(ParameterComp=@p1) eq '0'&@p1={\"PropertyInt16\":[1,2,3},\"PropertyString\":\"1\"}")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void keyPredicatesInExpressions() throws Exception {
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany(PropertyString='1',PropertyInt16=1)"
        + "/PropertyInt16 eq 1");
    testUri.runEx("ESTwoKeyNav", "$filter=NavPropertyETTwoKeyNavMany(Prop='22',P=2)/PropertyInt16 eq 0")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
  }
}
