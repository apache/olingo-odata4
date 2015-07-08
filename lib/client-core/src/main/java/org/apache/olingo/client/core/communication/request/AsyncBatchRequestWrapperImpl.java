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
package org.apache.olingo.client.core.communication.request;

import java.net.URI;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.header.ODataPreferences;
import org.apache.olingo.client.api.communication.request.AsyncBatchRequestWrapper;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.BatchManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.response.AsyncResponseWrapper;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.commons.api.http.HttpHeader;

public class AsyncBatchRequestWrapperImpl extends AsyncRequestWrapperImpl<ODataBatchResponse>
        implements AsyncBatchRequestWrapper {

  private BatchManager batchManager;

  protected AsyncBatchRequestWrapperImpl(final ODataClient odataClient, final ODataBatchRequest odataRequest) {
    super(odataClient, odataRequest);
    batchManager = odataRequest.payloadManager();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ODataChangeset addChangeset() {
    return batchManager.addChangeset();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRetrieve(final ODataBatchableRequest request) {
    batchManager.addRequest(request);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addOutsideUpdate(final ODataBatchableRequest request) {
    batchManager.addRequest(request);
  }

  @Override
  public AsyncResponseWrapper<ODataBatchResponse> execute() {
    return new AsyncResponseWrapperImpl(batchManager.getResponse());
  }

  public class AsyncResponseWrapperImpl
          extends AsyncRequestWrapperImpl<ODataBatchResponse>.AsyncResponseWrapperImpl {

    /**
     * Constructor.
     *
     * @param res OData batch response.
     */
    public AsyncResponseWrapperImpl(final ODataBatchResponse res) {
      super();

      if (res.getStatusCode() == 202) {
        retrieveMonitorDetails(res);
      } else {
        response = res;
      }
    }

    private void retrieveMonitorDetails(final ODataBatchResponse res) {
      Collection<String> headers = res.getHeader(HttpHeader.LOCATION);
      if (headers == null || headers.isEmpty()) {
        throw new AsyncRequestException("Invalid async request response. Monitor URL not found");
      } else {
        this.location = URI.create(headers.iterator().next());
      }

      headers = res.getHeader(HttpHeader.RETRY_AFTER);
      if (headers != null && !headers.isEmpty()) {
        this.retryAfter = Integer.parseInt(headers.iterator().next());
      }

      headers = res.getHeader(HttpHeader.PREFERENCE_APPLIED);
      if (headers != null && !headers.isEmpty()) {
        for (String header : headers) {
          if (header.equalsIgnoreCase(new ODataPreferences().respondAsync())) {
            preferenceApplied = true;
          }
        }
      }

      IOUtils.closeQuietly(res.getRawResponse());
    }
  }
}
