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
package org.apache.olingo.server.core.uri.queryoption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.core.uri.UriInfoImpl;
import org.apache.olingo.server.core.uri.parser.search.SearchTermImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.server.core.uri.queryoption.expression.LiteralImpl;
import org.junit.Test;

public class QueryOptionTest {

  @Test
  public void testAliasQueryOption() {
    AliasQueryOptionImpl option = new AliasQueryOptionImpl();

    Expression expression = new LiteralImpl(null, null);

    option.setAliasValue(expression);
    assertEquals(expression, option.getValue());
  }

  @Test
  public void testExpandItemImpl() {
    ExpandItemImpl option = new ExpandItemImpl();

    // input options
    ExpandOptionImpl expand = new ExpandOptionImpl();
    FilterOptionImpl filter = new FilterOptionImpl();
    CountOptionImpl inlinecount = new CountOptionImpl();
    OrderByOptionImpl orderby = new OrderByOptionImpl();
    SearchOptionImpl search = new SearchOptionImpl();
    SelectOptionImpl select = new SelectOptionImpl();
    SkipOptionImpl skip = new SkipOptionImpl();
    TopOptionImpl top = new TopOptionImpl();
    LevelsOptionImpl levels = new LevelsOptionImpl();

    option.setSystemQueryOption(expand);
    option.setSystemQueryOption(filter);
    option.setSystemQueryOption(inlinecount);
    option.setSystemQueryOption(orderby);
    option.setSystemQueryOption(search);
    option.setSystemQueryOption(select);
    option.setSystemQueryOption(skip);
    option.setSystemQueryOption(top);
    option.setSystemQueryOption(levels);

    assertEquals(expand, option.getExpandOption());
    assertEquals(filter, option.getFilterOption());
    assertEquals(inlinecount, option.getCountOption());
    assertEquals(orderby, option.getOrderByOption());
    assertEquals(search, option.getSearchOption());
    assertEquals(select, option.getSelectOption());
    assertEquals(skip, option.getSkipOption());
    assertEquals(top, option.getTopOption());
    assertEquals(levels, option.getLevelsOption());

    // just for completeness
    option = new ExpandItemImpl();
    option.setSystemQueryOption(new IdOptionImpl());

    option = new ExpandItemImpl();
    List<SystemQueryOption> list = new ArrayList<SystemQueryOption>();
    list.add(expand);
    list.add(filter);
    option.setSystemQueryOptions(list);
    assertEquals(expand, option.getExpandOption());
    assertEquals(filter, option.getFilterOption());

    option = new ExpandItemImpl();
    assertFalse(option.isRef());
    option.setIsRef(true);
    assertTrue(option.isRef());

    option = new ExpandItemImpl();
    assertFalse(option.isStar());
    option.setIsStar(true);
    assertTrue(option.isStar());

    option = new ExpandItemImpl();
    UriInfoResource resource = new UriInfoImpl().asUriInfoResource();
    option.setResourcePath(resource);
    assertEquals(resource, option.getResourcePath());

  }

  @Test
  public void testExpandOptionImpl() {
    ExpandOptionImpl option = new ExpandOptionImpl();
    assertEquals(SystemQueryOptionKind.EXPAND, option.getKind());

    ExpandItemImpl item1 = new ExpandItemImpl();
    ExpandItemImpl item2 = new ExpandItemImpl();
    option.addExpandItem(item1);
    option.addExpandItem(item2);
    assertEquals(item1, option.getExpandItems().get(0));
    assertEquals(item2, option.getExpandItems().get(1));
  }

  @Test
  public void testFilterOptionImpl() {
    FilterOptionImpl option = new FilterOptionImpl();
    assertEquals(SystemQueryOptionKind.FILTER, option.getKind());

    AliasImpl expression = new AliasImpl(null, null);

    option.setExpression(expression);
    assertEquals(expression, option.getExpression());
  }

  @Test
  public void testFormatOptionImpl() {
    FormatOptionImpl option = new FormatOptionImpl();
    assertEquals(SystemQueryOptionKind.FORMAT, option.getKind());

    option.setFormat("A");
    assertEquals("A", option.getFormat());
  }

  @Test
  public void testIdOptionImpl() {
    IdOptionImpl option = new IdOptionImpl();
    assertEquals(SystemQueryOptionKind.ID, option.getKind());

    option.setValue("A");
    assertEquals("A", option.getValue());
  }

  @Test
  public void testInlineCountImpl() {
    CountOptionImpl option = new CountOptionImpl();
    assertEquals(SystemQueryOptionKind.COUNT, option.getKind());

    assertFalse(option.getValue());
    option.setValue(true);
    assertTrue(option.getValue());
  }

  @Test
  public void testLevelsExpandOptionImpl() {
    LevelsOptionImpl option = new LevelsOptionImpl();
    assertEquals(SystemQueryOptionKind.LEVELS, option.getKind());

    assertEquals(0, option.getValue());
    option.setValue(1);
    assertEquals(1, option.getValue());

    option = new LevelsOptionImpl();
    option.setMax();
    assertTrue(option.isMax());
  }

  @Test
  public void testOrderByItemImpl() {
    OrderByItemImpl option = new OrderByItemImpl();

    AliasImpl expression = new AliasImpl(null, null);
    option.setExpression(expression);
    assertEquals(expression, option.getExpression());

    assertFalse(option.isDescending());
    option.setDescending(true);
    assertTrue(option.isDescending());
  }

  @Test
  public void testOrderByOptionImpl() {
    OrderByOptionImpl option = new OrderByOptionImpl();
    assertEquals(SystemQueryOptionKind.ORDERBY, option.getKind());

    OrderByItemImpl order0 = new OrderByItemImpl();
    OrderByItemImpl order1 = new OrderByItemImpl();
    option.addOrder(order0);
    option.addOrder(order1);

    assertEquals(order0, option.getOrders().get(0));
    assertEquals(order1, option.getOrders().get(1));
  }

  @Test
  public void testQueryOptionImpl() {
    QueryOptionImpl option = new AliasQueryOptionImpl();

    option.setName("A");
    option.setText("B");
    assertEquals("A", option.getName());
    assertEquals("B", option.getText());
  }

  @Test
  public void searchOptionImpl() {
    SearchOptionImpl option = new SearchOptionImpl();
    assertEquals(SystemQueryOptionKind.SEARCH, option.getKind());

    final SearchTermImpl searchExpression = new SearchTermImpl("A");
    option.setSearchExpression(searchExpression);
    assertEquals(searchExpression, option.getSearchExpression());
  }

  @Test
  public void selectItemImpl() {
    SelectItemImpl option = new SelectItemImpl();

    // no typed collection else case ( e.g. if not path is added)
    option = new SelectItemImpl();

    option = new SelectItemImpl();
    assertFalse(option.isStar());
    option.setStar(true);
    assertTrue(option.isStar());

    option = new SelectItemImpl();
    assertFalse(option.isAllOperationsInSchema());
    FullQualifiedName fqName = new FullQualifiedName("Namespace", "Name");
    option.addAllOperationsInSchema(fqName);
    assertTrue(option.isAllOperationsInSchema());
    assertEquals(fqName, option.getAllOperationsInSchemaNameSpace());
  }

  @Test
  public void selectOptionImpl() {
    SelectOptionImpl option = new SelectOptionImpl();
    assertEquals(SystemQueryOptionKind.SELECT, option.getKind());

    SelectItem item0 = new SelectItemImpl();
    SelectItem item1 = new SelectItemImpl();
    option.setSelectItems(Arrays.asList(item0, item1));

    assertEquals(item0, option.getSelectItems().get(0));
    assertEquals(item1, option.getSelectItems().get(1));
  }

  @Test
  public void testSkipOptionImpl() {
    SkipOptionImpl option = new SkipOptionImpl();
    assertEquals(SystemQueryOptionKind.SKIP, option.getKind());

    option.setValue(10);
    assertEquals(10, option.getValue());
  }

  @Test
  public void testSkipTokenOptionImpl() {
    SkipTokenOptionImpl option = new SkipTokenOptionImpl();
    assertEquals(SystemQueryOptionKind.SKIPTOKEN, option.getKind());

    option.setValue("A");
    assertEquals("A", option.getValue());
  }

  @Test
  public void testSystemQueryOptionImpl() {
    SystemQueryOptionImpl option = new ExpandOptionImpl();
    assertEquals(SystemQueryOptionKind.EXPAND, option.getKind());
    assertEquals("$expand", option.getName());
  }

  @Test
  public void testTopOptionImpl() {
    TopOptionImpl option = new TopOptionImpl();
    assertEquals(SystemQueryOptionKind.TOP, option.getKind());

    option.setValue(11);
    assertEquals(11, option.getValue());
  }
}
