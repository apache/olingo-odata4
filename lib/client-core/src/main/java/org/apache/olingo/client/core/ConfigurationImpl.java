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
package org.apache.olingo.client.core;

import org.apache.olingo.client.api.Configuration;
import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.HttpUriRequestFactory;
import org.apache.olingo.client.core.http.DefaultHttpClientFactory;
import org.apache.olingo.client.core.http.DefaultHttpUriRequestFactory;
import org.apache.olingo.commons.api.format.ContentType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConfigurationImpl implements Configuration {

  private static final String DEFAULT_PUB_FORMAT = "pubFormat";

  private static final String DEFAULT_VALUE_FORMAT = "valueFormat";

  private static final String DEFAULT_BATCH_ACCEPT_FORMAT = "batchAcceptFormat";

  private static final String DEFAULT_MEDIA_FORMAT = "valueFormat";

  private static final String HTTP_CLIENT_FACTORY = "httpClientFactory";

  private static final String HTTP_URI_REQUEST_FACTORY = "httpUriRequestFactory";

  private static final String USE_XHTTP_METHOD = "useHTTPMethod";

  private static final String KEY_AS_SEGMENT = "keyAsSegment";

  private static final String ADDRESS_DERIVED_TYPE = "addressDerivedType";

  private static final String USE_OPERATION_FQN_IN_URL = "useOperationFqnInUrl";

  private static final String GZIP_COMPRESSION = "gzipCompression";

  private static final String CHUNKING = "chunking";

  private static final String CONTINUE_ON_ERROR = "continueOnError";

  public static final int DEFAULT_BUFFER_SIZE = 4 * 1024 * 1024;  // 4MB

  private final Map<String, Object> CONF = new HashMap<String, Object>();

  private transient ExecutorService executor = createExecutor(10);

  private ExecutorService createExecutor(final int threads) {
    final ThreadPoolExecutor tp = new ThreadPoolExecutor(
        threads,
        threads,
        5L,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>());
    tp.allowCoreThreadTimeOut(true);
    return tp;
  }

  /**
   * Gets given configuration property.
   *
   * @param key key value of the property to be retrieved.
   * @param defaultValue default value to be used in case of the given key doesn't exist.
   * @return property value if exists; default value if does not exist.
   */
  protected Object getProperty(final String key, final Object defaultValue) {
    return CONF.containsKey(key) ? CONF.get(key) : defaultValue;
  }

  /**
   * Sets new configuration property.
   *
   * @param key configuration property key.
   * @param value configuration property value.
   * @return given value.
   */
  protected Object setProperty(final String key, final Object value) {
    return CONF.put(key, value);
  }

  @Override
  public ContentType getDefaultBatchAcceptFormat() {
    return (ContentType) getProperty(DEFAULT_BATCH_ACCEPT_FORMAT, ContentType.MULTIPART_MIXED);
  }

  @Override
  public void setDefaultBatchAcceptFormat(final ContentType contentType) {
    setProperty(DEFAULT_BATCH_ACCEPT_FORMAT, contentType);
  }

  @Override
  public ContentType getDefaultPubFormat() {
    return (ContentType) getProperty(DEFAULT_PUB_FORMAT, ContentType.JSON_FULL_METADATA);
  }

  @Override
  public void setDefaultPubFormat(final ContentType contentType) {
    setProperty(DEFAULT_PUB_FORMAT, contentType);
  }

  @Override
  public ContentType getDefaultFormat() {
    final ContentType contenType = getDefaultPubFormat();
    return contenType == ContentType.APPLICATION_ATOM_XML ? ContentType.APPLICATION_XML : contenType;
  }

  @Override
  public ContentType getDefaultValueFormat() {
    return (ContentType) getProperty(DEFAULT_VALUE_FORMAT, ContentType.TEXT_PLAIN);
  }

  @Override
  public void setDefaultValueFormat(final ContentType contentType) {
    setProperty(DEFAULT_VALUE_FORMAT, contentType);
  }

  @Override
  public ContentType getDefaultMediaFormat() {
    return (ContentType) getProperty(DEFAULT_VALUE_FORMAT, ContentType.APPLICATION_OCTET_STREAM);
  }

  @Override
  public void setDefaultMediaFormat(final ContentType contentType) {
    setProperty(DEFAULT_MEDIA_FORMAT, contentType);
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
  public boolean isKeyAsSegment() {
    return (Boolean) getProperty(KEY_AS_SEGMENT, false);
  }

  @Override
  public void setKeyAsSegment(final boolean value) {
    setProperty(KEY_AS_SEGMENT, value);
  }

  @Override
  public boolean isAddressingDerivedTypes() {
    return (Boolean) getProperty(ADDRESS_DERIVED_TYPE, true);
  }

  @Override
  public void setAddressingDerivedTypes(final boolean value) {
    setProperty(ADDRESS_DERIVED_TYPE, value);
  }

  @Override
  public boolean isUseUrlOperationFQN() {
    return (Boolean) getProperty(USE_OPERATION_FQN_IN_URL, true);
  }

  @Override
  public void setUseUrlOperationFQN(final boolean value) {
    setProperty(USE_OPERATION_FQN_IN_URL, value);
  }

  @Override
  public boolean isContinueOnError() {
    return (Boolean) getProperty(CONTINUE_ON_ERROR, false);
  }

  @Override
  public void setContinueOnError(final boolean value) {
    setProperty(CONTINUE_ON_ERROR, value);
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
