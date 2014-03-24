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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.retrieve.ODataRawRequest;
import org.apache.olingo.client.api.communication.response.ODataRawResponse;
import org.apache.olingo.commons.api.format.ODataPubFormat;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.ODataRequestImpl;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements a generic OData request.
 */
public class ODataRawRequestImpl extends ODataRequestImpl<ODataPubFormat>
        implements ODataRawRequest {

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri request URI.
   */
  ODataRawRequestImpl(final CommonODataClient odataClient, final URI uri) {
    super(odataClient, ODataPubFormat.class, HttpMethod.GET, uri);
  }

  @Override
  public void setFormat(final String format) {
    setAccept(format);
    setContentType(format);
  }

  @Override
  public ODataRawResponse execute() {
    return new ODataRawResponseImpl(httpClient, doExecute());
  }

  private class ODataRawResponseImpl extends AbstractODataResponse implements ODataRawResponse {

    private byte[] obj = null;

    /**
     * Constructor.
     * <br/>
     * Just to create response templates to be initialized from batch.
     */
    private ODataRawResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataRawResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    @Override
    public <T> T getBodyAs(final Class<T> reference) {
      if (obj == null) {
        try {
          this.obj = IOUtils.toByteArray(getRawResponse());
        } catch (IOException e) {
          throw new IllegalArgumentException(e);
        } finally {
          this.close();
        }
      }
      
      return odataClient.getReader().read(new ByteArrayInputStream(obj), getContentType(), reference);
    }

  }

}
