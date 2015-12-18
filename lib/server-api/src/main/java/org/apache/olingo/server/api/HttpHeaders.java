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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * HttpHeader container for internal use in this package.
 * @see ODataRequest
 * @see ODataResponse
 */
final class HttpHeaders {
  private final Map<String, List<String>> headers = new LinkedHashMap<String, List<String>>();

  /**
   * Adds a header with given name and value.
   * If a header with that name already exists the value is added to this header.
   * @param name name of header
   * @param value value for header
   * @return this container (fluent interface)
   */
  public HttpHeaders addHeader(final String name, final String value) {
    final String canonicalName = getCanonicalName(name);
    List<String> header = headers.get(canonicalName);
    if (header == null) {
      header = new ArrayList<String>();
    }
    header.add(value);
    headers.put(canonicalName, header);
    return this;
  }

  /**
   * Adds a header with the given name and values.
   * If a header with that name already exists the values are added to this header.
   * @param name name of header
   * @param values values for header
   * @return this container (fluent interface)
   */
  public HttpHeaders addHeader(final String name, final List<String> values) {
    final String canonicalName = getCanonicalName(name);
    List<String> header = headers.get(canonicalName);
    if (header == null) {
      header = new ArrayList<String>();
    }
    header.addAll(values);
    headers.put(canonicalName, header);
    return this;
  }

  /**
   * Set a header with given name and value.
   * If a header with that name already exists the old header is replaced with the new one.
   * @param name name of header
   * @param value value for header
   * @return this container (fluent interface)
   */
  public HttpHeaders setHeader(final String name, final String value) {
    removeHeader(name);
    addHeader(name, value);
    return this;
  }

  /**
   * Gets header values for the given name.
   * @param name name of header requested
   * @return corresponding header values or null if no values have been found
   */
  public List<String> getHeader(final String name) {
    final List<String> values = headers.get(getCanonicalName(name));
    return values == null || values.isEmpty() ? null : Collections.unmodifiableList(values);
  }

  /**
   * Removes header of the given name.
   * @param name name of header to be removed
   * @return removed header values or null if no header was known for this name
   */
  public List<String> removeHeader(final String name) {
    return headers.remove(getCanonicalName(name));
  }

  /**
   * Gets all headers with the according values.
   * @return an unmodifiable Map of header names/values or an empty collection if no headers have been set
   */
  public Map<String, List<String>> getHeaderToValues() {
    return headers.isEmpty() ? Collections.<String, List<String>> emptyMap() : Collections.unmodifiableMap(headers);
  }

  /**
   * Gets all header names.
   * @return all header names or an empty collection if no headers have been set
   */
  public Collection<String> getHeaderNames() {
    return headers.isEmpty() ? Collections.<String> emptySet() : Collections.unmodifiableSet(headers.keySet());
  }

  /**
   * The canonical form of a header name is the already-used form regarding case,
   * enabling applications to have pretty-looking headers instead of getting them
   * converted to all lowercase.
   * @param name HTTP header name
   */
  private String getCanonicalName(final String name) {
    for (final String headerName : headers.keySet()) {
      if (headerName.equalsIgnoreCase(name)) {
        return headerName;
      }
    }
    return name;
  }
}
