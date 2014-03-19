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
package org.apache.olingo.server.core.uri;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.olingo.commons.api.ODataRuntimeException;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoAll;
import org.apache.olingo.server.api.uri.UriInfoBatch;
import org.apache.olingo.server.api.uri.UriInfoCrossjoin;
import org.apache.olingo.server.api.uri.UriInfoEntityId;
import org.apache.olingo.server.api.uri.UriInfoKind;
import org.apache.olingo.server.api.uri.UriInfoMetadata;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriInfoService;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.TopOption;
import org.apache.olingo.server.core.uri.queryoption.CustomQueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.QueryOptionImpl;
import org.apache.olingo.server.core.uri.queryoption.SystemQueryOptionImpl;

public class UriInfoImpl implements UriInfo {

  private UriInfoKind kind;

  private List<String> entitySetNames = new ArrayList<String>(); // for $entity
  private EdmEntityType entityTypeCast; // for $entity

  private List<CustomQueryOptionImpl> customQueryOptions = new ArrayList<CustomQueryOptionImpl>();

  HashMap<SystemQueryOptionKind, SystemQueryOption> systemQueryOptions =
      new HashMap<SystemQueryOptionKind, SystemQueryOption>();

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
    return (ExpandOption) systemQueryOptions.get(SystemQueryOptionKind.EXPAND);
  }

  @Override
  public FilterOption getFilterOption() {
    return (FilterOption) systemQueryOptions.get(SystemQueryOptionKind.FILTER);
  }

  @Override
  public FormatOption getFormatOption() {
    return (FormatOption) systemQueryOptions.get(SystemQueryOptionKind.FORMAT);
  }

  @Override
  public IdOption getIdOption() {
    return (IdOption) systemQueryOptions.get(SystemQueryOptionKind.ID);
  }

  @Override
  public CountOption getCountOption() {
    return (CountOption) systemQueryOptions.get(SystemQueryOptionKind.COUNT);
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
    return (OrderByOption) systemQueryOptions.get(SystemQueryOptionKind.ORDERBY);
  }

  @Override
  public SearchOption getSearchOption() {

    return (SearchOption) systemQueryOptions.get(SystemQueryOptionKind.SEARCH);
  }

  @Override
  public SelectOption getSelectOption() {
    return (SelectOption) systemQueryOptions.get(SystemQueryOptionKind.SELECT);
  }

  @Override
  public SkipOption getSkipOption() {
    return (SkipOption) systemQueryOptions.get(SystemQueryOptionKind.SKIP);
  }

  @Override
  public SkipTokenOption getSkipTokenOption() {
    return (SkipTokenOption) systemQueryOptions.get(SystemQueryOptionKind.SKIPTOKEN);
  }

  @Override
  public TopOption getTopOption() {
    return (TopOption) systemQueryOptions.get(SystemQueryOptionKind.TOP);
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

    if (systemOption.getKind() == SystemQueryOptionKind.EXPAND) {
      systemQueryOptions.put(SystemQueryOptionKind.EXPAND, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.FILTER) {
      systemQueryOptions.put(SystemQueryOptionKind.FILTER, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.FORMAT) {
      systemQueryOptions.put(SystemQueryOptionKind.FORMAT, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.ID) {
      systemQueryOptions.put(SystemQueryOptionKind.ID, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.COUNT) {
      systemQueryOptions.put(SystemQueryOptionKind.COUNT, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.ORDERBY) {
      systemQueryOptions.put(SystemQueryOptionKind.ORDERBY, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.SEARCH) {
      systemQueryOptions.put(SystemQueryOptionKind.SEARCH, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.SELECT) {
      systemQueryOptions.put(SystemQueryOptionKind.SELECT, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.SKIP) {
      systemQueryOptions.put(SystemQueryOptionKind.SKIP, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.SKIPTOKEN) {
      systemQueryOptions.put(SystemQueryOptionKind.SKIPTOKEN, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.TOP) {
      systemQueryOptions.put(SystemQueryOptionKind.TOP, systemOption);
    } else if (systemOption.getKind() == SystemQueryOptionKind.LEVELS) {
      systemQueryOptions.put(SystemQueryOptionKind.LEVELS, systemOption);
    } else {
      throw new ODataRuntimeException("Unsupported System Query Option: " + systemOption.getName());
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

  @Override
  public Collection<SystemQueryOption> getSystemQueryOptions() {
    return Collections.unmodifiableCollection(systemQueryOptions.values());
  }

  @Override
  public UriResource getUriResourceLastPart() {
    return lastResourcePart;
  }
}