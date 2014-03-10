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
package org.apache.olingo.client.core.uri;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.V4URIBuilder;

public class V4URIBuilderImpl extends AbstractURIBuilder<V4URIBuilder> implements V4URIBuilder {

  private static final long serialVersionUID = -3506851722447870532L;

  public V4URIBuilderImpl(final String serviceRoot) {
    super(serviceRoot);
  }

  @Override
  protected V4URIBuilder getThis() {
    return this;
  }

  @Override
  protected String noKeysWrapper() {
    return "()";
  }

  @Override
  protected char getBoundOperationSeparator() {
    return '.';
  }

  @Override
  protected char getDerivedEntityTypeSeparator() {
    return '.';
  }

  @Override
  public V4URIBuilder appendSingletonSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.SINGLETON, segmentValue));
    return getThis();
  }

  @Override
  public V4URIBuilder appendEntityIdSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITY, null));
    return addQueryOption(QueryOption.ID, segmentValue);
  }

  @Override
  public V4URIBuilder appendRefSegment() {
    segments.add(new Segment(SegmentType.REF, SegmentType.REF.getValue()));
    return getThis();
  }

  @Override
  public V4URIBuilder appendCrossjoinSegment(final String... segmentValues) {
    StringBuilder segValue = new StringBuilder(SegmentType.CROSS_JOIN.getValue()).
            append('(').append(StringUtils.join(segmentValues, ",")).append(')');
    segments.add(new Segment(SegmentType.CROSS_JOIN, segValue.toString()));
    return getThis();
  }

  @Override
  public V4URIBuilder count(final boolean value) {
    return addQueryOption(QueryOption.COUNT, Boolean.toString(value));
  }

  @Override
  public V4URIBuilder appendAllSegment() {
    segments.add(new Segment(SegmentType.ALL, SegmentType.ALL.getValue()));
    return getThis();
  }

  @Override
  public V4URIBuilder id(final String idValue) {
    return addQueryOption(QueryOption.ID, idValue);
  }

  @Override
  public V4URIBuilder search(final String expression) {
    return addQueryOption(QueryOption.SEARCH, expression);
  }

}
