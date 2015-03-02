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
package org.apache.olingo.client.api.communication.request.batch;

import java.io.IOException;
import java.io.PipedOutputStream;

import org.apache.olingo.client.api.communication.request.ODataStreamedRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;

public interface ODataBatchRequest extends ODataStreamedRequest<ODataBatchResponse, BatchManager> {

  /**
   * Gets piped stream to be used to stream batch items.
   *
   * @return piped stream for the payload.
   */
  PipedOutputStream getOutputStream();

  /**
   * Appends the given byte array to the payload.
   *
   * @param toBeStreamed byte array to be appended.
   * @return the current batch request.
   * @throws IOException in case of write errors.
   */
  ODataBatchRequest rawAppend(final byte[] toBeStreamed) throws IOException;

  /**
   * Appends the given byte array to the payload.
   *
   * @param toBeStreamed byte array to be appended.
   * @param off byte array offset.
   * @param len number of byte to be streamed.
   * @return the current batch request.
   * @throws IOException in case of write errors.
   */
  ODataBatchRequest rawAppend(final byte[] toBeStreamed, int off, int len) throws IOException;

}
