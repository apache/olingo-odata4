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

import java.io.InputStream;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataMediaRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * This class implements an OData media query request.
 */
public class ODataMediaRequestImpl extends AbstractODataRetrieveRequest<InputStream> implements ODataMediaRequest {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  ODataMediaRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);

    setAccept(ContentType.APPLICATION_OCTET_STREAM.toString());
    setContentType(ContentType.APPLICATION_OCTET_STREAM.toString());
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultMediaFormat();
  }

  @Override
  public ODataRetrieveResponse<InputStream> execute() {
    final HttpResponse res = doExecute();
    return new ODataMediaResponseImpl(odataClient, httpClient, res);
  }

  /**
   * Response class about an ODataMediaRequest.
   */
  protected class ODataMediaResponseImpl extends AbstractODataRetrieveResponse {

    private InputStream input = null;

    private ODataMediaResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    /**
     * Gets query result objects.
     * <br/>
     * <b>WARNING</b>: Closing this <tt>ODataResponse</tt> instance is left to the caller.
     *
     * @return query result objects as <tt>InputStream</tt>.
     */
    @Override
    public InputStream getBody() {
      if (input == null) {
          input = getRawResponse();
      }
      return input;
    }
  }
}
