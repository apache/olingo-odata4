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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.utils.URIUtils;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData update request.
 */
public class ODataEntityUpdateRequestImpl extends AbstractODataBasicRequest<ODataEntityUpdateResponse, ODataPubFormat>
        implements ODataEntityUpdateRequest, ODataBatchableRequest {

  /**
   * Changes to be applied.
   */
  private final ODataEntity changes;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param uri URI of the entity to be updated.
   * @param changes changes to be applied.
   */
  ODataEntityUpdateRequestImpl(final CommonODataClient odataClient,
          final HttpMethod method, final URI uri, final ODataEntity changes) {

    super(odataClient, ODataPubFormat.class, method, uri);
    this.changes = changes;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataEntityUpdateResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataEntityUpdateResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream getPayload() {
    return odataClient.getWriter().writeEntity(changes, ODataPubFormat.fromString(getContentType()));
  }

  /**
   * Response class about an ODataEntityUpdateRequest.
   */
  private class ODataEntityUpdateResponseImpl extends AbstractODataResponse implements ODataEntityUpdateResponse {

    /**
     * Changes.
     */
    private ODataEntity entity = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataEntityUpdateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataEntityUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc ]
     */
    @Override
    public ODataEntity getBody() {
      if (entity == null) {
        try {
          entity = odataClient.getReader().
                  readEntity(getRawResponse(), ODataPubFormat.fromString(getAccept()));
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
