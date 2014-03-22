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
import org.apache.http.client.methods.HttpPost;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityCreateResponse;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.format.ODataPubFormat;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData create request.
 */
public class ODataEntityCreateRequestImpl extends AbstractODataBasicRequest<ODataEntityCreateResponse, ODataPubFormat>
        implements ODataEntityCreateRequest, ODataBatchableRequest {

  /**
   * Entity to be created.
   */
  private final ODataEntity entity;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param targetURI entity set URI.
   * @param entity entity to be created.
   */
  ODataEntityCreateRequestImpl(final CommonODataClient odataClient, final URI targetURI, final ODataEntity entity) {
    super(odataClient, ODataPubFormat.class, HttpMethod.POST, targetURI);
    this.entity = entity;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream getPayload() {
    return odataClient.getWriter().writeEntity(entity, ODataPubFormat.fromString(getContentType()));
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataEntityCreateResponse execute() {
    final InputStream input = getPayload();
    ((HttpPost) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataEntityCreateResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Response class about an ODataEntityCreateRequest.
   */
  private class ODataEntityCreateResponseImpl extends AbstractODataResponse implements ODataEntityCreateResponse {

    private ODataEntity entity = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataEntityCreateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataEntityCreateResponseImpl(final HttpClient client, final HttpResponse res) {
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
                  readEntity(getRawResponse(), ODataPubFormat.fromString(getAccept()));
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
