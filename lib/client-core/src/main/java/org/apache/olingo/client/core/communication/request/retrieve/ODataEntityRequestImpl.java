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

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.format.ODataPubFormat;

/**
 * This class implements an OData retrieve query request returning a single entity.
 */
public class ODataEntityRequestImpl extends AbstractODataRetrieveRequest<ODataEntity, ODataPubFormat>
        implements ODataEntityRequest {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  ODataEntityRequestImpl(final CommonODataClient odataClient, final URI query) {
    super(odataClient, ODataPubFormat.class, query);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataRetrieveResponse<ODataEntity> execute() {
    return new ODataEntityResponseImpl(httpClient, doExecute());
  }

  /**
   * Response class about an ODataEntityRequest.
   */
  public class ODataEntityResponseImpl extends ODataRetrieveResponseImpl {

    private ODataEntity entity = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataEntityResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataEntityResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataEntity getBody() {
      if (entity == null) {
        try {
          entity = odataClient.getReader().
                  readEntity(getRawResponse(), ODataPubFormat.fromString(getContentType()));
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
