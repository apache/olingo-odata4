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
package org.apache.olingo.client.api.uri;

import java.net.URI;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.commons.api.edm.EdmEnumType;

public interface URIBuilder {

  /**
   * Adds the specified query option to the URI.
   * <br />
   * Concatenates value if the specified query option already exists.
   *
   * @param option query option.
   * @param value query option value.
   * @return current URIBuilder instance
   */
  URIBuilder addQueryOption(QueryOption option, String value);
  
  /**
   * Adds/replaces the specified query option to the URI.
   *
   * @param option query option.
   * @param value query option value.
   * @return current URIBuilder instance
   */
  URIBuilder replaceQueryOption(QueryOption option, String value);

  /**
   * Adds/Replaces the specified (custom) query option to the URI.
   *
   * @param option query option.
   * @param value query option value.
   * @param replace if <tt>true</tt> then replace existing one.
   * @return current URIBuilder instance.
   */
  URIBuilder addQueryOption(String option, String value, boolean replace);

  /**
   * Adds the specified (custom) parameter alias to the URI.
   *
   * @param alias parameter alias.
   * @param exp expression value.
   * @return current URIBuilder instance.
   */
  URIBuilder addParameterAlias(final String alias, final String exp);

  /**
   * Appends EntitySet segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendEntitySetSegment(String segmentValue);

  /**
   * Appends key segment to the URI.
   *
   * @param val segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendKeySegment(Object val);

  /**
   * Appends key segment to the URI, for multiple keys.
   *
   * @param segmentValues segment values.
   * @return current URIBuilder instance
   */
  URIBuilder appendKeySegment(Map<String, Object> segmentValues);

  /**
   * Appends property segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendPropertySegment(String segmentValue);

  /**
   * Appends navigation segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendNavigationSegment(String segmentValue);

  /**
   * Appends derived entity type segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendDerivedEntityTypeSegment(String segmentValue);

  /**
   * Appends value segment to the URI.
   *
   * @return current URIBuilder instance
   */
  URIBuilder appendValueSegment();

  /**
   * Appends count segment to the URI.
   * @return current URIBuilder instance
   */
  URIBuilder appendCountSegment();

  /**
   * Appends operation (action or function) segment to the URI.
   *
   * @param operation Operation (action or function) name
   * @return current URIBuilder instance
   */
  URIBuilder appendOperationCallSegment(String operation);

  /**
   * Appends metadata segment to the URI.
   *
   * @return current URIBuilder instance
   */
  URIBuilder appendMetadataSegment();

  /**
   * Appends batch segment to the URI.
   *
   * @return current URIBuilder instance
   */
  URIBuilder appendBatchSegment();

  /**
   * Adds count query option.
   *
   * @return current URIBuilder instance
   */
  URIBuilder count();

  /**
   * Adds expand query option.
   *
   * @param expandItems items to be expanded in-line
   * @return current URIBuilder instance
   * @see QueryOption#EXPAND
   */
  URIBuilder expand(String... expandItems);

  /**
   * Adds format query option.
   *
   * @param format media type acceptable in a response.
   * @return current URIBuilder instance
   * @see QueryOption#FORMAT
   */
  URIBuilder format(String format);

  /**
   * Adds filter for filter query option.
   *
   * @param filter filter instance (to be obtained via <tt>FilterFactory</tt>);
   *               note that <tt>build()</tt> method will be immediately invoked.
   * @return current URIBuilder instance
   * @see QueryOption#FILTER
   * @see URIFilter
   * @see org.apache.olingo.client.api.uri.FilterFactory
   */
  URIBuilder filter(URIFilter filter);

  /**
   * Adds filter query option.
   *
   * @param filter filter string.
   * @return current URIBuilder instance
   * @see QueryOption#FILTER
   */
  URIBuilder filter(String filter);

  /**
   * Adds select query option.
   *
   * @param selectItems select items
   * @return current URIBuilder instance
   * @see QueryOption#SELECT
   */
  URIBuilder select(String... selectItems);

  /**
   * Adds orderby query option.
   *
   * @param order order string.
   * @return current URIBuilder instance
   * @see QueryOption#ORDERBY
   */
  URIBuilder orderBy(String order);

  /**
   * Adds top query option.
   *
   * @param top maximum number of entities to be returned.
   * @return current URIBuilder instance
   * @see QueryOption#TOP
   */
  URIBuilder top(int top);

  /**
   * Adds skip query option.
   *
   * @param skip number of entities to be skipped into the response.
   * @return current URIBuilder instance
   * @see QueryOption#SKIP
   */
  URIBuilder skip(int skip);

  /**
   * Adds skiptoken query option.
   *
   * @param skipToken opaque token.
   * @return current URIBuilder instance
   * @see QueryOption#SKIPTOKEN
   */
  URIBuilder skipToken(String skipToken);

  /**
   * Build OData URI.
   *
   * @return OData URI.
   */
  URI build();
  
  /**
   * Appends enum key segment to the URI.
   *
   * @param enumType enum type
   * @param memberName enum member name
   * @return current URIBuilder instance
   */
  URIBuilder appendKeySegment(EdmEnumType enumType, String memberName);

  /**
   * Appends key segment to the URI, for multiple keys.
   *
   * @param enumValues enum segment values.
   * @param segmentValues segment values.
   * @return current URIBuilder instance
   */
  URIBuilder appendKeySegment(Map<String, Pair<EdmEnumType, String>> enumValues, Map<String, Object> segmentValues);

  /**
   * Appends Singleton segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  URIBuilder appendSingletonSegment(String segmentValue);

  /**
   * Appends entity-id segment to the URI.
   *
   * @param segmentValue segment value
   * @return current URIBuilder instance
   */
  URIBuilder appendEntityIdSegment(String segmentValue);

  /**
   * Appends ref segment to the URI.
   *
   * @return current URIBuilder instance
   */
  URIBuilder appendRefSegment();

  /**
   * Appends cross join segment to the URI.
   *
   * @param segmentValues segment values.
   * @return current URIBuilder instance
   */
  URIBuilder appendCrossjoinSegment(String... segmentValues);

  /**
   * Appends all segment to the URI.
   *
   * @return current URIBuilder instance
   */
  URIBuilder appendAllSegment();

  /**
   * Adds id query option.
   *
   * @param idValue opaque token.
   * @return current URIBuilder instance
   * @see org.apache.olingo.client.api.uri.QueryOption#ID
   */
  URIBuilder id(String idValue);

  /**
   * Appends count query option.
   *
   * @param value true or false
   * @return current URIBuilder instance
   * @see org.apache.olingo.client.api.uri.QueryOption#COUNT
   */
  URIBuilder count(boolean value);

  /**
   * Appends search query option.
   *
   * @param search search expression
   * @return current URIBuilder instance
   * @see org.apache.olingo.client.api.uri.QueryOption#SEARCH
   */
  URIBuilder search(URISearch search);
  
  /**
   * Appends search query option.
   *
   * @param expression search expression
   * @return current URIBuilder instance
   * @see org.apache.olingo.client.api.uri.QueryOption#SEARCH
   */
  URIBuilder search(String expression);

  /**
   * The set of expanded entities can be refined through the application of expand options, expressed as a
   * semicolon-separated list of system query options, enclosed in parentheses, see [OData-URL].
   *
   * @param expandItem item to be expanded.
   * @param options System query options. Allowed query options are: $filter, $select, $orderby, $skip, $top, $count,
   * $search, $expand, and $levels.
   * @return current URIBuilder instance.
   * @see org.apache.olingo.client.api.uri.QueryOption#EXPAND
   */
  URIBuilder expandWithOptions(String expandItem, Map<QueryOption, Object> options);
  
  /**
   * The set of expanded entities can be refined through the application of expand options, expressed as a
   * semicolon-separated list of system query options, enclosed in parentheses, see [OData-URL].
   *
   * @param expandItem item to be expanded.
   * @param pathRef include the /$ref at the end of the $expand item's path;if true pathCount MUST be false
   * @param pathCount include /$count at the end of the $expand item's path;if true pathRef MUST be false
   * @param options System query options. Allowed query options are: $filter, $select, $orderby, $skip, $top, $count,
   * $search, $expand, and $levels.
   * @return current URIBuilder instance.
   * @see org.apache.olingo.client.api.uri.QueryOption#EXPAND
   */
  URIBuilder expandWithOptions(String expandItem, boolean pathRef,
      boolean pathCount, Map<QueryOption, Object> options);  
  
  /**
   * Properties of related entities can be specified by including the $select query option within the $expand.
   * <br />
   * <tt>http://host/service/Products?$expand=Category($select=Name)</tt>
   * @param expandItem related entity name.
   * @param selectItems properties to be selected.
   * @return current URIBuilder instance.
   * @see org.apache.olingo.client.api.uri.QueryOption#EXPAND
   * @see org.apache.olingo.client.api.uri.QueryOption#SELECT
   */
  URIBuilder expandWithSelect(String expandItem, String... selectItems);

  /**
   * Appends action segment to the URI.
   *
   * @param action Action name
   * @return current URIBuilder instance
   */
  URIBuilder appendActionCallSegment(String action);
  
  /**
   * Adds/Replaces the specified custom query option to the URI.
   *
   * @param option.
   * @param value.
   * @return current URIBuilder instance.
   */
  URIBuilder addCustomQueryOption(String customName, String customValue);
}
