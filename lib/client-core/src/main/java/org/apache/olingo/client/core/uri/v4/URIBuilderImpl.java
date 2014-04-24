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
package org.apache.olingo.client.core.uri.v4;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.v4.URIBuilder;
import org.apache.olingo.client.api.v4.Configuration;
import org.apache.olingo.client.core.uri.AbstractURIBuilder;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class URIBuilderImpl extends AbstractURIBuilder<URIBuilder> implements URIBuilder {

  private static final long serialVersionUID = -3506851722447870532L;

  public URIBuilderImpl(
          final ODataServiceVersion version, final Configuration configuration, final String serviceRoot) {

    super(version, configuration, serviceRoot);
  }

  @Override
  public URIBuilder appendKeySegment(final EdmEnumType enumType, final String memberName) {
    return appendKeySegment(enumType.toUriLiteral(memberName));
  }

  @Override
  public URIBuilder appendKeySegment(final Map<String, Pair<EdmEnumType, String>> enumValues,
          final Map<String, Object> segmentValues) {

    final Map<String, Object> values = new LinkedHashMap<String, Object>();
    for (Map.Entry<String, Pair<EdmEnumType, String>> entry : enumValues.entrySet()) {
      values.put(entry.getKey(), entry.getValue().getKey().toUriLiteral(entry.getValue().getValue()));
    }
    values.putAll(segmentValues);

    return appendKeySegment(values);
  }

  @Override
  protected URIBuilder getThis() {
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
  protected String getOperationInvokeMarker() {
    return "()";
  }

  @Override
  public URIBuilder appendSingletonSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.SINGLETON, segmentValue));
    return getThis();
  }

  @Override
  public URIBuilder appendEntityIdSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITY, null));
    return addQueryOption(QueryOption.ID, segmentValue);
  }

  @Override
  public URIBuilder appendRefSegment() {
    segments.add(new Segment(SegmentType.REF, SegmentType.REF.getValue()));
    return getThis();
  }

  @Override
  public URIBuilder appendCrossjoinSegment(final String... segmentValues) {
    final StringBuilder segValue = new StringBuilder(SegmentType.CROSS_JOIN.getValue()).
            append('(').append(StringUtils.join(segmentValues, ",")).append(')');
    segments.add(new Segment(SegmentType.CROSS_JOIN, segValue.toString()));
    return getThis();
  }

  @Override
  public URIBuilder appendAllSegment() {
    segments.add(new Segment(SegmentType.ALL, SegmentType.ALL.getValue()));
    return getThis();
  }

  @Override
  public URIBuilder id(final String idValue) {
    return addQueryOption(QueryOption.ID, idValue);
  }

  @Override
  public URIBuilder search(final String expression) {
    return addQueryOption(QueryOption.SEARCH, expression);
  }

  @Override
  public URIBuilder count(final boolean value) {
    return addQueryOption(QueryOption.COUNT, Boolean.toString(value));
  }

  @Override
  public URIBuilder expandWithOptions(final String expandItem, final Map<String, Object> options) {
    return expand(expandItem + buildMultiKeySegment(options, false));
  }

  @Override
  public URIBuilder expandWithSelect(final String expandItem, final String... selectItems) {
    return expand(expandItem + "($select=" + StringUtils.join(selectItems, ",") + ")");
  }
}
