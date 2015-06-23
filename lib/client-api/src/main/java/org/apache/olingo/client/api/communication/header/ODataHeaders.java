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
package org.apache.olingo.client.api.communication.header;

import java.util.Collection;

/**
 * ODataHeaders wraps OData request/response headers.
 *
 * @see org.apache.olingo.client.api.communication.request.ODataRequest
 * @see org.apache.olingo.client.api.communication.response.ODataResponse
 */
public interface ODataHeaders {

  /**
   * Gets the value of the header identified by the given name.
   * <br/>
   * Please note that header name is case-insensitive.
   *
   * @param name name of the header to be retrieved.
   * @return header value.
   */
  String getHeader(final String name);

  /**
   * Gets header names.
   * <br/>
   * Please note that header name is case-insensitive.
   *
   * @return header names.
   */
  Collection<String> getHeaderNames();

  /**
   * Add the specified (custom) header (header name is case-insensitive).
   *
   * @param name header key.
   * @param value header value.
   * @return the current updated header instance.
   */
  ODataHeaders setHeader(String name, String value);

  /**
   * Removes the header identified by the given name.
   * <br/>
   * Please note that header name is case-insensitive.
   *
   * @param name name of the header to be retrieved.
   * @return header name (if found).
   */
  String removeHeader(String name);

}
