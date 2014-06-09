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

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.v3.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.v3.ODataLinkCollectionRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.v3.ODataLinkCollection;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.op.ODataDeserializerException;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.core.communication.request.retrieve.AbstractODataRetrieveRequest;

/**
 * This class implements an OData link query request.
 */
public class ODataLinkCollectionRequestImpl extends AbstractODataRetrieveRequest<ODataLinkCollection, ODataFormat>
        implements ODataLinkCollectionRequest {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param targetURI target URI.
   * @param linkName link name.
   */
  ODataLinkCollectionRequestImpl(final ODataClient odataClient, final URI targetURI, final String linkName) {
    super(odataClient, ODataFormat.class,
            odataClient.newURIBuilder(targetURI.toASCIIString()).appendLinksSegment(linkName).build());
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataRetrieveResponse<ODataLinkCollection> execute() {
    return new ODataLinkCollectionResponseImpl(httpClient, doExecute());
  }

  protected class ODataLinkCollectionResponseImpl extends AbstractODataRetrieveResponse {

    private ODataLinkCollection links = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataLinkCollectionResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataLinkCollectionResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    @Override
    public ODataLinkCollection getBody() {
      if (links == null) {
        try {
          links = ((ODataClient) odataClient).getReader().readLinks(
                  res.getEntity().getContent(), ODataFormat.fromString(getContentType()));
        } catch (IOException e) {
          throw new HttpClientException(e);
        } catch (final ODataDeserializerException e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return links;
    }
  }
}
