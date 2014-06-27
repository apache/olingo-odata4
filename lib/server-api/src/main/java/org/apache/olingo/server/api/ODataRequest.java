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

public class ODataRequest {
  private HttpMethod method;
  private Map<String, List<String>> headers = new HashMap<String, List<String>>();
  private InputStream body;
  private String rawQueryPath;
  private String rawRequestUri;
  private String rawODataPath;
  private String rawBaseUri;
  private String rawServiceResolutionUri;

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
  public void addHeader(String name, List<String> values) {
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
  public List<String> getHeader(String name) {
    return headers.get(name.toUpperCase());
  }

  public InputStream getBody() {
    return body;
  }

  public void setBody(final InputStream body) {
    this.body = body;
  }

  public String getRawQueryPath() {
    return rawQueryPath;
  }

  public void setRawQueryPath(String rawQueryPath) {
    this.rawQueryPath = rawQueryPath;
  }

  public String getRawBaseUri() {
    return rawBaseUri;
  }

  public String getRawRequestUri() {
    return rawRequestUri;
  }

  public String getRawODataPath() {
    return rawODataPath;
  }

  public void setRawRequestUri(String rawRequestUri) {
    this.rawRequestUri = rawRequestUri;
  }

  public void setRawODataPath(String rawODataPath) {
    this.rawODataPath = rawODataPath;

  }

  public void setRawBaseUri(String rawBaseUri) {
    this.rawBaseUri = rawBaseUri;
  }

  public String getRawServiceResolutionUri() {
    return rawServiceResolutionUri;
  }

  public void setRawServiceResolutionUri(String rawServiceResolutionUri) {
    this.rawServiceResolutionUri = rawServiceResolutionUri;
  }
}
