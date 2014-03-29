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
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityUpdateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.commons.api.domain.CommonODataEntity;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.data.Container;
import org.apache.olingo.commons.api.data.Entry;

/**
 * This class implements an OData Media Entity create request. Get instance by using ODataStreamedRequestFactory.
 */
public class ODataMediaEntityUpdateRequestImpl
        extends AbstractODataStreamedEntityRequest<ODataMediaEntityUpdateResponse, MediaEntityUpdateStreamManager>
        implements ODataMediaEntityUpdateRequest, ODataBatchableRequest {

  private final InputStream media;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param editURI edit URI of the entity to be updated.
   * @param media media entity blob to be created.
   */
  ODataMediaEntityUpdateRequestImpl(final CommonODataClient odataClient,
          final HttpMethod method, final URI editURI, final InputStream media) {

    super(odataClient, method, editURI);
    this.media = media;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected MediaEntityUpdateStreamManager getStreamManager() {
    if (streamManager == null) {
      streamManager = new MediaEntityUpdateStreamManagerImpl(media);
    }
    return (MediaEntityUpdateStreamManager) streamManager;
  }

  /**
   * Media entity payload object.
   */
  public class MediaEntityUpdateStreamManagerImpl extends AbstractODataStreamManager<ODataMediaEntityUpdateResponse>
          implements MediaEntityUpdateStreamManager {

    /**
     * Private constructor.
     *
     * @param input media stream.
     */
    private MediaEntityUpdateStreamManagerImpl(final InputStream input) {
      super(ODataMediaEntityUpdateRequestImpl.this.futureWrapper, input);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected ODataMediaEntityUpdateResponse getResponse(final long timeout, final TimeUnit unit) {
      finalizeBody();
      return new ODataMediaEntityUpdateResponseImpl(httpClient, getHttpResponse(timeout, unit));
    }
  }

  /**
   * Response class about an ODataMediaEntityUpdateRequest.
   */
  private class ODataMediaEntityUpdateResponseImpl extends AbstractODataResponse
          implements ODataMediaEntityUpdateResponse {

    private CommonODataEntity entity = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataMediaEntityUpdateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataMediaEntityUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public CommonODataEntity getBody() {
      if (entity == null) {
        try {
          final Container<Entry> container = odataClient.getDeserializer().toEntry(getRawResponse(), getFormat());
          entity = odataClient.getBinder().getODataEntity(extractFromContainer(container));
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
