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
package org.apache.olingo.client.core.communication.request.batch;

import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataSingleRequest;
import org.apache.olingo.client.core.communication.request.AbstractODataRequest;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * Retrieve request wrapper for the corresponding batch item.
 */
public class ODataSingleRequestImpl extends AbstractODataBatchRequestItem implements ODataSingleRequest {

  private final ODataSingleResponseItem expectedResItem;

  /**
   * Constructor.
   *
   * @param req batch request.
   * @param expectedResItem expected batch response item.
   */
  ODataSingleRequestImpl(final ODataBatchRequest req, final ODataSingleResponseItem expectedResItem) {
    super(req);
    this.expectedResItem = expectedResItem;
  }

  /**
   * Close item.
   */
  @Override
  protected void closeItem() {
    // nop
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataSingleRequest setRequest(final ODataBatchableRequest request) {
    if (!isOpen()) {
      throw new IllegalStateException("Current batch item is closed");
    }

    hasStreamedSomething = true;

    // stream the request
    if (request.getMethod() == HttpMethod.GET) {
      streamRequestHeader(request);
    } else {
      streamRequestHeader(ODataSingleResponseItem.SINGLE_CONTENT_ID);
      request.batch(req, ODataSingleResponseItem.SINGLE_CONTENT_ID);
    }

    // close before in order to avoid any further setRequest calls.
    close();

    // add request to the list
    expectedResItem.addResponse(
            ODataSingleResponseItem.SINGLE_CONTENT_ID, ((AbstractODataRequest) request).getResponseTemplate());

    return this;
  }
}
