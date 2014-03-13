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
import org.apache.http.entity.ContentType;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataRequest;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMetadataRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.edm.xml.XMLMetadata;
import org.apache.olingo.client.api.format.ODataPubFormat;

/**
 * This class implements a metadata query request.
 */
class ODataMetadataRequestImpl extends AbstractODataRetrieveRequest<XMLMetadata, ODataPubFormat>
        implements ODataMetadataRequest {

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri metadata URI.
   */
  ODataMetadataRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, ODataPubFormat.class, uri);
    super.setAccept(ContentType.APPLICATION_XML.getMimeType());
    super.setContentType(ContentType.APPLICATION_XML.getMimeType());
  }

  @Override
  public ODataRequest setAccept(final String value) {
    // do nothing: Accept is application/XML
    return this;
  }

  @Override
  public ODataRequest setContentType(final String value) {
    // do nothing: Accept is application/XML
    return this;
  }

  @Override
  public ODataRetrieveResponse<XMLMetadata> execute() {
    final HttpResponse res = doExecute();
    return new ODataMetadataResponseImpl(httpClient, res);
  }

  /**
   * Response class about an ODataMetadataRequest.
   */
  protected class ODataMetadataResponseImpl extends ODataRetrieveResponseImpl {

    private XMLMetadata metadata = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    public ODataMetadataResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataMetadataResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    @SuppressWarnings("unchecked")
    public XMLMetadata getBody() {
      if (metadata == null) {
        try {
          metadata = (XMLMetadata) odataClient.getReader().readMetadata(getRawResponse());
        } finally {
          this.close();
        }
      }
      return metadata;
    }
  }
}
