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
import org.apache.olingo.client.api.communication.request.streamed.MediaEntityCreateStreamManager;
import org.apache.olingo.client.api.communication.request.streamed.ODataMediaEntityCreateRequest;
import org.apache.olingo.client.api.communication.response.ODataMediaEntityCreateResponse;
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
public class ODataMediaEntityCreateRequestImpl<E extends ClientEntity>
        extends AbstractODataStreamedEntityRequest<ODataMediaEntityCreateResponse<E>, MediaEntityCreateStreamManager<E>>
        implements ODataMediaEntityCreateRequest<E> {

  private final InputStream media;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param targetURI target entity set.
   * @param media media entity blob to be created.
   */
  public ODataMediaEntityCreateRequestImpl(final ODataClient odataClient, final URI targetURI,
          final InputStream media) {

    super(odataClient, HttpMethod.POST, targetURI);
    this.media = media;
  }

  @Override
  protected MediaEntityCreateStreamManager<E> getPayloadManager() {
    if (payloadManager == null) {
      payloadManager = new MediaEntityCreateStreamManagerImpl(media);
    }
    return (MediaEntityCreateStreamManager<E>) payloadManager;
  }

  /**
   * Media entity payload object.
   */
  public class MediaEntityCreateStreamManagerImpl extends AbstractODataStreamManager<ODataMediaEntityCreateResponse<E>>
          implements MediaEntityCreateStreamManager<E> {

    /**
     * Private constructor.
     *
     * @param input media stream.
     */
    private MediaEntityCreateStreamManagerImpl(final InputStream input) {
      super(ODataMediaEntityCreateRequestImpl.this.futureWrapper, input);
    }

    @Override
    protected ODataMediaEntityCreateResponse<E> getResponse(final long timeout, final TimeUnit unit) {
      finalizeBody();
      return new ODataMediaEntityCreateResponseImpl(odataClient, httpClient, getHttpResponse(timeout, unit));
    }
  }

  /**
   * Response class about an ODataMediaEntityCreateRequest.
   */
  private class ODataMediaEntityCreateResponseImpl extends AbstractODataResponse
          implements ODataMediaEntityCreateResponse<E> {

    private E entity = null;

    private ODataMediaEntityCreateResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
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
