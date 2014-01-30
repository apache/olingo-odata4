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
package org.apache.olingo.odata4.producer.core.uri.queryoption;

import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.InlineCountOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.LevelsExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SupportedQueryOptions;
import org.apache.olingo.odata4.producer.api.uri.queryoption.TopOption;

public class ExpandItemImpl implements ExpandItem {
  private LevelsExpandOption levelsExpandOption;
  private FilterOption filterOption;
  private SearchOption searchOption;
  private OrderByOption orderByOption;
  private SkipOption skipOption;
  private TopOption topOption;
  private InlineCountOption inlineCountOption;
  private SelectOption selectOption;
  private ExpandOption expandOption;

  private UriInfoResource resourcePath;

  private boolean isStar;

  private boolean isRef;

  public ExpandItemImpl setEdm(final Edm edm) {
    return this;
  }

  public ExpandItemImpl setExpandQueryOption(final QueryOptionImpl item) {
    if (item instanceof SystemQueryOptionImpl) {
      SystemQueryOptionImpl sysItem = (SystemQueryOptionImpl) item;

      if (sysItem.getKind() == SupportedQueryOptions.EXPAND) {
        expandOption = (ExpandOptionImpl) sysItem;
      } else if (sysItem.getKind() == SupportedQueryOptions.FILTER) {
        filterOption = (FilterOptionImpl) sysItem;
      } else if (sysItem.getKind() == SupportedQueryOptions.INLINECOUNT) {
        inlineCountOption = (InlineCountOptionImpl) sysItem;
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
    }
    return this;
  }

  public ExpandItemImpl setExpandQueryOptions(final List<QueryOptionImpl> list) {

    for (QueryOptionImpl item : list) {
      setExpandQueryOption(item);
    }
    return this;
  }

  @Override
  public LevelsExpandOption getLevels() {
    return levelsExpandOption;
  }

  @Override
  public FilterOption getFilter() {
    return filterOption;
  }

  @Override
  public SearchOption getSearch() {
    return searchOption;
  }

  @Override
  public OrderByOption getOrderBy() {
    return orderByOption;
  }

  @Override
  public SkipOption getSkip() {
    return skipOption;
  }

  @Override
  public TopOption getTop() {
    return topOption;
  }

  @Override
  public InlineCountOption getInlineCount() {
    return inlineCountOption;
  }

  @Override
  public SelectOption getSelect() {

    return selectOption;
  }

  @Override
  public ExpandOption getExpand() {
    return expandOption;
  }

  public ExpandItemImpl setResourcePath(final UriInfoResource resourcePath) {
    this.resourcePath = resourcePath;
    return this;
  }

  @Override
  public UriInfoResource getPath() {

    return resourcePath;
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

}
