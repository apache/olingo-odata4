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
package org.apache.olingo.client.core.communication.request.batch;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.batch.BatchStreamManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequestItem;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.communication.request.batch.ODataRetrieve;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataStreamManager;
import org.apache.olingo.client.core.communication.request.streamed.AbstractODataStreamedRequest;
import org.apache.olingo.client.core.communication.response.AbstractODataResponse;
import org.apache.olingo.client.core.communication.response.batch.ODataBatchResponseManager;

/**
 * This class implements a batch request.
 */
public class ODataBatchRequestImpl extends AbstractODataStreamedRequest<ODataBatchResponse, BatchStreamManager>
        implements ODataBatchRequest {

  /**
   * Batch request boundary.
   */
  private final String boundary;

  /**
   * Expected batch response items.
   */
  private final List<ODataBatchResponseItem> expectedResItems = new ArrayList<ODataBatchResponseItem>();

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri batch request URI (http://serviceRoot/$batch)
   */
  ODataBatchRequestImpl(final ODataClient odataClient, final URI uri) {
    super(odataClient, HttpMethod.POST, uri);

    // create a random UUID value for boundary
    boundary = "batch_" + UUID.randomUUID().toString();

    // specify the contentType header
    setContentType(ODataBatchConstants.MULTIPART_CONTENT_TYPE + ";" + ODataBatchConstants.BOUNDARY + "=" + boundary);
  }

  /**
   * {@inheritDoc }
   */
  @Override
  protected BatchStreamManager getStreamManager() {
    if (streamManager == null) {
      streamManager = new BatchStreamManagerImpl(this);
    }
    return (BatchStreamManager) streamManager;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public PipedOutputStream getOutputStream() {
    return getStreamManager().getBodyStreamWriter();
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataBatchRequestImpl rawAppend(final byte[] toBeStreamed) throws IOException {
    getStreamManager().getBodyStreamWriter().write(toBeStreamed);
    return this;
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataBatchRequestImpl rawAppend(final byte[] toBeStreamed, int off, int len) throws IOException {
    getStreamManager().getBodyStreamWriter().write(toBeStreamed, off, len);
    return this;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This operation is unsupported by a batch request.
   */
  @Override
  public void batch(ODataBatchRequest req) {
    throw new UnsupportedOperationException("A batch request is not batchable");
  }

  /**
   * This class implements a response to a batch request.
   *
   * @see org.apache.olingo.client.core.communication.request.ODataBatchRequest
   */
  private class ODataBatchResponseImpl extends AbstractODataResponse implements ODataBatchResponse {

    /**
     * Constructor.
     *
     * @param client HTTP client.
     * @param res HTTP response.
     */
    private ODataBatchResponseImpl(final HttpClient client, final HttpResponse res) {
      super(client, res);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<ODataBatchResponseItem> getBody() {
      return new ODataBatchResponseManager(this, expectedResItems);
    }
  }

  /**
   * Batch request payload management.
   */
  public class BatchStreamManagerImpl extends AbstractODataStreamManager<ODataBatchResponse> {

    /**
     * Batch request current item.
     */
    private ODataBatchRequestItem currentItem = null;

    /**
     * batch request reference.
     */
    private final ODataBatchRequest req;

    /**
     * Private constructor.
     *
     * @param req batch request reference.
     */
    private BatchStreamManagerImpl(final ODataBatchRequest req) {
      super(ODataBatchRequestImpl.this.futureWrapper);
      this.req = req;
    }

    /**
     * Gets a changeset batch item instance. A changeset can be submitted embedded into a batch request only.
     *
     * @return ODataChangeset instance.
     */
    public ODataChangeset addChangeset() {
      closeCurrentItem();

      // stream dash boundary
      streamDashBoundary();

      final ODataChangesetResponseItem expectedResItem = new ODataChangesetResponseItem();
      expectedResItems.add(expectedResItem);

      currentItem = new ODataChangesetImpl(req, expectedResItem);

      return (ODataChangeset) currentItem;
    }

    /**
     * Gets a retrieve batch item instance. A retrieve item can be submitted embedded into a batch request only.
     *
     * @return ODataRetrieve instance.
     */
    public ODataRetrieve addRetrieve() {
      closeCurrentItem();

      // stream dash boundary
      streamDashBoundary();

      final ODataRetrieveResponseItem expectedResItem = new ODataRetrieveResponseItem();
      currentItem = new ODataRetrieveImpl(req, expectedResItem);

      expectedResItems.add(expectedResItem);

      return (ODataRetrieve) currentItem;
    }

    /**
     * Close the current streamed item.
     */
    private void closeCurrentItem() {
      if (currentItem != null) {
        currentItem.close();
      }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    protected ODataBatchResponse getResponse(final long timeout, final TimeUnit unit) {
      closeCurrentItem();
      streamCloseDelimiter();
      finalizeBody();
      return new ODataBatchResponseImpl(httpClient, getHttpResponse(timeout, unit));
    }

    /**
     * Streams dash boundary.
     */
    private void streamDashBoundary() {
      // preamble
      newLine();

      // stream batch-boundary
      stream(("--" + boundary).getBytes());
      newLine();
    }

    /**
     * Streams close delimiter.
     */
    private void streamCloseDelimiter() {
      // stream close-delimiter
      newLine();
      stream(("--" + boundary + "--").getBytes());
    }
  }
}
