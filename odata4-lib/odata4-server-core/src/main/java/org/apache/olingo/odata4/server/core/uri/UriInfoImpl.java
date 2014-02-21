/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.odata4.server.core.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.odata4.commons.api.edm.EdmEntityType;
import org.apache.olingo.odata4.server.api.uri.UriInfo;
import org.apache.olingo.odata4.server.api.uri.UriInfoAll;
import org.apache.olingo.odata4.server.api.uri.UriInfoBatch;
import org.apache.olingo.odata4.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.odata4.server.api.uri.UriInfoEntityId;
import org.apache.olingo.odata4.server.api.uri.UriInfoKind;
import org.apache.olingo.odata4.server.api.uri.UriInfoMetadata;
import org.apache.olingo.odata4.server.api.uri.UriInfoResource;
import org.apache.olingo.odata4.server.api.uri.UriInfoService;
import org.apache.olingo.odata4.server.api.uri.UriResource;
import org.apache.olingo.odata4.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.IdOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.CountOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SupportedQueryOptions;
import org.apache.olingo.odata4.server.api.uri.queryoption.TopOption;
import org.apache.olingo.odata4.server.core.uri.queryoption.CountOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.ExpandOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FilterOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.FormatOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.IdOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.OrderByOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SearchOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SelectOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SkipOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SkipTokenOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.SystemQueryOptionImpl;
import org.apache.olingo.odata4.server.core.uri.queryoption.TopOptionImpl;

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
  private CountOptionImpl inlineCountOption;
  private OrderByOptionImpl orderByOption;
  private SearchOptionImpl searchOption;
  private SelectOptionImpl selectOption;
  private SkipOptionImpl skipOption;
  private SkipTokenOptionImpl skipTokenOption;
  private TopOptionImpl topOption;

  private String fragment;

  private UriResource lastResourcePart;
  private List<UriResource> pathParts = new ArrayList<UriResource>();

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
  public List<UriResource> getUriResourceParts() {
    List<UriResource> returnList = new ArrayList<UriResource>();
    for (UriResource item : pathParts) {
      returnList.add(item);
    }
    return Collections.unmodifiableList(returnList);
  }

  public UriInfoImpl addResourcePart(final UriResourceImpl uriPathInfo) {
    pathParts.add(uriPathInfo);
    lastResourcePart = uriPathInfo;
    return this;
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

  public UriInfoImpl setEntityTypeCast(final EdmEntityType type) {
    entityTypeCast = type;
    return this;
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
  public CountOption getInlineCountOption() {
    return inlineCountOption;
  }

  @Override
  public UriInfoKind getKind() {
    return kind;
  }

  public UriInfoImpl setKind(final UriInfoKind kind) {
    this.kind = kind;
    return this;
  }

  public UriResource getLastResourcePart() {
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
  public SkipTokenOption getSkipTokenOption() {
    return skipTokenOption;
  }

  @Override
  public TopOption getTopOption() {
    return topOption;
  }

  public UriInfoImpl setQueryOptions(final List<QueryOptionImpl> list) {

    for (QueryOptionImpl item : list) {
      if (item instanceof SystemQueryOptionImpl) {
        setSystemQueryOption((SystemQueryOptionImpl) item);
      } else if (item instanceof CustomQueryOptionImpl) {
        addCustomQueryOption(item);
      }
    }
    return this;
  }

  public void addCustomQueryOption(final QueryOptionImpl item) {
    customQueryOptions.add((CustomQueryOptionImpl) item);
  }

  public UriInfoImpl setSystemQueryOption(final SystemQueryOptionImpl systemOption) {

    if (systemOption.getKind() == SupportedQueryOptions.EXPAND) {
      expandOption = (ExpandOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.FILTER) {
      filterOption = (FilterOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.FORMAT) {
      formatOption = (FormatOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.ID) {
      idOption = (IdOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.COUNT) {
      inlineCountOption = (CountOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.ORDERBY) {
      orderByOption = (OrderByOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.SEARCH) {
      searchOption = (SearchOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.SELECT) {
      selectOption = (SelectOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.SKIP) {
      skipOption = (SkipOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.SKIPTOKEN) {
      skipTokenOption = (SkipTokenOptionImpl) systemOption;
    } else if (systemOption.getKind() == SupportedQueryOptions.TOP) {
      topOption = (TopOptionImpl) systemOption;
    }
    return this;
  }

  @Override
  public UriInfoService asUriInfoService() {
    return this;
  }

  @Override
  public String getFragment() {
    return fragment;
  }

  public UriInfoImpl setFragment(final String fragment) {
    this.fragment = fragment;
    return this;
  }

  public void removeResourcePart(int index) {
    this.pathParts.remove(index);
    
  }
}