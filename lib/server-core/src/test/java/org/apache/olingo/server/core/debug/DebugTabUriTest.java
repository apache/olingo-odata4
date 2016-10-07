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
package org.apache.olingo.server.core.debug;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmFunction;
import org.apache.olingo.commons.api.edm.EdmFunctionImport;
import org.apache.olingo.commons.api.edm.EdmNavigationProperty;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmReturnType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.core.edm.primitivetype.EdmPrimitiveTypeFactory;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.apply.AggregateExpression.StandardMethod;
import org.apache.olingo.server.api.uri.queryoption.expression.BinaryOperatorKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.MethodKind;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.UriParameterImpl;
import org.apache.olingo.server.core.uri.UriResourceEntitySetImpl;
import org.apache.olingo.server.core.uri.UriResourceFunctionImpl;
import org.apache.olingo.server.core.uri.UriResourceNavigationPropertyImpl;
import org.apache.olingo.server.core.uri.UriResourcePrimitivePropertyImpl;
import org.apache.olingo.server.core.uri.parser.search.SearchTermImpl;
import org.apache.olingo.server.core.uri.queryoption.AliasQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ApplyOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CountOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandItemImpl;
import org.apache.olingo.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.LevelsOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByItemImpl;
import org.apache.olingo.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SearchOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectItemImpl;
import org.apache.olingo.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SkipTokenOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.TopOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.AggregateExpressionImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.AggregateImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.GroupByImpl;
import org.apache.olingo.server.core.uri.queryoption.apply.GroupByItemImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.BinaryImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MemberImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.MethodImpl;
import org.junit.Test;

public class DebugTabUriTest extends AbstractDebugTabTest {

  @Test
  public void resourceEntitySet() throws Exception {
    EdmEntitySet edmEntitySet = mock(EdmEntitySet.class);
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.resource)
        .addResourcePart(new UriResourceEntitySetImpl(edmEntitySet)));

    assertEquals("{\"kind\":\"resource\",\"uriResourceParts\":["
        + "{\"uriResourceKind\":\"entitySet\",\"segment\":null,\"isCollection\":true}]}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Resource Path</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("uriResourceKind"), containsString("entitySet"),
        containsString("segment"), containsString("null")));
    assertThat(html, allOf(
        containsString("isCollection"), containsString("true"),
        endsWith("</li>\n</ul>\n")));
  }

  @Test
  public void resourceFunction() throws Exception {
    EdmReturnType returnType = mock(EdmReturnType.class);
    EdmFunction edmFunction = mock(EdmFunction.class);
    when(edmFunction.getReturnType()).thenReturn(returnType);
    EdmFunctionImport edmFunctionImport = mock(EdmFunctionImport.class);
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.resource)
        .addResourcePart(new UriResourceFunctionImpl(edmFunctionImport, edmFunction,
            Arrays.asList((UriParameter) new UriParameterImpl().setName("parameter1")))));

    assertEquals("{\"kind\":\"resource\",\"uriResourceParts\":["
        + "{\"uriResourceKind\":\"function\",\"segment\":null,\"isCollection\":false,"
        + "\"parameters\":{\"parameter1\":null}}]}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Resource Path</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("uriResourceKind"), containsString("function"),
        containsString("segment"), containsString("null")));
    assertThat(html, allOf(
        containsString("isCollection"), containsString("false"),
        containsString("parameters"), containsString("parameter1"),
        endsWith("</li>\n</ul>\n")));
  }

  @Test
  public void crossjoin() throws Exception {
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.crossjoin)
        .addEntitySetName("ES1").addEntitySetName("ES2"));

    assertEquals("{\"kind\":\"crossjoin\",\"entitySetNames\":[\"ES1\",\"ES2\"]}", createJson(tab));

    assertEquals("<h2>Crossjoin EntitySet Names</h2>\n"
        + "<ul>\n"
        + "<li>ES1</li>\n"
        + "<li>ES2</li>\n"
        + "</ul>\n",
        createHtml(tab));
  }

  @Test
  public void entityId() throws Exception {
    EdmEntityType edmEntityType = mock(EdmEntityType.class);
    when(edmEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("ns", "entityType"));
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.entityId)
        .setEntityTypeCast(edmEntityType));

    assertEquals("{\"kind\":\"entityId\",\"typeCast\":\"ns.entityType\"}", createJson(tab));

    assertEquals("<h2>Kind</h2>\n"
        + "<p>entityId</p>\n"
        + "<h2>Type Cast</h2>\n"
        + "<p>ns.entityType</p>\n",
        createHtml(tab));
  }

  @Test
  public void simpleQueryOptions() throws Exception {
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setQueryOption(new FormatOptionImpl().setFormat("json"))
        .setQueryOption(new IdOptionImpl().setValue("ES(42)").setText("ES(42)"))
        .setQueryOption(new SkipOptionImpl().setValue(123).setText("123"))
        .setQueryOption(new TopOptionImpl().setValue(456).setText("456"))
        .setQueryOption(new SkipTokenOptionImpl().setValue("xyz123"))
        .setQueryOption(new CountOptionImpl().setValue(false).setText("false"))
        .setQueryOption(new SearchOptionImpl().setSearchExpression(new SearchTermImpl("searchTest")))
        .setQueryOption(new CustomQueryOptionImpl().setName("customQuery").setText("customValue"))
        .setQueryOption(new AliasQueryOptionImpl().setAliasValue(null).setName("@alias")));

    assertEquals("{\"kind\":\"all\","
        + "\"format\":\"json\","
        + "\"id\":\"ES(42)\","
        + "\"skiptoken\":\"xyz123\","
        + "\"isCount\":false,"
        + "\"skip\":123,"
        + "\"top\":456,"
        + "\"search\":{\"nodeType\":\"searchTerm\",\"searchTerm\":\"searchTest\"},"
        + "\"aliases\":{\"@alias\":null},"
        + "\"customQueryOptions\":{\"customQuery\":\"customValue\"}"
        + "}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Kind</h2>\n"
            + "<p>all</p>\n"),
        containsString("<h2>Search Option</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("searchTerm"), containsString("searchTest"),
        containsString("<h2>Unstructured System Query Options</h2>\n"
            + "<table>\n"
            + "<thead>\n"
            + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
            + "</thead>\n"
            + "<tbody>\n"
            + "<tr><td class=\"name\">$count</td><td class=\"value\">false</td></tr>\n"
            + "<tr><td class=\"name\">$skip</td><td class=\"value\">123</td></tr>\n"
            + "<tr><td class=\"name\">$skiptoken</td><td class=\"value\">xyz123</td></tr>\n"
            + "<tr><td class=\"name\">$top</td><td class=\"value\">456</td></tr>\n"
            + "<tr><td class=\"name\">$format</td><td class=\"value\">json</td></tr>\n"
            + "<tr><td class=\"name\">$id</td><td class=\"value\">ES(42)</td></tr>\n"
            + "</tbody>\n"
            + "</table>\n")));
    assertThat(html, allOf(
        containsString("<h2>Aliases</h2>\n"
            + "<table>\n"
            + "<thead>\n"
            + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
            + "</thead>\n"
            + "<tbody>\n"
            + "<tr><td class=\"name\">@alias</td><td class=\"value\">null</td></tr>\n"
            + "</tbody>\n"
            + "</table>\n"),
        endsWith("<h2>Custom Query Options</h2>\n"
            + "<table>\n"
            + "<thead>\n"
            + "<tr><th class=\"name\">Name</th><th class=\"value\">Value</th></tr>\n"
            + "</thead>\n"
            + "<tbody>\n"
            + "<tr><td class=\"name\">customQuery</td><td class=\"value\">customValue</td></tr>\n"
            + "</tbody>\n"
            + "</table>\n")));
  }

  @Test
  public void select() throws Exception {
    EdmProperty edmProperty = mock(EdmProperty.class);
    when(edmProperty.getName()).thenReturn("property");
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setSystemQueryOption(new SelectOptionImpl().setSelectItems(Arrays.asList(
            (SelectItem) new SelectItemImpl().setStar(true),
            new SelectItemImpl().setResourcePath(
                new UriInfoImpl().setKind(UriInfoKind.resource)
                    .addResourcePart(new UriResourcePrimitivePropertyImpl(edmProperty)))))));
    assertEquals("{\"kind\":\"all\",\"select\":[\"*\",\"property\"]}", createJson(tab));

    assertEquals("<h2>Kind</h2>\n"
        + "<p>all</p>\n"
        + "<h2>Selected Properties</h2>\n"
        + "<ul>\n"
        + "<li>*</li>\n"
        + "<li>property</li>\n"
        + "</ul>\n",
        createHtml(tab));
  }

  @Test
  public void expand() throws Exception {
    EdmNavigationProperty edmProperty = mock(EdmNavigationProperty.class);
    when(edmProperty.getName()).thenReturn("property");
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setSystemQueryOption(new ExpandOptionImpl().addExpandItem(
            new ExpandItemImpl().setResourcePath(
                new UriInfoImpl().setKind(UriInfoKind.resource)
                    .addResourcePart(new UriResourceNavigationPropertyImpl(edmProperty)))
                .setSystemQueryOption(new LevelsOptionImpl().setValue(1)))));
    assertEquals("{\"kind\":\"all\",\"expand\":[{\"expandPath\":["
        + "{\"uriResourceKind\":\"navigationProperty\",\"segment\":\"property\",\"isCollection\":false}],"
        + "\"levels\":1}]}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Kind</h2>\n"
            + "<p>all</p>\n"
            + "<h2>Expand Option</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("navigationProperty"), containsString("property"),
        containsString("isCollection"), containsString("false")));
    assertThat(html, allOf(containsString("levels"), endsWith("</li>\n</ul>\n")));
  }

  @Test
  public void filter() throws Exception {
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setSystemQueryOption(new FilterOptionImpl().setExpression(
            new BinaryImpl(new LiteralImpl("1", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Int64)),
                BinaryOperatorKind.GT,
                new LiteralImpl("2", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.SByte)),
                EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)))));
    assertEquals("{\"kind\":\"all\",\"filter\":{"
        + "\"nodeType\":\"binary\",\"operator\":\"gt\",\"type\":\"Boolean\","
        + "\"left\":{\"nodeType\":\"literal\",\"type\":\"Edm.Int64\",\"value\":\"1\"},"
        + "\"right\":{\"nodeType\":\"literal\",\"type\":\"Edm.SByte\",\"value\":\"2\"}}}",
        createJson(tab));

    assertThat(createHtml(tab), allOf(
        startsWith("<h2>Kind</h2>\n"
            + "<p>all</p>\n"
            + "<h2>Filter Option</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("nodeType"), containsString("binary"),
        containsString("operator"), containsString("gt")));
    assertThat(createHtml(tab), allOf(
        containsString("literal"), containsString("Int64"),
        containsString("SByte"), containsString("2"),
        endsWith("</li>\n</ul>\n")));

    assertEquals("{\"kind\":\"all\",\"filter\":{"
        + "\"nodeType\":\"method\",\"operator\":\"ceiling\",\"type\":\"Number\","
        + "\"parameters\":[{\"nodeType\":\"literal\",\"type\":\"Edm.Decimal\",\"value\":\"1.5\"}]}}",
        createJson(new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
            .setSystemQueryOption(new FilterOptionImpl().setExpression(
                new MethodImpl(MethodKind.CEILING, Arrays.asList(
                    (Expression) new LiteralImpl("1.5",
                        EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Decimal)))))))));

    EdmEntityType edmEntityType = mock(EdmEntityType.class);
    when(edmEntityType.getFullQualifiedName()).thenReturn(new FullQualifiedName("ns", "entityType"));
    EdmProperty edmProperty = mock(EdmProperty.class);
    when(edmProperty.getName()).thenReturn("property");
    assertEquals("{\"kind\":\"all\",\"filter\":{"
        + "\"nodeType\":\"member\",\"type\":\"unknown\",\"typeFilter\":\"ns.entityType\","
        + "\"resourceSegments\":[{\"nodeType\":\"primitiveProperty\",\"name\":\"property\",\"type\":\"unknown\"}]}}",
        createJson(new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
            .setSystemQueryOption(new FilterOptionImpl().setExpression(
                new MemberImpl(new UriInfoImpl().setKind(UriInfoKind.resource)
                    .addResourcePart(new UriResourcePrimitivePropertyImpl(edmProperty)),
                    edmEntityType))))));
  }

  @Test
  public void orderby() throws Exception {
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setSystemQueryOption(new OrderByOptionImpl().addOrder(
            new OrderByItemImpl().setExpression(
                new LiteralImpl("false", EdmPrimitiveTypeFactory.getInstance(EdmPrimitiveTypeKind.Boolean)))
                .setDescending(true))));

    assertEquals("{\"kind\":\"all\",\"orderby\":{\"nodeType\":\"orderCollection\",\"orders\":["
        + "{\"nodeType\":\"order\",\"sortorder\":\"desc\",\"expression\":"
        + "{\"nodeType\":\"literal\",\"type\":\"Edm.Boolean\",\"value\":\"false\"}}]}}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Kind</h2>\n"
            + "<p>all</p>\n"
            + "<h2>OrderBy Option</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("nodeType"), containsString("order"),
        containsString("sortorder"), containsString("desc")));
    assertThat(html, allOf(
        containsString("expression"), containsString("literal"),
        containsString("Edm.Boolean"), containsString("false"),
        endsWith("</li>\n</ul>\n")));
  }

  @Test
  public void apply() throws Exception {
    EdmProperty edmProperty = mock(EdmProperty.class);
    when(edmProperty.getName()).thenReturn("property");
    final DebugTabUri tab = new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
        .setSystemQueryOption(new ApplyOptionImpl().add(new AggregateImpl().addExpression(
            new AggregateExpressionImpl()
                .setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
                    new UriResourcePrimitivePropertyImpl(edmProperty)))
                .setStandardMethod(StandardMethod.AVERAGE)
                .setAlias("average")))));

    assertEquals("{\"kind\":\"all\",\"apply\":[{\"kind\":\"AGGREGATE\",\"aggregate\":[{"
        + "\"path\":[{\"uriResourceKind\":\"primitiveProperty\",\"segment\":\"property\",\"isCollection\":false}],"
        + "\"standardMethod\":\"AVERAGE\",\"as\":\"average\"}]}]}",
        createJson(tab));

    final String html = createHtml(tab);
    assertThat(html, allOf(
        startsWith("<h2>Kind</h2>\n"
            + "<p>all</p>\n"
            + "<h2>Apply Option</h2>\n"
            + "<ul>\n"
            + "<li class=\"json\">"),
        containsString("kind"), containsString("AGGREGATE"),
        containsString("aggregate"), containsString("path")));
    assertThat(html, allOf(
        containsString("primitiveProperty"), containsString("property"),
        containsString("standardMethod"), containsString("AVERAGE")));
    assertThat(html, allOf(
        containsString("as"), containsString("average"),
        endsWith("</li>\n</ul>\n")));

    assertEquals("{\"kind\":\"all\",\"apply\":[{\"kind\":\"GROUP_BY\",\"groupBy\":[{"
        + "\"path\":[{\"uriResourceKind\":\"primitiveProperty\",\"segment\":\"property\",\"isCollection\":false}],"
        + "\"isRollupAll\":true}]}]}",
        createJson(new DebugTabUri(new UriInfoImpl().setKind(UriInfoKind.all)
            .setSystemQueryOption(new ApplyOptionImpl().add(new GroupByImpl().addGroupByItem(
                new GroupByItemImpl().setIsRollupAll()
                    .setPath(new UriInfoImpl().setKind(UriInfoKind.resource).addResourcePart(
                        new UriResourcePrimitivePropertyImpl(edmProperty)))))))));
  }
}
