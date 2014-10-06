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
package org.apache.olingo.client.api.communication.request;

import java.io.InputStream;
import java.util.concurrent.Future;

import org.apache.olingo.client.api.communication.response.ODataResponse;

/**
 * OData request payload management abstract class.
 *
 * @param <T> OData response type corresponding to the request implementation.
 */
public interface ODataPayloadManager<T extends ODataResponse> extends ODataStreamer {

  /**
   * Gets payload stream.
   *
   * @return payload stream.
   */
  InputStream getBody();

  /**
   * Closes piped output stream.
   */
  void finalizeBody();

  /**
   * Closes the payload input stream and gets the OData response back.
   *
   * @return OData response.
   */
  T getResponse();

  /**
   * Closes the payload input stream and ask for an asynchronous response.
   *
   * @return <code>Future&lt;ODataResponse&gt;</code> about the executed request.
   */
  Future<T> getAsyncResponse();
}
