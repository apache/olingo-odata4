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
package org.apache.olingo.client.api;

import java.util.concurrent.ExecutorService;

import org.apache.olingo.client.api.http.HttpClientFactory;
import org.apache.olingo.client.api.http.HttpUriRequestFactory;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * Configuration wrapper.
 */
public interface Configuration {

  /**
   * Gets the configured default <tt>Accept</tt> header value format for a batch request.
   *
   * @return configured default <tt>Accept</tt> header value for a batch request.
   */
  ContentType getDefaultBatchAcceptFormat();

  /**
   * Set the default <tt>Accept</tt> header value format for a batch request.
   *
   * @param contentType default <tt>Accept</tt> header value.
   */
  void setDefaultBatchAcceptFormat(ContentType contentType);

  /**
   * Gets the configured OData format for AtomPub exchanges. If this configuration parameter doesn't exist the
   * JSON_FULL_METADATA format will be used as default.
   *
   * @return configured OData format for AtomPub if specified; JSON_FULL_METADATA format otherwise.
   */
  ContentType getDefaultPubFormat();

  /**
   * Sets the default OData format for AtomPub exchanges.
   *
   * @param format default format.
   */
  void setDefaultPubFormat(ContentType format);

  /**
   * Gets the configured OData format. This value depends on what is returned from <tt>getDefaultPubFormat()</tt>.
   *
   * @return configured OData format
   * @see #getDefaultPubFormat()
   */
  ContentType getDefaultFormat();

  /**
   * Gets the configured OData value format. If this configuration parameter doesn't exist the TEXT format will be used
   * as default.
   *
   * @return configured OData value format if specified; TEXT_PLAIN format otherwise.
   */
  ContentType getDefaultValueFormat();

  /**
   * Sets the default OData value format.
   *
   * @param format default format.
   */
  void setDefaultValueFormat(ContentType format);

  /**
   * Gets the configured OData media format. If this configuration parameter doesn't exist the APPLICATION_OCTET_STREAM
   * format will be used as default.
   *
   * @return configured OData media format if specified; APPLICATION_OCTET_STREAM format otherwise.
   */
  ContentType getDefaultMediaFormat();

  /**
   * Sets the default OData media format.
   *
   * @param format default format.
   */
  void setDefaultMediaFormat(ContentType format);

  /**
   * Gets the HttpClient factory to be used for executing requests.
   *
   * @return provided implementation (if configured via <tt>setHttpClientFactory</tt> or default.
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
   * Checks whether URIs contain entity key between parentheses (standard) or instead as additional segment
   * (non-standard).
   * <br/>
   * Example: http://services.odata.org/V4/OData/OData.svc/Products(0) or
   * http://services.odata.org/V4/OData/OData.svc/Products/0
   *
   * @return whether URIs shall be built with entity key between parentheses (standard) or instead as additional
   * segment.
   */
  boolean isKeyAsSegment();

  /**
   * Sets whether URIs shall be built with entity key between parentheses (standard) or instead as additional segment
   * (non-standard).
   * <br/>
   * Example: http://services.odata.org/V4/OData/OData.svc/Products(0) or
   * http://services.odata.org/V4/OData/OData.svc/Products/0
   *
   * @param value 'TRUE' to use this feature.
   */
  void setKeyAsSegment(boolean value);

  /**
   * Gets whether query URIs in request should contain fully qualified type name. - OData Intermediate Conformance
   * Level: MUST support casting to a derived type according to [OData-URL] if derived types are present in the model.
   * <br/>
   * Example: http://host/service/Customers/Model.VipCustomer(102) or http://host/service/Customers/Model.VipCustomer
   *
   * @return whether query URIs in request should contain fully qualified type name. segment.
   */
  boolean isAddressingDerivedTypes();

  /**
   * Sets whether query URIs in request should contain fully qualified type name. - OData Intermediate Conformance
   * Level: MUST support casting to a derived type according to [OData-URL] if derived types are present in the model.
   * <br/>
   * Example: http://host/service/Customers/Model.VipCustomer(102) or http://host/service/Customers/Model.VipCustomer
   *
   * @param value 'TRUE' to use this feature.
   */
  void setAddressingDerivedTypes(boolean value);

  /**
   * Checks whether operation name in request URI should be fully qualified name, which is required by OData V4
   * protocol, but some service may still choose to support shorter name.
   * <br/>
   * Example: http://host/service/Customers(2)/NS1.Model.IncreaseSalary VS
   * http://host/service/Customers(2)/IncreaseSalary
   *
   * @return wheter operation name in request URI should be fully qualified name. segment.
   */
  boolean isUseUrlOperationFQN();

  /**
   * Sets whether operation name in request URI should be fully qualified name, which is required by OData V4 protocol,
   * but some service may still choose to support shorter name.
   * <br/>
   * Example: http://host/service/Customers(2)/NS1.Model.IncreaseSalary VS
   * http://host/service/Customers(2)/IncreaseSalary
   *
   * @param value 'TRUE' to use this feature.
   */
  void setUseUrlOperationFQN(boolean value);

  /**
   * When processing a set of requests (in batch requests, for example), checks if the execution will be aborted after
   * first error encountered or not.
   *
   * @return whether execution of a set of requests will be aborted after first error
   */
  boolean isContinueOnError();

  /**
   * When processing a set of requests (in batch requests, for example), sets if the execution will be aborted after
   * first error encountered or not.
   *
   * @param value 'TRUE' to use this feature.
   */
  void setContinueOnError(boolean value);

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
