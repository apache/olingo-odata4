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

import java.io.PipedOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.CommonODataClient;
import org.apache.olingo.client.api.communication.request.ODataPayloadManager;
import org.apache.olingo.client.api.communication.request.batch.CommonODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.streamed.AbstractODataStreamedRequest;
import org.apache.olingo.commons.api.format.ContentType;

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

  protected void addExpectedResItem(ODataBatchResponseItem item) {
    expectedResItems.add(item);
  }

  /**
   * Constructor.
   *
   * @param odataClient client instance getting this request
   * @param uri batch request URI (http://serviceRoot/$batch)
   */
  protected AbstractODataBatchRequest(final CommonODataClient<?> odataClient, final URI uri) {
    super(odataClient, HttpMethod.POST, uri);

    // create a random UUID value for boundary
    boundary = "batch_" + UUID.randomUUID().toString();

    // specify the contentType header
    setContentType(ContentType.MULTIPART_MIXED + ";" + ODataBatchConstants.BOUNDARY + "=" + boundary);
  }

  /**
   * {@inheritDoc }
   */
  public PipedOutputStream getOutputStream() {
    return getPayloadManager().getBodyStreamWriter();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This operation is unsupported by a batch request.
   */
  @Override
  public void batch(CommonODataBatchRequest req) {
    throw new UnsupportedOperationException("A batch request is not batchable");
  }
}
