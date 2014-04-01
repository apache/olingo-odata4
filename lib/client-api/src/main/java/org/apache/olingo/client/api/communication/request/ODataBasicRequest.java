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

import java.util.concurrent.Future;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.commons.api.format.Format;

/**
 * Basic OData request.
 *
 * @param <V> OData response type corresponding to the request implementation.
 * @param <T> Accepted content-type formats by the request in object.
 */
public interface ODataBasicRequest<V extends ODataResponse, T extends Format> extends ODataRequest {

  /**
   * Request execute.
   *
   * @return return an OData response.
   */
  V execute();

  /**
   * Async request execute.
   *
   * @return <code>Future&lt;ODataResponse&gt;</code> about the executed request.
   */
  Future<V> asyncExecute();

  /**
   * Override configured request format.
   *
   * @param format request format.
   * @see com.msopentech.odatajclient.engine.format.ODataFormat
   * @see com.msopentech.odatajclient.engine.format.ODataPubFormat
   */
  void setFormat(T format);
}
