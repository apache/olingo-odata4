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

import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;

/**
 * Object request that can be sent embedded into a batch request.
 */
public interface ODataBatchableRequest extends ODataRequest {

  /**
   * Writes (and consume) the request onto the given batch stream.
   * <p>
   * Please note that this method will consume the request (execution won't be possible anymore).
   *
   * @param req destination batch request.
   */
  void batch(final ODataBatchRequest req);

  /**
   * Writes (and consume) the request onto the given batch stream.
   * <p>
   * Please note that this method will consume the request (execution won't be possible anymore).
   *
   * @param req destination batch request.
   * @param contentId ContentId header value to be added to the serialization. Use this in case of changeset items.
   */
  void batch(final ODataBatchRequest req, final String contentId);
}
