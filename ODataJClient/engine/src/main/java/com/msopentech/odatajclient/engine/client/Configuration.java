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
package com.msopentech.odatajclient.engine.client;

import com.msopentech.odatajclient.engine.client.http.DefaultHttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.DefaultHttpUriRequestFactory;
import com.msopentech.odatajclient.engine.client.http.HttpClientFactory;
import com.msopentech.odatajclient.engine.client.http.HttpUriRequestFactory;
import com.msopentech.odatajclient.engine.format.ODataFormat;
import com.msopentech.odatajclient.engine.format.ODataMediaFormat;
import com.msopentech.odatajclient.engine.format.ODataPubFormat;
import com.msopentech.odatajclient.engine.format.ODataValueFormat;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;

/**
 * Configuration wrapper.
 */
public interface Configuration extends Serializable {

    /**
     * Gets the configured OData format for AtomPub exchanges.
     * If this configuration parameter doesn't exist the JSON_FULL_METADATA format will be used as default.
     *
     * @return configured OData format for AtomPub if specified; JSON_FULL_METADATA format otherwise.
     * @see ODataPubFormat#JSON_FULL_METADATA
     */
    ODataPubFormat getDefaultPubFormat();

    /**
     * Sets the default OData format for AtomPub exchanges.
     *
     * @param format default format.
     */
    void setDefaultPubFormat(ODataPubFormat format);

    /**
     * Gets the configured OData format.
     * This value depends on what is returned from <tt>getDefaultPubFormat()</tt>.
     *
     * @return configured OData format
     * @see #getDefaultPubFormat()
     */
    ODataFormat getDefaultFormat();

    /**
     * Gets the configured OData value format.
     * If this configuration parameter doesn't exist the TEXT format will be used as default.
     *
     * @return configured OData value format if specified; TEXT format otherwise.
     * @see ODataValueFormat#TEXT
     */
    ODataValueFormat getDefaultValueFormat();

    /**
     * Sets the default OData value format.
     *
     * @param format default format.
     */
    void setDefaultValueFormat(ODataValueFormat format);

    /**
     * Gets the configured OData media format.
     * If this configuration parameter doesn't exist the APPLICATION_OCTET_STREAM format will be used as default.
     *
     * @return configured OData media format if specified; APPLICATION_OCTET_STREAM format otherwise.
     * @see ODataMediaFormat#WILDCARD
     */
    ODataMediaFormat getDefaultMediaFormat();

    /**
     * Sets the default OData media format.
     *
     * @param format default format.
     */
    void setDefaultMediaFormat(ODataMediaFormat format);

    /**
     * Gets the HttpClient factory to be used for executing requests.
     *
     * @return provided implementation (if configured via <tt>setHttpClientFactory</tt> or default.
     * @see DefaultHttpClientFactory
     */
    HttpClientFactory getHttpClientFactory();

    /**
     * Sets the HttpClient factory to be used for executing requests.
     *
     * @param factory implementation of <tt>HttpClientFactory</tt>.
     * @see HttpClientFactory
     */
    void setHttpClientFactory(HttpClientFactory factory);

    /**
     * Gets the HttpUriRequest factory for generating requests to be executed.
     *
     * @return provided implementation (if configured via <tt>setHttpUriRequestFactory</tt> or default.
     * @see DefaultHttpUriRequestFactory
     */
    HttpUriRequestFactory getHttpUriRequestFactory();

    /**
     * Sets the HttpUriRequest factory generating requests to be executed.
     *
     * @param factory implementation of <tt>HttpUriRequestFactory</tt>.
     * @see HttpUriRequestFactory
     */
    void setHttpUriRequestFactory(HttpUriRequestFactory factory);

    /**
     * Gets whether <tt>PUT</tt>, <tt>MERGE</tt>, <tt>PATCH</tt>, <tt>DELETE</tt> HTTP methods need to be translated to
     * <tt>POST</tt> with additional <tt>X-HTTTP-Method</tt> header.
     *
     * @return whether <tt>X-HTTTP-Method</tt> header is to be used
     */
    boolean isUseXHTTPMethod();

    /**
     * Sets whether <tt>PUT</tt>, <tt>MERGE</tt>, <tt>PATCH</tt>, <tt>DELETE</tt> HTTP methods need to be translated to
     * <tt>POST</tt> with additional <tt>X-HTTTP-Method</tt> header.
     *
     * @param value 'TRUE' to use tunneling.
     */
    void setUseXHTTPMethod(boolean value);

    /**
     * Checks whether URIs contain entity key between parentheses (standard) or instead as additional segment.
     * Example: http://services.odata.org/V4/OData/OData.svc/Products(0) or
     * http://services.odata.org/V4/OData/OData.svc/Products/0
     *
     * @return
     */
    boolean isKeyAsSegment();

    /**
     * Sets whether URIs shall be built with entity key between parentheses (standard) or instead as additional
     * segment.
     * Example: http://services.odata.org/V4/OData/OData.svc/Products(0) or
     * http://services.odata.org/V4/OData/OData.svc/Products/0
     *
     * @return whether URIs shall be built with entity key between parentheses (standard) or instead as additional
     * segment.
     */
    void setKeyAsSegment(boolean value);

    /**
     * Checks whether Gzip compression (e.g. support for <tt>Accept-Encoding: gzip</tt> and
     * <tt>Content-Encoding: gzip</tt> HTTP headers) is enabled.
     *
     * @return whether HTTP Gzip compression is enabled
     */
    boolean isGzipCompression();

    /**
     * Sets Gzip compression (e.g. support for <tt>Accept-Encoding: gzip</tt> and
     * <tt>Content-Encoding: gzip</tt> HTTP headers) enabled or disabled.
     *
     * @param value whether to use Gzip compression.
     */
    void setGzipCompression(boolean value);

    /**
     * Checks whether chunk HTTP encoding is being used.
     *
     * @return whether chunk HTTP encoding is being used
     */
    boolean isUseChuncked();

    /**
     * Sets chunk HTTP encoding enabled or disabled.
     *
     * @param value whether to use chunk HTTP encoding.
     */
    void setUseChuncked(boolean value);

    /**
     * Retrieves request executor service.
     *
     * @return request executor service.
     */
    ExecutorService getExecutor();

    /**
     * Sets request executor service.
     *
     * @param executorService new executor services.
     */
    void setExecutor(ExecutorService executorService);

}
