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
package org.apache.olingo.client.core.communication.request.cud.v4;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataBasicRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.commons.api.format.ODataFormat;

import java.io.InputStream;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.request.cud.v4.ODataReferenceAddingRequest;
import org.apache.olingo.client.api.communication.response.v4.ODataReferenceAddingResponse;
import org.apache.olingo.commons.api.Constants;

/**
 * This class implements an OData delete request.
 */
public class ODataReferenceAddingRequestImpl extends AbstractODataBasicRequest<ODataReferenceAddingResponse>
        implements ODataReferenceAddingRequest {

  final URI reference;

  ODataReferenceAddingRequestImpl(
          final CommonODataClient<?> odataClient, final HttpMethod method, final URI uri, final URI reference) {
    super(odataClient, method, uri);
    this.reference = reference;


  }

  @Override
  public ODataFormat getDefaultFormat() {
    return odataClient.getConfiguration().getDefaultPubFormat();
  }

  /**
   * No payload: null will be returned.
   */
  @Override
  protected InputStream getPayload() {
    if (reference == null) {
      return null;
    } else {
      try {
        return IOUtils.toInputStream(reference.toASCIIString(), Constants.UTF8);
      } catch (IOException e) {
        LOG.warn("Error serializing reference {}", reference);
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Override
  public ODataReferenceAddingResponse execute() {
    return new ODataReferenceAddingResponseImpl(odataClient, httpClient, doExecute());
  }

  /**
   * Response class about an ODataDeleteRequest.
   */
  private class ODataReferenceAddingResponseImpl extends AbstractODataResponse implements ODataReferenceAddingResponse {

    private ODataReferenceAddingResponseImpl(
            final CommonODataClient<?> odataClient, final HttpClient httpClient, final HttpResponse res) {

      super(odataClient, httpClient, res);
      this.close();
    }
  }
}
