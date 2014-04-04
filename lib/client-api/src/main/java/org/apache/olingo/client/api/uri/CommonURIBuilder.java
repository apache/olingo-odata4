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

/**
 * OData URI builder.
 */
public interface CommonURIBuilder<UB extends CommonURIBuilder<?>> {

  /**
   * Adds the specified query option to the URI.
   *
   * @param option query option.
   * @param value query option value.
   * @return current URIBuilder instance
   */
  UB addQueryOption(QueryOption option, String value);

  /**
   * Adds the specified (custom) query option to the URI.
   *
   * @param option query option.
   * @param value query option value.
   * @return current URIBuilder instance.
   */
  UB addQueryOption(String option, String value);

  /**
   * Adds the specified (custom) parameter alias to the URI.
   *
   * @param alias parameter alias.
   * @param exp expression value.
   * @return current URIBuilder instance.
   */
  UB addParameterAlias(final String alias, final String exp);

  /**
   * Appends EntitySet segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  UB appendEntitySetSegment(String segmentValue);

  /**
   * Appends key segment to the URI.
   *
   * @param val segment value.
   * @return current URIBuilder instance
   */
  UB appendKeySegment(Object val);

  /**
   * Appends key segment to the URI, for multiple keys.
   *
   * @param segmentValues segment values.
   * @return current URIBuilder instance
   */
  UB appendKeySegment(Map<String, Object> segmentValues);

  /**
   * Appends property segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  UB appendPropertySegment(String segmentValue);

  /**
   * Appends navigation segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  UB appendNavigationSegment(String segmentValue);

  /**
   * Appends derived entity type segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  UB appendDerivedEntityTypeSegment(String segmentValue);

  /**
   * Appends value segment to the URI.
   *
   * @return current URIBuilder instance
   */
  UB appendValueSegment();

  /**
   * Appends operation (action or function) segment to the URI.
   *
   * @param operation Operation (action or function) name
   * @return current URIBuilder instance
   */
  UB appendOperationCallSegment(String operation);

  /**
   * Appends operation (action or function) segment to the URI.
   *
   * @param operation Operation (action or function) name
   * @param arguments Operation arguments
   * @return current URIBuilder instance
   */
  UB appendOperationCallSegment(String operation, Map<String, Object> arguments);

  /**
   * Appends metadata segment to the URI.
   *
   * @return current URIBuilder instance
   */
  UB appendMetadataSegment();

  /**
   * Appends batch segment to the URI.
   *
   * @return current URIBuilder instance
   */
  UB appendBatchSegment();

  /**
   * Adds count query option.
   *
   * @return current URIBuilder instance
   */
  UB count();

  /**
   * Adds expand query option.
   *
   * @param expandItems items to be expanded in-line
   * @return current URIBuilder instance
   * @see QueryOption#EXPAND
   */
  UB expand(String... expandItems);

  /**
   * Adds format query option.
   *
   * @param format media type acceptable in a response.
   * @return current URIBuilder instance
   * @see QueryOption#FORMAT
   */
  UB format(String format);

  /**
   * Adds filter for filter query option.
   *
   * @param filter filter instance (to be obtained via <tt>ODataFilterFactory</tt>): note that <tt>build()</tt> method
   * will be immediately invoked.
   * @return current URIBuilder instance
   * @see QueryOption#FILTER
   * @see URIFilter
   * @see org.apache.olingo.client.api.uri.filter.FilterFactory
   */
  UB filter(URIFilter filter);

  /**
   * Adds filter query option.
   *
   * @param filter filter string.
   * @return current URIBuilder instance
   * @see QueryOption#FILTER
   */
  UB filter(String filter);

  /**
   * Adds select query option.
   *
   * @param selectItems select items
   * @return current URIBuilder instance
   * @see QueryOption#SELECT
   */
  UB select(String... selectItems);

  /**
   * Adds orderby query option.
   *
   * @param order order string.
   * @return current URIBuilder instance
   * @see QueryOption#ORDERBY
   */
  UB orderBy(String order);

  /**
   * Adds top query option.
   *
   * @param top maximum number of entities to be returned.
   * @return current URIBuilder instance
   * @see QueryOption#TOP
   */
  UB top(int top);

  /**
   * Adds skip query option.
   *
   * @param skip number of entities to be skipped into the response.
   * @return current URIBuilder instance
   * @see QueryOption#SKIP
   */
  UB skip(int skip);

  /**
   * Adds skiptoken query option.
   *
   * @param skipToken opaque token.
   * @return current URIBuilder instance
   * @see QueryOption#SKIPTOKEN
   */
  UB skipToken(String skipToken);

  /**
   * Build OData URI.
   *
   * @return OData URI.
   */
  URI build();
}
