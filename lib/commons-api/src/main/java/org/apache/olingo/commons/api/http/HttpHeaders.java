/*******************************************************************************
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
 ******************************************************************************/
package org.apache.olingo.commons.api.http;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * HttpHeader container
 */
public class HttpHeaders {
  private final Map<String, HttpHeader> headers = new TreeMap<String, HttpHeader>();

  /**
   * Add a header with given name and value.
   * If header with name already exists the value is added to this header.
   *
   * @param name name of header
   * @param value value for header
   * @return this container (fluent interface)
   */
  public HttpHeaders addHeader(String name, String value) {
    HttpHeader eh = grantHeader(name);
    eh.addValue(value);
    return this;
  }

  /**
   * Add a header with given name and values.
   * If header with name already exists the values are added to this header.
   *
   * @param name name of header
   * @param values values for header
   * @return this container (fluent interface)
   */
  public HttpHeaders addHeader(String name, Collection<String> values) {
    HttpHeader eh = grantHeader(name);
    eh.addValues(values);
    return this;
  }

  /**
   * Set a header with given name and value.
   * If header with name already exists the old header is replaced with the new one.
   *
   * @param name name of header
   * @param value value for header
   * @return this container (fluent interface)
   */
  public HttpHeaders setHeader(String name, String value) {
    removeHeader(name);

    HttpHeader eh = grantHeader(name);
    eh.addValue(value);
    return this;
  }

  /**
   * Get header for given name.
   *
   * @param name name of header requested
   * @return corresponding header
   */
  public HttpHeader getHeader(String name) {
    return headers.get(HttpHeader.createCanonicalName(name));
  }

  /**
   * Remove header for given name.
   *
   * @param name name of header to be removed
   * @return header which was removed or null if no header was known for this name
   */
  public HttpHeader removeHeader(String name) {
    return headers.remove(HttpHeader.createCanonicalName(name));
  }


  /**
   * Get all headers.
   *
   * @return all headers
   */
  public Collection<HttpHeader> getHeaders() {
    return Collections.unmodifiableCollection(headers.values());
  }

  /**
   * Get all header names.
   *
   * @return all header names
   */
  public Collection<String> getHeaderNames() {
    Collection<String> headerNames = new ArrayList<String>();
    for (HttpHeader header : headers.values()) {
      headerNames.add(header.getName());
    }
    return headerNames;
  }

  /**
   * Get or create a header for given name.
   *
   * @return new or known header
   */
  private HttpHeader grantHeader(String name) {
    String key = HttpHeader.createCanonicalName(name);
    HttpHeader eh = headers.get(key);
    if(eh == null) {
      eh = new HttpHeader(name);
      headers.put(key, eh);
    }
    return eh;
  }
}
