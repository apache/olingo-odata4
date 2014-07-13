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
package org.apache.olingo.client.core.communication.request.batch.v4;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.v4.ODataBatchRequest;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.v4.ODataClient;
import org.apache.olingo.client.core.communication.request.batch.AbstractBatchManager;
import org.apache.olingo.client.core.communication.request.batch.AbstractODataBatchRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.communication.response.batch.ODataBatchResponseManager;

/**
 * This class implements a batch request.
 */
public class ODataBatchRequestImpl
        extends AbstractODataBatchRequest<ODataBatchResponse, BatchManager>
        implements ODataBatchRequest {

  private boolean continueOnError = false;

  public ODataBatchRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, uri);
    setAccept(odataClient.getConfiguration().getDefaultBatchAcceptFormat().toContentTypeString());
  }

  @Override
  protected BatchManager getPayloadManager() {
    if (payloadManager == null) {
      payloadManager = new BatchManagerImpl(this);
    }
    return (BatchManager) payloadManager;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataBatchRequest rawAppend(final byte[] toBeStreamed) throws IOException {
    getPayloadManager().getBodyStreamWriter().write(toBeStreamed);
    return this;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataBatchRequest rawAppend(final byte[] toBeStreamed, int off, int len) throws IOException {
    getPayloadManager().getBodyStreamWriter().write(toBeStreamed, off, len);
    return this;
  }

  @Override
  public ODataBatchRequest continueOnError() {
    addCustomHeader(HeaderName.prefer, new ODataPreferences(odataClient.getServiceVersion()).continueOnError());
    continueOnError = true;
    return this;
  }

  /**
   * Batch request payload management.
   */
  public class BatchManagerImpl extends AbstractBatchManager implements BatchManager {

    public BatchManagerImpl(final ODataBatchRequest req) {
      super(req, ODataBatchRequestImpl.this.futureWrapper);
    }

    @Override
    protected ODataBatchResponse getResponseInstance(final long timeout, final TimeUnit unit) {
      return new ODataBatchResponseImpl(odataClient, httpClient, getHttpResponse(timeout, unit));
    }

    @Override
    protected void validateSingleRequest(final ODataBatchableRequest request) {
    }
  }

  /**
   * This class implements a response to a batch request.
   *
   * @see org.apache.olingo.client.core.communication.request.ODataBatchRequest
   */
  protected class ODataBatchResponseImpl extends AbstractODataResponse implements ODataBatchResponse {

    protected ODataBatchResponseImpl(
            final CommonODataClient<?> odataClient, final HttpClient httpClient, final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<ODataBatchResponseItem> getBody() {
      return new ODataBatchResponseManager(this, expectedResItems, continueOnError);
    }

    @Override
    public void close() {
      for (ODataBatchResponseItem resItem : expectedResItems) {
        resItem.close();
      }
      super.close();
    }

  }
}
