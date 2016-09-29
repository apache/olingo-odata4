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

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the sytem query option $expand. */
public class ExpandParserTest {

  private static final Edm edm = OData.newInstance().createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  private final TestUriValidator testUri = new TestUriValidator().setEdm(edm);

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
        .goPath().first().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false);

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
}
