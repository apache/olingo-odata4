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
package org.apache.olingo.client.api.uri.v4;

import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.client.api.uri.CommonURIBuilder;

public interface URIBuilder extends CommonURIBuilder<URIBuilder> {

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
  URIBuilder expandWithOptions(String expandItem, Map<String, Object> options);
}
