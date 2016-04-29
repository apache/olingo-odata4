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
package org.apache.olingo.server.core.uri.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.olingo.commons.api.edm.Edm;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SelectItem;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;

public class ExpandValidator implements TestValidator {
  private Edm edm;
  private TestValidator invokedByValidator;

  private int expandItemIndex;
  private ExpandOption expandOption;
  private ExpandItem expandItem;

  // --- Setup ---

  public ExpandValidator setUpValidator(final TestValidator validator) {
    invokedByValidator = validator;
    return this;
  }

  public ExpandValidator setExpand(final ExpandOption expand) {
    expandOption = expand;
    first();
    return this;
  }

  public ExpandValidator setEdm(final Edm edm) {
    this.edm = edm;
    return this;
  }

  // --- Navigation ---

  public ExpandValidator goUpToExpandValidator() {
    return (ExpandValidator) invokedByValidator;
  }

  public TestUriValidator goUpToUriValidator() {
    return (TestUriValidator) invokedByValidator;
  }

  public ResourceValidator goPath() {
    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoPath(expandItem.getResourcePath());
  }

  public FilterValidator goOrder(final int index) {
    final OrderByOption orderBy = expandItem.getOrderByOption();
    return new FilterValidator()
        .setValidator(this)
        .setEdm(edm)
        .setExpression(orderBy.getOrders().get(index).getExpression());
  }

  public ResourceValidator goSelectItem(final int index) {
    final SelectOption select = expandItem.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    return new ResourceValidator()
        .setUpValidator(this)
        .setEdm(edm)
        .setUriInfoPath(item.getResourcePath());
  }

  public FilterValidator goFilter() {
    return new FilterValidator()
        .setEdm(edm)
        .setFilter(expandItem.getFilterOption())
        .setValidator(this);
  }

  public ExpandValidator goExpand() {
    return new ExpandValidator()
        .setExpand(expandItem.getExpandOption())
        .setUpValidator(this);
  }

  public ExpandValidator first() {
    expandItemIndex = 0;
    expandItem = expandOption.getExpandItems().get(expandItemIndex);
    return this;
  }

  public ExpandValidator next() {
    expandItemIndex++;
    assertTrue("not enough segments", expandItemIndex < expandOption.getExpandItems().size());
    expandItem = expandOption.getExpandItems().get(expandItemIndex);
    return this;
  }

  public ExpandValidator isSegmentStar() {
    assertTrue(expandItem.isStar());
    return this;
  }

  public ExpandValidator isSegmentRef() {
    assertTrue(expandItem.isRef());
    return this;
  }

  public ExpandValidator isLevelText(final String text) {
    final QueryOption option = (QueryOption) expandItem.getLevelsOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSkipText(final String text) {
    final QueryOption option = expandItem.getSkipOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isTopText(final String text) {
    final QueryOption option = expandItem.getTopOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isInlineCountText(final String text) {
    final QueryOption option = expandItem.getCountOption();
    assertEquals(text, option.getText());
    return this;
  }

  public ExpandValidator isSelectItemStar(final int index) {
    SelectOption select = expandItem.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertTrue(item.isStar());
    return this;
  }

  public ExpandValidator isSelectItemAllOperations(final int index, final FullQualifiedName fqn) {
    SelectOption select = expandItem.getSelectOption();
    SelectItem item = select.getSelectItems().get(index);
    assertEquals(fqn.toString(), item.getAllOperationsInSchemaNameSpace().toString());
    return this;
  }

  public ExpandValidator isSortOrder(final int index, final boolean descending) {
    OrderByOption orderBy = expandItem.getOrderByOption();
    assertEquals(descending, orderBy.getOrders().get(index).isDescending());
    return this;
  }

  public ExpandValidator isExpandStartType(final FullQualifiedName fullName) {
    assertNotNull(expandItem.getStartTypeFilter());
    assertEquals(fullName, expandItem.getStartTypeFilter().getFullQualifiedName());
    return this;
  }

  public ExpandValidator isSearchSerialized(final String serialized) {
    assertEquals(serialized, expandItem.getSearchOption().getSearchExpression().toString());
    return this;
  }
}
