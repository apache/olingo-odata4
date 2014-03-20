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
package org.apache.olingo.client.core.communication.request.retrieve;

import java.io.InputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRetrieveRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This is an abstract representation of an OData retrieve query request returning one or more result item.
 */
public abstract class AbstractODataRetrieveRequest<V, T extends Enum<T>>
        extends AbstractODataBasicRequest<ODataRetrieveResponse<V>, T>
        implements ODataRetrieveRequest<V, T>, ODataBatchableRequest {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param formatRef reference class for the format being used
   * @param query query to be executed.
   */
  public AbstractODataRetrieveRequest(final CommonODataClient odataClient, final Class<T> formatRef, final URI query) {
    super(odataClient, formatRef, HttpMethod.GET, query);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public abstract ODataRetrieveResponse<V> execute();

  /**
   * {@inheritDoc }
   * <p>
   * This kind of request doesn't have any payload: null will be returned.
   */
  @Override
  protected InputStream getPayload() {
    return null;
  }

  /**
   * Response abstract class about an ODataRetrieveRequest.
   */
  protected abstract class ODataRetrieveResponseImpl extends AbstractODataResponse implements ODataRetrieveResponse<V> {

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    protected ODataRetrieveResponseImpl() {
      super();
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    protected ODataRetrieveResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public abstract V getBody();
  }
}
