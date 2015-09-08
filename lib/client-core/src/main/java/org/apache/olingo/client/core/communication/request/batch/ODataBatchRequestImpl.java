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

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.communication.response.batch.ODataBatchResponseManager;

public class ODataBatchRequestImpl
        extends AbstractODataBatchRequest<ODataBatchResponse, BatchManager>
        implements ODataBatchRequest {

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

  @Override
  public ODataBatchRequest rawAppend(final byte[] toBeStreamed) throws IOException {
    getPayloadManager().getBodyStreamWriter().write(toBeStreamed);
    return this;
  }

  @Override
  public ODataBatchRequest rawAppend(final byte[] toBeStreamed, int off, int len) throws IOException {
    getPayloadManager().getBodyStreamWriter().write(toBeStreamed, off, len);
    return this;
  }

  @Override
  protected HttpResponse doExecute() {
    if (odataClient.getConfiguration().isContinueOnError()) {
      setPrefer(new ODataPreferences().continueOnError());
    }

    return super.doExecute();
  }

  /**
   * Batch request payload management.
   */
  public class BatchManagerImpl extends AbstractBatchManager implements BatchManager {

    public BatchManagerImpl(final ODataBatchRequest req) {
      super(req, ODataBatchRequestImpl.this.futureWrapper,
              ODataBatchRequestImpl.this.odataClient.getConfiguration().isContinueOnError());
    }

    @Override
    protected ODataBatchResponse getResponseInstance(final long timeout, final TimeUnit unit) {
      return new ODataBatchResponseImpl(odataClient, httpClient, getHttpResponse(timeout, unit));
    }

    @Override
    protected void validateSingleRequest(final ODataBatchableRequest request) {
      //TODO: Validate single batch request
    }
  }

  protected class ODataBatchResponseImpl extends AbstractODataResponse implements ODataBatchResponse {

    protected ODataBatchResponseImpl(
            final ODataClient odataClient, final HttpClient httpClient, final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public Iterator<ODataBatchResponseItem> getBody() {
      return new ODataBatchResponseManager(this, expectedResItems, odataClient.getConfiguration().isContinueOnError());
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
