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
package org.apache.olingo.client.core.communication.request.v4;

import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.batch.v4.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.v4.AsyncBatchRequestWrapper;
import org.apache.olingo.client.api.communication.request.v4.AsyncRequestFactory;
import org.apache.olingo.client.api.communication.request.v4.AsyncRequestWrapper;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.v4.ODataClient;

public class AsyncRequestFactoryImpl implements AsyncRequestFactory {

  private static final long serialVersionUID = 546577958047902917L;

  private final ODataClient client;

  public AsyncRequestFactoryImpl(final ODataClient client) {
    this.client = client;
  }

  @Override
  public <R extends ODataResponse> AsyncRequestWrapper<R> getAsyncRequestWrapper(final ODataRequest odataRequest) {
    return new AsyncRequestWrapperImpl<R>(client, odataRequest);
  }

  @Override
  public AsyncBatchRequestWrapper getAsyncBatchRequestWrapper(final ODataBatchRequest odataRequest) {
    return new AsyncBatchRequestWrapperImpl(client, odataRequest);
  }
}
