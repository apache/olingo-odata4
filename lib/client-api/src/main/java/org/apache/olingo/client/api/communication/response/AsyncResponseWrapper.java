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
package org.apache.olingo.client.api.communication.response;

import java.net.URI;

public interface AsyncResponseWrapper<R extends ODataResponse> {

  /**
   * Checks for preference applied.
   *
   * @return 'TRUE' if respond-async preference has been applied; 'FALSE' otherwise.
   */
  boolean isPreferenceApplied();

  /**
   * Checks if asynchronous processing has been terminated.
   *
   * @return 'TRUE' the process has been terminated; 'FALSE' otherwise.
   */
  boolean isDone();

  /**
   * Gets the real response.
   * <br />
   * If asynchronous processing has been terminated then the response will be returned immediately. This method retries
   * after a delay, specified by the 'Retry-After' header indicating the time, in seconds, the client should wait before
   * retry. If there isn't any 'Retry-After' response header available, a default of 5 seconds will be chosen. The query
   * will be retried for a maximum of five times.
   *
   * @return real OData response.
   */
  R getODataResponse();

  /**
   * Specifies the location for the next monitor check.
   * <br />
   * Overrides the location value retrieved among headers and nullifies the previous valid response (if exists).
   *
   * @param uri monitor location.
   * @return the current async response wrapper.
   */
  AsyncResponseWrapper<R> forceNextMonitorCheck(URI uri);

  /**
   * DeleteA DELETE request sent to the status monitor resource requests that the asynchronous processing be canceled. A
   * 200 OK or to a 204 No Content response indicates that the asynchronous processing has been successfully canceled.
   *
   * @return OData delete response.
   */
  ODataDeleteResponse delete();

  /**
   * A client can request that the DELETE should be executed asynchronously. A 202 Accepted response indicates that the
   * cancellation is being processed asynchronously; the client can use the returned Location header (which MUST be
   * different from the status monitor resource of the initial request) to query for the status of the cancellation. If
   * a delete request is not supported by the service, the service returns 405 Method Not Allowed.
   *
   * @return OData delete response.
   */
  AsyncResponseWrapper<ODataDeleteResponse> asyncDelete();
}
