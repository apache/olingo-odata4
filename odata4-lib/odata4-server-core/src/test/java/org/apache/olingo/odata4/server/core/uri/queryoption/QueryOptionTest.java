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
package org.apache.olingo.odata4.server.core.uri.queryoption;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.commons.api.edm.FullQualifiedName;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.queryoption.SupportedQueryOptions;
import org.apache.olingo.odata4.server.core.edm.provider.EdmProviderImpl;
import org.apache.olingo.odata4.server.core.testutil.EdmTechTestProvider;
import org.apache.olingo.odata4.server.core.uri.apiimpl.UriInfoImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.AliasImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.ExpressionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.LiteralImpl;
import org.junit.Test;

//TOOD add getKind check to all
public class QueryOptionTest {

  Edm edm = new EdmProviderImpl(new EdmTechTestProvider());

  @Test
  public void testAliasQueryOption() {
    AliasQueryOptionImpl option = new AliasQueryOptionImpl();

    ExpressionImpl expression = new LiteralImpl();

    option.setAliasValue(expression);
    assertEquals(expression, option.getValue());
  }

  @Test
  public void testExandItemImpl() {
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
    assertEquals(inlinecount, option.getInlineCountOption());
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
    List<SystemQueryOptionImpl> list = new ArrayList<SystemQueryOptionImpl>();
    list.add(expand);
    list.add(filter);
    option.setSystemQueryOptions(list);
    assertEquals(expand, option.getExpandOption());
    assertEquals(filter, option.getFilterOption());

    option = new ExpandItemImpl();
    assertEquals(false, option.isRef());
    option.setIsRef(true);
    assertEquals(true, option.isRef());

    option = new ExpandItemImpl();
    assertEquals(false, option.isStar());
    option.setIsStar(true);
    assertEquals(true, option.isStar());

    option = new ExpandItemImpl();
    UriInfoResource resource = new UriInfoImpl().asUriInfoResource();
    option.setResourceInfo(resource);
    assertEquals(resource, option.getResourceInfo());

  }

  @Test
  public void testExpandOptionImpl() {
    ExpandOptionImpl option = new ExpandOptionImpl();
    assertEquals(SupportedQueryOptions.EXPAND, option.getKind());

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
    assertEquals(SupportedQueryOptions.FILTER, option.getKind());

    AliasImpl expression = new AliasImpl();

    option.setExpression(expression);
    assertEquals(expression, option.getExpression());
  }

  @Test
  public void testFormatOptionImpl() {
    FormatOptionImpl option = new FormatOptionImpl();
    assertEquals(SupportedQueryOptions.FORMAT, option.getKind());

    option.setFormat("A");

    assertEquals("A", option.getFormat());
  }

  @Test
  public void testIdOptionImpl() {
    IdOptionImpl option = new IdOptionImpl();
    assertEquals(SupportedQueryOptions.ID, option.getKind());

    option.setValue("A");

    assertEquals("A", option.getValue());
  }

  @Test
  public void testInlineCountImpl() {
    CountOptionImpl option = new CountOptionImpl();
    assertEquals(SupportedQueryOptions.INLINECOUNT, option.getKind());

    assertEquals(false, option.getValue());
    option.setValue(true);
    assertEquals(true, option.getValue());
  }

  @Test
  public void testLevelsExpandOptionImpl() {
    LevelsOptionImpl option = new LevelsOptionImpl();
    assertEquals(SupportedQueryOptions.LEVELS, option.getKind());

    assertEquals(0, option.getValue());
    option.setValue(1);
    assertEquals(1, option.getValue());

    option = new LevelsOptionImpl();
    option.setMax();
    assertEquals(true, option.isMax());
  }

  @Test
  public void testOrderByItemImpl() {
    OrderByItemImpl option = new OrderByItemImpl();

    AliasImpl expression = new AliasImpl();
    option.setExpression(expression);
    assertEquals(expression, option.getExpression());

    assertEquals(false, option.isDescending());
    option.setDescending(true);
    assertEquals(true, option.isDescending());
  }

  @Test
  public void testOrderByOptionImpl() {
    OrderByOptionImpl option = new OrderByOptionImpl();

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
  public void testSearchOptionImpl() {
    SearchOptionImpl option = new SearchOptionImpl();
    assertEquals(SupportedQueryOptions.SEARCH, option.getKind());
    // TODO $search is not supported yet
  }

  @Test
  public void testSelectItemImpl() {
    SelectItemImpl option = new SelectItemImpl();

    // no typed collection else case ( e.g. if not path is added)
    option = new SelectItemImpl();

    option = new SelectItemImpl();
    assertEquals(false, option.isStar());
    option.setStar(true);
    assertEquals(true, option.isStar());

    option = new SelectItemImpl();
    assertEquals(false, option.isAllOperationsInSchema());
    FullQualifiedName fqName = new FullQualifiedName("Namespace", "Name");
    option.addAllOperationsInSchema(fqName);
    assertEquals(true, option.isAllOperationsInSchema());
    assertEquals(fqName, option.getAllOperationsInSchemaNameSpace());

  }

  @Test
  public void testSelectOptionImpl() {
    SelectOptionImpl option = new SelectOptionImpl();
    assertEquals(SupportedQueryOptions.SELECT, option.getKind());

    SelectItemImpl item0 = new SelectItemImpl();
    SelectItemImpl item1 = new SelectItemImpl();

    ArrayList<SelectItemImpl> list = new ArrayList<SelectItemImpl>();
    list.add(item0);
    list.add(item1);
    option.setSelectItems(list);

    assertEquals(item0, option.getSelectItems().get(0));
    assertEquals(item1, option.getSelectItems().get(1));

  }

  @Test
  public void testSkipOptionImpl() {
    SkipOptionImpl option = new SkipOptionImpl();
    assertEquals(SupportedQueryOptions.SKIP, option.getKind());

    option.setValue("A");
    assertEquals("A", option.getValue());
  }

  @Test
  public void testSkipTokenOptionImpl() {
    SkipTokenOptionImpl option = new SkipTokenOptionImpl();
    assertEquals(SupportedQueryOptions.SKIPTOKEN, option.getKind());

    option.setValue("A");
    assertEquals("A", option.getValue());
  }

  @Test
  public void testSystemQueryOptionImpl() {
    SystemQueryOptionImpl option = new SystemQueryOptionImpl();

    option.setKind(SupportedQueryOptions.EXPAND);
    assertEquals(SupportedQueryOptions.EXPAND, option.getKind());

    assertEquals("$expand", option.getName());
  }

  @Test
  public void testTopOptionImpl() {
    TopOptionImpl option = new TopOptionImpl();
    assertEquals(SupportedQueryOptions.TOP, option.getKind());

    option.setValue("A");
    assertEquals("A", option.getValue());
  }
}
