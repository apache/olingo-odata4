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
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityUpdateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityUpdateResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * This class implements an OData Media Entity create request. Get instance by using ODataStreamedRequestFactory.
 *
 * @param <E> concrete ODataEntity implementation
 */
public class ODataMediaEntityUpdateRequestImpl<E extends ClientEntity>
        extends AbstractODataStreamedEntityRequest<ODataMediaEntityUpdateResponse<E>, MediaEntityUpdateStreamManager<E>>
        implements ODataMediaEntityUpdateRequest<E> {

  private final InputStream media;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param editURI edit URI of the entity to be updated.
   * @param media media entity blob to be created.
   */
  public ODataMediaEntityUpdateRequestImpl(final ODataClient odataClient,
          final HttpMethod method, final URI editURI, final InputStream media) {

    super(odataClient, method, editURI);
    this.media = media;
  }

  @Override
  protected MediaEntityUpdateStreamManager<E> getPayloadManager() {
    if (payloadManager == null) {
      payloadManager = new MediaEntityUpdateStreamManagerImpl(media);
    }
    return (MediaEntityUpdateStreamManager<E>) payloadManager;
  }

  /**
   * Media entity payload object.
   */
  public class MediaEntityUpdateStreamManagerImpl extends AbstractODataStreamManager<ODataMediaEntityUpdateResponse<E>>
          implements MediaEntityUpdateStreamManager<E> {

    /**
     * Private constructor.
     *
     * @param input media stream.
     */
    private MediaEntityUpdateStreamManagerImpl(final InputStream input) {
      super(ODataMediaEntityUpdateRequestImpl.this.futureWrapper, input);
    }

    @Override
    protected ODataMediaEntityUpdateResponse<E> getResponse(final long timeout, final TimeUnit unit) {
      finalizeBody();
      return new ODataMediaEntityUpdateResponseImpl(odataClient, httpClient, getHttpResponse(timeout, unit));
    }
  }

  /**
   * Response class about an ODataMediaEntityUpdateRequest.
   */
  private class ODataMediaEntityUpdateResponseImpl extends AbstractODataResponse
          implements ODataMediaEntityUpdateResponse<E> {

    private E entity = null;

    private ODataMediaEntityUpdateResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getBody() {
      if (entity == null) {
        try {
          final ResWrap<Entity> resource = odataClient.getDeserializer(getFormat()).toEntity(getRawResponse());

          entity = (E) odataClient.getBinder().getODataEntity(resource);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
