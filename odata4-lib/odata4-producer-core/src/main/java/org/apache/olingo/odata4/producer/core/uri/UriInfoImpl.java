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
package org.apache.olingo.odata4.producer.core.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.producer.api.uri.UriInfo;
import org.apache.olingo.odata4.producer.api.uri.UriInfoAll;
import org.apache.olingo.odata4.producer.api.uri.UriInfoBatch;
import org.apache.olingo.odata4.producer.api.uri.UriInfoCrossjoin;
import org.apache.olingo.odata4.producer.api.uri.UriInfoEntityId;
import org.apache.olingo.odata4.producer.api.uri.UriInfoKind;
import org.apache.olingo.odata4.producer.api.uri.UriInfoMetadata;
import org.apache.olingo.odata4.producer.api.uri.UriInfoResource;
import org.apache.olingo.odata4.producer.api.uri.UriInfoService;
import org.apache.olingo.odata4.producer.api.uri.UriResourcePart;
import org.apache.olingo.odata4.producer.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.FormatOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.IdOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.InlineCountOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SkiptokenOption;
import org.apache.olingo.odata4.producer.api.uri.queryoption.SupportedQueryOptions;
import org.apache.olingo.odata4.producer.api.uri.queryoption.TopOption;
import org.apache.olingo.odata4.producer.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.InlineCountOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SearchOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SkiptokenOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.SystemQueryOptionImpl;
import org.apache.olingo.odata4.producer.core.uri.queryoption.TopOptionImpl;

public class UriInfoImpl implements UriInfo {

  private UriInfoKind kind;

  private List<String> entitySetNames = new ArrayList<String>(); // for $entity
  private EdmEntityType entityTypeCast; // for $entity

  // Query options
  private List<CustomQueryOptionImpl> customQueryOptions = new ArrayList<CustomQueryOptionImpl>();
  private ExpandOptionImpl expandOption;
  private FilterOptionImpl filterOption;
  private FormatOptionImpl formatOption;
  private IdOption idOption;
  private InlineCountOptionImpl inlineCountOption;
  private OrderByOptionImpl orderByOption;
  private SearchOptionImpl searchOption;
  private SelectOptionImpl selectOption;
  private SkipOptionImpl skipOption;
  private SkiptokenOptionImpl skipTokenOption;
  private TopOptionImpl topOption;

  private String fragment;

  private UriResourcePart lastResourcePart;
  private List<UriResourcePart> pathParts = new ArrayList<UriResourcePart>();

  @Override
  public UriInfoAll asUriInfoAll() {
    return this;
  }

  @Override
  public UriInfoBatch asUriInfoBatch() {
    return this;
  }

  @Override
  public UriInfoCrossjoin asUriInfoCrossjoin() {
    return this;
  }

  @Override
  public UriInfoEntityId asUriInfoEntityId() {
    return this;
  }

  @Override
  public UriInfoMetadata asUriInfoMetadata() {
    return this;
  }

  @Override
  public UriInfoResource asUriInfoResource() {
    return this;
  }

  @Override
  public List<String> getEntitySetNames() {
    return Collections.unmodifiableList(entitySetNames);
  }

  public void addEntitySetName(final String entitySet) {
    entitySetNames.add(entitySet);
  }

  @Override
  public List<UriResourcePart> getUriResourceParts() {
    List<UriResourcePart> returnList = new ArrayList<UriResourcePart>();
    for (UriResourcePart item : pathParts) {
      returnList.add(item);
    }
    return Collections.unmodifiableList(returnList);
  }

  public void addPathInfo(final UriResourcePartImpl uriPathInfo) {
    pathParts.add(uriPathInfo);
    lastResourcePart = uriPathInfo;
  }

  @Override
  public List<CustomQueryOption> getCustomQueryOptions() {
    List<CustomQueryOption> retList = new ArrayList<CustomQueryOption>();
    for (CustomQueryOptionImpl item : customQueryOptions) {
      retList.add(item);
    }
    return retList;
  }

  @Override
  public EdmEntityType getEntityTypeCast() {
    return entityTypeCast;
  }

  @Override
  public ExpandOption getExpandOption() {
    return expandOption;
  }

  @Override
  public FilterOption getFilterOption() {
    return filterOption;
  }

  @Override
  public FormatOption getFormatOption() {
    return formatOption;
  }

  @Override
  public IdOption getIdOption() {
    return idOption;
  }

  @Override
  public InlineCountOption getInlineCountOption() {
    return inlineCountOption;
  }

  @Override
  public UriInfoKind getKind() {
    return kind;
  }

  public UriResourcePart getLastResourcePart() {
    return lastResourcePart;
  }

  @Override
  public OrderByOption getOrderByOption() {
    return orderByOption;
  }

  @Override
  public SearchOption getSearchOption() {

    return searchOption;
  }

  @Override
  public SelectOption getSelectOption() {
    return selectOption;
  }

  @Override
  public SkipOption getSkipOption() {
    return skipOption;
  }

  @Override
  public SkiptokenOption getSkipTokenOption() {
    return skipTokenOption;
  }

  @Override
  public TopOption getTopOption() {
    return topOption;
  }

  public UriInfoImpl setEntityTypeCast(final EdmEntityType type) {
    entityTypeCast = type;
    return this;
  }

  public UriInfoImpl setFormat(final FormatOptionImpl formatOption) {
    this.formatOption = formatOption;
    return this;
  }

  protected UriInfoImpl setKind(final UriInfoKind kind) {
    this.kind = kind;
    return this;
  }

  public UriInfoImpl setQueryOptions(final List<QueryOptionImpl> list) {

    for (QueryOptionImpl item : list) {
      if (item instanceof SystemQueryOptionImpl) {
        SystemQueryOptionImpl sysItem = (SystemQueryOptionImpl) item;

        if (sysItem.getKind() == SupportedQueryOptions.EXPAND) {
          expandOption = (ExpandOptionImpl) sysItem;
        } else if (sysItem.getKind() == SupportedQueryOptions.FILTER) {
          filterOption = (FilterOptionImpl) sysItem;
        } else if (sysItem.getKind() == SupportedQueryOptions.FORMAT) {
          formatOption = (FormatOptionImpl) sysItem;
        } else if (sysItem.getKind() == SupportedQueryOptions.ID) {
          idOption = (IdOptionImpl) sysItem;
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
        } else if (sysItem.getKind() == SupportedQueryOptions.SKIPTOKEN) {
          skipTokenOption = (SkiptokenOptionImpl) sysItem;
        } else if (sysItem.getKind() == SupportedQueryOptions.TOP) {
          topOption = (TopOptionImpl) sysItem;
        }
      } else if (item instanceof CustomQueryOptionImpl) {
        customQueryOptions.add((CustomQueryOptionImpl) item);
      }
    }
    return this;
  }

  @Override
  public UriInfoService asUriInfoService() {
    return this;
  }

  public void clearPathInfo() {
    pathParts.clear();
  }

  public String getFragment() {
    return fragment;
  }

  public UriInfoImpl setFragment(String fragment) {
    this.fragment = fragment;
    return this;
  }
}