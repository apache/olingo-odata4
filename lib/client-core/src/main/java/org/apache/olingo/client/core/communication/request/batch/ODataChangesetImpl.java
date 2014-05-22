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

import java.util.UUID;
import org.apache.olingo.client.api.communication.header.HeaderName;
import org.apache.olingo.client.api.communication.request.ODataBatchableRequest;
import org.apache.olingo.client.api.communication.request.batch.CommonODataBatchRequest;
import org.apache.olingo.client.api.communication.request.batch.ODataChangeset;
import org.apache.olingo.client.api.http.HttpMethod;
import org.apache.olingo.client.core.communication.request.AbstractODataRequest;
import org.apache.olingo.commons.api.format.ContentType;

/**
 * Changeset wrapper for the corresponding batch item.
 */
public class ODataChangesetImpl extends AbstractODataBatchRequestItem
        implements ODataChangeset {

  /**
   * ContentId.
   */
  private int contentId = 0;

  /**
   * Changeset boundary.
   */
  private final String boundary;

  /**
   * Expected changeset response items.
   */
  private final ODataChangesetResponseItem expectedResItem;

  /**
   * Constructor.
   *
   * @param req batch request.
   * @param expectedResItem expected OData response items.
   */
  ODataChangesetImpl(final CommonODataBatchRequest req, final ODataChangesetResponseItem expectedResItem) {
    super(req);
    this.expectedResItem = expectedResItem;

    // create a random UUID value for boundary
    boundary = "changeset_" + UUID.randomUUID().toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getLastContentId() {
    return contentId;
  }

  /**
   * Close changeset item an send changeset request footer.
   */
  @Override
  protected void closeItem() {
    // stream close-delimiter
    if (hasStreamedSomething) {
      newLine();
      stream(("--" + boundary + "--").getBytes());
      newLine();
      newLine();
    }
  }

  /**
   * {@inheritDoc }
   */
  @Override
  public ODataChangeset addRequest(final ODataBatchableRequest request) {
    if (!isOpen()) {
      throw new IllegalStateException("Current batch item is closed");
    }

    if (request.getMethod() == HttpMethod.GET) {
      throw new IllegalArgumentException("Invalid request. GET method not allowed in changeset");
    }

    if (!hasStreamedSomething) {
      stream((HeaderName.contentType.toString() + ": "
              + ContentType.MULTIPART_MIXED + ";boundary=" + boundary).getBytes());

      newLine();
      newLine();

      hasStreamedSomething = true;
    }

    contentId++;

    // preamble
    newLine();

    // stream batch-boundary
    stream(("--" + boundary).getBytes());
    newLine();

    // stream the request
    streamRequestHeader(String.valueOf(contentId));

    request.batch(req, String.valueOf(contentId));

    // add request to the list
    expectedResItem.addResponse(String.valueOf(contentId), ((AbstractODataRequest) request).getResponseTemplate());
    return this;
  }
}
