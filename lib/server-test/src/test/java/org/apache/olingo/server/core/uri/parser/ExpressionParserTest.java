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
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.testutil.FilterValidator;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EnumTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.apache.olingo.server.tecsvc.provider.TypeDefinitionProvider;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the sytem query options $filter and $orderby. */
public class ExpressionParserTest {

  private static final OData oData = OData.newInstance();
  private static final Edm edm = oData.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final FilterValidator testFilter = new FilterValidator().setEdm(edm);

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
    testFilter.runOnETTwoKeyNavEx("CollPropertyComp/")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
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
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("NavPropertyETKeyNavMany/NavPropertyETTwoKeyNavOne eq null")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    testFilter.runOnETTwoKeyNavEx("NavPropertyETKeyNavMany ne null").isExSemantic(MessageKeys.COLLECTION_NOT_ALLOWED);
    testFilter.runOnETTwoKeyNavEx("CollPropertyString eq 'SomeString'")
        .isExSemantic(MessageKeys.COLLECTION_NOT_ALLOWED);
    testFilter.runOnETTwoKeyNavEx("NavPropertyETKeyNavMany").isExSemantic(MessageKeys.COLLECTION_NOT_ALLOWED);
    testFilter.runOnETTwoKeyNavEx("olingo.odata.test1.ETBaseTwoKeyNav/PropertyDate")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runUriEx("FICRTCTTwoPrimTwoParam(ParameterInt16=1,ParameterString='2')",
        "$filter=olingo.odata.test1.CTBase/AdditionalPropString")
        .isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
  }

  @Test
  public void filterUnaryOperators() throws Exception {
    testFilter.runOnETAllPrim("not PropertyBoolean").is("<not <PropertyBoolean>>");
    testFilter.runOnETAllPrim("not (PropertyBoolean)").is("<not <PropertyBoolean>>");
    testFilter.runOnETAllPrim("-PropertyInt16 eq PropertyInt16").is("<<- <PropertyInt16>> eq <PropertyInt16>>");
    testFilter.runOnETAllPrim("not (PropertyString eq null)").is("<not <<PropertyString> eq <null>>>");
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

    testFilter.runOnETAllPrim("PropertyInt16 gt null")
        .left().isType(PropertyProvider.nameInt16);

    testFilter.runOnETKeyNavEx("binary'Yw==' gt null").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETKeyNavEx("NavPropertyETKeyNavOne gt null").isExSemantic(MessageKeys.TYPES_NOT_COMPATIBLE);
    testFilter.runOnETKeyNavEx("NavPropertyETKeyNavMany gt null").isExSemantic(MessageKeys.COLLECTION_NOT_ALLOWED);
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
        .root().left().goPath().isPrimitiveProperty("PropertyByte", PropertyProvider.nameByte, false)
        .goUpFilterValidator()
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
        .first().isComplexProperty("PropertyCompMixedEnumDef", ComplexTypeProvider.nameCTMixEnumDef, false)
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
        .first().isComplexProperty("PropertyCompMixedEnumDef", ComplexTypeProvider.nameCTMixEnumDef, false)
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false)
        .goUpFilterValidator()
        .root().right().goPath()
        .first().isComplexProperty("PropertyCompMixedEnumDef", ComplexTypeProvider.nameCTMixEnumDef, false)
        .n().isPrimitiveProperty("PropertyEnumString", EnumTypeProvider.nameENString, false);

    testFilter.runOnETAllPrim("PropertyByte mod 0 gt 0")
        .is("<<<PropertyByte> mod <0>> gt <0>>");

    testFilter.runOnETKeyNavEx("CollPropertyComp/PropertyInt16 le 0")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
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

    testFilter.runOnString("endswith($it,'company.com')")
        .is("<endswith(<$it>,<'company.com'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'company.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnString("endswith($it,'company.com') eq false")
        .is("<<endswith(<$it>,<'company.com'>)> eq <false>>")
        .left()
        .isMethod(MethodKind.ENDSWITH, 2)
        .isParameterText(0, "<$it>")
        .isParameterText(1, "<'company.com'>")
        .goParameter(0)
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.it)
        .isType(PropertyProvider.nameString, false);

    testFilter.runOnETTwoKeyNav("endswith(PropertyComp/PropertyComp/PropertyString,'dorf')")
        .is("<endswith(<PropertyComp/PropertyComp/PropertyString>,<'dorf'>)>")
        .isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'dorf'");

    testFilter.runOnETTwoKeyNav("endswith(PropertyComp/PropertyComp/PropertyString,'dorf') eq true")
        .is("<<endswith(<PropertyComp/PropertyComp/PropertyString>,<'dorf'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.ENDSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
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
        .first().isComplexProperty("PropertyCompAllPrim", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETKeyNav("startswith(PropertyCompAllPrim/PropertyString,'Wall') eq true")
        .is("<<startswith(<PropertyCompAllPrim/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.STARTSWITH, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyCompAllPrim", ComplexTypeProvider.nameCTAllPrim, false)
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
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false).goUpFilterValidator()
        .root().goParameter(1).isLiteral("'Wall'");

    testFilter.runOnETTwoKeyNav("contains(PropertyComp/PropertyComp/PropertyString,'Wall') eq true")
        .is("<<contains(<PropertyComp/PropertyComp/PropertyString>,<'Wall'>)> eq <true>>")
        .isBinary(BinaryOperatorKind.EQ)
        .left().isMethod(MethodKind.CONTAINS, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
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
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().right()
        .goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    testFilter.runOnETTwoKeyNavEx("startswith(CollPropertyString,'wrong')")
        .isExSemantic(MessageKeys.COLLECTION_NOT_ALLOWED);
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

    testFilter.runOnETTwoKeyNav("CollPropertyComp/all(l:true)")
        .is("<CollPropertyComp/<ALL;<true>>>");

    testFilter.runOnETTwoKeyNav("CollPropertyComp/all(x:x/PropertyInt16 eq 2)")
        .is("<CollPropertyComp/<ALL;<<x/PropertyInt16> eq <2>>>>");

    testFilter.runOnETTwoKeyNav("CollPropertyComp/any(l:true)")
        .is("<CollPropertyComp/<ANY;<true>>>");
    testFilter.runOnETTwoKeyNav("CollPropertyComp/any()")
        .is("<CollPropertyComp/<ANY;>>");

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
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
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
        .n().isUriPathInfoKind(UriResourceKind.lambdaAny)
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
        .goParameter(0).goPath().isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoPrim);

    testFilter.runOnETKeyNav("isof(PropertyCompTwoPrim,olingo.odata.test1.CTTwoBase)")
        .is("<isof(<PropertyCompTwoPrim>,<olingo.odata.test1.CTTwoBase>)>")
        .root().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(ComplexTypeProvider.nameCTTwoBase);

    testFilter.runOnETKeyNav("isof(PropertyCompTwoPrim,olingo.odata.test1.CTTwoPrim) eq true")
        .is("<<isof(<PropertyCompTwoPrim>,<olingo.odata.test1.CTTwoPrim>)> eq <true>>")
        .root().left().isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath().isComplexProperty("PropertyCompTwoPrim", ComplexTypeProvider.nameCTTwoPrim, false)
        .goUpFilterValidator()
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
        .first().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameInt32);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyDateTimeOffset,Edm.DateTimeOffset)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyDateTimeOffset>,<Edm.DateTimeOffset>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDateTimeOffset", PropertyProvider.nameDateTimeOffset, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameDateTimeOffset);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyTimeOfDay,Edm.TimeOfDay)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyTimeOfDay>,<Edm.TimeOfDay>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyTimeOfDay", PropertyProvider.nameTimeOfDay, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameTimeOfDay);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyDuration,Edm.Duration)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyDuration>,<Edm.Duration>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameDuration);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyString,Edm.String)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyString>,<Edm.String>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .root().goParameter(1).isTypedLiteral(PropertyProvider.nameString);

    testFilter.runOnETTwoKeyNav("isof(PropertyComp/PropertyComp/PropertyString,Edm.Guid)")
        .is("<isof(<PropertyComp/PropertyComp/PropertyString>,<Edm.Guid>)>")
        .root()
        .isMethod(MethodKind.ISOF, 2)
        .goParameter(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
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
        .first().isComplexProperty("PropertyCompMixedEnumDef", ComplexTypeProvider.nameCTMixEnumDef, false)
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
        .first().isComplexProperty("PropertyCompMixedEnumDef", ComplexTypeProvider.nameCTMixEnumDef, false)
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
  public void orderby() throws Exception {
    testFilter.runOrderByOnETTwoKeyNav("PropertyString")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate")
        .isSortOrder(0, false)
        .goOrder(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
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
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false)
        .goUpFilterValidator()
        .goOrder(0).right().goPath()
        .first().isUriPathInfoKind(UriResourceKind.root)
        .n().isEntitySet("ESTwoKeyNav")
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyDate eq 2013-11-12 desc,"
        + "PropertyString eq 'SomeString' desc")
        .isSortOrder(0, true)
        .goOrder(0).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
        .n().isPrimitiveProperty("PropertyDate", PropertyProvider.nameDate, false).goUpFilterValidator()
        .goOrder(0).right().isLiteral("2013-11-12")
        .isSortOrder(1, true)
        .goOrder(1).left().goPath().first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false)
        .goUpFilterValidator()
        .goOrder(1).right().isLiteral("'SomeString'");

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp desc,PropertyComp/PropertyInt16 eq 1")
        .isSortOrder(0, true)
        .goOrder(0).goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .goUpFilterValidator()
        .isSortOrder(1, false)
        .goOrder(1).isBinary(BinaryOperatorKind.EQ)
        .left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
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
        .n().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false);

    testFilter.runOrderByOnETTwoKeyNav("PropertyComp/PropertyComp/PropertyInt16 eq 1")
        .isSortOrder(0, false).goOrder(0).left().goPath()
        .first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTAllPrim, false)
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
  public void filterComplexMixedPriority() throws Exception {
    testFilter.runOnETAllPrim("PropertyBoolean or true and false")
        .is("<<PropertyBoolean> or <<true> and <false>>>");
    testFilter.runOnETAllPrim("PropertyBoolean or true and PropertyInt64 eq PropertyByte")
        .is("<<PropertyBoolean> or <<true> and <<PropertyInt64> eq <PropertyByte>>>>");
    testFilter.runOnETAllPrim("PropertyBoolean or PropertyInt32 eq PropertyInt64 and true")
        .is("<<PropertyBoolean> or <<<PropertyInt32> eq <PropertyInt64>> and <true>>>");
    testFilter.runOnETAllPrim("PropertyBoolean or PropertyInt32 eq PropertyInt64 and PropertyByte eq PropertySByte")
        .is("<<PropertyBoolean> or <<<PropertyInt32> eq <PropertyInt64>> "
            + "and <<PropertyByte> eq <PropertySByte>>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyBoolean and true")
        .is("<<<PropertyInt16> eq <PropertyInt32>> or <<PropertyBoolean> and <true>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyBoolean and PropertyByte eq PropertySByte")
        .is("<<<PropertyInt16> eq <PropertyInt32>> "
            + "or <<PropertyBoolean> and <<PropertyByte> eq <PropertySByte>>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 eq PropertyByte and PropertyBoolean")
        .is("<<<PropertyInt16> eq <PropertyInt32>> "
            + "or <<<PropertyInt64> eq <PropertyByte>> and <PropertyBoolean>>>");
    testFilter.runOnETAllPrim("PropertyInt16 eq PropertyInt32 or PropertyInt64 eq PropertyByte "
        + "and PropertySByte eq PropertyDecimal")
        .is("<<<PropertyInt16> eq <PropertyInt32>> or <<<PropertyInt64> eq <PropertyByte>> "
            + "and <<PropertySByte> eq <PropertyDecimal>>>>");
  }

  @Test
  public void filterSimpleSameBinaryBinaryBinaryPriority() throws Exception {
    testFilter.runOnETAllPrim("1 add 2 add 3 add 4 ge 0").isCompr("<<<< <1> add   <2>> add  <3>>  add <4>> ge <0>>");
    testFilter.runOnETAllPrim("1 add 2 add 3 div 4 ge 0").isCompr("<<<  <1> add   <2>> add <<3>   div <4>>> ge <0>>");
    testFilter.runOnETAllPrim("1 add 2 div 3 add 4 ge 0").isCompr("<<<  <1> add  <<2>  div  <3>>> add <4>> ge <0>>");
    testFilter.runOnETAllPrim("1 add 2 div 3 div 4 ge 0").isCompr("<<   <1> add <<<2>  div  <3>>  div <4>>> ge <0>>");
    testFilter.runOnETAllPrim("1 div 2 add 3 add 4 ge 0").isCompr("<<<< <1> div   <2>> add  <3>>  add <4>> ge <0>>");
    testFilter.runOnETAllPrim("1 div 2 add 3 div 4 ge 0").isCompr("<<<  <1> div   <2>> add <<3>   div <4>>> ge <0>>");
    testFilter.runOnETAllPrim("1 div 2 div 3 add 4 ge 0").isCompr("<<<< <1> div   <2>> div  <3>>  add <4>> ge <0>>");
    testFilter.runOnETAllPrim("1 div 2 div 3 div 4 ge 0").isCompr("<<<< <1> div   <2>> div  <3>>  div <4>> ge <0>>");
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
  public void filterOnCountAndRef() throws Exception {
    testFilter.runUri("ESKeyNav/$count", "$filter=PropertyInt16 ge 0")
        .isBinary(BinaryOperatorKind.GE)
        .left().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().root().right().isLiteral("0");

    testFilter.runUri("ESKeyNav/$ref", "$filter=PropertyInt16 ge 0")
        .isBinary(BinaryOperatorKind.GE)
        .left().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().root().right().isLiteral("0");
  }

  @Test
  public void keyPredicatesInExpressions() throws Exception {
    testFilter.runOnETTwoKeyNav("NavPropertyETTwoKeyNavMany(PropertyString='1',PropertyInt16=1)"
        + "/PropertyInt16 eq 1");

    testFilter.runOnETTwoKeyNavEx("NavPropertyETTwoKeyNavMany(Prop='22',P=2)/PropertyInt16 eq 0")
        .isExValidation(UriValidationException.MessageKeys.INVALID_KEY_PROPERTY);
  }

  @Test
  public void geo() throws Exception {
    testFilter.runOnETAllPrim("geo.distance(geometry'SRID=0;Point(0 0)',geometry'SRID=0;Point(1 1)') lt 1.5")
        .left().isMethod(MethodKind.GEODISTANCE, 2)
        .goParameter(0).isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryPoint))
        .root().left()
        .goParameter(1).isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryPoint));
    testFilter.runOnETAllPrim("geo.length(geometry'SRID=0;LineString(0 0,1 1)') lt 1.5")
        .left().isMethod(MethodKind.GEOLENGTH, 1)
        .goParameter(0).isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryLineString));
    testFilter.runOnETAllPrim("geo.intersects(geometry'SRID=0;Point(0 0)',null)")
        .isMethod(MethodKind.GEOINTERSECTS, 2)
        .goParameter(0).isLiteralType(oData.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.GeometryPoint));
  }
}
