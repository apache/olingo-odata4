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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
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
import org.apache.olingo.server.api.uri.queryoption.AliasQueryOption;
import org.apache.olingo.server.api.uri.queryoption.CountOption;
import org.apache.olingo.server.api.uri.queryoption.CustomQueryOption;
import org.apache.olingo.server.api.uri.queryoption.ExpandOption;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.FormatOption;
import org.apache.olingo.server.api.uri.queryoption.IdOption;
import org.apache.olingo.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.server.api.uri.queryoption.QueryOption;
import org.apache.olingo.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.server.api.uri.queryoption.SelectOption;
import org.apache.olingo.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.server.api.uri.queryoption.SkipTokenOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOption;
import org.apache.olingo.server.api.uri.queryoption.SystemQueryOptionKind;
import org.apache.olingo.server.api.uri.queryoption.TopOption;

public class UriInfoImpl implements UriInfo {

  private UriInfoKind kind;

  private List<String> entitySetNames = new ArrayList<String>(); // for $entity
  private EdmEntityType entityTypeCast; // for $entity

  private UriResource lastResourcePart;
  private List<UriResource> pathParts = new ArrayList<UriResource>();

  private Map<SystemQueryOptionKind, SystemQueryOption> systemQueryOptions =
      new HashMap<SystemQueryOptionKind, SystemQueryOption>();
  private Map<String, AliasQueryOption> aliases = new HashMap<String, AliasQueryOption>();
  private List<CustomQueryOption> customQueryOptions = new ArrayList<CustomQueryOption>();

  private String fragment;

  public UriInfoImpl setKind(final UriInfoKind kind) {
    this.kind = kind;
    return this;
  }

  @Override
  public UriInfoKind getKind() {
    return kind;
  }

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
  public UriInfoService asUriInfoService() {
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

  public UriInfoImpl addEntitySetName(final String entitySet) {
    entitySetNames.add(entitySet);
    return this;
  }

  @Override
  public List<String> getEntitySetNames() {
    return Collections.unmodifiableList(entitySetNames);
  }

  public UriInfoImpl setEntityTypeCast(final EdmEntityType type) {
    entityTypeCast = type;
    return this;
  }

  @Override
  public EdmEntityType getEntityTypeCast() {
    return entityTypeCast;
  }

  public UriInfoImpl addResourcePart(final UriResource uriPathInfo) {
    pathParts.add(uriPathInfo);
    lastResourcePart = uriPathInfo;
    return this;
  }

  public UriInfoImpl removeResourcePart(final int index) {
    pathParts.remove(index);
    return this;
  }

  public UriResource getLastResourcePart() {
    return lastResourcePart;
  }

  @Override
  public List<UriResource> getUriResourceParts() {
    return Collections.unmodifiableList(pathParts);
  }

  public UriInfoImpl setQueryOptions(final List<QueryOption> list) {
    for (final QueryOption item : list) {
      if (item instanceof SystemQueryOption) {
        setSystemQueryOption((SystemQueryOption) item);
      } else if (item instanceof AliasQueryOption) {
        addAlias((AliasQueryOption) item);
      } else if (item instanceof CustomQueryOption) {
        addCustomQueryOption((CustomQueryOption) item);
      }
    }
    return this;
  }

  /**
   * Adds system query option.
   * @param systemOption the option to be added
   * @return this object for method chaining
   * @throws ODataRuntimeException if an unsupported option is provided
   * or an option of this kind has been added before
   */
  public UriInfoImpl setSystemQueryOption(final SystemQueryOption systemOption) {
    final SystemQueryOptionKind kind = systemOption.getKind();
    if (systemQueryOptions.containsKey(kind)) {
      throw new ODataRuntimeException("Double System Query Option: " + systemOption.getName());
    }

    switch (kind) {
    case EXPAND:
    case FILTER:
    case FORMAT:
    case ID:
    case COUNT:
    case ORDERBY:
    case SEARCH:
    case SELECT:
    case SKIP:
    case SKIPTOKEN:
    case TOP:
    case LEVELS:
      systemQueryOptions.put(kind, systemOption);
      break;
    default:
      throw new ODataRuntimeException("Unsupported System Query Option: " + systemOption.getName());
    }
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

  @Override
  public List<SystemQueryOption> getSystemQueryOptions() {
    return Collections.unmodifiableList(new ArrayList<SystemQueryOption>(systemQueryOptions.values()));
  }

  public UriInfoImpl addAlias(final AliasQueryOption alias) {
    aliases.put(alias.getName(), alias);
    return this;
  }

  @Override
  public String getValueForAlias(final String alias) {
    final AliasQueryOption aliasQueryOption = getAlias(alias);
    return aliasQueryOption == null ? null : aliasQueryOption.getText();
  }

  public AliasQueryOption getAlias(final String key) {
    return aliases.get(key);
  }

  @Override
  public List<AliasQueryOption> getAliases() {
    return Collections.unmodifiableList(new ArrayList<AliasQueryOption>(aliases.values()));
  }

  public UriInfoImpl addCustomQueryOption(final CustomQueryOption item) {
    customQueryOptions.add(item);
    return this;
  }

  @Override
  public List<CustomQueryOption> getCustomQueryOptions() {
    return Collections.unmodifiableList(customQueryOptions);
  }

  public UriInfoImpl setFragment(final String fragment) {
    this.fragment = fragment;
    return this;
  }

  @Override
  public String getFragment() {
    return fragment;
  }
}
