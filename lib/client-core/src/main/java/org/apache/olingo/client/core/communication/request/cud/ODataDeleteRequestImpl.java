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
package org.apache.olingo.client.core.communication.request.cud;

import java.io.InputStream;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataDeleteRequest;
import org.apache.olingo.client.api.communication.response.ODataDeleteResponse;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.ODataResponseImpl;

/**
 * This class implements an OData delete request.
 */
public class ODataDeleteRequestImpl extends AbstractODataBasicRequest<ODataDeleteResponse, ODataPubFormat>
        implements ODataDeleteRequest, ODataBatchableRequest {

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method HTTP method to be used
   * @param uri URI of the entity to be deleted.
   */
  ODataDeleteRequestImpl(final ODataClient odataClient, final HttpMethod method, final URI uri) {
    super(odataClient, ODataPubFormat.class, method, uri);
  }

  /**
   * {@inheritDoc }
   * <p>
   * No payload: null will be returned.
   */
  @Override
  protected InputStream getPayload() {
    return null;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataDeleteResponse execute() {
    return new ODataDeleteResponseImpl(httpClient, doExecute());
  }

  /**
   * Response class about an ODataDeleteRequest.
   */
  private class ODataDeleteResponseImpl extends ODataResponseImpl implements ODataDeleteResponse {

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataDeleteResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataDeleteResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
      this.close();
    }
  }
}
