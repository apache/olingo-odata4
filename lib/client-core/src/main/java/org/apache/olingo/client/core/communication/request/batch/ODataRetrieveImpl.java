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
import org.apache.olingo.client.api.communication.request.batch.CommonODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataRetrieve;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataRequest;

/**
 * Retrieve request wrapper for the corresponding batch item.
 */
public class ODataRetrieveImpl extends AbstractODataBatchRequestItem
        implements ODataRetrieve {

  private final ODataRetrieveResponseItem expectedResItem;

  /**
   * Constructor.
   *
   * @param req batch request.
   * @param expectedResItem expected batch response item.
   */
  ODataRetrieveImpl(final CommonODataBatchRequest req, final ODataRetrieveResponseItem expectedResItem) {
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
  public ODataRetrieve setRequest(final ODataBatchableRequest request) {
    if (!isOpen()) {
      throw new IllegalStateException("Current batch item is closed");
    }

    if (((AbstractODataRequest) request).getMethod() != HttpMethod.GET) {
      throw new IllegalArgumentException("Invalid request. Only GET method is allowed");
    }

    hasStreamedSomething = true;

    // stream the request
    streamRequestHeader(request);

    // close before in order to avoid any further setRequest calls.
    close();

    // add request to the list
    expectedResItem.addResponse(
            ODataRetrieveResponseItem.RETRIEVE_CONTENT_ID, ((AbstractODataRequest) request).getResponseTemplate());

    return this;
  }
}
