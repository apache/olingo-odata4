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

import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataValueUpdateResponse;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.domain.ODataPrimitiveValue;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ODataFormat;

/**
 * This class implements an OData update entity property value request.
 */
public class ODataValueUpdateRequestImpl extends AbstractODataBasicRequest<ODataValueUpdateResponse>
        implements ODataValueUpdateRequest {

  /**
   * Value to be created.
   */
  private final ODataPrimitiveValue value;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI entity set or entity or entity property URI.
   * @param value value to be created.
   */
  ODataValueUpdateRequestImpl(final CommonODataClient<?> odataClient,
          final HttpMethod method, final URI targetURI, final ODataPrimitiveValue value) {

    super(odataClient, method, targetURI);
    // set request body
    this.value = value;
  }

  @Override
  public ODataFormat getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultValueFormat();
  }

  @Override
  public ODataValueUpdateResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataValueUpdateResponseImpl(odataClient, httpClient, doExecute());
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

    private ODataPrimitiveValue value = null;

    private ODataValueUpdateResponseImpl(final CommonODataClient<?> odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public ODataPrimitiveValue getBody() {
      if (value == null) {
        final ODataFormat format = ODataFormat.fromString(getAccept());

        try {
          value = odataClient.getObjectFactory().newPrimitiveValueBuilder().
                  setType(format == ODataFormat.TEXT_PLAIN
                          ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                  setValue(getRawResponse()).
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
