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
package org.apache.olingo.client.core.uri;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.olingo.client.api.Configuration;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.URIBuilder;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.client.api.uri.URISearch;
import org.apache.olingo.commons.api.edm.EdmEnumType;
import org.apache.olingo.commons.core.Decoder;
import org.apache.olingo.commons.core.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URIBuilderImpl implements URIBuilder {

  /**
   * Constructor.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   */
  public URIBuilderImpl(final Configuration configuration, final String serviceRoot) {
    this.configuration = configuration;

    segments.add(new Segment(SegmentType.SERVICEROOT, serviceRoot));
  }

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

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(URIBuilderImpl.class);

  private final Configuration configuration;

  protected final List<Segment> segments = new ArrayList<Segment>();

  /**
   * Insertion-order map of query options.
   */
  protected final Map<String, String> queryOptions = new LinkedHashMap<String, String>();

  /**
   * Insertion-order map of custom query options.
   */
  protected final Map<String, String> customQueryOptions = new LinkedHashMap<String, String>();
  
  /**
   * Insertion-order map of parameter aliases.
   */
  protected final Map<String, String> parameters = new LinkedHashMap<String, String>();

  @Override
  public URIBuilder addQueryOption(final QueryOption option, final String value) {
    return addQueryOption(option.toString(), value, false);
  }

  @Override
  public URIBuilder replaceQueryOption(final QueryOption option, final String value) {
    return addQueryOption(option.toString(), value, true);
  }

  @Override
  public URIBuilder addQueryOption(final String option, final String value, final boolean replace) {
    final StringBuilder builder = new StringBuilder();
    if (!replace && queryOptions.containsKey(option)) {
      builder.append(queryOptions.get(option)).append(',');
    }
    builder.append(value);
    queryOptions.put(option, builder.toString());
    return this;
  }

  @Override
  public URIBuilder addParameterAlias(final String alias, final String exp) {
    parameters.put(alias, exp);
    return this;
  }

  @Override
  public URIBuilder appendEntitySetSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITYSET, segmentValue));
    return this;
  }

  @Override
  public URIBuilder appendKeySegment(final Object val) {
    final String segValue = URIUtils.escape(val);

    segments.add(configuration.isKeyAsSegment()
        ? new Segment(SegmentType.KEY_AS_SEGMENT, segValue)
        : new Segment(SegmentType.KEY, "(" + segValue + ")"));

    return this;
  }

  @Override
  public URIBuilder appendKeySegment(final Map<String, Object> segmentValues) {
    if (!configuration.isKeyAsSegment()) {
      final String key = buildMultiKeySegment(segmentValues, true, ',');
      if (StringUtils.isEmpty(key)) {
        segments.add(new Segment(SegmentType.KEY, noKeysWrapper()));
      } else {
        segments.add(new Segment(SegmentType.KEY, key));
      }
    }

    return this;
  }

  @Override
  public URIBuilder appendPropertySegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.PROPERTY, segmentValue));
    return this;

  }

  @Override
  public URIBuilder appendNavigationSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.NAVIGATION, segmentValue));
    return this;
  }

  @Override
  public URIBuilder appendDerivedEntityTypeSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.DERIVED_ENTITY_TYPE, segmentValue));
    return this;
  }

  @Override
  public URIBuilder appendValueSegment() {
    segments.add(new Segment(SegmentType.VALUE, SegmentType.VALUE.getValue()));
    return this;
  }

  @Override
  public URIBuilder appendCountSegment() {
    segments.add(new Segment(SegmentType.COUNT, SegmentType.COUNT.getValue()));
    return this;
  }

  @Override
  public URIBuilder appendActionCallSegment(final String action) {
    segments.add(new Segment(
        segments.size() == 1 ? SegmentType.UNBOUND_ACTION : SegmentType.BOUND_ACTION, action));
    return this;
  }

  @Override
  public URIBuilder appendOperationCallSegment(final String operation) {
    segments.add(new Segment(
        segments.size() == 1 ? SegmentType.UNBOUND_OPERATION : SegmentType.BOUND_OPERATION, operation));
    return this;
  }

  @Override
  public URIBuilder appendMetadataSegment() {
    segments.add(new Segment(SegmentType.METADATA, SegmentType.METADATA.getValue()));
    return this;
  }

  @Override
  public URIBuilder appendBatchSegment() {
    segments.add(new Segment(SegmentType.BATCH, SegmentType.BATCH.getValue()));
    return this;
  }

  @Override
  public URIBuilder count() {
    segments.add(new Segment(SegmentType.ROOT_QUERY_OPTION, "$" + QueryOption.COUNT.toString()));
    return this;
  }

  @Override
  public URIBuilder expand(final String... expandItems) {
    return addQueryOption(QueryOption.EXPAND, StringUtils.join(expandItems, ","));
  }

  @Override
  public URIBuilder format(final String format) {
    return replaceQueryOption(QueryOption.FORMAT, format);
  }

  @Override
  public URIBuilder filter(final URIFilter filter) {
    URIBuilder result;
    // decode in order to support @ in parameter aliases
    result = filter(Decoder.decode(filter.build()));
    return result;
  }

  @Override
  public URIBuilder filter(final String filter) {
    return replaceQueryOption(QueryOption.FILTER, filter);
  }

  @Override
  public URIBuilder select(final String... selectItems) {
    return addQueryOption(QueryOption.SELECT, StringUtils.join(selectItems, ","));
  }

  @Override
  public URIBuilder orderBy(final String order) {
    return replaceQueryOption(QueryOption.ORDERBY, order);
  }

  @Override
  public URIBuilder top(final int top) {
    return replaceQueryOption(QueryOption.TOP, String.valueOf(top));
  }

  @Override
  public URIBuilder skip(final int skip) {
    return replaceQueryOption(QueryOption.SKIP, String.valueOf(skip));
  }

  @Override
  public URIBuilder skipToken(final String skipToken) {
    return replaceQueryOption(QueryOption.SKIPTOKEN, skipToken);
  }

  @Override
  public URI build() {
    final StringBuilder segmentsBuilder = new StringBuilder();

    for (Segment seg : segments) {
      if (segmentsBuilder.length() > 0 && seg.getType() != SegmentType.KEY) {
        switch (seg.getType()) {
        case BOUND_OPERATION:
          segmentsBuilder.append(getBoundOperationSeparator());
          break;
        case BOUND_ACTION:
          segmentsBuilder.append(getBoundOperationSeparator());
          break;
        default:
          if (segmentsBuilder.length() > 0 && segmentsBuilder.charAt(segmentsBuilder.length() - 1) != '/') {
            segmentsBuilder.append('/');
          }
        }
      }

      if (seg.getType() == SegmentType.ENTITY) {
        segmentsBuilder.append(seg.getType().getValue());
      } else {
        segmentsBuilder.append(seg.getValue());
      }
      if (seg.getType() == SegmentType.BOUND_OPERATION || seg.getType() == SegmentType.UNBOUND_OPERATION) {
        segmentsBuilder.append(getOperationInvokeMarker());
      }
    }

    try {
      if ((customQueryOptions.size() + queryOptions.size() + parameters.size()) > 0) {
        segmentsBuilder.append("?");
        List<NameValuePair> list1 = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> option : queryOptions.entrySet()) {
          list1.add(new BasicNameValuePair("$" + option.getKey(), option.getValue()));
        }
        for (Map.Entry<String, String> parameter : parameters.entrySet()) {
          list1.add(new BasicNameValuePair("@" + parameter.getKey(), parameter.getValue()));
        }
        for (Map.Entry<String, String> customOption : customQueryOptions.entrySet()) {
          list1.add(new BasicNameValuePair(customOption.getKey(), customOption.getValue()));
        }
        // don't use UriBuilder.build():
        // it will try to call URLEncodedUtils.format(Iterable<>,Charset) method,
        // which works in desktop java application, however, throws NoSuchMethodError in android OS,
        // so here manually construct the URL by its overload URLEncodedUtils.format(List<>,String).
        final String queryStr = encodeQueryParameter(list1);
        segmentsBuilder.append(queryStr);
      }

      return URI.create(segmentsBuilder.toString());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Could not build valid URI", e);
    }
  }

  private String encodeQueryParameter(List<NameValuePair> list) {
    final StringBuilder builder = new StringBuilder();

    for (NameValuePair pair : list) {
      if (builder.length() > 0) {
        builder.append("&");
      }

      builder.append(Encoder.encode(pair.getName()));
      builder.append("=");
      builder.append(Encoder.encode(pair.getValue()));
    }

    return builder.toString();
  }

  @Override
  public String toString() {
    return build().toASCIIString();
  }

  protected String buildMultiKeySegment(final Map<String, Object> segmentValues, final boolean escape,
      final char sperator) {
    if (segmentValues == null || segmentValues.isEmpty()) {
      return StringUtils.EMPTY;
    } else {
      final StringBuilder keyBuilder = new StringBuilder().append('(');
      for (Map.Entry<String, Object> entry : segmentValues.entrySet()) {
        keyBuilder.append(entry.getKey()).append('=').append(
            escape ? URIUtils.escape(entry.getValue()) : entry.getValue());
        keyBuilder.append(sperator);
      }
      keyBuilder.deleteCharAt(keyBuilder.length() - 1).append(')');

      return keyBuilder.toString();
    }
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

  protected String noKeysWrapper() {
    return "()";
  }

  protected char getBoundOperationSeparator() {
    return '/';
  }

  protected String getOperationInvokeMarker() {
    return "()";
  }

  @Override
  public URIBuilder appendSingletonSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.SINGLETON, segmentValue));
    return this;
  }

  @Override
  public URIBuilder appendEntityIdSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITY, null));
    return addQueryOption(QueryOption.ID, segmentValue);
  }

  @Override
  public URIBuilder appendRefSegment() {
    segments.add(new Segment(SegmentType.REF, SegmentType.REF.getValue()));
    return this;
  }

  @Override
  public URIBuilder appendCrossjoinSegment(final String... segmentValues) {
    final StringBuilder segValue = new StringBuilder(SegmentType.CROSS_JOIN.getValue()).
        append('(').append(StringUtils.join(segmentValues, ",")).append(')');
    segments.add(new Segment(SegmentType.CROSS_JOIN, segValue.toString()));
    return this;
  }

  @Override
  public URIBuilder appendAllSegment() {
    segments.add(new Segment(SegmentType.ALL, SegmentType.ALL.getValue()));
    return this;
  }

  @Override
  public URIBuilder id(final String idValue) {
    return addQueryOption(QueryOption.ID, idValue);
  }

  @Override
  public URIBuilder search(final URISearch search) {
    return search(search.build());
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
  public URIBuilder expandWithOptions(final String expandItem, final Map<QueryOption, Object> options) {
    return expandWithOptions(expandItem, false, false, options);
  }

  @Override
  public URIBuilder expandWithOptions(String expandItem, boolean pathRef,
      boolean pathCount, Map<QueryOption, Object> options) {
    final Map<String, Object> _options = new LinkedHashMap<String, Object>();
    for (Map.Entry<QueryOption, Object> entry : options.entrySet()) {
      _options.put("$" + entry.getKey().toString(), entry.getValue());
    }
    String path = pathRef?"/$ref":pathCount?"/$count":StringUtils.EMPTY;
    return expand(expandItem + buildMultiKeySegment(_options, false, ';')+path);    
  }
  
  @Override
  public URIBuilder expandWithSelect(final String expandItem, final String... selectItems) {
    return expand(expandItem + "($select=" + StringUtils.join(selectItems, ",") + ")");
  }

  @Override
  public URIBuilder addCustomQueryOption(String customName, String customValue) {
    customQueryOptions.put(customName, customValue);
    return this;
  }
}
