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

import org.apache.olingo.commons.api.http.HttpStatusCode;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Response object to carry OData-relevant HTTP information (status code, response headers, and content).
 */
public class ODataResponse {

  private int statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode();
  private final HttpHeaders headers = new HttpHeaders();
  private InputStream content;

  /**
   * Sets the status code.
   * @see HttpStatusCode
   */
  public void setStatusCode(final int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Gets the status code.
   * @see HttpStatusCode
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * <p>Set a header to the response.</p>
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
   * <p>Adds a header to the response.</p>
   * <p>The header name will be handled as case-insensitive key.</p>
   * <p>If a header already exists then the list of values will just be extended.</p>
   * @param name case-insensitive header name
   * @param value value for the given header name
   * @see <a href="http://ietf.org/rfc/rfc7230.txt">RFC 7230, section 3.2.2</a>
   */
  public void addHeader(final String name, final String value) {
    headers.setHeader(name, value);
  }

  /**
   * <p>Adds a header to the response.</p>
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
   * Get all headers with the according values.
   *
   * @return an unmodifiable Map of header names/values
   */
  public Map<String, List<String>> getAllHeaders() {
    return headers.getHeaderToValues();
  }

  /**
   * Gets header value for a given name.
   * @param name the header name as a case-insensitive key
   * @return the header value(s) or null if not found
   */
  public List<String> getHeaders(final String name) {
    return headers.getHeader(name);
  }

  /**
   * Gets first header value for a given name.
   * If header name is not known <code>null</code> is returned.
   *
   * @param name the header name as a case-insensitive key
   * @return the first header value or null if not found
   */
  public String getHeader(final String name) {
    final List<String> values = getHeaders(name);
    return values == null || values.isEmpty() ? null : values.get(0);
  }

  /**
   * Sets the content (body).
   * @param content the content as {@link InputStream}
   */
  public void setContent(final InputStream content) {
    this.content = content;
  }

  /**
   * Gets the content (body).
   * @return the content as {@link InputStream}
   */
  public InputStream getContent() {
    return content;
  }

  private ODataContent odataContent;

  public void setODataContent(ODataContent result) {
    odataContent = result;
  }

  public ODataContent getODataContent() {
    return odataContent;
  }
}
