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
package org.apache.olingo.client.core.uri.v3;

import org.apache.olingo.client.core.uri.URIUtils;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.olingo.client.api.v3.Configuration;
import org.apache.olingo.client.api.uri.QueryOption;
import org.apache.olingo.client.api.uri.SegmentType;
import org.apache.olingo.client.api.uri.v3.URIBuilder;
import org.apache.olingo.client.core.uri.AbstractURIBuilder;
import org.apache.olingo.commons.api.edm.constants.ODataServiceVersion;

public class URIBuilderImpl extends AbstractURIBuilder<URIBuilder> implements URIBuilder {

  private static final long serialVersionUID = -3506851722447870532L;

  private final ODataServiceVersion version;

  private final Configuration configuration;

  public URIBuilderImpl(final ODataServiceVersion version, final Configuration configuration,
          final String serviceRoot) {

    super(version, serviceRoot);
    this.version = version;
    this.configuration = configuration;
  }

  @Override
  protected URIBuilder getThis() {
    return this;
  }

  @Override
  protected char getBoundOperationSeparator() {
    return '/';
  }

  @Override
  protected char getDerivedEntityTypeSeparator() {
    return '/';
  }

  @Override
  public URIBuilder appendLinksSegment(final String segmentValue) {
    segments.add(new Segment(SegmentType.LINKS, SegmentType.LINKS.getValue()));
    segments.add(new Segment(SegmentType.ENTITYSET, segmentValue));

    return getThis();
  }

  @Override
  protected String noKeysWrapper() {
    return StringUtils.EMPTY;
  }

  @Override
  public URIBuilder appendKeySegment(final Object val) {
    if (configuration.isKeyAsSegment()) {
      final String segValue = URIUtils.escape(version, val);
      segments.add(new Segment(SegmentType.KEY_AS_SEGMENT, segValue));
    } else {
      super.appendKeySegment(val);
    }

    return getThis();
  }

  @Override
  public URIBuilder appendKeySegment(final Map<String, Object> segmentValues) {
    if (!configuration.isKeyAsSegment()) {
      super.appendKeySegment(segmentValues);
    }

    return getThis();
  }

  @Override
  public URIBuilder inlineCount(final InlineCount inlineCount) {
    return addQueryOption(QueryOption.INLINECOUNT, inlineCount.name());
  }

}
