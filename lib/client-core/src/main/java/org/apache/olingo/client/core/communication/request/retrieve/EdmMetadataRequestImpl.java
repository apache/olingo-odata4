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

import java.net.URI;
import java.util.Collection;

import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.EdmMetadataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.XMLMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.commons.api.edm.Edm;

/**
 * This class implements a metadata query request.
 */
class EdmMetadataRequestImpl extends AbstractMetadataRequestImpl<Edm> implements EdmMetadataRequest {

  private final String serviceRoot;

  private EdmMetadataResponseImpl privateResponse;

  EdmMetadataRequestImpl(final ODataClient odataClient, final String serviceRoot, final URI uri) {
    super(odataClient, uri);
    this.serviceRoot = serviceRoot;
  }

  private EdmMetadataResponseImpl getPrivateResponse() {
    if (privateResponse == null) {
      XMLMetadataRequest request = odataClient.getRetrieveRequestFactory().getXMLMetadataRequest(serviceRoot);
      if (getPrefer() != null) {
        request.setPrefer(getPrefer());
      }
      if (getIfMatch() != null) {
        request.setIfMatch(getIfMatch());
      }
      if (getIfNoneMatch() != null) {
        request.setIfNoneMatch(getIfNoneMatch());
      }
      if (getHeader() != null) {
        for (String key : getHeaderNames()) {
          request.addCustomHeader(key, odataHeaders.getHeader(key));
        }
      }
      final ODataRetrieveResponse<XMLMetadata> xmlMetadataResponse = request.execute();

      privateResponse = new EdmMetadataResponseImpl(odataClient, httpClient, xmlMetadataResponse);
    }
    return privateResponse;
  }

  @Override
  public XMLMetadata getXMLMetadata() {
    return getPrivateResponse().getXMLMetadata();
  }

  @Override
  public ODataRetrieveResponse<Edm> execute() {
    return getPrivateResponse();
  }

  private class EdmMetadataResponseImpl extends AbstractODataRetrieveResponse {

    private final ODataRetrieveResponse<XMLMetadata> xmlMetadataResponse;

    private XMLMetadata metadata = null;

    private EdmMetadataResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
        final ODataRetrieveResponse<XMLMetadata> xmlMetadataResponse) {

      super(odataClient, httpClient, null);
      this.xmlMetadataResponse = xmlMetadataResponse;
    }

    @Override
    public void close() {
      super.close();
      xmlMetadataResponse.close();
    }

    @Override
    public int getStatusCode() {
      return xmlMetadataResponse.getStatusCode();
    }

    @Override
    public String getStatusMessage() {
      return xmlMetadataResponse.getStatusMessage();
    }

    @Override
    public Collection<String> getHeaderNames() {
      return xmlMetadataResponse.getHeaderNames();
    }

    @Override
    public Collection<String> getHeader(final String name) {
      return xmlMetadataResponse.getHeader(name);
    }

    public XMLMetadata getXMLMetadata() {
      if (metadata == null) {
        try {
          metadata = xmlMetadataResponse.getBody();
        } finally {
          this.close();
        }
      }
      return metadata;
    }

    @Override
    public Edm getBody() {
      return odataClient.getReader().readMetadata(getXMLMetadata().getSchemaByNsOrAlias());
    }
  }
}
