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
package org.apache.olingo.client.core.communication.request.streamed;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.streamed.ODataStreamUpdateRequest;
import org.apache.olingo.client.api.communication.request.streamed.StreamUpdateStreamManager;
import org.apache.olingo.client.api.communication.response.ODataStreamUpdateResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;

/**
 * This class implements an OData stream create/update request. Get instance by using ODataStreamedRequestFactory.
 */
public class ODataStreamUpdateRequestImpl
        extends AbstractODataStreamedRequest<ODataStreamUpdateResponse, StreamUpdateStreamManager>
        implements ODataStreamUpdateRequest {

  private final InputStream stream;

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param method request method.
   * @param targetURI target URI.
   * @param stream stream to be updated.
   */
  ODataStreamUpdateRequestImpl(final CommonODataClient<?> odataClient,
          final HttpMethod method, final URI targetURI, final InputStream stream) {

    super(odataClient, method, targetURI);
    this.stream = stream;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected StreamUpdateStreamManager getPayloadManager() {
    if (payloadManager == null) {
      payloadManager = new StreamUpdateStreamManagerImpl(this.stream);
    }

    return (StreamUpdateStreamManager) payloadManager;
  }

  public class StreamUpdateStreamManagerImpl extends AbstractODataStreamManager<ODataStreamUpdateResponse>
          implements StreamUpdateStreamManager {

    /**
     * Private constructor.
     *
     * @param input payload input stream.
     */
    private StreamUpdateStreamManagerImpl(final InputStream input) {
      super(ODataStreamUpdateRequestImpl.this.futureWrapper, input);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected ODataStreamUpdateResponse getResponse(final long timeout, final TimeUnit unit) {
      finalizeBody();
      return new ODataStreamUpdateResponseImpl(httpClient, getHttpResponse(timeout, unit));
    }
  }

  /**
   * Response class about an ODataStreamUpdateRequest.
   */
  private class ODataStreamUpdateResponseImpl extends AbstractODataResponse implements ODataStreamUpdateResponse {

    private InputStream input = null;

    /**
     * Constructor.
     * <p>
     * Just to create response templates to be initialized from batch.
     */
    private ODataStreamUpdateResponseImpl() {
    }

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataStreamUpdateResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
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
