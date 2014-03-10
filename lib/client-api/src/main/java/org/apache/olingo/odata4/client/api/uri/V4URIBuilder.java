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
package org.apache.olingo.odata4.client.api.uri;

public interface V4URIBuilder extends URIBuilder<V4URIBuilder> {

  /**
   * Appends Singleton segment to the URI.
   *
   * @param segmentValue segment value.
   * @return current URIBuilder instance
   */
  V4URIBuilder appendSingletonSegment(String segmentValue);

  /**
   * Appends entity-id segment to the URI.
   *
   * @param segmentValue segment value
   * @return current URIBuilder instance
   */
  V4URIBuilder appendEntityIdSegment(String segmentValue);

  /**
   * Appends ref segment to the URI.
   *
   * @return current URIBuilder instance
   */
  V4URIBuilder appendRefSegment();

  /**
   * Appends cross join segment to the URI.
   *
   * @param segmentValues segment values.
   * @return current URIBuilder instance
   */
  V4URIBuilder appendCrossjoinSegment(String... segmentValues);

  /**
   * Appends all segment to the URI.
   *
   * @return current URIBuilder instance
   */
  V4URIBuilder appendAllSegment();

  /**
   * Adds id query option.
   *
   * @param idValue opaque token.
   * @return current URIBuilder instance
   * @see QueryOption#ID
   */
  V4URIBuilder id(String idValue);

  /**
   * Appends count query option.
   *
   * @param value true or false
   * @return current URIBuilder instance
   * @see QueryOption#COUNT
   */
  V4URIBuilder count(boolean value);

  /**
   * Appends search query option.
   *
   * @param expression search expression
   * @return current URIBuilder instance
   * @see QueryOption#SEARCH
   */
  V4URIBuilder search(String expression);
}
