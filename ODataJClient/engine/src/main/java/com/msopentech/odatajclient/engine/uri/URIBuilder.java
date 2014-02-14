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

import com.msopentech.odatajclient.engine.uri.filter.FilterFactory;
import com.msopentech.odatajclient.engine.uri.filter.ODataFilter;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;

/**
 * OData URI builder.
 */
public interface URIBuilder extends Serializable {

    public static class Segment {

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
     * Adds the specified query option to the URI.
     *
     * @param option query option.
     * @param value query option value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder addQueryOption(QueryOption option, String value);

    /**
     * Adds the specified (custom) query option to the URI.
     *
     * @param option query option.
     * @param value query option value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder addQueryOption(String option, String value);

    /**
     * Append EntitySet segment to the URI.
     *
     * @param segmentValue segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendEntitySetSegment(String segmentValue);

    /**
     * Append EntityType segment to the URI.
     *
     * @param segmentValue segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendEntityTypeSegment(String segmentValue);

    /**
     * Append key segment to the URI.
     *
     * @param val segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendKeySegment(Object val);

    /**
     * Append key segment to the URI, for multiple keys.
     *
     * @param segmentValues segment values.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendKeySegment(Map<String, Object> segmentValues);

    /**
     * Append navigation link segment to the URI.
     *
     * @param segmentValue segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendNavigationLinkSegment(String segmentValue);

    /**
     * Append structural segment to the URI.
     *
     * @param segmentValue segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendStructuralSegment(String segmentValue);

    URIBuilder appendLinksSegment(String segmentValue);

    /**
     * Append value segment to the URI.
     *
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendValueSegment();

    /**
     * Append count segment to the URI.
     *
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendCountSegment();

    /**
     * Append function import segment to the URI.
     *
     * @param segmentValue segment value.
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendFunctionImportSegment(String segmentValue);

    /**
     * Append metadata segment to the URI.
     *
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendMetadataSegment();

    /**
     * Append batch segment to the URI.
     *
     * @return current ODataURIBuilder object.
     */
    URIBuilder appendBatchSegment();

    /**
     * Adds expand query option.
     *
     * @param entityName entity object to be in-line expanded.
     * @return current ODataURIBuilder object.
     * @see QueryOption#EXPAND
     */
    URIBuilder expand(String entityName);

    /**
     * Adds format query option.
     *
     * @param format media type acceptable in a response.
     * @return current ODataURIBuilder object.
     * @see QueryOption#FORMAT
     */
    URIBuilder format(String format);

    /**
     * Adds filter for filter query option.
     *
     * @param filter filter instance (to be obtained via <tt>ODataFilterFactory</tt>):
     * note that <tt>build()</tt> method will be immediately invoked.
     * @return current ODataURIBuilder object.
     * @see QueryOption#FILTER
     * @see ODataFilter
     * @see FilterFactory
     */
    URIBuilder filter(ODataFilter filter);

    /**
     * Adds filter query option.
     *
     * @param filter filter string.
     * @return current ODataURIBuilder object.
     * @see QueryOption#FILTER
     */
    URIBuilder filter(String filter);

    /**
     * Adds select query option.
     *
     * @param select select query option value.
     * @return current ODataURIBuilder object.
     * @see QueryOption#SELECT
     */
    URIBuilder select(String select);

    /**
     * Adds orderby query option.
     *
     * @param order order string.
     * @return current ODataURIBuilder object.
     * @see QueryOption#ORDERBY
     */
    URIBuilder orderBy(String order);

    /**
     * Adds top query option.
     *
     * @param top maximum number of entities to be returned.
     * @return current ODataURIBuilder object.
     * @see QueryOption#TOP
     */
    URIBuilder top(int top);

    /**
     * Adds skip query option.
     *
     * @param skip number of entities to be skipped into the response.
     * @return current ODataURIBuilder object.
     * @see QueryOption#SKIP
     */
    URIBuilder skip(int skip);

    /**
     * Adds skiptoken query option.
     *
     * @param skipToken opaque token.
     * @return current ODataURIBuilder object.
     * @see QueryOption#SKIPTOKEN
     */
    URIBuilder skipToken(String skipToken);

    /**
     * Adds inlinecount query option.
     *
     * @return current ODataURIBuilder object.
     * @see QueryOption#INLINECOUNT
     */
    URIBuilder inlineCount();

    /**
     * Build OData URI.
     *
     * @return OData URI.
     */
    URI build();

    /**
     * ${@inheritDoc }
     */
    @Override
    String toString();

}
