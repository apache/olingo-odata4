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
package org.apache.olingo.server.api;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Request object to carry HTTP information optimized for and required to handle OData requests only.
 */
public class ODataRequest {
  private HttpMethod method;
  private HttpHeaders headers = new HttpHeaders();
  private InputStream body;
  private String rawQueryPath;
  private String rawRequestUri;
  private String rawODataPath;
  private String rawBaseUri;
  private String rawServiceResolutionUri;
  private String protocol;

  /**
   * Gets the HTTP method.
   * @return the HTTP method (GET, PUT, POST ...)
   */
  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Sets the HTTP method.
   * @param method the HTTP method (GET, PUT, POST ...)
   */
  public void setMethod(final HttpMethod method) {
    this.method = method;
  }

  /**
   * <p>Sets a header in the request.</p>
   * <p>The header name will be handled as case-insensitive key.</p>
   * <p>If a header already exists then the header will be replaced by this new value.</p>
   * @param name case-insensitive header name
   * @param value value for the given header name
   * @see <a href="http://ietf.org/rfc/rfc7230.txt">RFC 7230, section 3.2.2</a>
   */
  public void setHeader(final String name, final String value) {
    headers.setHeader(name, value);
  }

  /**
   * <p>Adds a header to the request.</p>
   * <p>The header name will be handled as case-insensitive key.</p>
   * <p>If a header already exists then the list of values will just be extended.</p>
   * @param name case-insensitive header name
   * @param value value for the given header name
   * @see <a href="http://ietf.org/rfc/rfc7230.txt">RFC 7230, section 3.2.2</a>
   */
  public void addHeader(final String name, final String value) {
    headers.addHeader(name, value);
  }

  /**
   * <p>Adds a header to the request.</p>
   * <p>The header name will be handled as case-insensitive key.</p>
   * <p>If a header already exists then the list of values will just be extended.</p>
   * @param name case-insensitive header name
   * @param values list of values for the given header name
   * @see <a href="http://ietf.org/rfc/rfc7230.txt">RFC 7230, section 3.2.2</a>
   */
  public void addHeader(final String name, final List<String> values) {
    headers.addHeader(name, values);
  }

  /**
   * Gets header values for a given name.
   * @param name the header name as a case-insensitive key
   * @return the header value(s) or null if not found
   */
  public List<String> getHeaders(final String name) {
    return headers.getHeader(name);
  }

  /**
   * Gets first header value for a given name.
   * @param name the header name as a case-insensitive key
   * @return the first header value or null if not found
   */
  public String getHeader(final String name) {
    final List<String> values = getHeaders(name);
    return values == null || values.isEmpty() ? null : values.get(0);
  }

  /**
   * Gets all headers.
   * @return an unmodifiable Map of header names/values
   */
  public Map<String, List<String>> getAllHeaders() {
    return headers.getHeaderToValues();
  }

  /**
   * Gets the body of the request.
   * @return the request payload as {@link InputStream} or null
   */
  public InputStream getBody() {
    return body;
  }

  /**
   * Sets the body of the request.
   * @param body the request payload as {@link InputStream}
   */
  public void setBody(final InputStream body) {
    this.body = body;
  }

  /**
   * Gets the query part of the request URI.
   * @return the undecoded query options, e.g., "<code>$format=json,$top=10</code>"
   * @see <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986, section 3.4</a>
   */
  public String getRawQueryPath() {
    return rawQueryPath;
  }

  /**
   * Sets the query part of the request URI.
   * @see #getRawQueryPath()
   */
  public void setRawQueryPath(final String rawQueryPath) {
    this.rawQueryPath = rawQueryPath;
  }

  /**
   * Gets the base URI.
   * @return undecoded base URI, e.g., "<code>http://localhost/my%20service</code>"
   */
  public String getRawBaseUri() {
    return rawBaseUri;
  }

  /**
   * Sets the base URI.
   * @see #getRawBaseUri()
   */
  public void setRawBaseUri(final String rawBaseUri) {
    this.rawBaseUri = rawBaseUri;
  }

  /**
   * Gets the total request URI.
   * @return undecoded request URI, e.g., "<code>http://localhost/my%20service/sys1/Employees?$format=json</code>"
   */
  public String getRawRequestUri() {
    return rawRequestUri;
  }

  /**
   * Sets the total request URI.
   * @see #getRawRequestUri()
   */
  public void setRawRequestUri(final String rawRequestUri) {
    this.rawRequestUri = rawRequestUri;
  }

  /**
   * Gets the path segments of the request URI that belong to OData.
   * @return undecoded OData path segments, e.g., "/Employees"
   */
  public String getRawODataPath() {
    return rawODataPath;
  }

  /**
   * Sets the path segments of the request URI that belong to OData.
   * @see #getRawODataPath()
   */
  public void setRawODataPath(final String rawODataPath) {
    this.rawODataPath = rawODataPath;
  }

  /**
   * Gets the URI part responsible for service resolution.
   * @return undecoded path segments that do not belong to the OData URL schema or null, e.g., "<code>sys1</code>"
   */
  public String getRawServiceResolutionUri() {
    return rawServiceResolutionUri;
  }

  /**
   * Sets the URI part responsible for service resolution.
   * @see #getRawServiceResolutionUri()
   */
  public void setRawServiceResolutionUri(final String rawServiceResolutionUri) {
    this.rawServiceResolutionUri = rawServiceResolutionUri;
  }

  /**
   * @return the protocol version used e.g. HTTP/1.1
   */
  public String getProtocol() {
    return protocol;
  }

  /**
   * Sets the HTTP protocol used
   * @param protocol
   * @see #getProtocol()
   */
  public void setProtocol(final String protocol) {
    this.protocol = protocol;
  }

}
