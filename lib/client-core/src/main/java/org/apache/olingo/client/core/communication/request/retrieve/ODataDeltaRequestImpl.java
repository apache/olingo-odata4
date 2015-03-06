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
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataDeltaRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.data.Delta;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataDelta;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.serialization.ODataDeserializerException;

public class ODataDeltaRequestImpl extends AbstractODataRetrieveRequest<ODataDelta>
    implements ODataDeltaRequest {

  public ODataDeltaRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ODataFormat getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  @Override
  public ODataRetrieveResponse<ODataDelta> execute() {
    final HttpResponse res = doExecute();
    return new ODataDeltaResponseImpl(odataClient, httpClient, res);
  }

  protected class ODataDeltaResponseImpl extends AbstractODataRetrieveResponse {

    private ODataDelta delta = null;

    private ODataDeltaResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public ODataDelta getBody() {
      if (delta == null) {
        try {
          final ResWrap<Delta> resource = odataClient.getDeserializer(ODataFormat.fromString(getContentType())).
              toDelta(res.getEntity().getContent());

          delta = odataClient.getBinder().getODataDelta(resource);
        } catch (IOException e) {
          throw new HttpClientException(e);
        } catch (final ODataDeserializerException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      return delta;
    }
  }

}
