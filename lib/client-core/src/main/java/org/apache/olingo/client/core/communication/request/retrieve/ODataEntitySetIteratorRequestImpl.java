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
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataEntitySetIteratorRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.client.api.domain.ODataEntitySetIterator;
import org.apache.olingo.commons.api.domain.ODataEntity;
import org.apache.olingo.commons.api.domain.ODataEntitySet;
import org.apache.olingo.commons.api.format.ODataFormat;

/**
 * This class implements an OData EntitySet query request.
 */
public class ODataEntitySetIteratorRequestImpl<ES extends ODataEntitySet, E extends ODataEntity>
        extends AbstractODataRetrieveRequest<ODataEntitySetIterator<ES, E>>
        implements ODataEntitySetIteratorRequest<ES, E> {

  private ODataEntitySetIterator<ES, E> entitySetIterator = null;

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  public ODataEntitySetIteratorRequestImpl(final ODataClient odataClient, final URI query) {
    super(odataClient, query);
  }

  @Override
  public ODataFormat getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  @Override
  public ODataRetrieveResponse<ODataEntitySetIterator<ES, E>> execute() {
    final HttpResponse res = doExecute();
    return new ODataEntitySetIteratorResponseImpl(odataClient, httpClient, res);
  }

  /**
   * Response class about an ODataEntitySetIteratorRequest.
   */
  protected class ODataEntitySetIteratorResponseImpl extends AbstractODataRetrieveResponse {

    private ODataEntitySetIteratorResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public ODataEntitySetIterator<ES, E> getBody() {
      if (entitySetIterator == null) {
        entitySetIterator = new ODataEntitySetIterator<ES, E>(
                odataClient, getRawResponse(), ODataFormat.fromString(getContentType()));
      }
      return entitySetIterator;
    }
  }
}
