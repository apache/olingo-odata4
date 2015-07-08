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
package org.apache.olingo.client.core.communication.response.batch;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.io.IOUtils;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchLineIterator;
import org.apache.olingo.client.api.communication.request.batch.ODataBatchResponseItem;
import org.apache.olingo.client.api.communication.response.ODataBatchResponse;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchLineIteratorImpl;
import org.apache.olingo.client.core.communication.request.batch.ODataBatchUtilities;
import org.apache.olingo.client.core.communication.request.batch.ODataChangesetResponseItem;
import org.apache.olingo.commons.api.Constants;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Batch response manager class.
 */
public class ODataBatchResponseManager implements Iterator<ODataBatchResponseItem> {

  /**
   * Logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(ODataBatchResponseManager.class);

  /**
   * Batch response line iterator.
   */
  private final ODataBatchLineIterator batchLineIterator;

  /**
   * Batch boundary.
   */
  private final String batchBoundary;

  /**
   * Expected batch response items iterator.
   */
  private final Iterator<ODataBatchResponseItem> expectedItemsIterator;

  /**
   * Last retrieved batch response item.
   */
  private ODataBatchResponseItem current = null;

  private final boolean continueOnError;

  /**
   * Constructor.
   *
   * @param res OData batch response.
   * @param expectedItems expected batch response items.
   */
  public ODataBatchResponseManager(
          final ODataBatchResponse res,
          final List<ODataBatchResponseItem> expectedItems) {

    this(res, expectedItems, false);
  }

  public ODataBatchResponseManager(
          final ODataBatchResponse res,
          final List<ODataBatchResponseItem> expectedItems,
          final boolean continueOnError) {

    this.continueOnError = continueOnError;

    try {
      this.expectedItemsIterator = expectedItems.iterator();
      this.batchLineIterator = new ODataBatchLineIteratorImpl(
              IOUtils.lineIterator(res.getRawResponse(), Constants.UTF8));

      // search for boundary
      batchBoundary = ODataBatchUtilities.getBoundaryFromHeader(
          res.getHeader(HttpHeader.CONTENT_TYPE));
      LOG.debug("Retrieved batch response bondary '{}'", batchBoundary);
    } catch (IOException e) {
      LOG.error("Error parsing batch response", e);
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean hasNext() {
    return (current == null || continueOnError || !current.isBreaking()) && expectedItemsIterator.hasNext();
  }

  @Override
  public ODataBatchResponseItem next() {
    if (current != null) {
      current.close();
    }

    if (!hasNext()) {
      throw new NoSuchElementException("No item found");
    }

    current = expectedItemsIterator.next();

    final Map<String, Collection<String>> nextItemHeaders =
            ODataBatchUtilities.nextItemHeaders(batchLineIterator, batchBoundary);

    switch (ODataBatchUtilities.getItemType(nextItemHeaders)) {
      case CHANGESET:
        if (!current.isChangeset()) {
          throw new IllegalStateException("Unexpected batch item");
        }

        current.initFromBatch(
                batchLineIterator,
                ODataBatchUtilities.getBoundaryFromHeader(nextItemHeaders.get(HttpHeader.CONTENT_TYPE)));
        break;

      case RETRIEVE:
        if (current.isChangeset()) {
          // Maybe V4 error item
          ((ODataChangesetResponseItem) current).setUnexpected();
        }

        current.initFromBatch(
                batchLineIterator,
                batchBoundary);

        break;
      default:
        throw new IllegalStateException("Expected item not found");
    }

    return current;
  }

  /**
   * Unsupported operation.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException("Remove operation is not supported");
  }
}
