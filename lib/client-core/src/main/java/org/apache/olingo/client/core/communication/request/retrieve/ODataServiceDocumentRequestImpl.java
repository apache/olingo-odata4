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
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataServiceDocumentRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.data.ServiceDocument;
import org.apache.olingo.commons.api.data.ResWrap;
import org.apache.olingo.commons.api.domain.ODataServiceDocument;
import org.apache.olingo.commons.api.format.ODataFormat;

/**
 * This class implements an OData service document request.
 */
public class ODataServiceDocumentRequestImpl extends AbstractODataRetrieveRequest<ODataServiceDocument, ODataFormat>
        implements ODataServiceDocumentRequest {

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri request URI.
   */
  ODataServiceDocumentRequestImpl(final CommonODataClient odataClient, final URI uri) {
    super(odataClient, ODataFormat.class, uri);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataRetrieveResponse<ODataServiceDocument> execute() {
    final HttpResponse res = doExecute();
    return new ODataServiceResponseImpl(httpClient, res);
  }

  /**
   * Response class about an ODataServiceDocumentRequest.
   */
  protected class ODataServiceResponseImpl extends AbstractODataRetrieveResponse {

    private ODataServiceDocument serviceDocument = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataServiceResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataServiceResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    @Override
    public ODataServiceDocument getBody() {
      if (serviceDocument == null) {
        try {
          final ResWrap<ServiceDocument> resource = odataClient.getDeserializer().
                  toServiceDocument(getRawResponse(), ODataFormat.fromString(getContentType()));

          serviceDocument = odataClient.getBinder().getODataServiceDocument(resource.getPayload());
        } finally {
          this.close();
        }
      }
      return serviceDocument;
    }
  }
}
