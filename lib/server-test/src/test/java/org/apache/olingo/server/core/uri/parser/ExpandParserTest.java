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
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.apply.Aggregate;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression;
import org.apache.olingo.server.core.uri.parser.UriParserSemanticException.MessageKeys;
import org.apache.olingo.server.core.uri.testutil.ExpandValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.junit.Assert;
import org.junit.Test;

/** Tests of the parts of the URI parser that parse the sytem query option $expand. */
public class ExpandParserTest {

  private static final OData oData = OData.newInstance();
  private static final Edm edm = oData.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  @Test
  public void expandStar() throws Exception {
    runOnETKeyNav("*").isSegmentStar();

    runOnETKeyNav("*/$ref")
        .isSegmentStar()
        .isSegmentRef();

    runOnETKeyNav("*/$ref,NavPropertyETKeyNavMany")
        .isSegmentStar().isSegmentRef()
        .next()
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    runOnETKeyNav("*($levels=3)")
        .isSegmentStar()
        .isLevels(3);

    runOnETKeyNav("*($levels=max)")
        .isSegmentStar()
        .isLevelsMax();
  }

  @Test
  public void expandNavigationRef() throws Exception {
    runOnETKeyNav("NavPropertyETKeyNavMany/$ref")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef();

    runOnETKeyNav("NavPropertyETKeyNavOne/$ref")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isRef();

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($filter=PropertyInt16 eq 1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator().goFilter().is("<<PropertyInt16> eq <1>>");

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($orderby=PropertyInt16)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($skip=1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkip(1);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($top=2)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isTop(2);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($count=true)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isInlineCount(true);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($skip=1;$top=3)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkip(1)
        .isTop(3);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref($skip=1%3b$top=3)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .isSkip(1)
        .isTop(3);
  }

  @Test
  public void expandNavigationCount() throws Exception {
    runOnETKeyNav("NavPropertyETKeyNavMany/$count")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isCount();

    runOnETKeyNav("NavPropertyETKeyNavOne/$count")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isCount();

    runOnETKeyNav("NavPropertyETKeyNavMany/$count($filter=PropertyInt16 gt 1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isCount()
        .goUpExpandValidator()
        .goFilter().is("<<PropertyInt16> gt <1>>");
  }

  @Test
  public void expandNavigationOptions() throws Exception {
    runOnETTwoKeyNav("NavPropertyETKeyNavMany")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    runOnETKeyNav("NavPropertyETKeyNavMany($filter=PropertyInt16 eq 1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator().goFilter().is("<<PropertyInt16> eq <1>>");

    runOnETKeyNav("NavPropertyETKeyNavMany($orderby=PropertyInt16)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSortOrder(0, false)
        .goOrder(0).goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    runOnETKeyNav("NavPropertyETKeyNavMany($skip=1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkip(1);

    runOnETKeyNav("NavPropertyETKeyNavMany($top=2)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isTop(2);

    runOnETKeyNav("NavPropertyETKeyNavMany($count=true)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isInlineCount(true);

    runOnETKeyNav("NavPropertyETKeyNavMany($select=PropertyString)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    runOnETKeyNav("NavPropertyETKeyNavMany($expand=NavPropertyETTwoKeyNavOne)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    runOnETKeyNav("NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavMany)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    runOnETKeyNav("NavPropertyETKeyNavOne($levels=5)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevels(5);

    runOnETKeyNav("NavPropertyETKeyNavMany($select=PropertyString)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    runOnETKeyNav("NavPropertyETKeyNavOne($levels=max)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .isLevelsMax();

    runOnETKeyNav("NavPropertyETKeyNavMany($skip=1;$top=2)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkip(1)
        .isTop(2);

    runOnETKeyNav("NavPropertyETKeyNavMany($skip=1%3b$top=2)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSkip(1)
        .isTop(2);

    runOnETKeyNav("NavPropertyETKeyNavMany($search=Country AND Western)")
        .goPath().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .isSearchSerialized("{'Country' AND 'Western'}");
  }

  @Test
  public void expandNavigationApplyOption() throws Exception {
    UriInfo uriInfo = new Parser(edm, oData).parseUri("ESTwoKeyNav",
        "$expand=NavPropertyETKeyNavMany($apply=identity),NavPropertyETKeyNavOne", null, null);
    Assert.assertEquals(ApplyItem.Kind.IDENTITY,
        uriInfo.getExpandOption().getExpandItems().get(0).getApplyOption().getApplyItems().get(0).getKind());
    Assert.assertEquals("NavPropertyETKeyNavOne",
        uriInfo.getExpandOption().getExpandItems().get(1)
            .getResourcePath().getUriResourceParts().get(0).getSegmentValue());

    uriInfo = new Parser(edm, oData).parseUri("ESTwoKeyNav",
        "$expand=NavPropertyETKeyNavMany($apply=aggregate(PropertyInt16 with sum as s))", null, null);
    final ApplyItem applyItem =
        uriInfo.getExpandOption().getExpandItems().get(0).getApplyOption().getApplyItems().get(0);
    Assert.assertEquals(ApplyItem.Kind.AGGREGATE, applyItem.getKind());
    Assert.assertEquals(AggregateExpression.StandardMethod.SUM,
        ((Aggregate) applyItem).getExpressions().get(0).getStandardMethod());
  }

  @Test
  public void expandTypeCasts() throws Exception {
    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETKeyNavMany")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav/NavPropertyETTwoKeyNavOne")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav/*")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .isSegmentStar()
        .goPath().isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/PropertyCompNav"
        + "/olingo.odata.test1.CTTwoBasePrimCompNav/NavPropertyETTwoKeyNavOne")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTBasePrimCompNav, false)
        .isTypeFilter(ComplexTypeProvider.nameCTTwoBasePrimCompNav)
        .n()
        .isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false);

    runOnETKeyNav("NavPropertyETTwoKeyNavMany/Namespace1_Alias.ETBaseTwoKeyNav"
        + "($expand=NavPropertyETBaseTwoKeyNavOne)")
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goUpExpandValidator()
        // go to the expand options of the current expand
        .goExpand()
        .goPath()
        .isNavProperty("NavPropertyETBaseTwoKeyNavOne", EntityTypeProvider.nameETBaseTwoKeyNav, false);

    runOnETKeyNav("NavPropertyETKeyNavMany/$ref,NavPropertyETTwoKeyNavMany($skip=2;$top=1)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .n().isRef()
        .goUpExpandValidator()
        .next()
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator()
        .isSkip(2)
        .isTop(1);

    runOnETTwoKeyNav("olingo.odata.test1.ETBaseTwoKeyNav/NavPropertyETTwoKeyNavMany"
        + "/olingo.odata.test1.ETTwoBaseTwoKeyNav($select=PropertyString)")
        .isExpandStartType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETTwoBaseTwoKeyNav)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    runOnETKeyNav("NavPropertyETKeyNavOne($expand=NavPropertyETKeyNavMany($expand=NavPropertyETKeyNavOne))")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .goExpand()
        .goPath()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator()
        .goExpand()
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false);

    runOnETKeyNav("NavPropertyETKeyNavOne($select=PropertyInt16)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .goSelectItem(0).isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    runOnETKeyNav("NavPropertyETKeyNavOne($select=PropertyCompNav/PropertyInt16)")
        .goPath()
        .isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .goUpExpandValidator()
        .goSelectItem(0)
        .isComplexProperty("PropertyCompNav", ComplexTypeProvider.nameCTNavFiveProp, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    runOnETKeyNavEx("undefined").isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    runOnETTwoKeyNavEx("PropertyCompNav/undefined").isExSemantic(MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    runOnETTwoKeyNavEx("PropertyCompNav/*+")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void duplicatedSystemQueryOptionsInExpand() throws Exception {
    runOnETKeyNavEx("NavPropertyETKeyNavOne($select=PropertyInt16;$select=PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($filter=true;$filter=true)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($orderby=PropertyInt16;$orderby=PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($levels=2;$levels=3)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($expand=*;$expand=*)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($count=true;$count=true)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($top=1;$top=1)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($skip=2;$skip=2)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);

    runOnETKeyNavEx("NavPropertyETKeyNavOne($search=Test;$search=Test)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.DOUBLE_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void simpleKeyInExpandSystemQueryOption() throws Exception {
    runOnETTwoKeyNavEx("NavPropertyETKeyNavMany(-365)($filter=PropertyString eq 'Test String1')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void compoundKeyInExpandSystemQueryOption() throws Exception {
    runOnETKeyNavEx("NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString=2)"
        + "($filter=PropertyString eq 'Test String1')")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void keyPredicatesInExpandFilter() throws Exception {
    runOnETKeyNav("NavPropertyETTwoKeyNavMany($filter=NavPropertyETTwoKeyNavMany"
        + "(PropertyInt16=1,PropertyString='2')/PropertyInt16 eq 1)")
        .goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goFilter()
        .left().goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  @Test
  public void keyPredicatesInDoubleExpandedFilter() throws Exception {
    runOnETKeyNav("NavPropertyETTwoKeyNavMany($expand=NavPropertyETTwoKeyNavMany"
        + "($filter=NavPropertyETTwoKeyNavMany(PropertyInt16=1,PropertyString='2')/PropertyInt16 eq 1))")
        .goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goExpand()
        .goPath().isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, true)
        .goUpExpandValidator().goFilter()
        .left().goPath()
        .isNavProperty("NavPropertyETTwoKeyNavMany", EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  private ExpandValidator runOnETKeyNav(final String expand) throws ODataLibraryException {
    return new TestUriValidator().setEdm(edm).run("ESKeyNav(1)", "$expand=" + expand)
        .isKind(UriInfoKind.resource).goPath().isType(EntityTypeProvider.nameETKeyNav, false)
        .goUpUriValidator().goExpand();
  }

  private ExpandValidator runOnETTwoKeyNav(final String expand) throws ODataLibraryException {
    return new TestUriValidator().setEdm(edm)
        .run("ESTwoKeyNav(PropertyInt16=1,PropertyString='2')", "$expand=" + expand)
        .isKind(UriInfoKind.resource)
        .goPath()
        .isType(EntityTypeProvider.nameETTwoKeyNav, false)
        .isKeyPredicate(0, "PropertyInt16", "1")
        .isKeyPredicate(1, "PropertyString", "'2'")
        .goUpUriValidator()
        .goExpand();
  }

  private TestUriValidator runOnETKeyNavEx(final String expand) throws ODataLibraryException {
    return new TestUriValidator().setEdm(edm).runEx("ESKeyNav(1)", "$expand=" + expand);
  }

  private TestUriValidator runOnETTwoKeyNavEx(final String expand) throws ODataLibraryException {
    return new TestUriValidator().setEdm(edm).runEx("ESTwoKeyNav", "$expand=" + expand);
  }
}
