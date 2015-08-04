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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.olingo.commons.api.http.HttpStatusCode;

/**
 * Response object to carry OData-relevant HTTP information (status code, response headers, and content).
 */
public class ODataResponse {

  private int statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode();
  private final Map<String, String> headers = new HashMap<String, String>();
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
   * Sets a header.
   * @param name the name
   * @param value the value
   */
  public void setHeader(final String name, final String value) {
    headers.put(name, value);
  }

  /**
   * Gets all headers.
   * @return an unmodifiable Map of header names/values
   */
  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
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

}
