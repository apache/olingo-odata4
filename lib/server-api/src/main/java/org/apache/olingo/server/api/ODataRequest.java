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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Request object carry http information optimized and required to handle OData requests only. 
 */
public class ODataRequest {
  private HttpMethod method;
  private Map<String, List<String>> headers = new HashMap<String, List<String>>();
  private InputStream body;
  private String rawQueryPath;
  private String rawRequestUri;
  private String rawODataPath;
  private String rawBaseUri;
  private String rawServiceResolutionUri;

  /**
   * @return the http method (GET, PUT, POST ...)
   */
  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(final HttpMethod method) {
    this.method = method;
  }

  /**
   * Add header to request where name handled as case insensitive key. If a header already exists then the list of
   * values will just be extended.
   * @param name case insensitive header name
   * @param values
   */
  public void addHeader(final String name, final List<String> values) {
    String key = name.toUpperCase();
    if (headers.containsKey(key)) {
      List<String> oldValues = headers.get(key);

      List<String> newValues = new ArrayList<String>();
      newValues.addAll(oldValues);
      newValues.addAll(values);

      headers.put(name.toUpperCase(), newValues);
    } else {
      headers.put(name.toUpperCase(), values);
    }
  }

  /**
   * Returns header value for name where name is a case insensitive key.
   * @return the header value or null if not found
   */
  public List<String> getHeaders(final String name) {
    return headers.get(name.toUpperCase());
  }

  /**
   * Returns first header value for name where name is a case insensitive key.
   * @return the header value or null if not found
   */
  public String getHeader(final String name) {
    List<String> values = getHeaders(name);
    return values != null ? values.get(0) : null;
  }

  /**
   * @return the request payload or null
   */
  public InputStream getBody() {
    return body;
  }

  public void setBody(final InputStream body) {
    this.body = body;
  }

  /**
   * @return decoded query options e.g. "$format=json"
   */
  public String getRawQueryPath() {
    return rawQueryPath;
  }

  public void setRawQueryPath(final String rawQueryPath) {
    this.rawQueryPath = rawQueryPath;
  }

  /**
   * @return encoded base uri e.g. "http://localhost/my%20service"
   */
  public String getRawBaseUri() {
    return rawBaseUri;
  }

  /**
   * @return encoded request uri e.g. "http://localhost/my%20service/sys1/Employees?$format=json"
   */
  public String getRawRequestUri() {
    return rawRequestUri;
  }

  /**
   * @return encoded OData path segments e.g. "/Employees"
   */
  public String getRawODataPath() {
    return rawODataPath;
  }

  public void setRawRequestUri(final String rawRequestUri) {
    this.rawRequestUri = rawRequestUri;
  }

  public void setRawODataPath(final String rawODataPath) {
    this.rawODataPath = rawODataPath;

  }

  public void setRawBaseUri(final String rawBaseUri) {
    this.rawBaseUri = rawBaseUri;
  }

  /**
   * @return a decoded path segment that does not belong to the OData url schema or null  e.g. "sys1" 
   */
  public String getRawServiceResolutionUri() {
    return rawServiceResolutionUri;
  }

  public void setRawServiceResolutionUri(final String rawServiceResolutionUri) {
    this.rawServiceResolutionUri = rawServiceResolutionUri;
  }
}
