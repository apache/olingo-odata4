/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.olingo.client.core.communication.request.retrieve;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataDeltaRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.domain.ClientDelta;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.serialization.ODataDeserializerException;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.format.ContentType;

public class ODataDeltaRequestImpl extends AbstractODataRetrieveRequest<ClientDelta>
    implements ODataDeltaRequest {

  public ODataDeltaRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  @Override
  public ODataRetrieveResponse<ClientDelta> execute() {
    final HttpResponse res = doExecute();
    return new ODataDeltaResponseImpl(odataClient, httpClient, res);
  }

  protected class ODataDeltaResponseImpl extends AbstractODataRetrieveResponse {

    private ClientDelta delta = null;

    private ODataDeltaResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public ClientDelta getBody() {
      if (delta == null) {
        try {
          InputStream content;
          if(res == null){
            content = payload;
          }else{
            content = res.getEntity().getContent();
          }
          final ResWrap<Delta> resource = odataClient.getDeserializer(ContentType.parse(getContentType())).
              toDelta(content);

          delta = odataClient.getBinder().getODataDelta(resource);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } catch (IOException e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return delta;
    }
  }

}
