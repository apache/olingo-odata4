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
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataEntityUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataEntityUpdateResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * This class implements an OData update request.
 *
 * @param <E> concrete ODataEntity implementation
 */
public class ODataEntityUpdateRequestImpl<E extends ClientEntity>
        extends AbstractODataBasicRequest<ODataEntityUpdateResponse<E>>
        implements ODataEntityUpdateRequest<E> {

  /**
   * Changes to be applied.
   */
  private final E changes;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param uri URI of the entity to be updated.
   * @param changes changes to be applied.
   */
  public ODataEntityUpdateRequestImpl(final ODataClient odataClient,
          final HttpMethod method, final URI uri, final E changes) {

    super(odataClient, method, uri);
    this.changes = changes;
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  @Override
  public InputStream getPayload() {
    try {
      return odataClient.getWriter().writeEntity(changes, ContentType.parse(getContentType()));
    } catch (final ODataSerializerException e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public ODataEntityUpdateResponse<E> execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      final HttpResponse httpResponse = doExecute();
      final ODataEntityUpdateResponseImpl response =
              new ODataEntityUpdateResponseImpl(odataClient, httpClient, httpResponse);
      if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
        response.close();
      }
      return response;
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Response class about an ODataEntityUpdateRequest.
   */
  private class ODataEntityUpdateResponseImpl extends AbstractODataResponse implements ODataEntityUpdateResponse<E> {

    /**
     * Changes.
     */
    private E entity = null;

    private ODataEntityUpdateResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getBody() {
      if (entity == null) {
        try {
          final ResWrap<Entity> resource = odataClient.getDeserializer(ContentType.parse(getAccept())).
                  toEntity(getRawResponse());

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
