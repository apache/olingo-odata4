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

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.olingo.client.api.ODataBatchConstants;
import org.apache.olingo.client.api.communication.response.ODataResponse;
import org.apache.olingo.client.core.communication.response.AsyncResponseImpl;
import org.apache.olingo.client.core.communication.response.batch.ODataBatchErrorResponse;

/**
 * Changeset wrapper for the corresponding batch item.
 */
public class ODataChangesetResponseItem extends AbstractODataBatchResponseItem {

  private final boolean continueOnError;

  private boolean unexpected = false;

  public ODataChangesetResponseItem(final boolean continueOnError) {
    super(true);
    this.continueOnError = continueOnError;
  }

  public void setUnexpected() {
    this.unexpected = true;
  }

  @Override
  public ODataResponse next() {
    if (current != null) {
      current.close();
    }

    if (closed) {
      throw new IllegalStateException("Invalid request - the item has been closed");
    }

    if (!hasNext()) {
      throw new NoSuchElementException("No item found");
    }

    if (unexpected) {
      breaking = true;
      return nextUnexpected();
    } else {
      return nextExpected();
    }
  }

  private ODataResponse nextExpected() {
    // consume item for condition above (used like a counter ...)
    expectedItemsIterator.next();

    final Map<String, Collection<String>> nextItemHeaders =
            ODataBatchUtilities.nextItemHeaders(batchLineIterator, boundary);

    if (nextItemHeaders.isEmpty()) {
      throw new IllegalStateException("Expected item not found");
    }

    final Map.Entry<Integer, String> responseLine = ODataBatchUtilities.readResponseLine(batchLineIterator);
    LOG.debug("Retrieved item response {}", responseLine);

    final Map<String, Collection<String>> headers = ODataBatchUtilities.readHeaders(batchLineIterator);
    LOG.debug("Retrieved item headers {}", headers);

    Collection<String> contentId = nextItemHeaders.get(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME);

    if (contentId == null || contentId.isEmpty()) {
      contentId = headers.get(ODataBatchConstants.CHANGESET_CONTENT_ID_NAME);

      if (contentId == null || contentId.isEmpty()) {
        throw new IllegalStateException("Content-ID is missing");
      }
    }

    current = getResponse(contentId.iterator().next());

    if (current == null) {
      throw new IllegalStateException("Unexpected '" + contentId + "' item found");
    }

    current.initFromBatch(responseLine, headers, batchLineIterator, boundary);

    if (current.getStatusCode() >= 400 && !continueOnError) {
      // found error .... 
      breaking = true;
    }

    return current;
  }

  private ODataResponse nextUnexpected() {
    final Map.Entry<Integer, String> responseLine = ODataBatchUtilities.readResponseLine(batchLineIterator);
    LOG.debug("Retrieved item response {}", responseLine);

    final Map<String, Collection<String>> headers = ODataBatchUtilities.readHeaders(batchLineIterator);
    LOG.debug("Retrieved item headers {}", headers);

    if (responseLine.getKey() == 202) {
      // generate async response
      current = new AsyncResponseImpl(responseLine, headers, batchLineIterator, boundary);
      return current;
    } else if (responseLine.getKey() >= 400) {
      // generate error response
      current = new ODataBatchErrorResponse(responseLine, headers, batchLineIterator, boundary);
      return current;
    }

    throw new IllegalStateException("Expected item not found");
  }

  /**
   * Unsupported operation.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Not supported operation.");
  }
}
