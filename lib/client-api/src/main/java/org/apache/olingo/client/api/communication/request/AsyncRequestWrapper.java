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

import java.net.URI;

import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataResponse;

public interface AsyncRequestWrapper<R extends ODataResponse> {

  /**
   * Add wait http header.
   *
   * @param waitInSeconds wait time in seconds.
   * @return the current AsyncRequestWrapper instance.
   */
  AsyncRequestWrapper<R> wait(int waitInSeconds);

  /**
   * The odata.callback preference MUST include the parameter url whose value is the URL of a callback endpoint to be
   * invoked by the OData service when data is available. The syntax of the odata.callback preference is specified in
   * [OData-ABNF].
   * <br />
   * For HTTP based callbacks, the OData service executes an HTTP GET request against the specified URL.
   * <br/>
   * Services that support odata.callback SHOULD support notifying the client through HTTP.
   *
   * @param url callback URL
   * @return the current AsyncRequestWrapper instance.
   */
  AsyncRequestWrapper<R> callback(final URI url);

  /**
   * execute the request for the first time.
   *
   * @return the current AsyncRequestWrapper instance.
   */
  AsyncResponseWrapper<R> execute();
}
