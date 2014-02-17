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
package org.apache.olingo.odata4.server.api.uri.optiontree;

import java.util.List;

import org.apache.olingo.odata4.server.api.uri.UriResourceProperty;
import org.apache.olingo.odata4.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.InlineCountOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.OrderByOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SearchOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.SkipOption;
import org.apache.olingo.odata4.server.api.uri.queryoption.TopOption;

/**
 * Contains the merged $expand and $select options
 */
public interface OptionNode {

  /**
   * Contains the list of non navigation properties which should serialized at this expand level.
   */
  List<UriResourceProperty> getPropertyChainList();

  List<OptionProperty> getExpandetNavigationProperties();

  /**
   * Contains the filter which should be applied to this expand level.
   */
  FilterOption getFilter();

  /**
   * Contains the search information which should be applied to this expand level.
   */
  SearchOption getSearch();

  /**
   * Contains the orderBy information which should be applied to this expand level.
   */
  OrderByOption getOrderBy();

  /**
   * Contains the information about how many output entities should be skipped at this
   * expand level.
   */
  SkipOption getSkip();

  /**
   * Contains the information about how many output items should be serialized at this
   * expand level.
   */
  TopOption getTop();

  /**
   * Contains the information whether the number of output items should be serialized
   * at this expand level
   */
  InlineCountOption getInlineCount();

}
