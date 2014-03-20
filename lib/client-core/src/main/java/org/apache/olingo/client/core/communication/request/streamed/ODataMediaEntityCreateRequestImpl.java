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
package org.apache.olingo.client.core.communication.request.streamed;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
import org.apache.olingo.client.api.domain.ODataEntity;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData Media Entity create request. Get instance by using ODataStreamedRequestFactory.
 */
public class ODataMediaEntityCreateRequestImpl
        extends AbstractODataStreamedEntityRequest<ODataMediaEntityCreateResponse, MediaEntityCreateStreamManager>
        implements ODataMediaEntityCreateRequest, ODataBatchableRequest {

  private final InputStream media;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param targetURI target entity set.
   * @param media media entity blob to be created.
   */
  ODataMediaEntityCreateRequestImpl(final CommonODataClient odataClient, final URI targetURI, final InputStream media) {
    super(odataClient, HttpMethod.POST, targetURI);
    this.media = media;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected MediaEntityCreateStreamManager getStreamManager() {
    if (streamManager == null) {
      streamManager = new MediaEntityCreateStreamManagerImpl(media);
    }
    return (MediaEntityCreateStreamManager) streamManager;
  }

  /**
   * Media entity payload object.
   */
  public class MediaEntityCreateStreamManagerImpl extends AbstractODataStreamManager<ODataMediaEntityCreateResponse>
          implements MediaEntityCreateStreamManager {

    /**
     * Private constructor.
     *
     * @param input media stream.
     */
    private MediaEntityCreateStreamManagerImpl(final InputStream input) {
      super(ODataMediaEntityCreateRequestImpl.this.futureWrapper, input);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected ODataMediaEntityCreateResponse getResponse(final long timeout, final TimeUnit unit) {
      finalizeBody();
      return new ODataMediaEntityCreateResponseImpl(httpClient, getHttpResponse(timeout, unit));
    }
  }

  /**
   * Response class about an ODataMediaEntityCreateRequest.
   */
  private class ODataMediaEntityCreateResponseImpl extends AbstractODataResponse
          implements ODataMediaEntityCreateResponse {

    private ODataEntity entity = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataMediaEntityCreateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataMediaEntityCreateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataEntity getBody() {
      if (entity == null) {
        try {
          entity = odataClient.getReader().readEntity(getRawResponse(), getFormat());
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
