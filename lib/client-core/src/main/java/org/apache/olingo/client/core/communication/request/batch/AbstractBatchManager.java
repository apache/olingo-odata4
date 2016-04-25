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
package org.apache.olingo.client.core.communication.request.batch;

import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequestItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.ODataSingleRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.request.Wrapper;

/**
 * Batch request payload management.
 */
public abstract class AbstractBatchManager extends AbstractODataStreamManager<ODataBatchResponse> {

  protected final boolean continueOnError;
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

  /**
   * Batch request current item.
   */
  protected ODataBatchRequestItem currentItem = null;

  /**
   * batch request reference.
   */
  protected final ODataBatchRequest req;

  protected AbstractBatchManager(final ODataBatchRequest req,
      final Wrapper<Future<HttpResponse>> futureWrap, final boolean continueOnError) {

    super(futureWrap);
    this.req = req;
    this.continueOnError = continueOnError;
  }

  /**
   * Gets a changeset batch item instance. A changeset can be submitted embedded into a batch request only.
   *
   * @return ODataChangeset instance.
   */
  public ODataChangeset addChangeset() {
    closeCurrentItem();

    // stream dash boundary
    streamDashBoundary();

    final ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem(continueOnError);
    ((AbstractODataBatchRequest<?, ?>) req).addExpectedResItem(expectedResItem);

    currentItem = new ODataChangesetImpl(req, expectedResItem);

    return (ODataChangeset) currentItem;
  }

  /**
   * Adds a retrieve batch item instance. A retrieve item can be submitted embedded into a batch request only.
   *
   * @param request retrieve request to batch.
   */
  public void addRequest(final ODataBatchableRequest request) {
    validateSingleRequest(request);

    closeCurrentItem();

    // stream dash boundary
    streamDashBoundary();

    final ODataSingleResponseItem expectedResItem = new ODataSingleResponseItem();
    currentItem = new ODataSingleRequestImpl(req, expectedResItem);

    ((AbstractODataBatchRequest<?, ?>) req).addExpectedResItem(expectedResItem);

    ((ODataSingleRequest) currentItem).setRequest(request);
  }

  /**
   * Close the current streamed item.
   */
  protected void closeCurrentItem() {
    if (currentItem != null) {
      currentItem.close();
    }
  }

  @Override
  protected ODataBatchResponse getResponse(final long timeout, final TimeUnit unit) {
    closeCurrentItem();
    streamCloseDelimiter();
    finalizeBody();
    return getResponseInstance(timeout, unit);
  }

  protected abstract ODataBatchResponse getResponseInstance(final long timeout, final TimeUnit unit);

  /**
   * Streams dash boundary.
   */
  protected void streamDashBoundary() {
    // preamble
    newLine();

    // stream batch-boundary
    stream(("--" + ((AbstractODataBatchRequest<?, ?>) req).boundary).getBytes(DEFAULT_CHARSET));
    newLine();
  }

  /**
   * Streams close delimiter.
   */
  protected void streamCloseDelimiter() {
    // stream close-delimiter
    newLine();
    stream(("--" + ((AbstractODataBatchRequest<?, ?>) req).boundary + "--").getBytes(DEFAULT_CHARSET));
  }

  protected abstract void validateSingleRequest(ODataBatchableRequest request);
}
