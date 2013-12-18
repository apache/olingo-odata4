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

import java.util.ArrayList;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.Edm;
import org.apache.olingo.odata4.producer.api.uri.UriResourceProperty;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExceptionVisitExpand;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandItem;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandVisitor;
import org.apache.olingo.odata4.producer.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.InlineCountOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.LevelExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SystemQueryOptionEnum;
import org.apache.olingo.odata4.producer.api.uri.queryoption.TopOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.VisitableExpand;

public class ExpandItemImpl implements ExpandItem, VisitableExpand {
  private Edm edm;

  private ExpandSegment lastExpandSegment = null;

  private List<ExpandSegment> segments = new ArrayList<ExpandSegment>();

  private boolean isStar;
  private LevelExpandOption levelExpandOption;
  private FilterOption filterOption;
  private SearchOption searchOption;
  private OrderByOption orderByOption;
  private SkipOption skipOption;
  private TopOption topOption;
  private InlineCountOption inlineCountOption;
  private SelectOption selectOption;
  private ExpandOption expandOption;

  public ExpandItemImpl setEdm(Edm edm) {
    this.edm = edm;
    return this;
  }

  
  public ExpandItemImpl setStar(boolean isStar) {
    this.isStar = isStar;
    return this;
  }

  

  public ExpandSegment getLastSegement() {
    return lastExpandSegment;
  }
   
  
  public ExpandItemImpl setExpandQueryOption(QueryOptionImpl item) {
    if (item instanceof SystemQueryOptionImpl) {
        SystemQueryOptionImpl sysItem = (SystemQueryOptionImpl) item;

        if (sysItem.getKind() == SystemQueryOptionEnum.EXPAND) {
          expandOption = (ExpandOptionImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.FILTER) {
          filterOption = (FilterOptionImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.INLINECOUNT) {
          inlineCountOption = (InlineCountImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.ORDERBY) {
          orderByOption = (OrderByImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.SEARCH) {
          searchOption = (SearchOptionImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.SELECT) {
          selectOption = (SelectOptionImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.SKIP) {
          skipOption = (SkipOptionImpl) sysItem;
        } else if (sysItem.getKind() == SystemQueryOptionEnum.TOP) {
          topOption = (TopOptionImpl) sysItem;
        }
      }
    return this;
  }
  public ExpandItemImpl setExpandQueryOptions(List<QueryOptionImpl> list) {

    for (QueryOptionImpl item : list) {
      setExpandQueryOption(item);
    }
    return this;
  }

  public ExpandItemImpl addSegment(ExpandSegment segment) {
    lastExpandSegment = segment;
    segments.add(segment);
    return this;
  }


  @Override
  public <T> T accept(ExpandVisitor<T> visitor) throws ExceptionVisitExpand {

    List<T> parameters = new ArrayList<T>();
    for (ExpandSegment segment : segments) {
      parameters.add(segment.accept(visitor));
    }

    // TODO implement visitor pattern for options

    return null; //visitor.visitExpandItem(parameters, isStar, isRef, finalType);
  }

  @Override
  public boolean isStar() {
    return isStar;
  }

  

  

  @Override
  public List<UriResourceProperty> getPropertyChainList() {

    return null;
  }

  @Override
  public LevelExpandOption getLevel() {
    return levelExpandOption;
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



}
