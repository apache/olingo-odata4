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
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataValueRequest;
import org.apache.olingo.client.api.communication.response.ODataRetrieveResponse;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.format.ODataValueFormat;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * This class implements an OData entity property value query request.
 */
public class ODataValueRequestImpl extends AbstractODataRetrieveRequest<ODataPrimitiveValue, ODataValueFormat>
        implements ODataValueRequest {

  /**
   * Private constructor.
   *
   * @param odataClient client instance getting this request
   * @param query query to be executed.
   */
  ODataValueRequestImpl(final CommonODataClient odataClient, final URI query) {
    super(odataClient, ODataValueFormat.class, query);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataRetrieveResponse<ODataPrimitiveValue> execute() {
    final HttpResponse res = doExecute();
    return new ODataValueResponseImpl(httpClient, res);
  }

  /**
   * Response class about an ODataDeleteReODataValueRequestquest.
   */
  protected class ODataValueResponseImpl extends AbstractODataRetrieveResponse {

    private ODataPrimitiveValue value = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataValueResponseImpl() {
      super();
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataValueResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataPrimitiveValue getBody() {
      if (value == null) {
        final ODataValueFormat format = ODataValueFormat.fromString(getContentType());

        try {
          value = odataClient.getObjectFactory().newPrimitiveValueBuilder().
                  setType(format == ODataValueFormat.TEXT
                          ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                  setText(IOUtils.toString(getRawResponse())).
                  build();
        } catch (Exception e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return value;
    }
  }
}
