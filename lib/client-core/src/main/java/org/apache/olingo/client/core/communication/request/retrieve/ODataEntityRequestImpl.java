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
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntityRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * This class implements an OData retrieve query request returning a single entity.
 */
public class ODataEntityRequestImpl<E extends ClientEntity>
        extends AbstractODataRetrieveRequest<E> implements ODataEntityRequest<E> {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  public ODataEntityRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  @Override
  public ODataRetrieveResponse<E> execute() {
    return new ODataEntityResponseImpl(odataClient, httpClient, doExecute());
  }

  /**
   * Response class about an ODataEntityRequest.
   */
  public class ODataEntityResponseImpl extends AbstractODataRetrieveResponse {

    private E entity = null;

    private ODataEntityResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    @SuppressWarnings("unchecked")
    public E getBody() {
      if (entity == null) {
        try {
          final ResWrap<Entity> resource = odataClient.getDeserializer(ContentType.parse(getContentType())).
                  toEntity(getRawResponse());

          entity = (E) odataClient.getBinder().getODataEntity(resource);
        } catch (ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return entity;
    }
  }
}
