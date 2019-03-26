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
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.cud.ODataValueUpdateRequest;
import org.apache.olingo.client.api.communication.response.ODataValueUpdateResponse;
import org.apache.olingo.client.api.domain.ClientPrimitiveValue;
import org.apache.olingo.client.api.http.HttpClientException;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * This class implements an OData update entity property value request.
 */
public class ODataValueUpdateRequestImpl extends AbstractODataBasicRequest<ODataValueUpdateResponse>
        implements ODataValueUpdateRequest {

  /**
   * Value to be created.
   */
  private final ClientPrimitiveValue value;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI entity set or entity or entity property URI.
   * @param value value to be created.
   */
  ODataValueUpdateRequestImpl(final ODataClient odataClient,
          final HttpMethod method, final URI targetURI, final ClientPrimitiveValue value) {

    super(odataClient, method, targetURI);
    // set request body
    this.value = value;
  }

  @Override
  public ContentType getDefaultFormat() {
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
  public InputStream getPayload() {
    return IOUtils.toInputStream(value.toString());
  }

  /**
   * Response class about an ODataValueUpdateRequest.
   */
  private class ODataValueUpdateResponseImpl extends AbstractODataResponse implements ODataValueUpdateResponse {

    private ClientPrimitiveValue resValue = null;

    private ODataValueUpdateResponseImpl(final ODataClient odataClient, final HttpClient httpClient,
            final HttpResponse res) {

      super(odataClient, httpClient, res);
    }

    @Override
    public ClientPrimitiveValue getBody() {
      if (resValue == null) {
        final ContentType contentType = ContentType.parse(getAccept());
        
        try {
          resValue = odataClient.getObjectFactory().newPrimitiveValueBuilder().
                  setType(contentType.isCompatible(ContentType.TEXT_PLAIN)
                          ? EdmPrimitiveTypeKind.String : EdmPrimitiveTypeKind.Stream).
                  setValue(getRawResponse()).
                  build();
        } catch (Exception e) {
          throw new HttpClientException(e);
        } finally {
          this.close();
        }
      }
      return resValue;
    }
  }
}
