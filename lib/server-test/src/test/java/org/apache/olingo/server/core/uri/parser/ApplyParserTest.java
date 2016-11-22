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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceKind;
import org.apache.olingo.server.api.uri.queryoption.ApplyItem;
import org.apache.olingo.server.api.uri.queryoption.ApplyOption;
import org.apache.olingo.server.api.uri.queryoption.apply.Aggregate;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression.StandardMethod;
import org.apache.olingo.server.api.uri.queryoption.apply.BottomTop;
import org.apache.olingo.server.api.uri.queryoption.apply.BottomTop.Method;
import org.apache.olingo.server.api.uri.queryoption.apply.Compute;
import org.apache.olingo.server.api.uri.queryoption.apply.ComputeExpression;
import org.apache.olingo.server.api.uri.queryoption.apply.Concat;
import org.apache.olingo.server.api.uri.queryoption.apply.CustomFunction;
import org.apache.olingo.server.api.uri.queryoption.apply.Expand;
import org.apache.olingo.server.api.uri.queryoption.apply.Filter;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupBy;
import org.apache.olingo.server.api.uri.queryoption.apply.GroupByItem;
import org.apache.olingo.server.api.uri.queryoption.apply.Identity;
import org.apache.olingo.server.api.uri.queryoption.apply.Search;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.parser.search.SearchParserException;
import org.apache.olingo.server.core.uri.testutil.ExpandValidator;
import org.apache.olingo.server.core.uri.testutil.FilterValidator;
import org.apache.olingo.server.core.uri.testutil.ResourceValidator;
import org.apache.olingo.server.core.uri.testutil.TestUriValidator;
import org.apache.olingo.server.core.uri.testutil.TestValidator;
import org.apache.olingo.server.core.uri.validator.UriValidationException;
import org.apache.olingo.server.tecsvc.provider.ComplexTypeProvider;
import org.apache.olingo.server.tecsvc.provider.EdmTechProvider;
import org.apache.olingo.server.tecsvc.provider.EntityTypeProvider;
import org.apache.olingo.server.tecsvc.provider.FunctionProvider;
import org.apache.olingo.server.tecsvc.provider.PropertyProvider;
import org.junit.Test;

/** Tests of the $apply parser inspired by the ABNF test cases. */
public class ApplyParserTest {

  private static final OData odata = OData.newInstance();
  private static final Edm edm = odata.createServiceMetadata(
      new EdmTechProvider(), Collections.<EdmxReference> emptyList()).getEdm();

  @Test
  public void basic() throws Exception {
    parseEx("ESTwoKeyNav", "").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESAllPrim(0)/PropertyInt16", "identity")
        .isExValidation(UriValidationException.MessageKeys.SYSTEM_QUERY_OPTION_NOT_ALLOWED);
  }

  @Test
  public void aggregate() throws Exception {
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with sum as s)")
        .is(Aggregate.class)
        .goAggregate(0).isStandardMethod(StandardMethod.SUM).isAlias("s")
        .goExpression().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with min as m)")
        .goAggregate(0).isStandardMethod(StandardMethod.MIN).isAlias("m");
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with max as m)")
        .goAggregate(0).isStandardMethod(StandardMethod.MAX).isAlias("m");
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with average as a)")
        .goAggregate(0).isStandardMethod(StandardMethod.AVERAGE).isAlias("a");
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with countdistinct as c)")
        .goAggregate(0).isStandardMethod(StandardMethod.COUNT_DISTINCT).isAlias("c");
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with custom.aggregate as c)")
        .is(Aggregate.class)
        .goAggregate(0).isCustomMethod(new FullQualifiedName("custom", "aggregate")).isAlias("c");
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with min as min,PropertyInt16 with max as max)")
        .goAggregate(0).isStandardMethod(StandardMethod.MIN).isAlias("min").goUp()
        .goAggregate(1).isStandardMethod(StandardMethod.MAX).isAlias("max");

    parseEx("ESTwoKeyNav", "aggregate()")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 with sum)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 as s)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 with SUM as s)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyString with countdistinct as PropertyInt16)")
        .isExSemantic(UriParserSemanticException.MessageKeys.IS_PROPERTY);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 with min as m,PropertyInt16 with max as m)")
        .isExSemantic(UriParserSemanticException.MessageKeys.IS_PROPERTY);
  }

  @Test
  public void aggregateExpression() throws Exception {
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 mul PropertyComp/PropertyInt16 with sum as s)")
        .is(Aggregate.class)
        .goAggregate(0).isStandardMethod(StandardMethod.SUM)
        .goExpression().isBinary(BinaryOperatorKind.MUL)
        .left().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false)
        .goUpFilterValidator().root()
        .right().goPath().first().isComplexProperty("PropertyComp", ComplexTypeProvider.nameCTPrimComp, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    parse("ESTwoKeyNav",
        "aggregate(NavPropertyETKeyNavMany(PropertyInt16 mul NavPropertyETTwoKeyNavOne/PropertyInt16 with sum as s))")
        .goAggregate(0)
        .goInlineAggregateExpression().isStandardMethod(StandardMethod.SUM)
        .goUpAggregate()
        .goPath().first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    parseEx("ESTwoKeyNav", "aggregate((PropertyInt16 mul 2 with sum as s))")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void aggregateCount() throws Exception {
    parse("ESTwoKeyNav", "aggregate($count as count)")
        .is(Aggregate.class)
        .goAggregate(0).goPath().first().isCount();

    parseEx("ESTwoKeyNav", "aggregate($count)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate($count with sum as count)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void aggregateFrom() throws Exception {
    parse("ESTwoKeyNav", "aggregate(PropertyInt16 with sum as s from CollPropertyComp with average)")
        .goAggregate(0).isStandardMethod(StandardMethod.SUM)
        .goFrom(0).isStandardMethod(StandardMethod.AVERAGE)
        .goExpression().goPath().first()
        .isComplexProperty("CollPropertyComp", ComplexTypeProvider.nameCTPrimComp, true);
    parse("ESTwoKeyNav",
        "aggregate(PropertyInt16 with sum as s from CollPropertyComp with average from CollPropertyString with max)")
        .goAggregate(0).isStandardMethod(StandardMethod.SUM)
        .goFrom(0).isStandardMethod(StandardMethod.AVERAGE)
        .goUpAggregate().goFrom(1).isStandardMethod(StandardMethod.MAX);
    parse("ESTwoKeyNav", "aggregate(customAggregate as a from CollPropertyComp with average)")
        .goAggregate(0).goFrom(0).isStandardMethod(StandardMethod.AVERAGE);

    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 as a from CollPropertyComp with average)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "aggregate(PropertyInt16 with sum from CollPropertyComp with average)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void identity() throws Exception {
    parse("ESTwoKeyNav", "identity").is(Identity.class);

    parseEx("ESTwoKeyNav", "identity()")
        .isExSyntax(UriParserSyntaxException.MessageKeys.WRONG_VALUE_FOR_SYSTEM_QUERY_OPTION);
  }

  @Test
  public void compute() throws Exception {
    parse("ESTwoKeyNav", "compute(PropertyInt16 mul NavPropertyETKeyNavOne/PropertyInt16 as p)")
        .is(Compute.class)
        .goCompute(0).isAlias("p").goExpression().isBinary(BinaryOperatorKind.MUL)
        .left().isMember().goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "compute(PropertyInt16 mul 2 as p,day(now()) as d)")
        .goCompute(0).isAlias("p")
        .goUp().goCompute(1).isAlias("d")
        .goExpression().isMethod(MethodKind.DAY, 1).goParameter(0).isMethod(MethodKind.NOW, 0);

    parseEx("ESTwoKeyNav", "compute(PropertyInt16)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "compute(PropertyComp as c)")
        .isExSemantic(UriParserSemanticException.MessageKeys.ONLY_FOR_PRIMITIVE_TYPES);
  }

  @Test
  public void concat() throws Exception {
    parse("ESTwoKeyNav", "concat(topcount(2,PropertyInt16),bottomcount(2,PropertyInt16))")
        .is(Concat.class)
        .goConcat(0).goBottomTop().isMethod(Method.TOP_COUNT)
        .goUp().goUp()
        .goConcat(1).goBottomTop().isMethod(Method.BOTTOM_COUNT).goNumber().isLiteral("2");

    parseEx("ESTwoKeyNav", "concat(identity)").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void expand() throws Exception {
    parse("ESTwoKeyNav", "expand(NavPropertyETKeyNavMany,filter(PropertyInt16 gt 2))")
        .is(Expand.class).goExpand()
        .goPath().first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true)
        .goUpExpandValidator().goFilter().is("<<PropertyInt16> gt <2>>");
    parse("ESTwoKeyNav",
        "expand(NavPropertyETKeyNavMany,expand(NavPropertyETTwoKeyNavMany,filter(PropertyInt16 gt 2)))")
        .is(Expand.class).goExpand().goExpand().goFilter().is("<<PropertyInt16> gt <2>>");
    parse("ESTwoKeyNav",
        "expand(NavPropertyETKeyNavMany,expand(NavPropertyETTwoKeyNavMany,filter(PropertyInt16 gt 2)),"
        + "expand(NavPropertyETTwoKeyNavOne,expand(NavPropertyETKeyNavMany)))")
        .is(Expand.class).goExpand().goExpand().next().goExpand()
        .goPath().first().isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);

    parseEx("ESTwoKeyNav", "expand()")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
  }

  @Test
  public void search() throws Exception {
    parse("ESTwoKeyNav", "search(String)").isSearch("'String'");

    parseEx("ESTwoKeyNav", "search()")
        .isExceptionMessage(SearchParserException.MessageKeys.EXPECTED_DIFFERENT_TOKEN);
  }

  @Test
  public void filter() throws Exception {
    parse("ESTwoKeyNav", "filter(PropertyInt16 gt 3)")
        .is(Filter.class)
        .goFilter().isBinary(BinaryOperatorKind.GT)
        .left().isMember().goPath().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    parseEx("ESTwoKeyNav", "filter()").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void bottomTop() throws Exception {
    parse("ESTwoKeyNav", "topcount(2,PropertyInt16)")
        .goBottomTop().isMethod(Method.TOP_COUNT)
        .goNumber().isLiteralType(odata.createPrimitiveTypeInstance(EdmPrimitiveTypeKind.SByte))
        .isLiteral("2");
    parse("ESTwoKeyNav", "topsum(2,PropertyInt16)")
        .goBottomTop().isMethod(Method.TOP_SUM)
        .goValue().isMember().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "toppercent(2,PropertyInt16)").goBottomTop().isMethod(Method.TOP_PERCENT);

    parse("ESTwoKeyNav", "bottomcount(2,PropertyInt16)").goBottomTop().isMethod(Method.BOTTOM_COUNT);
    parse("ESTwoKeyNav", "bottomsum(2,PropertyInt16)").goBottomTop().isMethod(Method.BOTTOM_SUM);
    parse("ESTwoKeyNav", "bottompercent(2,PropertyInt16)").goBottomTop().isMethod(Method.BOTTOM_PERCENT);

    parseEx("ESTwoKeyNav", "bottompercent(1.2,PropertyInt16)")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPES_NOT_COMPATIBLE);
    parseEx("ESTwoKeyNav", "bottompercent(2,PropertyString)")
        .isExSemantic(UriParserSemanticException.MessageKeys.TYPES_NOT_COMPATIBLE);
  }

  @Test
  public void customFunction() throws Exception {
    parse("ESBaseTwoKeyNav", "Namespace1_Alias.BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isCustomFunction(FunctionProvider.nameBFCESBaseTwoKeyNavRTESBaseTwoKey);
    parse("ESKeyNav(1)/CollPropertyComp", "Namespace1_Alias.BFCCollCTPrimCompRTESAllPrim()")
        .isCustomFunction(FunctionProvider.nameBFCCollCTPrimCompRTESAllPrim);

    parseEx("ESBaseTwoKeyNav", "BFCESBaseTwoKeyNavRTESBaseTwoKey()")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESBaseTwoKeyNav", "Namespace1_Alias.BFCETBaseTwoKeyNavRTETTwoKeyNav()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);
    parseEx("ESBaseTwoKeyNav", "Namespace1_Alias.BFCCollStringRTESTwoKeyNav()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_NOT_FOUND);
    parseEx("ESTwoKeyNav", "Namespace1_Alias.BFCESTwoKeyNavRTTwoKeyNav()")
        .isExSemantic(UriParserSemanticException.MessageKeys.FUNCTION_MUST_USE_COLLECTIONS);
  }

  @Test
  public void groupBy() throws Exception {
    parse("ESTwoKeyNav", "groupby((PropertyString))")
        .is(GroupBy.class)
        .goGroupBy(0).goPath().first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
    parse("ESTwoKeyNav", "groupby((NavPropertyETKeyNavOne/PropertyInt16))")
        .is(GroupBy.class)
        .goGroupBy(0).goPath().first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "groupby((NavPropertyETKeyNavOne/PropertyInt16,PropertyString))")
        .is(GroupBy.class)
        .goGroupBy(1).goPath().first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
    parse("ESTwoKeyNav", "groupby((NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString))");
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString,PropertyString))")
        .goGroupBy(2).goPath().first().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    parse("ESTwoKeyNav", "groupby((Namespace1_Alias.ETBaseTwoKeyNav/NavPropertyETBaseTwoKeyNavOne/PropertyInt16))")
        .is(GroupBy.class)
        .goGroupBy(0).goPath().first().isType(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n().isNavProperty("NavPropertyETBaseTwoKeyNavOne", EntityTypeProvider.nameETBaseTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "groupby((NavPropertyETTwoKeyNavOne/Namespace1_Alias.ETBaseTwoKeyNav/PropertyInt16))")
        .is(GroupBy.class)
        .goGroupBy(0).goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .isTypeFilterOnCollection(EntityTypeProvider.nameETBaseTwoKeyNav)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);

    parseEx("ESTwoKeyNav", "groupby((wrongProperty))")
        .isExSemantic(UriParserSemanticException.MessageKeys.EXPRESSION_PROPERTY_NOT_IN_TYPE);
    parseEx("ESTwoKeyNav", "groupby((Namespace1_Alias.ETBaseTwoKeyNav))")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
    parseEx("ESTwoKeyNav", "groupby((NavPropertyETTwoKeyNavOne/Namespace1_Alias.ETBaseTwoKeyNav))")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void groupByAggregate() throws Exception {
    parse("ESTwoKeyNav", "groupby((PropertyInt16),aggregate(PropertyInt16 with sum as s))")
        .goGroupByOption().goAggregate(0).isStandardMethod(StandardMethod.SUM)
        .goExpression().goPath().first().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyInt16),aggregate(PropertyInt16 with average as a))")
        .goGroupByOption().goAggregate(0).isStandardMethod(StandardMethod.AVERAGE)
        .goUp().goUp().goGroupBy(0).goPath()
        .first().isNavProperty("NavPropertyETKeyNavOne", EntityTypeProvider.nameETKeyNav, false)
        .n().isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
    parse("ESTwoKeyNav", "groupby((NavPropertyETKeyNavOne/PropertyInt16),"
        + "aggregate(PropertyInt16 with sum as s,PropertyInt16 with average as a))")
        .goGroupByOption().goAggregate(1).isStandardMethod(StandardMethod.AVERAGE);
    parse("ESTwoKeyNav", "groupby((PropertyInt16),aggregate(NavPropertyETKeyNavMany/$count as c))")
        .goGroupByOption().goAggregate(0).goPath().at(1).isCount();
    parse("ESTwoKeyNav", "groupby((PropertyString),aggregate(NavPropertyETKeyNavMany(PropertyInt16 with sum as s)))")
        .goGroupByOption().goAggregate(0).goInlineAggregateExpression().isStandardMethod(StandardMethod.SUM)
        .goUpAggregate().goPath().first()
        .isNavProperty("NavPropertyETKeyNavMany", EntityTypeProvider.nameETKeyNav, true);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString),"
        + "aggregate(PropertyInt16 with sum as s))")
        .goGroupBy(1).goPath().at(1).isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString),"
        + "aggregate(PropertyInt16 with sum as s from NavPropertyETKeyNavOne/PropertyInt16 with average))")
        .goGroupByOption().goAggregate(0).goFrom(0).isStandardMethod(StandardMethod.AVERAGE);
    parse("ESTwoKeyNav", "groupby((NavPropertyETKeyNavOne),aggregate(CollPropertyComp(PropertyInt16 with sum as s)))");
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETTwoKeyNavOne/PropertyInt16),"
        + "topcount(2,PropertyInt16)/aggregate(PropertyInt16 with sum as s))")
        .goGroupByOption()
        .at(0).goBottomTop().isMethod(Method.TOP_COUNT)
        .goUp().at(1).goAggregate(0).isStandardMethod(StandardMethod.SUM);

    parseEx("ESTwoKeyNav", "groupby((PropertyInt16),identity,identity)")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void groupByRollUp() throws Exception {
    parse("ESTwoKeyNav",
        "groupby((rollup(NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString),"
        + "rollup(NavPropertyETKeyNavOne/NavPropertyETTwoKeyNavOne/PropertyInt16,"
        + "NavPropertyETTwoKeyNavOne/PropertyString),NavPropertyETTwoKeyNavOne/PropertyInt16),"
        + "aggregate(PropertyInt16 with sum as s))")
        .goGroupBy(1).goRollup(1).goPath()
        .first().isNavProperty("NavPropertyETTwoKeyNavOne", EntityTypeProvider.nameETTwoKeyNav, false)
        .n().isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    parse("ESTwoKeyNav",
        "groupby((rollup($all,NavPropertyETKeyNavOne/PropertyInt16,NavPropertyETKeyNavOne/PropertyString),"
        + "NavPropertyETTwoKeyNavOne/PropertyString),"
        + "aggregate(PropertyInt16 with sum as s from NavPropertyETTwoKeyNavOne/PropertyInt16 with average "
        + "from NavPropertyETTwoKeyNavOne/PropertyString with average))")
        .goGroupBy(0).isRollupAll().goUp().goGroupByOption().goAggregate(0).goFrom(1)
        .isStandardMethod(StandardMethod.AVERAGE).goExpression().goPath().at(1)
        .isPrimitiveProperty("PropertyString", PropertyProvider.nameString, false);

    parseEx("ESTwoKeyNav", "groupby((rollup($all)))")
        .isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void groupBySpecial() throws Exception {
    parse("ESTwoKeyNav", "groupby((NavPropertyETTwoKeyNavOne/PropertyInt16),aggregate(customAggregate))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(0)
        .goPath().first().isUriPathInfoKind(UriResourceKind.primitiveProperty);

    parse("ESTwoKeyNav",
        "groupby((PropertyString),aggregate(NavPropertyETKeyNavMany/$count as c,"
        + "NavPropertyETKeyNavMany(PropertyInt16 with sum as s)))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(0).isAlias("c").goPath().at(1).isCount();
    parse("ESTwoKeyNav",
        "groupby((PropertyString),aggregate(NavPropertyETKeyNavMany($count as c),"
        + "NavPropertyETKeyNavMany(PropertyInt16 with sum as s)))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(0).goInlineAggregateExpression()
        .isAlias("c").goPath().first().isCount();
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyString),"
        + "aggregate(PropertyInt16 with sum as s,customAggregate))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(1).isStandardMethod(null).isAlias(null)
        .goPath().first().isUriPathInfoKind(UriResourceKind.primitiveProperty);
    parse("ESTwoKeyNav",
        "groupby((PropertyString),aggregate(NavPropertyETKeyNavMany(PropertyInt16 with sum as s),"
        + "NavPropertyETKeyNavMany/customAggregate))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(1)
        .goPath().at(1).isUriPathInfoKind(UriResourceKind.primitiveProperty);
    parse("ESTwoKeyNav",
        "groupby((PropertyString),aggregate(NavPropertyETKeyNavMany(PropertyInt16 with sum as s),"
        + "NavPropertyETKeyNavMany(PropertyInt16 with average as a)))")
        .is(GroupBy.class)
        .goGroupByOption().goAggregate(0).goInlineAggregateExpression()
        .isStandardMethod(StandardMethod.SUM).isAlias("s")
        .goUpAggregate().goUp().goAggregate(1).goInlineAggregateExpression()
        .isStandardMethod(StandardMethod.AVERAGE).isAlias("a");
  }

  @Test
  public void sequence() throws Exception {
    parse("ESTwoKeyNav", "identity/identity/identity")
        .at(0).is(Identity.class).at(1).is(Identity.class).at(2).is(Identity.class);

    parse("ESTwoKeyNav", "filter(PropertyInt16 le 1)/aggregate(PropertyInt16 with sum as s)")
        .at(0).is(Filter.class)
        .at(1).is(Aggregate.class).goAggregate(0).isStandardMethod(StandardMethod.SUM).isAlias("s");
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne),aggregate(PropertyInt16 with sum as s))/"
        + "aggregate(s with average as a)")
        .at(1).goAggregate(0).isStandardMethod(StandardMethod.AVERAGE).isAlias("a");
    parse("ESTwoKeyNav",
        "filter(PropertyInt16 ge 1)/"
        + "groupby((NavPropertyETKeyNavOne/PropertyString),aggregate(PropertyInt16 with sum as s))")
        .at(0).is(Filter.class)
        .at(1).is(GroupBy.class);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyString),aggregate(PropertyInt16 with sum as s))/"
        + "filter(s ge 10)/concat(identity,groupby((NavPropertyETKeyNavOne/PropertyString),"
        + "aggregate(s with sum as t)))")
        .at(0).is(GroupBy.class)
        .at(1).is(Filter.class)
        .at(2).is(Concat.class).goConcat(0).is(Identity.class);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyString),aggregate(PropertyInt16 with sum as s))/"
        + "filter(s ge 10)/groupby((rollup(NavPropertyETKeyNavOne/PropertyString,"
        + "NavPropertyETKeyNavOne/PropertyCompAllPrim/PropertyDuration)),aggregate(s with sum as t))")
        .at(0).is(GroupBy.class)
        .at(1).is(Filter.class)
        .at(2).is(GroupBy.class).goGroupBy(0).goRollup(1).goPath().at(2)
        .isPrimitiveProperty("PropertyDuration", PropertyProvider.nameDuration, false);
    parse("ESTwoKeyNav",
        "groupby((NavPropertyETKeyNavOne/PropertyString),aggregate(PropertyInt16 with sum as s))/"
        + "concat(filter(s ge 10),groupby((NavPropertyETKeyNavOne/PropertyString),"
        + "aggregate(s with sum as t)))")
        .at(0).is(GroupBy.class)
        .at(1).is(Concat.class).goConcat(1).is(GroupBy.class);

    parse("ESTwoKeyNav",
        "filter(PropertyInt16 eq 1)/expand(NavPropertyETKeyNavMany,filter(not PropertyCompAllPrim/PropertyBoolean))/"
        + "groupby((NavPropertyETKeyNavOne/PropertyInt16),"
        + "aggregate(NavPropertyETKeyNavMany(PropertyInt16 with sum as s)))")
        .at(0).is(Filter.class)
        .at(1).is(Expand.class)
        .at(2).is(GroupBy.class);

    parseEx("ESTwoKeyNav", "identity/").isExSyntax(UriParserSyntaxException.MessageKeys.SYNTAX);
  }

  @Test
  public void otherQueryOptions() throws Exception {
    new TestUriValidator().setEdm(edm).run("ESTwoKeyNav",
        "$apply=aggregate(PropertyInt16 with sum as s)&$filter=s gt 3&$select=s")
        .goSelectItemPath(0).first().isPrimitiveProperty("s", PropertyProvider.nameDecimal, false)
        .goUpUriValidator()
        .goFilter().left().goPath().first().isPrimitiveProperty("s", PropertyProvider.nameDecimal, false);

    new FilterValidator().setEdm(edm).runUriOrderBy("ESTwoKeyNav",
        "$apply=aggregate(PropertyInt16 with sum as s)&$orderby=s")
        .goOrder(0).goPath().first().isPrimitiveProperty("s", PropertyProvider.nameDecimal, false);
  }

  @Test
  public void onCount() throws Exception {
    parse("ESTwoKeyNav/$count", "aggregate(PropertyInt16 with sum as s)")
        .goAggregate(0).isStandardMethod(StandardMethod.SUM).isAlias("s")
        .goExpression().goPath().first()
        .isPrimitiveProperty("PropertyInt16", PropertyProvider.nameInt16, false);
  }

  private ApplyValidator parse(final String path, final String apply)
      throws UriParserException, UriValidationException {
    final UriInfo uriInfo = new Parser(edm, odata).parseUri(path, "$apply=" + apply, null, null);
    return new ApplyValidator(uriInfo.getApplyOption());
  }

  private TestUriValidator parseEx(final String path, final String apply) {
    return new TestUriValidator().setEdm(edm).runEx(path, "$apply=" + apply);
  }

  private final class ApplyValidator implements TestValidator {

    private final ApplyOption applyOption;
    private final ApplyValidator previous;
    private ApplyItem applyItem;

    protected ApplyValidator(final ApplyOption applyOption) {
      this(applyOption, null);
    }

    private ApplyValidator(final ApplyOption applyOption, final ApplyValidator previous) {
      this.applyOption = applyOption;
      this.previous = previous;
      at(0);
    }

    public ApplyValidator at(final int index) {
      assertTrue(index < applyOption.getApplyItems().size());
      applyItem = applyOption.getApplyItems().get(index);
      return this;
    }

    public ApplyValidator is(final Class<? extends ApplyItem> cls) {
      assertNotNull(applyItem);
      assertTrue(cls.isAssignableFrom(applyItem.getClass()));
      return this;
    }

    public AggregateValidator goAggregate(final int index) {
      is(Aggregate.class);
      assertTrue(index < ((Aggregate) applyItem).getExpressions().size());
      return new AggregateValidator(((Aggregate) applyItem).getExpressions().get(index), this);
    }

    public ExpandValidator goExpand() {
      is(Expand.class);
      return new ExpandValidator().setUpValidator(this).setExpand(((Expand) applyItem).getExpandOption());
    }

    public FilterValidator goFilter() {
      is(Filter.class);
      return new FilterValidator().setFilter(((Filter) applyItem).getFilterOption());
    }

    public BottomTopValidator goBottomTop() {
      is(BottomTop.class);
      return new BottomTopValidator((BottomTop) applyItem, this);
    }

    public ApplyValidator isCustomFunction(final FullQualifiedName function) {
      is(CustomFunction.class);
      assertEquals(function, ((CustomFunction) applyItem).getFunction().getFullQualifiedName());
      return this;
    }

    public ApplyValidator isSearch(final String serializedSearch) {
      is(Search.class);
      assertEquals(serializedSearch, ((Search) applyItem).getSearchOption().getSearchExpression().toString());
      return this;
    }

    public ApplyValidator goConcat(final int index) {
      is(Concat.class);
      assertTrue(index < ((Concat) applyItem).getApplyOptions().size());
      return new ApplyValidator(((Concat) applyItem).getApplyOptions().get(index), this);
    }

    public ComputeValidator goCompute(final int index) {
      is(Compute.class);
      assertTrue(index < ((Compute) applyItem).getExpressions().size());
      return new ComputeValidator(((Compute) applyItem).getExpressions().get(index), this);
    }

    public GroupByValidator goGroupBy(final int index) {
      is(GroupBy.class);
      assertTrue(index < ((GroupBy) applyItem).getGroupByItems().size());
      return new GroupByValidator(((GroupBy) applyItem).getGroupByItems().get(index), this);
    }

    public ApplyValidator goGroupByOption() {
      is(GroupBy.class);
      assertNotNull(((GroupBy) applyItem).getApplyOption());
      return new ApplyValidator(((GroupBy) applyItem).getApplyOption(), this);
    }

    public ApplyValidator goUp() {
      return previous;
    }
  }

  private final class AggregateValidator implements TestValidator {

    private final AggregateExpression aggregateExpression;
    private final TestValidator previous;

    protected AggregateValidator(final AggregateExpression aggregateExpression, final TestValidator previous) {
      this.aggregateExpression = aggregateExpression;
      this.previous = previous;
    }

    public AggregateValidator isStandardMethod(final AggregateExpression.StandardMethod method) {
      assertNotNull(aggregateExpression);
      assertEquals(method, aggregateExpression.getStandardMethod());
      return this;
    }

    public AggregateValidator isCustomMethod(final FullQualifiedName method) {
      assertNotNull(aggregateExpression);
      assertEquals(method, aggregateExpression.getCustomMethod());
      return this;
    }

    public AggregateValidator isAlias(final String alias) {
      assertNotNull(aggregateExpression);
      assertEquals(alias, aggregateExpression.getAlias());
      return this;
    }

    public FilterValidator goExpression() {
      assertNotNull(aggregateExpression);
      assertNotNull(aggregateExpression.getExpression());
      return new FilterValidator().setValidator(this).setEdm(edm)
          .setExpression(aggregateExpression.getExpression());
    }

    public ResourceValidator goPath() {
      assertNotNull(aggregateExpression);
      assertFalse(aggregateExpression.getPath().isEmpty());
      UriInfoImpl resource = new UriInfoImpl().setKind(UriInfoKind.resource);
      for (final UriResource segment : aggregateExpression.getPath()) {
        resource.addResourcePart(segment);
      }
      return new ResourceValidator().setUpValidator(this).setEdm(edm).setUriInfoPath(resource);
    }

    public AggregateValidator goInlineAggregateExpression() {
      return new AggregateValidator(aggregateExpression.getInlineAggregateExpression(), this);
    }

    public AggregateValidator goFrom(final int index) {
      assertTrue(index < aggregateExpression.getFrom().size());
      return new AggregateValidator(aggregateExpression.getFrom().get(index), this);
    }

    public AggregateValidator goUpAggregate() {
      return (AggregateValidator) previous;
    }

    public ApplyValidator goUp() {
      return (ApplyValidator) previous;
    }
  }

  private final class BottomTopValidator implements TestValidator {

    private final BottomTop item;
    private final ApplyValidator previous;

    private BottomTopValidator(final BottomTop item, final ApplyValidator previous) {
      this.item = item;
      this.previous = previous;
    }

    public BottomTopValidator isMethod(final BottomTop.Method method) {
      assertEquals(method, item.getMethod());
      return this;
    }

    public FilterValidator goNumber() {
      assertNotNull(item.getNumber());
      return new FilterValidator().setValidator(this).setEdm(edm).setExpression(item.getNumber());
    }

    public FilterValidator goValue() {
      assertNotNull(item.getValue());
      return new FilterValidator().setValidator(this).setEdm(edm).setExpression(item.getValue());
    }

    public ApplyValidator goUp() {
      return previous;
    }
  }

  private final class ComputeValidator implements TestValidator {

    private final ComputeExpression item;
    private final ApplyValidator previous;

    private ComputeValidator(final ComputeExpression item, final ApplyValidator previous) {
      this.item = item;
      this.previous = previous;
    }

    public ComputeValidator isAlias(final String alias) {
      assertEquals(alias, item.getAlias());
      return this;
    }

    public FilterValidator goExpression() {
      assertNotNull(item.getExpression());
      return new FilterValidator().setValidator(this).setEdm(edm).setExpression(item.getExpression());
    }

    public ApplyValidator goUp() {
      return previous;
    }
  }

  private final class GroupByValidator implements TestValidator {

    private final GroupByItem item;
    private final TestValidator previous;

    private GroupByValidator(final GroupByItem item, final TestValidator previous) {
      this.item = item;
      this.previous = previous;
    }

    public ResourceValidator goPath() {
      assertFalse(item.getPath().isEmpty());
      UriInfoImpl resource = new UriInfoImpl().setKind(UriInfoKind.resource);
      for (final UriResource segment : item.getPath()) {
        resource.addResourcePart(segment);
      }
      return new ResourceValidator().setUpValidator(this).setEdm(edm).setUriInfoPath(resource);
    }

    public GroupByValidator isRollupAll() {
      assertTrue(item.isRollupAll());
      return this;
    }

    public GroupByValidator goRollup(final int index) {
      assertTrue(index < item.getRollup().size());
      return new GroupByValidator(item.getRollup().get(index), this);
    }

    public ApplyValidator goUp() {
      return (ApplyValidator) previous;
    }
  }
}
