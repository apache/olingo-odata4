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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractConfiguration implements Configuration {

    private static final String DEFAULT_PUB_FORMAT = "pubFormat";

    private static final String DEFAULT_VALUE_FORMAT = "valueFormat";

    private static final String DEFAULT_MEDIA_FORMAT = "valueFormat";

    private static final String HTTP_CLIENT_FACTORY = "httpClientFactory";

    private static final String HTTP_URI_REQUEST_FACTORY = "httpUriRequestFactory";

    private static final String USE_XHTTP_METHOD = "useHTTPMethod";

    private static final String KEY_AS_SEGMENT = "keyAsSegment";

    private static final String GZIP_COMPRESSION = "gzipCompression";

    private static final String CHUNKING = "chunking";

    private final Map<String, Object> CONF = new HashMap<String, Object>();

    private transient ExecutorService executor = Executors.newFixedThreadPool(10);

    /**
     * Gets given configuration property.
     *
     * @param key key value of the property to be retrieved.
     * @param defaultValue default value to be used in case of the given key doesn't exist.
     * @return property value if exists; default value if does not exist.
     */
    private Object getProperty(final String key, final Object defaultValue) {
        return CONF.containsKey(key) ? CONF.get(key) : defaultValue;
    }

    /**
     * Sets new configuration property.
     *
     * @param key configuration property key.
     * @param value configuration property value.
     * @return given value.
     */
    private Object setProperty(final String key, final Object value) {
        return CONF.put(key, value);
    }

    @Override
    public ODataPubFormat getDefaultPubFormat() {
        return ODataPubFormat.valueOf(
                getProperty(DEFAULT_PUB_FORMAT, ODataPubFormat.JSON_FULL_METADATA.name()).toString());
    }

    @Override
    public void setDefaultPubFormat(final ODataPubFormat format) {
        setProperty(DEFAULT_PUB_FORMAT, format.name());
    }

    @Override
    public ODataFormat getDefaultFormat() {
        ODataFormat format;

        switch (getDefaultPubFormat()) {
            case ATOM:
                format = ODataFormat.XML;
                break;

            case JSON_FULL_METADATA:
                format = ODataFormat.JSON_FULL_METADATA;
                break;

            case JSON_NO_METADATA:
                format = ODataFormat.JSON_NO_METADATA;
                break;

            case JSON:
            default:
                format = ODataFormat.JSON;
        }

        return format;
    }

    @Override
    public ODataValueFormat getDefaultValueFormat() {
        return ODataValueFormat.valueOf(
                getProperty(DEFAULT_VALUE_FORMAT, ODataValueFormat.TEXT.name()).toString());
    }

    @Override
    public void setDefaultValueFormat(final ODataValueFormat format) {
        setProperty(DEFAULT_VALUE_FORMAT, format.name());
    }

    @Override
    public ODataMediaFormat getDefaultMediaFormat() {
        return ODataMediaFormat.valueOf(
                getProperty(DEFAULT_VALUE_FORMAT, ODataMediaFormat.APPLICATION_OCTET_STREAM.name()).toString());
    }

    @Override
    public void setDefaultMediaFormat(final ODataMediaFormat format) {
        setProperty(DEFAULT_MEDIA_FORMAT, format.name());
    }

    @Override
    public HttpClientFactory getHttpClientFactory() {
        return (HttpClientFactory) getProperty(HTTP_CLIENT_FACTORY, new DefaultHttpClientFactory());
    }

    @Override
    public void setHttpClientFactory(final HttpClientFactory factory) {
        setProperty(HTTP_CLIENT_FACTORY, factory);
    }

    @Override
    public HttpUriRequestFactory getHttpUriRequestFactory() {
        return (HttpUriRequestFactory) getProperty(HTTP_URI_REQUEST_FACTORY, new DefaultHttpUriRequestFactory());
    }

    @Override
    public void setHttpUriRequestFactory(final HttpUriRequestFactory factory) {
        setProperty(HTTP_URI_REQUEST_FACTORY, factory);
    }

    @Override
    public boolean isUseXHTTPMethod() {
        return (Boolean) getProperty(USE_XHTTP_METHOD, false);
    }

    @Override
    public void setUseXHTTPMethod(final boolean value) {
        setProperty(USE_XHTTP_METHOD, value);
    }

    @Override
    public boolean isKeyAsSegment() {
        return (Boolean) getProperty(KEY_AS_SEGMENT, false);
    }

    @Override
    public void setKeyAsSegment(final boolean value) {
        setProperty(KEY_AS_SEGMENT, value);
    }

    @Override
    public boolean isGzipCompression() {
        return (Boolean) getProperty(GZIP_COMPRESSION, false);
    }

    @Override
    public void setGzipCompression(final boolean value) {
        setProperty(GZIP_COMPRESSION, value);
    }

    @Override
    public boolean isUseChuncked() {
        return (Boolean) getProperty(CHUNKING, true);
    }

    @Override
    public void setUseChuncked(final boolean value) {
        setProperty(CHUNKING, value);
    }

    @Override
    public ExecutorService getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(final ExecutorService executorService) {
        executor = executorService;
    }

}
