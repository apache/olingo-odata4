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
package org.apache.olingo.client.core.communication.request.cud;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataValueUpdateResponse;
import org.apache.olingo.client.api.domain.ODataPrimitiveValue;
import org.apache.olingo.client.api.domain.ODataValue;
import org.apache.olingo.client.api.format.ODataValueFormat;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.api.utils.URIUtils;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;

/**
 * This class implements an OData update entity property value request.
 */
public class ODataValueUpdateRequestImpl extends AbstractODataBasicRequest<ODataValueUpdateResponse, ODataValueFormat>
        implements ODataValueUpdateRequest, ODataBatchableRequest {

  /**
   * Value to be created.
   */
  private final ODataValue value;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI entity set or entity or entity property URI.
   * @param value value to be created.
   */
  ODataValueUpdateRequestImpl(final CommonODataClient odataClient,
          final HttpMethod method, final URI targetURI, final ODataValue value) {

    super(odataClient, ODataValueFormat.class, method, targetURI);
    // set request body
    this.value = value;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataValueUpdateResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataValueUpdateResponseImpl(httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected InputStream getPayload() {
    return IOUtils.toInputStream(value.toString());
  }

  /**
   * Response class about an ODataValueUpdateRequest.
   */
  private class ODataValueUpdateResponseImpl extends AbstractODataResponse implements ODataValueUpdateResponse {

    private ODataValue value = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataValueUpdateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataValueUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ODataValue getBody() {
      if (value == null) {
        final ODataValueFormat format = ODataValueFormat.fromString(getAccept());

        try {
          value = new ODataPrimitiveValue.Builder(odataClient).
                  setType(format == ODataValueFormat.TEXT
                          ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                  setText(IOUtils.toString(getRawResponse())).
                  build();
        } catch (IOException e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return value;
    }
  }
}
