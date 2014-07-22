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

/**
 * Response object to carry OData relevant http information (statusCode, content & response headers)
 */
public class ODataResponse {

  private int statusCode = 0;
  private Map<String, String> headers = new HashMap<String, String>();
  private InputStream content;

  public void setStatusCode(final int statusCode) {
    this.statusCode = statusCode;
  }

  public void setHeader(final String name, final String value) {
    headers.put(name, value);
  }

  public void setContent(final InputStream content) {
    this.content = content;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Map<String, String> getHeaders() {
    return Collections.unmodifiableMap(headers);
  }

  public InputStream getContent() {
    return content;
  }

}
