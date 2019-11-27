/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
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
import org.apache.olingo.client.api.communication.request.cud.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.response.ODataReferenceAddingResponse;
import org.apache.olingo.client.api.data.ResWrap;
import org.apache.olingo.client.api.serialization.ODataSerializerException;
import org.apache.olingo.client.api.serialization.ODataWriter;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.uri.URIUtils;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * See {@link ODataReferenceAddingRequest}
 * 
 * Will be used, for single-valued navigation properties as was well as collection navigation properties
 */
public class ODataReferenceAddingRequestImpl extends AbstractODataBasicRequest<ODataReferenceAddingResponse>
    implements ODataReferenceAddingRequest {

  final ResWrap<URI> reference;

  ODataReferenceAddingRequestImpl(
      final ODataClient odataClient, final HttpMethod method, final URI uri, final ResWrap<URI> reference) {
    super(odataClient, method, uri);
    this.reference = reference;
  }

  @Override
  public ContentType getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  /**
   * No payload: null will be returned.
   */
  @Override
  public InputStream getPayload() {
    if (reference == null) {
      return null;
    } else {
      ODataWriter writer = odataClient.getWriter();
      try {
        return writer.writeReference(reference, ContentType.parse(getContentType()));
      } catch (ODataSerializerException e) {
        LOG.warn("Error serializing reference {}", reference);
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Override
  public ODataReferenceAddingResponse execute() {
    final InputStream input = getPayload();
    ((HttpEntityEnclosingRequestBase) request).setEntity(URIUtils.buildInputStreamEntity(odataClient, input));

    try {
      return new ODataReferenceAddingResponseImpl(odataClient, httpClient, doExecute());
    } finally {
      IOUtils.closeQuietly(input);
    }
  }

  /**
   * Response class about an ODataReferenceAddingRequest.
   */
  private class ODataReferenceAddingResponseImpl extends AbstractODataResponse implements ODataReferenceAddingResponse {

    private ODataReferenceAddingResponseImpl(
        final ODataClient odataClient, final HttpClient httpClient, final HttpResponse res) {

      super(odataClient, httpClient, res);
      this.close();
    }
  }
}
