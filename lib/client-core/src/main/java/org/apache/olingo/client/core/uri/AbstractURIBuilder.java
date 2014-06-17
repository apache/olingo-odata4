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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.olingo.client.api.CommonConfiguration;
import org.apache.olingo.client.api.uri.CommonURIBuilder;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.URIFilter;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractURIBuilder<UB extends CommonURIBuilder<?>> implements CommonURIBuilder<UB> {

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
  private static final long serialVersionUID = -3267515371720408124L;

  /**
   * Logger.
   */
  protected static final Logger LOG = LoggerFactory.getLogger(CommonURIBuilder.class);

  private final ODataServiceVersion version;

  private final CommonConfiguration configuration;

  protected final List<Segment> segments = new ArrayList<Segment>();

  /**
   * Insertion-order map of query options.
   */
  protected final Map<String, String> queryOptions = new LinkedHashMap<String, String>();

  /**
   * Insertion-order map of parameter aliases.
   */
  protected final Map<String, String> parameters = new LinkedHashMap<String, String>();

  /**
   * Constructor.
   *
   * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the data
   * service.
   */
  protected AbstractURIBuilder(
          final ODataServiceVersion version, final CommonConfiguration configuration, final String serviceRoot) {

    this.version = version;
    this.configuration = configuration;
    segments.add(new Segment(SegmentType.SERVICEROOT, serviceRoot));
  }

  protected abstract UB getThis();

  @Override
  public UB addQueryOption(final QueryOption option, final String value) {
    return addQueryOption(option.toString(), value);
  }

  @Override
  public UB addQueryOption(final String option, final String value) {
    final StringBuilder builder = new StringBuilder();
    if (queryOptions.containsKey(option)) {
      builder.append(queryOptions.get(option)).append(',');
    }
    builder.append(value);
    queryOptions.put(option, builder.toString());
    return getThis();
  }

  @Override
  public UB addParameterAlias(final String alias, final String exp) {
    parameters.put(alias, exp);
    return getThis();
  }

  @Override
  public UB appendEntitySetSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.ENTITYSET, segmentValue));
    return getThis();
  }

  @Override
  public UB appendKeySegment(final Object val) {
    final String segValue = URIUtils.escape(version, val);

    segments.add(configuration.isKeyAsSegment()
            ? new Segment(SegmentType.KEY_AS_SEGMENT, segValue)
            : new Segment(SegmentType.KEY, "(" + segValue + ")"));

    return getThis();
  }

  protected abstract String noKeysWrapper();

  @Override
  public UB appendKeySegment(final Map<String, Object> segmentValues) {
    if (!configuration.isKeyAsSegment()) {
      final String key = buildMultiKeySegment(segmentValues, true);
      if (StringUtils.isEmpty(key)) {
        segments.add(new Segment(SegmentType.KEY, noKeysWrapper()));
      } else {
        segments.add(new Segment(SegmentType.KEY, key));
      }
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
  public UB appendOperationCallSegment(final String operation) {
    segments.add(new Segment(
            segments.size() == 1 ? SegmentType.UNBOUND_OPERATION : SegmentType.BOUND_OPERATION, operation));
    return getThis();
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
    UB result;
    try {
      // decode in order to support @ in parameter aliases
      result = filter(URLDecoder.decode(filter.build(), Constants.UTF8));
    } catch (UnsupportedEncodingException e) {
      result = filter(filter.build());
    }
    return result;
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

  protected abstract String getOperationInvokeMarker();

  @Override
  public URI build() {
    final StringBuilder segmentsBuilder = new StringBuilder();
    for (Segment seg : segments) {
      if (segmentsBuilder.length() > 0 && seg.getType() != SegmentType.KEY) {
        switch (seg.getType()) {
          case BOUND_OPERATION:
            segmentsBuilder.append(getBoundOperationSeparator());
            break;

          default:
            if(segmentsBuilder.length() > 0 && segmentsBuilder.charAt(segmentsBuilder.length()-1) != '/') {
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
      StringBuilder sb = segmentsBuilder;
      if((queryOptions.size() + parameters.size()) > 0){
          sb.append("?");
          List<NameValuePair> list1 = new LinkedList<NameValuePair>();
          for (Map.Entry<String, String> option : queryOptions.entrySet()) {
        	list1.add(new BasicNameValuePair("$" + option.getKey(), option.getValue()));
          }
          for (Map.Entry<String, String> parameter : parameters.entrySet()) {
    		list1.add(new BasicNameValuePair("@" + parameter.getKey(), parameter.getValue()));
          }

          // don't use UriBuilder.build():
          // it will try to call URLEncodedUtils.format(Iterable<>,Charset) method,
          // which works in desktop java application, however, throws NoSuchMethodError in android OS,
          // so here manually construct the URL by its overload URLEncodedUtils.format(List<>,String).
          String queryStr = URLEncodedUtils.format(list1, "UTF-8");
          sb.append(queryStr);
      }
      
      return URI.create(sb.toString());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Could not build valid URI", e);
    }
  }

  @Override
  public String toString() {
    return build().toASCIIString();
  }

  protected String buildMultiKeySegment(final Map<String, Object> segmentValues, final boolean escape) {
    if (segmentValues == null || segmentValues.isEmpty()) {
      return StringUtils.EMPTY;
    } else {
      final StringBuilder keyBuilder = new StringBuilder().append('(');
      for (Map.Entry<String, Object> entry : segmentValues.entrySet()) {
        keyBuilder.append(entry.getKey()).append('=').append(
                escape ? URIUtils.escape(version, entry.getValue()) : entry.getValue());
        keyBuilder.append(',');
      }
      keyBuilder.deleteCharAt(keyBuilder.length() - 1).append(')');

      return keyBuilder.toString();
    }
  }
}
