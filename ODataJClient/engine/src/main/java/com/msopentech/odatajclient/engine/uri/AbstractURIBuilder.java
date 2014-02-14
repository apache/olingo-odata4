/**
 * Copyright © Microsoft Open Technologies, Inc.
 *
 * All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 *
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 */
package com.msopentech.odatajclient.engine.uri;

import com.msopentech.odatajclient.engine.client.Configuration;
import com.msopentech.odatajclient.engine.uri.filter.ODataFilter;
import com.msopentech.odatajclient.engine.utils.URIUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractURIBuilder implements URIBuilder {

    private static final long serialVersionUID = -3267515371720408124L;

    /**
     * Logger.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(URIBuilder.class);

    protected final List<URIBuilder.Segment> segments;

    /**
     * Case-insensitive map of query options.
     */
    protected final Map<String, String> queryOptions = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

    /**
     * Constructor.
     *
     * @param serviceRoot absolute URL (schema, host and port included) representing the location of the root of the
     * data service.
     */
    protected AbstractURIBuilder(final String serviceRoot) {
        segments = new ArrayList<URIBuilder.Segment>();
        segments.add(new URIBuilder.Segment(SegmentType.SERVICEROOT, serviceRoot));
    }

    protected abstract Configuration getConfiguration();

    @Override
    public URIBuilder addQueryOption(final QueryOption option, final String value) {
        return addQueryOption(option.toString(), value);
    }

    @Override
    public URIBuilder addQueryOption(final String option, final String value) {
        queryOptions.put(option, value);
        return this;
    }

    @Override
    public URIBuilder appendEntitySetSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.ENTITYSET, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendEntityTypeSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.ENTITYTYPE, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendKeySegment(final Object val) {
        final String segValue = URIUtils.escape(val);

        segments.add(getConfiguration().isKeyAsSegment()
                ? new URIBuilder.Segment(SegmentType.KEY_AS_SEGMENT, segValue)
                : new URIBuilder.Segment(SegmentType.KEY, "(" + segValue + ")"));
        return this;
    }

    @Override
    public URIBuilder appendKeySegment(final Map<String, Object> segmentValues) {
        if (!getConfiguration().isKeyAsSegment()) {
            final StringBuilder keyBuilder = new StringBuilder().append('(');
            for (Map.Entry<String, Object> entry : segmentValues.entrySet()) {
                keyBuilder.append(entry.getKey()).append('=').append(URIUtils.escape(entry.getValue()));
                keyBuilder.append(',');
            }
            keyBuilder.deleteCharAt(keyBuilder.length() - 1).append(')');

            segments.add(new URIBuilder.Segment(SegmentType.KEY, keyBuilder.toString()));
        }

        return this;
    }

    @Override
    public URIBuilder appendNavigationLinkSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.NAVIGATION, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendStructuralSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.STRUCTURAL, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendLinksSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.LINKS, SegmentType.LINKS.getValue()));
        segments.add(new URIBuilder.Segment(SegmentType.ENTITYTYPE, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendValueSegment() {
        segments.add(new URIBuilder.Segment(SegmentType.VALUE, SegmentType.VALUE.getValue()));
        return this;
    }

    @Override
    public URIBuilder appendCountSegment() {
        segments.add(new URIBuilder.Segment(SegmentType.COUNT, SegmentType.COUNT.getValue()));
        return this;
    }

    @Override
    public URIBuilder appendFunctionImportSegment(final String segmentValue) {
        segments.add(new URIBuilder.Segment(SegmentType.FUNCTIONIMPORT, segmentValue));
        return this;
    }

    @Override
    public URIBuilder appendMetadataSegment() {
        segments.add(new URIBuilder.Segment(SegmentType.METADATA, SegmentType.METADATA.getValue()));
        return this;
    }

    @Override
    public URIBuilder appendBatchSegment() {
        segments.add(new URIBuilder.Segment(SegmentType.BATCH, SegmentType.BATCH.getValue()));
        return this;
    }

    @Override
    public URIBuilder expand(final String entityName) {
        return addQueryOption(QueryOption.EXPAND, entityName);
    }

    @Override
    public URIBuilder format(final String format) {
        return addQueryOption(QueryOption.FORMAT, format);
    }

    @Override
    public URIBuilder filter(final ODataFilter filter) {
        return addQueryOption(QueryOption.FILTER, filter.build());
    }

    @Override
    public URIBuilder filter(final String filter) {
        return addQueryOption(QueryOption.FILTER, filter);
    }

    @Override
    public URIBuilder select(final String select) {
        return addQueryOption(QueryOption.SELECT, select);
    }

    @Override
    public URIBuilder orderBy(final String order) {
        return addQueryOption(QueryOption.ORDERBY, order);
    }

    @Override
    public URIBuilder top(final int top) {
        return addQueryOption(QueryOption.TOP, String.valueOf(top));
    }

    @Override
    public URIBuilder skip(final int skip) {
        return addQueryOption(QueryOption.SKIP, String.valueOf(skip));
    }

    @Override
    public URIBuilder skipToken(final String skipToken) {
        return addQueryOption(QueryOption.SKIPTOKEN, skipToken);
    }

    @Override
    public URIBuilder inlineCount() {
        return addQueryOption(QueryOption.INLINECOUNT, "allpages");
    }

    @Override
    public URI build() {
        final StringBuilder segmentsBuilder = new StringBuilder();
        for (URIBuilder.Segment seg : segments) {
            if (segmentsBuilder.length() > 0 && seg.getType() != SegmentType.KEY) {
                segmentsBuilder.append('/');
            }

            segmentsBuilder.append(seg.getValue());
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
