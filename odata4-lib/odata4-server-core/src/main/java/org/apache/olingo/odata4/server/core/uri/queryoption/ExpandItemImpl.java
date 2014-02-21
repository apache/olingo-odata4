/*******************************************************************************
 * 
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

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmType;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.CountOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.LevelsExpandOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SupportedQueryOptions;
import org.apache.olingo.odata4.server.api.uri.queryoption.TopOption;
import org.apache.olingo.odata4.server.core.uri.queryoption.expression.MemberImpl;

public class ExpandItemImpl implements ExpandItem {
  private LevelsExpandOption levelsExpandOption;
  private FilterOption filterOption;
  private SearchOption searchOption;
  private OrderByOption orderByOption;
  private SkipOption skipOption;
  private TopOption topOption;
  private CountOption inlineCountOption;
  private SelectOption selectOption;
  private ExpandOption expandOption;

  private UriInfoResource resourceInfo;

  private boolean isStar;

  private boolean isRef;
  private EdmType startTypeFilter;

  public ExpandItemImpl setSystemQueryOption(final SystemQueryOptionImpl sysItem) {

    if (sysItem.getKind() == SupportedQueryOptions.EXPAND) {
      expandOption = (ExpandOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.FILTER) {
      filterOption = (FilterOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.COUNT) {
      inlineCountOption = (CountOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.ORDERBY) {
      orderByOption = (OrderByOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.SEARCH) {
      searchOption = (SearchOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.SELECT) {
      selectOption = (SelectOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.SKIP) {
      skipOption = (SkipOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.TOP) {
      topOption = (TopOptionImpl) sysItem;
    } else if (sysItem.getKind() == SupportedQueryOptions.LEVELS) {
      levelsExpandOption = (LevelsExpandOption) sysItem;
    }
    return this;
  }

  public ExpandItemImpl setSystemQueryOptions(final List<SystemQueryOptionImpl> list) {

    for (SystemQueryOptionImpl item : list) {
      setSystemQueryOption(item);
    }
    return this;
  }

  @Override
  public LevelsExpandOption getLevelsOption() {
    return levelsExpandOption;
  }

  @Override
  public FilterOption getFilterOption() {
    return filterOption;
  }

  @Override
  public SearchOption getSearchOption() {
    return searchOption;
  }

  @Override
  public OrderByOption getOrderByOption() {
    return orderByOption;
  }

  @Override
  public SkipOption getSkipOption() {
    return skipOption;
  }

  @Override
  public TopOption getTopOption() {
    return topOption;
  }

  @Override
  public CountOption getInlineCountOption() {
    return inlineCountOption;
  }

  @Override
  public SelectOption getSelectOption() {

    return selectOption;
  }

  @Override
  public ExpandOption getExpandOption() {
    return expandOption;
  }

  public ExpandItemImpl setResourcePath(final UriInfoResource resourceInfo) {
    this.resourceInfo = resourceInfo;
    return this;
  }

  @Override
  public UriInfoResource getResourcePath() {

    return resourceInfo;
  }

  @Override
  public boolean isStar() {
    return isStar;
  }

  public ExpandItemImpl setIsStar(final boolean isStar) {
    this.isStar = isStar;
    return this;
  }

  @Override
  public boolean isRef() {
    return isRef;
  }

  public ExpandItemImpl setIsRef(final boolean isRef) {
    this.isRef = isRef;
    return this;
  }

  
  
  @Override 
  public EdmType getStartTypeFilter() {
    return this.startTypeFilter;
  }

  public ExpandItemImpl setTypeFilter(EdmType startTypeFilter) {
     this.startTypeFilter = startTypeFilter;
     return this;
  }
}
