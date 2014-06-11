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
package org.apache.olingo.client.core.communication.request.cud.v3;

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.cud.v3.ODataLinkUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataLinkOperationResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataLink;
import org.apache.olingo.commons.api.format.ODataFormat;
import org.apache.olingo.commons.api.op.ODataSerializerException;

/**
 * This class implements an update link OData request.
 */
public class ODataLinkUpdateRequestImpl extends AbstractODataBasicRequest<ODataLinkOperationResponse, ODataFormat>
        implements ODataLinkUpdateRequest {

  /**
   * Entity to be linked.
   */
  private final ODataLink link;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI entity URI.
   * @param link entity to be linked.
   */
  ODataLinkUpdateRequestImpl(final CommonODataClient<?> odataClient,
          final HttpMethod method, final URI targetURI, final ODataLink link) {

    super(odataClient, ODataFormat.class, method, targetURI);
    // set request body
    this.link = link;
  }

  @Override
  public ODataLinkOperationResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataLinkUpdateResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  @Override
  protected InputStream getPayload() {
    try {
      return odataClient.getWriter().writeLink(link, ODataFormat.fromString(getContentType()));
    } catch (final ODataSerializerException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * This class implements the response to an OData link operation request.
   */
  public class ODataLinkUpdateResponseImpl extends AbstractODataResponse implements ODataLinkOperationResponse {

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    public ODataLinkUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }
  }
}
