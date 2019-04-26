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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.ODataClient;
import org.apache.olingo.client.api.communication.request.ODataPayloadManager;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.core.communication.util.PipedOutputStream;
import org.apache.olingo.client.core.communication.request.streamed.AbstractODataStreamedRequest;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;

/**
 * This class implements a batch request.
 */
public abstract class AbstractODataBatchRequest<V extends ODataResponse, T extends ODataPayloadManager<V>>
        extends AbstractODataStreamedRequest<V, T> {

  /**
   * Batch request boundary.
   */
  protected final String boundary;

  /**
   * Expected batch response items.
   */
  protected final List<ODataBatchResponseItem> expectedResItems = new ArrayList<ODataBatchResponseItem>();

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri batch request URI (http://serviceRoot/$batch)
   */
  protected AbstractODataBatchRequest(final ODataClient odataClient, final URI uri) {
    super(odataClient, HttpMethod.POST, uri);

    // create a random UUID value for boundary
    boundary = "batch_" + UUID.randomUUID().toString();

    // specify the contentType header
    setContentType(ContentType.MULTIPART_MIXED + ";" + ODataBatchConstants.BOUNDARY + "=" + boundary);
  }

  protected void addExpectedResItem(final ODataBatchResponseItem item) {
    expectedResItems.add(item);
  }

  public PipedOutputStream getOutputStream() {
    return (PipedOutputStream) getPayloadManager().getBodyStreamWriter();
  }

  /**
   * {@inheritDoc}
   * <br/>
   * This operation is unsupported by a batch request.
   */
  @Override
  public void batch(final ODataBatchRequest req) {
    throw new UnsupportedOperationException("A batch request is not batchable");
  }
}
