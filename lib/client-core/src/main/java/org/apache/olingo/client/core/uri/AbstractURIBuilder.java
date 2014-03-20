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

import org.apache.olingo.client.api.utils.URIUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractURIBuilder<UB extends CommonURIBuilder<?>> implements CommonURIBuilder<UB> {

  private static final long serialVersionUID = -3267515371720408124L;

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(CommonURIBuilder.class);

  protected static class Segment {

    private final SegmentType type;

    private final String value;

    public Segment(final SegmentType type, final String value) {
      this.type = type;
      this.value = value;
    }

    public SegmentType getType() {
      return type;
    }

    public String getValue() {
      return value;
    }

  }

  protected final List<Segment> segments = new ArrayList<Segment>();

  /**
   * Insertion-order map of query options.
   */
  protected final Map<String, String> queryOptions = new LinkedHashMap<String, String>();

  /**
   * Constructor.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   */
  protected AbstractURIBuilder(final String serviceRoot) {
    segments.add(new Segment(SegmentType.SERVICEROOT, serviceRoot));
  }

  protected abstract UB getThis();

  @Override
  public UB addQueryOption(final QueryOption option, final String value) {
    return addQueryOption(option.toString(), value);
  }

  @Override
  public UB addQueryOption(final String option, final String value) {
    queryOptions.put(option, value);
    return getThis();
  }

  @Override
  public UB appendEntitySetSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITYSET, segmentValue));
    return getThis();
  }

  @Override
  public UB appendKeySegment(final Object val) {
    final String segValue = URIUtils.escape(val);

    segments.add(new Segment(SegmentType.KEY, "(" + segValue + ")"));
    return getThis();
  }

  protected abstract String noKeysWrapper();

  @Override
  public UB appendKeySegment(final Map<String, Object> segmentValues) {
    if (segmentValues == null || segmentValues.isEmpty()) {
      segments.add(new Segment(SegmentType.KEY, noKeysWrapper()));
    } else {
      final StringBuilder keyBuilder = new StringBuilder().append('(');
      for (Map.Entry<String, Object> entry : segmentValues.entrySet()) {
        keyBuilder.append(entry.getKey()).append('=').append(URIUtils.escape(entry.getValue()));
        keyBuilder.append(',');
      }
      keyBuilder.deleteCharAt(keyBuilder.length() - 1).append(')');

      segments.add(new Segment(SegmentType.KEY, keyBuilder.toString()));
    }

    return getThis();
  }

  @Override
  public UB appendPropertySegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.PROPERTY, segmentValue));
    return getThis();

  }

  @Override
  public UB appendNavigationSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.NAVIGATION, segmentValue));
    return getThis();
  }

  @Override
  public UB appendDerivedEntityTypeSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.DERIVED_ENTITY_TYPE, segmentValue));
    return getThis();
  }

  @Override
  public UB appendValueSegment() {
    segments.add(new Segment(SegmentType.VALUE, SegmentType.VALUE.getValue()));
    return getThis();
  }

  @Override
  public UB appendOperationCallSegment(final String operation, final Map<String, Object> arguments) {
    segments.add(new Segment(
            segments.size() == 1 ? SegmentType.UNBOUND_OPERATION : SegmentType.BOUND_OPERATION, operation));
    return appendKeySegment(arguments);
  }

  @Override
  public UB appendMetadataSegment() {
    segments.add(new Segment(SegmentType.METADATA, SegmentType.METADATA.getValue()));
    return getThis();
  }

  @Override
  public UB appendBatchSegment() {
    segments.add(new Segment(SegmentType.BATCH, SegmentType.BATCH.getValue()));
    return getThis();
  }

  @Override
  public UB count() {
    segments.add(new Segment(SegmentType.ROOT_QUERY_OPTION, "$" + QueryOption.COUNT.toString()));
    return getThis();
  }

  @Override
  public UB expand(final String... expandItems) {
    return addQueryOption(QueryOption.EXPAND, StringUtils.join(expandItems, ","));
  }

  @Override
  public UB format(final String format) {
    return addQueryOption(QueryOption.FORMAT, format);
  }

  @Override
  public UB filter(final URIFilter filter) {
    return addQueryOption(QueryOption.FILTER, filter.build());
  }

  @Override
  public UB filter(final String filter) {
    return addQueryOption(QueryOption.FILTER, filter);
  }

  @Override
  public UB select(final String... selectItems) {
    return addQueryOption(QueryOption.SELECT, StringUtils.join(selectItems, ","));
  }

  @Override
  public UB orderBy(final String order) {
    return addQueryOption(QueryOption.ORDERBY, order);
  }

  @Override
  public UB top(final int top) {
    return addQueryOption(QueryOption.TOP, String.valueOf(top));
  }

  @Override
  public UB skip(final int skip) {
    return addQueryOption(QueryOption.SKIP, String.valueOf(skip));
  }

  @Override
  public UB skipToken(final String skipToken) {
    return addQueryOption(QueryOption.SKIPTOKEN, skipToken);
  }

  protected abstract char getBoundOperationSeparator();

  protected abstract char getDerivedEntityTypeSeparator();

  @Override
  public URI build() {
    final StringBuilder segmentsBuilder = new StringBuilder();
    for (Segment seg : segments) {
      if (segmentsBuilder.length() > 0 && seg.getType() != SegmentType.KEY) {
        switch (seg.getType()) {
          case BOUND_OPERATION:
            segmentsBuilder.append(getBoundOperationSeparator());
            break;

          case DERIVED_ENTITY_TYPE:
            segmentsBuilder.append(getDerivedEntityTypeSeparator());
            break;

          default:
            segmentsBuilder.append('/');
        }
      }

      if (seg.getType() == SegmentType.ENTITY) {
        segmentsBuilder.append(seg.getType().getValue());
      } else {
        segmentsBuilder.append(seg.getValue());
      }
    }

    try {
      final org.apache.http.client.utils.URIBuilder builder =
              new org.apache.http.client.utils.URIBuilder(segmentsBuilder.toString());

      for (Map.Entry<String, String> option : queryOptions.entrySet()) {
        builder.addParameter("$" + option.getKey(), option.getValue());
      }

      return builder.build().normalize();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Could not build valid URI", e);
    }
  }

  @Override
  public String toString() {
    return build().toASCIIString();
  }

}
