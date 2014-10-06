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
package org.apache.olingo.client.core.communication.request.retrieve.v3;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractMetadataRequestImpl;
import org.apache.olingo.commons.api.format.ODataFormat;

public class XMLMetadataRequestImpl extends AbstractMetadataRequestImpl<XMLMetadata>
        implements XMLMetadataRequest {

  XMLMetadataRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ODataRetrieveResponse<XMLMetadata> execute() {
    return new XMLMetadataResponseImpl(odataClient, httpClient, doExecute());
  }

  private class XMLMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private XMLMetadata metadata;

    private XMLMetadataResponseImpl(final CommonODataClient<?> odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public XMLMetadata getBody() {
      if (metadata == null) {
        try {
          metadata = odataClient.getDeserializer(ODataFormat.XML).toMetadata(getRawResponse());
        } finally {
          this.close();
        }
      }
      return metadata;
    }
  }
}
