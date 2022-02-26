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
  private final ODataBatchRequestContext batchRequestContext;

  /**
   * Constructor.
   *
   * @param req batch request.
   * @param expectedResItem expected OData response items.
   * @param batchRequestContext batch request context
   */
  ODataSingleRequestImpl(ODataBatchRequest req, ODataSingleResponseItem expectedResItem,
		ODataBatchRequestContext batchRequestContext) {
    super(req);
    this.expectedResItem = expectedResItem;
//	  this(req,expectedResItem);
	  this.batchRequestContext = batchRequestContext;
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

    int contentId = batchRequestContext.getAndIncrementContentId();
    // stream the request
    if (request.getMethod() == HttpMethod.GET) {
      streamRequestHeader(request);
    } else {
      streamRequestHeader(String.valueOf(contentId));
      request.batch(req, String.valueOf(contentId));
    }

    // close before in order to avoid any further setRequest calls.
    close();

    // add request to the list
    expectedResItem.addResponse(
    		String.valueOf(contentId), ((AbstractODataRequest) request).getResponseTemplate());

    return this;
  }
}
