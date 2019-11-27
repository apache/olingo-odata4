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
package org.apache.olingo.client.core.communication.request.invoke;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.invoke.ClientNoContent;
import org.apache.olingo.client.api.communication.response.ODataInvokeResponse;
import org.apache.olingo.client.api.domain.ClientEntity;
import org.apache.olingo.client.api.domain.ClientEntitySet;
import org.apache.olingo.client.api.domain.ClientInvokeResult;
import org.apache.olingo.client.api.domain.ClientProperty;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

public class ODataInvokeRequestImpl<T extends ClientInvokeResult> extends AbstractODataInvokeRequest<T> {

  private ContentType contentType;

  public ODataInvokeRequestImpl(final ODataClient odataClient, final Class<T> reference,
          final HttpMethod method, final URI uri) {

    super(odataClient, reference, method, uri);
  }

  @Override
  public void setFormat(final ContentType contentType) {
    super.setFormat(contentType);
    this.contentType = contentType;
  }

  @Override
  protected ContentType getPOSTParameterFormat() {
    return contentType == null ? getDefaultFormat() : contentType;
  }
  /**
   * Response class about an ODataInvokeRequest.
   */
  protected class ODataInvokeResponseImpl extends AbstractODataResponse implements ODataInvokeResponse<T> {

    private T invokeResult = null;

    private ODataInvokeResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public T getBody() {
      if (invokeResult == null) {
        try {
          if (ClientNoContent.class.isAssignableFrom(reference)) {
            invokeResult = reference.cast(new ClientNoContent());
          } else {
            // avoid getContent() twice:IllegalStateException: Content has been consumed
            final InputStream responseStream = this.payload == null ? res.getEntity().getContent() : this.payload;
            if (ClientEntitySet.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readEntitySet(responseStream,
                  ContentType.parse(getContentType())));
            } else if (ClientEntity.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readEntity(responseStream,
                  ContentType.parse(getContentType())));
            } else if (ClientProperty.class.isAssignableFrom(reference)) {
              invokeResult = reference.cast(odataClient.getReader().readProperty(responseStream,
                  ContentType.parse(getContentType())));
            }
          }
        } catch (IOException e) {
          throw new HttpClientException(e);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return invokeResult;
    }
  }
}
