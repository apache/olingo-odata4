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
import org.apache.olingo.client.api.communication.request.retrieve.ODataPropertyRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * This class implements an OData entity property query request.
 */
public class ODataPropertyRequestImpl<T extends ClientProperty>
        extends AbstractODataRetrieveRequest<T> implements ODataPropertyRequest<T> {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  public ODataPropertyRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultFormat();
  }

  @Override
  public ODataRetrieveResponse<T> execute() {
    final HttpResponse res = doExecute();
    return new ODataPropertyResponseImpl(odataClient, httpClient, res);
  }

  protected class ODataPropertyResponseImpl extends AbstractODataRetrieveResponse {

    private T property = null;

    private ODataPropertyResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getBody() {
      if (property == null) {
        try {
          final ResWrap<Property> resource = odataClient.getDeserializer(ContentType.parse(getContentType()))
                  .toProperty(getRawResponse());

          property = (T) odataClient.getBinder().getODataProperty(resource);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return property;
    }
  }
}
